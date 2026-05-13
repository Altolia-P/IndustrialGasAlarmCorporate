export enum MessageStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  PROCESSED = 'PROCESSED'
}

export const MessageStatusMap: Record<MessageStatus, string> = {
  [MessageStatus.PENDING]: '未处理',
  [MessageStatus.IN_PROGRESS]: '处理中',
  [MessageStatus.PROCESSED]: '已处理'
}

export interface MessageVO {
  messageUuid: string
  name: string
  phone: string
  content: string
  status: MessageStatus
  assignedStaffUuid: string
  assignedStaffName: string
  submittedAt: string
  remark: string
}

export interface SubmitMessageDTO {
  name: string
  phone: string
  content: string
}

export interface AssignMessageDTO {
  staffUuid: string
  staffName: string
}

export interface CompleteMessageDTO {
  remark: string
}

export interface BatchProcessMessageDTO {
  uuids: string[]
  remark: string
}
