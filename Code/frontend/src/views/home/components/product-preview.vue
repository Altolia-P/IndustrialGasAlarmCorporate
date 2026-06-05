<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { productApi } from '@/api/product'
import type { ProductVO } from '@/types/product'

const router = useRouter()
const products = ref<ProductVO[]>([])
const loading = ref(false)
const hasMore = ref(true)
let currentPage = 1

async function fetchProducts() {
  loading.value = true
  try {
    const data = await productApi.getPublicList({ page: currentPage, size: 6 })
    const filtered = data.content.filter(p => p.coverImage)
    products.value = currentPage === 1 ? filtered : [...products.value, ...filtered]
    hasMore.value = data.number < data.totalPages - 1
  } catch {
    if (currentPage === 1) products.value = []
  } finally {
    loading.value = false
  }
}

function loadMore() {
  if (loading.value || !hasMore.value) return
  currentPage++
  fetchProducts()
}

function goProducts() {
  router.push('/products')
}

onMounted(() => {
  fetchProducts()
})
</script>

<template>
  <section class="section section-products">
    <div class="container">
      <div class="section-header">
        <span class="section-tag">产品中心</span>
        <h2 class="section-title">专业的气体安全检测产品</h2>
        <p class="section-desc">从传感器到整体解决方案，提供全系列工业气体检测与火灾报警产品</p>
      </div>

      <div v-if="loading && products.length === 0" class="loading-row">
        <span class="loading-text">加载中...</span>
      </div>

      <div v-else-if="products.length" class="products-scroll-wrapper">
        <div class="products-scroll-track">
          <div
            v-for="p in products"
            :key="p.productUuid"
            class="product-card"
            @click="goProducts"
          >
            <div class="product-image">
              <img
                v-if="p.coverImage"
                :src="p.coverImage"
                :alt="p.name"
                loading="lazy"
                class="product-img"
              />
              <span v-else class="product-placeholder">📦</span>
              <span class="product-category-tag">{{ p.categoryName }}</span>
            </div>
            <div class="product-body">
              <h3 class="product-name">{{ p.name }}</h3>
              <p class="product-desc">{{ p.description }}</p>
            </div>
          </div>
          <div v-if="hasMore" class="load-more-trigger" @click="loadMore">
            <span v-if="!loading" class="load-more-text">查看更多 →</span>
            <span v-else class="load-more-text">加载中...</span>
          </div>
        </div>
        <div v-if="hasMore" class="scroll-hint">
          <span>← 左右滑动查看更多 →</span>
        </div>
      </div>

      <div v-else class="empty-row">
        <p class="empty-text">暂无产品展示，请联系管理员</p>
      </div>

      <div class="section-cta">
        <el-button round @click="goProducts">查看全部产品</el-button>
      </div>
    </div>
  </section>
</template>

<style scoped>
.section {
  padding: 80px 0;
}

.section-products {
  background: #ffffff;
  border-bottom: 1px solid #f3f4f6;
}

.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-header {
  text-align: center;
  max-width: 720px;
  margin: 0 auto 48px;
}

.section-tag {
  display: inline-block;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-primary);
  text-transform: uppercase;
  letter-spacing: 2px;
  margin-bottom: 16px;
}

.section-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--color-gray-900);
  margin: 0 0 16px;
  line-height: 1.3;
}

.section-desc {
  font-size: 18px;
  color: var(--color-gray-600);
  margin: 0;
  line-height: 1.7;
}

.section-cta {
  text-align: center;
  margin-top: 40px;
}

.loading-row,
.empty-row {
  text-align: center;
  padding: 48px 0;
}

.loading-text,
.empty-text {
  color: #9ca3af;
  font-size: 15px;
}

.product-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.3s, box-shadow 0.3s;
  width: 280px;
  flex-shrink: 0;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.products-scroll-wrapper {
  position: relative;
  overflow-x: auto;
  overflow-y: visible;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
  scrollbar-color: #d1d5db transparent;
  padding-bottom: 4px;
}

.products-scroll-wrapper::-webkit-scrollbar {
  height: 4px;
}

.products-scroll-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.products-scroll-wrapper::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 4px;
}

.products-scroll-track {
  display: flex;
  gap: 24px;
  padding: 4px 0 4px;
  width: max-content;
}

.scroll-hint {
  text-align: center;
  margin-top: 12px;
}

.scroll-hint span {
  font-size: 13px;
  color: #9ca3af;
  letter-spacing: 1px;
}

.product-card:hover {
  border-color: var(--color-primary);
  box-shadow: 0 6px 24px rgba(59, 130, 246, 0.1);
}

.product-image {
  aspect-ratio: 4/3;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-placeholder {
  font-size: 40px;
  opacity: 0.3;
}

.load-more-trigger {
  width: 160px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed #d1d5db;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  background: #fafbfc;
}

.load-more-trigger:hover {
  border-color: var(--color-primary);
  background: #eff6ff;
}

.load-more-text {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.load-more-trigger:hover .load-more-text {
  color: var(--color-primary);
}

.product-category-tag {
  position: absolute;
  bottom: 8px;
  left: 8px;
  font-size: 11px;
  font-weight: 600;
  color: #ffffff;
  background: rgba(26, 54, 93, 0.75);
  padding: 3px 10px;
  border-radius: 4px;
}

.product-body {
  padding: 24px;
}

.product-name {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-desc {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (max-width: 768px) {
  .container {
    padding: 0 16px;
  }
  .section {
    padding: 48px 0;
  }
  .section-title {
    font-size: 28px;
  }
  .section-desc {
    font-size: 15px;
  }
  .products-scroll-wrapper {
    overflow-x: visible;
  }
  .products-scroll-track {
    display: grid;
    grid-template-columns: 1fr;
    width: 100%;
    padding: 0;
  }
  .product-card {
    width: 100%;
  }
  .scroll-hint {
    display: none;
  }
  .load-more-trigger {
    display: none;
  }
}
</style>
