export enum WorkOrderType {
  TECH_SUPPORT = 'TECH_SUPPORT',
  AFTER_SALES = 'AFTER_SALES'
}

export const WorkOrderTypeMap: Record<WorkOrderType, string> = {
  [WorkOrderType.TECH_SUPPORT]: '技术支持',
  [WorkOrderType.AFTER_SALES]: '售后服务'
}

export enum WorkOrderStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED'
}

export const WorkOrderStatusMap: Record<WorkOrderStatus, string> = {
  [WorkOrderStatus.PENDING]: '待处理',
  [WorkOrderStatus.IN_PROGRESS]: '处理中',
  [WorkOrderStatus.COMPLETED]: '已完成'
}

export enum WorkOrderPriority {
  HIGH = 'HIGH',
  MEDIUM = 'MEDIUM',
  LOW = 'LOW'
}

export const WorkOrderPriorityMap: Record<WorkOrderPriority, string> = {
  [WorkOrderPriority.HIGH]: '高',
  [WorkOrderPriority.MEDIUM]: '中',
  [WorkOrderPriority.LOW]: '低'
}

export interface WorkOrderVO {
  workOrderUuid: string
  title: string
  type: WorkOrderType
  description: string
  status: WorkOrderStatus
  priority: WorkOrderPriority
  customerName: string
  customerPhone: string
  assignedStaffUuid: string
  assignedStaffName: string
  resolution: string
  createdAt: string
  updatedAt: string
}
