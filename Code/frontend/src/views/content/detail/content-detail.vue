<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { solutionDetailMap } from '@/data/solutions'

const router = useRouter()
const route = useRoute()

const data = computed(() => {
  const id = (route.params.uuid as string) || 'petrochemical'
  return solutionDetailMap[id] || solutionDetailMap.petrochemical
})

function goContact() {
  router.push('/contact')
}
</script>

<template>
  <div class="solution-detail-page">
    <section class="hero-section">
      <div class="container">
        <div class="hero-badge">
          <span class="badge-icon">{{ data.icon }}</span>
          行业解决方案
        </div>
        <h1 class="page-title">{{ data.title }}</h1>
        <p class="page-desc">{{ data.description }}</p>
      </div>
    </section>

    <section class="challenges-section">
      <div class="container">
        <h2 class="section-title">行业挑战</h2>
        <div class="challenges-grid">
          <div v-for="c in data.challenges" :key="c.title" class="challenge-card">
            <h3 class="challenge-name">{{ c.title }}</h3>
            <p class="challenge-desc">{{ c.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="solution-section section-gray">
      <div class="container">
        <h2 class="section-title">解决方案</h2>
        <div class="solution-steps">
          <div v-for="(s, i) in data.solution" :key="s.title" class="solution-step">
            <div class="step-number">{{ i + 1 }}</div>
            <div class="step-content">
              <h3 class="step-title">{{ s.title }}</h3>
              <p class="step-desc">{{ s.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="benefits-section">
      <div class="container">
        <h2 class="section-title">方案优势</h2>
        <div class="benefits-grid">
          <div v-for="b in data.benefits" :key="b.title" class="benefit-card">
            <span class="benefit-icon">{{ b.icon }}</span>
            <h3 class="benefit-name">{{ b.title }}</h3>
            <p class="benefit-desc">{{ b.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <section v-if="data.cases.length" class="section-gray">
      <div class="container">
        <h2 class="section-title">典型案例</h2>
        <div class="cases-grid">
          <div v-for="c in data.cases" :key="c.name" class="case-card">
            <h3 class="case-name">{{ c.name }}</h3>
            <p class="case-desc">{{ c.desc }}</p>
            <div class="case-result">
              <span class="result-label">项目成果</span>
              <span class="result-value">{{ c.result }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="cta-section">
      <div class="container">
        <div class="cta-inner">
          <h2 class="cta-title">获取定制化行业方案</h2>
          <p class="cta-desc">留下您的需求，我们的行业专家将为您量身定制专属方案</p>
          <div class="cta-actions">
            <el-button size="large" round class="btn-white" @click="goContact">在线咨询</el-button>
            <el-button size="large" round class="btn-outline-white" @click="goContact">预约演示</el-button>
          </div>
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
  padding: 120px 0 64px;
  background: linear-gradient(to bottom, #f9fafb, #ffffff);
  text-align: center;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 50px;
  color: #3b82f6;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 24px;
}

.badge-icon {
  font-size: 18px;
}

.page-title {
  font-size: 42px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 20px;
}

.page-desc {
  font-size: 18px;
  color: #6b7280;
  max-width: 800px;
  margin: 0 auto;
  line-height: 1.8;
}

.section-gray {
  background: #f9fafb;
}

.challenges-section,
.solution-section,
.benefits-section,
.section-gray {
  padding: 80px 0;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 48px;
  text-align: center;
}

.challenges-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.challenge-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 32px;
  transition: box-shadow 0.3s;
}

.challenge-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.challenge-name {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 12px;
}

.challenge-desc {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
  line-height: 1.7;
}

.solution-steps {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.solution-step {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.step-number {
  width: 48px;
  height: 48px;
  background: #3b82f6;
  color: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  flex-shrink: 0;
}

.step-title {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 8px;
}

.step-desc {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
  line-height: 1.7;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.benefit-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 32px;
  text-align: center;
  transition: transform 0.3s, box-shadow 0.3s;
}

.benefit-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
}

.benefit-icon {
  font-size: 36px;
  display: block;
  margin-bottom: 16px;
}

.benefit-name {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 12px;
}

.benefit-desc {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
  line-height: 1.6;
}

.cases-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.case-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  transition: all 0.3s;
}

.case-card:hover {
  background: #3b82f6;
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(59, 130, 246, 0.15);
}

.case-name {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 12px;
  transition: color 0.3s;
}

.case-card:hover .case-name {
  color: #ffffff;
}

.case-desc {
  font-size: 15px;
  color: #6b7280;
  margin: 0 0 20px;
  line-height: 1.6;
  transition: color 0.3s;
}

.case-card:hover .case-desc {
  color: rgba(255, 255, 255, 0.8);
}

.case-result {
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
  transition: border-color 0.3s;
}

.case-card:hover .case-result {
  border-color: rgba(255, 255, 255, 0.2);
}

.result-label {
  display: block;
  font-size: 13px;
  color: #9ca3af;
  margin-bottom: 4px;
  transition: color 0.3s;
}

.case-card:hover .result-label {
  color: rgba(255, 255, 255, 0.6);
}

.result-value {
  font-size: 18px;
  font-weight: 600;
  color: #3b82f6;
  transition: color 0.3s;
}

.case-card:hover .result-value {
  color: #ffffff;
}

.cta-section {
  padding: 80px 0;
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

.cta-actions {
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

.btn-outline-white {
  border-color: rgba(255, 255, 255, 0.5);
  color: #ffffff;
  background: transparent;
}

.btn-outline-white:hover {
  border-color: #ffffff;
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 100px 0 40px;
  }
  .page-title {
    font-size: 32px;
  }
  .challenges-grid,
  .benefits-grid,
  .cases-grid {
    grid-template-columns: 1fr;
  }
  .section-title {
    font-size: 26px;
  }
  .cta-inner {
    padding: 40px 24px;
  }
  .cta-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>
