<template>
  <div class="flex items-center">
    <div v-if="loading" class="text-sm text-[var(--cosmic-muted)]">加载中...</div>

    <!-- 已登录：显示头像和下拉菜单 -->
    <div v-else-if="user && user.nickname" class="flex items-center gap-2">
      <el-dropdown trigger="click">
        <div class="flex items-center gap-2 cursor-pointer rounded-full border border-transparent bg-white/45 px-2 py-1.5 shadow-[0_10px_24px_rgba(120,146,184,0.08)] transition-all hover:border-[rgba(148,176,231,0.16)] hover:bg-white/70 hover:shadow-[0_14px_28px_rgba(120,146,184,0.12)]">
          <img :src="displayAvatar" @error="handleAvatarError" alt="avatar" class="w-8 h-8 rounded-full object-cover border border-[rgba(149,171,210,0.28)] shadow-[0_0_0_4px_rgba(255,255,255,0.38)]" />
          <div class="hidden min-w-0 flex-col leading-tight sm:flex">
            <span class="truncate text-sm font-semibold text-slate-700">{{ user.nickname }}</span>
            <div class="mt-1 flex items-center gap-2 text-[11px] text-slate-400">
              <span class="inline-flex items-center gap-1">
                <span class="h-2 w-2 rounded-full bg-emerald-500 shadow-[0_0_0_3px_rgba(16,185,129,0.14)]"></span>
                在线
              </span>
            </div>
          </div>
          <svg class="hidden h-4 w-4 text-slate-400 sm:block" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
            <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5" />
          </svg>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="goUserCenter">用户中心</el-dropdown-item>
            <el-dropdown-item @click="goPublish">写文章</el-dropdown-item>
            <el-dropdown-item divided @click="logout">登出</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 未登录：显示小人图标，点击跳转登录页 -->
    <div v-else>
      <button 
        @click="goLogin"
        class="w-9 h-9 rounded-full flex items-center justify-center text-[var(--cosmic-muted)] hover:text-[var(--cosmic-blue-deep)] hover:bg-[rgba(148,176,231,0.12)] transition-all duration-200 border border-transparent hover:border-[rgba(148,176,231,0.18)]"
        title="登录"
      >
        <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const user = computed(() => userStore.frontendUserInfo)
const loading = computed(() => false)
const defaultAvatar = `${import.meta.env.BASE_URL}default-avatar.svg`
const displayAvatar = computed(() => user.value?.avatar || defaultAvatar)

const handleAvatarError = (event) => {
  if (event.target.src.endsWith('default-avatar.svg')) {
    return
  }
  event.target.src = defaultAvatar
}

// 跳转登录页
const goLogin = () => {
  router.push('/login')
}

// 登出
const logout = () => {
  userStore.logout()
  router.push('/')
}

// 跳转用户中心
const goUserCenter = () => {
  router.push('/user')
}

// 跳转写文章页
const goPublish = () => {
  router.push('/article/publish')
}
</script>
