<template>
  <div class="home-page">
    <!-- 增强星空背景 -->
    <div class="starry-bg">
      <div class="nebula nebula-1" />
      <div class="nebula nebula-2" />
      <div class="nebula nebula-3" />
      <canvas ref="starsCanvas" class="stars-canvas" />
    </div>

    <!-- 导航栏 -->
    <header class="hero-nav">
      <div class="hero-nav-inner">
        <!-- Logo -->
        <div class="hero-logo">
          <div class="hero-logo-orb">
            <svg viewBox="0 0 36 36" fill="none">
              <defs>
                <linearGradient id="heroLogoG" x1="0" y1="0" x2="36" y2="36" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#7c3aed"/>
                  <stop offset="0.5" stop-color="#06b6d4"/>
                  <stop offset="1" stop-color="#ec4899"/>
                </linearGradient>
              </defs>
              <circle cx="18" cy="18" r="16" stroke="url(#heroLogoG)" stroke-width="1" opacity="0.3"/>
              <circle cx="18" cy="18" r="11" stroke="url(#heroLogoG)" stroke-width="0.8" stroke-dasharray="2 3" opacity="0.5"/>
              <path d="M18 5L29 11V25L18 31L7 25V11L18 5Z" stroke="url(#heroLogoG)" stroke-width="1.5" fill="none"/>
              <path d="M18 10L25.5 14V22L18 26L10.5 22V14L18 10Z" fill="url(#heroLogoG)" opacity="0.3"/>
              <circle cx="18" cy="18" r="4" fill="white" opacity="0.9"/>
              <circle cx="18" cy="18" r="2" fill="#7c3aed"/>
              <circle cx="18" cy="5" r="1.5" fill="#a78bfa" opacity="0.7"/>
              <circle cx="31" cy="18" r="1" fill="#06b6d4" opacity="0.6"/>
              <circle cx="5" cy="18" r="1" fill="#ec4899" opacity="0.5"/>
            </svg>
          </div>
          <div class="hero-logo-text">
            <span class="hero-logo-title">追梦Dream</span>
            <span class="hero-logo-sub">AI Content Platform</span>
          </div>
        </div>

        <!-- 导航链接 -->
        <nav class="hero-nav-links">
          <a href="#features" class="hero-nav-link">功能介绍</a>
          <a href="#pricing" class="hero-nav-link">价格方案</a>
          <a href="#agency" class="hero-nav-link">代理招募</a>
        </nav>

        <!-- 按钮组 -->
        <div class="hero-nav-actions">
          <template v-if="isLoggedIn">
            <!-- 用户菜单 -->
            <div class="user-menu-wrapper" :class="{ open: userMenuOpen }">
              <div class="user-avatar-btn" @click="toggleUserMenu">
                <div class="avatar-initials">
                  {{ store.user?.nickname?.charAt(0) || store.user?.phone?.charAt(0) || 'U' }}
                </div>
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="user-arrow" :class="{ open: userMenuOpen }">
                  <polyline points="6 9 12 15 18 9"/>
                </svg>
              </div>
              <!-- 下拉菜单 -->
              <transition name="dropdown">
                <div v-if="userMenuOpen" class="user-dropdown" @click.stop>
                  <div class="dropdown-header">
                    <div class="dropdown-name">{{ store.user?.nickname || '追梦用户' }}</div>
                    <div class="dropdown-phone">{{ store.user?.phone || '' }}</div>
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
                  <router-link to="/recharge" class="dropdown-item" @click="userMenuOpen = false">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                    积分充值
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
            <router-link to="/login" class="btn-outline">登录</router-link>
            <router-link to="/login" class="btn-primary-sm">免费试用</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- ========== Hero 区域 ========== -->
    <section class="hero-section">
      <div class="hero-content">
        <!-- 标签 -->
        <div class="hero-badge">
          <span class="hero-badge-dot" />
          全新 AI 创作时代
        </div>

        <!-- 主标题 -->
        <h1 class="hero-title">
          <span class="hero-title-line">用 AI 为你的想象</span>
          <span class="hero-title-line gradient-text">插上创作的翅膀</span>
        </h1>

        <!-- 副标题 -->
        <p class="hero-subtitle">
          追梦Dream 是新一代 AI 内容创作平台，支持一键生成震撼图片与视频。<br/>
          强大的 AI 模型，简单易用的操作，让创意触手可及。
        </p>

        <!-- CTA 按钮 -->
        <div class="hero-cta">
          <router-link :to="primaryActionTo" class="btn-hero-primary">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/></svg>
            {{ primaryActionText }}
          </router-link>
          <router-link to="/ai/image" class="btn-hero-secondary">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polygon points="10 8 16 12 10 16 10 8"/></svg>
            查看演示作品
          </router-link>
        </div>

        <!-- 数据展示 -->
        <div class="hero-stats">
          <div class="hero-stat">
            <span class="hero-stat-val">50万+</span>
            <span class="hero-stat-label">创作用户</span>
          </div>
          <div class="hero-stat-divider" />
          <div class="hero-stat">
            <span class="hero-stat-val">1000万+</span>
            <span class="hero-stat-label">生成作品</span>
          </div>
          <div class="hero-stat-divider" />
          <div class="hero-stat">
            <span class="hero-stat-val">99.9%</span>
            <span class="hero-stat-label">服务可用</span>
          </div>
        </div>
      </div>

      <!-- Hero 视觉展示 -->
      <div class="hero-visual">
        <div class="hero-orb-container">
          <div class="hero-center-orb">
            <svg viewBox="0 0 200 200" fill="none">
              <defs>
                <radialGradient id="orbGrad" cx="50%" cy="50%" r="50%">
                  <stop offset="0%" stop-color="#8b5cf6" stop-opacity="0.8"/>
                  <stop offset="50%" stop-color="#7c3aed" stop-opacity="0.4"/>
                  <stop offset="100%" stop-color="#06b6d4" stop-opacity="0"/>
                </radialGradient>
                <radialGradient id="orbGrad2" cx="50%" cy="50%" r="50%">
                  <stop offset="0%" stop-color="#06b6d4" stop-opacity="0.6"/>
                  <stop offset="100%" stop-color="#06b6d4" stop-opacity="0"/>
                </radialGradient>
              </defs>
              <!-- 外环 -->
              <circle cx="100" cy="100" r="95" stroke="url(#orbGrad)" stroke-width="1" opacity="0.2" stroke-dasharray="4 8"/>
              <circle cx="100" cy="100" r="80" stroke="url(#orbGrad)" stroke-width="1.5" opacity="0.3" stroke-dasharray="3 6"/>
              <circle cx="100" cy="100" r="65" stroke="url(#orbGrad2)" stroke-width="2" opacity="0.5"/>
              <!-- 星云体 -->
              <ellipse cx="100" cy="100" rx="55" ry="45" fill="url(#orbGrad)" opacity="0.3"/>
              <!-- 中心 -->
              <circle cx="100" cy="100" r="20" fill="#7c3aed" opacity="0.6"/>
              <circle cx="100" cy="100" r="12" fill="white" opacity="0.8"/>
              <!-- 脉冲点 -->
              <circle cx="100" cy="5" r="3" fill="#a78bfa" opacity="0.8"/>
              <circle cx="195" cy="100" r="2.5" fill="#06b6d4" opacity="0.7"/>
              <circle cx="5" cy="100" r="2.5" fill="#ec4899" opacity="0.7"/>
              <circle cx="100" cy="195" r="3" fill="#22d3ee" opacity="0.6"/>
              <circle cx="155" cy="35" r="2" fill="#f59e0b" opacity="0.6"/>
              <circle cx="45" cy="155" r="2" fill="#f472b6" opacity="0.6"/>
              <!-- 射线 -->
              <line x1="100" y1="20" x2="100" y2="35" stroke="#a78bfa" stroke-width="1" opacity="0.5"/>
              <line x1="180" y1="100" x2="165" y2="100" stroke="#06b6d4" stroke-width="1" opacity="0.5"/>
              <line x1="20" y1="100" x2="35" y2="100" stroke="#ec4899" stroke-width="1" opacity="0.5"/>
            </svg>
          </div>
          <!-- 浮动卡片 -->
          <div class="float-card float-card-1 glass-card">
            <div class="fc-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#a78bfa" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="M21 15l-5-5L5 21"/></svg>
            </div>
            <div class="fc-text">图片生成完成</div>
          </div>
          <div class="float-card float-card-2 glass-card">
            <div class="fc-icon" style="color:#06b6d4">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#06b6d4" stroke-width="2"><polygon points="23 7 16 12 23 17 23 7"/><rect x="1" y="5" width="15" height="14" rx="2"/></svg>
            </div>
            <div class="fc-text">视频生成完成</div>
          </div>
          <div class="float-card float-card-3 glass-card">
            <div class="fc-icon" style="color:#f59e0b">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#f59e0b" stroke-width="2"><path d="M12 2L15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
            </div>
            <div class="fc-text">+100 积分到账</div>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== 功能介绍 ========== -->
    <section id="features" class="features-section">
      <div class="section-container">
        <div class="section-header">
          <div class="section-tag">核心能力</div>
          <h2 class="section-title">AI 赋能，想象即现实</h2>
          <p class="section-subtitle">多种强大 AI 模型，帮你将脑海中的画面转化为震撼视觉作品</p>
        </div>

        <div class="features-grid">
          <div
            v-for="(feature, index) in features"
            :key="feature.title"
            class="feature-card glass-card"
            :style="{ animationDelay: `${index * 100}ms` }"
          >
            <div class="feature-icon" :style="{ background: feature.gradient }">
              <span v-html="feature.icon" />
            </div>
            <div class="feature-content">
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-desc">{{ feature.desc }}</p>
            </div>
            <div class="feature-tags">
              <span v-for="tag in feature.tags" :key="tag" class="feature-tag">{{ tag }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== AI 展示 ========== -->
    <section class="showcase-section">
      <div class="section-container">
        <div class="showcase-split">
          <div class="showcase-text">
            <div class="section-tag">AI 图片</div>
            <h2 class="section-title">一键生成惊艳图片</h2>
            <p class="section-subtitle">
              输入描述词，AI 在数秒内为你生成精美图片。支持赛博朋克、国风水墨、3D渲染等数十种风格。
            </p>
            <ul class="feature-list">
              <li v-for="item in aiImageFeatures" :key="item">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#a78bfa" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>
                {{ item }}
              </li>
            </ul>
            <router-link :to="imageCtaTo" class="btn-hero-primary" style="margin-top: 32px; display: inline-flex;">
              {{ imageCtaText }} →
            </router-link>
          </div>
          <div class="showcase-images">
            <div class="showcase-img-grid">
              <img class="showcase-img si-1" src="https://images.unsplash.com/photo-1480714378408-67cf0d13bc1b?w=600&q=80" alt="城市" />
              <img class="showcase-img si-2" src="https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80" alt="抽象" />
              <img class="showcase-img si-3" src="https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?w=600&q=80" alt="星空" />
              <img class="showcase-img si-4" src="https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&q=80" alt="山水" />
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== 价格方案 ========== -->
    <section id="pricing" class="pricing-section">
      <div class="section-container">
        <div class="section-header">
          <div class="section-tag">灵活定价</div>
          <h2 class="section-title">按需付费，绝不浪费</h2>
          <p class="section-subtitle">积分制消费，图片仅需 2 积分，视频仅需 10 积分</p>
        </div>

        <div class="pricing-grid">
          <div v-for="pkg in pricingPackages" :key="pkg.name" class="pricing-card glass-card" :class="{ popular: pkg.popular }">
            <div v-if="pkg.popular" class="pricing-badge">最受欢迎</div>
            <div class="pricing-name">{{ pkg.name }}</div>
            <div class="pricing-points">
              <span class="pp-val">{{ pkg.points }}</span>
              <span class="pp-unit">积分</span>
            </div>
            <div class="pricing-price">¥{{ pkg.price }}</div>
            <div class="pricing-per">约 {{ pkg.per }} / 积分</div>
            <ul class="pricing-features">
              <li v-for="f in pkg.features" :key="f">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
                {{ f }}
              </li>
            </ul>
            <router-link to="/recharge" class="pricing-btn" :class="{ primary: pkg.popular }">
              {{ pkg.popular ? '立即购买' : '选择套餐' }}
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== 代理招募 ========== -->
    <section id="agency" class="agency-section">
      <div class="section-container">
        <div class="agency-content">
          <div class="agency-bg-text">AGENCY</div>
          <div class="section-header" style="text-align: center;">
            <div class="section-tag">代理分销</div>
            <h2 class="section-title">成为追梦 Dream 代理</h2>
            <p class="section-subtitle">分享创作平台，赚取丰厚佣金，轻松实现副业增收</p>
          </div>
          <div class="agency-features">
            <div v-for="af in agencyFeatures" :key="af.title" class="agency-feature glass-card">
              <div class="af-icon" v-html="af.icon" />
              <div class="af-title">{{ af.title }}</div>
              <div class="af-desc">{{ af.desc }}</div>
            </div>
          </div>
          <div class="agency-cta">
            <router-link :to="agencyCtaTo" class="btn-hero-primary">
              {{ agencyCtaText }} →
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== 底部 CTA ========== -->
    <section class="cta-section">
      <div class="cta-glow" />
      <div class="cta-content">
        <h2 class="cta-title">准备好开启创作之旅了吗？</h2>
        <p class="cta-sub">注册即送体验积分，立即感受 AI 创作的魅力</p>
        <router-link :to="primaryActionTo" class="btn-hero-primary cta-btn">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/></svg>
          {{ footerCtaText }}
        </router-link>
      </div>
    </section>

    <!-- Footer -->
    <footer class="home-footer">
      <div class="footer-inner">
        <div class="footer-logo">
          <span class="gradient-text" style="font-weight: 700;">追梦Dream</span>
          <span class="footer-copy">© 2024 追梦Dream. All rights reserved.</span>
        </div>
        <div class="footer-links">
          <a href="#">关于我们</a>
          <a href="#">服务条款</a>
          <a href="#">隐私政策</a>
          <a href="#">联系我们</a>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { cosmicIcons } from '@/utils/cosmicIcons'
import { logout } from '@/api/auth'

const starsCanvas = ref<HTMLCanvasElement | null>(null)
let animationId = 0
const store = useAppStore()
const router = useRouter()
const userMenuOpen = ref(false)

const isLoggedIn = computed(() => !!store.token)
const displayName = computed(() => store.user?.nickname || store.user?.phone || '已登录')
const primaryActionTo = computed(() => (isLoggedIn.value ? '/ai/image' : '/login'))
const primaryActionText = computed(() => (isLoggedIn.value ? '进入创作台' : '立即开始创作'))
const imageCtaTo = computed(() => (isLoggedIn.value ? '/ai/image' : '/login'))
const imageCtaText = computed(() => (isLoggedIn.value ? '继续图片生成' : '体验图片生成'))
const agencyCtaTo = computed(() => (isLoggedIn.value ? '/agency' : '/login'))
const agencyCtaText = computed(() => (isLoggedIn.value ? '进入代理中心' : '申请成为代理'))
const footerCtaText = computed(() => (isLoggedIn.value ? '立即开始创作' : '立即免费体验'))

function toggleUserMenu() {
  userMenuOpen.value = !userMenuOpen.value
}

function handleClickOutside(e: MouseEvent) {
  const target = e.target as HTMLElement
  if (!target.closest('.user-menu-wrapper')) {
    userMenuOpen.value = false
  }
}

async function handleLogout() {
  try {
    await logout()
  } catch (e) {
    console.error('Logout failed', e)
  }
  store.logout()
  userMenuOpen.value = false
  router.push('/login')
}

const features = [
  {
    title: 'AI 图片生成',
    desc: '输入文字描述，AI 在 5-10 秒内生成精美图片，支持数十种艺术风格',
    gradient: 'linear-gradient(135deg, #7c3aed, #8b5cf6)',
    icon: cosmicIcons.image,
    tags: ['5秒出图', '10+风格', '高清4K'],
  },
  {
    title: 'AI 视频生成',
    desc: '文字转视频，AI 将你的创意制作成动态短片，支持多种场景',
    gradient: 'linear-gradient(135deg, #06b6d4, #22d3ee)',
    icon: cosmicIcons.video,
    tags: ['文字转视频', '多种场景', '高清输出'],
  },
  {
    title: '积分制消费',
    desc: '按需付费，图片 2 积分/张，视频 10 积分/条，透明合理无隐藏费用',
    gradient: 'linear-gradient(135deg, #f59e0b, #fbbf24)',
    icon: cosmicIcons.star,
    tags: ['按需付费', '透明计费', '多档套餐'],
  },
  {
    title: '代理分佣体系',
    desc: '成为代理推广赚佣金，一级二级代理双重收益，轻松实现被动收入',
    gradient: 'linear-gradient(135deg, #10b981, #34d399)',
    icon: cosmicIcons.users,
    tags: ['二级代理', '实时分佣', '自动结算'],
  },
]

const aiImageFeatures = [
  '支持 4K 高清输出',
  '赛博朋克 / 国风 / 动漫 / 写实等数十种风格',
  '智能参考图片构图',
  '批量生成节省积分',
  '历史作品云端保存',
]

const pricingPackages = [
  {
    name: '体验版',
    points: 20,
    price: 9.9,
    per: '0.50',
    popular: false,
    features: ['20积分', '图片2积分/张', '视频10积分/条', '有效期30天'],
  },
  {
    name: '创作版',
    points: 100,
    price: 39.9,
    per: '0.40',
    popular: true,
    features: ['100积分', '图片2积分/张', '视频10积分/条', '有效期90天', '优先排队'],
  },
  {
    name: '专业版',
    points: 500,
    price: 169,
    per: '0.34',
    popular: false,
    features: ['500积分', '图片2积分/张', '视频10积分/条', '有效期180天', '专属客服'],
  },
  {
    name: '企业版',
    points: 2000,
    price: 599,
    per: '0.30',
    popular: false,
    features: ['2000积分', '图片2积分/张', '视频10积分/条', '有效期365天', 'API接口', '专属客服'],
  },
]

const agencyFeatures = [
  {
    title: '二级代理体系',
    desc: '发展下级代理，享受二级内所有用户的消费佣金',
    icon: cosmicIcons.users,
  },
  {
    title: '实时佣金结算',
    desc: '积分变化实时通知，佣金按分钟结算，快速提现',
    icon: cosmicIcons.chart,
  },
  {
    title: '团队数据透明',
    desc: '查看下级用户、消费流水、佣金明细，账目一目了然',
    icon: cosmicIcons.history,
  },
  {
    title: '积分分配权限',
    desc: '向下级用户分配积分，管理团队成员，灵活运营',
    icon: cosmicIcons.recharge,
  },
]

function initStars() {
  const canvas = starsCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  const resize = () => {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }
  resize()
  window.addEventListener('resize', resize)

  const stars: Array<{x: number; y: number; r: number; speed: number; opacity: number; twinkle: number; phase: number}> = []
  for (let i = 0; i < 250; i++) {
    stars.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      r: Math.random() * 1.5 + 0.3,
      speed: Math.random() * 0.3 + 0.05,
      opacity: Math.random() * 0.6 + 0.2,
      twinkle: Math.random() * 0.01 + 0.005,
      phase: Math.random() * Math.PI * 2,
    })
  }

  let t = 0
  function draw() {
    if (!ctx || !canvas) return
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    t += 0.016

    for (const s of stars) {
      const o = s.opacity * (0.7 + 0.3 * Math.sin(t * s.twinkle * 60 + s.phase))
      ctx.beginPath()
      ctx.arc(s.x, s.y, s.r, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(200, 200, 255, ${o})`
      ctx.fill()

      // 偶尔流星
      if (Math.random() < 0.0003) {
        const angle = Math.PI / 4 + (Math.random() - 0.5) * 0.3
        const len = Math.random() * 80 + 40
        const gx = s.x, gy = s.y
        const grad = ctx.createLinearGradient(gx, gy, gx + len * Math.cos(angle), gy - len * Math.sin(angle))
        grad.addColorStop(0, 'rgba(200, 200, 255, 0.8)')
        grad.addColorStop(1, 'rgba(200, 200, 255, 0)')
        ctx.beginPath()
        ctx.moveTo(gx, gy)
        ctx.lineTo(gx + len * Math.cos(angle), gy - len * Math.sin(angle))
        ctx.strokeStyle = grad
        ctx.lineWidth = 1
        ctx.stroke()
      }
    }

    animationId = requestAnimationFrame(draw)
  }
  draw()
}

onMounted(() => {
  initStars()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* ========== 整体 ========== */
.home-page {
  min-height: 100vh;
  background: #03000f;
  position: relative;
  overflow-x: hidden;
}

/* ========== 星空画布 ========== */
.starry-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}

.stars-canvas {
  position: absolute;
  inset: 0;
}

.nebula {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.12;
  pointer-events: none;
}

.nebula-1 {
  width: 600px; height: 600px;
  background: radial-gradient(circle, #7c3aed, transparent);
  top: -200px; right: -100px;
}

.nebula-2 {
  width: 500px; height: 500px;
  background: radial-gradient(circle, #06b6d4, transparent);
  bottom: 10%; left: -150px;
}

.nebula-3 {
  width: 400px; height: 400px;
  background: radial-gradient(circle, #ec4899, transparent);
  top: 40%; left: 30%;
  opacity: 0.08;
}

/* ========== Hero 导航 ========== */
.hero-nav {
  position: relative;
  z-index: 10;
  padding: 0 48px;
  height: 68px;
  display: flex;
  align-items: center;
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  background: rgba(3, 0, 15, 0.6);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.hero-nav-inner {
  display: flex;
  align-items: center;
  gap: 32px;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.hero-logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.hero-logo-orb {
  width: 36px;
  height: 36px;
  animation: orb-pulse 4s ease-in-out infinite;
}

@keyframes orb-pulse {
  0%, 100% { filter: drop-shadow(0 0 6px rgba(124, 58, 237, 0.4)); }
  50% { filter: drop-shadow(0 0 14px rgba(124, 58, 237, 0.7)); }
}

.hero-logo-title {
  font-family: var(--font-display);
  font-size: 17px;
  font-weight: 700;
  background: var(--gradient-dream);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: block;
  line-height: 1;
}

.hero-logo-sub {
  font-size: 9px;
  color: var(--color-text-muted);
  letter-spacing: 0.12em;
  text-transform: uppercase;
  display: block;
  margin-top: 2px;
}

.hero-nav-links {
  display: flex;
  align-items: center;
  gap: 24px;
  flex: 1;
}

.hero-nav-link {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-muted);
  text-decoration: none;
  transition: color 0.2s ease;
}

.hero-nav-link:hover { color: var(--color-text-primary); }

.hero-nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 用户菜单 */
.user-menu-wrapper {
  position: relative;
}

.user-avatar-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(124, 58, 237, 0.15);
  border: 1px solid rgba(124, 58, 237, 0.3);
  border-radius: 9999px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-avatar-btn:hover {
  background: rgba(124, 58, 237, 0.25);
  border-color: rgba(124, 58, 237, 0.5);
}

.avatar-initials {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--gradient-dream);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  color: white;
}

.user-arrow {
  color: rgba(255, 255, 255, 0.6);
  transition: transform 0.2s ease;
}

.user-arrow.open {
  transform: rotate(180deg);
}

/* 下拉菜单 */
.user-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 180px;
  background: rgba(15, 10, 31, 0.98);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.6), 0 0 0 1px rgba(124, 58, 237, 0.15);
  z-index: 100;
}

.dropdown-header {
  padding: 8px 12px 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
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
  margin: 8px 0;
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

.dropdown-item svg {
  flex-shrink: 0;
}

/* 下拉动画 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.95);
}

/* ========== Hero 区域 ========== */
.hero-section {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 64px;
  padding: 80px 48px 120px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.hero-content {
  flex: 1;
  max-width: 600px;
  animation: hero-in 0.8s ease forwards;
}

@keyframes hero-in {
  0% { opacity: 0; transform: translateY(30px); }
  100% { opacity: 1; transform: translateY(0); }
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  background: rgba(124, 58, 237, 0.1);
  border: 1px solid rgba(124, 58, 237, 0.25);
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 600;
  color: #a78bfa;
  margin-bottom: 24px;
  animation: hero-in 0.8s ease forwards;
}

.hero-badge-dot {
  width: 6px; height: 6px;
  border-radius: 50%;
  background: #7c3aed;
  animation: dot-pulse 2s ease-in-out infinite;
}

@keyframes dot-pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(0.7); }
}

.hero-title {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 24px;
}

.hero-title-line {
  font-size: clamp(32px, 5vw, 52px);
  font-weight: 800;
  line-height: 1.1;
  letter-spacing: -0.02em;
  color: var(--color-text-primary);
}

.gradient-text {
  background: var(--gradient-dream);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-text-muted);
  margin-bottom: 36px;
}

.hero-cta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 48px;
}

.btn-hero-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  background: var(--gradient-dream);
  color: white;
  font-size: 15px;
  font-weight: 700;
  border-radius: 12px;
  text-decoration: none;
  transition: all 0.25s ease;
  box-shadow: 0 4px 20px rgba(124, 58, 237, 0.35);
}

.btn-hero-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(124, 58, 237, 0.5);
}

.btn-hero-secondary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  background: rgba(255, 255, 255, 0.04);
  color: var(--color-text-secondary);
  font-size: 15px;
  font-weight: 600;
  border-radius: 12px;
  text-decoration: none;
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: all 0.2s ease;
}

.btn-hero-secondary:hover {
  background: rgba(255, 255, 255, 0.08);
  color: var(--color-text-primary);
}

.hero-stats {
  display: flex;
  align-items: center;
  gap: 24px;
}

.hero-stat { text-align: center; }

.hero-stat-val {
  display: block;
  font-size: 22px;
  font-weight: 800;
  background: var(--gradient-dream);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
}

.hero-stat-label {
  display: block;
  font-size: 12px;
  color: var(--color-text-muted);
  margin-top: 2px;
}

.hero-stat-divider {
  width: 1px;
  height: 32px;
  background: rgba(255, 255, 255, 0.08);
}

/* Hero 视觉 */
.hero-visual {
  flex-shrink: 0;
  width: 420px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-orb-container {
  position: relative;
  width: 320px;
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-center-orb {
  width: 200px;
  height: 200px;
  animation: orb-float 6s ease-in-out infinite;
  filter: drop-shadow(0 0 40px rgba(124, 58, 237, 0.3));
}

@keyframes orb-float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-16px); }
}

.float-card {
  position: absolute;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  animation: float 5s ease-in-out infinite;
}

.float-card-1 { top: 20px; right: -20px; animation-delay: 0s; }
.float-card-2 { bottom: 60px; left: -30px; animation-delay: 1.5s; }
.float-card-3 { bottom: 0; right: 40px; animation-delay: 3s; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.fc-icon { width: 16px; height: 16px; }
.fc-icon :deep(svg) { width: 16px; height: 16px; }
.fc-text { font-size: 12px; font-weight: 600; color: var(--color-text-secondary); white-space: nowrap; }

/* ========== 通用 Section ========== */
.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 48px;
}

.section-header {
  text-align: center;
  margin-bottom: 48px;
}

.section-tag {
  display: inline-block;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #a78bfa;
  padding: 4px 12px;
  background: rgba(124, 58, 237, 0.08);
  border: 1px solid rgba(124, 58, 237, 0.2);
  border-radius: 9999px;
  margin-bottom: 16px;
}

.section-title {
  font-size: 32px;
  font-weight: 800;
  letter-spacing: -0.02em;
  color: var(--color-text-primary);
  margin-bottom: 12px;
}

.section-subtitle {
  font-size: 15px;
  color: var(--color-text-muted);
  line-height: 1.7;
  max-width: 560px;
  margin: 0 auto;
}

/* ========== 功能区 ========== */
.features-section {
  position: relative;
  z-index: 1;
  padding: 100px 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.feature-card {
  padding: 28px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  animation: fade-in-up 0.5s ease forwards;
  opacity: 0;
  transition: all 0.25s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
  border-color: rgba(124, 58, 237, 0.2);
}

@keyframes fade-in-up {
  0% { opacity: 0; transform: translateY(20px); }
  100% { opacity: 1; transform: translateY(0); }
}

.feature-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-icon :deep(svg) { width: 26px; height: 26px; }

.feature-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 6px;
}

.feature-desc {
  font-size: 13px;
  color: var(--color-text-muted);
  line-height: 1.7;
  flex: 1;
}

.feature-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.feature-tag {
  font-size: 10px;
  font-weight: 600;
  padding: 3px 8px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 9999px;
  color: var(--color-text-muted);
}

/* ========== 展示区 ========== */
.showcase-section {
  position: relative;
  z-index: 1;
  padding: 80px 0;
}

.showcase-split {
  display: flex;
  align-items: center;
  gap: 80px;
}

.showcase-text { flex: 1; }
.showcase-text .section-title { text-align: left; font-size: 28px; }
.showcase-text .section-subtitle { margin: 0; text-align: left; }

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 24px;
}

.feature-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.showcase-images { flex-shrink: 0; }

.showcase-img-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 12px;
  width: 420px;
}

.showcase-img {
  border-radius: 14px;
  object-fit: cover;
  height: 160px;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.showcase-img:hover { transform: scale(1.03); }

.si-1 { grid-row: span 2; height: 332px; }
.si-4 { grid-column: span 2; width: 100%; }

/* ========== 价格区 ========== */
.pricing-section {
  position: relative;
  z-index: 1;
  padding: 100px 0;
}

.pricing-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  align-items: start;
}

.pricing-card {
  padding: 28px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  text-align: center;
  position: relative;
  transition: all 0.25s ease;
}

.pricing-card:hover { transform: translateY(-4px); }

.pricing-card.popular {
  border-color: rgba(124, 58, 237, 0.3);
  background: rgba(124, 58, 237, 0.08);
}

.pricing-badge {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10px;
  font-weight: 700;
  padding: 3px 12px;
  background: var(--gradient-dream);
  color: white;
  border-radius: 9999px;
  white-space: nowrap;
}

.pricing-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-muted);
  margin-bottom: 4px;
}

.pricing-points {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.pp-val { font-size: 36px; font-weight: 800; color: var(--color-text-primary); }
.pp-unit { font-size: 14px; color: var(--color-text-muted); }

.pricing-price {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-top: 4px;
}

.pricing-per {
  font-size: 11px;
  color: var(--color-text-muted);
  margin-bottom: 16px;
}

.pricing-features {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
  margin-bottom: 20px;
}

.pricing-features li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--color-text-muted);
  text-align: left;
}

.pricing-btn {
  display: block;
  width: 100%;
  padding: 10px;
  border-radius: 10px;
  text-align: center;
  font-size: 13px;
  font-weight: 600;
  text-decoration: none;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--color-text-secondary);
  transition: all 0.2s ease;
}

.pricing-btn.primary {
  background: var(--gradient-dream);
  color: white;
  border: none;
  box-shadow: 0 4px 16px rgba(124, 58, 237, 0.3);
}

/* ========== 代理区 ========== */
.agency-section {
  position: relative;
  z-index: 1;
  padding: 100px 0;
  overflow: hidden;
}

.agency-bg-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 120px;
  font-weight: 900;
  color: rgba(124, 58, 237, 0.04);
  letter-spacing: 0.1em;
  pointer-events: none;
  user-select: none;
  white-space: nowrap;
}

.agency-features {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 40px;
}

.agency-feature {
  padding: 24px 20px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.af-icon { width: 40px; height: 40px; }
.af-icon :deep(svg) { width: 40px; height: 40px; }

.af-title { font-size: 14px; font-weight: 700; color: var(--color-text-primary); }
.af-desc { font-size: 12px; color: var(--color-text-muted); line-height: 1.6; }

.agency-cta { text-align: center; }

/* ========== CTA ========== */
.cta-section {
  position: relative;
  z-index: 1;
  padding: 100px 48px;
  text-align: center;
  overflow: hidden;
}

.cta-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 600px;
  height: 300px;
  background: radial-gradient(ellipse, rgba(124, 58, 237, 0.15), transparent 70%);
  pointer-events: none;
}

.cta-content { position: relative; }

.cta-title {
  font-size: 36px;
  font-weight: 800;
  color: var(--color-text-primary);
  margin-bottom: 12px;
}

.cta-sub {
  font-size: 15px;
  color: var(--color-text-muted);
  margin-bottom: 32px;
}

.cta-btn { padding: 16px 36px; font-size: 16px; }

/* ========== Footer ========== */
.home-footer {
  position: relative;
  z-index: 1;
  padding: 32px 48px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.footer-logo { display: flex; align-items: center; gap: 16px; }
.footer-copy { font-size: 12px; color: var(--color-text-muted); }

.footer-links {
  display: flex;
  gap: 24px;
}

.footer-links a {
  font-size: 13px;
  color: var(--color-text-muted);
  text-decoration: none;
  transition: color 0.2s;
}

.footer-links a:hover { color: var(--color-text-primary); }

/* ========== 响应式 ========== */
@media (max-width: 1024px) {
  .features-grid { grid-template-columns: repeat(2, 1fr); }
  .pricing-grid { grid-template-columns: repeat(2, 1fr); }
  .agency-features { grid-template-columns: repeat(2, 1fr); }
  .hero-section { flex-direction: column; text-align: center; padding: 60px 32px 80px; }
  .hero-cta { justify-content: center; }
  .hero-stats { justify-content: center; }
  .showcase-split { flex-direction: column; }
  .showcase-img-grid { width: 100%; }
}

@media (max-width: 768px) {
  .hero-nav { padding: 0 20px; }
  .hero-nav-links { display: none; }
  .features-grid, .pricing-grid, .agency-features { grid-template-columns: 1fr; }
  .section-container { padding: 0 20px; }
  .cta-section { padding: 60px 20px; }
  .footer-inner { flex-direction: column; gap: 16px; text-align: center; }
  .footer-links { flex-wrap: wrap; justify-content: center; }
}
</style>
