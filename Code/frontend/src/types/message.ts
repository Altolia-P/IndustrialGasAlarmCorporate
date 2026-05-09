export enum MessageStatus {
  PENDING = 'PENDING',
  PROCESSED = 'PROCESSED'
}

export const MessageStatusMap: Record<MessageStatus, string> = {
  [MessageStatus.PENDING]: '未处理',
  [MessageStatus.PROCESSED]: '已处理'
}

export interface MessageVO {
  messageUuid: string
  name: string
  phone: string
  content: string
  status: MessageStatus
  processor: string | null
  remark: string | null
  submittedAt: string
  processedAt: string | null
}

export interface SubmitMessageDTO {
  name: string
  phone: string
  content: string
}

export interface ProcessMessageDTO {
  remark: string
}

export interface BatchProcessMessageDTO {
  uuids: string[]
  remark: string
}
