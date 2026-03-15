<template>
  <header 
    class="sticky top-0 z-50 transition-all duration-300 border-b"
    :class="[
      isScrolled 
        ? 'backdrop-blur-xl bg-[#fdf7eb]/92 shadow-sm border-[#eadfca]/70' 
        : 'bg-[#fdf7eb]/82 backdrop-blur-sm border-transparent'
    ]"
  >
    <div class="max-w-[1700px] mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-[78px] lg:h-[96px]">
        
        <!-- Left: Logo -->
        <div class="flex-shrink-0 flex items-center cursor-pointer group" @click="$router.push('/')">
          <div class="relative flex items-center justify-center overflow-hidden rounded-xl p-1.5 transition-all duration-300 group-hover:bg-blue-50">
            <img 
              :src="displayLogo"
              @error="handleLogoError"
              class="h-11 lg:h-15 w-auto max-w-[240px] object-contain transition-transform duration-500 group-hover:scale-105" 
              :alt="siteConfig.siteInfo.title || 'logo'" 
            />
          </div>
        </div>

        <!-- Middle: Navigation (Desktop) -->
        <div class="hidden md:flex flex-1 items-center justify-center px-10">
          <Navigation />
        </div>

        <!-- Right: Actions -->
        <div class="flex items-center gap-4 lg:gap-6">
          <!-- User Badge -->
          <UserBadge />

          <!-- Mobile Menu Button -->
          <button 
            @click="isMobileMenuOpen = !isMobileMenuOpen"
            class="md:hidden p-2 rounded-xl text-gray-500 hover:text-blue-600 hover:bg-blue-50 transition-colors focus:outline-none"
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
      <div v-if="isMobileMenuOpen" class="md:hidden absolute top-full left-0 w-full bg-[#fdf7eb]/96 backdrop-blur-xl border-b border-[#eadfca]/80 shadow-xl pb-6 pt-2">
        <div class="px-4 space-y-6">
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
import { computed, onMounted, onUnmounted, ref } from 'vue'
import Navigation from '@/components/frontend/Navigation.vue'
import UserBadge from '@/components/frontend/UserBadge.vue'
import { useUserStore } from '@/stores/user'
import { useSiteConfigStore } from '@/stores/siteConfig'

const fallbackLogo = '/thoughtflow_logo.png'

const userStore = useUserStore()
const siteConfig = useSiteConfigStore()
const user = computed(() => userStore.frontendUserInfo)

// 检查用户是否已登录
const isLoggedIn = computed(() => {
  return !!user.value && !!user.value.userId
})

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
}

.header-divider__glow {
  position: absolute;
  left: 50%;
  bottom: 0;
  width: min(52vw, 720px);
  height: 10px;
  transform: translateX(-50%);
  background: radial-gradient(circle, rgba(251, 191, 36, 0.18) 0%, rgba(251, 191, 36, 0.06) 42%, transparent 76%);
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
  background: linear-gradient(90deg, #2563eb 0%, #3b82f6 35%, #0ea5e9 68%, #22c55e 100%);
  transition: transform 120ms linear;
  box-shadow: 0 0 14px rgba(59, 130, 246, 0.26);
}
</style>
