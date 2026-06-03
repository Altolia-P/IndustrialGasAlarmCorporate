import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import { useLoading } from './use-loading'

export function useLogin() {
  const router = useRouter()
  const authStore = useAuthStore()
  const { loading, start, stop } = useLoading()

  const captchaImage = ref('')
  const captchaToken = ref('')

  async function refreshCaptcha() {
    try {
      const data = await authApi.getCaptcha()
      captchaImage.value = data.image
      captchaToken.value = data.token
    } catch {
      // captcha load failed, retry on next interaction
    }
  }

  async function login(username: string, password: string, captcha: string) {
    if (!captcha) {
      ElMessage.warning('请输入验证码')
      return
    }
    start()
    try {
      const data = await authApi.login({
        username,
        password,
        captcha,
        captchaToken: captchaToken.value,
      })
      onLoginSuccess(data)
    } catch (err: unknown) {
      refreshCaptcha()
      ElMessage.error((err as { message?: string })?.message || '账号或密码错误')
      stop()
    }
  }

  function onLoginSuccess(data: { token: string; userUuid: string; username: string; role: string }) {
    authStore.loginSuccess(data.token, data.userUuid, data.username, data.role)
    ElMessage.success('登录成功')
    stop()
    const redirect = router.currentRoute.value.query.redirect as string
    if (redirect && isValidRedirect(redirect)) {
      router.push(redirect)
      return
    }
    router.push(
      data.role === 'ADMIN' ? '/admin' : data.role === 'STAFF' ? '/staff' : '/user'
    )
  }

  function isValidRedirect(path: string): boolean {
    return path.startsWith('/') && !path.startsWith('//') && !path.startsWith('\\\\')
  }

  return { loading, captchaImage, captchaToken, login, refreshCaptcha }
}
