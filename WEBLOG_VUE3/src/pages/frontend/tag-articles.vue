<template>
  <div>
    <!-- Header -->
    <AppHeader :keyword="keyword" @update:keyword="keyword = $event" @search="searchArticles" />

    <!-- Main Content -->
    <div class="max-w-[1500px] mx-auto px-4 sm:px-6 lg:px-8">
      <main class="flex flex-col gap-6 py-6 lg:flex-row">
        <!-- Articles Section -->
        <section class="w-full lg:w-[76%]">
          <div class="mb-4 overflow-hidden rounded-[22px] border border-white/70 bg-white/85 p-4 shadow-[0_16px_38px_rgba(15,23,42,0.06)] backdrop-blur-xl sm:p-5">
            <p class="text-xs font-semibold uppercase tracking-[0.26em] text-slate-400">
              Tag Articles
            </p>
            <div class="mt-2 flex flex-col gap-2.5 lg:flex-row lg:items-end lg:justify-between">
              <div>
                <h1 class="text-[2rem] font-black tracking-tight text-slate-950 sm:text-[2.2rem]">
                  {{ tagName ? `标签: ${tagName}` : '文章列表' }}
                </h1>
                <p class="mt-1.5 max-w-2xl text-sm leading-6 text-slate-500 sm:text-[14px]">
                  关于这个标签的所有文章内容。
                </p>
              </div>
              <div class="flex flex-wrap items-center gap-2 text-sm text-slate-500">
                <span class="rounded-full bg-slate-100 px-3.5 py-1.5 text-[13px] font-semibold text-slate-700">
                  共 {{ total }} 篇
                </span>
              </div>
            </div>

            <div class="mt-3.5 max-w-[560px]">
              <label class="hero-search relative flex items-center p-1.5 rounded-full border border-white/60 bg-white/70 shadow-[0_8px_20px_rgba(15,23,42,0.04)] backdrop-blur-xl transition-all duration-300 focus-within:-translate-y-0.5 focus-within:bg-white/95 focus-within:shadow-[0_12px_28px_rgba(59,130,246,0.12)] focus-within:border-blue-200">
                <span class="pl-4 pr-3 flex items-center justify-center text-slate-400 group-focus-within:text-blue-500 transition-colors">
                  <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-4.35-4.35M10.5 18a7.5 7.5 0 100-15 7.5 7.5 0 000 15z" />
                  </svg>
                </span>
                <input
                  v-model="searchKeyword"
                  type="text"
                  class="min-w-0 flex-1 bg-transparent px-1 py-2 text-[15px] text-slate-700 outline-none border-none ring-0 placeholder:text-slate-400 focus:outline-none focus:ring-0"
                  :placeholder="`在标签 ${tagName || ''} 中搜索文章`"
                  @keyup.enter="searchArticles"
                />
                <button
                  type="button"
                  class="hero-search-button rounded-full bg-slate-900 px-6 py-2.5 text-[14px] font-semibold text-white transition-all duration-300 hover:bg-blue-600 hover:shadow-lg hover:shadow-blue-600/20 active:scale-95 ml-2"
                  @click="searchArticles"
                >
                  查询
                </button>
              </label>
            </div>
          </div>

          <div class="rounded-[26px] border border-white/70 bg-white/85 p-5 shadow-[0_18px_42px_rgba(15,23,42,0.06)] backdrop-blur-xl sm:p-6">
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
                <div class="mb-7 flex flex-col gap-3.5">
                  <ArticleCard 
                    v-for="article in articles" 
                    :key="article.id" 
                    :article="article"
                    variant="list"
                  />
                </div>

                <!-- Pagination -->
                <Pagination 
                  v-model:current-page="page" 
                  v-model:page-size="size" 
                  :total="total"
                  :simple-mode="true"
                  @page-change="loadArticles"
                  @size-change="loadArticles"
                />
              </div>
            </div>
          </div>
        </section>

        <!-- Sidebar -->
        <aside class="w-full lg:w-[24%]">
          <HomeSidebar />
        </aside>
      </main>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ArticleCard from '@/pages/frontend/articleCard.vue'
import HomeSidebar from '@/pages/frontend/HomeSidebar.vue'
import AppHeader from '@/components/frontend/AppHeader.vue'
import AppFooter from '@/components/frontend/AppFooter.vue'
import Pagination from '@/components/frontend/Pagination.vue'
import { getArticlePageListByTag } from '@/api/frontend/article'
import { getTagList } from '@/api/frontend/tag'

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
    const params = {
      current: page.value,
      size: size.value,
      name: searchKeyword.value,
      tagId: Number(tagId)
    }
    
    const res = await getArticlePageListByTag(params)
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

function searchArticles() {
  page.value = 1
  loadArticles()
}
</script>
