import request from '@/utils/request'
import type { StaffVO, UpdateStaffProfileDTO } from '@/types/staff'
import type { Page } from '@/types/common'

export const staffApi = {
  getAdminList(params: {
    name?: string
    role?: string
    status?: string
    page?: number
    size?: number
  }): Promise<Page<StaffVO>> {
    return request.get('/admin/staff', { params })
  },
  getByUuid(uuid: string): Promise<StaffVO> {
    return request.get(`/admin/staff/${uuid}`)
  },
  create(data: {
    name: string
    phone: string
    email?: string
    username: string
    password: string
    role: string
    status: string
  }): Promise<StaffVO> {
    return request.post('/admin/staff', data)
  },
  update(uuid: string, data: {
    name?: string
    phone?: string
    email?: string
    role?: string
    status?: string
  }): Promise<StaffVO> {
    return request.put(`/admin/staff/${uuid}`, data)
  },
  remove(uuid: string): Promise<null> {
    return request.delete(`/admin/staff/${uuid}`)
  },
  getMyProfile(): Promise<StaffVO> {
    return request.get('/staff/me')
  },
  updateMyProfile(dto: UpdateStaffProfileDTO): Promise<StaffVO> {
    return request.put('/staff/profile', dto)
  }
}

export const staffNotifyApi = {
  getUnreadCount(since?: string): Promise<number> {
    return request.get('/staff/notifications/unread-count', { params: since ? { since } : {} })
  },
  getRecentNotifications(): Promise<import('@/types/device').NotificationVO[]> {
    return request.get('/staff/notifications/recent')
  }
}
