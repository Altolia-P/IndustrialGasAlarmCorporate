<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<template>
  <div class="profile-page">
    <h3 class="section-title">个人信息</h3>
    <p class="section-desc">您的账户基本信息</p>

    <div class="form-card">
      <div class="form-header">
        <span class="form-label">基本信息</span>
      </div>

      <el-form label-width="100px">
        <el-form-item label="用户名">
          <div class="readonly-field">{{ authStore.username }}</div>
        </el-form-item>
        <el-form-item label="角色">
          <div class="readonly-field">普通用户</div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 6px;
}

.section-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 28px;
}

.form-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 28px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  max-width: 640px;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.form-label {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
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
