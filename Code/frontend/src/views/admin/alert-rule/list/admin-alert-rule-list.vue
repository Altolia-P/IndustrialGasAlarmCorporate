<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { alertRuleApi } from '@/api/device'
import { AlertRuleTypeMap, AlertSeverityMap } from '@/types/device'
import type { AlertRuleVO } from '@/types/device'
import { useLoading } from '@/composables/use-loading'

const router = useRouter()
const { loading, start, stop } = useLoading()
const list = ref<AlertRuleVO[]>([])

const severityTagType: Record<string, string> = {
  CRITICAL: 'danger',
  WARNING: 'warning',
  INFO: 'info'
}

async function fetchData() {
  start()
  try {
    list.value = await alertRuleApi.getAdminList()
  } finally {
    stop()
  }
}

function handleCreate() { router.push({ name: 'AdminAlertRuleCreate' }) }
function handleEdit(uuid: string) { router.push({ name: 'AdminAlertRuleEdit', params: { uuid } }) }

async function handleDelete(uuid: string, name: string) {
  try {
    await ElMessageBox.confirm(`确认删除规则「${name}」？`, '删除规则', { type: 'warning' })
    await alertRuleApi.remove(uuid)
    ElMessage.success('删除成功')
    fetchData()
  } catch { /* cancelled */ }
}

async function handleToggle(rule: AlertRuleVO) {
  try {
    if (rule.enabled) {
      await alertRuleApi.disable(rule.ruleUuid)
      ElMessage.success('已禁用')
    } else {
      await alertRuleApi.enable(rule.ruleUuid)
      ElMessage.success('已启用')
    }
    fetchData()
  } catch { /* ignore */ }
}

onMounted(() => fetchData())
</script>

<template>
  <div class="admin-alert-rule-list">
    <div class="search-bar">
      <h3 class="page-title">报警规则</h3>
      <el-button type="primary" @click="handleCreate">新增规则</el-button>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="list.length === 0" class="empty-state">
      <p>暂无报警规则</p>
    </div>

    <div v-else class="table-wrapper">
      <el-table :data="list" stripe style="width:100%">
        <el-table-column prop="name" label="规则名称" min-width="140" />
        <el-table-column label="规则类型" width="110">
          <template #default="{ row }">{{ AlertRuleTypeMap[row.ruleType as keyof typeof AlertRuleTypeMap] || row.ruleType }}</template>
        </el-table-column>
        <el-table-column label="严重级别" width="90">
          <template #default="{ row }">
            <el-tag :type="severityTagType[row.severity] || 'info'" size="small">
              {{ AlertSeverityMap[row.severity as keyof typeof AlertSeverityMap] || row.severity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="threshold" label="阈值" width="100" />
        <el-table-column prop="durationSeconds" label="持续(秒)" width="90" />
        <el-table-column label="自动建工单" width="100">
          <template #default="{ row }">{{ row.autoCreateWorkOrder ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch :model-value="row.enabled" @change="handleToggle(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row.ruleUuid)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.ruleUuid, row.name)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.admin-alert-rule-list { display:flex; flex-direction:column; gap:16px; }
.search-bar { display:flex; align-items:center; justify-content:space-between; padding:20px; background:#fff; border-radius:8px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
.page-title { font-size:16px; font-weight:600; color:#1f2937; margin:0; }
.table-wrapper { background:#fff; border-radius:8px; padding:0 0 20px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
.loading-state { background:#fff; border-radius:8px; padding:40px; box-shadow:0 1px 3px rgba(0,0,0,0.04); }
.empty-state { display:flex; align-items:center; justify-content:center; padding:80px 24px; background:#fff; border-radius:8px; color:#9ca3af; font-size:15px; }
</style>
