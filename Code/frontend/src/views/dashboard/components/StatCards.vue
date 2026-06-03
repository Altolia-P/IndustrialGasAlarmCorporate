<script setup lang="ts">
import { ref, computed } from 'vue'

interface StatItem {
  label: string
  value: string | number
  sub?: string
  color: string
  icon: string
}

const props = defineProps<{
  onlineCount: number
  totalCount: number
  alertCount: number
  todayDataPoints: number | string
  avgConcentration: string
  uptime: string
}>()

const emit = defineEmits<{
  refresh: []
}>()

const stats = computed<StatItem[]>(() => [
  {
    label: '在线设备',
    value: `${props.onlineCount}/${props.totalCount}`,
    sub: props.totalCount > 0 ? `${Math.round((props.onlineCount / props.totalCount) * 100)}%` : '—',
    color: '#10b981',
    icon: '🟢'
  },
  {
    label: '告警总数',
    value: props.alertCount,
    color: '#ef4444',
    icon: '🔴'
  },
  {
    label: '今日数据点',
    value: props.todayDataPoints,
    color: '#3b82f6',
    icon: '📊'
  },
  {
    label: '平均浓度',
    value: props.avgConcentration,
    color: '#f59e0b',
    icon: '📈'
  },
  {
    label: '系统运行',
    value: props.uptime,
    color: '#8b5cf6',
    icon: '⏱️'
  }
])
</script>

<template>
  <div class="stat-cards">
    <div
      v-for="s in stats"
      :key="s.label"
      class="stat-card"
      :style="{ borderTopColor: s.color }"
    >
      <div class="stat-icon" :style="{ color: s.color }">{{ s.icon }}</div>
      <div class="stat-body">
        <div class="stat-value">{{ s.value }}</div>
        <div class="stat-label">{{ s.label }}</div>
        <div v-if="s.sub" class="stat-sub">{{ s.sub }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.stat-cards {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-top: 3px solid;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: box-shadow 0.2s;
}

.stat-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  font-size: 28px;
  line-height: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #9ca3af;
  margin-top: 2px;
}

.stat-sub {
  font-size: 12px;
  color: #6b7280;
  margin-top: 2px;
}

@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
    margin-bottom: 12px;
  }
  .stat-card {
    padding: 14px;
    gap: 10px;
  }
  .stat-value {
    font-size: 20px;
  }
  .stat-icon {
    font-size: 22px;
  }
}
</style>
