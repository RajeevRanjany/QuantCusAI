# QuantaCus — Product Intelligence Dashboard

A full-stack web application that ingests e-commerce product data (CSV or video), runs automated quality validation, benchmarks competitor pricing, and surfaces actionable insights through a real-time dashboard.

---

## Features

- **Multi-source ingestion** — upload product catalogs via CSV or video; background pipeline extracts, validates, and enriches data automatically
- **Quality scoring** — every product receives a 0–100 quality score computed across title completeness, description, imagery, pricing, and availability
- **Competitor price benchmarking** — live competitor price refresh with above/below-market flagging per SKU
- **Validation alerts** — severity-tiered (ERROR / WARNING / INFO) alerts per product, filterable and resolvable from the UI
- **Duplicate detection** — automatic cross-SKU duplicate flagging within a job
- **Title enhancement** — on-demand AI-assisted product title rewrite
- **Job management** — full job lifecycle tracking with status polling and per-job drill-down
- **Dashboard summary** — aggregate stats: avg quality score, alert breakdown, duplicate count, price positioning

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 19, Vite 5, Tailwind CSS 3, React Router 6, Axios |
| Backend | Spring Boot 3.5, Java 21, Spring Data JPA, Hibernate 6 |
| Database | PostgreSQL 15 (H2 in-memory for local dev) |
| API Docs | SpringDoc OpenAPI 2 (Swagger UI) |
| Build | Maven 3.9, Node 20+ |

---

## Architecture

```
┌─────────────────────────────────────┐
│           React Frontend            │
│  Pages · Components · useFetch hook │
│         Vite dev proxy /api         │
└────────────────┬────────────────────┘
                 │ REST  /api/v1/*
┌────────────────▼────────────────────┐
│         Spring Boot Backend         │
│                                     │
│  Controllers → Services → Repos     │
│                                     │
│  UploadController  → PipelineService│
│  JobController     → JobService     │
│  ProductController → ProductService │
│  AlertController   → AlertService   │
│  DashboardController                │
│  CompetitorPriceController          │
└────────────────┬────────────────────┘
                 │ JPA / Hibernate
┌────────────────▼────────────────────┐
│            PostgreSQL               │
│  jobs · products · validation_alerts│
│  competitor_prices                  │
└─────────────────────────────────────┘
```

**Data flow:** File upload → `PipelineService` spawns async job → `ExtractionService` parses records → `ValidationService` scores and alerts → `CompetitorPriceService` benchmarks → job marked `COMPLETED`.

---

## Setup Instructions

### Prerequisites

- Java 21 (Eclipse Temurin recommended)
- Maven 3.9+
- Node.js 20+
- PostgreSQL 15 running locally

> **Important:** Ensure `JAVA_HOME` points to Java 21. Lombok is incompatible with Java 25 (Homebrew default on Apple Silicon).
> ```sh
> export JAVA_HOME=/Users/$USER/Library/Java/JavaVirtualMachines/temurin-21.0.11/Contents/Home
> ```

### Database

> Development and demo environments use an **H2 in-memory database** — no setup required.
> The **production profile** connects to PostgreSQL.

To run against PostgreSQL, create the database first:

```sql
CREATE DATABASE quantacus_db;
-- default user: postgres / password: postgres
-- adjust src/main/resources/application.yml if different
```

### Backend

```sh
cd backend
mvn spring-boot:run
# API available at http://localhost:8080
# Swagger UI at http://localhost:8080/swagger-ui.html
```

For local development with H2 (no Postgres required):

```sh
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Frontend

```sh
cd frontend
npm install
npm run dev
# App available at http://localhost:5173
```

Production build:

```sh
npm run build   # outputs to dist/
```

---

## API Documentation

Interactive docs available at **`http://localhost:8080/swagger-ui.html`** when the backend is running.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/upload-products-csv` | Upload product CSV and start pipeline |
| `POST` | `/api/v1/upload-video` | Upload product video and start pipeline |
| `GET` | `/api/v1/jobs` | List all jobs |
| `GET` | `/api/v1/jobs/{jobId}` | Get job details |
| `GET` | `/api/v1/products?jobId=` | List products for a job |
| `GET` | `/api/v1/products/{productId}` | Get product detail with alerts and prices |
| `PUT` | `/api/v1/products/{productId}` | Update product fields |
| `POST` | `/api/v1/products/{productId}/enhance-title` | Rewrite product title |
| `GET` | `/api/v1/alerts?jobId=` | List alerts for a job |
| `POST` | `/api/v1/alerts/{alertId}/resolve` | Mark alert as resolved |
| `GET` | `/api/v1/dashboard/quality-summary?jobId=` | Get dashboard summary for a job |
| `POST` | `/api/v1/competitor-prices/refresh` | Refresh competitor prices |

All responses follow a uniform envelope:

```json
{
  "success": true,
  "data": { ... },
  "timestamp": "2026-05-29T10:00:00"
}
```

---

## Screenshots

> _Add screenshots here once the UI is running._

| Page | Description |
|------|-------------|
| Upload | CSV / video drop zone with job creation |
| Jobs | Job list with status badges and timestamps |
| Dashboard | Quality score cards, alert counts, price positioning |
| Products | Filterable product grid with quality meters |
| Product Detail | Full product fields, competitor price table, alert list |
| Alerts | Severity-filtered alert list with resolve action |

---

## Project Structure

```
QunatCus/
├── backend/
│   └── src/main/java/com/quantacus/dashboard/
│       ├── controller/       # REST endpoints
│       ├── service/          # Business logic
│       ├── entity/           # JPA entities
│       ├── repository/       # Spring Data repos
│       ├── dto/              # Request / response DTOs
│       ├── enums/            # JobStatus, AlertSeverity, …
│       └── exception/        # Global error handling
├── frontend/
│   └── src/
│       ├── api/              # Axios API modules
│       ├── components/       # Reusable UI components
│       ├── hooks/            # useFetch, useJobPolling
│       ├── pages/            # Route-level page components
│       └── utils/            # Formatters and helpers
└── README.md
```

---

## Future Improvements

- **Authentication** — JWT-based login with role separation (admin / viewer)
- **Real-time updates** — WebSocket or SSE job progress instead of polling
- **AI enrichment** — LLM-powered description generation alongside title enhancement
- **Export** — CSV / Excel download for product and alert reports
- **Multi-tenant** — organisation-scoped jobs and products
- **Charts** — quality score distribution histogram, price positioning scatter plot
- **Pagination** — server-side pagination for large catalogs (10 k+ SKUs)
- **Notifications** — email / Slack alert when a job completes or errors

** Rajeev Ranjan | MNNIT ALLAHABAD **