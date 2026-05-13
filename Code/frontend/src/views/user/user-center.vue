<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const stats = [
  { label: '我的咨询', value: '0', icon: '💬' },
  { label: '我的工单', value: '0', icon: '📋' },
  { label: '未读消息', value: '0', icon: '📬' }
]

const shortcuts = [
  { title: '提交工单', desc: '请求服务', icon: '📝', path: '/user/tickets' },
  { title: '在线咨询', desc: '联系客服获取帮助', icon: '💬', path: '/user/inquiries' },
  { title: '个人信息', desc: '查看与编辑个人资料', icon: '👤', path: '/user/profile' },
  { title: '账户设置', desc: '修改密码与安全设置', icon: '⚙️', path: '/user/settings' }
]
</script>

<template>
  <div class="dashboard">
    <div class="welcome-row">
      <div>
        <h3 class="welcome-title">欢迎回来，{{ authStore.username }}</h3>
        <p class="welcome-sub">以下是您的账户概览信息</p>
      </div>
    </div>

    <div class="stats-row">
      <div v-for="s in stats" :key="s.label" class="stat-card">
        <span class="stat-icon">{{ s.icon }}</span>
        <div class="stat-body">
          <span class="stat-value">{{ s.value }}</span>
          <span class="stat-label">{{ s.label }}</span>
        </div>
      </div>
    </div>

    <h4 class="section-label">快捷入口</h4>
    <div class="shortcuts-grid">
      <div
        v-for="item in shortcuts"
        :key="item.title"
        class="shortcut-card"
        @click="$router.push(item.path)"
      >
        <span class="shortcut-icon">{{ item.icon }}</span>
        <div>
          <h4 class="shortcut-title">{{ item.title }}</h4>
          <p class="shortcut-desc">{{ item.desc }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.welcome-row {
  margin-bottom: 28px;
}

.welcome-title {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 4px;
}

.welcome-sub {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 36px;
}

.stat-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.stat-icon {
  font-size: 32px;
}

.stat-body {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
}

.section-label {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 16px;
}

.shortcuts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.shortcut-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.3s, transform 0.3s;
}

.shortcut-card:hover {
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.shortcut-icon {
  font-size: 32px;
}

.shortcut-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 4px;
}

.shortcut-desc {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
  .shortcuts-grid {
    grid-template-columns: 1fr;
  }
}
</style>
