# Changelog

## [Unreleased]

### Fixed
- **乐观锁字段缺失** — 所有 7 个业务 PO 新增 `@Version private Integer version`，对齐数据库约定
- **ProductAttributePO / ProductImagePO 主键策略错误** — `IdType.AUTO` 自增 → 改为 `String` UUID 主键，对齐数据库 `CHAR(36)` 定义

### Changed
- **前端留言类型对齐后端** (`src/types/message.ts`)
  - 移除 `IN_PROGRESS` 枚举值（仅保留 `PENDING` / `PROCESSED`）
  - 移除 `assignedStaffUuid`、`assignedStaffName` 字段
  - 废弃 `AssignMessageDTO` / `CompleteMessageDTO` → 改为 `ProcessMessageDTO`
- **前端留言 API 对齐后端** (`src/api/message.ts`)
  - `assign()` / `complete()` → 统一为 `process(uuid, ProcessMessageDTO)`
- **前端留言管理列表** (`src/views/admin/message/list/admin-message-list.vue`)
  - 全面重写：移除指派人、处理中状态、拆单功能，改用真实 `messageApi.process`
  - 状态选项仅保留"未处理"/"已处理"，操作仅保留"处理"按钮+弹窗填备注
- **移除超前员工端/工单模块**
  - 删除：`types/staff.ts`、`types/workorder.ts`、`api/staff.ts`、`api/workorder.ts`、`data/workorder.ts`、`layouts/staff-layout.vue`
  - 删除：`views/staff/`、`views/admin/staff/`、`views/admin/workorder/` 全部页面
  - 路由：移除 `/staff/*`、`/admin/staff/*`、`/admin/workorder/*`；移除 `requiresStaff` 守卫
  - Store：移除 `isStaff` 计算属性
  - 布局：`admin-layout.vue` 移除「员工管理」「工单管理」菜单
  - 登录跳转：移除 `STAFF` 角色分支，`contact.vue` 移除 `isStaff` 引用
- **新建后端基础设施 PO**
  - `OperationLogPO` — 操作审计日志（`t_operation_log`）
  - `SystemConfigPO` — 系统键值配置（`t_system_config`，含 `@Version`）
  - `EventOutboxPO` — 可靠消息发件箱（`t_event_outbox`）
- **新建数据库增量迁移脚本** (`Doc/04_系统设计/3.数据库设计/migration_V1.3.sql`)
  - `t_admin_user` 新增 `phone` / `company` 列
  - 所有业务表新增 `version` 列

## [Unreleased]

### Added
- **新闻详情页** (`src/views/news/detail/news-detail.vue`)
  - 品牌深色 Hero 区：面包屑导航、分类标签、标题、发布日期
  - 双栏布局：左侧正文（段落渲染）+ 右侧侧边栏（相关新闻推荐，sticky 定位）
  - 响应式设计适配 375px–1920px
- **后台新闻管理列表页** (`src/views/admin/news/list/admin-news-list.vue`)
  - 搜索筛选：按标题 / 分类查询
  - 表格展示：标题、分类、状态（草稿/已发布）、创建时间、操作
  - 支持新建、编辑、删除、发布/下架操作
  - 分页功能
- **后台新闻编辑页** (`src/views/admin/news/edit/admin-news-edit.vue`)
  - 表单字段：标题（100字限制 + 字数提示）、分类选择（公司新闻/行业动态/产品发布）、正文（16行文本域）、封面图片上传、状态单选（草稿/已发布）
  - 提交前校验
- **新闻路由配置**
  - 前端路由：`/news/:id` → `NewsDetail`
  - 后台路由：`/admin/news` → `AdminNewsList`、`/admin/news/create` → `AdminNewsCreate`、`/admin/news/:uuid/edit` → `AdminNewsEdit`
- **新闻数据结构扩展** (`src/data/home.ts`)
  - `NewsItem` 接口新增 `body` 字段
  - 为全部 6 条新闻数据补充了完整的正文段落内容

### Fixed
- **方案 Tab 页"了解详情"链接错误**
  - 文件：`src/views/home/components/solution-tabs.vue`
  - 问题：所有方案按钮均跳转到 `/solutions`（始终显示石油化工页面）
  - 修复：改为动态路由 `/solutions/${currentSolution.value.id}`，根据当前选中的 Tab 跳转对应方案详情
- **跨 Tab 登录状态不同步** (`src/router/index.ts`)
  - 问题：第一个 Tab 登录后，第二个 Tab 打开首页按钮仍显示"登录/注册"（而非"用户中心"）
  - 原因：公开页面路由守卫不校验 token，Pinia store 的 `tokenVerified` 始终为 `false`，导致 `isLoggedIn` 为 `false`
  - 修复：公开页面 + 已存储 token 时，路由守卫静默调用 `verifyToken()` 同步状态；无效 token 自动清除

### Changed
- **后台管理侧边栏增强** (`src/layouts/admin-layout.vue`)
  - 新增用户头像区：首字母头像（蓝色渐变圆形）+ 用户名 + "管理员"角色标签
  - 顶部 header 新增"← 返回首页"链接，点击跳转前台首页
  - 移除旧版纯文本用户名显示，样式对齐普通用户后台（`user-layout.vue`）
- **客户案例区样式重设计** (`src/views/home/components/client-cases.vue`)
  - 移除：`logoColors` 颜色数组及每个客户项不同的彩色背景
  - 移除：`tagClass` 数组及 `.tag-blue`、`.tag-green`、`.tag-amber` 多色标签样式
  - 统一：客户项白底 + `var(--color-gray-200)` 边框
  - 统一：行业标签使用 `var(--color-primary)` 文本色 + `rgba(59, 130, 246, 0.08)` 背景
  - 新增：hover 时边框变主题色 + 浅蓝色投影效果
- **新闻卡片点击跳转** (`src/views/home/components/news-scroll.vue`)
  - 新闻卡片新增 `@click` 事件，点击跳转至 `/news/${id}` 详情页
- **后台布局菜单** (`src/layouts/admin-layout.vue`)
  - 侧边栏新增"新闻管理"菜单项，位于"内容管理"与"留言管理"之间
