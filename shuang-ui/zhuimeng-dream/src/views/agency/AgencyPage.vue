<template>
  <div class="agency-page" style="padding-top: 32px;">
    <div class="page-container">
      <!-- 代理概览 -->
      <div class="agency-hero glass-card glow">
        <div class="hero-content">
          <div class="agency-badge">
            <span v-html="icons.star" class="icon-wrap" style="width: 18px; height: 18px;" />
            <span>{{ agencyInfo.levelName }}</span>
          </div>
          <h1 class="hero-title">代理中心</h1>
          <p class="hero-sub">分享创意工具，获得下级充值分成，轻松实现副业变现</p>
        </div>

        <div class="hero-stats">
          <div class="hero-stat">
            <div class="hero-stat-val text-gradient stat-number">{{ agencyInfo.totalCommission }}</div>
            <div class="hero-stat-label">累计收益（积分）</div>
          </div>
          <div class="hero-stat">
            <div class="hero-stat-val stat-number" style="color: #22d3ee;">{{ agencyInfo.todayCommission }}</div>
            <div class="hero-stat-label">今日收益</div>
          </div>
          <div class="hero-stat">
            <div class="hero-stat-val stat-number" style="color: #f87171;">{{ agencyInfo.frozenCommission }}</div>
            <div class="hero-stat-label">冻结中</div>
          </div>
          <div class="hero-stat">
            <div class="hero-stat-val stat-number" style="color: #f59e0b;">{{ agencyInfo.childrenCount }}</div>
            <div class="hero-stat-label">下级用户</div>
          </div>
        </div>
      </div>

      <div class="agency-grid">
        <!-- 左侧：代理信息 + 积分分配 -->
        <div class="left-col">
          <!-- 代理等级 -->
          <div class="glass-card info-card">
            <h3 class="card-title">代理等级权益</h3>
            <div class="level-cards">
              <div v-for="level in levelInfo" :key="level.name" class="level-card" :class="{ active: agencyInfo.level >= level.level }">
                <div class="level-header">
                  <span class="level-name">{{ level.name }}</span>
                  <span class="level-rate text-gradient">{{ level.rate }}</span>
                </div>
                <p class="level-desc">{{ level.desc }}</p>
                <div class="level-progress">
                  <div class="level-progress-fill" :style="{ width: agencyInfo.level >= level.level ? '100%' : '0%' }" />
                </div>
              </div>
            </div>
          </div>

          <!-- 积分分配 -->
          <div class="glass-card">
            <h3 class="card-title">分配积分给用户</h3>
            <p class="card-desc">将你的积分分配给下级用户，帮助他们进行 AI 创作</p>
            <div class="transfer-form">
              <div class="form-field">
                <label class="field-label">接收方用户</label>
                <input v-model="transferForm.toUser" type="text" class="input-dream" placeholder="输入用户ID或手机号" />
              </div>
              <div class="form-field">
                <label class="field-label">分配积分数量</label>
                <input v-model.number="transferForm.amount" type="number" class="input-dream" placeholder="输入积分数量" min="1" />
              </div>
              <div class="form-field">
                <label class="field-label">备注（选填）</label>
                <input v-model="transferForm.note" type="text" class="input-dream" placeholder="填写分配说明" />
              </div>
              <div class="transfer-balance">
                <span>当前可分配积分：</span>
                <span class="text-gradient" style="font-weight: 700;">{{ agencyWallet.availablePoints }}</span>
              </div>
              <button class="btn-primary transfer-btn" :disabled="!canTransfer" @click="doTransfer">
                <span v-html="icons.exchange" class="icon-wrap" style="width: 16px; height: 16px;" />
                确认分配
              </button>
            </div>
          </div>

          <!-- 代理关系 -->
          <div class="glass-card">
            <h3 class="card-title">代理关系链</h3>
            <div class="relation-chain">
              <div class="chain-node chain-me">
                <div class="chain-avatar">我</div>
                <div class="chain-info">
                  <div class="chain-name">{{ agencyInfo.levelName }}</div>
                  <div class="chain-sub">当前代理</div>
                </div>
              </div>
              <div class="chain-arrow">
                <span v-html="icons.arrow" class="icon-wrap" style="width: 24px; height: 24px;" />
              </div>
              <div class="chain-node" v-for="child in children" :key="child.key">
                <div class="chain-avatar" :style="{ background: child.color }">{{ child.name }}</div>
                <div class="chain-info">
                  <div class="chain-name">{{ child.count }}人</div>
                  <div class="chain-sub">{{ child.level }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：分佣记录 -->
        <div class="right-col">
          <div class="glass-card records-card">
            <div class="records-header">
              <h3 class="card-title">分佣记录</h3>
              <div class="records-filter">
                <button v-for="f in filters" :key="f.key" class="filter-btn" :class="{ active: activeFilter === f.key }" @click="activeFilter = f.key">{{ f.label }}</button>
              </div>
            </div>

            <div class="records-list">
              <div v-for="record in filteredRecords" :key="record.id" class="record-item">
                <div class="record-icon" :class="record.type">
                  <span v-if="record.type === 'commission'" v-html="icons.star" class="icon-wrap" style="width: 16px; height: 16px;" />
                  <span v-else v-html="icons.exchange" class="icon-wrap" style="width: 16px; height: 16px;" />
                </div>
                <div class="record-info">
                  <div class="record-title">{{ record.title }}</div>
                  <div class="record-sub">{{ record.time }}</div>
                </div>
                <div class="record-amount" :class="record.type">
                  {{ record.amount > 0 ? '+' : '' }}{{ record.amount }}
                </div>
              </div>
            </div>

            <div class="records-footer">
              <button class="btn-ghost" @click="loadMore">加载更多</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { cosmicIcons } from '@/utils/cosmicIcons'
import { useAppStore } from '@/stores/app'
import { storeToRefs } from 'pinia'
import {
  getAgencyWallet,
  getChildren as fetchAgencyChildren,
  getCommissionRecords,
  getMyAgency,
  transferPoints,
  type ChildAgencyVO,
  type CommissionRecord,
} from '@/api/agency'
import { toast } from '@/utils/toast'

const icons = cosmicIcons

const store = useAppStore()
const { agencyUser } = storeToRefs(store)

const agencyInfo = computed(() => agencyUser.value ?? {
  agencyEnabled: false,
  level: 0,
  levelName: '普通用户',
  totalPoints: 0,
  distributedPoints: 0,
  availablePoints: 0,
  directInviteCount: 0,
  teamTotalCount: 0,
  totalCommission: 0,
  todayCommission: 0,
  frozenCommission: 0,
  childrenCount: 0,
})

const agencyWallet = ref({
  availablePoints: 0,
  frozenPoints: 0,
  totalDistributed: 0,
  totalReceived: 0,
})

const levelInfo = [
  { level: 2, name: '一级代理', rate: '20% 分成', desc: '直推≥100人 或 付费999元，可享下级充值20%分成' },
  { level: 1, name: '二级代理', rate: '8% 分成', desc: '由一级代理邀请，可享下级充值8%分成' },
]

const levelNameMap: Record<number, string> = {
  0: '普通用户',
  1: '二级代理',
  2: '一级代理',
}

const palette = ['#7c3aed', '#06b6d4', '#6366f1', '#f59e0b', '#ec4899']
const childRecords = ref<ChildAgencyVO[]>([])
const children = computed(() => childRecords.value.slice(0, 5).map((child, index) => ({
  key: child.userId,
  name: (child.nickname || `U${child.userId}`).slice(0, 1),
  count: Math.max(1, child.totalPoints > 0 ? 1 : 0),
  level: levelNameMap[child.level] || '普通用户',
  color: palette[index % palette.length],
})))

const transferForm = ref({ toUser: '', amount: 0, note: '' })
const loading = ref(false)
const loadingMoreRecords = ref(false)
const recordPageNo = ref(1)

const canTransfer = computed(() => {
  const toUserId = Number(transferForm.value.toUser)
  return Number.isInteger(toUserId) && toUserId > 0 && transferForm.value.amount > 0 && transferForm.value.amount <= agencyWallet.value.availablePoints
})

async function doTransfer() {
  if (!canTransfer.value) return
  try {
    await transferPoints({
      toUserId: Number(transferForm.value.toUser),
      pointAmount: transferForm.value.amount,
      description: transferForm.value.note || undefined,
    })
    toast.success(`已分配 ${transferForm.value.amount} 积分`)
    transferForm.value = { toUser: '', amount: 0, note: '' }
    await loadAgencyData()
  } catch (error) {
    toast.error(error instanceof Error ? error.message : '积分分配失败')
  }
}

const filters = [
  { key: 'all', label: '全部' },
  { key: 'commission', label: '分佣' },
  { key: 'transfer', label: '积分分配' },
]

const activeFilter = ref('all')

const records = ref<Array<CommissionRecord & { time: string; viewType: 'commission' | 'transfer' }>>([])

const filteredRecords = computed(() => {
  if (activeFilter.value === 'all') return records.value
  return records.value.filter(r => r.viewType === activeFilter.value)
})

function formatTime(time?: string) {
  if (!time) return '-'
  return time.replace('T', ' ').slice(0, 16)
}

function normalizeRecord(record: CommissionRecord) {
  const isTransfer = record.type === 'transfer'
  const isNegative = record.amount < 0 || record.type === 'consume'
  return {
    ...record,
    type: record.type,
    viewType: isTransfer || isNegative ? 'transfer' as const : 'commission' as const,
    title: record.title || (isNegative ? '积分分配' : '分佣到账'),
    time: formatTime(record.createTime),
  }
}

async function loadAgencyData() {
  loading.value = true
  try {
    const [agency, wallet, childrenList, commissionList] = await Promise.all([
      getMyAgency(),
      getAgencyWallet(),
      fetchAgencyChildren(),
      getCommissionRecords({ pageNo: 1, pageSize: 10 }),
    ])
    store.agencyUser = {
      agencyEnabled: agency.agencyEnabled,
      level: agency.level,
      levelName: agency.levelName || levelNameMap[agency.level] || '普通用户',
      totalPoints: agency.totalPoints ?? 0,
      distributedPoints: agency.distributedPoints ?? 0,
      availablePoints: agency.availablePoints ?? 0,
      directInviteCount: agency.directInviteCount ?? 0,
      teamTotalCount: agency.teamTotalCount ?? 0,
      totalCommission: agency.totalCommission ?? agency.totalPoints ?? 0,
      todayCommission: agency.todayCommission ?? 0,
      frozenCommission: agency.frozenCommission ?? wallet.frozenPoints ?? 0,
      childrenCount: agency.childrenCount ?? agency.teamTotalCount ?? childrenList.length,
      inviteCode: agency.inviteCode,
    }
    agencyWallet.value = wallet
    childRecords.value = childrenList
    records.value = commissionList.map(normalizeRecord)
    recordPageNo.value = 1
  } catch (error) {
    toast.error(error instanceof Error ? error.message : '代理数据加载失败')
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  if (loadingMoreRecords.value) return
  loadingMoreRecords.value = true
  try {
    const nextPage = recordPageNo.value + 1
    const list = await getCommissionRecords({ pageNo: nextPage, pageSize: 10 })
    if (!list.length) {
      toast.warning('没有更多记录了')
      return
    }
    records.value.push(...list.map(normalizeRecord))
    recordPageNo.value = nextPage
  } catch (error) {
    toast.error(error instanceof Error ? error.message : '加载更多失败')
  } finally {
    loadingMoreRecords.value = false
  }
}

onMounted(() => {
  loadAgencyData()
})
</script>

<style scoped>
.icon-wrap { display: flex; align-items: center; justify-content: center; }
.icon-wrap :deep(svg) { width: 100%; height: 100%; }
.agency-page { min-height: 100vh; background: var(--color-bg-base); }
.page-container { max-width: 1280px; margin: 0 auto; padding: 32px 24px 24px; display: flex; flex-direction: column; gap: 24px; }

/* Hero */
.agency-hero { padding: 32px; display: flex; flex-direction: column; gap: 32px; }
.hero-content { }
.agency-badge { display: inline-flex; align-items: center; gap: 6px; padding: 6px 14px; background: rgba(245,158,11,0.1); border: 1px solid rgba(245,158,11,0.3); border-radius: 9999px; font-size: 13px; font-weight: 600; color: #f59e0b; margin-bottom: 12px; }
.hero-title { font-size: 32px; font-weight: 800; letter-spacing: -0.02em; }
.hero-sub { font-size: 14px; color: var(--color-text-muted); margin-top: 8px; }
.hero-stats { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.hero-stat { padding: 20px; background: rgba(255,255,255,0.03); border-radius: 12px; text-align: center; }
.hero-stat-val { font-size: 32px; font-weight: 800; letter-spacing: -0.02em; }
.hero-stat-label { font-size: 12px; color: var(--color-text-muted); margin-top: 4px; }

/* Grid */
.agency-grid { display: grid; grid-template-columns: 420px 1fr; gap: 24px; }
.left-col, .right-col { display: flex; flex-direction: column; gap: 20px; }
.card-title { font-size: 15px; font-weight: 700; margin-bottom: 16px; }
.card-desc { font-size: 13px; color: var(--color-text-muted); margin-bottom: 16px; }

/* 等级卡片 */
.level-cards { display: flex; flex-direction: column; gap: 12px; }
.level-card { padding: 16px; background: rgba(255,255,255,0.02); border: 1px solid rgba(255,255,255,0.06); border-radius: 12px; transition: all 0.2s ease; }
.level-card.active { background: rgba(124,58,237,0.06); border-color: rgba(124,58,237,0.2); }
.level-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.level-name { font-size: 14px; font-weight: 600; color: var(--color-text-primary); }
.level-rate { font-size: 14px; font-weight: 800; }
.level-desc { font-size: 12px; color: var(--color-text-muted); line-height: 1.5; }
.level-progress { height: 3px; background: rgba(255,255,255,0.06); border-radius: 9999px; margin-top: 10px; overflow: hidden; }
.level-progress-fill { height: 100%; background: var(--gradient-dream); border-radius: 9999px; transition: width 0.5s ease; }

/* 积分分配 */
.transfer-form { display: flex; flex-direction: column; gap: 14px; }
.form-field { display: flex; flex-direction: column; gap: 6px; }
.field-label { font-size: 13px; font-weight: 600; color: var(--color-text-secondary); }
.transfer-balance { font-size: 13px; color: var(--color-text-muted); display: flex; gap: 6px; }
.transfer-btn { width: 100%; }
.transfer-btn:disabled { opacity: 0.5; cursor: not-allowed; }

/* 关系链 */
.relation-chain { display: flex; align-items: center; gap: 16px; flex-wrap: wrap; }
.chain-node { display: flex; align-items: center; gap: 10px; padding: 10px 14px; background: rgba(255,255,255,0.03); border-radius: 10px; }
.chain-me { background: rgba(124,58,237,0.1); border: 1px solid rgba(124,58,237,0.2); }
.chain-avatar { width: 36px; height: 36px; border-radius: 50%; background: var(--gradient-dream); display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 700; color: white; }
.chain-name { font-size: 13px; font-weight: 600; color: var(--color-text-primary); }
.chain-sub { font-size: 11px; color: var(--color-text-muted); }
.chain-arrow { color: var(--color-text-muted); }

/* 分佣记录 */
.records-card { }
.records-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.records-filter { display: flex; gap: 4px; background: rgba(255,255,255,0.04); padding: 3px; border-radius: 8px; }
.filter-btn { padding: 5px 12px; border-radius: 6px; border: none; background: transparent; color: var(--color-text-muted); font-size: 12px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; }
.filter-btn.active { background: var(--gradient-dream); color: white; }
.records-list { display: flex; flex-direction: column; }
.record-item { display: flex; align-items: center; gap: 12px; padding: 14px 0; border-bottom: 1px solid rgba(255,255,255,0.04); }
.record-item:last-child { border-bottom: none; }
.record-icon { width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.record-icon.commission { background: linear-gradient(135deg, #7c3aed, #8b5cf6); }
.record-icon.transfer { background: linear-gradient(135deg, #06b6d4, #22d3ee); }
.record-info { flex: 1; }
.record-title { font-size: 13px; font-weight: 500; color: var(--color-text-primary); }
.record-sub { font-size: 11px; color: var(--color-text-muted); margin-top: 2px; }
.record-amount { font-size: 15px; font-weight: 700; flex-shrink: 0; }
.record-amount.commission { color: #10b981; }
.record-amount.transfer { color: #f87171; }
.records-footer { display: flex; justify-content: center; padding-top: 12px; }
.records-footer button { font-size: 13px; }

@media (max-width: 900px) {
  .agency-grid { grid-template-columns: 1fr; }
  .hero-stats { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 600px) {
  .hero-stats { grid-template-columns: 1fr; }
  .relation-chain { flex-direction: column; align-items: flex-start; }
}
</style>
