<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { StaffStatus, StaffStatusMap, StaffRoleMap } from '@/types/staff'
import { staffApi } from '@/api/staff'
import type { StaffVO } from '@/types/staff'
import { useLoading } from '@/composables/use-loading'
import { usePagination } from '@/composables/use-pagination'
import { useExport } from '@/composables/use-export'

const router = useRouter()

const statusTagType: Record<string, string> = {
  [StaffStatus.WORKING]: 'success',
  [StaffStatus.STANDBY]: 'info',
  [StaffStatus.VACATION]: 'warning',
  [StaffStatus.BUSINESS_TRIP]: ''
}

const staff = ref<StaffVO[]>([])
const { loading, start, stop } = useLoading()
const { state: pagination, backendPage, setTotal, goToPage } = usePagination()
const { exportToExcel } = useExport()

const searchForm = ref({ name: '', role: '', status: '' })

async function fetchStaff() {
  start()
  try {
    const params: Record<string, unknown> = {
      page: backendPage.value,
      size: pagination.value.size
    }
    if (searchForm.value.name) params.name = searchForm.value.name
    if (searchForm.value.role) params.role = searchForm.value.role
    if (searchForm.value.status) params.status = searchForm.value.status
    const page = await staffApi.getAdminList(params)
    staff.value = page.content
    setTotal(page.totalElements, page.totalPages)
  } catch {
    staff.value = []
    ElMessage.error('加载失败')
  } finally {
    stop()
  }
}

onMounted(() => {
  fetchStaff()
})

function handleCreate() {
  router.push('/admin/staff/create')
}

function handleEdit(uuid: string) {
  router.push(`/admin/staff/${uuid}/edit`)
}

async function handleDelete(uuid: string) {
  await ElMessageBox.confirm('确认删除该员工？', '删除确认', { type: 'warning' })
  try {
    await staffApi.remove(uuid)
    ElMessage.success('删除成功')
    await fetchStaff()
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '删除失败')
  }
}

const staffExportCols = [
  { header: '姓名', key: 'name' },
  { header: '岗位', key: 'role' },
  { header: '工作状态', key: 'status' },
  { header: '联系电话', key: 'phone' },
  { header: '电子邮箱', key: 'email' },
  { header: '入职日期', key: 'createdAt' }
]

async function handleExport() {
  const params: Record<string, unknown> = {}
  if (searchForm.value.name) params.name = searchForm.value.name
  if (searchForm.value.role) params.role = searchForm.value.role
  if (searchForm.value.status) params.status = searchForm.value.status
  await exportToExcel(
    (p) => staffApi.getAdminList(p),
    params,
    staffExportCols,
    '员工列表'
  )
}

function handleSearch() {
  goToPage(1)
  fetchStaff()
}

function handlePageChange(page: number) {
  goToPage(page)
  fetchStaff()
}
</script>

<template>
  <div class="admin-staff-list">
    <div class="search-bar">
      <div class="search-left">
        <el-input v-model="searchForm.name" placeholder="员工姓名" clearable style="width:180px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="searchForm.role" placeholder="岗位分类" clearable style="width:160px" @change="handleSearch">
          <el-option v-for="(label, key) in StaffRoleMap" :key="key" :label="label" :value="key" />
        </el-select>
        <el-select v-model="searchForm.status" placeholder="工作状态" clearable style="width:130px" @change="handleSearch">
          <el-option label="休假" value="VACATION" />
          <el-option label="待班" value="STANDBY" />
          <el-option label="工作中" value="WORKING" />
          <el-option label="出差" value="BUSINESS_TRIP" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      <el-button type="primary" @click="handleCreate">新增员工</el-button>
        <el-button @click="handleExport">导出Excel</el-button>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="staff.length === 0" class="empty-state">
      <p>暂无员工</p>
    </div>

    <div v-else class="table-wrapper">
      <el-table :data="staff" stripe style="width:100%">
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column label="岗位" width="140">
          <template #default="{ row }">
            {{ StaffRoleMap[row.role as keyof typeof StaffRoleMap] }}
          </template>
        </el-table-column>
        <el-table-column label="工作状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType[row.status]" size="small">
              {{ StaffStatusMap[row.status as keyof typeof StaffStatusMap] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="email" label="电子邮箱" min-width="200" />
        <el-table-column prop="createdAt" label="入职日期" width="120" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row.staffUuid)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.staffUuid)">删除</el-button>
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
  </div>
</template>

<style scoped>
.admin-staff-list {
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
</style>
