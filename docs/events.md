# NOVA ATHLETICS — Events Architecture

## 1. Model

- **Runtime:** Spring `ApplicationEventPublisher` (sync) + `@Async` listeners where safe. Future: bridge to Kafka.
- **Persistence:** không event sourcing — events là side-effects, source of truth vẫn là DB. Optional outbox pattern phase sau.
- **Naming:** past tense, e.g. `OrderCreated`, `PaymentSucceeded`.

---

## 2. Event Catalog

| Event | Producer | Consumers | Payload | Future Kafka Topic |
|-------|----------|-----------|---------|-------------------|
| UserRegistered | auth | notification, analytics | `{ userId, email, customerId }` | `user.events` |
| OrderCreated | checkout/order | inventory (reserve), notification, analytics | `{ orderId, orderNumber, customerId, grandTotal, items: [{variantId, qty}] }` | `order.events` |
| OrderConfirmed | order (payment PAID) | notification, analytics, inventory (sale) | `{ orderId, customerId }` | `order.events` |
| OrderCancelled | order | inventory (release/return), notification, promotion (release coupon) | `{ orderId, reason, statusBefore }` | `order.events` |
| OrderShipped | order/shipment | notification, analytics | `{ orderId, trackingNumber, carrier }` | `order.events` |
| OrderDelivered | order | notification, review (enable), analytics | `{ orderId, customerId }` | `order.events` |
| PaymentSucceeded | payment | order (confirm), notification | `{ paymentId, orderId, amount, providerRef }` | `payment.events` |
| PaymentFailed | payment | order (cancel), inventory (release), notification | `{ paymentId, orderId, reason }` | `payment.events` |
| PaymentRefunded | payment | notification, analytics | `{ paymentId, orderId, amount }` | `payment.events` |
| InventoryReserved | inventory | — (internal) | `{ reservationId, variantId, warehouseId, qty }` | `inventory.events` |
| InventoryReleased | inventory | notification | `{ reservationId, variantId, qty, reason }` | `inventory.events` |
| InventoryAdjusted | inventory | analytics, audit | `{ variantId, warehouseId, type, qty, by }` | `inventory.events` |
| ReviewCreated | review | notification (admin), analytics | `{ reviewId, productId, rating }` | `review.events` |
| CouponApplied | promotion | analytics | `{ couponId, orderId, discount }` | `promotion.events` |

---

## 3. Listener Examples

```java
@Component
class OrderNotificationListener {
  @EventListener
  @Async
  void onOrderConfirmed(OrderConfirmed e) {
    notificationService.send(e.customerId(), "ORDER_CONFIRMED",
      "Đơn hàng " + e.orderNumber() + " đã được xác nhận", Map.of("orderId", e.orderId()));
  }
}

@Component
class PaymentOrderListener {
  @EventListener
  @Transactional
  void onPaymentSucceeded(PaymentSucceeded e) {
    orderService.confirmOrder(e.orderId()); // PENDING → CONFIRMED inside TX
  }
  @EventListener
  void onPaymentFailed(PaymentFailed e) {
    inventoryService.releaseForOrder(e.orderId());
  }
}
```

---

## 4. Ordering & Failure

- **Ordering:** trong cùng TX, publish sau commit (`@TransactionalEventListener(phase = AFTER_COMMIT)`) để tránh consumer đọc dirty.
- **Failure:** listener `@Async` có retry 3 lần (Spring Retry). Critical path (payment→order) là sync trong TX, không async.
- **Future Kafka:** `@TransactionalEventListener` sẽ write vào outbox table, CDC/debezium hoặc poller push sang Kafka — consumers idempotent via `eventId` deduplication.

---

## 5. Idempotency for Consumers

- Mỗi listener check `processed_events(eventId)` table (eventId = `aggregateType:aggregateId:version`) trước khi xử lý.

---

## 6. What NOT to do

- Không để FE subscribe trực tiếp Kafka.
- Không dùng event để thay thế synchronous validation (ví dụ checkout phải sync reserve, không async).
