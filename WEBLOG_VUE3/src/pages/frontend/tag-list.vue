<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
     <!-- Header -->
    <AppHeader :keyword="keyword" @update:keyword="keyword = $event" @search="search" />

    <!-- Main Content -->
    <main class="flex flex-col lg:flex-row gap-8 py-8">
      <!-- Tags Section -->
      <section class="w-full lg:w-[68%]">
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-2xl font-bold text-gray-900">文章标签</h2>
          </div>

          <div v-if="loading" class="py-16 text-center">
            <div class="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
            <p class="mt-2 text-gray-600">加载中...</p>
          </div>

          <div v-else>
            <div v-if="tags.length === 0" class="py-16 text-center">
              <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z">
                </path>
              </svg>
              <h3 class="mt-2 text-sm font-medium text-gray-900">暂无标签</h3>
              <p class="mt-1 text-sm text-gray-500">目前还没有创建任何标签。</p>
            </div>

            <div v-else>
              <div class="mt-10">
                <h3 class="text-lg font-semibold text-gray-900 mb-4">标签列表</h3>
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  <div 
                    v-for="tag in tags" 
                    :key="tag.id" 
                    class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow duration-300 cursor-pointer flex items-center justify-between"
                    @click="goToTagArticles(tag.id)"
                  >
                    <div class="flex items-center">
                      <div class="w-3 h-3 rounded-full bg-blue-500 mr-3"></div>
                      <span class="font-medium text-gray-900">{{ tag.name }}</span>
                    </div>
                    <span class="bg-gray-100 text-gray-800 text-xs font-medium px-2.5 py-0.5 rounded-full">
                      {{ tag.articleCount }} 篇文章
                    </span>
                  </div>
                </div>
              </div>
              
              <!-- Pagination -->
              <div class="mt-8">
                <Pagination 
                  :current-page="page" 
                  :page-size="size" 
                  :total="total"
                  @update:current-page="handlePageChange"
                  @page-change="handlePageChange"
                />
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Sidebar -->
      <aside class="w-full lg:w-[32%]">
        <Sidebar />
      </aside>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTagList, getAllTagList } from '@/api/frontend/tag'
import { getAllCategoryList } from '@/api/frontend/category'
import AppHeader from '@/components/frontend/AppHeader.vue'
import Pagination from '@/components/frontend/Pagination.vue'
import Sidebar from '@/pages/frontend/sidebar.vue'

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