import request from '@/utils/request'
import type { CommentVO } from '@/types/comment'

function getComments(prefix: string, resource: string, uuid: string): Promise<CommentVO[]> {
  return request.get(`/${prefix}/${resource}/${uuid}/comments`)
}

function addComment(prefix: string, resource: string, uuid: string, content: string): Promise<CommentVO> {
  return request.post(`/${prefix}/${resource}/${uuid}/comments`, { content })
}

export const commentApi = {
  // Admin
  getAdminWorkOrderComments: (uuid: string) => getComments('admin', 'workorders', uuid),
  addAdminWorkOrderComment: (uuid: string, content: string) => addComment('admin', 'workorders', uuid, content),
  getAdminMessageComments: (uuid: string) => getComments('admin', 'messages', uuid),
  addAdminMessageComment: (uuid: string, content: string) => addComment('admin', 'messages', uuid, content),

  // Staff
  getStaffWorkOrderComments: (uuid: string) => getComments('staff', 'workorders', uuid),
  addStaffWorkOrderComment: (uuid: string, content: string) => addComment('staff', 'workorders', uuid, content),
  getStaffInquiryComments: (uuid: string) => getComments('staff', 'inquiries', uuid),
  addStaffInquiryComment: (uuid: string, content: string) => addComment('staff', 'inquiries', uuid, content),

  // User
  getUserWorkOrderComments: (uuid: string) => getComments('user', 'workorders', uuid),
  addUserWorkOrderComment: (uuid: string, content: string) => addComment('user', 'workorders', uuid, content),
  getUserMessageComments: (uuid: string) => getComments('user', 'messages', uuid),
  addUserMessageComment: (uuid: string, content: string) => addComment('user', 'messages', uuid, content)
}
