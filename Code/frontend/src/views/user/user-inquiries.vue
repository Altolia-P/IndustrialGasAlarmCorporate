<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { messageApi } from '@/api/message'
import { commentApi } from '@/api/comment'
import CommentSection from '@/components/comment/CommentSection.vue'
import { MessageStatus, MessageStatusMap } from '@/types/message'
import type { MessageVO } from '@/types/message'
import { useFormSubmit } from '@/composables/use-form-submit'
import { useLoading } from '@/composables/use-loading'

const authStore = useAuthStore()

const showForm = ref(false)
const form = ref({ title: '', phone: '', content: '' })
const inquiries = ref<MessageVO[]>([])
const { loading, start, stop } = useLoading()

const { loading: submitting, submit: doSubmit } = useFormSubmit(
  (dto: { name: string; phone: string; content: string }) => messageApi.submit(dto),
  {
    successMsg: '咨询已提交，我们会尽快回复您',
    onSuccess: () => {
      showForm.value = false
      form.value = { title: '', phone: '', content: '' }
      fetchInquiries()
    }
  }
)

async function fetchInquiries() {
  start()
  try {
    const page = await messageApi.getUserMessages({ size: 100 })
    inquiries.value = page.content
  } catch {
    inquiries.value = []
  } finally {
    stop()
  }
}

function handleSubmit() {
  if (!form.value.title || !form.value.phone || !form.value.content) {
    ElMessage.warning('请填写咨询标题、手机号和内容')
    return
  }
  if (!/^1\d{10}$/.test(form.value.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  doSubmit({
    name: authStore.username || '在线用户',
    phone: form.value.phone,
    content: `【${form.value.title}】${form.value.content}`
  })
}

onMounted(() => {
  fetchInquiries()
})

const statusTagType: Record<string, string> = {
  [MessageStatus.PENDING]: 'warning',
  [MessageStatus.IN_PROGRESS]: 'primary',
  [MessageStatus.PROCESSED]: 'success'
}

const detailDialogVisible = ref(false)
const detailInquiry = ref<MessageVO | null>(null)

function openDetail(item: MessageVO) {
  detailInquiry.value = item
  detailDialogVisible.value = true
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
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="详细描述">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请详细描述您的问题..." />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交咨询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="inquiries.length === 0" class="empty-state">
      <div class="empty-icon">📋</div>
      <p class="empty-text">暂无咨询记录</p>
      <el-button type="primary" size="small" @click="showForm = true">新建咨询</el-button>
    </div>

    <div v-else class="inquiry-list">
      <div v-for="item in inquiries" :key="item.messageUuid" class="inquiry-card" @click="openDetail(item)">
        <div class="inquiry-header">
          <div class="inquiry-left">
            <h4 class="inquiry-content">{{ item.content }}</h4>
          </div>
          <el-tag :type="statusTagType[item.status]" size="small">
            {{ MessageStatusMap[item.status as MessageStatus] }}
          </el-tag>
        </div>
        <div class="inquiry-meta">
          <span>{{ item.submittedAt }}</span>
          <span v-if="item.assignedStaffName">处理人：{{ item.assignedStaffName }}</span>
        </div>
        <div v-if="item.remark" class="inquiry-remark">
          <span class="remark-label">回复：</span>
          <p>{{ item.remark }}</p>
        </div>
      </div>
    </div>

    <el-dialog v-model="detailDialogVisible" title="咨询详情" width="640px" @closed="detailInquiry = null">
      <template v-if="detailInquiry">
        <div class="detail-info">
          <div class="detail-row"><span class="detail-label">内容：</span>{{ detailInquiry.content }}</div>
          <div class="detail-row"><span class="detail-label">状态：</span>
            <el-tag :type="statusTagType[detailInquiry.status]" size="small">{{ MessageStatusMap[detailInquiry.status as MessageStatus] }}</el-tag>
          </div>
          <div class="detail-row" v-if="detailInquiry.assignedStaffName"><span class="detail-label">处理人：</span>{{ detailInquiry.assignedStaffName }}</div>
          <div class="detail-row"><span class="detail-label">提交时间：</span>{{ detailInquiry.submittedAt }}</div>
          <div class="detail-row" v-if="detailInquiry.remark"><span class="detail-label">处理备注：</span>{{ detailInquiry.remark }}</div>
        </div>
        <CommentSection
          :fetch-comments="() => commentApi.getUserMessageComments(detailInquiry!.messageUuid)"
          :add-comment="(content: string) => commentApi.addUserMessageComment(detailInquiry!.messageUuid, content)"
        />
      </template>
    </el-dialog>
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
  gap: 12px;
}

.inquiry-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.inquiry-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.inquiry-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.inquiry-left {
  flex: 1;
  margin-right: 12px;
  min-width: 0;
}

.inquiry-content {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  line-height: 1.5;
}

.inquiry-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #9ca3af;
  margin-bottom: 8px;
}

.inquiry-remark {
  margin-top: 8px;
  padding: 10px 12px;
  background: #ecfdf5;
  border-radius: 8px;
  border-left: 3px solid #10b981;
}

.remark-label {
  font-size: 13px;
  font-weight: 600;
  color: #10b981;
}

.inquiry-remark p {
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

.detail-info {
  margin-bottom: 16px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.detail-row {
  font-size: 14px;
  color: #374151;
  line-height: 1.8;
}

.detail-label {
  font-weight: 600;
  color: #6b7280;
}
</style>
