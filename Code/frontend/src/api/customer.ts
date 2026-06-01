import request from '@/utils/request'
import type { Customer360VO } from '@/types/customer'

export const customerApi = {
  get360(phone: string): Promise<Customer360VO> {
    return request.get('/admin/customers/360', { params: { phone } })
  }
}
