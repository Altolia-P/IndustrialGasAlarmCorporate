<script setup lang="ts">
import { computed, watch, onMounted, ref } from 'vue'
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

const props = defineProps<{
  deviceData: Map<string, DataPoint[]>
  deviceNames: Map<string, string>
}>()

const history = ref<{ time: string; series: Record<string, number> }[]>([])

const chartOption = computed(() => {
  const deviceUuids = Array.from(props.deviceData.keys())
  if (deviceUuids.length === 0) {
    return { title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#9ca3af' } } }
  }

  const series = deviceUuids.map((uuid, idx) => {
    const points = props.deviceData.get(uuid) || []
    const name = props.deviceNames.get(uuid) || uuid.slice(0, 8)
    const colors = ['#3b82f6', '#ef4444', '#10b981', '#f59e0b', '#8b5cf6', '#ec4899']
    return {
      name,
      type: 'line' as const,
      smooth: true,
      symbol: 'none' as const,
      lineStyle: { color: colors[idx % colors.length], width: 2 },
      data: points.map(p => [p.timestamp, parseFloat(p.concentration) || 0])
    }
  })

  const allPoints = deviceUuids.flatMap(u => props.deviceData.get(u) || [])
  const timestamps = allPoints.map(p => p.timestamp).sort()

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
      <span class="chart-hint">最近 5 分钟</span>
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
}

.chart-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.chart-hint {
  font-size: 12px;
  color: #9ca3af;
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
}
</style>
