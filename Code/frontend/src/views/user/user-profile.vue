<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'

const authStore = useAuthStore()

const loading = ref(false)
const saving = ref(false)
const form = reactive({ phone: '', company: '' })

function roleLabel(role: string) {
  const map: Record<string, string> = { ADMIN: '管理员', STAFF: '员工', USER: '普通用户' }
  return map[role] || role
}

async function loadProfile() {
  loading.value = true
  try {
    const profile = await authApi.getCurrentUser()
    form.phone = profile.phone || ''
    form.company = profile.company || ''
  } catch {
    // Basic info (username/role) still shown from authStore
  } finally {
    loading.value = false
  }
}

async function saveProfile() {
  if (!/^1\d{10}$/.test(form.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  saving.value = true
  try {
    await authApi.updateProfile({ phone: form.phone, company: form.company })
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
  <div class="profile-page">
    <h3 class="section-title">个人信息</h3>
    <p class="section-desc">查看和编辑您的账户基本信息</p>

    <div class="form-card">
      <div class="form-header">
        <span class="form-label">基本信息</span>
      </div>

      <el-form label-width="100px">
        <el-form-item label="用户名">
          <div class="readonly-field">{{ authStore.username }}</div>
        </el-form-item>
        <el-form-item label="角色">
          <div class="readonly-field">{{ roleLabel(authStore.role) }}</div>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="公司">
          <el-input v-model="form.company" placeholder="请输入公司名称（选填）" maxlength="100" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving || loading" @click="saveProfile">保存</el-button>
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
