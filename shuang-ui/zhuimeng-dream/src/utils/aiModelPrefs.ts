const IMAGE_MODEL_KEY = 'ai:last-image-model-id'
const VIDEO_MODEL_KEY = 'ai:last-video-model-id'

export function getLastImageModelId() {
  const raw = localStorage.getItem(IMAGE_MODEL_KEY)
  return raw ? Number(raw) : null
}

export function setLastImageModelId(id: number) {
  localStorage.setItem(IMAGE_MODEL_KEY, String(id))
}

export function getLastVideoModelId() {
  const raw = localStorage.getItem(VIDEO_MODEL_KEY)
  return raw ? Number(raw) : null
}

export function setLastVideoModelId(id: number) {
  localStorage.setItem(VIDEO_MODEL_KEY, String(id))
}
