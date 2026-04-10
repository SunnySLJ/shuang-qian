/**
 * 代理中心 API
 * 对接后端: /app/agency
 */

import request, { get, post } from '@/utils/request'

// ========== 请求 VO ==========

/** 绑定上级代理 */
export interface BindAgencyReqVO {
  inviteCode: string   // 上级邀请码
}

/** 分配积分请求 */
export interface TransferPointsReqVO {
  toUserId: number    // 接收方用户 ID
  pointAmount: number // 积分数量
  description?: string // 备注
}

// ========== 响应 VO ==========

/** 我的代理信息 */
export interface MyAgencyVO {
  agencyEnabled: boolean  // 是否开启代理
  level: number          // 代理等级：0=普通用户，1=一级代理，2=二级代理
  totalPoints: number     // 总积分
  distributedPoints: number  // 已分配积分
  availablePoints: number   // 可分配积分
  directInviteCount: number  // 直推人数
  teamTotalCount: number     // 团队总人数
  // 以下字段兼容前端已有结构
  levelName?: string
  totalCommission?: number   // 累计收益（积分）
  todayCommission?: number   // 今日收益
  frozenCommission?: number // 冻结收益
  childrenCount?: number     // 下级用户数
}

/** 下级代理/用户 */
export interface ChildAgencyVO {
  userId: number
  nickname: string
  level: number
  agencyEnabled: boolean
  totalPoints: number
  distributedPoints: number
  createTime?: string
}

/** 代理积分钱包 */
export interface AgencyWalletVO {
  availablePoints: number   // 可用积分
  frozenPoints: number      // 冻结积分
  totalDistributed: number // 累计分配
  totalReceived: number    // 累计获得
}

/** 分佣记录 */
export interface CommissionRecord {
  id: number
  type: 'commission' | 'transfer' | 'recharge'
  title: string
  amount: number     // 正数=获得，负数=支出
  createTime: string
}

// ========== API 方法 ==========

/** 获取我的代理信息 */
export function getMyAgency() {
  return get<MyAgencyVO>('/app/agency/user/my')
}

/** 绑定上级代理 */
export function bindAgency(data: BindAgencyReqVO) {
  return post<Boolean>('/app/agency/user/bind', data)
}

/** 分配积分给用户 */
export function transferPoints(data: TransferPointsReqVO) {
  return post<Boolean>('/app/agency/point/transfer', data)
}

/** 获取我的下级列表 */
export function getChildren() {
  return get<ChildAgencyVO[]>('/app/agency/user/children')
}

/** 获取我的代理积分钱包 */
export function getAgencyWallet() {
  return get<AgencyWalletVO>('/app/agency/point/wallet')
}

/** 获取分佣记录（兼容前端已有结构） */
export function getCommissionRecords(params?: { pageNo?: number; pageSize?: number }) {
  return get<{ list: CommissionRecord[]; total: number }>('/app/agency/commission/records', params)
}
