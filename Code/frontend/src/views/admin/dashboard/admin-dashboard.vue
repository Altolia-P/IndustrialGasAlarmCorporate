<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { messageApi } from '@/api/message'
import { dashboardApi } from '@/api/device'
import { workOrderApi } from '@/api/workorder'
import { MessageStatusMap } from '@/types/message'
import { WorkOrderStatusMap } from '@/types/workorder'
import type { MessageVO } from '@/types/message'
import type { DashboardStatsVO } from '@/types/device'
import type { WorkOrderVO } from '@/types/workorder'

use([CanvasRenderer, LineChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const router = useRouter()
const stats = ref<DashboardStatsVO | null>(null)
const recentMessages = ref<MessageVO[]>([])
const recentWorkOrders = ref<WorkOrderVO[]>([])

const overviewCards = computed(() => {
  if (!stats.value) return []
  const s = stats.value
  return [
    { label: '待处理留言', value: s.pendingMessages, color: '#f59e0b', bg: '#fffbeb', route: { path: '/admin/messages', query: { status: 'PENDING' } } },
    { label: '待处理工单', value: s.pendingWorkOrders, color: '#ef4444', bg: '#fef2f2', route: { path: '/admin/workorders', query: { status: 'PENDING' } } },
    { label: '处理中工单', value: s.inProgressWorkOrders, color: '#3b82f6', bg: '#eff6ff', route: { path: '/admin/workorders', query: { status: 'IN_PROGRESS' } } },
    { label: '今日报警', value: s.alertsToday, color: '#f59e0b', bg: '#fffbeb', route: { path: '/admin/alerts', query: { status: 'PENDING' } } },
    { label: '待处理报警', value: s.pendingAlerts, color: '#ef4444', bg: '#fef2f2', route: { path: '/admin/alerts', query: { status: 'PENDING' } } }
  ]
})

const alertTrendOption = computed(() => {
  if (!stats.value?.alertTrend?.length) return {}
  const data = stats.value.alertTrend
  return {
    tooltip: { trigger: 'axis' as const },
    grid: { left: 40, right: 20, top: 20, bottom: 30 },
    xAxis: {
      type: 'category' as const,
      data: data.map((d) => d.date.slice(5)),
      axisLabel: { color: '#9ca3af', fontSize: 12 }
    },
    yAxis: {
      type: 'value' as const,
      minInterval: 1,
      axisLabel: { color: '#9ca3af', fontSize: 12 },
      splitLine: { lineStyle: { color: '#f3f4f6' } }
    },
    series: [{
      type: 'line',
      data: data.map((d) => d.count),
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { color: '#3b82f6', width: 2 },
      itemStyle: { color: '#3b82f6' },
      areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(59,130,246,0.15)' }, { offset: 1, color: 'rgba(59,130,246,0)' }] } }
    }]
  }
})

const woPieOption = computed(() => {
  if (!stats.value?.workOrderStatusDistribution) return {}
  const dist = stats.value.workOrderStatusDistribution
  const colorMap: Record<string, string> = { PENDING: '#f59e0b', IN_PROGRESS: '#3b82f6', COMPLETED: '#10b981' }
  const data = Object.entries(dist).map(([k, v]) => ({
    name: WorkOrderStatusMap[k as keyof typeof WorkOrderStatusMap] || k,
    value: v,
    itemStyle: { color: colorMap[k] || '#9ca3af' }
  }))
  return {
    tooltip: { trigger: 'item' as const, formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, textStyle: { color: '#6b7280', fontSize: 12 } },
    series: [{
      type: 'pie',
      radius: ['55%', '80%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: false,
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data
    }]
  }
})

const recentActivities = computed(() => {
  const items: { type: 'message' | 'workorder'; time: string; text: string; status: string }[] = []
  recentMessages.value.forEach((m) => {
    items.push({ type: 'message', time: m.submittedAt, text: `${m.name} 提交了留言`, status: m.status })
  })
  recentWorkOrders.value.forEach((w) => {
    items.push({ type: 'workorder', time: w.createdAt, text: `工单「${w.title}」`, status: w.status })
  })
  items.sort((a, b) => b.time.localeCompare(a.time))
  return items.slice(0, 8)
})

async function loadStats() {
  try {
    stats.value = await dashboardApi.getStats()
  } catch { /* leave as null */ }
}

async function loadRecent() {
  try {
    const [msgs, wos] = await Promise.all([
      messageApi.getAdminList({ size: 5 }),
      workOrderApi.getAdminList({ size: 5 })
    ])
    recentMessages.value = msgs.content
    recentWorkOrders.value = wos.content
  } catch {
    recentMessages.value = []
    recentWorkOrders.value = []
  }
}

function goTo(route: string | { path: string; query?: Record<string, string> }) {
  router.push(route)
}

onMounted(() => {
  loadStats()
  loadRecent()
})
</script>

<template>
  <div class="dashboard">
    <div class="overview-grid">
      <div
        v-for="card in overviewCards"
        :key="card.label"
        class="overview-card"
        :style="{ borderLeft: `4px solid ${card.color}` }"
        @click="goTo(card.route)"
      >
        <div class="overview-body">
          <span class="overview-label">{{ card.label }}</span>
          <span class="overview-value" :style="{ color: card.color }">{{ stats ? card.value : '-' }}</span>
        </div>
      </div>
    </div>

    <div class="charts-row">
      <div class="chart-card chart-card--wide">
        <h3 class="card-title">近7日报警趋势</h3>
        <VChart v-if="alertTrendOption.series" :option="alertTrendOption" autoresize style="height:280px" />
        <div v-else class="chart-empty">暂无报警数据</div>
      </div>
      <div class="chart-card chart-card--narrow">
        <h3 class="card-title">工单状态分布</h3>
        <VChart v-if="woPieOption.series" :option="woPieOption" autoresize style="height:280px" />
        <div v-else class="chart-empty">暂无工单数据</div>
      </div>
    </div>

    <div class="bottom-row">
      <div class="dashboard-card">
        <h3 class="card-title">最近动态</h3>
        <div v-if="recentActivities.length === 0" class="empty-text">暂无最近动态</div>
        <div v-else class="activity-list">
          <div v-for="(item, i) in recentActivities" :key="i" class="activity-item">
            <span class="activity-dot" :class="item.type" />
            <span class="activity-text">{{ item.text }}</span>
            <span class="activity-time">{{ item.time }}</span>
          </div>
        </div>
      </div>

      <div class="dashboard-card">
        <h3 class="card-title">快捷操作</h3>
        <div class="quick-actions">
          <div class="action-item" @click="goTo('/admin/products/create')">
            <span class="action-icon">➕</span>
            <span>新增产品</span>
          </div>
          <div class="action-item" @click="goTo('/admin/contents/create')">
            <span class="action-icon">📝</span>
            <span>新增内容</span>
          </div>
          <div class="action-item" @click="goTo('/admin/workorders/create')">
            <span class="action-icon">🔧</span>
            <span>新建工单</span>
          </div>
          <div class="action-item" @click="goTo('/admin/messages')">
            <span class="action-icon">💬</span>
            <span>查看留言</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

.overview-card {
  background: #ffffff;
  border-radius: 10px;
  padding: 20px 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
}

.overview-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.overview-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.overview-label {
  font-size: 13px;
  color: #9ca3af;
}

.overview-value {
  font-size: 28px;
  font-weight: 700;
}

.charts-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.chart-card {
  background: #ffffff;
  border-radius: 10px;
  padding: 20px 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.chart-card--wide { }
.chart-card--narrow { }

.bottom-row {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 20px;
}

.dashboard-card {
  background: #ffffff;
  border-radius: 10px;
  padding: 20px 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f3f4f6;
}

.chart-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 280px;
  color: #9ca3af;
  font-size: 14px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.activity-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.activity-dot.message {
  background: #3b82f6;
}

.activity-dot.workorder {
  background: #10b981;
}

.activity-text {
  flex: 1;
  color: #374151;
}

.activity-time {
  color: #9ca3af;
  font-size: 12px;
  white-space: nowrap;
}

.empty-text {
  color: #9ca3af;
  font-size: 14px;
  text-align: center;
  padding: 40px 0;
}

.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.action-item:hover {
  background: #eff6ff;
}

.action-icon {
  font-size: 24px;
}

.action-item span:last-child {
  font-size: 12px;
  font-weight: 500;
  color: #374151;
}

@media (max-width: 1200px) {
  .overview-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .charts-row {
    grid-template-columns: 1fr;
  }
  .bottom-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .overview-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .overview-grid {
    grid-template-columns: 1fr;
  }
}
</style>
