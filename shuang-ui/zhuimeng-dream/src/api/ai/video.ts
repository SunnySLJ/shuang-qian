/**
 * AI 视频生成 API
 * 对接后端: /ai/video
 */

import request, { get, post } from '@/utils/request'

// ========== 请求 VO ==========

/** 视频生成请求 */
export interface VideoGenerateReqVO {
  prompt: string
  duration?: number
  modelId?: number       // 模型 ID（可选）
  imageUrl?: string
  ratio?: string
  styleType?: string
  visualStyle?: string
  cameraMovement?: string
  shotFocus?: string
  qualityLevel?: string
  clientId?: string
}

/** 图生视频请求 */
export interface ImageToVideoReqVO {
  imageUrl: string       // 参考图片 URL
  prompt?: string        // 视频描述（可选）
  duration?: number
  modelId?: number
  clientId?: string
}

export interface VideoPromptFromImagesReqVO {
  imageUrls: string[]
  userPrompt?: string
  promptLevel?: string
}

export interface VideoPromptFromImagesRespVO {
  visualSummary?: string
  basePrompt?: string
  optimizedPrompt?: string
}

// ========== 响应 VO ==========

/** 视频记录 */
export interface VideoRecord {
  id: number
  prompt: string
  generationType?: number
  thumbnailUrl?: string
  coverUrl?: string
  picUrl?: string
  videoUrl?: string
  outputUrl?: string
  duration?: number
  status: number        // 与后端 AiImageStatusEnum：10 进行中，20 成功，30 失败
  statusName?: string
  errorMessage?: string
  cost?: number
  createTime?: string
  bizOptions?: Record<string, unknown>
  inputVideoUrl?: string
}

export interface VideoGenerateRespVO {
  id: number           // 返回生成的记录 ID
}

export interface VideoPageRespVO {
  list: VideoRecord[]
  total: number
}

// ========== API 方法 ==========

/** 文生视频 */
export function textToVideo(data: VideoGenerateReqVO) {
  return post<number>('/ai/video/text-to-video', data)
}

/** 图生视频 */
export function imageToVideo(data: ImageToVideoReqVO) {
  return post<number>('/ai/video/image-to-video', data)
}

/** 黄金6秒拼接 */
export function golden6s(data: { prompt: string; modelId?: number }) {
  return post<number>('/ai/video/golden-6s', data)
}

/** 获取视频列表（分页） */
export function getVideoPage(params: { userId?: number; pageNo?: number; pageSize?: number }) {
  return get<VideoPageRespVO>('/ai/video/page', params)
}

/** 获取视频详情 */
export function getVideoDetail(id: number) {
  return get<VideoRecord>('/ai/video/detail', { id })
}

/** 同步视频状态（轮询） */
export function syncVideoStatus(id: number) {
  return post<Boolean>('/ai/video/sync-status', null, { params: { id } })
}

// ========== 视频拆解 API ==========

/** 视频拆解请求 */
export interface VideoAnalyzeReqVO {
  videoUrl: string
  modelId?: number
  provider?: string
  clientId?: string
}

export interface VideoResolveRespVO {
  extractedUrl?: string
  resolvedPageUrl?: string
  previewVideoUrl?: string
  platform?: string
  previewable?: boolean
}

/** 视频拆解结果 */
export interface VideoAnalyzeResult {
  id: number
  inputVideoUrl?: string
  prompt?: string
  outputUrl?: string
  status: number
  statusName?: string
  options?: Record<string, unknown>
  bizOptions?: Record<string, unknown>
  errorMessage?: string
  cost?: number
  createTime?: string
  finishTime?: string
}

/** 视频拆解 - 提取脚本 */
export function extractScript(data: VideoAnalyzeReqVO) {
  return post<number>('/ai/video/extract-script', data)
}

/** 视频拆解 - 分析元素 */
export function analyzeElements(data: VideoAnalyzeReqVO) {
  return post<number>('/ai/video/analyze-elements', data)
}

/** 视频拆解 - 生成提示词 */
export function generatePrompt(data: VideoAnalyzeReqVO) {
  return post<number>('/ai/video/generate-prompt', data)
}

export function resolveVideoLink(rawText: string) {
  return get<VideoResolveRespVO>('/ai/video/resolve-link', { rawText })
}

export function generatePromptFromImages(data: VideoPromptFromImagesReqVO) {
  return post<VideoPromptFromImagesRespVO>('/ai/video/generate-prompt-from-images', data)
}
