<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ProductStatus } from '@/types/product'
import { adminProducts } from '@/data/content'

const router = useRouter()

const statusMap: Record<string, { text: string; type: string }> = {
  [ProductStatus.PUBLISHED]: { text: '已上架', type: 'success' },
  [ProductStatus.DRAFT]: { text: '草稿', type: 'info' },
  [ProductStatus.UNPUBLISHED]: { text: '已下架', type: 'danger' }
}

const searchForm = ref({ name: '', status: '' })
const currentPage = ref(1)

const filteredProducts = computed(() => {
  let list = adminProducts
  if (searchForm.value.name) {
    const kw = searchForm.value.name.toLowerCase()
    list = list.filter((p) => p.name.toLowerCase().includes(kw))
  }
  if (searchForm.value.status) {
    list = list.filter((p) => p.status === searchForm.value.status)
  }
  return list
})

function handleCreate() {
  router.push('/admin/products/create')
}

function handleEdit(uuid: string) {
  router.push(`/admin/products/${uuid}/edit`)
}

async function handleDelete(uuid: string) {
  await ElMessageBox.confirm('确认删除该产品？', '删除确认', { type: 'warning' })
  const idx = adminProducts.findIndex((p) => p.productUuid === uuid)
  if (idx !== -1) adminProducts.splice(idx, 1)
  ElMessage.success('删除成功')
}

function handlePublish(uuid: string) {
  const p = adminProducts.find((p) => p.productUuid === uuid)
  if (p) {
    p.status = ProductStatus.PUBLISHED
    ElMessage.success('上架成功')
  }
}

function handleUnpublish(uuid: string) {
  const p = adminProducts.find((p) => p.productUuid === uuid)
  if (p) {
    p.status = ProductStatus.UNPUBLISHED
    ElMessage.success('下架成功')
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
  <div class="admin-product-list">
    <div class="search-bar">
      <div class="search-left">
        <el-input v-model="searchForm.name" placeholder="产品名称" clearable style="width:220px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="searchForm.status" placeholder="状态" clearable style="width:140px" @change="handleSearch">
          <el-option label="已上架" value="PUBLISHED" />
          <el-option label="草稿" value="DRAFT" />
          <el-option label="已下架" value="UNPUBLISHED" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      <el-button type="primary" @click="handleCreate">新增产品</el-button>
    </div>

    <div class="table-wrapper">
      <el-table :data="filteredProducts" stripe style="width:100%">
        <el-table-column prop="name" label="产品名称" min-width="200" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">
              {{ statusMap[row.status]?.text }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建日期" width="120" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row.productUuid)">编辑</el-button>
            <el-button v-if="row.status !== ProductStatus.PUBLISHED" size="small" type="success" @click="handlePublish(row.productUuid)">上架</el-button>
            <el-button v-if="row.status === ProductStatus.PUBLISHED" size="small" type="warning" @click="handleUnpublish(row.productUuid)">下架</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.productUuid)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-bar">
      <el-pagination
        v-model:current-page="currentPage"
        :total="filteredProducts.length"
        :page-size="20"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.admin-product-list {
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
