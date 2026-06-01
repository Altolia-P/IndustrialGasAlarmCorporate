# 设备监控平台 — 前端模块设计

> 基于 FUNCTIONAL_SPEC_DEVICE_PLATFORM.md | 2026-05-27

---

## 1. 路由设计

### 1.1 新增路由

在现有 4 个布局下增加设备相关路由：

```
客户后台 (/user, user-layout, requiresAuth)
├── devices                    → DeviceList       我的设备列表
└── devices/:uuid              → DeviceDetail     设备详情 + 浓度曲线

管理后台 (/admin, admin-layout, requiresAuth + requiresAdmin)
├── devices                    → AdminDeviceList     设备管理列表
├── devices/create             → AdminDeviceCreate   录入设备
├── devices/:uuid/edit         → AdminDeviceEdit     编辑设备 + 告警规则配置
├── alerts                     → AdminAlertList      告警记录列表
└── alerts/:uuid               → AdminAlertDetail    告警详情 + 关联工单
```

### 1.2 现有路由扩展

| 现有路由 | 扩展内容 |
|---------|---------|
| `/admin/dashboard` | 看板顶部新增 4 个设备统计卡片（总数/在线/离线/异常告警） |
| `/staff/tasks/:uuid` | 工单详情里如果关联了告警，展示跳转链接和告警摘要 |
| `/user` | 概览页新增设备状态卡片，有异常时红色高亮 |

---

## 2. 类型定义

### 2.1 `types/device.ts`

```typescript
// ── 枚举 ──
export enum DeviceStatus {
  NORMAL = 'NORMAL',
  ABNORMAL = 'ABNORMAL',
  OFFLINE = 'OFFLINE',
  MAINTENANCE = 'MAINTENANCE'
}

export const DeviceStatusMap: Record<DeviceStatus, string> = {
  [DeviceStatus.NORMAL]: '正常',
  [DeviceStatus.ABNORMAL]: '异常',
  [DeviceStatus.OFFLINE]: '离线',
  [DeviceStatus.MAINTENANCE]: '维护中'
}

export enum GasType {
  CH4 = 'CH4',
  H2S = 'H2S',
  CO = 'CO',
  NH3 = 'NH3',
  O2 = 'O2',
  OTHER = 'OTHER'
}

export const GasTypeMap: Record<GasType, string> = {
  [GasType.CH4]: '甲烷 CH₄',
  [GasType.H2S]: '硫化氢 H₂S',
  [GasType.CO]: '一氧化碳 CO',
  [GasType.NH3]: '氨气 NH₃',
  [GasType.O2]: '氧气 O₂',
  [GasType.OTHER]: '其他'
}

// ── VO ──
export interface DeviceVO {
  deviceUuid: string
  serialNumber: string
  model: string
  name: string
  customerName: string
  gasType: GasType
  installLocation: string
  status: DeviceStatus
  lastConcentration: number | null
  lastReportTime: string | null
}

export interface DeviceDetailVO extends DeviceVO {
  customerUuid: string
  rangeMin: number
  rangeMax: number
  alertThreshold: number
  installDate: string
  todayAlertCount: number
}

export interface DeviceDataPoint {
  deviceUuid: string
  timestamp: string
  concentration: number
  battery: number
  temperature: number
  humidity: number
  signalStrength: number
}

// ── DTO ──
export interface CreateDeviceDTO {
  serialNumber: string
  model: string
  name: string
  customerUuid: string
  gasType: GasType
  installLocation: string
  rangeMin: number
  rangeMax: number
  alertThreshold: number
}

export interface UpdateDeviceDTO extends Partial<CreateDeviceDTO> {
  deviceUuid: string
}
```

### 2.2 `types/alert.ts`

```typescript
export enum AlertSeverity {
  INFO = 'INFO',
  WARN = 'WARN',
  CRITICAL = 'CRITICAL'
}

export const AlertSeverityMap: Record<AlertSeverity, string> = {
  [AlertSeverity.INFO]: '提示',
  [AlertSeverity.WARN]: '警告',
  [AlertSeverity.CRITICAL]: '严重'
}

export enum AlertStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  RESOLVED = 'RESOLVED',
  CLOSED = 'CLOSED'
}

export const AlertStatusMap: Record<AlertStatus, string> = {
  [AlertStatus.PENDING]: '待处理',
  [AlertStatus.CONFIRMED]: '已确认',
  [AlertStatus.RESOLVED]: '已解决',
  [AlertStatus.CLOSED]: '已关闭'
}

export enum AlertRuleType {
  THRESHOLD = 'THRESHOLD',
  RAPID_RISE = 'RAPID_RISE',
  OFFLINE = 'OFFLINE',
  LOW_BATTERY = 'LOW_BATTERY'
}

export const AlertRuleTypeMap: Record<AlertRuleType, string> = {
  [AlertRuleType.THRESHOLD]: '阈值超限',
  [AlertRuleType.RAPID_RISE]: '快速上升',
  [AlertRuleType.OFFLINE]: '设备离线',
  [AlertRuleType.LOW_BATTERY]: '低电量'
}

// ── VO ──
export interface AlertVO {
  alertUuid: string
  deviceUuid: string
  deviceName: string
  ruleType: AlertRuleType
  severity: AlertSeverity
  status: AlertStatus
  concentration: number
  threshold: number
  message: string
  workOrderUuid: string | null
  triggeredAt: string
}

export interface AlertDetailVO extends AlertVO {
  deviceModel: string
  customerName: string
  recentDataPoints: DeviceDataPoint[]
  confirmedBy: string | null
  confirmedAt: string | null
  closedBy: string | null
  closedAt: string | null
}

export interface AlertRuleVO {
  ruleUuid: string
  deviceUuid: string | null       // null 表示全局规则
  ruleType: AlertRuleType
  threshold: number
  duration: number                // 持续多少秒后触发
  severity: AlertSeverity
  autoCreateWorkOrder: boolean
  enabled: boolean
}

// ── DTO ──
export interface CreateAlertRuleDTO {
  deviceUuid: string | null
  ruleType: AlertRuleType
  threshold: number
  duration: number
  severity: AlertSeverity
  autoCreateWorkOrder: boolean
}
```

### 2.3 `types/dashboard.ts`

```typescript
export interface DeviceOverviewStats {
  total: number          // 设备总数
  online: number         // 在线
  offline: number        // 离线
  abnormal: number       // 正在告警
}

export interface AlertTrendItem {
  date: string           // 日期
  infoCount: number
  warnCount: number
  criticalCount: number
}

export interface TodayStats {
  dataPointCount: number
  alertCount: number
  resolvedCount: number
  pendingCount: number
}
```

---

## 3. API 层

### 3.1 `api/device.ts`

```typescript
import request from '@/utils/request'
import type { DeviceVO, DeviceDetailVO, CreateDeviceDTO, UpdateDeviceDTO, DeviceDataPoint } from '@/types/device'
import type { Page } from '@/types/common'

export const deviceApi = {
  // ── 客户 ──
  getMyDevices(): Promise<DeviceVO[]> {
    return request.get('/user/devices')
  },
  getMyDeviceDetail(uuid: string): Promise<DeviceDetailVO> {
    return request.get(`/user/devices/${uuid}`)
  },
  getDeviceDataPoints(uuid: string, range: string): Promise<DeviceDataPoint[]> {
    return request.get(`/user/devices/${uuid}/data`, { params: { range } })
  },

  // ── 管理员 ──
  getAdminList(params: {
    customerUuid?: string
    model?: string
    gasType?: string
    status?: string
    page?: number
    size?: number
  }): Promise<Page<DeviceVO>> {
    return request.get('/admin/devices', { params })
  },
  getAdminDetail(uuid: string): Promise<DeviceDetailVO> {
    return request.get(`/admin/devices/${uuid}`)
  },
  create(dto: CreateDeviceDTO): Promise<DeviceVO> {
    return request.post('/admin/devices', dto)
  },
  update(uuid: string, dto: UpdateDeviceDTO): Promise<DeviceVO> {
    return request.put(`/admin/devices/${uuid}`, dto)
  },
  remove(uuid: string): Promise<null> {
    return request.delete(`/admin/devices/${uuid}`)
  }
}
```

### 3.2 `api/alert.ts`

```typescript
import request from '@/utils/request'
import type { AlertVO, AlertDetailVO, AlertRuleVO, CreateAlertRuleDTO } from '@/types/alert'
import type { Page } from '@/types/common'

export const alertApi = {
  // ── 告警记录 ──
  getAdminList(params: {
    deviceUuid?: string
    severity?: string
    status?: string
    page?: number
    size?: number
  }): Promise<Page<AlertVO>> {
    return request.get('/admin/alerts', { params })
  },
  getAdminDetail(uuid: string): Promise<AlertDetailVO> {
    return request.get(`/admin/alerts/${uuid}`)
  },
  confirm(uuid: string): Promise<null> {
    return request.put(`/admin/alerts/${uuid}/confirm`)
  },
  close(uuid: string, reason?: string): Promise<null> {
    return request.put(`/admin/alerts/${uuid}/close`, { reason })
  },

  // ── 告警规则 ──
  getRules(deviceUuid: string): Promise<AlertRuleVO[]> {
    return request.get(`/admin/devices/${deviceUuid}/rules`)
  },
  createRule(deviceUuid: string, dto: CreateAlertRuleDTO): Promise<AlertRuleVO> {
    return request.post(`/admin/devices/${deviceUuid}/rules`, dto)
  },
  updateRule(ruleUuid: string, dto: Partial<CreateAlertRuleDTO>): Promise<AlertRuleVO> {
    return request.put(`/admin/alerts/rules/${ruleUuid}`, dto)
  },
  deleteRule(ruleUuid: string): Promise<null> {
    return request.delete(`/admin/alerts/rules/${ruleUuid}`)
  },

  // ── 客户 ──
  getMyAlerts(deviceUuid: string, params: { page?: number; size?: number }): Promise<Page<AlertVO>> {
    return request.get(`/user/devices/${deviceUuid}/alerts`, { params })
  }
}
```

### 3.3 `api/dashboard.ts`

```typescript
import request from '@/utils/request'
import type { DeviceOverviewStats, AlertTrendItem, TodayStats } from '@/types/dashboard'
import type { AlertVO } from '@/types/alert'

export const dashboardApi = {
  getDeviceOverview(): Promise<DeviceOverviewStats> {
    return request.get('/admin/dashboard/device-overview')
  },
  getTodayStats(): Promise<TodayStats> {
    return request.get('/admin/dashboard/today-stats')
  },
  getAlertTrend(days: number): Promise<AlertTrendItem[]> {
    return request.get('/admin/dashboard/alert-trend', { params: { days } })
  },
  getRealtimeAlerts(): Promise<AlertVO[]> {
    return request.get('/admin/dashboard/realtime-alerts')
  }
}
```

---

## 4. Composables

### 4.1 `composables/use-device-data.ts`

```typescript
// 设备详情页用：轮询获取最新数据点，自动刷新图表
// 核心逻辑：
//   - 进入详情页 → 立即加载最近 1h 数据点
//   - 启动 30s 间隔轮询 → 追加新数据点
//   - 切换时间范围 (1h/6h/24h/7d) → 重新请求
//   - 离开页面 → 清除定时器
// 导出：dataPoints, selectedRange, setRange, loading, error
```

### 4.2 `composables/use-websocket.ts`

```typescript
// 管理后台全局：建立 WebSocket 接收实时告警推送
// 核心逻辑：
//   - 管理员登录后自动连接
//   - 收到新告警 → 更新全局未读数 + 顶部闪烁提示
//   - 断线自动重连（指数退避 1s→2s→4s→max 30s）
//   - 登出断开连接
// 导出：connected, reconnectCount, lastAlert
```

### 4.3 `composables/use-concentration-chart.ts`

```typescript
// 将 DeviceDataPoint[] 转换为 ECharts 配置
// 输入：dataPoints[], 时间范围, 告警阈值线
// 输出：ECharts option 对象（含阈值标记线 + 异常区间红色高亮）
// 导出：chartOption (computed)
```

---

## 5. 页面设计

### 5.1 客户设备列表 — `views/user/device-list.vue`

```
┌──────────────────────────────────────────────┐
│  我的设备                        [共 5 台]    │
├──────────────────────────────────────────────┤
│ ┌─────────────────────────────┐              │
│ │ ● DEV-001                   │              │
│ │   3号车间东墙  IGA-200Pro   │              │
│ │   最新读数: 12 ppm          │  → 详情      │
│ │   10秒前 · 正常             │              │
│ ├─────────────────────────────┤              │
│ │ ● DEV-002                   │              │
│ │   3号车间西墙  IGA-200Pro   │              │
│ │   最新读数: 78 ppm ⚠       │              │
│ │   5秒前 · 异常 · 2条告警    │  → 详情      │
│ ├─────────────────────────────┤              │
│ │ ◎ DEV-003                   │              │
│ │   4号车间  IGA-100          │              │
│ │   最后读数: 2分钟前         │              │
│ │   离线                     │  → 详情      │
│ └─────────────────────────────┘              │
└──────────────────────────────────────────────┘
```

设备卡片：
- 左侧状态圆点：绿色（正常） / 红色闪烁（异常） / 灰色（离线）
- 红点设备卡片边框变红，有告警时显示告警数角标
- 点击整个卡片进入设备详情，不设独立按钮

### 5.2 客户设备详情 — `views/user/device-detail.vue`

```
┌─────────────────────────────────────────────────────┐
│  ← 返回     DEV-001 · 3号车间东墙                     │
├─────────────────────────────────────────────────────┤
│  ┌─────────┬─────────┬─────────┬─────────┐          │
│  │ 浓度     │ 电池    │ 温度    │ 信号    │          │
│  │ 12 ppm  │ 4.12V  │ 25.3°C │ 92%    │          │
│  │ 正常     │         │         │         │          │
│  └─────────┴─────────┴─────────┴─────────┘          │
│                                                      │
│  浓度趋势  [1h] [6h] [24h] [7d]                      │
│  ┌──────────────────────────────────────────┐       │
│  │            ── 告警阈值 25ppm              │       │
│  │    ╱╲                                    │       │
│  │   ╱  ╲     ╱╲                           │       │
│  │  ╱    ╲   ╱  ╲___╱╲___                  │       │
│  │ ╱      ╲_╱                             │       │
│  └──────────────────────────────────────────┘       │
│                                                      │
│  告警记录                              [查看全部 →]  │
│  ┌──────────────────────────────────────────┐       │
│  │ ⚠ 10:30  浓度超标  85ppm > 25ppm  已解决 │       │
│  │ ⚠ 09:15  浓度超标  62ppm > 25ppm  已解决 │       │
│  └──────────────────────────────────────────┘       │
│                                                      │
│  关联工单                                            │
│  ┌──────────────────────────────────────────┐       │
│  │ WO-001  10:30 告警排查  处理中  →      │       │
│  └──────────────────────────────────────────┘       │
│                                                      │
│  设备信息                                            │
│  型号: IGA-200Pro | 安装日期: 2026-03-15            │
│  气体类型: 硫化氢 H₂S | 量程: 0-100 ppm             │
└─────────────────────────────────────────────────────┘
```

### 5.3 管理后台设备列表 — `views/admin/device/index.vue`

```
┌──────────────────────────────────────────────────────┐
│  设备管理               [+ 录入设备]  [搜索...]       │
├──────────────────────────────────────────────────────┤
│  筛选: [全部客户 ▾] [全部型号 ▾] [全部状态 ▾]        │
│                                                      │
│  ┌─────────────────────────────────────────────┐     │
│  │ #  序列号   名称     客户   浓度  状态  操作 │     │
│  │ 1  SN-001 3号车间东  张三  12    ● 正常  → │     │
│  │ 2  SN-002 3号车间西  张三  85    ● 异常  → │     │
│  │ 3  SN-003 4号车间   李四  —     ◎ 离线  → │     │
│  └─────────────────────────────────────────────┘     │
│                                                      │
│  < 1  2  3  4  5  >                           共 42 条│
└──────────────────────────────────────────────────────┘
```

### 5.4 管理后台设备编辑 — `views/admin/device/edit.vue`

```
┌──────────────────────────────────────────────────────┐
│  编辑设备 / 录入设备                                  │
├──────────────────────────────────────────────────────┤
│                                                      │
│  序列号:  [________________]                         │
│  型号:    [________________]                         │
│  名称:    [________________]                         │
│  所属客户: [选择用户 ▾]                              │
│  气体类型: [CH₄ ▾]                                   │
│  安装位置: [________________]                         │
│  量程下限: [0    ] ppm   量程上限: [100  ] ppm       │
│  告警阈值: [25   ] ppm                               │
│                                                      │
│  ── 告警规则配置 ─────────────────────────────────── │
│  ☑ 阈值超限  阈值[25]ppm  持续[30]s  级别[严重 ▾] ☑自动建工单 │
│  ☑ 快速上升  增幅[200]%   窗口[30]s  级别[警告 ▾] ☐自动建工单 │
│  ☐ 设备离线  持续[120]s              级别[提示 ▾] ☐自动建工单 │
│  ☐ 低电量    电压<[3.6]V             级别[提示 ▾] ☐自动建工单 │
│  [+ 添加规则]                                       │
│                                                      │
│  [保存]  [取消]                                      │
└──────────────────────────────────────────────────────┘
```

### 5.5 告警记录列表 — `views/admin/alert/index.vue`

```
┌──────────────────────────────────────────────────────┐
│  告警记录                                            │
├──────────────────────────────────────────────────────┤
│  筛选: [全部级别 ▾] [全部状态 ▾] [全部设备 ▾]        │
│                                                      │
│  🔴 CRITICAL  DEV-002  H₂S 超标 85ppm  →  10:30    │
│  🟡 WARN      DEV-005  电池 3.5V       →  10:25    │
│  🔴 CRITICAL  DEV-002  H₂S 超标 62ppm  ✓  09:15    │
│  🔵 INFO      DEV-008  离线 2分钟      ✓  08:00    │
│                                                      │
│  < 1  2  3  >                                  共 23 条│
└──────────────────────────────────────────────────────┘
```

每行告警显示：严重级别图标 + 设备名 + 简短描述 + 状态（→ 待处理 / ✓ 已解决）+ 时间。点击进入详情。

### 5.6 告警详情 — `views/admin/alert/detail.vue`

```
┌──────────────────────────────────────────────────────┐
│  ← 返回    告警详情                                   │
├──────────────────────────────────────────────────────┤
│  🔴 严重告警                                         │
│  设备: DEV-002 (3号车间西墙)  型号: IGA-200Pro      │
│  客户: 张三                                         │
│  规则: 阈值超限 — H₂S 浓度 > 25ppm                   │
│  触发值: 85 ppm         触发时间: 10:30              │
│                                                      │
│  触发前 1 分钟数据:                                  │
│  ┌──────────────────────────────────────────┐       │
│  │ 时间    浓度  │                            │       │
│  │ 10:29  22    │           ╱                 │       │
│  │ 10:29  28  ⚠ │          ╱                  │       │
│  │ 10:30  85  🔴│         ╱                   │       │
│  │ ............ │    ────╱ 阈值线              │       │
│  └──────────────────────────────────────────┘       │
│                                                      │
│  关联工单: WO-001  [查看工单 →]                      │
│  处理人: 王工                         状态: 待处理    │
│                                                      │
│  [确认告警]  [关闭告警]                              │
└──────────────────────────────────────────────────────┘
```

### 5.7 看板扩展 — 补充到现有 `admin-dashboard.vue`

```
现有看板顶部新增一行设备统计卡片：
┌──────────┬──────────┬──────────┬──────────┐
│  设备总数 │  在线    │  离线    │  告警中  │
│    42    │   38    │    2    │    3 🔴 │
│  全部设备 │  绿色圆点 │  灰色圆点 │  红色闪烁 │
└──────────┴──────────┴──────────┴──────────┘

下方新增实时告警列表（WebSocket 推送，新告警自动插入顶部）：
⚠ 10:30  DEV-002  3号车间西墙  H₂S超标 85ppm  → 查看
```

---

## 6. 组件设计

### 6.1 组件树

```
src/components/
├── device/
│   ├── DeviceStatusDot.vue        # 设备状态圆点（绿/灰/红闪烁）
│   ├── DeviceCard.vue             # 设备卡片（客户设备列表用）
│   ├── DeviceStatsCards.vue       # 统计卡片行（总数/在线/离线/告警）
│   ├── DeviceDataTable.vue        # 最近数据点表格
│   ├── ConcentrationChart.vue     # ECharts 浓度趋势折线图
│   ├── AlertRuleForm.vue          # 告警规则配置表单
│   └── AlertBadge.vue             # 告警数角标
├── alert/
│   ├── AlertSeverityTag.vue       # 严重级别标签（红/黄/蓝色）
│   ├── AlertStatusTag.vue         # 告警状态标签
│   └── RealtimeAlertPopup.vue     # 实时告警弹窗（右上角弹出）
└── dashboard/
    └── AlertTrendChart.vue        # ECharts 告警趋势柱状图
```

### 6.2 关键组件规格

#### DeviceStatusDot.vue

```typescript
// Props: status: DeviceStatus, size?: number (默认 12px)
// 渲染：
//   NORMAL     → 绿色圆点，脉搏动画（呼吸灯）
//   ABNORMAL   → 红色圆点，闪烁动画（flash）
//   OFFLINE    → 灰色圆点，无动画
//   MAINTENANCE → 黄色圆点，无动画
```

#### ConcentrationChart.vue

```typescript
// Props: dataPoints: DeviceDataPoint[], threshold: number, height?: string
// 功能：
//   - ECharts 折线图，x轴=时间，y轴=浓度
//   - 告警阈值标记线（红色虚线）
//   - 超过阈值的区间红色半透明填充
//   - 响应式 resize
//   - 支持 tooltip 显示详细值
//   - 空数据时显示占位提示
```

#### RealtimeAlertPopup.vue

```typescript
// 全局挂载在 admin-layout 中
// 监听 WebSocket 消息 → 新告警到达时
//   从右上角滑入红色弹窗卡片
//   显示：设备名 + 严重级别 + 简短描述
//   3 秒后自动消失，或点击 × 关闭
//   同时顶部导航栏告警图标数字 +1
```

---

## 7. Store

### `stores/device-alert.ts`

```typescript
// 仅管理后台使用，管理 WebSocket 连接和实时告警
// State:
//   unreadAlertCount: number          # 未读告警数（顶部角标）
//   realtimeAlerts: AlertVO[]         # 最近 20 条实时告警
//   wsConnected: boolean              # WebSocket 连接状态
// Actions:
//   connect()                         # 建立 WebSocket
//   disconnect()                      # 断开
//   addAlert(alert)                   # 新告警到达
//   clearUnread()                     # 已读
//   acknowledgeAlert(uuid)            # 确认告警
```

---

## 8. 交互细节

### 8.1 设备状态刷新

| 页面 | 刷新方式 | 说明 |
|------|---------|------|
| 客户设备列表 | 30s 自动轮询 | 只刷新状态圆点和最新读数，不整页刷新 |
| 客户设备详情 | 30s 自动轮询 | 刷新顶部实时读数卡片 + 追加浓度曲线新数据点 |
| 管理后台设备列表 | 手动刷新按钮 | 管理员通常不会长时间盯列表 |
| 管理告警记录 | WebSocket 推送 | 新告警自动弹出 + 列表顶部插入 |
| 看板统计卡片 | 5min 自动轮询 | 统计数字不需要秒级刷新 |

### 8.2 异常视觉提示

| 状态 | 视觉 |
|------|------|
| 设备异常告警中 | 卡片红色边框 + 浓度数字红色 + 右侧告警角标 |
| 设备离线 > 10 分钟 | 卡片灰色调 + 浓度显示 "--" |
| 新告警到达 | 右上角弹出红色卡片（3s 自消）+ 顶部铃铛数字 +1 |
| 电池 < 20% | 电池图标黄色 + 数值黄色 |

### 8.3 跳转关系

```
客户设备详情 ──→ 点击告警记录 ──→ 告警详情（同页展开或弹窗）
客户设备详情 ──→ 点击关联工单 ──→ 工单详情（新页）
管理告警详情 ──→ 点击设备名 ──→ 设备详情（新页）
管理告警详情 ──→ 点击关联工单 ──→ 工单详情（新页）
管理设备列表 ──→ 点击行 ──→ 设备编辑页
管理看板   ──→ 点击告警中数字 ──→ 告警列表（筛选未处理）
```

---

## 9. 依赖新增

| 包 | 用途 |
|------|------|
| `echarts` + `vue-echarts` | 浓度趋势图、告警趋势柱状图 |
| 无其他新增 | Pinia/ElementPlus/Axios/VueRouter 已有 |

---

## 10. 文件清单

```
新建文件（约 25 个）：

src/types/
├── device.ts
├── alert.ts
└── dashboard.ts

src/api/
├── device.ts
├── alert.ts
└── dashboard.ts

src/views/user/
├── device-list.vue              # 客户设备列表
└── device-detail.vue            # 客户设备详情

src/views/admin/
├── device/
│   ├── index.vue                # 设备列表
│   └── edit.vue                 # 设备编辑/创建
├── alert/
│   ├── index.vue                # 告警列表
│   └── detail.vue               # 告警详情

src/components/device/
├── DeviceStatusDot.vue
├── DeviceCard.vue
├── DeviceStatsCards.vue
├── DeviceDataTable.vue
├── ConcentrationChart.vue
├── AlertRuleForm.vue
└── AlertBadge.vue

src/components/alert/
├── AlertSeverityTag.vue
├── AlertStatusTag.vue
└── RealtimeAlertPopup.vue

src/components/dashboard/
└── AlertTrendChart.vue

src/composables/
├── use-device-data.ts
├── use-websocket.ts
└── use-concentration-chart.ts

src/stores/
└── device-alert.ts

修改文件（约 6 个）：
src/router/index.ts              # 新增路由
src/views/admin/dashboard.vue    # 顶部统计卡片 + 告警区
src/views/staff/tasks/detail.vue # 告警关联展示
src/views/user/user-center.vue   # 设备概览卡片
src/layouts/admin-layout.vue     # 挂载 RealtimeAlertPopup + WebSocket 连接
src/api/staff.ts                 # 如需要
```
