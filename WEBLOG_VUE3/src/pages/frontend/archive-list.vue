<template>
  <div class="min-h-screen bg-[#F5F7FA] flex flex-col">
    <div v-if="!isLoggedIn" class="min-h-[calc(100vh-80px)] flex flex-col items-center justify-center p-4">
      <div class="bg-white/60 backdrop-blur-xl rounded-3xl shadow-xl border border-gray-100 p-8 w-full max-w-md text-center transform hover:scale-[1.02] transition-all duration-300">
        <div class="bg-gradient-to-br from-blue-50 to-indigo-50 w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-6">
          <el-icon class="text-5xl text-blue-500">
            <Lock />
          </el-icon>
        </div>
        <h2 class="text-3xl font-extrabold text-gray-900 mb-3 tracking-tight">访问受限</h2>
        <p class="text-gray-500 mb-8 leading-relaxed">文章归档功能仅对注册用户开放<br>带您穿梭时光，请先登录查阅历史足迹</p>
        <div class="space-y-4">
          <el-button type="primary" size="large" class="w-full !rounded-xl !text-lg !h-12 !font-semibold shadow-md hover:shadow-lg transition-all" @click="goToLogin">
            立即登录
          </el-button>
          <el-button size="large" class="w-full !rounded-xl !h-12 !ml-0 border-transparent hover:border-gray-300 hover:bg-gray-50 transition-all text-gray-600" @click="goHome">
            回到首页探索
          </el-button>
        </div>
      </div>
    </div>

    <template v-else>
      <AppHeader :keyword="keyword" @update:keyword="keyword = $event" @search="search" />

      <main class="flex-1 max-w-[1500px] w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 lg:py-12 flex flex-col xl:flex-row gap-8 xl:gap-8">
        <aside class="hidden xl:block xl:w-[250px] xl:shrink-0">
          <DailyNoteSidebar />
        </aside>

        <section class="flex-1 min-w-0">
          <div class="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 md:p-10 min-h-[600px]">
            <div class="flex items-end justify-between mb-10 pb-6 border-b border-gray-100/80">
              <div>
                <h2 class="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
                  <el-icon class="text-blue-600"><Calendar /></el-icon>
                  时光机归档
                </h2>
                <p class="mt-2 text-sm text-gray-500">重温过去的点滴思考</p>
              </div>
              <div class="bg-blue-50 text-blue-700 font-bold px-4 py-1.5 rounded-full text-sm">
                共记录 {{ totalArticles }} 篇
              </div>
            </div>

            <div v-if="loading" class="py-20 text-center">
               <div class="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
               <p class="mt-4 text-sm text-gray-500 font-medium">翻阅资料库中 ...</p>
            </div>

            <div v-else-if="Object.keys(groupedByYear).length === 0" class="py-24 text-center">
              <div class="w-24 h-24 mx-auto mb-6 bg-gray-50 rounded-full flex items-center justify-center">
                <svg class="h-12 w-12 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                </svg>
              </div>
              <h3 class="text-xl font-bold text-gray-900 mb-2">暂无归档文章</h3>
              <p class="text-gray-500 max-w-sm mx-auto">岁月还未留下痕迹，等待您的第一篇分享。</p>
            </div>

            <div v-else class="relative pl-6 lg:pl-8">
              <!-- 大时间轴线段 -->
              <div class="absolute left-[13px] lg:left-[21px] top-3 bottom-0 w-[2px] bg-gradient-to-b from-blue-100 via-gray-100 to-transparent"></div>

              <div v-for="(months, year) in groupedByYear" :key="year" class="mb-14 relative group/year">
                <!-- 年份节点大圆点 -->
                <div class="absolute -left-[35px] lg:-left-[27px] top-1 w-7 h-7 rounded-full bg-blue-500 border-4 border-white shadow-sm z-10 flex items-center justify-center group-hover/year:scale-110 transition-transform">
                   <div class="w-2 h-2 bg-white rounded-full"></div>
                </div>
                <h3 class="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-gray-900 to-gray-500 italic mb-8 -mt-2 tracking-tighter">{{ year }}</h3>

                <div v-for="(articles, month) in months" :key="month" class="mb-10 pl-6 lg:pl-10 relative">
                  <!-- 月份节点小圆点 -->
                  <div class="absolute -left-[35px] lg:-left-[15px] top-[6px] w-3 h-3 rounded-full bg-blue-200 border-2 border-white z-10"></div>

                  <h4 class="text-xl font-bold text-gray-800 mb-6 flex items-center gap-3">
                    {{ getMonthName(month) }}
                    <span class="text-xs font-semibold text-gray-500 bg-gray-100 px-2.5 py-1 rounded-full">{{ articles.length }} 篇</span>
                  </h4>

                  <div class="space-y-4">
                    <div v-for="article in articles" :key="article.id"
                      class="group bg-white border border-gray-100 rounded-2xl p-4 sm:p-5 hover:shadow-xl hover:border-blue-200 transition-all duration-300 cursor-pointer flex flex-col sm:flex-row sm:items-center gap-4 hover:-translate-y-1 relative overflow-hidden"
                      @click="goToArticle(article.id)">
                      
                      <!-- 悬浮时的背景高亮 -->
                      <div class="absolute inset-0 bg-blue-50/10 opacity-0 group-hover:opacity-100 transition-opacity"></div>

                      <!-- 左侧：日期小日历样式 -->
                      <div class="flex-shrink-0 w-16 text-center bg-gray-50 rounded-xl py-2 group-hover:bg-blue-600 transition-colors z-10">
                        <div class="text-xs text-gray-400 group-hover:text-blue-100 font-medium">{{ getMonthName(month).slice(0,3).toUpperCase() }}</div>
                        <div class="text-2xl font-black text-gray-700 group-hover:text-white leading-none mt-1">{{ getDay(article.createTime) }}</div>
                      </div>

                      <!-- 中间：文章标题与元信息 -->
                      <div class="flex-1 min-w-0 z-10">
                        <h5 class="font-bold text-gray-900 truncate group-hover:text-blue-600 transition-colors text-lg mb-1.5">
                          {{ article.title }}
                        </h5>
                        <div class="flex flex-wrap items-center gap-2 sm:gap-4 mt-auto text-xs text-gray-500 font-medium">
                          <span class="flex items-center gap-1.5 bg-gray-50 group-hover:bg-white px-2.5 py-1 rounded-md transition-colors border border-transparent group-hover:border-gray-100">
                            <el-icon><Folder /></el-icon> {{ article.category || '未分类' }}
                          </span>
                          <span v-if="article.viewCount" class="flex items-center gap-1.5">
                            <el-icon><View /></el-icon> {{ article.viewCount }}
                          </span>
                        </div>
                      </div>

                      <!-- 右侧：直达箭头 -->
                      <div class="hidden sm:flex self-center w-10 h-10 rounded-full bg-gray-50 items-center justify-center group-hover:bg-blue-50 group-hover:text-blue-600 transition-colors z-10">
                         <el-icon class="text-gray-400 group-hover:text-blue-600 translate-x-0 group-hover:translate-x-1 transition-transform"><ArrowRight /></el-icon>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Sidebar -->
        <aside class="w-full xl:w-[320px] shrink-0 space-y-8">
          <HomeSidebar />
        </aside>
      </main>
      
      <!-- 全局页脚 -->
      <AppFooter />
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs' // 建议安装: npm install dayjs
import { Lock, Calendar, Folder, View, ArrowRight } from '@element-plus/icons-vue'
import { getArticleArchive } from '@/api/frontend/article'
import AppHeader from '@/components/frontend/AppHeader.vue'
import AppFooter from '@/components/frontend/AppFooter.vue'
import HomeSidebar from '@/pages/frontend/HomeSidebar.vue'
import DailyNoteSidebar from '@/pages/frontend/DailyNoteSidebar.vue'
import { useUserStore } from '@/stores/user'
import { getToken } from '@/composables/cookie'

const router = useRouter()
const userStore = useUserStore()
const user = computed(() => userStore.frontendUserInfo)

// 检查登录状态
const isLoggedIn = computed(() => !!getToken() && !!user.value.userId)

const archiveItems = ref([])
const loading = ref(false)
const keyword = ref('')

// 计算总文章数
const totalArticles = computed(() => archiveItems.value.length)

// 核心初始化
onMounted(() => {
  if (isLoggedIn.value) {
    loadArchiveItems()
  }
})

// 

/**
 * 加载归档数据
 */
async function loadArchiveItems(kw = '') {
  loading.value = true
  try {
    const params = {
      keyword: (kw || keyword.value || '').trim(),
      userId: user.value.userId || ''
    }

    const res = await getArticleArchive(params)

    if (res?.success && Array.isArray(res.data)) {
      // 数据清洗逻辑
      archiveItems.value = normalizeArchiveData(res.data)
    } else {
      archiveItems.value = [] // 生产环境建议清空，而非加载 Mock 数据，Mock 数据应仅在开发环境使用
    }
  } catch (err) {
    console.error('加载归档失败:', err)
    archiveItems.value = []
  } finally {
    loading.value = false
  }
}

/**
 * 统一后端数据格式（适配多种返回结构）
 */
function normalizeArchiveData(data) {
  if (!data || data.length === 0) return []

  const first = data[0]
  // 情况1: 后端已经分组 [{ month: [2023, 12], articles: [...] }]
  if (first.month && Array.isArray(first.articles)) {
    return data.flatMap(group => {
      return (group.articles || []).map(article => formatArticleItem(article))
    })
  }
  // 情况2: 扁平列表
  return data.map(item => formatArticleItem(item))
}

/**
 * 格式化单篇文章对象
 */
function formatArticleItem(a) {
  return {
    id: a.id || a.articleId || null,
    title: a.title || a.articleTitle || '无标题',
    // 兼容多种时间字段
    createTime: a.createDate || a.createTime || (a.createMonth ? `${a.createMonth[0]}-${String(a.createMonth[1]).padStart(2, '0')}-01` : new Date()),
    category: (a.category?.name) || a.categoryName || a.category || '未分类',
    viewCount: a.readNum || a.viewCount || 0,
    cover: a.cover || null
  }
}

/**
 * 按年份-月份分组逻辑 (保持原有逻辑，稍作精简)
 */
const groupedByYear = computed(() => {
  const groups = {}

  // 1. 分组
  archiveItems.value.forEach(item => {
    const date = dayjs(item.createTime)
    if (!date.isValid()) return

    const year = date.year()
    const month = date.month() + 1 // dayjs month is 0-11

    if (!groups[year]) groups[year] = {}
    if (!groups[year][month]) groups[year][month] = []

    groups[year][month].push(item)
  })

  // 2. 排序 (年份倒序 -> 月份倒序)
  const sortedGroups = {}
  Object.keys(groups).sort((a, b) => b - a).forEach(year => {
    sortedGroups[year] = {}
    Object.keys(groups[year]).sort((a, b) => b - a).forEach(month => {
      sortedGroups[year][month] = groups[year][month]
    })
  })

  return sortedGroups
})

// 辅助函数
const search = () => loadArchiveItems()
const goToLogin = () => router.push('/login')
const goHome = () => router.push('/')
const goToArticle = (id) => router.push(`/article/${id}`)

function getMonthName(month) {
  return ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'][month - 1] || month
}

// 获取日期的 "日" 部分
function getDay(dateString) {
  return dayjs(dateString).format('DD')
}
</script>

<style scoped>
/* 可以在这里添加自定义动画 */
</style>

