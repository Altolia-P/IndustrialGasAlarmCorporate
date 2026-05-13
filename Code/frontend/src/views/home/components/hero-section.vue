<script setup lang="ts">
import { useRouter } from 'vue-router'
import { stats } from '@/data/home'

const router = useRouter()

function goProducts() {
  router.push('/products')
}

function goContact() {
  router.push('/contact')
}
</script>

<template>
  <section class="hero">
    <div class="hero-bg"></div>
    <div class="container hero-content">
      <div class="hero-grid">
        <div class="hero-text">
          <div class="hero-badge">
            <span class="badge-dot"></span>
            智能化气体安全解决方案
          </div>
          <h1 class="hero-title">
            守护工业安全
            <span class="hero-highlight">智能感知未来</span>
          </h1>
          <p class="hero-subtitle">
            专注工业气体检测与火灾报警领域20年，为石油化工、冶金钢铁、电力能源等行业提供全方位的安全监测解决方案。
          </p>
          <div class="hero-actions">
            <el-button type="primary" size="large" round @click="goContact">了解更多</el-button>
            <el-button size="large" round class="btn-outline" @click="goProducts">产品中心</el-button>
          </div>
          <div class="hero-stats">
            <div v-for="stat in stats" :key="stat.label" class="stat-item">
              <span class="stat-value">{{ stat.value }}</span>
              <span class="stat-label">{{ stat.label }}</span>
            </div>
          </div>
        </div>
        <div class="hero-cards">
          <div class="hero-card">
            <div class="hero-card-icon">🏅</div>
            <h3 class="hero-card-title">资质认证齐全</h3>
            <p class="hero-card-desc">通过ISO9001、防爆认证、消防认证等多项国家级资质认证</p>
          </div>
          <div class="hero-card">
            <div class="hero-card-icon">👥</div>
            <h3 class="hero-card-title">专业技术团队</h3>
            <p class="hero-card-desc">拥有50+研发工程师，持续创新，为客户提供最优解决方案</p>
          </div>
          <div class="hero-card">
            <div class="hero-card-icon">🎧</div>
            <h3 class="hero-card-title">全天候服务</h3>
            <p class="hero-card-desc">7x24小时技术支持，全国200+服务网点，快速响应</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.hero {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #3b82f620 100%);
}

.hero-bg::before {
  content: '';
  position: absolute;
  top: 25%;
  right: 25%;
  width: 384px;
  height: 384px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.2), transparent);
  border-radius: 50%;
  filter: blur(48px);
  animation: heroPulse 4s ease-in-out infinite;
}

.hero-bg::after {
  content: '';
  position: absolute;
  bottom: 25%;
  left: 25%;
  width: 256px;
  height: 256px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.1), transparent);
  border-radius: 50%;
  filter: blur(48px);
  animation: heroPulse 4s ease-in-out 2s infinite;
}

@keyframes heroPulse {
  0%, 100% { opacity: 0.6; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.1); }
}

.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.hero-content {
  position: relative;
  padding: 120px 0 80px;
}

.hero-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
  align-items: center;
}

.hero-text {
  text-align: left;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 50px;
  color: #60a5fa;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 24px;
}

.badge-dot {
  width: 8px;
  height: 8px;
  background: #60a5fa;
  border-radius: 50%;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  color: #ffffff;
  line-height: 1.2;
  margin: 0 0 8px;
}

.hero-highlight {
  display: block;
  color: #60a5fa;
  margin-top: 4px;
}

.hero-subtitle {
  font-size: 18px;
  color: #9ca3af;
  max-width: 480px;
  margin: 24px 0 32px;
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 48px;
}

.btn-outline {
  border-color: rgba(255, 255, 255, 0.3);
  color: #ffffff;
  background: transparent;
}

.btn-outline:hover {
  border-color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
}

.stat-label {
  font-size: 14px;
  color: #9ca3af;
  margin-top: 4px;
}

.hero-cards {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  transition: border-color 0.3s;
}

.hero-card:hover {
  border-color: rgba(59, 130, 246, 0.5);
}

.hero-card-icon {
  width: 48px;
  height: 48px;
  background: rgba(59, 130, 246, 0.15);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 16px;
}

.hero-card-title {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 8px;
}

.hero-card-desc {
  font-size: 14px;
  color: #9ca3af;
  margin: 0;
  line-height: 1.6;
}

@media (max-width: 1024px) {
  .hero-grid {
    grid-template-columns: 1fr;
  }
  .hero-cards {
    display: none;
  }
  .hero-title {
    font-size: 36px;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 28px;
  }
  .hero-stats {
    gap: 16px;
  }
  .stat-value {
    font-size: 24px;
  }
}
</style>
