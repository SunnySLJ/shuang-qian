<template>
  <header class="navbar" :class="{ scrolled: isScrolled }">
    <div class="navbar-inner">
      <!-- Logo -->
      <router-link to="/" class="logo">
        <div class="logo-icon">
          <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <linearGradient id="logoGrad" x1="0" y1="0" x2="40" y2="40" gradientUnits="userSpaceOnUse">
                <stop stop-color="#7c3aed"/>
                <stop offset="0.5" stop-color="#06b6d4"/>
                <stop offset="1" stop-color="#ec4899"/>
              </linearGradient>
            </defs>
            <path d="M20 4L36 12V28L20 36L4 28V12L20 4Z" stroke="url(#logoGrad)" stroke-width="2" fill="none"/>
            <path d="M20 10L28 14.5V23.5L20 28L12 23.5V14.5L20 10Z" fill="url(#logoGrad)" opacity="0.6"/>
            <circle cx="20" cy="19" r="4" fill="white"/>
          </svg>
        </div>
        <div class="logo-text">
          <span class="logo-title">追梦Dream</span>
          <span class="logo-sub">AI Content Platform</span>
        </div>
      </router-link>

      <!-- 导航链接 -->
      <nav class="nav-links" :class="{ open: mobileOpen }">
        <router-link v-for="item in navItems" :key="item.path" :to="item.path" class="nav-item" @click="mobileOpen = false">
          <span class="nav-label">{{ item.label }}</span>
          <span v-if="item.badge" class="nav-badge">{{ item.badge }}</span>
        </router-link>
      </nav>

      <!-- 右侧操作 -->
      <div class="nav-actions">
        <template v-if="isLoggedIn">
          <!-- 积分余额 -->
          <router-link to="/recharge" class="points-badge">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
            </svg>
            <span class="points-value">{{ wallet.balance }}</span>
            <span class="points-label">积分</span>
          </router-link>

          <!-- 用户头像 -->
          <div class="user-avatar" @click="toggleUserMenu">
            <img v-if="user?.avatar" :src="user.avatar" alt="avatar" />
            <div v-else class="avatar-placeholder">
              {{ user?.nickname?.charAt(0) || 'U' }}
            </div>
            <!-- 用户下拉菜单 -->
            <transition name="dropdown">
              <div v-if="userMenuOpen" class="user-dropdown" @click.stop>
                <div class="dropdown-header">
                  <div class="dropdown-name">{{ user?.nickname || '用户' }}</div>
                  <div class="dropdown-phone">{{ user?.phone || '' }}</div>
                </div>
                <div class="dropdown-divider" />
                <router-link to="/dashboard" class="dropdown-item" @click="userMenuOpen = false">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
                  工作台
                </router-link>
                <router-link to="/agency" class="dropdown-item" @click="userMenuOpen = false">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                  代理中心
                </router-link>
                <div class="dropdown-divider" />
                <button class="dropdown-item danger" @click="handleLogout">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                  退出登录
                </button>
              </div>
            </transition>
          </div>
        </template>
        <template v-else>
          <router-link to="/login" class="btn-secondary" style="padding: 8px 20px; font-size: 14px;">登录</router-link>
          <router-link to="/login" class="btn-primary" style="padding: 8px 20px; font-size: 14px;">立即体验</router-link>
        </template>

        <!-- 移动端汉堡菜单 -->
        <button class="hamburger" @click="mobileOpen = !mobileOpen" aria-label="菜单">
          <span :class="{ active: mobileOpen }" />
          <span :class="{ active: mobileOpen }" />
          <span :class="{ active: mobileOpen }" />
        </button>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'

const router = useRouter()
const store = useAppStore()
const wallet = computed(() => store.wallet)
const isLoggedIn = computed(() => !!store.token)

const isScrolled = ref(false)
const mobileOpen = ref(false)
const userMenuOpen = ref(false)

const navItems = [
  { label: 'AI 创作', path: '/ai/image', badge: '' },
  { label: '图片生成', path: '/ai/image' },
  { label: '视频生成', path: '/ai/video' },
  { label: '我的作品', path: '/ai/history' },
  { label: '代理中心', path: '/agency' },
]

function handleScroll() {
  isScrolled.value = window.scrollY > 20
}

function toggleUserMenu() {
  userMenuOpen.value = !userMenuOpen.value
}

function handleLogout() {
  store.logout()
  userMenuOpen.value = false
  router.push('/')
}

function handleClickOutside(e: MouseEvent) {
  const target = e.target as HTMLElement
  if (!target.closest('.user-avatar')) {
    userMenuOpen.value = false
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 0 24px;
  transition: all 0.3s ease;
}

.navbar.scrolled {
  background: rgba(3, 0, 20, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.navbar-inner {
  max-width: 1280px;
  margin: 0 auto;
  height: 72px;
  display: flex;
  align-items: center;
  gap: 32px;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  flex-shrink: 0;
}

.logo-icon {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
}

.logo-icon svg {
  width: 100%;
  height: 100%;
}

.logo-text {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.logo-title {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 700;
  background: var(--gradient-dream);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.05em;
}

.logo-sub {
  font-size: 10px;
  color: var(--color-text-muted);
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

/* 导航 */
.nav-links {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  color: var(--color-text-secondary);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.2s ease;
  position: relative;
}

.nav-item:hover {
  color: var(--color-text-primary);
  background: rgba(255, 255, 255, 0.05);
}

.nav-item.router-link-active {
  color: var(--color-text-primary);
  background: rgba(124, 58, 237, 0.1);
}

.nav-item.router-link-active::after {
  content: '';
  position: absolute;
  bottom: 2px;
  left: 50%;
  transform: translateX(-50%);
  width: 16px;
  height: 2px;
  background: var(--gradient-dream);
  border-radius: 1px;
}

.nav-badge {
  background: rgba(239, 68, 68, 0.9);
  color: white;
  font-size: 10px;
  font-weight: 700;
  padding: 1px 5px;
  border-radius: 9999px;
}

/* 积分徽章 */
.points-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: rgba(124, 58, 237, 0.15);
  border: 1px solid rgba(124, 58, 237, 0.3);
  border-radius: 9999px;
  color: #a78bfa;
  text-decoration: none;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.points-badge:hover {
  background: rgba(124, 58, 237, 0.25);
  border-color: rgba(124, 58, 237, 0.5);
  transform: translateY(-1px);
}

.points-value {
  color: #c4b5fd;
  font-weight: 700;
}

.points-label {
  font-size: 11px;
  opacity: 0.7;
}

/* 用户头像 */
.user-avatar {
  position: relative;
  cursor: pointer;
}

.user-avatar img,
.avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(124, 58, 237, 0.4);
  transition: border-color 0.2s ease;
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-dream);
  color: white;
  font-weight: 700;
  font-size: 16px;
}

.user-avatar:hover img,
.user-avatar:hover .avatar-placeholder {
  border-color: rgba(124, 58, 237, 0.8);
}

/* 下拉菜单 */
.user-dropdown {
  position: absolute;
  top: calc(100% + 12px);
  right: 0;
  width: 200px;
  background: rgba(15, 10, 31, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.6), 0 0 0 1px rgba(124, 58, 237, 0.1);
}

.dropdown-header {
  padding: 8px 12px 12px;
}

.dropdown-name {
  font-weight: 600;
  font-size: 14px;
  color: var(--color-text-primary);
}

.dropdown-phone {
  font-size: 12px;
  color: var(--color-text-muted);
  margin-top: 2px;
}

.dropdown-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.06);
  margin: 4px 0;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 12px;
  border-radius: 8px;
  color: var(--color-text-secondary);
  text-decoration: none;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.15s ease;
  width: 100%;
  border: none;
  background: transparent;
  cursor: pointer;
  text-align: left;
}

.dropdown-item:hover {
  background: rgba(255, 255, 255, 0.05);
  color: var(--color-text-primary);
}

.dropdown-item.danger:hover {
  background: rgba(239, 68, 68, 0.1);
  color: #f87171;
}

/* 汉堡菜单 */
.hamburger {
  display: none;
  flex-direction: column;
  gap: 5px;
  padding: 8px;
  background: transparent;
  border: none;
  cursor: pointer;
}

.hamburger span {
  display: block;
  width: 22px;
  height: 2px;
  background: var(--color-text-secondary);
  border-radius: 1px;
  transition: all 0.3s ease;
}

.hamburger span.active:nth-child(1) {
  transform: translateY(7px) rotate(45deg);
  background: var(--color-text-primary);
}
.hamburger span.active:nth-child(2) {
  opacity: 0;
}
.hamburger span.active:nth-child(3) {
  transform: translateY(-7px) rotate(-45deg);
  background: var(--color-text-primary);
}

/* 动画 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.95);
}

/* 响应式 */
@media (max-width: 768px) {
  .navbar {
    padding: 0 16px;
  }

  .nav-links {
    display: none;
    position: fixed;
    top: 72px;
    left: 0;
    right: 0;
    background: rgba(3, 0, 20, 0.98);
    backdrop-filter: blur(20px);
    flex-direction: column;
    padding: 16px;
    gap: 4px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  }

  .nav-links.open {
    display: flex;
  }

  .nav-item {
    width: 100%;
    padding: 12px 16px;
  }

  .hamburger {
    display: flex;
  }

  .points-label,
  .logo-sub {
    display: none;
  }
}
</style>
