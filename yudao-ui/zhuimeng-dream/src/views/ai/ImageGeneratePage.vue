<template>
  <div class="image-generate-page">
    <div class="page-layout">
      <!-- 左侧：输入区 -->
      <div class="input-panel glass-card">
        <div class="panel-header">
          <h2 class="panel-title">
            <span v-html="icons.image" class="panel-title-icon" />
            AI 图片生成
          </h2>
          <div class="panel-cost">
            <span v-html="icons.star" />
            <span>{{ cost }} 积分/次</span>
          </div>
        </div>

        <!-- Prompt 输入 -->
        <div class="prompt-area">
          <textarea
            v-model="prompt"
            class="prompt-input"
            placeholder="描述你想要的画面，比如：未来城市天际线，赛博朋克风格，高达机器人站在雨中..."
            rows="5"
          />
          <div class="prompt-actions">
            <button class="btn-ghost" @click="prompt = ''" title="清空">
              <span v-html="icons.refresh" />
              清空
            </button>
          </div>
        </div>

        <!-- 风格选择 -->
        <div class="style-section">
          <div class="section-label">艺术风格</div>
          <div class="style-grid">
            <button
              v-for="style in styles"
              :key="style.id"
              class="style-btn"
              :class="{ active: selectedStyle === style.id }"
              @click="selectedStyle = style.id"
            >
              <span class="style-emoji">{{ style.emoji }}</span>
              <span>{{ style.name }}</span>
            </button>
          </div>
        </div>

        <!-- 尺寸 -->
        <div class="size-section">
          <div class="section-label">图片尺寸</div>
          <div class="size-grid">
            <button
              v-for="size in sizes"
              :key="size.id"
              class="size-btn"
              :class="{ active: selectedSize === size.id }"
              @click="selectedSize = size.id"
            >
              <span class="size-icon" :style="size.grid" />
              <span>{{ size.name }}</span>
            </button>
          </div>
        </div>

        <!-- 生成按钮 -->
        <button
          class="btn-primary generate-btn"
          :disabled="!prompt.trim() || generating"
          @click="generate"
        >
          <span v-if="generating" class="loading-spinner" />
          <span v-else v-html="icons.rocket" />
          {{ generating ? '创作中...' : '立即生成' }}
        </button>

        <!-- 积分不足提示 -->
        <div v-if="!canAfford" class="balance-warning">
          <span v-html="icons.target" />
          积分不足，当前 {{ store.wallet?.balance || 0 }}，需要 {{ cost }} 积分
          <router-link to="/recharge" class="btn-ghost" style="margin-left: auto; font-size: 12px; color: #a78bfa;">去充值 →</router-link>
        </div>
      </div>

      <!-- 右侧：结果展示 -->
      <div class="result-panel">
        <!-- 生成中状态 -->
        <div v-if="generating" class="generating-status glass-card">
          <div class="gen-animation">
            <div class="gen-ring" />
            <div class="gen-ring ring-2" />
            <div class="gen-core">
              <span v-html="icons.image" />
            </div>
          </div>
          <div class="gen-text">
            <div class="gen-title">AI 正在创作中...</div>
            <div class="gen-sub">预计需要 5-10 秒</div>
          </div>
          <div class="gen-progress-bar">
            <div class="gen-progress-fill" />
          </div>
        </div>

        <!-- 结果网格 -->
        <div v-else-if="results.length > 0" class="results-grid">
          <div
            v-for="(result, index) in results"
            :key="result.id"
            class="result-item glass-card"
            :style="{ animationDelay: `${index * 100}ms` }"
          >
            <div class="result-image">
              <img :src="result.url" :alt="result.prompt" />
              <div class="result-overlay">
                <button class="result-action" @click="downloadImage(result)">
                  <span v-html="icons.download" />
                </button>
                <button class="result-action">
                  <span v-html="icons.copy" />
                </button>
                <button class="result-action" @click="regenerate(result)">
                  <span v-html="icons.refresh" />
                </button>
              </div>
            </div>
            <div class="result-info">
              <p class="result-prompt">{{ result.prompt }}</p>
              <div class="result-meta">
                <span class="result-style">{{ result.style }}</span>
                <span class="result-cost">-{{ result.cost }}积分</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-state glass-card">
          <div class="empty-orb">
            <span v-html="icons.create" />
          </div>
          <p class="empty-title">还没有生成作品</p>
          <p class="empty-sub">在左侧输入描述词<br>AI 将为你创作独一无二的图片</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { cosmicIcons } from '@/utils/cosmicIcons'

const icons = cosmicIcons
const store = useAppStore()
const route = useRoute()

const cost = 2
const prompt = ref(route.query.prompt as string || '')
const selectedStyle = ref('cyberpunk')
const selectedSize = ref('1:1')
const generating = ref(false)
const results = ref<Array<{id: number; url: string; prompt: string; style: string; cost: number}>>([])

const canAfford = computed(() => (store.wallet?.balance || 0) >= cost)

const styles = [
  { id: 'cyberpunk', name: '赛博朋克', emoji: '🌃' },
  { id: 'realistic', name: '写实摄影', emoji: '📷' },
  { id: 'anime', name: '动漫风格', emoji: '🎨' },
  { id: 'oil', name: '油画质感', emoji: '🖼️' },
  { id: '3d', name: '3D渲染', emoji: '🎮' },
  { id: 'chinese', name: '国风水墨', emoji: '🏮' },
  { id: 'fantasy', name: '魔幻奇幻', emoji: '✨' },
  { id: 'minimal', name: '极简主义', emoji: '⬜' },
]

const sizes = [
  { id: '1:1', name: '1:1 方图', grid: 'width: 28px; height: 28px; background: rgba(255,255,255,0.3); border-radius: 4px;' },
  { id: '16:9', name: '16:9 宽图', grid: 'width: 36px; height: 20px; background: rgba(255,255,255,0.3); border-radius: 4px;' },
  { id: '9:16', name: '9:16 竖图', grid: 'width: 20px; height: 36px; background: rgba(255,255,255,0.3); border-radius: 4px;' },
  { id: '4:3', name: '4:3 横版', grid: 'width: 32px; height: 24px; background: rgba(255,255,255,0.3); border-radius: 4px;' },
]

async function generate() {
  if (!prompt.value.trim() || generating.value || !canAfford.value) return
  generating.value = true
  store.deductPoints(cost)
  await new Promise(resolve => setTimeout(resolve, 3000))
  const styleName = styles.find(s => s.id === selectedStyle.value)?.name || ''
  const unsplashImages = [
    'https://images.unsplash.com/photo-1480714378408-67cf0d13bc1b?w=800&q=80',
    'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&q=80',
    'https://images.unsplash.com/photo-1686191128892-3b37add4c844?w=800&q=80',
    'https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?w=800&q=80',
    'https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800&q=80',
  ]
  results.value.unshift({
    id: Date.now(),
    url: unsplashImages[Math.floor(Math.random() * unsplashImages.length)],
    prompt: prompt.value,
    style: styleName,
    cost,
  })
  generating.value = false
}

function downloadImage(result: typeof results.value[0]) {
  window.open(result.url, '_blank')
}

function regenerate(result: typeof results.value[0]) {
  if (!canAfford.value) return
  store.deductPoints(cost)
  generating.value = true
  setTimeout(() => {
    result.url = `https://picsum.photos/seed/${Date.now()}/800/800`
    generating.value = false
  }, 2000)
}
</script>

<style scoped>
.image-generate-page { padding: 32px 32px; }
.page-layout { display: grid; grid-template-columns: 420px 1fr; gap: 24px; max-width: 1400px; margin: 0 auto; }

.input-panel { height: fit-content; padding: 28px; display: flex; flex-direction: column; gap: 24px; }
.panel-header { display: flex; justify-content: space-between; align-items: center; }
.panel-title { display: flex; align-items: center; gap: 10px; font-size: 18px; font-weight: 700; }
.panel-title-icon { display: flex; }
.panel-title-icon :deep(svg) { width: 22px; height: 22px; }
.panel-cost { display: flex; align-items: center; gap: 4px; font-size: 13px; color: #a78bfa; font-weight: 600; }
.panel-cost :deep(svg) { width: 14px; height: 14px; }

.prompt-area { display: flex; flex-direction: column; gap: 8px; }
.prompt-input { width: 100%; padding: 14px 16px; background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08); border-radius: 12px; color: var(--color-text-primary); font-size: 14px; line-height: 1.7; resize: none; outline: none; font-family: inherit; transition: all 0.2s ease; }
.prompt-input::placeholder { color: var(--color-text-muted); }
.prompt-input:focus { border-color: rgba(124,58,237,0.4); background: rgba(124,58,237,0.05); box-shadow: 0 0 0 3px rgba(124,58,237,0.1); }
.prompt-actions { display: flex; gap: 4px; justify-content: flex-end; }
.prompt-actions :deep(svg) { width: 14px; height: 14px; }

.section-label { font-size: 13px; font-weight: 600; color: var(--color-text-secondary); margin-bottom: 10px; }
.style-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px; }
.style-btn { display: flex; flex-direction: column; align-items: center; gap: 4px; padding: 10px 4px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 10px; color: var(--color-text-secondary); font-size: 11px; font-weight: 500; cursor: pointer; transition: all 0.2s ease; }
.style-btn:hover { background: rgba(255,255,255,0.06); color: var(--color-text-primary); }
.style-btn.active { background: rgba(124,58,237,0.15); border-color: rgba(124,58,237,0.4); color: #c4b5fd; }
.style-emoji { font-size: 18px; }
.size-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px; }
.size-btn { display: flex; flex-direction: column; align-items: center; gap: 6px; padding: 12px 4px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 10px; color: var(--color-text-secondary); font-size: 11px; font-weight: 500; cursor: pointer; transition: all 0.2s ease; }
.size-btn:hover { background: rgba(255,255,255,0.06); }
.size-btn.active { background: rgba(124,58,237,0.15); border-color: rgba(124,58,237,0.4); }

.generate-btn { width: 100%; padding: 14px; font-size: 16px; }
.generate-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.generate-btn :deep(svg) { width: 18px; height: 18px; }
.loading-spinner { width: 18px; height: 18px; border: 2px solid rgba(255,255,255,0.3); border-top-color: white; border-radius: 50%; animation: spin 0.6s linear infinite; display: inline-block; }
@keyframes spin { to { transform: rotate(360deg); } }

.balance-warning { display: flex; align-items: center; gap: 8px; padding: 10px 14px; background: rgba(239,68,68,0.08); border: 1px solid rgba(239,68,68,0.2); border-radius: 10px; font-size: 13px; color: #f87171; }
.balance-warning :deep(svg) { width: 16px; height: 16px; flex-shrink: 0; }

.generating-status { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 24px; min-height: 400px; padding: 48px; }
.gen-animation { position: relative; width: 100px; height: 100px; display: flex; align-items: center; justify-content: center; }
.gen-ring { position: absolute; width: 100px; height: 100px; border-radius: 50%; border: 2px solid transparent; border-top-color: #7c3aed; animation: rotate 1.5s linear infinite; }
.ring-2 { width: 80px; height: 80px; border-top-color: #06b6d4; animation-direction: reverse; animation-duration: 1s; }
@keyframes rotate { to { transform: rotate(360deg); } }
.gen-core { width: 50px; height: 50px; border-radius: 50%; background: var(--gradient-dream); display: flex; align-items: center; justify-content: center; color: white; box-shadow: 0 0 30px rgba(124,58,237,0.4); }
.gen-core :deep(svg) { width: 24px; height: 24px; }
.gen-title { font-size: 18px; font-weight: 700; }
.gen-sub { font-size: 13px; color: var(--color-text-muted); margin-top: 4px; }
.gen-progress-bar { width: 200px; height: 3px; background: rgba(255,255,255,0.06); border-radius: 9999px; overflow: hidden; }
.gen-progress-fill { height: 100%; background: var(--gradient-dream); border-radius: 9999px; animation: progress 3s ease forwards; }
@keyframes progress { 0% { width: 0%; } 100% { width: 100%; } }

.results-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.result-item { padding: 0; overflow: hidden; animation: fade-in-up 0.4s ease forwards; opacity: 0; }
@keyframes fade-in-up { 0% { opacity: 0; transform: translateY(20px); } 100% { opacity: 1; transform: translateY(0); } }
.result-image { position: relative; height: 240px; overflow: hidden; }
.result-image img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s ease; }
.result-item:hover .result-image img { transform: scale(1.05); }
.result-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; gap: 8px; opacity: 0; transition: opacity 0.2s ease; }
.result-item:hover .result-overlay { opacity: 1; }
.result-action { width: 40px; height: 40px; border-radius: 10px; background: rgba(255,255,255,0.15); backdrop-filter: blur(8px); border: 1px solid rgba(255,255,255,0.2); color: white; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.2s ease; }
.result-action:hover { background: rgba(124,58,237,0.5); transform: scale(1.1); }
.result-action :deep(svg) { width: 18px; height: 18px; }
.result-info { padding: 14px; }
.result-prompt { font-size: 13px; color: var(--color-text-secondary); line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin-bottom: 8px; }
.result-meta { display: flex; justify-content: space-between; align-items: center; }
.result-style { font-size: 11px; padding: 2px 8px; background: rgba(124,58,237,0.12); border-radius: 9999px; color: #a78bfa; }
.result-cost { font-size: 12px; color: #f87171; font-weight: 600; }

.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 400px; gap: 16px; text-align: center; padding: 48px; }
.empty-orb { width: 80px; height: 80px; opacity: 0.15; }
.empty-orb :deep(svg) { width: 80px; height: 80px; }
.empty-title { font-size: 18px; font-weight: 600; color: var(--color-text-secondary); }
.empty-sub { font-size: 14px; color: var(--color-text-muted); line-height: 1.7; }

@media (max-width: 900px) {
  .page-layout { grid-template-columns: 1fr; }
  .results-grid { grid-template-columns: 1fr; }
}
</style>