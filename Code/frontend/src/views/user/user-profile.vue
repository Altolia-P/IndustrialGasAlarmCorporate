<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()

const form = reactive({
  username: authStore.username,
  phone: '138****8888',
  email: '',
  company: '深圳某某科技有限公司'
})

const editing = ref(false)
const saving = ref(false)

function startEdit() {
  editing.value = true
}

function cancelEdit() {
  editing.value = false
  form.username = authStore.username
}

function handleSave() {
  saving.value = true
  setTimeout(() => {
    saving.value = false
    editing.value = false
    ElMessage.success('个人信息已更新')
  }, 800)
}
</script>

<template>
  <div class="profile-page">
    <h3 class="section-title">个人信息</h3>
    <p class="section-desc">管理您的账户基本信息</p>

    <div class="form-card">
      <div class="form-header">
        <span class="form-label">基本信息</span>
        <el-button v-if="!editing" text type="primary" size="small" @click="startEdit">编辑</el-button>
      </div>

      <el-form :model="form" label-width="100px" :disabled="!editing">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="电子邮箱">
          <el-input v-model="form.email" placeholder="请输入电子邮箱" />
        </el-form-item>
        <el-form-item label="公司名称">
          <el-input v-model="form.company" placeholder="请输入公司名称" />
        </el-form-item>
      </el-form>

      <div v-if="editing" class="form-actions">
        <el-button @click="cancelEdit">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
      </div>
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

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f3f4f6;
}
</style>
