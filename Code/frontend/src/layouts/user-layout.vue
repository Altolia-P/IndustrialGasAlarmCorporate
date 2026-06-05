<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const sidebarCollapsed = ref(false)
const mobileSidebarOpen = ref(false)

function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

function toggleMobileSidebar() {
  mobileSidebarOpen.value = !mobileSidebarOpen.value
  document.body.style.overflow = mobileSidebarOpen.value ? 'hidden' : ''
}

function closeMobileSidebar() {
  mobileSidebarOpen.value = false
  document.body.style.overflow = ''
}

watch(() => route.path, () => {
  closeMobileSidebar()
})

function handleLogout() {
  authStore.logout()
  router.push('/')
}
</script>

<template>
  <div class="dashboard-layout">
    <aside class="dashboard-sidebar" :class="{ collapsed: sidebarCollapsed, 'mobile-open': mobileSidebarOpen }">
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
        <div class="user-avatar">{{ authStore.username.charAt(0).toUpperCase() }}</div>
        <div v-if="!sidebarCollapsed" class="user-info">
          <span class="user-name">{{ authStore.username }}</span>
          <span class="user-role">普通用户</span>
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
        <el-menu-item index="/user">
          <span>首页概览</span>
        </el-menu-item>
        <el-menu-item index="/dashboard">
          <span>📊 数据大屏</span>
        </el-menu-item>
        <el-menu-item index="/user/profile">
          <span>个人信息</span>
        </el-menu-item>
        <el-menu-item index="/user/inquiries">
          <span>我的咨询</span>
        </el-menu-item>
        <el-menu-item index="/user/tickets">
          <span>我的工单</span>
        </el-menu-item>
        <el-menu-item index="/user/settings">
          <span>账户设置</span>
        </el-menu-item>
      </el-menu>

      <div v-if="!sidebarCollapsed" class="sidebar-footer">
        <el-button
          v-if="authStore.isAdmin"
          type="primary"
          size="small"
          class="admin-btn"
          @click="router.push('/admin')"
        >
          管理后台
        </el-button>
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
          <button class="mobile-sidebar-toggle" @click="toggleMobileSidebar">☰</button>
          <router-link to="/" class="back-home">← 返回首页</router-link>
          <h2>{{ sidebarCollapsed ? authStore.username : ($route.meta.title || '用户中心') }}</h2>
        </div>
      </header>
      <main class="dashboard-main">
        <router-view />
      </main>
    </div>

    <div v-if="mobileSidebarOpen" class="sidebar-overlay" @click="closeMobileSidebar"></div>
  </div>
</template>

<style scoped>
.admin-btn {
  width: 100%;
}
</style>

<style>
@import '@/styles/layout-dashboard.css';
</style>
