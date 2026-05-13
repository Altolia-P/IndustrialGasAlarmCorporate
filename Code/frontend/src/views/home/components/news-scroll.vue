<script setup lang="ts">
import { useRouter } from 'vue-router'
import { newsItems } from '@/data/home'

const router = useRouter()

function goNewsDetail(id: number) {
  router.push(`/news/${id}`)
}
</script>

<template>
  <section id="news-section" class="section section-news">
    <div class="container">
      <div class="section-header">
        <span class="section-tag">新闻动态</span>
        <h2 class="section-title">最新资讯</h2>
        <p class="section-desc">实时更新公司动态与行业前沿信息，由管理员后台维护发布</p>
      </div>

      <div class="news-scroll-wrapper">
        <div class="news-scroll-track">
          <article
            v-for="(item, index) in newsItems"
            :key="item.id"
            class="news-card"
            :style="{ animationDelay: `${index * 0.12}s` }"
            @click="goNewsDetail(item.id)"
          >
            <div class="news-card-badge">
              <span class="badge-label">{{ item.category }}</span>
              <span class="badge-date">{{ item.date }}</span>
            </div>
            <h3 class="news-title">{{ item.title }}</h3>
            <p class="news-excerpt">{{ item.excerpt }}</p>
            <div class="news-card-footer">
              <span class="news-read-more">阅读全文 →</span>
            </div>
          </article>
        </div>
      </div>

      <div class="news-indicator">
        <span class="news-indicator-text">← 左右滑动查看更多 →</span>
      </div>
    </div>
  </section>
</template>

<style scoped>
.section {
  padding: 80px 0;
}

.section-news {
  background: linear-gradient(180deg, var(--color-brand-hero) 0%, var(--color-brand-deeper) 50%, var(--color-brand-hero) 100%);
  position: relative;
  overflow: hidden;
}

.section-news::before {
  content: '';
  position: absolute;
  top: 0;
  right: -20%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.06), transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}

.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-header {
  text-align: center;
  max-width: 720px;
  margin: 0 auto 64px;
}

.section-tag {
  display: inline-block;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-accent-light);
  text-transform: uppercase;
  letter-spacing: 2px;
  margin-bottom: 16px;
}

.section-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--color-white);
  margin: 0 0 16px;
  line-height: 1.3;
}

.section-desc {
  font-size: 18px;
  color: var(--color-text-muted);
  margin: 0;
  line-height: 1.7;
}

.news-scroll-wrapper {
  position: relative;
  margin: 0 -24px;
  padding: 0 24px;
  overflow-x: auto;
  overflow-y: visible;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
  scrollbar-color: rgba(255, 255, 255, 0.15) transparent;
}

.news-scroll-wrapper::-webkit-scrollbar {
  height: 4px;
}

.news-scroll-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.news-scroll-wrapper::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 4px;
}

.news-scroll-track {
  display: flex;
  gap: 24px;
  padding: 8px 0 24px;
  width: max-content;
}

.news-card {
  width: 340px;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 28px;
  cursor: pointer;
  transition: all 0.35s ease;
  animation: newsSlideIn 0.6s ease backwards;
  position: relative;
  overflow: hidden;
}

.news-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.06), transparent);
  opacity: 0;
  transition: opacity 0.35s;
}

.news-card:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
}

.news-card:hover::after {
  opacity: 1;
}

@keyframes newsSlideIn {
  from {
    opacity: 0;
    transform: translateX(60px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.news-card-badge {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.badge-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-accent-light);
  background: rgba(59, 130, 246, 0.15);
  padding: 4px 12px;
  border-radius: 50px;
}

.badge-date {
  font-size: 12px;
  color: var(--color-text-dim);
}

.news-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-light);
  margin: 0 0 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-excerpt {
  font-size: 14px;
  color: var(--color-text-muted);
  line-height: 1.7;
  margin: 0 0 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-card-footer {
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.news-read-more {
  font-size: 13px;
  color: var(--color-accent-light);
  font-weight: 500;
  transition: all 0.2s;
}

.news-card:hover .news-read-more {
  color: #93c5fd;
  letter-spacing: 0.5px;
}

.news-indicator {
  text-align: center;
  margin-top: 12px;
}

.news-indicator-text {
  font-size: 13px;
  color: var(--color-text-dim);
  letter-spacing: 1px;
}

@media (max-width: 1024px) {
  .news-card {
    width: 280px;
  }
}

@media (max-width: 768px) {
  .section {
    padding: 48px 0;
  }
  .section-title {
    font-size: 28px;
  }
}
</style>
