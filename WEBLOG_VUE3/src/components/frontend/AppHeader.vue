<template>
  <header 
    class="sticky top-0 z-50 transition-all duration-300 border-b"
    :class="[
      isScrolled 
        ? 'backdrop-blur-xl bg-[rgba(250,253,255,0.82)] shadow-[0_16px_34px_rgba(108,137,184,0.12)] border-[rgba(149,171,210,0.18)]' 
        : 'bg-[rgba(247,250,255,0.64)] backdrop-blur-sm border-transparent'
    ]"
  >
    <div class="max-w-[1700px] mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-[68px] sm:h-[76px] lg:h-[96px]">
        
        <!-- Left: Logo -->
        <div class="flex-shrink-0 flex items-center cursor-pointer group" @click="$router.push('/')">
          <div class="relative flex items-center justify-center overflow-hidden rounded-xl p-1 transition-all duration-300 group-hover:bg-[rgba(148,176,231,0.12)]">
            <img 
              :src="displayLogo"
              @error="handleLogoError"
              class="h-9 sm:h-10 lg:h-15 w-auto max-w-[200px] sm:max-w-[240px] object-contain transition-transform duration-500 group-hover:scale-105" 
              :alt="siteConfig.siteInfo.title || 'logo'" 
            />
          </div>
        </div>

        <!-- Middle: Navigation (Desktop) -->
        <div class="hidden md:flex flex-1 items-center justify-center px-10">
          <Navigation />
        </div>

        <!-- Right: Actions -->
        <div class="flex items-center gap-2 sm:gap-4 lg:gap-6">
          <!-- User Badge -->
          <div class="hidden sm:block">
            <UserBadge />
          </div>

          <!-- Mobile Menu Button -->
          <button 
            @click="isMobileMenuOpen = !isMobileMenuOpen"
            class="md:hidden inline-flex items-center justify-center p-2.5 rounded-xl text-[var(--cosmic-muted)] hover:text-[var(--cosmic-blue-deep)] hover:bg-[rgba(148,176,231,0.12)] transition-colors focus:outline-none"
            :aria-expanded="isMobileMenuOpen"
            aria-label="打开导航菜单"
          >
            <svg v-if="!isMobileMenuOpen" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
            </svg>
            <svg v-else class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Mobile Menu Overlay -->
    <transition
      enter-active-class="transition duration-300 ease-out transform origin-top"
      enter-from-class="scale-y-0 opacity-0"
      enter-to-class="scale-y-100 opacity-100"
      leave-active-class="transition duration-200 ease-in transform origin-top"
      leave-from-class="scale-y-100 opacity-100"
      leave-to-class="scale-y-0 opacity-0"
    >
      <div v-if="isMobileMenuOpen" class="md:hidden absolute top-full left-0 w-full bg-[rgba(250,253,255,0.98)] backdrop-blur-xl border-b border-[rgba(149,171,210,0.18)] shadow-xl pb-5 pt-3">
        <div class="px-4 space-y-4">
          <div class="sm:hidden rounded-2xl border border-[rgba(149,171,210,0.14)] bg-white/80 px-4 py-3 shadow-[0_12px_26px_rgba(120,146,186,0.08)]">
            <UserBadge />
          </div>
          <Navigation direction="vertical" @navigate="isMobileMenuOpen = false" /> 
        </div>
      </div>
    </transition>

    <!-- 底部分割线：基础分割线负责稳定的页面分层，进度条负责滚动反馈 -->
    <div class="header-divider" aria-hidden="true">
      <span class="header-divider__base"></span>
      <span class="header-divider__glow"></span>
      <div class="header-progress">
        <div class="header-progress__bar" :style="{ transform: `scaleX(${scrollProgress})` }"></div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import Navigation from '@/components/frontend/Navigation.vue'
import UserBadge from '@/components/frontend/UserBadge.vue'
import { useSiteConfigStore } from '@/stores/siteConfig'

const fallbackLogo = `${import.meta.env.BASE_URL}thoughtflow_logo.png`
const route = useRoute()

const siteConfig = useSiteConfigStore()

const props = defineProps({
  keyword: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:keyword', 'search'])

const displayLogo = computed(() => siteConfig.siteInfo.logoUrl || fallbackLogo)

const handleLogoError = (event) => {
  if (event.target.src.endsWith(fallbackLogo)) {
    return
  }
  event.target.src = fallbackLogo
}

// 状态控制
const isMobileMenuOpen = ref(false)
const isScrolled = ref(false)
const scrollProgress = ref(0)

// 监听滚动，实现吸顶时的样式变化
const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
  const scrollTop = window.scrollY
  const scrollHeight = document.documentElement.scrollHeight - window.innerHeight
  scrollProgress.value = scrollHeight > 0 ? Math.min(scrollTop / scrollHeight, 1) : 0
}

watch(
  () => route.fullPath,
  () => {
    isMobileMenuOpen.value = false
  }
)

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
  handleScroll() // 初始化检查
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.header-divider {
  position: absolute;
  inset-inline: 0;
  bottom: 0;
  pointer-events: none;
}

.header-divider__base {
  position: absolute;
  inset-inline: 0;
  bottom: 0;
  height: 1px;
  background: linear-gradient(90deg, rgba(226, 232, 240, 0.1), rgba(203, 213, 225, 0.9), rgba(226, 232, 240, 0.1));
  opacity: 0.55;
}

.header-divider__glow {
  position: absolute;
  left: 50%;
  bottom: 0;
  width: min(52vw, 720px);
  height: 10px;
  transform: translateX(-50%);
  background: radial-gradient(circle, rgba(148, 176, 231, 0.3) 0%, rgba(148, 176, 231, 0.08) 42%, transparent 76%);
  filter: blur(6px);
}

.header-progress {
  position: absolute;
  inset-inline: 0;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: rgba(148, 163, 184, 0.08);
  overflow: hidden;
}

.header-progress__bar {
  height: 100%;
  width: 100%;
  transform-origin: left center;
  background: linear-gradient(90deg, #6e92d8 0%, #94b0e7 48%, #c8d8f3 100%);
  transition: transform 120ms linear;
  box-shadow: 0 0 14px rgba(110, 146, 216, 0.2);
}
</style>
