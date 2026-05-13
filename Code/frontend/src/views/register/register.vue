<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'

const router = useRouter()

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  company: ''
})

const loading = ref(false)

const RE_PHONE = /^1[3-9]\d{9}$/

function validate(): string | null {
  if (!form.username || !form.password || !form.phone) return '请填写用户名、密码和手机号'
  if (form.password !== form.confirmPassword) return '两次密码输入不一致'
  if (form.password.length < 6) return '密码长度不能少于6位'
  if (!RE_PHONE.test(form.phone)) return '请输入正确的手机号码'
  return null
}

async function handleRegister() {
  const err = validate()
  if (err) {
    ElMessage.warning(err)
    return
  }

  loading.value = true
  try {
    await authApi.register({
      username: form.username,
      password: form.password,
      phone: form.phone,
      company: form.company
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

function goLogin() {
  router.push('/login')
}
</script>

<template>
  <div class="register-page">
    <div class="register-card">
      <h2 class="register-title">注册账号</h2>

      <el-form :model="form" label-width="0" @submit.prevent="handleRegister">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.phone" placeholder="手机号" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.company" placeholder="公司名称（选填）" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码（至少6位）"
            show-password
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            show-password
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            native-type="submit"
            size="large"
            class="submit-btn"
          >
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <router-link to="/login" class="back-home">← 返回登录页</router-link>
        <span class="login-tip">
          已有账号？<a href="#" class="login-link" @click.prevent="goLogin">立即登录</a>
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0f4ff 0%, #e8edf5 50%, #f5f7fa 100%);
}

.register-card {
  width: 440px;
  padding: 44px 40px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 32px rgba(0, 0, 0, 0.08), 0 1px 4px rgba(0, 0, 0, 0.04);
}

.register-title {
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

.register-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
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

.login-tip {
  font-size: 13px;
  color: #9ca3af;
}

.login-link {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 500;
}

.login-link:hover {
  text-decoration: underline;
}
</style>
