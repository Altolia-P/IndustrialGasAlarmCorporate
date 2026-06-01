<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deviceApi } from '@/api/device'
import { useLoading } from '@/composables/use-loading'
import { usePagination } from '@/composables/use-pagination'
import { useExport } from '@/composables/use-export'
import { DeviceStatus, DeviceStatusMap, GasTypeMap } from '@/types/device'
import type { DeviceVO } from '@/types/device'

const router = useRouter()
const { loading, start, stop } = useLoading()
const { state: pagination, backendPage, setTotal, goToPage } = usePagination()
const { exportToExcel } = useExport()

const list = ref<DeviceVO[]>([])
const searchForm = ref({
  customerUuid: '',
  model: '',
  gasType: '',
  status: ''
})

const statusTagType: Record<string, 'success' | 'warning' | 'danger' | 'info'> = {
  [DeviceStatus.NORMAL]: 'success',
  [DeviceStatus.ABNORMAL]: 'danger',
  [DeviceStatus.OFFLINE]: 'info',
  [DeviceStatus.MAINTENANCE]: 'warning'
}

async function fetchData() {
  start()
  try {
    const params: Record<string, string | number> = { page: backendPage.value, size: pagination.value.size }
    if (searchForm.value.customerUuid) params.customerUuid = searchForm.value.customerUuid
    if (searchForm.value.model) params.model = searchForm.value.model
    if (searchForm.value.gasType) params.gasType = searchForm.value.gasType
    if (searchForm.value.status) params.status = searchForm.value.status
    const page = await deviceApi.getAdminList(params)
    list.value = page.content
    setTotal(page.totalElements, page.totalPages)
  } finally {
    stop()
  }
}

function handleSearch() { pagination.value.page = 1; fetchData() }
function handlePageChange(page: number) { goToPage(page); fetchData() }
function handleCreate() { router.push({ name: 'AdminDeviceCreate' }) }
function handleEdit(uuid: string) { router.push({ name: 'AdminDeviceEdit', params: { uuid } }) }
function handleDetail(uuid: string) { router.push({ name: 'AdminDeviceDetail', params: { uuid } }) }

async function handleDelete(uuid: string, name: string) {
  try {
    await ElMessageBox.confirm(`确认删除设备「${name}」？`, '删除设备', {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deviceApi.remove(uuid)
    ElMessage.success('删除成功')
    fetchData()
  } catch { /* cancelled */ }
}

async function handleStatusAction(uuid: string, action: string) {
  try {
    switch (action) {
      case 'markAbnormal': await deviceApi.markAbnormal(uuid); break
      case 'markNormal': await deviceApi.markNormal(uuid); break
      case 'markOffline': await deviceApi.markOffline(uuid); break
      case 'startMaintenance': await deviceApi.startMaintenance(uuid); break
      case 'endMaintenance': await deviceApi.endMaintenance(uuid); break
    }
    ElMessage.success('状态已更新')
    fetchData()
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '操作失败')
  }
}

const deviceExportCols = [
  { header: '序列号', key: 'serialNumber' },
  { header: '设备名称', key: 'name' },
  { header: '型号', key: 'model' },
  { header: '客户名称', key: 'customerName' },
  { header: '客户电话', key: 'customerPhone' },
  { header: '气体类型', key: 'gasType' },
  { header: '状态', key: 'status' },
  { header: '安装位置', key: 'installLocation' },
  { header: '创建时间', key: 'createdAt' }
]

async function handleExport() {
  const params: Record<string, unknown> = {}
  if (searchForm.value.customerUuid) params.customerUuid = searchForm.value.customerUuid
  if (searchForm.value.model) params.model = searchForm.value.model
  if (searchForm.value.gasType) params.gasType = searchForm.value.gasType
  if (searchForm.value.status) params.status = searchForm.value.status
  await exportToExcel(
    (p) => deviceApi.getAdminList(p as any),
    params,
    deviceExportCols,
    '设备列表'
  )
}

onMounted(() => fetchData())
</script>

<template>
  <div class="admin-device-list">
    <div class="search-bar">
      <div class="search-left">
        <el-input v-model="searchForm.customerUuid" placeholder="客户名称" clearable style="width:180px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-input v-model="searchForm.model" placeholder="设备型号" clearable style="width:150px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="searchForm.gasType" placeholder="气体类型" clearable style="width:150px" @change="handleSearch" @clear="handleSearch">
          <el-option v-for="(label, key) in GasTypeMap" :key="key" :label="label" :value="key" />
        </el-select>
        <el-select v-model="searchForm.status" placeholder="设备状态" clearable style="width:130px" @change="handleSearch" @clear="handleSearch">
          <el-option v-for="(label, key) in DeviceStatusMap" :key="key" :label="label" :value="key" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      <el-button type="primary" @click="handleCreate">新增设备</el-button>
      <el-button @click="handleExport">导出Excel</el-button>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="list.length === 0" class="empty-state">
      <p>暂无设备数据</p>
    </div>

    <div v-else class="table-wrapper">
      <el-table :data="list" stripe style="width:100%">
        <el-table-column prop="serialNumber" label="序列号" width="140" />
        <el-table-column prop="name" label="设备名称" min-width="140">
          <template #default="{ row }">
            <el-link type="primary" @click="handleDetail(row.deviceUuid)">{{ row.name }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="model" label="型号" width="100" />
        <el-table-column label="客户名称" width="140">
          <template #default="{ row }">
            <span>{{ row.customerName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="客户电话" width="130">
          <template #default="{ row }">
            <el-link v-if="row.customerPhone" type="primary" :underline="false" @click="router.push({ name: 'AdminCustomer360', query: { phone: row.customerPhone } })">
              {{ row.customerPhone }}
            </el-link>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="气体类型" width="110">
          <template #default="{ row }">{{ GasTypeMap[row.gasType as keyof typeof GasTypeMap] || row.gasType }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType[row.status] || 'info'" size="small">{{ DeviceStatusMap[row.status as keyof typeof DeviceStatusMap] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="installLocation" label="安装位置" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row.deviceUuid)">详情</el-button>
            <el-button size="small" @click="handleEdit(row.deviceUuid)">编辑</el-button>
            <el-dropdown trigger="click" @command="(cmd: string) => handleStatusAction(row.deviceUuid, cmd)">
              <el-button size="small">状态 ▾</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="markAbnormal" v-if="row.status === DeviceStatus.NORMAL">标记为异常</el-dropdown-item>
                  <el-dropdown-item command="markNormal" v-if="row.status === DeviceStatus.ABNORMAL">恢复为正常</el-dropdown-item>
                  <el-dropdown-item command="markOffline" v-if="row.status === DeviceStatus.NORMAL">标记为离线</el-dropdown-item>
                  <el-dropdown-item command="startMaintenance" v-if="row.status !== DeviceStatus.MAINTENANCE">进入维护</el-dropdown-item>
                  <el-dropdown-item command="endMaintenance" v-if="row.status === DeviceStatus.MAINTENANCE">结束维护</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button size="small" type="danger" @click="handleDelete(row.deviceUuid, row.name)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-if="!loading && list.length > 0" class="pagination-bar">
      <el-pagination
        v-model:current-page="pagination.page"
        :total="pagination.totalElements"
        :page-size="pagination.size"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.admin-device-list { display:flex; flex-direction:column; gap:16px; }
.search-bar { display:flex; align-items:center; justify-content:space-between; padding:20px; background:#fff; border-radius:8px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
.search-left { display:flex; align-items:center; gap:12px; }
.table-wrapper { background:#fff; border-radius:8px; padding:0 0 20px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
.pagination-bar { display:flex; justify-content:flex-end; }
.loading-state { background:#fff; border-radius:8px; padding:40px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
.empty-state { display:flex; align-items:center; justify-content:center; padding:80px 24px; background:#fff; border-radius:8px; color:#9ca3af; font-size:15px; }
</style>
