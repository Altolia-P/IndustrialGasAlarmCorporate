<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ContentType, ContentStatus, ContentStatusMap } from '@/types/content'
import { contents as sharedContents } from '@/data/content'

const router = useRouter()

const searchForm = ref({ title: '', categoryName: '' })
const currentPage = ref(1)

const filteredNewsList = computed(() => {
  let list = sharedContents.filter((c) => c.type === ContentType.NEWS)
  if (searchForm.value.title) {
    const kw = searchForm.value.title.toLowerCase()
    list = list.filter((n) => n.title.toLowerCase().includes(kw))
  }
  if (searchForm.value.categoryName) {
    list = list.filter((n) => n.categoryName === searchForm.value.categoryName)
  }
  return list
})

function handleCreate() {
  router.push('/admin/news/create')
}

function handleEdit(uuid: string) {
  router.push(`/admin/news/${uuid}/edit`)
}

async function handleDelete(uuid: string) {
  await ElMessageBox.confirm('确认删除该新闻？删除后不可恢复。', '删除确认', { type: 'warning' })
  const idx = sharedContents.findIndex((n) => n.contentUuid === uuid)
  if (idx !== -1) sharedContents.splice(idx, 1)
  ElMessage.success('删除成功')
}

function handlePublish(uuid: string) {
  const item = sharedContents.find((n) => n.contentUuid === uuid)
  if (item) {
    item.status = ContentStatus.PUBLISHED
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
  <div class="admin-news-list">
    <div class="search-bar">
      <div class="search-left">
        <el-input v-model="searchForm.title" placeholder="新闻标题" clearable style="width:220px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="searchForm.categoryName" placeholder="新闻分类" clearable style="width:140px" @change="handleSearch">
          <el-option label="公司新闻" value="公司新闻" />
          <el-option label="行业动态" value="行业动态" />
          <el-option label="产品发布" value="产品发布" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      <el-button type="primary" @click="handleCreate">新增新闻</el-button>
    </div>

    <div class="table-wrapper">
      <el-table :data="filteredNewsList" stripe style="width:100%">
        <el-table-column prop="title" label="标题" min-width="300" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === ContentStatus.PUBLISHED ? 'success' : 'info'" size="small">
              {{ ContentStatusMap[row.status as ContentStatus] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布日期" width="120" />
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
        :total="filteredNewsList.length"
        :page-size="20"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.admin-news-list {
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
