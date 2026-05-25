<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { messageApi } from '@/api/message'
import { MessageStatus, MessageStatusMap } from '@/types/message'
import type { MessageVO } from '@/types/message'
import { useLoading } from '@/composables/use-loading'

const inquiries = ref<MessageVO[]>([])
const { loading, start, stop } = useLoading()

async function fetchInquiries() {
  start()
  try {
    const page = await messageApi.getStaffInquiries({ size: 100 })
    inquiries.value = page.content
  } catch {
    inquiries.value = []
  } finally {
    stop()
  }
}

onMounted(() => {
  fetchInquiries()
})

const statusTagType: Record<string, string> = {
  [MessageStatus.PENDING]: 'warning',
  [MessageStatus.IN_PROGRESS]: 'primary',
  [MessageStatus.PROCESSED]: 'success'
}
</script>

<template>
  <div class="staff-inquiries">
    <div class="page-header">
      <div>
        <h3 class="section-title">我的咨询</h3>
        <p class="section-desc">管理员分配给你的客户咨询</p>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="inquiries.length === 0" class="empty-state">
      <p>暂无被分配的客户咨询</p>
    </div>

    <div v-else class="inquiry-list">
      <div v-for="item in inquiries" :key="item.messageUuid" class="inquiry-card">
        <div class="inquiry-header">
          <div class="inquiry-left">
            <h4 class="inquiry-name">{{ item.name }}</h4>
            <span class="inquiry-phone">{{ item.phone }}</span>
          </div>
          <el-tag :type="statusTagType[item.status]" size="small">
            {{ MessageStatusMap[item.status as MessageStatus] }}
          </el-tag>
        </div>

        <div class="inquiry-body">
          <p class="inquiry-content">{{ item.content }}</p>
        </div>

        <div class="inquiry-footer">
          <span class="inquiry-time">{{ item.submittedAt }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  margin-bottom: 24px;
}

.section-title { font-size: 20px; font-weight: 700; color: #111827; margin: 0 0 6px; }
.section-desc { font-size: 14px; color: #6b7280; margin: 0; }

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  color: #9ca3af;
  font-size: 15px;
}

.loading-state {
  background: #ffffff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.inquiry-list { display: flex; flex-direction: column; gap: 12px; }

.inquiry-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.inquiry-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.inquiry-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.inquiry-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.inquiry-phone {
  font-size: 13px;
  color: #6b7280;
}

.inquiry-body {
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  margin-bottom: 8px;
}

.inquiry-content {
  font-size: 14px;
  color: #374151;
  margin: 0;
  line-height: 1.5;
}

.inquiry-footer {
  display: flex;
  justify-content: flex-end;
}

.inquiry-time {
  font-size: 13px;
  color: #9ca3af;
}
</style>
