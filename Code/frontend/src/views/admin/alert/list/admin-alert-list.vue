<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { alertApi } from '@/api/device'
import { AlertStatus, AlertStatusMap, AlertSeverityMap } from '@/types/device'
import type { AlertVO } from '@/types/device'
import { useLoading } from '@/composables/use-loading'
import { usePagination } from '@/composables/use-pagination'
import { useExport } from '@/composables/use-export'
import { useAlertActions } from '@/composables/useAlertActions'

const router = useRouter()
const route = useRoute()
const { loading, start, stop } = useLoading()
const { state: pagination, backendPage, setTotal, goToPage } = usePagination()
const { exportToExcel } = useExport()
const list = ref<AlertVO[]>([])

const activeTab = ref((route.query.status as string) || 'PENDING')
const tabs = [
  { label: '待处理', value: AlertStatus.PENDING },
  { label: '已确认', value: AlertStatus.CONFIRMED },
  { label: '已解决', value: AlertStatus.RESOLVED },
  { label: '已关闭', value: AlertStatus.CLOSED }
]

const severityTagType: Record<string, string> = {
  CRITICAL: 'danger',
  WARNING: 'warning',
  INFO: 'info'
}

const { acting, confirm, resolve, closeAlert, statusTagType } = useAlertActions(() => fetchData())

async function fetchData() {
  start()
  try {
    const params: Record<string, string | number> = {
      page: backendPage.value,
      size: pagination.value.size,
      status: activeTab.value
    }
    const page = await alertApi.getAdminList(params)
    list.value = page.content
    setTotal(page.totalElements, page.totalPages)
  } finally {
    stop()
  }
}

function handleTabChange() {
  goToPage(1)
  fetchData()
}

const alertExportCols = [
  { header: '设备名称', key: 'deviceName' },
  { header: '客户名称', key: 'customerName' },
  { header: '严重级别', key: 'severity' },
  { header: '报警信息', key: 'message' },
  { header: '浓度', key: 'concentration' },
  { header: '阈值', key: 'threshold' },
  { header: '状态', key: 'status' },
  { header: '触发时间', key: 'triggeredAt' }
]

async function handleExport() {
  await exportToExcel(
    (p) => alertApi.getAdminList({ status: activeTab.value, ...p } as any),
    {} as Record<string, unknown>,
    alertExportCols,
    '报警记录'
  )
}

function handlePageChange(page: number) { goToPage(page); fetchData() }
function handleDetail(uuid: string) { router.push({ name: 'AdminAlertDetail', params: { uuid } }) }

onMounted(() => fetchData())
</script>

<template>
  <div class="admin-alert-list">
    <div class="search-bar">
      <h3 class="page-title">报警记录</h3>
      <el-button @click="handleExport">导出Excel</el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane v-for="tab in tabs" :key="tab.value" :label="tab.label" :name="tab.value" />
    </el-tabs>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="list.length === 0" class="empty-state">
      <p>暂无报警记录</p>
    </div>

    <div v-else class="table-wrapper">
      <el-table :data="list" stripe style="width:100%">
        <el-table-column label="严重级别" width="80">
          <template #default="{ row }">
            <el-tag :type="severityTagType[row.severity] || 'info'" size="small">
              {{ AlertSeverityMap[row.severity as keyof typeof AlertSeverityMap] || row.severity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ AlertStatusMap[row.status as keyof typeof AlertStatusMap] || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="设备" min-width="120">
          <template #default="{ row }">
            <span>{{ row.deviceName || row.deviceUuid }}</span>
          </template>
        </el-table-column>
        <el-table-column label="客户" min-width="100">
          <template #default="{ row }">
            <span>{{ row.customerName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="报警信息" min-width="200" show-overflow-tooltip />
        <el-table-column prop="concentration" label="浓度" width="90" />
        <el-table-column prop="threshold" label="阈值" width="90" />
        <el-table-column prop="triggeredAt" label="触发时间" width="160" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row.alertUuid)">详情</el-button>
            <el-button v-if="row.status === AlertStatus.PENDING" size="small" type="primary" :loading="acting" @click="confirm(row.alertUuid)">确认</el-button>
            <el-button v-if="row.status === AlertStatus.CONFIRMED" size="small" type="success" :loading="acting" @click="resolve(row.alertUuid)">解决</el-button>
            <el-button v-if="row.status === AlertStatus.PENDING || row.status === AlertStatus.RESOLVED" size="small" type="danger" :loading="acting" @click="closeAlert(row.alertUuid)">关闭</el-button>
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
.admin-alert-list { display:flex; flex-direction:column; gap:16px; }
.search-bar { display:flex; align-items:center; justify-content:space-between; padding:20px 20px 0; background:#fff; border-radius:8px 8px 0 0; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
.page-title { font-size:16px; font-weight:600; color:#1f2937; margin:0; }
.table-wrapper { background:#fff; border-radius:0 0 8px 8px; padding:0 0 20px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
.pagination-bar { display:flex; justify-content:flex-end; }
.loading-state { background:#fff; border-radius:8px; padding:40px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
.empty-state { display:flex; align-items:center; justify-content:center; padding:80px 24px; background:#fff; border-radius:0 0 8px 8px; color:#9ca3af; font-size:15px; }
</style>
