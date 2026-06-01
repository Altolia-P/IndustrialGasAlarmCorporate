import request from '@/utils/request'
import type {
  ProductVO,
  ProductDetailVO,
  ProductStatus
} from '@/types/product'
import type { Page } from '@/types/common'

export const productApi = {
  getPublicList(params: {
    categoryUuid?: string
    name?: string
    page?: number
    size?: number
  }): Promise<Page<ProductVO>> {
    return request.get('/public/products', { params })
  },
  getPublicDetail(uuid: string): Promise<ProductDetailVO> {
    return request.get(`/public/products/${uuid}`)
  },
  getAdminDetail(uuid: string): Promise<ProductDetailVO> {
    return request.get(`/admin/products/${uuid}`)
  },
  getAdminList(params: {
    name?: string
    categoryUuid?: string
    status?: ProductStatus
    page?: number
    size?: number
  }): Promise<Page<ProductVO>> {
    return request.get('/admin/products', { params })
  },
  create(formData: FormData): Promise<ProductVO> {
    return request.post('/admin/products', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  update(uuid: string, formData: FormData): Promise<ProductVO> {
    return request.put(`/admin/products/${uuid}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  remove(uuid: string): Promise<null> {
    return request.delete(`/admin/products/${uuid}`)
  },
  publish(uuid: string): Promise<null> {
    return request.post(`/admin/products/${uuid}/publish`)
  },
  unpublish(uuid: string): Promise<null> {
    return request.post(`/admin/products/${uuid}/unpublish`)
  }
}
