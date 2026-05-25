<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { WorkOrderType, WorkOrderTypeMap, WorkOrderStatus, WorkOrderStatusMap, WorkOrderPriority, WorkOrderPriorityMap } from '@/types/workorder'
import { StaffRole, StaffRoleMap, StaffStatus } from '@/types/staff'
import { workOrderApi } from '@/api/workorder'
import { staffApi } from '@/api/staff'
import type { StaffVO } from '@/types/staff'
import type { WorkOrderVO } from '@/types/workorder'
import { useLoading } from '@/composables/use-loading'

const router = useRouter()
const route = useRoute()
const isEdit = computed(() => !!route.params.uuid)
const isCreate = computed(() => !isEdit.value)
const pageTitle = computed(() => isCreate.value ? '新建工单' : '工单详情')

interface WorkOrderForm {
  title: string
  type: WorkOrderType
  description: string
  priority: WorkOrderPriority
  customerName: string
  customerPhone: string
  assignedStaffUuid: string
  assignedStaffName: string
  status: WorkOrderStatus
  resolution: string
}

const form = reactive<WorkOrderForm>({
  title: '',
  type: WorkOrderType.TECH_SUPPORT,
  description: '',
  priority: WorkOrderPriority.MEDIUM,
  customerName: '',
  customerPhone: '',
  assignedStaffUuid: '',
  assignedStaffName: '',
  status: WorkOrderStatus.PENDING,
  resolution: ''
})

const staffList = ref<StaffVO[]>([])
const { loading: submitting, start: startSubmit, stop: stopSubmit } = useLoading()
const { loading: pageLoading, start: startPage, stop: stopPage } = useLoading()

async function fetchStaffList() {
  try {
    const page = await staffApi.getAdminList({ size: 100 })
    staffList.value = page.content
  } catch {
    staffList.value = []
  }
}

async function fetchWorkOrder() {
  if (!isEdit.value) return
  startPage()
  try {
    const wo = await workOrderApi.getByUuid(route.params.uuid as string)
    form.title = wo.title
    form.type = wo.type
    form.description = wo.description
    form.priority = wo.priority
    form.customerName = wo.customerName
    form.customerPhone = wo.customerPhone
    form.assignedStaffUuid = wo.assignedStaffUuid
    form.assignedStaffName = wo.assignedStaffName
    form.status = wo.status
    form.resolution = wo.resolution
  } catch {
    ElMessage.error('加载工单失败')
  } finally {
    stopPage()
  }
}

onMounted(async () => {
  await fetchStaffList()
  const staffUuid = route.query.staffUuid as string
  const staffName = route.query.staffName as string
  if (isCreate.value && staffUuid) {
    form.assignedStaffUuid = staffUuid
    form.assignedStaffName = staffName || ''
  }
  await fetchWorkOrder()
})

function onStaffSelect(uuid: string) {
  if (!uuid) {
    form.assignedStaffUuid = ''
    form.assignedStaffName = ''
    return
  }
  const staff = staffList.value.find((s) => s.staffUuid === uuid)
  form.assignedStaffUuid = uuid
  form.assignedStaffName = staff?.name || ''
}

function validate(): string | null {
  if (!form.title || !form.title.trim()) return '请输入工单标题'
  if (!form.description || !form.description.trim()) return '请填写问题描述'
  if (!form.customerName || !form.customerName.trim()) return '请输入客户名称'
  if (!form.type) return '请选择工单类型'
  return null
}

async function handleSubmit() {
  const err = validate()
  if (err) {
    ElMessage.warning(err)
    return
  }
  startSubmit()
  try {
    const data = {
      title: form.title,
      type: form.type,
      description: form.description,
      priority: form.priority,
      customerName: form.customerName,
      customerPhone: form.customerPhone,
      assignedStaffUuid: form.assignedStaffUuid,
      assignedStaffName: form.assignedStaffName
    }
    if (isCreate.value) {
      await workOrderApi.create(data)
      ElMessage.success('创建成功')
    }
    router.push('/admin/workorders')
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '操作失败，请稍后重试')
  } finally {
    stopSubmit()
  }
}

async function handleComplete() {
  if (!form.resolution || !form.resolution.trim()) {
    ElMessage.warning('请填写处理结果')
    return
  }
  try {
    await workOrderApi.complete(route.params.uuid as string, form.resolution)
    form.status = WorkOrderStatus.COMPLETED
    ElMessage.success('工单已完成')
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '操作失败，请稍后重试')
  }
}

function handleCancel() {
  router.push('/admin/workorders')
}
</script>

<template>
  <div class="admin-workorder-edit">
    <div class="edit-card">
      <h3 class="edit-title">{{ pageTitle }}</h3>
      <el-form :model="form" label-width="100px" class="edit-form">
        <el-form-item label="工单标题" required>
          <el-input v-model="form.title" placeholder="请输入工单标题" maxlength="100" show-word-limit :disabled="isEdit" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单类型" required>
              <el-select v-model="form.type" placeholder="请选择工单类型" style="width: 100%" :disabled="isEdit">
                <el-option v-for="(label, key) in WorkOrderTypeMap" :key="key" :label="label" :value="key" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" required>
              <el-radio-group v-model="form.priority" :disabled="isEdit">
                <el-radio-button v-for="(label, key) in WorkOrderPriorityMap" :key="key" :value="key">{{ label }}</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户名称" required>
              <el-input v-model="form.customerName" placeholder="请输入客户名称" maxlength="50" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="form.customerPhone" placeholder="请输入联系电话" maxlength="20" :disabled="isEdit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="问题描述" required>
          <el-input v-model="form.description" type="textarea" :rows="5" placeholder="请详细描述问题..." maxlength="500" show-word-limit :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="指派人员">
          <el-select v-if="isCreate" v-model="form.assignedStaffUuid" placeholder="请选择负责人" clearable style="width: 100%" @change="onStaffSelect">
            <el-option v-for="s in staffList" :key="s.staffUuid" :label="`${s.name} - ${StaffRoleMap[s.role as keyof typeof StaffRoleMap]}`" :value="s.staffUuid" />
          </el-select>
          <div v-else class="readonly-field">
            {{ form.assignedStaffName || '暂未指派' }}
          </div>
        </el-form-item>
        <el-form-item v-if="isEdit && form.status !== 'COMPLETED'" label="处理结果">
          <el-input v-model="form.resolution" type="textarea" :rows="4" placeholder="填写处理结果，完成后即可归档..." maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item v-if="isEdit && form.status === 'COMPLETED'" label="处理结果">
          <div class="resolution-readonly">{{ form.resolution || '无' }}</div>
        </el-form-item>
        <el-form-item>
          <template v-if="isCreate">
            <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">创建工单</el-button>
            <el-button size="large" @click="handleCancel">取消</el-button>
          </template>
          <template v-else>
            <el-button v-if="form.status !== 'COMPLETED'" type="success" size="large" @click="handleComplete">标记完成</el-button>
            <el-button size="large" @click="handleCancel">返回</el-button>
          </template>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.admin-workorder-edit {
  display: flex;
  justify-content: center;
  padding-top: 8px;
}

.edit-card {
  width: 780px;
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

.readonly-field {
  padding: 8px 12px;
  background: #f5f7fa;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  color: #374151;
  font-size: 14px;
  line-height: 1.6;
}

.resolution-readonly {
  padding: 12px;
  background: #f9fafb;
  border-radius: 6px;
  color: #6b7280;
  font-size: 14px;
  line-height: 1.6;
}
</style>
