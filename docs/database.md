# NOVA ATHLETICS — Database Design

> CONTRACT: table / column / type / constraint / index dưới đây là source of truth cho Liquibase + JPA entities (PROMPT 2). Không tự ý đổi tên.

**Conventions:**
- PK: `id BIGSERIAL PRIMARY KEY`
- FK: `*_id BIGINT REFERENCES <table>(id)` + index
- Timestamps: `created_at TIMESTAMPTZ NOT NULL DEFAULT now()`, `updated_at TIMESTAMPTZ NOT NULL DEFAULT now()` (trigger cập nhật)
- Soft delete: `deleted_at TIMESTAMPTZ NULL` (partial index `WHERE deleted_at IS NULL` cho unique)
- Enums: `VARCHAR(32)` + CHECK constraint
- Money: `NUMERIC(12,2)` (VND có thể lưu nguyên nhưng giữ 2 decimals cho mở rộng)
- JSONB cho flexible attrs khi cần (product_attributes là normalized, không JSONB bừa)

---

## 1. users

| Column | Type | Null | Default | Constraint |
|--------|------|------|---------|------------|
| id | BIGSERIAL | NO | auto | PK |
| email | VARCHAR(255) | NO | — | UNIQUE WHERE deleted_at IS NULL |
| password_hash | VARCHAR(255) | NO | — | — |
| status | VARCHAR(32) | NO | 'ACTIVE' | CHECK IN ('ACTIVE','LOCKED','DISABLED','PENDING_VERIFICATION') |
| email_verified | BOOLEAN | NO | false | — |
| last_login_at | TIMESTAMPTZ | YES | — | — |
| deleted_at | TIMESTAMPTZ | YES | — | soft delete (hiếm khi dùng, chủ yếu disable) |
| created_at | TIMESTAMPTZ | NO | now() | — |
| updated_at | TIMESTAMPTZ | NO | now() | — |

Indexes: `idx_users_email`, `idx_users_status`.

## 2. roles

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| name | VARCHAR(64) | NO | UNIQUE — values: CUSTOMER, EMPLOYEE, MANAGER, ADMIN, SUPER_ADMIN |
| description | VARCHAR(255) | YES | — |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

## 3. permissions

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| name | VARCHAR(64) | NO | UNIQUE — e.g. PRODUCT_READ |
| description | VARCHAR(255) | YES | — |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

## 4. user_roles

| Column | Type | Null | Constraint |
|--------|------|------|------------|
| user_id | BIGINT | NO | FK → users(id), ON DELETE CASCADE |
| role_id | BIGINT | NO | FK → roles(id), ON DELETE CASCADE |
| granted_at | TIMESTAMPTZ | NO | default now() |
| granted_by | BIGINT | YES | FK → users(id) |

PK: `(user_id, role_id)`, index `idx_user_roles_role`.

## 5. role_permissions

| Column | Type | Null | Constraint |
|--------|------|------|------------|
| role_id | BIGINT | NO | FK → roles(id) CASCADE |
| permission_id | BIGINT | NO | FK → permissions(id) CASCADE |

PK: `(role_id, permission_id)`.

## 6. customers

| Column | Type | Null | Default | Constraint |
|--------|------|------|---------|------------|
| id | BIGSERIAL | NO | PK | — |
| user_id | BIGINT | NO | — | UNIQUE, FK → users(id) CASCADE |
| first_name | VARCHAR(100) | YES | — | — |
| last_name | VARCHAR(100) | YES | — | — |
| phone | VARCHAR(32) | YES | — | — |
| date_of_birth | DATE | YES | — | — |
| gender | VARCHAR(16) | YES | — | CHECK IN ('MALE','FEMALE','OTHER','UNKNOWN') |
| avatar_url | VARCHAR(512) | YES | — | — |
| deleted_at | TIMESTAMPTZ | YES | — | soft delete (GDPR) |
| created_at | TIMESTAMPTZ | NO | now() | — |
| updated_at | TIMESTAMPTZ | NO | now() | — |

Index `idx_customers_user`.

## 7. employees

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| user_id | BIGINT | NO | UNIQUE FK → users(id) CASCADE |
| employee_code | VARCHAR(32) | NO | UNIQUE |
| department | VARCHAR(100) | YES | — |
| position | VARCHAR(100) | YES | — |
| hire_date | DATE | YES | — |
| status | VARCHAR(32) | NO | default 'ACTIVE' CHECK IN ('ACTIVE','INACTIVE','ON_LEAVE') |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

Index `idx_employees_code`.

## 8. addresses

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| customer_id | BIGINT | NO | FK → customers(id) CASCADE |
| label | VARCHAR(64) | YES | e.g. Home, Office |
| recipient_name | VARCHAR(255) | NO | — |
| phone | VARCHAR(32) | NO | — |
| line1 | VARCHAR(255) | NO | — |
| line2 | VARCHAR(255) | YES | — |
| ward | VARCHAR(100) | YES | — |
| district | VARCHAR(100) | YES | — |
| city | VARCHAR(100) | NO | — |
| country | VARCHAR(64) | NO | default 'VN' |
| is_default | BOOLEAN | NO | default false |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

Index `idx_addresses_customer`.

## 9. categories

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| parent_id | BIGINT | YES | FK → categories(id) SET NULL |
| name | VARCHAR(255) | NO | — |
| slug | VARCHAR(255) | NO | UNIQUE WHERE deleted_at IS NULL |
| description | TEXT | YES | — |
| image_url | VARCHAR(512) | YES | — |
| sort_order | INT | NO | default 0 |
| is_active | BOOLEAN | NO | default true |
| deleted_at | TIMESTAMPTZ | YES | — |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

Indexes: `idx_categories_slug`, `idx_categories_parent`.

## 10. collections

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| name | VARCHAR(255) | NO | — |
| slug | VARCHAR(255) | NO | UNIQUE WHERE deleted_at IS NULL |
| description | TEXT | YES | — |
| image_url | VARCHAR(512) | YES | — |
| is_active | BOOLEAN | NO | default true |
| deleted_at | TIMESTAMPTZ | YES | — |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

Index `idx_collections_slug`.

## 11. products

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| name | VARCHAR(255) | NO | — |
| slug | VARCHAR(255) | NO | UNIQUE WHERE deleted_at IS NULL |
| subtitle | VARCHAR(255) | YES | — |
| description | TEXT | YES | — |
| brand | VARCHAR(100) | YES | default 'NOVA' |
| gender | VARCHAR(16) | YES | CHECK IN ('MEN','WOMEN','KIDS','UNISEX') |
| sport | VARCHAR(64) | YES | e.g. RUNNING, BASKETBALL, FOOTBALL, TRAINING, LIFESTYLE |
| base_price | NUMERIC(12,2) | NO | — |
| sale_price | NUMERIC(12,2) | YES | — |
| currency | VARCHAR(3) | NO | default 'VND' |
| status | VARCHAR(32) | NO | default 'DRAFT' CHECK IN ('DRAFT','ACTIVE','INACTIVE','ARCHIVED') |
| is_featured | BOOLEAN | NO | default false |
| tsv | TSVECTOR | YES | generated column for search |
| deleted_at | TIMESTAMPTZ | YES | — |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

Indexes: `idx_products_slug`, `idx_products_status`, `idx_products_gender`, `idx_products_sport`, `idx_products_tsv GIN(tsv)`, `idx_products_base_price`.

Generated: `tsv = to_tsvector('english', coalesce(name,'') || ' ' || coalesce(subtitle,'') || ' ' || coalesce(description,''))` (sẽ customize cho tiếng Việt nếu cần).

## 12. product_variants

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| product_id | BIGINT | NO | FK → products(id) CASCADE |
| sku | VARCHAR(64) | NO | UNIQUE — e.g. NAR-BLK-40 |
| size | VARCHAR(32) | YES | e.g. 40, 41, M, L |
| color | VARCHAR(64) | YES | e.g. BLACK, WHITE |
| color_hex | VARCHAR(7) | YES | e.g. #000000 |
| price_override | NUMERIC(12,2) | YES | NULL = dùng base_price |
| weight_grams | INT | YES | — |
| is_active | BOOLEAN | NO | default true |
| deleted_at | TIMESTAMPTZ | YES | — |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

Indexes: `idx_variants_product`, `idx_variants_sku`, `idx_variants_color`, `idx_variants_size`.

## 13. product_images

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| product_id | BIGINT | NO | FK → products(id) CASCADE |
| variant_id | BIGINT | YES | FK → product_variants(id) SET NULL |
| media_id | BIGINT | YES | FK → media(id) SET NULL |
| url | VARCHAR(512) | NO | — |
| alt_text | VARCHAR(255) | YES | — |
| sort_order | INT | NO | default 0 |
| is_primary | BOOLEAN | NO | default false |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

Indexes: `idx_pimages_product`, `idx_pimages_variant`.

## 14. product_attributes

| Column | Type | Null |
|--------|------|------|
| id | BIGSERIAL | PK |
| name | VARCHAR(64) | UNIQUE — e.g. SIZE, COLOR, MATERIAL |
| display_name | VARCHAR(64) | — |
| created_at | TIMESTAMPTZ | now() |
| updated_at | TIMESTAMPTZ | now() |

## 15. product_attribute_values

| Column | Type | Null |
|--------|------|------|
| id | BIGSERIAL | PK |
| product_id | BIGINT | FK → products(id) CASCADE |
| attribute_id | BIGINT | FK → product_attributes(id) CASCADE |
| value | VARCHAR(255) | — |
| created_at | TIMESTAMPTZ | now() |
| updated_at | TIMESTAMPTZ | now() |

Index `idx_pav_product`, unique `(product_id, attribute_id, value)`.

## 16. product_tags

| Column | Type | Null |
|--------|------|------|
| id | BIGSERIAL | PK |
| name | VARCHAR(64) | UNIQUE |
| slug | VARCHAR(64) | UNIQUE |
| created_at | TIMESTAMPTZ | now() |

## 17. product_tag_mapping

| Column | Type | Null |
|--------|------|------|
| product_id | BIGINT | FK → products(id) CASCADE |
| tag_id | BIGINT | FK → product_tags(id) CASCADE |

PK `(product_id, tag_id)`.

## 18. product_categories

| Column | Type | Null |
|--------|------|------|
| product_id | BIGINT | FK → products(id) CASCADE |
| category_id | BIGINT | FK → categories(id) CASCADE |

PK `(product_id, category_id)`, index `idx_pc_category`.

## 19. product_collections

| Column | Type | Null |
|--------|------|------|
| product_id | BIGINT | FK → products(id) CASCADE |
| collection_id | BIGINT | FK → collections(id) CASCADE |

PK `(product_id, collection_id)`.

## 20. warehouses

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| code | VARCHAR(32) | NO | UNIQUE — e.g. HCM-01 |
| name | VARCHAR(255) | NO | — |
| address | VARCHAR(512) | YES | — |
| city | VARCHAR(100) | YES | — |
| is_active | BOOLEAN | NO | default true |
| deleted_at | TIMESTAMPTZ | YES | — |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

## 21. warehouse_inventory

| Column | Type | Null | Default |
|--------|------|------|---------|
| variant_id | BIGINT | NO | FK → product_variants(id) CASCADE |
| warehouse_id | BIGINT | NO | FK → warehouses(id) CASCADE |
| quantity_on_hand | INT | NO | default 0 CHECK >=0 |
| quantity_reserved | INT | NO | default 0 CHECK >=0 |
| version | INT | NO | default 0 — optimistic lock bổ sung |
| updated_at | TIMESTAMPTZ | NO | now() |

PK `(variant_id, warehouse_id)`.  
Derived: `quantity_available = quantity_on_hand - quantity_reserved` (không lưu, tính ở query/service).  
Pessimistic lock target cho reservation.

## 22. inventory_transactions

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| variant_id | BIGINT | NO | FK → product_variants(id) |
| warehouse_id | BIGINT | NO | FK → warehouses(id) |
| type | VARCHAR(32) | NO | CHECK IN ('PURCHASE','SALE','RETURN','ADJUSTMENT','TRANSFER_IN','TRANSFER_OUT','DAMAGE','RESERVATION','RELEASE') |
| quantity | INT | NO | — signed: positive for in, negative for out (RESERVATION/RELEASE là 0 net nhưng vẫn log) |
| reference_type | VARCHAR(32) | YES | e.g. ORDER, TRANSFER, MANUAL |
| reference_id | BIGINT | YES | — |
| note | VARCHAR(512) | YES | — |
| created_by | BIGINT | YES | FK → users(id) |
| created_at | TIMESTAMPTZ | NO | now() |

Indexes: `idx_itrans_variant`, `idx_itrans_warehouse`, `idx_itrans_type`, `idx_itrans_created`. No updated_at (append-only).

## 23. inventory_reservations

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| variant_id | BIGINT | NO | FK → product_variants(id) |
| warehouse_id | BIGINT | NO | FK → warehouses(id) |
| order_id | BIGINT | YES | FK → orders(id) SET NULL |
| cart_id | BIGINT | YES | FK → carts(id) SET NULL |
| quantity | INT | NO | CHECK >0 |
| status | VARCHAR(32) | NO | default 'ACTIVE' CHECK IN ('ACTIVE','CONFIRMED','RELEASED','EXPIRED') |
| expires_at | TIMESTAMPTZ | NO | — default now()+15m |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

Indexes: `idx_ires_variant`, `idx_ires_order`, `idx_ires_status_expires`.

## 24. inventory_transfers

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| transfer_number | VARCHAR(32) | NO | UNIQUE — e.g. TRF-20260906-0001 |
| from_warehouse_id | BIGINT | NO | FK → warehouses(id) |
| to_warehouse_id | BIGINT | NO | FK → warehouses(id) CHECK != from |
| status | VARCHAR(32) | NO | default 'PENDING' CHECK IN ('PENDING','IN_TRANSIT','COMPLETED','CANCELLED') |
| created_by | BIGINT | YES | FK → users(id) |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

## 25. inventory_transfer_items

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| transfer_id | BIGINT | NO | FK → inventory_transfers(id) CASCADE |
| variant_id | BIGINT | NO | FK → product_variants(id) |
| quantity | INT | NO | CHECK >0 |
| created_at | TIMESTAMPTZ | NO | now() |

Index `idx_titems_transfer`.

## 26. carts

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| customer_id | BIGINT | YES | FK → customers(id) CASCADE — NULL for guest |
| guest_id | VARCHAR(64) | YES | — unique for guest |
| status | VARCHAR(32) | NO | default 'ACTIVE' CHECK IN ('ACTIVE','CONVERTED','ABANDONED') |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

Indexes: `idx_carts_customer`, `idx_carts_guest` unique where guest_id not null.

## 27. cart_items

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| cart_id | BIGINT | NO | FK → carts(id) CASCADE |
| variant_id | BIGINT | NO | FK → product_variants(id) |
| quantity | INT | NO | CHECK >0 AND <= 10 |
| added_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

Unique `(cart_id, variant_id)`, index `idx_citems_variant`.

## 28. wishlists

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| customer_id | BIGINT | NO | UNIQUE FK → customers(id) CASCADE |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

## 29. wishlist_items

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| wishlist_id | BIGINT | NO | FK → wishlists(id) CASCADE |
| product_id | BIGINT | NO | FK → products(id) CASCADE — wishlist theo product (variant chọn sau) |
| variant_id | BIGINT | YES | FK → product_variants(id) SET NULL |
| added_at | TIMESTAMPTZ | NO | now() |

Unique `(wishlist_id, product_id)`.

## 30. addresses — đã định nghĩa ở §8 (dùng chung).

## 31. orders

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| order_number | VARCHAR(32) | NO | UNIQUE — e.g. NVA-20260906-000001 |
| customer_id | BIGINT | NO | FK → customers(id) |
| status | VARCHAR(32) | NO | default 'PENDING' CHECK IN ('PENDING','CONFIRMED','PROCESSING','PACKED','SHIPPED','DELIVERED','CANCELLED','REFUNDED') |
| subtotal | NUMERIC(12,2) | NO | — |
| discount_total | NUMERIC(12,2) | NO | default 0 |
| shipping_fee | NUMERIC(12,2) | NO | default 0 |
| tax_total | NUMERIC(12,2) | NO | default 0 |
| grand_total | NUMERIC(12,2) | NO | — |
| currency | VARCHAR(3) | NO | default 'VND' |
| coupon_code | VARCHAR(64) | YES | snapshot |
| shipping_address | JSONB | NO | snapshot of address |
| billing_address | JSONB | YES | — |
| notes | VARCHAR(512) | YES | — |
| cancelled_reason | VARCHAR(512) | YES | — |
| version | INT | NO | default 0 — optimistic lock |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

Indexes: `idx_orders_customer`, `idx_orders_status`, `idx_orders_number`, `idx_orders_created`.

## 32. order_items

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| order_id | BIGINT | NO | FK → orders(id) CASCADE |
| product_id | BIGINT | NO | — snapshot FK (không enforce FK để giữ history) |
| variant_id | BIGINT | NO | — snapshot FK |
| product_name | VARCHAR(255) | NO | snapshot |
| sku | VARCHAR(64) | NO | snapshot |
| variant_label | VARCHAR(255) | YES | e.g. BLACK / 42 |
| image_url | VARCHAR(512) | YES | snapshot |
| unit_price | NUMERIC(12,2) | NO | snapshot at order time |
| quantity | INT | NO | CHECK >0 |
| line_total | NUMERIC(12,2) | NO | unit_price * quantity |
| created_at | TIMESTAMPTZ | NO | now() |

Index `idx_oitems_order`.

## 33. payments

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| order_id | BIGINT | NO | UNIQUE FK → orders(id) |
| payment_number | VARCHAR(32) | NO | UNIQUE |
| provider | VARCHAR(32) | NO | default 'MOCK' CHECK IN ('MOCK','VNPAY','MOMO','STRIPE','COD') |
| method | VARCHAR(32) | YES | e.g. CARD, WALLET, COD |
| status | VARCHAR(32) | NO | default 'PENDING' CHECK IN ('PENDING','AUTHORIZED','PAID','FAILED','REFUNDED','CANCELLED') |
| amount | NUMERIC(12,2) | NO | — phải = grand_total |
| currency | VARCHAR(3) | NO | default 'VND' |
| idempotency_key | VARCHAR(64) | YES | UNIQUE WHERE NOT NULL |
| provider_ref | VARCHAR(255) | YES | — gateway transaction id |
| paid_at | TIMESTAMPTZ | YES | — |
| failed_reason | VARCHAR(512) | YES | — |
| version | INT | NO | default 0 — optimistic lock |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

Indexes: `idx_payments_order`, `idx_payments_status`.

## 34. shipments

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| order_id | BIGINT | NO | FK → orders(id) CASCADE |
| warehouse_id | BIGINT | YES | FK → warehouses(id) SET NULL |
| carrier | VARCHAR(64) | YES | e.g. GHN, GHTK |
| tracking_number | VARCHAR(64) | YES | UNIQUE WHERE NOT NULL |
| status | VARCHAR(32) | NO | default 'PENDING' CHECK IN ('PENDING','PACKED','SHIPPED','IN_TRANSIT','DELIVERED','RETURNED','CANCELLED') |
| shipped_at | TIMESTAMPTZ | YES | — |
| delivered_at | TIMESTAMPTZ | YES | — |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

## 35. reviews

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| product_id | BIGINT | NO | FK → products(id) CASCADE |
| customer_id | BIGINT | NO | FK → customers(id) CASCADE |
| order_id | BIGINT | YES | FK → orders(id) SET NULL — verified purchase |
| rating | SMALLINT | NO | CHECK 1..5 |
| title | VARCHAR(255) | YES | — |
| content | TEXT | YES | — |
| status | VARCHAR(32) | NO | default 'PENDING' CHECK IN ('PENDING','APPROVED','REJECTED') |
| is_verified_purchase | BOOLEAN | NO | default false |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

Indexes: `idx_reviews_product`, `idx_reviews_customer`, `idx_reviews_status`, unique `(product_id, customer_id, order_id)` where order_id not null để tránh duplicate.

## 36. coupons

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| code | VARCHAR(64) | NO | UNIQUE — e.g. NOVA10 |
| name | VARCHAR(255) | NO | — |
| description | TEXT | YES | — |
| discount_type | VARCHAR(32) | NO | CHECK IN ('PERCENTAGE','FIXED_AMOUNT','FREE_SHIPPING') |
| discount_value | NUMERIC(12,2) | NO | — |
| min_order_amount | NUMERIC(12,2) | YES | — |
| max_discount_amount | NUMERIC(12,2) | YES | for percentage cap |
| usage_limit | INT | YES | NULL = unlimited |
| usage_count | INT | NO | default 0 |
| per_customer_limit | INT | YES | — |
| starts_at | TIMESTAMPTZ | YES | — |
| ends_at | TIMESTAMPTZ | YES | — |
| is_active | BOOLEAN | NO | default true |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

## 37. coupon_usages

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| coupon_id | BIGINT | NO | FK → coupons(id) |
| customer_id | BIGINT | NO | FK → customers(id) |
| order_id | BIGINT | NO | FK → orders(id) |
| discount_applied | NUMERIC(12,2) | NO | — |
| used_at | TIMESTAMPTZ | NO | default now() |

Unique `(coupon_id, order_id)`, index `idx_cusage_customer`.

## 38. notifications

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| user_id | BIGINT | NO | FK → users(id) CASCADE |
| type | VARCHAR(64) | NO | e.g. ORDER_CONFIRMED, PAYMENT_FAILED, REVIEW_APPROVED |
| title | VARCHAR(255) | NO | — |
| body | TEXT | YES | — |
| data | JSONB | YES | — e.g. {orderId: 123} |
| is_read | BOOLEAN | NO | default false |
| created_at | TIMESTAMPTZ | NO | now() |

Index `idx_notifications_user_read`, `idx_notifications_created`.

## 39. media

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| key | VARCHAR(512) | NO | UNIQUE — MinIO object key |
| url | VARCHAR(512) | NO | — public/presigned url |
| original_name | VARCHAR(255) | YES | — |
| mime_type | VARCHAR(100) | NO | — |
| size_bytes | BIGINT | NO | — |
| uploaded_by | BIGINT | YES | FK → users(id) SET NULL |
| created_at | TIMESTAMPTZ | NO | now() |
| updated_at | TIMESTAMPTZ | NO | now() |

## 40. recently_viewed

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| customer_id | BIGINT | NO | FK → customers(id) CASCADE |
| product_id | BIGINT | NO | FK → products(id) CASCADE |
| viewed_at | TIMESTAMPTZ | NO | default now() |

Unique `(customer_id, product_id)`, index `idx_rv_customer_viewed`.

## 41. audit_logs

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| user_id | BIGINT | YES | FK → users(id) SET NULL |
| action | VARCHAR(64) | NO | e.g. PRODUCT_CREATE, ORDER_STATUS_CHANGE |
| entity_type | VARCHAR(64) | NO | e.g. Product, Order |
| entity_id | VARCHAR(64) | YES | — |
| old_value | JSONB | YES | — |
| new_value | JSONB | YES | — |
| ip_address | VARCHAR(45) | YES | — |
| user_agent | VARCHAR(255) | YES | — |
| created_at | TIMESTAMPTZ | NO | default now() |

No updated_at (append-only). Partition by range `created_at` monthly.

## 42. store_settings

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK — single row hoặc per-key |
| key | VARCHAR(100) | NO | UNIQUE — e.g. SHIPPING_FEE, TAX_RATE |
| value | JSONB | NO | — |
| updated_by | BIGINT | YES | FK → users(id) |
| updated_at | TIMESTAMPTZ | NO | now() |
| created_at | TIMESTAMPTZ | NO | now() |

## 43. idempotency_keys

| Column | Type | Null | Default |
|--------|------|------|---------|
| id | BIGSERIAL | NO | PK |
| key | VARCHAR(64) | NO | UNIQUE |
| method | VARCHAR(16) | NO | e.g. POST |
| path | VARCHAR(255) | NO | — |
| response_status | INT | YES | — |
| response_body | JSONB | YES | — |
| created_at | TIMESTAMPTZ | NO | now() |
| expires_at | TIMESTAMPTZ | NO | default now()+24h |

---

## Liquibase Master

```
db/changelog/db.changelog-master.xml
├── 001-create-users-roles-permissions.xml
├── 002-create-customers-employees-addresses.xml
├── 003-create-categories-collections.xml
├── 004-create-products-variants-images-attributes-tags.xml
├── 005-create-warehouses-inventory.xml
├── 006-create-carts-wishlists.xml
├── 007-create-orders-payments-shipments.xml
├── 008-create-coupons-reviews-notifications.xml
├── 009-create-media-recently-viewed-audit-settings.xml
├── 010-create-idempotency-keys.xml
└── 011-seed-roles-permissions-admin.xml
```

Seed: roles + permissions + SUPER_ADMIN user (email từ env).
