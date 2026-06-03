<script setup lang="ts">
import { ref, computed } from 'vue'

const props = defineProps<{
  alerts: Array<{
    alertUuid: string
    deviceUuid: string
    severity: string
    message: string
    concentration: string
    triggeredAt: string
  }>
}>()

const severityFilter = ref('')

const severityOptions = [
  { label: '全部', value: '' },
  { label: '严重', value: 'CRITICAL' },
  { label: '警告', value: 'WARNING' },
  { label: '提示', value: 'INFO' }
]

const filteredAlerts = computed(() => {
  if (!severityFilter.value) return props.alerts
  return props.alerts.filter(a => a.severity === severityFilter.value)
})

function severityTag(severity: string): { label: string; color: string; bg: string } {
  switch (severity) {
    case 'CRITICAL': return { label: '严重', color: '#ef4444', bg: '#fef2f2' }
    case 'WARNING': return { label: '警告', color: '#f59e0b', bg: '#fffbeb' }
    case 'INFO': return { label: '提示', color: '#3b82f6', bg: '#eff6ff' }
    default: return { label: severity, color: '#6b7280', bg: '#f9fafb' }
  }
}
</script>

<template>
  <div class="alert-feed">
    <div class="feed-header">
      <h3 class="feed-title">最近告警</h3>
      <el-select v-model="severityFilter" placeholder="严重程度" size="small" style="width:120px" clearable>
        <el-option
          v-for="opt in severityOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
    </div>
    <div class="feed-scroll">
      <div
        v-for="a in filteredAlerts"
        :key="a.alertUuid"
        class="alert-item"
        :style="{ borderLeftColor: severityTag(a.severity).color }"
      >
        <div class="alert-header">
          <span
            class="severity-badge"
            :style="{ background: severityTag(a.severity).bg, color: severityTag(a.severity).color }"
          >
            {{ severityTag(a.severity).label }}
          </span>
          <span class="alert-time">{{ a.triggeredAt }}</span>
        </div>
        <div class="alert-msg">{{ a.message }}</div>
        <div class="alert-conc">{{ a.concentration }} %LEL</div>
      </div>
      <div v-if="filteredAlerts.length === 0" class="empty">{{ severityFilter ? '无匹配告警' : '暂无告警' }}</div>
    </div>
  </div>
</template>

<style scoped>
.alert-feed {
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

@media (max-width: 768px) {
  .alert-feed {
    padding: 14px;
  }
  .feed-scroll {
    max-height: none;
  }
  .empty {
    padding: 24px 0;
  }
  .alert-item {
    padding: 10px 12px;
    margin-bottom: 6px;
  }
}

.feed-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.feed-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.feed-scroll {
  flex: 1;
  overflow-y: auto;
  max-height: 340px;
}

.feed-scroll::-webkit-scrollbar {
  width: 5px;
}

.feed-scroll::-webkit-scrollbar-thumb {
  background: #e5e7eb;
  border-radius: 3px;
}

.alert-item {
  padding: 12px 14px;
  border-left: 3px solid;
  margin-bottom: 8px;
  background: #fafafa;
  border-radius: 0 8px 8px 0;
  transition: background 0.15s;
}

.alert-item:hover {
  background: #f3f4f6;
}

.alert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.severity-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 4px;
}

.alert-time {
  font-size: 11px;
  color: #9ca3af;
}

.alert-msg {
  font-size: 13px;
  color: #374151;
  margin-bottom: 4px;
  line-height: 1.4;
}

.alert-conc {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.empty {
  text-align: center;
  color: #9ca3af;
  padding: 40px 0;
  font-size: 14px;
}
</style>
