export interface User {
  id: number
  nickname: string
  avatar: string
  phone: string
  level: number       // 代理等级：0=普通用户，1=一级代理，2=二级代理
  brokerageUserId: number | null  // 上级代理用户ID
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
  id: number
  userId: number
  level: number
  brokerageEnabled: boolean
  totalCommission: number
  frozenCommission: number
  childrenCount: number
  createTime: string
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
