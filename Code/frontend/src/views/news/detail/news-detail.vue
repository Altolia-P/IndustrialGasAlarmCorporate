<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import DOMPurify from 'dompurify'
import { contentApi } from '@/api/content'
import { ContentType } from '@/types/content'
import type { ContentDetailVO, ContentVO } from '@/types/content'

const router = useRouter()
const route = useRoute()

const news = ref<ContentDetailVO | null>(null)
const relatedNews = ref<ContentVO[]>([])
const loading = ref(false)
const loadingRelated = ref(false)

async function fetchDetail() {
  loading.value = true
  try {
    const data = await contentApi.getPublicDetail(route.params.uuid as string)
    news.value = { ...data, body: DOMPurify.sanitize(data.body) }
  } catch {
    news.value = null
  } finally {
    loading.value = false
  }
}

async function fetchRelated() {
  loadingRelated.value = true
  try {
    const page = await contentApi.getPublicList({ type: ContentType.NEWS, size: 4 })
    relatedNews.value = page.content.filter((c) => c.contentUuid !== route.params.uuid).slice(0, 3)
  } catch {
    relatedNews.value = []
  } finally {
    loadingRelated.value = false
  }
}

onMounted(() => {
  fetchDetail()
  fetchRelated()
})

function goNewsDetail(uuid: string) {
  router.push(`/news/${uuid}`)
}

function goBack() {
  router.push('/')
}
</script>

<template>
  <div class="news-detail-page">
    <div v-if="loading" class="loading-state">
      <p>加载中...</p>
    </div>

    <div v-else-if="!news" class="error-state">
      <p>新闻不存在或已删除</p>
      <el-button size="large" round @click="goBack">返回首页</el-button>
    </div>

    <template v-else>
      <section class="hero-section">
        <div class="container">
          <div class="hero-breadcrumb">
            <span class="breadcrumb-link" @click="goBack">首页</span>
            <span class="breadcrumb-sep">/</span>
            <span class="breadcrumb-link" @click="goBack">新闻动态</span>
            <span class="breadcrumb-sep">/</span>
            <span class="breadcrumb-current">{{ news.categoryName }}</span>
          </div>
          <div class="hero-badge">{{ news.categoryName }}</div>
          <h1 class="page-title">{{ news.title }}</h1>
          <div class="hero-meta">
            <span class="meta-date">{{ news.createdAt }}</span>
          </div>
        </div>
      </section>

      <section class="content-section">
        <div class="container">
          <div class="content-layout">
            <article class="article-main">
              <div class="article-body" v-html="news.body" />
            </article>

            <aside class="article-sidebar">
              <div class="sidebar-block">
                <h3 class="sidebar-title">相关新闻</h3>
                <div v-if="loadingRelated" class="sidebar-loading">
                  <p>加载中...</p>
                </div>
                <div v-else-if="relatedNews.length === 0" class="sidebar-empty">
                  <p>暂无相关新闻</p>
                </div>
                <div v-else class="related-list">
                  <div
                    v-for="item in relatedNews"
                    :key="item.contentUuid"
                    class="related-item"
                    @click="goNewsDetail(item.contentUuid)"
                  >
                    <span class="related-category">{{ item.categoryName }}</span>
                    <h4 class="related-title">{{ item.title }}</h4>
                    <span class="related-date">{{ item.createdAt }}</span>
                  </div>
                </div>
              </div>
            </aside>
          </div>
        </div>
      </section>
    </template>

    <section class="back-section">
      <div class="container">
        <el-button size="large" round @click="goBack">返回首页</el-button>
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

.loading-state,
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120px 24px;
  color: var(--color-text-muted);
  font-size: 15px;
  gap: 16px;
}

.hero-section {
  padding: 120px 0 48px;
  background: linear-gradient(180deg, var(--color-brand-hero) 0%, var(--color-brand-deeper) 100%);
  text-align: center;
}

.hero-breadcrumb {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 24px;
  font-size: 14px;
}

.breadcrumb-link {
  color: var(--color-text-muted);
  cursor: pointer;
  transition: color 0.2s;
}

.breadcrumb-link:hover {
  color: var(--color-accent-light);
}

.breadcrumb-sep {
  color: var(--color-text-dim);
}

.breadcrumb-current {
  color: var(--color-text-light);
}

.hero-badge {
  display: inline-block;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-accent-light);
  background: rgba(59, 130, 246, 0.15);
  padding: 6px 18px;
  border-radius: 50px;
  margin-bottom: 24px;
}

.page-title {
  font-size: 38px;
  font-weight: 700;
  color: var(--color-white);
  margin: 0 0 20px;
  line-height: 1.4;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}

.hero-meta {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
}

.meta-date {
  font-size: 14px;
  color: var(--color-text-muted);
}

.content-section {
  padding: 64px 0;
  background: #f9fafb;
}

.content-layout {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 40px;
  align-items: start;
}

.article-main {
  background: #ffffff;
  border-radius: 16px;
  padding: 48px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.04);
}

.article-body {
  max-width: 720px;
  font-size: 16px;
  color: var(--color-gray-700);
  line-height: 1.9;
}

.article-body :deep(p) {
  margin: 0 0 20px;
}

.article-body :deep(p:last-child) {
  margin-bottom: 0;
}

.article-body :deep(img) {
  max-width: 100%;
  border-radius: 8px;
}

.article-sidebar {
  position: sticky;
  top: 24px;
}

.sidebar-block {
  background: #ffffff;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.04);
}

.sidebar-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-gray-900);
  margin: 0 0 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--color-gray-200);
}

.sidebar-loading,
.sidebar-empty {
  color: var(--color-gray-400);
  font-size: 14px;
  text-align: center;
  padding: 20px 0;
}

.related-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.related-item {
  padding: 14px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.25s;
  border: 1px solid transparent;
}

.related-item:hover {
  background: var(--color-gray-50);
  border-color: var(--color-gray-200);
}

.related-category {
  display: inline-block;
  font-size: 11px;
  font-weight: 600;
  color: var(--color-primary);
  background: rgba(59, 130, 246, 0.08);
  padding: 2px 10px;
  border-radius: 50px;
  margin-bottom: 8px;
}

.related-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-gray-900);
  margin: 0 0 6px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.related-date {
  font-size: 12px;
  color: var(--color-gray-400);
}

.back-section {
  padding: 48px 0 80px;
  background: #f9fafb;
  text-align: center;
}

@media (max-width: 1024px) {
  .content-layout {
    grid-template-columns: 1fr;
  }
  .article-sidebar {
    position: static;
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 100px 0 36px;
  }
  .page-title {
    font-size: 26px;
  }
  .article-main {
    padding: 28px 20px;
  }
}
</style>
