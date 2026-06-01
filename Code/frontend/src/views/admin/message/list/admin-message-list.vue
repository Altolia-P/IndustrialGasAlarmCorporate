<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { StaffRoleMap } from '@/types/staff'
import { MessageStatus, MessageStatusMap } from '@/types/message'
import { messageApi } from '@/api/message'
import { staffApi } from '@/api/staff'
import { commentApi } from '@/api/comment'
import CommentSection from '@/components/comment/CommentSection.vue'
import type { MessageVO } from '@/types/message'
import type { StaffVO } from '@/types/staff'
import { useLoading } from '@/composables/use-loading'
import { usePagination } from '@/composables/use-pagination'
import { useExport } from '@/composables/use-export'

const statusTagType: Record<string, string> = {
  [MessageStatus.PENDING]: 'warning',
  [MessageStatus.IN_PROGRESS]: 'primary',
  [MessageStatus.PROCESSED]: 'success'
}

const messages = ref<MessageVO[]>([])
const staffList = ref<StaffVO[]>([])
const { loading, start, stop } = useLoading()
const { state: pagination, backendPage, setTotal, goToPage } = usePagination()
const { exportToExcel } = useExport()

const route = useRoute()
const searchForm = ref({ name: '', status: '' })

const detailDialogVisible = ref(false)
const detailMessage = ref<MessageVO | null>(null)

// Inline assign state
const assignPopoverUuid = ref('')
const assignStaffUuid = ref('')
const staffRoleFilter = ref('')
const filteredStaffList = computed(() => {
  if (!staffRoleFilter.value) return staffList.value
  return staffList.value.filter(s => s.role === staffRoleFilter.value)
})

// Inline complete state
const completePopoverUuid = ref('')
const inlineRemark = ref('')
const completing = ref(false)

function openDetail(row: MessageVO) {
  detailMessage.value = row
  detailDialogVisible.value = true
}

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
  if (route.query.status) searchForm.value.status = route.query.status as string
  fetchMessages()
  fetchStaffList()
})

function openAssignPopover(uuid: string) {
  assignPopoverUuid.value = uuid
  assignStaffUuid.value = ''
  staffRoleFilter.value = ''
}

async function handleAssignInline(uuid: string) {
  if (!assignStaffUuid.value) return
  const staff = staffList.value.find((s) => s.staffUuid === assignStaffUuid.value)
  if (!staff) return
  try {
    await messageApi.assign(uuid, { staffUuid: assignStaffUuid.value, staffName: staff.name })
    const row = messages.value.find(m => m.messageUuid === uuid)
    if (row) {
      row.assignedStaffUuid = staff.staffUuid
      row.assignedStaffName = staff.name
      row.status = MessageStatus.IN_PROGRESS
    }
    assignPopoverUuid.value = ''
    ElMessage.success(`已安排 ${staff.name} 处理`)
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '安排失败')
  }
}

function openCompletePopover(uuid: string) {
  completePopoverUuid.value = uuid
  inlineRemark.value = ''
}

async function handleCompleteInline(uuid: string) {
  if (!inlineRemark.value.trim()) {
    ElMessage.warning('请填写处理备注')
    return
  }
  completing.value = true
  try {
    await messageApi.process(uuid, { remark: inlineRemark.value })
    const row = messages.value.find(m => m.messageUuid === uuid)
    if (row) {
      row.status = MessageStatus.PROCESSED
      row.remark = inlineRemark.value
    }
    completePopoverUuid.value = ''
    ElMessage.success('留言已处理完成')
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '操作失败')
  } finally {
    completing.value = false
  }
}

const msgExportCols = [
  { header: '姓名', key: 'name' },
  { header: '电话', key: 'phone' },
  { header: '留言内容', key: 'content' },
  { header: '状态', key: 'status' },
  { header: '负责人', key: 'assignedStaffName' },
  { header: '处理备注', key: 'remark' },
  { header: '提交时间', key: 'submittedAt' }
]

async function handleExport() {
  const params: Record<string, unknown> = {}
  if (searchForm.value.name) params.name = searchForm.value.name
  if (searchForm.value.status) params.status = searchForm.value.status
  await exportToExcel(
    (p) => messageApi.getAdminList(p as Parameters<typeof messageApi.getAdminList>[0]),
    params,
    msgExportCols,
    '留言列表'
  )
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
      <el-button @click="handleExport">导出Excel</el-button>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="messages.length === 0" class="empty-state">
      <p>暂无留言</p>
    </div>

    <div v-else class="table-wrapper">
      <el-table :data="messages" stripe style="width:100%" @row-click="openDetail">
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column label="电话" width="130">
          <template #default="{ row }">
            <el-link v-if="row.phone" type="primary" :underline="false" @click.stop="$router.push({ name: 'AdminCustomer360', query: { phone: row.phone } })">
              {{ row.phone }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="留言内容" min-width="200" show-overflow-tooltip />
        <el-table-column label="负责人" width="140">
          <template #default="{ row }">
            <template v-if="row.status === 'PROCESSED'">
              <span>{{ row.assignedStaffName || '-' }}</span>
            </template>
            <template v-else>
              <el-popover
                :visible="assignPopoverUuid === row.messageUuid"
                placement="bottom"
                :width="280"
                trigger="click"
                @show="openAssignPopover(row.messageUuid)"
                @hide="assignPopoverUuid = ''"
              >
                <template #reference>
                  <el-link type="primary" :underline="false" @click.stop>
                    {{ row.assignedStaffName || '点击指派' }}
                  </el-link>
                </template>
                <div class="inline-assign-body" @click.stop>
                  <div class="inline-assign-row">
                    <el-select v-model="staffRoleFilter" placeholder="职位筛选" clearable size="small" style="width:100%">
                      <el-option v-for="(label, key) in StaffRoleMap" :key="key" :label="label" :value="key" />
                    </el-select>
                  </div>
                  <div class="inline-assign-row">
                    <el-select v-model="assignStaffUuid" placeholder="选择负责人" size="small" style="width:100%">
                      <el-option v-for="s in filteredStaffList" :key="s.staffUuid" :label="`${s.name} — ${StaffRoleMap[s.role as keyof typeof StaffRoleMap]}`" :value="s.staffUuid" />
                    </el-select>
                  </div>
                  <div class="inline-assign-actions">
                    <el-button size="small" @click="assignPopoverUuid = ''">取消</el-button>
                    <el-button size="small" type="primary" :disabled="!assignStaffUuid" @click="handleAssignInline(row.messageUuid)">确认</el-button>
                  </div>
                </div>
              </el-popover>
            </template>
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
            <template v-if="row.status === 'IN_PROGRESS'">
              <el-popover
                :visible="completePopoverUuid === row.messageUuid"
                placement="left"
                :width="300"
                trigger="click"
                @show="openCompletePopover(row.messageUuid)"
                @hide="completePopoverUuid = ''"
              >
                <template #reference>
                  <el-button size="small" type="success" @click.stop>标记完成</el-button>
                </template>
                <div class="inline-complete-body" @click.stop>
                  <el-input
                    v-model="inlineRemark"
                    type="textarea"
                    :rows="3"
                    placeholder="处理备注..."
                    size="small"
                  />
                  <div class="inline-complete-actions">
                    <el-button size="small" @click="completePopoverUuid = ''">取消</el-button>
                    <el-button size="small" type="primary" :loading="completing" @click="handleCompleteInline(row.messageUuid)">确认完成</el-button>
                  </div>
                </div>
              </el-popover>
            </template>
            <span v-else-if="row.status === 'PROCESSED'" class="processed-text">已处理</span>
            <span v-else class="pending-hint">待指派</span>
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

    <el-dialog v-model="detailDialogVisible" title="留言详情" width="640px" @closed="detailMessage = null">
      <template v-if="detailMessage">
        <div class="detail-info">
          <div class="detail-row"><span class="detail-label">姓名：</span>{{ detailMessage.name }}</div>
          <div class="detail-row"><span class="detail-label">电话：</span>{{ detailMessage.phone }}</div>
          <div class="detail-row"><span class="detail-label">内容：</span>{{ detailMessage.content }}</div>
          <div class="detail-row"><span class="detail-label">状态：</span>
            <el-tag :type="statusTagType[detailMessage.status]" size="small">{{ MessageStatusMap[detailMessage.status as MessageStatus] }}</el-tag>
          </div>
          <div class="detail-row"><span class="detail-label">负责人：</span>{{ detailMessage.assignedStaffName || '待指派' }}</div>
          <div class="detail-row"><span class="detail-label">提交时间：</span>{{ detailMessage.submittedAt }}</div>
        </div>
        <CommentSection
          :fetch-comments="() => commentApi.getAdminMessageComments(detailMessage!.messageUuid)"
          :add-comment="(content: string) => commentApi.addAdminMessageComment(detailMessage!.messageUuid, content)"
        />
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

.pending-hint {
  color: #9ca3af;
  font-size: 13px;
}

.inline-assign-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.inline-assign-row {
  width: 100%;
}

.inline-assign-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.inline-complete-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.inline-complete-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.detail-info {
  margin-bottom: 16px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.detail-row {
  font-size: 14px;
  color: #374151;
  line-height: 1.8;
}

.detail-label {
  font-weight: 600;
  color: #6b7280;
}
</style>
