<template>
  <div class="article-detail-page min-h-screen bg-transparent flex flex-col">
    <!-- Header -->
    <AppHeader :keyword="searchKeyword" @update:keyword="searchKeyword = $event" @search="searchArticles" />
    
    <!-- Main Content -->
    <main class="flex-1 max-w-[1720px] w-full mx-auto px-3 sm:px-6 lg:px-8 py-6 sm:py-8 lg:py-12">
      <div class="grid grid-cols-1 gap-6 xl:grid-cols-[260px_minmax(0,1fr)_280px] xl:gap-6">
        <!-- 左侧导航栏（文章目录） -->
        <aside v-if="article" class="hidden xl:block">
          <div class="sticky top-24">
            <div class="rounded-[26px] border border-[rgba(129,158,196,0.22)] bg-white/78 p-5 shadow-[0_18px_48px_rgba(120,146,184,0.14)] backdrop-blur-2xl">
              <h3 class="mb-4 flex items-center gap-2 text-sm font-bold text-slate-800">
                <el-icon class="text-[var(--theme-primary)]"><Menu /></el-icon>
                文章目录
              </h3>
              <nav
                v-if="headings.length > 0"
                class="space-y-1 max-h-[calc(100vh-200px)] overflow-y-auto pr-2 custom-scrollbar"
              >
                <a
                  v-for="(heading, index) in headings"
                  :key="index"
                  :href="`#${heading.id}`"
                  @click.prevent="scrollToHeading(heading.id)"
                  :class="[
                    'block py-2 px-3 text-sm rounded-xl transition-all duration-300 relative',
                    heading.level === 1 ? 'pl-3' : heading.level === 2 ? 'pl-6' : heading.level === 3 ? 'pl-9' : 'pl-12',
                    activeHeading === heading.id 
                      ? 'bg-[rgba(116,149,195,0.16)] text-[var(--theme-primary-deep)] font-semibold shadow-[inset_0_0_0_1px_rgba(116,149,195,0.18)]'
                      : 'text-slate-500 hover:bg-white/90 hover:text-[var(--theme-primary)]'
                  ]"
                >
                  <span v-if="activeHeading === heading.id" class="absolute left-0 top-1/2 h-4 w-1 -translate-y-1/2 rounded-r-full bg-[var(--theme-primary)]"></span>
                  <span class="truncate block">{{ heading.text }}</span>
                </a>
              </nav>
              <div v-else class="rounded-2xl border border-dashed border-[rgba(129,158,196,0.28)] bg-[rgba(244,248,252,0.92)] px-4 py-5 text-sm leading-6 text-slate-500">
                正文里暂时没有识别到可导航的标题，等内容渲染完成后目录会自动出现。
              </div>
            </div>
          </div>
        </aside>

        <!-- 主内容区域 -->
        <div class="min-w-0 flex flex-col gap-8">
          <!-- Article Detail Section -->
          <article class="w-full min-w-0">
            <div v-if="loading" class="py-20 text-center">
              <div class="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
              <p class="mt-4 text-sm text-gray-500 font-medium">全力加载中 ...</p>
            </div>

            <div v-else-if="article" class="rounded-[28px] border border-[rgba(129,158,196,0.2)] bg-white/92 p-4 shadow-[0_24px_80px_rgba(120,146,184,0.14)] backdrop-blur-xl sm:rounded-[34px] sm:p-10 lg:p-14">
              <!-- 封面图 -->
              <div v-if="article.cover" class="mb-8 rounded-[28px] overflow-hidden border border-slate-200/80 bg-slate-950 shadow-[0_24px_70px_rgba(15,23,42,0.12)]">
                <img :src="article.cover" :alt="article.title || article.summary || '文章封面'" class="article-hero-image w-full h-auto max-h-[560px] object-contain">
              </div>

              <!-- 文章头部信息 -->
              <header class="mb-10">
                <div v-if="article.title || article.summary" class="mb-6 space-y-5">
                  <h1 v-if="article.title" class="text-[clamp(1.95rem,8.4vw,3.55rem)] sm:text-4xl font-extrabold text-gray-900 leading-[1.08] tracking-tight">{{ article.title }}</h1>

                  <p v-if="article.summary" class="max-w-3xl text-[0.98rem] leading-7 text-slate-600 sm:text-base sm:leading-8">
                    {{ article.summary }}
                  </p>
                </div>

                <div v-if="headings.length > 0" class="mb-6 xl:hidden">
                  <button
                    type="button"
                    class="flex w-full items-center justify-between rounded-2xl border border-[rgba(129,158,196,0.18)] bg-[rgba(244,248,252,0.9)] px-4 py-3 text-left text-sm font-semibold text-slate-700 shadow-[0_12px_28px_rgba(120,146,184,0.08)]"
                    @click="mobileTocOpen = !mobileTocOpen"
                  >
                    <span class="inline-flex items-center gap-2">
                      <el-icon class="text-[var(--theme-primary)]"><Menu /></el-icon>
                      文章目录
                    </span>
                    <span class="text-xs font-medium text-slate-400">{{ mobileTocOpen ? '收起' : '展开' }}</span>
                  </button>

                  <transition name="toc-slide">
                    <div
                      v-show="mobileTocOpen"
                      class="mt-3 rounded-2xl border border-[rgba(129,158,196,0.16)] bg-white/92 p-3 shadow-[0_14px_32px_rgba(120,146,184,0.1)]"
                    >
                      <nav class="max-h-72 space-y-1 overflow-y-auto pr-1 custom-scrollbar">
                        <a
                          v-for="(heading, index) in headings"
                          :key="`mobile-${index}`"
                          :href="`#${heading.id}`"
                          @click.prevent="scrollToHeading(heading.id)"
                          :class="[
                            'block rounded-xl py-2 text-sm transition-all duration-300',
                            heading.level === 1 ? 'pl-3' : heading.level === 2 ? 'pl-5' : heading.level === 3 ? 'pl-7' : 'pl-9',
                            activeHeading === heading.id
                              ? 'bg-[rgba(116,149,195,0.14)] text-[var(--theme-primary-deep)] font-semibold'
                              : 'text-slate-500 hover:bg-[rgba(244,248,252,0.96)] hover:text-[var(--theme-primary)]'
                          ]"
                        >
                          <span class="line-clamp-2 block">{{ heading.text }}</span>
                        </a>
                      </nav>
                    </div>
                  </transition>
                </div>

                <div class="flex flex-wrap items-center gap-3 text-sm text-slate-500 sm:gap-4">
                   <div v-if="article.category" class="rounded-full border border-[rgba(116,149,195,0.2)] bg-[rgba(116,149,195,0.12)] px-3 py-1 text-xs font-semibold tracking-[0.08em] text-[var(--theme-primary-deep)] shadow-sm">
                     {{ typeof article.category === 'string' ? article.category : article.category.name }}
                   </div>
                   <span class="flex items-center gap-1.5">
                     <svg class="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                     </svg>
                     {{ formatDate(article.createTime) }}
                   </span>
                   <span v-if="article.readNum !== undefined" class="flex items-center gap-1.5">
                     <svg class="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                     </svg>
                     {{ article.readNum }} 次阅读
                   </span>

                  <!-- 收藏按钮 - 仅在功能开启且用户登录时显示 -->
                  <button 
                    v-if="siteConfig.isFeatureEnabled('favoriteEnabled') && isLoggedIn"
                    @click="toggleCollect"
                    :disabled="collectLoading"
                    class="inline-flex w-full items-center justify-center gap-2 rounded-full border px-4 py-2 text-sm font-medium transition-all duration-300 focus:outline-none sm:ml-auto sm:w-auto"
                    :class="isCollected ? 'border-[rgba(116,149,195,0.24)] bg-[rgba(116,149,195,0.14)] text-[var(--theme-primary-deep)] shadow-sm hover:bg-[rgba(116,149,195,0.18)] hover:-translate-y-0.5' : 'border-[rgba(129,158,196,0.24)] bg-white/90 text-slate-500 hover:border-[rgba(116,149,195,0.28)] hover:bg-[rgba(116,149,195,0.08)] hover:text-[var(--theme-primary)] hover:-translate-y-0.5'"
                  >
                    <el-icon class="text-base leading-none"><StarFilled v-if="isCollected" /><Star v-else /></el-icon>
                    <span>{{ isCollected ? '已收藏' : '收藏文章' }}</span>
                  </button>
                  <!-- 未登录用户提示 -->
                  <button 
                    v-else-if="siteConfig.isFeatureEnabled('favoriteEnabled') && !isLoggedIn"
                    @click="handleCollectClickForGuest"
                    class="inline-flex w-full items-center justify-center gap-2 rounded-full border border-[rgba(129,158,196,0.24)] bg-white/90 px-4 py-2 text-sm font-medium text-slate-500 transition-all duration-300 focus:outline-none hover:-translate-y-0.5 hover:border-[rgba(116,149,195,0.3)] hover:bg-[rgba(116,149,195,0.08)] hover:text-[var(--theme-primary)] sm:ml-auto sm:w-auto"
                  >
                     <el-icon class="text-base leading-none"><Star /></el-icon>
                     <span>登录后收藏</span>
                  </button>
                </div>

                <div v-if="article.tags && article.tags.length" class="flex flex-wrap gap-2 mt-6">
                  <span v-for="tag in article.tags" :key="tag.id || tag.name" 
                    class="inline-flex cursor-pointer items-center rounded-full border border-[rgba(129,158,196,0.16)] bg-[rgba(240,245,251,0.95)] px-3 py-1 text-xs font-medium text-slate-600 transition-colors hover:border-[rgba(116,149,195,0.24)] hover:bg-white hover:text-[var(--theme-primary)]">
                    # {{ tag.name || tag }}
                  </span>
                </div>
              </header>

              <!-- 正文内容 -->
              <div ref="articleContentRef" class="prose prose-blue prose-lg max-w-none article-content" v-viewer v-html="article.content"></div>

              <!-- 上一篇/下一篇导航 -->
              <div class="mt-16 border-t border-[rgba(129,158,196,0.14)] pt-8">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4 sm:gap-6">
                  <!-- 上一篇 -->
                  <router-link v-if="displayPreArticle" :to="`/article/${displayPreArticle.articleId}`"
                    class="group flex flex-col rounded-[24px] border border-[rgba(129,158,196,0.16)] bg-[rgba(244,248,252,0.88)] p-5 transition-all duration-300 hover:-translate-y-1 hover:border-[rgba(116,149,195,0.24)] hover:bg-white hover:shadow-[0_18px_40px_rgba(120,146,184,0.14)]">
                    <span class="mb-2 text-xs font-semibold uppercase tracking-[0.18em] text-slate-400 transition-colors group-hover:text-[var(--theme-primary)]">Previous</span>
                    <span class="line-clamp-2 text-base font-bold text-slate-900 group-hover:text-[var(--theme-primary-deep)]">{{ displayPreArticle.articleTitle || '无标题文章' }}</span>
                  </router-link>
                  <div v-else class="flex flex-col rounded-[24px] border border-dashed border-[rgba(129,158,196,0.2)] bg-[rgba(244,248,252,0.72)] p-5">
                    <span class="mb-2 text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">Previous</span>
                    <span class="text-sm text-slate-400">已经是第一篇了</span>
                  </div>

                  <!-- 下一篇 -->
                  <router-link v-if="displayNextArticle" :to="`/article/${displayNextArticle.articleId}`"
                    class="group flex flex-col rounded-[24px] border border-[rgba(129,158,196,0.16)] bg-[rgba(244,248,252,0.88)] p-5 transition-all duration-300 hover:-translate-y-1 hover:border-[rgba(116,149,195,0.24)] hover:bg-white hover:shadow-[0_18px_40px_rgba(120,146,184,0.14)] md:text-right">
                    <span class="mb-2 text-xs font-semibold uppercase tracking-[0.18em] text-slate-400 transition-colors group-hover:text-[var(--theme-primary)]">Next</span>
                    <span class="line-clamp-2 text-base font-bold text-slate-900 group-hover:text-[var(--theme-primary-deep)]">{{ displayNextArticle.articleTitle || '无标题文章' }}</span>
                  </router-link>
                  <div v-else class="flex flex-col rounded-[24px] border border-dashed border-[rgba(129,158,196,0.2)] bg-[rgba(244,248,252,0.72)] p-5 md:text-right">
                    <span class="mb-2 text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">Next</span>
                    <span class="text-sm text-slate-400">已经是最后一篇了</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Comment Section -->
            <CommentSection v-if="article && !loading" :article-id="article.id" class="mt-8" />

            <!-- Mobile Sidebar -->
            <section v-if="article && !loading" class="mt-8 xl:hidden">
              <HomeSidebar />
            </section>

            <!-- 404状态 -->
            <div v-if="!article && !loading" class="rounded-[34px] border border-[rgba(129,158,196,0.18)] bg-white/92 px-6 py-24 text-center shadow-[0_24px_80px_rgba(120,146,184,0.12)]">
              <div class="mx-auto mb-6 flex h-24 w-24 items-center justify-center rounded-full bg-[rgba(244,248,252,0.92)]">
                <svg class="h-12 w-12 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
              </div>
              <h3 class="mb-2 text-xl font-bold text-slate-900">文章未找到</h3>
              <p class="mx-auto mb-8 max-w-sm text-slate-500">抱歉，您访问的文章不存在或已被删除。可能链接有误或它已不在地球上。</p>
              <router-link to="/" class="theme-btn-primary inline-flex items-center justify-center rounded-xl px-6 py-2.5 font-medium">
                返回首页
              </router-link>
            </div>
          </article>
        </div>

        <!-- Sidebar -->
        <aside class="hidden xl:block">
          <HomeSidebar />
        </aside>
      </div>
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
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Star, StarFilled, Menu } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useSiteConfigStore } from '@/stores/siteConfig'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const siteConfig = useSiteConfigStore()

const article = ref(null)
const loading = ref(true)
const searchKeyword = ref('')
const articleContentRef = ref(null)
const isCollected = ref(false)
const collectLoading = ref(false)
const headings = ref([]) // 文章目录
const activeHeading = ref('') // 当前激活的标题
const categoryNeighbors = ref({ preArticle: null, nextArticle: null })
const mobileTocOpen = ref(false)

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
  // 先刷新配置，确保权限是最新的，再加载用户和文章信息
  siteConfig.fetchPermissions().then(() => {
    userStore.setFrontendUserInfo().then(() => {
      loadArticle()
    }).catch(() => {
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

// 代码高亮函数
function highlightCode() {
  if (!articleContentRef.value) {
    return
  }

  // 使用 DOM API 直接查询
  const blocks = articleContentRef.value.querySelectorAll('pre code')

  blocks.forEach((block, index) => {
    try {
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
    } catch (e) {
      console.warn('代码高亮失败:', e)
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
    if (res && res.success) {
      isCollected.value = res.data
    } else if (res && !res.success && res.errorCode === '20002') {
      // 特殊处理认证错误
    }
  } catch (error) {
    // 如果是权限错误，不显示错误消息，因为用户可能未登录
    if (error.response && error.response.data) {
      const errorMsg = error.response.data.message || error.response.data.errorMsg
      const errorCode = error.response.data.errorCode
      if (errorCode === '20002' || (errorMsg && (errorMsg.includes('无访问权限') || errorMsg.includes('请先登录')))) {
        // 认证错误，不显示错误消息
      } else {
        ElMessage.error('检查收藏状态失败')
      }
    }
  }
}

async function loadArticle() {
  const articleId = route.params.id
  if (!articleId) {
    loading.value = false
    return
  }

  loading.value = true
  try {
    // 后端期望接收 JSON { articleId: <number> }
    const res = await getArticleDetail(Number(articleId))

    if (res && res.success) {
      article.value = res.data
      mobileTocOpen.value = false
      await resolveCategoryNeighbors()
      
      // 动态更新浏览器标签页标题
      document.title = `${article.value.title || '文章详情'} - ThoughtFlow`
      
      // 检查是否已收藏（只有登录用户才会检查）
      if (isLoggedIn.value) {
        await checkIfCollected()
      }
    } else {
      article.value = null
    }

    // 等待 DOM 更新后对文章中的代码块进行高亮
    await nextTick()

    // 增加一个小延迟确保 DOM 完全渲染
    setTimeout(() => {
      highlightCode()
      extractHeadings() // 提取文章目录
    }, 100)

  } catch (e) {
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
  const headingElements = contentEl.querySelectorAll('h1, h2, h3, h4')
  
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
.article-hero-image {
  background:
    radial-gradient(circle at top left, rgba(59, 130, 246, 0.18), transparent 28%),
    linear-gradient(180deg, rgba(15, 23, 42, 0.88), rgba(15, 23, 42, 0.96));
}

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
  color: var(--theme-primary-deep);
  text-decoration: none;
  border-bottom: 1px solid rgba(116, 149, 195, 0.25);
  transition: color 0.25s ease, border-color 0.25s ease;
}

::v-deep(.article-content a:hover) {
  color: var(--theme-primary);
  border-color: rgba(116, 149, 195, 0.42);
}

::v-deep(.article-content ul),
::v-deep(.article-content ol) {
  margin: 1.15rem 0;
  padding-left: 1.5rem;
}

::v-deep(.article-content ul) {
  list-style-type: disc;
}

::v-deep(.article-content ol) {
  list-style-type: decimal;
}

::v-deep(.article-content li) {
  display: list-item;
  margin: 0.55rem 0;
  padding-left: 0.2rem;
}

::v-deep(.article-content ul ul) {
  list-style-type: circle;
}

::v-deep(.article-content ul ul ul) {
  list-style-type: square;
}

::v-deep(.article-content blockquote) {
  margin: 1.8rem 0;
  padding: 1.1rem 1.25rem;
  border-left: 4px solid var(--theme-primary);
  border-radius: 0 18px 18px 0;
  background: linear-gradient(135deg, rgba(235, 243, 251, 0.96), rgba(255, 255, 255, 0.95));
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
  background: rgba(235, 243, 251, 0.96);
  color: var(--theme-primary-deep);
  font-weight: 700;
}

::v-deep(.article-content tr:last-child td) {
  border-bottom: none;
}

::v-deep(.article-content code:not(pre code)) {
  padding: 0.18rem 0.45rem;
  margin: 0 0.15rem;
  border-radius: 8px;
  border: 1px solid rgba(116, 149, 195, 0.18);
  background: rgba(235, 243, 251, 0.96);
  color: var(--theme-primary-deep);
  font-size: 0.92em !important;
  font-family: 'JetBrains Mono', 'Cascadia Code', Consolas, Monaco, monospace;
}

::v-deep(pre) {
  margin: 1.7rem 0;
  position: relative;
  overflow-x: auto;
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
  background: rgba(116, 149, 195, 0.85);
  transform: translateY(-1px);
}

.toc-slide-enter-active,
.toc-slide-leave-active {
  transition: all 0.22s ease;
}

.toc-slide-enter-from,
.toc-slide-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

@media (max-width: 768px) {
  ::v-deep(.article-content) {
    font-size: 0.98rem;
    line-height: 1.86;
  }

  ::v-deep(.article-content h2) {
    margin-top: 2.3rem;
    font-size: clamp(1.45rem, 7vw, 2rem);
  }

  ::v-deep(.article-content h3) {
    margin-top: 1.8rem;
    font-size: clamp(1.15rem, 5.2vw, 1.45rem);
  }

  ::v-deep(.article-content h4) {
    margin-top: 1.4rem;
    font-size: 1.02rem;
  }

  ::v-deep(.article-content blockquote) {
    margin: 1.4rem 0;
    padding: 0.95rem 1rem;
  }

  ::v-deep(.article-content img) {
    border-radius: 16px;
  }

  ::v-deep(.article-content table) {
    display: block;
    overflow-x: auto;
    white-space: nowrap;
    border-radius: 14px;
  }

  ::v-deep(.article-content th),
  ::v-deep(.article-content td) {
    padding: 0.78rem 0.85rem;
  }

  ::v-deep(pre.code-block-shell) {
    border-radius: 16px;
  }

  ::v-deep(.code-block-toolbar) {
    padding: 0.5rem 0.75rem 0.45rem;
  }

  ::v-deep(pre code.hljs) {
    font-size: 0.86rem;
    line-height: 1.7;
    padding: 0.9rem 1rem 1rem;
  }

  ::v-deep(.code-copy-button) {
    padding: 0.24rem 0.52rem;
  }
}
</style>
