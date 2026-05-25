<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { StaffRoleMap } from '@/types/staff'
import { MessageStatus, MessageStatusMap } from '@/types/message'
import { messageApi } from '@/api/message'
import { staffApi } from '@/api/staff'
import type { MessageVO } from '@/types/message'
import type { StaffVO } from '@/types/staff'
import { useLoading } from '@/composables/use-loading'
import { usePagination } from '@/composables/use-pagination'

const statusTagType: Record<string, string> = {
  [MessageStatus.PENDING]: 'warning',
  [MessageStatus.IN_PROGRESS]: 'primary',
  [MessageStatus.PROCESSED]: 'success'
}

const messages = ref<MessageVO[]>([])
const staffList = ref<StaffVO[]>([])
const { loading, start, stop } = useLoading()
const { state: pagination, backendPage, setTotal, goToPage } = usePagination()

const searchForm = ref({ name: '', status: '' })

const assignDialogVisible = ref(false)
const assignStaffUuid = ref('')
const currentUuid = ref('')

const completeDialogVisible = ref(false)
const currentRemark = ref('')

async function fetchMessages() {
  start()
  try {
    const params: Record<string, unknown> = {
      page: backendPage.value,
      size: pagination.value.size
    }
    if (searchForm.value.name) params.name = searchForm.value.name
    if (searchForm.value.status) params.status = searchForm.value.status
    const page = await messageApi.getAdminList(params as Parameters<typeof messageApi.getAdminList>[0])
    messages.value = page.content
    setTotal(page.totalElements, page.totalPages)
  } catch {
    messages.value = []
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
  fetchMessages()
  fetchStaffList()
})

function openAssign(uuid: string) {
  currentUuid.value = uuid
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
    await messageApi.assign(currentUuid.value, { staffUuid: assignStaffUuid.value, staffName: staff.name })
    assignDialogVisible.value = false
    ElMessage.success(`已安排 ${staff.name} 处理该留言`)
    await fetchMessages()
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '安排失败')
  }
}

function openComplete(uuid: string) {
  currentUuid.value = uuid
  currentRemark.value = ''
  completeDialogVisible.value = true
}

async function handleComplete() {
  if (!currentRemark.value.trim()) {
    ElMessage.warning('请填写处理备注')
    return
  }
  try {
    await messageApi.process(currentUuid.value, { remark: currentRemark.value })
    completeDialogVisible.value = false
    ElMessage.success('留言已处理完成')
    await fetchMessages()
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '操作失败')
  }
}

function handleSearch() {
  goToPage(1)
  fetchMessages()
}

function handlePageChange(page: number) {
  goToPage(page)
  fetchMessages()
}
</script>

<template>
  <div class="admin-message-list">
    <div class="search-bar">
      <div class="search-left">
        <el-input v-model="searchForm.name" placeholder="姓名" clearable style="width:180px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="searchForm.status" placeholder="状态" clearable style="width:130px" @change="handleSearch">
          <el-option label="未处理" value="PENDING" />
          <el-option label="处理中" value="IN_PROGRESS" />
          <el-option label="已处理" value="PROCESSED" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="messages.length === 0" class="empty-state">
      <p>暂无留言</p>
    </div>

    <div v-else class="table-wrapper">
      <el-table :data="messages" stripe style="width:100%">
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="content" label="留言内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="assignedStaffName" label="负责人" width="100">
          <template #default="{ row }">
            <span v-if="row.assignedStaffName">{{ row.assignedStaffName }}</span>
            <span v-else class="unassigned">待指派</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType[row.status]" size="small">{{ MessageStatusMap[row.status as MessageStatus] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="处理备注" min-width="140" show-overflow-tooltip />
        <el-table-column prop="submittedAt" label="提交时间" width="160" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" size="small" type="primary" @click="openAssign(row.messageUuid)">处理</el-button>
            <el-button v-if="row.status === 'IN_PROGRESS'" size="small" type="success" @click="openComplete(row.messageUuid)">标记完成</el-button>
            <span v-if="row.status === 'PROCESSED'" class="processed-text">已处理</span>
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

    <el-dialog v-model="assignDialogVisible" title="安排人员处理" width="440px">
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

    <el-dialog v-model="completeDialogVisible" title="处理完成" width="480px">
      <el-form label-width="80px">
        <el-form-item label="处理备注">
          <el-input v-model="currentRemark" type="textarea" :rows="4" placeholder="请输入处理备注..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleComplete">确认完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.admin-message-list {
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

.processed-text {
  color: #9ca3af;
  font-size: 13px;
}

.unassigned {
  color: #9ca3af;
  font-size: 13px;
}
</style>
