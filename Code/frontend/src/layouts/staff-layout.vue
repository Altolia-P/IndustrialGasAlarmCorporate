<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import NotificationBell from '@/components/NotificationBell.vue'
import { staffNotifyApi } from '@/api/staff'

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
        <div class="header-right">
          <NotificationBell :api="staffNotifyApi" view-all-route="/staff/notifications" />
        </div>
      </header>
      <main class="dashboard-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.staff-avatar {
  background: linear-gradient(135deg, #10b981, #065f46);
}
</style>

<style>
@import '@/styles/layout-dashboard.css';
</style>
