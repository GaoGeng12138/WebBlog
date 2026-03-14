<template>
  <header 
    class="sticky top-0 z-50 border-b border-gray-100 transition-all duration-300"
    :class="[isScrolled ? 'backdrop-blur-md bg-white/80 shadow-sm' : 'bg-transparent']"
  >
    <div class="max-w-[1600px] mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        
        <div class="flex-shrink-0 flex items-center cursor-pointer group" @click="$router.push('/')">
          <!-- Logo -->
          <div class="relative bg-transparent z-10 flex items-center justify-center">
            <img 
              :src="siteConfig.siteInfo.logoUrl || '/vite.svg'" 
              class="h-12 w-auto max-w-[200px] mr-3 object-contain transition-all duration-300 group-hover:scale-110  bg-transparent" 
              style="background: transparent !important;"
              :alt="siteConfig.siteInfo.title || 'logo'" 
            />
          </div>
          
          <!-- 标题和标语 -->
          <div class="flex flex-col relative">
            <h1 class="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-600 to-purple-600 tracking-tight leading-tight">
              <span v-if="isLoggedIn && user.nickname">
                {{ user.nickname }}
                <span class="mx-1.5">の</span>
              </span>
              {{ siteConfig.siteInfo.title || 'My Blog' }}
            </h1>
            <p v-if="siteConfig.siteInfo.slogan" class="text-xs text-gray-500 font-medium tracking-wide hidden sm:block">
              {{ siteConfig.siteInfo.slogan }}
            </p>
            
            <!-- 描述浮层（鼠标悬停显示） -->
            <div 
              v-if="siteConfig.siteInfo.description" 
              class="absolute top-full left-0 mt-2 w-64 bg-white rounded-lg shadow-xl p-4 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-300 z-50 border border-gray-100"
            >
              <div class="flex items-start gap-2">
                <svg class="w-4 h-4 text-blue-500 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <p class="text-xs text-gray-600 leading-relaxed">
                  {{ siteConfig.siteInfo.description }}
                </p>
              </div>
              <!-- 三角箭头 -->
              <div class="absolute -top-2 left-4 w-4 h-4 bg-white border-l border-t border-gray-100 transform rotate-45"></div>
            </div>
          </div>
        </div>

        <div class="hidden md:block flex-1 px-8">
           <div class="flex justify-center">
             <Navigation />
           </div>
        </div>

        <div class="flex items-center gap-3">
          
          <div class="relative hidden sm:block group">
            <input 
              :value="keyword" 
              @input="$emit('update:keyword', $event.target.value)"
              @keyup.enter="$emit('search')"
              placeholder="Search..." 
              class="bg-gray-100 border-none text-sm rounded-full px-4 py-1.5 pl-9 
                     focus:ring-2 focus:ring-blue-500/50 focus:bg-white transition-all duration-300 
                     w-32 focus:w-64 placeholder-gray-400"
            />
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg class="h-4 w-4 text-gray-400 group-focus-within:text-blue-500 transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>

          <UserBadge />

          <button 
            @click="isMobileMenuOpen = !isMobileMenuOpen"
            class="md:hidden p-2 rounded-md text-gray-500 hover:bg-gray-100 transition-colors focus:outline-none"
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

    <transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="transform -translate-y-2 opacity-0"
      enter-to-class="transform translate-y-0 opacity-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="transform translate-y-0 opacity-100"
      leave-to-class="transform -translate-y-2 opacity-0"
    >
      <div v-if="isMobileMenuOpen" class="md:hidden border-t border-gray-100 bg-white">
        <div class="px-4 py-4 space-y-4">
          <div class="relative">
             <input 
              :value="keyword" 
              @input="$emit('update:keyword', $event.target.value)"
              @keyup.enter="$emit('search')"
              placeholder="搜索文章..." 
              class="w-full bg-gray-100 border-none rounded-lg px-4 py-2 pl-10 focus:ring-2 focus:ring-blue-500" 
            />
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
               <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
               </svg>
            </div>
          </div>
          
          <Navigation direction="vertical" /> 
        </div>
      </div>
    </transition>
  </header>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import Navigation from '@/components/frontend/Navigation.vue'
import UserBadge from '@/components/frontend/UserBadge.vue'
import { useUserStore } from '@/stores/user'
import { useSiteConfigStore } from '@/stores/siteConfig'

const userStore = useUserStore()
const siteConfig = useSiteConfigStore()
const user = computed(() => userStore.frontendUserInfo)

// 检查用户是否已登录
const isLoggedIn = computed(() => {
  return !!user.value && !!user.value.userId
})

defineProps({
  keyword: {
    type: String,
    default: ''
  }
})

defineEmits(['update:keyword', 'search'])

// 状态控制
const isMobileMenuOpen = ref(false)
const isScrolled = ref(false)

// 监听滚动，实现吸顶时的样式变化
const handleScroll = () => {
  isScrolled.value = window.scrollY > 10
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* 如果你需要更复杂的背景模糊，确保 tailwind.config.js 开启了 backdrop-filter */
</style>