<template>
  <footer class="dream-footer">
    <div class="footer-inner">
      <div class="footer-top">
        <!-- 品牌 -->
        <div class="footer-brand">
          <div class="footer-logo">
            <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg" width="36" height="36">
              <defs>
                <linearGradient id="footerLogoGrad" x1="0" y1="0" x2="40" y2="40" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#7c3aed"/>
                  <stop offset="0.5" stop-color="#06b6d4"/>
                  <stop offset="1" stop-color="#ec4899"/>
                </linearGradient>
              </defs>
              <path d="M20 4L36 12V28L20 36L4 28V12L20 4Z" stroke="url(#footerLogoGrad)" stroke-width="2" fill="none"/>
              <path d="M20 10L28 14.5V23.5L20 28L12 23.5V14.5L20 10Z" fill="url(#footerLogoGrad)" opacity="0.6"/>
              <circle cx="20" cy="19" r="4" fill="white"/>
            </svg>
            <span class="footer-logo-text">追梦Dream</span>
          </div>
          <p class="footer-desc">
            探索 AI 创造的无限可能<br>
            用科技点燃创意，让梦想触手可及
          </p>
          <div class="footer-social">
            <a href="#" class="social-link" aria-label="微信">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M8.5 11.5a1 1 0 1 0 0-2 1 1 0 0 0 0 2zm5 0a1 1 0 1 0 0-2 1 1 0 0 0 0 2z"/><path d="M12 2C6.477 2 2 6.477 2 12c0 1.89.525 3.66 1.438 5.168L2.586 20.4a1 1 0 0 0 1.014.852l3.257-1.178A11.95 11.95 0 0 0 12 22c5.523 0 10-4.477 10-10S17.523 2 12 2z" fill="none" stroke="currentColor" stroke-width="1.5"/></svg>
            </a>
            <a href="#" class="social-link" aria-label="微博">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M20.194 14.197c0 3.058-3.927 5.54-8.77 5.54-4.842 0-8.77-2.482-8.77-5.54 0-3.058 3.928-5.54 8.77-5.54 4.843 0 8.77 2.482 8.77 5.54z" fill="none" stroke="currentColor" stroke-width="1.5"/><ellipse cx="15.5" cy="9.5" rx="1.5" ry="2" fill="none" stroke="currentColor" stroke-width="1.5"/></svg>
            </a>
            <a href="#" class="social-link" aria-label="抖音">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M19.59 6.69a4.83 4.83 0 0 1-3.77-4.25V2h-3.45v13.67a2.89 2.89 0 0 1-2.88 2.5 2.89 2.89 0 0 1-2.89-2.89 2.89 2.89 0 0 1 2.89-2.89c.28 0 .54.04.79.1V9.01a6.33 6.33 0 0 0-.79-.05 6.34 6.34 0 0 0-6.34 6.34 6.34 6.34 0 0 0 6.34 6.34 6.34 6.34 0 0 0 6.33-6.34V8.69a8.18 8.18 0 0 0 4.78 1.53V6.75a4.85 4.85 0 0 1-1.01-.06z"/></svg>
            </a>
          </div>
        </div>

        <!-- 链接 -->
        <div class="footer-links">
          <div class="footer-col" v-for="col in footerCols" :key="col.title">
            <h4 class="footer-col-title">{{ col.title }}</h4>
            <ul class="footer-col-list">
              <li v-for="link in col.links" :key="link.label">
                <a :href="link.href" class="footer-link">{{ link.label }}</a>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <div class="footer-divider" />

      <div class="footer-bottom">
        <p class="footer-copy">
          © {{ new Date().getFullYear() }} 追梦Dream · AI 内容创作平台 · 京ICP备XXXXXXXX号
        </p>
        <div class="footer-bottom-links">
          <a href="#" class="footer-bottom-link">用户协议</a>
          <a href="#" class="footer-bottom-link">隐私政策</a>
          <a href="#" class="footer-bottom-link">联系方式</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
const footerCols = [
  {
    title: '产品服务',
    links: [
      { label: 'AI 图片生成', href: '/ai/image' },
      { label: 'AI 视频生成', href: '/ai/video' },
      { label: '积分充值', href: '/recharge' },
    ],
  },
  {
    title: '代理合作',
    links: [
      { label: '代理中心', href: '/agency' },
      { label: '分佣规则', href: '#' },
      { label: '推广素材', href: '#' },
    ],
  },
  {
    title: '帮助与支持',
    links: [
      { label: '使用教程', href: '#' },
      { label: '常见问题', href: '#' },
      { label: 'API 文档', href: '#' },
    ],
  },
]
</script>

<style scoped>
.dream-footer {
  background: linear-gradient(to top, rgba(124, 58, 237, 0.05) 0%, transparent 100%);
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding: 64px 24px 32px;
  margin-top: 80px;
}

.footer-inner {
  max-width: 1280px;
  margin: 0 auto;
}

.footer-top {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 64px;
  margin-bottom: 48px;
}

/* 品牌 */
.footer-brand {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.footer-logo-text {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 700;
  background: var(--gradient-dream);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-desc {
  font-size: 13px;
  color: var(--color-text-muted);
  line-height: 1.8;
}

.footer-social {
  display: flex;
  gap: 8px;
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: var(--color-text-muted);
  transition: all 0.2s ease;
}

.social-link:hover {
  background: rgba(124, 58, 237, 0.15);
  border-color: rgba(124, 58, 237, 0.3);
  color: #a78bfa;
  transform: translateY(-2px);
}

/* 链接组 */
.footer-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.footer-col-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 16px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.footer-col-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.footer-link {
  font-size: 13px;
  color: var(--color-text-muted);
  text-decoration: none;
  transition: color 0.2s ease;
}

.footer-link:hover {
  color: #a78bfa;
}

.footer-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.08), transparent);
  margin-bottom: 24px;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.footer-copy {
  font-size: 12px;
  color: var(--color-text-muted);
}

.footer-bottom-links {
  display: flex;
  gap: 24px;
}

.footer-bottom-link {
  font-size: 12px;
  color: var(--color-text-muted);
  text-decoration: none;
  transition: color 0.2s ease;
}

.footer-bottom-link:hover {
  color: #a78bfa;
}

/* 响应式 */
@media (max-width: 900px) {
  .footer-top {
    grid-template-columns: 1fr;
    gap: 40px;
  }

  .footer-links {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .footer-links {
    grid-template-columns: 1fr;
  }

  .footer-bottom {
    flex-direction: column;
    text-align: center;
  }
}
</style>
