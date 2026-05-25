import axios, { type AxiosResponse, type AxiosRequestConfig } from 'axios'
import type { Result } from '@/types/common'
import { getToken, removeToken, removeRole } from '@/utils/auth'

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

instance.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

instance.interceptors.response.use(
  (response: AxiosResponse<Result<unknown>>) => {
    const { code, message, data } = response.data

    if (code === 0) {
      return data as never
    }

    if (code === 4008) {
      removeToken()
      removeRole()
      window.location.href = '/login?reason=expired'
      return Promise.reject(new Error(message || '登录已过期，请重新登录'))
    }

    return Promise.reject({ code, message })
  },
  (error) => {
    if (error.response?.status === 401) {
      removeToken()
      removeRole()
      window.location.href = '/login?reason=unauthorized'
      return Promise.reject({ code: 4008, message: '登录已过期，请重新登录' })
    }
    if (error.code === 'ECONNABORTED') {
      return Promise.reject({ code: 5001, message: '网络异常，请稍后重试' })
    }
    return Promise.reject({ code: 5001, message: '网络异常，请稍后重试' })
  }
)

// Typed wrapper: unwraps Result<T>.data so callers see Promise<T> not Promise<AxiosResponse<Result<T>>>
const request = {
  get<T = unknown>(url: string, config?: AxiosRequestConfig) {
    return instance.get(url, config) as Promise<T>
  },
  post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return instance.post(url, data, config) as Promise<T>
  },
  put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return instance.put(url, data, config) as Promise<T>
  },
  delete<T = unknown>(url: string, config?: AxiosRequestConfig) {
    return instance.delete(url, config) as Promise<T>
  }
}

export default request
