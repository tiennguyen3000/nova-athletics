# NOVA ATHLETICS — Development Plan (11 Phases)

## PHASE 1 — Infrastructure (Week 1)
- [ ] Docker Compose: postgres, redis, minio, backend, frontend
- [ ] Backend skeleton: Spring Boot 3, Java 21, Maven, `application.yml`, Actuator, OpenAPI
- [ ] Frontend skeleton: Next.js App Router, TS, Tailwind, shadcn/ui init, TanStack Query, Zustand, Zod, React Hook Form
- [ ] Liquibase master + 011 seed structure
- [ ] CI: lint, build, test placeholders
- **Deliverable:** `docker compose up` runs empty app, Swagger at `/swagger-ui.html`

## PHASE 2 — Authentication (Week 2)
- [ ] Tables: users, roles, permissions, user_roles, role_permissions, refresh_tokens, verification_tokens, idempotency_keys
- [ ] Auth module: register, login, refresh rotation, logout, me, forgot/reset abstraction
- [ ] SecurityConfig, JwtProvider, JwtAuthFilter, BCrypt, CORS, rate limiting
- [ ] FE: login/register pages, auth store, interceptor
- **Deliverable:** full auth flow + RBAC seed SUPER_ADMIN

## PHASE 3 — Catalog (Week 3-4)
- [ ] Tables: products, variants, images, categories, collections, attributes, tags, mappings, warehouses
- [ ] Modules: product, category, collection, media (MinIO presign)
- [ ] Search: PostgresSearchService (tsvector), facets
- [ ] Admin CRUD products/categories/collections, media upload
- [ ] FE: Homepage, PLP (men/women/kids/sport), PDP, search, collections
- **Deliverable:** catalog browsable, admin quản được catalog

## PHASE 4 — Cart / Wishlist (Week 5)
- [ ] Tables: carts, cart_items, wishlists, wishlist_items, recently_viewed (Redis + DB)
- [ ] Modules: cart (guest + merge), wishlist, recently_viewed
- [ ] Redis: guest cart, recently viewed
- [ ] FE: cart page, wishlist page, mini-cart, PDP add to cart
- **Deliverable:** cart/wishlist end-to-end

## PHASE 5 — Checkout / Payment / Order (Week 6-7)
- [ ] Tables: orders, order_items (snapshot), payments, shipments, inventory_reservations
- [ ] Modules: checkout (orchestrator), order (state machine), payment (state machine, mock gateway)
- [ ] Inventory reservation flow + pessimistic lock + idempotency + cron releaseExpired
- [ ] FE: checkout validate → place order, account/orders list & detail, cancel
- **Deliverable:** đặt hàng → thanh toán mock → order lifecycle

## PHASE 6 — Customer (Week 8)
- [ ] Tables: customers (already), addresses
- [ ] Modules: customer profile, addresses, notifications inbox
- [ ] FE: account/profile, addresses, notifications, recently viewed
- **Deliverable:** customer self-service

## PHASE 7 — Admin Orders & Reports (Week 8-9)
- [ ] Admin orders: list, detail, status transition, refund
- [ ] Shipments, tracking
- [ ] Reports scaffolding
- **Deliverable:** admin xử lý đơn full

## PHASE 8 — Inventory / Warehouse (Week 9-10)
- [ ] Tables: warehouse_inventory, inventory_transactions, transfers
- [ ] Modules: inventory, warehouse — adjustments, transfers, low stock
- [ ] FE: admin/inventory, warehouses, transfers
- **Deliverable:** quản tồn kho đa kho

## PHASE 9 — Employee / RBAC (Week 10)
- [ ] Tables: employees
- [ ] Module: employee + RBAC matrix enforcement
- [ ] Admin employees CRUD, role assignment
- **Deliverable:** RBAC hoàn chỉnh, audit log wiring

## PHASE 10 — Promotions / Reviews / Notification / Analytics / Audit (Week 11-12)
- [ ] Tables: coupons, coupon_usages, reviews, notifications, audit_logs, store_settings
- [ ] Modules: promotion (coupon validate at checkout), review (create, moderation), notification (event listeners), analytics (aggregations), audit (AOP), store_settings
- [ ] FE: admin/promotions, reviews, audit-logs, reports, settings; customer reviews
- **Deliverable:** feature-complete

## PHASE 11 — Testing / Performance / Security (Week 13-14)
- [ ] Unit + integration tests (Testcontainers), E2E (Playwright)
- [ ] Performance: pagination, N+1, Redis hit rate, load test checkout
- [ ] Security: ZAP, dependency scan, rate limit, input validation, price tampering tests, idempotency
- [ ] Hardening: rate limiting tuning, CORS, headers, secrets rotation
- **Deliverable:** production-ready, docs updated

---

## Milestones

| Milestone | Phase | Demo |
|-----------|-------|------|
| M1 | 1-2 | Auth + skeleton |
| M2 | 3-4 | Catalog + cart |
| M3 | 5 | Checkout → order |
| M4 | 6-8 | Customer + admin + inventory |
| M5 | 9-10 | RBAC + promo/review/notif |
| M6 | 11 | Launch ready |

## Dependencies

```
P1 ─► P2 ─► P3 ─► P4 ─► P5 ─► P6 ─► P7 ─► P8 ─► P9 ─► P10 ─► P11
              ▲
              └── P8 có thể song song với P7 sau P5
```
