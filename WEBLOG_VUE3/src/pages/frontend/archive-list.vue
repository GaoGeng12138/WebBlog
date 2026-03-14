<template>
  <div class="min-h-screen bg-[#F5F7FA]">
    <div v-if="!isLoggedIn" class="min-h-screen flex flex-col items-center justify-center p-4">
      <div
        class="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md text-center transform hover:scale-[1.01] transition-all duration-300">
        <div class="bg-blue-50 w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-6">
          <el-icon class="text-4xl text-blue-500">
            <Lock />
          </el-icon>
        </div>
        <h2 class="text-2xl font-bold text-gray-800 mb-2">访问受限</h2>
        <p class="text-gray-500 mb-8 leading-relaxed">文章归档功能仅对注册用户开放<br>请登录后查看完整的历史文章</p>
        <div class="space-y-3">
          <el-button type="primary" size="large" class="w-full !rounded-lg !text-lg !h-12" @click="goToLogin">
            立即登录
          </el-button>

          <el-button size="large" class="w-full !rounded-lg !h-12 !ml-0" @click="goHome">
            返回首页
          </el-button>
        </div>
      </div>
    </div>

    <template v-else>
      <AppHeader :keyword="keyword" @update:keyword="keyword = $event" @search="search" />

      <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 flex flex-col lg:flex-row gap-8">
        <section class="w-full lg:w-[68%]">
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 md:p-8 min-h-[500px]">
            <div class="flex items-center justify-between mb-8 border-b border-gray-100 pb-4">
              <h2 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
                <el-icon class="text-blue-500">
                  <Calendar />
                </el-icon>
                文章归档
              </h2>
              <span class="text-gray-400 text-sm">共 {{ totalArticles }} 篇</span>
            </div>

            <el-skeleton v-if="loading" :rows="10" animated />

            <div v-else-if="Object.keys(groupedByYear).length === 0" class="py-20 text-center">
              <el-empty description="暂无归档文章" />
            </div>

            <div v-else class="relative pl-4">
              <div class="absolute left-[7px] top-2 bottom-0 w-[2px] bg-gray-100"></div>

              <div v-for="(months, year) in groupedByYear" :key="year" class="mb-12 relative">
                <div class="flex items-center gap-4 mb-6 relative">
                  <div class="w-4 h-4 rounded-full bg-blue-500 border-4 border-white shadow-sm z-10"></div>
                  <h3 class="text-2xl font-bold text-gray-800 italic">{{ year }}</h3>
                </div>

                <div v-for="(articles, month) in months" :key="month" class="mb-8 pl-8 relative">
                  <div
                    class="absolute left-[-25px] top-[6px] w-2.5 h-2.5 rounded-full bg-blue-200 border-2 border-white z-10">
                  </div>

                  <h4 class="text-lg font-semibold text-gray-600 mb-4 flex items-center gap-2">
                    {{ getMonthName(month) }}
                    <span class="text-xs font-normal text-gray-400 bg-gray-100 px-2 py-0.5 rounded-full">{{
                      articles.length }}篇</span>
                  </h4>

                  <div class="space-y-3">
                    <div v-for="article in articles" :key="article.id"
                      class="group bg-white border border-gray-100 rounded-xl p-4 hover:shadow-lg hover:border-blue-100 transition-all duration-300 cursor-pointer flex items-center gap-4"
                      @click="goToArticle(article.id)">
                      <div
                        class="flex-shrink-0 w-12 text-center bg-gray-50 rounded-lg py-1 group-hover:bg-blue-50 transition-colors">
                        <div class="text-xs text-gray-400 group-hover:text-blue-400">{{ getDay(article.createTime) }}
                        </div>
                        <div class="text-sm font-bold text-gray-600 group-hover:text-blue-600">日</div>
                      </div>

                      <div class="flex-1 min-w-0">
                        <h5
                          class="font-medium text-gray-800 truncate group-hover:text-blue-600 transition-colors text-base">
                          {{ article.title }}
                        </h5>
                        <div class="flex items-center gap-3 mt-1.5 text-xs text-gray-400">
                          <span class="flex items-center gap-1 bg-gray-50 px-2 py-0.5 rounded">
                            <el-icon>
                              <Folder />
                            </el-icon> {{ article.category || '未分类' }}
                          </span>
                          <span v-if="article.viewCount" class="flex items-center gap-1">
                            <el-icon>
                              <View />
                            </el-icon> {{ article.viewCount }}
                          </span>
                        </div>
                      </div>

                      <el-icon class="text-gray-300 group-hover:translate-x-1 transition-transform">
                        <ArrowRight />
                      </el-icon>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <aside class="w-full lg:w-[32%] space-y-8">
          <Sidebar />
        </aside>
      </main>
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
import Sidebar from '@/pages/frontend/sidebar.vue'
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