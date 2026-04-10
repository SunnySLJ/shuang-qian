<template>
  <div class="recharge-page" style="padding-top: 32px;">
    <div class="page-container">
      <!-- 钱包概览 -->
      <div class="wallet-overview glass-card">
        <div class="wallet-info">
          <div class="wallet-label">当前积分余额</div>
          <div class="wallet-balance text-gradient stat-number">{{ wallet.balance }}</div>
          <div class="wallet-sub">累计充值：{{ wallet.totalRecharge }} 积分 · 已消耗：{{ wallet.totalUsed }} 积分</div>
        </div>
        <div class="wallet-actions">
          <router-link to="/ai/image" class="btn-primary">去创作</router-link>
        </div>
      </div>

      <!-- 充值套餐 -->
      <div class="section-block">
        <h2 class="section-title">选择套餐</h2>
        <div class="packages-grid">
          <div
            v-for="pkg in packages"
            :key="pkg.id"
            class="package-card glass-card"
            :class="{ 'package-best': pkg.isBest, 'package-hot': pkg.isHot }"
          >
            <div v-if="pkg.isBest" class="package-badge best-badge">最受欢迎</div>
            <div v-if="pkg.isHot" class="package-badge hot-badge">限时特惠</div>
            <div class="package-name">{{ pkg.name }}</div>
            <div class="package-price">
              <span class="pkg-symbol">¥</span>
              <span class="pkg-value">{{ pkg.price }}</span>
            </div>
            <div class="package-points">
              <span class="pkg-points-main">{{ pkg.points }} 积分</span>
              <span v-if="pkg.bonusPoints > 0" class="pkg-points-bonus">+{{ pkg.bonusPoints }} 赠送</span>
            </div>
            <div class="package-unit">约 {{ (pkg.points / pkg.price).toFixed(1) }} 积分/元</div>
            <ul class="package-features">
              <li>
                <span v-html="icons.check" class="icon-wrap" style="width: 14px; height: 14px;" />
                图片生成 {{ Math.floor(pkg.points / 2) }} 次
              </li>
              <li>
                <span v-html="icons.check" class="icon-wrap" style="width: 14px; height: 14px;" />
                视频生成 {{ Math.floor(pkg.points / 10) }} 次
              </li>
              <li v-if="pkg.isBest || pkg.isHot">
                <span v-html="icons.check" class="icon-wrap" style="width: 14px; height: 14px;" />
                优先队列生成
              </li>
            </ul>
            <button class="package-cta" :class="{ 'pkg-cta-best': pkg.isBest }" @click="selectPackage(pkg)">
              立即购买
            </button>
          </div>
        </div>
      </div>

      <!-- 自定义充值 -->
      <div class="section-block">
        <h2 class="section-title">自定义充值</h2>
        <div class="custom-recharge glass-card">
          <div class="custom-input-group">
            <label class="field-label">充值积分数量</label>
            <div class="custom-input-wrap">
              <input v-model.number="customPoints" type="number" class="input-dream custom-input" placeholder="输入积分数量" min="10" step="10" />
              <span class="input-suffix">积分</span>
            </div>
          </div>
          <div class="custom-result">
            <span class="custom-label">应付金额</span>
            <span class="custom-amount text-gradient stat-number">¥{{ customAmount }}</span>
          </div>
          <button class="btn-primary" style="width: 100%;" @click="customRecharge">确认充值</button>
        </div>
      </div>

      <!-- 充值记录 -->
      <div class="section-block">
        <h2 class="section-title">充值记录</h2>
        <div class="records-table glass-card">
          <table>
            <thead>
              <tr>
                <th>订单号</th>
                <th>套餐</th>
                <th>积分</th>
                <th>金额</th>
                <th>状态</th>
                <th>时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="record in records" :key="record.id">
                <td class="order-id">{{ record.id }}</td>
                <td>{{ record.name }}</td>
                <td class="points-col">+{{ record.points }}</td>
                <td>¥{{ record.price }}</td>
                <td><span class="status-badge success">成功</span></td>
                <td>{{ record.time }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 支付弹窗 -->
      <transition name="modal">
        <div v-if="payModalOpen" class="modal-overlay" @click.self="payModalOpen = false">
          <div class="pay-modal glass-card">
            <div class="pay-modal-header">
              <h3>确认支付</h3>
              <button class="modal-close" @click="payModalOpen = false">✕</button>
            </div>
            <div class="pay-modal-body">
              <div class="pay-summary">
                <div class="pay-item">
                  <span class="pay-item-label">套餐</span>
                  <span class="pay-item-value">{{ selectedPkg?.name }}</span>
                </div>
                <div class="pay-item">
                  <span class="pay-item-label">获得积分</span>
                  <span class="pay-item-value points-highlight">+{{ selectedPkg?.points + (selectedPkg?.bonusPoints || 0) }}</span>
                </div>
                <div class="pay-item total">
                  <span class="pay-item-label">应付金额</span>
                  <span class="pay-item-value text-gradient">¥{{ selectedPkg?.price }}</span>
                </div>
              </div>
              <div class="pay-methods">
                <div class="pay-method-label">选择支付方式</div>
                <div class="pay-method-list">
                  <button v-for="method in payMethods" :key="method.id" class="pay-method-btn" :class="{ active: payMethod === method.id }" @click="payMethod = method.id">
                    <span class="pay-method-icon" v-html="method.icon" />
                    <span>{{ method.name }}</span>
                  </button>
                </div>
              </div>
              <button class="btn-primary pay-confirm-btn" @click="confirmPay">
                <span v-html="icons.recharge" class="icon-wrap" style="width: 18px; height: 18px;" />
                确认支付 ¥{{ selectedPkg?.price }}
              </button>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { cosmicIcons } from '@/utils/cosmicIcons'
import { useAppStore, useRechargeStore } from '@/stores/app'
import { storeToRefs } from 'pinia'

const icons = cosmicIcons

const store = useAppStore()
const rechargeStore = useRechargeStore()
const { wallet } = storeToRefs(store)
const { addPoints } = store
const { packages } = storeToRefs(rechargeStore)

const customPoints = ref(100)
const payModalOpen = ref(false)
const selectedPkg = ref<(typeof packages)[0] | null>(null)
const payMethod = ref('wechat')

const customAmount = computed(() => {
  return Math.max(10, Math.floor(customPoints.value * 0.1))
})

const records = [
  { id: 'DZ20260328001', name: '标准版', points: 110, price: 99, time: '2026-03-28 14:32' },
  { id: 'DZ20260325002', name: '专业版', points: 250, price: 199, time: '2026-03-25 09:15' },
  { id: 'DZ20260320003', name: '体验版', points: 30, price: 30, time: '2026-03-20 20:44' },
]

const payMethods = [
  { id: 'wechat', name: '微信支付', icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="#07c160"><path d="M8.5 11.5a1 1 0 1 0 0-2 1 1 0 0 0 0 2zm5 0a1 1 0 1 0 0-2 1 1 0 0 0 0 2z"/><path d="M12 2C6.477 2 2 6.477 2 12c0 1.89.525 3.66 1.438 5.168L2.586 20.4a1 1 0 0 0 1.014.852l3.257-1.178A11.95 11.95 0 0 0 12 22c5.523 0 10-4.477 10-10S17.523 2 12 2z" fill="none" stroke="#07c160" stroke-width="1.5"/></svg>` },
  { id: 'alipay', name: '支付宝', icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="#1677ff"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15h2v-6h-2v6zm0-8h2V7h-2v2z"/></svg>` },
  { id: 'card', name: '银行卡', icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#6366f1" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>` },
]

function selectPackage(pkg: (typeof packages)[0]) {
  selectedPkg.value = pkg
  payModalOpen.value = true
}

function customRecharge() {
  const pkg = {
    id: 0,
    name: '自定义充值',
    price: customAmount.value,
    points: customPoints.value,
    bonusPoints: 0,
  }
  selectedPkg.value = pkg
  payModalOpen.value = true
}

async function confirmPay() {
  if (!selectedPkg.value) return
  // TODO: 调用支付API
  await new Promise(resolve => setTimeout(resolve, 1500))
  addPoints(selectedPkg.value.points + (selectedPkg.value.bonusPoints || 0))
  payModalOpen.value = false
  alert(`充值成功！获得 ${selectedPkg.value.points + (selectedPkg.value.bonusPoints || 0)} 积分`)
}
</script>

<style scoped>
.icon-wrap { display: flex; align-items: center; justify-content: center; }
.icon-wrap :deep(svg) { width: 100%; height: 100%; }
.recharge-page { min-height: 100vh; background: var(--color-bg-base); }
.page-container { max-width: 1100px; margin: 0 auto; padding: 32px 24px 24px; display: flex; flex-direction: column; gap: 32px; }

/* 钱包概览 */
.wallet-overview { display: flex; justify-content: space-between; align-items: center; padding: 32px; }
.wallet-label { font-size: 13px; color: var(--color-text-muted); margin-bottom: 8px; }
.wallet-balance { font-size: 48px; font-weight: 800; letter-spacing: -0.03em; }
.wallet-sub { font-size: 13px; color: var(--color-text-muted); margin-top: 8px; }

/* 套餐 */
.section-block { }
.section-title { font-size: 18px; font-weight: 700; margin-bottom: 20px; }
.packages-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.package-card { padding: 28px 20px; display: flex; flex-direction: column; gap: 10px; position: relative; transition: all 0.3s ease; }
.package-card:hover { transform: translateY(-4px); }
.package-best { border-color: rgba(124,58,237,0.4); background: rgba(124,58,237,0.06); transform: translateY(-8px); }
.package-best:hover { transform: translateY(-12px); }
.package-badge { position: absolute; top: -10px; left: 50%; transform: translateX(-50%); padding: 3px 12px; border-radius: 9999px; font-size: 11px; font-weight: 700; color: white; white-space: nowrap; }
.best-badge { background: var(--gradient-dream); }
.hot-badge { background: linear-gradient(135deg, #f59e0b, #fb923c); }
.package-name { font-size: 15px; font-weight: 600; color: var(--color-text-secondary); text-align: center; }
.package-price { display: flex; align-items: baseline; justify-content: center; gap: 2px; }
.pkg-symbol { font-size: 16px; color: var(--color-text-secondary); }
.pkg-value { font-size: 36px; font-weight: 800; color: var(--color-text-primary); letter-spacing: -0.03em; }
.package-points { display: flex; flex-direction: column; align-items: center; gap: 4px; }
.pkg-points-main { font-size: 14px; font-weight: 600; color: var(--color-text-secondary); }
.pkg-points-bonus { font-size: 12px; color: #10b981; font-weight: 600; }
.package-unit { font-size: 12px; color: var(--color-text-muted); text-align: center; }
.package-features { list-style: none; display: flex; flex-direction: column; gap: 8px; flex: 1; margin-top: 4px; }
.package-features li { display: flex; align-items: center; gap: 8px; font-size: 12px; color: var(--color-text-secondary); }
.package-features li svg { color: #10b981; flex-shrink: 0; }
.package-cta { padding: 11px; border-radius: 10px; background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.1); color: var(--color-text-primary); font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; }
.package-cta:hover { background: rgba(124,58,237,0.15); border-color: rgba(124,58,237,0.4); }
.pkg-cta-best { background: var(--gradient-dream); border: none; color: white; box-shadow: 0 4px 20px rgba(124,58,237,0.3); }
.pkg-cta-best:hover { box-shadow: 0 8px 30px rgba(124,58,237,0.5); }

/* 自定义充值 */
.custom-recharge { display: flex; flex-direction: column; gap: 20px; padding: 28px; max-width: 500px; }
.custom-input-group { display: flex; flex-direction: column; gap: 8px; }
.field-label { font-size: 13px; font-weight: 600; color: var(--color-text-secondary); }
.custom-input-wrap { display: flex; align-items: center; gap: 8px; }
.custom-input { flex: 1; }
.input-suffix { font-size: 14px; color: var(--color-text-muted); }
.custom-result { display: flex; justify-content: space-between; align-items: center; padding: 16px; background: rgba(124,58,237,0.06); border-radius: 10px; }
.custom-label { font-size: 14px; color: var(--color-text-secondary); }
.custom-amount { font-size: 28px; font-weight: 800; }

/* 表格 */
.records-table { padding: 0; overflow: hidden; }
table { width: 100%; border-collapse: collapse; }
th { text-align: left; padding: 14px 20px; font-size: 12px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: 0.05em; background: rgba(255,255,255,0.02); border-bottom: 1px solid rgba(255,255,255,0.05); }
td { padding: 14px 20px; font-size: 13px; color: var(--color-text-secondary); border-bottom: 1px solid rgba(255,255,255,0.03); }
tr:last-child td { border-bottom: none; }
tr:hover td { background: rgba(255,255,255,0.02); }
.order-id { font-family: monospace; font-size: 12px; color: var(--color-text-muted); }
.points-col { color: #10b981; font-weight: 600; }
.status-badge { padding: 3px 10px; border-radius: 9999px; font-size: 11px; font-weight: 600; }
.status-badge.success { background: rgba(16,185,129,0.1); color: #10b981; }

/* 支付弹窗 */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.7); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 24px; }
.pay-modal { width: 100%; max-width: 420px; padding: 0; overflow: hidden; }
.pay-modal-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; border-bottom: 1px solid rgba(255,255,255,0.06); }
.pay-modal-header h3 { font-size: 16px; font-weight: 700; }
.modal-close { width: 32px; height: 32px; border-radius: 8px; background: rgba(255,255,255,0.04); border: none; color: var(--color-text-muted); cursor: pointer; font-size: 14px; display: flex; align-items: center; justify-content: center; }
.modal-close:hover { background: rgba(255,255,255,0.08); color: var(--color-text-primary); }
.pay-modal-body { padding: 24px; display: flex; flex-direction: column; gap: 20px; }
.pay-summary { background: rgba(255,255,255,0.03); border-radius: 12px; padding: 16px; display: flex; flex-direction: column; gap: 10px; }
.pay-item { display: flex; justify-content: space-between; align-items: center; font-size: 13px; }
.pay-item-label { color: var(--color-text-muted); }
.pay-item-value { color: var(--color-text-primary); font-weight: 600; }
.pay-item.total { padding-top: 10px; border-top: 1px solid rgba(255,255,255,0.06); }
.points-highlight { color: #10b981; font-size: 15px; }
.pay-method-label { font-size: 13px; font-weight: 600; color: var(--color-text-secondary); margin-bottom: 10px; }
.pay-method-list { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
.pay-method-btn { display: flex; flex-direction: column; align-items: center; gap: 6px; padding: 12px 8px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 10px; color: var(--color-text-secondary); font-size: 12px; cursor: pointer; transition: all 0.2s ease; }
.pay-method-btn:hover { background: rgba(255,255,255,0.06); }
.pay-method-btn.active { border-color: rgba(124,58,237,0.4); background: rgba(124,58,237,0.08); color: var(--color-text-primary); }
.pay-method-icon { display: flex; }
.pay-confirm-btn { width: 100%; }

/* 动画 */
.modal-enter-active, .modal-leave-active { transition: all 0.3s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; }
.modal-enter-from .pay-modal, .modal-leave-to .pay-modal { transform: scale(0.95) translateY(20px); }

@media (max-width: 900px) { .packages-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) { .packages-grid { grid-template-columns: 1fr; } .wallet-overview { flex-direction: column; gap: 20px; text-align: center; } }
</style>
