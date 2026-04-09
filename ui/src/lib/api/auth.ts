// 认证相关API服务
import { API_ENDPOINTS, REFRESH_TOKEN_KEY, USER_INFO_KEY, TOKEN_KEY } from './config';
import { post, getStoredToken, setToken, clearToken } from './client';

// 类型定义
export interface LoginRequest {
  mobile: string;
  password: string;
}

export interface SmsLoginRequest {
  mobile: string;
  code: string;
}

export interface SendSmsCodeRequest {
  mobile: string;
  scene: number; // 1=登录, 2=注册
}

export interface LoginResponse {
  userId: number;
  accessToken: string;
  refreshToken: string;
  expiresTime: string;
}

export interface UserInfo {
  id: number;
  nickname: string;
  avatar: string;
  mobile: string;
}

// 登录（手机+密码）
export async function login(data: LoginRequest): Promise<LoginResponse> {
  const response = await post<LoginResponse>(API_ENDPOINTS.auth.login, data);
  // 保存token
  setToken(response.accessToken);
  if (typeof window !== 'undefined') {
    localStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken);
  }
  return response;
}

// 短信登录
export async function smsLogin(data: SmsLoginRequest): Promise<LoginResponse> {
  const response = await post<LoginResponse>(API_ENDPOINTS.auth.smsLogin, data);
  setToken(response.accessToken);
  if (typeof window !== 'undefined') {
    localStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken);
  }
  return response;
}

// 发送验证码
export async function sendSmsCode(data: SendSmsCodeRequest): Promise<boolean> {
  return post<boolean>(API_ENDPOINTS.auth.sendSmsCode, data);
}

// 登出
export async function logout(): Promise<boolean> {
  try {
    await post<boolean>(API_ENDPOINTS.auth.logout);
  } finally {
    clearToken();
    if (typeof window !== 'undefined') {
      localStorage.removeItem(REFRESH_TOKEN_KEY);
      localStorage.removeItem(USER_INFO_KEY);
    }
  }
  return true;
}

// 检查是否已登录
export function isAuthenticated(): boolean {
  return !!getStoredToken();
}

// 获取存储的用户信息
export function getStoredUserInfo(): UserInfo | null {
  if (typeof window === 'undefined') return null;
  const info = localStorage.getItem(USER_INFO_KEY);
  return info ? JSON.parse(info) : null;
}

// 保存用户信息
export function setUserInfo(info: UserInfo): void {
  if (typeof window === 'undefined') return;
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(info));
}