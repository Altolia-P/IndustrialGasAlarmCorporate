import request from '@/utils/request'
import type { SubmitMessageDTO, MessageVO, MessageStatus } from '@/types/message'
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
  process(uuid: string, remark: string): Promise<null> {
    return request.put(`/admin/messages/${uuid}/process`, { remark })
  },
  processBatch(uuids: string[], remark: string): Promise<null> {
    return request.put('/admin/messages/process/batch', { uuids, remark })
  }
}
