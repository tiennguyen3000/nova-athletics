# NOVA ATHLETICS — System Architecture

> **Version:** 1.0.0 — PROMPT 1 Contract  
> **Date:** 2026-09-06  
> **Status:** CONTRACT — PROMPT 2/3/4 MUST follow without renaming tables, entities, endpoints, DTO fields, enums.

---

## 1. Overview

NOVA ATHLETICS là ecommerce sportswear production-grade lấy cảm hứng UX Nike — triển khai ORIGINAL, không copy asset/thương hiệu Nike.

**Tech stack (CONTRACT - không đổi):**

| Layer | Choice |
|-------|--------|
| Backend | Java 21, Spring Boot 3.x, Spring Security, Spring Data JPA, Hibernate, Liquibase, Maven, OpenAPI |
| DB | PostgreSQL 16 |
| Cache / Session | Redis 7 |
| Storage | MinIO (S3 compatible) |
| Frontend | Next.js 14 App Router, TypeScript, React, Tailwind CSS, shadcn/ui, TanStack Query, Zustand, React Hook Form, Zod |
| Infra | Docker Compose — postgres, redis, minio, backend, frontend |
| Future | Kafka, OpenSearch (abstraction sẵn, chưa bật) |
| Architecture | **Modular Monolith** — single deployable, module boundaries nghiêm ngặt, không 15 microservices |

---

## 2. High-Level Topology

```
Browser / Mobile Web
        │
        ▼
  Next.js Frontend (SSR + CSR)
        │  HTTPS / JSON
        ▼
  Spring Boot API — /api/v1  (Modular Monolith)
   ┌─────────────────────────────────────────────┐
   │  auth │ user │ customer │ product │ category│
   │  collection │ search │ cart │ wishlist │    │
   │  checkout │ order │ payment │ inventory │   │
   │  warehouse │ promotion │ review │ notification│
   │  employee │ analytics │ audit │ media       │
   └─────────────────────────────────────────────┘
        │              │              │
        ▼              ▼              ▼
   PostgreSQL      Redis          MinIO
   (source of      (cache,        (object
    truth)          session,       storage)
                    rate limit,
                    recently viewed)
```

### Request flow

1. Next.js gọi `GET /api/v1/...` kèm `Authorization: Bearer <accessToken>`.
2. Spring Security filter chain: JWT validation → SecurityContext → RBAC check (`@PreAuthorize`).
3. Controller → Service (domain logic, transaction) → Repository (JPA) → PostgreSQL.
4. Cache-aside qua Redis cho catalog/search/category.
5. Domain events (Spring `ApplicationEventPublisher`) → internal listeners (notification, audit, analytics). Future: Kafka bridge.
6. Upload media → MinIO (presigned URL).

---

## 3. Backend — Modular Monolith Layout

**Package root:** `com.novaathletics`

```
com.novaathletics
├── NovaAthleticsApplication.java
├── common/               # cross-cutting
│   ├── config/           # Security, JPA, Redis, Jackson, OpenAPI, CORS, MinIO
│   ├── error/            # GlobalExceptionHandler, ErrorCode, ApiError
│   ├── pagination/       # PageResponse<T>, Sort handling
│   ├── security/         # JwtProvider, JwtAuthFilter, UserPrincipal
│   └── audit/            # AuditableEntity, @Audited
├── modules/
│   ├── auth/
│   ├── user/             # User, Role, Permission, UserRole, RolePermission
│   ├── customer/         # Customer, Address
│   ├── employee/         # Employee
│   ├── product/          # Product, ProductVariant, ProductImage, ProductTag, Attribute
│   ├── category/
│   ├── collection/
│   ├── search/
│   ├── cart/
│   ├── wishlist/
│   ├── checkout/
│   ├── order/            # Order, OrderItem, Shipment
│   ├── payment/
│   ├── inventory/        # WarehouseInventory, InventoryTransaction, Reservation, Transfer
│   ├── warehouse/
│   ├── promotion/        # Coupon, CouponUsage
│   ├── review/
│   ├── notification/
│   ├── analytics/
│   ├── audit/
│   └── media/            # MinIO abstraction
└── resources/
    ├── db/migration/     # Liquibase changelogs
    └── application.yml
```

### Quy tắc module

- Mỗi module có `controller/`, `service/`, `repository/`, `entity/`, `dto/`, `mapper/`, `event/` riêng.
- **Không vòng tròn:** dependency graph là DAG (xem §5). Module chỉ import interface/event của module khác, không import entity trực tiếp cross-module (qua ID hoặc DTO).
- Shared kernel chỉ nằm ở `common/`.
- Liquibase: một changelog per module `db/changelog/<module>/001-...xml` aggregated trong `db.changelog-master.xml`.
- OpenAPI nhóm theo tag = module name.

---

## 4. Module Responsibilities

### 4.1 auth
- Register, login, refresh (rotation), logout, me.
- JWT access (15m) + refresh (7d, httpOnly cookie + DB whitelist).
- Password hashing (BCrypt 12), account status (ACTIVE/LOCKED/DISABLED), email verification & password reset abstraction (token table, mail sender interface).
- Phát `UserRegistered` event.

### 4.2 user
- Aggregate root `User` (credentials). Không chứa business profile.
- Quản Role/Permission, gán Role cho User.
- Cung cấp `UserPrincipal` cho Security.

### 4.3 customer
- Hồ sơ mua hàng: `Customer` 1-1 `User`, `Address` (ship/billing), purchase history view.
- Liên kết Order, Review, Wishlist, RecentlyViewed.

### 4.4 employee
- Hồ sơ nhân sự nội bộ: `Employee` 1-1 `User`, phòng ban, mã NV, trạng thái.
- CRUD chỉ ADMIN/SUPER_ADMIN.

### 4.5 product
- `Product` (SPU) + `ProductVariant` (SKU) + `ProductImage` + `ProductTag` + `ProductAttribute` (ví dụ size/color/material).
- Product KHÔNG chứa inventory trực tiếp (contract §6).
- Full-text search fields: name, subtitle, description, SKU, category, collection, tags.

### 4.6 category
- Cây danh mục hierarchical (self-join parent_id), slug unique.
- Cache Redis `category:tree` TTL 1h.

### 4.7 collection
- Bộ sưu tập marketing (New Arrivals, Summer 2026). Slug unique, product_collections join.

### 4.8 search
- Interface `SearchService` → `PostgresSearchService` (tsvector + GIN). Future swap `OpenSearchSearchService`.
- Fields: name, subtitle, description, SKU, category, collection, tags. Pagination + filters + sort.

### 4.9 cart
- `Cart` 1-N `CartItem` → `ProductVariant`. Guest cart (cookie id) + authenticated cart merge.
- Validate price/stock từ inventory trước checkout.
- Redis optional cho guest cart (TTL 30d).

### 4.10 wishlist
- `Wishlist` 1-N `WishlistItem` → Product (hoặc variant). Auth required.

### 4.11 checkout
- Orchestrator: validate cart → validate inventory → reserve inventory → create payment → confirm order.
- Idempotency-Key header cho POST /checkout.
- Pessimistic lock trên `warehouse_inventory`.

### 4.12 order
- `Order` + `OrderItem` (snapshot) + `Shipment`. State machine nghiêm ngặt.
- Không phụ thuộc dữ liệu Product hiện tại để hiển thị lịch sử.

### 4.13 payment
- `Payment` state machine, idempotency, webhook abstraction (mock provider trước, cổng `PaymentGateway`).
- Mỗi payment sinh transaction log, refund path.

### 4.14 inventory
- `warehouse_inventory` (variant × warehouse), `inventory_transactions`, `inventory_reservations`, `inventory_transfers`.
- Mọi mutation sinh transaction. Công thức `available = on_hand - reserved`.

### 4.15 warehouse
- `Warehouse` master, CRUD + stock view per warehouse.

### 4.16 promotion
- `Coupon` + `CouponUsage`. Validate tại checkout, áp dụng discount server-side, không tin frontend.

### 4.17 review
- `Review` gắn Product + Customer, rating 1-5, moderation (PENDING/APPROVED/REJECTED).

### 4.18 notification
- `Notification` inbox per user. Listener các event Order*/Payment*. Future: email/push via queue.

### 4.19 employee / RBAC
- RBAC matrix, permission enforcement tại controller/service.

### 4.20 analytics
- Read-model aggregations: doanh thu, top product, tồn kho. Query view, không mutate core.

### 4.21 audit
- `audit_logs` append-only. Mọi admin mutation tự ghi (AOP interceptor).

### 4.22 media
- Abstraction `MediaService` → MinIO. Entity `media` lưu key, url, mime, size, uploader.
- Upload qua presigned URL, validate MIME & size server-side.

---

## 5. Module Dependency Graph (DAG — no cycles)

```
auth ──► user ──► customer
                └─► employee

product ──► category, collection, media, inventory (reads stock via interface)
search ──► product, category, collection
cart ──► product, inventory (availability), promotion (preview)
wishlist ──► product
checkout ──► cart, inventory, promotion, order, payment
order ──► customer, product (snapshot), payment, inventory, promotion
payment ──► order
inventory ──► product, warehouse
warehouse ──► (standalone)
promotion ──► order (coupon usage)
review ──► product, customer, order (verified purchase)
notification ◄── order, payment, review (events)
analytics ◄── order, product, inventory (read)
audit ◄── all admin modules (AOP)
```

Quy tắc: mũi tên A→B nghĩa A được phép dependency B. Không chiều ngược.

---

## 6. Cross-Cutting Concerns

### Security
- Stateless JWT, BCrypt, CORS whitelist (env), rate limiting (Redis + Bucket4j filter), validation (Jakarta + Zod), không trust price/discount/stock/userId/orderTotal từ FE.

### Validation & Error
- Unified `ApiError { code, message, details, traceId }`, traceId = `X-Request-Id` hoặc UUID.

### Pagination
- Query: `page` (0-index), `size` (default 24, max 100), `sort` (e.g. `createdAt,desc`), filters.
- Response: `{ data: [], meta: { page, size, totalElements, totalPages } }`.

### Idempotency
- Header `Idempotency-Key: <uuid>` cho `POST /checkout` và `POST /admin/...` mutating. Lưu `idempotency_keys` (key, response hash, TTL 24h).

### Concurrency
- `warehouse_inventory` pessimistic `SELECT ... FOR UPDATE` khi reserve/release.
- `orders`, `payments` optimistic `@Version` cho status transition.
- Idempotency keys ngăn double submit.

### Redis Strategy (không cache everything)
| Key pattern | TTL | Purpose |
|-------------|-----|---------|
| `product:{id}` / `product:slug:{slug}` | 10m | Catalog detail |
| `category:tree` | 1h | Category tree |
| `collection:{slug}` | 1h | Collection detail |
| `search:suggest:{q}` | 5m | Auto-suggest |
| `recently_viewed:{customerId}` | 30d (LIST, max 50) | Recently viewed |
| `cart:guest:{id}` | 30d | Guest cart |
| `rate_limit:{ip}:{endpoint}` | sliding window 1m | Rate limiting |
| `idempotency:{key}` | 24h | Idempotency |

### Observability
- Spring Actuator `/actuator/health`, Micrometer, structured JSON logs với traceId.
- Audit log cho admin.

---

## 7. Deployment — Docker Compose

```yaml
services:
  postgres: { image: postgres:16-alpine, ports: ["5432:5432"], volumes: [pgdata:/var/lib/postgresql/data] }
  redis:    { image: redis:7-alpine, ports: ["6379:6379"] }
  minio:    { image: minio/minio, ports: ["9000:9000","9001:9001"], command: server /data --console-address :9001 }
  backend:  { build: ./backend, ports: ["8080:8080"], depends_on: [postgres, redis, minio], env_file: .env }
  frontend: { build: ./frontend, ports: ["3000:3000"], depends_on: [backend] }
```

---

## 8. Architectural Decisions (ADR)

| # | Decision | Rationale | Trade-off |
|---|----------|-----------|-----------|
| ADR-01 | Modular monolith thay vì microservices | Team nhỏ, domain bounded chưa ổn định, deploy đơn giản, transaction ACID dễ | Scale theo module phải refactor sau nếu cần |
| ADR-02 | PostgreSQL full-text trước, OpenSearch sau | Đủ cho 10-100k SKU, GIN + tsvector, giảm infra ban đầu | Search relevance kém hơn OS ở fuzzy/synonym |
| ADR-03 | Liquibase thay Flyway | Rollback, changelog XML rõ ràng cho modular | Verbose hơn |
| ADR-04 | JWT stateless + refresh rotation + DB whitelist | Cân bằng UX & revoke capability | Thêm 1 lookup Redis/DB mỗi refresh |
| ADR-05 | Pessimistic lock cho inventory, optimistic cho order/payment | Inventory hot spot cần serialize, order ít contention | Pessimistic có thể queue ở flash sale |
| ADR-06 | MinIO thay S3 thật | Dev parity, Docker Compose self-contained | Prod cần migrate sang S3/R2 |
| ADR-07 | Snapshot order item thay live join | Lịch sử bất biến, product đổi giá không ảnh hưởng đơn cũ | Redundancy storage |

---

## 9. Risks & Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Overselling flash sale | Bán quá tồn | Pessimistic lock + `available` check trong transaction + reservation TTL 15m + job release expired |
| Double checkout / duplicate payment | Trùng đơn/trừ tiền double | Idempotency-Key + unique constraint order_number + payment idempotency |
| Price tampering từ FE | Thất thoát | Server tính total từ DB, không đọc price từ request |
| N+1 query catalog | P95 cao | EntityGraph / fetch join, pagination, Redis cache |
| Media upload abuse | Cost / XSS | MIME whitelist, size limit 10MB, virus scan hook, presigned URL expiry 5m |
| Audit log bloat | DB phình | Partition by month, retention 12m, archive to object storage |

---

## 10. References

- `docs/database.md` — full DDL spec
- `docs/erd.md` — ERD diagram (Mermaid)
- `docs/api.md` — REST contract per endpoint
- `docs/authentication.md` — JWT flow
- `docs/rbac.md` — permission matrix
- `docs/inventory.md` — inventory model
- `docs/order-lifecycle.md` — order & payment state machines
- `docs/events.md` — domain events
- `docs/frontend-routes.md` / `docs/admin-routes.md` — route maps
- `docs/development-plan.md` — 11 phases

> PROMPT 2/3/4 MUST import this contract verbatim. Nếu cần đổi tên/field, ghi ADR bổ sung và migration.
