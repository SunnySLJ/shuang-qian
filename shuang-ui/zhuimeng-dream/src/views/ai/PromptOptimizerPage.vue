<template>
  <div class="prompt-optimizer-page">
    <div class="page-grid">
      <section class="input-panel glass-card">
        <div class="panel-head">
          <p class="eyebrow">Prompt Lab</p>
          <h1 class="title">爆款提示词优化器</h1>
          <p class="subtitle">
            参考结构化提示词优化器的工作方式，但专门按你的短视频拆解、视频生成和爆款文案场景做了收敛。
          </p>
        </div>

        <label class="field">
          <span class="field-label">原始需求</span>
          <textarea
            v-model="form.rawPrompt"
            rows="8"
            class="field-input textarea"
            placeholder="例如：做一个适合抖音投流的减脂代餐视频，突出低卡饱腹、真实反馈、上班族午餐替代，开头要抓人。"
          />
        </label>

        <div class="grid-2">
          <label class="field">
            <span class="field-label">行业</span>
            <select v-model="form.industry" class="field-input">
              <option value="">请选择</option>
              <option v-for="item in industries" :key="item" :value="item">{{ item }}</option>
            </select>
          </label>

          <label class="field">
            <span class="field-label">平台</span>
            <select v-model="form.platform" class="field-input">
              <option value="">请选择</option>
              <option v-for="item in platforms" :key="item" :value="item">{{ item }}</option>
            </select>
          </label>
        </div>

        <div class="grid-2">
          <label class="field">
            <span class="field-label">目标模型</span>
            <select v-model="form.targetModel" class="field-input">
              <option value="">请选择</option>
              <option v-for="item in targetModels" :key="item" :value="item">{{ item }}</option>
            </select>
          </label>

          <label class="field">
            <span class="field-label">模式</span>
            <input value="hot_video" class="field-input" disabled />
          </label>
        </div>

        <div class="actions">
          <button class="btn-ghost" @click="fillExample">填入示例</button>
          <button class="btn-ghost" @click="resetForm">清空</button>
          <button class="btn-primary optimize-btn" :disabled="pending || !form.rawPrompt.trim()" @click="optimize">
            <span v-if="pending" class="spinner" />
            {{ pending ? '优化中...' : '立即优化' }}
          </button>
        </div>
      </section>

      <section class="result-panel">
        <div v-if="!result" class="empty-state glass-card">
          <p class="empty-title">还没有优化结果</p>
          <p class="empty-sub">系统会输出开头钩子、卖点拆解、镜头节奏，以及可直接复制的中文和英文 Prompt。</p>
        </div>

        <template v-else>
          <div class="hero-result glass-card">
            <div class="hero-head">
              <div>
                <p class="eyebrow">Core Output</p>
                <h2>成品提示词</h2>
              </div>
              <div class="hero-actions">
                <button class="btn-ghost small" @click="copyText(result.fullPromptZh || '')">复制中文</button>
                <button class="btn-ghost small" @click="copyText(result.fullPromptEn || '')">复制英文</button>
              </div>
            </div>

            <div class="result-block">
              <div class="result-label">中文成品 Prompt</div>
              <pre class="result-pre">{{ result.fullPromptZh }}</pre>
            </div>

            <div class="result-block">
              <div class="result-label">英文成品 Prompt</div>
              <pre class="result-pre">{{ result.fullPromptEn }}</pre>
            </div>
          </div>

          <div class="meta-grid">
            <article v-for="item in metaCards" :key="item.label" class="meta-card glass-card">
              <p class="eyebrow">{{ item.label }}</p>
              <div class="meta-content">{{ item.value || '暂无' }}</div>
            </article>
          </div>
        </template>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { optimizeHotVideoPrompt, type PromptOptimizeResult } from '@/api/ai/prompt'

const pending = ref(false)
const result = ref<PromptOptimizeResult | null>(null)

const industries = [
  '电商-产品展示',
  '电商-促销广告',
  '大健康-产品介绍',
  '美食-探店视频',
  '短剧-剧情片段',
  '娱乐-搞笑视频',
]

const platforms = ['抖音', '小红书', '视频号', '快手', 'B站']
const targetModels = ['豆包', '阿里百炼', '舞墨', 'Kling', 'Runway', '通用']

const form = reactive({
  rawPrompt: '',
  industry: '电商-促销广告',
  platform: '抖音',
  targetModel: '豆包',
  mode: 'hot_video',
})

const metaCards = computed(() => {
  if (!result.value) return []
  return [
    { label: '开头钩子', value: result.value.hook },
    { label: '目标人群', value: result.value.targetAudience },
    { label: '核心卖点', value: result.value.sellingPoints },
    { label: '镜头拆解', value: result.value.shotBreakdown },
    { label: '文案策略', value: result.value.copyStrategy },
    { label: '平台建议', value: result.value.platformTips },
    { label: '镜头运动', value: result.value.cameraMovement },
    { label: '情绪氛围', value: result.value.emotion },
    { label: '光影效果', value: result.value.lighting },
    { label: '画面风格', value: result.value.style },
    { label: '建议时长', value: result.value.duration },
    { label: '优化说明', value: result.value.optimizationReasoning },
  ]
})

function fillExample() {
  form.rawPrompt = '做一个适合抖音投流的减脂代餐视频，突出低卡饱腹、真实反馈、上班族午餐替代，开头就要抓人，最好像达人测评。'
  form.industry = '大健康-产品介绍'
  form.platform = '抖音'
  form.targetModel = '豆包'
}

function resetForm() {
  form.rawPrompt = ''
  result.value = null
}

async function optimize() {
  if (!form.rawPrompt.trim() || pending.value) return
  pending.value = true
  try {
    result.value = await optimizeHotVideoPrompt({
      rawPrompt: form.rawPrompt,
      industry: form.industry || undefined,
      platform: form.platform || undefined,
      targetModel: form.targetModel || undefined,
    })
  } finally {
    pending.value = false
  }
}

async function copyText(text: string) {
  if (!text) return
  await navigator.clipboard.writeText(text)
}
</script>

<style scoped>
.prompt-optimizer-page {
  min-height: 100%;
  padding: 28px 24px 48px;
  max-width: 1360px;
  margin: 0 auto;
}

.page-grid {
  display: grid;
  grid-template-columns: 420px minmax(0, 1fr);
  gap: 24px;
  align-items: start;
}

.input-panel {
  position: sticky;
  top: 96px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.eyebrow {
  margin: 0 0 6px;
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.14em;
  color: rgba(34, 211, 238, 0.8);
}

.title {
  margin: 0;
  font-size: 30px;
  line-height: 1.1;
  color: rgba(255, 255, 255, 0.96);
}

.subtitle {
  margin: 10px 0 0;
  color: rgba(255, 255, 255, 0.62);
  line-height: 1.7;
  font-size: 14px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-label {
  font-size: 13px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.82);
}

.field-input {
  width: 100%;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: rgba(255, 255, 255, 0.88);
  border-radius: 12px;
  padding: 12px 14px;
  outline: none;
  font-size: 14px;
}

.field-input:focus {
  border-color: rgba(34, 211, 238, 0.35);
  box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.08);
}

.textarea {
  resize: vertical;
  min-height: 160px;
  line-height: 1.7;
}

.grid-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.optimize-btn {
  margin-left: auto;
}

.spinner {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.24);
  border-top-color: white;
  display: inline-block;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.result-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.empty-state,
.hero-result,
.meta-card {
  padding: 24px;
}

.empty-title {
  margin: 0 0 8px;
  font-size: 20px;
  color: rgba(255, 255, 255, 0.9);
}

.empty-sub {
  margin: 0;
  color: rgba(255, 255, 255, 0.58);
  line-height: 1.7;
}

.hero-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 18px;
}

.hero-head h2 {
  margin: 0;
  color: rgba(255, 255, 255, 0.94);
}

.hero-actions {
  display: flex;
  gap: 8px;
}

.small {
  padding: 8px 12px;
  font-size: 12px;
}

.result-block + .result-block {
  margin-top: 16px;
}

.result-label {
  margin-bottom: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.58);
}

.result-pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  padding: 16px;
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.05), rgba(255, 255, 255, 0.03));
  border: 1px solid rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.86);
  line-height: 1.7;
  font-size: 14px;
}

.meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.meta-content {
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}

@media (max-width: 980px) {
  .page-grid {
    grid-template-columns: 1fr;
  }

  .input-panel {
    position: static;
  }

  .meta-grid {
    grid-template-columns: 1fr;
  }
}
</style>
