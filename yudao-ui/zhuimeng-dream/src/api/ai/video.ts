/**
 * AI 视频生成 API
 * 对接后端: /ai/video
 */

import request, { get, post } from '@/utils/request'

// ========== 请求 VO ==========

/** 视频生成请求 */
export interface VideoGenerateReqVO {
  prompt: string
  duration?: string       // 时长：5s, 15s, 30s
  modelId?: number       // 模型 ID（可选）
}

/** 图生视频请求 */
export interface ImageToVideoReqVO {
  imageUrl: string       // 参考图片 URL
  prompt?: string        // 视频描述（可选）
  duration?: string
  modelId?: number
}

// ========== 响应 VO ==========

/** 视频记录 */
export interface VideoRecord {
  id: number
  prompt: string
  thumbnailUrl?: string
  videoUrl?: string
  duration?: string
  status: number        // 0=待处理，1=生成中，2=成功，3=失败
  statusName?: string
  errorMessage?: string
  cost: number          // 消耗积分
  createTime: string
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
