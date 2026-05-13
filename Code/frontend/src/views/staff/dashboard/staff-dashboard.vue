<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { workOrders } from '@/data/workorder'
import { WorkOrderStatus, WorkOrderStatusMap } from '@/types/workorder'

const authStore = useAuthStore()
const router = useRouter()

const myTasks = computed(() =>
  workOrders.filter((w) => w.assignedStaffUuid === authStore.userUuid)
)

const stats = computed(() => ({
  total: myTasks.value.length,
  inProgress: myTasks.value.filter((w) => w.status === WorkOrderStatus.IN_PROGRESS).length,
  completed: myTasks.value.filter((w) => w.status === WorkOrderStatus.COMPLETED).length
}))
</script>

<template>
  <div class="staff-dashboard">
    <div class="welcome-card">
      <h3>欢迎回来，{{ authStore.username }}</h3>
      <p>以下是您的工作概览</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <span class="stat-value">{{ stats.total }}</span>
        <span class="stat-label">全部工单</span>
      </div>
      <div class="stat-card stat-progress">
        <span class="stat-value">{{ stats.inProgress }}</span>
        <span class="stat-label">处理中</span>
      </div>
      <div class="stat-card stat-done">
        <span class="stat-value">{{ stats.completed }}</span>
        <span class="stat-label">已完成</span>
      </div>
    </div>

    <div class="quick-actions">
      <el-button type="primary" @click="router.push('/staff/tasks')">查看我的工单</el-button>
      <el-button @click="router.push('/staff/profile')">个人信息</el-button>
    </div>
  </div>
</template>

<style scoped>
.welcome-card {
  background: linear-gradient(135deg, #10b981, #059669);
  color: #ffffff;
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 24px;
}

.welcome-card h3 {
  margin: 0 0 8px;
  font-size: 22px;
}

.welcome-card p {
  margin: 0;
  font-size: 14px;
  opacity: 0.85;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.stat-value {
  display: block;
  font-size: 36px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.stat-progress .stat-value { color: #3b82f6; }
.stat-done .stat-value { color: #10b981; }

.quick-actions {
  display: flex;
  gap: 12px;
}
</style>
