/**
 * 通用 API 类型定义
 * 与后端 CommonResult 对应
 */

// ========== 通用响应结构 ==========

/** 通用分页结果 */
export interface PageResult<T> {
  list: T[]
  total: number
}

/** 通用 API 响应 */
export interface ApiResult<T = any> {
  code: number    // 0=成功，非0=失败
  data: T
  msg?: string
  message?: string
}

// ========== 业务类型 ==========

export interface User {
  id: number
  nickname: string
  avatar: string
  phone: string
  level: number       // 代理等级：0=普通用户，1=一级代理，2=二级代理
  brokerageUserId: number | null  // 上级代理用户ID
  inviteCode?: string   // 邀请码
}

export interface Wallet {
  id: number
  userId: number
  balance: number        // 积分余额
  frozenBalance: number   // 冻结积分
  totalRecharge: number   // 累计充值
  totalUsed: number       // 累计使用
}

export interface WalletTransaction {
  id: number
  walletId: number
  bizType: number
  bizTypeName: string
  amount: number
  balance: number
  description: string
  createTime: string
}

export interface AgencyUser {
  agencyEnabled: boolean
  level: number
  levelName?: string
  totalPoints: number
  distributedPoints: number
  availablePoints: number
  directInviteCount: number
  teamTotalCount: number
  totalCommission: number
  todayCommission: number
  frozenCommission: number
  childrenCount: number
  inviteCode?: string
}

export interface RechargePackage {
  id: number
  name: string
  price: number        // 单位：元
  points: number       // 获得积分
  bonusPoints: number   // 赠送积分
  isHot?: boolean
  isBest?: boolean
}

export interface AiImageRecord {
  id: number
  prompt: string
  style: string
  imageUrl: string
  status: 'pending' | 'generating' | 'success' | 'failed'
  pointsCost: number
  createTime: string
}

export interface AiVideoRecord {
  id: number
  prompt: string
  duration: string
  videoUrl?: string
  thumbnailUrl?: string
  status: 'pending' | 'generating' | 'success' | 'failed'
  progress: number
  pointsCost: number
  createTime: string
}

export interface NavItem {
  label: string
  path: string
  icon?: string
  children?: NavItem[]
}

export interface MenuItem {
  id: string
  label: string
  icon: string
  path: string
  badge?: string | number
  description?: string
}

export interface FeatureCard {
  icon: string
  title: string
  description: string
  gradient: string
}

// ========== 积分业务类型枚举 ==========

/** 积分变动业务类型 */
export enum PointBizType {
  RECHARGE = 1,          // 充值
  AI_IMAGE = 2,           // AI 图片生成
  AI_VIDEO = 3,           // AI 视频生成
  AGENCY_COMMISSION = 4, // 代理分佣
  TRANSFER_IN = 5,        // 积分转入
  TRANSFER_OUT = 6,       // 积分转出
  REFUND = 7,             // 退款
  ADMIN_ADJUST = 8,       // 管理员调整
}

/** 代理等级 */
export enum AgencyLevel {
  NORMAL = 0,    // 普通用户
  LEVEL_1 = 1,   // 一级代理
  LEVEL_2 = 2,   // 二级代理
}
