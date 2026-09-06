# NOVA ATHLETICS — API Contract

> Base path: `/api/v1`  
> Content-Type: `application/json`  
> Auth: `Authorization: Bearer <accessToken>` (trừ endpoint public)  
> Pagination: `page` (0-index, default 0), `size` (default 24, max 100), `sort` (e.g. `createdAt,desc`)  
> Response envelope: `{ data: T, meta?: { page, size, totalElements, totalPages } }`  
> Error envelope: `{ error: { code, message, details, traceId } }` — xem `docs/api.md#16`

---

## 13. Public Endpoints — Customer Storefront

### 13.1 Auth

#### POST /api/v1/auth/register
- **Auth:** none
- **Body:**
```json
{
  "email": "user@example.com",
  "password": "Str0ng!Pass",
  "firstName": "Tien",
  "lastName": "Nguyen",
  "phone": "0901234567"
}
```
- **Validation:** email format, password 8-64, contains upper/lower/digit/special.
- **200 OK:**
```json
{
  "data": {
    "id": 1,
    "email": "user@example.com",
    "customer": { "id": 1, "firstName": "Tien" },
    "accessToken": "eyJ...",
    "refreshToken": "eyJ...",
    "expiresIn": 900
  }
}
```
- **Errors:** `EMAIL_ALREADY_EXISTS (409)`, `VALIDATION_ERROR (400)`.

#### POST /api/v1/auth/login
- **Auth:** none
- **Body:** `{ "email": "...", "password": "..." }`
- **200:** same as register + set `refreshToken` httpOnly cookie (Secure, SameSite=Lax, 7d).
- **Errors:** `INVALID_CREDENTIALS (401)`, `ACCOUNT_LOCKED (403)`, `ACCOUNT_DISABLED (403)`.

#### POST /api/v1/auth/refresh
- **Auth:** refreshToken (cookie hoặc body)
- **Body:** `{ "refreshToken": "..." }` (nếu không dùng cookie)
- **200:** `{ "data": { "accessToken": "...", "expiresIn": 900, "refreshToken": "..." } }` — rotation: old refresh invalidated.
- **Errors:** `INVALID_REFRESH_TOKEN (401)`, `REFRESH_TOKEN_EXPIRED (401)`.

#### POST /api/v1/auth/logout
- **Auth:** required
- **200:** `{ "data": { "message": "Logged out" } }` — revoke refresh, clear cookie.

#### GET /api/v1/auth/me
- **Auth:** required
- **200:** `{ "data": { "user": {...}, "customer": {...}, "roles": ["CUSTOMER"], "permissions": [...] } }`

#### POST /api/v1/auth/forgot-password
- **Auth:** none
- **Body:** `{ "email": "..." }`
- **200:** `{ "data": { "message": "If email exists, reset link sent" } }` (không leak existence).

#### POST /api/v1/auth/reset-password
- **Auth:** none
- **Body:** `{ "token": "...", "newPassword": "..." }`
- **200:** `{ "data": { "message": "Password reset" } }`

---

### 13.2 Products

#### GET /api/v1/products
- **Auth:** public
- **Query:**
  - `page`, `size`, `sort` (allowed: `createdAt`, `basePrice`, `name`, `isFeatured`)
  - `category` (slug), `collection` (slug), `gender` (MEN/WOMEN/KIDS/UNISEX), `sport`, `brand`
  - `size` (variant size), `color`, `minPrice`, `maxPrice`, `availability` (IN_STOCK boolean), `q` (keyword), `isFeatured`
  - `status` — only for admin, customer chỉ thấy ACTIVE
- **200:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "NOVA AIR RUNNER",
      "slug": "nova-air-runner",
      "subtitle": "Lightweight daily trainer",
      "brand": "NOVA",
      "gender": "MEN",
      "sport": "RUNNING",
      "basePrice": "2990000.00",
      "salePrice": "2490000.00",
      "currency": "VND",
      "isFeatured": true,
      "primaryImage": "https://cdn/.../nar-01.jpg",
      "categories": [{ "id": 1, "name": "Running", "slug": "running" }],
      "collections": [],
      "availableSizes": ["40","41","42"],
      "availableColors": [{ "color": "BLACK", "hex": "#000000" }],
      "inStock": true
    }
  ],
  "meta": { "page": 0, "size": 24, "totalElements": 500, "totalPages": 21 }
}
```
- **Cache:** Redis `product:list:{hash(query)}` TTL 5m.

#### GET /api/v1/products/{slug}
- **Auth:** public
- **Path:** `slug` (or numeric id fallback via `id:{id}`).
- **200:**
```json
{
  "data": {
    "id": 1,
    "name": "NOVA AIR RUNNER",
    "slug": "nova-air-runner",
    "description": "...",
    "images": [{ "url": "...", "altText": "...", "sortOrder": 0, "isPrimary": true }],
    "variants": [
      { "id": 10, "sku": "NAR-BLK-40", "size": "40", "color": "BLACK", "colorHex": "#000000", "price": "2990000.00", "available": 12, "isActive": true }
    ],
    "attributes": [{ "name": "MATERIAL", "value": "Mesh" }],
    "tags": ["breathable"],
    "categories": [],
    "collections": [],
    "reviewsSummary": { "averageRating": 4.6, "totalReviews": 128 }
  }
}
```
- **Errors:** `PRODUCT_NOT_FOUND (404)`.

### 13.3 Categories

#### GET /api/v1/categories
- **Auth:** public
- **200:** `{ "data": [ { "id":1, "name":"Running", "slug":"running", "parentId": null, "children": [...] } ] }` — cây.

#### GET /api/v1/categories/{slug}
- **Auth:** public
- **200:** `{ "data": { "id":1, "name":"Running", "slug":"running", "products": { "data": [...], "meta": {...} } } }` — kèm product page.

### 13.4 Collections

#### GET /api/v1/collections
- **Auth:** public
- **200:** list collections active.

#### GET /api/v1/collections/{slug}
- **Auth:** public
- **200:** detail + products page.

### 13.5 Search

#### GET /api/v1/search
- **Auth:** public
- **Query:** `q` (required, 1-100 chars), `page`, `size`, `sort`, `category`, `gender`, `minPrice`, `maxPrice`, `color`, `size`
- **Search fields:** name, subtitle, description, SKU, category name, collection name, tags — via Postgres `tsvector` + `ILIKE` fallback cho SKU.
- **200:** same envelope as products, thêm `meta.query` và `facets`:
```json
{
  "data": [...],
  "meta": { "page":0,"size":24,"totalElements":42,"totalPages":2, "query":"air runner" },
  "facets": {
    "categories": [{ "slug":"running", "count": 30 }],
    "genders": [{ "value":"MEN","count":20 }],
    "priceRanges": [{ "min":0,"max":3000000,"count":15 }]
  }
}
```
- **Errors:** `VALIDATION_ERROR` nếu thiếu `q`.

### 13.6 Cart

#### GET /api/v1/cart
- **Auth:** optional (guest via `X-Guest-Id` header hoặc cookie `guest_id`)
- **200:**
```json
{
  "data": {
    "id": 1,
    "items": [
      { "id": 10, "variant": { "id": 5, "sku":"NAR-BLK-40", "productName":"NOVA AIR RUNNER", "size":"40","color":"BLACK","price":"2990000.00","imageUrl":"..." }, "quantity": 2, "lineTotal":"5980000.00" }
    ],
    "subtotal": "5980000.00",
    "itemCount": 2
  }
}
```

#### POST /api/v1/cart/items
- **Auth:** optional (guest OK)
- **Headers:** `Idempotency-Key` optional
- **Body:** `{ "variantId": 5, "quantity": 2 }`
- **Validation:** variant exists & active, quantity 1-10, `available >= quantity` (check inventory).
- **201:** updated cart.
- **Errors:** `VARIANT_NOT_FOUND (404)`, `OUT_OF_STOCK (409)`, `VALIDATION_ERROR (400)`.

#### PATCH /api/v1/cart/items/{id}
- **Body:** `{ "quantity": 3 }`
- **200:** updated cart.

#### DELETE /api/v1/cart/items/{id}
- **204:** no content (or 200 với cart mới).

#### DELETE /api/v1/cart
- **Auth:** optional
- **204:** clear cart.

### 13.7 Wishlist

#### GET /api/v1/wishlist
- **Auth:** required
- **200:** `{ "data": { "items": [{ "id":1, "product": {...}, "variant": {...}, "addedAt":"..." }] } }`

#### POST /api/v1/wishlist/items
- **Auth:** required
- **Body:** `{ "productId": 1, "variantId": 5 }` (variantId optional)
- **201:** item.
- **Errors:** `WISHLIST_ITEM_EXISTS (409)` nếu đã có.

#### DELETE /api/v1/wishlist/items/{id}
- **Auth:** required — ownership check.
- **204.**

### 13.8 Checkout

#### POST /api/v1/checkout/validate
- **Auth:** required
- **Body:**
```json
{
  "shippingAddressId": 1,
  "couponCode": "NOVA10",
  "shippingMethod": "STANDARD"
}
```
- **200:**
```json
{
  "data": {
    "valid": true,
    "items": [{ "variantId":5,"quantity":2,"unitPrice":"2990000.00","available":12 }],
    "pricing": { "subtotal":"5980000.00","discount":"598000.00","shippingFee":"30000.00","grandTotal":"5412000.00" },
    "coupon": { "code":"NOVA10","discountType":"PERCENTAGE","discountValue":"10.00" }
  }
}
```
- **Errors:** `CART_EMPTY (400)`, `OUT_OF_STOCK (409)`, `COUPON_INVALID (400)`, `COUPON_EXPIRED (400)`.

#### POST /api/v1/checkout
- **Auth:** required
- **Headers:** `Idempotency-Key: <uuid>` **required**
- **Body:**
```json
{
  "shippingAddressId": 1,
  "billingAddressId": 1,
  "couponCode": "NOVA10",
  "paymentMethod": "MOCK",
  "idempotencyKey": "uuid",
  "notes": "..."
}
```
- **Flow:** validate cart → reserve inventory (pessimistic) → create order PENDING → create payment PENDING → return order.
- **201:**
```json
{
  "data": {
    "order": { "id": 100, "orderNumber":"NVA-20260906-000100","status":"PENDING","grandTotal":"5412000.00" },
    "payment": { "id": 50, "status":"PENDING","provider":"MOCK","amount":"5412000.00" }
  }
}
```
- **Errors:** `OUT_OF_STOCK (409)`, `IDEMPOTENCY_CONFLICT (409)` nếu key trùng với response khác.

### 13.9 Orders (Customer)

#### GET /api/v1/orders
- **Auth:** required — chỉ orders của chính customer
- **Query:** `page`, `size`, `sort`, `status`
- **200:** `{ "data": [{ "id":100,"orderNumber":"...","status":"CONFIRMED","grandTotal":"...","createdAt":"..." }], "meta": {...} }`

#### GET /api/v1/orders/{id}
- **Auth:** required — ownership check (customer chỉ xem order của mình)
- **200:**
```json
{
  "data": {
    "id": 100,
    "orderNumber": "NVA-20260906-000100",
    "status": "CONFIRMED",
    "items": [{ "productName":"NOVA AIR RUNNER","sku":"NAR-BLK-40","variantLabel":"BLACK / 40","unitPrice":"2990000.00","quantity":2,"lineTotal":"5980000.00","imageUrl":"..." }],
    "pricing": {...},
    "shippingAddress": {...},
    "payment": { "status":"PAID","provider":"MOCK","paidAt":"..." },
    "shipment": { "status":"PENDING","trackingNumber": null },
    "createdAt": "..."
  }
}
```
- **Errors:** `ORDER_NOT_FOUND (404)`, `FORBIDDEN (403)` nếu không sở hữu.

#### POST /api/v1/orders/{id}/cancel
- **Auth:** required — ownership
- **Body:** `{ "reason": "Changed mind" }`
- **200:** `{ "data": { "status":"CANCELLED" } }`
- **Rule:** chỉ khi `status IN (PENDING, CONFIRMED)` — sau đó release inventory.
- **Errors:** `ORDER_CANNOT_CANCEL (409)`.

### 13.10 Reviews

#### GET /api/v1/products/{id}/reviews
- **Auth:** public
- **Query:** `page`, `size`, `sort` (newest, rating), `rating` (1-5 filter)
- **200:** `{ "data": [{ "id":1,"rating":5,"title":"...","content":"...","customerName":"T***","isVerifiedPurchase":true,"createdAt":"..." }], "meta": {...}, "summary": { "averageRating":4.6,"totalReviews":128,"distribution":{ "5":80,"4":30 } } }`

#### POST /api/v1/products/{id}/reviews
- **Auth:** required
- **Body:** `{ "rating": 5, "title":"Great!", "content":"...", "orderId": 100 }` (orderId optional để verify purchase)
- **201:** review (status PENDING, chờ moderation nếu cần).
- **Errors:** `ALREADY_REVIEWED (409)` nếu đã review product này với cùng order, `ORDER_NOT_VERIFIED (400)` nếu order không chứa product.

#### PATCH /api/v1/reviews/{id}
- **Auth:** required — owner only
- **Body:** `{ "rating":4, "content":"..." }`
- **200.**

#### DELETE /api/v1/reviews/{id}
- **Auth:** required — owner hoặc REVIEW_MANAGE permission.

### 13.11 Customer Account

#### GET /api/v1/account/profile
- **Auth:** required
- **200:** customer profile.

#### PUT /api/v1/account/profile
- **Body:** `{ "firstName":"...","lastName":"...","phone":"...","dateOfBirth":"...","gender":"MALE" }`

#### GET /api/v1/account/addresses
#### POST /api/v1/account/addresses
#### PUT /api/v1/account/addresses/{id}
#### DELETE /api/v1/account/addresses/{id}

#### GET /api/v1/account/notifications
- **Query:** `page`, `size`, `isRead`
- **200:** list inbox.

#### PATCH /api/v1/account/notifications/{id}/read
#### POST /api/v1/account/notifications/read-all

#### GET /api/v1/account/recently-viewed
- **200:** list 20 sản phẩm vừa xem.

---

## 14. Admin Endpoints

> Tất cả `/api/v1/admin/**` yêu cầu `Authorization` + RBAC permission. Prefix `ADMIN` được enforce bởi `JwtAuthFilter` + `@PreAuthorize`.

### Common query: `page`, `size`, `sort`, filters riêng.

### 14.1 Products Admin

| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | /admin/products | PRODUCT_READ | List với filter status, search, pagination |
| POST | /admin/products | PRODUCT_CREATE | Create product + variants + images |
| GET | /admin/products/{id} | PRODUCT_READ | Detail đầy đủ |
| PUT | /admin/products/{id} | PRODUCT_UPDATE | Update product + variants |
| DELETE | /admin/products/{id} | PRODUCT_DELETE | Soft delete |
| POST | /admin/products/{id}/images | PRODUCT_UPDATE | Upload images (multipart, presigned) |
| PUT | /admin/products/{id}/variants/{variantId} | PRODUCT_UPDATE | Update single variant |
| PATCH | /admin/products/{id}/status | PRODUCT_UPDATE | Change status (DRAFT→ACTIVE etc) |

**POST /admin/products body:**
```json
{
  "name": "NOVA AIR RUNNER",
  "slug": "nova-air-runner",
  "subtitle": "Lightweight trainer",
  "description": "...",
  "brand": "NOVA",
  "gender": "MEN",
  "sport": "RUNNING",
  "basePrice": "2990000.00",
  "salePrice": "2490000.00",
  "categoryIds": [1,2],
  "collectionIds": [1],
  "tagIds": [1],
  "attributes": [{ "attributeId": 1, "value": "Mesh" }],
  "variants": [
    { "sku":"NAR-BLK-40","size":"40","color":"BLACK","colorHex":"#000000","priceOverride": null,"weightGrams": 280 }
  ],
  "images": [{ "url":"https://...","altText":"...","sortOrder":0,"isPrimary":true }]
}
```

### 14.2 Categories Admin

| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | /admin/categories | CATEGORY_MANAGE | Tree hoặc flat list |
| POST | /admin/categories | CATEGORY_MANAGE | Create |
| PUT | /admin/categories/{id} | CATEGORY_MANAGE | Update |
| DELETE | /admin/categories/{id} | CATEGORY_MANAGE | Soft delete |
| PATCH | /admin/categories/{id}/move | CATEGORY_MANAGE | Move parent |

### 14.3 Collections Admin

| Method | Path | Permission |
|--------|------|------------|
| GET | /admin/collections | COLLECTION_MANAGE |
| POST | /admin/collections | COLLECTION_MANAGE |
| PUT | /admin/collections/{id} | COLLECTION_MANAGE |
| DELETE | /admin/collections/{id} | COLLECTION_MANAGE |

### 14.4 Orders Admin

| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | /admin/orders | ORDER_READ | Filter status, date range, customer, search orderNumber |
| GET | /admin/orders/{id} | ORDER_READ | Detail full |
| PATCH | /admin/orders/{id}/status | ORDER_UPDATE | Transition state machine |
| POST | /admin/orders/{id}/refund | ORDER_REFUND | Refund (kèm reason) |
| POST | /admin/orders/{id}/cancel | ORDER_CANCEL | Cancel by admin |

**PATCH status body:** `{ "status": "PROCESSING", "reason": "..." }` — validate transition.

### 14.5 Inventory Admin

| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | /admin/inventory | INVENTORY_READ | List warehouse_inventory với filter warehouse, product, lowStock |
| GET | /admin/inventory/transactions | INVENTORY_READ | History với filter variant, type, date |
| POST | /admin/inventory/adjustments | INVENTORY_UPDATE | `{ "variantId":1,"warehouseId":1,"type":"ADJUSTMENT","quantity":10,"note":"..." }` |
| POST | /admin/inventory/transfers | INVENTORY_TRANSFER | Create transfer |
| GET | /admin/inventory/transfers | INVENTORY_READ | List transfers |
| PATCH | /admin/inventory/transfers/{id}/status | INVENTORY_TRANSFER | IN_TRANSIT → COMPLETED |

### 14.6 Warehouses Admin

| Method | Path | Permission |
|--------|------|------------|
| GET | /admin/warehouses | INVENTORY_READ |
| POST | /admin/warehouses | INVENTORY_UPDATE |
| PUT | /admin/warehouses/{id} | INVENTORY_UPDATE |
| DELETE | /admin/warehouses/{id} | INVENTORY_UPDATE (soft deactivate) |

### 14.7 Customers Admin

| Method | Path | Permission |
|--------|------|------------|
| GET | /admin/customers | CUSTOMER_READ |
| GET | /admin/customers/{id} | CUSTOMER_READ |
| PATCH | /admin/customers/{id}/status | CUSTOMER_UPDATE |

### 14.8 Employees Admin

| Method | Path | Permission |
|--------|------|------------|
| GET | /admin/employees | EMPLOYEE_READ |
| POST | /admin/employees | EMPLOYEE_MANAGE |
| PUT | /admin/employees/{id} | EMPLOYEE_MANAGE |
| DELETE | /admin/employees/{id} | EMPLOYEE_MANAGE |
| PATCH | /admin/employees/{id}/roles | EMPLOYEE_MANAGE |

### 14.9 Promotions Admin

| Method | Path | Permission |
|--------|------|------------|
| GET | /admin/promotions | PROMOTION_MANAGE |
| POST | /admin/promotions | PROMOTION_MANAGE |
| PUT | /admin/promotions/{id} | PROMOTION_MANAGE |
| DELETE | /admin/promotions/{id} | PROMOTION_MANAGE |
| GET | /admin/promotions/{id}/usages | PROMOTION_MANAGE |

**POST promotion body:**
```json
{
  "code": "NOVA10",
  "name": "10% off",
  "discountType": "PERCENTAGE",
  "discountValue": "10.00",
  "minOrderAmount": "500000.00",
  "maxDiscountAmount": "500000.00",
  "usageLimit": 100,
  "perCustomerLimit": 1,
  "startsAt": "2026-09-06T00:00:00Z",
  "endsAt": "2026-09-30T23:59:59Z"
}
```

### 14.10 Reviews Admin

| Method | Path | Permission |
|--------|------|------------|
| GET | /admin/reviews | REVIEW_MANAGE |
| PATCH | /admin/reviews/{id}/status | REVIEW_MANAGE — `{ "status":"APPROVED" }` |
| DELETE | /admin/reviews/{id} | REVIEW_MANAGE |

### 14.11 Reports & Analytics

| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | /admin/reports/revenue | REPORT_READ | Query `from`, `to`, `granularity` (DAY/WEEK/MONTH) |
| GET | /admin/reports/top-products | REPORT_READ | Top selling |
| GET | /admin/reports/inventory | REPORT_READ | Low stock, out of stock |
| GET | /admin/reports/orders | REPORT_READ | Count by status |

### 14.12 Audit Logs

| Method | Path | Permission |
|--------|------|------------|
| GET | /admin/audit-logs | AUDIT_READ — filter `userId`, `entityType`, `action`, `from`, `to` |

### 14.13 Media

| Method | Path | Permission |
|--------|------|------------|
| POST | /admin/media/presign | PRODUCT_CREATE/UPDATE — `{ "fileName":"...","mimeType":"image/jpeg","size":12345 }` → presigned URL |
| GET | /admin/media | PRODUCT_READ |

### 14.14 Store Settings

| Method | Path | Permission |
|--------|------|------------|
| GET | /admin/settings | REPORT_READ |
| PUT | /admin/settings | ADMIN |

---

## 15. API Cross-Cutting Contract

### 15.1 Pagination
Request: `?page=0&size=24&sort=createdAt,desc`  
Response:
```json
{ "data": [...], "meta": { "page":0,"size":24,"totalElements":500,"totalPages":21 } }
```
`page` 0-index, `size` clamped 1-100.

### 15.2 Sorting
Whitelist per endpoint. Unknown field → `VALIDATION_ERROR`.

### 15.3 Filtering
Query params documented per endpoint. Multi-value: `?color=BLACK&color=WHITE`.

### 15.4 Idempotency
- Required for `POST /checkout`, optional for others mutating.
- Header `Idempotency-Key: <uuid v4>`.
- Server stores key 24h → duplicate request returns cached response (same status + body) instead of re-executing.

### 15.5 Rate Limiting
- Anonymous: 60 req/min per IP.
- Authenticated: 120 req/min per user.
- Auth endpoints: 5 req/min per IP.
- Headers: `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`.
- 429 với `Retry-After`.

### 15.6 CORS
- Allowed origins từ env `CORS_ALLOWED_ORIGINS` (default `http://localhost:3000`).
- Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS.
- Headers: Authorization, Content-Type, Idempotency-Key, X-Request-Id.

### 15.7 Validation
- Jakarta Validation ở DTO + Zod ở FE.
- 400 với `details` là field errors:
```json
{ "error": { "code":"VALIDATION_ERROR","message":"Validation failed","details":{"fieldErrors":{"email":"must be valid email"}},"traceId":"..." } }
```

---

## 16. Error Contract

### Envelope
```json
{
  "error": {
    "code": "PRODUCT_NOT_FOUND",
    "message": "Product not found",
    "details": null,
    "traceId": "a1b2c3d4-..."
  }
}
```

### Common Error Codes

| Code | HTTP | Meaning |
|------|------|---------|
| VALIDATION_ERROR | 400 | Bean validation / Zod fail |
| UNAUTHORIZED | 401 | Missing/invalid JWT |
| FORBIDDEN | 403 | RBAC deny / ownership fail |
| NOT_FOUND | 404 | Generic |
| PRODUCT_NOT_FOUND | 404 | — |
| VARIANT_NOT_FOUND | 404 | — |
| CATEGORY_NOT_FOUND | 404 | — |
| COLLECTION_NOT_FOUND | 404 | — |
| ORDER_NOT_FOUND | 404 | — |
| CUSTOMER_NOT_FOUND | 404 | — |
| COUPON_NOT_FOUND | 404 | — |
| EMAIL_ALREADY_EXISTS | 409 | — |
| OUT_OF_STOCK | 409 | Inventory insufficient |
| ORDER_CANNOT_CANCEL | 409 | State machine deny |
| ORDER_INVALID_TRANSITION | 409 | — |
| PAYMENT_INVALID_TRANSITION | 409 | — |
| WISHLIST_ITEM_EXISTS | 409 | — |
| ALREADY_REVIEWED | 409 | — |
| COUPON_INVALID | 400 | Expired / usage limit / min amount |
| COUPON_EXPIRED | 400 | — |
| CART_EMPTY | 400 | — |
| INVALID_CREDENTIALS | 401 | — |
| INVALID_REFRESH_TOKEN | 401 | — |
| REFRESH_TOKEN_EXPIRED | 401 | — |
| ACCOUNT_LOCKED | 403 | — |
| ACCOUNT_DISABLED | 403 | — |
| IDEMPOTENCY_CONFLICT | 409 | Key reuse with different payload |
| RATE_LIMIT_EXCEEDED | 429 | — |
| INTERNAL_ERROR | 500 | — |

### HTTP Status Mapping
- 200 OK — GET, PATCH success
- 201 Created — POST create
- 204 No Content — DELETE success
- 400 Bad Request — validation / business rule (coupon)
- 401 Unauthorized — auth fail
- 403 Forbidden — RBAC / ownership
- 404 Not Found
- 409 Conflict — state / duplicate / stock
- 429 Too Many Requests
- 500 Internal Server Error

---

## 17. OpenAPI

- Spec sinh tự động từ SpringDoc OpenAPI tại `/v3/api-docs` và Swagger UI `/swagger-ui.html` (chỉ dev).
- File tĩnh `docs/openapi.yaml` được commit như contract snapshot (generate từ code, fail CI nếu drift).

---

## 18. Security Notes (Never Trust Frontend)

Server là source of truth cho:
- `price`, `discount`, `stock`, `userId`, `orderTotal` — tính lại từ DB, không đọc từ request.
- `coupon` discount — validate server-side.
- Ownership: customer chỉ CRUD cart/wishlist/order của chính mình (check `customer_id = currentUser.customerId`).
- Admin: mọi `/admin/**` check permission, không chỉ role.
