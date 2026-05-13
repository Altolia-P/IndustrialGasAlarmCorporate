<script setup lang="ts">

const inquiries = [
  { id: 1, title: '关于某石化厂检修方案确认', status: '已回复', date: '2024-03-15', reply: '已确认检修方案，按计划执行。' },
  { id: 2, title: '设备配件申请咨询', status: '处理中', date: '2024-03-10', reply: '' }
]
</script>

<template>
  <div class="staff-inquiries">
    <div class="page-header">
      <div>
        <h3 class="section-title">我的咨询</h3>
        <p class="section-desc">查看管理员安排的工作相关咨询</p>
      </div>
    </div>

    <div class="inquiry-list">
      <div v-for="item in inquiries" :key="item.id" class="inquiry-card">
        <div class="inquiry-header">
          <h4>{{ item.title }}</h4>
          <span class="inquiry-date">{{ item.date }}</span>
        </div>
        <div v-if="item.reply" class="inquiry-reply">
          <span class="reply-label">管理员回复：</span>
          <p>{{ item.reply }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.section-title { font-size: 20px; font-weight: 700; color: #111827; margin: 0 0 6px; }
.section-desc { font-size: 14px; color: #6b7280; margin: 0; }

.inquiry-list { display: flex; flex-direction: column; gap: 16px; }

.inquiry-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.inquiry-header { display: flex; justify-content: space-between; align-items: flex-start; }
.inquiry-header h4 { font-size: 16px; font-weight: 600; color: #111827; margin: 0; }
.inquiry-date { font-size: 13px; color: #9ca3af; white-space: nowrap; }

.inquiry-reply {
  margin-top: 16px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
  border-left: 3px solid #10b981;
}

.reply-label { font-size: 13px; font-weight: 600; color: #10b981; }
.inquiry-reply p { font-size: 14px; color: #374151; margin: 8px 0 0; line-height: 1.6; }
</style>
