<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { workOrderApi } from '@/api/workorder'
import { WorkOrderStatus, WorkOrderStatusMap, WorkOrderPriority, WorkOrderPriorityMap } from '@/types/workorder'
import type { WorkOrderVO } from '@/types/workorder'
import { useFormSubmit } from '@/composables/use-form-submit'
import { useLoading } from '@/composables/use-loading'

const authStore = useAuthStore()

const showForm = ref(false)
const form = ref({ title: '', type: '', priority: 'LOW', description: '' })
const tickets = ref<WorkOrderVO[]>([])
const { loading, start, stop } = useLoading()

const ticketTypes = ['安装调试', '故障排查', '维修服务', '校准服务', '技术咨询']

const priorityOptions = [
  { label: '普通', value: 'LOW' },
  { label: '高', value: 'MEDIUM' },
  { label: '紧急', value: 'HIGH' }
]

const { loading: submitting, submit: doSubmit } = useFormSubmit(
  (dto: { title: string; type: string; description: string; priority: string; customerName: string; customerPhone: string }) =>
    workOrderApi.create(dto),
  {
    successMsg: '工单已提交，我们会尽快处理',
    onSuccess: () => {
      showForm.value = false
      form.value = { title: '', type: '', priority: 'LOW', description: '' }
      fetchTickets()
    }
  }
)

const TYPE_MAP: Record<string, string> = {
  '安装调试': 'TECH_SUPPORT',
  '故障排查': 'TECH_SUPPORT',
  '维修服务': 'AFTER_SALES',
  '校准服务': 'AFTER_SALES',
  '技术咨询': 'TECH_SUPPORT'
}

async function fetchTickets() {
  start()
  try {
    const page = await workOrderApi.getUserWorkOrders({ size: 100 })
    tickets.value = page.content
  } catch {
    tickets.value = []
  } finally {
    stop()
  }
}

function handleSubmit() {
  if (!form.value.title || !form.value.type) {
    ElMessage.warning('请填写工单标题和类型')
    return
  }
  doSubmit({
    title: form.value.title,
    type: TYPE_MAP[form.value.type] || 'TECH_SUPPORT',
    description: form.value.description,
    priority: form.value.priority,
    customerName: authStore.username || '在线用户',
    customerPhone: '未填写'
  })
}

onMounted(() => {
  fetchTickets()
})

const statusTagType: Record<string, string> = {
  [WorkOrderStatus.PENDING]: 'warning',
  [WorkOrderStatus.IN_PROGRESS]: 'primary',
  [WorkOrderStatus.COMPLETED]: 'success'
}

const priorityTagType: Record<string, string> = {
  [WorkOrderPriority.HIGH]: 'danger',
  [WorkOrderPriority.MEDIUM]: 'warning',
  [WorkOrderPriority.LOW]: 'info'
}
</script>

<template>
  <div class="tickets-page">
    <div class="page-header">
      <div>
        <h3 class="section-title">我的工单</h3>
        <p class="section-desc">提交技术支持工单，跟踪处理进度</p>
      </div>
      <el-button type="primary" @click="showForm = !showForm">
        {{ showForm ? '取消' : '提交工单' }}
      </el-button>
    </div>

    <div v-if="showForm" class="new-ticket-card">
      <el-form :model="form" label-width="100px">
        <el-form-item label="工单标题">
          <el-input v-model="form.title" placeholder="请简要描述问题" />
        </el-form-item>
        <el-form-item label="工单类型">
          <el-select v-model="form.type" placeholder="请选择工单类型" style="width: 100%">
            <el-option v-for="t in ticketTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-radio-group v-model="form.priority">
            <el-radio v-for="p in priorityOptions" :key="p.value" :value="p.value">{{ p.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="问题描述">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请详细描述您遇到的问题..." />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交工单</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="tickets.length === 0" class="empty-state">
      <div class="empty-icon">🎫</div>
      <p class="empty-text">暂无工单记录</p>
      <el-button type="primary" size="small" @click="showForm = true">提交工单</el-button>
    </div>

    <div v-else class="ticket-list">
      <div v-for="ticket in tickets" :key="ticket.workOrderUuid" class="ticket-card">
        <div class="ticket-header">
          <div class="ticket-left">
            <el-tag :type="priorityTagType[ticket.priority]" size="small" class="ticket-priority">
              {{ WorkOrderPriorityMap[ticket.priority as WorkOrderPriority] }}
            </el-tag>
            <h4 class="ticket-title">{{ ticket.title }}</h4>
          </div>
          <el-tag :type="statusTagType[ticket.status]" size="small">
            {{ WorkOrderStatusMap[ticket.status as WorkOrderStatus] }}
          </el-tag>
        </div>

        <div class="ticket-meta">
          <span>类型：{{ ticket.type }}</span>
          <span>创建时间：{{ ticket.createdAt }}</span>
          <span v-if="ticket.assignedStaffName">处理人：{{ ticket.assignedStaffName }}</span>
        </div>

        <div class="ticket-desc">
          <span class="desc-label">问题描述：</span>
          <p>{{ ticket.description }}</p>
        </div>

        <div v-if="ticket.resolution" class="ticket-resolution">
          <span class="resolution-label">处理结果：</span>
          <p>{{ ticket.resolution }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 6px;
}

.section-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.new-ticket-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 28px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  margin-bottom: 24px;
}

.ticket-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.ticket-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.ticket-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.ticket-priority {
  flex-shrink: 0;
}

.ticket-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.ticket-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #9ca3af;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.ticket-desc {
  padding: 10px 12px;
  background: #f9fafb;
  border-radius: 8px;
}

.desc-label {
  font-size: 13px;
  font-weight: 600;
  color: #6b7280;
}

.ticket-desc p {
  font-size: 14px;
  color: #374151;
  margin: 4px 0 0;
  line-height: 1.5;
}

.ticket-resolution {
  margin-top: 10px;
  padding: 10px 12px;
  background: #ecfdf5;
  border-radius: 8px;
  border-left: 3px solid #10b981;
}

.resolution-label {
  font-size: 13px;
  font-weight: 600;
  color: #10b981;
}

.ticket-resolution p {
  font-size: 14px;
  color: #374151;
  margin: 4px 0 0;
  line-height: 1.5;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 24px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 15px;
  color: #9ca3af;
  margin: 0 0 20px;
}

.loading-state {
  background: #ffffff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}
</style>
