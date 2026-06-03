import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { aiChatApi } from '@/api/ai'
import { useAiChatStore } from '@/stores/ai-chat'
import { MessageRole } from '@/types/ai-chat'
import type { ChatMessageVO } from '@/types/ai-chat'

const MAX_MESSAGE_LENGTH = 500
const TYPING_SPEED_MIN = 30
const TYPING_SPEED_MAX = 50

function generateId(): string {
  return Date.now().toString(36) + Math.random().toString(36).slice(2, 9)
}

export function useChat() {
  let store: ReturnType<typeof useAiChatStore>
  let activeInterval: ReturnType<typeof setInterval> | null = null

  function getStore() {
    if (!store) {
      store = useAiChatStore()
    }
    return store
  }

  const inputText = ref('')

  function sendMessage() {
    const s = getStore()
    const text = inputText.value.trim()
    if (!text || s.isSending) return

    if (text.length > MAX_MESSAGE_LENGTH) {
      ElMessage.warning(`消息不能超过 ${MAX_MESSAGE_LENGTH} 字`)
      return
    }

    const userMsg: ChatMessageVO = {
      id: generateId(),
      role: MessageRole.User,
      content: text,
      timestamp: Date.now()
    }
    s.addMessage(userMsg)
    inputText.value = ''
    s.setSending(true)

    aiChatApi
      .sendMessage({ sessionId: s.sessionId, message: text })
      .then((res) => {
        if (res.sessionId) {
          s.setSessionId(res.sessionId)
        }

        const assistantMsg: ChatMessageVO = {
          id: generateId(),
          role: MessageRole.Assistant,
          content: res.reply,
          timestamp: Date.now(),
          products: res.recommendedProducts,
          solutions: res.recommendedSolutions,
          isTyping: true,
          typedContent: ''
        }
        s.addMessage(assistantMsg)

        const reactiveMsg = s.messages[s.messages.length - 1]
        startTypingAnimation(reactiveMsg)
      })
      .catch((e: unknown) => {
        const msg = (e as { message?: string }).message || '网络异常，请稍后重试'
        ElMessage.error(msg)
        s.setSending(false)
      })
  }

  function startTypingAnimation(msg: ChatMessageVO) {
    const s = getStore()
    const fullText = msg.content
    let index = 0

    if (activeInterval) clearInterval(activeInterval)

    activeInterval = setInterval(() => {
      index++
      msg.typedContent = fullText.slice(0, index)

      if (index >= fullText.length) {
        clearInterval(activeInterval!)
        activeInterval = null
        msg.isTyping = false
        s.setSending(false)
      }
    }, TYPING_SPEED_MIN + Math.floor(Math.random() * (TYPING_SPEED_MAX - TYPING_SPEED_MIN)))
  }

  function dispose() {
    if (activeInterval) {
      clearInterval(activeInterval)
      activeInterval = null
    }
  }

  async function clearChat() {
    try {
      await ElMessageBox.confirm('确认清空所有对话记录？', '清空对话', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      })
      getStore().clearMessages()
    } catch {
      // 用户取消
    }
  }

  function toggleChat() {
    getStore().toggleOpen()
  }

  return {
    inputText,
    sendMessage,
    clearChat,
    toggleChat,
    dispose,
    store: computed(() => getStore())
  }
}
