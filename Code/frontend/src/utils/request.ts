import axios, { type AxiosResponse } from 'axios'
import type { Result } from '@/types/common'
import { getToken, removeToken } from '@/utils/auth'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response: AxiosResponse<Result<unknown>>) => {
    const { code, message, data } = response.data

    if (code === 0) {
      return data as never
    }

    if (code === 4008) {
      removeToken()
      window.location.href = '/admin/login'
      return Promise.reject(new Error(message))
    }

    return Promise.reject({ code, message })
  },
  (error) => {
    if (error.code === 'ECONNABORTED') {
      return Promise.reject({ code: 5001, message: '网络异常，请稍后重试' })
    }
    return Promise.reject({ code: 5001, message: '网络异常，请稍后重试' })
  }
)

export default request
