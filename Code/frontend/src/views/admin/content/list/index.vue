<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

interface ContentRow {
  contentUuid: string
  title: string
  type: string
  categoryName: string
  status: string
  createdAt: string
}

const contents = ref<ContentRow[]>([
  { contentUuid: '1', title: '石油化工行业气体监测方案', type: 'SOLUTION', categoryName: '解决方案', status: 'PUBLISHED', createdAt: '2024-01-10' },
  { contentUuid: '2', title: '冶金钢铁行业安全方案', type: 'SOLUTION', categoryName: '解决方案', status: 'PUBLISHED', createdAt: '2024-01-12' },
  { contentUuid: '3', title: '公司荣获年度创新奖', type: 'NEWS', categoryName: '公司新闻', status: 'PUBLISHED', createdAt: '2024-03-15' },
  { contentUuid: '4', title: '新一代气体探测器发布', type: 'NEWS', categoryName: '产品发布', status: 'DRAFT', createdAt: '2024-03-05' }
])

const typeMap: Record<string, string> = {
  SOLUTION: '解决方案',
  NEWS: '新闻'
}

const statusMap: Record<string, { text: string; type: string }> = {
  PUBLISHED: { text: '已发布', type: 'success' },
  DRAFT: { text: '草稿', type: 'info' }
}

const searchForm = ref({ title: '', type: '' })
const currentPage = ref(1)
const total = ref(4)

function handleCreate() {
  router.push('/admin/contents/create')
}

function handleEdit(uuid: string) {
  router.push(`/admin/contents/${uuid}/edit`)
}

async function handleDelete(uuid: string) {
  await ElMessageBox.confirm('确认删除该内容？', '删除确认', { type: 'warning' })
  contents.value = contents.value.filter((c) => c.contentUuid !== uuid)
  ElMessage.success('删除成功')
}

function handlePublish(uuid: string) {
  const c = contents.value.find((c) => c.contentUuid === uuid)
  if (c) {
    c.status = 'PUBLISHED'
    ElMessage.success('发布成功')
  }
}

function handleSearch() {
  currentPage.value = 1
}

function handlePageChange(page: number) {
  currentPage.value = page
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
    </div>

    <div class="table-wrapper">
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
            <el-button v-if="row.status !== 'PUBLISHED'" size="small" type="success" @click="handlePublish(row.contentUuid)">发布</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.contentUuid)">删除</el-button>
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
</style>
