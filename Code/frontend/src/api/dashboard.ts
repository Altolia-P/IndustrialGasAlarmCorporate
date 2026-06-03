import request from '@/utils/request'

export interface DashboardOverview {
  onlineCount: number
  totalCount: number
  alertCount: number
  todayDataPoints: number
  avgConcentration: string
  uptime: string
}

export interface DashboardAlert {
  alertUuid: string
  deviceUuid: string
  deviceName: string
  severity: string
  alertType: string
  concentration: string
  message: string
  triggeredAt: string
}

export interface DashboardDevice {
  deviceUuid: string
  name: string
  model: string
  gasType: string
  installLocation: string
  status: string
  latestConcentration: string
  customerUuid: string
  customerName: string
}

export interface DeviceDataPoint {
  deviceUuid: string
  timestamp: string
  concentration: string
  battery: string
  temperature: string
  humidity: string
  signalStrength: number
  createdAt: string
}

export const dashboardApi = {
  getOverview() {
    return request.get<DashboardOverview>('/dashboard/overview')
  },

  getAlerts(limit = 20) {
    return request.get<DashboardAlert[]>('/dashboard/alerts', {
      params: { limit }
    })
  },

  getDevices() {
    return request.get<DashboardDevice[]>('/dashboard/devices')
  },

  getDataPoints(deviceUuid: string, from?: string, to?: string) {
    return request.get<DeviceDataPoint[]>('/dashboard/device-data', {
      params: { deviceUuid, from, to }
    })
  },

  getLatestDataPoint(deviceUuid: string) {
    return request.get<DeviceDataPoint>('/dashboard/device-data/latest', {
      params: { deviceUuid }
    })
  }
}
