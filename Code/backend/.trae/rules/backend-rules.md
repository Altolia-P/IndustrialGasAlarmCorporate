# 后端开发规则

> 供 AI 代码生成助手使用的后端开发规范，约束技术选型、架构分层和代码生成行为。
> 基于 Doc/ 下需求文档、架构设计文档、类图设计文档、数据库设计文档、接口设计文档提取。

---

## 1. 技术栈

| 类别 | 选型 |
|------|------|
| 框架 | Spring Boot 3.5.x |
| 语言 | Java 17 |
| 持久化 | MyBatis-Plus 3.5.5（**团队统一标准，禁止使用 JPA**） |
| 数据库 | MySQL 8.0，InnoDB，utf8mb4 |
| 缓存 | Redis（分类缓存、防刷计数、JWT 黑名单） |
| 安全 | Spring Security + BCrypt + JJWT 0.12.5 |
| 构建 | Maven |
| 工具 | Lombok、SLF4J、Jakarta Validation |

---

## 2. 架构分层（DDD 四层 + Assembler 隔离）

```
Controller → Service 接口 → Assembler + 聚合根 + Repository 接口
                ↑ 依赖接口        ↑ 依赖倒置
              ServiceImpl       RepositoryImpl → Mapper → PO
```

### 各层职责

| 层 | 包路径 | 职责 | 约束 |
|---|--------|------|------|
| **接口层** | `interfaces/public/`, `interfaces/admin/` | 路由分发、参数校验 | 仅允许：参数接收、`@Valid` 校验、调用 Service、返回 `Result<T>`。禁止：if/else 业务判断、调用 Repository/Redis、手动 DTO↔VO 转换 |
| **应用层** | `application/{context}/service/impl/` | 编排领域对象、Assembler、Repository，管理事务 | 加 `@Transactional`，通过 Assembler 做 DTO↔Domain 转换 |
| **组装层** | `assembler/` | DTO ↔ 领域对象 ↔ VO 纯映射 | **禁止包含任何业务逻辑**，DTO↔Domain↔VO 转换**必须**经过 Assembler，禁止在 Controller/Service 中直接 `BeanUtils.copyProperties` |
| **领域层** | `domain/` | 聚合根、值对象、Repository 接口、领域事件 | **零框架依赖** — 禁止 Spring 注解、MyBatis 注解、Lombok `@Data`、Jackson 注解；只能使用纯 Java |
| **基础设施层** | `infrastructure/` | Repository 实现、PO/Mapper、Redis 封装、Security、Config | 依赖倒置实现 domain 层接口 |

### 包结构

```
com.niit.industrialgasalarmcorporate/
├── common/
│   ├── base/              → Result<T>, Page<T>
│   ├── exception/         → BusinessException 及子类
│   ├── enums/             → ErrorCode 枚举
│   └── utils/             → JwtUtil, DateUtils
│
├── domain/                # 聚合根、值对象、枚举、Repository 接口
│   ├── shared/            → BaseEntity, AggregateRoot
│   ├── product/           → Product, ProductImage, ProductAttribute, ProductStatus, ProductRepository
│   ├── content/           → Content, ContentStatus, ContentType, ContentRepository
│   ├── category/          → Category, CategoryType, CategoryRepository
│   ├── message/           → ContactMessage, MessageStatus, MessageRepository
│   ├── auth/              → User, LoginResult, UserRepository
│   └── event/             → DomainEvent, ProductPublishedEvent, MessageSubmittedEvent, AccountLockedEvent
│
├── application/           # Service 接口 + Impl（按上下文分包，接口与实现分离）
│   ├── product/           → dto/ + vo/ + service/ + service/impl/
│   ├── content/           → dto/ + vo/ + service/ + service/impl/
│   ├── category/          → vo/ + service/ + service/impl/
│   ├── message/           → dto/ + vo/ + service/ + service/impl/
│   └── auth/              → dto/ + vo/ + service/ + service/impl/
│
├── assembler/             → ProductAssembler, ContentAssembler, CategoryAssembler, MessageAssembler
│
├── interfaces/
│   ├── public/            → ProductController, ContentController, CategoryController, MessageController
│   └── admin/             → AuthController, AdminProductController, AdminContentController, AdminMessageController
│
├── infrastructure/
│   ├── repository/
│   │   ├── po/            → @TableName 注解的持久化对象
│   │   ├── mapper/        → 继承 BaseMapper<PO>
│   │   └── impl/          → Repository 实现（PO ↔ Domain 转换）
│   ├── redis/             → 按职责拆分的 Repository（防刷、JWT 黑名单、缓存）
│   ├── security/          → JWT 过滤器、认证配置
│   └── config/            → MyBatis-Plus、Redis 等配置类
```

---

## 3. 编码规范

### 命名

| 元素 | 规范 | 示例 |
|------|------|------|
| Java 类 | PascalCase | `ProductRepositoryImpl` |
| 方法/变量 | camelCase | `getUserById` |
| 常量 | UPPER_SNAKE_CASE | `TOKEN_EXPIRE_TIME` |
| 数据库表 | `t_` 前缀 + snake_case | `t_product`, `t_contact_message` |
| 主键列 | `xxx_uuid` + `CHAR(36)` | `product_uuid` |
| 枚举 DB 值 | UPPER_SNAKE_CASE | `PUBLISHED`, `PRODUCT_CATEGORY` |
| 接口路径 | 复数名词 | `/products`, `/messages` |

### Lombok 使用范围

| 位置 | 允许 | 禁止 |
|------|------|------|
| DTO / VO | `@Data` | - |
| PO | `@Getter` + `@Setter` | **禁止** `@Data`（避免 toString 循环引用、equals/hashCode 异常） |
| Domain 聚合根/值对象 | 纯 Java，无 Lombok | **禁止** `@Data`、`@Builder`、`@NoArgsConstructor`、`@Setter` |
| Service | `@Slf4j`、`@RequiredArgsConstructor` | - |
| Controller | `@RequiredArgsConstructor` | - |

### 依赖注入

- **禁止 field injection**（`@Autowired` 字段注入）
- 强制使用构造器注入：`@RequiredArgsConstructor` 或显式构造方法
- 防止循环依赖：构造器注入会在启动时检测循环，迫使你重构

### 日志

- 统一 `@Slf4j`
- 日志格式：`logger.info("业务描述: {}", 关键参数)` — 禁止字符串拼接
- 禁止在循环中打日志
- 异常日志使用 `logger.error("描述", exception)`，传递完整异常栈

### 数据库规范

- 主键：UUID v4 `CHAR(36)`，应用层生成
- 逻辑删除：所有业务表加 `deleted TINYINT(1) DEFAULT 0`（`t_admin_user` 除外）
- 审计字段：所有表加 `created_at`、`updated_at`，MyBatis-Plus MetaObjectHandler 自动填充
- 无物理外键，表间关系由应用层保证
- 管理员表无 `deleted` 字段（无需逻辑删除），无 `salt` 字段（BCrypt 已内嵌盐值）

---

## 4. 关键设计约束

### 聚合根行为

聚合根封装业务行为方法，避免完全贫血：

| 聚合根 | 行为方法 | 约束 |
|--------|----------|------|
| `Product` | `publish()`, `unpublish()`, `addImage()`, `addAttribute()` | 已上架再 publish 抛异常；非 PUBLISHED 状态 unpublish 抛异常 |
| `Content` | `publish()` | 已发布再 publish 抛异常 |
| `ContactMessage` | `markProcessed(processor, remark)` | - |
| `User` | `login(password)`, `changePassword(newRawPassword)` | BCrypt 校验；连续 5 次失败锁定 30 分钟 |

### 枚举

| 枚举 | 值 |
|------|----|
| `ProductStatus` | `DRAFT`, `PUBLISHED`, `UNPUBLISHED` |
| `ContentStatus` | `DRAFT`, `PUBLISHED` |
| `ContentType` | `SOLUTION`（当前）, `NEWS`（预留） |
| `CategoryType` | `PRODUCT_CATEGORY`, `CONTENT_CATEGORY` |
| `MessageStatus` | `PENDING`, `PROCESSED` |

枚举字段在 PO 中用 **String** 存储，Assembler 中完成 String↔Enum 转换。

### Repository 接口

- 定义在 `domain` 层，实现在 `infrastructure/repository/impl`
- **单对象查询必须返回 `Optional<T>`，禁止返回 `null`**
- 实现中完成 PO ↔ Domain 转换

### 异常处理

- 所有业务异常继承 `BusinessException`，构造时传入 `ErrorCode`
- 全局 `@RestControllerAdvice` 统一处理，返回 `Result<T>`
- Controller 禁止 try-catch
- `ErrorCode` 枚举管理所有错误码，禁止魔法数字

### 事务

- 所有写操作加 `@Transactional`
- 查询加 `@Transactional(readOnly = true)`
- 事务内：调用聚合根 → Repository save → 事务提交 → 事件发布（MVP 暂用同步日志）

### 安全

- 登录接口 `/api/v1/admin/login` 必须在 Security 配置中 `permitAll()` 放行
- 后台其余接口需 JWT 认证
- BCrypt 加密密码，无需单独存 salt
- JWT 黑名单放在 Redis 中

### Redis

- **禁止大而全的 RedisUtil 工具类**，按职责拆分：
  - `MessageRateLimitRepository` — 手机号 + IP 防刷
  - `JwtBlacklistRepository` — JWT 黑名单管理
  - `CategoryCacheRepository` — 分类列表缓存
- 分类列表缓存 TTL 1 小时
- 手机号防刷 60 秒 + IP 每分钟 3 次

### 图片上传

| 约束 | 规则 |
|------|------|
| 允许格式 | 仅 `jpg`, `png`, `webp` |
| 单文件大小 | ≤ 5MB |
| 存储方式 | 上传到文件服务器 / OSS，数据库仅保存 URL 字符串 |
| 禁止 | 禁止将图片二进制存入 MySQL |

---

## 5. API 路径约定

| 前缀 | 认证 | 说明 |
|------|------|------|
| `/api/v1/public/**` | 无需认证 | 前台公开接口 |
| `/api/v1/admin/**` | JWT 认证 | 后台管理接口（登录接口除外） |
| `/api/v1/admin/login` | 无需认证 | 登录接口，Security 显式放行 |

### 响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "success": true
}
```

### 分页

- **前端页码从 1 开始**
- 后端接口统一接收 `page`（从 1 开始），内部转换为 `page - 1` 传给 MyBatis-Plus
- `size` 默认 20

### 时间格式

`YYYY-MM-DD HH:mm:ss`

### API 风格

- 基础 CRUD 接口必须遵循 RESTful 风格（`GET` 查询、`POST` 创建、`PUT` 更新、`DELETE` 删除）
- 领域行为接口（如上架/下架/发布/标记处理）允许使用动作式端点，如 `/products/{uuid}/publish`
- 动作式端点统一使用 `POST`
- 禁止混用多种风格表达同一行为

---

## 6. 领域上下文一览

| 上下文 | 聚合根 | 值对象 | 核心枚举 |
|--------|--------|--------|----------|
| 产品 | Product | ProductImage, ProductAttribute | ProductStatus |
| 内容 | Content | - | ContentType, ContentStatus |
| 分类 | Category | -（parentUuid 树形） | CategoryType |
| 留言 | ContactMessage | -（ip 后端注入，前端不采集） | MessageStatus |
| 认证 | User | LoginResult（内部类） | - |

---

## 7. 代码生成提示

1. **先判断上下文**：属于 product/content/category/message/auth 哪个域
2. **Domain 层写纯 Java**：聚合根、值对象、枚举、Repository 接口，零注解
3. **DTO/VO 按领域分包**：放在 `application/{context}/dto/` 和 `application/{context}/vo/`
4. **Service 接口与实现分离**：`service/` 放接口，`service/impl/` 放实现
5. **DTO↔Domain↔VO 转换必须经过 Assembler** — 禁止在 Controller/Service 中用 `BeanUtils.copyProperties`
6. **PO 用 `@Getter` + `@Setter`**（不用 `@Data`），枚举字段存 String
7. **Repository 查询返回 `Optional<T>`**，禁止返回 null，判空后抛 `BusinessException`
8. **Controller 只做**：参数接收 + `@Valid` + 调用 Service + 返回 `Result<T>`。**禁止做**：if/else 判断、调用 Repository/Redis、手动转换
9. **防刷逻辑放 `MessageRateLimitRepository`**，在 MessageService 中调用
10. **图片上传**：限制 `jpg/png/webp` ≤ 5MB，存 URL 到数据库
11. **分页转换**：前端传 `page=1`，后端 `pageParam - 1` 传给 MyBatis-Plus
12. **构造器注入**：用 `@RequiredArgsConstructor`，禁止 `@Autowired` 字段注入

---

## 8. 禁止事项（AI 容易踩坑）

| 禁止 | 原因 |
|------|------|
| Domain 层使用任何框架注解 | 破坏领域层纯洁性 |
| PO 使用 `@Data` | toString 循环引用、equals/hashCode 异常 |
| `@Autowired` 字段注入 | 隐藏循环依赖，不利于测试 |
| Controller 中写 if/else 业务判断 | 破坏分层，无法单元测试 |
| Service 中直接 `BeanUtils.copyProperties` | 绕过 Assembler，映射逻辑散落 |
| Repository 查询方法返回 `null` | 调用方需判空，容易遗漏 |
| RedisUtil 大而全工具类 | 业务 Key 散落，后期混乱 |
| 图片二进制存 MySQL | 性能灾难 |
| 字符串拼接打日志 | 性能差，可读性差 |
| 循环中打日志 | 日志爆炸 |