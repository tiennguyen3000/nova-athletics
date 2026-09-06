# NOVA ATHLETICS — Inventory Model

## 1. Core Principle (CONTRACT)

- **Product KHÔNG chứa inventory.** Inventory thuộc về `(ProductVariant × Warehouse)`.
- `ProductVariant` là SKU (ví dụ `NAR-BLK-40`), `warehouse_inventory` lưu số lượng per warehouse.

---

## 2. Data Model

### warehouse_inventory
```
PK (variant_id, warehouse_id)
quantity_on_hand      INT >=0
quantity_reserved     INT >=0
quantity_available    = on_hand - reserved  (computed, không lưu)
version               INT  (optimistic bổ sung, nhưng reserve dùng pessimistic lock)
```

### inventory_transactions (append-only)
Mọi mutation sinh 1 row:
```
variant_id, warehouse_id, type, quantity (signed), reference_type, reference_id, note, created_by, created_at
```

### inventory_reservations
Giữ chỗ 15 phút khi checkout:
```
variant_id, warehouse_id, order_id?, cart_id?, quantity, status=ACTIVE|CONFIRMED|RELEASED|EXPIRED, expires_at
```

### inventory_transfers + transfer_items
Chuyển kho: `PENDING → IN_TRANSIT → COMPLETED | CANCELLED`.

---

## 3. Transaction Types

| Type | Quantity sign | Sinh khi |
|------|--------------|----------|
| PURCHASE | + | Nhập hàng (admin adjustment) |
| SALE | − | Order CONFIRMED (reservation → sale) |
| RETURN | + | Refund / trả hàng |
| ADJUSTMENT | ± | Kiểm kho thủ công |
| TRANSFER_OUT | − | Xuất kho chuyển |
| TRANSFER_IN | + | Nhập kho nhận chuyển |
| DAMAGE | − | Hàng hỏng |
| RESERVATION | 0 (log) | Reserve checkout |
| RELEASE | 0 (log) | Release (payment fail / cancel / expire) |

---

## 4. Reservation Flow (CONTRACT)

```
Checkout
  │
  ├─► Validate cart (variant active, quantity 1..10)
  ├─► Validate inventory: FOR UPDATE trên warehouse_inventory
  │     available = on_hand - reserved
  │     if available < requested → OUT_OF_STOCK (409)
  ├─► Reserve: UPDATE warehouse_inventory SET quantity_reserved += qty
  │   + INSERT inventory_reservation (expires_at = now()+15m)
  │   + INSERT inventory_transaction type=RESERVATION
  ├─► Create Order (PENDING) + Payment (PENDING) — cùng transaction với reserve
  │
Payment success (webhook / mock confirm)
  ├─► Order: PENDING → CONFIRMED
  ├─► Reservation: ACTIVE → CONFIRMED
  ├─► Inventory: on_hand -= qty, reserved -= qty
  ├─► Transaction: SALE (-qty)
  └─► Publish OrderConfirmed / PaymentSucceeded

Payment failure / timeout
  ├─► Release: reserved -= qty
  ├─► Reservation: ACTIVE → RELEASED
  ├─► Transaction: RELEASE
  └─► Publish PaymentFailed / InventoryReleased

Cancellation (PENDING/CONFIRMED only)
  ├─► nếu CONFIRMED đã trừ on_hand → RETURN (+qty)
  ├─► nếu PENDING (chỉ reserved) → RELEASE
  └─► Order → CANCELLED

Expired reservation (cron mỗi phút)
  └─► ACTIVE where expires_at < now() → RELEASED + reserved -= qty + RELEASE log
```

---

## 5. Concurrency & Anti-Oversell

| Mechanism | Dùng ở đâu | Tại sao |
|-----------|------------|---------|
| **Pessimistic lock** `SELECT ... FOR UPDATE` trên `warehouse_inventory` | Reserve / Release / Sale / Transfer | Serialize hot spot, ngăn 2 checkout cùng đọc available rồi cùng reserve quá tay |
| **Optimistic @Version** trên `orders`, `payments` | Status transition | Order/payment ít contention, retry rẻ |
| **Idempotency-Key** | POST /checkout | Ngăn double checkout (user double-click) — key 24h |
| **DB transactions** (`@Transactional`) | Toàn bộ flow reserve+order+payment | ACID |
| **Unique constraints** | `order_number`, `payment.idempotency_key` | Ngăn duplicate order/payment nếu retry |

**Example reserve (pseudocode):**
```java
@Transactional
public void reserve(List<CartItem> items, Long orderId) {
  for (CartItem item : items) {
    WarehouseInventory inv = em.createQuery(
      "SELECT w FROM WarehouseInventory w WHERE w.variantId=:v AND w.warehouseId=:wh FOR UPDATE", ...)
      .getSingleResult();
    int available = inv.getOnHand() - inv.getReserved();
    if (available < item.getQuantity()) throw OutOfStockException(item.getSku());
    inv.setReserved(inv.getReserved() + item.getQuantity());
    reservationRepo.save(new Reservation(..., item.getQuantity(), ACTIVE, now()+15m));
    txRepo.save(new InventoryTransaction(..., RESERVATION, 0, "ORDER", orderId));
  }
}
```

---

## 6. Warehouse Selection

- Mặc định chọn warehouse có `available` lớn nhất cho variant (hoặc warehouse mặc định theo region — future).
- Admin có thể chỉ định warehouse khi adjust/transfer.

---

## 7. API Admin

- `GET /admin/inventory?warehouseId=&productId=&lowStock=true`
- `POST /admin/inventory/adjustments { variantId, warehouseId, type, quantity, note }`
- `POST /admin/inventory/transfers { from, to, items: [{variantId, quantity}] }`

---

## 8. Job — Release Expired

```java
@Scheduled(fixedDelay = 60_000)
@Transactional
void releaseExpired() {
  List<Reservation> expired = repo.findByStatusAndExpiresAtBefore(ACTIVE, Instant.now());
  for (Reservation r : expired) {
    WarehouseInventory inv = lock(r.getVariantId(), r.getWarehouseId());
    inv.setReserved(inv.getReserved() - r.getQuantity());
    r.setStatus(EXPIRED);
    txRepo.save(new Transaction(..., RELEASE, 0, ...));
    eventPublisher.publish(new InventoryReleased(r));
  }
}
```

---

## 9. Risks

- Flash sale spike → pessimistic queue dài → consider Redis semaphore / queue trước DB ở phase sau.
- Transfer race → lock both warehouses in id order để tránh deadlock.

