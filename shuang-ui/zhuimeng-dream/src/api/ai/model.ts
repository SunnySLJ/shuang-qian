import { get } from '@/utils/request'

export interface AiModelOption {
  id: number
  name: string
  model: string
  platform: string
  type: number
  sort?: number
  status?: number
}

export function getAiModelSimpleList(type: number, platform?: string) {
  return get<AiModelOption[]>('/ai/model/simple-list', { type, platform })
}
