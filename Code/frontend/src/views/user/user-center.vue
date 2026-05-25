<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const shortcuts = [
  { title: '提交工单', desc: '请求技术支持或售后服务', icon: '📝', path: '/user/tickets' },
  { title: '在线咨询', desc: '提交产品咨询或方案需求', icon: '💬', path: '/user/inquiries' },
  { title: '个人信息', desc: '查看账户基本信息', icon: '👤', path: '/user/profile' },
  { title: '账户设置', desc: '修改密码与通知偏好', icon: '⚙️', path: '/user/settings' }
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
  .shortcuts-grid {
    grid-template-columns: 1fr;
  }
}
</style>
