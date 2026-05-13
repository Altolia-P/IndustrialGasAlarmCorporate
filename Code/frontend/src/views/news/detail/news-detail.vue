<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { newsItems } from '@/data/home'
import { contents as sharedContents } from '@/data/content'
import { ContentType, ContentStatus } from '@/types/content'

const router = useRouter()
const route = useRoute()

const allNews = computed(() => {
  const published = sharedContents
    .filter((c) => c.type === ContentType.NEWS && c.status === ContentStatus.PUBLISHED)
    .map((c, idx) => ({
      id: 100000 + idx,
      title: c.title,
      category: c.categoryName,
      date: c.createdAt,
      summary: c.summary,
      body: (c as Record<string, unknown>).body as string || c.summary
    }))
  return [...newsItems, ...published]
})

const news = computed(() => {
  const id = Number(route.params.id)
  return allNews.value.find((n) => n.id === id) || newsItems[0]
})

const relatedNews = computed(() => {
  return allNews.value.filter((n) => n.id !== news.value.id).slice(0, 3)
})

const formattedBody = computed(() => {
  return news.value.body.split('\n').filter((p) => p.trim()).map((p) => p.trim())
})

function goNewsDetail(id: number) {
  router.push(`/news/${id}`)
}

function goBack() {
  router.push('/')
}
</script>

<template>
  <div class="news-detail-page">
    <section class="hero-section">
      <div class="container">
        <div class="hero-breadcrumb">
          <span class="breadcrumb-link" @click="goBack">首页</span>
          <span class="breadcrumb-sep">/</span>
          <span class="breadcrumb-link" @click="goBack">新闻动态</span>
          <span class="breadcrumb-sep">/</span>
          <span class="breadcrumb-current">{{ news.category }}</span>
        </div>
        <div class="hero-badge">{{ news.category }}</div>
        <h1 class="page-title">{{ news.title }}</h1>
        <div class="hero-meta">
          <span class="meta-date">{{ news.date }}</span>
        </div>
      </div>
    </section>

    <section class="content-section">
      <div class="container">
        <div class="content-layout">
          <article class="article-main">
            <div class="article-body">
              <p v-for="(paragraph, i) in formattedBody" :key="i" class="article-paragraph">{{ paragraph }}</p>
            </div>
          </article>

          <aside class="article-sidebar">
            <div class="sidebar-block">
              <h3 class="sidebar-title">相关新闻</h3>
              <div class="related-list">
                <div
                  v-for="item in relatedNews"
                  :key="item.id"
                  class="related-item"
                  @click="goNewsDetail(item.id)"
                >
                  <span class="related-category">{{ item.category }}</span>
                  <h4 class="related-title">{{ item.title }}</h4>
                  <span class="related-date">{{ item.date }}</span>
                </div>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </section>

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
}

.article-paragraph {
  font-size: 16px;
  color: var(--color-gray-700);
  line-height: 1.9;
  margin: 0 0 20px;
  text-align: justify;
}

.article-paragraph:last-child {
  margin-bottom: 0;
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
  .article-paragraph {
    font-size: 15px;
  }
}
</style>
