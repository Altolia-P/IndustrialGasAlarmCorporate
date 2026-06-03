<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { dashboardApi, type DashboardDevice, type DashboardAlert, type DeviceDataPoint } from '@/api/dashboard'
import { useWebSocket } from '@/composables/useWebSocket'
import StatCards from './components/StatCards.vue'
import TrendChart from './components/TrendChart.vue'
import DeviceStatusList from './components/DeviceStatusList.vue'
import AlertFeed from './components/AlertFeed.vue'

const router = useRouter()
const authStore = useAuthStore()

const { connected, lastMessage, connect, disconnect } = useWebSocket()

const onlineCount = ref(0)
const totalCount = ref(0)
const alertCount = ref(0)
const todayDataPoints = ref<number | string>('—')
const avgConcentration = ref('—')
const uptime = ref('—')
const devices = ref<DashboardDevice[]>([])
const alerts = ref<DashboardAlert[]>([])
const fullscreen = ref(false)

const deviceData = ref<Map<string, DeviceDataPoint[]>>(new Map())
const deviceNames = ref<Map<string, string>>(new Map())
const selectedDeviceUuid = ref('')

let refreshTimer: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  loadInitialData()
  connect()
  refreshTimer = setInterval(loadInitialData, 10_000)
})

onUnmounted(() => {
  disconnect()
  if (refreshTimer) clearInterval(refreshTimer)
})

watch(lastMessage, (msg) => {
  if (!msg) return
  for (const dp of msg.dataPoints || []) {
    const points = deviceData.value.get(dp.deviceUuid) || []
    points.push({
      deviceUuid: dp.deviceUuid,
      timestamp: dp.timestamp,
      concentration: dp.concentration
    } as DeviceDataPoint)
    if (points.length > 60) points.shift()
    deviceData.value.set(dp.deviceUuid, points)
    deviceNames.value.set(dp.deviceUuid, dp.deviceName)
  }
})

async function loadInitialData() {
  try {
    const [devicesData, alertsData, overviewData] = await Promise.all([
      dashboardApi.getDevices(),
      dashboardApi.getAlerts(20),
      dashboardApi.getOverview()
    ])

    if (overviewData) {
      todayDataPoints.value = overviewData.todayDataPoints
      avgConcentration.value = overviewData.avgConcentration
      uptime.value = overviewData.uptime
    }

    if (devicesData) {
      devices.value = devicesData
      totalCount.value = devicesData.length
      onlineCount.value = devicesData.filter(
        d => d.status === 'NORMAL' || d.status === 'ABNORMAL'
      ).length

      devicesData.forEach(d => {
        deviceNames.value.set(d.deviceUuid, d.name)
      })

      // Default select the first online device (simulator preferred)
      if (!selectedDeviceUuid.value && devicesData.length > 0) {
        const sim = devicesData.find(d => d.name.toLowerCase().includes('sim'))
        selectedDeviceUuid.value = sim ? sim.deviceUuid : devicesData[0].deviceUuid
      }

      // Fetch latest data for selected device for initial chart
      const targetUuid = selectedDeviceUuid.value || devicesData[0]?.deviceUuid
      if (targetUuid) {
        try {
          const dp = await dashboardApi.getLatestDataPoint(targetUuid)
          if (dp) {
            const points = deviceData.value.get(targetUuid) || []
            points.push(dp)
            if (points.length > 60) points.shift()
            deviceData.value.set(targetUuid, points)
          }
        } catch {
          // skip offline devices
        }
      }
    }

    if (alertsData) {
      alerts.value = alertsData
      alertCount.value = alertsData.length
    }
  } catch {
    // degrade gracefully
  }
}

function toggleFullscreen() {
  if (!fullscreen.value) {
    document.documentElement.requestFullscreen?.()
    fullscreen.value = true
  } else {
    document.exitFullscreen?.()
    fullscreen.value = false
  }
}

function goBack() {
  if (authStore.isAdmin) router.push('/admin')
  else if (authStore.isStaff) router.push('/staff')
  else router.push('/user')
}

const trendChartDevices = ref<{ deviceUuid: string; name: string }[]>([])

// Sync device list for TrendChart selector whenever devices changes
watch(devices, (list) => {
  trendChartDevices.value = list.map(d => ({ deviceUuid: d.deviceUuid, name: d.name }))
}, { immediate: true })
</script>

<template>
  <div class="dashboard" :class="{ fullscreen: fullscreen }">
    <div class="dashboard-header">
      <div class="header-left">
        <button class="btn-back" @click="goBack" title="返回">&larr;</button>
        <h1>InterSense 工业气体报警监控平台</h1>
        <span class="ws-status" :class="{ connected }">
          {{ connected ? '实时' : '离线' }}
        </span>
      </div>
      <div class="header-right">
        <span class="user-info">{{ authStore.username }}</span>
        <router-link to="/dashboard/history" class="btn-history">📋 历史数据</router-link>
        <button class="btn-icon" @click="toggleFullscreen" title="全屏">
          {{ fullscreen ? '⤤' : '⛶' }}
        </button>
        <button class="btn-logout" @click="authStore.logout(); router.push('/login')">退出</button>
      </div>
    </div>

    <div class="dashboard-body">
      <StatCards
        :online-count="onlineCount"
        :total-count="totalCount"
        :alert-count="alertCount"
        :today-data-points="todayDataPoints"
        :avg-concentration="avgConcentration"
        :uptime="uptime"
      />

      <div class="top-row">
        <div class="col-chart">
          <TrendChart
            :device-data="deviceData"
            :device-names="deviceNames"
            :devices="trendChartDevices"
            v-model:selected-device-uuid="selectedDeviceUuid"
          />
        </div>
        <div class="col-alert">
          <AlertFeed :alerts="alerts" />
        </div>
      </div>

      <div class="bottom-row">
        <DeviceStatusList :devices="devices" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: #f8fafc;
}

.dashboard.fullscreen {
  background: #0f172a;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background: #ffffff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.fullscreen .dashboard-header {
  background: #1e293b;
  border-bottom-color: rgba(255, 255, 255, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.fullscreen .header-left h1 {
  color: #f1f5f9;
}

.header-left h1 {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.btn-back {
  background: none;
  border: 1px solid rgba(0, 0, 0, 0.1);
  color: #6b7280;
  font-size: 18px;
  cursor: pointer;
  border-radius: 8px;
  padding: 4px 12px;
  line-height: 1;
  transition: all 0.2s;
}

.btn-back:hover {
  background: #f3f4f6;
  color: #111827;
}

.btn-history {
  padding: 6px 14px;
  font-size: 13px;
  color: #3b82f6;
  text-decoration: none;
  border: 1px solid #3b82f6;
  border-radius: 8px;
  transition: all 0.2s;
}

.btn-history:hover {
  background: #eff6ff;
}

.ws-status {
  font-size: 12px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 999px;
  background: #fef2f2;
  color: #ef4444;
}

.ws-status.connected {
  background: #f0fdf4;
  color: #10b981;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  font-size: 14px;
  color: #6b7280;
}

.btn-icon {
  background: none;
  border: 1px solid rgba(0, 0, 0, 0.1);
  font-size: 18px;
  cursor: pointer;
  border-radius: 8px;
  padding: 4px 10px;
  color: #6b7280;
}

.btn-logout {
  padding: 6px 16px;
  font-size: 13px;
  background: #ef4444;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.dashboard-body {
  padding: 20px 24px;
  max-width: 1600px;
  margin: 0 auto;
}

.top-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.col-chart {
  min-height: 400px;
  display: flex;
  flex-direction: column;
}

.col-alert {
  min-height: 400px;
  display: flex;
  flex-direction: column;
}

.bottom-row {
  min-height: 300px;
  display: flex;
  flex-direction: column;
}

@media (max-width: 1200px) {
  .top-row {
    grid-template-columns: 1fr;
  }
  .col-chart, .col-alert {
    min-height: 320px;
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    padding: 10px 16px;
    flex-wrap: wrap;
    gap: 8px;
  }
  .dashboard-header h1 {
    font-size: 15px;
  }
  .dashboard-body {
    padding: 10px;
  }
  .top-row {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-bottom: 12px;
  }
  .col-chart,
  .col-alert {
    flex: 1 1 0;
    min-height: 260px;
  }
  .bottom-row {
    min-height: 0;
  }
}
</style>
