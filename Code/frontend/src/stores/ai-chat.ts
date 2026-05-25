import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { ChatMessageVO } from '@/types/ai-chat'
import { MessageRole } from '@/types/ai-chat'

const WELCOME_MESSAGE: ChatMessageVO = {
  id: 'welcome',
  role: MessageRole.Assistant,
  content: '您好，我是智能助手，可以帮您推荐产品和解决方案，请问有什么需要？',
  timestamp: Date.now()
}

export const useAiChatStore = defineStore('ai-chat', () => {
  const sessionId = ref<string>('')
  const messages = ref<ChatMessageVO[]>([{ ...WELCOME_MESSAGE }])
  const isOpen = ref(false)
  const isMinimized = ref(false)
  const unreadCount = ref(0)
  const isSending = ref(false)

  const hasMessages = computed(() => messages.value.length > 0)

  function toggleOpen() {
    if (isOpen.value) {
      isOpen.value = false
      isMinimized.value = true
    } else {
      isOpen.value = true
      isMinimized.value = false
      unreadCount.value = 0
    }
  }

  function minimize() {
    isOpen.value = false
    isMinimized.value = true
  }

  function addMessage(msg: ChatMessageVO) {
    messages.value.push(msg)
  }

  function clearMessages() {
    sessionId.value = ''
    messages.value = [{ ...WELCOME_MESSAGE, timestamp: Date.now() }]
  }

  function setSending(v: boolean) {
    isSending.value = v
  }

  function incrementUnread() {
    unreadCount.value++
  }

  function clearUnread() {
    unreadCount.value = 0
  }

  function setSessionId(id: string) {
    sessionId.value = id
  }

  return {
    sessionId,
    messages,
    isOpen,
    isMinimized,
    unreadCount,
    isSending,
    hasMessages,
    toggleOpen,
    minimize,
    addMessage,
    clearMessages,
    setSending,
    incrementUnread,
    clearUnread,
    setSessionId
  }
})
