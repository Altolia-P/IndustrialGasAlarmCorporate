# 工业气体报警企业官网系统

> 面向 ToB 的工业气体报警企业门户网站，支持产品展示、解决方案展示、客户留言收集及后台内容管理。

---

## 项目概述

本项目为工业气体传感器与工业火灾报警系统企业打造的一套完整官网系统。前台面向客户展示产品与解决方案，后台提供管理员内容管理功能。

### MVP 范围

- 官网展示（首页、产品中心、解决方案、联系我们）
- 客户线索收集（留言表单 + Redis 防刷）
- 后台内容管理（产品 CRUD、内容 CRUD、留言处理）
- 管理员登录与 JWT 安全控制

---

## 技术栈

| 类别 | 技术选型 |
|------|----------|
| **后端框架** | Spring Boot 3.5.14 + Java 17 |
| **持久化** | MyBatis-Plus 3.5.5 |
| **数据库** | MySQL 8.0（InnoDB、utf8mb4） |
| **缓存** | Redis（分类缓存、防刷计数、JWT 黑名单） |
| **安全** | Spring Security + BCrypt + JJWT 0.12.5 |
| **构建** | Maven |
| **前端框架** | Vue 3 + Vite + TypeScript |
| **状态管理** | Pinia |
| **路由** | Vue Router |
| **HTTP** | Axios |
| **样式** | 响应式布局（375px–1920px） |

---

## 架构设计

### 后端架构（DDD 四层 + Assembler 隔离）

```
Controller → Service 接口 → Assembler + 聚合根 + Repository 接口
                ↑ 依赖接口        ↑ 依赖倒置
              ServiceImpl       RepositoryImpl → Mapper → PO
```

| 层 | 包路径 | 职责 |
|----|--------|------|
| **Interface** | `interfaces/public/`、`interfaces/admin/` | 路由分发、参数校验、返回 `Result<T>` |
| **Application** | `application/{context}/service/impl/` | 业务编排、DTO↔Domain 转换、事务管理 |
| **Assembler** | `assembler/` | 纯 DTO↔Domain↔VO 映射，无业务逻辑 |
| **Domain** | `domain/` | 聚合根、值对象、Repository 接口，零框架注解 |
| **Infrastructure** | `infrastructure/` | Repository 实现、PO/Mapper、Redis、Security |

### 领域上下文

| 上下文 | 聚合根 | 说明 |
|--------|--------|------|
| Product | Product | 产品管理、上下架 |
| Content | Content | 内容管理（解决方案/新闻） |
| Category | Category | 分类管理（产品分类/内容分类） |
| Message | ContactMessage | 客户留言提交与处理 |
| Auth | User | 管理员登录与安全认证 |

### 前端分层架构

```
views → composables → api → axios
views → components（props/emits 通信）
views → stores（仅用户/认证状态）
views → utils（纯函数工具）
```

---

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.x+
- Node.js 18+

### 后端启动

```bash
cd Code/backend/IndustrialGasAlarmCorporate

# 编译
mvn clean install

# 启动
mvn spring-boot:run
```

### 前端启动

```bash
cd Code/frontend

# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev

# 生产构建
pnpm build
```

---

## API 规范

- 前缀：`/api/v1/public/**`（免认证）、`/api/v1/admin/**`（JWT 认证，登录接口除外）
- 统一响应：`Result<T>` `{code: 0, message: "success", data: T, success: boolean}`
- 分页：前端传 `page=1`，后端转 `page-1` 传给 MyBatis-Plus
- 时间格式：`YYYY-MM-DD HH:mm:ss`

### 前台公开接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/public/products` | 产品列表 |
| GET | `/api/v1/public/products/{uuid}` | 产品详情 |
| GET | `/api/v1/public/contents` | 内容列表 |
| GET | `/api/v1/public/contents/{uuid}` | 内容详情 |
| GET | `/api/v1/public/categories` | 分类列表 |
| POST | `/api/v1/public/messages` | 提交留言 |

### 后台管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/admin/login` | 管理员登录 |
| GET/POST/PUT/DELETE | `/api/v1/admin/products` | 产品 CRUD |
| POST | `/api/v1/admin/products/{uuid}/publish` | 产品上架 |
| POST | `/api/v1/admin/products/{uuid}/unpublish` | 产品下架 |
| GET/POST/PUT/DELETE | `/api/v1/admin/contents` | 内容 CRUD |
| GET | `/api/v1/admin/messages` | 留言列表 |
| PUT | `/api/v1/admin/messages/{uuid}/process` | 标记已处理 |

---

## 数据库设计

- 主键：UUID v4 `CHAR(36)`，应用层生成
- 表前缀：`t_` + snake_case（如 `t_product`、`t_contact_message`）
- 逻辑删除：所有业务表通过 `deleted TINYINT(1)` 字段软删除
- 审计字段：`created_at`、`updated_at` 由 MyBatis-Plus 自动填充
- 无物理外键

---

## 项目结构

```
IndustrialGasAlarmCorporate/
├── Code/
│   ├── backend/IndustrialGasAlarmCorporate/     # Spring Boot 后端
│   │   ├── src/main/java/com/niit/industrialgasalarmcorporate/
│   │   │   ├── common/                          # 公共基础（Result、异常、枚举、工具类）
│   │   │   ├── domain/                          # 领域层（聚合根、值对象、Repository 接口）
│   │   │   ├── application/                     # 应用层（Service 接口与实现）
│   │   │   ├── assembler/                       # 组装层（DTO↔Domain↔VO 映射）
│   │   │   ├── interfaces/                      # 接口层（Controller）
│   │   │   └── infrastructure/                  # 基础设施层（PO、Mapper、Redis、Security）
│   │   └── pom.xml
│   └── frontend/                                # Vue 3 前端
│       ├── src/
│       │   ├── api/                             # API 请求封装
│       │   ├── components/                      # 公共组件
│       │   ├── composables/                     # 可复用逻辑
│       │   ├── layouts/                         # 布局组件
│       │   ├── router/                          # 路由配置
│       │   ├── stores/                          # Pinia 状态管理
│       │   ├── types/                           # TypeScript 类型定义
│       │   ├── utils/                           # 工具函数
│       │   └── views/                           # 页面组件
│       └── package.json
├── CLAUDE.md                                    # Claude Code 项目指令
└── README.md                                    # 本文件
```

---

## 安全策略

- 密码使用 **BCrypt** 加密存储
- JWT Token 支持 Redis 黑名单机制
- 留言防刷：同一手机号 60 秒内限制 + 同一 IP 每分钟 3 次限制
- 登录失败超过 5 次锁定 30 分钟
- 图片上传仅允许 `jpg/png/webp`，单文件 ≤ 5MB

---

## 文档

设计文档位于 `Doc/` 和 `Code/Doc/` 目录（不在 Git 仓库中）。
