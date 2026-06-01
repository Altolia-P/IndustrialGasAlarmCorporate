<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { deviceApi } from '@/api/device'
import { GasTypeMap } from '@/types/device'

const router = useRouter()
const route = useRoute()
const isEdit = ref(!!route.params.uuid)
const submitting = ref(false)

const form = reactive({
  serialNumber: '',
  name: '',
  model: '',
  customerUuid: '',
  gasType: '',
  installLocation: '',
  rangeMin: '',
  rangeMax: '',
  alertThreshold: ''
})

onMounted(async () => {
  if (isEdit.value) {
    try {
      const device = await deviceApi.getByUuid(route.params.uuid as string)
      form.serialNumber = device.serialNumber
      form.name = device.name
      form.model = device.model
      form.customerUuid = device.customerUuid
      form.gasType = device.gasType
      form.installLocation = device.installLocation || ''
      form.rangeMin = device.rangeMin || ''
      form.rangeMax = device.rangeMax || ''
      form.alertThreshold = device.alertThreshold || ''
    } catch {
      ElMessage.error('加载设备信息失败')
      router.push({ name: 'AdminDevices' })
    }
  }
})

async function handleSubmit() {
  if (!form.serialNumber || !form.name || !form.model || !form.customerUuid || !form.gasType) {
    ElMessage.warning('请填写必填字段')
    return
  }
  submitting.value = true
  try {
    const data = { ...form }
    if (isEdit.value) {
      await deviceApi.update(route.params.uuid as string, data)
      ElMessage.success('保存成功')
    } else {
      await deviceApi.create(data)
      ElMessage.success('创建成功')
    }
    router.push({ name: 'AdminDevices' })
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

function handleCancel() {
  router.push({ name: 'AdminDevices' })
}
</script>

<template>
  <div class="admin-device-edit">
    <div class="form-card">
      <h2 class="form-title">{{ isEdit ? '编辑设备' : '新增设备' }}</h2>

      <el-form :model="form" label-width="100px" class="edit-form">
        <el-form-item label="序列号" required>
          <el-input v-model="form.serialNumber" placeholder="设备序列号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="设备名称" required>
          <el-input v-model="form.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="型号" required>
          <el-input v-model="form.model" placeholder="设备型号" />
        </el-form-item>
        <el-form-item label="客户UUID" required>
          <el-input v-model="form.customerUuid" placeholder="关联客户UUID" />
        </el-form-item>
        <el-form-item label="气体类型" required>
          <el-select v-model="form.gasType" placeholder="请选择气体类型" style="width:100%">
            <el-option v-for="(label, key) in GasTypeMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="安装位置">
          <el-input v-model="form.installLocation" placeholder="安装位置" />
        </el-form-item>
        <el-form-item label="量程下限">
          <el-input v-model="form.rangeMin" placeholder="量程下限" />
        </el-form-item>
        <el-form-item label="量程上限">
          <el-input v-model="form.rangeMax" placeholder="量程上限" />
        </el-form-item>
        <el-form-item label="报警阈值">
          <el-input v-model="form.alertThreshold" placeholder="报警阈值" />
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
.admin-device-edit { max-width:800px; }
.form-card { background:#fff; border-radius:8px; padding:32px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
.form-title { font-size:20px; font-weight:600; color:#1f2937; margin:0 0 32px; padding-bottom:16px; border-bottom:1px solid #f3f4f6; }
.edit-form { max-width:640px; }
.form-actions { display:flex; gap:12px; }
</style>
