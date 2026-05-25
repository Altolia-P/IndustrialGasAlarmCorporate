<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ProductStatus } from '@/types/product'
import { productApi } from '@/api/product'
import type { ProductVO } from '@/types/product'
import { useLoading } from '@/composables/use-loading'
import { usePagination } from '@/composables/use-pagination'

const router = useRouter()

const statusMap: Record<string, { text: string; type: string }> = {
  [ProductStatus.PUBLISHED]: { text: '已上架', type: 'success' },
  [ProductStatus.DRAFT]: { text: '草稿', type: 'info' },
  [ProductStatus.UNPUBLISHED]: { text: '已下架', type: 'danger' }
}

const products = ref<ProductVO[]>([])
const { loading, start, stop } = useLoading()
const { state: pagination, backendPage, setTotal, goToPage } = usePagination()

const searchForm = ref({ name: '', status: '' })

async function fetchProducts() {
  start()
  try {
    const params: Record<string, unknown> = {
      page: backendPage.value,
      size: pagination.value.size
    }
    if (searchForm.value.name) params.name = searchForm.value.name
    if (searchForm.value.status) params.status = searchForm.value.status
    const page = await productApi.getAdminList(params)
    products.value = page.content
    setTotal(page.totalElements, page.totalPages)
  } catch {
    products.value = []
    ElMessage.error('加载失败')
  } finally {
    stop()
  }
}

onMounted(() => {
  fetchProducts()
})

function handleCreate() {
  router.push('/admin/products/create')
}

function handleEdit(uuid: string) {
  router.push(`/admin/products/${uuid}/edit`)
}

async function handleDelete(uuid: string) {
  await ElMessageBox.confirm('确认删除该产品？', '删除确认', { type: 'warning' })
  try {
    await productApi.remove(uuid)
    ElMessage.success('删除成功')
    await fetchProducts()
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '删除失败')
  }
}

async function handlePublish(uuid: string) {
  try {
    await productApi.publish(uuid)
    ElMessage.success('上架成功')
    await fetchProducts()
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '上架失败')
  }
}

async function handleUnpublish(uuid: string) {
  try {
    await productApi.unpublish(uuid)
    ElMessage.success('下架成功')
    await fetchProducts()
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '下架失败')
  }
}

function handleSearch() {
  goToPage(1)
  fetchProducts()
}

function handlePageChange(page: number) {
  goToPage(page)
  fetchProducts()
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

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="products.length === 0" class="empty-state">
      <p>暂无产品</p>
    </div>

    <div v-else class="table-wrapper">
      <el-table :data="products" stripe style="width:100%">
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
