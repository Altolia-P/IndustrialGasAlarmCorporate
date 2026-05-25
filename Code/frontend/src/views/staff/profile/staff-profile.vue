<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<template>
  <div class="staff-profile">
    <div class="profile-card">
      <h3>个人信息</h3>
      <el-form label-width="100px">
        <el-form-item label="用户名">
          <div class="readonly-field">{{ authStore.username }}</div>
        </el-form-item>
        <el-form-item label="角色">
          <div class="readonly-field">{{ authStore.isStaff ? '员工' : '未知' }}</div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.profile-card {
  max-width: 560px;
  background: #ffffff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.profile-card h3 {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 24px;
}

.readonly-field {
  padding: 8px 12px;
  background: #f5f7fa;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  color: #374151;
  font-size: 14px;
}
</style>
