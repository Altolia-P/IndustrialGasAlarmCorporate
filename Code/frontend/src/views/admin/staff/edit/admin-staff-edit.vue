<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { StaffRole, StaffRoleMap, StaffStatus, StaffStatusMap } from '@/types/staff'
import { staffApi } from '@/api/staff'

const router = useRouter()
const route = useRoute()
const isEdit = computed(() => !!route.params.uuid)
const pageTitle = computed(() => isEdit.value ? '编辑员工' : '新增员工')

interface StaffForm {
  name: string
  phone: string
  email: string
  username: string
  password: string
  role: StaffRole
  status: StaffStatus
}

const form = reactive<StaffForm>({
  name: '',
  phone: '',
  email: '',
  username: '',
  password: '',
  role: StaffRole.FIELD_TECH,
  status: StaffStatus.STANDBY
})

const submitting = ref(false)

const RE_CHINESE = /^[\u4e00-\u9fa5]+$/
const RE_PHONE = /^1[3-9]\d{9}$/
const RE_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function filterChinese(val: string) {
  return val.replace(/[^\u4e00-\u9fa5]/g, '')
}
function filterPhone(val: string) {
  return val.replace(/[^\d]/g, '')
}

function onNameInput(val: string) {
  form.name = filterChinese(val)
}
function onPhoneInput(val: string) {
  form.phone = filterPhone(val)
}

function validate(): string | null {
  if (!form.name || !form.name.trim()) return '请输入员工姓名'
  if (!RE_CHINESE.test(form.name)) return '姓名仅支持汉字'
  if (!form.phone || !form.phone.trim()) return '请输入联系电话'
  if (!RE_PHONE.test(form.phone)) return '请输入正确的手机号码'
  if (form.email && !RE_EMAIL.test(form.email)) return '请输入正确的电子邮箱'
  if (!isEdit.value) {
    if (!form.username || !form.username.trim()) return '请输入登录账号'
    if (!/^[a-zA-Z0-9_]{4,20}$/.test(form.username)) return '账号由4-20位字母、数字或下划线组成'
    if (!form.password || !form.password.trim()) return '请输入登录密码'
    if (form.password.length < 6) return '密码长度至少6位'
  }
  return null
}

async function handleSubmit() {
  const err = validate()
  if (err) {
    ElMessage.warning(err)
    return
  }
  submitting.value = true
  try {
    if (isEdit.value) {
      await staffApi.update(route.params.uuid as string, {
        name: form.name,
        phone: form.phone,
        email: form.email,
        role: form.role,
        status: form.status
      })
    } else {
      await staffApi.create({
        name: form.name,
        phone: form.phone,
        email: form.email,
        username: form.username,
        password: form.password,
        role: form.role,
        status: form.status
      })
    }
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    router.push('/admin/staff')
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '操作失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

function handleCancel() {
  router.push('/admin/staff')
}

onMounted(async () => {
  if (isEdit.value) {
    try {
      const staff = await staffApi.getByUuid(route.params.uuid as string)
      form.name = staff.name
      form.phone = staff.phone
      form.email = staff.email
      form.role = staff.role
      form.status = staff.status
    } catch {
      ElMessage.error('获取员工信息失败')
    }
  }
})
</script>

<template>
  <div class="admin-staff-edit">
    <div class="edit-card">
      <h3 class="edit-title">{{ pageTitle }}</h3>
      <el-form :model="form" label-width="100px" class="edit-form">
        <el-form-item label="姓名" required>
          <el-input :model-value="form.name" placeholder="请输入员工姓名（汉字）" maxlength="20" show-word-limit @update:model-value="onNameInput" />
        </el-form-item>
        <el-form-item label="联系电话" required>
          <el-input :model-value="form.phone" placeholder="请输入手机号码" maxlength="11" show-word-limit @update:model-value="onPhoneInput" />
        </el-form-item>
        <el-form-item label="电子邮箱">
          <el-input v-model="form.email" placeholder="请输入电子邮箱" maxlength="50" />
        </el-form-item>
        <template v-if="!isEdit">
          <el-form-item label="登录账号" required>
            <el-input v-model="form.username" placeholder="4-20位字母、数字或下划线" maxlength="20" />
          </el-form-item>
          <el-form-item label="登录密码" required>
            <el-input v-model="form.password" type="password" placeholder="至少6位" maxlength="32" show-password />
          </el-form-item>
        </template>
        <el-form-item label="岗位分类" required>
          <el-select v-model="form.role" placeholder="请选择岗位" style="width: 100%">
            <el-option v-for="(label, key) in StaffRoleMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="工作状态" required>
          <el-radio-group v-model="form.status">
            <el-radio-button v-for="(label, key) in StaffStatusMap" :key="key" :value="key">{{ label }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">{{ pageTitle }}</el-button>
          <el-button size="large" @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.admin-staff-edit {
  display: flex;
  justify-content: center;
  padding-top: 8px;
}

.edit-card {
  width: 640px;
  background: #ffffff;
  border-radius: 8px;
  padding: 32px 40px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.edit-title {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 28px;
}

.edit-form .el-form-item:last-child {
  margin-bottom: 0;
}
</style>
