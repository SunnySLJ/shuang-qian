<template>
  <div class="hot-dissect-page">
    <!-- 顶栏：与参考图一致 -->
    <header class="page-head">
      <h1 class="page-head-title">一键拆解视频</h1>
      <div class="page-head-search">
        <span class="search-ico" v-html="icons.search" />
        <input
          v-model="keywordFilter"
          type="search"
          class="search-input"
          placeholder="搜索关键字"
          autocomplete="off"
        />
      </div>
    </header>

    <div class="main-split">
      <!-- 左：预览 + 免责 + 立即生成 -->
      <section class="col-left">
        <div class="preview-shell">
          <div
            class="preview-frame"
            :class="{ 'has-video': !!previewSrc }"
            @click="onPreviewClick"
          >
            <video
              v-if="previewSrc"
              :src="previewSrc"
              class="preview-video"
              controls
              playsinline
              preload="metadata"
            />
            <div v-else class="preview-placeholder">
              <span v-html="icons.upload" class="ph-icon" />
              <p>点击添加视频</p>
              <p class="ph-sub">支持本地上传或粘贴直链</p>
            </div>
          </div>

          <div class="preview-foot">
            <p class="disclaimer">
              注：智能生成的内容仅供参考，不代表平台立场
            </p>
            <button
              type="button"
              class="btn-gen"
              :disabled="!canGenerate || analyzing"
              @click="startAnalyze"
            >
              <span v-if="analyzing" class="spin" />
              <template v-else>立即生成</template>
              <span class="btn-cost" aria-hidden="true">
                <svg class="hex" viewBox="0 0 24 24" width="14" height="14" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M12 2l8 4.5v11L12 22l-8-4.5v-11L12 2z" stroke="currentColor" stroke-width="1.2" opacity="0.9"/>
                </svg>
                {{ cost }}点/次
              </span>
            </button>
          </div>
        </div>

        <!-- 素材来源 -->
        <div class="source-card">
          <div class="source-tabs">
            <button
              type="button"
              class="st"
              :class="{ on: sourceType === 'upload' }"
              @click="sourceType = 'upload'"
            >
              本地上传
            </button>
            <button
              type="button"
              class="st"
              :class="{ on: sourceType === 'link' }"
              @click="sourceType = 'link'"
            >
              视频链接
            </button>
          </div>
          <div v-if="sourceType === 'link'" class="source-body">
            <input
              v-model="videoInput"
              class="url-field"
              type="text"
              placeholder="粘贴视频分享文案、分享链接或直链"
            />
            <div class="link-actions">
              <button type="button" class="btn-add" :disabled="resolvingLink || !videoInput.trim()" @click="resolveSharedLink">
                <span v-if="resolvingLink" class="spin tiny-spin" />
                <template v-else>解析链接</template>
              </button>
              <span v-if="resolvedPlatform" class="resolved-tag">{{ resolvedPlatform }}</span>
            </div>
            <div v-if="extractedUrl || resolvedPageUrl || resolvedVideoUrl" class="resolved-meta">
              <p v-if="extractedUrl"><span>提取链接：</span>{{ extractedUrl }}</p>
              <p v-if="resolvedPageUrl"><span>落地页面：</span>{{ resolvedPageUrl }}</p>
              <p v-if="resolvedVideoUrl"><span>可用直链：</span>{{ resolvedVideoUrl }}</p>
            </div>
            <p v-if="resolvedVideoUrl" class="resolved-tip">已解析可用视频链接，可预览后再点击“立即生成”。</p>
            <p v-else class="resolved-tip">支持抖音、小红书、快手等分享文案，系统会先提取有效链接。</p>
          </div>
          <div v-else class="source-body">
            <input
              ref="fileInputRef"
              type="file"
              accept="video/mp4,video/quicktime,video/webm,.mp4,.mov,.webm"
              class="sr-only"
              @change="handleFileChange"
            />
            <button type="button" class="btn-add" @click="triggerFileInput">
              <span v-html="icons.upload" />
              {{ localFile ? '重新选择' : '选择文件' }}
            </button>
            <span v-if="localFile" class="file-name">{{ localFile.name }}</span>
          </div>
        </div>

        <div v-if="!canAfford" class="warn-box">
          <span v-html="icons.warning" />
          积分不足（需 {{ cost }} 点）
          <router-link to="/recharge" class="link-r">去充值</router-link>
        </div>
        <div v-if="analyzeError" class="err-box">
          <span v-html="icons.warning" />
          {{ analyzeError }}
        </div>
      </section>

      <!-- 右：说明 + 流程图 + 结果 -->
      <section class="col-right">
        <h2 class="right-title">一键拆解视频</h2>
        <p class="formula">
          上传视频素材 = <span class="pink">拆解结果</span>
        </p>

        <div class="workflow">
          <div class="wf-card">
            <div class="wf-thumb thumb-a" />
            <span class="wf-cap">添加视频</span>
          </div>
          <div class="wf-arrow" aria-hidden="true">➜</div>
          <div class="wf-card wf-card-wide">
            <div class="wf-thumb thumb-b" />
            <span class="wf-cap">生成爆款提示与结构拆解</span>
            <div class="wf-detail">
              <p class="wf-detail-title">解析角度包括：</p>
              <p class="wf-detail-text">
                视频内容、视频主题、场景、构图/运镜、产品介绍、情绪/氛围、字幕/内容、镜头拆解等
              </p>
            </div>
          </div>
        </div>

        <div v-if="analyzing" class="state-block loading">
          <div class="ring-wrap">
            <div class="ring" />
            <div class="ring r2" />
          </div>
          <p class="state-title">正在拆解分析…</p>
          <p class="state-sub">{{ progress }}%</p>
          <div class="bar">
            <div class="bar-in" :style="{ width: progress + '%' }" />
          </div>
        </div>

        <div v-else-if="resultDisplay" class="state-block out glass-card">
          <div class="out-head">
            <span v-html="icons.check" class="ok-ico" />
            <span>拆解完成</span>
          </div>
          <div class="result-section">
            <div class="result-section-label">原始文案</div>
            <div class="out-body scroll-y raw-body">
              {{ rawPromptDisplay }}
            </div>
          </div>
          <div class="result-section">
            <div class="result-section-label">拆解结果</div>
          <div class="out-body scroll-y">
            {{ resultDisplay }}
          </div>
          </div>
          <div class="out-actions">
            <button type="button" class="oa" @click="copyOut">
              <span v-html="icons.copy" /> 复制全文
            </button>
            <button type="button" class="oa" :disabled="optimizingPrompt" @click="optimizePromptFromResult">
              <span v-if="optimizingPrompt" class="spin tiny-spin" />
              <template v-else><span v-html="icons.sparkle" /> 优化提示词</template>
            </button>
            <button type="button" class="oa primary" @click="goImage">
              <span v-html="icons.image" /> 去 AI 做图
            </button>
            <button type="button" class="oa primary" @click="goVideo">
              <span v-html="icons.video" /> 去 AI 视频
            </button>
          </div>
          <div v-if="optimizedPrompt" class="optimized-panel">
            <div class="optimized-head">
              <div class="optimized-title">成品 Prompt</div>
              <button type="button" class="oa" @click="copyOptimizedPrompt">
                <span v-html="icons.copy" /> 复制成品
              </button>
            </div>
            <div class="optimized-grid">
              <div class="optimized-item">
                <div class="optimized-label">开头钩子</div>
                <div class="optimized-text">{{ optimizedPrompt.hook || '暂无' }}</div>
              </div>
              <div class="optimized-item">
                <div class="optimized-label">目标人群</div>
                <div class="optimized-text">{{ optimizedPrompt.targetAudience || '暂无' }}</div>
              </div>
              <div class="optimized-item">
                <div class="optimized-label">镜头拆解</div>
                <div class="optimized-text">{{ optimizedPrompt.shotBreakdown || '暂无' }}</div>
              </div>
              <div class="optimized-item">
                <div class="optimized-label">文案策略</div>
                <div class="optimized-text">{{ optimizedPrompt.copyStrategy || '暂无' }}</div>
              </div>
            </div>
            <div class="optimized-output">
              <div class="optimized-label">中文成品 Prompt</div>
              <pre class="optimized-pre">{{ optimizedPrompt.fullPromptZh }}</pre>
            </div>
          </div>
        </div>

        <div v-else class="state-block hint">
          <p>添加左侧视频后，点击「立即生成」，将按右侧解析维度输出结构化拆解文案。</p>
        </div>
      </section>
    </div>

    <transition name="toast">
      <div v-if="showToast" class="toast">
        <span v-html="icons.check" />
        {{ toastMessage }}
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { cosmicIcons } from '@/utils/cosmicIcons'
import { useAppStore } from '@/stores/app'
import { analyzeElements, getVideoDetail, resolveVideoLink, type VideoAnalyzeResult } from '@/api/ai/video'
import { optimizeHotVideoPrompt, type PromptOptimizeResult } from '@/api/ai/prompt'
import request from '@/utils/request'

const icons = cosmicIcons
const store = useAppStore()
const router = useRouter()

const cost = 5
const keywordFilter = ref('')
const sourceType = ref<'link' | 'upload'>('upload')
const videoInput = ref('')
const resolvedVideoUrl = ref('')
const resolvedPlatform = ref('')
const extractedUrl = ref('')
const resolvedPageUrl = ref('')
const resolvingLink = ref(false)
const fileInputRef = ref<HTMLInputElement | null>(null)
const localFile = ref<File | null>(null)
const objectUrl = ref<string | null>(null)
const uploading = ref(false)
const analyzing = ref(false)
const progress = ref(0)
const result = ref<VideoAnalyzeResult | null>(null)
const analyzeError = ref('')
const showToast = ref(false)
const toastMessage = ref('')
const optimizingPrompt = ref(false)
const optimizedPrompt = ref<PromptOptimizeResult | null>(null)

const wallet = computed(() => store.wallet)
const analyzeProvider = 'doubao'

const previewSrc = computed(() => {
  if (sourceType.value === 'link' && resolvedVideoUrl.value.trim()) {
    return resolvedVideoUrl.value.trim()
  }
  if (sourceType.value === 'upload' && objectUrl.value) {
    return objectUrl.value
  }
  return ''
})

watch([localFile, sourceType], () => {
  if (objectUrl.value) {
    URL.revokeObjectURL(objectUrl.value)
    objectUrl.value = null
  }
  if (sourceType.value === 'upload' && localFile.value) {
    objectUrl.value = URL.createObjectURL(localFile.value)
  }
})

watch(videoInput, () => {
  resolvedVideoUrl.value = ''
  resolvedPlatform.value = ''
  extractedUrl.value = ''
  resolvedPageUrl.value = ''
})

onUnmounted(() => {
  if (objectUrl.value) URL.revokeObjectURL(objectUrl.value)
})

const canAfford = computed(() => (wallet.value?.balance ?? 0) >= cost)

const canGenerate = computed(() => {
  if (uploading.value) return false
  if (sourceType.value === 'link') return !!resolvedVideoUrl.value.trim()
  return !!localFile.value
})

function triggerFileInput() {
  fileInputRef.value?.click()
}

function onPreviewClick() {
  if (!previewSrc.value) triggerFileInput()
}

function handleFileChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (file && file.type.startsWith('video/')) {
    localFile.value = file
  }
}

async function uploadFile(file: File): Promise<string> {
  const formData = new FormData()
  formData.append('file', file)
  uploading.value = true
  try {
    const response = await request.post<unknown>('/infra/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    const url = typeof response === 'string'
      ? response
      : (response && typeof response === 'object'
        ? String((response as Record<string, unknown>).url || (response as Record<string, unknown>).data || '')
        : '')
    if (!url) throw new Error('上传成功但未获取到文件地址')
    uploading.value = false
    return url
  } catch {
    uploading.value = false
    throw new Error('上传失败，请重试')
  }
}

/** 从 bizOptions / 旧字段拼出展示文案 */
function resultTextFrom(res: VideoAnalyzeResult | null): string {
  if (!res) return ''
  const raw =
    (res.bizOptions?.result as string) ||
    (res.options?.result as string) ||
    res.outputUrl ||
    res.prompt ||
    ''
  if (typeof raw === 'string' && /^https?:\/\//i.test(raw.trim())) {
    return `分析结果链接：${raw}\n（若为文本结果，请打开链接查看或等待平台对接纯文本返回）`
  }
  return String(raw)
}

const resultDisplay = computed(() => {
  if (!result.value) return ''
  const t = resultTextFrom(result.value)
  const k = keywordFilter.value.trim()
  if (!k) return t
  const lines = t.split('\n').filter((line) => line.includes(k))
  return lines.length ? lines.join('\n') : '无匹配行，可调整搜索关键字'
})

const rawPromptDisplay = computed(() => {
  if (optimizedPrompt.value?.originalPrompt) {
    return optimizedPrompt.value.originalPrompt
  }
  return resultTextFrom(result.value)
})

async function startAnalyze() {
  if (!canGenerate.value || analyzing.value || !canAfford.value) return
  analyzing.value = true
  analyzeError.value = ''
  result.value = null
  optimizedPrompt.value = null
  progress.value = 0

  try {
    let url = resolvedVideoUrl.value.trim()
    if (sourceType.value === 'upload' && localFile.value) {
      url = await uploadFile(localFile.value)
    }
    if (sourceType.value === 'link' && !url && videoInput.value.trim()) {
      await resolveSharedLink()
      url = resolvedVideoUrl.value.trim()
    }
    if (!url) {
      throw new Error('请先解析出可用的视频链接，再进行拆解')
    }

    const recordId = await analyzeElements({
      videoUrl: url,
      provider: analyzeProvider,
      clientId: `video-analyze-${Date.now()}-${Math.random().toString(36).slice(2, 10)}`,
    })
    await pollResult(recordId)
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : '拆解失败'
    analyzeError.value = msg
    analyzing.value = false
  }
}

async function resolveSharedLink() {
  if (!videoInput.value.trim() || resolvingLink.value) return
  resolvingLink.value = true
  analyzeError.value = ''
  try {
    const data = await resolveVideoLink(videoInput.value.trim())
    extractedUrl.value = data.extractedUrl || ''
    resolvedPageUrl.value = data.resolvedPageUrl || ''
    resolvedPlatform.value = data.platform || ''
    resolvedVideoUrl.value = data.previewVideoUrl || ''
    if (!data.previewVideoUrl) {
      throw new Error('已识别分享链接，但暂时没提取到可预览的视频直链')
    }
    showToastMsg('链接解析成功')
  } catch (e: unknown) {
    resolvedVideoUrl.value = ''
    const msg = e instanceof Error ? e.message : '链接解析失败'
    analyzeError.value = msg
  } finally {
    resolvingLink.value = false
  }
}

async function pollResult(recordId: number) {
  const MAX_WAIT = 120000
  const INTERVAL = 2500
  let elapsed = 0

  return new Promise<void>((resolve, reject) => {
    const timer = setInterval(async () => {
      elapsed += INTERVAL
      progress.value = Math.min(92, Math.round((elapsed / MAX_WAIT) * 100))
      try {
        const data = await getVideoDetail(recordId)
        // 与 AiImageStatusEnum 一致：10 进行中，20 成功，30 失败
        if (data.status === 20) {
          progress.value = 100
          result.value = data as unknown as VideoAnalyzeResult
          analyzing.value = false
          clearInterval(timer)
          resolve()
        } else if (data.status === 30) {
          analyzing.value = false
          analyzeError.value = data.errorMessage || '拆解失败'
          clearInterval(timer)
          reject(new Error(analyzeError.value))
        } else if (elapsed >= MAX_WAIT) {
          analyzing.value = false
          analyzeError.value = '处理超时，请稍后在「我的作品」中查看或重试'
          clearInterval(timer)
          reject(new Error('timeout'))
        }
      } catch {
        clearInterval(timer)
        analyzing.value = false
        reject(new Error('查询结果失败'))
      }
    }, INTERVAL)
  })
}

function copyOut() {
  const t = resultTextFrom(result.value)
  if (!t) return
  navigator.clipboard.writeText(t).then(() => showToastMsg('已复制'))
}

async function optimizePromptFromResult() {
  const t = resultTextFrom(result.value)
  if (!t || optimizingPrompt.value) return
  optimizingPrompt.value = true
  try {
    const industry = inferIndustry(t)
    const platform = inferPlatform(previewSrc.value || videoInput.value)
    optimizedPrompt.value = await optimizeHotVideoPrompt({
      rawPrompt: t.slice(0, 4000),
      industry,
      platform,
      targetModel: '豆包',
    })
    showToastMsg(`提示词已优化${industry ? `：${industry}` : ''}`)
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : '提示词优化失败'
    showToastMsg(msg)
  } finally {
    optimizingPrompt.value = false
  }
}

function copyOptimizedPrompt() {
  const t = optimizedPrompt.value?.fullPromptZh
  if (!t) return
  navigator.clipboard.writeText(t).then(() => showToastMsg('已复制成品 Prompt'))
}

function goImage() {
  const t = optimizedPrompt.value?.fullPromptZh || resultTextFrom(result.value)
  if (t) router.push({ path: '/ai/image', query: { prompt: t.slice(0, 2000) } })
}

function goVideo() {
  const t = optimizedPrompt.value?.fullPromptZh || resultTextFrom(result.value)
  if (t) router.push({ path: '/ai/video', query: { prompt: t.slice(0, 2000) } })
}

function showToastMsg(msg: string) {
  toastMessage.value = msg
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 2000)
}

function inferPlatform(urlText: string) {
  const text = (urlText || '').toLowerCase()
  if (text.includes('douyin')) return '抖音'
  if (text.includes('xiaohongshu') || text.includes('xhslink')) return '小红书'
  if (text.includes('kuaishou')) return '快手'
  if (text.includes('bilibili') || text.includes('b23.tv')) return 'B站'
  if (text.includes('weixin') || text.includes('channels.weixin')) return '视频号'
  return '抖音'
}

function inferIndustry(content: string) {
  const text = (content || '').toLowerCase()
  if (hasAny(text, ['探店', '餐厅', '菜品', '美食', '火锅', '奶茶', '小吃', '饭店'])) {
    return '美食-探店视频'
  }
  if (hasAny(text, ['减脂', '瘦身', '代餐', '热量', '控糖', '营养', '胶原蛋白', '保健'])) {
    return '大健康-产品介绍'
  }
  if (hasAny(text, ['搞笑', '整活', '反转', '爆笑', '段子'])) {
    return '娱乐-搞笑视频'
  }
  if (hasAny(text, ['剧情', '对白', '男女主', '反派', '冲突', '短剧'])) {
    return '短剧-剧情片段'
  }
  if (hasAny(text, ['口红', '粉底', '护肤', '穿搭', '服饰', '面膜'])) {
    return '美妆-产品展示'
  }
  if (hasAny(text, ['产品', '卖点', '下单', '转化', '优惠', '限时', '爆款', '种草'])) {
    return '电商-促销广告'
  }
  return '电商-产品展示'
}

function hasAny(text: string, keywords: string[]) {
  return keywords.some((keyword) => text.includes(keyword))
}
</script>

<style scoped>
.hot-dissect-page {
  min-height: 100%;
  padding: 20px 24px 40px;
  max-width: 1280px;
  margin: 0 auto;
}

.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.page-head-title {
  font-size: 18px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.92);
  letter-spacing: 0.02em;
}

.page-head-search {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 14px;
  border-radius: 9999px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
  min-width: 220px;
}

.search-ico :deep(svg) {
  width: 16px;
  height: 16px;
  opacity: 0.5;
}

.search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: rgba(255, 255, 255, 0.85);
  font-size: 13px;
}

.search-input::placeholder {
  color: rgba(255, 255, 255, 0.35);
}

.main-split {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 28px;
  align-items: start;
}

@media (max-width: 960px) {
  .main-split {
    grid-template-columns: 1fr;
  }
}

.col-left {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.preview-shell {
  background: linear-gradient(145deg, rgba(30, 30, 36, 0.95), rgba(18, 18, 22, 0.98));
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 16px;
  overflow: hidden;
}

.preview-frame {
  aspect-ratio: 9 / 16;
  max-height: min(62vh, 520px);
  margin: 0 auto;
  width: 100%;
  max-width: 320px;
  background: #0c0c0f;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.preview-frame.has-video {
  cursor: default;
}

.preview-video {
  width: 100%;
  height: 100%;
  object-fit: contain;
  vertical-align: middle;
}

.preview-placeholder {
  text-align: center;
  padding: 24px;
  color: rgba(255, 255, 255, 0.45);
}

.ph-icon :deep(svg) {
  width: 40px;
  height: 40px;
  opacity: 0.35;
  margin-bottom: 8px;
}

.preview-placeholder p {
  margin: 0;
  font-size: 14px;
}

.ph-sub {
  font-size: 12px !important;
  margin-top: 6px !important;
  opacity: 0.7;
}

.preview-foot {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px 16px;
  flex-wrap: wrap;
}

.disclaimer {
  flex: 1;
  min-width: 180px;
  font-size: 11px;
  line-height: 1.5;
  color: rgba(255, 255, 255, 0.32);
  margin: 0;
}

.btn-gen {
  position: relative;
  border: none;
  border-radius: 9999px;
  padding: 12px 22px;
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  cursor: pointer;
  background: linear-gradient(90deg, #ec4899 0%, #a855f7 45%, #6366f1 100%);
  box-shadow: 0 8px 28px rgba(236, 72, 153, 0.35);
  display: inline-flex;
  align-items: center;
  gap: 10px;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.btn-gen:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 12px 32px rgba(168, 85, 247, 0.4);
}

.btn-gen:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.btn-cost {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  opacity: 0.95;
}

.hex {
  color: rgba(255, 255, 255, 0.9);
}

.spin {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.tiny-spin {
  width: 12px;
  height: 12px;
}

.source-card {
  padding: 14px 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.source-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.st {
  flex: 1;
  padding: 8px;
  border-radius: 10px;
  border: 1px solid transparent;
  background: rgba(255, 255, 255, 0.04);
  color: rgba(255, 255, 255, 0.55);
  font-size: 13px;
  cursor: pointer;
}

.st.on {
  border-color: rgba(236, 72, 153, 0.45);
  color: #f9a8d4;
  background: rgba(236, 72, 153, 0.1);
}

.source-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.link-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.resolved-tag {
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  color: #f9a8d4;
  background: rgba(236, 72, 153, 0.12);
  border: 1px solid rgba(236, 72, 153, 0.18);
}

.resolved-tip {
  margin: 0;
  font-size: 12px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.5);
}

.resolved-meta {
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.resolved-meta p {
  margin: 0;
  font-size: 12px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.72);
  word-break: break-all;
}

.resolved-meta p + p {
  margin-top: 6px;
}

.resolved-meta span {
  color: rgba(255, 255, 255, 0.45);
}

.url-field {
  width: 100%;
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.25);
  color: #fff;
  font-size: 13px;
}

.btn-add {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 10px;
  border: 1px dashed rgba(255, 255, 255, 0.2);
  background: transparent;
  color: rgba(255, 255, 255, 0.75);
  cursor: pointer;
  font-size: 13px;
}

.btn-add :deep(svg) {
  width: 16px;
  height: 16px;
}

.file-name {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.45);
  word-break: break-all;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  border: 0;
}

.warn-box,
.err-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 10px;
  font-size: 13px;
}

.warn-box {
  background: rgba(239, 68, 68, 0.12);
  border: 1px solid rgba(239, 68, 68, 0.25);
  color: #fca5a5;
}

.err-box {
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.25);
  color: #fcd34d;
}

.warn-box :deep(svg),
.err-box :deep(svg) {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.link-r {
  margin-left: auto;
  color: #f472b6;
  font-weight: 600;
}

.optimized-panel {
  margin-top: 14px;
  padding: 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.optimized-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.optimized-title,
.optimized-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.58);
}

.optimized-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 12px;
}

.optimized-item {
  padding: 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.03);
}

.optimized-text {
  margin-top: 6px;
  white-space: pre-wrap;
  word-break: break-word;
  color: rgba(255, 255, 255, 0.84);
  line-height: 1.7;
}

.optimized-pre {
  margin: 8px 0 0;
  white-space: pre-wrap;
  word-break: break-word;
  padding: 12px;
  border-radius: 12px;
  background: rgba(12, 12, 15, 0.65);
  color: rgba(255, 255, 255, 0.86);
  line-height: 1.7;
}

.result-section + .result-section {
  margin-top: 12px;
}

.result-section-label {
  margin-bottom: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.58);
}

.raw-body {
  background: rgba(255, 255, 255, 0.025);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

/* 右侧 */
.col-right {
  padding-top: 8px;
}

.right-title {
  font-size: 22px;
  font-weight: 800;
  color: rgba(255, 255, 255, 0.94);
  margin: 0 0 10px;
  letter-spacing: 0.03em;
}

.formula {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.55);
  margin: 0 0 22px;
}

.formula .pink {
  color: #f472b6;
  font-weight: 700;
}

.workflow {
  display: flex;
  align-items: stretch;
  gap: 12px;
  margin-bottom: 20px;
}

.wf-card {
  flex: 1;
  min-width: 0;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.wf-card-wide {
  flex: 1.4;
}

.wf-thumb {
  border-radius: 8px;
  height: 88px;
  background-size: cover;
  background-position: center;
}

.thumb-a {
  background-image: linear-gradient(135deg, rgba(244, 114, 182, 0.35), rgba(99, 102, 241, 0.25)),
    url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><rect fill="%23222" width="100" height="100"/><circle cx="50" cy="45" r="18" fill="%23444"/></svg>');
}

.thumb-b {
  background-image: linear-gradient(135deg, rgba(52, 211, 153, 0.2), rgba(99, 102, 241, 0.2)),
    url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><rect fill="%231a1a1f" width="100" height="100"/><path d="M20 70 L50 30 L80 70 Z" fill="%236366f1" opacity="0.5"/></svg>');
}

.wf-cap {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.75);
  text-align: center;
  line-height: 1.35;
}

.wf-arrow {
  align-self: center;
  font-size: 22px;
  color: #fbbf24;
  padding: 0 4px;
}

.wf-detail {
  margin-top: 4px;
  padding: 8px 6px 4px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.wf-detail-title {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.45);
  margin: 0 0 6px;
}

.wf-detail-text {
  font-size: 11px;
  line-height: 1.55;
  color: rgba(255, 255, 255, 0.55);
  margin: 0;
}

.state-block {
  border-radius: 14px;
  padding: 20px;
}

.state-block.loading {
  text-align: center;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.ring-wrap {
  position: relative;
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
}

.ring {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 2px solid transparent;
  border-top-color: #ec4899;
  animation: spin 1.2s linear infinite;
}

.ring.r2 {
  inset: 8px;
  border-top-color: #a855f7;
  animation-direction: reverse;
  animation-duration: 0.9s;
}

.state-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px;
}

.state-sub {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.45);
  margin: 0 0 12px;
}

.bar {
  height: 4px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 9999px;
  overflow: hidden;
  max-width: 240px;
  margin: 0 auto;
}

.bar-in {
  height: 100%;
  background: linear-gradient(90deg, #ec4899, #a855f7);
  transition: width 0.4s ease;
}

.state-block.hint {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.45);
  line-height: 1.65;
  background: rgba(255, 255, 255, 0.02);
  border: 1px dashed rgba(255, 255, 255, 0.1);
}

.out {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: min(48vh, 420px);
}

.out-head {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  font-size: 15px;
}

.ok-ico :deep(svg) {
  width: 18px;
  height: 18px;
  color: #34d399;
}

.out-body {
  font-size: 13px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.78);
  white-space: pre-wrap;
  flex: 1;
  min-height: 80px;
}

.scroll-y {
  overflow-y: auto;
}

.out-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.oa {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 10px;
  font-size: 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.85);
  cursor: pointer;
}

.oa :deep(svg) {
  width: 14px;
  height: 14px;
}

.oa.primary {
  border-color: rgba(236, 72, 153, 0.45);
  background: rgba(236, 72, 153, 0.12);
  color: #fbcfe8;
}

.glass-card {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(12px);
}

.toast {
  position: fixed;
  bottom: 28px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 20px;
  border-radius: 9999px;
  background: rgba(16, 185, 129, 0.92);
  color: #fff;
  font-size: 14px;
  z-index: 9999;
  display: flex;
  align-items: center;
  gap: 8px;
}

.toast-enter-active,
.toast-leave-active {
  transition: opacity 0.25s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
}
</style>
