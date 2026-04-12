const imageStyleMap: Record<string, string> = {
  cyberpunk: '赛博朋克，霓虹灯，潮湿反光街道，强烈冷暖对比，电影级细节',
  realistic: '写实摄影，自然肤感/材质，真实镜头语言，商业摄影质感',
  anime: '高完成度动漫插画，清晰线稿，角色表情鲜明，色彩层次丰富',
  oil: '油画质感，厚涂笔触，画布纹理明显，艺术展陈级氛围',
  '3d': '3D 渲染，体积光，精细材质，具备真实空间透视',
  chinese: '国风水墨，东方留白，诗意构图，传统美学气质',
  fantasy: '魔幻奇幻，超现实场景，发光元素，史诗氛围',
  minimal: '极简主义，主体明确，背景克制，干净高级',
}

const imageSizeMap: Record<string, string> = {
  '1:1': '1:1 方图，适合封面和商品主图',
  '16:9': '16:9 横版，适合横幅和场景叙事',
  '9:16': '9:16 竖版，适合短视频封面和信息流',
  '4:3': '4:3 横版，兼顾主体与环境',
}

const videoDurationMap: Record<string, string> = {
  '5s': '5 秒，强钩子，1 个核心记忆点，节奏紧凑',
  '15s': '15 秒，适合短视频投流，包含钩子-展开-收束',
  '30s': '30 秒，适合完整叙事，包含冲突、展开和收尾动作',
}

function normalizePromptText(text: string) {
  return text.replace(/\s+/g, ' ').replace(/[，,;；]+/g, '，').trim()
}

function joinSections(sections: Array<string | undefined>) {
  return sections.filter(Boolean).join('\n')
}

export function buildImagePrompt(input: {
  rawPrompt: string
  styleId?: string
  sizeId?: string
}) {
  const rawPrompt = normalizePromptText(input.rawPrompt)
  const style = imageStyleMap[input.styleId || ''] || '高质量视觉创作，主体突出，细节清晰'
  const size = imageSizeMap[input.sizeId || ''] || '构图比例自然，适配主流生成尺寸'

  return joinSections([
    '### Instruction',
    '请将以下需求生成 1 条可直接用于 AI 图片模型的中文视觉提示词。',
    '### Context',
    `风格约束：${style}`,
    `画幅约束：${size}`,
    '请突出主体、场景、光线、镜头感和材质细节，保持描述具体，不要空泛堆词。',
    '### Input',
    rawPrompt,
    '### Output Indicator',
    '输出单条中文图片 Prompt，直接给出最终画面描述，不要解释。',
  ])
}

export function buildVideoPrompt(input: {
  rawPrompt: string
  durationId?: string
  ratioLabel?: string
  styleTypeLabel?: string
  visualStyleLabel?: string
  cameraMovementLabel?: string
  shotFocusLabel?: string
  qualityLevelLabel?: string
}) {
  const rawPrompt = normalizePromptText(input.rawPrompt)
  const duration = videoDurationMap[input.durationId || ''] || '时长紧凑，镜头表达明确'

  return joinSections([
    '### Instruction',
    '请将以下需求生成 1 条可直接用于 AI 视频模型的中文视频提示词。',
    '### Context',
    `时长策略：${duration}`,
    input.ratioLabel ? `画幅约束：${input.ratioLabel}` : undefined,
    input.styleTypeLabel ? `风格类型：${input.styleTypeLabel}` : undefined,
    input.visualStyleLabel ? `视觉风格：${input.visualStyleLabel}` : undefined,
    input.cameraMovementLabel ? `运镜方式：${input.cameraMovementLabel}` : undefined,
    input.shotFocusLabel ? `画面重点：${input.shotFocusLabel}` : undefined,
    input.qualityLevelLabel ? `质量目标：${input.qualityLevelLabel}` : undefined,
    '请明确主体、场景、镜头运动、情绪节奏、光线氛围和结尾动作，保证镜头连续且便于生成。',
    '### Input',
    rawPrompt,
    '### Output Indicator',
    '输出单条中文视频 Prompt，直接给出最终镜头描述，不要解释。',
  ])
}

export function buildHotVideoOptimizationInput(input: {
  rawPrompt: string
  industry?: string
  platform?: string
  targetModel?: string
}) {
  const rawPrompt = normalizePromptText(input.rawPrompt)

  return joinSections([
    '### Instruction',
    '请把下面内容重写为适合爆款短视频生成与拆解优化的原始需求底稿。',
    '### Context',
    input.industry ? `行业：${input.industry}` : undefined,
    input.platform ? `平台：${input.platform}` : undefined,
    input.targetModel ? `目标模型：${input.targetModel}` : undefined,
    '要求聚焦受众、钩子、卖点、镜头节奏、情绪氛围和转化意图，用明确表达替代模糊描述。',
    '### Input',
    rawPrompt,
    '### Output Indicator',
    '保留关键信息，压缩冗余表达，形成适合进一步优化的结构化原始需求。',
  ])
}

export function getImageStyleLabel(styleId: string) {
  return imageStyleMap[styleId] || ''
}

export function getImageSizeLabel(sizeId: string) {
  return imageSizeMap[sizeId] || ''
}

export function getVideoDurationLabel(durationId: string) {
  return videoDurationMap[durationId] || ''
}
