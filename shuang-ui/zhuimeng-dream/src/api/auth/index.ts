/**
 * 认证模块 API
 * 对接后端: /app-api/member/auth
 */

// ========== 请求 VO ==========

/** 手机号 + 密码登录 */
export interface LoginReqVO {
  mobile: string
  password: string
}

/** 手机号 + 密码注册 */
export interface RegisterReqVO {
  mobile: string
  password: string
  confirmPassword: string
  inviteCode?: string
}

// ========== 响应 VO ==========

/** 登录响应 */
export interface LoginRespVO {
  userId: number
  accessToken: string
  refreshToken: string
  expiresTime: string
  openid?: string
  user?: {
    id: number
    nickname: string
    avatar: string
    phone: string
  }
}

export interface LoginUserInfo {
  id: number
  nickname: string
  avatar: string
  mobile: string
  point?: number
}

// ========== API 方法 ==========

import request, { post, get } from '@/utils/request'

/** 手机号 + 密码登录 */
export function login(data: LoginReqVO) {
  return post<LoginRespVO>('/member/auth/login', data)
}

/** 手机号 + 密码注册 */
export function register(data: RegisterReqVO) {
  return post<LoginRespVO>('/member/auth/register', data)
}

/** 刷新 Token */
export function refreshToken(refreshToken: string) {
  return post<LoginRespVO>(`/member/auth/refresh-token?refreshToken=${encodeURIComponent(refreshToken)}`)
}

/** 退出登录 */
export function logout() {
  return post<boolean>('/member/auth/logout', {})
}

/** 获取当前登录用户信息 */
export function getLoginUserInfo() {
  return get<LoginUserInfo>('/member/user/get')
}
