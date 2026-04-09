<template>
  <div class="dashboard-page">
    <div class="dashboard-main">
      <!-- 顶部欢迎 -->
      <div class="welcome-section">
        <div class="welcome-text">
          <h1 class="welcome-title">
            {{ greeting }}，<span class="text-gradient">{{ store.user?.nickname || '追梦用户' }}</span>
            <span class="welcome-wave">👋</span>
          </h1>
          <p class="welcome-sub">开始今天的创作吧，你的灵感值得被 AI 唤醒</p>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div v-for="stat in statsData" :key="stat.label" class="stat-card glass-card">
          <div class="stat-card-icon" :style="{ background: stat.gradient }">
            <span v-html="stat.icon" />
          </div>
          <div class="stat-card-info">
            <div class="stat-card-value stat-number" :style="{ color: stat.color }">{{ stat.value }}</div>
            <div class="stat-card-label">{{ stat.label }}</div>
          </div>
          <div class="stat-card-trend" :class="{ up: stat.trend > 0, down: stat.trend < 0 }">
            <svg v-if="stat.trend > 0" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="18 15 12 9 6 15"/></svg>
            <svg v-else width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"/></svg>
            {{ Math.abs(stat.trend) }}%
          </div>
        </div>
      </div>

      <!-- 快捷入口 -->
      <div class="quick-actions">
        <h2 class="section-title-sm">快速创作</h2>
        <div class="quick-actions-grid">
          <router-link
            v-for="action in quickActions"
            :key="action.path"
            :to="action.path"
            class="quick-action-card glass-card"
          >
            <div class="qa-icon" :style="{ background: action.gradient }">
              <span v-html="action.icon" />
            </div>
            <div class="qa-info">
              <div class="qa-title">{{ action.title }}</div>
              <div class="qa-desc">{{ action.desc }}</div>
            </div>
            <div class="qa-cost">
              <span class="qa-cost-val">{{ action.cost }}</span>
              <span class="qa-cost-label">积分</span>
            </div>
          </router-link>
        </div>
      </div>

      <!-- 近期作品 -->
      <div class="recent-works">
        <div class="section-header-row">
          <h2 class="section-title-sm">近期作品</h2>
          <router-link to="/ai/history" class="btn-ghost">查看全部 →</router-link>
        </div>
        <div class="works-grid">
          <div
            v-for="work in recentWorks"
            :key="work.id"
            class="work-card glass-card"
          >
            <div class="work-thumb">
              <img :src="work.thumbnail" :alt="work.title" />
              <div class="work-overlay">
                <button class="work-play-btn" v-if="work.type === 'video'">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="white"><polygon points="5 3 19 12 5 21 5 3"/></svg>
                </button>
              </div>
              <span class="work-type-badge" :class="work.type">{{ work.type === 'video' ? '视频' : '图片' }}</span>
            </div>
            <div class="work-info">
              <div class="work-title">{{ work.title }}</div>
              <div class="work-meta">
                <span>{{ work.time }}</span>
                <span>消耗 {{ work.cost }} 积分</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 代理数据 -->
      <div class="agency-overview" v-if="store.agencyUser">
        <h2 class="section-title-sm">代理收益概览</h2>
        <div class="agency-overview-grid">
          <div class="stat-card glass-card">
            <div class="stat-card-icon" style="background: linear-gradient(135deg, #f59e0b, #fbbf24)">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><path d="M12 2L15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
            </div>
            <div class="stat-card-info">
              <div class="stat-card-value stat-number text-gradient">{{ store.agencyUser.totalCommission }}</div>
              <div class="stat-card-label">累计收益（积分）</div>
            </div>
          </div>
          <div class="stat-card glass-card">
            <div class="stat-card-icon" style="background: linear-gradient(135deg, #7c3aed, #8b5cf6)">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            </div>
            <div class="stat-card-info">
              <div class="stat-card-value stat-number text-gradient">{{ store.agencyUser.childrenCount }}</div>
              <div class="stat-card-label">下级用户</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'
import { cosmicIcons } from '@/utils/cosmicIcons'

const store = useAppStore()

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return '早上好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const statsData = [
  { label: '积分余额', value: computed(() => store.wallet?.balance || 0), icon: cosmicIcons.star, gradient: 'linear-gradient(135deg, #7c3aed, #8b5cf6)', color: '#c4b5fd', trend: 12 },
  { label: '今日消耗', value: 28, icon: cosmicIcons.history, gradient: 'linear-gradient(135deg, #ec4899, #f472b6)', color: '#f9a8d4', trend: -8 },
  { label: '累计生成', value: 156, icon: cosmicIcons.image, gradient: 'linear-gradient(135deg, #06b6d4, #22d3ee)', color: '#22d3ee', trend: 24 },
  { label: '代理收益', value: computed(() => store.agencyUser?.totalCommission || 0), icon: cosmicIcons.chart, gradient: 'linear-gradient(135deg, #f59e0b, #fbbf24)', color: '#fcd34d', trend: 36 },
]

const quickActions = [
  { path: '/ai/image', title: 'AI 图片生成', desc: '输入描述词，AI 一键生成', cost: 2, gradient: 'linear-gradient(135deg, #7c3aed, #8b5cf6)', icon: cosmicIcons.image },
  { path: '/ai/video', title: 'AI 视频生成', desc: '文字转视频，创意动起来', cost: 10, gradient: 'linear-gradient(135deg, #06b6d4, #22d3ee)', icon: cosmicIcons.video },
  { path: '/recharge', title: '积分充值', desc: '多种套餐，灵活选择', cost: 0, gradient: 'linear-gradient(135deg, #10b981, #34d399)', icon: cosmicIcons.recharge },
  { path: '/agency', title: '代理中心', desc: '查看收益，管理下线', cost: 0, gradient: 'linear-gradient(135deg, #f59e0b, #fbbf24)', icon: cosmicIcons.users },
]

const recentWorks = [
  { id: 1, title: '未来城市天际线', thumbnail: 'https://images.unsplash.com/photo-1480714378408-67cf0d13bc1b?w=400&q=80', type: 'image', time: '10分钟前', cost: 2 },
  { id: 2, title: '山水意境国风短片', thumbnail: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&q=80', type: 'video', time: '30分钟前', cost: 10 },
  { id: 3, title: '赛博朋克霓虹女孩', thumbnail: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&q=80', type: 'image', time: '1小时前', cost: 2 },
  { id: 4, title: '梦幻星空动画', thumbnail: 'https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?w=400&q=80', type: 'video', time: '2小时前', cost: 10 },
]
</script>

<style scoped>
.dashboard-page {
  padding: 32px 32px;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.dashboard-main {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.welcome-section {
  display: flex;
  align-items: center;
}

.welcome-title {
  font-size: 28px;
  font-weight: 800;
  display: flex;
  align-items: center;
  gap: 8px;
}

.welcome-wave {
  animation: wave 1s ease-in-out infinite;
}

@keyframes wave {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(20deg); }
  75% { transform: rotate(-10deg); }
}

.welcome-sub {
  font-size: 14px;
  color: var(--color-text-muted);
  margin-top: 6px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.stat-card-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-card-icon :deep(svg) {
  width: 22px;
  height: 22px;
}

.stat-card-info {
  flex: 1;
  overflow: hidden;
}

.stat-card-value {
  font-size: 26px;
  font-weight: 800;
  letter-spacing: -0.02em;
  line-height: 1;
}

.stat-card-label {
  font-size: 12px;
  color: var(--color-text-muted);
  margin-top: 4px;
}

.stat-card-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 9999px;
  flex-shrink: 0;
}

.stat-card-trend.up { color: #10b981; background: rgba(16, 185, 129, 0.1); }
.stat-card-trend.down { color: #f87171; background: rgba(248, 113, 113, 0.1); }

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-top: 12px;
}

.quick-action-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  text-decoration: none;
  color: inherit;
}

.qa-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qa-icon :deep(svg) {
  width: 22px;
  height: 22px;
}

.qa-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 4px;
}

.qa-desc {
  font-size: 12px;
  color: var(--color-text-muted);
  line-height: 1.5;
  flex: 1;
}

.qa-cost { display: flex; align-items: baseline; gap: 2px; }
.qa-cost-val { font-size: 18px; font-weight: 700; color: #a78bfa; }
.qa-cost-label { font-size: 11px; color: var(--color-text-muted); }

.section-title-sm {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.section-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-top: 12px;
}

.work-card {
  padding: 0;
  overflow: hidden;
}

.work-thumb {
  position: relative;
  height: 140px;
  overflow: hidden;
}

.work-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.work-card:hover .work-thumb img { transform: scale(1.05); }

.work-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.work-card:hover .work-overlay { opacity: 1; }

.work-play-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
  border: 2px solid rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.work-play-btn:hover { transform: scale(1.1); }

.work-type-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  font-size: 10px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 9999px;
}

.work-type-badge.image { background: rgba(124, 58, 237, 0.8); color: white; }
.work-type-badge.video { background: rgba(6, 182, 212, 0.8); color: white; }

.work-info { padding: 12px; }

.work-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.work-meta {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: var(--color-text-muted);
  margin-top: 4px;
}

.agency-overview { margin-top: 8px; }
.agency-overview-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }

@media (max-width: 1100px) {
  .stats-grid, .quick-actions-grid, .works-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 600px) {
  .dashboard-page { padding: 20px 16px; }
  .stats-grid, .quick-actions-grid, .works-grid { grid-template-columns: 1fr; }
  .welcome-title { font-size: 22px; }
}
</style>