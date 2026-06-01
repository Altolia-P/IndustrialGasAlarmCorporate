<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { workOrderApi } from '@/api/workorder'
import { commentApi } from '@/api/comment'
import CommentSection from '@/components/comment/CommentSection.vue'
import { WorkOrderType, WorkOrderTypeMap, WorkOrderStatus, WorkOrderStatusMap, WorkOrderPriority, WorkOrderPriorityMap } from '@/types/workorder'
import type { WorkOrderVO } from '@/types/workorder'
import { useLoading } from '@/composables/use-loading'

const router = useRouter()
const route = useRoute()

const task = ref<WorkOrderVO | null>(null)
const resolution = ref('')
const { loading: submitting, start: startSubmit, stop: stopSubmit } = useLoading()
const { loading, start, stop } = useLoading()

async function fetchTask() {
  start()
  try {
    const uuid = route.params.uuid as string
    task.value = await workOrderApi.getMyTaskByUuid(uuid)
  } catch {
    task.value = null
  } finally {
    stop()
  }
}

onMounted(() => {
  fetchTask()
})

async function handleComplete() {
  if (!resolution.value.trim()) {
    ElMessage.warning('请填写处理结果')
    return
  }
  if (!task.value) return
  startSubmit()
  try {
    await workOrderApi.completeMyTask(task.value.workOrderUuid, resolution.value)
    ElMessage.success('工单已完成')
    task.value.status = WorkOrderStatus.COMPLETED
    task.value.resolution = resolution.value
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '操作失败')
  } finally {
    stopSubmit()
  }
}

function goBack() {
  router.push('/staff/tasks')
}
</script>

<template>
  <div class="staff-task-detail">
    <div v-if="!task" class="not-found">
      <p>工单不存在或已被删除</p>
      <el-button @click="goBack">返回列表</el-button>
    </div>

    <div v-else class="detail-card">
      <div class="detail-header">
        <div>
          <h2>{{ task.title }}</h2>
          <span class="detail-type">{{ WorkOrderTypeMap[task.type as WorkOrderType] }}</span>
        </div>
        <el-tag :type="task.status === 'COMPLETED' ? 'success' : 'primary'" size="large">
          {{ WorkOrderStatusMap[task.status as WorkOrderStatus] }}
        </el-tag>
      </div>

      <div class="detail-section">
        <h4 class="section-label">客户信息</h4>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">客户名称</span>
            <span class="info-value">{{ task.customerName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">联系电话</span>
            <span class="info-value">{{ task.customerPhone }}</span>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h4 class="section-label">工单信息</h4>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">优先级</span>
            <span class="info-value">{{ WorkOrderPriorityMap[task.priority as WorkOrderPriority] }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">创建时间</span>
            <span class="info-value">{{ task.createdAt }}</span>
          </div>
          <div class="info-item full-width">
            <span class="info-label">问题描述</span>
            <div class="desc-box">{{ task.description }}</div>
          </div>
        </div>
      </div>

      <div v-if="task.resolution" class="detail-section resolution-section">
        <h4 class="section-label">处理结果</h4>
        <div class="desc-box green-bg">{{ task.resolution }}</div>
      </div>

      <div v-if="task.status !== 'COMPLETED'" class="detail-section">
        <h4 class="section-label">填写处理结果</h4>
        <el-input
          v-model="resolution"
          type="textarea"
          :rows="4"
          placeholder="请详细描述处理过程与结果..."
          maxlength="500"
          show-word-limit
        />
      </div>

      <CommentSection
        :fetch-comments="() => commentApi.getStaffWorkOrderComments(task!.workOrderUuid)"
        :add-comment="(content: string) => commentApi.addStaffWorkOrderComment(task!.workOrderUuid, content)"
      />

      <div class="detail-actions">
        <el-button
          v-if="task.status !== 'COMPLETED'"
          type="success"
          size="large"
          :loading="submitting"
          @click="handleComplete"
        >
          标记完成
        </el-button>
        <el-button size="large" @click="goBack">返回列表</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  text-align: center;
  padding: 80px 24px;
  background: #ffffff;
  border-radius: 12px;
}

.not-found p { font-size: 15px; color: #9ca3af; margin: 0 0 16px; }

.detail-card {
  max-width: 780px;
  background: #ffffff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 28px;
}

.detail-header h2 {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 6px;
}

.detail-type {
  font-size: 13px;
  color: #6b7280;
}

.detail-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f3f4f6;
}

.detail-section:last-of-type { border-bottom: none; }

.section-label {
  font-size: 15px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 12px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.info-item.full-width { grid-column: 1 / -1; }

.info-label { display: block; font-size: 12px; color: #9ca3af; margin-bottom: 4px; }
.info-value { font-size: 14px; font-weight: 500; color: #111827; }

.desc-box {
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  font-size: 14px;
  color: #374151;
  line-height: 1.6;
}

.green-bg { background: #ecfdf5; border-left: 3px solid #10b981; }

.detail-actions {
  margin-top: 8px;
  display: flex;
  gap: 12px;
}

.resolution-section { border-bottom: 1px solid #f3f4f6; }
</style>
