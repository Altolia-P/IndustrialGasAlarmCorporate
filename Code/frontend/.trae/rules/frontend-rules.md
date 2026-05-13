# 前端开发规则

> 供 AI 代码生成助手使用的前端开发规范，约束技术选型、分层架构和代码生成行为。
> 基于 Doc/ 下需求文档、架构设计文档、接口设计文档提取。

---

## 1. 技术栈

| 类别 | 选型 |
|------|------|
| 框架 | Vue 3 + Vite |
| 语言 | TypeScript |
| 路由 | Vue Router |
| 状态管理 | Pinia |
| HTTP | Axios |
| 样式 | 响应式布局，适配 375px - 1920px |
| UI 库 | 按需引入（Element Plus 或轻量方案） |
| 环境变量 | Vite `import.meta.env`，**禁止硬编码 localhost** |
| 代码规范 | ESLint |
| 格式统一 | Prettier |

---

## 2. 项目结构

```
src/
├── api/             # API 封装（按模块拆分，如 product.ts、message.ts）
├── assets/          # 静态资源
├── components/      # 公共 UI 组件（禁止调用 API）
├── composables/     # 可复用逻辑（分页、loading、表单提交、权限校验等）
├── layouts/         # 布局组件（FrontLayout / AdminLayout）
├── router/          # 路由配置（前台路由 + 后台路由，后台路由配 meta.requiresAuth）
├── stores/          # Pinia 状态管理（仅用户/认证状态）
├── types/           # TypeScript 类型定义（仅 VO 类型，无 DTO/PO/Domain）
├── utils/           # 工具函数（请求拦截、Token 管理、分页转换等）
├── views/           # 页面组件（只做展示和事件绑定，禁止直接调 API）
│   ├── home/
│   ├── product/
│   │   ├── list/
│   │   └── detail/
│   ├── content/
│   │   ├── list/
│   │   └── detail/
│   ├── contact/
│   └── admin/
│       ├── login/
│       ├── product/
│       │   ├── list/
│       │   ├── edit/
│       │   └── components/
│       ├── content/
│       │   ├── list/
│       │   ├── edit/
│       │   └── components/
│       └── message/
│           ├── list/
│           └── components/
├── App.vue
└── main.ts
```

> 目录嵌套建议不超过 4 层，避免过度套娃（如 `views/admin/product/components/dialogs/`）。

---

## 3. 前端分层约束（核心）

| 层 | 路径 | 职责 | 禁止 |
|---|------|------|------|
| **页面层** | `views/` | 页面展示、事件绑定、调用 composables | 禁止调 axios、禁止复杂业务逻辑、禁止操作 localStorage、禁止操作 Pinia 以外的全局状态 |
| **组件层** | `components/` | 可复用的 UI 组件 | 禁止调用 API、禁止操作 store |
| **API 层** | `api/` | 接口请求封装，返回 Promise<T> | 禁止处理 UI 状态（loading/error） |
| **可复用逻辑** | `composables/` | 分页、loading、表单、权限等逻辑抽象 | 禁止依赖具体页面组件 |
| **状态层** | `stores/` | 用户会话、Token、认证状态 | 禁止存页面列表数据、禁止承载业务逻辑、禁止调用 API |
| **工具层** | `utils/` | 纯工具函数 | 禁止依赖 Vue 组件或生命周期 |

### 调用关系

```
views → composables → api → axios
views → components（props/emits 通信）
views → stores（仅读/写用户状态）
views → utils（纯函数工具）
```

### 页面层职责

页面组件只负责：
- 页面结构组织
- composables 调用
- 组件组合
- 路由参数获取
- 用户事件绑定

禁止：
- 直接调用 axios
- 编写复杂业务逻辑或大量数据转换逻辑
- 操作 localStorage
- 操作 Pinia 以外的全局状态

---

## 4. 编码规范

### 命名

| 元素 | 规范 | 示例 |
|------|------|------|
| 组件 | PascalCase | `ProductList.vue` |
| 文件/目录 | kebab-case | `product-list.vue`、`user-profile/` |
| 变量/函数 | camelCase | `getProductList` |
| 常量 | UPPER_SNAKE_CASE | `TOKEN_KEY` |
| 枚举 | PascalCase | `ProductStatus` |
| API 函数 | camelCase + 模块前缀 | `productApi.getList()` |

### 组件规范

- 每个单文件组件（`.vue`）不超过 400 行，超过则拆分子组件
- 组件命名采用多单词，避免与 HTML 原生标签冲突
- 页面组件放 `views/`，公共组件放 `components/`
- **禁止**在组件中使用 `any` 类型
- **禁止**在页面/组件中出现超过 3 层 if/else 嵌套，复杂逻辑抽到 composables
- **禁止**在同一元素上同时使用 `v-if` 和 `v-for`（优先 `computed` 过滤）
- `v-for` 必须使用稳定唯一 `key`，**禁止使用 `index` 作为 key**
- **禁止用 `watch` 代替 `computed`**；优先 `computed`，其次 `watch`；禁止深度 `watch` 监听大型对象

### 组件风格

- 所有组件统一使用 `<script setup lang="ts">`
- 禁止使用 Options API
- 优先使用 Composition API

### 样式规范

- 使用 CSS 自定义属性（变量）管理主题色
- 移动端优先，保证 375px - 1920px 适配
- 禁止内联样式，样式写在 `<style scoped>` 中

### 类型边界

- **前端仅允许使用 VO 类型**，禁止出现 PO、Entity、Domain 等后端类型
- DTO 类型仅用于表单提交，不用于页面展示
- VO 类型定义在 `types/` 下，与 `api/` 返回结果对齐
- 示例：`ProductVO`（展示）、`SubmitMessageDTO`（表单提交）
- **禁止在组件中硬编码状态字符串**（如 `status === 'PUBLISHED'`），必须使用枚举类型 + 映射函数转换展示文案

### 组件通信

- 父子组件优先使用 `props` / `emits`
- 跨页面共享状态才允许使用 Pinia
- **禁止使用事件总线（event bus）**

---

## 5. API 对接规范

### 请求封装

- 所有 API 请求通过 Axios 实例发送，**禁止直接使用 fetch 或裸 axios**
- API 地址统一使用 `import.meta.env.VITE_API_BASE_URL`，**禁止硬编码**
- Axios 实例统一配置：
  - `baseURL`：环境变量指定
  - 请求拦截器自动注入 Token
  - 响应拦截器统一处理错误码
- 所有接口必须封装在 `api/{module}.ts`，页面和组件只能调用 `api` 层函数
- api 层统一返回 `response.data.data`（即 `Result.data`），**禁止返回 AxiosResponse 给调用方**

### 响应处理

```typescript
interface Result<T> {
  code: number;     // 0 成功，4xxx 业务异常，5xxx 系统异常
  message: string;
  data: T;
  success: boolean;
}

interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;   // 后端返回从 0 开始，前端展示时 +1
}
```

### 错误码处理

| code | 前端处理 |
|------|---------|
| `0` | 正常展示 |
| `4000` | Toast 提示 message，标红对应字段 |
| `4001`–`4004` | Toast 提示 |
| `4005` | 跳转登录页 |
| `4006`–`4007` | Toast 提示（含剩余次数/时间） |
| `4008` | 清除 Token，跳转登录页 |
| `4009` | Toast "无操作权限" |
| `5001` | Toast "系统繁忙，请稍后重试" |

### Token 管理

- Token 存入 `localStorage`，**操作统一封装在 `utils/auth.ts`**，页面禁止直接调 localStorage
- 请求拦截器注入 `Authorization: Bearer <token>`
- 响应 401/`4008` 时自动清除 Token 并跳转登录页
- 后台页面未登录 → 跳转 `/admin/login?redirect=原地址` → 登录成功 → 跳回原页面

### 图片上传规范

- 上传格式仅允许 `jpg`、`png`、`webp`
- 单文件 ≤ 5MB
- 上传前前端先校验格式与大小
- 上传接口使用 `multipart/form-data`
- 上传成功后保存返回的 URL 字符串

### API 风格

调用接口时必须与后端 API 风格一致：

- 基础 CRUD 使用 RESTful 风格（`GET` 查询、`POST` 创建、`PUT` 更新、`DELETE` 删除）
- 领域行为接口（如上架/下架/发布/标记处理）使用动作式端点，如 `POST /products/{uuid}/publish`
- 动作式操作统一使用 `POST`

---

## 6. 页面与路由

### 路由设计

```
前台路由（无需认证）：
/                   → 首页
/products           → 产品列表
/products/:uuid     → 产品详情
/solutions          → 解决方案列表
/solutions/:uuid    → 解决方案详情
/contact            → 联系我们

后台路由（需认证）：
/admin/login        → 登录页（无需认证）
/admin              → 后台首页
/admin/products     → 产品管理
/admin/contents     → 内容管理
/admin/messages     → 留言管理
```

### 路由权限

- 前后台路由通过不同的 layout 组件（FrontLayout / AdminLayout）隔离
- 后台路由配置必须加 `meta: { title: '页面名称', requiresAuth: true }`
- 全局 `beforeEach` 守卫检查 `requiresAuth`：
  - 无 Token → 跳转 `/admin/login?redirect=目标路径`
  - 有 Token → 放行
- 登录页不检查 Token，已登录直接跳转后台首页

### 页面与后端上下文映射

| 前端页面 | 后端上下文 | 聚合根 | 核心 API 模块 |
|----------|-----------|--------|--------------|
| 产品列表 / 详情 | `product` | Product | `api/product.ts` |
| 解决方案列表 / 详情 | `content` | Content | `api/content.ts` |
| 分类筛选 | `category` | Category | `api/category.ts` |
| 留言提交 | `message` | ContactMessage | `api/message.ts` |
| 后台产品管理 | `product` + `category` | Product, Category | `api/product.ts`、`api/category.ts` |
| 后台内容管理 | `content` + `category` | Content, Category | `api/content.ts`、`api/category.ts` |
| 后台留言管理 | `message` | ContactMessage | `api/message.ts` |
| 管理员登录 | `auth` | User | `api/auth.ts` |

---

## 7. 交互规范

| 场景 | 要求 |
|------|------|
| 加载状态 | 按钮点击后进入 loading 并 disabled；列表展示骨架屏或加载动画 |
| 空状态 | 列表无数据时展示占位图 + 引导文案（如"暂无产品"） |
| 表单校验 | 所有表单必须使用响应式 form 对象，提交前调 `validate()`；后端 4000 错误映射到对应字段标红 |
| 接口错误 | 页面顶部 Toast 提示，3 秒自动消失 |
| 删除操作 | 必须弹窗二次确认（"确认删除该产品？"） |
| 批量操作 | 勾选后弹窗二次确认 |
| 分页 | 每页 20 条，页码从 **1** 开始，请求时转换为 `page - 1` 发给后端 |
| 时间显示 | 统一使用 `YYYY-MM-DD HH:mm:ss` |
| 重复提交 | 提交按钮点击后立即 disabled，防止重复点击 |
| 网络超时 | 超过 10 秒提示"网络异常，请稍后重试" |
| 表单数据结构 | 使用 `reactive()` 定义表单对象，禁止大量 `ref()` 分散字段；提交逻辑统一由 `useFormSubmit()` 封装 |
| 后台列表页 | 统一包含：搜索区域 + 表格区域 + 分页区域 + loading 状态 + empty 状态 + 删除确认弹窗 |

---

## 8. 状态管理

- 全局共享状态（用户信息、Token 等）放入 Pinia store
- 页面级数据（产品列表、留言列表）**在组件内管理，不放入 store**
- API 请求状态（loading/error）在组件局部管理，不在 store 中维护
- **禁止**在 Pinia 中存储：列表数据、分页数据、临时表单数据、页面级筛选条件
- **禁止在页面/组件中直接调用 store 方法获取业务数据**（如 `store.fetchProducts()`），数据请求必须通过 `composables → api` 链路
- **禁止使用事件总线**替代状态管理

---

## 9. 可复用逻辑（composables）

可复用逻辑必须抽离到 `composables/`，例如：

| composable | 用途 |
|------------|------|
| `usePagination()` | 分页逻辑（页码、页大小、总条数、切换事件） |
| `useLoading()` | loading 状态管理 |
| `useFormSubmit()` | 表单提交（校验、loading、成功/错误处理） |
| `useAuth()` | 用户认证状态、Token 管理 |

### composables 边界

composables 只允许封装：
- 可复用的状态逻辑
- UI 无关的逻辑抽象
- 通用的交互逻辑（分页、loading、提交）

禁止：
- 包含页面专属业务逻辑
- 操作 DOM
- 包含大型 if/else 业务流程

---

## 10. AI 生成优先级

生成代码时必须按以下优先级决策：

1. **最高优先级**：本前端开发规则
2. **次高优先级**：接口设计文档（接口规则Skill.md）
3. **参考优先级**：需求文档（功能描述、交互规则）
4. **最低优先级**：自主推断

若规则冲突：
- 以前端开发规则为**最高优先级**
- 禁止 AI 自行补充未定义的业务逻辑
- 遇到不明确的业务场景，生成 `// TODO: 确认业务逻辑` 注释替代猜测

---

## 11. 禁止事项（AI 容易踩坑）

| 禁止 | 原因 |
|------|------|
| 使用 `any` 类型 | 失去 TypeScript 类型保护 |
| 页面/组件中直接调 `axios.get` | 绕过 API 层，URL 散落 |
| API URL 硬编码 | 环境切换需全局替换 |
| 硬编码 `localhost` | 无法适配不同环境 |
| 页面中直接操作 `localStorage` | Token 操作分散，安全风险 |
| 组件中直接调用 API | 违反分层约束，无法复用 |
| 使用 PO、Entity、Domain 类型 | 暴露后端实现，耦合 DDD 内部结构 |
| 事件总线（event bus） | 难以追踪，Vue3 推荐 props/emits + Pinia |
| 页面出现超过 3 层 if/else | 复杂度高，应抽到 composables |
| 忽略 loading / empty / error 状态 | 需求文档明确要求 |
| 前端页码直接传 0 给后端 | 用户看到第 0 页，交互不友好 |
| 删除操作不经二次确认 | 不可逆操作 |
| 图片上传前不校验格式和大小 | 后端限制 jpg/png/webp ≤ 5MB |
| 组件中硬编码业务状态字符串（如 `status === 'PUBLISHED'`） | 应使用枚举类型 + 映射函数 |
| 同一元素同时使用 `v-if` 和 `v-for` | Vue 官方不推荐，优先 computed 过滤 |
| `v-for` 使用 `index` 作为 `key` | 破坏组件复用和列表渲染稳定性 |
| 用 `watch` 代替 `computed` | computed 更声明式、可缓存、性能更好 |
| api 层返回 AxiosResponse 给调用方 | 调用方需 `res.data.data`，层级混乱 |
| 页面/组件直接调 store action 获取业务数据 | 应通过 composables → api 链路 |
| 组件超过 400 行不拆分 | 难于维护和测试 |

---

## 12. 智能体调用规范

### 12.1 总体架构

```
                         ┌──────────────┐
                         │ Project Lead │  ← 需求门禁
                         └──────┬───────┘
                                │ 结构化需求
                         ┌──────▼───────┐
                         │   Trae       │
                         │ Orchestrator │
                         └──────┬───────┘
                                │
            ┌───────────────────┼───────────────────┐
            │                   │                   │
      ┌─────▼─────┐       ┌─────▼─────┐       ┌─────▼─────┐
      │ 核心开发层  │       │ 质量保障层  │       │ 工程支撑层  │
      └─────┬─────┘       └─────┬─────┘       └─────┬─────┘
            │                   │                   │
    ┌───────┼───────┐     ┌─────┼─────┐     ┌───────┼───────┐
    │       │       │     │     │     │     │       │       │
    ▼       ▼       ▼     ▼     ▼     ▼     ▼       ▼       ▼
  CBuild  Styler  Layout TWriter A11y Perf  Integr  Build  Migrat
    │       │       │     └─────┼─────┘     │       │       │
    ▼       ▼       ▼           ▼           ▼       ▼       ▼
  SDesigner LArch  Reviewer   Profiler    DocW   (others)
```

**调用链路**：`Project Lead → Trae Orchestrator → 三层子智能体`

### 12.2 各层智能体职责

#### 需求门禁层

| 智能体 | 职责 | 触发条件 |
|--------|------|---------|
| **Project Lead** | 需求质量把关、澄清模糊点、输出结构化需求文档 | 任何新增开发需求首次提交时 |

- 需求不明确时，Project Lead **直接追问用户**，不进入后续流程
- 输出结构化需求后，交由 Trae Orchestrator 拆解执行
- 单纯的 bug 修复、样式微调等小改动**无需经过** Project Lead

#### 核心开发层

| 智能体 | 职责 | 典型场景 |
|--------|------|---------|
| **component-builder** | 生成 Vue 3 组件（Element Plus + Vite） | 新建页面组件、表单组件、弹窗组件 |
| **styling-implementation-expert** | Tailwind CSS 样式实现、设计令牌管理、主题切换 | 样式调整、主题色修改、响应式适配 |
| **layout-architect** | 页面布局设计、响应式断点配置、Grid/Flex 方案 | 页面整体布局、复杂响应式结构 |

#### 质量保障层

| 智能体 | 职责 | 典型场景 |
|--------|------|---------|
| **test-writer** | 生成单元测试、集成测试、E2E 测试 | 组件测试、接口测试、交互测试 |
| **a11y-auditor** | 可访问性审计（ARIA、键盘导航、色彩对比度） | 页面可访问性检查 |
| **frontend-code-reviewer** | 前端代码审查（安全、最佳实践） | 上线前检查、代码审查 |
| **performance-profiler** | 前端性能诊断（Bundle 分析、Web Vitals） | 加载慢、渲染卡顿、包体积过大 |

#### 工程支撑层

| 智能体 | 职责 | 典型场景 |
|--------|------|---------|
| **api-integrator** | 前端 API 对接（请求封装、错误处理、状态管理） | 对接新接口、错误处理优化 |
| **build-engineer** | 构建优化（Vite/Webpack 配置、依赖管理） | 构建慢、包体积大、依赖升级 |
| **code-migration-runner** | 框架/库版本升级、Breaking Change 修复 | Vue 2→3 迁移、ESLint 升级 |
| **component-documentation-writer** | 组件文档生成（Storybook、README、JSDoc） | 组件库文档补充 |
| **state-designer** | 复杂状态逻辑设计（Zustand/Pinia/Context） | 状态架构重构、数据流设计 |

### 12.3 调用规则

| 规则 | 说明 |
|------|------|
| **需求先行** | 新功能开发必须先经 Project Lead 评估，输出结构化需求后再进入开发 |
| **门禁阻断** | 需求不明确时 Project Lead 直接追问用户，**禁止**带着模糊需求进入 Orchestrator |
| **小改动直通** | bug 修复、文案修改、样式微调等单文件改动，**无需**经过 Project Lead，直接执行 |
| **层级不跳过** | Orchestrator 负责将需求拆解为子任务并分派到对应层级的智能体，**禁止**跨层直接调用 |
| **同层可并行** | 同一层内的多个智能体任务可以并行执行（如同时调用 component-builder 和 styling-implementation-expert） |
| **质量必经** | 核心开发层完成后，必须经过质量保障层（至少 code-reviewer）检查，方可交付 |
| **结果汇总** | Orchestrator 负责收集所有子智能体的执行结果，整合后统一交付给用户 |

### 12.4 智能体调用决策树

```
用户提交需求
    │
    ├─ 是否为新增功能/大范围改动？
    │   ├─ 是 → 调用 Project Lead（需求门禁）
    │   │        ├─ 需求明确 → 输出结构化需求 → 调用 Trae Orchestrator
    │   │        └─ 需求不明确 → 追问用户 → 等待回复 → 重新评估
    │   │
    │   └─ 否（bug 修复/小改动）→ 直接执行，无需 Project Lead
    │
    ├─ 是否涉及多个模块/文件？
    │   ├─ 是 → 调用 Trae Orchestrator 拆解任务
    │   └─ 否 → 直接调用对应专业智能体或主助手直接完成
    │
    └─ 是否需要质量检查？
        ├─ 上线前/大改动后 → 调用 frontend-code-reviewer
        └─ 日常开发 → 可选
```

### 12.5 禁止事项

| 禁止 | 原因 |
|------|------|
| 跳过 Project Lead 直接开发大功能 | 需求理解偏差导致返工 |
| 需求不明确时自行猜测并继续 | 产出不符合预期，浪费资源 |
| 跨层直接调用子智能体 | 破坏任务拆解和协调机制 |
| Orchestrator 未汇总就交付 | 用户无法了解完整改动范围 |
| 单文件小改动走完整门禁流程 | 效率低下，过度工程化 |
