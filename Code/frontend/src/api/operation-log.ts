import request from '@/utils/request'
import type { Page } from '@/types/common'
import type { OperationLogVO } from '@/types/operation-log'

export const operationLogApi = {
  getList(params: {
    operatorName?: string
    operation?: string
    targetType?: string
    page?: number
    size?: number
  }): Promise<Page<OperationLogVO>> {
    return request.get('/admin/operation-logs', { params })
  },
  getById(logId: string): Promise<OperationLogVO> {
    return request.get(`/admin/operation-logs/${logId}`)
  }
}
