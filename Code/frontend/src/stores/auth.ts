import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, removeToken, hasToken, getRole, setRole, removeRole } from '@/utils/auth'
import { authApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(getToken())
  const userUuid = ref<string>('')
  const username = ref<string>('')
  const role = ref<string>(getRole())
  const tokenVerified = ref(false)
  const verifying = ref(false)

  const isLoggedIn = computed(() => tokenVerified.value && hasToken() && token.value !== null)
  const isAdmin = computed(() => role.value === 'ADMIN')
  const isStaff = computed(() => role.value === 'STAFF')

  function loginSuccess(loginToken: string, uuid: string, name: string, userRole: string) {
    setToken(loginToken)
    setRole(userRole)
    token.value = loginToken
    userUuid.value = uuid
    username.value = name
    role.value = userRole
    tokenVerified.value = true
  }

  function logout() {
    removeToken()
    removeRole()
    token.value = null
    userUuid.value = ''
    username.value = ''
    role.value = ''
    tokenVerified.value = false
    authApi.logout().catch(() => {})
  }

  async function verifyToken(): Promise<boolean> {
    if (tokenVerified.value) return true
    if (!hasToken()) return false

    verifying.value = true
    try {
      const user = await authApi.getCurrentUser()
      userUuid.value = user.userUuid
      username.value = user.username
      tokenVerified.value = true
      return true
    } catch {
      logout()
      return false
    } finally {
      verifying.value = false
    }
  }

  return { token, userUuid, username, role, tokenVerified, verifying, isLoggedIn, isAdmin, isStaff, loginSuccess, logout, verifyToken }
})
