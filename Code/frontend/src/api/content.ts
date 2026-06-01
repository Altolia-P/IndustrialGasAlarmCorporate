import request from '@/utils/request'
import type {
  ContentVO,
  ContentDetailVO,
  ContentType,
  ContentStatus
} from '@/types/content'
import type { Page } from '@/types/common'

export const contentApi = {
  getPublicList(params: {
    type: ContentType
    categoryUuid?: string
    page?: number
    size?: number
  }): Promise<Page<ContentVO>> {
    return request.get('/public/contents', { params })
  },
  getPublicDetail(uuid: string): Promise<ContentDetailVO> {
    return request.get(`/public/contents/${uuid}`)
  },
  getAdminDetail(uuid: string): Promise<ContentDetailVO> {
    return request.get(`/admin/contents/${uuid}`)
  },
  getAdminList(params: {
    title?: string
    type?: ContentType
    categoryUuid?: string
    status?: ContentStatus
    page?: number
    size?: number
  }): Promise<Page<ContentVO>> {
    return request.get('/admin/contents', { params })
  },
  create(formData: FormData): Promise<ContentVO> {
    return request.post('/admin/contents', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  update(uuid: string, formData: FormData): Promise<ContentVO> {
    return request.put(`/admin/contents/${uuid}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  remove(uuid: string): Promise<null> {
    return request.delete(`/admin/contents/${uuid}`)
  },
  publish(uuid: string): Promise<null> {
    return request.post(`/admin/contents/${uuid}/publish`)
  },
  unpublish(uuid: string): Promise<null> {
    return request.post(`/admin/contents/${uuid}/unpublish`)
  }
}
