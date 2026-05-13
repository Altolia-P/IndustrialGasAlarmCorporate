<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ContentType, ContentStatus } from '@/types/content'
import { contents as sharedContents } from '@/data/content'

const router = useRouter()

const typeMap: Record<string, string> = {
  [ContentType.SOLUTION]: '解决方案',
  [ContentType.NEWS]: '新闻'
}

const statusMap: Record<string, { text: string; type: string }> = {
  [ContentStatus.PUBLISHED]: { text: '已发布', type: 'success' },
  [ContentStatus.DRAFT]: { text: '草稿', type: 'info' }
}

const searchForm = ref({ title: '', type: '' })
const currentPage = ref(1)

const filteredContents = computed(() => {
  let list = sharedContents
  if (searchForm.value.title) {
    const kw = searchForm.value.title.toLowerCase()
    list = list.filter((c) => c.title.toLowerCase().includes(kw))
  }
  if (searchForm.value.type) {
    list = list.filter((c) => c.type === searchForm.value.type)
  }
  return list
})

function handleCreate() {
  router.push('/admin/contents/create')
}

function handleEdit(uuid: string) {
  router.push(`/admin/contents/${uuid}/edit`)
}

async function handleDelete(uuid: string) {
  await ElMessageBox.confirm('确认删除该内容？', '删除确认', { type: 'warning' })
  const idx = sharedContents.findIndex((c) => c.contentUuid === uuid)
  if (idx !== -1) sharedContents.splice(idx, 1)
  ElMessage.success('删除成功')
}

function handlePublish(uuid: string) {
  const c = sharedContents.find((c) => c.contentUuid === uuid)
  if (c) {
    c.status = ContentStatus.PUBLISHED
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
      <el-table :data="filteredContents" stripe style="width:100%">
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
        v-model:current-page="currentPage"
        :total="filteredContents.length"
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
