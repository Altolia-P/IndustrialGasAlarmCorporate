import request from '@/utils/request'
import type { SendMessageDTO, ChatResponseVO } from '@/types/ai-chat'

export const aiChatApi = {
  sendMessage(dto: SendMessageDTO): Promise<ChatResponseVO> {
    return request.post('/user/ai/chat', dto, {
      timeout: Number(import.meta.env.VITE_AI_CHAT_TIMEOUT) || 30000
    })
  }
}
