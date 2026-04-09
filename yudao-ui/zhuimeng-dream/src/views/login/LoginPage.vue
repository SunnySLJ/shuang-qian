<template>
  <div class="login-page">
    <ParticleBackground />
    <div class="login-container">
      <!-- 左侧品牌 -->
      <div class="login-brand">
        <div class="brand-inner">
          <div class="brand-logo">
            <svg viewBox="0 0 60 60" fill="none" xmlns="http://www.w3.org/2000/svg" width="60" height="60">
              <defs>
                <linearGradient id="loginLogoGrad" x1="0" y1="0" x2="60" y2="60" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#7c3aed"/>
                  <stop offset="0.5" stop-color="#06b6d4"/>
                  <stop offset="1" stop-color="#ec4899"/>
                </linearGradient>
              </defs>
              <path d="M30 5L55 18V42L30 55L5 42V18L30 5Z" stroke="url(#loginLogoGrad)" stroke-width="2" fill="none"/>
              <path d="M30 15L42 21.5V35.5L30 42L18 35.5V21.5L30 15Z" fill="url(#loginLogoGrad)" opacity="0.5"/>
              <circle cx="30" cy="28" r="7" fill="white"/>
            </svg>
            <div>
              <h1 class="brand-name">追梦Dream</h1>
              <p class="brand-tagline">AI 内容创作平台</p>
            </div>
          </div>
          <div class="brand-features">
            <div class="brand-feature" v-for="f in brandFeatures" :key="f.text">
              <span class="brand-feature-icon" v-html="f.icon" />
              <span>{{ f.text }}</span>
            </div>
          </div>
          <div class="brand-quote">
            <p>"用 AI 将想象力化为现实"</p>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-form-wrapper">
        <div class="login-card glass-card">
          <div class="login-tabs">
            <button
              v-for="tab in tabs"
              :key="tab.key"
              class="login-tab"
              :class="{ active: activeTab === tab.key }"
              @click="activeTab = tab.key"
            >
              {{ tab.label }}
            </button>
          </div>

          <!-- 登录表单 -->
          <form v-if="activeTab === 'login'" class="form-body" @submit.prevent="handleLogin">
            <div class="form-title">欢迎回来</div>
            <p class="form-subtitle">登录您的追梦Dream账户</p>

            <div class="form-fields">
              <div class="form-field">
                <label class="field-label">手机号</label>
                <div class="field-input-wrap">
                  <span class="field-prefix">+86</span>
                  <input
                    v-model="loginForm.phone"
                    type="tel"
                    class="input-dream"
                    placeholder="请输入手机号"
                    maxlength="11"
                  />
                </div>
              </div>

              <div class="form-field">
                <div class="field-label-row">
                  <label class="field-label">验证码</label>
                  <button type="button" class="btn-ghost" @click="sendCode" :disabled="countdown > 0">
                    {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
                  </button>
                </div>
                <input
                  v-model="loginForm.code"
                  type="text"
                  class="input-dream"
                  placeholder="请输入验证码"
                  maxlength="6"
                />
              </div>
            </div>

            <div v-if="errorMsg" class="error-alert">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
              {{ errorMsg }}
            </div>

            <button type="submit" class="btn-primary form-submit" :disabled="loading">
              <span v-if="loading" class="loading-spinner" />
              {{ loading ? '登录中...' : '登录' }}
            </button>

            <p class="form-footer-text">
              登录即表示同意
              <a href="#" class="link">《用户协议》</a> 和
              <a href="#" class="link">《隐私政策》</a>
            </p>
          </form>

          <!-- 注册表单 -->
          <form v-else class="form-body" @submit.prevent="handleRegister">
            <div class="form-title">创建账户</div>
            <p class="form-subtitle">加入追梦Dream，开启 AI 创作之旅</p>

            <div class="form-fields">
              <div class="form-field">
                <label class="field-label">手机号</label>
                <div class="field-input-wrap">
                  <span class="field-prefix">+86</span>
                  <input
                    v-model="registerForm.phone"
                    type="tel"
                    class="input-dream"
                    placeholder="请输入手机号"
                    maxlength="11"
                  />
                </div>
              </div>

              <div class="form-field">
                <label class="field-label">验证码</label>
                <input
                  v-model="registerForm.code"
                  type="text"
                  class="input-dream"
                  placeholder="请输入验证码"
                  maxlength="6"
                />
              </div>

              <div class="form-field">
                <label class="field-label">邀请码（选填）</label>
                <input
                  v-model="registerForm.inviteCode"
                  type="text"
                  class="input-dream"
                  placeholder="输入邀请码，绑定上级代理"
                />
              </div>
            </div>

            <button type="submit" class="btn-primary form-submit" :disabled="loading">
              <span v-if="loading" class="loading-spinner" />
              {{ loading ? '注册中...' : '注册' }}
            </button>

            <p class="form-footer-text">
              注册即表示同意
              <a href="#" class="link">《用户协议》</a> 和
              <a href="#" class="link">《隐私政策》</a>
            </p>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import ParticleBackground from '@/components/common/ParticleBackground.vue'

const router = useRouter()
const store = useAppStore()

const activeTab = ref<'login' | 'register'>('login')
const loading = ref(false)
const errorMsg = ref('')
const countdown = ref(0)

const tabs = [
  { key: 'login' as const, label: '登录' },
  { key: 'register' as const, label: '注册' },
]

const loginForm = reactive({ phone: '13800138000', code: '123456' })
const registerForm = reactive({ phone: '13800138000', code: '123456', inviteCode: '' })

const brandFeatures = [
  { icon: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#a78bfa" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21"/></svg>`, text: 'AI 图片生成' },
  { icon: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#22d3ee" stroke-width="2"><polygon points="23 7 16 12 23 17 23 7"/><rect x="1" y="5" width="15" height="14" rx="2"/></svg>`, text: 'AI 视频生成' },
  { icon: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#f59e0b" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>`, text: '代理分销收益' },
]

async function sendCode() {
  const phone = activeTab.value === 'login' ? loginForm.phone : registerForm.phone
  if (!phone || phone.length < 11) {
    errorMsg.value = '请输入正确的手机号'
    return
  }
  // TODO: 调用发送验证码API
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}

async function handleLogin() {
  if (!loginForm.phone || !loginForm.code) {
    errorMsg.value = '请填写完整信息'
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    // TODO: 调用登录API
    await new Promise(resolve => setTimeout(resolve, 1000))
    store.login('mock-token', { id: 1, nickname: '追梦用户', avatar: '', phone: loginForm.phone, level: 0, brokerageUserId: null })
    router.push('/dashboard')
  } catch {
    errorMsg.value = '登录失败，请重试'
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (!registerForm.phone || !registerForm.code) {
    errorMsg.value = '请填写完整信息'
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    // TODO: 调用注册API
    await new Promise(resolve => setTimeout(resolve, 1000))
    store.login('mock-token', { id: 1, nickname: '新用户', avatar: '', phone: registerForm.phone, level: 0, brokerageUserId: null })
    router.push('/dashboard')
  } catch {
    errorMsg.value = '注册失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  max-width: 1000px;
  width: 100%;
  min-height: 600px;
  border-radius: 24px;
  overflow: hidden;
  position: relative;
  z-index: 1;
  margin: 24px;
  box-shadow: 0 32px 80px rgba(0, 0, 0, 0.5);
}

/* 左侧品牌 */
.login-brand {
  background: linear-gradient(135deg, #1a0a3a 0%, #0a1628 100%);
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-brand::before {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(124, 58, 237, 0.3) 0%, transparent 70%);
  top: -100px;
  left: -100px;
  pointer-events: none;
}

.login-brand::after {
  content: '';
  position: absolute;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(6, 182, 212, 0.2) 0%, transparent 70%);
  bottom: -50px;
  right: -50px;
  pointer-events: none;
}

.brand-inner {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 16px;
}

.brand-name {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 700;
  background: var(--gradient-dream);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-tagline {
  font-size: 13px;
  color: var(--color-text-muted);
  margin-top: 4px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.brand-feature {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.brand-feature-icon {
  display: flex;
  align-items: center;
}

.brand-quote {
  padding: 20px;
  border-left: 2px solid rgba(124, 58, 237, 0.5);
  background: rgba(124, 58, 237, 0.05);
  border-radius: 0 8px 8px 0;
}

.brand-quote p {
  font-size: 15px;
  color: var(--color-text-secondary);
  font-style: italic;
  line-height: 1.6;
}

/* 右侧表单 */
.login-form-wrapper {
  background: rgba(10, 5, 25, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-card {
  background: transparent;
  border: none;
  box-shadow: none;
  padding: 0;
}

.login-card:hover {
  transform: none;
  background: transparent;
  border: none;
  box-shadow: none;
}

.login-tabs {
  display: flex;
  gap: 0;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 10px;
  padding: 4px;
  margin-bottom: 32px;
  width: fit-content;
}

.login-tab {
  padding: 8px 24px;
  border-radius: 8px;
  border: none;
  background: transparent;
  color: var(--color-text-muted);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.login-tab.active {
  background: var(--gradient-dream);
  color: white;
  box-shadow: 0 2px 12px rgba(124, 58, 237, 0.3);
}

.form-body {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.form-subtitle {
  font-size: 14px;
  color: var(--color-text-muted);
  margin-top: -8px;
}

.form-fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-secondary);
}

.field-label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.field-input-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 10px;
  padding-left: 12px;
  transition: all 0.2s ease;
}

.field-input-wrap:focus-within {
  border-color: rgba(124, 58, 237, 0.5);
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.1);
}

.field-prefix {
  font-size: 14px;
  color: var(--color-text-muted);
  font-weight: 500;
}

.field-input-wrap .input-dream {
  border: none;
  background: transparent;
  padding-left: 0;
  flex: 1;
}

.error-alert {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: 8px;
  font-size: 13px;
  color: #f87171;
}

.form-submit {
  width: 100%;
  padding: 14px;
  font-size: 15px;
  margin-top: 8px;
}

.form-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.form-footer-text {
  font-size: 12px;
  color: var(--color-text-muted);
  text-align: center;
}

.link {
  color: #a78bfa;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .login-container {
    grid-template-columns: 1fr;
  }

  .login-brand {
    display: none;
  }
}
</style>
