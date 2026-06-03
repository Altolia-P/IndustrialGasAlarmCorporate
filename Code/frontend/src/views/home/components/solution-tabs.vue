<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { solutionTabs } from '@/data/home'

const router = useRouter()
const activeTab = ref(solutionTabs[0].id)
const currentSolution = ref(solutionTabs[0])

function switchTab(id: string) {
  activeTab.value = id
  currentSolution.value = solutionTabs.find((s) => s.id === id)!
}

function goDetail() {
  router.push(`/solutions/${currentSolution.value.id}`)
}
</script>

<template>
  <section class="section section-solutions">
    <div class="container">
      <div class="section-header">
        <span class="section-tag">解决方案</span>
        <h2 class="section-title">行业定制化安全方案</h2>
        <p class="section-desc">深耕多个工业领域，提供针对性的气体安全监测解决方案</p>
      </div>
      <div class="solutions-layout">
        <div class="solutions-tabs">
          <div class="solutions-tabs-scroll">
            <button
              v-for="tab in solutionTabs"
              :key="tab.id"
              :class="['solution-tab', { active: activeTab === tab.id }]"
              @click="switchTab(tab.id)"
            >
              <span class="tab-icon">{{ tab.icon }}</span>
              <span class="tab-name">{{ tab.name }}</span>
            </button>
          </div>
        </div>
        <div class="solutions-content">
          <Transition name="fade-slide" mode="out-in">
            <div :key="activeTab" class="solution-panel">
              <h3 class="solution-title">{{ currentSolution.title }}</h3>
              <p class="solution-desc">{{ currentSolution.description }}</p>
              <div class="solution-features">
                <div v-for="(f, i) in currentSolution.features" :key="i" class="solution-feature-item">
                  <span class="feature-dot"></span>
                  {{ f }}
                </div>
              </div>
              <div class="solution-stats">
                <div class="solution-stat">
                  <span class="stat-num">{{ currentSolution.projects }}</span>
                  <span class="stat-text">成功项目</span>
                </div>
                <div class="solution-stat">
                  <span class="stat-num">{{ currentSolution.clients }}</span>
                  <span class="stat-text">服务客户</span>
                </div>
                <el-button type="primary" size="large" round @click="goDetail">了解详情</el-button>
              </div>
            </div>
          </Transition>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.section {
  padding: 80px 0;
}

.section-solutions {
  background: linear-gradient(180deg, #ffffff 0%, #eef2f8 50%, #f0f4fa 100%);
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

.solutions-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 32px;
  align-items: stretch;
}

.solutions-tabs {
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.solutions-tabs-scroll {
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow-y: auto;
  max-height: 100%;
  padding-right: 4px;
}

.solutions-tabs-scroll::-webkit-scrollbar {
  width: 4px;
}

.solutions-tabs-scroll::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 2px;
}

.solution-tab {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border: none;
  border-radius: 12px;
  background: var(--color-gray-100);
  cursor: pointer;
  transition: all 0.3s;
  font-size: 15px;
  color: var(--color-gray-600);
  width: 100%;
  text-align: left;
}

.solution-tab:hover {
  background: var(--color-gray-200);
}

.solution-tab.active {
  background: var(--color-brand-dark);
  color: #ffffff;
  box-shadow: 0 8px 24px rgba(26, 54, 93, 0.35);
}

.tab-icon {
  font-size: 24px;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: #ffffff;
  flex-shrink: 0;
}

.solution-tab.active .tab-icon {
  background: rgba(255, 255, 255, 0.2);
}

.tab-name {
  font-weight: 600;
}

.solutions-content {
  background: #ffffff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.04);
}

.solution-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-gray-900);
  margin: 0 0 16px;
}

.solution-desc {
  font-size: 16px;
  color: var(--color-gray-600);
  margin: 0 0 32px;
  line-height: 1.7;
}

.solution-features {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 32px;
}

.solution-feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--color-gray-700);
  font-size: 15px;
}

.feature-dot {
  width: 8px;
  height: 8px;
  background: var(--color-primary);
  border-radius: 50%;
  flex-shrink: 0;
}

.solution-stats {
  display: flex;
  align-items: center;
  gap: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--color-gray-200);
}

.solution-stat {
  display: flex;
  flex-direction: column;
}

.stat-num {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-primary);
}

.stat-text {
  font-size: 13px;
  color: var(--color-gray-500);
  margin-top: 2px;
}

@media (max-width: 1024px) {
  .solutions-layout {
    grid-template-columns: 1fr;
  }
  .solutions-tabs {
    flex-direction: row;
  }
  .solutions-tabs-scroll {
    flex-direction: row;
    overflow-x: auto;
    overflow-y: hidden;
    max-height: none;
    padding-right: 0;
    padding-bottom: 4px;
  }
  .solution-tab {
    white-space: nowrap;
    flex-shrink: 0;
  }
}

@media (max-width: 768px) {
  .section {
    padding: 48px 0;
  }
  .section-title {
    font-size: 28px;
  }
  .solutions-content {
    padding: 24px;
  }
  .solution-features {
    grid-template-columns: 1fr;
  }
  .solution-stats {
    flex-wrap: wrap;
    gap: 16px;
  }
}

/* Transition */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(12px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-12px);
}
</style>
