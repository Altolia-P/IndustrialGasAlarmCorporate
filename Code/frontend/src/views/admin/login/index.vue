<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api/auth'
import { useFormSubmit } from '@/composables/useFormSubmit'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({ username: '', password: '' })

const { loading, submit } = useFormSubmit(authApi.login, {
  successMsg: '登录成功',
  onSuccess: (data) => {
    authStore.loginSuccess(data.token, data.userUuid, data.username)
    const redirect = router.currentRoute.value.query.redirect as string
    router.push(redirect || '/admin')
  }
})
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <h2>管理员登录</h2>
      <el-form :model="form" label-width="0" @submit.prevent="submit(form)">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" native-type="submit" style="width:100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}
.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}
.login-card h2 {
  margin: 0 0 24px;
  text-align: center;
  font-size: 22px;
}
</style>
