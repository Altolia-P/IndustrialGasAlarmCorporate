<script setup lang="ts">
import type { ChatMessageVO } from '@/types/ai-chat'
import { MessageRole } from '@/types/ai-chat'

defineProps<{
  message: ChatMessageVO
}>()
</script>

<template>
  <div class="message-row" :class="{ 'message-user': message.role === MessageRole.User }">
    <div class="message-bubble" :class="{ 'bubble-user': message.role === MessageRole.User }">
      <p v-if="message.role === MessageRole.Assistant && message.isTyping" class="message-text">
        {{ message.typedContent }}<span class="typing-cursor">|</span>
      </p>
      <p v-else class="message-text">{{ message.content }}</p>

      <div v-if="message.products && message.products.length > 0" class="recommended-cards">
        <div v-for="product in message.products" :key="product.uuid" class="recommend-card">
          <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.name" class="card-image" />
          <div class="card-body">
            <span class="card-label">推荐产品</span>
            <strong class="card-title">{{ product.name }}</strong>
            <p class="card-summary">{{ product.summary }}</p>
          </div>
        </div>
      </div>

      <div v-if="message.solutions && message.solutions.length > 0" class="recommended-cards">
        <div v-for="solution in message.solutions" :key="solution.uuid" class="recommend-card">
          <img v-if="solution.imageUrl" :src="solution.imageUrl" :alt="solution.title" class="card-image" />
          <div class="card-body">
            <span class="card-label">推荐方案</span>
            <strong class="card-title">{{ solution.title }}</strong>
            <p class="card-summary">{{ solution.summary }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.message-row {
  display: flex;
  padding: 0 16px;
  margin-bottom: 16px;
}

.message-row.message-user {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 80%;
  padding: 10px 14px;
  border-radius: 12px;
  background: var(--color-gray-100);
  color: var(--color-gray-900);
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.message-bubble.bubble-user {
  background: var(--color-primary);
  color: var(--color-white);
  border-bottom-right-radius: 4px;
}

.message-bubble:not(.bubble-user) {
  border-bottom-left-radius: 4px;
}

.message-text {
  margin: 0;
}

.typing-cursor {
  animation: blink 0.8s infinite;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

.recommended-cards {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.recommend-card {
  display: flex;
  background: var(--color-white);
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--color-gray-200);
}

.card-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  flex-shrink: 0;
}

.card-body {
  padding: 8px 10px;
  min-width: 0;
}

.card-label {
  display: block;
  font-size: 11px;
  color: var(--color-primary);
  font-weight: 500;
  margin-bottom: 2px;
}

.card-title {
  display: block;
  font-size: 13px;
  color: var(--color-gray-900);
  margin-bottom: 2px;
}

.card-summary {
  margin: 0;
  font-size: 12px;
  color: var(--color-gray-500);
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
