<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <!-- Header -->
    <AppHeader :keyword="keyword" @update:keyword="keyword = $event" @search="search" />

    <!-- Main Content -->
    <main class="flex flex-col lg:flex-row gap-8 py-8">
      <!-- Articles Section -->
      <section class="w-full lg:w-[68%]">
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-2xl font-bold text-gray-900">
              <span v-if="tagName">标签: {{ tagName }}</span>
              <span v-else>文章列表</span>
            </h2>
          </div>

          <div v-if="loading" class="py-16 text-center">
            <div class="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
            <p class="mt-2 text-gray-600">加载中...</p>
          </div>

          <div v-else>
            <div v-if="articles.length === 0" class="py-16 text-center">
              <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z"></path>
              </svg>
              <h3 class="mt-2 text-sm font-medium text-gray-900">暂无文章</h3>
              <p class="mt-1 text-sm text-gray-500">该标签下还没有发布任何文章。</p>
            </div>

            <div v-else>
              <!-- Articles Grid -->
              <div class="grid grid-cols-1 gap-6 mb-8">
                <ArticleCard 
                  v-for="article in articles" 
                  :key="article.id" 
                  :article="article"
                  class="hover:shadow-lg transition-shadow duration-300"
                />
              </div>

              <!-- Pagination -->
              <Pagination 
                v-model:current-page="page" 
                v-model:page-size="size" 
                :total="total"
                @page-change="loadArticles"
                @size-change="loadArticles"
              />
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
import { useRoute, useRouter } from 'vue-router'
import ArticleCard from '@/pages/frontend/articleCard.vue'
import Sidebar from '@/pages/frontend/sidebar.vue'
import AppHeader from '@/components/frontend/AppHeader.vue'
import Pagination from '@/components/frontend/Pagination.vue'
import { getArticlePageList } from '@/api/frontend/article'
import { getTagList } from '@/api/frontend/tag'  // Added import

const route = useRoute()
const router = useRouter()

const articles = ref([])
const page = ref(1)
const size = ref(6)
const total = ref(0)
const loading = ref(false)
const searchKeyword = ref('')
const tagName = ref('')

const tagId = route.params.id

onMounted(() => {
  if (tagId) {
    loadTagInfo()
    loadArticles()
  }
})

async function loadTagInfo() {
  try {
    // Fetch tag details to get the name
    const res = await getTagList({ current: 1, size: 1, id: tagId })
    if (res && res.success && res.data && res.data.length > 0) {
      tagName.value = res.data[0].name || `标签 ${tagId}`
    } else {
      tagName.value = `标签 ${tagId}`
    }
  } catch (error) {
    console.error('Failed to load tag info:', error)
    tagName.value = `标签 ${tagId}`
  }
}

async function loadArticles() {
  loading.value = true
  try {
    // Pass tag ID as a filter parameter
    const params = {
      current: page.value,
      size: size.value,
      name: searchKeyword.value,
      tagId: tagId // Filter by tag ID
    }
    
    const res = await getArticlePageList(params)
    if (res && res.success) {
      const list = Array.isArray(res.data) ? res.data : []
      articles.value = list.map(a => ({
        id: a.id || a._id,
        cover: a.cover,
        title: a.title,
        createTime: a.createTime,
        summary: a.summary,
        description: a.description,
        content: a.content,
        category: a.category,
        tags: a.tags || []
      }))
      page.value = res.current || page.value
      size.value = res.size || size.value
      total.value = res.total || total.value
    } else {
      articles.value = []
      total.value = 0
    }
  } catch (e) {
    console.error('加载文章失败', e)
    articles.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}
</script>