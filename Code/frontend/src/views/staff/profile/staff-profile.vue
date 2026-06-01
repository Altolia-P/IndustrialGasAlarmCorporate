<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { staffApi } from '@/api/staff'
import { StaffRoleMap, StaffStatusMap } from '@/types/staff'
import type { StaffVO } from '@/types/staff'

const authStore = useAuthStore()

const loading = ref(false)
const saving = ref(false)
const profile = ref<StaffVO | null>(null)
const form = reactive({ name: '', phone: '', email: '' })

function roleLabel(role: string) {
  return (StaffRoleMap as Record<string, string>)[role] || role
}
function statusLabel(status: string) {
  return (StaffStatusMap as Record<string, string>)[status] || status
}

async function loadProfile() {
  loading.value = true
  try {
    const data = await staffApi.getMyProfile()
    profile.value = data
    form.name = data.name
    form.phone = data.phone || ''
    form.email = data.email || ''
  } catch {
    ElMessage.error('获取员工信息失败')
  } finally {
    loading.value = false
  }
}

async function saveProfile() {
  if (!form.name || !form.phone) {
    ElMessage.warning('请输入姓名和手机号')
    return
  }
  if (!/^1\d{10}$/.test(form.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  saving.value = true
  try {
    const updated = await staffApi.updateMyProfile({ name: form.name, phone: form.phone, email: form.email })
    profile.value = updated
    ElMessage.success('资料更新成功')
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<template>
  <div class="staff-profile">
    <div class="profile-card">
      <h3>个人信息</h3>

      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="4" animated />
      </div>

      <el-form v-else label-width="100px">
        <el-form-item label="登录账号">
          <div class="readonly-field">{{ authStore.username }}</div>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="请输入姓名" maxlength="50" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱（选填）" maxlength="100" />
        </el-form-item>
        <el-form-item v-if="profile" label="岗位">
          <div class="readonly-field">{{ roleLabel(profile.role) }}</div>
        </el-form-item>
        <el-form-item v-if="profile" label="状态">
          <div class="readonly-field">{{ statusLabel(profile.status) }}</div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button>
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

.loading-state {
  padding: 16px 0;
}
</style>
