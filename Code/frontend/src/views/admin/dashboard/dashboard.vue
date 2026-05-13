<script setup lang="ts">
import { ref } from 'vue'

const stats = [
  { label: '产品总数', value: '16', color: '#3b82f6', icon: '📦' },
  { label: '内容总数', value: '12', color: '#10b981', icon: '📄' },
  { label: '待处理留言', value: '3', color: '#f59e0b', icon: '💬' },
  { label: '今日访问', value: '1,256', color: '#8b5cf6', icon: '👁️' }
]

const recentMessages = [
  { id: 1, name: '张某', company: '某石化公司', phone: '138****8888', content: '想了解你们的气体检测仪产品...', time: '2024-03-15 14:30' },
  { id: 2, name: '李某', company: '某钢铁集团', phone: '139****9999', content: '需要冶金行业的气体监测方案...', time: '2024-03-15 11:20' },
  { id: 3, name: '王某', company: '某电力公司', phone: '137****7777', content: '咨询SF6在线监测系统...', time: '2024-03-14 16:45' }
]
</script>

<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div v-for="stat in stats" :key="stat.label" class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <span class="stat-label">{{ stat.label }}</span>
            <span class="stat-value">{{ stat.value }}</span>
          </div>
          <div class="stat-icon" :style="{ background: stat.color + '15', color: stat.color }">
            {{ stat.icon }}
          </div>
        </div>
      </div>
    </div>

    <div class="dashboard-grid">
      <div class="dashboard-card">
        <h3 class="card-title">最近留言</h3>
        <div class="message-list">
          <div v-for="msg in recentMessages" :key="msg.id" class="message-item">
            <div class="message-header">
              <span class="message-name">{{ msg.name }}</span>
              <span class="message-company">{{ msg.company }}</span>
              <span class="message-time">{{ msg.time }}</span>
            </div>
            <p class="message-content">{{ msg.content }}</p>
          </div>
        </div>
      </div>

      <div class="dashboard-card">
        <h3 class="card-title">快捷操作</h3>
        <div class="quick-actions">
          <div class="action-item" @click="$router.push('/admin/products/create')">
            <span class="action-icon">➕</span>
            <span>新增产品</span>
          </div>
          <div class="action-item" @click="$router.push('/admin/contents/create')">
            <span class="action-icon">📝</span>
            <span>新增内容</span>
          </div>
          <div class="action-item" @click="$router.push('/admin/products')">
            <span class="action-icon">📦</span>
            <span>管理产品</span>
          </div>
          <div class="action-item" @click="$router.push('/admin/messages')">
            <span class="action-icon">💬</span>
            <span>查看留言</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 14px;
  color: #9ca3af;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 20px;
}

.dashboard-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.message-name {
  font-weight: 600;
  font-size: 14px;
  color: #1f2937;
}

.message-company {
  font-size: 13px;
  color: #6b7280;
}

.message-time {
  font-size: 12px;
  color: #9ca3af;
  margin-left: auto;
}

.message-content {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  background: #f9fafb;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-item:hover {
  background: #eff6ff;
  transform: translateY(-2px);
}

.action-icon {
  font-size: 28px;
}

.action-item span:last-child {
  font-size: 13px;
  font-weight: 500;
  color: #374151;
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  .quick-actions {
    grid-template-columns: 1fr;
  }
}
</style>
