// HTTP请求客户端
import { API_CONFIG, TOKEN_KEY } from './config';

// 响应类型
interface ApiResponse<T = unknown> {
  code: number;
  msg: string;
  data: T;
}

// 请求配置
interface RequestOptions extends RequestInit {
  params?: Record<string, string>;
  token?: string;
}

// 获取存储的token
export function getStoredToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem(TOKEN_KEY);
}

// 设置token
export function setToken(token: string): void {
  if (typeof window === 'undefined') return;
  localStorage.setItem(TOKEN_KEY, token);
}

// 清除token
export function clearToken(): void {
  if (typeof window === 'undefined') return;
  localStorage.removeItem(TOKEN_KEY);
}

// 构建URL
function buildUrl(endpoint: string, params?: Record<string, string>): string {
  const url = new URL(`${API_CONFIG.baseURL}${endpoint}`);
  if (params) {
    Object.entries(params).forEach(([key, value]) => {
      url.searchParams.append(key, value);
    });
  }
  return url.toString();
}

// HTTP请求
export async function request<T = unknown>(
  endpoint: string,
  options: RequestOptions = {}
): Promise<T> {
  const { params, token, ...fetchOptions } = options;

  const url = buildUrl(endpoint, params);
  const authToken = token || getStoredToken();

  const headers: HeadersInit = {
    ...API_CONFIG.headers,
    ...fetchOptions.headers,
  };

  if (authToken) {
    headers['Authorization'] = `Bearer ${authToken}`;
  }

  const response = await fetch(url, {
    ...fetchOptions,
    headers,
  });

  const result: ApiResponse<T> = await response.json();

  // 检查业务错误
  if (result.code !== 0) {
    throw new Error(result.msg || '请求失败');
  }

  return result.data;
}

// GET请求
export function get<T = unknown>(endpoint: string, params?: Record<string, string>): Promise<T> {
  return request<T>(endpoint, { method: 'GET', params });
}

// POST请求
export function post<T = unknown>(endpoint: string, data?: unknown): Promise<T> {
  return request<T>(endpoint, {
    method: 'POST',
    body: data ? JSON.stringify(data) : undefined,
  });
}

// PUT请求
export function put<T = unknown>(endpoint: string, data?: unknown): Promise<T> {
  return request<T>(endpoint, {
    method: 'PUT',
    body: data ? JSON.stringify(data) : undefined,
  });
}

// DELETE请求
export function del<T = unknown>(endpoint: string): Promise<T> {
  return request<T>(endpoint, { method: 'DELETE' });
}