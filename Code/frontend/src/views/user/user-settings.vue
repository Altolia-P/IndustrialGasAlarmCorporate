<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'

const authStore = useAuthStore()

const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const changingPwd = ref(false)

async function changePassword() {
  if (!passwordForm.oldPassword) {
    ElMessage.warning('请输入旧密码')
    return
  }
  if (!passwordForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次新密码输入不一致')
    return
  }
  if (passwordForm.newPassword.length < 8) {
    ElMessage.warning('新密码长度不能少于8位')
    return
  }
  if (!/[0-9]/.test(passwordForm.newPassword) || !/[a-zA-Z]/.test(passwordForm.newPassword)) {
    ElMessage.warning('新密码需同时包含字母和数字')
    return
  }
  changingPwd.value = true
  try {
    await authApi.resetPassword({ oldPassword: passwordForm.oldPassword, newPassword: passwordForm.newPassword })
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    ElMessage.success('密码修改成功')
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '密码修改失败')
  } finally {
    changingPwd.value = false
  }
}

const NOTIFY_KEY = 'user-notify-settings'
const defaultNotifications = [
  { type: '工单更新', enabled: true },
  { type: '咨询回复', enabled: true },
  { type: '产品更新', enabled: false },
  { type: '系统公告', enabled: true }
]

const notifications = reactive(defaultNotifications.map((n, i) => {
  try {
    const saved = JSON.parse(localStorage.getItem(NOTIFY_KEY) || '[]')
    return saved[i] || n
  } catch {
    return n
  }
}))

function toggleNotification(index: number) {
  notifications[index].enabled = !notifications[index].enabled
  localStorage.setItem(NOTIFY_KEY, JSON.stringify(notifications))
  ElMessage.success('通知设置已更新')
}

onMounted(() => {
  try {
    const saved = JSON.parse(localStorage.getItem(NOTIFY_KEY) || '[]')
    if (saved.length > 0) {
      saved.forEach((s: typeof defaultNotifications[0], i: number) => {
        if (notifications[i]) notifications[i].enabled = s.enabled
      })
    }
  } catch { /* use defaults */ }
})
</script>

<template>
  <div class="settings-page">
    <h3 class="section-title">账户设置</h3>
    <p class="section-desc">管理密码安全与通知偏好</p>

    <div class="form-card">
      <div class="card-section-title">修改密码</div>
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="旧密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入当前密码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="含字母和数字，至少8位" />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="changingPwd" @click="changePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="form-card">
      <div class="card-section-title">通知设置</div>
      <div class="notify-list">
        <div v-for="(item, i) in notifications" :key="item.type" class="notify-row">
          <span class="notify-type">{{ item.type }}</span>
          <el-switch :model-value="item.enabled" @change="toggleNotification(i)" />
        </div>
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
  margin-bottom: 20px;
}

.card-section-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f3f4f6;
}

.notify-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.notify-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid #f9fafb;
}

.notify-row:last-child {
  border-bottom: none;
}

.notify-type {
  font-size: 15px;
  color: #374151;
}
</style>
