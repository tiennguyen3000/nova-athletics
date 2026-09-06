# NOVA ATHLETICS — Frontend Route Map (Customer)

> Next.js App Router — `frontend/app/`  
> Auth guard: middleware kiểm JWT, redirect `/login` nếu cần.  
> Data fetching: TanStack Query + Server Components cho SEO pages.

## Public

| Route | File | Description | Data |
|-------|------|-------------|------|
| `/` | `app/page.tsx` | Homepage: hero, featured, collections, new arrivals | `GET /products?isFeatured=true`, `GET /collections` |
| `/men` | `app/men/page.tsx` | PLP filter gender=MEN | `GET /products?gender=MEN` |
| `/women` | `app/women/page.tsx` | PLP women | `gender=WOMEN` |
| `/kids` | `app/kids/page.tsx` | PLP kids | `gender=KIDS` |
| `/running` | `app/running/page.tsx` | Sport PLP | `sport=RUNNING` |
| `/basketball` | `app/basketball/page.tsx` | — | `sport=BASKETBALL` |
| `/football` | `app/football/page.tsx` | — | `sport=FOOTBALL` |
| `/training` | `app/training/page.tsx` | — | `sport=TRAINING` |
| `/lifestyle` | `app/lifestyle/page.tsx` | — | `sport=LIFESTYLE` |
| `/sale` | `app/sale/page.tsx` | Sale — filter salePrice not null, sort discount desc | `GET /products?minPrice=&maxPrice=&sort=...` |
| `/new-arrivals` | `app/new-arrivals/page.tsx` | Sort createdAt desc, filter recent 30d | — |
| `/products/[slug]` | `app/products/[slug]/page.tsx` | PDP: images, variants, add to cart/wishlist, reviews | `GET /products/{slug}`, `GET /products/{id}/reviews` |
| `/collections/[slug]` | `app/collections/[slug]/page.tsx` | Collection PLP | `GET /collections/{slug}` |
| `/search` | `app/search/page.tsx` | Search + facets, query `?q=...` | `GET /search?q=...` |

## Cart & Checkout (auth optional for cart, required for checkout)

| Route | File | Auth | Description |
|-------|------|------|-------------|
| `/cart` | `app/cart/page.tsx` | optional (guest) | Cart items, quantity, coupon input |
| `/checkout` | `app/checkout/page.tsx` | required | Address select, coupon, payment method, validate + place order |

## Account (required)

| Route | File | Description |
|-------|------|-------------|
| `/account` | `app/account/page.tsx` | Dashboard overview |
| `/account/orders` | `app/account/orders/page.tsx` | List orders |
| `/account/orders/[id]` | `app/account/orders/[id]/page.tsx` | Order detail + cancel |
| `/account/profile` | `app/account/profile/page.tsx` | Edit profile |
| `/account/addresses` | `app/account/addresses/page.tsx` | CRUD addresses |
| `/account/wishlist` | `app/account/wishlist/page.tsx` | Wishlist grid |
| `/account/reviews` | `app/account/reviews/page.tsx` | My reviews |
| `/account/notifications` | `app/account/notifications/page.tsx` | Inbox |

## Auth

| Route | File |
|-------|------|
| `/login` | `app/(auth)/login/page.tsx` |
| `/register` | `app/(auth)/register/page.tsx` |
| `/forgot-password` | `app/(auth)/forgot-password/page.tsx` |
| `/reset-password` | `app/(auth)/reset-password/page.tsx` |

## Shared Components

- `components/layout/Header.tsx` (nav, search, cart, wishlist, account)
- `components/product/ProductCard.tsx`, `ProductGrid.tsx`, `VariantSelector.tsx`
- `components/cart/CartStore.ts` (Zustand), `components/search/SearchBar.tsx`

## SEO & Metadata

- PDP/PLP dùng `generateMetadata` từ product data.
- Sitemap `app/sitemap.ts` từ products/categories.

## Error Pages

- `app/not-found.tsx` (404), `app/error.tsx` (500), `app/loading.tsx`.

