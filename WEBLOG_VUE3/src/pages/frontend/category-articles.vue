<template>
  <div class="min-h-screen flex flex-col">
    <AppHeader :keyword="keyword" @update:keyword="keyword = $event" @search="searchArticles" />

    <div class="flex-1 w-full max-w-[1700px] mx-auto px-4 sm:px-6 lg:px-8">
    <main class="flex flex-col gap-6 py-6 2xl:flex-row">
      <aside class="hidden 2xl:block 2xl:w-[230px] 2xl:shrink-0">
        <DailyNoteSidebar />
      </aside>

      <section class="w-full 2xl:flex-1">
        <div class="mb-4 overflow-hidden rounded-[22px] border border-[rgba(149,171,210,0.16)] bg-[rgba(255,255,255,0.78)] p-4 shadow-[0_16px_30px_rgba(120,146,186,0.08)] backdrop-blur-xl sm:p-5">
          <p class="text-[11px] font-medium uppercase tracking-[0.22em] text-[var(--cosmic-muted)]">
            Category Articles
          </p>
          <div class="mt-2 flex flex-col gap-2.5 lg:flex-row lg:items-end lg:justify-between">
            <div>
              <h1 class="text-[1.95rem] font-semibold tracking-[-0.03em] text-[var(--cosmic-text-light)] sm:text-[2.1rem]">
                {{ categoryName }}
              </h1>
              <p class="mt-1.5 max-w-2xl text-[13px] leading-6 text-[var(--cosmic-text-light-muted)] sm:text-[14px]">
                {{ categoryDescription }}
              </p>
            </div>
            <div class="flex flex-wrap items-center gap-2 text-[13px] text-[var(--cosmic-muted)]">
              <span class="rounded-full bg-[rgba(148,176,231,0.12)] px-3.5 py-1.5 text-[12px] font-medium text-[var(--cosmic-blue-deep)]">
                共 {{ total }} 篇
              </span>
            </div>
          </div>

          <div class="mt-3.5 max-w-[560px]">
            <label class="hero-search relative flex items-center p-1.5 rounded-full border border-[rgba(149,171,210,0.16)] bg-[rgba(255,255,255,0.72)] shadow-[0_10px_24px_rgba(120,146,186,0.08)] backdrop-blur-xl transition-all duration-300 focus-within:-translate-y-0.5 focus-within:bg-[rgba(255,255,255,0.95)] focus-within:shadow-[0_12px_28px_rgba(110,146,216,0.12)] focus-within:border-[rgba(148,176,231,0.24)]">
              <span class="pl-4 pr-3 flex items-center justify-center text-[var(--cosmic-muted)] group-focus-within:text-[var(--cosmic-blue)] transition-colors">
                <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-4.35-4.35M10.5 18a7.5 7.5 0 100-15 7.5 7.5 0 000 15z" />
                </svg>
              </span>
              <input
                v-model="keyword"
                type="text"
                class="min-w-0 flex-1 bg-transparent px-1 py-2 text-[14px] text-[var(--cosmic-text-light)] outline-none border-none ring-0 placeholder:text-[var(--cosmic-muted)] focus:outline-none focus:ring-0"
                :placeholder="`在 ${categoryName || '当前分类'} 中搜索文章`"
                @keyup.enter="searchArticles"
              />
              <button
                type="button"
                class="theme-btn-primary hero-search-button ml-2 rounded-full px-6 py-2.5 text-[13px] font-medium text-white transition-all duration-300 active:scale-95"
                @click="searchArticles"
              >
                查询
              </button>
            </label>
          </div>
        </div>

        <div class="rounded-[26px] border border-[rgba(149,171,210,0.16)] bg-[rgba(255,255,255,0.82)] p-5 shadow-[0_18px_34px_rgba(120,146,186,0.08)] backdrop-blur-xl sm:p-6">
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
              <p class="mt-1 text-sm text-gray-500">该分类下还没有发布任何文章。</p>
            </div>

            <div v-else>
              <div class="mb-7 flex flex-col gap-3.5">
                <ArticleCard 
                  v-for="(article, index) in articles" 
                  :key="article.id" 
                  :article="article"
                  variant="list"
                  :category-id="categoryId"
                  :show-category-tag="false"
                  :image-index="index"
                />
              </div>

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
      <aside class="w-full lg:w-[320px] 2xl:w-[320px] 2xl:shrink-0">
        <HomeSidebar />
      </aside>
    </main>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ArticleCard from '@/pages/frontend/articleCard.vue'
import HomeSidebar from '@/pages/frontend/HomeSidebar.vue'
import DailyNoteSidebar from '@/pages/frontend/DailyNoteSidebar.vue'
import AppHeader from '@/components/frontend/AppHeader.vue'
import AppFooter from '@/components/frontend/AppFooter.vue'
import Pagination from '@/components/frontend/Pagination.vue'
import { getArticlePageListByCategory } from '@/api/frontend/article'
import { getCategoryList } from '@/api/frontend/category'

const route = useRoute()
const router = useRouter()

const articles = ref([])
const page = ref(1)
const size = ref(6)
const total = ref(0)
const loading = ref(false)
const keyword = ref('')
const categoryName = ref('')

const categoryId = route.params.id

const categoryDescription = computed(() => {
  if (keyword.value) {
    return `当前展示 ${categoryName.value || '当前分类'} 中与“${keyword.value}”相关的 ${total.value} 篇文章结果。`
  }

  return `按 ${categoryName.value || '当前分类'} 分类集中浏览文章，保留紧凑列表阅读方式，方便快速扫读和连续查看。`
})

onMounted(() => {
  if (categoryId) {
    loadCategoryInfo()
    loadArticles()
  }
})

async function loadCategoryInfo() {
  try {
    const res = await getCategoryList({ current: 1, size: 1, id: categoryId })
    if (res && res.success && res.data && res.data.length > 0) {
      categoryName.value = res.data[0].name || `分类 ${categoryId}`
    } else {
      categoryName.value = `分类 ${categoryId}`
    }
  } catch (error) {
    console.error('Failed to load category info:', error)
    categoryName.value = `分类 ${categoryId}`
  }
}

async function loadArticles() {
  loading.value = true
  try {
    const params = {
      current: page.value,
      size: size.value,
      name: keyword.value,
      categoryId: Number(categoryId)
    }

    const res = await getArticlePageListByCategory(params)
    if (res && res.success) {
      const list = Array.isArray(res.data) ? res.data : []
      articles.value = list.map(a => ({
        id: a.id || a._id,
        cover: a.cover,
        title: a.title,
        author: a.author,
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
