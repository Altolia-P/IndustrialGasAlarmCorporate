import { ref, onMounted, onUnmounted, watch } from 'vue'
import { deviceApi } from '@/api/device'
import type { DeviceDataPointVO } from '@/types/device'

export function useDeviceData(deviceUuid: () => string | null, intervalMs = 30000) {
  const latest = ref<DeviceDataPointVO | null>(null)
  const history = ref<DeviceDataPointVO[]>([])
  const polling = ref(false)
  let timer: ReturnType<typeof setInterval> | null = null

  async function fetchLatest() {
    const uuid = deviceUuid()
    if (!uuid) return
    try {
      latest.value = await deviceApi.getLatest(uuid)
    } catch {
      // silently ignore poll errors
    }
  }

  async function loadHistory(from: string, to: string) {
    const uuid = deviceUuid()
    if (!uuid) return
    try {
      history.value = await deviceApi.getDataPoints(uuid, from, to)
    } catch {
      history.value = []
    }
  }

  function startPolling() {
    if (polling.value) return
    polling.value = true
    fetchLatest()
    timer = setInterval(fetchLatest, intervalMs)
  }

  function stopPolling() {
    polling.value = false
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }

  onMounted(() => {
    if (deviceUuid()) {
      startPolling()
    }
  })

  watch(
    () => deviceUuid(),
    (val) => {
      if (val) {
        startPolling()
      } else {
        stopPolling()
      }
    }
  )

  onUnmounted(() => {
    stopPolling()
  })

  return { latest, history, polling, fetchLatest, loadHistory, startPolling, stopPolling }
}
