<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

interface ProductItem {
  id: string
  name: string
  spec: string
}

interface ProductCategory {
  id: string
  name: string
  description: string
  icon: string
  features: string[]
  image: string
  products: ProductItem[]
}

const categories: ProductCategory[] = [
  {
    id: 'gas-detector',
    name: '气体检测仪',
    description: '便携式与固定式气体检测设备，实时监测多种有毒有害气体浓度',
    icon: '🔍',
    features: ['高精度检测', '实时报警', '多气体同测', '防爆认证'],
    image: 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=600&h=400&fit=crop',
    products: [
      { id: '1', name: 'IS-100 便携式气体检测仪', spec: '单一气体检测' },
      { id: '2', name: 'IS-200 复合气体检测仪', spec: '四合一检测' },
      { id: '3', name: 'IS-300 固定式气体检测器', spec: '在线监测' },
      { id: '4', name: 'IS-400 无线气体检测系统', spec: '物联网方案' }
    ]
  },
  {
    id: 'controller',
    name: '气体报警控制器',
    description: '集中管理与控制气体报警系统，支持多通道接入与联动控制',
    icon: '🖥️',
    features: ['多通道接入', '联动控制', '远程监控', '数据存储'],
    image: 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=600&h=400&fit=crop',
    products: [
      { id: '5', name: 'ISC-8 八通道控制器', spec: '小型场所' },
      { id: '6', name: 'ISC-16 十六通道控制器', spec: '中型场所' },
      { id: '7', name: 'ISC-64 六十四通道控制器', spec: '大型场所' },
      { id: '8', name: 'ISC-N 网络型控制器', spec: '分布式系统' }
    ]
  },
  {
    id: 'sensor',
    name: '气体传感器',
    description: '核心感测元件，采用电化学、催化燃烧、红外等多种检测原理',
    icon: '⚙️',
    features: ['响应迅速', '稳定可靠', '寿命长', '选择性好'],
    image: 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=600&h=400&fit=crop',
    products: [
      { id: '9', name: 'ISS-EC 电化学传感器', spec: '有毒气体' },
      { id: '10', name: 'ISS-CAT 催化燃烧传感器', spec: '可燃气体' },
      { id: '11', name: 'ISS-IR 红外传感器', spec: 'CO₂/碳氢' },
      { id: '12', name: 'ISS-PID 光离子传感器', spec: 'VOC检测' }
    ]
  },
  {
    id: 'fire-alarm',
    name: '火灾报警系统',
    description: '完整的火灾探测与报警解决方案，保障人员与财产安全',
    icon: '🔥',
    features: ['早期预警', '智能分析', '消防联动', '应急广播'],
    image: 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&h=400&fit=crop',
    products: [
      { id: '13', name: 'ISF-S 点型感烟探测器', spec: '烟雾探测' },
      { id: '14', name: 'ISF-T 点型感温探测器', spec: '温度探测' },
      { id: '15', name: 'ISF-B 线型光束感烟', spec: '大空间' },
      { id: '16', name: 'ISF-C 火灾报警控制器', spec: '集中控制' }
    ]
  }
]

function goDetail(id: string) {
  router.push(`/products/${id}`)
}

function goContact() {
  router.push('/contact')
}
</script>

<template>
  <div class="product-list-page">
    <section class="hero-section">
      <div class="container">
        <h1 class="page-title">产品中心</h1>
        <p class="page-desc">二十年专注气体安全检测领域，为您提供从传感器到系统的完整产品线，覆盖工业安全监测的全部需求</p>
      </div>
    </section>

    <section class="categories-section">
      <div class="container">
        <div v-for="(category, index) in categories" :key="category.id" class="category-block">
          <div :class="['category-row', index % 2 === 1 ? 'reverse' : '']">
            <div class="category-image">
              <img :src="category.image" :alt="category.name" class="category-img" />
              <div class="image-overlay"></div>
              <div class="image-icon">{{ category.icon }}</div>
            </div>
            <div class="category-content">
              <h2 class="category-name">{{ category.name }}</h2>
              <p class="category-desc">{{ category.description }}</p>
              <div class="category-features">
                <span v-for="f in category.features" :key="f" class="category-tag">{{ f }}</span>
              </div>
              <div class="category-products">
                <div v-for="p in category.products" :key="p.id" class="category-product-item" @click="goDetail(p.id)">
                  <span class="product-item-name">{{ p.name }}</span>
                  <span class="product-item-spec">{{ p.spec }}</span>
                </div>
              </div>
              <el-button type="primary" round @click="goDetail(category.products[0].id)">查看详情</el-button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="cta-section">
      <div class="container">
        <h2 class="cta-title">找不到合适的产品？</h2>
        <p class="cta-desc">我们的技术专家将根据您的实际需求，为您推荐最合适的产品方案</p>
        <div class="cta-buttons">
          <el-button size="large" round class="btn-white" @click="goContact">联系我们</el-button>
          <el-button size="large" round class="btn-outline-dark">下载产品手册</el-button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.hero-section {
  padding: 120px 0 80px;
  background: linear-gradient(to bottom, #f9fafb, #ffffff);
  text-align: center;
}

.page-title {
  font-size: 44px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 20px;
}

.page-desc {
  font-size: 18px;
  color: #6b7280;
  max-width: 640px;
  margin: 0 auto;
  line-height: 1.7;
}

.categories-section {
  padding: 40px 0 80px;
}

.category-block {
  margin-bottom: 96px;
}

.category-block:last-child {
  margin-bottom: 0;
}

.category-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 64px;
  align-items: center;
}

.category-row.reverse {
  direction: rtl;
}

.category-row.reverse .category-content {
  direction: ltr;
}

.category-image {
  position: relative;
  aspect-ratio: 4/3;
  border-radius: 20px;
  overflow: hidden;
  background: #f3f4f6;
}

.category-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.3), transparent);
}

.image-icon {
  position: absolute;
  bottom: 24px;
  left: 24px;
  width: 56px;
  height: 56px;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(8px);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.category-name {
  font-size: 32px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 16px;
}

.category-desc {
  font-size: 16px;
  color: #6b7280;
  line-height: 1.7;
  margin: 0 0 24px;
}

.category-features {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 32px;
}

.category-tag {
  padding: 6px 16px;
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  font-size: 14px;
  border-radius: 50px;
}

.category-products {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 32px;
}

.category-product-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: #f9fafb;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s;
}

.category-product-item:hover {
  background: #f3f4f6;
}

.product-item-name {
  font-weight: 500;
  color: #111827;
}

.product-item-spec {
  font-size: 13px;
  color: #9ca3af;
}

.cta-section {
  background: #111827;
  padding: 80px 0;
  text-align: center;
}

.cta-title {
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 16px;
}

.cta-desc {
  font-size: 18px;
  color: #9ca3af;
  margin: 0 0 32px;
}

.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.btn-white {
  background: #ffffff;
  color: #111827;
}

.btn-white:hover {
  background: #f0f0f0;
}

.btn-outline-dark {
  border-color: rgba(255,255,255,0.3);
  color: #ffffff;
  background: transparent;
}

.btn-outline-dark:hover {
  border-color: #ffffff;
  background: rgba(255,255,255,0.1);
  color: #ffffff;
}

@media (max-width: 1024px) {
  .category-row {
    grid-template-columns: 1fr;
    gap: 32px;
  }
  .category-row.reverse {
    direction: ltr;
  }
  .page-title {
    font-size: 32px;
  }
  .category-name {
    font-size: 26px;
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 100px 0 48px;
  }
  .page-title {
    font-size: 28px;
  }
  .category-block {
    margin-bottom: 64px;
  }
  .cta-buttons {
    flex-direction: column;
    align-items: center;
  }
}
</style>
