<template>
  <div class="flex items-center gap-3">
    <div v-if="loading" class="text-sm text-gray-400">加载中...</div>
    <div v-else-if="user && user.nickname" class="flex items-center gap-2">
      <el-dropdown trigger="click">
        <div class="flex items-center gap-2 cursor-pointer hover:opacity-80 transition-opacity">
          <img :src="user.avatar || '/pics/default-avatar.png'" alt="avatar" class="w-8 h-8 rounded-full object-cover border border-gray-200" />
          <span class="text-sm font-medium text-gray-900">{{ user.nickname || user.username }}</span>
          <span class="text-xs text-gray-500">更多</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click.native.prevent="goUserCenter">用户中心</el-dropdown-item>
            <el-dropdown-item divided @click.native.prevent="logout">登出</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    <div v-else>
      <el-button type="primary" size="default" round @click="$router.push('/login')">登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { removeToken } from '@/composables/cookie'

const router = useRouter()
const userStore = useUserStore()
const user = computed(() => userStore.frontendUserInfo)
const loading = computed(() => false)

const logout = () => {
  // Call the store's logout function to properly clear all user info
  userStore.logout()
  router.push('/')
}

const goUserCenter = () => {
  router.push('/user')
}
</script>

<style scoped>
.el-dropdown-link {
  cursor: pointer;
}
</style>