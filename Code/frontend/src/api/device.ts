import request from '@/utils/request'
import type { Page } from '@/types/common'
import type {
  DeviceVO,
  DeviceDataPointVO,
  AlertRuleVO,
  AlertVO,
  NotificationVO,
  DashboardStatsVO
} from '@/types/device'

export const deviceApi = {
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

  getByUuid(uuid: string): Promise<DeviceVO> {
    return request.get(`/admin/devices/${uuid}`)
  },

  create(data: {
    serialNumber: string
    name: string
    model: string
    customerUuid: string
    gasType: string
    installLocation?: string
    rangeMin?: string
    rangeMax?: string
    alertThreshold?: string
  }): Promise<DeviceVO> {
    return request.post('/admin/devices', data)
  },

  update(uuid: string, data: {
    name?: string
    model?: string
    customerUuid?: string
    gasType?: string
    installLocation?: string
    rangeMin?: string
    rangeMax?: string
    alertThreshold?: string
  }): Promise<DeviceVO> {
    return request.put(`/admin/devices/${uuid}`, data)
  },

  remove(uuid: string): Promise<null> {
    return request.delete(`/admin/devices/${uuid}`)
  },

  markAbnormal(uuid: string): Promise<null> {
    return request.post(`/admin/devices/${uuid}/mark-abnormal`)
  },

  markNormal(uuid: string): Promise<null> {
    return request.post(`/admin/devices/${uuid}/mark-normal`)
  },

  markOffline(uuid: string): Promise<null> {
    return request.post(`/admin/devices/${uuid}/mark-offline`)
  },

  startMaintenance(uuid: string): Promise<null> {
    return request.post(`/admin/devices/${uuid}/start-maintenance`)
  },

  endMaintenance(uuid: string): Promise<null> {
    return request.post(`/admin/devices/${uuid}/end-maintenance`)
  },

  getDataPoints(uuid: string, from?: string, to?: string): Promise<DeviceDataPointVO[]> {
    return request.get(`/admin/devices/${uuid}/data`, { params: { from, to } })
  },

  getLatest(uuid: string): Promise<DeviceDataPointVO> {
    return request.get(`/admin/devices/${uuid}/latest`)
  }
}

export const alertRuleApi = {
  getAdminList(): Promise<AlertRuleVO[]> {
    return request.get('/admin/alert-rules')
  },

  getByUuid(uuid: string): Promise<AlertRuleVO> {
    return request.get(`/admin/alert-rules/${uuid}`)
  },

  create(data: {
    name: string
    deviceUuid?: string
    ruleType: string
    gasType?: string
    threshold?: string
    durationSeconds?: number
    severity?: string
    autoCreateWorkOrder?: boolean
  }): Promise<AlertRuleVO> {
    return request.post('/admin/alert-rules', data)
  },

  update(uuid: string, data: {
    name?: string
    ruleType?: string
    gasType?: string
    threshold?: string
    durationSeconds?: number
    severity?: string
    autoCreateWorkOrder?: boolean
  }): Promise<AlertRuleVO> {
    return request.put(`/admin/alert-rules/${uuid}`, data)
  },

  remove(uuid: string): Promise<null> {
    return request.delete(`/admin/alert-rules/${uuid}`)
  },

  enable(uuid: string): Promise<null> {
    return request.post(`/admin/alert-rules/${uuid}/enable`)
  },

  disable(uuid: string): Promise<null> {
    return request.post(`/admin/alert-rules/${uuid}/disable`)
  }
}

export const alertApi = {
  getAdminList(params: {
    deviceUuid?: string
    severity?: string
    status?: string
    page?: number
    size?: number
  }): Promise<Page<AlertVO>> {
    return request.get('/admin/alerts', { params })
  },

  getByUuid(uuid: string): Promise<AlertVO> {
    return request.get(`/admin/alerts/${uuid}`)
  },

  confirm(uuid: string): Promise<null> {
    return request.post(`/admin/alerts/${uuid}/confirm`)
  },

  resolve(uuid: string): Promise<null> {
    return request.post(`/admin/alerts/${uuid}/resolve`)
  },

  close(uuid: string): Promise<null> {
    return request.post(`/admin/alerts/${uuid}/close`)
  },

  getNotifications(alertUuid: string): Promise<NotificationVO[]> {
    return request.get(`/admin/alerts/${alertUuid}/notifications`)
  },
  listAllNotifications(params: { page?: number; size?: number }): Promise<Page<NotificationVO>> {
    return request.get('/admin/notifications', { params })
  },

  getUnreadCount(since?: string): Promise<number> {
    return request.get('/admin/notifications/unread-count', { params: since ? { since } : {} })
  },

  getRecentNotifications(): Promise<NotificationVO[]> {
    return request.get('/admin/notifications/recent')
  }
}

export const dashboardApi = {
  getStats(): Promise<DashboardStatsVO> {
    return request.get('/admin/dashboard/stats')
  }
}
