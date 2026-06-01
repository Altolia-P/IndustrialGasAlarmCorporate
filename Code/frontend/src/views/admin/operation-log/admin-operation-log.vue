<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { operationLogApi } from '@/api/operation-log'
import { OperationTypeMap, TargetTypeMap } from '@/types/operation-log'
import type { OperationLogVO } from '@/types/operation-log'
import type { Page } from '@/types/common'

const loading = ref(false)
const logs = ref<OperationLogVO[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

const filterOperation = ref('')
const filterTargetType = ref('')
const filterOperator = ref('')

const operationOptions = Object.entries(OperationTypeMap).map(([value, label]) => ({ value, label }))
const targetTypeOptions = Object.entries(TargetTypeMap).map(([value, label]) => ({ value, label }))

async function fetchLogs() {
  loading.value = true
  try {
    const result: Page<OperationLogVO> = await operationLogApi.getList({
      operatorName: filterOperator.value || undefined,
      operation: filterOperation.value || undefined,
      targetType: filterTargetType.value || undefined,
      page: currentPage.value,
      size: pageSize.value
    })
    logs.value = result.content
    total.value = result.totalElements
  } catch {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetchLogs()
}

function handleReset() {
  filterOperation.value = ''
  filterTargetType.value = ''
  filterOperator.value = ''
  currentPage.value = 1
  fetchLogs()
}

function handlePageChange(page: number) {
  currentPage.value = page
  fetchLogs()
}

function getOperationLabel(op: string) {
  return OperationTypeMap[op] || op
}

function getTargetTypeLabel(t: string) {
  return TargetTypeMap[t] || t
}

onMounted(fetchLogs)
</script>

<template>
  <div class="log-page">
    <div class="page-header">
      <h3>操作日志</h3>
    </div>

    <el-card>
      <div class="filter-bar">
        <el-input v-model="filterOperator" placeholder="操作人" clearable style="width: 180px" @clear="handleSearch" />
        <el-select v-model="filterOperation" placeholder="操作类型" clearable style="width: 140px" @change="handleSearch" @clear="handleSearch">
          <el-option v-for="opt in operationOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
        </el-select>
        <el-select v-model="filterTargetType" placeholder="操作对象" clearable style="width: 140px" @change="handleSearch" @clear="handleSearch">
          <el-option v-for="opt in targetTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="logs" v-loading="loading" stripe>
        <el-table-column label="操作人" width="120" prop="operatorName" />
        <el-table-column label="操作" width="90">
          <template #default="{ row }">
            <el-tag size="small">{{ getOperationLabel(row.operation) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="对象类型" width="100">
          <template #default="{ row }">
            {{ getTargetTypeLabel(row.targetType) }}
          </template>
        </el-table-column>
        <el-table-column label="对象名称" min-width="150" prop="targetName">
          <template #default="{ row }">
            {{ row.targetName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="IP" width="130" prop="ip" />
        <el-table-column label="操作时间" width="170" prop="createdAt" />
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>

      <el-empty v-if="!loading && logs.length === 0" description="暂无操作日志" />
    </el-card>
  </div>
</template>

<style scoped>
.log-page {
  padding: 0;
}
.page-header {
  margin-bottom: 16px;
}
.page-header h3 {
  margin: 0;
  font-size: 18px;
}
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
