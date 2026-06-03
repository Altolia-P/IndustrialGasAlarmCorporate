<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { dashboardApi, type DashboardDevice, type DashboardAlert, type DeviceDataPoint } from '@/api/dashboard'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent, DataZoomComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([LineChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent, DataZoomComponent, CanvasRenderer])

const router = useRouter()
const authStore = useAuthStore()

const devices = ref<DashboardDevice[]>([])
const alerts = ref<DashboardAlert[]>([])
const dataPoints = ref<DeviceDataPoint[]>([])
const selectedDevice = ref('')
const timeRange = ref('1h')
const customerFilter = ref('')
const customFrom = ref('')
const customTo = ref('')
const loading = ref(false)

const customerOptions = computed(() => {
  const seen = new Set<string>()
  const opts: { label: string; value: string }[] = [{ label: '全部客户', value: '' }]
  for (const d of devices.value) {
    const key = d.customerUuid || '_none'
    if (!seen.has(key)) {
      seen.add(key)
      opts.push({ label: d.customerName || '未分配', value: d.customerUuid || '' })
    }
  }
  return opts
})

const filteredDevices = computed(() => {
  if (!customerFilter.value) return devices.value
  return devices.value.filter(d => d.customerUuid === customerFilter.value)
})

const timeRangeOptions = [
  { label: '最近1小时', value: '1h' },
  { label: '最近6小时', value: '6h' },
  { label: '最近24小时', value: '24h' },
  { label: '自定义', value: 'custom' }
]

onMounted(async () => {
  try {
    const devs = await dashboardApi.getDevices()
    if (devs) {
      devices.value = authStore.isAdmin ? devs : devs.filter(() => true)
    }
  } catch { /* degrade */ }
})

watch(filteredDevices, (list) => {
  if (!selectedDevice.value || !list.find(d => d.deviceUuid === selectedDevice.value)) {
    selectedDevice.value = list.length > 0 ? list[0].deviceUuid : ''
  }
})

watch(selectedDevice, () => { if (selectedDevice.value) loadHistory() })
watch(timeRange, () => { if (selectedDevice.value && timeRange.value !== 'custom') loadHistory() })

function loadHistory() {
  if (!selectedDevice.value) return
  loading.value = true

  let from: string | undefined
  let to: string | undefined

  if (timeRange.value === 'custom') {
    from = customFrom.value || undefined
    to = customTo.value || undefined
  } else {
    const now = new Date()
    to = formatLocalTime(now)
    const hours = timeRange.value === '1h' ? 1 : timeRange.value === '6h' ? 6 : 24
    const past = new Date(now.getTime() - hours * 3600 * 1000)
    from = formatLocalTime(past)
  }

  Promise.all([
    dashboardApi.getDataPoints(selectedDevice.value, from, to),
    dashboardApi.getAlerts(200)
  ]).then(([points, alts]) => {
    dataPoints.value = points || []
    if (alts) {
      alerts.value = alts.filter(a => a.deviceUuid === selectedDevice.value)
    }
    loading.value = false
  }).catch(() => { loading.value = false })
}

const chartOption = computed(() => {
  if (dataPoints.value.length === 0) {
    return {
      title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#9ca3af' } }
    }
  }

  const selectedDev = devices.value.find(d => d.deviceUuid === selectedDevice.value)
  const name = selectedDev?.name || selectedDevice.value.slice(0, 8)

  return {
    grid: { top: 50, right: 24, bottom: 60, left: 60 },
    title: { text: `${name} — 历史浓度`, left: 'center', top: 8, textStyle: { fontSize: 14, color: '#374151' } },
    tooltip: {
      trigger: 'axis' as const,
      formatter: (params: any) => {
        if (!Array.isArray(params) || params.length === 0) return ''
        const p = params[0]
        return `<div style="color:#374151">${p.axisValue}</div><div><b>${p.value[1]} %LEL</b></div>`
      }
    },
    xAxis: {
      type: 'time' as const,
      axisLabel: { fontSize: 11, color: '#9ca3af', rotate: 20 },
      splitLine: { show: false }
    },
    yAxis: {
      type: 'value' as const,
      name: '%LEL',
      axisLabel: { fontSize: 11, color: '#9ca3af' },
      splitLine: { lineStyle: { color: '#f3f4f6' } }
    },
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      { type: 'slider', start: 0, end: 100, height: 24, bottom: 8 }
    ],
    series: [{
      name,
      type: 'line' as const,
      smooth: false,
      symbol: 'none' as const,
      lineStyle: { color: '#3b82f6', width: 1.5 },
      areaStyle: { color: 'rgba(59,130,246,0.08)' },
      data: dataPoints.value.map(p => [p.timestamp, parseFloat(p.concentration) || 0])
    }]
  }
})

function goBack() {
  router.push('/dashboard')
}

function severityTag(severity: string) {
  switch (severity) {
    case 'CRITICAL': return '严重'
    case 'WARNING': return '警告'
    case 'INFO': return '提示'
    default: return severity
  }
}

function formatLocalTime(date: Date): string {
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}
</script>

<template>
  <div class="history-view">
    <div class="history-header">
      <button class="btn-back" @click="goBack">&larr; 返回大屏</button>
      <h1>历史数据查询</h1>
    </div>

    <div class="history-controls">
      <div class="control-group">
        <label>客户筛选</label>
        <select v-model="customerFilter" class="device-select">
          <option v-for="opt in customerOptions" :key="opt.value" :value="opt.value">
            {{ opt.label }}
          </option>
        </select>
      </div>

      <div class="control-group">
        <label>设备选择</label>
        <select v-model="selectedDevice" class="device-select">
          <option v-for="d in filteredDevices" :key="d.deviceUuid" :value="d.deviceUuid">
            {{ d.name }} ({{ d.gasType }})
          </option>
        </select>
      </div>

      <div class="control-group">
        <label>时间范围</label>
        <div class="time-btns">
          <button
            v-for="opt in timeRangeOptions"
            :key="opt.value"
            class="time-btn"
            :class="{ active: timeRange === opt.value }"
            @click="timeRange = opt.value"
          >
            {{ opt.label }}
          </button>
        </div>
      </div>

      <div v-if="timeRange === 'custom'" class="control-group custom-range">
        <label>从</label>
        <input type="datetime-local" v-model="customFrom" class="date-input" />
        <label>到</label>
        <input type="datetime-local" v-model="customTo" class="date-input" />
        <button class="btn-query" @click="loadHistory">查询</button>
      </div>
    </div>

    <div class="history-content">
      <div class="chart-panel">
        <VChart v-if="dataPoints.length > 0" class="chart" :option="chartOption" autoresize />
        <div v-else class="chart-empty">
          <p v-if="loading">加载中...</p>
          <p v-else>请选择设备和时间范围查询</p>
        </div>
      </div>

      <div class="alert-panel">
        <h3>历史告警 <span v-if="alerts.length">({{ alerts.length }})</span></h3>
        <div class="alert-scroll">
          <div v-for="a in alerts" :key="a.alertUuid" class="alert-row">
            <span class="alert-sev" :class="a.severity.toLowerCase()">
              {{ severityTag(a.severity) }}
            </span>
            <span class="alert-msg">{{ a.message }}</span>
            <span class="alert-time">{{ a.triggeredAt }}</span>
          </div>
          <div v-if="alerts.length === 0" class="empty">选择设备后显示告警记录</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.history-view {
  min-height: 100vh;
  background: #f8fafc;
}

.history-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 24px;
  background: #ffffff;
  border-bottom: 1px solid rgba(0,0,0,0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.history-header h1 {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.btn-back {
  background: none;
  border: 1px solid rgba(0,0,0,0.1);
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 14px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-back:hover { background: #f3f4f6; color: #111827; }

.history-controls {
  display: flex;
  align-items: flex-end;
  gap: 24px;
  padding: 16px 24px;
  background: #ffffff;
  border-bottom: 1px solid rgba(0,0,0,0.04);
  flex-wrap: wrap;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.control-group label {
  font-size: 13px;
  font-weight: 600;
  color: #6b7280;
}

.device-select {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  color: #111827;
  background: #ffffff;
  min-width: 260px;
  cursor: pointer;
}

.time-btns {
  display: flex;
  gap: 6px;
}

.time-btn {
  padding: 8px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #ffffff;
  font-size: 13px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
}

.time-btn.active {
  background: #3b82f6;
  color: #ffffff;
  border-color: #3b82f6;
}

.time-btn:hover:not(.active) { background: #f3f4f6; }

.custom-range {
  flex-direction: row;
  align-items: center;
  gap: 10px;
}

.date-input {
  padding: 7px 10px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 13px;
  color: #111827;
}

.btn-query {
  padding: 8px 20px;
  background: #10b981;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}

.btn-query:hover { background: #059669; }

.history-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
  padding: 20px 24px;
  max-width: 1600px;
  margin: 0 auto;
}

.chart-panel {
  background: #ffffff;
  border: 1px solid rgba(0,0,0,0.06);
  border-radius: 12px;
  padding: 20px;
  min-height: 400px;
}

.chart {
  height: 420px;
  width: 100%;
}

.chart-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #9ca3af;
}

.alert-panel {
  background: #ffffff;
  border: 1px solid rgba(0,0,0,0.06);
  border-radius: 12px;
  padding: 20px;
  max-height: 500px;
  display: flex;
  flex-direction: column;
}

.alert-panel h3 {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 14px;
}

.alert-scroll { flex: 1; overflow-y: auto; }

.alert-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid rgba(0,0,0,0.04);
}

.alert-sev {
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 4px;
  flex-shrink: 0;
  min-width: 40px;
  text-align: center;
}

.alert-sev.critical { background: #fef2f2; color: #ef4444; }
.alert-sev.warning { background: #fffbeb; color: #f59e0b; }
.alert-sev.info { background: #eff6ff; color: #3b82f6; }

.alert-msg {
  flex: 1;
  font-size: 13px;
  color: #374151;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.alert-time {
  font-size: 12px;
  color: #9ca3af;
  flex-shrink: 0;
}

.empty {
  text-align: center;
  color: #9ca3af;
  padding: 30px 0;
}

@media (max-width: 1024px) {
  .history-content { grid-template-columns: 1fr; }
}
</style>
