export enum StaffRole {
  FIELD_TECH = 'FIELD_TECH',
  CUSTOMER_SERVICE = 'CUSTOMER_SERVICE',
  TECH_SUPPORT = 'TECH_SUPPORT',
  AFTER_SALES = 'AFTER_SALES'
}

export const StaffRoleMap: Record<StaffRole, string> = {
  [StaffRole.FIELD_TECH]: '外派技术员',
  [StaffRole.CUSTOMER_SERVICE]: '客服专员',
  [StaffRole.TECH_SUPPORT]: '技术支持工程师',
  [StaffRole.AFTER_SALES]: '售后工程师'
}

export enum StaffStatus {
  VACATION = 'VACATION',
  STANDBY = 'STANDBY',
  WORKING = 'WORKING',
  BUSINESS_TRIP = 'BUSINESS_TRIP'
}

export const StaffStatusMap: Record<StaffStatus, string> = {
  [StaffStatus.VACATION]: '休假',
  [StaffStatus.STANDBY]: '待班',
  [StaffStatus.WORKING]: '工作中',
  [StaffStatus.BUSINESS_TRIP]: '出差'
}

export interface StaffVO {
  staffUuid: string
  name: string
  phone: string
  email: string
  role: StaffRole
  status: StaffStatus
  createdAt: string
}
