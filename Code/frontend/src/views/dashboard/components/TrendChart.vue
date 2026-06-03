<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([LineChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent, CanvasRenderer])

interface DataPoint {
  timestamp: string
  concentration: string
  deviceUuid: string
}

interface DeviceOption {
  deviceUuid: string
  name: string
}

const props = defineProps<{
  deviceData: Map<string, DataPoint[]>
  deviceNames: Map<string, string>
  devices: DeviceOption[]
  selectedDeviceUuid: string
}>()

const emit = defineEmits<{
  'update:selectedDeviceUuid': [value: string]
}>()

function onSelectChange(e: Event) {
  emit('update:selectedDeviceUuid', (e.target as HTMLSelectElement).value)
}

const chartOption = computed(() => {
  const deviceUuids = Array.from(props.deviceData.keys())
  const filteredUuids = props.selectedDeviceUuid
    ? deviceUuids.filter(u => u === props.selectedDeviceUuid)
    : deviceUuids

  if (filteredUuids.length === 0) {
    return {
      title: { text: '暂无实时数据', left: 'center', top: 'center', textStyle: { color: '#9ca3af' } }
    }
  }

  const colors = ['#3b82f6', '#ef4444', '#10b981', '#f59e0b', '#8b5cf6', '#ec4899']
  const series = filteredUuids.map((uuid, idx) => {
    const points = props.deviceData.get(uuid) || []
    const name = props.deviceNames.get(uuid) || uuid.slice(0, 8)
    return {
      name,
      type: 'line' as const,
      smooth: true,
      symbol: 'none' as const,
      lineStyle: { color: colors[idx % colors.length], width: 2 },
      areaStyle: {
        color: {
          type: 'linear' as const, x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: colors[idx % colors.length] + '33' },
            { offset: 1, color: colors[idx % colors.length] + '05' }
          ]
        }
      },
      data: points.map(p => [p.timestamp, parseFloat(p.concentration) || 0])
    }
  })

  return {
    grid: { top: 40, right: 20, bottom: 30, left: 50 },
    legend: { top: 6, textStyle: { fontSize: 12, color: '#6b7280' } },
    tooltip: {
      trigger: 'axis' as const,
      formatter: (params: any) => {
        if (!Array.isArray(params)) return ''
        return params.map((p: any) =>
          `<div style="color:#374151">${p.seriesName}: <b>${p.value[1]} %LEL</b></div>`
        ).join('')
      }
    },
    xAxis: {
      type: 'time' as const,
      axisLabel: { fontSize: 11, color: '#9ca3af' },
      splitLine: { show: false }
    },
    yAxis: {
      type: 'value' as const,
      name: '%LEL',
      axisLabel: { fontSize: 11, color: '#9ca3af' },
      splitLine: { lineStyle: { color: '#f3f4f6' } }
    },
    series
  }
})
</script>

<template>
  <div class="trend-chart">
    <div class="chart-header">
      <h3>实时浓度趋势</h3>
      <div class="chart-controls">
        <select
          class="device-select"
          :value="selectedDeviceUuid"
          @change="onSelectChange"
        >
          <option value="">全部设备</option>
          <option
            v-for="d in devices"
            :key="d.deviceUuid"
            :value="d.deviceUuid"
          >
            {{ d.name }}
          </option>
        </select>
        <span class="chart-hint">最近 5 分钟</span>
      </div>
    </div>
    <VChart class="chart" :option="chartOption" autoresize />
  </div>
</template>

<style scoped>
.trend-chart {
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  padding: 20px;
  flex: 1;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
  gap: 12px;
}

.chart-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0;
  white-space: nowrap;
}

.chart-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.device-select {
  font-size: 13px;
  padding: 4px 10px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: #ffffff;
  color: #374151;
  cursor: pointer;
  max-width: 180px;
  outline: none;
}

.device-select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.15);
}

.chart-hint {
  font-size: 12px;
  color: #9ca3af;
  white-space: nowrap;
}

.chart {
  height: 320px;
  width: 100%;
}

@media (max-width: 768px) {
  .trend-chart {
    padding: 14px;
  }
  .chart {
    height: 240px;
  }
  .chart-header {
    flex-wrap: wrap;
  }
  .device-select {
    max-width: 140px;
  }
}
</style>
