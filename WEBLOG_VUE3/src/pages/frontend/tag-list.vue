<template>
  <div class="min-h-screen bg-[#F8FAFC] flex flex-col">
    <!-- Header -->
    <AppHeader :keyword="keyword" @update:keyword="keyword = $event" @search="search" />

    <!-- Main Content -->
    <main class="flex-1 max-w-[1500px] w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 lg:py-12">
      <div class="flex flex-col lg:flex-row gap-8 lg:gap-12">
        <!-- Tags Section -->
        <section class="flex-1 min-w-0">
          <div class="mb-8">
            <h2 class="text-3xl font-extrabold text-gray-900 tracking-tight">探索标签</h2>
            <p class="mt-2 text-gray-500">通过标签快速定位你感兴趣的技术内容</p>
          </div>

          <div v-if="loading" class="py-20 text-center">
            <div class="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
            <p class="mt-4 text-sm text-gray-500 font-medium">全力加载标签数据 ...</p>
          </div>

          <div v-else>
            <div v-if="tags.length === 0" class="flex flex-col items-center justify-center py-20 px-4 bg-white/60 backdrop-blur-md rounded-3xl border border-gray-100 shadow-sm">
              <div class="w-24 h-24 mb-6 bg-gray-50 rounded-full flex items-center justify-center">
                <svg class="h-12 w-12 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"></path>
                </svg>
              </div>
              <h3 class="text-lg font-bold text-gray-900 mb-2">暂无标签</h3>
              <p class="text-gray-500 text-center max-w-sm">目前还没有创建任何标签，请稍后再来。</p>
            </div>

            <div v-else>
              <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4 sm:gap-6">
                <div 
                  v-for="(tag, index) in tags" 
                  :key="tag.id" 
                  class="group relative bg-white rounded-2xl p-5 border border-gray-100 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 cursor-pointer overflow-hidden flex flex-col items-center justify-center text-center h-32"
                  @click="goToTagArticles(tag.id)"
                >
                  <!-- 悬浮时的背景高亮 -->
                  <div class="absolute inset-0 bg-gradient-to-br opacity-0 group-hover:opacity-10 transition-opacity duration-300"
                    :class="['from-blue-500 to-indigo-600', 'from-emerald-400 to-teal-500', 'from-orange-400 to-rose-500', 'from-purple-500 to-pink-500'][index % 4]">
                  </div>
                  
                  <div class="relative z-10 w-full">
                     <div class="inline-flex items-center justify-center mb-3">
                       <span class="text-2xl font-black text-gray-200 group-hover:text-blue-200 transition-colors mr-1">#</span>
                       <span class="font-bold text-gray-900 group-hover:text-blue-600 transition-colors text-lg truncate max-w-full" :title="tag.name">{{ tag.name }}</span>
                     </div>
                     <div class="bg-gray-50 group-hover:bg-blue-50 text-gray-500 group-hover:text-blue-600 text-xs font-semibold px-3 py-1 rounded-full transition-colors inline-block">
                        {{ tag.articleCount }} 篇文章
                     </div>
                  </div>
                </div>
              </div>
              
              <!-- Pagination -->
              <div class="mt-12 flex justify-center">
                <Pagination 
                  :total="total"
                  :current="page"
                  :size="size"
                  @page-change="handlePageChange"
                  class="scale-105"
                />
              </div>
            </div>
          </div>
        </section>

        <!-- Sidebar -->
        <aside class="w-full lg:w-[320px] xl:w-[360px] shrink-0">
          <HomeSidebar />
        </aside>
      </div>
    </main>
    
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTagList, getAllTagList } from '@/api/frontend/tag'
import { getAllCategoryList } from '@/api/frontend/category'
import AppHeader from '@/components/frontend/AppHeader.vue'
import AppFooter from '@/components/frontend/AppFooter.vue'
import Pagination from '@/components/frontend/Pagination.vue'
import HomeSidebar from '@/pages/frontend/HomeSidebar.vue'

const router = useRouter()

const tags = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(20)
const total = ref(0)
const keyword = ref('')
const sidebarTags = ref([])
const categories = ref([])

onMounted(() => {
  loadTags()
  loadSidebarData()
})

async function loadTags() {
  loading.value = true
  try {
    const res = await getTagList({ current: page.value, size: size.value, name: keyword.value })
    if (res && res.success) {
      tags.value = res.data.map(tag => ({
        id: tag.id,
        name: tag.name || tag,
        articleCount: tag.articleCount || 0
      }))
      // Update pagination info
      page.value = res.current || page.value
      size.value = res.size || size.value
      total.value = res.total || total.value
    }
  } catch (error) {
    console.error('Failed to load tags:', error)
    // Fallback to mock data if API fails
    tags.value = [
      { id: 1, name: 'Java', articleCount: 12 },
      { id: 2, name: 'Spring Boot', articleCount: 7 },
      { id: 3, name: 'Vue.js', articleCount: 15 },
      { id: 4, name: 'Postman', articleCount: 8 },
      { id: 5, name: 'API 测试', articleCount: 5 },
      { id: 6, name: '数据库', articleCount: 10 },
      { id: 7, name: 'MySQL', articleCount: 6 },
      { id: 8, name: 'Redis', articleCount: 4 },
      { id: 9, name: '微服务', articleCount: 9 },
      { id: 10, name: 'Docker', articleCount: 7 },
      { id: 11, name: 'Kubernetes', articleCount: 3 },
      { id: 12, name: 'DevOps', articleCount: 5 }
    ]
  } finally {
    loading.value = false
  }
}

async function loadSidebarData() {
  try {
    // Load tags for sidebar
    const tagRes = await getAllTagList()
    if (tagRes && tagRes.success) {
      sidebarTags.value = tagRes.data.map(tag => tag.name || tag)
    }
    
    // Load categories for sidebar
    const categoryRes = await getAllCategoryList()
    if (categoryRes && categoryRes.success) {
      categories.value = categoryRes.data.map(category => ({
        id: category.id,
        name: category.name,
        count: category.articleCount || 0
      }))
    }
  } catch (error) {
    console.error('Failed to load sidebar data:', error)
    // Fallback to mock data if API fails
    sidebarTags.value = ['工具', 'API 调试', 'Python', '从 0 到 1', '后端开发', '前端框架', '数据库', '微服务']
    categories.value = [
      { id: 1, name: 'Java', count: 12 },
      { id: 2, name: 'Postman', count: 8 },
      { id: 3, name: 'Vue.js', count: 15 },
      { id: 4, name: 'Spring Boot', count: 7 }
    ]
  }
}

function search() {
  page.value = 1
  loadTags()
}

function handlePageChange(newPage) {
  page.value = newPage
  loadTags()
}

function goToTagArticles(tagId) {
  // Navigate to articles filtered by tag
  router.push(`/tag/${tagId}`)
}

function getTagClass(articleCount) {
  if (articleCount > 10) {
    return 'bg-blue-500 text-white'
  } else if (articleCount > 5) {
    return 'bg-blue-400 text-white'
  } else if (articleCount > 2) {
    return 'bg-blue-300 text-gray-800'
  } else {
    return 'bg-blue-100 text-gray-800'
  }
}

function goToCategory(categoryId) {
  router.push(`/category/${categoryId}`)
}
</script>




