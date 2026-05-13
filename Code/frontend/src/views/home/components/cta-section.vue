<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function goContact() {
  router.push('/contact')
}
</script>

<template>
  <section class="section section-cta-block">
    <div class="cta-pattern"></div>
    <div class="container">
      <div class="cta-grid">
        <div class="cta-text">
          <h2 class="cta-title">需要专业的气体安全解决方案？</h2>
          <p class="cta-subtitle">我们的专业团队将为您提供免费咨询和定制方案</p>
          <div class="cta-actions">
            <el-button size="large" round class="btn-white" @click="goContact">免费获取方案</el-button>
            <el-button size="large" round class="btn-outline-white" @click="goContact">在线咨询</el-button>
          </div>
        </div>
        <div class="cta-contact">
          <div class="cta-contact-card">
            <span class="contact-icon">📞</span>
            <span class="contact-label">服务热线</span>
            <span class="contact-value">400-888-8888</span>
          </div>
          <div class="cta-contact-card">
            <span class="contact-icon">✉️</span>
            <span class="contact-label">商务邮箱</span>
            <span class="contact-value">sales@intersense.cn</span>
          </div>
          <div class="cta-contact-card">
            <span class="contact-icon">📍</span>
            <span class="contact-label">公司地址</span>
            <span class="contact-value">深圳市南山区科技园</span>
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

.section-cta-block {
  background: linear-gradient(135deg, var(--color-brand-dark) 0%, var(--color-brand-deeper) 100%);
  position: relative;
  overflow: hidden;
}

.cta-pattern {
  position: absolute;
  inset: 0;
  opacity: 0.06;
  background-image: 
    radial-gradient(circle at 30% 70%, rgba(59, 130, 246, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 70% 30%, rgba(96, 165, 250, 0.2) 0%, transparent 50%);
  pointer-events: none;
}

.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.cta-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
  align-items: center;
  position: relative;
}

.cta-title {
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 16px;
  line-height: 1.3;
}

.cta-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0 0 32px;
  line-height: 1.7;
}

.cta-actions {
  display: flex;
  gap: 16px;
}

.btn-white {
  background: #ffffff;
  color: var(--color-brand-dark);
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

.cta-contact {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.cta-contact-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  padding: 24px;
  text-align: center;
}

.contact-icon {
  display: block;
  font-size: 24px;
  margin-bottom: 8px;
}

.contact-label {
  display: block;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 4px;
}

.contact-value {
  font-size: 15px;
  font-weight: 600;
  color: #ffffff;
}

@media (max-width: 1024px) {
  .cta-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .section {
    padding: 48px 0;
  }
  .cta-contact {
    grid-template-columns: 1fr;
  }
}
</style>
