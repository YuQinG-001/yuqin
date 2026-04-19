# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Meinian Health (美年健康) is a health management platform with a **Spring Boot 3.5 backend** and **Vue 3 + TypeScript frontend**. The active backend project lives in `code/meinian-api/` (has its own git repo). The frontend is in `code/sohealthy-vue/`.

> **Note:** `code/pom.xml` and `code/src/` are legacy/outdated — the active backend is `code/meinian-api/`.

## Backend (code/meinian-api/)

### Tech Stack
- **Java 21**, Spring Boot 3.5.6
- **MyBatis-Plus 3.5.12** + mybatis-plus-join (multi-table queries)
- **Sa-Token 1.44.0** for auth (token-based, stored in Redis)
- **MySQL** (meinian-his DB), **Redis** (cache + sessions), **MongoDB** (documents)
- **SpringDoc OpenAPI** — Swagger UI at `/meinian-api/swagger-ui.html`
- Integrations: Tencent Cloud SDK, WeChat Pay V3, MinIO (object storage), QLExpress (rule engine)

### Commands
```bash
cd code/meinian-api

# Run the application
./mvnw spring-boot:run

# Build (skip tests)
./mvnw clean package -DskipTests

# Run a single test
./mvnw test -Dtest=MeinianApiApplicationTests

# Run all tests
./mvnw test
```

### Architecture
Package: `com.yuqin.meinian.api`

```
config/          # CORS, MyBatis-Plus, Redis, ThreadPool, XSS filter, Sa-Token
db/
  entity/        # DB entity classes (Lombok @Data)
  mapper/        # MyBatis-Plus mapper interfaces
common/          # R<T> unified response wrapper
mis/
  controller/    # REST controllers (MIS = management backend)
  DTO/           # Request DTOs (with Bean Validation)
  VO/            # Response VOs
service/         # Service interfaces
serviceImpl/     # Service implementations
handler/         # Order handling strategy pattern (OneOrderHandler, TwoOrderHandler)
exception/       # HisException
```

**Key patterns:**
- Controllers use `@RequiredArgsConstructor` for DI, `R<T>` for uniform responses
- Auth via `@SaCheckLogin` and `@SaCheckPermission` annotations
- Pagination via `BasePageDTO` + MyBatis-Plus `IPage`
- MyBatis XML mappers in `src/main/resources/mapper/`
- API base path: `/meinian-api` (context-path), port 8080 with SSL
- Token submitted via HTTP header: `satoken: <token>`

### Business Domains (from entities)
- **Sys***: User, Role, Department, Permission, Config, ResourceModule, OperationType — MIS RBAC system
- **Crm***: Customer, CustomerIm, CustomerLocation — CRM/customer management
- **Med***: ExamPackage, ExamAppointment, ExamReport, AppointmentLimit, DepartmentFlow — medical exam booking
- **Trade***: Order (with Strategy pattern for order types)
- **Promotion***: Promotion rules

## Frontend (code/sohealthy-vue/)

### Tech Stack
- **Vue 3.5** + **TypeScript 5.8** + **Vite 6**
- **Element Plus** UI + `@element-plus/icons-vue`
- **Vue Router 5**, **Axios** for HTTP
- Auto-imports via `unplugin-auto-import` + `unplugin-vue-components`
- Less/SASS for styling

### Commands
```bash
cd code/sohealthy-vue

# Dev server
npm run dev

# Type check + build
npm run build

# Preview production build
npm run preview
```

### Architecture
```
src/
  views/
    mis/       # Management backend pages (login, user, dept, role)
    front/     # Customer-facing pages (index, goods)
  router/      # Split into front.ts, mis.ts, common.ts
  utils/       # HTTP client (axios), helpers
  hooks/       # Vue composables
  components/  # Shared Vue components
  assets/      # Images, SCSS
  icons/       # SVG icons
```

**Routing guard:** `router.beforeEach` checks `localStorage` for `token` and `permissions`. MIS routes require both; front routes for customer pages require token only.

## Database
- MySQL at `192.168.5.3:3320`, database `meinian-his`
- MongoDB at `192.168.5.3:27017`, database `meinian-his`
- SQL scripts directory: `sohealthy-datagrip/` (currently empty)
- PlantUML diagrams: `meinian-api/plantUML/` and `code/plantUML/`
