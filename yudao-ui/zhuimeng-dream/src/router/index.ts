import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/home/HomePage.vue'),
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/LoginPage.vue'),
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/login/LoginPage.vue'),
    meta: { registerMode: true },
  },
  // 需要侧边栏布局的页面
  {
    path: '/dashboard',
    name: 'dashboard',
    component: () => import('@/views/dashboard/DashboardPage.vue'),
    meta: { requiresAuth: true, layout: 'app' },
  },
  {
    path: '/ai/image',
    name: 'ai-image',
    component: () => import('@/views/ai/ImageGeneratePage.vue'),
    meta: { requiresAuth: true, layout: 'app' },
  },
  {
    path: '/ai/video',
    name: 'ai-video',
    component: () => import('@/views/ai/VideoGeneratePage.vue'),
    meta: { requiresAuth: true, layout: 'app' },
  },
  {
    path: '/ai/video/analyze',
    name: 'ai-video-analyze',
    component: () => import('@/views/ai/VideoAnalyzePage.vue'),
    meta: { requiresAuth: true, layout: 'app' },
  },
  {
    path: '/ai/history',
    name: 'ai-history',
    component: () => import('@/views/ai/HistoryPage.vue'),
    meta: { requiresAuth: true, layout: 'app' },
  },
  {
    path: '/recharge',
    name: 'recharge',
    component: () => import('@/views/recharge/RechargePage.vue'),
    meta: { requiresAuth: true, layout: 'app' },
  },
  {
    path: '/inspiration',
    name: 'inspiration',
    component: () => import('@/views/inspiration/InspirationSquarePage.vue'),
    meta: { requiresAuth: true, layout: 'app' },
  },
  {
    path: '/agency',
    name: 'agency',
    component: () => import('@/views/agency/AgencyPage.vue'),
    meta: { requiresAuth: true, layout: 'app' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0, behavior: 'smooth' }
  },
})

router.beforeEach((to, _from, next) => {
  if (to.meta.requiresAuth) {
    const isLoggedIn = localStorage.getItem('token')
    if (!isLoggedIn) {
      next({ name: 'login', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router
