/**
 * HTTP 请求工具 - 使用原生 fetch 实现
 * 与原 axios 接口保持兼容
 */
import router from '@/router'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:48080/app-api'
const TIMEOUT = 30000

/** 通用业务响应体 */
interface ApiResponse<T = unknown> {
  code: number
  msg?: string
  message?: string
  data: T
}

/** 发送请求的核心方法 */
async function request<T = unknown>(
  method: 'GET' | 'POST' | 'PUT' | 'DELETE',
  url: string,
  data?: unknown,
  options?: { params?: Record<string, unknown>; headers?: Record<string, string> }
): Promise<T> {
  // 拼接 URL 参数
  let fullUrl = `${BASE_URL}${url}`
  if (options?.params) {
    const searchParams = new URLSearchParams()
    Object.entries(options.params).forEach(([k, v]) => {
      if (v !== undefined && v !== null) searchParams.set(k, String(v))
    })
    const qs = searchParams.toString()
    if (qs) fullUrl += `?${qs}`
  }

  // 构建 headers
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...options?.headers,
  }

  // 注入 token
  const token = localStorage.getItem('token')
  if (token) headers['Authorization'] = `Bearer ${token}`

  // 注入租户 ID
  const tenantId = localStorage.getItem('tenantId')
  if (tenantId) headers['tenant-id'] = tenantId

  // 处理 FormData（文件上传等场景）
  const isFormData = data instanceof FormData
  if (isFormData) delete headers['Content-Type'] // 浏览器自动设置 boundary

  // 超时控制
  const controller = new AbortController()
  const timeoutId = setTimeout(() => controller.abort(), TIMEOUT)

  try {
    const res = await fetch(fullUrl, {
      method,
      headers,
      body: isFormData
        ? data
        : data !== undefined ? JSON.stringify(data) : undefined,
      signal: controller.signal,
    })
    clearTimeout(timeoutId)

    if (!res.ok) {
      if (res.status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
        throw new Error('登录已过期，请重新登录')
      }
      if (res.status === 403) throw new Error('没有权限访问')
      if (res.status === 404) throw new Error('请求的资源不存在')
      if (res.status >= 500) throw new Error('服务器错误，请稍后重试')
      throw new Error(`请求失败 (${res.status})`)
    }

    const json: ApiResponse<T> = await res.json()

    if (json.code !== 0) {
      const msg = json.msg || json.message || '请求失败'
      if (json.code === 401 || json.code === 40102) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }
      throw new Error(msg)
    }

    return json.data
  } catch (err: unknown) {
    clearTimeout(timeoutId)
    if (err instanceof Error) {
      if (err.name === 'AbortError') {
        throw new Error('请求超时，请稍后重试')
      }
      if (err.message.includes('请求失败') || err.message.includes('没有权限') || err.message.includes('登录')) {
        throw err
      }
      throw new Error('网络连接失败，请检查网络')
    }
    throw new Error('请求异常')
  }
}

// ========== 与原 axios 接口兼容的导出 ==========

export default {
  get<T = unknown>(url: string, data?: unknown, options?: { params?: Record<string, unknown>; headers?: Record<string, string> }): Promise<T> {
    return request<T>('GET', url, undefined, { params: data as Record<string, unknown>, ...options })
  },
  post<T = unknown>(url: string, data?: unknown, options?: { params?: Record<string, unknown>; headers?: Record<string, string> }): Promise<T> {
    return request<T>('POST', url, data, options)
  },
  put<T = unknown>(url: string, data?: unknown, options?: { params?: Record<string, unknown>; headers?: Record<string, string> }): Promise<T> {
    return request<T>('PUT', url, data, options)
  },
  delete<T = unknown>(url: string, data?: unknown, options?: { params?: Record<string, unknown>; headers?: Record<string, string> }): Promise<T> {
    return request<T>('DELETE', url, data, options)
  },
}

export function get<T = unknown>(url: string, params?: unknown, options?: { params?: Record<string, unknown>; headers?: Record<string, string> }): Promise<T> {
  return request<T>('GET', url, undefined, { params: params as Record<string, unknown>, ...options })
}

export function post<T = unknown>(url: string, data?: unknown, options?: { params?: Record<string, unknown>; headers?: Record<string, string> }): Promise<T> {
  return request<T>('POST', url, data, options)
}

export function put<T = unknown>(url: string, data?: unknown, options?: { params?: Record<string, unknown>; headers?: Record<string, string> }): Promise<T> {
  return request<T>('PUT', url, data, options)
}

export function del<T = unknown>(url: string, params?: unknown, options?: { params?: Record<string, unknown>; headers?: Record<string, string> }): Promise<T> {
  return request<T>('DELETE', url, params, options)
}
