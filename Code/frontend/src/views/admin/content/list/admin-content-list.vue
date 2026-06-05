<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ContentType, ContentStatus } from '@/types/content'
import { contentApi } from '@/api/content'
import type { ContentVO } from '@/types/content'
import { useLoading } from '@/composables/use-loading'
import { usePagination } from '@/composables/use-pagination'
import { useExport } from '@/composables/use-export'

const router = useRouter()

const typeMap: Record<string, string> = {
  [ContentType.SOLUTION]: '解决方案',
  [ContentType.NEWS]: '新闻'
}

const statusMap: Record<string, { text: string; type: string }> = {
  [ContentStatus.PUBLISHED]: { text: '已发布', type: 'success' },
  [ContentStatus.DRAFT]: { text: '草稿', type: 'info' }
}

const contents = ref<ContentVO[]>([])
const { loading, start, stop } = useLoading()
const { state: pagination, backendPage, setTotal, goToPage } = usePagination()
const { exportToExcel } = useExport()

const searchForm = ref({ title: '', type: '' })

async function fetchContents() {
  start()
  try {
    const params: Record<string, unknown> = {
      page: backendPage.value,
      size: pagination.value.size
    }
    if (searchForm.value.title) params.title = searchForm.value.title
    if (searchForm.value.type) params.type = searchForm.value.type
    const page = await contentApi.getAdminList(params as Parameters<typeof contentApi.getAdminList>[0])
    contents.value = page.content
    setTotal(page.totalElements, page.totalPages)
  } catch {
    contents.value = []
    ElMessage.error('加载失败')
  } finally {
    stop()
  }
}

onMounted(() => {
  fetchContents()
})

function handleCreate() {
  router.push('/admin/contents/create')
}

function handleEdit(uuid: string) {
  router.push(`/admin/contents/${uuid}/edit`)
}

async function handleDelete(uuid: string) {
  await ElMessageBox.confirm('确认删除该内容？', '删除确认', { type: 'warning' })
  try {
    await contentApi.remove(uuid)
    ElMessage.success('删除成功')
    await fetchContents()
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '删除失败')
  }
}

async function handlePublish(uuid: string) {
  try {
    await contentApi.publish(uuid)
    ElMessage.success('发布成功')
    await fetchContents()
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '发布失败')
  }
}

const contentExportCols = [
  { header: '标题', key: 'title' },
  { header: '类型', key: 'type' },
  { header: '分类', key: 'categoryName' },
  { header: '状态', key: 'status' },
  { header: '更新时间', key: 'updatedAt' },
  { header: '创建时间', key: 'createdAt' }
]

async function handleExport() {
  const params: Record<string, unknown> = {}
  if (searchForm.value.title) params.title = searchForm.value.title
  if (searchForm.value.type) params.type = searchForm.value.type
  await exportToExcel(
    (p) => contentApi.getAdminList(p as any),
    params,
    contentExportCols,
    '内容列表'
  )
}

function handleSearch() {
  goToPage(1)
  fetchContents()
}

function handlePageChange(page: number) {
  goToPage(page)
  fetchContents()
}
</script>

<template>
  <div class="admin-content-list">
    <div class="search-bar">
      <div class="search-left">
        <el-input v-model="searchForm.title" placeholder="标题" clearable style="width:220px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="searchForm.type" placeholder="类型" clearable style="width:140px" @change="handleSearch">
          <el-option label="解决方案" value="SOLUTION" />
          <el-option label="新闻" value="NEWS" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      <el-button type="primary" @click="handleCreate">新增内容</el-button>
      <el-button @click="handleExport">导出Excel</el-button>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="contents.length === 0" class="empty-state">
      <p>暂无内容</p>
    </div>

    <div v-else class="table-wrapper">
      <el-table :data="contents" stripe style="width:100%">
        <el-table-column prop="title" label="标题" min-width="240" />
        <el-table-column label="类型" width="110">
          <template #default="{ row }">{{ typeMap[row.type] }}</template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建日期" width="120" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row.contentUuid)">编辑</el-button>
            <el-button v-if="row.status !== ContentStatus.PUBLISHED" size="small" type="success" @click="handlePublish(row.contentUuid)">发布</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.contentUuid)">删除</el-button>
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
.admin-content-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  padding: 20px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.search-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
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
