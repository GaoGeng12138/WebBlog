<template>
  <div>
    <!-- Header -->
    <AppHeader :keyword="keyword" @update:keyword="keyword = $event" @search="search" />
    
    <!-- Main Content -->
    <div class="max-w-[1600px] mx-auto px-4 sm:px-6 lg:px-8">
      <main class="flex gap-8 py-8">
        <!-- 左侧导航栏（文章目录） -->
        <aside v-if="article && headings.length > 0" class="hidden xl:block w-64 shrink-0">
          <div class="sticky top-20">
            <div class="bg-white rounded-xl shadow-md border border-gray-200 p-4">
              <h3 class="text-lg font-bold text-gray-900 mb-4 flex items-center gap-2">
                <el-icon class="text-blue-600"><Menu /></el-icon>
                文章目录
              </h3>
              <nav class="space-y-1 max-h-[calc(100vh-200px)] overflow-y-auto">
                <a
                  v-for="(heading, index) in headings"
                  :key="index"
                  :href="`#${heading.id}`"
                  @click.prevent="scrollToHeading(heading.id)"
                  :class="[
                    'block py-2.5 px-3 text-sm rounded-lg transition-all duration-200 border-l-3',
                    heading.level === 2 ? 'pl-3' : heading.level === 3 ? 'pl-6' : 'pl-9',
                    activeHeading === heading.id 
                      ? 'bg-gradient-to-r from-blue-50 to-blue-100 text-blue-700 font-bold border-l-4 border-blue-600 shadow-sm transform scale-105' 
                      : 'text-gray-600 hover:bg-gray-50 hover:text-blue-600 border-transparent hover:border-gray-300 hover:shadow-sm'
                  ]"
                >
                  <span :class="activeHeading === heading.id ? 'flex items-center gap-2' : ''">
                    <span v-if="activeHeading === heading.id" class="w-1.5 h-1.5 bg-blue-600 rounded-full animate-pulse"></span>
                    {{ heading.text }}
                  </span>
                </a>
              </nav>
            </div>
          </div>
        </aside>

        <!-- 主内容区域 -->
        <div class="flex-1 flex gap-8 min-w-0">
          <!-- Article Detail Section -->
          <section class="flex-1 max-w-4xl">
        <div v-if="loading" class="py-16 text-center">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
          <p class="mt-2 text-gray-600">加载中...</p>
        </div>

        <div v-else-if="article" class="bg-white rounded-xl shadow-md p-6">
          <div class="flex justify-between items-start mb-4">
            <h1 class="text-3xl font-bold text-gray-900">{{ article.title }}</h1>
            <!-- 收藏按钮 - 仅在功能开启且用户登录时显示 -->
            <el-button 
              v-if="siteConfig.isFeatureEnabled('favoriteEnabled') && isLoggedIn"
              :type="isCollected ? 'danger' : 'default'" 
              :icon="isCollected ? 'StarFilled' : 'Star'"
              circle
              @click="toggleCollect"
              :loading="collectLoading"
              class="!border-0"
            >
            </el-button>
            <!-- 未登录用户提示 -->
            <el-button 
              v-else-if="siteConfig.isFeatureEnabled('favoriteEnabled') && !isLoggedIn"
              type="default" 
              icon="Star"
              circle
              @click="handleCollectClickForGuest"
              class="!border-0"
            >
            </el-button>
          </div>

          <div class="flex items-center gap-4 text-gray-600 mb-6">
            <span>{{ formatDate(article.createTime) }}</span>
            <span v-if="article.category">分类: {{ typeof article.category === 'string' ? article.category :
              article.category.name }}</span>
            <span v-if="article.readNum !== undefined">阅读: {{ article.readNum }} 次</span>
          </div>

          <div class="flex flex-wrap gap-2 mb-6">
            <el-tag v-for="tag in article.tags" :key="tag.id || tag.name" type="success" size="small"
              class="rounded-full px-2 py-1">
              {{ tag.name || tag }}
            </el-tag>
          </div>

          <div v-if="article.cover" class="mb-6">
            <img :src="article.cover" :alt="article.title" class="w-full h-auto rounded-lg">
          </div>
          <!-- 正文 -->
          <div ref="articleContentRef" class="prose prose-lg max-w-none mt-5 article-content" v-viewer
            v-html="article.content">
          </div>

          <!-- 上一篇/下一篇导航 -->
          <div class="mt-12 pt-8 border-t border-gray-200">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- 上一篇 -->
              <div v-if="article.preArticle">
                <a href="#" @click.prevent="navigateToArticle(article.preArticle.articleId)"
                  class="block p-4 rounded-lg border border-gray-200 hover:border-blue-500 hover:shadow-md transition-all duration-300 group">
                  <p class="text-xs text-gray-500 mb-2">上一篇</p>
                  <p class="text-lg font-semibold text-gray-900 group-hover:text-blue-600 line-clamp-2">
                    {{ article.preArticle.articleTitle }}
                  </p>
                  <p class="text-xs text-gray-400 mt-2">← 阅读</p>
                </a>
              </div>
              <div v-else class="p-4 rounded-lg border border-gray-200 bg-gray-50">
                <p class="text-xs text-gray-500 mb-2">上一篇</p>
                <p class="text-gray-400">没有了</p>
              </div>

              <!-- 下一篇 -->
              <div v-if="article.nextArticle" class="md:col-start-2">
                <a href="#" @click.prevent="navigateToArticle(article.nextArticle.articleId)"
                  class="block p-4 rounded-lg border border-gray-200 hover:border-blue-500 hover:shadow-md transition-all duration-300 group">
                  <p class="text-xs text-gray-500 mb-2">下一篇</p>
                  <p class="text-lg font-semibold text-gray-900 group-hover:text-blue-600 line-clamp-2">
                    {{ article.nextArticle.articleTitle }}
                  </p>
                  <p class="text-xs text-gray-400 mt-2">阅读 →</p>
                </a>
              </div>
              <div v-else class="md:col-start-2 p-4 rounded-lg border border-gray-200 bg-gray-50">
                <p class="text-xs text-gray-500 mb-2">下一篇</p>
                <p class="text-gray-400">没有了</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Comment Section -->
        <CommentSection v-if="article" :article-id="article.id" class="mt-8" />

        <div v-else class="py-16 text-center">
          <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <h3 class="mt-2 text-sm font-medium text-gray-900">文章未找到</h3>
          <p class="mt-1 text-sm text-gray-500">抱歉，您访问的文章不存在或已被删除。</p>
          <div class="mt-6">
            <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
          </div>
        </div>
      </section>

      <!-- Sidebar -->
      <aside class="w-full lg:w-80 shrink-0">
        <Sidebar />
      </aside>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { collectArticle, uncollectArticle, isArticleCollected } from '@/api/frontend/favorite'
import { getArticleDetail } from '@/api/frontend/article'
import AppHeader from '@/components/frontend/AppHeader.vue'
import Sidebar from '@/pages/frontend/sidebar.vue'
import CommentSection from '@/components/frontend/CommentSection.vue'
import hljs from 'highlight.js'
// 代码高亮样式
import 'highlight.js/styles/tokyo-night-dark.css'
import moment from 'moment'
import { nextTick, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Star, StarFilled, Menu } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useSiteConfigStore } from '@/stores/siteConfig'
import { computed } from 'vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const siteConfig = useSiteConfigStore()

const article = ref(null)
const loading = ref(false)
const searchKeyword = ref('')
const articleContentRef = ref(null)
const isCollected = ref(false)
const collectLoading = ref(false)
const headings = ref([]) // 文章目录
const activeHeading = ref('') // 当前激活的标题

// 检查用户是否已登录
const isLoggedIn = computed(() => {
  return !!userStore.frontendUserInfo && !!userStore.frontendUserInfo.userId
})

onMounted(() => {
    // 先刷新配置，确保权限是最新的
    console.log('刷新网站权限配置...')
    siteConfig.fetchPermissions().then(() => {
        console.log('权限配置刷新完成')
        // Load frontend user info first, then load article
        userStore.setFrontendUserInfo().then(() => {
            loadArticle()
        }).catch((error) => {
            console.error('Failed to load user info:', error)
            // Even if user info fails to load, we still try to load the article
            loadArticle()
        })
    })
})

// 当路由参数 id 变化时，重新加载文章（保证使用路由跳转也能触发）
watch(() => route.params.id, (newId, oldId) => {
  if (newId && newId !== oldId) {
    loadArticle()
  }
})

// 点击上一篇/下一篇时调用：更新地址并触发加载
function navigateToArticle(id) {
  if (!id) return
  // 更新路由（push 会改变地址），watch 会触发 loadArticle
  router.push(`/article/${id}`)
  // 平滑滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 代码高亮函数
function highlightCode() {
  if (!articleContentRef.value) {
    console.warn('articleContentRef.value 为空')
    return
  }

  // 使用 DOM API 直接查询
  const blocks = articleContentRef.value.querySelectorAll('pre code')
  console.log(`找到 ${blocks.length} 个代码块`)

  blocks.forEach((block, index) => {
    try {
      console.log(`正在高亮第 ${index + 1} 个代码块`)
      // 移除之前的 hljs 类（防止重复高亮）
      block.removeAttribute('data-highlighted')
      // 执行高亮
      hljs.highlightElement(block)
      console.log(`第 ${index + 1} 个代码块高亮成功`)
    } catch (e) {
      console.warn(`第 ${index + 1} 个代码块高亮失败:`, e)
    }
  })
}

// 切换收藏状态
async function toggleCollect() {
  if (!article.value) return
  
  // 检查收藏功能是否开启
  if (!siteConfig.isFeatureEnabled('favoriteEnabled')) {
    ElMessage.warning('收藏功能已关闭')
    return
  }
  
  // 检查用户是否已登录
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再进行收藏操作')
    // 可以选择跳转到登录页面
    router.push('/login')
    return
  }
  
  collectLoading.value = true
  try {
    if (isCollected.value) {
      // 取消收藏
      await uncollectArticle(article.value.id)
      isCollected.value = false
      ElMessage.success('已取消收藏')
    } else {
      // 收藏
      await collectArticle(article.value.id)
      isCollected.value = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    // 检查是否是权限错误
    if (error.response && error.response.data) {
      const errorMsg = error.response.data.message || error.response.data.errorMsg
      if (errorMsg && (errorMsg.includes('无访问权限') || errorMsg.includes('请先登录'))) {
        ElMessage.warning('请先登录后再进行收藏操作')
        // 可以选择跳转到登录页面
        router.push('/login')
        return
      }
    }
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    collectLoading.value = false
  }
}

// 检查文章是否已收藏
async function checkIfCollected() {
  if (!article.value) return
  
  // 检查收藏功能是否开启
  if (!siteConfig.isFeatureEnabled('favoriteEnabled')) {
    isCollected.value = false
    return
  }
  
  try {
    const res = await isArticleCollected(article.value.id)
    console.log('检查收藏状态结果:', res)
    if (res && res.success) {
      isCollected.value = res.data
    } else if (res && !res.success && res.errorCode === '20002') {
      // 特殊处理认证错误
      console.log('用户未登录，无法检查收藏状态')
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
    // 如果是权限错误，不显示错误消息，因为用户可能未登录
    if (error.response && error.response.data) {
      const errorMsg = error.response.data.message || error.response.data.errorMsg
      const errorCode = error.response.data.errorCode
      if (errorCode === '20002' || (errorMsg && (errorMsg.includes('无访问权限') || errorMsg.includes('请先登录')))) {
        // 认证错误，不显示错误消息
        console.log('用户未登录，无法检查收藏状态')
      } else {
        ElMessage.error('检查收藏状态失败')
      }
    }
  }
}

async function loadArticle() {
  const articleId = route.params.id
  if (!articleId) {
    console.warn('未获取到文章 ID')
    return
  }

  loading.value = true
  try {
    // 后端期望接收 JSON { articleId: <number> }
    const res = await getArticleDetail(Number(articleId))
    console.log('文章加载结果:', res)

    if (res && res.success) {
      article.value = res.data
      console.log('文章数据设置成功')
      
      // 动态更新浏览器标签页标题
      const userInfo = userStore.frontendUserInfo
      const nickname = userInfo?.nickname
      const siteTitle = siteConfig.config?.title || 'WebLog'
      
      if (nickname) {
        document.title = `${article.value.title} - ${nickname}のBlog`
      } else {
        document.title = `${article.value.title} - ${siteTitle}`
      }
      
      // 检查是否已收藏（只有登录用户才会检查）
      if (isLoggedIn.value) {
        await checkIfCollected()
      }
    } else {
      article.value = null
      console.warn('文章加载失败或无数据')
    }

    // 等待 DOM 更新后对文章中的代码块进行高亮
    await nextTick()

    // 增加一个小延迟确保 DOM 完全渲染
    setTimeout(() => {
      highlightCode()
      extractHeadings() // 提取文章目录
    }, 100)

  } catch (e) {
    console.error('加载文章失败', e)
    article.value = null
  } finally {
    loading.value = false
  }
}

function formatDate(ts) {
  if (!ts) return ''
  return moment(ts).format('YYYY-MM-DD HH:mm')
}

// 提取文章目录
function extractHeadings() {
  if (!articleContentRef.value) return
  
  const contentEl = articleContentRef.value
  const headingElements = contentEl.querySelectorAll('h2, h3, h4')
  
  headings.value = Array.from(headingElements).map((el, index) => {
    // 为每个标题添加 ID
    const id = el.id || `heading-${index}`
    el.id = id
    
    return {
      id,
      text: el.textContent,
      level: parseInt(el.tagName.substring(1)) // h2 -> 2, h3 -> 3
    }
  })
  
  // 监听滚动，高亮当前标题
  window.addEventListener('scroll', updateActiveHeading)
  updateActiveHeading()
}

// 更新当前激活的标题
function updateActiveHeading() {
  if (headings.value.length === 0) return
  
  const scrollPosition = window.scrollY + 100
  
  for (let i = headings.value.length - 1; i >= 0; i--) {
    const heading = headings.value[i]
    const element = document.getElementById(heading.id)
    
    if (element && element.offsetTop <= scrollPosition) {
      activeHeading.value = heading.id
      return
    }
  }
  
  activeHeading.value = headings.value[0]?.id || ''
}

// 滚动到指定标题
function scrollToHeading(id) {
  const element = document.getElementById(id)
  if (element) {
    const top = element.offsetTop - 80 // 留出 header 空间
    window.scrollTo({
      top,
      behavior: 'smooth'
    })
  }
}

function searchArticles() {
  if (searchKeyword.value.trim()) {
    router.push(`/?search=${encodeURIComponent(searchKeyword.value.trim())}`)
  }
}

// 处理未登录用户的收藏点击
function handleCollectClickForGuest() {
  ElMessage.warning('请先登录后再进行收藏操作')
  router.push('/login')
}
</script>

<style scoped>
/* code 样式 */
::v-deep(.article-content code:not(pre code)) {
  padding: 2px 4px;
  margin: 0 2px;
  font-size: 95% !important;
  border-radius: 4px;
  color: rgb(41, 128, 185);
  background-color: rgba(27, 31, 35, 0.05);
  font-family: Operator Mono, Consolas, Monaco, Menlo, monospace;
}

/* pre code 样式 */
::v-deep(code) {
  font-size: 98%;
}

::v-deep(pre) {
  margin-bottom: 20px;
  position: relative;
}

::v-deep(pre code.hljs) {
  padding-top: 2rem;
  padding-left: 1rem;
  padding-right: 1rem;
  padding-bottom: 0.7rem;
  border-radius: 6px;
}

::v-deep(pre:before) {
  background: #fc625d;
  border-radius: 50%;
  box-shadow: 20px 0 #fdbc40, 40px 0 #35cd4b;
  content: ' ';
  height: 10px;
  position: absolute;
  width: 10px;
  top: 12px;
  left: 12px;
}
</style>