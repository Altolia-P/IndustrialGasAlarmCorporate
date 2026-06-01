<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { systemConfigApi } from '@/api/system-config'
import type { SystemConfigVO } from '@/types/system-config'

const loading = ref(false)
const saving = ref(false)
const configs = ref<SystemConfigVO[]>([])

async function fetchConfigs() {
  loading.value = true
  try {
    configs.value = await systemConfigApi.getAdminList()
  } catch {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

async function handleSave(config: SystemConfigVO) {
  saving.value = true
  try {
    await systemConfigApi.update(config.configKey, {
      configValue: config.configValue,
      description: config.description
    })
    ElMessage.success('保存成功')
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(fetchConfigs)
</script>

<template>
  <div class="config-page">
    <div class="page-header">
      <h3>系统配置</h3>
    </div>
    <el-card v-loading="loading">
      <el-table :data="configs" stripe>
        <el-table-column prop="configKey" label="配置键" width="220" />
        <el-table-column prop="configValue" label="配置值" min-width="300">
          <template #default="{ row }">
            <el-input v-model="row.configValue" />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" width="200">
          <template #default="{ row }">
            <el-input v-model="row.description" placeholder="配置说明" />
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" :loading="saving" @click="handleSave(row)">
              保存
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && configs.length === 0" description="暂无配置项" />
    </el-card>
  </div>
</template>

<style scoped>
.config-page {
  padding: 0;
}
.page-header {
  margin-bottom: 16px;
}
.page-header h3 {
  margin: 0;
  font-size: 18px;
}
</style>
