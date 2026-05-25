<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { workOrderApi } from '@/api/workorder'
import { WorkOrderStatus, WorkOrderStatusMap, WorkOrderPriority, WorkOrderPriorityMap } from '@/types/workorder'
import type { WorkOrderVO } from '@/types/workorder'
import { useLoading } from '@/composables/use-loading'

const router = useRouter()

const tasks = ref<WorkOrderVO[]>([])
const { loading, start, stop } = useLoading()

async function fetchTasks() {
  start()
  try {
    const page = await workOrderApi.getMyTasks({ size: 100 })
    tasks.value = page.content
  } catch {
    tasks.value = []
  } finally {
    stop()
  }
}

onMounted(() => {
  fetchTasks()
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

function goDetail(uuid: string) {
  router.push(`/staff/tasks/${uuid}`)
}
</script>

<template>
  <div class="staff-tasks">
    <div class="page-header">
      <div>
        <h3 class="section-title">我的工单任务</h3>
        <p class="section-desc">管理员分配的工单任务，请及时处理</p>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="tasks.length === 0" class="empty-state">
      <div class="empty-icon">📋</div>
      <p class="empty-text">暂无待处理工单任务</p>
    </div>

    <div v-else class="task-list">
      <div v-for="task in tasks" :key="task.workOrderUuid" class="task-card" @click="goDetail(task.workOrderUuid)">
        <div class="task-header">
          <div class="task-left">
            <el-tag :type="priorityTagType[task.priority]" size="small" class="task-priority">
              {{ WorkOrderPriorityMap[task.priority as WorkOrderPriority] }}
            </el-tag>
            <h4 class="task-title">{{ task.title }}</h4>
          </div>
          <el-tag :type="statusTagType[task.status]" size="small">
            {{ WorkOrderStatusMap[task.status as WorkOrderStatus] }}
          </el-tag>
        </div>

        <div class="task-meta">
          <div class="meta-row">
            <span class="meta-label">客户：</span>
            <span class="meta-value">{{ task.customerName }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">联系电话：</span>
            <span class="meta-value">{{ task.customerPhone }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">创建时间：</span>
            <span class="meta-value">{{ task.createdAt }}</span>
          </div>
        </div>

        <div class="task-desc">
          <span class="desc-label">问题描述：</span>
          <p>{{ task.description }}</p>
        </div>

        <div v-if="task.resolution" class="task-resolution">
          <span class="resolution-label">处理结果：</span>
          <p>{{ task.resolution }}</p>
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
  flex-direction: column;
  align-items: center;
  padding: 80px 24px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.empty-icon { font-size: 48px; margin-bottom: 16px; }
.empty-text { font-size: 15px; color: #9ca3af; margin: 0; }

.loading-state {
  background: #ffffff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.task-list { display: flex; flex-direction: column; gap: 16px; }

.task-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.task-card:hover { box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08); }

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.task-left { display: flex; align-items: center; gap: 10px; }
.task-priority { flex-shrink: 0; }
.task-title { font-size: 16px; font-weight: 600; color: #111827; margin: 0; }

.task-meta {
  display: flex;
  gap: 24px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.meta-row { display: flex; align-items: center; font-size: 13px; }
.meta-label { color: #9ca3af; }
.meta-value { color: #374151; font-weight: 500; }

.task-desc {
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  margin-bottom: 0;
}

.desc-label { font-size: 13px; font-weight: 600; color: #6b7280; }
.task-desc p { font-size: 14px; color: #374151; margin: 6px 0 0; line-height: 1.5; }

.task-resolution {
  margin-top: 12px;
  padding: 12px;
  background: #ecfdf5;
  border-radius: 8px;
  border-left: 3px solid #10b981;
}

.resolution-label { font-size: 13px; font-weight: 600; color: #10b981; }
.task-resolution p { font-size: 14px; color: #374151; margin: 6px 0 0; line-height: 1.5; }
</style>
