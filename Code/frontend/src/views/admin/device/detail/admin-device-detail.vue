<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, DataZoomComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { deviceApi } from '@/api/device'
import { DeviceStatusMap, GasTypeMap } from '@/types/device'
import type { DeviceVO, DeviceDataPointVO } from '@/types/device'

use([LineChart, GridComponent, TooltipComponent, LegendComponent, DataZoomComponent, CanvasRenderer])

const route = useRoute()
const router = useRouter()
const uuid = route.params.uuid as string

const device = ref<DeviceVO | null>(null)
const latest = ref<DeviceDataPointVO | null>(null)
const history = ref<DeviceDataPointVO[]>([])
const loading = ref(false)
let pollTimer: ReturnType<typeof setInterval> | null = null

const rangeOptions = [
  { label: '最近1小时', value: '1h' },
  { label: '最近6小时', value: '6h' },
  { label: '最近24小时', value: '24h' },
  { label: '最近7天', value: '7d' }
]
const selectedRange = ref('1h')

function getTimeRange(range: string): [string, string] {
  const now = new Date()
  const to = now.toISOString().replace('T', ' ').substring(0, 19)
  let from: Date
  switch (range) {
    case '1h': from = new Date(now.getTime() - 3600000); break
    case '6h': from = new Date(now.getTime() - 21600000); break
    case '24h': from = new Date(now.getTime() - 86400000); break
    case '7d': from = new Date(now.getTime() - 604800000); break
    default: from = new Date(now.getTime() - 3600000)
  }
  return [from.toISOString().replace('T', ' ').substring(0, 19), to]
}

async function fetchDevice() {
  try {
    device.value = await deviceApi.getByUuid(uuid)
  } catch {
    ElMessage.error('加载设备信息失败')
  }
}

async function fetchLatest() {
  try {
    latest.value = await deviceApi.getLatest(uuid)
  } catch { /* silent */ }
}

async function fetchHistory() {
  const [from, to] = getTimeRange(selectedRange.value)
  try {
    history.value = await deviceApi.getDataPoints(uuid, from, to)
  } catch {
    history.value = []
  }
}

function handleRangeChange() {
  fetchHistory()
}

const chartOption = computed(() => {
  if (!history.value.length) return {}
  const data = [...history.value].sort((a, b) => a.timestamp.localeCompare(b.timestamp))
  return {
    tooltip: { trigger: 'axis' },
    legend: { data: ['浓度', '温度', '湿度'] },
    grid: { left: 50, right: 20, top: 40, bottom: 60 },
    dataZoom: [{ type: 'inside' }, { type: 'slider', height: 20 }],
    xAxis: { type: 'category', data: data.map(d => d.timestamp.substring(5, 16)), boundaryGap: false },
    yAxis: [
      { type: 'value', name: '浓度' },
      { type: 'value', name: '温度(°C)' },
      { type: 'value', name: '湿度(%)' }
    ],
    series: [
      { name: '浓度', type: 'line', data: data.map(d => Number(d.concentration)), smooth: true, symbol: 'none' },
      { name: '温度', type: 'line', yAxisIndex: 1, data: data.map(d => d.temperature ? Number(d.temperature) : null), smooth: true, symbol: 'none' },
      { name: '湿度', type: 'line', yAxisIndex: 2, data: data.map(d => d.humidity ? Number(d.humidity) : null), smooth: true, symbol: 'none' }
    ]
  }
})

function startPolling() {
  fetchLatest()
  pollTimer = setInterval(fetchLatest, 30000)
}

function stopPolling() {
  if (pollTimer) { clearInterval(pollTimer); pollTimer = null }
}

onMounted(async () => {
  loading.value = true
  await fetchDevice()
  await Promise.all([fetchLatest(), fetchHistory()])
  loading.value = false
  startPolling()
})

onUnmounted(() => stopPolling())
</script>

<template>
  <div class="admin-device-detail">
    <div class="page-header">
      <el-button text @click="router.push({ name: 'AdminDevices' })">← 返回设备列表</el-button>
      <el-button type="primary" @click="router.push({ name: 'AdminDeviceEdit', params: { uuid } })">编辑设备</el-button>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="6" animated />
    </div>

    <template v-else-if="device">
      <div class="info-card">
        <h3 class="card-title">设备信息</h3>
        <div class="info-grid">
          <div class="info-item"><span class="info-label">设备名称</span><span class="info-value">{{ device.name }}</span></div>
          <div class="info-item"><span class="info-label">序列号</span><span class="info-value">{{ device.serialNumber }}</span></div>
          <div class="info-item"><span class="info-label">型号</span><span class="info-value">{{ device.model }}</span></div>
          <div class="info-item"><span class="info-label">气体类型</span><span class="info-value">{{ GasTypeMap[device.gasType as keyof typeof GasTypeMap] || device.gasType }}</span></div>
          <div class="info-item"><span class="info-label">状态</span><span class="info-value"><el-tag size="small">{{ DeviceStatusMap[device.status as keyof typeof DeviceStatusMap] || device.status }}</el-tag></span></div>
          <div class="info-item"><span class="info-label">安装位置</span><span class="info-value">{{ device.installLocation || '-' }}</span></div>
          <div class="info-item"><span class="info-label">量程</span><span class="info-value">{{ device.rangeMin || '-' }} ~ {{ device.rangeMax || '-' }}</span></div>
          <div class="info-item"><span class="info-label">报警阈值</span><span class="info-value">{{ device.alertThreshold || '-' }}</span></div>
          <div class="info-item"><span class="info-label">客户名称</span><span class="info-value">{{ device.customerName || '-' }}</span></div>
          <div class="info-item"><span class="info-label">客户电话</span><span class="info-value">{{ device.customerPhone || '-' }}</span></div>
          <div class="info-item"><span class="info-label">创建时间</span><span class="info-value">{{ device.createdAt }}</span></div>
          <div class="info-item"><span class="info-label">更新时间</span><span class="info-value">{{ device.updatedAt }}</span></div>
        </div>
      </div>

      <div class="info-card">
        <div class="card-header">
          <h3 class="card-title">实时数据</h3>
          <span class="polling-hint" v-if="latest">每30秒自动刷新</span>
        </div>
        <div v-if="latest" class="latest-grid">
          <div class="latest-item">
            <span class="latest-label">浓度</span>
            <span class="latest-value concentration">{{ latest.concentration }}</span>
          </div>
          <div class="latest-item">
            <span class="latest-label">电池</span>
            <span class="latest-value">{{ latest.battery || '-' }}</span>
          </div>
          <div class="latest-item">
            <span class="latest-label">温度</span>
            <span class="latest-value">{{ latest.temperature ? latest.temperature + '°C' : '-' }}</span>
          </div>
          <div class="latest-item">
            <span class="latest-label">湿度</span>
            <span class="latest-value">{{ latest.humidity ? latest.humidity + '%' : '-' }}</span>
          </div>
          <div class="latest-item">
            <span class="latest-label">信号强度</span>
            <span class="latest-value">{{ latest.signalStrength }}</span>
          </div>
          <div class="latest-item">
            <span class="latest-label">数据时间</span>
            <span class="latest-value latest-time">{{ latest.timestamp }}</span>
          </div>
        </div>
        <div v-else class="no-data">暂无实时数据</div>
      </div>

      <div class="info-card">
        <div class="card-header">
          <h3 class="card-title">历史趋势</h3>
          <el-select v-model="selectedRange" style="width:140px" @change="handleRangeChange">
            <el-option v-for="opt in rangeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </div>
        <div v-if="history.length > 0" class="chart-wrapper">
          <VChart :option="chartOption" autoresize style="height:360px" />
        </div>
        <div v-else class="no-data">暂无历史数据</div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.admin-device-detail { display:flex; flex-direction:column; gap:16px; }
.page-header { display:flex; align-items:center; justify-content:space-between; }
.info-card { background:#fff; border-radius:8px; padding:24px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
.card-title { font-size:16px; font-weight:600; color:#1f2937; margin:0; }
.card-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px; }
.card-header .card-title { margin-bottom:0; }
.polling-hint { font-size:12px; color:#9ca3af; }
.info-grid { display:grid; grid-template-columns:repeat(2, 1fr); gap:12px 32px; margin-top:16px; }
.info-item { display:flex; gap:8px; }
.info-label { color:#6b7280; font-size:14px; min-width:72px; }
.info-value { color:#1f2937; font-size:14px; }
.latest-grid { display:grid; grid-template-columns:repeat(3, 1fr); gap:16px; margin-top:16px; }
.latest-item { display:flex; flex-direction:column; gap:4px; padding:16px; background:#f9fafb; border-radius:8px; }
.latest-label { font-size:13px; color:#6b7280; }
.latest-value { font-size:24px; font-weight:700; color:#1f2937; }
.latest-value.concentration { color:#ef4444; }
.latest-time { font-size:13px; }
.chart-wrapper { margin-top:16px; }
.no-data { display:flex; align-items:center; justify-content:center; padding:60px 24px; color:#9ca3af; font-size:14px; }
.loading-state { background:#fff; border-radius:8px; padding:40px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
</style>
