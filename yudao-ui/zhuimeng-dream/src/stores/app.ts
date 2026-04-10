import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, Wallet, AgencyUser, AiImageRecord, AiVideoRecord, RechargePackage } from '@/types'

export const useAppStore = defineStore('app', () => {
  // --- 用户状态 ---
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))
  const isLoggedIn = computed(() => !!token.value)

  // --- 钱包状态 ---
  const wallet = ref<Wallet>({
    id: 1,
    userId: 1,
    balance: 288,
    frozenBalance: 0,
    totalRecharge: 580,
    totalUsed: 292,
  })

  // --- 代理状态 ---
  const agencyUser = ref<AgencyUser | null>(null)

  // --- AI 生成记录 ---
  const imageRecords = ref<AiImageRecord[]>([])
  const videoRecords = ref<AiVideoRecord[]>([])

  // --- 积分扣减 ---
  function deductPoints(amount: number): boolean {
    if (wallet.value.balance < amount) return false
    wallet.value.balance -= amount
    wallet.value.totalUsed += amount
    return true
  }

  // --- 积分增加 ---
  function addPoints(amount: number) {
    wallet.value.balance += amount
    wallet.value.totalRecharge += amount
  }

  // --- 登录 ---
  function login(newToken: string, userInfo: User) {
    token.value = newToken
    user.value = userInfo
    localStorage.setItem('token', newToken)
  }

  // --- 登出 ---
  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  return {
    user,
    token,
    isLoggedIn,
    wallet,
    agencyUser,
    imageRecords,
    videoRecords,
    deductPoints,
    addPoints,
    login,
    logout,
  }
})

export const useRechargeStore = defineStore('recharge', () => {
  const packages = ref<RechargePackage[]>([
    { id: 1, name: '体验版', price: 30, points: 30, bonusPoints: 0 },
    { id: 2, name: '标准版', price: 99, points: 100, bonusPoints: 10, isHot: true },
    { id: 3, name: '专业版', price: 199, points: 220, bonusPoints: 30, isBest: true },
    { id: 4, name: '旗舰版', price: 399, points: 500, bonusPoints: 80 },
  ])

  return { packages }
})
