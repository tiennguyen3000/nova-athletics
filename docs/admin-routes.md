# NOVA ATHLETICS — Admin Route Map

> App Router segment `frontend/app/admin/` — guard `MANAGER|ADMIN|SUPER_ADMIN`, RBAC hide menu.

## Layout

- `app/admin/layout.tsx` — sidebar, header, breadcrumb, permission guard.
- `app/admin/page.tsx` — Dashboard: revenue chart, orders by status, low stock, recent orders.

| Route | File | Permission | Description |
|-------|------|------------|-------------|
| `/admin` | `app/admin/page.tsx` | REPORT_READ | Dashboard |
| `/admin/products` | `app/admin/products/page.tsx` | PRODUCT_READ | List, filter status/search, pagination |
| `/admin/products/new` | `app/admin/products/new/page.tsx` | PRODUCT_CREATE | Create form (React Hook Form + Zod) |
| `/admin/products/[id]` | `app/admin/products/[id]/page.tsx` | PRODUCT_READ | Detail + edit tabs (info, variants, images, inventory) |
| `/admin/products/[id]/edit` | `app/admin/products/[id]/edit/page.tsx` | PRODUCT_UPDATE | Edit form |
| `/admin/categories` | `app/admin/categories/page.tsx` | CATEGORY_MANAGE | Tree + CRUD dialog |
| `/admin/collections` | `app/admin/collections/page.tsx` | COLLECTION_MANAGE | List + CRUD |
| `/admin/orders` | `app/admin/orders/page.tsx` | ORDER_READ | List filter status/date/customer |
| `/admin/orders/[id]` | `app/admin/orders/[id]/page.tsx` | ORDER_READ | Detail + status transition + refund |
| `/admin/customers` | `app/admin/customers/page.tsx` | CUSTOMER_READ | List |
| `/admin/customers/[id]` | `app/admin/customers/[id]/page.tsx` | CUSTOMER_READ | Detail, orders, addresses |
| `/admin/inventory` | `app/admin/inventory/page.tsx` | INVENTORY_READ | Stock per warehouse, low stock, adjust dialog |
| `/admin/inventory/transfers` | `app/admin/inventory/transfers/page.tsx` | INVENTORY_READ | Transfers list |
| `/admin/inventory/transfers/new` | `app/admin/inventory/transfers/new/page.tsx` | INVENTORY_TRANSFER | Create transfer |
| `/admin/warehouses` | `app/admin/warehouses/page.tsx` | INVENTORY_READ | CRUD warehouses |
| `/admin/promotions` | `app/admin/promotions/page.tsx` | PROMOTION_MANAGE | Coupons CRUD |
| `/admin/promotions/new` | `app/admin/promotions/new/page.tsx` | PROMOTION_MANAGE | Create coupon |
| `/admin/reviews` | `app/admin/reviews/page.tsx` | REVIEW_MANAGE | List + approve/reject |
| `/admin/employees` | `app/admin/employees/page.tsx` | EMPLOYEE_READ | List |
| `/admin/employees/new` | `app/admin/employees/new/page.tsx` | EMPLOYEE_MANAGE | Create employee + assign roles |
| `/admin/employees/[id]` | `app/admin/employees/[id]/page.tsx` | EMPLOYEE_MANAGE | Edit |
| `/admin/reports` | `app/admin/reports/page.tsx` | REPORT_READ | Tabs: revenue, top products, inventory, orders |
| `/admin/audit-logs` | `app/admin/audit-logs/page.tsx` | AUDIT_READ | Filterable log table |
| `/admin/settings` | `app/admin/settings/page.tsx` | ADMIN | Store settings (shipping fee, tax) |
| `/admin/media` | `app/admin/media/page.tsx` | PRODUCT_READ | Media library |

## Admin API mapping

Mọi page gọi `GET /api/v1/admin/...` tương ứng (xem `docs/api.md#14`).

## Components

- `components/admin/DataTable.tsx` (shadcn table + pagination)
- `components/admin/StatusBadge.tsx`
- `components/admin/forms/*` — Zod-validated forms.

## Guard

```ts
// middleware.ts
if (pathname.startsWith('/admin')) {
  const user = await getUserFromToken(req);
  if (!user || !hasAnyRole(user, ['MANAGER','ADMIN','SUPER_ADMIN'])) return NextResponse.redirect('/login');
}
```
