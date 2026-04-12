<template>
  <div class="dream-app">
    <router-view v-slot="{ Component, route }">
      <!-- 有 app 布局的页面：用 AppLayout 包裹 -->
      <AppLayout v-if="route.meta.layout === 'app'">
        <component :is="Component" :key="route.path" />
      </AppLayout>
      <!-- 落地页 / 登录页：无 layout，直接渲染 -->
      <transition v-else name="page" mode="out-in">
        <component :is="Component" :key="route.path" />
      </transition>
    </router-view>
  </div>
</template>

<script setup lang="ts">
import AppLayout from '@/components/layout/AppLayout.vue'
</script>

<style scoped>
.dream-app {
  min-height: 100vh;
  background: var(--color-bg-base);
}

.page-enter-active,
.page-leave-active {
  transition: opacity 0.25s var(--ease-smooth), transform 0.25s var(--ease-smooth);
}

.page-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
