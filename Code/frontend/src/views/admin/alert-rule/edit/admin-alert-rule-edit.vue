<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { alertRuleApi } from '@/api/device'
import { AlertRuleTypeMap, AlertSeverityMap } from '@/types/device'

const router = useRouter()
const route = useRoute()
const isEdit = ref(!!route.params.uuid)
const submitting = ref(false)

const form = reactive({
  name: '',
  deviceUuid: '',
  ruleType: 'THRESHOLD',
  gasType: '',
  threshold: '',
  durationSeconds: 60,
  severity: 'WARNING',
  autoCreateWorkOrder: false
})

onMounted(async () => {
  if (isEdit.value) {
    try {
      const rule = await alertRuleApi.getByUuid(route.params.uuid as string)
      form.name = rule.name
      form.deviceUuid = rule.deviceUuid || ''
      form.ruleType = rule.ruleType
      form.gasType = rule.gasType || ''
      form.threshold = rule.threshold || ''
      form.durationSeconds = rule.durationSeconds
      form.severity = rule.severity
      form.autoCreateWorkOrder = rule.autoCreateWorkOrder
    } catch {
      ElMessage.error('加载规则信息失败')
      router.push({ name: 'AdminAlertRules' })
    }
  }
})

async function handleSubmit() {
  if (!form.name) {
    ElMessage.warning('请填写规则名称')
    return
  }
  submitting.value = true
  try {
    const data = {
      name: form.name,
      deviceUuid: form.deviceUuid || undefined,
      ruleType: form.ruleType,
      gasType: form.gasType || undefined,
      threshold: form.threshold || undefined,
      durationSeconds: form.durationSeconds,
      severity: form.severity,
      autoCreateWorkOrder: form.autoCreateWorkOrder
    }
    if (isEdit.value) {
      await alertRuleApi.update(route.params.uuid as string, data)
      ElMessage.success('保存成功')
    } else {
      await alertRuleApi.create(data as { name: string; ruleType: string; severity?: string })
      ElMessage.success('创建成功')
    }
    router.push({ name: 'AdminAlertRules' })
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

function handleCancel() {
  router.push({ name: 'AdminAlertRules' })
}
</script>

<template>
  <div class="admin-alert-rule-edit">
    <div class="form-card">
      <h2 class="form-title">{{ isEdit ? '编辑报警规则' : '新增报警规则' }}</h2>

      <el-form :model="form" label-width="120px" class="edit-form">
        <el-form-item label="规则名称" required>
          <el-input v-model="form.name" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="关联设备UUID">
          <el-input v-model="form.deviceUuid" placeholder="留空表示全局规则" />
        </el-form-item>
        <el-form-item label="规则类型" required>
          <el-select v-model="form.ruleType" style="width:100%">
            <el-option v-for="(label, key) in AlertRuleTypeMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="气体类型">
          <el-input v-model="form.gasType" placeholder="如 CH4, H2S，留空表示所有" />
        </el-form-item>
        <el-form-item label="阈值">
          <el-input v-model="form.threshold" placeholder="阈值超限类型必填" />
        </el-form-item>
        <el-form-item label="持续秒数">
          <el-input-number v-model="form.durationSeconds" :min="0" :step="10" />
        </el-form-item>
        <el-form-item label="严重级别">
          <el-select v-model="form.severity" style="width:100%">
            <el-option v-for="(label, key) in AlertSeverityMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="自动创建工单">
          <el-switch v-model="form.autoCreateWorkOrder" />
        </el-form-item>

        <el-form-item>
          <div class="form-actions">
            <el-button type="primary" :loading="submitting" @click="handleSubmit">
              {{ isEdit ? '保存' : '创建' }}
            </el-button>
            <el-button @click="handleCancel">取消</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.admin-alert-rule-edit { max-width:800px; }
.form-card { background:#fff; border-radius:8px; padding:32px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
.form-title { font-size:20px; font-weight:600; color:#1f2937; margin:0 0 32px; padding-bottom:16px; border-bottom:1px solid #f3f4f6; }
.edit-form { max-width:640px; }
.form-actions { display:flex; gap:12px; }
</style>
