import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, removeToken, hasToken } from '@/utils/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(getToken())
  const userUuid = ref<string>('')
  const username = ref<string>('')

  const isLoggedIn = computed(() => hasToken() && token.value !== null)

  function loginSuccess(loginToken: string, uuid: string, name: string) {
    setToken(loginToken)
    token.value = loginToken
    userUuid.value = uuid
    username.value = name
  }

  function logout() {
    removeToken()
    token.value = null
    userUuid.value = ''
    username.value = ''
  }

  return { token, userUuid, username, isLoggedIn, loginSuccess, logout }
})
