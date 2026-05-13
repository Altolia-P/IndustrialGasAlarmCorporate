<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { solutions, type SolutionData } from '@/data/solutions'
import { contents as sharedContents } from '@/data/content'
import { ContentType, ContentStatus } from '@/types/content'

const router = useRouter()

const activeId = ref(solutions[0].id)

const allSolutions = computed<SolutionData[]>(() => {
  const published = sharedContents
    .filter((c) => c.type === ContentType.SOLUTION && c.status === ContentStatus.PUBLISHED)
    .map((c) => ({
      id: c.contentUuid,
      name: c.title,
      icon: '🔧',
      description: c.summary,
      detail: {
        title: c.title,
        description: c.summary,
        features: [] as string[],
        stats: { projects: '-', clients: '-' }
      }
    } satisfies SolutionData))
  const existingIds = new Set(solutions.map((s) => s.id))
  const newOnes = published.filter((s) => !existingIds.has(s.id))
  return [...solutions, ...newOnes]
})

const currentSolution = computed(() => {
  return allSolutions.value.find((s) => s.id === activeId.value) || allSolutions.value[0]
})

function switchTab(id: string) {
  activeId.value = id
}

function goDetail(id: string) {
  router.push(`/solutions/${id}`)
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

    <section class="solutions-detail">
      <div class="container">
        <div class="solutions-layout">
          <div class="solution-tabs">
            <button
              v-for="sol in allSolutions"
              :key="sol.id"
              :class="['solution-tab-btn', { active: activeId === sol.id }]"
              @click="switchTab(sol.id)"
            >
              <span class="tab-icon-circle">{{ sol.icon }}</span>
              <span class="tab-label">{{ sol.name }}</span>
            </button>
          </div>
          <div class="solution-content">
            <h2 class="solution-title">{{ currentSolution.detail.title }}</h2>
            <p class="solution-desc">{{ currentSolution.detail.description }}</p>

            <div class="feature-grid">
              <div v-for="(f, i) in currentSolution.detail.features" :key="i" class="feature-item">
                <span class="feature-check">✓</span>
                <span>{{ f }}</span>
              </div>
            </div>

            <div class="solution-footer">
              <div class="solution-stat">
                <span class="stat-big">{{ currentSolution.detail.stats.projects }}</span>
                <span class="stat-small">成功项目</span>
              </div>
              <div class="solution-stat">
                <span class="stat-big">{{ currentSolution.detail.stats.clients }}</span>
                <span class="stat-small">服务客户</span>
              </div>
              <div class="solution-actions">
                <el-button type="primary" size="large" round @click="goDetail(currentSolution.id)">
                  了解详情
                </el-button>
                <el-button size="large" round class="btn-outline-blue" @click="goContact">
                  在线咨询
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="all-solutions">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">全部行业方案</h2>
          <p class="section-desc">每种方案都经过行业验证，可灵活适配不同规模的项目需求</p>
        </div>
        <div class="solutions-grid">
          <div v-for="sol in allSolutions" :key="sol.id" class="solution-card" @click="goDetail(sol.id)">
            <div class="card-icon">{{ sol.icon }}</div>
            <h3 class="card-name">{{ sol.name }}</h3>
            <p class="card-desc">{{ sol.description }}</p>
            <div class="card-stats">
              <span><strong>{{ sol.detail.stats.projects }}</strong> 项目</span>
              <span><strong>{{ sol.detail.stats.clients }}</strong> 客户</span>
            </div>
            <span class="card-link">查看方案 →</span>
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

.solutions-detail {
  padding: 0 0 80px;
}

.solutions-layout {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 40px;
  align-items: start;
  background: #f9fafb;
  border-radius: 20px;
  padding: 40px;
}

.solution-tabs {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.solution-tab-btn {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 20px;
  border: none;
  border-radius: 12px;
  background: #ffffff;
  cursor: pointer;
  font-size: 15px;
  color: #4b5563;
  transition: all 0.3s;
  width: 100%;
  text-align: left;
}

.solution-tab-btn:hover {
  background: #e5e7eb;
}

.solution-tab-btn.active {
  background: #3b82f6;
  color: #ffffff;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.3);
}

.tab-icon-circle {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: #f3f4f6;
  font-size: 20px;
  flex-shrink: 0;
}

.solution-tab-btn.active .tab-icon-circle {
  background: rgba(255, 255, 255, 0.2);
}

.tab-label {
  font-weight: 600;
}

.solution-content {
  padding: 8px 0;
}

.solution-title {
  font-size: 26px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 16px;
}

.solution-desc {
  font-size: 16px;
  color: #6b7280;
  line-height: 1.8;
  margin: 0 0 28px;
}

.feature-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 32px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  color: #374151;
}

.feature-check {
  width: 22px;
  height: 22px;
  background: #3b82f6;
  color: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}

.solution-footer {
  display: flex;
  align-items: center;
  gap: 40px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.solution-stat {
  display: flex;
  flex-direction: column;
}

.stat-big {
  font-size: 28px;
  font-weight: 700;
  color: #3b82f6;
}

.stat-small {
  font-size: 13px;
  color: #9ca3af;
  margin-top: 2px;
}

.solution-actions {
  display: flex;
  gap: 12px;
  margin-left: auto;
}

.btn-outline-blue {
  border-color: #3b82f6;
  color: #3b82f6;
}

.all-solutions {
  padding: 80px 0;
}

.section-header {
  text-align: center;
  margin-bottom: 48px;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 12px;
}

.section-desc {
  font-size: 18px;
  color: #6b7280;
  margin: 0;
}

.solutions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.solution-card {
  background: #f9fafb;
  border-radius: 16px;
  padding: 32px;
  cursor: pointer;
  transition: all 0.3s;
}

.solution-card:hover {
  background: #3b82f6;
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(59, 130, 246, 0.1);
}

.card-icon {
  font-size: 36px;
  margin-bottom: 16px;
}

.card-name {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 12px;
  transition: color 0.3s;
}

.solution-card:hover .card-name {
  color: #ffffff;
}

.card-desc {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0 0 20px;
  transition: color 0.3s;
}

.solution-card:hover .card-desc {
  color: rgba(255, 255, 255, 0.8);
}

.card-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
  font-size: 13px;
  color: #9ca3af;
  transition: color 0.3s;
}

.solution-card:hover .card-stats {
  color: rgba(255, 255, 255, 0.7);
}

.card-stats strong {
  color: #3b82f6;
  transition: color 0.3s;
}

.solution-card:hover .card-stats strong {
  color: #ffffff;
}

.card-link {
  font-size: 14px;
  font-weight: 500;
  color: #3b82f6;
  transition: color 0.3s;
}

.solution-card:hover .card-link {
  color: #ffffff;
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
  .solutions-layout {
    grid-template-columns: 1fr;
    padding: 24px;
  }
  .solution-tabs {
    flex-direction: row;
    overflow-x: auto;
  }
  .solution-tab-btn {
    white-space: nowrap;
    flex-shrink: 0;
  }
  .solutions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .feature-grid {
    grid-template-columns: 1fr;
  }
  .solution-footer {
    flex-wrap: wrap;
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
