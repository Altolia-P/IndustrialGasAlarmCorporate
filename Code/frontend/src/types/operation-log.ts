export enum OperationType {
  CREATE = 'CREATE',
  UPDATE = 'UPDATE',
  DELETE = 'DELETE',
  PUBLISH = 'PUBLISH',
  UNPUBLISH = 'UNPUBLISH',
  PROCESS = 'PROCESS'
}

export const OperationTypeMap: Record<string, string> = {
  CREATE: '新增',
  UPDATE: '更新',
  DELETE: '删除',
  PUBLISH: '发布',
  UNPUBLISH: '下架',
  PROCESS: '处理'
}

export const TargetTypeMap: Record<string, string> = {
  PRODUCT: '产品',
  CONTENT: '内容',
  CATEGORY: '分类',
  MESSAGE: '留言',
  USER: '管理员',
  STAFF: '员工',
  WORKORDER: '工单'
}

export interface OperationLogVO {
  logId: string
  operatorUuid: string
  operatorName: string
  operation: string
  targetType: string
  targetId: string
  targetName: string
  detail: string
  ip: string
  createdAt: string
}
