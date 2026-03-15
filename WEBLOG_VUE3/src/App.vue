<template>
  <div class="app-shell">
    <div class="snowfall-layer" aria-hidden="true">
      <span
        v-for="flake in snowflakes"
        :key="flake.id"
        class="snowfall-layer__flake"
        :style="flake.style"
      >❄</span>
    </div>

    <div class="app-content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { watch } from 'vue'
import { useRoute } from 'vue-router'
import { useSiteConfigStore } from '@/stores/siteConfig'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const siteConfig = useSiteConfigStore()
const userStore = useUserStore()
const snowflakes = [
  { id: 1, style: '--left: 6%; --size: 16px; --duration: 28s; --delay: -5s; --drift: -18px; --opacity: 0.55;' },
  { id: 2, style: '--left: 15%; --size: 20px; --duration: 34s; --delay: -18s; --drift: 22px; --opacity: 0.48;' },
  { id: 3, style: '--left: 21%; --size: 12px; --duration: 24s; --delay: -7s; --drift: 12px; --opacity: 0.46;' },
  { id: 4, style: '--left: 27%; --size: 14px; --duration: 26s; --delay: -10s; --drift: -16px; --opacity: 0.52;' },
  { id: 5, style: '--left: 33%; --size: 17px; --duration: 29s; --delay: -15s; --drift: -22px; --opacity: 0.5;' },
  { id: 6, style: '--left: 39%; --size: 18px; --duration: 31s; --delay: -13s; --drift: 19px; --opacity: 0.46;' },
  { id: 7, style: '--left: 45%; --size: 11px; --duration: 23s; --delay: -9s; --drift: -10px; --opacity: 0.44;' },
  { id: 8, style: '--left: 52%; --size: 15px; --duration: 27s; --delay: -3s; --drift: -14px; --opacity: 0.54;' },
  { id: 9, style: '--left: 58%; --size: 13px; --duration: 25s; --delay: -17s; --drift: 11px; --opacity: 0.45;' },
  { id: 10, style: '--left: 64%; --size: 19px; --duration: 33s; --delay: -20s; --drift: 20px; --opacity: 0.47;' },
  { id: 11, style: '--left: 70%; --size: 12px; --duration: 24s; --delay: -6s; --drift: -15px; --opacity: 0.49;' },
  { id: 12, style: '--left: 76%; --size: 13px; --duration: 25s; --delay: -8s; --drift: -12px; --opacity: 0.56;' },
  { id: 13, style: '--left: 81%; --size: 15px; --duration: 28s; --delay: -19s; --drift: 14px; --opacity: 0.43;' },
  { id: 14, style: '--left: 86%; --size: 17px; --duration: 30s; --delay: -16s; --drift: 16px; --opacity: 0.5;' },
  { id: 15, style: '--left: 90%; --size: 11px; --duration: 22s; --delay: -12s; --drift: -9px; --opacity: 0.42;' },
  { id: 16, style: '--left: 94%; --size: 14px; --duration: 29s; --delay: -11s; --drift: -10px; --opacity: 0.53;' }
]

// 监听路由变化，动态更新标题
watch(
  () => route.meta.title,
  (newTitle) => {
    if (newTitle) {
      if (route.path.startsWith('/admin')) {
        document.title = `${newTitle} - ThoughtFlow 管理`
      } else {
        document.title = `${newTitle} - ThoughtFlow`
      }
    }
  },
  { immediate: true }
)

// 监听整个路由对象，处理动态路由（如文章详情）
watch(
  () => route.fullPath,
  () => {
    if (route.meta.title) {
      const newTitle = route.meta.title
      if (route.path.startsWith('/admin')) {
        document.title = `${newTitle} - ThoughtFlow 管理`
      } else {
        document.title = `${newTitle} - ThoughtFlow`
      }
    }
  }
)
</script>

<style scoped>
.app-shell {
  position: relative;
  min-height: 100vh;
}

.app-content {
  position: relative;
  z-index: 1;
}

.snowfall-layer {
  position: fixed;
  inset: 0;
  z-index: 2;
  pointer-events: none;
  overflow: hidden;
}

.snowfall-layer__flake {
  position: absolute;
  top: -10%;
  left: var(--left);
  font-size: var(--size);
  line-height: 1;
  color: rgba(255, 255, 255, var(--opacity));
  text-shadow:
    0 0 10px rgba(255, 255, 255, 0.85),
    0 0 18px rgba(255, 255, 255, 0.46),
    0 1px 2px rgba(148, 163, 184, 0.18);
  animation: snowfall-fall var(--duration) linear infinite;
  animation-delay: var(--delay);
  will-change: transform;
  opacity: 0.9;
}

@keyframes snowfall-fall {
  0% {
    transform: translate3d(0, -12vh, 0) rotate(0deg) scale(0.92);
  }

  50% {
    transform: translate3d(calc(var(--drift) * 0.55), 54vh, 0) rotate(120deg) scale(1);
  }

  100% {
    transform: translate3d(var(--drift), 112vh, 0) rotate(240deg) scale(0.96);
  }
}

@media (prefers-reduced-motion: reduce) {
  .snowfall-layer__flake {
    animation: none;
    opacity: 0.28;
  }
}
</style>


<style>
/* 自定义顶部加载 Loading 颜色 */
#nprogress .bar {
   background: #409eff!important;
}
</style>
