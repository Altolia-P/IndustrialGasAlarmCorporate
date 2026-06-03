<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { contentApi } from '@/api/content'
import { ContentType, ContentStatus } from '@/types/content'
import type { ContentVO } from '@/types/content'
import { useLoading } from '@/composables/use-loading'

const router = useRouter()

const newsList = ref<ContentVO[]>([])
const { loading, start, stop } = useLoading()

async function fetchNews() {
  start()
  try {
    const page = await contentApi.getPublicList({ type: ContentType.NEWS, size: 100 })
    newsList.value = page.content.filter((c) => c.status === ContentStatus.PUBLISHED)
  } catch {
    newsList.value = []
  } finally {
    stop()
  }
}

onMounted(() => {
  fetchNews()
})

function goDetail(uuid: string) {
  router.push(`/news/${uuid}`)
}
</script>

<template>
  <div class="news-list-page">
    <section class="hero-section">
      <div class="container">
        <h1 class="page-title">新闻动态</h1>
        <p class="page-desc">了解公司最新动态与行业资讯</p>
      </div>
    </section>

    <section class="news-content">
      <div class="container">
        <div v-if="loading" class="loading-state">
          <p>加载中...</p>
        </div>

        <div v-else-if="newsList.length === 0" class="empty-state">
          <p>暂无新闻</p>
        </div>

        <div v-else class="news-grid">
          <article
            v-for="item in newsList"
            :key="item.contentUuid"
            class="news-card"
            @click="goDetail(item.contentUuid)"
          >
            <div class="card-image">
              <img
                v-if="item.coverImage"
                :src="item.coverImage"
                :alt="item.title"
                class="card-cover"
              />
              <div v-else class="card-placeholder">
                <span class="placeholder-text">NEWS</span>
              </div>
            </div>
            <div class="card-body">
              <div class="card-meta">
                <span class="card-category">{{ item.categoryName }}</span>
                <span class="card-date">{{ item.createdAt }}</span>
              </div>
              <h3 class="card-title">{{ item.title }}</h3>
              <p class="card-summary">{{ item.summary }}</p>
              <span class="card-link">阅读全文 →</span>
            </div>
          </article>
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

.news-content {
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

.news-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.news-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.news-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
  border-color: #3b82f6;
}

.card-image {
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

.placeholder-text {
  font-size: 20px;
  font-weight: 700;
  color: #93c5fd;
  letter-spacing: 4px;
}

.card-body {
  padding: 20px;
}

.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.card-category {
  font-size: 12px;
  font-weight: 600;
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.08);
  padding: 2px 10px;
  border-radius: 50px;
}

.card-date {
  font-size: 12px;
  color: #9ca3af;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 10px;
  line-height: 1.4;
}

.card-summary {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-link {
  font-size: 14px;
  font-weight: 500;
  color: #3b82f6;
}

@media (max-width: 1024px) {
  .news-grid {
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
  .news-grid {
    grid-template-columns: 1fr;
  }
}
</style>
