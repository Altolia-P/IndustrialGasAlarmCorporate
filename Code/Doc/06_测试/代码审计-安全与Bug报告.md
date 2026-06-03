# 代码审计 — 安全与 Bug 报告

> 审计日期：2026-06-03  
> 范围：后端 200+ Java + 前端 68 TS/Vue + SQL + 配置  
> 来源：外部审计报告 + 补充深度扫描  
> 核实状态：已逐项验证，1 项误报已标记

---

## 修复状态说明

| 标记 | 含义 |
|:----:|------|
| ✅ | 已修复 |
| 🔲 | 待修复（见 `代码审计-待修复清单.md`） |
| ⚠️ | 有意设计 / 需确认 |
| 🔵 | 需人工操作 |
| ❌ | 误报 |

---

## 一、安全漏洞

### 1.1 密钥/凭证泄露

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 1 | CRITICAL | 数据库密码硬编码 `password: yueyang20041202` | `application-local.yml:3` | ✅ |
| 2 | CRITICAL | DeepSeek API Key 暴露 `sk-809...` | `application-local.yml:19-20` | ✅ / 🔵 需轮换 Key |
| 3 | HIGH | Nacos 默认凭据 `nacos/nacos` | `bootstrap.yml:12-13,17-18` | ✅ |
| 4 | HIGH | JWT 密钥弱默认值 `local-dev-only-change-me` | `application-local.yml:17` | ✅ |
| 5 | LOW | JWT 生产默认为空 — 空值时 ERROR 日志 + 随机密钥兜底 | `application.yml:62`, `JwtUtil.java:25-29` | ✅ |

### 1.2 SQL 注入

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 6 | MEDIUM | `.last("LIMIT " + limit)` 字符串拼接 (x4) | `AlertRepositoryImpl.java:144`, `ContentRepositoryImpl.java:72`, `ProductRepositoryImpl.java:95`, `DeviceDataPointRepositoryImpl.java:55` | 🔲 |
| 7 | MEDIUM | 搜索关键词无 `@Size` 限制 | `SearchController.java:28-30` | 🔲 |

### 1.3 认证/授权

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 8 | HIGH | CORS 双配置 | `WebConfig.java:35-41` | ✅ |
| 9 | HIGH | 路由守卫角色检查在服务器验证之前 | `router/index.ts:385-393` | ✅ |
| 10 | HIGH | Open Redirect | `use-login.ts:29`, `use-auth.ts:18` | ✅ |
| 11 | MEDIUM | 验证码可绕过 | `AuthServiceImpl.java:59-67` | 🔲 |
| 12 | MEDIUM | WebSocket 无认证 | `DashboardWebSocketHandler.java`, `useWebSocket.ts` | ✅ |
| 13 | MEDIUM | HealthController 公开暴露 DB/Redis 状态 | `HealthController.java:35-45` | 🔲 |
| 14 | MEDIUM | API Token Filter 未配置时静默放行 | `ApiTokenFilter.java:53-56` | ✅ |
| 15 | MEDIUM | 登出接口手动解析 Token 无签名校验 | `AuthController.java:37-45` | ✅ |
| 16 | MEDIUM | 公共消息详情端点暴露 PII | `MessageController.java:29-32` | 🔲 |
| 17 | MEDIUM | 公共系统配置端点公开 | `SystemConfigController.java:18-21` | 🔲 |

### 1.4 输入验证

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 18 | MEDIUM | `RegisterDTO.phone` 缺少格式正则 | `RegisterDTO.java:18` | 🔲 |
| 19 | MEDIUM | multipart 控制器手动校验 | `AdminContentController.java`, `AdminProductController.java` | 🔲 |
| 20 | MEDIUM | `BatchProcessDTO.uuids` 无 `@Size` | `BatchProcessDTO.java:12` | 🔲 |
| 21 | LOW | `CreateDeviceDTO` 缺少字段长度约束 | `CreateDeviceDTO.java` | ✅ |

### 1.5 XSS

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 22 | HIGH | `v-html` 未净化 (x2) | `news-detail.vue:86`, `content-detail.vue:90` | ✅ |

### 1.6 Token 与存储

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 23 | MEDIUM | JWT 明文存 localStorage — 加密存储方案已评估 | `auth.ts:5,9,13,17` | 🔲 |
| 24 | MEDIUM | 角色信息明文存 localStorage | `auth.ts:21,25,29` | 🔲 |
| 25 | MEDIUM | WebSocket token 经 URL 参数传递 | `useWebSocket.ts:58` | 🔲 |

### 1.7 日志/文件/密码

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 26 | LOW | 异常处理器泄露 `e.getMessage()` (x4) | `GlobalExceptionHandler.java:91-112` | ✅ |
| 27 | LOW | 登录日志记录用户名明文 | `AuthServiceImpl.java:73,91,95` | ✅ |
| 28 | MEDIUM | 文件扩展名校验无魔数检查 | `FileStorageService.java:78-84` | 🔲 |
| 29 | LOW | 公共下载无频率限制 | `PublicDownloadController.java:42-68` | 🔲 |
| 30 | LOW | 前端上传提示 "<=5MB" 但无客户端校验 | `admin-product-edit.vue`, `admin-content-edit.vue` | ✅ |
| 31 | MEDIUM | 默认管理员密码弱熵 | `DataInitializer.java:24` | 🔲 |
| 32 | LOW | 密码最小长度不一致 (6 vs 8) | `RegisterDTO.java:15`, `ResetPasswordDTO.java:14` | ✅ |
| 33 | LOW | 前端密码修改无复杂度要求 | `user-settings.vue:25`, `staff-settings.vue:25` | ✅ |

---

## 二、Bug

### 2.1 并发

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 34 | CRITICAL | `CaptchaGenerator.lastText` 共享可变状态 | `CaptchaGenerator.java:21-29` | ✅ |
| 35 | HIGH | BCrypt 空字符串崩溃 — `BCrypt.checkpw(raw, "")` 抛异常 | `AuthServiceImpl.java:75` | ✅ |
| 36 | LOW | `User.login()` failCount 读写非原子 | `User.java:93-100` | ✅ |
| 37 | MEDIUM | `LoginRateLimitRepository` increment/expire 非原子 | `LoginRateLimitRepository.java:21-26` | 🔲 |
| 38 | MEDIUM | `CaptchaRepository` get/delete 非原子 | `CaptchaRepository.java:22-28` | 🔲 |

### 2.2 数据完整性

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 39 | HIGH | 下载列表分页 1-indexed vs 0-indexed | `admin-download-list.vue:22-23` | ✅ |
| 40 | — | ~~乐观锁未生效~~ — `OptimisticLockerInnerInterceptor` 已注册 + 全部 PO 有 `@Version` | — | ❌ 误报 |
| 41 | MEDIUM | 删除设备不清理关联告警/数据点 | `DeviceServiceImpl.java:79-85` | 🔲 |
| 42 | MEDIUM | 删除员工不删关联 User | `StaffServiceImpl.java:77-83` | 🔲 |
| 43 | MEDIUM | `AlertRule.update()` boolean 原始类型无 null 检查 | `AlertRule.java:108` | 🔲 |
| 44 | MEDIUM | `REQUIRES_NEW` 导致外层回滚时工单成孤儿 | `WorkOrderServiceImpl.java:59` | 🔲 |
| 45 | MEDIUM | 删除分类不检查依赖 | `CategoryServiceImpl.java:84-90` | ✅ |
| 46 | LOW | 工单可直接 complete 无 assign | `WorkOrder.java:126-133` | ✅ |

### 2.3 资源泄漏

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 47 | MEDIUM | 上传成功但 JSON 解析失败，文件成孤儿 | `AdminProductController.java:55-81` | 🔲 |
| 48 | MEDIUM | 更新产品先删旧文件再写 DB | `ProductServiceImpl.java:46-61` | 🔲 |
| 49 | HIGH | AI 聊天 `setInterval` 不随组件卸载清理 | `use-chat.ts:77-91`, `ai-assistant.vue` | ✅ |

### 2.4 空指针/类型安全

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 50 | HIGH | `(String) auth.getPrincipal()` 强转 ClassCastException | `OperationLogAspect.java:29` | ✅ |
| 51 | MEDIUM | `claims.getSubject()` 可能 null | `JwtAuthFilter.java:44-45` | 🔲 |
| 52 | HIGH | `device.updatedAt` 不在 `DeviceVO` 类型 | `admin-device-detail.vue:143` | ✅ |
| 53 | HIGH | 导出 `publishedAt` 不在 `ContentVO` — 改用 `updatedAt` | `admin-content-list.vue:91`, `ContentVO.java` | ✅ |

### 2.5 功能缺口

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 54 | HIGH | 工单编辑 update 从未调用，表单全部 disabled | `admin-workorder-edit.vue:124-145` | ✅ |
| 55 | CRITICAL | `t_staff` 建表 SQL 缺 `user_uuid` 列 | `Gas-ToB-SQL.sql:52` | ✅ |
| 56 | HIGH | resetPassword API 路径不匹配 | `auth.ts:24` | ✅ |
| 57 | MEDIUM | 邮件通知桩实现 | `MailSender.java:26` | ⚠️ MVP 只需站内通知 |
| 58 | MEDIUM | 短信通知桩实现 | `AliyunSender.java:26` | ⚠️ MVP 只需站内通知 |
| 59 | HIGH | Staff 通知链接指向不存在的 `/staff/notifications` | `staff-layout.vue:86` | ✅ |

### 2.6 前端状态管理

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 60 | MEDIUM | Dashboard WS 和 REST 同时写状态无协调 | `DashboardView.vue:95-148` | 🔲 |
| 61 | MEDIUM | 系统配置页单个 `saving` ref 控制所有行 | `admin-system-config.vue:8` | 🔲 |
| 62 | LOW | `use-notification-bell.ts` 变量 `from` 未使用 | `use-notification-bell.ts:31` | ✅ |

### 2.7 代码规范

| # | 严重度 | 问题 | 位置 | 状态 |
|---|:------:|------|------|:----:|
| 63 | LOW | `@Autowired` 字段注入 | `AIChatServiceImpl.java:58` | ✅ |
| 64 | LOW | `closeAlert()` 缺 `@LogOperation` | `AdminAlertController.java:51` | ✅ |
| 65 | LOW | `CLAUDE.md` 前端状态描述过时 | `CLAUDE.md:48` | ✅ |

---

## 三、DDL 索引审计

| 表 | 缺失索引 |
|----|---------|
| `t_device` | `serial_number` UNIQUE, `customer_uuid` INDEX |
| `t_staff` | `phone` UNIQUE |
| `t_alert` | `device_uuid`, `rule_uuid`, `status` INDEX |
| `t_product_image` | `product_uuid` INDEX |
| `t_product_attribute` | `product_uuid` INDEX |
| `t_product` | `category_uuid` INDEX |
| `t_content` | `category_uuid` INDEX |
| `t_work_order` | `assigned_staff_uuid` INDEX |
| `t_notification` | `alert_uuid` INDEX |
| `t_device_data_point` | `(device_uuid, recorded_at)` 复合索引 |

> 14 张表缺索引，15 张表无外键约束。

---

## 四、依赖安全

| 依赖 | 版本 | 风险 |
|------|------|:--:|
| LangChain4j | 1.0.0-beta2 | 🟡 Beta 版 |
| xlsx | 0.18.5 | 🟡 CVE-2023-30533 |
| vite | ^6.2.0 | 🟡 CVE-2025-30208 (dev) |
| Spring Boot | 3.5.14 | 🟢 |
| MyBatis-Plus | 3.5.16 | 🟢 |
| jjwt | 0.12.5 | 🟢 |

---

## 五、汇总

| 来源 | CRITICAL | HIGH | MEDIUM | LOW |
|------|:--------:|:----:|:------:|:---:|
| 安全漏洞 | 2 | 5 | 15 | 8 |
| Bug | 1 | 8 | 13 | 5 |
| DDL 索引 | — | — | — | 14 表 |
| **合计** | **3** | **13** | **28** | **27** |

| 状态 | 数量 |
|:----:|:----:|
| ✅ 已修复 | 36 |
| 🔲 待修复 | 29 |
| ⚠️ 有意设计 | 2 |
| ❌ 误报 | 1 |
| 🔵 需人工操作 | 1 |

> 待修复项详见 `代码审计-待修复清单.md`
