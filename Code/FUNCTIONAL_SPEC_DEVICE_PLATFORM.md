# 设备监控平台 — 功能规格文档

> 版本：V1.0-draft | 2026-05-27 | 状态：设计中

---

## 1. 整体概述

在现有企业官网系统基础上，新增**设备监控平台**，将业务从「企业展示」扩展为「企业展示 + 设备运行监控」。客户购买报警器后，可通过平台实时查看设备状态、接收告警通知。

### 1.1 系统边界

```
                    ┌─ 企业官网（已有） ─────────────────────┐
                    │  产品展示 / 解决方案 / 留言 / 内容管理      │
                    │                                          │
用户 ──────────────┤  工单系统（已有）                         │
                    │  └─ 告警自动创建工单（新增联动）           │
                    │                                          │
                    ├─ 设备监控平台（新增） ────────────────────┤
                    │  设备管理 / 实时数据 / 告警引擎 / 通知 / 看板 │
                    └──────────────────────────────────────────┘
```

### 1.2 用户角色与权限

| 角色 | 可见范围 | 典型操作 |
|------|---------|---------|
| **客户** (USER) | 自己购买的设备 | 查看设备列表、实时读数、历史告警、浓度趋势图 |
| **员工** (STAFF) | 被指派的客户的设备 | 处理告警工单、查看设备详情辅助排查 |
| **管理员** (ADMIN) | 全部设备 | 设备录入/绑定客户、设置告警阈值、全局看板 |

### 1.3 新增术语

| 术语 | 说明 |
|------|------|
| **设备 (Device)** | 一台物理报警器，有唯一 deviceId，绑定到一个客户 |
| **数据点 (DataPoint)** | 设备一次上报的数据：浓度、电池、温度、时间戳 |
| **告警规则 (AlertRule)** | 一条告警触发条件，例如「浓度 > 50ppm 持续 30秒」 |
| **告警事件 (Alert)** | 一条规则被触发后生成的告警记录 |
| **设备模拟器 (Simulator)** | 开发/测试用，模拟 N 台设备定时上报数据 |

---

## 2. 模块详述

### 2.1 设备管理 (Device Management)

**入口**：管理后台 → 设备管理

#### 2.1.1 设备录入

管理员在后台录入设备信息：

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| deviceId | String(36) | ✓ | UUID v4，录入时自动生成 |
| serialNumber | String | ✓ | 硬件序列号，唯一，由工厂提供 |
| model | String | ✓ | 设备型号，如 "IGA-200Pro" |
| name | String | | 别名，如 "3号车间东墙" |
| customerUuid | String | ✓ | 所属客户（关联到注册用户） |
| installLocation | String | | 安装位置描述 |
| installDate | Date | | 安装日期 |
| gasType | Enum | ✓ | 检测气体类型：CH₄ / H₂S / CO / NH₃ / O₂ / 其他 |
| rangeMax | Decimal | ✓ | 量程上限（ppm），如 100 |
| rangeMin | Decimal | ✓ | 量程下限，通常 0 |
| alertThreshold | Decimal | ✓ | 告警阈值（ppm），如 25 |
| status | Enum | | NORMAL / ABNORMAL / OFFLINE / MAINTENANCE |

#### 2.1.2 设备列表

| 功能 | 描述 |
|------|------|
| 筛选 | 按客户、型号、气体类型、状态筛选 |
| 排序 | 按安装日期、告警次数排序 |
| 在线状态 | 实时显示（绿色/灰色/红色圆点） |
| 最新读数 | 列表每行显示最近一次上报的浓度值和时间 |

#### 2.1.3 设备详情

| 功能 | 描述 |
|------|------|
| 基本信息 | 显示设备全部字段，管理员可编辑 |
| 实时读数 | 最近 10 条数据点，自动刷新 |
| 浓度趋势 | 折线图：最近 1h / 6h / 24h / 7d |
| 告警历史 | 该设备的所有告警记录，含处理状态 |
| 关联工单 | 由告警自动生成的工单列表 |

#### 2.1.4 客户视角

客户登录后只看到自己的设备，功能同详情页，不可编辑设备信息（只读）。

---

### 2.2 设备模拟器 (Device Simulator)

**用途**：开发/测试阶段模拟硬件上报，生产环境不部署。

#### 2.2.1 模拟能力

| 参数 | 说明 | 默认值 |
|------|------|--------|
| 设备数量 | 模拟 N 台设备 | 50 |
| 上报间隔 | 每隔 N 秒上报一次 | 30s |
| 浓度正常范围 | 随机生成范围 | 0-15 ppm |
| 异常概率 | 每台设备每次上报产生异常读数的概率 | 5% |
| 异常浓度范围 | 超标时的随机范围 | 阈值×1.5 ~ 阈值×3 |
| 断线概率 | 设备上报失败模拟 | 2% |

#### 2.2.2 手动操控

提供 REST 接口供测试时手动触发：

| 接口 | 功能 |
|------|------|
| `POST /simulator/device/{deviceId}/anomaly` | 让指定设备下一次上报产生异常读数 |
| `POST /simulator/device/{deviceId}/offline` | 让指定设备模拟断线 |
| `POST /simulator/device/{deviceId}/online` | 恢复在线 |
| `POST /simulator/batch` | 调整模拟参数（数量、间隔、概率） |
| `GET /simulator/status` | 查看当前模拟器运行状态 |

#### 2.2.3 数据格式

模拟器生成的每条上报数据：

```json
{
  "deviceId": "UUID-xxxx",
  "timestamp": "2026-05-27T10:30:00",
  "concentration": 12.5,
  "battery": 4.12,
  "temperature": 25.3,
  "humidity": 58.0,
  "signalStrength": 92,
  "online": true
}
```

---

### 2.3 数据采集服务 (Device Ingestion)

**职责**：接收设备上报数据，校验、写入存储。

#### 2.3.1 数据接收

| 方式 | 说明 |
|------|------|
| HTTP POST | 设备/模拟器通过 HTTP 上报，路径 `POST /api/v1/ingestion/device-data` |
| MQTT | 后期扩展：设备 → MQTT Broker → Ingestion Service 订阅 |

#### 2.3.2 处理流程

```
设备上报
  → 校验（deviceId 是否存在、数据格式是否正确）
  → 更新设备在线状态（Redis）
  → 写入 Redis Stream（原始数据流）
  → 更新设备最新读数缓存（Redis Hash）
  → 检查告警规则（调用告警引擎）
  → 持久化到时序库 / MySQL
```

#### 2.3.3 校验规则

| 规则 | 失败处理 |
|------|---------|
| deviceId 不存在于设备表 | 丢弃，记录 WARN 日志 |
| 浓度值超出量程 2 倍以上 | 标记为疑似传感器故障 |
| 时间戳与服务器时间差 > 5 分钟 | 记录 WARN，仍接受 |
| 同设备 5 秒内重复上报 | 去重，只保留最新一条 |

---

### 2.4 告警引擎 (Alert Engine)

**职责**：实时检测数据流，触发告警，联动工单系统。

#### 2.4.1 告警规则

| 规则类型 | 条件 | 示例 |
|---------|------|------|
| 阈值超限 | 浓度 > 阈值 持续 N 秒 | 浓度 > 25ppm 持续 30s |
| 快速上升 | 30s 内浓度增幅 > X% | 30s 内上升超过 200% |
| 设备离线 | 超过 N 秒无数据上报 | 120s 无数据 |
| 低电量 | 电池电压 < 阈值 | 电压 < 3.6V |

每条规则可配置：规则类型、阈值参数、严重级别（INFO / WARN / CRITICAL）、是否自动创建工单。

#### 2.4.2 滑动窗口判断

告警引擎消费 Redis Stream 中的设备数据：

```
每来一条数据 → 查询该设备最近 30s 的读数（Redis Sorted Set）
  → 计算窗口内平均值 / 最小值是否全部超阈值
  → 是 → 创建告警 + 可选自动创建工单
  → 否 → 忽略
```

窗口判断逻辑避免单次尖峰误报。

#### 2.4.3 告警生命周期

```
触发 → PENDING
  管理员/员工确认 → CONFIRMED
  自动创建工单 → 关联 workOrderUuid
  工单完成 → RESOLVED
  管理员关闭 → CLOSED（可选填关闭原因）
```

#### 2.4.4 告警抑制

| 规则 | 说明 |
|------|------|
| 重复抑制 | 同一设备同一规则，5 分钟内不重复创建告警 |
| 维护抑制 | 设备状态为 MAINTENANCE 时不触发告警 |
| 时间窗口 | 可配置某些规则仅在指定时段生效（如夜间降低阈值） |

---

### 2.5 通知服务 (Notification Service)

**职责**：告警产生后，通过多种渠道通知相关人员。

#### 2.5.1 通知渠道

| 渠道 | 使用场景 | 优先级 |
|------|---------|:------:|
| 站内消息 | 客户登录后可见 | 低 |
| 短信 | CRITICAL 级别告警 | 高 |
| 邮件 | WARN 及以上，每日摘要 | 中 |
| WebSocket 推送 | 管理员/员工实时弹窗 | 低 |

#### 2.5.2 通知模板

```
站内消息：
  「您的设备 DEV-003（3号车间东墙）于 10:30 检测到 H₂S 浓度超标：
   当前浓度 85ppm，告警阈值 25ppm。请及时排查。」

短信：
  【工业气体报警】设备DEV-003 H₂S超标85ppm，已超阈值25ppm。
  请立即检查。回T退订。

邮件：
  同站内消息 + 最近 10 条浓度数据表格 + 设备管理页面链接
```

#### 2.5.3 通知策略

| 规则 | 说明 |
|------|------|
| 渠道优先级 | CRITICAL → 短信+站内；WARN → 邮件+站内；INFO → 仅站内 |
| 防骚扰 | 同一设备同规则 30 分钟内至多 1 条短信 |
| 重试 | 短信/邮件发送失败 → 1min 后重试 → 3 次失败入死信队列 |
| 确认回执 | 短信发送后记录发送状态（已发送/已送达/已失败） |

---

### 2.6 数据看板 (Analytics Dashboard)

**入口**：管理后台 → 数据看板 / 客户后台 → 我的设备

#### 2.6.1 管理员全局看板

| 面板 | 内容 |
|------|------|
| 设备概览 | 总数 / 在线 / 离线 / 异常 四色统计卡片 |
| 实时告警 | 正在告警的设备列表（红色闪烁），点击跳转详情 |
| 今日统计 | 今日上报数据量 / 告警次数 / 处理率 |
| 设备地图 | （后期）设备按地理位置标记在地图上 |
| 告警趋势 | 近 7 天各等级告警数量柱状图 |

#### 2.6.2 客户设备看板

| 面板 | 内容 |
|------|------|
| 设备状态 | 所有设备在线状态 + 最新浓度读数 |
| 浓度曲线 | 单设备浓度随时间变化折线图（1h/6h/24h） |
| 告警记录 | 该设备历史告警时间线 |
| 关联工单 | 由告警产生的工单及当前处理状态 |

#### 2.6.3 看板数据刷新

| 刷新模式 | 说明 | 技术 |
|---------|------|------|
| 统计卡片 | 页面打开时加载，手动刷新 | HTTP 请求 |
| 实时告警列表 | 新告警自动弹出 | Redis Pub/Sub → WebSocket |
| 浓度曲线 | 切换时间范围时重新请求 | HTTP 请求 |

---

## 3. 与现有系统联动

### 3.1 告警 → 自动创建工单

```
告警触发（Alert Engine）
  → 检查设备所属客户 + 规则配置的严重级别
  → CRITICAL 级别自动创建工单：
      title: "[告警] {设备名称} {气体类型}超标"
      type: TECH_SUPPORT
      priority: 对应告警级别
      customerName: 设备所属客户
      description: 展示浓度趋势 + 告警详情
      assignedStaffUuid: 自动指派（轮询/按区域/按负载）
  → 工单创建后关联到告警记录
```

### 3.2 工单处理 → 告警状态联动

```
员工完成工单 → 勾选"告警已处理" → 告警状态自动更新为 RESOLVED
管理员关闭告警 → 关联工单自动标记完成（如仍未处理）
```

### 3.3 设备录入 → 客户绑定

设备录入时选择客户，客户登录后自动看到自己的设备。与现有 User 体系关联（userUuid）。

---

## 4. 数据模型概览

### 4.1 新增数据库表

| 表名 | 说明 | 主要字段 |
|------|------|---------|
| `t_device` | 设备信息 | deviceId, serialNumber, model, customerUuid, gasType, alertThreshold, status |
| `t_device_data` | 设备上报数据点 | dataId, deviceId, concentration, battery, temperature, timestamp |
| `t_alert_rule` | 告警规则 | ruleId, deviceId(可空=全局), ruleType, threshold, duration, severity, autoCreateWorkOrder |
| `t_alert` | 告警记录 | alertId, deviceId, ruleId, concentration, severity, status, workOrderUuid, confirmedBy, closedAt |
| `t_notification` | 通知记录 | notificationId, alertId, channel, recipient, content, status, retryCount, sentAt |

### 4.2 Redis 数据结构

| Key 模式 | 类型 | 用途 | TTL |
|---------|------|------|-----|
| `device:online:{deviceId}` | String | 在线状态 + 最后上报时间 | — |
| `device:latest:{deviceId}` | Hash | 最新读数各字段 | — |
| `device:data:stream` | Stream | 原始数据流（告警引擎消费） | 1h 裁剪 |
| `device:window:{deviceId}` | Sorted Set | 滑动窗口（score=时间戳） | 120s |
| `alert:suppress:{deviceId}:{ruleId}` | String | 重复告警抑制标记 | 5min |
| `dashboard:today:alerts` | String | 今日告警计数 | 当天 |
| `dashboard:online:bitmap` | Bitmap | 设备在线状态位图（按天） | 7d |

---

## 5. 前后端新增总结

### 5.1 后端新增

| 层 | 新增内容 |
|------|---------|
| **Domain** | Device、DeviceData、AlertRule、Alert、Notification 5 个聚合根 + 对应 Repository 接口 |
| **Application** | DeviceService、IngestionService、AlertService、NotificationService、DashboardService 5 个 Service |
| **Assembler** | 对应 Assembler |
| **Infrastructure** | PO/Mapper/RepositoryImpl、告警引擎（Redis Stream 消费者）、通知渠道适配器（短信/邮件/WebSocket） |
| **Interface** | AdminDeviceController、IngestionController、CustomerDeviceController、SimulatorController |
| **Simulator** | 独立模块（多模块 POM），含模拟数据生成 + 手动操控接口 |

### 5.2 前端新增

| 层 | 新增内容 |
|------|---------|
| **Types** | `device.ts`、`alert.ts`、`dashboard.ts` |
| **API** | `device.ts`、`alert.ts`、`dashboard.ts` |
| **Views** | 客户：`views/device/`（设备列表 + 详情 + 浓度曲线）、管理后台：`views/admin/device/`（设备管理 + 告警规则配置）、`views/admin/dashboard/` 扩充告警看板 |
| **Components** | 实时告警弹窗、浓度趋势图（ECharts）、设备状态圆点 |

---

## 6. 开发顺序建议

| 阶段 | 内容 | 依赖 | 预计 |
|------|------|------|------|
| **P0** | 设备管理 CRUD（后端 + 管理后台） | MySQL 新表 | 先做 |
| **P1** | 设备模拟器 + 数据采集服务 | P0 | 先做 |
| **P2** | 告警引擎（阈值超限 + 滑动窗口） | P1 | 核心 |
| **P3** | 告警 → 工单联动 | P2 + 现有机工单模块 | 关键 |
| **P4** | 客户设备看板（只读） | P1 | 见效果 |
| **P5** | 通知服务（站内消息优先） | P2 | 后补 |
| **P6** | 数据看板（管理员全局） | P2 | 后补 |
| **P7** | 短信/邮件通知 | P5 | 可选 |
