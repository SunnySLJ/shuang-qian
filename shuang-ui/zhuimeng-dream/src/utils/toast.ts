/**
 * 简单的 Toast 消息提示工具
 * 不依赖 element-plus 等重型 UI 库
 */

type ToastType = 'success' | 'error' | 'warning' | 'info'

interface ToastOptions {
  message: string
  type: ToastType
  duration?: number
}

function showToast({ message, type, duration = 3000 }: ToastOptions) {
  // 移除已存在的 toast
  const existing = document.querySelector('.dream-toast')
  if (existing) {
    existing.remove()
  }

  // 创建 toast 元素
  const toast = document.createElement('div')
  toast.className = `dream-toast dream-toast-${type}`

  // 图标
  const icons: Record<ToastType, string> = {
    success: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>',
    error: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>',
    warning: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>',
    info: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>',
  }

  toast.innerHTML = `
    <div class="dream-toast-icon">${icons[type]}</div>
    <span class="dream-toast-message">${message}</span>
  `

  // 样式
  toast.style.cssText = `
    position: fixed;
    top: 80px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 20px;
    background: rgba(15, 10, 31, 0.95);
    backdrop-filter: blur(20px);
    border: 1px solid ${type === 'success' ? 'rgba(16, 185, 129, 0.5)' : type === 'error' ? 'rgba(239, 68, 68, 0.5)' : type === 'warning' ? 'rgba(245, 158, 11, 0.5)' : 'rgba(124, 58, 237, 0.5)'};
    border-radius: 12px;
    box-shadow: 0 16px 48px rgba(0, 0, 0, 0.5);
    color: ${type === 'success' ? '#34d399' : type === 'error' ? '#f87171' : type === 'warning' ? '#fbbf24' : '#c4b5fd'};
    font-size: 14px;
    font-weight: 500;
    z-index: 9999;
    animation: toast-slide-in 0.3s ease;
  `

  // 添加动画样式
  if (!document.querySelector('#dream-toast-styles')) {
    const style = document.createElement('style')
    style.id = 'dream-toast-styles'
    style.textContent = `
      @keyframes toast-slide-in {
        from { transform: translateX(-50%) translateY(-20px); opacity: 0; }
        to { transform: translateX(-50%) translateY(0); opacity: 1; }
      }
      @keyframes toast-fade-out {
        from { opacity: 1; }
        to { opacity: 0; }
      }
    `
    document.head.appendChild(style)
  }

  document.body.appendChild(toast)

  // 自动移除
  setTimeout(() => {
    toast.style.animation = 'toast-fade-out 0.3s ease'
    setTimeout(() => toast.remove(), 300)
  }, duration)
}

export const toast = {
  success: (message: string) => showToast({ message, type: 'success' }),
  error: (message: string) => showToast({ message, type: 'error' }),
  warning: (message: string) => showToast({ message, type: 'warning' }),
  info: (message: string) => showToast({ message, type: 'info' }),
}
