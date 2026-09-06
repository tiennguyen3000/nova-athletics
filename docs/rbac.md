# NOVA ATHLETICS — RBAC

## 1. Roles

| Role | Description | Scope |
|------|-------------|-------|
| CUSTOMER | Người mua hàng (mặc định khi register) | Storefront |
| EMPLOYEE | Nhân viên vận hành (xem đơn, kho) | Admin read-heavy |
| MANAGER | Quản lý (CRUD catalog, xử lý đơn, kho) | Admin full trừ employee & audit |
| ADMIN | Quản trị (thêm nhân sự, cấu hình) | Admin full trừ SUPER_ADMIN actions |
| SUPER_ADMIN | Root (gán ADMIN, audit, settings) | All |

**Gán:** `user_roles` — một user có thể nhiều roles (thường 1). `SUPER_ADMIN` chỉ seed thủ công hoặc SUPER_ADMIN gán.

---

## 2. Permissions (29)

```
PRODUCT_READ, PRODUCT_CREATE, PRODUCT_UPDATE, PRODUCT_DELETE
CATEGORY_MANAGE, COLLECTION_MANAGE
ORDER_READ, ORDER_UPDATE, ORDER_CANCEL, ORDER_REFUND
INVENTORY_READ, INVENTORY_UPDATE, INVENTORY_TRANSFER
CUSTOMER_READ, CUSTOMER_UPDATE
EMPLOYEE_READ, EMPLOYEE_MANAGE
PROMOTION_MANAGE
REVIEW_MANAGE
REPORT_READ
AUDIT_READ
```

> `*_MANAGE` = CRUD full trên resource đó. `*_READ` chỉ GET.

---

## 3. Role–Permission Matrix

| Permission | CUSTOMER | EMPLOYEE | MANAGER | ADMIN | SUPER_ADMIN |
|------------|----------|----------|---------|-------|-------------|
| PRODUCT_READ | ✅ | ✅ | ✅ | ✅ | ✅ |
| PRODUCT_CREATE | — | — | ✅ | ✅ | ✅ |
| PRODUCT_UPDATE | — | — | ✅ | ✅ | ✅ |
| PRODUCT_DELETE | — | — | — | ✅ | ✅ |
| CATEGORY_MANAGE | — | — | ✅ | ✅ | ✅ |
| COLLECTION_MANAGE | — | — | ✅ | ✅ | ✅ |
| ORDER_READ | own | ✅ | ✅ | ✅ | ✅ |
| ORDER_UPDATE | — | ✅ | ✅ | ✅ | ✅ |
| ORDER_CANCEL | own (PENDING/CONFIRMED) | — | ✅ | ✅ | ✅ |
| ORDER_REFUND | — | — | ✅ | ✅ | ✅ |
| INVENTORY_READ | — | ✅ | ✅ | ✅ | ✅ |
| INVENTORY_UPDATE | — | — | ✅ | ✅ | ✅ |
| INVENTORY_TRANSFER | — | — | ✅ | ✅ | ✅ |
| CUSTOMER_READ | own | ✅ | ✅ | ✅ | ✅ |
| CUSTOMER_UPDATE | own | — | — | ✅ | ✅ |
| EMPLOYEE_READ | — | — | ✅ | ✅ | ✅ |
| EMPLOYEE_MANAGE | — | — | — | ✅ | ✅ |
| PROMOTION_MANAGE | — | — | ✅ | ✅ | ✅ |
| REVIEW_MANAGE | — | — | ✅ | ✅ | ✅ |
| REPORT_READ | — | — | ✅ | ✅ | ✅ |
| AUDIT_READ | — | — | — | ✅ | ✅ |

**Notes:**
- CUSTOMER `own` = chỉ tác động resource sở hữu (order của mình, profile của mình) — enforce bằng ownership check trong service, không chỉ RBAC.
- EMPLOYEE không được cancel/refund — phải escalate.
- MANAGER vs ADMIN: MANAGER không xóa product permanent (chỉ archive), không quản employee.

---

## 4. Enforcement

### 4.1 Annotation (Controller)
```java
@PreAuthorize("hasAuthority('PRODUCT_CREATE')")
@PostMapping("/admin/products")
createProduct(...)

@PreAuthorize("hasAnyAuthority('ORDER_READ','ORDER_UPDATE')")
@GetMapping("/admin/orders")
listOrders(...)

// Customer ownership: method-level check
@PreAuthorize("isAuthenticated()")
@GetMapping("/orders/{id}")
getOrder(@PathVariable Long id) {
  Order o = orderService.findById(id);
  if (!o.getCustomerId().equals(currentCustomerId())) throw Forbidden;
}
```

### 4.2 Service-level (defense in depth)
- Mọi service method admin đều check `SecurityUtils.hasPermission(...)` nếu có thể gọi nội bộ (không chỉ qua controller).
- Ownership checks luôn ở service (không tin controller).

### 4.3 Data seeding
```sql
-- roles
INSERT INTO roles(name) VALUES ('CUSTOMER'),('EMPLOYEE'),('MANAGER'),('ADMIN'),('SUPER_ADMIN');

-- permissions + role_permissions theo matrix trên
-- SUPER_ADMIN có tất cả
-- Tạo SUPER_ADMIN user từ env: SUPER_ADMIN_EMAIL / SUPER_ADMIN_PASSWORD
```

---

## 5. Frontend Guard

- Next.js middleware: đọc JWT (cookie hoặc header) → decode roles → redirect `/login` nếu thiếu.
- Admin layout check `roles.includes('MANAGER'|'ADMIN'|'SUPER_ADMIN')` else 403 page.
- Hide/show menu theo permissions (ví dụ chỉ MANAGER+ thấy Promotions).

---

## 6. Auditing RBAC Changes

- Mọi `grantRole`, `revokeRole`, `grantPermission` đều ghi `audit_logs` với `action=ROLE_ASSIGNED`, `old_value`/`new_value`.
- Chỉ SUPER_ADMIN/ADMIN được thay đổi role của người khác.

---

## 7. Testing

- [ ] CUSTOMER GET /admin/products → 403
- [ ] EMPLOYEE POST /admin/products → 403
- [ ] MANAGER DELETE /admin/products/{id} → 403 (chỉ ADMIN)
- [ ] CUSTOMER cancel order của người khác → 403 (ownership)
- [ ] SUPER_ADMIN có mọi permission
