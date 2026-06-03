<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api/auth'

const authStore = useAuthStore()
const submitting = ref(false)

const form = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

async function handleChangePassword() {
  if (!form.oldPassword) {
    ElMessage.warning('请输入旧密码')
    return
  }
  if (!form.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  if (form.newPassword !== form.confirmPassword) {
    ElMessage.warning('两次新密码输入不一致')
    return
  }
  if (form.newPassword.length < 8) {
    ElMessage.warning('新密码长度不能少于8位')
    return
  }
  if (!/[0-9]/.test(form.newPassword) || !/[a-zA-Z]/.test(form.newPassword)) {
    ElMessage.warning('新密码需同时包含字母和数字')
    return
  }
  submitting.value = true
  try {
    await authApi.resetPassword({ oldPassword: form.oldPassword, newPassword: form.newPassword })
    form.oldPassword = ''
    form.newPassword = ''
    form.confirmPassword = ''
    ElMessage.success('密码修改成功')
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '密码修改失败')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="staff-settings">
    <div class="settings-card">
      <h3>账户设置</h3>
      <el-form :model="form" label-width="100px">
        <el-form-item label="旧密码">
          <el-input v-model="form.oldPassword" type="password" placeholder="请输入当前密码" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="form.newPassword" type="password" placeholder="含字母和数字，至少8位" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleChangePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.settings-card {
  max-width: 560px;
  background: #ffffff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.settings-card h3 {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 24px;
}
</style>
