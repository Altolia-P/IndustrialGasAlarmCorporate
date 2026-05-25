<script setup lang="ts">
import { watch, nextTick, ref } from 'vue'
import type { ChatMessageVO } from '@/types/ai-chat'
import ChatMessage from './ai-chat-message.vue'
import ChatInput from './ai-chat-input.vue'

const props = defineProps<{
  messages: ChatMessageVO[]
  inputText: string
  isSending: boolean
  isOpen: boolean
}>()

const emit = defineEmits<{
  'update:inputText': [value: string]
  send: []
  clear: []
  minimize: []
}>()

const messageListRef = ref<HTMLElement | null>(null)

watch(
  () => props.messages.length,
  () => {
    nextTick(() => {
      if (messageListRef.value) {
        messageListRef.value.scrollTop = messageListRef.value.scrollHeight
      }
    })
  }
)

watch(
  () => props.isOpen,
  (val) => {
    if (val) {
      nextTick(() => {
        if (messageListRef.value) {
          messageListRef.value.scrollTop = messageListRef.value.scrollHeight
        }
      })
    }
  }
)
</script>

<template>
  <Transition name="dialog-slide">
    <div v-if="isOpen" class="chat-dialog" @click.stop>
      <div class="dialog-header">
        <div class="header-left">
          <span class="header-title">AI 智能助手</span>
          <span v-if="isSending" class="header-status">正在回复...</span>
        </div>
        <div class="header-actions">
          <button class="action-btn" title="清空对话" @click="emit('clear')">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none">
              <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" fill="currentColor"/>
            </svg>
          </button>
          <button class="action-btn" title="最小化" @click="emit('minimize')">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none">
              <path d="M19 13H5v-2h14v2z" fill="currentColor"/>
            </svg>
          </button>
        </div>
      </div>

      <div ref="messageListRef" class="message-list">
        <div v-if="messages.length === 0" class="empty-state">
          <p class="empty-text">暂无对话，开始提问吧</p>
        </div>
        <ChatMessage
          v-for="msg in messages"
          :key="msg.id"
          :message="msg"
        />
      </div>

      <ChatInput
        :model-value="inputText"
        :disabled="isSending"
        :sending="isSending"
        @update:model-value="emit('update:inputText', $event)"
        @send="emit('send')"
      />
    </div>
  </Transition>
</template>

<style scoped>
.chat-dialog {
  width: 380px;
  height: 520px;
  display: flex;
  flex-direction: column;
  background: var(--color-white);
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: linear-gradient(135deg, var(--color-primary), #8b5cf6);
  color: var(--color-white);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-title {
  font-size: 15px;
  font-weight: 600;
}

.header-status {
  font-size: 12px;
  opacity: 0.85;
}

.header-actions {
  display: flex;
  gap: 4px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: none;
  background: rgba(255, 255, 255, 0.15);
  color: var(--color-white);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
  background: var(--color-gray-50);
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 24px;
}

.empty-text {
  font-size: 14px;
  color: var(--color-gray-400);
}

.dialog-slide-enter-active {
  transition: all 0.3s ease;
}

.dialog-slide-leave-active {
  transition: all 0.25s ease;
}

.dialog-slide-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

.dialog-slide-leave-to {
  opacity: 0;
  transform: translateY(10px) scale(0.97);
}

@media (max-width: 768px) {
  .chat-dialog {
    width: 100vw;
    height: 100vh;
    border-radius: 0;
    position: fixed;
    top: 0;
    left: 0;
    z-index: 10001;
  }
}
</style>
