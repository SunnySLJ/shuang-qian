<template>
  <div class="video-generate-page" style="padding-top: 32px;">
    <div class="page-layout">
      <!-- 左侧输入 -->
      <div class="input-panel glass-card">
        <div class="panel-header">
          <h2 class="panel-title">
            <span v-html="icons.video" class="icon-wrap" style="width: 20px; height: 20px;" />
            AI 视频生成
          </h2>
          <div class="panel-cost">
            <span v-html="icons.star" class="icon-wrap" style="width: 14px; height: 14px; color: #06b6d4;" />
            <span>{{ cost }} 积分/次</span>
          </div>
        </div>

        <div class="prompt-area">
          <textarea v-model="prompt" class="prompt-input" rows="5" placeholder="描述你想要的视频内容，比如：无人机航拍未来城市，霓虹灯光，夜晚，高达机器人穿梭于建筑之间..." />
          <div class="prompt-actions">
            <button class="btn-ghost" @click="prompt = ''">清空</button>
          </div>
        </div>

        <div class="section-label">时长</div>
        <div class="duration-grid">
          <button v-for="d in durations" :key="d.id" class="duration-btn" :class="{ active: selectedDuration === d.id }" @click="selectedDuration = d.id">
            <span>{{ d.label }}</span>
            <span class="duration-cost">{{ d.points }}积分</span>
          </button>
        </div>

        <button class="btn-primary generate-btn" :disabled="!prompt.trim() || generating" @click="generate" style="background: linear-gradient(135deg, #06b6d4, #22d3ee);">
          <span v-if="generating" class="loading-spinner" />
          <span v-else v-html="icons.play" class="icon-wrap" style="width: 20px; height: 20px;" />
          {{ generating ? '生成中...' : '立即生成视频' }}
        </button>

        <div v-if="!canAfford" class="balance-warning">
          积分不足，当前余额 {{ wallet.balance }}，需要 {{ cost }} 积分
          <router-link to="/recharge" class="btn-ghost" style="margin-left: auto; font-size: 12px; color: #a78bfa;">去充值 →</router-link>
        </div>
      </div>

      <!-- 右侧预览 -->
      <div class="result-panel">
        <!-- 视频预览 -->
        <div v-if="generating" class="generating-status glass-card">
          <div class="gen-animation">
            <div class="gen-ring" style="border-top-color: #06b6d4;" />
            <div class="gen-ring ring-2" style="border-top-color: #22d3ee;" />
            <div class="gen-core" style="background: linear-gradient(135deg, #06b6d4, #22d3ee); box-shadow: 0 0 30px rgba(6,182,212,0.4);">
              <span v-html="icons.video" class="icon-wrap" style="width: 24px; height: 24px; display: flex; color: white;" />
            </div>
          </div>
          <div class="gen-text">
            <div class="gen-title">视频生成中...</div>
            <div class="gen-sub">预计需要 30-60 秒，请稍候</div>
          </div>
          <div class="gen-progress-bar">
            <div class="gen-progress-fill" style="background: linear-gradient(90deg, #06b6d4, #22d3ee);" />
          </div>
        </div>

        <div v-else-if="videoResult" class="video-result glass-card">
          <div class="video-player">
            <img :src="videoResult.thumbnail" alt="视频封面" class="video-thumbnail" />
            <button class="play-btn">
              <span v-html="icons.play" class="icon-wrap" style="width: 32px; height: 32px; display: flex;" />
            </button>
            <div class="video-duration">{{ videoResult.duration }}</div>
          </div>
          <div class="video-info">
            <p class="video-prompt">{{ videoResult.prompt }}</p>
            <div class="video-meta">
              <span class="tag">-{{ videoResult.cost }}积分</span>
              <span class="tag tag-accent">{{ videoResult.duration }}</span>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <div class="empty-icon">
            <span v-html="icons.video" class="icon-wrap" style="width: 64px; height: 64px;" />
          </div>
          <p class="empty-title">还没有生成视频</p>
          <p class="empty-sub">输入描述词，AI 将为你生成精彩视频</p>
        </div>

        <!-- 历史视频 -->
        <div v-if="videoHistory.length > 0" class="video-history">
          <div class="history-header">最近视频</div>
          <div class="history-list">
            <div v-for="v in videoHistory" :key="v.id" class="history-item glass-card">
              <img :src="v.thumbnail" class="history-thumb" :alt="v.title" />
              <div class="history-info">
                <div class="history-title">{{ v.title }}</div>
                <div class="history-meta">{{ v.time }} · -{{ v.cost }}积分</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { cosmicIcons } from '@/utils/cosmicIcons'
import { useAppStore } from '@/stores/app'
import { storeToRefs } from 'pinia'

const icons = cosmicIcons

const store = useAppStore()
const route = useRoute()
const { wallet } = storeToRefs(store)
const { deductPoints } = store

const cost = 10
const prompt = ref(route.query.prompt as string || '')
const selectedDuration = ref('5s')
const generating = ref(false)
const videoResult = ref<{id: number; thumbnail: string; prompt: string; cost: number; duration: string} | null>(null)

const canAfford = computed(() => wallet.balance >= cost)

const durations = [
  { id: '5s', label: '5 秒', points: 10 },
  { id: '15s', label: '15 秒', points: 30 },
  { id: '30s', label: '30 秒', points: 60 },
]

const videoHistory = [
  { id: 1, title: '山水意境国风', thumbnail: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=200&q=80', time: '今天 14:23', cost: 10 },
  { id: 2, title: '城市光影流', thumbnail: 'https://images.unsplash.com/photo-1480714378408-67cf0d13bc1b?w=200&q=80', time: '昨天 21:30', cost: 10 },
]

async function generate() {
  if (!prompt.value.trim() || generating.value) return
  if (!canAfford.value) return

  generating.value = true
  deductPoints(cost)
  await new Promise(resolve => setTimeout(resolve, 4000))

  videoResult.value = {
    id: Date.now(),
    thumbnail: 'https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?w=800&q=80',
    prompt: prompt.value,
    cost,
    duration: selectedDuration.value,
  }
  generating.value = false
}
</script>

<style scoped>
.icon-wrap { display: flex; align-items: center; justify-content: center; }
.icon-wrap :deep(svg) { width: 100%; height: 100%; }
.video-generate-page { min-height: 100vh; background: var(--color-bg-base); }
.page-layout { display: grid; grid-template-columns: 420px 1fr; gap: 24px; max-width: 1400px; margin: 0 auto; padding: 32px 24px 24px; min-height: 100vh; }
.input-panel { height: fit-content; position: sticky; top: 96px; padding: 28px; display: flex; flex-direction: column; gap: 24px; }
.panel-header { display: flex; justify-content: space-between; align-items: center; }
.panel-title { display: flex; align-items: center; gap: 10px; font-size: 18px; font-weight: 700; }
.panel-cost { display: flex; align-items: center; gap: 4px; font-size: 13px; color: #22d3ee; font-weight: 600; }
.prompt-area { display: flex; flex-direction: column; gap: 8px; }
.prompt-input { width: 100%; padding: 14px 16px; background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08); border-radius: 12px; color: var(--color-text-primary); font-size: 14px; line-height: 1.7; resize: none; outline: none; font-family: inherit; }
.prompt-input:focus { border-color: rgba(6,182,212,0.4); background: rgba(6,182,212,0.05); box-shadow: 0 0 0 3px rgba(6,182,212,0.1); }
.prompt-actions { display: flex; justify-content: flex-end; }
.section-label { font-size: 13px; font-weight: 600; color: var(--color-text-secondary); margin-bottom: 10px; }
.duration-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
.duration-btn { display: flex; flex-direction: column; align-items: center; gap: 4px; padding: 14px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 10px; color: var(--color-text-secondary); font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; }
.duration-btn:hover { background: rgba(255,255,255,0.06); }
.duration-btn.active { background: rgba(6,182,212,0.12); border-color: rgba(6,182,212,0.4); color: #22d3ee; }
.duration-cost { font-size: 11px; color: var(--color-text-muted); font-weight: 400; }
.generate-btn { width: 100%; padding: 14px; font-size: 16px; }
.generate-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.loading-spinner { width: 18px; height: 18px; border: 2px solid rgba(255,255,255,0.3); border-top-color: white; border-radius: 50%; animation: spin 0.6s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.balance-warning { display: flex; align-items: center; gap: 8px; padding: 10px 14px; background: rgba(239,68,68,0.08); border: 1px solid rgba(239,68,68,0.2); border-radius: 10px; font-size: 13px; color: #f87171; }
.result-panel { padding-top: 0; display: flex; flex-direction: column; gap: 24px; }
.generating-status { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 24px; min-height: 400px; padding: 48px; }
.gen-animation { position: relative; width: 100px; height: 100px; display: flex; align-items: center; justify-content: center; }
.gen-ring { position: absolute; width: 100px; height: 100px; border-radius: 50%; border: 2px solid transparent; border-top-color: #06b6d4; animation: rotate 1.5s linear infinite; }
.ring-2 { width: 80px; height: 80px; animation-direction: reverse; animation-duration: 1s; }
@keyframes rotate { to { transform: rotate(360deg); } }
.gen-core { width: 50px; height: 50px; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.gen-title { font-size: 18px; font-weight: 700; }
.gen-sub { font-size: 13px; color: var(--color-text-muted); }
.gen-progress-bar { width: 200px; height: 3px; background: rgba(255,255,255,0.06); border-radius: 9999px; overflow: hidden; }
.gen-progress-fill { height: 100%; border-radius: 9999px; animation: progress 4s ease forwards; }
@keyframes progress { 0% { width: 0%; } 100% { width: 100%; } }
.video-result { padding: 0; overflow: hidden; }
.video-player { position: relative; height: 320px; overflow: hidden; }
.video-thumbnail { width: 100%; height: 100%; object-fit: cover; }
.play-btn { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 72px; height: 72px; border-radius: 50%; background: rgba(6,182,212,0.3); backdrop-filter: blur(8px); border: 2px solid rgba(255,255,255,0.3); display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.2s ease; }
.play-btn:hover { background: rgba(6,182,212,0.5); transform: translate(-50%, -50%) scale(1.1); }
.video-duration { position: absolute; bottom: 12px; right: 12px; padding: 4px 10px; background: rgba(0,0,0,0.6); border-radius: 6px; font-size: 12px; color: white; font-weight: 600; }
.video-info { padding: 16px; }
.video-prompt { font-size: 14px; color: var(--color-text-secondary); line-height: 1.5; margin-bottom: 10px; }
.video-meta { display: flex; gap: 8px; }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 400px; gap: 16px; text-align: center; }
.empty-icon { color: rgba(255,255,255,0.08); }
.empty-title { font-size: 18px; font-weight: 600; color: var(--color-text-secondary); }
.empty-sub { font-size: 14px; color: var(--color-text-muted); }
.video-history { margin-top: 16px; }
.history-header { font-size: 14px; font-weight: 600; color: var(--color-text-secondary); margin-bottom: 12px; }
.history-list { display: flex; flex-direction: column; gap: 8px; }
.history-item { display: flex; align-items: center; gap: 12px; padding: 12px; }
.history-thumb { width: 80px; height: 50px; border-radius: 8px; object-fit: cover; flex-shrink: 0; }
.history-title { font-size: 13px; font-weight: 600; color: var(--color-text-primary); }
.history-meta { font-size: 11px; color: var(--color-text-muted); margin-top: 2px; }
@media (max-width: 900px) {
  .page-layout { grid-template-columns: 1fr; padding: 32px 16px 16px; }
  .input-panel { position: static; }
}
</style>
