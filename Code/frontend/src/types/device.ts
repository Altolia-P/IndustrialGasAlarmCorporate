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
  [GasType.CH4]: '甲烷 (CH4)',
  [GasType.H2S]: '硫化氢 (H2S)',
  [GasType.CO]: '一氧化碳 (CO)',
  [GasType.NH3]: '氨气 (NH3)',
  [GasType.O2]: '氧气 (O2)',
  [GasType.OTHER]: '其他'
}

export interface DeviceVO {
  deviceUuid: string
  serialNumber: string
  name: string
  model: string
  customerUuid: string
  customerName?: string
  customerPhone?: string
  installLocation: string | null
  installDate: string | null
  gasType: string
  rangeMin: string | null
  rangeMax: string | null
  alertThreshold: string | null
  status: string
  createdAt: string
  updatedAt?: string
}

export interface DeviceDataPointVO {
  deviceUuid: string
  timestamp: string
  concentration: string
  battery: string | null
  temperature: string | null
  humidity: string | null
  signalStrength: number | null
  createdAt: string
}

export enum AlertRuleType {
  THRESHOLD = 'THRESHOLD',
  OFFLINE = 'OFFLINE',
  LOW_BATTERY = 'LOW_BATTERY'
}

export const AlertRuleTypeMap: Record<AlertRuleType, string> = {
  [AlertRuleType.THRESHOLD]: '阈值超限',
  [AlertRuleType.OFFLINE]: '设备离线',
  [AlertRuleType.LOW_BATTERY]: '低电量'
}

export enum AlertSeverity {
  CRITICAL = 'CRITICAL',
  WARNING = 'WARNING',
  INFO = 'INFO'
}

export const AlertSeverityMap: Record<AlertSeverity, string> = {
  [AlertSeverity.CRITICAL]: '严重',
  [AlertSeverity.WARNING]: '警告',
  [AlertSeverity.INFO]: '提示'
}

export interface AlertRuleVO {
  ruleUuid: string
  name: string
  deviceUuid: string | null
  ruleType: string
  gasType: string | null
  threshold: string | null
  durationSeconds: number
  severity: string
  autoCreateWorkOrder: boolean
  enabled: boolean
  createdAt: string
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

export interface AlertVO {
  alertUuid: string
  deviceUuid: string
  deviceName?: string
  deviceSerialNumber?: string
  customerUuid?: string
  customerName?: string
  customerPhone?: string
  ruleUuid: string | null
  alertType: string
  severity: string
  concentration: string | null
  threshold: string | null
  message: string | null
  status: string
  triggeredAt: string
  confirmedAt: string | null
  confirmedBy: string | null
  resolvedAt: string | null
  resolvedBy: string | null
  workOrderUuid: string | null
  createdAt: string
}

export interface NotificationVO {
  notificationUuid: string
  alertUuid: string
  recipient: string
  channel: string
  content: string | null
  status: string
  retryCount: number
  errorMessage: string | null
  sentAt: string | null
  createdAt: string
}

export interface AlertTrendItem {
  date: string
  count: number
}

export interface DashboardStatsVO {
  totalDevices: number
  normalDevices: number
  abnormalDevices: number
  offlineDevices: number
  maintenanceDevices: number
  totalAlerts: number
  alertsToday: number
  pendingAlerts: number
  criticalAlerts: number
  warningAlerts: number
  pendingMessages: number
  pendingWorkOrders: number
  inProgressWorkOrders: number
  workOrderStatusDistribution: Record<string, number>
  alertTrend: AlertTrendItem[]
}
