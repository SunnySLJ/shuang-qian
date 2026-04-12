import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getMyWallet } from '@/api/member'
import { getMyAgency, type MyAgencyVO } from '@/api/agency'
import type { User, Wallet, AgencyUser, AiImageRecord, AiVideoRecord, RechargePackage } from '@/types'

export const useAppStore = defineStore('app', () => {
  const defaultWallet: Wallet = {
    id: 0,
    userId: 0,
    balance: 0,
    frozenBalance: 0,
    totalRecharge: 0,
    totalUsed: 0,
  }
  const storedUser = localStorage.getItem('userInfo')

  // --- 用户状态 ---
  const user = ref<User | null>(storedUser ? JSON.parse(storedUser) : null)
  const token = ref<string | null>(localStorage.getItem('token'))
  const isLoggedIn = computed(() => !!token.value)

  // --- 钱包状态 ---
  const wallet = ref<Wallet>({ ...defaultWallet })

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

  function setWallet(walletInfo: Wallet | null) {
    wallet.value = walletInfo ? { ...walletInfo } : { ...defaultWallet }
  }

  async function fetchWallet() {
    if (!token.value && !localStorage.getItem('token')) {
      setWallet(null)
      return null
    }
    const walletInfo = await getMyWallet()
    setWallet(walletInfo)
    return walletInfo
  }

  function normalizeAgency(agency: MyAgencyVO | null): AgencyUser | null {
    if (!agency) return null
    const levelNameMap: Record<number, string> = {
      0: '普通用户',
      1: '二级代理',
      2: '一级代理',
    }
    const totalCommission = agency.totalCommission ?? agency.totalPoints ?? 0
    const childrenCount = agency.childrenCount ?? agency.teamTotalCount ?? 0
    return {
      agencyEnabled: agency.agencyEnabled,
      level: agency.level,
      levelName: agency.levelName || levelNameMap[agency.level] || '普通用户',
      totalPoints: agency.totalPoints ?? 0,
      distributedPoints: agency.distributedPoints ?? 0,
      availablePoints: agency.availablePoints ?? 0,
      directInviteCount: agency.directInviteCount ?? 0,
      teamTotalCount: agency.teamTotalCount ?? 0,
      totalCommission,
      todayCommission: agency.todayCommission ?? 0,
      frozenCommission: agency.frozenCommission ?? 0,
      childrenCount,
      inviteCode: agency.inviteCode,
    }
  }

  async function fetchAgency() {
    if (!token.value && !localStorage.getItem('token')) {
      agencyUser.value = null
      return null
    }
    const agency = await getMyAgency()
    agencyUser.value = normalizeAgency(agency)
    return agencyUser.value
  }

  // --- 登录 ---
  function login(newToken: string, userInfo: User) {
    token.value = newToken
    user.value = userInfo
    localStorage.setItem('token', newToken)
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  }

  function setUser(userInfo: User | null) {
    user.value = userInfo
    if (userInfo) {
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    } else {
      localStorage.removeItem('userInfo')
    }
  }

  // --- 登出 ---
  function logout() {
    token.value = null
    user.value = null
    setWallet(null)
    agencyUser.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('refreshToken')
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
    setWallet,
    fetchWallet,
    fetchAgency,
    login,
    setUser,
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
