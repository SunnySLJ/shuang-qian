<template>
  <div class="particle-container" ref="containerRef">
    <canvas ref="canvasRef" class="particle-canvas" />
    <!-- 渐变光斑 -->
    <div class="orb orb-1" />
    <div class="orb orb-2" />
    <div class="orb orb-3" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const containerRef = ref<HTMLDivElement>()
const canvasRef = ref<HTMLCanvasElement>()
let animFrameId = 0

interface Particle {
  x: number
  y: number
  size: number
  speedX: number
  speedY: number
  opacity: number
  color: string
}

const colors = ['#7c3aed', '#06b6d4', '#ec4899', '#8b5cf6', '#22d3ee']

function initCanvas() {
  const canvas = canvasRef.value
  const container = containerRef.value
  if (!canvas || !container) return

  const dpr = window.devicePixelRatio || 1
  canvas.width = container.offsetWidth * dpr
  canvas.height = container.offsetHeight * dpr
  canvas.style.width = container.offsetWidth + 'px'
  canvas.style.height = container.offsetHeight + 'px'
  const ctx = canvas.getContext('2d')!
  ctx.scale(dpr, dpr)

  const w = container.offsetWidth
  const h = container.offsetHeight

  const particles: Particle[] = []
  for (let i = 0; i < 80; i++) {
    particles.push({
      x: Math.random() * w,
      y: Math.random() * h,
      size: Math.random() * 2 + 0.5,
      speedX: (Math.random() - 0.5) * 0.3,
      speedY: (Math.random() - 0.5) * 0.3,
      opacity: Math.random() * 0.5 + 0.1,
      color: colors[Math.floor(Math.random() * colors.length)],
    })
  }

  function draw() {
    ctx.clearRect(0, 0, w, h)
    for (const p of particles) {
      ctx.beginPath()
      ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2)
      ctx.fillStyle = p.color
      ctx.globalAlpha = p.opacity
      ctx.fill()
      ctx.globalAlpha = 1

      // 连接附近粒子
      for (const q of particles) {
        const dx = p.x - q.x
        const dy = p.y - q.y
        const dist = Math.sqrt(dx * dx + dy * dy)
        if (dist < 120) {
          ctx.beginPath()
          ctx.moveTo(p.x, p.y)
          ctx.lineTo(q.x, q.y)
          ctx.strokeStyle = p.color
          ctx.globalAlpha = 0.06 * (1 - dist / 120)
          ctx.lineWidth = 0.5
          ctx.stroke()
          ctx.globalAlpha = 1
        }
      }

      p.x += p.speedX
      p.y += p.speedY

      if (p.x < 0) p.x = w
      if (p.x > w) p.x = 0
      if (p.y < 0) p.y = h
      if (p.y > h) p.y = 0
    }
    animFrameId = requestAnimationFrame(draw)
  }

  draw()
}

onMounted(() => {
  initCanvas()
  window.addEventListener('resize', initCanvas)
})

onUnmounted(() => {
  cancelAnimationFrame(animFrameId)
  window.removeEventListener('resize', initCanvas)
})
</script>

<style scoped>
.particle-container {
  position: fixed;
  inset: 0;
  overflow: hidden;
  z-index: 0;
  pointer-events: none;
}

.particle-canvas {
  position: absolute;
  inset: 0;
}

/* 渐变光斑 */
.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, #7c3aed 0%, transparent 70%);
  top: -200px;
  left: -100px;
  animation: drift 25s ease-in-out infinite;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, #06b6d4 0%, transparent 70%);
  bottom: -150px;
  right: -100px;
  animation: drift 30s ease-in-out infinite reverse;
  animation-delay: -10s;
}

.orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, #ec4899 0%, transparent 70%);
  top: 40%;
  left: 50%;
  transform: translateX(-50%);
  animation: drift 20s ease-in-out infinite;
  animation-delay: -5s;
  opacity: 0.15;
}

@keyframes drift {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(40px, -60px) scale(1.05); }
  50% { transform: translate(-30px, 40px) scale(0.95); }
  75% { transform: translate(60px, 20px) scale(1.02); }
}
</style>
