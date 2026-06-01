<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { customerApi } from '@/api/customer'
import { DeviceStatusMap } from '@/types/device'
import { WorkOrderStatusMap, WorkOrderTypeMap, WorkOrderPriorityMap } from '@/types/workorder'
import { MessageStatusMap } from '@/types/message'
import { AlertSeverityMap, AlertStatusMap } from '@/types/device'
import type { Customer360VO } from '@/types/customer'

const route = useRoute()
const router = useRouter()
const phone = (route.query.phone as string) || ''
const data = ref<Customer360VO | null>(null)
const loading = ref(false)
const activeTab = ref('devices')

const severityTagType: Record<string, string> = { CRITICAL: 'danger', WARNING: 'warning', INFO: 'info' }

async function load() {
  if (!phone) {
    ElMessage.warning('缺少客户手机号')
    router.back()
    return
  }
  loading.value = true
  try {
    data.value = await customerApi.get360(phone)
  } catch {
    data.value = null
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function goWorkOrder(uuid: string) {
  router.push(`/admin/workorders/${uuid}/edit`)
}

function goDevice(uuid: string) {
  router.push({ name: 'AdminDeviceDetail', params: { uuid } })
}

function goAlert(uuid: string) {
  router.push({ name: 'AdminAlertDetail', params: { uuid } })
}

onMounted(load)
</script>

<template>
  <div class="customer-360">
    <div class="page-header">
      <el-button text @click="router.back()">← 返回</el-button>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="8" animated />
    </div>

    <template v-else-if="data">
      <div class="customer-card">
        <div class="customer-info">
          <div class="customer-avatar">{{ data.name.charAt(0).toUpperCase() }}</div>
          <div class="customer-meta">
            <h2>{{ data.name }}</h2>
            <div class="meta-row">
              <span class="meta-label">手机</span>
              <span>{{ data.phone }}</span>
            </div>
            <div class="meta-row" v-if="data.company">
              <span class="meta-label">公司</span>
              <span>{{ data.company }}</span>
            </div>
            <el-tag :type="data.registered ? 'success' : 'info'" size="small">
              {{ data.registered ? '已注册' : '未注册' }}
            </el-tag>
          </div>
        </div>
        <div class="customer-stats">
          <div class="stat-item" :class="{ active: activeTab === 'devices' }" @click="activeTab = 'devices'">
            <span class="stat-num">{{ data.deviceCount }}</span>
            <span class="stat-label">设备</span>
          </div>
          <div class="stat-item" :class="{ active: activeTab === 'workorders' }" @click="activeTab = 'workorders'">
            <span class="stat-num">{{ data.workOrderCount }}</span>
            <span class="stat-label">工单</span>
          </div>
          <div class="stat-item" :class="{ active: activeTab === 'messages' }" @click="activeTab = 'messages'">
            <span class="stat-num">{{ data.messageCount }}</span>
            <span class="stat-label">留言</span>
          </div>
          <div class="stat-item" :class="{ active: activeTab === 'alerts' }" @click="activeTab = 'alerts'">
            <span class="stat-num">{{ data.alertCount }}</span>
            <span class="stat-label">报警</span>
          </div>
        </div>
      </div>

      <div class="tab-content">
        <!-- Devices -->
        <div v-if="activeTab === 'devices'" class="section">
          <div v-if="data.devices.length === 0" class="empty-text">暂无设备</div>
          <el-table v-else :data="data.devices" stripe>
            <el-table-column prop="serialNumber" label="序列号" width="140" />
            <el-table-column prop="name" label="名称" min-width="140">
              <template #default="{ row }">
                <el-link type="primary" @click="goDevice(row.deviceUuid)">{{ row.name }}</el-link>
              </template>
            </el-table-column>
            <el-table-column prop="model" label="型号" width="100" />
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="row.status === 'NORMAL' ? 'success' : row.status === 'ABNORMAL' ? 'danger' : 'info'">
                  {{ DeviceStatusMap[row.status as keyof typeof DeviceStatusMap] || row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="installLocation" label="安装位置" width="120" />
            <el-table-column prop="createdAt" label="创建时间" width="160" />
          </el-table>
        </div>

        <!-- Work Orders -->
        <div v-if="activeTab === 'workorders'" class="section">
          <div v-if="data.workOrders.length === 0" class="empty-text">暂无工单</div>
          <el-table v-else :data="data.workOrders" stripe>
            <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
            <el-table-column label="类型" width="100">
              <template #default="{ row }">{{ WorkOrderTypeMap[row.type as keyof typeof WorkOrderTypeMap] }}</template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="row.status === 'COMPLETED' ? 'success' : row.status === 'IN_PROGRESS' ? 'primary' : 'warning'">
                  {{ WorkOrderStatusMap[row.status as keyof typeof WorkOrderStatusMap] }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="assignedStaffName" label="负责人" width="100" />
            <el-table-column prop="createdAt" label="创建时间" width="160" />
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button size="small" @click="goWorkOrder(row.workOrderUuid)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- Messages -->
        <div v-if="activeTab === 'messages'" class="section">
          <div v-if="data.messages.length === 0" class="empty-text">暂无留言</div>
          <el-table v-else :data="data.messages" stripe>
            <el-table-column prop="name" label="姓名" width="100" />
            <el-table-column prop="content" label="内容" min-width="240" show-overflow-tooltip />
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="row.status === 'PROCESSED' ? 'success' : row.status === 'IN_PROGRESS' ? 'primary' : 'warning'">
                  {{ MessageStatusMap[row.status as keyof typeof MessageStatusMap] }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="submittedAt" label="提交时间" width="160" />
          </el-table>
        </div>

        <!-- Alerts -->
        <div v-if="activeTab === 'alerts'" class="section">
          <div v-if="data.recentAlerts.length === 0" class="empty-text">暂无报警</div>
          <el-table v-else :data="data.recentAlerts" stripe>
            <el-table-column label="级别" width="80">
              <template #default="{ row }">
                <el-tag :type="severityTagType[row.severity] || 'info'" size="small">
                  {{ AlertSeverityMap[row.severity as keyof typeof AlertSeverityMap] }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="message" label="报警信息" min-width="200" show-overflow-tooltip />
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag size="small">{{ AlertStatusMap[row.status as keyof typeof AlertStatusMap] }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="triggeredAt" label="触发时间" width="160" />
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button size="small" @click="goAlert(row.alertUuid)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </template>

    <div v-else class="empty-state">
      <p>客户数据加载失败</p>
    </div>
  </div>
</template>

<style scoped>
.customer-360 {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  margin-bottom: -8px;
}

.customer-card {
  background: #fff;
  border-radius: 10px;
  padding: 28px 32px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.customer-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.customer-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  color: #fff;
  font-size: 28px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.customer-meta h2 {
  margin: 0 0 8px;
  font-size: 22px;
  color: #1f2937;
}

.meta-row {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 4px;
}

.meta-label {
  color: #9ca3af;
  margin-right: 8px;
}

.customer-stats {
  display: flex;
  gap: 0;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}

.stat-item {
  padding: 14px 20px;
  text-align: center;
  cursor: pointer;
  transition: background 0.15s;
  border-right: 1px solid #e5e7eb;
  min-width: 80px;
}

.stat-item:last-child {
  border-right: none;
}

.stat-item:hover {
  background: #f9fafb;
}

.stat-item.active {
  background: #eff6ff;
}

.stat-num {
  display: block;
  font-size: 22px;
  font-weight: 700;
  color: #1f2937;
}

.stat-label {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 2px;
}

.tab-content {
  background: #fff;
  border-radius: 10px;
  padding: 0 0 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}

.section {
  padding: 0;
}

.empty-text {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
  color: #9ca3af;
  font-size: 14px;
}

.loading-state {
  background: #fff;
  border-radius: 10px;
  padding: 40px;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px;
  background: #fff;
  border-radius: 10px;
  color: #9ca3af;
}

@media (max-width: 768px) {
  .customer-card {
    flex-direction: column;
    gap: 20px;
  }

  .customer-stats {
    width: 100%;
    justify-content: stretch;
  }

  .stat-item {
    flex: 1;
  }
}
</style>
