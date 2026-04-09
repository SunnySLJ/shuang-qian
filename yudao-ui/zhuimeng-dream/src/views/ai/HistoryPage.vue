<template>
  <div class="history-page" style="padding-top: 32px;">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">我的作品</h1>
        <div class="filter-tabs">
          <button v-for="tab in tabs" :key="tab.key" class="filter-tab" :class="{ active: activeTab === tab.key }" @click="activeTab = tab.key">
            {{ tab.label }}
          </button>
        </div>
      </div>

      <!-- 图片列表 -->
      <div v-if="activeTab === 'image'" class="works-grid">
        <div v-for="work in imageWorks" :key="work.id" class="work-card glass-card">
          <div class="work-image">
            <img :src="work.url" :alt="work.prompt" />
            <div class="work-overlay">
              <button class="action-btn" @click="download(work)">
                <span v-html="icons.download" class="icon-wrap" style="width: 18px; height: 18px;" />
              </button>
              <button class="action-btn">
                <span v-html="icons.copy" class="icon-wrap" style="width: 18px; height: 18px;" />
              </button>
            </div>
            <span class="work-type image-type">图片</span>
          </div>
          <div class="work-info">
            <p class="work-prompt">{{ work.prompt }}</p>
            <div class="work-meta">
              <span class="work-style">{{ work.style }}</span>
              <span class="work-cost">-{{ work.cost }}积分</span>
              <span class="work-time">{{ work.time }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 视频列表 -->
      <div v-if="activeTab === 'video'" class="works-grid">
        <div v-for="work in videoWorks" :key="work.id" class="work-card glass-card">
          <div class="work-image video-thumb">
            <img :src="work.url" :alt="work.title" />
            <button class="play-btn-center">
              <span v-html="icons.play" class="icon-wrap" style="width: 24px; height: 24px;" />
            </button>
            <span class="work-type video-type">视频</span>
            <span class="work-duration">{{ work.duration }}</span>
          </div>
          <div class="work-info">
            <p class="work-prompt">{{ work.title }}</p>
            <div class="work-meta">
              <span class="work-cost">-{{ work.cost }}积分</span>
              <span class="work-time">{{ work.time }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="(activeTab === 'image' && !imageWorks.length) || (activeTab === 'video' && !videoWorks.length)" class="empty-state">
        <div class="empty-icon">
          <span v-html="icons.history" class="icon-wrap" style="width: 64px; height: 64px;" />
        </div>
        <p class="empty-title">暂无{{ activeTab === 'image' ? '图片' : '视频' }}作品</p>
        <router-link :to="activeTab === 'image' ? '/ai/image' : '/ai/video'" class="btn-primary" style="margin-top: 16px;">去创作</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { cosmicIcons } from '@/utils/cosmicIcons'

const icons = cosmicIcons

const activeTab = ref<'image' | 'video'>('image')
const tabs = [
  { key: 'image' as const, label: '图片' },
  { key: 'video' as const, label: '视频' },
]

const imageWorks = [
  { id: 1, url: 'https://images.unsplash.com/photo-1480714378408-67cf0d13bc1b?w=600&q=80', prompt: '未来城市天际线，赛博朋克风格', style: '赛博朋克', cost: 2, time: '10分钟前' },
  { id: 2, url: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80', prompt: '赛博朋克霓虹女孩', style: '动漫风格', cost: 2, time: '1小时前' },
  { id: 3, url: 'https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?w=600&q=80', prompt: '梦幻星空动画', style: '魔幻奇幻', cost: 2, time: '2小时前' },
  { id: 4, url: 'https://images.unsplash.com/photo-1519681393784-d120267933ba?w=600&q=80', prompt: '中国山水画风格', style: '国风水墨', cost: 2, time: '3小时前' },
  { id: 5, url: 'https://images.unsplash.com/photo-1686191128892-3b37add4c844?w=600&q=80', prompt: '极简主义建筑设计', style: '极简主义', cost: 2, time: '昨天' },
  { id: 6, url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&q=80', prompt: '云雾缭绕山峰', style: '国风水墨', cost: 2, time: '昨天' },
]

const videoWorks = [
  { id: 1, url: 'https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?w=600&q=80', title: '梦幻星空动画', duration: '5秒', cost: 10, time: '2小时前' },
  { id: 2, url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&q=80', title: '山水意境国风短片', duration: '5秒', cost: 10, time: '昨天' },
]

function download(work: typeof imageWorks[0]) {
  window.open(work.url, '_blank')
}
</script>

<style scoped>
.icon-wrap { display: flex; align-items: center; justify-content: center; }
.icon-wrap :deep(svg) { width: 100%; height: 100%; }
.history-page { min-height: 100vh; background: var(--color-bg-base); }
.page-container { max-width: 1280px; margin: 0 auto; padding: 32px 24px 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px; }
.page-title { font-size: 24px; font-weight: 700; }
.filter-tabs { display: flex; gap: 4px; background: rgba(255,255,255,0.04); padding: 4px; border-radius: 10px; }
.filter-tab { padding: 8px 20px; border-radius: 8px; border: none; background: transparent; color: var(--color-text-muted); font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; }
.filter-tab.active { background: var(--gradient-dream); color: white; }
.works-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.work-card { padding: 0; overflow: hidden; }
.work-image { position: relative; height: 220px; overflow: hidden; }
.work-image img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s ease; }
.work-card:hover .work-image img { transform: scale(1.05); }
.work-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; gap: 8px; opacity: 0; transition: opacity 0.2s ease; }
.work-card:hover .work-overlay { opacity: 1; }
.action-btn { width: 40px; height: 40px; border-radius: 10px; background: rgba(255,255,255,0.15); backdrop-filter: blur(8px); border: 1px solid rgba(255,255,255,0.2); color: white; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.2s ease; }
.action-btn:hover { background: rgba(124,58,237,0.5); }
.work-type { position: absolute; top: 8px; right: 8px; font-size: 10px; font-weight: 600; padding: 2px 8px; border-radius: 9999px; }
.image-type { background: rgba(124,58,237,0.8); color: white; }
.video-type { background: rgba(6,182,212,0.8); color: white; }
.video-thumb { }
.play-btn-center { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 52px; height: 52px; border-radius: 50%; background: rgba(6,182,212,0.3); backdrop-filter: blur(8px); border: 2px solid rgba(255,255,255,0.3); display: flex; align-items: center; justify-content: center; cursor: pointer; }
.work-duration { position: absolute; bottom: 8px; right: 8px; padding: 2px 8px; background: rgba(0,0,0,0.6); border-radius: 4px; font-size: 11px; color: white; font-weight: 600; }
.work-info { padding: 14px; }
.work-prompt { font-size: 13px; color: var(--color-text-secondary); line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin-bottom: 8px; }
.work-meta { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.work-style { font-size: 11px; padding: 2px 8px; background: rgba(124,58,237,0.12); border-radius: 9999px; color: #a78bfa; }
.work-cost { font-size: 12px; color: #f87171; font-weight: 600; }
.work-time { font-size: 12px; color: var(--color-text-muted); margin-left: auto; }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 400px; gap: 16px; text-align: center; }
.empty-icon { color: rgba(255,255,255,0.08); }
.empty-title { font-size: 18px; font-weight: 600; color: var(--color-text-secondary); }
@media (max-width: 900px) { .works-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) { .works-grid { grid-template-columns: 1fr; } .page-header { flex-direction: column; gap: 16px; align-items: flex-start; } }
</style>
