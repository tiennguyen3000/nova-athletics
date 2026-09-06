# NOVA ATHLETICS

Production-grade sportswear ecommerce — modular monolith (Spring Boot + Next.js) inspired by Nike UX (ORIGINAL implementation).

## Quick Start

```bash
cp .env.example .env
docker compose up --build
# Frontend http://localhost:3000
# Backend  http://localhost:8080  Swagger: http://localhost:8080/swagger-ui.html
```

## Docs (PROMPT 1 Contract)

| Doc | Content |
|-----|---------|
| [architecture.md](docs/architecture.md) | System architecture, modules, ADR, risks |
| [erd.md](docs/erd.md) | ERD (Mermaid) |
| [database.md](docs/database.md) | Full DDL spec 43 tables |
| [api.md](docs/api.md) | REST contract + error model |
| [authentication.md](docs/authentication.md) | JWT + refresh rotation |
| [rbac.md](docs/rbac.md) | Role–permission matrix |
| [inventory.md](docs/inventory.md) | Inventory model & reservation flow |
| [order-lifecycle.md](docs/order-lifecycle.md) | Order & payment state machines |
| [events.md](docs/events.md) | Domain events & Kafka future |
| [frontend-routes.md](docs/frontend-routes.md) | Customer routes |
| [admin-routes.md](docs/admin-routes.md) | Admin routes |
| [development-plan.md](docs/development-plan.md) | 11 phases |

> **PROMPT 2/3/4 MUST follow this contract verbatim** — không đổi tên table/entity/endpoint/field/enum nếu không có ADR.

## Folder Structure

```
nova-athletics/
├── docs/            # PROMPT 1 contract (this)
├── backend/         # Spring Boot modular monolith
├── frontend/        # Next.js App Router
├── infra/           # docker-compose, MinIO, nginx
└── .env.example
```

### Backend `com.novaathletics.modules.*`

`auth, user, customer, employee, product, category, collection, search, cart, wishlist, checkout, order, payment, inventory, warehouse, promotion, review, notification, analytics, audit, media` — see `docs/architecture.md#5` for DAG.

### Frontend `app/`

`/, /men, /women, /kids, /running, /basketball, /football, /training, /lifestyle, /sale, /new-arrivals, /products/[slug], /collections/[slug], /search, /cart, /checkout, /account/**, /login, /register, /admin/**`

## Technology Contract

Java 21, Spring Boot 3+, PostgreSQL 16, Redis 7, MinIO, Next.js + TS + Tailwind + shadcn/ui + TanStack Query + Zustand + RHF + Zod — see `docs/architecture.md`.

## License

Proprietary — ORIGINAL code, no Nike assets.
