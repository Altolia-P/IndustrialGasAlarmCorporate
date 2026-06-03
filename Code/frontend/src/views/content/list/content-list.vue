<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { contentApi } from '@/api/content'
import { ContentType, ContentStatus } from '@/types/content'
import type { ContentVO } from '@/types/content'
import { useLoading } from '@/composables/use-loading'

const router = useRouter()

const solutions = ref<ContentVO[]>([])
const { loading, start, stop } = useLoading()

async function fetchSolutions() {
  start()
  try {
    const page = await contentApi.getPublicList({ type: ContentType.SOLUTION, size: 100 })
    solutions.value = page.content.filter((c) => c.status === ContentStatus.PUBLISHED)
  } catch {
    solutions.value = []
  } finally {
    stop()
  }
}

onMounted(() => {
  fetchSolutions()
})

function goDetail(uuid: string) {
  router.push(`/solutions/${uuid}`)
}

function goContact() {
  router.push('/contact')
}
</script>

<template>
  <div class="solutions-page">
    <section class="hero-section">
      <div class="container">
        <h1 class="page-title">解决方案</h1>
        <p class="page-desc">深耕多个工业领域，提供针对性的气体安全监测解决方案</p>
      </div>
    </section>

    <section class="solutions-grid-section">
      <div class="container">
        <div v-if="loading" class="loading-state">
          <p>加载中...</p>
        </div>

        <div v-else-if="solutions.length === 0" class="empty-state">
          <p>暂无解决方案</p>
        </div>

        <div v-else class="solutions-grid">
          <div
            v-for="sol in solutions"
            :key="sol.contentUuid"
            class="solution-card"
            @click="goDetail(sol.contentUuid)"
          >
            <div class="card-image">
              <img
                v-if="sol.coverImage"
                :src="sol.coverImage"
                :alt="sol.title"
                class="card-cover"
              />
              <div v-else class="card-placeholder">
                <span class="placeholder-icon">🔧</span>
              </div>
              <span class="card-category">{{ sol.categoryName }}</span>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ sol.title }}</h3>
              <p class="card-desc">{{ sol.summary }}</p>
              <div class="card-footer">
                <span class="card-date">{{ sol.createdAt }}</span>
                <span class="card-link">了解详情 →</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="cta-section">
      <div class="container">
        <div class="cta-inner">
          <h2 class="cta-title">需要定制化解决方案？</h2>
          <p class="cta-desc">我们的专业团队将深入了解您的需求，为您量身打造最合适的安全监测方案</p>
          <el-button size="large" round class="btn-white" @click="goContact">立即咨询</el-button>
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
  padding: 120px 0 48px;
  background: linear-gradient(to bottom, #f9fafb, #ffffff);
  text-align: center;
}

.page-title {
  font-size: 44px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 16px;
}

.page-desc {
  font-size: 18px;
  color: #6b7280;
}

.solutions-grid-section {
  padding: 0 0 80px;
}

.loading-state,
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  color: #9ca3af;
  font-size: 15px;
}

.solutions-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.solution-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.solution-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
  border-color: #3b82f6;
}

.card-image {
  position: relative;
  height: 180px;
  background: #f3f4f6;
  overflow: hidden;
}

.card-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
}

.placeholder-icon {
  font-size: 48px;
}

.card-category {
  position: absolute;
  top: 12px;
  left: 12px;
  font-size: 12px;
  font-weight: 600;
  color: #3b82f6;
  background: rgba(255, 255, 255, 0.95);
  padding: 4px 12px;
  border-radius: 50px;
}

.card-body {
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 10px;
  line-height: 1.4;
}

.card-desc {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-date {
  font-size: 12px;
  color: #9ca3af;
}

.card-link {
  font-size: 14px;
  font-weight: 500;
  color: #3b82f6;
}

.cta-section {
  padding: 0 0 80px;
}

.cta-inner {
  background: linear-gradient(135deg, #111827, #1e293b);
  border-radius: 24px;
  padding: 64px;
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

.btn-white {
  background: #ffffff;
  color: #111827;
}

.btn-white:hover {
  background: #f0f0f0;
}

@media (max-width: 1024px) {
  .solutions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 100px 0 32px;
  }
  .page-title {
    font-size: 32px;
  }
  .solutions-grid {
    grid-template-columns: 1fr;
  }
  .cta-inner {
    padding: 40px 24px;
  }
}
</style>
