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

      <div v-if="loading" class="empty-state">
        <div class="empty-title">加载中...</div>
      </div>

      <template v-else>
        <div v-if="activeTab === 'image'" class="works-grid">
          <div v-for="work in imageWorks" :key="work.id" class="work-card glass-card">
            <div class="work-image">
              <img :src="work.url" :alt="work.prompt" />
              <div class="work-overlay">
                <button class="action-btn" @click="download(work.url)">
                  <span v-html="icons.download" class="icon-wrap" style="width: 18px; height: 18px;" />
                </button>
                <button class="action-btn" @click="copyText(work.prompt)">
                  <span v-html="icons.copy" class="icon-wrap" style="width: 18px; height: 18px;" />
                </button>
                <button v-if="work.canRetry" class="action-btn" @click="retryImage(work.prompt)">
                  <span v-html="icons.refresh" class="icon-wrap" style="width: 18px; height: 18px;" />
                </button>
              </div>
              <span class="work-type image-type">图片</span>
              <span class="work-status" :class="work.statusClass">{{ work.statusText }}</span>
            </div>
            <div class="work-info">
              <p class="work-prompt">{{ work.prompt }}</p>
              <div class="work-meta">
                <span class="work-style">{{ work.size }}</span>
                <span class="work-time">{{ work.time }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'video'" class="works-grid">
          <div v-for="work in videoWorks" :key="work.id" class="work-card glass-card">
            <div class="work-image video-thumb">
              <img v-if="work.previewType === 'image'" :src="work.previewUrl" :alt="work.prompt" />
              <video
                v-else-if="work.previewType === 'video'"
                :src="work.previewUrl"
                class="work-video-preview"
                muted
                playsinline
                preload="metadata"
              />
              <div v-else class="work-video-empty">
                <span v-html="icons.video" class="icon-wrap" style="width: 42px; height: 42px;" />
              </div>
              <button class="play-btn-center" :disabled="!work.playableUrl" @click="download(work.playableUrl)">
                <span v-html="icons.play" class="icon-wrap" style="width: 24px; height: 24px;" />
              </button>
              <button v-if="work.canRetry" class="action-btn retry-btn" @click="retryVideo(work.prompt)">
                <span v-html="icons.refresh" class="icon-wrap" style="width: 18px; height: 18px;" />
              </button>
              <span class="work-type video-type">视频</span>
              <span class="work-duration">{{ work.duration }}</span>
              <span class="work-status" :class="work.statusClass">{{ work.statusText }}</span>
            </div>
            <div class="work-info">
              <p class="work-prompt">{{ work.prompt }}</p>
              <div class="work-meta">
                <span class="work-style">{{ work.bizLabel }}</span>
                <span class="work-style">{{ work.modelHint }}</span>
                <span class="work-time">{{ work.time }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="(activeTab === 'image' && !imageWorks.length) || (activeTab === 'video' && !videoWorks.length)" class="empty-state">
          <div class="empty-icon">
            <span v-html="icons.history" class="icon-wrap" style="width: 64px; height: 64px;" />
          </div>
          <p class="empty-title">暂无{{ activeTab === 'image' ? '图片' : '视频' }}作品</p>
          <router-link :to="activeTab === 'image' ? '/ai/image' : '/ai/video'" class="btn-primary" style="margin-top: 16px;">去创作</router-link>
        </div>

        <div v-if="(activeTab === 'image' && hasMoreImages) || (activeTab === 'video' && hasMoreVideos)" class="load-more-wrap">
          <button class="btn-ghost" :disabled="loadingMore" @click="loadMore">
            {{ loadingMore ? '加载中...' : '加载更多' }}
          </button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { cosmicIcons } from '@/utils/cosmicIcons'
import { getMyImageList, type ImageRecord } from '@/api/ai/image'
import { getVideoPage, type VideoRecord } from '@/api/ai/video'
import { toast } from '@/utils/toast'

const icons = cosmicIcons
const router = useRouter()

const activeTab = ref<'image' | 'video'>('image')
const loading = ref(false)
const loadingMore = ref(false)
const imagePageNo = ref(1)
const videoPageNo = ref(1)
const pageSize = 12
const hasMoreImages = ref(true)
const hasMoreVideos = ref(true)
const tabs = [
  { key: 'image' as const, label: '图片' },
  { key: 'video' as const, label: '视频' },
]

const imageRecords = ref<ImageRecord[]>([])
const videoRecords = ref<VideoRecord[]>([])

const imageWorks = computed(() => imageRecords.value.map((record) => ({
  id: record.id,
  url: record.outputUrl || record.thumbnailUrl || '',
  prompt: record.prompt,
  size: record.width && record.height ? `${record.width}x${record.height}` : '图片作品',
  time: formatTime(record.createTime),
  statusText: statusText(record.status),
  statusClass: statusClass(record.status),
  canRetry: record.status === 30,
})))

const videoWorks = computed(() => videoRecords.value.map((record) => ({
  previewType: resolveVideoPreview(record).type,
  previewUrl: resolveVideoPreview(record).url,
  id: record.id,
  url: record.coverUrl || record.picUrl || record.thumbnailUrl || '',
  videoUrl: record.outputUrl || record.inputVideoUrl,
  playableUrl: record.outputUrl || record.inputVideoUrl || '',
  prompt: record.prompt,
  bizLabel: generationTypeLabel(record.generationType),
  duration: record.duration ? `${record.duration}秒` : statusMeta(record.status).durationText,
  time: formatTime(record.createTime),
  modelHint: statusMeta(record.status, !!record.outputUrl).hintText,
  statusText: statusText(record.status),
  statusClass: statusClass(record.status),
  canRetry: record.status === 30,
})))

function resolveVideoPreview(record: VideoRecord): { type: 'image' | 'video' | 'empty'; url: string } {
  const imageUrl = record.coverUrl || record.picUrl || record.thumbnailUrl || ''
  if (imageUrl) {
    return { type: 'image', url: imageUrl }
  }
  if (record.outputUrl) {
    return { type: 'video', url: record.outputUrl }
  }
  if (record.inputVideoUrl) {
    return { type: 'video', url: record.inputVideoUrl }
  }
  return { type: 'empty', url: '' }
}

async function loadImages(reset: boolean = false) {
  if (reset) {
    imagePageNo.value = 1
    hasMoreImages.value = true
  }
  const list = await getMyImageList(imagePageNo.value * pageSize)
  imageRecords.value = list
  hasMoreImages.value = list.length >= imagePageNo.value * pageSize
}

async function loadVideos(reset: boolean = false) {
  if (reset) {
    videoPageNo.value = 1
    hasMoreVideos.value = true
  }
  const page = await getVideoPage({ pageNo: 1, pageSize: videoPageNo.value * pageSize })
  videoRecords.value = page.list
  hasMoreVideos.value = page.list.length < page.total
}

async function loadCurrentTab(reset: boolean = true) {
  loading.value = true
  try {
    if (activeTab.value === 'image') {
      await loadImages(reset)
    } else {
      await loadVideos(reset)
    }
  } catch (error) {
    toast.error(error instanceof Error ? error.message : '加载历史失败')
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  loadingMore.value = true
  try {
    if (activeTab.value === 'image') {
      imagePageNo.value += 1
      await loadImages()
    } else {
      videoPageNo.value += 1
      await loadVideos()
    }
  } catch (error) {
    toast.error(error instanceof Error ? error.message : '加载更多失败')
  } finally {
    loadingMore.value = false
  }
}

function formatTime(time?: string) {
  if (!time) return '刚刚'
  return time.replace('T', ' ').slice(0, 16)
}

function statusText(status: number) {
  if (status === 20) return '已完成'
  if (status === 30) return '失败'
  return '处理中'
}

function statusClass(status: number) {
  if (status === 20) return 'success'
  if (status === 30) return 'fail'
  return 'pending'
}

function statusMeta(status: number, hasOutputUrl: boolean = false) {
  if (status === 20) {
    return {
      hintText: hasOutputUrl ? '已生成' : '已完成，文件同步中',
      durationText: '已完成',
    }
  }
  if (status === 30) {
    return {
      hintText: '生成失败',
      durationText: '失败',
    }
  }
  return {
    hintText: '处理中',
    durationText: '处理中',
  }
}

function generationTypeLabel(generationType?: number) {
  switch (generationType) {
    case -2:
      return '文生视频'
    case -3:
      return '图生视频'
    case -4:
      return '黄金6秒'
    case -5:
      return 'AI混剪'
    case -6:
      return '提取脚本'
    case -7:
      return '分析元素'
    case -8:
      return '生成提示词'
    default:
      return '视频任务'
  }
}

function download(url: string) {
  if (!url) {
    toast.warning('当前还没有可播放文件')
    return
  }
  window.open(url, '_blank')
}

async function copyText(text: string) {
  if (!text) return
  await navigator.clipboard.writeText(text)
  toast.success('已复制 Prompt')
}

function retryImage(prompt: string) {
  router.push({ path: '/ai/image', query: { prompt: prompt.slice(0, 2000) } })
}

function retryVideo(prompt: string) {
  router.push({ path: '/ai/video', query: { prompt: prompt.slice(0, 2000) } })
}

watch(activeTab, () => {
  loadCurrentTab(true)
})

onMounted(() => {
  loadCurrentTab(true)
})
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
.work-image { position: relative; height: 220px; overflow: hidden; background: rgba(255,255,255,0.03); }
.work-image img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s ease; }
.work-video-preview { width: 100%; height: 100%; object-fit: cover; background: rgba(255,255,255,0.03); }
.work-video-empty { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; color: rgba(255,255,255,0.2); }
.work-card:hover .work-image img { transform: scale(1.05); }
.work-card:hover .work-video-preview { transform: scale(1.05); }
.work-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; gap: 8px; opacity: 0; transition: opacity 0.2s ease; }
.work-card:hover .work-overlay { opacity: 1; }
.action-btn { width: 40px; height: 40px; border-radius: 10px; background: rgba(255,255,255,0.15); backdrop-filter: blur(8px); border: 1px solid rgba(255,255,255,0.2); color: white; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.2s ease; }
.action-btn:hover { background: rgba(124,58,237,0.5); }
.work-type { position: absolute; top: 8px; right: 8px; font-size: 10px; font-weight: 600; padding: 2px 8px; border-radius: 9999px; }
.work-status { position: absolute; top: 8px; left: 8px; font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 9999px; }
.work-status.success { background: rgba(16,185,129,0.8); color: white; }
.work-status.fail { background: rgba(239,68,68,0.85); color: white; }
.work-status.pending { background: rgba(245,158,11,0.85); color: white; }
.image-type { background: rgba(124,58,237,0.8); color: white; }
.video-type { background: rgba(6,182,212,0.8); color: white; }
.play-btn-center { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 52px; height: 52px; border-radius: 50%; background: rgba(6,182,212,0.3); backdrop-filter: blur(8px); border: 2px solid rgba(255,255,255,0.3); display: flex; align-items: center; justify-content: center; cursor: pointer; }
.play-btn-center:disabled { opacity: 0.55; cursor: not-allowed; }
.retry-btn { position: absolute; bottom: 8px; left: 8px; z-index: 2; }
.work-duration { position: absolute; bottom: 8px; right: 8px; padding: 2px 8px; background: rgba(0,0,0,0.6); border-radius: 4px; font-size: 11px; color: white; font-weight: 600; }
.work-info { padding: 14px; }
.work-prompt { font-size: 13px; color: var(--color-text-secondary); line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin-bottom: 8px; }
.work-meta { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.work-style { font-size: 11px; padding: 2px 8px; background: rgba(124,58,237,0.12); border-radius: 9999px; color: #a78bfa; }
.work-time { font-size: 12px; color: var(--color-text-muted); margin-left: auto; }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 400px; gap: 16px; text-align: center; }
.empty-icon { color: rgba(255,255,255,0.08); }
.empty-title { font-size: 18px; font-weight: 600; color: var(--color-text-secondary); }
.load-more-wrap { display: flex; justify-content: center; margin-top: 24px; }
@media (max-width: 900px) { .works-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) { .works-grid { grid-template-columns: 1fr; } .page-header { flex-direction: column; gap: 16px; align-items: flex-start; } }
</style>
