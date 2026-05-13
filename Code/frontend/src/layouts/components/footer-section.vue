<script setup lang="ts">
import { footerLinks } from '@/data/navigation'
</script>

<template>
  <footer class="front-footer">
    <div class="container">
      <div class="footer-grid">
        <div class="footer-brand">
          <div class="footer-logo">
            <div class="logo-icon">IS</div>
            <span class="footer-logo-name">InterSense</span>
          </div>
          <p class="footer-desc">
            专注工业气体安全检测20年，为石油化工、冶金钢铁、电力能源等行业提供全方位的安全监测解决方案。
          </p>
        </div>

        <div v-for="section in footerLinks" :key="section.title" class="footer-column">
          <template v-if="section.href && (!section.links || section.links.length === 0)">
            <router-link :to="section.href" class="footer-column-title footer-column-link">{{ section.title }}</router-link>
          </template>
          <template v-else>
            <h4 class="footer-column-title">{{ section.title }}</h4>
            <ul class="footer-links">
              <li v-for="link in section.links" :key="link.name">
                <router-link :to="link.href" class="footer-link">{{ link.name }}</router-link>
              </li>
            </ul>
          </template>
        </div>
      </div>

      <div class="footer-bottom">
        <span>© 2026 InterSense. All Rights Reserved.</span>
        <div class="footer-bottom-links">
          <span>隐私政策</span>
          <span>服务条款</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.front-footer {
  background: var(--color-dark);
  padding: 64px 0 32px;
}

.container {
  max-width: 1320px;
  margin: 0 auto;
  padding: 0 32px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 1.6fr repeat(4, 1fr);
  gap: 48px;
  margin-bottom: 48px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.logo-icon {
  width: 42px;
  height: 42px;
  background: #3b82f6;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-weight: 700;
  font-size: 17px;
}

.footer-logo-name {
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
}

.footer-desc {
  font-size: 14px;
  color: #9ca3af;
  line-height: 1.7;
  margin: 0;
}

.footer-column-title {
  font-size: 15px;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 20px;
}

.footer-column-link {
  display: inline-block;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-column-link:hover {
  color: #3b82f6;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 11px;
}

.footer-link {
  font-size: 14px;
  color: #9ca3af;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-link:hover {
  color: #ffffff;
}

.footer-bottom {
  padding-top: 32px;
  border-top: 1px solid #1f2937;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #6b7280;
}

.footer-bottom-links {
  display: flex;
  gap: 24px;
}

.footer-bottom-links span {
  cursor: pointer;
  transition: color 0.2s;
}

.footer-bottom-links span:hover {
  color: #9ca3af;
}

@media (max-width: 1024px) {
  .footer-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 20px;
  }
  .footer-grid {
    grid-template-columns: 1fr;
    gap: 32px;
  }
  .footer-bottom {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
}
</style>
