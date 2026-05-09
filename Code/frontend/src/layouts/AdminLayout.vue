<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  router.push({ name: 'AdminLogin' })
}
</script>

<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="sidebar-title">后台管理</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#1a1a2e"
        text-color="#bbb"
        active-text-color="#fff"
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
        <el-menu-item index="/admin/messages">
          <span>留言管理</span>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <span class="username">{{ authStore.username }}</span>
        <el-button text @click="handleLogout">退出登录</el-button>
      </div>
    </aside>
    <div class="admin-right">
      <header class="admin-header">
        <h2>{{ $route.meta.title || '后台管理' }}</h2>
      </header>
      <main class="admin-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}
.admin-sidebar {
  width: 220px;
  background: #1a1a2e;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}
.sidebar-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  padding: 24px 20px 20px;
}
.sidebar-footer {
  margin-top: auto;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1px solid #333;
}
.username {
  color: #bbb;
  font-size: 14px;
}
.admin-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}
.admin-header {
  background: #fff;
  padding: 16px 24px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.admin-header h2 {
  margin: 0;
  font-size: 18px;
}
.admin-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
</style>
