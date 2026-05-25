<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useChat } from '@/composables/use-chat'
import { useAuthStore } from '@/stores/auth'
import AiFloatBall from './ai-float-ball.vue'
import AiChatDialog from './ai-chat-dialog.vue'

const router = useRouter()
const authStore = useAuthStore()
const { inputText, sendMessage, clearChat, toggleChat, store } = useChat()

onMounted(() => {
  if (!authStore.tokenVerified) {
    authStore.verifyToken()
  }
})

function handleFloatBallClick() {
  if (authStore.isLoggedIn) {
    toggleChat()
  } else {
    ElMessage.info('请先登录后使用 AI 智能助手')
    router.push('/login')
  }
}
</script>

<template>
  <div class="ai-assistant-root">
    <Transition name="ball-swap">
      <AiFloatBall
        v-if="!store.isOpen"
        :unread-count="store.unreadCount"
        @click="handleFloatBallClick"
      />
    </Transition>

    <AiChatDialog
      :messages="store.messages"
      :input-text="inputText"
      :is-sending="store.isSending"
      :is-open="store.isOpen"
      @update:input-text="inputText = $event"
      @send="sendMessage"
      @clear="clearChat"
      @minimize="store.minimize()"
    />
  </div>
</template>

<style scoped>
.ai-assistant-root {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 10000;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.ball-swap-enter-active,
.ball-swap-leave-active {
  transition: all 0.25s ease;
}

.ball-swap-enter-from,
.ball-swap-leave-to {
  opacity: 0;
  transform: scale(0.6);
}

@media (max-width: 768px) {
  .ai-assistant-root {
    bottom: 16px;
    right: 16px;
  }
}
</style>
