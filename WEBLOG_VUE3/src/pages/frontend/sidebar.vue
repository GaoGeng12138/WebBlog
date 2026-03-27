<template>
  <aside class="w-full">
    <div class="space-y-6 lg:sticky lg:top-24">
      <!-- Author Card -->
      <div class="bg-white rounded-3xl p-6 shadow-sm border border-gray-100 text-center relative overflow-hidden group">
        <!-- 装饰背景图案 -->
        <div class="absolute -top-12 -right-12 w-32 h-32 bg-blue-50 rounded-full blur-2xl opacity-50 group-hover:bg-blue-100 transition-colors duration-500"></div>
        <div class="absolute -bottom-8 -left-8 w-24 h-24 bg-indigo-50 rounded-full blur-xl opacity-50 group-hover:bg-indigo-100 transition-colors duration-500"></div>
        
        <div class="relative z-10 flex justify-center mb-4 mt-2">
          <div class="relative">
            <el-avatar :size="80" :src="displayAvatar"
              class="border-4 border-white shadow-md z-10 relative group-hover:scale-105 transition-transform duration-500" />
            <!-- 头像呼吸发光效果 -->
            <div class="absolute inset-0 bg-blue-400 rounded-full blur-md opacity-20 group-hover:opacity-40 animate-pulse-slow z-0"></div>
          </div>
        </div>
        <h3 class="relative z-10 text-xl font-bold text-gray-900 mb-2">{{ user.nickname || user.username }}</h3>
        <p class="relative z-10 text-gray-500 text-sm mb-5 px-2">{{ user.introduction || "很高兴相遇，期待与你发现更多精彩！"}}</p>
        
        <!-- Social Media Links -->
        <div v-if="hasSocialLinks" class="relative z-10 flex justify-center space-x-4 mb-2">
          <!-- GitHub -->
          <a 
            v-if="siteConfig.siteInfo.githubEnabled && siteConfig.siteInfo.githubShowFront && user.githubUrl" 
            :href="user.githubUrl" 
            target="_blank"
            rel="noopener noreferrer"
            class="w-10 h-10 rounded-full bg-gray-50 hover:bg-gray-100 flex items-center justify-center text-gray-700 hover:text-gray-900 transition-all duration-300 hover:scale-110 shadow-sm"
            title="GitHub"
          >
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z" />
            </svg>
          </a>
          <!-- Twitter -->
          <a 
            v-if="siteConfig.siteInfo.twitterEnabled && siteConfig.siteInfo.twitterShowFront && user.twitterUrl" 
            :href="user.twitterUrl" 
            target="_blank"
            rel="noopener noreferrer"
            class="w-10 h-10 rounded-full bg-blue-50 hover:bg-blue-100 flex items-center justify-center text-blue-400 hover:text-blue-600 transition-all duration-300 hover:scale-110 shadow-sm"
            title="Twitter"
          >
            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
              <path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723a10.054 10.054 0 01-3.127 1.195a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417a9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z" />
            </svg>
          </a>
          <!-- Weibo -->
          <a 
            v-if="siteConfig.siteInfo.weiboEnabled && siteConfig.siteInfo.weiboShowFront && user.weiboUrl" 
            :href="user.weiboUrl" 
            target="_blank"
            rel="noopener noreferrer"
            class="w-10 h-10 rounded-full bg-red-50 hover:bg-red-100 flex items-center justify-center text-red-500 hover:text-red-600 transition-all duration-300 hover:scale-110 shadow-sm"
            title="微博"
          >
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
              <path d="M9.63 17.95c-3.58.34-6.67-1.27-6.9-3.6-.24-2.32 2.48-4.48 6.06-4.82 3.58-.34 6.68 1.27 6.91 3.6.24 2.32-2.48 4.48-6.07 4.82zm10.23-4.39c-.3-.1-.51-.17-.35-.62.34-.98.38-1.82.01-2.42-.68-1.11-2.54-1.05-4.67-.03 0 0-.67.33-.5-.27.33-1.23.28-2.26-.3-2.86-1.31-1.37-4.8.05-7.79 3.17-2.24 2.33-3.54 4.79-3.54 7.03 0 4.14 5.31 6.66 10.5 6.66 6.81 0 11.33-3.95 11.33-7.09 0-1.9-1.6-2.97-3.69-2.57zm2.76-6.15c-1.56-1.74-3.88-2.4-5.99-2.01-.45.08-.73.52-.65.97.08.45.52.74.97.65 1.59-.29 3.35.2 4.53 1.51 1.18 1.31 1.57 3.11 1.18 4.72-.12.44.14.9.58 1.02.44.12.9-.14 1.02-.58.52-2.14.02-4.54-1.64-6.28z" />
            </svg>
          </a>
        </div>
      </div>

      <!-- Categories -->
      <div class="bg-white rounded-3xl p-6 shadow-sm border border-gray-100">
        <h3 class="text-base font-bold text-gray-900 mb-5 relative pl-3">
          <span class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-4 bg-blue-500 rounded-full"></span>
          分类目录
        </h3>
        <div class="space-y-3">
          <div v-for="category in categories" :key="category.id" class="space-y-2">
            <button
              type="button"
              class="flex w-full items-center justify-between rounded-xl py-2.5 px-3 text-left transition-colors group hover:bg-blue-50"
              @click="goToCategory(category.id)"
            >
              <span class="flex items-center gap-2 font-medium text-gray-600 transition-colors group-hover:text-blue-600">
                <el-icon class="text-gray-400 group-hover:text-blue-500"><Folder /></el-icon>
                {{ category.name }}
              </span>
              <span class="rounded-full bg-gray-100 px-2.5 py-0.5 text-xs font-semibold text-gray-500 transition-colors group-hover:bg-blue-100 group-hover:text-blue-600">{{ category.count }}</span>
            </button>

            <div v-if="category.children && category.children.length" class="ml-3 space-y-1 border-l border-dashed border-blue-100 pl-3">
              <button
                v-for="child in category.children"
                :key="child.id"
                type="button"
                class="flex w-full items-center justify-between rounded-xl px-3 py-2 text-left transition-colors group hover:bg-blue-50"
                @click="goToCategory(child.id)"
              >
                <span class="flex items-center gap-2 text-sm font-medium text-gray-500 transition-colors group-hover:text-blue-600">
                  <span class="h-1.5 w-1.5 rounded-full bg-blue-300"></span>
                  {{ child.name }}
                </span>
                <span class="rounded-full bg-gray-100 px-2 py-0.5 text-[11px] font-semibold text-gray-400 transition-colors group-hover:bg-blue-100 group-hover:text-blue-600">{{ child.count }}</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Tags -->
      <div class="bg-white rounded-3xl p-6 shadow-sm border border-gray-100">
        <h3 class="text-base font-bold text-gray-900 mb-5 relative pl-3">
          <span class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-4 bg-blue-500 rounded-full"></span>
          热门标签
        </h3>
        <div class="flex flex-wrap gap-2.5">
          <span v-for="(tag, index) in tags" :key="index"
            class="inline-flex items-center px-3 py-1.5 rounded-lg text-xs font-medium bg-gray-50 text-gray-600 border border-transparent hover:bg-white hover:border-gray-200 hover:shadow-sm hover:text-blue-600 transition-all cursor-pointer"
            @click="goToTag(tag)">
            <span class="text-gray-400 mr-1">#</span>{{ tag }}
          </span>
        </div>
      </div>

      <!-- Newsletter -->
      <div class="bg-gradient-to-br from-blue-600 to-indigo-700 rounded-3xl p-8 text-white shadow-lg relative overflow-hidden">
        <div class="absolute top-0 right-0 w-32 h-32 bg-white rounded-full blur-3xl opacity-10 translate-x-1/2 -translate-y-1/2"></div>
        <h3 class="text-xl font-bold mb-2 relative z-10">订阅更新</h3>
        <p class="text-blue-100 text-sm mb-6 relative z-10 leading-relaxed">第一时间获取最新文章与见解推送，不错过任何干货。</p>
        <div class="space-y-3 relative z-10">
          <input type="email" placeholder="邮箱地址"
            class="w-full px-4 py-3 rounded-xl bg-white/10 border border-white/20 text-white placeholder-blue-200 focus:outline-none focus:ring-2 focus:ring-white/50 focus:bg-white/20 transition-all backdrop-blur-sm">
          <button class="w-full bg-white text-blue-700 font-bold py-3 rounded-xl hover:bg-gray-50 transition-colors shadow-sm">
            立即订阅
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
const defaultAvatar = `${import.meta.env.BASE_URL}default-avatar.svg`
const displayAvatar = computed(() => user.value?.avatar || defaultAvatar)

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
      const data = Array.isArray(res.data) ? res.data : (res.data ? [res.data] : [])
      categories.value = buildCategoryTree(data.map(category => ({
        id: category.id,
        name: category.name,
        count: category.articleCount || 0,
        parentId: category.parentId ?? null,
        showOnFront: category.showOnFront !== false,
        children: []
      })))
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

function buildCategoryTree(list) {
  const nodeMap = new Map()
  const roots = []

  list.forEach((category, index) => {
    if (!category.showOnFront) {
      return
    }

    nodeMap.set(category.id, {
      ...category,
      order: index,
      children: []
    })
  })

  nodeMap.forEach((node) => {
    const parentId = node.parentId
    const hasParent = parentId !== null && parentId !== undefined && Number(parentId) !== 0
    const parentNode = hasParent ? nodeMap.get(parentId) : null

    if (parentNode) {
      parentNode.children.push(node)
    } else {
      roots.push(node)
    }
  })

  const sortTree = (nodes) => {
    nodes.sort((left, right) => left.order - right.order)
    nodes.forEach(item => sortTree(item.children))
  }

  sortTree(roots)
  return roots
}

function goToTag(tagName) {
  // For tags, we'll need to find a way to get the tag ID or use the name
  // For now, we'll redirect to the tag list page
  router.push('/tag')
}
</script>
