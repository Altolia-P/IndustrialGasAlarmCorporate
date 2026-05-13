import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export function useAuth() {
  const router = useRouter()
  const authStore = useAuthStore()

  function requireAuth() {
    if (!authStore.isLoggedIn) {
      router.push({ name: 'Login', query: { redirect: router.currentRoute.value.fullPath } })
      return false
    }
    return true
  }

  function redirectAfterLogin() {
    const redirect = router.currentRoute.value.query.redirect as string
    router.push(redirect || { name: 'AdminDashboard' })
  }

  return { requireAuth, redirectAfterLogin, isLoggedIn: authStore.isLoggedIn }
}
