import request from '@/utils/request'
import type { SubmitMessageDTO, AssignMessageDTO, CompleteMessageDTO, MessageVO, MessageStatus } from '@/types/message'
import type { Page } from '@/types/common'

export const messageApi = {
  submit(dto: SubmitMessageDTO): Promise<null> {
    return request.post('/public/messages', dto)
  },
  getAdminList(params: {
    name?: string
    phone?: string
    status?: MessageStatus
    page?: number
    size?: number
  }): Promise<Page<MessageVO>> {
    return request.get('/admin/messages', { params })
  },
  assign(uuid: string, dto: AssignMessageDTO): Promise<null> {
    return request.put(`/admin/messages/${uuid}/assign`, dto)
  },
  process(uuid: string, dto: CompleteMessageDTO): Promise<null> {
    return request.put(`/admin/messages/${uuid}/process`, dto)
  },
  processBatch(uuids: string[], remark: string): Promise<null> {
    return request.put('/admin/messages/process/batch', { uuids, remark })
  },
  getUserMessages(params: { page?: number; size?: number }): Promise<Page<MessageVO>> {
    return request.get('/user/messages', { params })
  },
  getStaffInquiries(params: { page?: number; size?: number }): Promise<Page<MessageVO>> {
    return request.get('/staff/inquiries', { params })
  }
}
