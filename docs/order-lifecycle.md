# NOVA ATHLETICS — Order & Payment Lifecycle

## 1. Order Model

### Order
- PK `id`, `order_number` UNIQUE (`NVA-YYYYMMDD-######`), `customer_id`, `status`, pricing (`subtotal`, `discount_total`, `shipping_fee`, `tax_total`, `grand_total`, `currency`), snapshot `shipping_address`/`billing_address` JSONB, `coupon_code`, `version` (optimistic), timestamps.

### OrderItem (snapshot — immutable)
```
order_id, product_id (snapshot), variant_id (snapshot),
product_name, sku, variant_label (e.g. BLACK / 42), image_url,
unit_price, quantity, line_total
```
Không FK enforce sang products — giữ lịch sử ngay cả khi product xóa/đổi giá.

### Payment
```
order_id UNIQUE, payment_number UNIQUE, provider (MOCK/VNPAY/MOMO/STRIPE/COD),
method, status, amount (= grand_total), currency, idempotency_key UNIQUE,
provider_ref, paid_at, version
```

### Shipment
```
order_id, warehouse_id, carrier, tracking_number UNIQUE, status, shipped_at, delivered_at
```

---

## 2. Order State Machine

```
                    ┌──────────────┐
                    │   PENDING    │  ← created at checkout, inventory reserved
                    └──────┬───────┘
                           │ confirm (payment success)
                           ▼
                    ┌──────────────┐
                    │  CONFIRMED   │  ← payment PAID, inventory SALE
                    └──────┬───────┘
                           │ start processing
                           ▼
                    ┌──────────────┐
                    │ PROCESSING   │  ← warehouse picking
                    └──────┬───────┘
                           │ packed
                           ▼
                    ┌──────────────┐
                    │    PACKED    │
                    └──────┬───────┘
                           │ shipped (tracking)
                           ▼
                    ┌──────────────┐
                    │   SHIPPED    │  ← shipment SHIPPED
                    └──────┬───────┘
                           │ delivered
                           ▼
                    ┌──────────────┐
                    │  DELIVERED   │  (terminal)
                    └──────────────┘

  PENDING ──► CANCELLED  (customer or admin, release reservation)
  CONFIRMED ──► CANCELLED (admin only, RETURN inventory)
  Any ──► REFUNDED (after DELIVERED or after CANCELLED with payment PAID)

  CANCELLED, DELIVERED, REFUNDED are terminal (no outgoing except REFUNDED from DELIVERED/CANCELLED).
```

### Valid Transitions Table

| From | To | Trigger | Who | Inventory effect |
|------|----|---------|-----|------------------|
| PENDING | CONFIRMED | payment PAID | system | reserved→SALE (on_hand-=qty) |
| PENDING | CANCELLED | cancel | customer/admin | RELEASE (reserved-=qty) |
| CONFIRMED | PROCESSING | admin status change | MANAGER+ | — |
| CONFIRMED | CANCELLED | cancel | admin (MANAGER+) | RETURN (+qty) |
| PROCESSING | PACKED | admin | MANAGER+ | — |
| PACKED | SHIPPED | admin + tracking | MANAGER+ | shipment SHIPPED |
| SHIPPED | DELIVERED | admin / carrier webhook | MANAGER+ | — |
| DELIVERED | REFUNDED | refund | ADMIN (ORDER_REFUND) | RETURN (+qty) |
| CANCELLED | REFUNDED | refund if paid | ADMIN | RETURN if needed |

**Rules:**
- Không skip state (e.g. PENDING → SHIPPED cấm).
- Customer chỉ được cancel PENDING (và CONFIRMED nếu chưa PROCESSING — configurable).
- REFUNDED chỉ từ DELIVERED hoặc CANCELLED đã PAID.
- Mỗi transition ghi `audit_logs` + publish event.

### Transition Guard (service)
```java
private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
  PENDING, Set.of(CONFIRMED, CANCELLED),
  CONFIRMED, Set.of(PROCESSING, CANCELLED),
  PROCESSING, Set.of(PACKED),
  PACKED, Set.of(SHIPPED),
  SHIPPED, Set.of(DELIVERED),
  DELIVERED, Set.of(REFUNDED),
  CANCELLED, Set.of(REFUNDED)
);
void transition(Order order, OrderStatus target) {
  if (!ALLOWED.getOrDefault(order.getStatus(), Set.of()).contains(target))
    throw new OrderInvalidTransitionException(order.getStatus(), target);
  // optimistic lock via @Version
}
```

---

## 3. Payment State Machine

```
  PENDING ──► AUTHORIZED ──► PAID
      │            │           │
      │            └────► FAILED
      │                       │
      └────► FAILED            └────► REFUNDED
      └────► CANCELLED         └────► CANCELLED (rare, before capture)
```

| State | Meaning |
|-------|---------|
| PENDING | Tạo cùng order, chờ gateway |
| AUTHORIZED | Gateway giữ tiền (chưa capture) — cho CARD |
| PAID | Capture thành công → trigger order CONFIRMED |
| FAILED | Gateway báo fail → release inventory |
| REFUNDED | Hoàn tiền (từ PAID) |
| CANCELLED | Hủy trước khi paid → release |

### Valid Transitions

| From | To | Trigger |
|------|----|---------|
| PENDING | AUTHORIZED | gateway authorize |
| PENDING | PAID | MOCK/COD immediate |
| PENDING | FAILED | gateway fail / timeout |
| PENDING | CANCELLED | order cancelled |
| AUTHORIZED | PAID | capture |
| AUTHORIZED | FAILED | capture fail |
| PAID | REFUNDED | refund |
| FAILED | — | terminal |
| CANCELLED | — | terminal |

Mỗi payment transition cũng `@Version` + idempotency (provider_ref unique).

---

## 4. Checkout → Order → Payment Flow

```
POST /checkout (Idempotency-Key required)
  1. Validate cart not empty, addresses belong to customer
  2. Price server-side: load variants → unit_price (salePrice || basePrice || priceOverride)
  3. Validate coupon (active, date, usage limit, min amount)
  4. BEGIN TX
     - Pessimistic lock warehouse_inventory per variant
     - Check available >= qty else 409
     - Reserve (reserved+=qty, reservation ACTIVE 15m, tx RESERVATION)
     - Compute pricing: subtotal, discount, shipping_fee, grand_total
     - INSERT orders PENDING + order_items (snapshot)
     - INSERT payments PENDING (idempotency_key)
     - INSERT coupon_usages (if coupon)
  5. COMMIT

Payment (MOCK: immediate success; real: webhook)
  - On PAID: TX update payment PENDING→PAID, order PENDING→CONFIRMED,
             inventory on_hand-=qty, reserved-=qty, tx SALE, reservation CONFIRMED
             publish PaymentSucceeded, OrderConfirmed
  - On FAILED: TX payment→FAILED, order→CANCELLED (or keep PENDING + release), reservation RELEASED, tx RELEASE, publish PaymentFailed
```

---

## 5. Cancellation & Refund Rules

| Order status | Cancel allowed? | Refund needed? | Inventory |
|--------------|-----------------|----------------|-----------|
| PENDING | yes (customer/admin) | no (payment PENDING→CANCELLED) | RELEASE |
| CONFIRMED | admin only | if payment PAID → REFUNDED | RETURN |
| PROCESSING+ | no cancel, only refund | REFUNDED | RETURN |
| SHIPPED/DELIVERED | no cancel | REFUNDED | RETURN on refund |

---

## 6. Shipment Lifecycle

```
PENDING → PACKED → SHIPPED → IN_TRANSIT → DELIVERED
  └────► CANCELLED / RETURNED
```
- Tracking number gán khi PACKED→SHIPPED.
- Carrier webhook cập nhật IN_TRANSIT/DELIVERED.

