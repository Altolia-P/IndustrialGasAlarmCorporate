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
        <div class="user-avatar admin-avatar">{{ authStore.username.charAt(0).toUpperCase() }}</div>
        <div v-if="!sidebarCollapsed" class="user-info">
          <span class="user-name">{{ authStore.username }}</span>
          <span class="user-role">管理员</span>
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
        <el-menu-item index="/admin">
          <span>首页概览</span>
        </el-menu-item>
        <el-menu-item index="/admin/products">
          <span>产品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/contents">
          <span>内容管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/news">
          <span>新闻管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/messages">
          <span>留言管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/staff">
          <span>员工管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/workorders">
          <span>工单管理</span>
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
          <h2>{{ sidebarCollapsed ? authStore.username : ($route.meta.title || '后台管理') }}</h2>
        </div>
      </header>
      <main class="dashboard-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-avatar {
  background: linear-gradient(135deg, var(--color-primary), var(--color-brand-dark));
}
</style>

<style>
@import '@/styles/layout-dashboard.css';
</style>
