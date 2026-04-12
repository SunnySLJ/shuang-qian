import { post } from '@/utils/request'

export interface PromptOptimizeReqVO {
  rawPrompt: string
  industry?: string
  platform?: string
  targetModel?: string
  mode?: string
}

export interface PromptOptimizeResult {
  originalPrompt?: string
  fullPromptEn?: string
  fullPromptZh?: string
  cameraMovement?: string
  emotion?: string
  lighting?: string
  style?: string
  duration?: string
  subject?: string
  background?: string
  audio?: string
  hook?: string
  targetAudience?: string
  sellingPoints?: string
  shotBreakdown?: string
  copyStrategy?: string
  platformTips?: string
  optimizationReasoning?: string
}

export function optimizeHotVideoPrompt(data: PromptOptimizeReqVO) {
  return post<PromptOptimizeResult>('/ai/prompt/optimize/hot-video', data)
}
