import request from '@/utils/request'
import type { SystemConfigVO } from '@/types/system-config'

export const systemConfigApi = {
  getAdminList(): Promise<SystemConfigVO[]> {
    return request.get('/admin/system-configs')
  },
  getByKey(configKey: string): Promise<SystemConfigVO> {
    return request.get(`/admin/system-configs/${configKey}`)
  },
  update(configKey: string, data: { configValue: string; description?: string }): Promise<SystemConfigVO> {
    return request.put(`/admin/system-configs/${configKey}`, data)
  }
}
