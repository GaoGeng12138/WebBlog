<template>
  <div class="article-detail-page min-h-screen bg-[#F8FAFC] flex flex-col">
    <!-- Header -->
    <AppHeader :keyword="keyword" @update:keyword="keyword = $event" @search="searchArticles" />
    
    <!-- Main Content -->
    <main class="flex-1 max-w-[1720px] w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 lg:py-12">
      <div class="flex gap-5 lg:gap-6">
        <!-- 左侧导航栏（文章目录） -->
        <aside v-if="article && headings.length > 0" class="hidden 2xl:block w-56 shrink-0">
          <div class="sticky top-24">
            <div class="bg-white/60 backdrop-blur-xl rounded-2xl shadow-sm border border-gray-100 p-5">
              <h3 class="text-sm font-bold text-gray-900 mb-4 flex items-center gap-2">
                <el-icon class="text-blue-500"><Menu /></el-icon>
                文章目录
              </h3>
              <nav class="space-y-1 max-h-[calc(100vh-200px)] overflow-y-auto pr-2 custom-scrollbar">
                <a
                  v-for="(heading, index) in headings"
                  :key="index"
                  :href="`#${heading.id}`"
                  @click.prevent="scrollToHeading(heading.id)"
                  :class="[
                    'block py-2 px-3 text-sm rounded-xl transition-all duration-300 relative',
                    heading.level === 2 ? 'pl-3' : heading.level === 3 ? 'pl-6' : 'pl-9',
                    activeHeading === heading.id 
                      ? 'bg-blue-50 text-blue-700 font-semibold' 
                      : 'text-gray-600 hover:bg-gray-50/80 hover:text-blue-600'
                  ]"
                >
                  <span v-if="activeHeading === heading.id" class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-4 bg-blue-600 rounded-r-full"></span>
                  <span class="truncate block">{{ heading.text }}</span>
                </a>
              </nav>
            </div>
          </div>
        </aside>

        <!-- 主内容区域 -->
        <div class="flex-1 flex flex-col gap-8 min-w-0">
          <!-- Article Detail Section -->
          <article class="flex-1 max-w-[1120px] w-full mx-auto md:mx-0">
            <div v-if="loading" class="py-20 text-center">
              <div class="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
              <p class="mt-4 text-sm text-gray-500 font-medium">全力加载中 ...</p>
            </div>

            <div v-else-if="article" class="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 sm:p-10 lg:p-14">
              <!-- 文章头部信息 -->
              <header class="mb-10">
                <div class="flex flex-wrap items-center gap-3 text-sm text-gray-500 mb-6">
                   <div v-if="article.category" class="bg-blue-50 text-blue-600 px-3 py-1 rounded-full font-medium text-xs">
                     {{ typeof article.category === 'string' ? article.category : article.category.name }}
                   </div>
                   <span class="flex items-center gap-1.5">
                     <svg class="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                     </svg>
                     {{ formatDate(article.createTime) }}
                   </span>
                   <span v-if="article.readNum !== undefined" class="flex items-center gap-1.5 ml-auto">
                     <svg class="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                     </svg>
                     {{ article.readNum }} 次阅读
                   </span>
                </div>

                <div class="flex justify-between items-start gap-4">
                  <h1 class="text-3xl sm:text-4xl font-extrabold text-gray-900 leading-tight">{{ article.title }}</h1>
                  
                  <!-- 收藏按钮 - 仅在功能开启且用户登录时显示 -->
                  <button 
                    v-if="siteConfig.isFeatureEnabled('favoriteEnabled') && isLoggedIn"
                    @click="toggleCollect"
                    :disabled="collectLoading"
                    class="shrink-0 p-2.5 rounded-full transition-all duration-300 border focus:outline-none"
                    :class="isCollected ? 'bg-orange-50 border-orange-200 text-orange-500 hover:bg-orange-100 hover:scale-105' : 'bg-white border-gray-200 text-gray-400 hover:text-orange-500 hover:border-orange-200 hover:bg-orange-50'"
                  >
                    <el-icon class="text-xl leading-none"><StarFilled v-if="isCollected" /><Star v-else /></el-icon>
                  </button>
                  <!-- 未登录用户提示 -->
                  <button 
                    v-else-if="siteConfig.isFeatureEnabled('favoriteEnabled') && !isLoggedIn"
                    @click="handleCollectClickForGuest"
                    class="shrink-0 p-2.5 rounded-full bg-white border border-gray-200 text-gray-400 hover:text-orange-500 hover:border-orange-200 hover:bg-orange-50 transition-all duration-300 focus:outline-none"
                  >
                     <el-icon class="text-xl leading-none"><Star /></el-icon>
                  </button>
                </div>

                <div v-if="article.tags && article.tags.length" class="flex flex-wrap gap-2 mt-6">
                  <span v-for="tag in article.tags" :key="tag.id || tag.name" 
                    class="inline-flex items-center px-2.5 py-1 rounded-md text-xs font-medium bg-gray-100 text-gray-600 hover:bg-gray-200 transition-colors cursor-pointer">
                    # {{ tag.name || tag }}
                  </span>
                </div>
              </header>

              <!-- 封面图 -->
              <div v-if="article.cover" class="mb-10 rounded-2xl overflow-hidden bg-gray-50 border border-gray-100 shadow-sm relative group">
                <img :src="article.cover" :alt="article.title" class="w-full h-auto max-h-[500px] object-cover transition-transform duration-700 group-hover:scale-105">
              </div>

              <!-- 正文内容 -->
              <div ref="articleContentRef" class="prose prose-blue prose-lg max-w-none article-content" v-viewer v-html="article.content"></div>

              <!-- 上一篇/下一篇导航 -->
              <div class="mt-16 pt-8 border-t border-gray-100">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4 sm:gap-6">
                  <!-- 上一篇 -->
                  <router-link v-if="displayPreArticle" :to="`/article/${displayPreArticle.articleId}`"
                    class="group flex flex-col p-5 rounded-2xl border border-gray-100 bg-gray-50/50 hover:bg-white hover:border-blue-200 hover:shadow-md transition-all duration-300">
                    <span class="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2 group-hover:text-blue-500 transition-colors">Previous</span>
                    <span class="text-base font-bold text-gray-900 line-clamp-2 group-hover:text-blue-600">{{ displayPreArticle.articleTitle }}</span>
                  </router-link>
                  <div v-else class="flex flex-col p-5 rounded-2xl border border-dashed border-gray-200 bg-gray-50/30">
                    <span class="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2">Previous</span>
                    <span class="text-sm text-gray-400">已经是第一篇了</span>
                  </div>

                  <!-- 下一篇 -->
                  <router-link v-if="displayNextArticle" :to="`/article/${displayNextArticle.articleId}`"
                    class="group flex flex-col md:text-right p-5 rounded-2xl border border-gray-100 bg-gray-50/50 hover:bg-white hover:border-blue-200 hover:shadow-md transition-all duration-300">
                    <span class="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2 group-hover:text-blue-500 transition-colors">Next</span>
                    <span class="text-base font-bold text-gray-900 line-clamp-2 group-hover:text-blue-600">{{ displayNextArticle.articleTitle }}</span>
                  </router-link>
                  <div v-else class="flex flex-col md:text-right p-5 rounded-2xl border border-dashed border-gray-200 bg-gray-50/30">
                    <span class="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2">Next</span>
                    <span class="text-sm text-gray-400">已经是最后一篇了</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Comment Section -->
            <CommentSection v-if="article && !loading" :article-id="article.id" class="mt-8 bg-white rounded-3xl shadow-sm border border-gray-100 p-6 sm:p-10 lg:p-12" />

            <!-- 404状态 -->
            <div v-if="!article && !loading" class="py-24 text-center bg-white rounded-3xl shadow-sm border border-gray-100 px-6">
              <div class="w-24 h-24 mx-auto mb-6 bg-gray-50 rounded-full flex items-center justify-center">
                <svg class="h-12 w-12 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
              </div>
              <h3 class="text-xl font-bold text-gray-900 mb-2">文章未找到</h3>
              <p class="text-gray-500 max-w-sm mx-auto mb-8">抱歉，您访问的文章不存在或已被删除。可能链接有误或它已不在地球上。</p>
              <router-link to="/" class="inline-flex items-center justify-center px-6 py-2.5 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 transition-colors">
                返回首页
              </router-link>
            </div>
          </article>
        </div>

        <!-- Sidebar -->
        <aside class="w-[260px] xl:w-[280px] shrink-0">
          <HomeSidebar />
        </aside>
      </div> <!-- 闭合 <div class="flex gap-8 lg:gap-12"> -->
    </main>

    <AppFooter />
  </div>
</template>

<script setup>
import { collectArticle, uncollectArticle, isArticleCollected } from '@/api/frontend/favorite'
import { getArticleDetail, getArticlePageListByCategory } from '@/api/frontend/article'
import AppHeader from '@/components/frontend/AppHeader.vue'
import AppFooter from '@/components/frontend/AppFooter.vue'
import HomeSidebar from '@/pages/frontend/HomeSidebar.vue'
import CommentSection from '@/components/frontend/CommentSection.vue'
import hljs from 'highlight.js'
// 代码高亮样式
import 'highlight.js/styles/tokyo-night-dark.css'
import moment from 'moment'
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
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
const categoryNeighbors = ref({ preArticle: null, nextArticle: null })

// 检查用户是否已登录
const isLoggedIn = computed(() => {
  return !!userStore.frontendUserInfo && !!userStore.frontendUserInfo.userId
})

const normalizedCategoryId = computed(() => {
  if (!route.query.categoryId) {
    return null
  }

  const categoryId = Number(route.query.categoryId)
  return Number.isFinite(categoryId) ? categoryId : null
})

const displayPreArticle = computed(() => {
  if (normalizedCategoryId.value) {
    return categoryNeighbors.value.preArticle
  }
  return article.value?.preArticle || null
})

const displayNextArticle = computed(() => {
  if (normalizedCategoryId.value) {
    return categoryNeighbors.value.nextArticle
  }
  return article.value?.nextArticle || null
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

onBeforeUnmount(() => {
  window.removeEventListener('scroll', updateActiveHeading)
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
      const pre = block.parentElement
      if (pre) {
        pre.classList.add('code-block-shell')
        const existingToolbar = pre.querySelector('.code-block-toolbar')
        if (existingToolbar) {
          existingToolbar.remove()
        }

        const languageClass = Array.from(block.classList).find((name) => name.startsWith('language-'))
        const hljsLanguage = Array.from(block.classList).find((name) => name.startsWith('hljs-'))
        const rawLanguage = block.getAttribute('data-language')
          || (languageClass ? languageClass.replace('language-', '') : '')
          || (hljsLanguage ? hljsLanguage.replace('hljs-', '') : '')
          || ''
        const language = formatLanguageLabel(rawLanguage)

        const toolbar = document.createElement('div')
        toolbar.className = 'code-block-toolbar'

        const dots = document.createElement('div')
        dots.className = 'code-block-dots'
        dots.innerHTML = '<span></span><span></span><span></span>'

        const actions = document.createElement('div')
        actions.className = 'code-block-actions'

        const languageLabel = document.createElement('span')
        languageLabel.className = 'code-language-label'
        languageLabel.textContent = language

        const copyButton = document.createElement('button')
        copyButton.className = 'code-copy-button'
        copyButton.type = 'button'
        copyButton.textContent = '复制'
        copyButton.addEventListener('click', async () => {
          try {
            await navigator.clipboard.writeText(block.textContent || '')
            copyButton.textContent = '已复制'
            setTimeout(() => {
              copyButton.textContent = '复制'
            }, 1600)
          } catch (error) {
            console.warn('复制代码失败:', error)
            copyButton.textContent = '复制失败'
            setTimeout(() => {
              copyButton.textContent = '复制'
            }, 1600)
          }
        })

        actions.appendChild(copyButton)
        actions.appendChild(languageLabel)
        toolbar.appendChild(dots)
        toolbar.appendChild(actions)
        pre.insertBefore(toolbar, block)
      }
      console.log(`第 ${index + 1} 个代码块高亮成功`)
    } catch (e) {
      console.warn(`第 ${index + 1} 个代码块高亮失败:`, e)
    }
  })
}

function formatLanguageLabel(language) {
  const key = String(language || '').trim().toLowerCase()
  const labelMap = {
    js: 'JS',
    jsx: 'JSX',
    javascript: 'JS',
    ts: 'TS',
    tsx: 'TSX',
    typescript: 'TS',
    sh: 'BASH',
    shell: 'BASH',
    bash: 'BASH',
    zsh: 'ZSH',
    powershell: 'POWERSHELL',
    ps1: 'POWERSHELL',
    py: 'PYTHON',
    python: 'PYTHON',
    java: 'JAVA',
    c: 'C',
    cpp: 'C++',
    'c++': 'C++',
    csharp: 'C#',
    cs: 'C#',
    go: 'GO',
    golang: 'GO',
    php: 'PHP',
    html: 'HTML',
    xml: 'XML',
    css: 'CSS',
    scss: 'SCSS',
    less: 'LESS',
    json: 'JSON',
    yaml: 'YAML',
    yml: 'YAML',
    toml: 'TOML',
    sql: 'SQL',
    kotlin: 'KOTLIN',
    kt: 'KOTLIN',
    swift: 'SWIFT',
    rust: 'RUST',
    rs: 'RUST',
    ruby: 'RUBY',
    rb: 'RUBY',
    vue: 'VUE',
    dockerfile: 'DOCKER',
    docker: 'DOCKER',
    plaintext: 'TEXT',
    text: 'TEXT',
    txt: 'TEXT',
    md: 'MARKDOWN',
    markdown: 'MARKDOWN'
  }

  return labelMap[key] || (key ? key.toUpperCase() : 'TEXT')
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
      await resolveCategoryNeighbors()
      
      // 动态更新浏览器标签页标题
      document.title = `${article.value.title} - ThoughtFlow`
      
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
    categoryNeighbors.value = { preArticle: null, nextArticle: null }
  } finally {
    loading.value = false
  }
}

async function resolveCategoryNeighbors() {
  const categoryId = normalizedCategoryId.value
  const currentArticleId = Number(article.value?.id)

  if (!categoryId || !currentArticleId) {
    categoryNeighbors.value = { preArticle: null, nextArticle: null }
    return
  }

  try {
    const res = await getArticlePageListByCategory({
      current: 1,
      size: 200,
      categoryId
    })

    if (!(res && res.success && Array.isArray(res.data))) {
      categoryNeighbors.value = { preArticle: null, nextArticle: null }
      return
    }

    const list = res.data
      .map(item => ({
        articleId: Number(item.id || item._id),
        articleTitle: item.title
      }))
      .filter(item => item.articleId)

    const currentIndex = list.findIndex(item => item.articleId === currentArticleId)

    if (currentIndex === -1) {
      categoryNeighbors.value = { preArticle: null, nextArticle: null }
      return
    }

    categoryNeighbors.value = {
      preArticle: currentIndex > 0 ? list[currentIndex - 1] : null,
      nextArticle: currentIndex < list.length - 1 ? list[currentIndex + 1] : null
    }
  } catch (error) {
    console.error('按分类计算相邻文章失败:', error)
    categoryNeighbors.value = { preArticle: null, nextArticle: null }
  }
}

function formatDate(ts) {
  if (!ts) return ''
  return moment(ts).format('YYYY-MM-DD HH:mm')
}

// 提取文章目录
function extractHeadings() {
  if (!articleContentRef.value) return

  window.removeEventListener('scroll', updateActiveHeading)
  
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
::v-deep(.article-content) {
  color: #334155;
  font-size: 1.02rem;
  line-height: 1.95;
}

::v-deep(.article-content > :first-child) {
  margin-top: 0;
}

::v-deep(.article-content h1),
::v-deep(.article-content h2),
::v-deep(.article-content h3),
::v-deep(.article-content h4) {
  color: #0f172a;
  font-weight: 800;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

::v-deep(.article-content h2) {
  margin-top: 3.2rem;
  margin-bottom: 1.25rem;
  padding-bottom: 0.85rem;
  border-bottom: 1px solid rgba(226, 232, 240, 0.9);
  font-size: clamp(1.8rem, 2.8vw, 2.8rem);
}

::v-deep(.article-content h3) {
  margin-top: 2.2rem;
  margin-bottom: 1rem;
  font-size: clamp(1.3rem, 2vw, 1.75rem);
}

::v-deep(.article-content h4) {
  margin-top: 1.8rem;
  margin-bottom: 0.75rem;
  font-size: 1.15rem;
}

::v-deep(.article-content p) {
  margin: 1rem 0;
}

::v-deep(.article-content strong) {
  color: #0f172a;
  font-weight: 800;
}

::v-deep(.article-content a) {
  color: #d97706;
  text-decoration: none;
  border-bottom: 1px solid rgba(245, 158, 11, 0.25);
  transition: color 0.25s ease, border-color 0.25s ease;
}

::v-deep(.article-content a:hover) {
  color: #b45309;
  border-color: rgba(217, 119, 6, 0.45);
}

::v-deep(.article-content ul),
::v-deep(.article-content ol) {
  margin: 1.15rem 0;
  padding-left: 1.5rem;
}

::v-deep(.article-content li) {
  margin: 0.55rem 0;
  padding-left: 0.2rem;
}

::v-deep(.article-content blockquote) {
  margin: 1.8rem 0;
  padding: 1.1rem 1.25rem;
  border-left: 4px solid #f59e0b;
  border-radius: 0 18px 18px 0;
  background: linear-gradient(135deg, rgba(255, 251, 235, 0.95), rgba(255, 255, 255, 0.92));
  color: #475569;
}

::v-deep(.article-content hr) {
  margin: 2.2rem 0;
  border: 0;
  border-top: 1px solid rgba(226, 232, 240, 0.9);
}

::v-deep(.article-content img) {
  display: block;
  max-width: 100%;
  margin: 1.8rem auto;
  border-radius: 20px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
}

::v-deep(.article-content table) {
  width: 100%;
  margin: 1.6rem 0;
  overflow: hidden;
  border-collapse: separate;
  border-spacing: 0;
  border: 1px solid rgba(226, 232, 240, 0.95);
  border-radius: 18px;
  background: #fff;
}

::v-deep(.article-content th),
::v-deep(.article-content td) {
  padding: 0.9rem 1rem;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  text-align: left;
}

::v-deep(.article-content th) {
  background: #fff7ed;
  color: #9a3412;
  font-weight: 700;
}

::v-deep(.article-content tr:last-child td) {
  border-bottom: none;
}

::v-deep(.article-content code:not(pre code)) {
  padding: 0.18rem 0.45rem;
  margin: 0 0.15rem;
  border-radius: 8px;
  border: 1px solid rgba(251, 191, 36, 0.18);
  background: #fff7ed;
  color: #c2410c;
  font-size: 0.92em !important;
  font-family: 'JetBrains Mono', 'Cascadia Code', Consolas, Monaco, monospace;
}

::v-deep(pre) {
  margin: 1.7rem 0;
  position: relative;
}

::v-deep(pre.code-block-shell) {
  overflow: hidden;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 20px;
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.08);
  background: #1f2937;
}

::v-deep(.code-block-toolbar) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.6rem 0.9rem 0.5rem;
  border-bottom: 1px solid rgba(148, 163, 184, 0.12);
  background: rgba(15, 23, 42, 0.22);
}

::v-deep(.code-block-dots) {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

::v-deep(.code-block-dots span) {
  width: 9px;
  height: 9px;
  border-radius: 999px;
}

::v-deep(.code-block-dots span:nth-child(1)) {
  background: #fc625d;
}

::v-deep(.code-block-dots span:nth-child(2)) {
  background: #fdbc40;
}

::v-deep(.code-block-dots span:nth-child(3)) {
  background: #35cd4b;
}

::v-deep(.code-block-actions) {
  display: inline-flex;
  align-items: center;
  gap: 0.55rem;
}

::v-deep(.code-language-label) {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.12em;
  color: rgba(226, 232, 240, 0.82);
}

::v-deep(pre code.hljs) {
  padding: 1rem 1.2rem 1.15rem;
  border-radius: 0 0 20px 20px;
  font-size: 0.94rem;
  line-height: 1.8;
  font-family: 'JetBrains Mono', 'Cascadia Code', Consolas, Monaco, monospace;
}

::v-deep(.code-copy-button) {
  padding: 0.28rem 0.62rem;
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.32);
  color: #f8fafc;
  font-size: 11px;
  font-weight: 600;
  transition: background 0.25s ease, transform 0.25s ease;
}

::v-deep(.code-copy-button:hover) {
  background: rgba(245, 158, 11, 0.85);
  transform: translateY(-1px);
}
</style>
