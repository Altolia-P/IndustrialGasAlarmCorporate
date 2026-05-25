<script setup lang="ts">
defineProps<{
  unreadCount: number
}>()

defineEmits<{
  click: []
}>()
</script>

<template>
  <button class="float-ball" @click="$emit('click')" :title="'AI 智能助手' + (unreadCount > 0 ? ` (${unreadCount} 条新消息)` : '')">
    <div class="ball-icon">
      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z" fill="currentColor"/>
      </svg>
    </div>
    <Transition name="badge-fade">
      <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
    </Transition>
  </button>
</template>

<style scoped>
.float-ball {
  position: relative;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, var(--color-primary), #8b5cf6);
  color: var(--color-white);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.4);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.float-ball:hover {
  transform: scale(1.08);
  box-shadow: 0 6px 24px rgba(59, 130, 246, 0.55);
}

.float-ball:active {
  transform: scale(0.96);
}

.ball-icon {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 20px;
  height: 20px;
  padding: 0 5px;
  border-radius: 10px;
  background: #ef4444;
  color: var(--color-white);
  font-size: 11px;
  font-weight: 600;
  line-height: 20px;
  text-align: center;
  box-shadow: 0 2px 6px rgba(239, 68, 68, 0.45);
}

.badge-fade-enter-active,
.badge-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.badge-fade-enter-from,
.badge-fade-leave-to {
  opacity: 0;
  transform: scale(0.6);
}
</style>
