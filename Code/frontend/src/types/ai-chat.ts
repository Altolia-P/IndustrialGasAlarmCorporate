export enum MessageRole {
  User = 'user',
  Assistant = 'assistant'
}

export interface RecommendedProductVO {
  uuid: string
  name: string
  summary: string
  imageUrl?: string
}

export interface RecommendedSolutionVO {
  uuid: string
  title: string
  summary: string
  imageUrl?: string
}

export interface ChatMessageVO {
  id: string
  role: MessageRole
  content: string
  timestamp: number
  products?: RecommendedProductVO[]
  solutions?: RecommendedSolutionVO[]
  isTyping?: boolean
  typedContent?: string
}

export interface ChatResponseVO {
  sessionId: string
  reply: string
  recommendedProducts?: RecommendedProductVO[]
  recommendedSolutions?: RecommendedSolutionVO[]
}

export interface SendMessageDTO {
  sessionId: string
  message: string
}
