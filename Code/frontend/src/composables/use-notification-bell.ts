import { ref, onMounted, onUnmounted } from 'vue'
import type { NotificationVO } from '@/types/device'

export interface NotificationBellApi {
  getUnreadCount: (since?: string) => Promise<number>
  getRecentNotifications: () => Promise<NotificationVO[]>
}

const LS_KEY = 'notification_last_seen_at'

function getLastSeenAt(): string {
  return localStorage.getItem(LS_KEY) || ''
}

function saveLastSeenAt(time: string) {
  localStorage.setItem(LS_KEY, time)
}

export function useNotificationBell() {
  const unreadCount = ref(0)
  const recentNotifications = ref<NotificationVO[]>([])
  const visible = ref(false)
  let timer: ReturnType<typeof setInterval> | null = null
  let alertApi: NotificationBellApi | null = null

  async function refresh(api: NotificationBellApi) {
    alertApi = api
    try {
      const since = getLastSeenAt()
      // Fetch count + recent notification list
      const count = await alertApi.getUnreadCount(since || undefined)
      const recent = await alertApi.getRecentNotifications()
      unreadCount.value = count
      recentNotifications.value = recent || []
    } catch {
      // silently ignore
    }
  }

  function toggle() {
    visible.value = !visible.value
    if (!visible.value) {
      // Closing the dropdown = mark all as read
      const now = new Date().toISOString().replace('Z', '')
      saveLastSeenAt(now)
      unreadCount.value = 0
    }
  }

  function close() {
    visible.value = false
    const now = new Date().toISOString().replace('Z', '')
    saveLastSeenAt(now)
    unreadCount.value = 0
  }

  function startPolling(api: NotificationBellApi, intervalMs = 30000) {
    refresh(api)
    timer = setInterval(() => refresh(api), intervalMs)
  }

  function stopPolling() {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }

  return { unreadCount, recentNotifications, visible, toggle, close, startPolling, stopPolling, refresh }
}
