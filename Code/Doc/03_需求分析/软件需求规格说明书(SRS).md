# 工业气体报警企业官网系统 — 软件需求规格说明书 (SRS)

> 文档版本：V2.0 | 日期：2026-06-01 | 作者：岳阳
> 来源：PRD V1.6（终版）+ 实际代码实现

---

## 一、引言

### 1.1 编写目的

本文档基于 PRD V1.3 终版和实际代码实现，从开发视角精确描述系统功能需求、业务规则、数据约束和接口契约。开发人员可凭本文档直接编码，测试人员可凭本文档编写测试用例。

### 1.2 项目范围

本系统为 InterSense（英森思）品牌的企业官网，覆盖 **5 个前端入口 + 18 个业务模块**：

```
前台门户 (/)          客户中心 (/user)      后台管理 (/admin)       员工后台 (/staff)      数据大屏 (/dashboard)
─────────────────    ─────────────────    ──────────────────    ─────────────────    ──────────────────
首页                  个人中心              仪表盘                  工作台                 实时设备状态
产品中心              我的设备              产品管理                待办工单               告警统计
解决方案              设备数据              内容管理                留言处理                趋势图
新闻动态              告警记录              分类管理                告警确认               WebSocket 推送
资料下载              我的工单              留言管理                通知查看
在线留言              我的留言              工单管理                个人信息
AI 助手                                     用户管理
关于我们                                    员工管理
联系我们                                    设备管理
                                           告警规则
                                           告警记录
                                           通知管理
                                           客户360
                                           评论管理
                                           文件下载
                                           操作日志
                                           系统配置
                                           AI 客服
```

### 1.3 用户角色

| 角色 | 英文标识 | 权限范围 | 前端入口 |
|------|---------|---------|---------|
| 游客 | GUEST | 浏览前台公开内容（产品/方案/新闻）、提交留言、下载资料 | `/` |
| 客户 | USER | 游客权限 + 查看自有设备、提交工单、查看告警 | `/user` |
| 员工 | STAFF | 处理指派工单、回复留言、确认告警、设备巡检 | `/staff` |
| 管理员 | ADMIN | 全部权限：内容管理、用户管理、员工管理、系统配置 | `/admin` |

### 1.4 名词定义

| 术语 | 定义 |
|------|------|
| **内容 (Content)** | 解决方案文章或新闻动态，type 字段区分 |
| **留言 (Message)** | 客户通过前台联系页提交的需求表单 |
| **工单 (WorkOrder)** | 管理员/系统创建的运维任务，指派员工处理 |
| **设备 (Device)** | 客户部署的物理气体检测仪，通过 API Token 上报数据 |
| **数据点 (DataPoint)** | 设备上报的单次监测数据（浓度/温度/湿度/电量/信号） |
| **告警规则 (AlertRule)** | 触发告警的条件定义（阈值/离线/低电量） |
| **告警 (Alert)** | 规则触发后生成的告警记录，有生命周期（PENDING→CONFIRMED→RESOLVED→CLOSED） |
| **通知 (Notification)** | 通过短信/邮件发送的告警通知 |
| **三命脉** | 设备入口 → 告警联动 → 通知触达，系统的核心业务闭环 |

---

## 二、功能需求

### 2.1 用户认证模块

#### 2.1.1 登录

| 项目 | 说明 |
|------|------|
| 触发 | POST `/api/auth/login` |
| 入参 | `username` + `password` |
| 正常流程 | 验证凭证 → 生成 access_token(30min) + refresh_token(7d) → 返回 token 对 |
| 异常流程 | 用户不存在 → "用户名或密码错误"；连续失败 5 次 → 锁定 30 分钟（Redis）；账号已锁定 → "账号已锁定，请30分钟后重试" |
| 安全机制 | BCrypt 密码哈希；JWT 双 Token；登录失败计数存 Redis；Token 黑名单（Redis Set） |

#### 2.1.2 注册

| 项目 | 说明 |
|------|------|
| 触发 | POST `/api/auth/register` |
| 入参 | `username`(4-20字符) + `password`(6-20字符) + `phone` + `company` |
| 业务规则 | 用户名唯一；密码 BCrypt 加密后存储；角色默认 USER |
| 异常 | 用户名已存在 → "用户名已被注册" |

#### 2.1.3 登出

| 项目 | 说明 |
|------|------|
| 触发 | POST `/api/auth/logout` |
| 流程 | 当前 access_token 加入 Redis 黑名单（TTL = 剩余有效期） |

### 2.2 产品模块

#### 2.2.1 产品列表（前台）

| 项目 | 说明 |
|------|------|
| 触发 | GET `/api/products?categoryUuid=xxx&status=PUBLISHED&page=1&size=12` |
| 排序 | 按 created_at 降序 |
| 筛选 | 支持按分类 UUID 筛选；仅返回 status=PUBLISHED |

#### 2.2.2 产品详情

| 项目 | 说明 |
|------|------|
| 触发 | GET `/api/products/{productUuid}` |
| 返回 | 产品基本信息 + 图片列表（sort_order 排序）+ 属性列表（key-value） |

#### 2.2.3 产品管理（后台）

| 操作 | 说明 | 权限 |
|------|------|------|
| 列表 | 分页 + 按分类/状态筛选 + 模糊搜索名称 | ADMIN |
| 新增 | 名称(必填/200字) + 描述(富文本) + 分类(下拉选择) + 封面图 + 多图 + 属性键值对 | ADMIN |
| 编辑 | 同新增，乐观锁 version 防并发 | ADMIN |
| 发布/下架 | status 切换：DRAFT → PUBLISHED / PUBLISHED → UNPUBLISHED | ADMIN |
| 删除 | 逻辑删除（deleted=1），关联图片+属性级联删除 | ADMIN |

### 2.3 内容模块（解决方案 + 新闻）

| 操作 | 说明 | 权限 |
|------|------|------|
| 前台列表 | GET `/api/contents?type=SOLUTION&status=PUBLISHED` 分页+分类筛选 | GUEST |
| 前台详情 | GET `/api/contents/{contentUuid}` 返回富文本 body | GUEST |
| 后台列表 | 分页 + type(SOLUTION/NEWS) + status + 分类筛选 | ADMIN |
| 后台新增 | title(必填/200) + summary(500) + body(MEDIUMTEXT) + coverImage + type + categoryUuid | ADMIN |
| 后台编辑 | 同新增，乐观锁 | ADMIN |
| 发布/下架/删除 | 同产品模块 | ADMIN |

### 2.4 分类模块

| 操作 | 说明 |
|------|------|
| 列表 | GET `/api/categories?type=PRODUCT_CATEGORY` |
| 新增 | name + type(PRODUCT_CATEGORY/CONTENT_CATEGORY) + parentUuid(可选) + sortOrder |
| 编辑/删除 | 乐观锁 + 逻辑删除 |

### 2.5 客户留言模块

| 操作 | 说明 | 权限 |
|------|------|------|
| 前台提交 | POST `/api/messages` name + phone + content + ip（自动获取） | GUEST |
| 后台列表 | 分页 + 按状态筛选（PENDING/IN_PROGRESS/PROCESSED） | ADMIN/STAFF |
| 处理 | 指派员工 → 状态变更为 IN_PROGRESS；处理完成 → PROCESSED + processedAt | ADMIN/STAFF |
| 防重复提交 | 前端按钮加载态禁用；后端无强校验（同一 IP 可能多次提交不同需求） | — |

### 2.6 工单模块

| 操作 | 说明 | 权限 |
|------|------|------|
| 列表 | 分页 + 按 type/status/priority 筛选 | ADMIN/STAFF/USER |
| 创建 | title + type(TECH_SUPPORT/AFTER_SALES/ALERT) + description + priority + customerName + customerPhone | ADMIN/USER |
| 指派 | 指派员工 UUID → assignedStaffUuid + assignedStaffName | ADMIN |
| 处理 | 员工处理后填入 resolution + 状态变更 → COMPLETED + completedAt | STAFF |
| 自动创建 | 告警规则 auto_create_work_order=1 时，CRITICAL 告警触发自动生成 ALERT 类型工单 | 系统 |

### 2.7 设备模块

#### 2.7.1 设备注册

| 项目 | 说明 |
|------|------|
| 触发 | POST `/api/devices` (ADMIN) |
| 字段 | serialNumber(唯一) + name + model + customerUuid + installLocation + gasType(CH4/H2S/CO/NH3/O2/OTHER) + rangeMin/Max + alertThreshold |
| 自动生成 | apiToken = SHA256(serialNumber + secret)，用于设备端 API 认证 |

#### 2.7.2 设备状态

| 状态 | 含义 | 触发条件 |
|------|------|---------|
| NORMAL | 正常 | 有数据上报 + 浓度正常 |
| ABNORMAL | 异常 | 浓度超过阈值 |
| OFFLINE | 离线 | 超过 5 分钟无数据上报 |
| MAINTENANCE | 维护中 | 管理员手动标记 |

#### 2.7.3 数据点

| 项目 | 说明 |
|------|------|
| 上报 | POST `/api/devices/{deviceUuid}/data-points`（设备端，apiToken 认证） |
| 字段 | concentration + battery + temperature + humidity + signalStrength + recordedAt |
| 存储 | 追加写入，无更新无删除，按 device_uuid + recorded_at 索引 |

### 2.8 告警模块

#### 2.8.1 告警规则

| 规则类型 | 说明 | 参数 |
|---------|------|------|
| THRESHOLD | 浓度阈值超限 | gasType + threshold + durationSeconds |
| OFFLINE | 设备离线 | durationSeconds（默认 300） |
| LOW_BATTERY | 低电量 | durationSeconds（默认 60） |

规则 device_uuid=NULL 表示全局规则，对所有设备生效。

#### 2.8.2 告警生命周期

```
数据点入库 → 告警引擎扫描规则
                    ↓ 命中
              创建 Alert (PENDING)
                    ↓
          ┌── 通知发送（SMS/EMAIL）
          ↓
     管理员/员工 CONFIRMED
          ↓
     员工处理 → RESOLVED（填入 resolvedBy + resolvedAt）
          ↓
     管理员 CLOSED（归档）
```

### 2.9 通知模块

| 渠道 | 说明 | 状态流转 |
|------|------|---------|
| SMS | 短信通知（阿里云/腾讯云接口预留） | PENDING → SENT → DELIVERED / FAILED |
| EMAIL | 邮件通知（SMTP 接口预留） | 同上 |

通知内容模板：`【InterSense报警】{设备名}：{告警消息}，请立即处理！`

### 2.10 评论模块

| 项目 | 说明 |
|------|------|
| 目标类型 | PRODUCT / CONTENT / WORK_ORDER |
| 作者类型 | ADMIN / STAFF / CUSTOMER |
| 功能 | 增删查（按 targetType+targetUuid），不支持编辑 |

### 2.11 下载文件模块

| 项目 | 说明 |
|------|------|
| 列表 | GET `/api/downloads` — 公开 |
| 管理 | 后台 CRUD — ADMIN |
| 字段 | displayName + originalName + fileSize + contentType + storedPath |

---

## 三、非功能需求

### 3.1 性能

| 指标 | 要求 | 实现方式 |
|------|------|---------|
| 首页加载 | < 2s | 前端懒加载 + 后端分页 |
| 产品列表 | 分页每页 12 条 | MyBatis-Plus Page |
| 设备数据点查询 | device_uuid + 时间范围索引 | 联合索引 |
| API 响应 | < 500ms (P95) | Redis 缓存热点数据 |

### 3.2 安全

| 要求 | 实现 |
|------|------|
| 认证 | JWT 双 Token（access 30min + refresh 7d） |
| 密码 | BCrypt 哈希 |
| 登录保护 | Redis 计数：5 次失败锁定 30 分钟 |
| Token 吊销 | 退出登录 → Redis 黑名单 |
| SQL 注入 | MyBatis-Plus 参数化查询，全项目零拼接 SQL |
| XSS | 前端输出转义 + 后端参数校验 |
| CSRF | SPA + JWT Bearer 天然免疫 |
| 接口鉴权 | Spring Security + @PreAuthorize 注解 |

### 3.3 兼容性

- 浏览器：Chrome 最新 2 版、Edge 最新版、Safari 最新 2 版
- 响应式：375px（手机）– 1920px（桌面）
- 数据库：MySQL 8.0+
- JDK：Java 17 LTS

### 3.4 可维护性

- 代码结构：DDD 四层，模块化分包
- 日志：SLF4J + Logback，按级别输出
- 操作审计：OperationLog 记录所有管理操作
- 事件追溯：EventOutbox 确保告警事件不丢失

---

## 四、数据约束

### 4.1 唯一性约束

| 表 | 约束 |
|---|------|
| t_admin_user | username UNIQUE |
| t_device | serial_number UNIQUE |
| t_product | (name, category_uuid, deleted) UNIQUE |

### 4.2 枚举约束

| 表.字段 | 可选值 |
|--------|--------|
| t_admin_user.role | ADMIN / STAFF / USER |
| t_staff.role | FIELD_TECH / CUSTOMER_SERVICE / TECH_SUPPORT / AFTER_SALES |
| t_staff.status | VACATION / STANDBY / WORKING / BUSINESS_TRIP |
| t_device.status | NORMAL / ABNORMAL / OFFLINE / MAINTENANCE |
| t_device.gas_type | CH4 / H2S / CO / NH3 / O2 / OTHER |
| t_alert_rule.rule_type | THRESHOLD / OFFLINE / LOW_BATTERY |
| t_alert.status | PENDING / CONFIRMED / RESOLVED / CLOSED |
| t_alert.severity | CRITICAL / WARNING / INFO |
| t_work_order.type | TECH_SUPPORT / AFTER_SALES / ALERT |
| t_work_order.priority | HIGH / MEDIUM / LOW |
| t_contact_message.status | PENDING / IN_PROGRESS / PROCESSED |
| t_notification.channel | SMS / EMAIL / IN_APP |
| t_category.type | PRODUCT_CATEGORY / CONTENT_CATEGORY |
| t_content.type | SOLUTION / NEWS |

---

## 五、原型图（页面清单）

基于 PRD 设计，实际实现共 **22 个页面**（扩展自原始 16 页）：

### 5.1 前台门户（7 页）

| 页面 | 路由 | 说明 |
|------|------|------|
| 首页 | `/` | Banner + 公司简介 + 核心优势 + 产品/方案入口 + 联系入口 |
| 产品列表 | `/products` | 分类筛选 + 卡片列表 + 分页 |
| 产品详情 | `/products/:id` | 封面图 + 多图轮播 + 属性表 + 咨询按钮 |
| 解决方案列表 | `/solutions` | 分类筛选 + 卡片列表 |
| 解决方案详情 | `/solutions/:id` | 封面图 + 富文本正文 |
| 新闻列表 | `/news` | 分类筛选 + 列表 |
| 新闻详情 | `/news/:id` | 封面图 + 富文本正文 |
| 联系页 | `/contact` | 表单（姓名+电话+需求描述）+ 公司联系方式 |
| 资料下载 | `/downloads` | 文件列表 + 点击下载 |

### 5.2 客户中心（4 页）

| 页面 | 路由 | 说明 |
|------|------|------|
| 我的设备 | `/user/devices` | 设备列表 + 状态标签 |
| 设备详情 | `/user/devices/:id` | 设备信息 + 实时数据 + 趋势图(ECharts) |
| 告警记录 | `/user/alerts` | 按状态/时间筛选 |
| 工单 | `/user/work-orders` | 提交工单 + 历史列表 |

### 5.3 后台管理（11 页）

| 页面 | 路由 | 说明 |
|------|------|------|
| 登录 | `/admin/login` | 用户名 + 密码 |
| 仪表盘 | `/admin/dashboard` | 统计卡片 + 图表 |
| 产品管理 | `/admin/products` | 列表 + 新增/编辑/发布/删除 |
| 内容管理 | `/admin/contents` | 列表 + 新增/编辑（富文本编辑器） |
| 分类管理 | `/admin/categories` | 树形列表 + 增删改 |
| 留言管理 | `/admin/messages` | 列表 + 详情 + 指派员工 |
| 工单管理 | `/admin/work-orders` | 列表 + 创建 + 指派 |
| 用户管理 | `/admin/users` | 列表 + 角色管理 |
| 员工管理 | `/admin/staffs` | 列表 + 增删改 + 状态管理 |
| 设备管理 | `/admin/devices` | 设备注册 + 列表 + 状态监控 |
| 告警规则 | `/admin/alert-rules` | 规则列表 + 增删改 + 启用/禁用 |
| 系统配置 | `/admin/config` | Key-Value 编辑 |

### 5.4 员工后台（3 页）

| 页面 | 路由 | 说明 |
|------|------|------|
| 待办工单 | `/staff/work-orders` | 指派给我的工单列表 |
| 留言处理 | `/staff/messages` | 指派给我的留言 |
| 告警确认 | `/staff/alerts` | PENDING 告警确认 |

---

## 六、外部接口

### 6.1 设备端 API

| 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|
| `/api/devices/{uuid}/data-points` | POST | apiToken (Header) | 设备上报数据点 |
| `/api/devices/{uuid}/heartbeat` | POST | apiToken | 设备心跳（续期在线状态） |

### 6.2 通知服务

| 服务 | 接口 | 说明 |
|------|------|------|
| 短信 | AliyunSender / TxySender | 预留接口，演示用 Mock |
| 邮件 | MailSender (SMTP) | 预留接口，演示用 Mock |

---

## 七、附录：与原始 PRD 的差异

本 SRS 文档与原始 PRD V1.3 的差异（反映实际开发中的演进）：

| 变更 | 原始 PRD | 实际实现 | 原因 |
|------|---------|---------|------|
| 设备管理 | MVP 不含 | ✅ 已实现 | 简历竞争力 + 三命脉闭环 |
| 告警系统 | MVP 不含 | ✅ 已实现 | 同上 |
| 工单 + 员工 | — | ✅ 已实现 | 客户服务闭环 |
| 评论 | — | ✅ 已实现 | 产品/内容互动 |
| AI 集成 | 不含 | LangChain4j 预留 | 技术栈展示 |
| 微服务 | 含 | 预留（Nacos + Spring Cloud） | 毕设加分项 |
| 响应式 | 要求 | Element Plus 响应式 | 符合要求 |
