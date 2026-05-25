<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const contactInfo = [
  { icon: '📞', title: '销售热线', value: '400-888-8888', desc: '周一至周五 8:30-18:00' },
  { icon: '📞', title: '技术支持', value: '400-999-9999', desc: '7x24小时技术支持' },
  { icon: '✉️', title: '商务邮箱', value: 'sales@intersense.cn', desc: '工作日24小时内回复' },
  { icon: '📍', title: '总部地址', value: '深圳市南山区科技园', desc: '高新技术产业园A座18层' }
]

const offices = [
  { name: '深圳总部', address: '深圳市南山区科技园南区高新技术产业园A座18层', phone: '0755-88888888', email: 'shenzhen@intersense.cn' },
  { name: '北京分公司', address: '北京市海淀区中关村科技园区8号楼12层', phone: '010-88888888', email: 'beijing@intersense.cn' },
  { name: '上海分公司', address: '上海市浦东新区张江高科技园区创业大厦15层', phone: '021-88888888', email: 'shanghai@intersense.cn' },
  { name: '成都分公司', address: '成都市高新区天府软件园E区6栋8层', phone: '028-88888888', email: 'chengdu@intersense.cn' }
]

const inquiryTypes = ['产品咨询', '方案定制', '技术支持', '售后服务', '商务合作', '其他']

interface FormData {
  name: string
  company: string
  phone: string
  email: string
  type: string
  message: string
}

const form = reactive<FormData>({
  name: '',
  company: '',
  phone: '',
  email: '',
  type: '',
  message: ''
})

const submitting = ref(false)

function handleSubmit() {
  if (!form.name || !form.phone) {
    ElMessage.warning('请填写姓名和联系电话')
    return
  }
  submitting.value = true
  setTimeout(() => {
    submitting.value = false
    ElMessage.success('感谢您的咨询，我们将尽快与您联系！')
    form.name = ''
    form.company = ''
    form.phone = ''
    form.email = ''
    form.type = ''
    form.message = ''
  }, 1000)
}
</script>

<template>
  <div class="contact-page">
    <section class="hero-section">
      <div class="container">
        <h1 class="page-title">联系我们</h1>
        <p class="page-desc">无论您有任何问题或需求，我们都期待与您交流。专业团队将为您提供及时、专业的服务。</p>
      </div>
    </section>

    <section class="info-section">
      <div class="container">
        <div class="info-grid">
          <div v-for="info in contactInfo" :key="info.title" class="info-card">
            <div class="info-icon-wrap">
              <span class="info-icon">{{ info.icon }}</span>
            </div>
            <h3 class="info-title">{{ info.title }}</h3>
            <p class="info-value">{{ info.value }}</p>
            <p class="info-desc">{{ info.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="form-section">
      <div class="container">
        <div class="form-grid">
          <div class="form-main">
            <h2 class="form-title">在线咨询</h2>
            <p class="form-subtitle">请填写以下信息，我们将尽快与您联系</p>
            <form @submit.prevent="handleSubmit" class="contact-form">
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">姓名 <span class="required">*</span></label>
                  <el-input v-model="form.name" placeholder="请输入您的姓名" />
                </div>
                <div class="form-group">
                  <label class="form-label">公司名称</label>
                  <el-input v-model="form.company" placeholder="请输入公司名称" />
                </div>
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">联系电话 <span class="required">*</span></label>
                  <el-input v-model="form.phone" placeholder="请输入联系电话" />
                </div>
                <div class="form-group">
                  <label class="form-label">电子邮箱</label>
                  <el-input v-model="form.email" placeholder="请输入电子邮箱" />
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">咨询类型</label>
                <el-select v-model="form.type" placeholder="请选择咨询类型" style="width: 100%">
                  <el-option v-for="t in inquiryTypes" :key="t" :label="t" :value="t" />
                </el-select>
              </div>
              <div class="form-group">
                <label class="form-label">留言内容</label>
                <el-input
                  v-model="form.message"
                  type="textarea"
                  :rows="5"
                  placeholder="请描述您的需求..."
                />
              </div>
              <el-button
                type="primary"
                size="large"
                native-type="submit"
                :loading="submitting"
                class="submit-btn"
              >
                提交咨询
              </el-button>
            </form>
          </div>

          <div class="form-sidebar">
            <div class="sidebar-card">
              <h3 class="sidebar-title">服务时间</h3>
              <div class="service-time">
                <div class="time-item">
                  <span class="time-label">工作日</span>
                  <span class="time-value">8:30 - 18:00</span>
                </div>
                <div class="time-item">
                  <span class="time-label">技术支持</span>
                  <span class="time-value">7 x 24小时</span>
                </div>
              </div>
            </div>

            <div class="sidebar-card">
              <h3 class="sidebar-title">全国办事处</h3>
              <div class="office-list">
                <div v-for="office in offices" :key="office.name" class="office-item">
                  <h4 class="office-name">{{ office.name }}</h4>
                  <p class="office-address">{{ office.address }}</p>
                  <p class="office-contact">
                    <span>📞 {{ office.phone }}</span>
                    <span>✉️ {{ office.email }}</span>
                  </p>
                </div>
              </div>
            </div>
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

.page-title {
  font-size: 44px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 16px;
}

.page-desc {
  font-size: 18px;
  color: #6b7280;
  max-width: 640px;
  margin: 0 auto;
  line-height: 1.7;
}

/* ===== Contact Info ===== */
.info-section {
  padding: 0 0 64px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.info-card {
  background: #f9fafb;
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  transition: transform 0.3s, box-shadow 0.3s;
}

.info-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06);
}

.info-icon-wrap {
  width: 56px;
  height: 56px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.info-icon {
  font-size: 24px;
}

.info-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 8px;
}

.info-value {
  font-size: 17px;
  font-weight: 600;
  color: #3b82f6;
  margin: 0 0 4px;
}

.info-desc {
  font-size: 13px;
  color: #9ca3af;
  margin: 0;
}

/* ===== Form ===== */
.form-section {
  padding: 0 0 80px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 48px;
}

.form-main {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  padding: 40px;
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 8px;
}

.form-subtitle {
  font-size: 15px;
  color: #6b7280;
  margin: 0 0 32px;
}

.contact-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.required {
  color: #ef4444;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  margin-top: 8px;
}

/* ===== Sidebar ===== */
.form-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-card {
  background: #f9fafb;
  border-radius: 16px;
  padding: 28px;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 20px;
}

.service-time {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.time-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time-label {
  font-size: 14px;
  color: #6b7280;
}

.time-value {
  font-size: 14px;
  font-weight: 500;
  color: #111827;
}

.office-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.office-item {
  padding-bottom: 20px;
  border-bottom: 1px solid #e5e7eb;
}

.office-item:last-child {
  padding-bottom: 0;
  border-bottom: none;
}

.office-name {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 6px;
}

.office-address {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 6px;
  line-height: 1.5;
}

.office-contact {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

@media (max-width: 1024px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .form-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 100px 0 40px;
  }
  .page-title {
    font-size: 32px;
  }
  .info-grid {
    grid-template-columns: 1fr;
  }
  .form-row {
    grid-template-columns: 1fr;
  }
  .form-main {
    padding: 24px;
  }
}
</style>
