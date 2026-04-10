/**
 * 用户与积分模块 API
 * 对接后端: /member/user, /member/point
 */

import request, { get, post } from '@/utils/request'

// ========== 响应类型 ==========

/** 钱包信息 */
export interface WalletInfo {
  id: number
  userId: number
  balance: number       // 积分余额
  frozenBalance: number  // 冻结积分
  totalRecharge: number  // 累计充值
  totalUsed: number     // 累计消耗
}

/** 积分记录 */
export interface PointRecord {
  id: number
  bizType: number
  bizTypeName: string
  amount: number        // 正数=获得，负数=消耗
  balance: number       // 变动后余额
  description: string
  createTime: string
}

/** 用户信息 */
export interface UserInfo {
  id: number
  nickname: string
  avatar: string
  phone: string
}

// ========== API 方法 ==========

/** 获取我的钱包（积分）信息 */
export function getMyWallet() {
  return get<WalletInfo>('/member/point/wallet')
}

/** 获取积分记录（分页） */
export interface GetPointRecordsReqVO {
  pageNo?: number
  pageSize?: number
  bizType?: number
}

export interface GetPointRecordsRespVO {
  list: PointRecord[]
  total: number
}

export function getPointRecords(params: GetPointRecordsReqVO) {
  return get<GetPointRecordsRespVO>('/member/point/record/page', params)
}

/** 获取用户信息 */
export function getUserInfo() {
  return get<UserInfo>('/member/user/get')
}
