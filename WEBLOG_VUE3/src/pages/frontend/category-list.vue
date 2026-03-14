<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <!-- Header -->
    <AppHeader :keyword="keyword" @update:keyword="keyword = $event" @search="search" />

    <!-- Main Content -->
    <main class="flex flex-col lg:flex-row gap-8 py-8">
      <!-- Categories Section -->
      <section class="w-full lg:w-[68%]">
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h2 class="text-2xl font-bold text-gray-900 mb-6">文章分类</h2>

          <div v-if="loading" class="py-16 text-center">
            <div class="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500">
            </div>
            <p class="mt-2 text-gray-600">加载中...</p>
          </div>

          <div v-else>
            <div v-if="categories.length === 0" class="py-16 text-center">
              <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10">
                </path>
              </svg>
              <h3 class="mt-2 text-sm font-medium text-gray-900">暂无分类</h3>
              <p class="mt-1 text-sm text-gray-500">目前还没有创建任何分类。</p>
            </div>

            <div v-else>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div v-for="category in categories" :key="category.id"
                  class="border border-gray-200 rounded-lg p-5 hover:shadow-md transition-shadow duration-300 cursor-pointer"
                  @click="goToCategoryArticles(category.id)">
                  <div class="flex items-center justify-between">
                    <h3 class="text-lg font-semibold text-gray-900">{{ category.name }}</h3>
                    <span class="bg-blue-100 text-blue-800 text-sm font-medium px-2.5 py-0.5 rounded-full">
                      {{ category.articleCount }} 篇文章
                    </span>
                  </div>
                  <p class="mt-2 text-gray-600 text-sm">{{ category.illustrate || '暂无描述' }}</p>
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
import { getAllCategoryList, getCategoryList } from '@/api/frontend/category'
import { getAllTagList } from '@/api/frontend/tag'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/frontend/AppHeader.vue'
import Pagination from '@/components/frontend/Pagination.vue'
import Sidebar from '@/pages/frontend/sidebar.vue'

const router = useRouter()

const categories = ref([])
const sideCategories = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(6)
const total = ref(0)
const keyword = ref('')
const sidebarTags = ref([])

onMounted(() => {
  loadCategories()
  loadSidebarData()
})

// 异步加载分类数据
async function loadCategories() {
  loading.value = true
  try {
    const res = await getCategoryList({ current: page.value, size: size.value, name: keyword.value })
    if (res && res.success) {
      categories.value = res.data.map(category => ({
        id: category.id,
        name: category.name,
        articleCount: category.articleCount || 0,
        illustrate: category.illustrate || ''
      }))
      // Update pagination info
      page.value = res.current || page.value
      size.value = res.size || size.value
      total.value = res.total || total.value
    }
  } catch (error) {
    console.error('Failed to load categories:', error)
    // Fallback to mock data if API fails
    categories.value = [
      { id: 1, name: 'Java', articleCount: 12, description: 'Java 相关技术文章' },
      { id: 2, name: 'Postman', articleCount: 8, description: 'API 测试工具使用指南' },
      { id: 3, name: 'Vue.js', articleCount: 15, description: '前端框架 Vue.js 实践' },
      { id: 4, name: 'Spring Boot', articleCount: 7, description: 'Java 微服务框架' },
      { id: 5, name: '数据库', articleCount: 10, description: '数据库设计与优化' },
      { id: 6, name: 'DevOps', articleCount: 5, description: '持续集成与部署实践' }
    ]
  } finally {
    loading.value = false
  }
}

// 异步加载侧边栏数据
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
      sideCategories.value = categoryRes.data.map(category => ({
        id: category.id,
        name: category.name,
        count: category.articleCount || 0
      }))
    }
  } catch (error) {
    console.error('Failed to load sidebar data:', error)
    // Fallback to mock data if API fails
    sidebarTags.value = ['工具', 'API 调试', 'Python', '从 0 到 1', '后端开发', '前端框架', '数据库', '微服务']
    sideCategories.value = [
      { id: 1, name: 'Java', count: 12 },
      { id: 2, name: 'Postman', count: 8 },
      { id: 3, name: 'Vue.js', count: 15 },
      { id: 4, name: 'Spring Boot', count: 7 }
    ]
  }
}

function search() {
  page.value = 1
  loadCategories()
}

function handlePageChange(newPage) {
  page.value = newPage
  loadCategories()
}

function goToCategoryArticles(categoryId) {
  router.push(`/category/${categoryId}`)
}

function goToCategory(categoryId) {
  router.push(`/category/${categoryId}`)
}
</script>