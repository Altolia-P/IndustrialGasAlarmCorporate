import request from '@/utils/request'
import type { CategoryVO, CategoryType } from '@/types/category'

export const categoryApi = {
  getCategories(type: CategoryType): Promise<CategoryVO[]> {
    return request.get('/public/categories', { params: { type } })
  }
}
