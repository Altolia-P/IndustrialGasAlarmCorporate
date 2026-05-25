# 工业气体报警企业官网系统 — 项目全貌文档

> 生成日期：2026-05-25 | 分支：main

---

## 1. 项目概述

**定位**：ToB 工业气体报警设备企业官网，提供产品展示、解决方案展示、客户留言收集、工单跟踪及后台内容管理功能。

**技术栈**：

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.5.14 + Java 17 |
| ORM | MyBatis-Plus 3.5.5 |
| 安全 | Spring Security + JJWT 0.12.5 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis |
| AI | LangChain4j + DeepSeek (OpenAI 兼容协议) |
| 前端框架 | Vue 3 + Vite + TypeScript |
| UI 库 | Element Plus |
| 状态管理 | Pinia |
| 路由 | Vue Router 4 |
| HTTP 客户端 | Axios |

**架构模式**：后端采用 DDD 四层架构 + Assembler 隔离；前端采用 Vue 3 分层架构（views → composables → api → axios）。

**代码规模**：后端 182 个 Java 源文件，前端约 120 个源文件，覆盖 8 个业务领域。

---

## 2. 后端架构

### 2.1 整体分层

```
interfaces/          ← Controller：参数校验，路由分发，返回 Result<T>
    ↓ 调用
application/         ← Service 接口 + ServiceImpl：业务编排，@Transactional
    ↓ 依赖 (接口)
domain/              ← 聚合根 + Repository 接口：零框架注解，纯 POJO
    ↑ 实现
infrastructure/      ← RepositoryImpl + Mapper + PO：数据持久化
    ↑ 隔离
assembler/           ← DTO ↔ Domain ↔ VO：纯映射，无业务逻辑
```

### 2.2 Common 层（通用基础）

**路径**：`common/`

| 文件 | 职责 |
|------|------|
| `Result<T>` | 统一响应体 `{code, message, data, success}`，code=0 表示成功 |
| `Page<T>` | 分页结果 `{content, totalElements, totalPages, size, number}` |
| `BaseEntity` | 实体基类（审计字段） |
| `ErrorCode` | 枚举错误码：4000 参数校验、4001-4011 各业务实体不存在、5001 系统错误 |
| `BusinessException` | 业务异常基类，携带 ErrorCode |
| 子异常类 | `ProductNotFoundException`、`UserNotFoundException`、`UnauthorizedException` 等 10 个精细化异常 |
| `JwtUtil` | JJWT 0.12.5：生成/解析 Token，claims 含 userUuid、username、role |
| `CaptchaGenerator` | 图形验证码生成 |

### 2.3 Domain 层（领域模型，8 个上下文）

**特点**：零框架注解，纯 Java 对象，充血模型（行为在聚合根内部）。

#### 2.3.1 Shared（共享内核）

- **`AggregateRoot`** — 聚合根基类，维护 `List<DomainEvent>`，提供 `collectEvent()` / `clearEvents()`
- **`DomainEvent`** — 领域事件基类，含 eventId (UUID) 和 occurredOn

#### 2.3.2 Product（产品上下文）

- **`Product`** (聚合根)：name、description、coverImage、images (List)、attributes (List)、categoryUuid、status
- **`ProductStatus`** 枚举：`DRAFT → PUBLISHED ⇄ UNPUBLISHED`
- **`ProductImage`**：url、altText、sortOrder
- **`ProductAttribute`**：attrKey、attrVal
- **`ProductRepository`** 接口：findById、save、deleteById、findByCategory、findAll、findAllWithFilter（多条件筛选）、searchByKeyword（全文搜索）

#### 2.3.3 Content（内容上下文）

- **`Content`**：title、summary、body、coverImage、type (SOLUTION/NEWS)、status (DRAFT/PUBLISHED)、categoryUuid
- **`ContentType`** 枚举：`SOLUTION`、`NEWS`
- **`ContentStatus`** 枚举：`DRAFT`、`PUBLISHED`
- **`ContentRepository`** 接口：增删改查 + 分页筛选 + 关键词搜索

#### 2.3.4 Category（分类上下文）

- **`Category`**：name、type (PRODUCT_CATEGORY/CONTENT_CATEGORY)、parentUuid（支持树形）、sortOrder
- **`CategoryType`** 枚举：`PRODUCT_CATEGORY`、`CONTENT_CATEGORY`
- **`CategoryRepository`** 接口：按 type 查列表、CRUD

#### 2.3.5 Message（留言上下文）

- **`ContactMessage`** (聚合根)：name、phone、content、ip、status (PENDING→IN_PROGRESS→PROCESSED)、assignedStaffUuid/Name、processor、remark、submittedAt、processedAt
- **领域行为**：`assign(staffUuid, staffName)` → IN_PROGRESS；`markProcessed(processor, remark)` → PROCESSED
- **`MessageRepository`** 接口：多维度查询（按姓名/手机/状态/员工Uuid）

#### 2.3.6 Auth（认证上下文）

- **`User`** (聚合根)：username、passwordHash、phone、company、failCount、locked、lockTime、role
- **核心行为**：`login(rawPassword, passwordHasher)` → 5次失败锁定30分钟；`changePassword()`
- **`PasswordHasher`** 接口（领域层定义接口，基础设施层 BCrypt 实现）
- **`LoginResult`**：领域值对象，封装登录结果（success/failed + 消息）
- **`UserRepository`** 接口：findByUsername、save、existsByUsername

#### 2.3.7 Staff（员工上下文）

- **`Staff`**：name、phone、email、role (FIELD_TECH/CUSTOMER_SERVICE/TECH_SUPPORT/AFTER_SALES)、status (VACATION/STANDBY/WORKING/BUSINESS_TRIP)、createdAt
- **`StaffRepository`** 接口：多条件分页查询 + CRUD

#### 2.3.8 WorkOrder（工单上下文）

- **`WorkOrder`** (聚合根)：title、type (TECH_SUPPORT/AFTER_SALES)、description、status (PENDING→IN_PROGRESS→COMPLETED)、priority (HIGH/MEDIUM/LOW)、customerName/Phone、assignedStaffUuid/Name、resolution
- **领域行为**：`assign()` → IN_PROGRESS；`complete(resolution)` → COMPLETED
- **`WorkOrderRepository`** 接口：多维度查询 + CRUD

### 2.4 Application 层（业务编排）

每个领域上下对应一组 Service 接口及实现类，负责：
- DTO 校验 → 调用 Repository 加载聚合根 → 调用聚合根方法 → 通过 Assembler 转换为 VO → 返回
- `@Transactional` 事务管理
- 领域事件发布

#### Service 接口一览

| Service | 核心方法 |
|---------|---------|
| **ProductService** | createProduct、updateProduct、publishProduct、unpublishProduct、getProduct、findPublicProducts、findAdminProducts（多条件筛选）、deleteProduct |
| **ContentService** | createContent、updateContent、publishContent、unpublishContent、getContent、findPublicContents（按 type）、findAdminContents（多条件）、deleteContent |
| **CategoryService** | getCategoriesByType、createCategory、updateCategory、deleteCategory |
| **MessageService** | submitMessage（+IP 频率限制）、assignMessage、markProcessed、batchProcess、getMessage、findMessages（多条件）、findUserMessages、findStaffMessages |
| **AuthService** | login（含验证码）、generateCaptcha、register、getCurrentUser、logout（JWT 黑名单）、resetPassword |
| **StaffService** | findStaffs（多条件筛选）、getStaff、createStaff、updateStaff、deleteStaff |
| **WorkOrderService** | findWorkOrders（多条件）、getWorkOrder、createWorkOrder、updateWorkOrder、assignWorkOrder、completeWorkOrder、deleteWorkOrder、findMyTasks（员工视角）、findUserWorkOrders（客户视角） |
| **AIChatService** | chat（发送消息 + 返回 AI 回复 + 产品/方案推荐） |

### 2.5 Assembler 层（映射隔离）

8 个 Assembler，纯静态方法，DTO ↔ Domain ↔ VO 映射。**严格禁止在 Service/Controller 中使用 `BeanUtils.copyProperties`**。

### 2.6 Infrastructure 层

#### 2.6.1 配置 (`config/`)

| 配置类 | 职责 |
|--------|------|
| **SecurityConfig** | Spring Security：CSRF 关闭、无状态 Session、JWT Filter 前置；`/api/v1/public/**` 公开；`/api/v1/admin/**` 需 ADMIN 角色；`/api/v1/staff/**` 需 STAFF/ADMIN；`/api/v1/user/**` 需认证 |
| **WebConfig** | CORS 跨域配置 |
| **MyBatisPlusConfig** | 分页插件、MetaObjectHandler 自动填充审计字段 |
| **RedisConfig** | RedisTemplate 序列化配置 |
| **DataInitializer** | 应用启动时初始化数据（管理员账号等） |
| **GlobalExceptionHandler** | `@RestControllerAdvice`：BusinessException → 200+错误码；MethodArgumentNotValidException → 400；Exception → 500 |

#### 2.6.2 安全 (`security/`)

- **`JwtAuthFilter`** (`OncePerRequestFilter`)：提取 Bearer Token → 验证黑名单 → 解析 JWT → 设置 SecurityContext 和 request attributes (userUuid, username, role)
- **`BCryptPasswordHasher`**：实现 domain 层定义的 `PasswordHasher` 接口

#### 2.6.3 Redis 仓库 (`redis/`)

按职责分离为独立的 Redis Repository（无单体 RedisUtil）：

| Repository | Key 前缀 | 用途 |
|------------|----------|------|
| **JwtBlacklistRepository** | `jwt:blacklist:` | 登出 Token 黑名单，TTL = JWT 剩余有效期 |
| **CaptchaRepository** | `captcha:` | 图形验证码存储（5分钟过期） |
| **MessageRateLimitRepository** | `msg:rate:` | 留言 IP 频率限制 |
| **AIChatRateLimitRepository** | `ai:rate:` | AI 对话频率限制 |
| **CategoryCacheRepository** | `cat:cache:` | 分类数据缓存 |
| **ChatSessionRepository** | `chat:session:` | AI 对话会话上下文 |

#### 2.6.4 AI 集成 (`ai/`)

- **`DeepSeekConfig`**：通过 LangChain4j 的 `OpenAiChatModel` 接入 DeepSeek API（OpenAI 兼容协议），支持 chat 和 embedding
- **`EmbeddingSearchService`**：基于 Embedding 的语义搜索服务
- **`ProductTextProvider` / `SolutionTextProvider`**：为语义搜索提供产品和方案文本

#### 2.6.5 事件 (`event/`)

- **`SpringEventBus`**：实现 domain 层的 `EventBus` 接口，基于 Spring `ApplicationEventPublisher`
- **事件监听器**：`AccountLockedEventListener`、`MessageSubmittedEventListener`、`ProductPublishedEventListener`

#### 2.6.6 持久化 (`repository/`)

每个 Domain Repository 接口对应一个 Infrastructure 实现：
- RepositoryImpl 负责 PO ↔ Domain 转换 + 调用 Mapper
- Mapper 继承 MyBatis-Plus `BaseMapper<PO>`
- PO 使用 `@TableName` 映射数据库表，字段 `@TableField`

#### 2.6.7 文件存储 (`storage/`)

- **`FileStorageService`**：处理图片上传（jpg/png/webp，≤5MB），存储并返回 URL

---

## 3. 前端架构

### 3.1 分层结构

```
views/          ← 页面：展示 + 事件绑定 + 调用 composable
    ↓ 使用
composables/    ← 可复用逻辑（分页、加载、表单提交、聊天）
    ↓ 调用
api/            ← Axios 请求封装，返回 Promise<T>
    ↓ 依赖
utils/request   ← Axios 实例 + 拦截器（Token 注入、响应解包、401 处理）
stores/         ← Pinia（仅 auth 和 ai-chat 状态）
types/          ← TypeScript 类型定义 + 枚举映射
```

### 3.2 入口与技术组合

- **`main.ts`**：创建 Vue App → 注册 Pinia → 注册 Router → 注册 Element Plus → 挂载
- **`app.vue`**：`<RouterView />` + 全局 `<AiAssistant />`（悬浮 AI 助手）
- **构建工具**：Vite，`vite.config.ts` 配置了 `@` 路径别名

### 3.3 类型系统 (`types/`)

每个业务域对应一个类型文件，定义：
- **枚举**（如 `ProductStatus`）及其中文映射（`ProductStatusMap`）
- **VO 接口**（列表项）：用于列表展示
- **DetailVO 接口**：包含完整字段（如 body、images、attributes）
- **DTO 接口**：用于创建/更新请求

示例：`ProductVO`（7字段），`ProductDetailVO`（继承+扩展，10字段），`CreateProductDTO`（含 File 字段）

### 3.4 API 层 (`api/`)

8 个 API 模块，每个模块导出对象包含方法，均通过 `request` 工具函数调用：

| 模块 | 公开接口 | 管理接口 |
|------|---------|---------|
| **product** | getPublicList、getPublicDetail | getAdminList、create、update、remove、publish、unpublish |
| **content** | getPublicList、getPublicDetail | getAdminList、create、update、remove、publish |
| **category** | getCategories | getAdminCategories、create、update、remove |
| **message** | submit | getAdminList、assign、process、processBatch、getUserMessages、getStaffInquiries |
| **auth** | register | login、loginWithCaptcha、getCaptcha、getCurrentUser、logout、resetPassword |
| **ai** | — | sendMessage（`/user/ai/chat`） |
| **workorder** | — | getAdminList、getByUuid、create、update、assign、complete、remove、getMyTasks、getUserWorkOrders |
| **staff** | — | getAdminList、getByUuid、create、update、remove |

### 3.5 HTTP 请求封装 (`utils/request.ts`)

- **Axios 实例**：baseURL 从 `VITE_API_BASE_URL` 环境变量读取，超时 10s
- **请求拦截器**：自动注入 `Authorization: Bearer <token>`
- **响应拦截器**：
  - `code === 0` → 直接返回 `data`（解包 `Result<T>`）
  - `code === 4008`（未登录）→ 清除 token，跳转登录页
  - HTTP 401 → 同等处理
  - 网络超时 → 统一错误提示
  - **关键设计**：API 调用方拿到的是 `Promise<T>` 而非 `Promise<AxiosResponse<Result<T>>>`，对业务代码完全透明

### 3.6 状态管理 (`stores/`)

**`auth.ts` (useAuthStore)**：
- State：token、userUuid、username、role、tokenVerified、verifying
- Getters：isLoggedIn、isAdmin、isStaff
- Actions：loginSuccess（设置 token+role+验证状态）、logout（清除 + 调 API）、verifyToken（调 `/admin/currentUser` 验证）

**`ai-chat.ts` (useAiChatStore)**：
- State：sessionId、messages（ChatMessageVO[]）、isOpen、isMinimized、unreadCount、isSending
- Actions：toggleOpen、minimize、addMessage、clearMessages、setSending、incrementUnread、clearUnread

### 3.7 Composables（可复用逻辑）

| Composable | 功能 |
|------------|------|
| **useAuth** | 认证相关逻辑封装 |
| **useChat** | AI 对话核心逻辑：sendMessage → API 调用 → 打字机动画（逐字显示，30-50ms/字）→ 推荐产品/方案展示 |
| **useFormSubmit** | 表单提交 + loading + 错误处理通用逻辑 |
| **useLoading** | 加载状态管理 |
| **useLogin** | 登录表单逻辑（含验证码模式） |
| **usePagination** | 分页参数管理（page/size/total） |

### 3.8 路由设计 (`router/index.ts`)

**4 个路由分组**：

#### 前台路由 (`/` → front-layout)
| 路径 | 页面 | 说明 |
|------|------|------|
| `/` | home | 首页 |
| `/products` | product-list | 产品列表 |
| `/products/:uuid` | product-detail | 产品详情 |
| `/solutions` | content-list (SOLUTION) | 解决方案列表 |
| `/solutions/:uuid` | content-detail | 方案详情 |
| `/support` | support | 服务支持 |
| `/about` | about | 关于我们 |
| `/contact` | contact | 联系我们 |
| `/news/:id` | news-detail | 新闻详情 |

#### 用户中心 (`/user` → user-layout，需认证)
| 路径 | 页面 |
|------|------|
| `/user` | user-center（概览） |
| `/user/profile` | user-profile（个人信息） |
| `/user/inquiries` | user-inquiries（我的咨询） |
| `/user/tickets` | user-tickets（我的工单） |
| `/user/settings` | user-settings（账户设置） |

#### 员工后台 (`/staff` → staff-layout，需 STAFF 角色)
| 路径 | 页面 |
|------|------|
| `/staff` | staff-dashboard（概览） |
| `/staff/profile` | staff-profile |
| `/staff/tasks` | staff-task-list（工单任务） |
| `/staff/tasks/:uuid` | staff-task-detail |
| `/staff/inquiries` | staff-inquiries（客户咨询） |
| `/staff/settings` | staff-settings |

#### 管理后台 (`/admin` → admin-layout，需 ADMIN 角色)
| 路径 | 页面 |
|------|------|
| `/admin` | admin-dashboard（概览） |
| `/admin/products` / `create` / `:uuid/edit` | 产品管理 |
| `/admin/contents` / `create` / `:uuid/edit` | 内容管理 |
| `/admin/news` / `create` / `:uuid/edit` | 新闻管理 |
| `/admin/categories` | 分类管理 |
| `/admin/messages` | 留言管理 |
| `/admin/staff` / `create` / `:uuid/edit` | 员工管理 |
| `/admin/workorders` / `create` / `:uuid/edit` | 工单管理 |

**路由守卫** (`beforeEach`)：
- `requiresAdmin` 且角色非 ADMIN → 重定向
- `requiresStaff` 且角色非 STAFF → 重定向
- `requiresAuth` 且无 token → 跳转登录页
- `requiresAuth` 且有 token 但未验证 → 调 `verifyToken()` 验证
- 已登录用户访问登录页 → 按角色跳转对应首页

### 3.9 布局系统 (`layouts/`)

**4 个布局组件**：

| 布局 | 适用场景 | 结构 |
|------|---------|------|
| **front-layout** | 前台公开页面 | HeaderSection + `<router-view>` + FooterSection |
| **user-layout** | 用户中心 | 含用户导航侧边栏 |
| **staff-layout** | 员工后台 | 含员工导航 |
| **admin-layout** | 管理后台 | 深色侧边栏（#1a1a2e）+ el-menu（可折叠）+ 顶栏 + `<router-view>`；InterSense 品牌标识；角色头像 |

### 3.10 页面组件（Views）

#### 前台页面
- **首页 (`home.vue`)**：Hero区、产品预览 (`product-preview`)、解决方案选项卡 (`solution-tabs`)、优势卡片 (`advantage-cards`)、客户案例 (`client-cases`)、新闻滚动 (`news-scroll`)、CTA 行动号召 (`cta-section`)
- **产品列表/详情**：分类筛选 + 搜索 + 分页
- **解决方案列表/详情**：按 SOLUTION 类型展示
- **联系我们**：留言表单（name/phone/content）+ 提交
- **关于我们**：企业介绍
- **服务支持**：技术支持 + 下载

#### 管理后台页面
- **Dashboard**：统计卡片（产品数、留言数、工单数等）
- **CRUD 页面**：产品/内容/新闻/分类/员工/工单的完整增删改查，Element Plus Table + Form + Pagination
- **留言管理**：列表 + 指派员工 + 标记处理 + 批量处理
- **工单管理**：列表 + 创建/编辑 + 指派 + 完成

### 3.11 AI 助手组件 (`components/ai-assistant/`)

4 个组件构成完整的 AI 对话系统：

| 组件 | 职责 |
|------|------|
| **ai-assistant.vue** | 顶层容器：管理浮动球 + 对话框的显示/隐藏/最小化 |
| **ai-float-ball.vue** | 悬浮球：点击展开对话框，显示未读消息数徽标 |
| **ai-chat-dialog.vue** | 聊天对话框：消息列表 + 输入区 + 清空/最小化按钮 |
| **ai-chat-message.vue** | 单条消息：支持用户/AI 不同样式，AI 消息含打字动画 + 推荐产品卡片 + 推荐方案链接 |
| **ai-chat-input.vue** | 输入区：文本输入 + 发送按钮 + 字数限制 |

**对话流程**：
1. 用户在任意前台页面点击悬浮球 → 展开对话框
2. 输入消息 → `useChat().sendMessage()` → 调 `/api/v1/user/ai/chat`
3. 后端 DeepSeek API 返回回复 + 推荐产品列表 + 推荐方案列表
4. 前端逐字打字动画展示回复 → 在消息下方卡片展示推荐项

### 3.12 静态数据 (`data/`)

| 文件 | 内容 |
|------|------|
| **navigation.ts** | 导航栏结构（6 个一级菜单 + 子菜单）+ 页脚链接 |
| **home.ts** | 首页静态数据（优势卡片、客户案例等） |
| **solutions.ts** | 解决方案行业分类数据 |

---

## 4. API 接口总览

### 4.1 公开接口 `/api/v1/public`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/products` | 产品列表（支持 categoryUuid/name 筛选 + 分页） |
| GET | `/products/{uuid}` | 产品详情 |
| GET | `/contents?type=SOLUTION\|NEWS` | 内容列表（按类型 + 分类筛选） |
| GET | `/contents/{uuid}` | 内容详情 |
| GET | `/categories?type=PRODUCT_CATEGORY\|CONTENT_CATEGORY` | 分类列表 |
| POST | `/messages` | 提交留言 |
| GET | `/messages/{uuid}` | 留言详情 |
| GET | `/search?keyword=&limit=` | 全文搜索（产品+内容） |
| POST | `/register` | 用户注册 |

### 4.2 管理接口 `/api/v1/admin`（需 ADMIN 角色）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/login` | 管理员登录 |
| GET | `/captcha` | 获取验证码 |
| GET | `/currentUser` | 当前用户信息 |
| POST | `/logout` | 登出（JWT 加入黑名单） |
| POST | `/resetPassword` | 重置密码 |
| CRUD | `/products` | 产品管理（含上架/下架操作 + 图片上传） |
| CRUD | `/contents` | 内容管理（含发布/取消发布 + 图片上传） |
| CRUD | `/categories` | 分类管理 |
| GET/PUT | `/messages` | 留言管理（含指派/处理/批量处理） |
| CRUD | `/staff` | 员工管理 |
| CRUD | `/workorders` | 工单管理（含指派/完成操作） |

### 4.3 员工接口 `/api/v1/staff`（需 STAFF/ADMIN）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/inquiries` | 指派给我的留言 |
| GET | `/workorders` | 指派给我的工单任务 |

### 4.4 用户接口 `/api/v1/user`（需认证）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/messages` | 我的留言列表 |
| GET | `/workorders` | 我的工单列表 |
| POST | `/ai/chat` | AI 对话 |

---

## 5. 安全设计

- **认证**：JWT (HMAC-SHA)，Token 含 userUuid、username、role，可配置过期时间
- **登出**：Token 加入 Redis 黑名单，TTL = JWT 剩余有效期
- **角色**：ADMIN（管理员）、STAFF（员工）、普通注册用户
- **密码**：BCrypt 加密，5次登录失败锁定 30 分钟
- **频率限制**：留言提交按 IP 频率限制；AI 对话按 IP 频率限制
- **文件上传**：仅允许 jpg/png/webp，≤5MB

---

## 6. 数据库设计要点

- 主键：全表 UUID v4 CHAR(36)，应用层生成，无自增
- 表前缀：`t_` + snake_case
- 乐观锁：`version INT DEFAULT 0`，MyBatis-Plus `@Version`
- 逻辑删除：`deleted TINYINT(1) DEFAULT 0`
- 审计字段：`created_at`、`updated_at` 自动填充
- 无物理外键

核心表：`t_product`、`t_product_image`、`t_product_attribute`、`t_content`、`t_category`、`t_contact_message`、`t_admin_user`、`t_staff`、`t_work_order`、`t_operation_log`、`t_system_config`、`t_event_outbox`

---

## 7. 项目亮点总结

1. **DDD 四层架构**：严格分层依赖，领域层无框架侵入，充血模型封装业务规则
2. **Assembler 模式**：DTO/Domain/VO 映射集中在 Assembler 层，避免污染 Service/Controller
3. **职责分离的 Redis 仓库**：6 个独立 Redis Repository 各司其职，无单体 Util
4. **前端响应解包**：Axios 拦截器自动从 `Result<T>` 解包出 `data`，API 调用方无感
5. **AI 集成**：基于 LangChain4j + DeepSeek，实现智能客服对话 + 产品/方案推荐 + 打字机动画
6. **三层角色体系**：ADMIN（全量管理）→ STAFF（工单/咨询处理）→ User（自助查询），路由守卫 + API 鉴权双保险
7. **工单生命周期**：PENDING → (assign) → IN_PROGRESS → (complete) → COMPLETED，与留言流程解耦
8. **安全保障**：JWT 黑名单、登录锁定、IP 频率限制、文件上传校验、全局异常处理
