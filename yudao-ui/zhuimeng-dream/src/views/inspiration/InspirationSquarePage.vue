<template>
  <div class="inspiration-page">
    <div class="page-inner">
      <!-- 顶部标题 -->
      <div class="page-header">
        <div class="header-text">
          <h1 class="page-title">
            <span class="title-icon" v-html="icons.sparkle" />
            灵感广场
          </h1>
          <p class="page-subtitle">精选优质创作模板，一键激发创意灵感</p>
        </div>

        <!-- 搜索框 -->
        <div class="search-wrap">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="search-icon">
            <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <input
            v-model="searchKeyword"
            type="text"
            class="search-input"
            placeholder="搜索模板关键词..."
          />
        </div>
      </div>

      <!-- 分类标签（横向滚动） -->
      <div class="category-tabs">
        <div class="tabs-scroll">
          <button
            v-for="cat in categories"
            :key="cat.id"
            class="tab-btn"
            :class="{ active: activeCategory === cat.id }"
            @click="activeCategory = cat.id"
          >
            <span class="tab-icon" v-html="cat.icon" />
            {{ cat.name }}
            <span class="tab-count">{{ cat.count }}</span>
          </button>
        </div>
      </div>

      <!-- 模板统计 -->
      <div class="template-stats">
        <span class="stats-num">{{ filteredTemplates.length }}</span> 个模板
        <span class="stats-sep">·</span>
        <span class="stats-tip">点击卡片直接使用模板创作</span>
      </div>

      <!-- 模板瀑布流网格 -->
      <div class="templates-grid" ref="gridRef">
        <div
          v-for="(tmpl, index) in filteredTemplates"
          :key="tmpl.id"
          class="template-card"
          :style="{ animationDelay: `${Math.min(index, 12) * 60}ms` }"
          @click="useTemplate(tmpl)"
        >
          <!-- 封面图 -->
          <div class="tmpl-cover">
            <img :src="tmpl.cover" :alt="tmpl.title" loading="lazy" />
            <!-- 悬停遮罩 -->
            <div class="tmpl-overlay">
              <button class="use-btn" @click.stop="useTemplate(tmpl)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/></svg>
                立即使用
              </button>
              <button class="like-btn" @click.stop="toggleLike(tmpl)">
                <svg width="14" height="14" viewBox="0 0 24 24" :fill="tmpl.liked ? '#f43f5e' : 'none'" stroke="#f43f5e" stroke-width="2">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                </svg>
                {{ tmpl.liked ? tmpl.likes + 1 : tmpl.likes }}
              </button>
            </div>
            <!-- 类型标签 -->
            <div class="tmpl-type-badge" :class="tmpl.type">
              {{ tmpl.type === 'video' ? '视频' : '图片' }}
            </div>
          </div>

          <!-- 信息区 -->
          <div class="tmpl-info">
            <div class="tmpl-header-row">
              <div class="tmpl-category-tag" :style="{ color: tmpl.catColor }">
                <span class="cat-dot" :style="{ background: tmpl.catColor }" />
                {{ tmpl.category }}
              </div>
              <span class="tmpl-cost">
                <svg width="10" height="10" viewBox="0 0 24 24" fill="#f59e0b"><path d="M12 2l2.4 7.4h7.6l-6 4.6 2.3 7.4-6.3-4.6-6.3 4.6 2.3-7.4-6-4.6h7.6L12 2z"/></svg>
                {{ tmpl.cost }}积分
              </span>
            </div>
            <h3 class="tmpl-title">{{ tmpl.title }}</h3>
            <p class="tmpl-prompt">{{ tmpl.prompt }}</p>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredTemplates.length === 0" class="empty-state">
        <div class="empty-icon" v-html="icons.sparkle" />
        <p>没有找到相关模板</p>
        <p class="empty-tip">换个关键词试试，或浏览全部模板</p>
        <button class="btn-ghost" @click="searchKeyword = ''; activeCategory = 'all'">重置筛选</button>
      </div>
    </div>

    <!-- 使用模板弹窗 -->
    <transition name="modal">
      <div v-if="selectedTemplate" class="modal-overlay" @click.self="selectedTemplate = null">
        <div class="use-modal glass-card">
          <div class="modal-header">
            <h3 class="modal-title">
              <span v-html="icons.rocket" />
              使用模板创作
            </h3>
            <button class="modal-close" @click="selectedTemplate = null">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>

          <div class="modal-body">
            <div class="modal-preview">
              <img :src="selectedTemplate.cover" :alt="selectedTemplate.title" />
            </div>
            <div class="modal-info">
              <div class="modal-tmpl-name">{{ selectedTemplate.title }}</div>
              <div class="modal-prompt-label">Prompt</div>
              <div class="modal-prompt-text">{{ selectedTemplate.prompt }}</div>
              <div class="modal-meta">
                <span class="meta-tag" :class="selectedTemplate.type">
                  {{ selectedTemplate.type === 'video' ? '视频' : '图片' }}模板
                </span>
                <span class="meta-cost">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="#f59e0b"><path d="M12 2l2.4 7.4h7.6l-6 4.6 2.3 7.4-6.3-4.6-6.3 4.6 2.3-7.4-6-4.6h7.6L12 2z"/></svg>
                  消耗 {{ selectedTemplate.cost }} 积分
                </span>
              </div>
              <button
                class="btn-primary use-submit-btn"
                :disabled="generating"
                @click="startGenerate"
              >
                <span v-if="generating" class="loading-spinner" />
                <span v-else v-html="icons.rocket" />
                {{ generating ? '正在跳转...' : `去 ${selectedTemplate.type === 'video' ? '视频' : '做图'} 生成` }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { cosmicIcons } from '@/utils/cosmicIcons'

const icons = cosmicIcons
const store = useAppStore()
const router = useRouter()

const searchKeyword = ref('')
const activeCategory = ref('all')
const selectedTemplate = ref<TemplateItem | null>(null)
const generating = ref(false)

interface TemplateItem {
  id: number
  title: string
  prompt: string
  cover: string
  category: string
  catColor: string
  type: 'image' | 'video'
  cost: number
  likes: number
  liked: boolean
}

const categories = [
  { id: 'all', name: '全部', icon: icons.sparkle, count: 24 },
  { id: 'city', name: '城市建筑', icon: icons.home, count: 4 },
  { id: 'nature', name: '自然风景', icon: icons.target, count: 3 },
  { id: 'person', name: '人物写真', icon: icons.users, count: 4 },
  { id: 'anime', name: '动漫风格', icon: icons.create, count: 3 },
  { id: 'chinese', name: '国风水墨', icon: icons.diamond, count: 3 },
  { id: 'cyberpunk', name: '赛博朋克', icon: icons.video, count: 3 },
  { id: 'product', name: '产品商业', icon: icons.chart, count: 2 },
  { id: 'abstract', name: '抽象艺术', icon: icons.sparkle, count: 2 },
]

const templates: TemplateItem[] = [
  {
    id: 1,
    title: '未来都市夜景',
    prompt: '未来城市天际线，赛博朋克风格，高达机器人站在雨中，霓虹灯牌，湿润的街道反射灯光，科幻电影感，8K超清',
    cover: 'https://images.unsplash.com/photo-1480714378408-67cf0d13bc1b?w=600&q=80',
    category: '城市建筑', catColor: '#06b6d4', type: 'image', cost: 2, likes: 2847, liked: false,
  },
  {
    id: 2,
    title: '动漫少女头像',
    prompt: '精美动漫女孩头像，银色长发，星空眼睛，柔和光线，精美细节，高质量插画，二次元风格，8K',
    cover: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80',
    category: '动漫风格', catColor: '#ec4899', type: 'image', cost: 2, likes: 5621, liked: false,
  },
  {
    id: 3,
    title: '山水意境短片',
    prompt: '中国水墨山水，云雾缭绕，古风建筑，飞鸟穿过松林，晨光熹微，诗意画卷，禅意氛围，4K高清',
    cover: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&q=80',
    category: '国风水墨', catColor: '#f59e0b', type: 'video', cost: 10, likes: 3204, liked: false,
  },
  {
    id: 4,
    title: '赛博朋克少女',
    prompt: '赛博朋克风格未来少女，机械义肢，霓虹灯光，雨水打在脸上，科幻城市背景，电影感，高细节，8K',
    cover: 'https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?w=600&q=80',
    category: '赛博朋克', catColor: '#8b5cf6', type: 'image', cost: 2, likes: 4102, liked: false,
  },
  {
    id: 5,
    title: '极简产品展示',
    prompt: '极简主义产品摄影，纯白背景，高端科技产品，商业摄影风格，专业灯光，干净利落，8K',
    cover: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&q=80',
    category: '产品商业', catColor: '#64748b', type: 'image', cost: 2, likes: 1563, liked: false,
  },
  {
    id: 6,
    title: '星空银河延时',
    prompt: '壮丽银河星空延时摄影，流星划过，深蓝夜空，远处雪山剪影，震撼宇宙之美，电影级画质',
    cover: 'https://images.unsplash.com/photo-1519681393784-d120267933ba?w=600&q=80',
    category: '自然风景', catColor: '#6366f1', type: 'video', cost: 10, likes: 7823, liked: false,
  },
  {
    id: 7,
    title: '未来战士',
    prompt: '未来科幻战士，机械装甲，沙漠背景，烈日当空，金属质感，细节丰富，电影海报风格，8K',
    cover: 'https://images.unsplash.com/photo-1531306728370-e2ebd9d7bb99?w=600&q=80',
    category: '赛博朋克', catColor: '#8b5cf6', type: 'image', cost: 2, likes: 2315, liked: false,
  },
  {
    id: 8,
    title: '古风仙子',
    prompt: '中国古风仙子，白衣长发，轻纱飘逸，桃花树下，月光皎洁，水墨渲染，东方美学，8K超清',
    cover: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&q=80',
    category: '国风水墨', catColor: '#f59e0b', type: 'image', cost: 2, likes: 4456, liked: false,
  },
  {
    id: 9,
    title: '森林精灵少女',
    prompt: '奇幻森林精灵少女，发光透明翅膀，萤火虫环绕，古老树木，神秘氛围，魔幻光效，8K',
    cover: 'https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80',
    category: '动漫风格', catColor: '#ec4899', type: 'image', cost: 2, likes: 3876, liked: false,
  },
  {
    id: 10,
    title: '日出云海',
    prompt: '壮丽日出云海，仙人山峰，云雾翻涌，金色阳光照耀，大气磅礴，风光摄影，8K超清',
    cover: 'https://images.unsplash.com/photo-1472214103451-9374bd1c798e?w=600&q=80',
    category: '自然风景', catColor: '#6366f1', type: 'image', cost: 2, likes: 5934, liked: false,
  },
  {
    id: 11,
    title: '现代建筑群',
    prompt: '现代极简建筑群，白色几何体，晴朗天空，干净利落的线条，建筑摄影，商业地产风格，8K',
    cover: 'https://images.unsplash.com/photo-1486325212027-8081e485255e?w=600&q=80',
    category: '城市建筑', catColor: '#06b6d4', type: 'image', cost: 2, likes: 1823, liked: false,
  },
  {
    id: 12,
    title: '宇宙星云',
    prompt: '宇宙星云，特写摄影，绚丽色彩，恒星诞生区，哈勃风格，太空探索，科幻震撼，8K超清',
    cover: 'https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=600&q=80',
    category: '抽象艺术', catColor: '#a78bfa', type: 'image', cost: 2, likes: 6120, liked: false,
  },
  {
    id: 13,
    title: '赛博都市夜景',
    prompt: '赛博朋克都市夜景，霓虹灯牌密集，飞行汽车穿梭，高楼林立，雨夜，科幻电影感，8K',
    cover: 'https://images.unsplash.com/photo-1488972685288-c3fd157d7c7a?w=600&q=80',
    category: '城市建筑', catColor: '#06b6d4', type: 'image', cost: 2, likes: 5234, liked: false,
  },
  {
    id: 14,
    title: '水墨山水',
    prompt: '中国水墨山水画，浓墨淡彩，云雾缭绕，诗意盎然，古典东方美学，极简留白，宋代画风',
    cover: 'https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?w=600&q=80',
    category: '国风水墨', catColor: '#f59e0b', type: 'image', cost: 2, likes: 3789, liked: false,
  },
  {
    id: 15,
    title: '机械少女',
    prompt: '未来感机械少女，精密机械构造，金属光泽，蓝色光效，暗色调，科幻设定画，8K',
    cover: 'https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=600&q=80',
    category: '动漫风格', catColor: '#ec4899', type: 'image', cost: 2, likes: 4567, liked: false,
  },
  {
    id: 16,
    title: '极光极地',
    prompt: '北极光极地风光，绿色极光在天空中舞动，冰原和雪山，星空倒映湖面，宁静震撼，4K',
    cover: 'https://images.unsplash.com/photo-1531366936337-7c912a4589a7?w=600&q=80',
    category: '自然风景', catColor: '#6366f1', type: 'video', cost: 10, likes: 8923, liked: false,
  },
  {
    id: 17,
    title: '深海光影',
    prompt: '深海生物光影，水母群发着幽蓝光芒，黑暗的海底，神秘氛围，科学与艺术结合，8K',
    cover: 'https://images.unsplash.com/photo-1518020382113-a7e8fc38eac9?w=600&q=80',
    category: '抽象艺术', catColor: '#a78bfa', type: 'image', cost: 2, likes: 3456, liked: false,
  },
  {
    id: 18,
    title: '产品主图',
    prompt: '高端产品商业摄影，皮具/腕表/珠宝，特写细节，专业布光，纯白背景，商业广告级，8K',
    cover: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&q=80',
    category: '产品商业', catColor: '#64748b', type: 'image', cost: 2, likes: 2190, liked: false,
  },
  {
    id: 19,
    title: '古建筑群',
    prompt: '中国古建筑群全景，故宫风格，红墙黄瓦，庄严大气，晴天，天空云朵，建筑摄影，8K',
    cover: 'https://images.unsplash.com/photo-1547981609-4b6bfe67ca0b?w=600&q=80',
    category: '城市建筑', catColor: '#06b6d4', type: 'image', cost: 2, likes: 2987, liked: false,
  },
]

const filteredTemplates = computed(() => {
  let list = templates
  if (activeCategory.value !== 'all') {
    const catMap: Record<string, string> = {
      city: '城市建筑', nature: '自然风景', person: '人物写真',
      anime: '动漫风格', chinese: '国风水墨', cyberpunk: '赛博朋克',
      product: '产品商业', abstract: '抽象艺术',
    }
    list = list.filter(t => t.category === catMap[activeCategory.value])
  }
  if (searchKeyword.value.trim()) {
    const kw = searchKeyword.value.trim().toLowerCase()
    list = list.filter(t =>
      t.title.toLowerCase().includes(kw) ||
      t.prompt.toLowerCase().includes(kw) ||
      t.category.toLowerCase().includes(kw)
    )
  }
  return list
})

function useTemplate(tmpl: TemplateItem) {
  selectedTemplate.value = tmpl
}

function toggleLike(tmpl: TemplateItem) {
  tmpl.liked = !tmpl.liked
  if (tmpl.liked) tmpl.likes++
  else tmpl.likes--
}

async function startGenerate() {
  if (!selectedTemplate.value) return
  generating.value = true
  const tmpl = selectedTemplate.value
  const path = tmpl.type === 'video' ? '/ai/video' : '/ai/image'
  await new Promise(resolve => setTimeout(resolve, 500))
  generating.value = false
  selectedTemplate.value = null
  router.push({ path, query: { prompt: tmpl.prompt, template: tmpl.title } })
}
</script>

<style scoped>
.inspiration-page {
  padding: 32px;
  min-height: 100%;
}

.page-inner {
  max-width: 1300px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 28px;
}

/* 顶部标题 */
.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
}

.title-icon {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title-icon :deep(svg) { width: 28px; height: 28px; }

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 26px;
  font-weight: 800;
  color: var(--color-text-primary);
}

.page-subtitle {
  font-size: 14px;
  color: var(--color-text-muted);
  margin-top: 4px;
}

/* 搜索框 */
.search-wrap {
  position: relative;
  width: 280px;
  flex-shrink: 0;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-text-muted);
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: 10px 12px 10px 38px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 10px;
  color: var(--color-text-primary);
  font-size: 13px;
  outline: none;
  transition: all 0.2s ease;
}

.search-input::placeholder { color: var(--color-text-muted); }
.search-input:focus {
  border-color: rgba(124, 58, 237, 0.4);
  background: rgba(124, 58, 237, 0.05);
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.08);
}

/* 分类标签 */
.category-tabs {
  position: relative;
}

.tabs-scroll {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 4px;
  scrollbar-width: none;
}

.tabs-scroll::-webkit-scrollbar { display: none; }

.tab-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 7px 14px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 9999px;
  color: var(--color-text-muted);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  flex-shrink: 0;
  font-family: inherit;
}

.tab-btn:hover {
  background: rgba(255, 255, 255, 0.06);
  color: var(--color-text-primary);
}

.tab-btn.active {
  background: rgba(124, 58, 237, 0.12);
  border-color: rgba(124, 58, 237, 0.35);
  color: #c4b5fd;
}

.tab-icon {
  width: 14px;
  height: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.tab-icon :deep(svg) { width: 14px; height: 14px; }

.tab-count {
  font-size: 10px;
  font-weight: 600;
  padding: 1px 5px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 9999px;
  color: var(--color-text-muted);
}

.tab-btn.active .tab-count {
  background: rgba(124, 58, 237, 0.2);
  color: #a78bfa;
}

/* 统计 */
.template-stats {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--color-text-muted);
}

.stats-num {
  font-size: 15px;
  font-weight: 700;
  color: #a78bfa;
}

.stats-sep { opacity: 0.4; }

/* 模板网格 */
.templates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 18px;
}

.template-card {
  border-radius: 16px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  cursor: pointer;
  transition: all 0.25s ease;
  animation: card-in 0.4s ease forwards;
  opacity: 0;
}

@keyframes card-in {
  0% { opacity: 0; transform: translateY(16px); }
  100% { opacity: 1; transform: translateY(0); }
}

.template-card:hover {
  border-color: rgba(124, 58, 237, 0.3);
  transform: translateY(-3px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
}

/* 封面 */
.tmpl-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.tmpl-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s ease;
}

.template-card:hover .tmpl-cover img {
  transform: scale(1.06);
}

/* 悬停遮罩 */
.tmpl-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.template-card:hover .tmpl-overlay { opacity: 1; }

.use-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 9px 20px;
  background: var(--gradient-dream);
  color: white;
  font-size: 13px;
  font-weight: 700;
  border-radius: 9999px;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 20px rgba(124, 58, 237, 0.4);
}

.use-btn:hover { transform: scale(1.05); }

.like-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 5px 12px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 9999px;
  color: #f87171;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.like-btn:hover { background: rgba(244, 63, 94, 0.15); }

/* 类型标签 */
.tmpl-type-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  font-size: 10px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 9999px;
  backdrop-filter: blur(8px);
}

.tmpl-type-badge.image {
  background: rgba(124, 58, 237, 0.7);
  color: white;
}

.tmpl-type-badge.video {
  background: rgba(6, 182, 212, 0.7);
  color: white;
}

/* 信息区 */
.tmpl-info {
  padding: 14px 16px 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.tmpl-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.tmpl-category-tag {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  font-weight: 600;
}

.cat-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  flex-shrink: 0;
}

.tmpl-cost {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 11px;
  font-weight: 600;
  color: #f59e0b;
  flex-shrink: 0;
}

.tmpl-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1.3;
}

.tmpl-prompt {
  font-size: 12px;
  color: var(--color-text-muted);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px;
  gap: 12px;
  text-align: center;
}

.empty-icon {
  width: 64px;
  height: 64px;
  opacity: 0.15;
}

.empty-icon :deep(svg) { width: 64px; height: 64px; }

.empty-state p {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-secondary);
}

.empty-tip {
  font-size: 13px !important;
  font-weight: 400 !important;
  color: var(--color-text-muted) !important;
}

/* ========== 使用模板弹窗 ========== */
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 300;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.use-modal {
  width: 100%;
  max-width: 680px;
  border-radius: 20px;
  overflow: hidden;
  padding: 0;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 0;
}

.modal-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.modal-title :deep(svg) { width: 18px; height: 18px; }

.modal-close {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: var(--color-text-muted);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.1);
  color: var(--color-text-primary);
}

.modal-body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  padding: 20px 24px 24px;
}

.modal-preview {
  border-radius: 12px;
  overflow: hidden;
  height: 240px;
}

.modal-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.modal-info {
  padding-left: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.modal-tmpl-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.modal-prompt-label {
  font-size: 11px;
  font-weight: 700;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.modal-prompt-text {
  font-size: 12px;
  color: var(--color-text-secondary);
  line-height: 1.7;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 8px;
  padding: 10px 12px;
  max-height: 100px;
  overflow-y: auto;
}

.modal-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.meta-tag {
  font-size: 11px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 9999px;
}

.meta-tag.image { background: rgba(124, 58, 237, 0.12); color: #a78bfa; }
.meta-tag.video { background: rgba(6, 182, 212, 0.12); color: #22d3ee; }

.meta-cost {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #f59e0b;
  font-weight: 600;
}

.use-submit-btn {
  width: 100%;
  padding: 12px;
  font-size: 14px;
  margin-top: auto;
}

.use-submit-btn :deep(svg) { width: 16px; height: 16px; }

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
  display: inline-block;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* 弹窗动画 */
.modal-enter-active, .modal-leave-active {
  transition: all 0.25s ease;
}
.modal-enter-from, .modal-leave-to {
  opacity: 0;
}
.modal-enter-from .use-modal, .modal-leave-to .use-modal {
  transform: scale(0.95) translateY(20px);
}

/* ========== 响应式 ========== */
@media (max-width: 900px) {
  .page-header { flex-direction: column; align-items: flex-start; }
  .search-wrap { width: 100%; }
  .templates-grid { grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); }
  .modal-body { grid-template-columns: 1fr; }
  .modal-preview { height: 200px; }
  .modal-info { padding-left: 0; padding-top: 16px; }
}

@media (max-width: 600px) {
  .inspiration-page { padding: 20px 16px; }
  .templates-grid { grid-template-columns: repeat(2, 1fr); gap: 12px; }
}
</style>
