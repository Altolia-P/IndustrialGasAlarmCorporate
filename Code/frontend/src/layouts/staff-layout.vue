<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const sidebarCollapsed = ref(false)

function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

function handleLogout() {
  authStore.logout()
  router.push('/')
}
</script>

<template>
  <div class="dashboard-layout">
    <aside class="dashboard-sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <router-link to="/" class="sidebar-brand">
          <div class="brand-icon">IS</div>
          <span v-if="!sidebarCollapsed" class="brand-text">InterSense</span>
        </router-link>
        <button class="collapse-btn" @click="toggleSidebar">
          {{ sidebarCollapsed ? '☰' : '✕' }}
        </button>
      </div>

      <div class="sidebar-user">
        <div class="user-avatar staff-avatar">{{ authStore.username.charAt(0).toUpperCase() }}</div>
        <div v-if="!sidebarCollapsed" class="user-info">
          <span class="user-name">{{ authStore.username }}</span>
          <span class="user-role">员工</span>
        </div>
      </div>

      <el-menu
        :default-active="$route.path"
        :collapse="sidebarCollapsed"
        router
        background-color="#1a1a2e"
        text-color="#94a3b8"
        active-text-color="#60a5fa"
      >
        <el-menu-item index="/staff">
          <span>首页概览</span>
        </el-menu-item>
        <el-menu-item index="/staff/profile">
          <span>个人信息</span>
        </el-menu-item>
        <el-menu-item index="/staff/tasks">
          <span>我的工单任务</span>
        </el-menu-item>
        <el-menu-item index="/staff/inquiries">
          <span>我的咨询</span>
        </el-menu-item>
        <el-menu-item index="/staff/settings">
          <span>账户设置</span>
        </el-menu-item>
      </el-menu>

      <div v-if="!sidebarCollapsed" class="sidebar-footer">
        <el-button text size="small" class="logout-btn" @click="handleLogout">
          退出登录
        </el-button>
      </div>
      <div v-else class="sidebar-footer-collapsed">
        <el-button text size="small" @click="handleLogout">⇥</el-button>
      </div>
    </aside>

    <div class="dashboard-right">
      <header class="dashboard-header">
        <div class="header-left">
          <router-link to="/" class="back-home">← 返回首页</router-link>
          <h2>{{ sidebarCollapsed ? authStore.username : ($route.meta.title || '员工工作台') }}</h2>
        </div>
      </header>
      <main class="dashboard-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.dashboard-layout {
  display: flex;
  min-height: 100vh;
}

.dashboard-sidebar {
  width: 240px;
  background: #1a1a2e;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  transition: width 0.25s ease;
  overflow: hidden;
}

.dashboard-sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}

.brand-icon {
  width: 32px;
  height: 32px;
  background: #3b82f6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-weight: 700;
  font-size: 13px;
  flex-shrink: 0;
}

.brand-text {
  color: #ffffff;
  font-size: 16px;
  font-weight: 700;
  white-space: nowrap;
}

.collapse-btn {
  background: rgba(255, 255, 255, 0.06);
  border: none;
  color: #94a3b8;
  font-size: 16px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: color 0.2s;
}

.collapse-btn:hover {
  color: #ffffff;
}

.sidebar-user {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.user-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #3b82f6, #1a365d);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-weight: 700;
  font-size: 16px;
  flex-shrink: 0;
}

.staff-avatar {
  background: linear-gradient(135deg, #10b981, #065f46);
}

.user-info {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 12px;
  color: #94a3b8;
}

.sidebar-footer {
  margin-top: auto;
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  justify-content: center;
}

.sidebar-footer-collapsed {
  margin-top: auto;
  padding: 8px;
  display: flex;
  justify-content: center;
}

.logout-btn {
  color: #94a3b8;
}

.dashboard-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
  min-width: 0;
}

.dashboard-header {
  background: #ffffff;
  padding: 16px 28px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-home {
  font-size: 13px;
  color: #6b7280;
  text-decoration: none;
  white-space: nowrap;
  transition: color 0.2s;
}

.back-home:hover {
  color: #3b82f6;
}

.dashboard-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.dashboard-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .dashboard-sidebar:not(.collapsed) {
    position: fixed;
    z-index: 500;
    height: 100vh;
  }
}
</style>
