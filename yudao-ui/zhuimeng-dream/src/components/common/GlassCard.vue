<template>
  <div class="glass-card" :class="[`variant-${variant}`, { glow: glow }]" :style="customStyle">
    <slot />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'default' | 'bordered' | 'elevated' | 'flat'
  glow?: boolean
  padding?: string
  radius?: string
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  glow: false,
  padding: '24px',
  radius: '16px',
})

const customStyle = computed(() => ({
  padding: props.padding,
  borderRadius: props.radius,
}))
</script>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: var(--radius-glass);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.glass-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: linear-gradient(135deg, rgba(255,255,255,0.03) 0%, transparent 50%);
  pointer-events: none;
}

.glass-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(124, 58, 237, 0.1);
  transform: translateY(-2px);
}

/* 变体 */
.variant-bordered {
  border-color: rgba(124, 58, 237, 0.2);
}

.variant-elevated {
  background: rgba(255, 255, 255, 0.05);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.variant-flat {
  background: rgba(255, 255, 255, 0.02);
  backdrop-filter: blur(10px);
}

/* 发光效果 */
.glow {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), 0 0 60px rgba(124, 58, 237, 0.15);
}

.glow::after {
  content: '';
  position: absolute;
  inset: -1px;
  border-radius: inherit;
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.3), rgba(6, 182, 212, 0.3), rgba(236, 72, 153, 0.3));
  z-index: -1;
  filter: blur(20px);
  opacity: 0.5;
}
</style>
