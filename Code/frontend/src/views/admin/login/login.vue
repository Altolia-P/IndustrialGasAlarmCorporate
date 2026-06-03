<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useLogin } from '@/composables/use-login'
import { ElMessage } from 'element-plus'

const form = ref({ username: '', password: '', captcha: '' })
const { loading, captchaImage, login, refreshCaptcha } = useLogin()

onMounted(() => {
  refreshCaptcha()
})

async function handleSubmit() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  await login(form.value.username, form.value.password, form.value.captcha)
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <h2 class="login-title">登录</h2>

      <el-form :model="form" label-width="0" @submit.prevent="handleSubmit">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            show-password
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <div class="captcha-row">
            <el-input
              v-model="form.captcha"
              placeholder="验证码"
              size="large"
              class="captcha-input"
              maxlength="4"
            />
            <img
              :src="captchaImage"
              alt="验证码"
              class="captcha-img"
              title="点击刷新验证码"
              @click="refreshCaptcha"
            />
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            native-type="submit"
            size="large"
            class="submit-btn"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <router-link to="/" class="back-home">← 返回首页</router-link>
        <span class="register-tip">
          没有账号？<router-link to="/register" class="register-link">立即注册</router-link>
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0f4ff 0%, #e8edf5 50%, #f5f7fa 100%);
}

.login-card {
  width: 420px;
  padding: 44px 40px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 32px rgba(0, 0, 0, 0.08), 0 1px 4px rgba(0, 0, 0, 0.04);
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a365d;
  text-align: center;
  margin: 0 0 32px;
}

.submit-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  letter-spacing: 2px;
}

.captcha-row {
  display: flex;
  gap: 12px;
  align-items: center;
}

.captcha-input {
  flex: 1;
}

.captcha-img {
  height: 40px;
  width: 110px;
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  cursor: pointer;
  object-fit: cover;
}

.login-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f3f4f6;
}

.back-home {
  font-size: 13px;
  color: #9ca3af;
  text-decoration: none;
  transition: color 0.2s;
}

.back-home:hover {
  color: #3b82f6;
}

.register-tip {
  font-size: 13px;
  color: #9ca3af;
}

.register-link {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 500;
}

.register-link:hover {
  text-decoration: underline;
}
</style>
