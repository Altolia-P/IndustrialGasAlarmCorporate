<script setup lang="ts">
import { ref, computed } from 'vue'

const props = defineProps<{
  devices: Array<{
    deviceUuid: string
    name: string
    status: string
    gasType: string
    installLocation: string
    latestConcentration: string
    customerUuid: string
    customerName: string
  }>
}>()

const customerFilter = ref('')
const statusFilter = ref('')

const customerOptions = computed(() => {
  const seen = new Set<string>()
  const opts: { label: string; value: string }[] = [{ label: '全部客户', value: '' }]
  for (const d of props.devices) {
    const key = d.customerUuid || '_none'
    if (!seen.has(key)) {
      seen.add(key)
      opts.push({ label: d.customerName || '未分配', value: d.customerUuid || '' })
    }
  }
  return opts
})

const filteredDevices = computed(() => {
  let list = props.devices
  if (customerFilter.value) list = list.filter(d => d.customerUuid === customerFilter.value)
  if (statusFilter.value) list = list.filter(d => d.status === statusFilter.value)
  return list
})

function statusColor(status: string): string {
  switch (status) {
    case 'NORMAL': return '#10b981'
    case 'ABNORMAL': return '#f59e0b'
    case 'OFFLINE': return '#ef4444'
    case 'MAINTENANCE': return '#9ca3af'
    default: return '#d1d5db'
  }
}

function statusLabel(status: string): string {
  switch (status) {
    case 'NORMAL': return '在线'
    case 'ABNORMAL': return '异常'
    case 'OFFLINE': return '离线'
    case 'MAINTENANCE': return '维护'
    default: return status
  }
}
</script>

<template>
  <div class="device-list">
    <div class="list-header">
      <h3 class="list-title">设备状态</h3>
      <div class="list-filters">
        <el-select v-model="statusFilter" placeholder="设备状态" size="small" style="width:120px" clearable>
          <el-option label="全部" value="" />
          <el-option label="在线" value="NORMAL" />
          <el-option label="异常" value="ABNORMAL" />
          <el-option label="离线" value="OFFLINE" />
          <el-option label="维护" value="MAINTENANCE" />
        </el-select>
        <el-select v-model="customerFilter" placeholder="筛选客户" size="small" style="width:160px" clearable>
          <el-option
            v-for="opt in customerOptions"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </el-select>
      </div>
    </div>
    <div class="list-scroll">
      <div v-for="d in filteredDevices" :key="d.deviceUuid" class="device-row">
        <span
          class="status-dot"
          :style="{ background: statusColor(d.status) }"
          :title="statusLabel(d.status)"
        ></span>
        <div class="device-info">
          <div class="device-name-row">
            <span class="device-name">{{ d.name }}</span>
            <span v-if="d.customerName" class="customer-tag">{{ d.customerName }}</span>
          </div>
          <span class="device-meta">{{ d.gasType || '—' }} · {{ d.installLocation || '—' }}</span>
        </div>
        <div class="device-value">
          <span class="concentration">{{ d.latestConcentration || '—' }}</span>
          <span class="unit" v-if="d.latestConcentration !== '—'">%LEL</span>
        </div>
        <span class="status-tag" :style="{ color: statusColor(d.status), background: statusColor(d.status) + '18' }">
          {{ statusLabel(d.status) }}
        </span>
      </div>
      <div v-if="filteredDevices.length === 0" class="empty">暂无设备</div>
    </div>
  </div>
</template>

<style scoped>
.device-list {
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

@media (max-width: 768px) {
  .device-list {
    padding: 14px;
  }
  .list-scroll {
    max-height: none;
  }
  .empty {
    padding: 24px 0;
  }
  .device-row {
    padding: 10px 0;
  }
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  flex-wrap: wrap;
  gap: 8px;
}

.list-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.list-filters {
  display: flex;
  gap: 8px;
}

.list-scroll {
  flex: 1;
  overflow-y: auto;
  max-height: 460px;
}

.list-scroll::-webkit-scrollbar {
  width: 5px;
}

.list-scroll::-webkit-scrollbar-thumb {
  background: #e5e7eb;
  border-radius: 3px;
}

.device-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
  transition: background 0.15s;
}

.device-row:hover {
  background: #fafafa;
  margin: 0 -12px;
  padding-left: 12px;
  padding-right: 12px;
  border-radius: 6px;
}

.device-row:last-child {
  border-bottom: none;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.status-dot[style*="NORMAL"], .status-dot[style*="ABNORMAL"] {
  animation: pulse-dot 2s infinite;
}

.device-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.device-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.device-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.customer-tag {
  font-size: 11px;
  color: #3b82f6;
  background: #eff6ff;
  padding: 1px 6px;
  border-radius: 3px;
  flex-shrink: 0;
}

.device-meta {
  font-size: 12px;
  color: #9ca3af;
}

.device-value {
  display: flex;
  align-items: baseline;
  gap: 2px;
  flex-shrink: 0;
}

.concentration {
  font-size: 15px;
  font-weight: 600;
  color: #374151;
}

.unit {
  font-size: 10px;
  color: #9ca3af;
}

.status-tag {
  font-size: 11px;
  font-weight: 500;
  flex-shrink: 0;
  min-width: 32px;
  text-align: center;
  padding: 2px 6px;
  border-radius: 4px;
}

.empty {
  text-align: center;
  color: #9ca3af;
  padding: 40px 0;
  font-size: 14px;
}
</style>
