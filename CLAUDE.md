# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Blacklist

工作范围仅限于以下路径，除此之外的所有文件/目录禁止读取和引用：
- `Code/`（含所有子目录）
- `.claude/`
- `CLAUDE.md`

**例外（黑名单）**：根目录下的 `Doc/` 禁止读取。

## Project Overview

工业气体报警企业官网系统 — ToB 企业官网，支持产品展示、解决方案展示、客户留言收集及后台内容管理。

## Tech Stack

- **Backend**: Spring Boot 3.5.14 + Java 17 + MyBatis-Plus 3.5.5 + MySQL 8.0 + Redis + Spring Security + JJWT 0.12.5
- **Frontend**: Vue 3 + Vite + TypeScript + Pinia + Vue Router + Axios（响应式布局 375px–1920px）
- **Build**: Maven (backend), npm/pnpm (frontend)
- **Architecture**: DDD 四层架构 + Assembler 隔离

## Backend Commands

```bash
# Backend project path: Code/backend/IndustrialGasAlarmCorporate/

# Build
mvn clean install

# Run
mvn spring-boot:run

# Run tests
mvn test

# Run a single test
mvn test -Dtest=TestClassName

# Package
mvn clean package -DskipTests
```

## Frontend Commands

（前端项目尚未初始化，预计位于 `Code/frontend/`）

```bash
# Install dependencies
pnpm install    # or npm install

# Dev server
pnpm dev        # or npm run dev

# Build
pnpm build      # or npm run build

# Lint
pnpm lint       # or npm run lint
```

## Backend Architecture (DDD 4-Layer + Assembler)

```
Controller → Service 接口 → Assembler + 聚合根 + Repository 接口
                ↑ 依赖接口        ↑ 依赖倒置
              ServiceImpl       RepositoryImpl → Mapper → PO
```

| Layer | Package | Responsibility |
|-------|---------|---------------|
| **Interface** | `interfaces/public/`, `interfaces/admin/` | Route dispatch, param validation, return `Result<T>` |
| **Application** | `application/{context}/service/impl/` | Business orchestration, DTO↔Domain via Assembler, `@Transactional` |
| **Assembler** | `assembler/` | Pure DTO↔Domain↔VO mapping. **No business logic** |
| **Domain** | `domain/` | Aggregate roots, value objects, Repository interfaces. **Zero framework annotations** |
| **Infrastructure** | `infrastructure/` | Repository impl (PO/Mapper), Redis, Security, Config, Event |

### Package Structure

```
com.niit.industrialgasalarmcorporate/
├── common/              → Result<T>, Page<T>, BusinessException, ErrorCode, JwtUtil
├── domain/              → AggregateRoot, Product, Content, Category, ContactMessage, User
├── application/         → {product,content,category,message,auth}/ dto/ vo/ service/ impl/
├── assembler/           → ProductAssembler, ContentAssembler, etc.
├── interfaces/          → public/ (公开), admin/ (后台)
└── infrastructure/      → repository/{po, mapper, impl}, redis/, security/, config/, event/
```

### Domain Contexts

| Context | Aggregate Root | Key Enums |
|---------|---------------|-----------|
| Product | Product | `DRAFT`, `PUBLISHED`, `UNPUBLISHED` |
| Content | Content | `SOLUTION`/`NEWS`(ContentType), `DRAFT`/`PUBLISHED`(ContentStatus) |
| Category | Category | `PRODUCT_CATEGORY`, `CONTENT_CATEGORY` |
| Message | ContactMessage | `PENDING`, `PROCESSED` |
| Auth | User | — |

## Frontend Architecture (Vue 3 Layered)

```
views → composables → api → axios
views → components (props/emits)
views → stores (read/write user state only)
views → utils (pure functions)
```

| Layer | Path | Responsibility |
|-------|------|---------------|
| Page | `views/` | Display, event binding, composable calls |
| Component | `components/` | Reusable UI. **No API calls, no store access** |
| API | `api/` | Axios requests, returns `Promise<T>` |
| Composable | `composables/` | Reusable logic (pagination, loading, form submit) |
| Store | `stores/` | Pinia — user/auth state only |
| Util | `utils/` | Pure functions (token, request interceptor) |

## Key API Conventions

- Prefix: `/api/v1/public/**` (no auth), `/api/v1/admin/**` (JWT required, except login)
- Unified response: `Result<T>` `{code: 0, message: "success", data: T, success: boolean}`
- Pagination: frontend sends `page=1`, backend converts to `page-1` for MyBatis-Plus
- Time format: `YYYY-MM-DD HH:mm:ss`
- RESTful CRUD + action endpoints (`POST /products/{uuid}/publish`)

## Database Conventions

- 全表 UUID v4 `CHAR(36)` 主键，应用层生成，**无自增主键**（微服务分库无冲突）
- 表前缀：`t_` + snake_case（如 `t_product`、`t_contact_message`）
- 乐观锁：所有业务表含 `version INT NOT NULL DEFAULT 0`，MyBatis-Plus `@Version` 维护
- 逻辑删除：`deleted TINYINT(1) DEFAULT 0`（`t_admin_user` 除外，通过 `locked` 字段停用）
- 审计字段：`created_at`、`updated_at`（MyBatis-Plus MetaObjectHandler 自动填充）
- 无物理外键，表间关系由应用层保证
- 枚举字段以 String 存入 PO
- 基础设施表：`t_operation_log`（操作审计）、`t_system_config`（系统配置）、`t_event_outbox`（微服务可靠消息）

## Key Constraints

- **Domain layer**: zero framework annotations (no Spring, no Lombok, no Jackson)
- **Assembler layer**: DTO↔Domain↔VO conversion **must** go through Assembler, never `BeanUtils.copyProperties` in Service/Controller
- **Repository**: single-object queries return `Optional<T>`, never null
- **Controller**: param validation + service call + return `Result<T>`. No if/else business logic, no Repository/Redis calls
- **Dependency injection**: constructor injection with `@RequiredArgsConstructor`, no `@Autowired` field injection
- **Redis**: responsibility-separated repositories (`MessageRateLimitRepository`, `JwtBlacklistRepository`, `CategoryCacheRepository`), no monolithic RedisUtil
- **Error codes**: all business exceptions via `BusinessException` + `ErrorCode` enum, no magic numbers
- **Image upload**: only `jpg/png/webp`, ≤5MB, store URL string in DB
- **PO uses `@Getter` + `@Setter` only** (no `@Data` to avoid toString cycles)
- **Frontend**: no `any` types, no hardcoded `localhost`, no event bus, no `index` as `:key`

## Documentation

All design docs are in `Doc/`:
- `03_需求分析/` — PRD requirements docs
- `04_系统设计/1.架构设计文档ADD/` — Architecture design docs
- `04_系统设计/2.类图设计文档/` — Class diagram docs
- `04_系统设计/3.数据库设计/` — Database design docs
- `04_系统设计/4.接口文档(API)/` — API design docs

## Skill routing

When the user's request matches an available skill, invoke it via the Skill tool. When in doubt, invoke the skill.

Key routing rules:
- Product ideas/brainstorming → invoke /office-hours
- Strategy/scope → invoke /plan-ceo-review
- Architecture → invoke /plan-eng-review
- Design system/plan review → invoke /design-consultation or /plan-design-review
- Full review pipeline → invoke /autoplan
- Bugs/errors → invoke /investigate
- QA/testing site behavior → invoke /qa or /qa-only
- Code review/diff check → invoke /review
- Visual polish → invoke /design-review
- Ship/deploy/PR → invoke /ship or /land-and-deploy
- Save progress → invoke /context-save
- Resume context → invoke /context-restore
