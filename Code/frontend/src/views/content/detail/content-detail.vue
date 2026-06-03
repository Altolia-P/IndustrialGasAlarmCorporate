<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import DOMPurify from 'dompurify'
import { contentApi } from '@/api/content'
import { ContentType } from '@/types/content'
import type { ContentDetailVO, ContentVO } from '@/types/content'

const router = useRouter()
const route = useRoute()

const solution = ref<ContentDetailVO | null>(null)
const relatedSolutions = ref<ContentVO[]>([])
const loading = ref(false)
const loadingRelated = ref(false)

async function fetchDetail() {
  loading.value = true
  try {
    const data = await contentApi.getPublicDetail(route.params.uuid as string)
    solution.value = { ...data, body: DOMPurify.sanitize(data.body) }
  } catch {
    solution.value = null
  } finally {
    loading.value = false
  }
}

async function fetchRelated() {
  loadingRelated.value = true
  try {
    const page = await contentApi.getPublicList({ type: ContentType.SOLUTION, size: 4 })
    relatedSolutions.value = page.content
      .filter((c) => c.contentUuid !== route.params.uuid)
      .slice(0, 3)
  } catch {
    relatedSolutions.value = []
  } finally {
    loadingRelated.value = false
  }
}

onMounted(() => {
  fetchDetail()
  fetchRelated()
})

function goSolution(uuid: string) {
  router.push(`/solutions/${uuid}`)
}

function goList() {
  router.push('/solutions')
}

function goContact() {
  router.push('/contact')
}
</script>

<template>
  <div class="solution-detail-page">
    <div v-if="loading" class="loading-state">
      <p>加载中...</p>
    </div>

    <div v-else-if="!solution" class="error-state">
      <p>解决方案不存在或已删除</p>
      <el-button size="large" round @click="goList">返回列表</el-button>
    </div>

    <template v-else>
      <section class="hero-section">
        <div class="container">
          <div class="hero-breadcrumb">
            <span class="breadcrumb-link" @click="goList">解决方案</span>
            <span class="breadcrumb-sep">/</span>
            <span class="breadcrumb-current">{{ solution.categoryName }}</span>
          </div>
          <div class="hero-badge">行业解决方案</div>
          <h1 class="page-title">{{ solution.title }}</h1>
          <div class="hero-meta">
            <span class="meta-date">{{ solution.createdAt }}</span>
          </div>
        </div>
      </section>

      <section class="content-section">
        <div class="container">
          <div class="content-layout">
            <article class="article-main">
              <div class="article-body" v-html="solution.body" />
            </article>

            <aside class="article-sidebar">
              <div class="sidebar-block">
                <h3 class="sidebar-title">相关方案</h3>
                <div v-if="loadingRelated" class="sidebar-loading">
                  <p>加载中...</p>
                </div>
                <div v-else-if="relatedSolutions.length === 0" class="sidebar-empty">
                  <p>暂无相关方案</p>
                </div>
                <div v-else class="related-list">
                  <div
                    v-for="item in relatedSolutions"
                    :key="item.contentUuid"
                    class="related-item"
                    @click="goSolution(item.contentUuid)"
                  >
                    <span class="related-category">{{ item.categoryName }}</span>
                    <h4 class="related-title">{{ item.title }}</h4>
                    <span class="related-date">{{ item.createdAt }}</span>
                  </div>
                </div>
              </div>

              <div class="sidebar-cta">
                <h3 class="cta-title">需要定制方案？</h3>
                <p class="cta-desc">我们的行业专家将为您量身打造专属解决方案</p>
                <el-button type="primary" round class="btn-full" @click="goContact">在线咨询</el-button>
              </div>
            </aside>
          </div>
        </div>
      </section>
    </template>
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
  color: #9ca3af;
  font-size: 15px;
  gap: 16px;
}

.hero-section {
  padding: 120px 0 48px;
  background: linear-gradient(180deg, #f0f9ff, #e0f2fe);
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
  color: #6b7280;
  cursor: pointer;
  transition: color 0.2s;
}

.breadcrumb-link:hover {
  color: #3b82f6;
}

.breadcrumb-sep {
  color: #d1d5db;
}

.breadcrumb-current {
  color: #111827;
}

.hero-badge {
  display: inline-block;
  font-size: 13px;
  font-weight: 600;
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.1);
  padding: 6px 18px;
  border-radius: 50px;
  margin-bottom: 24px;
}

.page-title {
  font-size: 38px;
  font-weight: 700;
  color: #111827;
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
  color: #9ca3af;
}

.content-section {
  padding: 64px 0 80px;
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
  color: #4b5563;
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

.article-body :deep(h2) {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 32px 0 16px;
}

.article-body :deep(h3) {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 24px 0 12px;
}

.article-body :deep(ul),
.article-body :deep(ol) {
  margin: 0 0 20px;
  padding-left: 24px;
}

.article-body :deep(li) {
  margin-bottom: 8px;
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
  margin-bottom: 20px;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.sidebar-loading,
.sidebar-empty {
  color: #9ca3af;
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
  background: #f9fafb;
  border-color: #e5e7eb;
}

.related-category {
  display: inline-block;
  font-size: 11px;
  font-weight: 600;
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.08);
  padding: 2px 10px;
  border-radius: 50px;
  margin-bottom: 8px;
}

.related-title {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 6px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.related-date {
  font-size: 12px;
  color: #9ca3af;
}

.sidebar-cta {
  background: linear-gradient(135deg, #111827, #1e293b);
  border-radius: 16px;
  padding: 28px;
  text-align: center;
}

.sidebar-cta .cta-title {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 8px;
}

.sidebar-cta .cta-desc {
  font-size: 14px;
  color: #9ca3af;
  margin: 0 0 20px;
  line-height: 1.6;
}

.btn-full {
  width: 100%;
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
