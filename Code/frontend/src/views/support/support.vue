<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const supportItems = [
  {
    icon: '📖',
    title: '产品文档',
    desc: '产品数据手册、安装指南、操作手册下载',
    items: ['气体检测仪系列', '报警控制器系列', '传感器模组系列', '系统平台手册']
  },
  {
    icon: '🎓',
    title: '培训服务',
    desc: '提供线上/线下产品使用培训与安全知识讲座',
    items: ['产品操作培训', '安装调试培训', '安全知识讲座', '定制化培训方案']
  },
  {
    icon: '🔧',
    title: '售后服务',
    desc: '设备维修、定期校准、备件更换等售后保障',
    items: ['设备返厂维修', '定期校准服务', '备品备件供应', '延保服务方案']
  }
]

const downloads = [
  { name: 'IS-9000系列产品手册', size: '12.5 MB', type: 'PDF' },
  { name: '气体报警控制器选型指南', size: '8.3 MB', type: 'PDF' },
  { name: '智慧燃气平台API文档', size: '3.1 MB', type: 'PDF' },
  { name: '产品安装与调试指南', size: '15.7 MB', type: 'PDF' },
  { name: 'Modbus通讯协议说明', size: '2.8 MB', type: 'PDF' },
  { name: '传感器选型对照表', size: '1.2 MB', type: 'Excel' }
]

function goContact() {
  router.push('/contact')
}
</script>

<template>
  <div class="support-page">
    <section class="hero-section">
      <div class="container">
        <span class="section-tag">Support</span>
        <h1 class="page-title">服务支持</h1>
        <p class="page-desc">从产品选型到售后维护，我们提供全生命周期的技术支持与服务保障</p>
      </div>
    </section>

    <section class="cards-section">
      <div class="container">
        <div class="support-grid">
          <div v-for="item in supportItems" :key="item.title" class="support-card">
            <div class="card-header">
              <span class="card-icon">{{ item.icon }}</span>
              <div>
                <h3 class="card-title">{{ item.title }}</h3>
                <p class="card-desc">{{ item.desc }}</p>
              </div>
            </div>
            <ul class="card-list">
              <li v-for="sub in item.items" :key="sub" class="card-list-item">
                <span class="list-dot"></span>
                {{ sub }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <section id="downloads" class="downloads-section">
      <div class="container">
        <div class="section-header">
          <span class="section-tag">Downloads</span>
          <h2 class="section-title">下载中心</h2>
          <p class="section-desc">产品手册、技术文档、软件工具，一键下载</p>
        </div>
        <div class="downloads-scroll">
          <div v-for="doc in downloads" :key="doc.name" class="download-row">
            <span class="download-icon">📄</span>
            <div class="download-info">
              <span class="download-name">{{ doc.name }}</span>
              <div class="download-meta">
                <span class="download-type">{{ doc.type }}</span>
                <span class="download-size">{{ doc.size }}</span>
              </div>
            </div>
            <el-button size="small" round>下载</el-button>
          </div>
        </div>
      </div>
    </section>

    <section class="cta-section">
      <div class="container">
        <div class="cta-card">
          <h2 class="cta-title">需要更多帮助？</h2>
          <p class="cta-desc">我们的技术团队随时准备为您提供专业支持</p>
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
  padding: 120px 0 64px;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  text-align: center;
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
  margin: 0 0 16px;
}

.page-desc {
  font-size: 18px;
  color: #94a3b8;
  max-width: 640px;
  margin: 0 auto;
  line-height: 1.7;
}

.cards-section {
  padding: 0 0 80px;
  margin-top: -32px;
  position: relative;
  z-index: 1;
}

.support-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.support-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s, box-shadow 0.3s;
}

.support-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 20px;
}

.card-icon {
  font-size: 32px;
  width: 56px;
  height: 56px;
  background: #f3f4f6;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 6px;
}

.card-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.card-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.card-list-item {
  font-size: 14px;
  color: #374151;
  display: flex;
  align-items: center;
  gap: 8px;
}

.list-dot {
  width: 6px;
  height: 6px;
  background: #3b82f6;
  border-radius: 50%;
  flex-shrink: 0;
}

/* Downloads */
.downloads-section {
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

.downloads-scroll {
  max-width: 800px;
  margin: 0 auto;
  max-height: 360px;
  overflow-y: auto;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.04);
}

.downloads-scroll::-webkit-scrollbar {
  width: 6px;
}

.downloads-scroll::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.download-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 28px;
  border-bottom: 1px solid #f3f4f6;
  height: 64px;
  box-sizing: border-box;
}

.download-row:last-child {
  border-bottom: none;
}

.download-icon {
  font-size: 22px;
  flex-shrink: 0;
}

.download-info {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 0 16px;
  min-width: 0;
}

.download-name {
  font-size: 15px;
  color: #111827;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.download-meta {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: #9ca3af;
  flex-shrink: 0;
  margin-left: 16px;
}

.download-type {
  background: #f3f4f6;
  padding: 2px 8px;
  border-radius: 4px;
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
    padding: 100px 0 48px;
  }
  .page-title {
    font-size: 32px;
  }
  .support-grid {
    grid-template-columns: 1fr;
  }
  .card-list {
    grid-template-columns: 1fr;
  }
  .download-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
