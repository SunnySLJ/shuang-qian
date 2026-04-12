<template>
  <div class="app-layout">
    <!-- 粒子宇宙背景 -->
    <ParticleBackground />

    <!-- ========== 左侧星空导航 ========== -->
    <aside class="sidebar" :class="{ 'sidebar-collapsed': collapsed }">
      <!-- Logo（窄栏居中） -->
      <div class="sidebar-logo">
        <div class="logo-orb">
          <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <linearGradient id="sidebarLogoGrad" x1="0" y1="0" x2="40" y2="40" gradientUnits="userSpaceOnUse">
                <stop stop-color="#7c3aed"/>
                <stop offset="0.5" stop-color="#06b6d4"/>
                <stop offset="1" stop-color="#ec4899"/>
              </linearGradient>
            </defs>
            <circle cx="20" cy="20" r="17" stroke="url(#sidebarLogoGrad)" stroke-width="1" opacity="0.3"/>
            <circle cx="20" cy="20" r="12" stroke="url(#sidebarLogoGrad)" stroke-width="1" stroke-dasharray="3 3" opacity="0.5"/>
            <path d="M20 6L32 12V28L20 34L8 28V12L20 6Z" stroke="url(#sidebarLogoGrad)" stroke-width="1.5" fill="none"/>
            <path d="M20 11L28 15.5V24.5L20 29L12 24.5V15.5L20 11Z" fill="url(#sidebarLogoGrad)" opacity="0.4"/>
            <circle cx="20" cy="20" r="4" fill="white" opacity="0.9"/>
            <circle cx="20" cy="20" r="2" fill="#7c3aed"/>
            <!-- 脉冲星 -->
            <circle cx="20" cy="6" r="1.5" fill="#a78bfa" opacity="0.7"/>
            <circle cx="34" cy="20" r="1" fill="#06b6d4" opacity="0.6"/>
            <circle cx="20" cy="34" r="1" fill="#ec4899" opacity="0.6"/>
            <circle cx="6" cy="20" r="1" fill="#22d3ee" opacity="0.5"/>
          </svg>
        </div>
        <span v-if="!collapsed" class="logo-rail-title">追梦</span>
        <button class="collapse-btn" type="button" @click="collapsed = !collapsed" :title="collapsed ? '展开文字' : '仅图标'">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline v-if="!collapsed" points="15 18 9 12 15 6"/>
            <polyline v-else points="9 18 15 12 9 6"/>
          </svg>
        </button>
      </div>

      <!-- 用户信息（窄栏：头像 + 12px 昵称） -->
      <div class="sidebar-user" v-if="store.user || store.token">
        <div class="user-orb" @click="toggleUserMenu">
          <div class="user-avatar">
            {{ store.user?.nickname?.charAt(0) || 'U' }}
          </div>
          <div v-if="!collapsed" class="user-rail-row">
            <span class="user-name">{{ store.user?.nickname || '追梦用户' }}</span>
            <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="user-arrow" :class="{ open: userMenuOpen }">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
          </div>
        </div>
      </div>

      <!-- 下拉菜单 - 放在侧边栏外面避免被 overflow: hidden 裁剪 -->
      <transition name="dropdown">
        <div v-if="userMenuOpen" class="user-dropdown" @click.stop>
          <div class="dropdown-header">
            <div class="dropdown-name">{{ store.user?.nickname || '追梦用户' }}</div>
            <div class="dropdown-phone">{{ store.user?.phone || '' }}</div>
          </div>
          <div class="dropdown-divider" />
          <router-link to="/dashboard" class="dropdown-item" @click="userMenuOpen = false">
            <span v-html="icons.home" class="dropdown-icon" />
            工作台
          </router-link>
          <router-link to="/agency" class="dropdown-item" @click="userMenuOpen = false">
            <span v-html="icons.users" class="dropdown-icon" />
            代理中心
          </router-link>
          <router-link to="/recharge" class="dropdown-item" @click="userMenuOpen = false">
            <span v-html="icons.recharge" class="dropdown-icon" />
            积分充值
          </router-link>
          <div class="dropdown-divider" />
          <button class="dropdown-item danger" @click="handleLogout">
            <span v-html="icons.logout" class="dropdown-icon" />
            退出登录
          </button>
        </div>
      </transition>

      <!-- 导航：参考图 — 图标上、12px 标题下、项间距大 -->
      <nav class="sidebar-nav">
        <div class="nav-group">
          <router-link
            v-for="item in mainNav"
            :key="item.path"
            :to="item.path"
            class="nav-item rail-nav-item"
            :class="{ active: isActive(item.path) }"
            :title="collapsed ? item.shortLabel : ''"
          >
            <div class="rail-icon-slot" :class="{ active: isActive(item.path) }">
              <span class="rail-icon" v-html="item.icon" />
            </div>
            <span v-if="!collapsed" class="rail-label">{{ item.shortLabel }}</span>
            <span
              v-if="!collapsed && item.subBadge"
              class="rail-sub"
              :class="{ gradient: item.subGradient }"
            >{{ item.subBadge }}</span>
          </router-link>
        </div>
      </nav>

      <!-- 底部：获取积分（与参考图一致） -->
      <div class="sidebar-footer">
        <router-link
          v-if="store.user || store.token"
          to="/recharge"
          class="points-rail-card"
        >
          <div class="points-rail-top">
            <span class="points-rail-star" aria-hidden="true">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="white" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2l2.4 7.4h7.6l-6 4.6 2.3 7.4-6.3-4.6-6.3 4.6 2.3-7.4-6-4.6h7.6L12 2z"/>
              </svg>
            </span>
            <span class="points-rail-num">{{ store.wallet?.balance ?? 0 }}</span>
          </div>
          <span v-if="!collapsed" class="points-rail-cta">获取积分</span>
        </router-link>
        <div v-if="!collapsed" class="footer-version">
          <span v-html="icons.planet" />
          v1.0
        </div>
      </div>
    </aside>

    <!-- ========== 右侧主内容区 ========== -->
    <main class="main-content">
      <!-- 顶部工具栏 -->
      <header class="topbar">
        <div class="topbar-left">
          <!-- 面包屑 -->
          <div class="breadcrumb">
            <span class="breadcrumb-root">{{ currentPageInfo?.group || '创作中心' }}</span>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
            <span class="breadcrumb-current">{{ currentPageInfo?.label || '工作台' }}</span>
          </div>
        </div>
        <div class="topbar-right">
          <!-- 日期 -->
          <div class="topbar-date">{{ currentDateStr }}</div>
          <!-- 快捷操作 -->
          <router-link to="/ai/image" class="topbar-action-btn">
            <span v-html="icons.rocket" />
            快速创作
          </router-link>
        </div>
      </header>

      <!-- 页面内容 -->
      <div class="page-content">
        <slot />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ParticleBackground from '@/components/common/ParticleBackground.vue'
import { useAppStore } from '@/stores/app'
import { cosmicIcons } from '@/utils/cosmicIcons'
import { logout } from '@/api/auth'

const store = useAppStore()
const route = useRoute()
const router = useRouter()
const collapsed = ref(false)
const userMenuOpen = ref(false)

const icons = cosmicIcons

const currentDateStr = computed(() => {
  const d = new Date()
  const weeks = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${d.getMonth() + 1}月${d.getDate()}日 ${weeks[d.getDay()]}`
})

const mainNav = [
  {
    shortLabel: '工作台',
    path: '/dashboard',
    icon: icons.home,
    group: '首页',
    subBadge: '',
    subGradient: false,
  },
  {
    shortLabel: '灵感',
    path: '/inspiration',
    icon: icons.sparkle,
    group: '创作',
    subBadge: '模板',
    subGradient: true,
  },
  {
    shortLabel: '爆款',
    path: '/ai/video/analyze',
    icon: icons.analyze,
    group: '创作',
    subBadge: '拆解',
    subGradient: true,
  },
  {
    shortLabel: '做图',
    path: '/ai/image',
    icon: icons.image,
    group: 'AI 创作',
    subBadge: '2积分/次',
    subGradient: false,
  },
  {
    shortLabel: '视频',
    path: '/ai/video',
    icon: icons.video,
    group: 'AI 创作',
    subBadge: 'AI短片',
    subGradient: true,
  },
  {
    shortLabel: '作品',
    path: '/ai/history',
    icon: icons.history,
    group: 'AI 创作',
    subBadge: '',
    subGradient: false,
  },
  {
    shortLabel: '充值',
    path: '/recharge',
    icon: icons.recharge,
    group: '财务',
    subBadge: '',
    subGradient: false,
  },
  {
    shortLabel: '代理',
    path: '/agency',
    icon: icons.users,
    group: '代理',
    subBadge: '',
    subGradient: false,
  },
]

const pageInfoMap: Record<string, { label: string; group: string }> = {
  '/dashboard': { label: '工作台', group: '首页' },
  '/inspiration': { label: '灵感广场', group: '创作' },
  '/ai/video/analyze': { label: '一键拆解视频', group: '创作' },
  '/ai/image': { label: 'AI 做图', group: 'AI 创作' },
  '/ai/video': { label: 'AI 视频', group: 'AI 创作' },
  '/ai/history': { label: '我的作品', group: 'AI 创作' },
  '/recharge': { label: '积分充值', group: '财务' },
  '/agency': { label: '代理中心', group: '代理' },
}

const currentPageInfo = computed(() => pageInfoMap[route.path] || { label: '页面', group: '追梦Dream' })

function isActive(path: string): boolean {
  if (route.path === path) return true
  // 「AI 视频」与「爆款拆解」路径前缀相同，需区分
  if (path === '/ai/video') {
    return route.path.startsWith('/ai/video/') && !route.path.startsWith('/ai/video/analyze')
  }
  return route.path.startsWith(path + '/')
}

function toggleUserMenu() {
  userMenuOpen.value = !userMenuOpen.value
  console.log('User menu toggled:', userMenuOpen.value)
}

function handleLogout() {
  console.log('Logging out...')
  logout()
    .catch((e) => {
      console.error('Logout API error:', e)
    })
    .finally(() => {
      store.logout()
      userMenuOpen.value = false
      console.log('Logged out, redirecting to login')
      router.push('/login')
    })
}

function handleClickOutside(e: MouseEvent) {
  const target = e.target as HTMLElement
  if (!target.closest('.sidebar-user')) {
    userMenuOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
  background: var(--color-bg-base);
  position: relative;
}

/* ========== 侧边栏（参考图：窄轨 ~92px，#121214） ========== */
.sidebar {
  width: 92px;
  min-height: 100vh;
  background: #121214;
  border-right: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  transition: width 0.28s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.sidebar-collapsed {
  width: 64px;
}

/* Logo */
.sidebar-logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 14px 8px 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  position: relative;
}

.logo-orb {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
  animation: orb-pulse 4s ease-in-out infinite;
}

@keyframes orb-pulse {
  0%, 100% { filter: drop-shadow(0 0 8px rgba(124, 58, 237, 0.4)); }
  50% { filter: drop-shadow(0 0 16px rgba(124, 58, 237, 0.7)); }
}

.logo-rail-title {
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.88);
  letter-spacing: 0.02em;
  line-height: 1;
}

.collapse-btn {
  position: absolute;
  right: 4px;
  top: 8px;
  width: 20px;
  height: 20px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 0;
}

.collapse-btn:hover {
  background: rgba(124, 58, 237, 0.2);
  border-color: rgba(124, 58, 237, 0.35);
  color: #c4b5fd;
}

/* 用户区（窄栏） */
.sidebar-user {
  padding: 8px 6px 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  position: relative;
}

.user-orb {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 6px 4px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.user-orb:hover {
  background: rgba(124, 58, 237, 0.2);
}

.user-orb:active {
  transform: scale(0.95);
}

/* 提示用户可以点击 */
.user-orb::after {
  content: '点击菜单';
  position: absolute;
  top: -24px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10px;
  color: rgba(255, 255, 255, 0.6);
  background: rgba(0, 0, 0, 0.8);
  padding: 2px 6px;
  border-radius: 4px;
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.2s ease;
}

.user-orb:hover::after {
  opacity: 1;
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--gradient-dream);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  color: white;
  flex-shrink: 0;
  border: 1px solid rgba(124, 58, 237, 0.45);
}

.user-rail-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2px;
  max-width: 100%;
  padding: 0 2px;
}

.user-name {
  font-size: 12px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.75);
  line-height: 1.2;
  max-width: 68px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-align: center;
}

.user-arrow {
  color: rgba(255, 255, 255, 0.35);
  flex-shrink: 0;
  transition: transform 0.2s ease;
}

.user-arrow.open {
  transform: rotate(180deg);
}

/* 下拉菜单（使用 fixed 定位，避免被 overflow: hidden 裁剪） */
.user-dropdown {
  position: fixed;
  left: 98px;
  top: 76px;
  width: 180px;
  background: rgba(15, 10, 31, 0.98);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 12px;
  padding: 6px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.6), 0 0 0 1px rgba(124, 58, 237, 0.2);
  z-index: 1000;
}

/* 侧边栏收起时的位置 */
.sidebar-collapsed ~ .user-dropdown {
  left: 70px;
}

.dropdown-overlay {
  position: fixed;
  inset: 0;
  z-index: 999;
}

.dropdown-header {
  padding: 12px 12px 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.dropdown-name {
  font-weight: 600;
  font-size: 14px;
  color: var(--color-text-primary);
}

.dropdown-phone {
  font-size: 11px;
  color: var(--color-text-muted);
  margin-top: 2px;
}

.dropdown-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.08);
  margin: 8px 0;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.65);
  transition: all 0.15s ease;
  width: 100%;
  border: none;
  background: transparent;
  cursor: pointer;
  font-family: inherit;
  text-decoration: none;
}

.dropdown-item:hover {
  background: rgba(124, 58, 237, 0.15);
  color: rgba(255, 255, 255, 0.95);
}

/* 退出登录按钮 - 醒目红色 */
.dropdown-item.danger {
  margin-top: 4px;
  border-top: 1px solid rgba(239, 68, 68, 0.3);
  padding-top: 10px;
  color: #f87171;
}

.dropdown-item.danger:hover {
  background: rgba(239, 68, 68, 0.2);
  color: #fca5a5;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.2);
}

.dropdown-icon {
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: currentColor;
}

.dropdown-icon :deep(svg) {
  width: 100%;
  height: 100%;
}

/* 导航轨 */
.sidebar-nav {
  flex: 1;
  padding: 10px 6px 8px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  overflow-x: hidden;
}

.nav-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

/* 参考图：图标上、12px 文案下、项间距约 32–40px 视觉 → padding 叠加 */
.rail-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  max-width: 76px;
  margin: 0 auto;
  padding: 10px 4px 14px;
  border-radius: 14px;
  text-decoration: none;
  color: rgba(255, 255, 255, 0.55);
  transition: background 0.2s ease, color 0.2s ease;
}

.rail-nav-item:hover {
  background: rgba(255, 255, 255, 0.04);
  color: rgba(255, 255, 255, 0.9);
}

/* 选中：整块圆角底 + 微紫蓝（非渐变铺满图标盒） */
.rail-nav-item.active {
  background: rgba(88, 76, 140, 0.35);
  color: #fff;
}

.rail-icon-slot {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 6px;
}

.rail-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.rail-icon :deep(svg) {
  width: 26px;
  height: 26px;
  opacity: 0.85;
  transition: opacity 0.2s ease, filter 0.2s ease;
}

.rail-nav-item:hover .rail-icon :deep(svg) {
  opacity: 1;
}

.rail-nav-item.active .rail-icon :deep(svg) {
  opacity: 1;
  filter: brightness(1.15) drop-shadow(0 0 6px rgba(255, 255, 255, 0.25));
}

.rail-label {
  font-size: 12px;
  font-weight: 500;
  line-height: 1.2;
  text-align: center;
  color: rgba(255, 255, 255, 0.82);
  letter-spacing: 0.01em;
}

.rail-nav-item:not(.active) .rail-label {
  color: rgba(255, 255, 255, 0.72);
}

.rail-sub {
  margin-top: 4px;
  font-size: 10px;
  font-weight: 600;
  line-height: 1.2;
  text-align: center;
  padding: 2px 6px;
  border-radius: 9999px;
  max-width: 100%;
  color: rgba(251, 146, 60, 0.95);
  background: rgba(0, 0, 0, 0.35);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.rail-sub.gradient {
  font-style: italic;
  font-weight: 700;
  letter-spacing: 0.02em;
  border: none;
  background: linear-gradient(90deg, #34d399, #22d3ee);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  padding: 0 2px;
}

/* 底部积分卡（参考图） */
.sidebar-footer {
  padding: 10px 8px 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.points-rail-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 76px;
  margin: 0 auto;
  padding: 10px 6px 12px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.25);
  text-decoration: none;
  transition: border-color 0.2s ease, background 0.2s ease;
}

.points-rail-card:hover {
  border-color: rgba(236, 72, 153, 0.45);
  background: rgba(236, 72, 153, 0.08);
}

.points-rail-top {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.points-rail-star {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ec4899, #f472b6);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 12px rgba(236, 72, 153, 0.45);
}

.points-rail-star svg {
  display: block;
}

.points-rail-num {
  font-size: 17px;
  font-weight: 700;
  color: #fff;
  letter-spacing: -0.02em;
  line-height: 1;
}

.points-rail-cta {
  margin-top: 8px;
  font-size: 12px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.88);
  line-height: 1;
}

.sidebar-collapsed .points-rail-card {
  padding: 8px 4px;
  max-width: 52px;
}

.sidebar-collapsed .points-rail-cta {
  display: none;
}

.footer-version {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 10px;
  color: rgba(255, 255, 255, 0.35);
}

.footer-version :deep(svg) {
  width: 10px;
  height: 10px;
  flex-shrink: 0;
  opacity: 0.6;
}

/* ========== 主内容区 ========== */
.main-content {
  flex: 1;
  margin-left: 92px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  transition: margin-left 0.28s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar-collapsed ~ .main-content {
  margin-left: 64px;
}

/* 顶部工具栏 */
.topbar {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: rgba(3, 0, 20, 0.6);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
  position: sticky;
  top: 0;
  z-index: 50;
}

.topbar-left, .topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.breadcrumb-root {
  color: var(--color-text-muted);
}

.breadcrumb svg {
  color: var(--color-text-muted);
  opacity: 0.5;
}

.breadcrumb-current {
  color: var(--color-text-primary);
  font-weight: 600;
}

.topbar-date {
  font-size: 12px;
  color: var(--color-text-muted);
}

.topbar-action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 7px 16px;
  background: var(--gradient-dream);
  color: white;
  font-size: 13px;
  font-weight: 600;
  border-radius: 8px;
  text-decoration: none;
  transition: all 0.2s ease;
  box-shadow: 0 2px 12px rgba(124, 58, 237, 0.3);
}

.topbar-action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 20px rgba(124, 58, 237, 0.5);
}

.topbar-action-btn :deep(svg) {
  width: 14px;
  height: 14px;
}

/* 页面内容 */
.page-content {
  flex: 1;
  padding: 0;
}

/* ========== 动画 ========== */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.dropdown-enter-active, .dropdown-leave-active {
  transition: all 0.2s ease;
}
.dropdown-enter-from, .dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.97);
}

.page-enter-active, .page-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.page-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.page-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .sidebar {
    width: 64px;
  }

  .sidebar-collapsed {
    width: 56px;
  }

  .main-content {
    margin-left: 64px;
  }

  .sidebar-collapsed ~ .main-content {
    margin-left: 56px;
  }

  .logo-rail-title,
  .rail-label,
  .rail-sub,
  .user-rail-row,
  .points-rail-cta,
  .footer-version {
    display: none !important;
  }

  .collapse-btn {
    display: none;
  }
}
</style>
