import { ref, onUnmounted } from 'vue'
import { getToken } from '@/utils/auth'

export interface WsMessage {
  type: string
  timestamp: string
  onlineCount: number
  offlineCount: number
  maintenanceCount: number
  totalCount: number
  pendingAlertCount: number
  devices: WsDevice[]
  alerts: WsAlert[]
  dataPoints: WsDataPoint[]
}

interface WsDevice {
  deviceUuid: string
  name: string
  status: string
  gasType: string
  installLocation: string
}

interface WsAlert {
  alertUuid: string
  deviceUuid: string
  severity: string
  message: string
  concentration: string
  triggeredAt: string
}

interface WsDataPoint {
  deviceUuid: string
  deviceName: string
  concentration: string
  timestamp: string
}

export function useWebSocket() {
  const connected = ref(false)
  const lastMessage = ref<WsMessage | null>(null)
  const reconnectCount = ref(0)

  let ws: WebSocket | null = null
  let reconnectTimer: ReturnType<typeof setTimeout> | null = null

  function connect() {
    if (ws && (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING)) {
      return
    }

    const token = getToken()
    if (!token) return

    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    const wsUrl = `${protocol}//${window.location.host}/ws/dashboard?token=${encodeURIComponent(token)}`

    try {
      ws = new WebSocket(wsUrl)
    } catch {
      scheduleReconnect()
      return
    }

    ws.onopen = () => {
      connected.value = true
      reconnectCount.value = 0
    }

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data) as WsMessage
        lastMessage.value = data
      } catch {
        // ignore malformed messages
      }
    }

    ws.onclose = () => {
      connected.value = false
      scheduleReconnect()
    }

    ws.onerror = () => {
      ws?.close()
    }
  }

  function scheduleReconnect() {
    if (reconnectTimer) return
    const delay = Math.min(1000 * Math.pow(2, reconnectCount.value), 30000)
    reconnectTimer = setTimeout(() => {
      reconnectTimer = null
      reconnectCount.value++
      connect()
    }, delay)
  }

  function disconnect() {
    if (reconnectTimer) {
      clearTimeout(reconnectTimer)
      reconnectTimer = null
    }
    ws?.close()
    ws = null
    connected.value = false
  }

  onUnmounted(disconnect)

  return { connected, lastMessage, connect, disconnect }
}
