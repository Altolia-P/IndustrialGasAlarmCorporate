<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()

const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const changingPwd = ref(false)

function changePassword() {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('请填写完整密码信息')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次新密码输入不一致')
    return
  }
  if (passwordForm.newPassword.length < 6) {
    ElMessage.warning('新密码长度不能少于6位')
    return
  }
  changingPwd.value = true
  setTimeout(() => {
    changingPwd.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    ElMessage.success('密码修改成功，请重新登录')
    authStore.logout()
  }, 800)
}

const notifications = reactive([
  { type: '工单更新', enabled: true },
  { type: '咨询回复', enabled: true },
  { type: '产品更新', enabled: false },
  { type: '系统公告', enabled: true }
])

function toggleNotification(index: number) {
  notifications[index].enabled = !notifications[index].enabled
  ElMessage.success('通知设置已更新')
}
</script>

<template>
  <div class="settings-page">
    <h3 class="section-title">账户设置</h3>
    <p class="section-desc">管理密码安全与通知偏好</p>

    <div class="form-card">
      <div class="card-section-title">修改密码</div>
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="当前密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入当前密码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="至少6位字符" />
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
