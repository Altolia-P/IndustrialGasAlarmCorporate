import request from '@/utils/request'
import type { DownloadFileVO } from '@/types/download-file'
import type { Page } from '@/types/common'

export const downloadFileApi = {
  getPublicList(params?: { page?: number; size?: number }): Promise<Page<DownloadFileVO>> {
    return request.get('/public/downloads', { params })
  },

  getAdminList(params?: { page?: number; size?: number }): Promise<Page<DownloadFileVO>> {
    return request.get('/admin/downloads', { params })
  },

  upload(formData: FormData): Promise<DownloadFileVO> {
    return request.post('/admin/downloads', formData)
  },

  remove(uuid: string): Promise<null> {
    return request.delete(`/admin/downloads/${uuid}`)
  },

  getDownloadUrl(uuid: string): string {
    return `${import.meta.env.VITE_API_BASE_URL}/public/downloads/${uuid}/file`
  }
}
