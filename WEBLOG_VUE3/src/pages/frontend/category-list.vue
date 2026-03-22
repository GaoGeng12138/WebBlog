```
<template>
  <div class="min-h-screen bg-[#F8FAFC] flex flex-col">
    <!-- Header -->
    <AppHeader :keyword="keyword" @update:keyword="keyword = $event" @search="search" />

    <!-- Main Content -->
    <main class="flex-1 max-w-[1500px] w-full mx-auto px-3 sm:px-6 lg:px-8 py-6 lg:py-12">
      <div class="flex flex-col xl:flex-row gap-8 xl:gap-8">
        <aside class="hidden xl:block xl:w-[250px] xl:shrink-0">
          <DailyNoteSidebar />
        </aside>

        <!-- Categories Section -->
        <section class="flex-1 min-w-0">
          <div class="mb-7 pl-1 sm:pl-2">
            <h2 class="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">文章分类</h2>
            <p class="mt-2 text-gray-500">探索博客涵盖的所有技术领域</p>
          </div>

          <div v-if="loading" class="py-20 text-center">
            <div class="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
            <p class="mt-4 text-sm text-gray-500 font-medium">全力加载分类数据 ...</p>
          </div>

          <div v-else>
            <div v-if="categories.length === 0" class="flex flex-col items-center justify-center py-20 px-4 bg-white/60 backdrop-blur-md rounded-3xl border border-gray-100 shadow-sm">
              <div class="w-24 h-24 mb-6 bg-blue-50 rounded-full flex items-center justify-center">
                <svg class="h-12 w-12 text-blue-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 002-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
                </svg>
              </div>
              <h3 class="text-lg font-bold text-gray-900 mb-2">暂无分类</h3>
              <p class="text-gray-500 text-center max-w-sm">作者还在努力码字中，暂未创建任何分类。</p>
            </div>

            <div v-else>
              <div class="grid grid-cols-2 gap-3 sm:grid-cols-2 sm:gap-4 lg:grid-cols-3 xl:grid-cols-4 xl:gap-6">
                <div 
                  v-for="(category, index) in categories" 
                  :key="category.id" 
                  class="group bg-white rounded-[22px] p-4 sm:p-6 border border-gray-100 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 cursor-pointer overflow-hidden relative min-h-[170px] sm:min-h-[210px]"
                  :class="[
                    index % 2 === 1 ? 'mt-4 sm:mt-0' : 'mt-0',
                    index % 4 === 3 ? 'sm:mt-0' : ''
                  ]"
                  @click="goToCategoryArticles(category)"
                >
                  <div class="absolute inset-0 bg-gradient-to-br from-gray-50 to-white opacity-0 group-hover:opacity-100 transition-opacity z-0"></div>
                  
                  <div class="relative z-10 flex flex-col h-full">
                    <div class="flex items-start justify-between mb-4">
                      <!-- 动态生成前四个分类的渐变色块，后续则默认为灰蓝 -->
                      <div class="w-10 h-10 sm:w-12 sm:h-12 rounded-2xl flex items-center justify-center shadow-sm text-white font-bold text-base sm:text-xl group-hover:scale-110 transition-transform"
                        :class="{
                          'bg-gradient-to-br from-blue-500 to-indigo-600': index % 4 === 0,
                          'bg-gradient-to-br from-emerald-400 to-teal-500': index % 4 === 1,
                          'bg-gradient-to-br from-orange-400 to-rose-500': index % 4 === 2,
                          'bg-gradient-to-br from-purple-500 to-pink-500': index % 4 === 3
                        }"
                      >
                         {{ category.name ? category.name.substring(0, 1).toUpperCase() : 'C' }}
                      </div>
                      <span class="bg-gray-50 group-hover:bg-blue-50 text-gray-400 group-hover:text-blue-600 text-[10px] sm:text-xs font-bold px-2.5 py-1 rounded-full transition-colors flex items-center gap-1">
                        <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
                        {{ category.articleCount || 0 }}
                      </span>
                    </div>
                    
                    <h3 class="text-base sm:text-xl font-bold text-gray-900 group-hover:text-blue-600 transition-colors mb-2 line-clamp-2">{{ category.name }}</h3>
                    <p class="text-[12px] sm:text-sm text-gray-500 line-clamp-2 mt-auto">包含有关 {{ category.name }} 的各类技术探讨和文章分享。</p>
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
        <aside class="hidden xl:block w-full xl:w-[320px] xl:shrink-0">
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
import { getCategoryList } from '@/api/frontend/category'
import AppHeader from '@/components/frontend/AppHeader.vue'
import AppFooter from '@/components/frontend/AppFooter.vue'
import HomeSidebar from '@/pages/frontend/HomeSidebar.vue'
import DailyNoteSidebar from '@/pages/frontend/DailyNoteSidebar.vue'
import Pagination from '@/components/frontend/Pagination.vue'

const router = useRouter()
const categories = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(12)
const loading = ref(false)
const keyword = ref('')

onMounted(() => {
  loadCategories()
})

// 异步加载分类数据
async function loadCategories() {
  loading.value = true
  try {
    const res = await getCategoryList({ current: page.value, size: size.value, name: keyword.value })
    if (res && res.success) {
      if (res.data && res.data.records) {
        categories.value = res.data.records.map(category => ({
          id: category.id,
          name: category.name,
          articleCount: category.articleCount || 0,
          illustrate: category.illustrate || ''
        }))
        total.value = res.data.total
      } else {
        const dataArr = Array.isArray(res.data) ? res.data : (res.data ? [res.data] : [])
        categories.value = dataArr.map(category => ({
          id: category.id,
          name: category.name,
          articleCount: category.articleCount || 0,
          illustrate: category.illustrate || ''
        }))
        page.value = res.current || page.value
        size.value = res.size || size.value
        total.value = res.total || categories.value.length
      }
    }
  } catch (error) {
    console.error('Failed to load categories:', error)
  } finally {
    loading.value = false
  }
}

function search(searchKeyword) {
  if (searchKeyword) keyword.value = searchKeyword
  page.value = 1
  loadCategories()
}

function handlePageChange(newPage) {
  page.value = newPage
  loadCategories()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function goToCategoryArticles(category) {
  router.push({
    path: `/category/${category.id}`,
    query: {
      name: category.name
    }
  })
}

</script>

