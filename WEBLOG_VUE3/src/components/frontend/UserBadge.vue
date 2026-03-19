<template>
  <div class="flex items-center">
    <div v-if="loading" class="text-sm text-gray-400">加载中...</div>

    <!-- 已登录：显示头像和下拉菜单 -->
    <div v-else-if="user && user.nickname" class="flex items-center gap-2">
      <el-dropdown trigger="click">
        <div class="flex items-center gap-2 cursor-pointer hover:opacity-80 transition-opacity">
          <img :src="displayAvatar" @error="handleAvatarError" alt="avatar" class="w-8 h-8 rounded-full object-cover border border-gray-200" />
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click.native.prevent="goUserCenter">用户中心</el-dropdown-item>
            <el-dropdown-item divided @click.native.prevent="logout">登出</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 未登录：显示小人图标，点击跳转登录页 -->
    <div v-else>
      <button 
        @click="goLogin"
        class="w-9 h-9 rounded-full flex items-center justify-center text-gray-500 hover:text-blue-600 hover:bg-blue-50 transition-all duration-200"
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
</script>
