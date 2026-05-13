<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { MessageStatus } from '@/types/message'
import { messages } from '@/data/workorder'
import type { MessageVO } from '@/types/message'

enum InquiryStatus {
  REPLIED = 'REPLIED',
  PENDING = 'PENDING'
}

const InquiryStatusText: Record<InquiryStatus, string> = {
  [InquiryStatus.REPLIED]: '已回复',
  [InquiryStatus.PENDING]: '处理中'
}

const InquiryStatusClass: Record<InquiryStatus, string> = {
  [InquiryStatus.REPLIED]: 'status-done',
  [InquiryStatus.PENDING]: 'status-pending'
}

interface InquiryItem {
  id: number
  title: string
  status: InquiryStatus
  date: string
  reply: string
}

const inquiries: InquiryItem[] = [
  { id: 1, title: '关于气体探测器选型咨询', status: InquiryStatus.REPLIED, date: '2024-03-15', reply: '您好，感谢您的咨询。针对您提到的炼化项目场景，我们推荐IS-9000系列...' },
  { id: 2, title: '固定式探测器安装方案咨询', status: InquiryStatus.PENDING, date: '2024-03-08', reply: '' },
  { id: 3, title: '传感器校准周期咨询', status: InquiryStatus.REPLIED, date: '2024-02-20', reply: '根据使用环境和频率，建议每6-12个月进行一次校准...' }
]

const showForm = ref(false)
const form = reactive({ title: '', content: '' })
const submitting = ref(false)

function formatDateTime(date: Date): string {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${min}`
}

function generateMessageUuid(): string {
  const today = new Date()
  const dateStr =
    today.getFullYear().toString() +
    String(today.getMonth() + 1).padStart(2, '0') +
    String(today.getDate()).padStart(2, '0')
  const prefix = `MSG-${dateStr}`

  const seqs = messages
    .map((m) => m.messageUuid)
    .filter((uuid) => uuid.startsWith(prefix))
    .map((uuid) => {
      const parts = uuid.split('-')
      return parseInt(parts[parts.length - 1], 10)
    })
    .filter((n) => !isNaN(n))

  const nextSeq = seqs.length > 0 ? Math.max(...seqs) + 1 : 1
  return `${prefix}-${String(nextSeq).padStart(3, '0')}`
}

function handleSubmit() {
  if (!form.title || !form.content) {
    ElMessage.warning('请填写咨询标题和内容')
    return
  }
  submitting.value = true

  const now = formatDateTime(new Date())

  const newMessage: MessageVO = {
    messageUuid: generateMessageUuid(),
    name: '在线用户',
    phone: '未填写',
    content: `【${form.title}】${form.content}`,
    status: MessageStatus.PENDING,
    assignedStaffUuid: '',
    assignedStaffName: '',
    submittedAt: now,
    remark: ''
  }

  messages.push(newMessage)

  submitting.value = false
  showForm.value = false
  form.title = ''
  form.content = ''
  ElMessage.success('咨询已提交，我们会尽快回复您')
}
</script>

<template>
  <div class="inquiries-page">
    <div class="page-header">
      <div>
        <h3 class="section-title">我的咨询</h3>
        <p class="section-desc">查看您提交过的咨询记录及回复</p>
      </div>
      <el-button type="primary" @click="showForm = !showForm">
        {{ showForm ? '取消' : '新建咨询' }}
      </el-button>
    </div>

    <div v-if="showForm" class="new-inquiry-card">
      <el-form :model="form" label-width="80px">
        <el-form-item label="咨询标题">
          <el-input v-model="form.title" placeholder="请简要概括您的问题" />
        </el-form-item>
        <el-form-item label="详细描述">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请详细描述您的问题..." />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交咨询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="inquiry-list">
      <div v-if="inquiries.length === 0" class="empty-state">
        <div class="empty-icon">📋</div>
        <p class="empty-text">暂无咨询记录</p>
        <el-button type="primary" size="small" @click="showForm = true">新建咨询</el-button>
      </div>
      <div v-for="item in inquiries" :key="item.id" class="inquiry-card">
        <div class="inquiry-header">
          <div class="inquiry-info">
            <h4 class="inquiry-title">{{ item.title }}</h4>
            <span class="inquiry-date">{{ item.date }}</span>
          </div>
          <span :class="['inquiry-status', InquiryStatusClass[item.status]]">
            {{ InquiryStatusText[item.status] }}
          </span>
        </div>
        <div v-if="item.reply" class="inquiry-reply">
          <span class="reply-label">客服回复：</span>
          <p class="reply-content">{{ item.reply }}</p>
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

.new-inquiry-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 28px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  margin-bottom: 24px;
}

.inquiry-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.inquiry-card {
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

.inquiry-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.inquiry-info {
  flex: 1;
}

.inquiry-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 6px;
}

.inquiry-date {
  font-size: 13px;
  color: #9ca3af;
}

.inquiry-status {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 50px;
  white-space: nowrap;
}

.status-done {
  background: #ecfdf5;
  color: #10b981;
}

.status-pending {
  background: #fef3c7;
  color: #d97706;
}

.inquiry-reply {
  margin-top: 16px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
  border-left: 3px solid #3b82f6;
}

.reply-label {
  font-size: 13px;
  font-weight: 600;
  color: #3b82f6;
}

.reply-content {
  font-size: 14px;
  color: #374151;
  margin: 8px 0 0;
  line-height: 1.6;
}
</style>
