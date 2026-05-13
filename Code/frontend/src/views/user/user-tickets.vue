<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { WorkOrderType, WorkOrderStatus, WorkOrderPriority } from '@/types/workorder'
import { workOrders } from '@/data/workorder'
import type { WorkOrderVO } from '@/types/workorder'

// ---------------------------------------------------------------------------
// 本地展示枚举（保持模板兼容）
// ---------------------------------------------------------------------------

enum TicketStatus {
  COMPLETED = 'COMPLETED',
  IN_PROGRESS = 'IN_PROGRESS'
}

const TicketStatusText: Record<TicketStatus, string> = {
  [TicketStatus.COMPLETED]: '已完成',
  [TicketStatus.IN_PROGRESS]: '处理中'
}

const TicketStatusClass: Record<TicketStatus, string> = {
  [TicketStatus.COMPLETED]: 'status-done',
  [TicketStatus.IN_PROGRESS]: 'status-pending'
}

enum TicketPriority {
  URGENT = 'URGENT',
  HIGH = 'HIGH',
  NORMAL = 'NORMAL'
}

const TicketPriorityText: Record<TicketPriority, string> = {
  [TicketPriority.URGENT]: '紧急',
  [TicketPriority.HIGH]: '高',
  [TicketPriority.NORMAL]: '普通'
}

const TicketPriorityClass: Record<TicketPriority, string> = {
  [TicketPriority.URGENT]: 'pri-urgent',
  [TicketPriority.HIGH]: 'pri-high',
  [TicketPriority.NORMAL]: 'pri-normal'
}

interface TicketItem {
  id: string
  title: string
  status: TicketStatus
  priority: TicketPriority
  date: string
  handler: string
  remark: string
}

const tickets: TicketItem[] = [
  { id: 'TK-20240315-001', title: 'IS-9000系列探测器安装调试', status: TicketStatus.COMPLETED, priority: TicketPriority.HIGH, date: '2024-03-15', handler: '张工', remark: '已现场完成安装调试，设备运行正常' },
  { id: 'TK-20240308-002', title: '报警控制器通讯异常排查', status: TicketStatus.IN_PROGRESS, priority: TicketPriority.URGENT, date: '2024-03-08', handler: '李工', remark: '' },
  { id: 'TK-20240220-003', title: '传感器探头更换服务', status: TicketStatus.COMPLETED, priority: TicketPriority.NORMAL, date: '2024-02-20', handler: '王工', remark: '已完成4个传感器探头更换，2024-08校准到期' }
]

const showForm = ref(false)
const form = reactive({ title: '', type: '', priority: TicketPriority.NORMAL, description: '' })
const submitting = ref(false)

const ticketTypes = ['安装调试', '故障排查', '维修服务', '校准服务', '技术咨询']

// ---------------------------------------------------------------------------
// 工具函数
// ---------------------------------------------------------------------------

function formatDateTime(date: Date): string {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${min}`
}

function generateWorkOrderUuid(): string {
  const today = new Date()
  const dateStr =
    today.getFullYear().toString() +
    String(today.getMonth() + 1).padStart(2, '0') +
    String(today.getDate()).padStart(2, '0')
  const prefix = `TK-${dateStr}`

  const seqs = workOrders
    .map((w) => w.workOrderUuid)
    .filter((uuid) => uuid.startsWith(prefix))
    .map((uuid) => {
      const parts = uuid.split('-')
      return parseInt(parts[parts.length - 1], 10)
    })
    .filter((n) => !isNaN(n))

  const nextSeq = seqs.length > 0 ? Math.max(...seqs) + 1 : 1
  return `${prefix}-${String(nextSeq).padStart(3, '0')}`
}

const PRIORITY_MAP: Record<TicketPriority, WorkOrderPriority> = {
  [TicketPriority.NORMAL]: WorkOrderPriority.LOW,
  [TicketPriority.HIGH]: WorkOrderPriority.MEDIUM,
  [TicketPriority.URGENT]: WorkOrderPriority.HIGH
}

const TYPE_MAP: Record<string, WorkOrderType> = {
  '安装调试': WorkOrderType.TECH_SUPPORT,
  '故障排查': WorkOrderType.TECH_SUPPORT,
  '维修服务': WorkOrderType.AFTER_SALES,
  '校准服务': WorkOrderType.AFTER_SALES,
  '技术咨询': WorkOrderType.TECH_SUPPORT
}

// ---------------------------------------------------------------------------
// 提交处理
// ---------------------------------------------------------------------------

function handleSubmit() {
  if (!form.title || !form.type) {
    ElMessage.warning('请填写工单标题和类型')
    return
  }
  submitting.value = true

  const now = formatDateTime(new Date())

  const newWorkOrder: WorkOrderVO = {
    workOrderUuid: generateWorkOrderUuid(),
    title: form.title,
    type: TYPE_MAP[form.type] || WorkOrderType.TECH_SUPPORT,
    description: form.description,
    status: WorkOrderStatus.PENDING,
    priority: PRIORITY_MAP[form.priority],
    customerName: '在线用户',
    customerPhone: '未填写',
    assignedStaffUuid: '',
    assignedStaffName: '',
    resolution: '',
    createdAt: now,
    updatedAt: now
  }

  workOrders.push(newWorkOrder)

  submitting.value = false
  showForm.value = false
  form.title = ''
  form.type = ''
  form.priority = TicketPriority.NORMAL
  form.description = ''
  ElMessage.success('工单已提交，我们会尽快处理')
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
            <el-radio :value="TicketPriority.NORMAL">{{ TicketPriorityText[TicketPriority.NORMAL] }}</el-radio>
            <el-radio :value="TicketPriority.HIGH">{{ TicketPriorityText[TicketPriority.HIGH] }}</el-radio>
            <el-radio :value="TicketPriority.URGENT">{{ TicketPriorityText[TicketPriority.URGENT] }}</el-radio>
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

    <div class="ticket-list">
      <div v-if="tickets.length === 0" class="empty-state">
        <div class="empty-icon">🎫</div>
        <p class="empty-text">暂无工单记录</p>
        <el-button type="primary" size="small" @click="showForm = true">提交工单</el-button>
      </div>
      <div v-for="item in tickets" :key="item.id" class="ticket-card">
        <div class="ticket-header">
          <div class="ticket-info">
            <span class="ticket-id">{{ item.id }}</span>
            <h4 class="ticket-title">{{ item.title }}</h4>
          </div>
          <span :class="['ticket-status', TicketStatusClass[item.status]]">
            {{ TicketStatusText[item.status] }}
          </span>
        </div>
        <div class="ticket-meta">
          <span class="meta-item"><span class="meta-label">优先级：</span>
            <span :class="['priority-tag', TicketPriorityClass[item.priority]]">{{ TicketPriorityText[item.priority] }}</span>
          </span>
          <span class="meta-item"><span class="meta-label">提交日期：</span>{{ item.date }}</span>
          <span class="meta-item"><span class="meta-label">处理人：</span>{{ item.handler }}</span>
        </div>
        <div v-if="item.remark" class="ticket-remark">
          <span class="remark-label">处理备注：</span>
          <p class="remark-content">{{ item.remark }}</p>
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
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
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

.ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 12px;
}

.ticket-id {
  font-size: 12px;
  color: #9ca3af;
  font-family: monospace;
}

.ticket-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 4px 0 0;
}

.ticket-status {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 50px;
  white-space: nowrap;
}

.status-done { background: #ecfdf5; color: #10b981; }
.status-pending { background: #fef3c7; color: #d97706; }

.ticket-meta {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  font-size: 13px;
  color: #6b7280;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-label {
  color: #9ca3af;
}

.priority-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.pri-normal { background: #f3f4f6; color: #6b7280; }
.pri-high { background: #fef3c7; color: #d97706; }
.pri-urgent { background: #fef2f2; color: #dc2626; }

.ticket-remark {
  margin-top: 12px;
  padding: 12px 16px;
  background: #f9fafb;
  border-radius: 8px;
  border-left: 3px solid #3b82f6;
}

.remark-label { font-size: 13px; font-weight: 600; color: #3b82f6; }
.remark-content { font-size: 14px; color: #374151; margin: 6px 0 0; line-height: 1.6; }
</style>
