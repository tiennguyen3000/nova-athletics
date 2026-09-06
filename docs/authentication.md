# NOVA ATHLETICS — Authentication Design

## 1. Overview

- Stateless JWT cho `accessToken`, stateful refresh rotation với DB whitelist.
- Password: BCrypt strength 12.
- Account status: ACTIVE, LOCKED (sau 5 failed attempts / 15m), DISABLED (admin), PENDING_VERIFICATION.
- Email verification & password reset là abstraction (token table + mail interface) — chưa bắt buộc gửi mail thật ở phase 1.

---

## 2. Token Model

### Access Token (JWT)
- **Algo:** HS256 (secret 256-bit từ env `JWT_SECRET`) — future có thể RS256.
- **TTL:** 15 phút (`JWT_ACCESS_TTL=900s`).
- **Claims:**
```json
{
  "sub": "123",              // user.id
  "email": "user@example.com",
  "roles": ["CUSTOMER"],
  "permissions": ["PRODUCT_READ"],
  "iat": 1725619200,
  "exp": 1725620100,
  "jti": "uuid"
}
```
- **Header:** `Authorization: Bearer <accessToken>`
- **Validation:** `JwtAuthFilter extends OncePerRequestFilter` — verify signature + expiry + `status == ACTIVE`.

### Refresh Token
- **Format:** opaque random 64 bytes base64url (không phải JWT để revoke dễ).
- **TTL:** 7 ngày (`JWT_REFRESH_TTL=604800s`).
- **Storage:** `refresh_tokens` table (hoặc `users` column — chọn table để support multi-device):
  - `id`, `user_id`, `token_hash` (SHA256), `expires_at`, `revoked`, `created_at`, `replaced_by`, `device_info`
  - Index `idx_refresh_user`, `idx_refresh_hash`.
- **Delivery:** `httpOnly`, `Secure`, `SameSite=Lax` cookie `refreshToken` + đồng thời trả trong JSON cho mobile.
- **Rotation:** mỗi `/auth/refresh` sinh refresh mới, revoke cái cũ (grace 10s cho concurrent). Detect reuse → revoke tất cả sessions của user (possible theft).

---

## 3. Flows

### 3.1 Register
```
POST /auth/register { email, password, firstName, lastName, phone }
  → validate
  → check email unique
  → BCrypt hash
  → INSERT users (status=ACTIVE, email_verified=false)
  → INSERT customers (user_id)
  → assign role CUSTOMER
  → issue access + refresh
  → publish UserRegistered event
  → return 201 + set cookie
```

### 3.2 Login
```
POST /auth/login { email, password }
  → find user by email
  → check status (LOCKED/DISABLED → 403)
  → BCrypt matches?
      no  → increment failed_attempts (Redis `login:fail:{email}`), lock if >=5 → 401
      yes → reset fails, update last_login_at
  → issue tokens
  → merge guest cart → customer cart (nếu có X-Guest-Id)
```

### 3.3 Refresh
```
POST /auth/refresh { refreshToken } (or cookie)
  → hash(token) lookup
  → check not revoked, not expired
  → load user, check ACTIVE
  → revoke old, issue new pair
  → return + set new cookie
```
- Grace: old token trong 10s vẫn accept nhưng chỉ 1 lần (dùng `replaced_by` tracking).

### 3.4 Logout
```
POST /auth/logout (Auth required)
  → revoke refresh (by cookie/body)
  → clear cookie
  → optional: blacklist access jti in Redis TTL còn lại (900s) — để instant revoke
```

### 3.5 Me
```
GET /auth/me
  → JwtAuthFilter → SecurityContext → load user + customer + roles/permissions → return
```

---

## 4. Password & Account

- **Hash:** `BCryptPasswordEncoder(12)` — không lưu plain.
- **Failed login:** Redis counter `login:fail:{email_or_ip}` TTL 15m. >=5 → `status=LOCKED`, `locked_until = now()+15m`. Job unlock hoặc admin manual.
- **Email verification (abstraction):**
  - Table `verification_tokens (id, user_id, token_hash, type=EMAIL_VERIFY, expires_at, used)`.
  - `POST /auth/verify-email { token }` → set `email_verified=true`.
  - Mail sender là interface `MailSender` — impl `LogMailSender` (dev) / `SmtpMailSender` (prod).
- **Password reset (abstraction):**
  - `POST /auth/forgot-password { email }` → tạo token 1h, gửi mail (log ở dev).
  - `POST /auth/reset-password { token, newPassword }` → validate → update hash → revoke all refresh.

---

## 5. Spring Security Config

```java
@Configuration
@EnableWebSecurity
class SecurityConfig {
  SecurityFilterChain filterChain(HttpSecurity http) {
    http
      .csrf(csrf -> csrf.disable()) // stateless JWT
      .cors(cors -> cors.configurationSource(corsConfig))
      .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/v1/auth/register","/api/v1/auth/login","/api/v1/auth/refresh",
                         "/api/v1/auth/forgot-password","/api/v1/auth/reset-password").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/v1/products/**","/api/v1/categories/**",
                         "/api/v1/collections/**","/api/v1/search").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/v1/products/*/reviews").permitAll()
        .requestMatchers("/api/v1/admin/**").authenticated()
        .requestMatchers("/api/v1/**").authenticated()
        .anyRequest().permitAll()
      )
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
      .exceptionHandling(e -> e
        .authenticationEntryPoint(unauthorizedHandler) // 401
        .accessDeniedHandler(forbiddenHandler)         // 403
      );
  }
}
```

- `JwtAuthFilter`: đọc header, validate, set `Authentication = UsernamePasswordAuthenticationToken(principal, null, authorities)` với authorities = `ROLE_<name>` + `PERMISSION_<name>`.
- `UserPrincipal` chứa `userId`, `customerId` (nullable), `employeeId`, `email`, `roles`, `permissions`.
- Password encoder bean: `BCryptPasswordEncoder`.

---

## 6. Frontend Integration

- **Storage:** accessToken in memory (Zustand) + localStorage fallback, refreshToken chỉ ở httpOnly cookie (không JS).
- **Axios interceptor:** 401 → gọi `/auth/refresh` → retry original request once → nếu fail → redirect `/login`.
- **Guest cart:** `guest_id` UUID lưu localStorage + cookie, gửi `X-Guest-Id` mỗi cart request khi chưa login.

---

## 7. Env Vars

```
JWT_SECRET=<64-char random>
JWT_ACCESS_TTL=900
JWT_REFRESH_TTL=604800
CORS_ALLOWED_ORIGINS=http://localhost:3000
BCRYPT_STRENGTH=12
LOGIN_MAX_ATTEMPTS=5
LOGIN_LOCK_MINUTES=15
```

---

## 8. Testing Checklist

- [ ] Register với email trùng → 409
- [ ] Login sai 5 lần → lock 15m
- [ ] Refresh rotation → old token reuse → revoke all
- [ ] Logout → refresh không dùng lại được
- [ ] Access expired → 401, refresh OK → retry success
- [ ] RBAC: CUSTOMER gọi /admin/products → 403
