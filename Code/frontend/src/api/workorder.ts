import request from '@/utils/request'
import type { WorkOrderVO } from '@/types/workorder'
import type { Page } from '@/types/common'

export const workOrderApi = {
  getAdminList(params: {
    title?: string
    type?: string
    status?: string
    page?: number
    size?: number
  }): Promise<Page<WorkOrderVO>> {
    return request.get('/admin/workorders', { params })
  },
  getByUuid(uuid: string): Promise<WorkOrderVO> {
    return request.get(`/admin/workorders/${uuid}`)
  },
  create(data: {
    title: string
    type: string
    description: string
    priority: string
    customerName: string
    customerPhone?: string
    assignedStaffUuid?: string
    assignedStaffName?: string
  }): Promise<WorkOrderVO> {
    return request.post('/admin/workorders', data)
  },
  update(uuid: string, data: {
    title?: string
    type?: string
    description?: string
    priority?: string
    customerName?: string
    customerPhone?: string
    assignedStaffUuid?: string
    assignedStaffName?: string
    resolution?: string
  }): Promise<WorkOrderVO> {
    return request.put(`/admin/workorders/${uuid}`, data)
  },
  assign(uuid: string, staffUuid: string, staffName: string): Promise<null> {
    return request.put(`/admin/workorders/${uuid}/assign`, { staffUuid, staffName })
  },
  complete(uuid: string, resolution: string): Promise<null> {
    return request.put(`/admin/workorders/${uuid}/complete`, { resolution })
  },
  remove(uuid: string): Promise<null> {
    return request.delete(`/admin/workorders/${uuid}`)
  },
  getMyTasks(params: {
    status?: string
    page?: number
    size?: number
  }): Promise<Page<WorkOrderVO>> {
    return request.get('/staff/workorders', { params })
  }
}
