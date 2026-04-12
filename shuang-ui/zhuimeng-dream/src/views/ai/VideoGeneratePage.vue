<template>
  <div class="video-generate-page">
    <!-- 顶部导航 -->
    <div class="top-nav glass-card">
      <div class="nav-tabs">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          class="nav-tab"
          :class="{ active: activeTab === tab.id }"
          @click="activeTab = tab.id"
        >
          <span v-html="tab.icon" class="tab-icon" />
          <span>{{ tab.label }}</span>
        </button>
      </div>
      <div class="nav-right">
        <div class="cost-badge">
          <span v-html="icons.star" class="cost-star" />
          <span>{{ currentCost }} 积分/次</span>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-layout">
      <!-- 左侧参数面板 -->
      <div class="left-panel">
        <!-- 参考图片上传区 -->
        <div class="panel-section glass-card">
          <div class="section-title-row">
            <span class="section-title">参考图片</span>
            <span class="section-sub">最多 4 张</span>
          </div>
          <input ref="fileInputRef" type="file" class="hidden-input" accept="image/*" multiple @change="handleFileChange" />
          <div class="upload-row">
            <button class="upload-chip" :disabled="uploading || uploadedImages.length >= 4" @click="triggerFileInput">
              <span v-html="icons.image" class="chip-icon" />
              <span>+ 图片</span>
            </button>
            <div v-for="(item, index) in uploadedImages" :key="item.url" class="chip-thumb">
              <img :src="item.url" :alt="`参考图${index + 1}`" class="chip-img" />
              <button class="chip-remove" @click="removeImage(index)">×</button>
            </div>
          </div>
        </div>

        <!-- Prompt 输入区 -->
        <div class="panel-section glass-card">
          <div class="section-title-row">
            <span class="section-title">视频描述</span>
          </div>
          <div class="prompt-wrapper">
            <textarea
              v-model="prompt"
              class="prompt-textarea"
              rows="4"
              placeholder="输入视频描述，或上传图片后点击「AI 理解」自动生成..."
            />
            <div class="prompt-toolbar">
              <button class="btn-tool" :disabled="uploadedImages.length === 0 || generatingPrompt" @click="generatePromptByImages">
                {{ generatingPrompt ? '识别中...' : 'AI 理解' }}
              </button>
              <button class="btn-tool" :disabled="!prompt.trim() || optimizingPrompt" @click="optimizePrompt">
                {{ optimizingPrompt ? '优化中...' : '电影级增强' }}
              </button>
              <button class="btn-tool" @click="prompt = ''">清空</button>
            </div>
          </div>
          <div v-if="visualSummary" class="summary-bar">
            <span class="summary-dot" />
            <span class="summary-text">{{ visualSummary }}</span>
          </div>
        </div>

        <!-- 参数配置区 -->
        <div class="panel-section glass-card">
          <div class="section-title-row">
            <span class="section-title">生成参数</span>
          </div>
          <div class="param-grid">
            <div class="param-row">
              <label class="param-label">时长</label>
              <div class="chip-group">
                <button
                  v-for="d in durations"
                  :key="d.id"
                  class="param-chip"
                  :class="{ active: selectedDuration === d.id }"
                  @click="selectedDuration = d.id"
                >
                  {{ d.label }}
                </button>
              </div>
            </div>
            <div class="param-row">
              <label class="param-label">比例</label>
              <div class="chip-group">
                <button
                  v-for="r in ratioOptions"
                  :key="r.id"
                  class="param-chip"
                  :class="{ active: selectedRatio === r.id }"
                  @click="selectedRatio = r.id"
                >
                  {{ r.label }}
                </button>
              </div>
            </div>
            <div class="param-row">
              <label class="param-label">风格</label>
              <select v-model="selectedStyleType" class="param-select">
                <option v-for="item in styleTypeOptions" :key="item.id" :value="item.id">{{ item.label }}</option>
              </select>
            </div>
            <div class="param-row">
              <label class="param-label">质量</label>
              <select v-model="selectedQualityLevel" class="param-select">
                <option v-for="item in qualityLevelOptions" :key="item.id" :value="item.id">{{ item.label }}</option>
              </select>
            </div>
          </div>
        </div>

        <!-- 模型选择 -->
        <div class="panel-section glass-card">
          <div class="section-title-row">
            <span class="section-title">生成模型</span>
          </div>
          <select v-model.number="selectedModelId" class="model-select">
            <option v-for="model in modelOptions" :key="model.id" :value="model.id">
              {{ model.name }} · {{ model.platform }}
            </option>
          </select>
        </div>

        <!-- 生成按钮 -->
        <button
          class="generate-btn"
          :disabled="!canGenerate || generating"
          @click="generate"
        >
          <span v-if="generating" class="btn-spinner" />
          <span v-else v-html="icons.play" class="btn-play" />
          <span>{{ generating ? '生成中...' : '生成视频' }}</span>
          <span v-if="!generating" class="btn-cost">+ {{ currentCost }}</span>
        </button>

        <div v-if="!canAfford" class="balance-tip">
          积分不足 {{ wallet.balance }} / 需要 {{ currentCost }}
          <router-link to="/recharge" class="recharge-link">去充值 →</router-link>
        </div>
      </div>

      <!-- 右侧预览区 -->
      <div class="right-preview">
        <!-- 生成中状态 -->
        <div v-if="generating" class="preview-state glass-card generating-center">
          <div class="gen-orbs">
            <div class="orb orb-1" />
            <div class="orb orb-2" />
            <div class="orb orb-3" />
          </div>
          <div class="gen-core-ring">
            <div class="gen-core">
              <span v-html="icons.video" class="core-icon" />
            </div>
          </div>
          <div class="gen-title">小云雀正在生成营销视频</div>
          <div class="gen-sub">视频将在 5 分钟内完成，请稍候</div>
          <div class="gen-progress-track">
            <div class="gen-progress-fill" />
          </div>
        </div>

        <!-- 视频结果 -->
        <div v-else-if="videoResult" class="preview-state glass-card result-ready">
          <div class="result-video-wrap">
            <img :src="videoResult.thumbnail" :alt="videoResult.prompt" class="result-thumb" />
            <button class="result-play-btn" @click="openVideo(videoResult.videoUrl)">
              <span v-html="icons.play" class="play-icon" />
            </button>
            <div class="result-duration">{{ videoResult.duration }}</div>
            <div class="result-cost-tag">-{{ videoResult.cost }}积分</div>
          </div>
          <div class="result-info">
            <div class="result-prompt">{{ videoResult.prompt }}</div>
            <div class="result-meta-row">
              <span class="meta-tag">{{ videoResult.duration }}</span>
              <span class="meta-tag tag-accent">-{{ videoResult.cost }}积分</span>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="preview-state glass-card preview-empty">
          <div class="empty-visual">
            <div class="empty-orbs">
              <div class="e-orb e-orb-1" />
              <div class="e-orb e-orb-2" />
              <div class="e-orb e-orb-3" />
            </div>
            <div class="empty-laptop">
              <span v-html="icons.video" class="laptop-icon" />
            </div>
          </div>
          <p class="empty-title">上传图片 · 输入描述</p>
          <p class="empty-sub">小云雀将根据图片和描述生成精美营销视频</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import request from '@/utils/request'
import { cosmicIcons } from '@/utils/cosmicIcons'
import { useAppStore } from '@/stores/app'
import { buildVideoPrompt } from '@/utils/promptBuilder'
import { getAiModelSimpleList, type AiModelOption } from '@/api/ai/model'
import { optimizeHotVideoPrompt } from '@/api/ai/prompt'
import { generatePromptFromImages, getVideoDetail, syncVideoStatus, textToVideo, type VideoRecord } from '@/api/ai/video'
import { toast } from '@/utils/toast'
import { getLastVideoModelId, setLastVideoModelId } from '@/utils/aiModelPrefs'

const icons = cosmicIcons
const route = useRoute()
const store = useAppStore()
const { wallet } = storeToRefs(store)

// ========== 状态 ==========
const activeTab = ref('img2video')
const tabs = [
  { id: 'head_tail', label: '首尾帧', icon: icons.grid },
  { id: 'img2video', label: '参考生视频', icon: icons.image },
  { id: 'text2video', label: '文生视频', icon: icons.edit },
  { id: 'edit', label: '视频编辑', icon: icons.video },
]

const prompt = ref(route.query.prompt as string || '')
const visualSummary = ref('')
const selectedDuration = ref('5s')
const selectedModelId = ref<number | null>(null)
const selectedRatio = ref('16:9')
const selectedStyleType = ref('product_showcase')
const selectedVisualStyle = ref('cinematic_ad')
const selectedCameraMovement = ref('push_orbit')
const selectedShotFocus = ref('subject_product')
const selectedQualityLevel = ref('cinematic')
const generating = ref(false)
const generatingPrompt = ref(false)
const optimizingPrompt = ref(false)
const uploading = ref(false)
const fileInputRef = ref<HTMLInputElement | null>(null)
const modelOptions = ref<AiModelOption[]>([])
const uploadedImages = ref<Array<{ url: string }>>([])
const videoResult = ref<{ id: number; thumbnail: string; videoUrl: string; prompt: string; cost: number; duration: string } | null>(null)

// ========== 选项 ==========
const durations = [
  { id: '5s', label: '5秒', points: 20 },
  { id: '15s', label: '15秒', points: 40 },
  { id: '30s', label: '30秒', points: 60 },
]

const ratioOptions = [
  { id: '16:9', label: '16:9' },
  { id: '9:16', label: '9:16' },
  { id: '1:1', label: '1:1' },
]

const styleTypeOptions = [
  { id: 'product_showcase', label: '产品展示' },
  { id: 'lifestyle', label: '生活方式' },
  { id: 'tech_review', label: '科技评测' },
  { id: 'custom', label: '自定义电影感' },
]

const visualStyleOptions = [
  { id: 'cinematic_ad', label: '电影级商业广告' },
  { id: 'luxury_realism', label: '高端超写实' },
  { id: 'emotional_story', label: '情绪叙事' },
]

const cameraMovementOptions = [
  { id: 'push_orbit', label: '推进 + 环绕' },
  { id: 'tracking_follow', label: '跟拍推进' },
  { id: 'macro_reveal', label: '微距揭示' },
]

const shotFocusOptions = [
  { id: 'subject_product', label: '人物 / 产品主体' },
  { id: 'material_detail', label: '材质细节' },
  { id: 'scene_atmosphere', label: '场景氛围' },
]

const qualityLevelOptions = [
  { id: 'cinematic', label: '电影级' },
  { id: 'premium', label: '高级商业感' },
  { id: 'blockbuster', label: '超级炸裂成片' },
]

// ========== 计算 ==========
const currentCost = computed(() => durations.find(d => d.id === selectedDuration.value)?.points || 20)
const canAfford = computed(() => wallet.value.balance >= currentCost.value)
const canGenerate = computed(() => {
  if (!prompt.value.trim()) return false
  if (!selectedModelId.value) return false
  return canAfford.value
})
const selectedRatioLabel = computed(() => ratioOptions.find(r => r.id === selectedRatio.value)?.label || '')
const selectedStyleTypeLabel = computed(() => styleTypeOptions.find(s => s.id === selectedStyleType.value)?.label || '')
const selectedVisualStyleLabel = computed(() => visualStyleOptions.find(v => v.id === selectedVisualStyle.value)?.label || '')
const selectedCameraMovementLabel = computed(() => cameraMovementOptions.find(c => c.id === selectedCameraMovement.value)?.label || '')
const selectedShotFocusLabel = computed(() => shotFocusOptions.find(s => s.id === selectedShotFocus.value)?.label || '')
const selectedQualityLevelLabel = computed(() => qualityLevelOptions.find(q => q.id === selectedQualityLevel.value)?.label || '')
const composedPrompt = computed(() => buildVideoPrompt({
  rawPrompt: prompt.value,
  durationId: selectedDuration.value,
  ratioLabel: selectedRatioLabel.value,
  styleTypeLabel: selectedStyleTypeLabel.value,
  visualStyleLabel: selectedVisualStyleLabel.value,
  cameraMovementLabel: selectedCameraMovementLabel.value,
  shotFocusLabel: selectedShotFocusLabel.value,
  qualityLevelLabel: selectedQualityLevelLabel.value,
}))

// ========== 方法 ==========
function extractUploadedUrl(payload: unknown) {
  if (typeof payload === 'string') return payload
  if (payload && typeof payload === 'object') {
    const candidate = payload as Record<string, unknown>
    const url = candidate.url || candidate.data || candidate.path
    return typeof url === 'string' ? url : ''
  }
  return ''
}

function triggerFileInput() {
  fileInputRef.value?.click()
}

async function handleFileChange(event: Event) {
  const files = Array.from((event.target as HTMLInputElement).files || [])
  if (!files.length) return
  const remaining = 4 - uploadedImages.value.length
  const selected = files.slice(0, remaining)
  if (!selected.length) { toast.warning('最多上传 4 张图片'); return }

  uploading.value = true
  try {
    for (const file of selected) {
      const formData = new FormData()
      formData.append('file', file)
      const response = await request.post<unknown>('/infra/file/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      const url = extractUploadedUrl(response)
      if (!url) throw new Error('上传成功但未获取到图片地址')
      uploadedImages.value.push({ url })
    }
    toast.success('图片上传成功')
  } catch (error) {
    toast.error(error instanceof Error ? error.message : '图片上传失败')
  } finally {
    uploading.value = false
    ;(event.target as HTMLInputElement).value = ''
  }
}

function removeImage(index: number) {
  uploadedImages.value.splice(index, 1)
}

async function generatePromptByImages() {
  if (!uploadedImages.value.length || generatingPrompt.value) return
  generatingPrompt.value = true
  try {
    const data = await generatePromptFromImages({
      imageUrls: uploadedImages.value.map(i => i.url),
      userPrompt: prompt.value,
      promptLevel: 'cinematic',
    })
    visualSummary.value = data.visualSummary || ''
    prompt.value = data.optimizedPrompt || data.basePrompt || prompt.value
    toast.success('已生成电影级提示词')
  } catch (error) {
    toast.error(error instanceof Error ? error.message : '识图生成提示词失败')
  } finally {
    generatingPrompt.value = false
  }
}

async function optimizePrompt() {
  if (!prompt.value.trim() || optimizingPrompt.value) return
  optimizingPrompt.value = true
  try {
    const modelName = modelOptions.value.find(m => m.id === selectedModelId.value)?.name || '小云雀'
    const result = await optimizeHotVideoPrompt({
      rawPrompt: prompt.value,
      targetModel: modelName,
      mode: 'hot_video',
    })
    prompt.value = result.fullPromptZh || prompt.value
    toast.success('已完成电影级增强')
  } catch (error) {
    toast.error(error instanceof Error ? error.message : '提示词优化失败')
  } finally {
    optimizingPrompt.value = false
  }
}

async function generate() {
  if (!canGenerate.value || generating.value) return
  generating.value = true
  try {
    const recordId = await textToVideo({
      prompt: prompt.value,
      imageUrl: uploadedImages.value[0]?.url || '',
      duration: Number.parseInt(selectedDuration.value, 10),
      modelId: selectedModelId.value!,
      ratio: selectedRatio.value,
      styleType: selectedStyleType.value,
      visualStyle: selectedVisualStyleLabel.value,
      cameraMovement: selectedCameraMovementLabel.value,
      shotFocus: selectedShotFocusLabel.value,
      qualityLevel: selectedQualityLevelLabel.value,
      clientId: `video-gen-${Date.now()}`,
    })
    const detail = await pollVideoResult(recordId)
    videoResult.value = {
      id: detail.id,
      thumbnail: detail.coverUrl || detail.picUrl || detail.thumbnailUrl || '',
      videoUrl: detail.outputUrl || '',
      prompt: detail.prompt || composedPrompt.value,
      cost: currentCost.value,
      duration: `${detail.duration || Number.parseInt(selectedDuration.value, 10)}秒`,
    }
    await store.fetchWallet()
    toast.success('视频生成成功')
  } catch (error) {
    toast.error(error instanceof Error ? error.message : '视频生成失败')
    await store.fetchWallet()
  } finally {
    generating.value = false
  }
}

async function pollVideoResult(id: number): Promise<VideoRecord> {
  for (let attempt = 0; attempt < 60; attempt += 1) {
    const detail = await getVideoDetail(id)
    if (detail.status === 20 && detail.outputUrl) return detail
    if (detail.status === 30) throw new Error(detail.errorMessage || '视频生成失败')
    await syncVideoStatus(id)
    await new Promise(resolve => setTimeout(resolve, 3000))
  }
  throw new Error('视频生成超时，请稍后在历史记录查看')
}

async function loadModels() {
  try {
    modelOptions.value = await getAiModelSimpleList(4)
    const lastId = getLastVideoModelId()
    selectedModelId.value = modelOptions.value.some(m => m.id === lastId)
      ? lastId
      : (modelOptions.value[0]?.id ?? null)
  } catch (error) {
    toast.error(error instanceof Error ? error.message : '加载模型失败')
  }
}

function openVideo(url: string) {
  if (!url) return
  window.open(url, '_blank')
}

onMounted(() => { loadModels() })
watch(selectedModelId, (id) => { if (id) setLastVideoModelId(id) })
</script>

<style scoped>
/* ===== 页面布局 ===== */
.video-generate-page {
  min-height: 100vh;
  background: var(--color-bg-base);
  padding: 20px 24px 32px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ===== 顶部导航 ===== */
.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  border-radius: 14px;
}

.nav-tabs {
  display: flex;
  gap: 4px;
}

.nav-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: var(--color-text-muted);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.nav-tab:hover { background: rgba(255,255,255,0.04); color: var(--color-text-secondary); }
.nav-tab.active {
  background: rgba(124, 58, 237, 0.18);
  color: #a78bfa;
  box-shadow: 0 0 0 1px rgba(124, 58, 237, 0.35);
}
.tab-icon { width: 14px; height: 14px; display: flex; }

.nav-right { display: flex; align-items: center; }
.cost-badge {
  display: flex; align-items: center; gap: 6px;
  padding: 6px 14px; border-radius: 9999px;
  background: rgba(6, 182, 212, 0.1);
  border: 1px solid rgba(6, 182, 212, 0.25);
  font-size: 13px; font-weight: 600; color: #22d3ee;
}
.cost-star { width: 13px; height: 13px; }

/* ===== 主布局 ===== */
.main-layout {
  display: grid;
  grid-template-columns: 380px 1fr;
  gap: 16px;
  flex: 1;
}

/* ===== 左侧面板 ===== */
.left-panel { display: flex; flex-direction: column; gap: 12px; }

.panel-section { padding: 16px; }

.section-title-row {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 12px;
}
.section-title { font-size: 13px; font-weight: 700; color: var(--color-text-secondary); }
.section-sub { font-size: 11px; color: var(--color-text-muted); }

.hidden-input { display: none; }

/* 上传区 */
.upload-row { display: flex; gap: 8px; flex-wrap: wrap; align-items: center; }
.upload-chip {
  display: flex; align-items: center; gap: 6px;
  padding: 8px 14px; border-radius: 10px;
  border: 1px dashed rgba(124, 58, 237, 0.4);
  background: rgba(124, 58, 237, 0.08);
  color: #a78bfa; font-size: 13px; font-weight: 500;
  cursor: pointer; transition: all 0.2s ease;
}
.upload-chip:hover { background: rgba(124, 58, 237, 0.15); border-color: rgba(124, 58, 237, 0.6); }
.upload-chip:disabled { opacity: 0.4; cursor: not-allowed; }
.chip-icon { width: 14px; height: 14px; }

.chip-thumb { position: relative; width: 44px; height: 44px; border-radius: 10px; overflow: hidden; border: 1px solid rgba(255,255,255,0.1); }
.chip-img { width: 100%; height: 100%; object-fit: cover; }
.chip-remove {
  position: absolute; top: 2px; right: 2px;
  width: 16px; height: 16px; border-radius: 50%;
  background: rgba(0,0,0,0.65); border: none; color: white;
  font-size: 10px; line-height: 1; cursor: pointer; display: flex; align-items: center; justify-content: center;
}

/* Prompt 区 */
.prompt-wrapper { display: flex; flex-direction: column; gap: 8px; }
.prompt-textarea {
  width: 100%; padding: 12px 14px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 10px; color: var(--color-text-primary);
  font-size: 14px; line-height: 1.6; resize: vertical;
  outline: none; font-family: inherit;
}
.prompt-textarea:focus {
  border-color: rgba(124, 58, 237, 0.4);
  background: rgba(124, 58, 237, 0.04);
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.1);
}
.prompt-textarea::placeholder { color: var(--color-text-muted); }

.prompt-toolbar { display: flex; gap: 6px; flex-wrap: wrap; }
.btn-tool {
  display: flex; align-items: center; gap: 5px;
  padding: 6px 12px; border-radius: 8px;
  border: 1px solid rgba(255,255,255,0.08);
  background: rgba(255,255,255,0.03);
  color: var(--color-text-muted); font-size: 12px; font-weight: 500;
  cursor: pointer; transition: all 0.2s ease;
}
.btn-tool:hover { background: rgba(255,255,255,0.07); color: var(--color-text-secondary); }
.btn-tool:disabled { opacity: 0.4; cursor: not-allowed; }

.summary-bar {
  display: flex; align-items: flex-start; gap: 8px;
  padding: 10px 12px; border-radius: 10px;
  background: rgba(6, 182, 212, 0.06);
  border: 1px solid rgba(6, 182, 212, 0.15);
}
.summary-dot { width: 6px; height: 6px; border-radius: 50%; background: #22d3ee; margin-top: 6px; flex-shrink: 0; }
.summary-text { font-size: 12px; color: #22d3ee; line-height: 1.6; }

/* 参数区 */
.param-grid { display: flex; flex-direction: column; gap: 12px; }
.param-row { display: flex; align-items: center; gap: 10px; }
.param-label { font-size: 12px; color: var(--color-text-muted); width: 36px; flex-shrink: 0; }
.chip-group { display: flex; gap: 6px; flex-wrap: wrap; }
.param-chip {
  display: flex; align-items: center;
  padding: 5px 12px; border-radius: 8px;
  border: 1px solid rgba(255,255,255,0.08);
  background: rgba(255,255,255,0.03);
  color: var(--color-text-muted); font-size: 12px; font-weight: 500;
  cursor: pointer; transition: all 0.2s ease;
}
.param-chip.active {
  background: rgba(124, 58, 237, 0.18);
  border-color: rgba(124, 58, 237, 0.45);
  color: #a78bfa;
}
.param-select {
  flex: 1; padding: 6px 10px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 8px; color: var(--color-text-secondary);
  font-size: 12px; outline: none; cursor: pointer;
}
.param-select:focus { border-color: rgba(124, 58, 237, 0.4); }

/* 模型选择 */
.model-select {
  width: 100%; padding: 10px 14px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 10px; color: var(--color-text-primary);
  font-size: 13px; outline: none; cursor: pointer;
}
.model-select:focus { border-color: rgba(124, 58, 237, 0.4); }

/* 生成按钮 */
.generate-btn {
  display: flex; align-items: center; justify-content: center; gap: 8px;
  width: 100%; padding: 14px;
  background: linear-gradient(135deg, #7c3aed 0%, #8b5cf6 40%, #06b6d4 100%);
  color: white; font-size: 16px; font-weight: 700;
  border: none; border-radius: 14px; cursor: pointer;
  box-shadow: 0 4px 20px rgba(124, 58, 237, 0.35);
  transition: all 0.2s ease;
}
.generate-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 8px 30px rgba(124, 58, 237, 0.5);
}
.generate-btn:disabled { opacity: 0.5; cursor: not-allowed; transform: none; }
.btn-play { width: 18px; height: 18px; }
.btn-spinner {
  width: 16px; height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white; border-radius: 50%;
  animation: spin 0.6s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
.btn-cost {
  padding: 2px 8px; border-radius: 9999px;
  background: rgba(255,255,255,0.2); font-size: 12px; font-weight: 600;
}

.balance-tip {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 14px; border-radius: 10px;
  background: rgba(239, 68, 68, 0.07);
  border: 1px solid rgba(239, 68, 68, 0.18);
  font-size: 12px; color: #f87171;
}
.recharge-link { margin-left: auto; color: #a78bfa; text-decoration: none; }
.recharge-link:hover { text-decoration: underline; }

/* ===== 右侧预览区 ===== */
.right-preview { min-height: 500px; }

.preview-state {
  height: 100%;
  min-height: 500px;
  border-radius: 18px;
  overflow: hidden;
  position: relative;
}

/* 生成中 */
.generating-center {
  display: flex; flex-direction: column;
  align-items: center; justify-content: center; gap: 20px;
  padding: 48px;
}

.gen-orbs { position: absolute; inset: 0; overflow: hidden; pointer-events: none; }
.orb {
  position: absolute; border-radius: 50%;
  filter: blur(60px); opacity: 0.3;
  animation: drift 8s ease-in-out infinite;
}
.orb-1 { width: 300px; height: 300px; top: -80px; left: -60px; background: #7c3aed; }
.orb-2 { width: 250px; height: 250px; bottom: -60px; right: -40px; background: #06b6d4; animation-delay: -3s; }
.orb-3 { width: 200px; height: 200px; top: 50%; left: 50%; transform: translate(-50%,-50%); background: #ec4899; animation-delay: -5s; }

@keyframes drift {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -20px) scale(1.05); }
  66% { transform: translate(-20px, 15px) scale(0.95); }
}

.gen-core-ring {
  width: 80px; height: 80px; border-radius: 50%;
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.3), rgba(6, 182, 212, 0.3));
  display: flex; align-items: center; justify-content: center;
  animation: pulse-ring 2s ease-in-out infinite;
  position: relative; z-index: 1;
}
@keyframes pulse-ring {
  0%, 100% { box-shadow: 0 0 0 0 rgba(124, 58, 237, 0.4); }
  50% { box-shadow: 0 0 0 16px rgba(124, 58, 237, 0); }
}
.gen-core {
  width: 52px; height: 52px; border-radius: 50%;
  background: linear-gradient(135deg, #7c3aed, #06b6d4);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 0 30px rgba(124, 58, 237, 0.5);
}
.core-icon { width: 22px; height: 22px; color: white; }
.gen-title { font-size: 18px; font-weight: 700; color: var(--color-text-primary); position: relative; z-index: 1; }
.gen-sub { font-size: 13px; color: var(--color-text-muted); position: relative; z-index: 1; }
.gen-progress-track {
  width: 200px; height: 3px; background: rgba(255,255,255,0.08);
  border-radius: 9999px; overflow: hidden; position: relative; z-index: 1;
}
.gen-progress-fill {
  height: 100%; border-radius: 9999px;
  background: linear-gradient(90deg, #7c3aed, #06b6d4, #ec4899);
  animation: progress 6s ease-in-out infinite;
}
@keyframes progress {
  0% { width: 0%; }
  50% { width: 80%; }
  100% { width: 100%; }
}

/* 视频结果 */
.result-ready { padding: 0; }
.result-video-wrap {
  position: relative; height: 420px; overflow: hidden;
}
.result-thumb { width: 100%; height: 100%; object-fit: cover; }
.result-play-btn {
  position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
  width: 72px; height: 72px; border-radius: 50%;
  background: rgba(124, 58, 237, 0.4);
  backdrop-filter: blur(8px);
  border: 2px solid rgba(255,255,255,0.3);
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.2s ease;
}
.result-play-btn:hover { background: rgba(124, 58, 237, 0.6); transform: translate(-50%, -50%) scale(1.05); }
.play-icon { width: 30px; height: 30px; color: white; }
.result-duration {
  position: absolute; bottom: 12px; right: 12px;
  padding: 4px 10px; background: rgba(0,0,0,0.6); border-radius: 6px;
  font-size: 12px; color: white; font-weight: 600;
}
.result-cost-tag {
  position: absolute; bottom: 12px; left: 12px;
  padding: 4px 10px; background: rgba(239, 68, 68, 0.7); border-radius: 6px;
  font-size: 12px; color: white; font-weight: 600;
}
.result-info { padding: 16px; }
.result-prompt { font-size: 13px; color: var(--color-text-secondary); line-height: 1.6; margin-bottom: 10px; white-space: pre-wrap; }
.result-meta-row { display: flex; gap: 8px; }
.meta-tag {
  padding: 4px 10px; border-radius: 9999px;
  background: rgba(255,255,255,0.06); font-size: 12px; color: var(--color-text-muted);
}
.tag-accent { color: #22d3ee; background: rgba(6, 182, 212, 0.1); }

/* 空状态 */
.preview-empty {
  display: flex; flex-direction: column;
  align-items: center; justify-content: center; gap: 16px;
  padding: 48px; text-align: center;
}
.empty-visual { position: relative; width: 200px; height: 140px; display: flex; align-items: center; justify-content: center; }
.empty-orbs { position: absolute; inset: 0; }
.e-orb { position: absolute; border-radius: 50%; filter: blur(40px); opacity: 0.15; animation: drift 10s ease-in-out infinite; }
.e-orb-1 { width: 120px; height: 120px; top: 0; left: 0; background: #7c3aed; }
.e-orb-2 { width: 100px; height: 100px; bottom: 0; right: 0; background: #06b6d4; animation-delay: -4s; }
.e-orb-3 { width: 80px; height: 80px; top: 50%; left: 50%; transform: translate(-50%,-50%); background: #ec4899; animation-delay: -7s; }
.empty-laptop {
  width: 100px; height: 70px; border-radius: 12px;
  background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08);
  display: flex; align-items: center; justify-content: center;
  position: relative; z-index: 1;
}
.laptop-icon { width: 36px; height: 36px; color: rgba(255,255,255,0.15); }
.empty-title { font-size: 17px; font-weight: 700; color: var(--color-text-secondary); }
.empty-sub { font-size: 13px; color: var(--color-text-muted); line-height: 1.6; max-width: 260px; }

/* ===== 响应式 ===== */
@media (max-width: 900px) {
  .main-layout { grid-template-columns: 1fr; }
  .right-preview { min-height: 380px; }
  .nav-tabs { overflow-x: auto; }
}
</style>
