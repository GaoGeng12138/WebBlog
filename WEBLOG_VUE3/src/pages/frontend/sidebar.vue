<template>
  <aside class="w-full">
    <div class="space-y-6 lg:sticky lg:top-24">
      <!-- Author Card -->
      <div class="bg-white rounded-lg p-5 shadow-sm border border-gray-100 text-center">
        <div class="flex justify-center mb-3">
          <el-avatar :size="70" :src="user.avatar || 'https://api.dicebear.com/7.x/shapes/svg?seed=DefaultUser&backgroundColor=c0aede'
"
            class="border-3 border-white shadow-sm" />
        </div>
        <h3 class="text-lg font-bold text-gray-900 mb-1">{{ user.nickname || user.username }}</h3>
        <p class="text-gray-600 text-xs mb-3">{{ user.introduction ||""}}</p>
        <!-- Social Media Links -->
        <div v-if="hasSocialLinks" class="flex justify-center space-x-3 mb-3">
          <!-- GitHub -->
          <a 
            v-if="siteConfig.siteInfo.githubEnabled && siteConfig.siteInfo.githubShowFront && user.githubUrl" 
            :href="user.githubUrl" 
            target="_blank"
            rel="noopener noreferrer"
            class="text-gray-700 hover:text-gray-900 transition-colors"
            title="GitHub"
          >
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
              <path
                d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z" />
            </svg>
          </a>
          <!-- Twitter -->
          <a 
            v-if="siteConfig.siteInfo.twitterEnabled && siteConfig.siteInfo.twitterShowFront && user.twitterUrl" 
            :href="user.twitterUrl" 
            target="_blank"
            rel="noopener noreferrer"
            class="text-blue-400 hover:text-blue-600 transition-colors"
            title="Twitter"
          >
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
              <path
                d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723a10.054 10.054 0 01-3.127 1.195a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417a9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z" />
            </svg>
          </a>
          <!-- Weibo -->
          <a 
            v-if="siteConfig.siteInfo.weiboEnabled && siteConfig.siteInfo.weiboShowFront && user.weiboUrl" 
            :href="user.weiboUrl" 
            target="_blank"
            rel="noopener noreferrer"
            class="text-red-500 hover:text-red-700 transition-colors"
            title="微博"
          >
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
              <path
                d="M9.63 17.95c-3.58.34-6.67-1.27-6.9-3.6-.24-2.32 2.48-4.48 6.06-4.82 3.58-.34 6.68 1.27 6.91 3.6.24 2.32-2.48 4.48-6.07 4.82zm10.23-4.39c-.3-.1-.51-.17-.35-.62.34-.98.38-1.82.01-2.42-.68-1.11-2.54-1.05-4.67-.03 0 0-.67.33-.5-.27.33-1.23.28-2.26-.3-2.86-1.31-1.37-4.8.05-7.79 3.17-2.24 2.33-3.54 4.79-3.54 7.03 0 4.14 5.31 6.66 10.5 6.66 6.81 0 11.33-3.95 11.33-7.09 0-1.9-1.6-2.97-3.69-2.57zm2.76-6.15c-1.56-1.74-3.88-2.4-5.99-2.01-.45.08-.73.52-.65.97.08.45.52.74.97.65 1.59-.29 3.35.2 4.53 1.51 1.18 1.31 1.57 3.11 1.18 4.72-.12.44.14.9.58 1.02.44.12.9-.14 1.02-.58.52-2.14.02-4.54-1.64-6.28z" />
            </svg>
          </a>
        </div>
      </div>

      <!-- Categories -->
      <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
        <h3 class="text-lg font-bold text-gray-900 mb-4 pb-2 border-b border-gray-100">分类目录</h3>
        <div class="space-y-2">
          <a v-for="category in categories" :key="category.id" href="#"
            class="flex items-center justify-between py-2 px-3 rounded-lg hover:bg-gray-50 transition-colors group"
            @click.prevent="goToCategory(category.id)">
            <span class="text-gray-700 group-hover:text-blue-600">{{ category.name }}</span>
            <span class="text-xs bg-gray-100 text-gray-500 rounded-full px-2 py-1">{{ category.count }}</span>
          </a>
        </div>
      </div>

      <!-- Tags -->
      <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
        <h3 class="text-lg font-bold text-gray-900 mb-4 pb-2 border-b border-gray-100">热门标签</h3>
        <div class="flex flex-wrap gap-2">
          <el-tag v-for="(tag, index) in tags" :key="index" type="success"
            class="rounded-full px-3 py-1 text-sm cursor-pointer hover:scale-105 transition-transform"
            @click="goToTag(tag)">
            {{ tag }}
          </el-tag>
        </div>
      </div>

      <!-- Newsletter -->
      <div class="bg-gradient-to-r from-blue-500 to-indigo-600 rounded-xl p-6 text-white">
        <h3 class="text-lg font-bold mb-2">订阅更新</h3>
        <p class="text-blue-100 text-sm mb-4">第一时间获取最新文章推送</p>
        <div class="space-y-3">
          <input type="email" placeholder="邮箱地址"
            class="w-full px-4 py-2 rounded-lg text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-300">
          <button class="w-full bg-white text-blue-600 font-medium py-2 rounded-lg hover:bg-blue-50 transition-colors">
            订阅
          </button>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getAllCategoryList } from '@/api/frontend/category'
import { getAllTagList } from '@/api/frontend/tag'
import { useUserStore } from '@/stores/user'
import { useSiteConfigStore } from '@/stores/siteConfig'
import { getToken } from '@/composables/cookie'

const router = useRouter()
const userStore = useUserStore()
const siteConfig = useSiteConfigStore()
const user = computed(() => userStore.frontendUserInfo)

// 检查是否有任何社交链接需要显示
const hasSocialLinks = computed(() => {
  console.log('Checking social links:', siteConfig.siteInfo, user.value)
  return (siteConfig.siteInfo.githubEnabled && siteConfig.siteInfo.githubShowFront && user.value.githubUrl) ||
         (siteConfig.siteInfo.twitterEnabled && siteConfig.siteInfo.twitterShowFront && user.value.twitterUrl) ||
         (siteConfig.siteInfo.weiboEnabled && siteConfig.siteInfo.weiboShowFront && user.value.weiboUrl)
})

const categories = ref([])
const tags = ref([])
const loading = ref(true)

onMounted(() => {
  loadCategories()
  loadTags()
})



async function loadCategories() {
  try {
    const res = await getAllCategoryList()
    if (res && res.success) {
      // Transform the data to match the expected format
      categories.value = res.data.map(category => ({
        id: category.id,
        name: category.name,
        count: category.articleCount || 0
      }))
    }
  } catch (error) {
    console.error('Failed to load categories:', error)
    // Fallback to mock data if API fails
    categories.value = [
      { id: 1, name: 'Java', count: 12 },
      { id: 2, name: 'Postman', count: 8 },
      { id: 3, name: 'Vue.js', count: 15 },
      { id: 4, name: 'Spring Boot', count: 7 }
    ]
  }
}

async function loadTags() {
  try {
    const res = await getAllTagList()
    if (res && res.success) {
      // Transform the data to match the expected format
      tags.value = res.data.map(tag => tag.name || tag)
    }
  } catch (error) {
    console.error('Failed to load tags:', error)
    // Fallback to mock data if API fails
    tags.value = ['工具', 'API 调试', 'Python', '从 0 到 1', '后端开发', '前端框架', '数据库', '微服务']
  }
}

function goToCategory(categoryId) {
  router.push(`/category/${categoryId}`)
}

function goToTag(tagName) {
  // For tags, we'll need to find a way to get the tag ID or use the name
  // For now, we'll redirect to the tag list page
  router.push('/tag')
}
</script>