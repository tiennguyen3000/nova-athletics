# NOVA ATHLETICS — CI/CD

**Repo:** `tiennguyen3000/nova-athletics` · Stack: Spring Boot 3 (Java 21) + Next.js 14 + Postgres 16 + Redis 7

## Workflows

| Workflow | Trigger | Việc làm |
|----------|---------|---------|
| `CI` (.github/workflows/ci.yml) | push/PR → main,develop | backend `mvn verify` (H2), frontend `npm run build`, `docker compose build` |
| `CD (GHCR)` (.github/workflows/cd.yml) | push main / tag v* / dispatch | build & push `ghcr.io/tiennguyen3000/nova-athletics-backend:main|latest` + `...-frontend:main|latest` (Buildx cache GHA) |

Images public sau khi bật **Settings → Packages** hoặc repo public (mặc định `GITHUB_TOKEN` đủ quyền `packages:write`).

## Deploy

### Option A — GHCR + VPS (SSH)
1. Trên VPS: `git clone https://github.com/tiennguyen3000/nova-athletics.git /opt/nova-athletics`
2. Repo → Settings → Secrets and variables → Actions → New repository secret: `VPS_HOST`, `VPS_USER`, `VPS_SSH_KEY` (private key)
3. Mở `cd.yml`, uncomment block `Deploy to VPS` (đã để sẵn), commit push → mỗi push main tự `git pull && docker compose up -d --build`.

### Option B — GHCR + docker compose pull (không SSH)
```bash
# trên server
docker login ghcr.io -u tiennguyen3000 -p $GH_PAT
docker compose pull   # kéo :latest từ GHCR
docker compose up -d
```

### Option C — Fly.io / Railway
- `fly launch --dockerfile backend/Dockerfile` / `frontend/Dockerfile` hoặc Railway → New Service → From GitHub → chọn repo → env từ `.env.example`.

## Biến môi trường

- `NEXT_PUBLIC_API_URL` — đặt ở **Variables** (Settings → Secrets and variables → Actions → Variables): ví dụ `https://api.nova.example.com` — được inject vào `frontend/Dockerfile` qua `ARG`.
- `POSTGRES_PASSWORD`, `JWT_SECRET`, `MINIO_*` — để ở **Secrets** cho CD deploy.

## Badges

Thêm vào README nếu cần:
```md
![CI](https://github.com/tiennguyen3000/nova-athletics/actions/workflows/ci.yml/badge.svg)
![CD](https://github.com/tiennguyen3000/nova-athletics/actions/workflows/cd.yml/badge.svg)
```
