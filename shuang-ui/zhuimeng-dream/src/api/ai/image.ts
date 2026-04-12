/**
 * AI 图片生成 API
 * 对接后端: /ai/image
 */

import request, { get, post } from '@/utils/request'

// ========== 请求 VO ==========

/** 图片生成请求 */
export interface ImageGenerateReqVO {
  prompt: string
  style?: string        // 艺术风格：cyberpunk, realistic, anime 等
  width?: number        // 宽度，默认 1024
  height?: number       // 高度，默认 1024
  modelId?: number     // 模型 ID（可选）
  referenceImageUrl?: string  // 参考图片 URL（图生图时传）
  negativePrompt?: string
  clientId?: string
}

// ========== 响应 VO ==========

/** 图片记录 */
export interface ImageRecord {
  id: number
  prompt: string
  referenceImageUrl?: string
  outputUrl?: string
  thumbnailUrl?: string
  width?: number
  height?: number
  status: number        // 10=生成中，20=成功，30=失败
  statusName?: string
  errorMessage?: string
  cost?: number          // 消耗积分
  createTime?: string
}

export interface ImageGenerateRespVO {
  id: number           // 返回生成的记录 ID
}

export interface ImagePageRespVO {
  list: ImageRecord[]
  total: number
}

// ========== API 方法 ==========

/** 生成图片 */
export function generateImage(data: ImageGenerateReqVO) {
  return post<number>('/ai/image/generate', data)
}

/** 获取我的图片列表（简单列表） */
export function getMyImageList(limit: number = 20) {
  return get<ImageRecord[]>('/ai/image/list', { limit })
}

/** 分页获取我的图片列表 */
export function getMyImagePage(params: { pageNo?: number; pageSize?: number }) {
  return get<ImagePageRespVO>('/ai/image/page', params)
}

/** 获取图片详情 */
export function getImageDetail(id: number) {
  return get<ImageRecord>('/ai/image/get', { id })
}

/** 删除图片 */
export function deleteImage(id: number) {
  return request.delete('/ai/image/delete', { params: { id } })
}
