import request from '@/utils/request'
import type { CategoryVO, CategoryType } from '@/types/category'

export const categoryApi = {
  getCategories(type: CategoryType): Promise<CategoryVO[]> {
    return request.get('/public/categories', { params: { type } })
  },
  getAdminCategories(type: CategoryType): Promise<CategoryVO[]> {
    return request.get('/admin/categories', { params: { type } })
  },
  create(data: { name: string; type: CategoryType; parentUuid?: string; sortOrder?: number }): Promise<CategoryVO> {
    return request.post('/admin/categories', data)
  },
  update(uuid: string, data: { name?: string; parentUuid?: string; sortOrder?: number }): Promise<CategoryVO> {
    return request.put(`/admin/categories/${uuid}`, data)
  },
  remove(uuid: string): Promise<null> {
    return request.delete(`/admin/categories/${uuid}`)
  }
}
