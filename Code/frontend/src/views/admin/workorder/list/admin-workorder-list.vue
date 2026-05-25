<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { WorkOrderType, WorkOrderTypeMap, WorkOrderStatus, WorkOrderStatusMap, WorkOrderPriority, WorkOrderPriorityMap } from '@/types/workorder'
import { StaffRoleMap } from '@/types/staff'
import { workOrderApi } from '@/api/workorder'
import { staffApi } from '@/api/staff'
import type { WorkOrderVO } from '@/types/workorder'
import type { StaffVO } from '@/types/staff'
import { useLoading } from '@/composables/use-loading'
import { usePagination } from '@/composables/use-pagination'

const router = useRouter()

const statusTagType: Record<string, string> = {
  [WorkOrderStatus.PENDING]: 'warning',
  [WorkOrderStatus.IN_PROGRESS]: 'primary',
  [WorkOrderStatus.COMPLETED]: 'success'
}

const priorityTagType: Record<string, string> = {
  [WorkOrderPriority.HIGH]: 'danger',
  [WorkOrderPriority.MEDIUM]: 'warning',
  [WorkOrderPriority.LOW]: 'info'
}

const workOrders = ref<WorkOrderVO[]>([])
const staffList = ref<StaffVO[]>([])
const { loading, start, stop } = useLoading()
const { state: pagination, backendPage, setTotal, goToPage } = usePagination()

const searchForm = ref({ title: '', type: '', status: '' })

const assignDialogVisible = ref(false)
const assignStaffUuid = ref('')
const currentWorkOrderUuid = ref('')

async function fetchWorkOrders() {
  start()
  try {
    const params: Record<string, unknown> = {
      page: backendPage.value,
      size: pagination.value.size
    }
    if (searchForm.value.title) params.title = searchForm.value.title
    if (searchForm.value.type) params.type = searchForm.value.type
    if (searchForm.value.status) params.status = searchForm.value.status
    const page = await workOrderApi.getAdminList(params as Parameters<typeof workOrderApi.getAdminList>[0])
    workOrders.value = page.content
    setTotal(page.totalElements, page.totalPages)
  } catch {
    workOrders.value = []
    ElMessage.error('加载失败')
  } finally {
    stop()
  }
}

async function fetchStaffList() {
  try {
    const page = await staffApi.getAdminList({ size: 100 })
    staffList.value = page.content
  } catch {
    staffList.value = []
  }
}

onMounted(() => {
  fetchWorkOrders()
  fetchStaffList()
})

function handleCreate() {
  router.push('/admin/workorders/create')
}

function handleEdit(uuid: string) {
  router.push(`/admin/workorders/${uuid}/edit`)
}

async function handleDelete(uuid: string) {
  await ElMessageBox.confirm('确认删除该工单？', '删除确认', { type: 'warning' })
  try {
    await workOrderApi.remove(uuid)
    ElMessage.success('删除成功')
    await fetchWorkOrders()
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '删除失败')
  }
}

function openAssign(uuid: string) {
  currentWorkOrderUuid.value = uuid
  assignStaffUuid.value = ''
  assignDialogVisible.value = true
}

async function handleAssign() {
  if (!assignStaffUuid.value) {
    ElMessage.warning('请选择负责人')
    return
  }
  const staff = staffList.value.find((s) => s.staffUuid === assignStaffUuid.value)
  if (!staff) return
  try {
    await workOrderApi.assign(currentWorkOrderUuid.value, assignStaffUuid.value, staff.name)
    assignDialogVisible.value = false
    ElMessage.success(`已安排 ${staff.name} 处理工单`)
    await fetchWorkOrders()
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '安排失败')
  }
}

function handleSearch() {
  goToPage(1)
  fetchWorkOrders()
}

function handlePageChange(page: number) {
  goToPage(page)
  fetchWorkOrders()
}
</script>

<template>
  <div class="admin-workorder-list">
    <div class="search-bar">
      <div class="search-left">
        <el-input v-model="searchForm.title" placeholder="工单标题" clearable style="width:220px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="searchForm.type" placeholder="工单类型" clearable style="width:140px" @change="handleSearch">
          <el-option label="技术支持" value="TECH_SUPPORT" />
          <el-option label="售后服务" value="AFTER_SALES" />
        </el-select>
        <el-select v-model="searchForm.status" placeholder="处理状态" clearable style="width:130px" @change="handleSearch">
          <el-option label="待处理" value="PENDING" />
          <el-option label="处理中" value="IN_PROGRESS" />
          <el-option label="已完成" value="COMPLETED" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      <el-button type="primary" @click="handleCreate">新建工单</el-button>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="workOrders.length === 0" class="empty-state">
      <p>暂无工单</p>
    </div>

    <div v-else class="table-wrapper">
      <el-table :data="workOrders" stripe style="width:100%">
        <el-table-column label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="priorityTagType[row.priority]" size="small">
              {{ WorkOrderPriorityMap[row.priority as WorkOrderPriority] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="工单标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            {{ WorkOrderTypeMap[row.type as WorkOrderType] }}
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="客户" width="140" />
        <el-table-column prop="assignedStaffName" label="负责人" width="100">
          <template #default="{ row }">
            <span v-if="row.assignedStaffName">{{ row.assignedStaffName }}</span>
            <span v-else class="unassigned">待指派</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType[row.status]" size="small">
              {{ WorkOrderStatusMap[row.status as WorkOrderStatus] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row.workOrderUuid)">详情</el-button>
            <el-button v-if="row.status === 'PENDING'" size="small" type="primary" @click="openAssign(row.workOrderUuid)">安排人员</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.workOrderUuid)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-bar">
      <el-pagination
        v-model:current-page="pagination.page"
        :total="pagination.totalElements"
        :page-size="pagination.size"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>

    <el-dialog v-model="assignDialogVisible" title="安排人员" width="440px">
      <el-form label-width="80px">
        <el-form-item label="负责人">
          <el-select v-model="assignStaffUuid" placeholder="请选择负责人" style="width: 100%">
            <el-option v-for="s in staffList" :key="s.staffUuid" :label="`${s.name} - ${StaffRoleMap[s.role as keyof typeof StaffRoleMap]}`" :value="s.staffUuid" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssign">确认安排</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.admin-workorder-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.search-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.table-wrapper {
  background: #ffffff;
  border-radius: 8px;
  padding: 0 0 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.pagination-bar {
  display: flex;
  justify-content: flex-end;
}

.loading-state {
  background: #ffffff;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  color: #9ca3af;
  font-size: 15px;
}

.unassigned {
  color: #9ca3af;
  font-size: 13px;
}
</style>
