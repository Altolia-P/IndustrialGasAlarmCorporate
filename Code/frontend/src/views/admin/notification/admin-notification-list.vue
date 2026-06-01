<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { alertApi } from '@/api/device'
import type { NotificationVO } from '@/types/device'
import type { Page } from '@/types/common'

const loading = ref(false)
const notifications = ref<NotificationVO[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

const channelMap: Record<string, string> = {
  IN_APP: '站内通知',
  SMS: '短信',
  EMAIL: '邮件'
}

function getChannelLabel(ch: string) {
  return channelMap[ch] || ch
}

async function fetchNotifications() {
  loading.value = true
  try {
    const result = await alertApi.listAllNotifications({ page: currentPage.value, size: pageSize.value })
    notifications.value = result.content
    total.value = result.totalElements
  } catch {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function handlePageChange(page: number) {
  currentPage.value = page
  fetchNotifications()
}

onMounted(fetchNotifications)
</script>

<template>
  <div class="notification-page">
    <div class="page-header">
      <h3>通知记录</h3>
    </div>

    <el-card>
      <el-table :data="notifications" v-loading="loading" stripe>
        <el-table-column label="报警编号" width="200" prop="alertUuid">
          <template #default="{ row }">
            <router-link :to="`/admin/alerts/${row.alertUuid}`" class="link">
              {{ row.alertUuid?.substring(0, 8) }}...
            </router-link>
          </template>
        </el-table-column>
        <el-table-column label="通知渠道" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.channel === 'SMS' ? 'warning' : row.channel === 'EMAIL' ? '' : 'primary'">
              {{ getChannelLabel(row.channel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="内容" min-width="250" prop="content" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 'SENT' || row.status === 'DELIVERED' ? 'success' : row.status === 'FAILED' ? 'danger' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发送时间" width="170" prop="sentAt" />
        <el-table-column label="创建时间" width="170" prop="createdAt" />
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

      <el-empty v-if="!loading && notifications.length === 0" description="暂无通知记录" />
    </el-card>
  </div>
</template>

<style scoped>
.notification-page {
  padding: 0;
}
.page-header {
  margin-bottom: 16px;
}
.page-header h3 {
  margin: 0;
  font-size: 18px;
}
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
.link {
  color: var(--color-primary);
  text-decoration: none;
}
</style>
