<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { productApi } from '@/api/product'
import type { ProductDetailVO } from '@/types/product'
import { useLoading } from '@/composables/use-loading'

const router = useRouter()
const route = useRoute()

interface ProductDetailData {
  name: string
  category: string
  description: string
  image: string
  images: string[]
  features: string[]
  params: { name: string; value: string }[]
}

const fallbackProduct: ProductDetailData = {
  name: 'IS-200 复合气体检测仪',
  category: '气体检测仪',
  description: '新一代四合一复合气体检测仪，可同时检测可燃气体、氧气、一氧化碳和硫化氢等四种气体。采用先进的电化学传感器技术，具备IP67防护等级和本安防爆认证，广泛应用于石油化工、冶金、消防等领域。',
  image: 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=800&h=600&fit=crop',
  images: [] as string[],
  features: ['四合一气体检测', 'IP67防护等级', '本安防爆认证', '10小时续航', '声光震动报警', '数据记录功能'],
  params: [
    { name: '检测气体', value: '可燃气体、O₂、CO、H₂S' },
    { name: '检测原理', value: '催化燃烧 + 电化学' },
    { name: '采样方式', value: '扩散式' },
    { name: '防护等级', value: 'IP67' },
    { name: '防爆等级', value: 'Ex ia IIC T4 Ga' },
    { name: '报警方式', value: '声、光、振动' },
    { name: '显示屏幕', value: '2.4英寸高清彩屏' },
    { name: '工作温度', value: '-40℃ ~ +70℃' },
    { name: '电池续航', value: '≥10小时' },
    { name: '数据存储', value: '1000条报警记录' }
  ]
}

const product = ref<ProductDetailData>(fallbackProduct)
const lightboxImage = ref<string | null>(null)
const { loading, start, stop } = useLoading()

async function fetchProduct() {
  const uuid = route.params.uuid as string
  if (!uuid) return
  start()
  try {
    const detail = await productApi.getPublicDetail(uuid)
    product.value = {
      name: detail.name,
      category: detail.categoryName,
      description: detail.description,
      image: detail.coverImage || fallbackProduct.image,
      images: (detail.images || []).map(img => img.url),
      features: (detail.attributes || []).map(a => a.attrVal),
      params: (detail.attributes || []).map(a => ({ name: a.attrKey, value: a.attrVal }))
    }
  } catch {
    product.value = fallbackProduct
  } finally {
    stop()
  }
}

onMounted(() => {
  fetchProduct()
})

const relatedProducts = [
  { id: '1', name: 'IS-100 便携式气体检测仪', spec: '单一气体检测', icon: '🔍' },
  { id: '3', name: 'IS-300 固定式气体检测器', spec: '在线监测', icon: '📡' },
  { id: '4', name: 'IS-400 无线气体检测系统', spec: '物联网方案', icon: '🛰️' }
]

function goContact() {
  router.push('/contact')
}

function goProduct(uuid: string) {
  router.push(`/products/${uuid}`)
}
</script>

<template>
  <div class="product-detail-page">
    <section class="detail-hero">
      <div class="container">
        <div class="breadcrumb">
          <span class="breadcrumb-link">首页</span>
          <span class="breadcrumb-sep">/</span>
          <span class="breadcrumb-link">产品中心</span>
          <span class="breadcrumb-sep">/</span>
          <span class="breadcrumb-current">{{ product.category }}</span>
          <span class="breadcrumb-sep">/</span>
          <span class="breadcrumb-current">{{ product.name }}</span>
        </div>
        <div class="detail-grid">
          <div class="detail-image">
            <img :src="product.image" :alt="product.name" class="main-image" />
          </div>
          <div class="detail-info">
            <span class="detail-category">{{ product.category }}</span>
            <h1 class="detail-name">{{ product.name }}</h1>
            <p class="detail-desc">{{ product.description }}</p>
            <div class="detail-features">
              <span v-for="f in product.features" :key="f" class="detail-tag">{{ f }}</span>
            </div>
            <div class="detail-actions">
              <el-button type="primary" size="large" round @click="goContact">立即咨询</el-button>
              <el-button size="large" round class="btn-secondary" @click="router.push('/support')">下载规格书</el-button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section v-if="product.images.length > 0" class="gallery-section">
      <div class="container">
        <h2 class="section-title">产品图片</h2>
        <div class="gallery-grid">
          <div
            v-for="(img, i) in product.images"
            :key="i"
            class="gallery-item"
            @click="lightboxImage = img"
          >
            <img :src="img" :alt="`${product.name} - ${i + 1}`" class="gallery-thumb" />
          </div>
        </div>
      </div>
    </section>

    <section class="detail-content">
      <div class="container">
        <div class="content-grid">
          <div class="content-main">
            <h2 class="section-title">技术参数</h2>
            <div class="params-table">
              <div v-for="p in product.params" :key="p.name" class="param-row">
                <span class="param-name">{{ p.name }}</span>
                <span class="param-value">{{ p.value }}</span>
              </div>
            </div>
          </div>

          <aside class="content-sidebar">
            <div class="sidebar-card">
              <h3 class="sidebar-title">相关产品</h3>
              <div class="related-list">
                <div v-for="rp in relatedProducts" :key="rp.id" class="related-item" @click="goProduct(rp.id)">
                  <span class="related-icon">{{ rp.icon }}</span>
                  <div class="related-info">
                    <span class="related-name">{{ rp.name }}</span>
                    <span class="related-spec">{{ rp.spec }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="sidebar-card sidebar-cta">
              <h3 class="sidebar-cta-title">需要帮助？</h3>
              <p class="sidebar-cta-desc">我们的技术专家将为您提供专业的产品选型建议</p>
              <el-button type="primary" round class="btn-full" @click="goContact">立即咨询</el-button>
            </div>
          </aside>
        </div>
      </div>
    </section>

    <Teleport to="body">
      <div v-if="lightboxImage" class="lightbox-overlay" @click="lightboxImage = null">
        <div class="lightbox-container">
          <img :src="lightboxImage" class="lightbox-img" @click.stop />
          <button class="lightbox-close" @click="lightboxImage = null">✕</button>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<style scoped>
.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.detail-hero {
  padding: 100px 0 64px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 40px;
  font-size: 14px;
}

.breadcrumb-link {
  color: #9ca3af;
  cursor: pointer;
}

.breadcrumb-link:hover {
  color: #3b82f6;
}

.breadcrumb-sep {
  color: #d1d5db;
}

.breadcrumb-current {
  color: #111827;
  font-weight: 500;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 64px;
  align-items: center;
}

.main-image {
  width: 100%;
  aspect-ratio: 4/3;
  object-fit: cover;
  border-radius: 20px;
  background: #f3f4f6;
}

.detail-category {
  display: inline-block;
  font-size: 13px;
  font-weight: 600;
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.1);
  padding: 4px 14px;
  border-radius: 50px;
  margin-bottom: 16px;
}

.detail-name {
  font-size: 36px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 20px;
}

.detail-desc {
  font-size: 16px;
  color: #6b7280;
  line-height: 1.8;
  margin: 0 0 24px;
}

.detail-features {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 32px;
}

.detail-tag {
  padding: 8px 18px;
  background: #f3f4f6;
  color: #4b5563;
  font-size: 14px;
  border-radius: 8px;
}

.detail-actions {
  display: flex;
  gap: 16px;
}

.btn-secondary {
  border-color: #d1d5db;
  color: #4b5563;
}

.detail-content {
  padding: 0 0 80px;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 64px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #3b82f6;
}

.params-table {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
}

.param-row {
  display: grid;
  grid-template-columns: 200px 1fr;
  padding: 16px 20px;
  border-bottom: 1px solid #f3f4f6;
}

.param-row:last-child {
  border-bottom: none;
}

.param-row:nth-child(even) {
  background: #f9fafb;
}

.param-name {
  font-weight: 600;
  color: #111827;
  font-size: 14px;
}

.param-value {
  color: #6b7280;
  font-size: 14px;
}

.sidebar-card {
  background: #f9fafb;
  border-radius: 16px;
  padding: 28px;
  margin-bottom: 20px;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 20px;
}

.related-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.related-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  background: #ffffff;
  border-radius: 10px;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.related-item:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.related-icon {
  font-size: 24px;
  width: 44px;
  height: 44px;
  background: #f3f4f6;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.related-info {
  display: flex;
  flex-direction: column;
}

.related-name {
  font-weight: 500;
  font-size: 14px;
  color: #111827;
}

.related-spec {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 2px;
}

.sidebar-cta {
  background: #3b82f6;
}

.sidebar-cta-title {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 8px;
}

.sidebar-cta-desc {
  font-size: 14px;
  color: rgba(255,255,255,0.8);
  margin: 0 0 20px;
  line-height: 1.6;
}

.btn-full {
  width: 100%;
}

.gallery-section {
  padding: 0 0 80px;
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.gallery-item {
  aspect-ratio: 4/3;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.25s;
}

.gallery-item:hover {
  border-color: #3b82f6;
}

.gallery-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: #f3f4f6;
  transition: transform 0.3s;
}

.gallery-item:hover .gallery-thumb {
  transform: scale(1.05);
}

.lightbox-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.lightbox-container {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
}

.lightbox-img {
  max-width: 90vw;
  max-height: 85vh;
  object-fit: contain;
  border-radius: 8px;
  cursor: default;
}

.lightbox-close {
  position: absolute;
  top: -48px;
  right: 0;
  background: none;
  border: none;
  color: #ffffff;
  font-size: 28px;
  cursor: pointer;
  padding: 8px;
  line-height: 1;
  transition: opacity 0.2s;
}

.lightbox-close:hover {
  opacity: 0.7;
}

@media (max-width: 1024px) {
  .detail-grid {
    grid-template-columns: 1fr;
    gap: 32px;
  }
  .content-grid {
    grid-template-columns: 1fr;
  }
  .detail-name {
    font-size: 28px;
  }
}

@media (max-width: 768px) {
  .detail-hero {
    padding: 80px 0 40px;
  }
  .detail-actions {
    flex-direction: column;
  }
  .param-row {
    grid-template-columns: 120px 1fr;
  }
  .gallery-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
