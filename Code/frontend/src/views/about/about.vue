<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const timeline = [
  { year: '2005', title: '公司成立', desc: 'InterSense在深圳成立，聚焦工业气体检测领域' },
  { year: '2008', title: '首获认证', desc: '通过ISO9001质量管理体系认证，推出首款固定式气体探测器' },
  { year: '2012', title: '全国布局', desc: '北京、上海、成都分公司相继成立，服务覆盖全国' },
  { year: '2016', title: '技术突破', desc: '自主研发红外气体传感器，打破国外技术垄断' },
  { year: '2020', title: '智能升级', desc: '推出智慧燃气云平台，进入物联网安全监测新阶段' },
  { year: '2024', title: '全球发展', desc: '产品远销30+国家和地区，服务5000+企业客户' }
]

const values = [
  { icon: '🎯', title: '使命', desc: '以科技守护生命与财产安全，让每一处工业场所都拥有可靠的气体安全防护' },
  { icon: '🔭', title: '愿景', desc: '成为全球领先的工业气体安全监测解决方案提供商' },
  { icon: '💎', title: '价值观', desc: '诚信为本、创新驱动、品质至上、客户第一、合作共赢' }
]

const honors = [
  { year: '2024', title: '工业安全技术创新奖' },
  { year: '2023', title: '国家级专精特新"小巨人"企业' },
  { year: '2022', title: '广东省科技进步二等奖' },
  { year: '2021', title: '中国气体检测行业十大品牌' },
  { year: '2020', title: '国家高新技术企业认定' },
  { year: '2019', title: 'CE/ATEX国际防爆认证' }
]

function goContact() {
  router.push('/contact')
}
</script>

<template>
  <div class="about-page">
    <section class="hero-section">
      <div class="container">
        <span class="section-tag">About Us</span>
        <h1 class="page-title">关于 InterSense</h1>
        <p class="page-desc">专注工业气体安全检测20年，从深圳出发走向全球，致力于为客户提供可靠的安全监测解决方案</p>
        <div class="hero-stats">
          <div class="hero-stat">
            <span class="stat-num">20+</span>
            <span class="stat-label">年行业深耕</span>
          </div>
          <div class="hero-stat">
            <span class="stat-num">5000+</span>
            <span class="stat-label">服务企业客户</span>
          </div>
          <div class="hero-stat">
            <span class="stat-num">30+</span>
            <span class="stat-label">远销国家地区</span>
          </div>
        </div>
      </div>
    </section>

    <section id="values" class="values-section">
      <div class="container">
        <div class="values-grid">
          <div v-for="v in values" :key="v.icon" class="value-card">
            <div class="value-icon">{{ v.icon }}</div>
            <h3 class="value-title">{{ v.title }}</h3>
            <p class="value-desc">{{ v.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="timeline-section">
      <div class="container">
        <div class="section-header">
          <span class="section-tag">Milestones</span>
          <h2 class="section-title">发展历程</h2>
          <p class="section-desc">二十载砥砺前行，每一步都凝聚着对安全的不懈追求</p>
        </div>
        <div class="timeline">
          <div v-for="(item, i) in timeline" :key="item.year" class="timeline-item" :class="{ right: i % 2 === 1 }">
            <div class="timeline-dot"></div>
            <div class="timeline-content">
              <span class="timeline-year">{{ item.year }}</span>
              <h3 class="timeline-title">{{ item.title }}</h3>
              <p class="timeline-desc">{{ item.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section id="honors" class="honors-section">
      <div class="container">
        <div class="section-header">
          <span class="section-tag">Honors</span>
          <h2 class="section-title">资质荣誉</h2>
          <p class="section-desc">权威认证与行业认可，是我们品质的最好证明</p>
        </div>
        <div class="honors-grid">
          <div v-for="h in honors" :key="h.title" class="honor-card">
            <div class="honor-year">{{ h.year }}</div>
            <h3 class="honor-title">{{ h.title }}</h3>
          </div>
        </div>
      </div>
    </section>

    <section class="cta-section">
      <div class="container">
        <div class="cta-card">
          <h2 class="cta-title">想了解更多？</h2>
          <p class="cta-desc">欢迎联系我们，获取详细的公司介绍与业务咨询</p>
          <el-button type="primary" size="large" round @click="goContact">联系我们</el-button>
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
  padding: 120px 0 80px;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.hero-section::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120px;
  background: linear-gradient(to top, rgba(15, 23, 42, 0.3), transparent);
  pointer-events: none;
}

.section-tag {
  display: inline-block;
  font-size: 13px;
  font-weight: 600;
  color: #60a5fa;
  text-transform: uppercase;
  letter-spacing: 3px;
  margin-bottom: 16px;
}

.page-title {
  font-size: 44px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 20px;
}

.page-desc {
  font-size: 18px;
  color: #94a3b8;
  max-width: 640px;
  margin: 0 auto 48px;
  line-height: 1.7;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 64px;
}

.hero-stat {
  text-align: center;
}

.stat-num {
  display: block;
  font-size: 36px;
  font-weight: 700;
  color: #60a5fa;
}

.stat-label {
  font-size: 14px;
  color: #94a3b8;
  margin-top: 4px;
}

/* Values */
.values-section {
  padding: 0 0 48px;
  margin-top: -48px;
  position: relative;
  z-index: 1;
}

.values-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.value-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 40px 32px;
  text-align: center;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s, box-shadow 0.3s;
}

.value-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
}

.value-icon {
  font-size: 40px;
  margin-bottom: 16px;
}

.value-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 12px;
}

.value-desc {
  font-size: 15px;
  color: #6b7280;
  line-height: 1.7;
  margin: 0;
}

/* Timeline */
.timeline-section {
  padding: 80px 0;
  background: #f9fafb;
}

.section-header {
  text-align: center;
  max-width: 720px;
  margin: 0 auto 64px;
}

.section-title {
  font-size: 36px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 16px;
}

.section-desc {
  font-size: 16px;
  color: #6b7280;
  margin: 0;
}

.timeline {
  position: relative;
  max-width: 800px;
  margin: 0 auto;
  padding: 0;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e5e7eb;
  transform: translateX(-50%);
}

.timeline-item {
  display: flex;
  justify-content: flex-end;
  padding-right: calc(50% + 32px);
  margin-bottom: 48px;
  position: relative;
}

.timeline-item.right {
  justify-content: flex-start;
  padding-right: 0;
  padding-left: calc(50% + 32px);
}

.timeline-dot {
  position: absolute;
  left: 50%;
  top: 8px;
  width: 16px;
  height: 16px;
  background: #3b82f6;
  border: 3px solid #ffffff;
  border-radius: 50%;
  transform: translateX(-50%);
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.15);
}

.timeline-content {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  max-width: 320px;
}

.timeline-year {
  font-size: 13px;
  font-weight: 600;
  color: #3b82f6;
}

.timeline-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 6px 0 8px;
}

.timeline-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  line-height: 1.6;
}

/* Honors */
.honors-section {
  padding: 80px 0;
}

.honors-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  max-width: 900px;
  margin: 0 auto;
}

.honor-card {
  background: #f9fafb;
  border: 1px solid #f3f4f6;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  transition: border-color 0.3s;
}

.honor-card:hover {
  border-color: #3b82f6;
}

.honor-year {
  font-size: 24px;
  font-weight: 700;
  color: #3b82f6;
  margin-bottom: 8px;
}

.honor-title {
  font-size: 15px;
  font-weight: 500;
  color: #374151;
  margin: 0;
}

/* CTA */
.cta-section {
  padding: 0 0 80px;
}

.cta-card {
  background: linear-gradient(135deg, #1a365d 0%, #152238 100%);
  border-radius: 20px;
  padding: 64px 48px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.cta-card::before {
  content: '';
  position: absolute;
  top: -30%;
  right: -20%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.15), transparent);
  border-radius: 50%;
}

.cta-title {
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 12px;
  position: relative;
}

.cta-desc {
  font-size: 16px;
  color: #94a3b8;
  margin: 0 0 32px;
  position: relative;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 100px 0 64px;
  }
  .page-title {
    font-size: 32px;
  }
  .hero-stats {
    gap: 32px;
  }
  .values-grid {
    grid-template-columns: 1fr;
  }
  .timeline::before {
    left: 20px;
  }
  .timeline-item,
  .timeline-item.right {
    padding: 0 0 0 56px;
    justify-content: flex-start;
  }
  .timeline-dot {
    left: 20px;
  }
  .honors-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
