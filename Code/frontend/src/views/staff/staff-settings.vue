<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const submitting = ref(false)

const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

function handleChangePassword() {
  if (!form.value.oldPassword || !form.value.newPassword) {
    ElMessage.warning('请填写完整密码信息')
    return
  }
  if (form.value.newPassword !== form.value.confirmPassword) {
    ElMessage.warning('两次新密码输入不一致')
    return
  }
  if (form.value.newPassword.length < 6) {
    ElMessage.warning('新密码长度不能少于6位')
    return
  }
  submitting.value = true
  setTimeout(() => {
    submitting.value = false
    form.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    ElMessage.success('密码修改成功')
  }, 600)
}
</script>

<template>
  <div class="staff-settings">
    <div class="settings-card">
      <h3>账户设置</h3>
      <el-form :model="form" label-width="100px">
        <el-form-item label="原密码">
          <el-input v-model="form.oldPassword" type="password" placeholder="请输入原密码" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码（至少6位）" show-password />
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
