<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface MessageRow {
  messageUuid: string
  name: string
  phone: string
  content: string
  status: string
  submittedAt: string
  remark: string
}

const messages = ref<MessageRow[]>([
  { messageUuid: '1', name: '张某', phone: '138****8888', content: '想了解贵公司的气体检测仪产品，能否发一份产品手册？', status: 'PENDING', submittedAt: '2024-03-15 14:30', remark: '' },
  { messageUuid: '2', name: '李某', phone: '139****9999', content: '我们需要一套冶金行业的气体监测方案，请联系我', status: 'PENDING', submittedAt: '2024-03-15 11:20', remark: '' },
  { messageUuid: '3', name: '王某', phone: '137****7777', content: '咨询贵公司的SF6在线监测系统，用于变电站', status: 'PENDING', submittedAt: '2024-03-14 16:45', remark: '' },
  { messageUuid: '4', name: '赵某', phone: '136****6666', content: '想预约一个产品演示', status: 'PROCESSED', submittedAt: '2024-03-14 10:00', remark: '已安排3月18日下午演示' }
])

const statusMap: Record<string, { text: string; type: string }> = {
  PENDING: { text: '未处理', type: 'warning' },
  PROCESSED: { text: '已处理', type: 'success' }
}

const searchForm = ref({ name: '', status: '' })
const remarkDialogVisible = ref(false)
const currentRemark = ref('')
const currentUuid = ref('')
const currentPage = ref(1)
const total = ref(4)

function openRemark(uuid: string) {
  currentUuid.value = uuid
  currentRemark.value = ''
  remarkDialogVisible.value = true
}

function handleProcess() {
  if (!currentRemark.value) {
    ElMessage.warning('请填写处理备注')
    return
  }
  const msg = messages.value.find((m) => m.messageUuid === currentUuid.value)
  if (msg) {
    msg.status = 'PROCESSED'
    msg.remark = currentRemark.value
  }
  remarkDialogVisible.value = false
  ElMessage.success('处理成功')
}

function handleSearch() {
  currentPage.value = 1
}

function handlePageChange(page: number) {
  currentPage.value = page
}
</script>

<template>
  <div class="admin-message-list">
    <div class="search-bar">
      <div class="search-left">
        <el-input v-model="searchForm.name" placeholder="姓名" clearable style="width:180px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="searchForm.status" placeholder="状态" clearable style="width:130px" @change="handleSearch">
          <el-option label="未处理" value="PENDING" />
          <el-option label="已处理" value="PROCESSED" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
    </div>

    <div class="table-wrapper">
      <el-table :data="messages" stripe style="width:100%">
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="content" label="留言内容" min-width="220" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="处理备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="submittedAt" label="提交时间" width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" size="small" type="primary" @click="openRemark(row.messageUuid)">处理</el-button>
            <span v-else class="processed-text">已处理</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-bar">
      <el-pagination
        v-model:current-page="currentPage"
        :total="total"
        :page-size="20"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>

    <el-dialog v-model="remarkDialogVisible" title="留言处理" width="480px">
      <el-form label-width="80px">
        <el-form-item label="处理备注">
          <el-input v-model="currentRemark" type="textarea" :rows="4" placeholder="请输入处理备注..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="remarkDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleProcess">确认处理</el-button>
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

.processed-text {
  color: #9ca3af;
  font-size: 13px;
}
</style>
