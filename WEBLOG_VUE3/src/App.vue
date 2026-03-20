<template>
  <div class="app-shell">
    <div class="atmosphere-layer" aria-hidden="true">
      <div class="atmosphere-layer__mist atmosphere-layer__mist--left"></div>
      <div class="atmosphere-layer__mist atmosphere-layer__mist--right"></div>
      <div class="atmosphere-layer__veil"></div>
      <span
        v-for="orb in orbs"
        :key="orb.id"
        class="atmosphere-layer__orb"
        :style="orb.style"
      ></span>
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
const orbs = [
  { id: 1, style: '--left: 7%; --top: 12%; --size: 10px; --delay: 0s; --duration: 12s;' },
  { id: 2, style: '--left: 18%; --top: 22%; --size: 6px; --delay: 1.5s; --duration: 10s;' },
  { id: 3, style: '--left: 31%; --top: 10%; --size: 8px; --delay: 2.1s; --duration: 13s;' },
  { id: 4, style: '--left: 46%; --top: 18%; --size: 5px; --delay: 0.7s; --duration: 11s;' },
  { id: 5, style: '--left: 63%; --top: 14%; --size: 9px; --delay: 2.8s; --duration: 12.5s;' },
  { id: 6, style: '--left: 79%; --top: 9%; --size: 6px; --delay: 1.2s; --duration: 10.8s;' },
  { id: 7, style: '--left: 91%; --top: 20%; --size: 8px; --delay: 3.1s; --duration: 13.5s;' },
  { id: 8, style: '--left: 12%; --top: 56%; --size: 7px; --delay: 2.5s; --duration: 12.2s;' },
  { id: 9, style: '--left: 37%; --top: 68%; --size: 9px; --delay: 0.8s; --duration: 11.6s;' },
  { id: 10, style: '--left: 72%; --top: 63%; --size: 7px; --delay: 1.9s; --duration: 12.8s;' }
]

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

.atmosphere-layer {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

.atmosphere-layer__mist,
.atmosphere-layer__veil {
  position: absolute;
  border-radius: 9999px;
  filter: blur(80px);
}

.atmosphere-layer__mist--left {
  left: -10%;
  top: -4%;
  width: 44rem;
  height: 26rem;
  background: radial-gradient(circle, rgba(144, 179, 241, 0.24) 0%, rgba(190, 214, 245, 0.1) 48%, transparent 74%);
}

.atmosphere-layer__mist--right {
  right: -8%;
  top: 20%;
  width: 36rem;
  height: 30rem;
  background: radial-gradient(circle, rgba(177, 205, 245, 0.22) 0%, rgba(224, 234, 248, 0.1) 44%, transparent 76%);
}

.atmosphere-layer__veil {
  left: 18%;
  bottom: -12%;
  width: 42rem;
  height: 20rem;
  background: radial-gradient(circle, rgba(245, 238, 226, 0.32) 0%, rgba(236, 242, 251, 0.16) 38%, transparent 68%);
}

.atmosphere-layer__orb {
  position: absolute;
  left: var(--left);
  top: var(--top);
  width: var(--size);
  height: var(--size);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow:
    0 0 10px rgba(255, 255, 255, 0.35),
    0 0 18px rgba(149, 184, 236, 0.2);
  animation: floatPulse var(--duration) ease-in-out infinite;
  animation-delay: var(--delay);
}

@keyframes floatPulse {
  0%, 100% {
    opacity: 0.4;
    transform: translateY(0) scale(0.92);
  }

  50% {
    opacity: 0.9;
    transform: translateY(-10px) scale(1.08);
  }
}

@media (prefers-reduced-motion: reduce) {
  .atmosphere-layer__orb {
    animation: none;
    opacity: 0.55;
  }
}
</style>

<style>
#nprogress .bar {
   background: linear-gradient(90deg, #7a9de6 0%, #9cb9ef 48%, #c4d8f8 100%) !important;
   box-shadow: 0 0 12px rgba(122, 157, 230, 0.24);
}
</style>
