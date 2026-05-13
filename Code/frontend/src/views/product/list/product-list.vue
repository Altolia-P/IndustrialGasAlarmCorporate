<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { productApi } from '@/api/product'
import { categoryApi } from '@/api/category'
import { CategoryType } from '@/types/category'
import type { ProductVO } from '@/types/product'
import type { CategoryVO } from '@/types/category'

const router = useRouter()

const categories = ref<CategoryVO[]>([])
const products = ref<ProductVO[]>([])
const loading = ref(false)
const total = ref(0)
const totalPages = ref(0)

const filters = reactive({
  keyword: '',
  categoryUuid: ''
})

const pagination = reactive({
  current: 1,
  pageSize: 20
})

async function fetchCategories() {
  try {
    categories.value = await categoryApi.getCategories(CategoryType.PRODUCT_CATEGORY)
  } catch {
    categories.value = []
  }
}

async function fetchProducts() {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      page: pagination.current - 1,
      size: pagination.pageSize
    }
    if (filters.keyword) params.name = filters.keyword
    if (filters.categoryUuid) params.categoryUuid = filters.categoryUuid
    const data = await productApi.getPublicList(params as never)
    products.value = data.content
    total.value = data.totalElements
    totalPages.value = data.totalPages
  } catch {
    products.value = []
    total.value = 0
    totalPages.value = 0
  } finally {
    loading.value = false
  }
}

function onSearch() {
  pagination.current = 1
  fetchProducts()
}

function onCategoryChange(uuid: string) {
  filters.categoryUuid = uuid
  pagination.current = 1
  fetchProducts()
}

function onPageChange(page: number) {
  pagination.current = page
  fetchProducts()
}

function goDetail(uuid: string) {
  router.push(`/products/${uuid}`)
}

function goContact() {
  router.push('/contact')
}

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>

<template>
  <div class="product-list-page">
    <section class="hero-section">
      <div class="container">
        <h1 class="page-title">产品中心</h1>
        <p class="page-desc">二十年专注气体安全检测领域，提供从传感器到系统的完整产品线</p>
      </div>
    </section>

    <section class="tag-section">
      <div class="container">
        <div class="tag-bar">
          <span class="tag-label">分类筛选</span>
          <div class="tag-tabs">
            <button
              :class="['tag-btn', { active: !filters.categoryUuid }]"
              @click="onCategoryChange('')"
            >
              全部
            </button>
            <button
              v-for="c in categories"
              :key="c.categoryUuid"
              :class="['tag-btn', { active: filters.categoryUuid === c.categoryUuid }]"
              @click="onCategoryChange(c.categoryUuid)"
            >
              {{ c.name }}
            </button>
          </div>
        </div>
      </div>
    </section>

    <section class="search-section">
      <div class="container">
        <div class="search-bar">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索产品名称..."
            clearable
            size="default"
            class="search-input"
            @keyup.enter="onSearch"
            @clear="onSearch"
          >
            <template #prefix>
              <span class="search-icon">🔍</span>
            </template>
          </el-input>
          <el-button type="primary" @click="onSearch">搜索</el-button>
        </div>
      </div>
    </section>

    <section class="results-section">
      <div class="container">
        <div v-if="loading" class="status-row">
          <span class="status-text">加载中...</span>
        </div>

        <template v-else-if="products.length">
          <div class="products-grid">
            <div
              v-for="p in products"
              :key="p.productUuid"
              class="product-row"
              @click="goDetail(p.productUuid)"
            >
              <div class="product-thumb">
                <img
                  v-if="p.coverImage"
                  :src="p.coverImage"
                  :alt="p.name"
                  loading="lazy"
                  class="thumb-img"
                />
                <span v-else class="thumb-placeholder">📦</span>
              </div>
              <div class="product-main">
                <div class="product-head">
                  <h3 class="product-name">{{ p.name }}</h3>
                  <span class="product-category">{{ p.categoryName }}</span>
                </div>
                <p class="product-desc">{{ p.description }}</p>
              </div>
              <span class="product-arrow">→</span>
            </div>
          </div>

          <div v-if="totalPages > 1" class="pagination-row">
            <el-pagination
              :current-page="pagination.current"
              :page-size="pagination.pageSize"
              :total="total"
              layout="prev, pager, next"
              background
              @current-change="onPageChange"
            />
          </div>
        </template>

        <div v-else class="status-row">
          <span class="status-text">暂无匹配产品</span>
        </div>
      </div>
    </section>

    <section class="cta-section">
      <div class="container">
        <div class="cta-card">
          <h2 class="cta-title">找不到合适的产品？</h2>
          <p class="cta-desc">我们的技术专家将根据您的实际需求，推荐最合适的产品方案</p>
          <el-button size="large" round class="btn-white" @click="goContact">联系我们</el-button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.container {
  max-width: 1120px;
  margin: 0 auto;
  padding: 0 24px;
}

.hero-section {
  padding: 48px 0 32px;
  text-align: center;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 12px;
}

.page-desc {
  font-size: 16px;
  color: #6b7280;
  max-width: 600px;
  margin: 0 auto;
}

.tag-section {
  padding: 0 0 16px;
}

.tag-bar {
  display: flex;
  align-items: center;
  gap: 16px;
}

.tag-label {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  white-space: nowrap;
}

.tag-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-btn {
  padding: 6px 18px;
  font-size: 13px;
  font-weight: 500;
  color: #4b5563;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.tag-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}

.tag-btn.active {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #ffffff;
}

.search-section {
  padding: 0 0 24px;
}

.search-bar {
  display: flex;
  gap: 10px;
  background: #f9fafb;
  border-radius: 12px;
  padding: 16px 20px;
}

.search-input {
  flex: 1;
  max-width: 400px;
}

.search-icon {
  font-size: 14px;
}

.results-section {
  padding: 0 0 80px;
}

.status-row {
  text-align: center;
  padding: 64px 0;
}

.status-text {
  color: #9ca3af;
  font-size: 15px;
}

.products-grid {
  display: flex;
  flex-direction: column;
  gap: 2px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
}

.product-row {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 18px 24px;
  cursor: pointer;
  transition: background 0.15s;
  border-bottom: 1px solid #f3f4f6;
}

.product-row:last-child {
  border-bottom: none;
}

.product-row:hover {
  background: #f9fafb;
}

.product-thumb {
  width: 64px;
  height: 48px;
  border-radius: 6px;
  overflow: hidden;
  background: #f3f4f6;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.thumb-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumb-placeholder {
  font-size: 22px;
  opacity: 0.3;
}

.product-main {
  flex: 1;
  min-width: 0;
}

.product-head {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 4px;
}

.product-name {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.product-category {
  font-size: 12px;
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.08);
  padding: 1px 8px;
  border-radius: 4px;
  white-space: nowrap;
}

.product-desc {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-arrow {
  font-size: 16px;
  color: #d1d5db;
  flex-shrink: 0;
  transition: transform 0.2s;
}

.product-row:hover .product-arrow {
  transform: translateX(3px);
  color: #3b82f6;
}

.pagination-row {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.cta-section {
  padding: 0 0 80px;
}

.cta-card {
  background: #f3f4f6;
  border-radius: 16px;
  padding: 48px;
  text-align: center;
}

.cta-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 8px;
}

.cta-desc {
  font-size: 15px;
  color: #6b7280;
  margin: 0 0 24px;
}

.btn-white {
  background: #ffffff;
  color: #111827;
}

.btn-white:hover {
  background: #f0f0f0;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 32px 0 24px;
  }
  .page-title {
    font-size: 28px;
  }
  .tag-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  .search-bar {
    flex-direction: column;
  }
  .search-input {
    max-width: 100%;
  }
  .product-row {
    padding: 14px 16px;
    gap: 14px;
  }
  .product-desc {
    display: none;
  }
  .cta-card {
    padding: 32px 24px;
  }
}
</style>
