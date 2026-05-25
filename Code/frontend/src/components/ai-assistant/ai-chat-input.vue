<script setup lang="ts">
import { computed } from 'vue'

const modelValue = defineModel<string>({ default: '' })

const props = defineProps<{
  disabled: boolean
  sending: boolean
}>()

const emit = defineEmits<{
  send: []
}>()

const MAX_LENGTH = 500

const canSend = computed(() => modelValue.value.trim().length > 0 && !props.sending)

function handleKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    if (canSend.value) {
      handleSend()
    }
  }
}

function handleSend() {
  if (!canSend.value) return
  emit('send')
}
</script>

<template>
  <div class="chat-input-area">
    <div class="input-wrapper">
      <textarea
        v-model="modelValue"
        class="input-field"
        :disabled="disabled"
        :placeholder="disabled ? 'AI 正在回复...' : '输入您的问题...'"
        :maxlength="MAX_LENGTH"
        rows="1"
        @keydown="handleKeydown"
      />
      <button
        class="send-btn"
        :class="{ sending: sending }"
        :disabled="!canSend"
        @click="handleSend"
        title="发送"
      >
        <svg v-if="!sending" viewBox="0 0 24 24" width="18" height="18" fill="none">
          <path d="M2 21l21-9L2 3v7l15 2-15 2v7z" fill="currentColor"/>
        </svg>
        <span v-else class="loading-dot"></span>
      </button>
    </div>
    <p class="input-hint">{{ modelValue.length }}/{{ MAX_LENGTH }}</p>
  </div>
</template>

<style scoped>
.chat-input-area {
  padding: 12px 16px;
  border-top: 1px solid var(--color-gray-200);
  background: var(--color-white);
}

.input-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

.input-field {
  flex: 1;
  min-height: 40px;
  max-height: 120px;
  padding: 10px 14px;
  border: 1px solid var(--color-gray-200);
  border-radius: 20px;
  font-size: 14px;
  line-height: 1.5;
  color: var(--color-gray-900);
  background: var(--color-gray-50);
  resize: none;
  outline: none;
  transition: border-color 0.2s;
  font-family: inherit;
}

.input-field:focus {
  border-color: var(--color-primary);
  background: var(--color-white);
}

.input-field:disabled {
  background: var(--color-gray-100);
  color: var(--color-gray-400);
  cursor: not-allowed;
}

.send-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: var(--color-primary);
  color: var(--color-white);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: background 0.2s, transform 0.15s;
}

.send-btn:hover:not(:disabled) {
  background: var(--color-primary-dark);
  transform: scale(1.05);
}

.send-btn:active:not(:disabled) {
  transform: scale(0.95);
}

.send-btn:disabled {
  background: var(--color-gray-200);
  color: var(--color-gray-400);
  cursor: not-allowed;
}

.send-btn.sending {
  background: var(--color-primary);
  cursor: wait;
}

.loading-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--color-white);
  animation: pulse 0.8s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.4; transform: scale(0.7); }
}

.input-hint {
  margin: 4px 0 0;
  font-size: 11px;
  color: var(--color-gray-400);
  text-align: right;
}
</style>
