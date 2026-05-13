<script setup lang="ts">
import { clientLogos, cases } from '@/data/home'
</script>

<template>
  <section class="section section-clients">
    <div class="container">
      <div class="section-header">
        <span class="section-tag">客户案例</span>
        <h2 class="section-title">服务众多行业领先企业</h2>
        <p class="section-desc">与众多央企、国企及行业龙头建立长期合作关系</p>
      </div>
      <div class="clients-row">
        <div v-for="client in clientLogos" :key="client" class="client-item">
          <span class="client-name">{{ client }}</span>
        </div>
      </div>
      <div class="cases-grid">
        <div v-for="c in cases" :key="c.title" class="case-card">
          <span class="case-industry">{{ c.industry }}</span>
          <h3 class="case-title">{{ c.title }}</h3>
          <p class="case-desc">{{ c.description }}</p>
          <div class="case-result">
            <span class="result-label">项目成果</span>
            <span class="result-value">{{ c.result }}</span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.section {
  padding: 80px 0;
}

.section-clients {
  background: #f9fafb;
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

.clients-row {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 12px;
  margin-bottom: 48px;
}

.client-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 56px;
  border-radius: 8px;
  padding: 0 10px;
  background: #ffffff;
  border: 1px solid var(--color-gray-200);
  transition: all 0.3s;
}

.client-item:hover {
  border-color: var(--color-primary);
  box-shadow: 0 2px 12px rgba(59, 130, 246, 0.1);
}

.client-name {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-gray-700);
}

.cases-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.case-card {
  background: #ffffff;
  border-radius: 14px;
  padding: 28px;
  border: 1px solid #e5e7eb;
  transition: all 0.3s;
}

.case-card:hover {
  border-color: var(--color-primary);
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(59, 130, 246, 0.12);
}

.case-industry {
  display: inline-block;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 14px;
  border-radius: 50px;
  margin-bottom: 16px;
  color: var(--color-primary);
  background: rgba(59, 130, 246, 0.08);
}

.case-title {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 12px;
}

.case-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 20px;
  line-height: 1.6;
}

.case-result {
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.result-label {
  display: block;
  font-size: 13px;
  color: #9ca3af;
  margin-bottom: 4px;
}

.result-value {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary);
}

@media (max-width: 1024px) {
  .clients-row {
    grid-template-columns: repeat(4, 1fr);
  }
  .cases-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .section {
    padding: 48px 0;
  }
  .section-title {
    font-size: 28px;
  }
  .clients-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
