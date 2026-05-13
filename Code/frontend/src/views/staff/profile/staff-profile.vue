<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()

const form = reactive({
  phone: '138****8888',
  email: '',
  company: ''
})

const submitting = ref(false)

function handleSave() {
  submitting.value = true
  setTimeout(() => {
    submitting.value = false
    ElMessage.success('个人信息已保存')
  }, 600)
}
</script>

<template>
  <div class="staff-profile">
    <div class="profile-card">
      <h3>个人信息</h3>
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <div class="readonly-field">{{ authStore.username }}</div>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="所属公司">
          <el-input v-model="form.company" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSave">保存</el-button>
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
