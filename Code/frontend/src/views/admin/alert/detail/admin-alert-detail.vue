<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { alertApi } from '@/api/device'
import { AlertStatus, AlertStatusMap, AlertSeverityMap } from '@/types/device'
import type { AlertVO, NotificationVO } from '@/types/device'
import { useAlertActions } from '@/composables/useAlertActions'

const route = useRoute()
const router = useRouter()
const uuid = route.params.uuid as string

const alert = ref<AlertVO | null>(null)
const notifications = ref<NotificationVO[]>([])
const loading = ref(false)

const channelMap: Record<string, string> = { SMS: '短信', EMAIL: '邮件', PHONE: '电话' }
const notificationStatusMap: Record<string, string> = { PENDING: '待发送', SENT: '已发送', FAILED: '发送失败' }

const { acting, confirm, resolve, closeAlert, statusTagType } = useAlertActions(async () => {
  await fetchAlert()
})

async function fetchAlert() {
  try {
    alert.value = await alertApi.getByUuid(uuid)
  } catch {
    ElMessage.error('加载报警信息失败')
  }
}

async function fetchNotifications() {
  try {
    notifications.value = await alertApi.getNotifications(uuid)
  } catch {
    notifications.value = []
  }
}

onMounted(async () => {
  loading.value = true
  await Promise.all([fetchAlert(), fetchNotifications()])
  loading.value = false
})
</script>

<template>
  <div class="admin-alert-detail">
    <div class="page-header">
      <el-button text @click="router.push({ name: 'AdminAlerts' })">← 返回报警列表</el-button>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <template v-else-if="alert">
      <div class="info-card">
        <div class="card-header">
          <h3 class="card-title">报警详情</h3>
          <div class="header-actions">
            <el-button v-if="alert.status === AlertStatus.PENDING" type="primary" size="small" :loading="acting" @click="confirm(alert.alertUuid)">确认报警</el-button>
            <el-button v-if="alert.status === AlertStatus.CONFIRMED" type="success" size="small" :loading="acting" @click="resolve(alert.alertUuid)">标记已解决</el-button>
            <el-button v-if="alert.status !== AlertStatus.CLOSED" type="danger" size="small" :loading="acting" @click="closeAlert(alert.alertUuid)">关闭报警</el-button>
          </div>
        </div>
        <div class="info-grid">
          <div class="info-item"><span class="info-label">状态</span><span class="info-value"><el-tag :type="statusTagType(alert.status)" size="small">{{ AlertStatusMap[alert.status as keyof typeof AlertStatusMap] }}</el-tag></span></div>
          <div class="info-item"><span class="info-label">严重级别</span><span class="info-value">{{ AlertSeverityMap[alert.severity as keyof typeof AlertSeverityMap] || alert.severity }}</span></div>
          <div class="info-item"><span class="info-label">设备名称</span><span class="info-value">{{ alert.deviceName || alert.deviceUuid }}</span></div>
          <div class="info-item"><span class="info-label">序列号</span><span class="info-value">{{ alert.deviceSerialNumber || '-' }}</span></div>
          <div class="info-item"><span class="info-label">浓度</span><span class="info-value">{{ alert.concentration || '-' }}</span></div>
          <div class="info-item"><span class="info-label">阈值</span><span class="info-value">{{ alert.threshold || '-' }}</span></div>
          <div class="info-item"><span class="info-label">报警信息</span><span class="info-value">{{ alert.message || '-' }}</span></div>
          <div class="info-item"><span class="info-label">触发时间</span><span class="info-value">{{ alert.triggeredAt }}</span></div>
          <div class="info-item"><span class="info-label">确认时间</span><span class="info-value">{{ alert.confirmedAt || '-' }}</span></div>
          <div class="info-item"><span class="info-label">确认人</span><span class="info-value">{{ alert.confirmedBy || '-' }}</span></div>
          <div class="info-item"><span class="info-label">解决时间</span><span class="info-value">{{ alert.resolvedAt || '-' }}</span></div>
          <div class="info-item"><span class="info-label">解决人</span><span class="info-value">{{ alert.resolvedBy || '-' }}</span></div>
          <div class="info-item"><span class="info-label">客户名称</span><span class="info-value">{{ alert.customerName || '-' }}</span></div>
          <div class="info-item"><span class="info-label">客户电话</span><span class="info-value">{{ alert.customerPhone || '-' }}</span></div>
          <div class="info-item"><span class="info-label">关联工单</span><span class="info-value">{{ alert.workOrderUuid || '-' }}</span></div>
        </div>
      </div>

      <div class="info-card">
        <h3 class="card-title">通知记录</h3>
        <div v-if="notifications.length === 0" class="no-data">暂无通知记录</div>
        <el-table v-else :data="notifications" stripe style="width:100%;margin-top:16px">
          <el-table-column prop="recipient" label="接收人" width="160" />
          <el-table-column label="渠道" width="80">
            <template #default="{ row }">{{ channelMap[row.channel] || row.channel }}</template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template #default="{ row }">{{ notificationStatusMap[row.status] || row.status }}</template>
          </el-table-column>
          <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="sentAt" label="发送时间" width="160" />
          <el-table-column prop="retryCount" label="重试次数" width="80" />
          <el-table-column prop="errorMessage" label="错误信息" width="160" show-overflow-tooltip />
        </el-table>
      </div>
    </template>
  </div>
</template>

<style scoped>
.admin-alert-detail { display:flex; flex-direction:column; gap:16px; }
.page-header { display:flex; align-items:center; justify-content:space-between; }
.info-card { background:#fff; border-radius:8px; padding:24px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
.card-title { font-size:16px; font-weight:600; color:#1f2937; margin:0; }
.card-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px; }
.card-header .card-title { margin-bottom:0; }
.header-actions { display:flex; gap:8px; }
.info-grid { display:grid; grid-template-columns:repeat(2, 1fr); gap:12px 32px; margin-top:16px; }
.info-item { display:flex; gap:8px; }
.info-label { color:#6b7280; font-size:14px; min-width:72px; }
.info-value { color:#1f2937; font-size:14px; }
.no-data { display:flex; align-items:center; justify-content:center; padding:40px 24px; color:#9ca3af; font-size:14px; }
.loading-state { background:#fff; border-radius:8px; padding:40px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
</style>
