<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationBell, type NotificationBellApi } from '@/composables/use-notification-bell'
import type { NotificationVO } from '@/types/device'

const props = withDefaults(defineProps<{
  api: NotificationBellApi
  viewAllRoute?: string
}>(), {
  viewAllRoute: '/admin/notifications'
})

const router = useRouter()
const { unreadCount, recentNotifications, visible, toggle, close, startPolling, stopPolling } = useNotificationBell()

onMounted(() => startPolling(props.api))
onUnmounted(() => stopPolling())

function goToNotification(uuid: string) {
  close()
  router.push(props.viewAllRoute)
}

function goToAll() {
  close()
  router.push(props.viewAllRoute)
}

function channelLabel(ch: string) {
  return { IN_APP: '站内', SMS: '短信', EMAIL: '邮件' }[ch] || ch
}
</script>

<template>
  <div class="notification-bell">
    <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
      <el-button class="bell-btn" :icon="unreadCount > 0 ? 'Bell' : 'Bell'" circle @click="toggle">
        <span class="bell-icon">{{ unreadCount > 0 ? '🔔' : '🔕' }}</span>
      </el-button>
    </el-badge>

    <div v-if="visible" class="notif-dropdown" @click.stop>
      <div class="notif-dropdown-header">
        <span>通知</span>
        <el-button text size="small" @click="goToAll">查看全部</el-button>
      </div>

      <div v-if="recentNotifications.length === 0" class="notif-empty">
        暂无新通知
      </div>

      <div v-else class="notif-list">
        <div
          v-for="item in recentNotifications"
          :key="item.notificationUuid"
          class="notif-item"
          @click="goToNotification(item.notificationUuid)"
        >
          <div class="notif-item-content">{{ item.content }}</div>
          <div class="notif-item-meta">
            <span class="notif-item-time">{{ item.createdAt }}</span>
            <el-tag size="small" type="info">{{ channelLabel(item.channel) }}</el-tag>
          </div>
        </div>
      </div>
    </div>

    <div v-if="visible" class="notif-backdrop" @click="close" />
  </div>
</template>

<style scoped>
.notification-bell {
  position: relative;
}

.bell-btn {
  border: none;
  background: transparent;
  font-size: 18px;
  width: 40px;
  height: 40px;
}

.bell-icon {
  font-size: 18px;
  line-height: 1;
}

.notif-dropdown {
  position: absolute;
  top: 48px;
  right: 0;
  width: 380px;
  max-height: 420px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  z-index: 1001;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.notif-dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f3f4f6;
  font-weight: 600;
  font-size: 14px;
  color: #1f2937;
}

.notif-empty {
  padding: 40px 16px;
  text-align: center;
  color: #9ca3af;
  font-size: 14px;
}

.notif-list {
  overflow-y: auto;
  max-height: 360px;
}

.notif-item {
  padding: 12px 16px;
  border-bottom: 1px solid #f9fafb;
  cursor: pointer;
  transition: background 0.15s;
}

.notif-item:hover {
  background: #f9fafb;
}

.notif-item-content {
  font-size: 13px;
  color: #374151;
  line-height: 1.5;
  margin-bottom: 6px;
}

.notif-item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notif-item-time {
  font-size: 12px;
  color: #9ca3af;
}

.notif-backdrop {
  position: fixed;
  inset: 0;
  z-index: 1000;
}
</style>
