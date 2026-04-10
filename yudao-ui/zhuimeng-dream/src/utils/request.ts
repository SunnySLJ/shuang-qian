import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import router from '@/router'

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  // TODO: 替换为实际后端地址
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:48080/app-api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 注入 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    // 注入租户 ID（如果有）
    const tenantId = localStorage.getItem('tenantId')
    if (tenantId) {
      config.headers['tenant-id'] = tenantId
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    // 业务错误处理
    if (res.code !== 0) {
      const msg = res.msg || res.message || '请求失败'
      // Token 过期
      if (res.code === 401 || res.code === 40102) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
        return Promise.reject(new Error(msg))
      }
      return Promise.reject(new Error(msg))
    }
    return res.data
  },
  (error: AxiosError<{ code?: number; msg?: string; message?: string }>) => {
    // 网络错误处理
    if (!error.response) {
      if (error.code === 'ECONNABORTED') {
        return Promise.reject(new Error('请求超时，请稍后重试'))
      }
      return Promise.reject(new Error('网络连接失败，请检查网络'))
    }

    const status = error.response.status
    switch (status) {
      case 401:
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
        return Promise.reject(new Error('登录已过期，请重新登录'))
      case 403:
        return Promise.reject(new Error('没有权限访问'))
      case 404:
        return Promise.reject(new Error('请求的资源不存在'))
      case 500:
        return Promise.reject(new Error('服务器错误，请稍后重试'))
      default: {
        return Promise.reject(new Error(msg))
      }
    }
  }
)

export default request

// 泛型请求方法封装
export function get<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
  return request.get(url, { params, ...config })
}

export function post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  return request.post(url, data, config)
}

export function put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  return request.put(url, data, config)
}

export function del<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
  return request.delete(url, { params, ...config })
}
