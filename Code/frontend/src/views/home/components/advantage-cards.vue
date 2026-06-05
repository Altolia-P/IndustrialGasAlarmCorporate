<script setup lang="ts">
import { advantages } from '@/data/home'
</script>

<template>
  <section class="section section-advantages">
    <div class="advantages-pattern"></div>
    <div class="container">
      <div class="section-header">
        <span class="section-tag">为什么选择我们</span>
        <h2 class="section-title">值得信赖的安全合作伙伴</h2>
        <p class="section-desc">20年深耕工业安全领域，以专业和品质赢得客户信赖</p>
      </div>
      <div class="advantages-grid">
        <div v-for="item in advantages" :key="item.title" class="advantage-card">
          <div class="advantage-icon">{{ item.icon }}</div>
          <h3 class="advantage-title">{{ item.title }}</h3>
          <p class="advantage-desc">{{ item.description }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.section {
  padding: 80px 0;
}

.section-advantages {
  background: linear-gradient(180deg, var(--color-brand-deeper) 0%, var(--color-brand-dark) 40%, #1e3a5f 100%);
  position: relative;
  overflow: hidden;
}

.advantages-pattern {
  position: absolute;
  inset: 0;
  opacity: 0.04;
  background-image:
    radial-gradient(circle at 20% 50%, rgba(59, 130, 246, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(96, 165, 250, 0.2) 0%, transparent 40%);
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
  color: var(--color-gray-400);
  margin: 0;
  line-height: 1.7;
}

.advantages-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  position: relative;
}

.advantage-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 32px;
  transition: border-color 0.3s;
}

.advantage-card:hover {
  border-color: rgba(59, 130, 246, 0.5);
}

.advantage-icon {
  width: 56px;
  height: 56px;
  background: rgba(59, 130, 246, 0.15);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-bottom: 20px;
}

.advantage-title {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 12px;
}

.advantage-desc {
  font-size: 15px;
  color: #9ca3af;
  margin: 0;
  line-height: 1.7;
}

@media (max-width: 1024px) {
  .advantages-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 16px;
  }
  .section {
    padding: 48px 0;
  }
  .section-title {
    font-size: 28px;
  }
  .section-desc {
    font-size: 15px;
  }
  .advantages-grid {
    grid-template-columns: 1fr;
  }
  .advantage-card {
    padding: 24px;
  }
}
</style>
