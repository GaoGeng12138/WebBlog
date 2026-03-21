<template>
  <div class="frontend-shell home-page min-h-screen flex flex-col">
    <AppHeader :keyword="keyword" @update:keyword="keyword = $event" @search="handleSearch" />

    <main class="relative flex-1 mx-auto w-full max-w-[1700px] px-4 py-6 sm:px-6 lg:px-8 lg:py-8">
      <section class="hero-panel relative overflow-hidden rounded-[20px] border px-4 py-4 shadow-[0_18px_34px_rgba(120,146,186,0.12)] sm:px-5 lg:px-6 lg:py-4.5">
        <div class="hero-glow hero-glow-left"></div>
        <div class="hero-glow hero-glow-right"></div>

        <div class="relative z-10 grid gap-4 lg:grid-cols-[minmax(0,1fr)_220px] lg:items-center">
          <div class="max-w-[560px]">
            <p class="inline-flex items-center gap-2 rounded-full border border-[rgba(149,171,210,0.18)] bg-[rgba(255,255,255,0.62)] px-3 py-1 text-[10px] font-medium uppercase tracking-[0.24em] text-[var(--cosmic-muted)] backdrop-blur">
              <span class="h-2 w-2 rounded-full bg-[var(--cosmic-blue)]"></span>
              ThoughtFlow
            </p>

            <h1 class="mt-2 text-[2rem] font-semibold tracking-[-0.03em] text-[var(--cosmic-text-strong)] sm:text-[2.25rem] lg:text-[2.4rem]">
              记录
              <span class="hero-text-gradient">思考的流动</span>
            </h1>

            <p class="mt-1.5 max-w-md text-[13px] leading-6 text-[var(--cosmic-text-light-muted)] sm:text-[14px]">
              {{ heroDescription }}
            </p>

            <div class="mt-3.5 max-w-[540px]">
              <label class="hero-search relative flex items-center p-1.5 rounded-full border border-[rgba(149,171,210,0.16)] bg-[rgba(255,255,255,0.72)] shadow-[0_12px_28px_rgba(120,146,186,0.1)] backdrop-blur-xl transition-all duration-300 focus-within:-translate-y-0.5 focus-within:bg-[rgba(255,255,255,0.95)] focus-within:shadow-[0_16px_30px_rgba(110,146,216,0.12)] focus-within:border-[rgba(148,176,231,0.24)]">
                <span class="pl-4 pr-3 flex items-center justify-center text-[var(--cosmic-muted)] group-focus-within:text-[var(--cosmic-blue)] transition-colors">
                  <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-4.35-4.35M10.5 18a7.5 7.5 0 100-15 7.5 7.5 0 000 15z" />
                  </svg>
                </span>
                <input
                  v-model="keyword"
                  type="text"
                  class="min-w-0 flex-1 bg-transparent px-1 py-2 text-[14px] text-[var(--cosmic-text-light)] outline-none border-none ring-0 placeholder:text-[var(--cosmic-muted)] focus:outline-none focus:ring-0"
                  placeholder="搜索文章或你想回顾的主题"
                  @keyup.enter="handleSearch"
                />
                <button
                  type="button"
                  class="hero-search-button ml-2 rounded-full bg-[linear-gradient(135deg,var(--cosmic-blue),var(--cosmic-blue-deep))] px-6 py-2.5 text-[13px] font-medium text-white transition-all duration-300 hover:shadow-lg hover:shadow-[rgba(110,146,216,0.22)] active:scale-95"
                  @click="handleSearch"
                >
                  开始搜索
                </button>
              </label>
            </div>
          </div>

          <div class="hero-side grid gap-2.5 sm:grid-cols-2 lg:grid-cols-1">
            <div
              v-for="item in heroFacts"
              :key="item.label"
              class="hero-fact-card rounded-[18px] border border-[rgba(149,171,210,0.16)] bg-[rgba(255,255,255,0.64)] p-3 backdrop-blur-xl"
            >
              <div class="flex items-start justify-between gap-3">
                <div>
                  <p class="text-[11px] font-medium tracking-[0.14em] text-[var(--cosmic-muted)]">{{ item.label }}</p>
                  <p class="mt-1 text-[1.42rem] font-semibold tracking-[-0.03em] text-[var(--cosmic-text-light)]">{{ item.value }}</p>
                </div>
                <span class="hero-fact-card__meta">{{ item.meta }}</span>
              </div>
              <div class="hero-fact-card__footer">
                <div class="hero-fact-card__bars" aria-hidden="true">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
                <span class="hero-fact-card__caption">{{ item.caption }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <div class="mt-10 flex flex-col gap-10 2xl:flex-row 2xl:items-start 2xl:gap-8">
        <aside class="hidden 2xl:block 2xl:w-[230px] 2xl:shrink-0">
          <DailyNoteSidebar />
        </aside>

        <section class="min-w-0 flex-1">
          <div class="section-heading-shell mb-6 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
            <div class="min-w-0">
              <p class="text-[11px] font-medium uppercase tracking-[0.2em] text-[var(--cosmic-muted)]">
                {{ keyword ? 'Search Result' : 'Latest Writing' }}
              </p>
              <div class="mt-2 flex flex-wrap items-center gap-3">
                <h2 class="text-[2rem] font-semibold tracking-[-0.03em] text-[var(--cosmic-text-strong)]">
                  {{ sectionTitle }}
                </h2>
                <span class="section-status-pill">
                  <span class="section-status-pill__dot"></span>
                  {{ sectionMeta }}
                </span>
              </div>
              <div class="section-heading__line" aria-hidden="true"></div>
            </div>

            <div class="section-sort-panel flex flex-wrap items-center gap-2 text-[13px] text-[var(--cosmic-muted)]">
              <span class="font-medium text-[var(--cosmic-text-light)]">排序</span>
              <button
                v-for="item in sortOptions"
                :key="item.key"
                type="button"
                class="rounded-full px-4 py-2 text-[12px] font-medium transition-all duration-300"
                :class="sortKey === item.key
                  ? 'bg-[linear-gradient(135deg,var(--cosmic-blue),var(--cosmic-blue-deep))] text-white shadow-lg shadow-[rgba(110,146,216,0.18)]'
                  : 'bg-[rgba(255,255,255,0.72)] text-[var(--cosmic-muted)] ring-1 ring-[rgba(149,171,210,0.14)] hover:bg-[rgba(255,255,255,0.96)] hover:text-[var(--cosmic-blue-deep)]'"
                @click="sortKey = item.key"
              >
                {{ item.label }}
              </button>
            </div>
          </div>

          <transition-group
            name="fade-up"
            tag="div"
            class="space-y-6"
          >
            <div
              key="grid"
              class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 2xl:grid-cols-4"
              v-loading="loading"
            >
              <div
                v-for="(article, index) in gridArticles"
                :key="article.id || article._id"
                class="reveal-item"
                :style="{ animationDelay: `${Math.min(index * 80, 480)}ms` }"
              >
                <ArticleCard :article="article" :compact="true" :image-index="index" />
              </div>
            </div>
          </transition-group>

          <div
            v-if="!loading && sortedArticles.length === 0"
            class="mt-6 flex flex-col items-center justify-center rounded-[32px] border border-[rgba(149,171,210,0.16)] bg-[rgba(255,255,255,0.72)] px-4 py-20 text-center shadow-[0_20px_40px_rgba(120,146,186,0.1)] backdrop-blur-md"
          >
            <div class="mb-6 flex h-28 w-28 items-center justify-center rounded-full bg-[rgba(148,176,231,0.12)] border border-[rgba(148,176,231,0.16)]">
              <svg class="h-14 w-14 text-[var(--cosmic-blue-soft)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 002-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
            </div>
            <p class="text-lg font-semibold text-[var(--cosmic-text-strong)]">暂无相关文章</p>
            <p class="mt-2 max-w-sm text-sm leading-7 text-[var(--cosmic-text-light-muted)]">
              可以换个关键词试试，或者回到首页看看最近更新的内容。
            </p>
          </div>

          <div v-if="total > 0" class="mt-12 flex justify-center">
            <Pagination
              :total="total"
              :current-page="searchParams.current"
              :page-size="searchParams.size"
              :pager-only="true"
              @page-change="handlePageChange"
              class="scale-105"
            />
          </div>
        </section>

        <aside class="w-full shrink-0 lg:w-[320px] 2xl:w-[320px]">
          <HomeSidebar />
        </aside>
      </div>
    </main>

    <AppFooter />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useSiteConfigStore } from '@/stores/siteConfig'
import AppHeader from '@/components/frontend/AppHeader.vue'
import AppFooter from '@/components/frontend/AppFooter.vue'
import Pagination from '@/components/frontend/Pagination.vue'
import HomeSidebar from '@/pages/frontend/HomeSidebar.vue'
import DailyNoteSidebar from '@/pages/frontend/DailyNoteSidebar.vue'
import ArticleCard from '@/pages/frontend/articleCard.vue'
import { getArticlePageList } from '@/api/frontend/article'

const route = useRoute()
const userStore = useUserStore()
const siteConfigStore = useSiteConfigStore()

const keyword = ref('')
const articles = ref([])
const total = ref(0)
const loading = ref(false)
const sortKey = ref('update')

const sortOptions = [
  { key: 'update', label: '最新更新' },
  { key: 'views', label: '最高浏览' },
  { key: 'likes', label: '最多点赞' },
  { key: 'comments', label: '最多评论' }
]

const searchParams = ref({
  current: 1,
  size: 12,
  name: ''
})

const sortedArticles = computed(() => {
  const list = [...articles.value]
  const valueOfTime = (value) => (value ? new Date(value).getTime() : 0)
  const valueOfNumber = (value) => Number(value || 0)

  return list.sort((a, b) => {
    switch (sortKey.value) {
      case 'views':
        return valueOfNumber(b.readNum || b.viewCount) - valueOfNumber(a.readNum || a.viewCount)
      case 'likes':
        return valueOfNumber(b.likeCount) - valueOfNumber(a.likeCount)
      case 'comments':
        return valueOfNumber(b.commentCount) - valueOfNumber(a.commentCount)
      case 'update':
      default:
        return valueOfTime(b.updateTime || b.createTime) - valueOfTime(a.updateTime || a.createTime)
    }
  })
})

const gridArticles = computed(() => sortedArticles.value)

const siteTitle = computed(() => siteConfigStore.siteInfo.title || 'Blog')
const heroDescription = computed(() => {
  return siteConfigStore.siteInfo.description || '技术文章、开发札记和项目经验都集中在这里，用更清晰的方式把灵感、问题和解决过程沉淀下来。'
})

const latestDateText = computed(() => {
  const latest = sortedArticles.value[0]
  const value = latest?.updateTime || latest?.createTime
  return value ? formatDate(value) : '--'
})

const heroFacts = computed(() => ([
  {
    label: '本页内容',
    value: `${total.value || sortedArticles.value.length}`,
    meta: 'ART',
    caption: keyword.value ? '筛选结果' : '内容总览'
  },
  {
    label: '最新更新',
    value: latestDateText.value,
    meta: 'SYNC',
    caption: sortKey.value === 'update' ? '实时排序' : '时间锚点'
  }
]))

const sectionTitle = computed(() => {
  if (keyword.value) {
    return `“${keyword.value}” 的搜索结果`
  }

  switch (sortKey.value) {
    case 'views':
      return '热门文章'
    case 'likes':
      return '点赞精选'
    case 'comments':
      return '讨论最多'
    case 'update':
    default:
      return '最新发布'
  }
})

const sectionMeta = computed(() => {
  if (keyword.value) {
    return `${total.value || sortedArticles.value.length} 篇结果`
  }

  switch (sortKey.value) {
    case 'views':
      return '按热度浏览'
    case 'likes':
      return '按点赞浏览'
    case 'comments':
      return '按讨论浏览'
    case 'update':
    default:
      return `更新于 ${latestDateText.value}`
  }
})

function syncPageSizeFromConfig() {
  searchParams.value.size = siteConfigStore.siteInfo.frontendArticlePageSize || 12
}

async function loadArticles() {
  loading.value = true
  try {
    const res = await getArticlePageList(searchParams.value)
    if (res && res.success) {
      if (res.data && res.data.records) {
        articles.value = res.data.records
        total.value = res.data.total
      } else {
        articles.value = Array.isArray(res.data) ? res.data : []
        total.value = res.total || articles.value.length
      }
    }
  } catch (error) {
    console.error('加载文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

function formatDate(value) {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '--'
  return `${date.getMonth() + 1}/${date.getDate()}`
}

function handleSearch() {
  searchParams.value.name = keyword.value.trim()
  searchParams.value.current = 1
  loadArticles()
}

function handlePageChange(page) {
  searchParams.value.current = page
  loadArticles()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(async () => {
  userStore.setFrontendUserInfo()
  await siteConfigStore.fetchSiteInfo()
  syncPageSizeFromConfig()
  const queryKeyword = route.query.keyword
  if (queryKeyword) {
    keyword.value = queryKeyword
    searchParams.value.name = queryKeyword
  }
  loadArticles()
})

watch(() => route.query.keyword, (newVal) => {
  keyword.value = newVal || ''
  searchParams.value.name = keyword.value
  searchParams.value.current = 1
  loadArticles()
})

watch(() => siteConfigStore.siteInfo.frontendArticlePageSize, (newVal) => {
  if (newVal && searchParams.value.size !== newVal) {
    searchParams.value.size = newVal
    searchParams.value.current = 1
    loadArticles()
  }
})
</script>

<style scoped>
.home-page {
  position: relative;
}

.hero-panel {
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.76), rgba(244, 249, 255, 0.72)),
    radial-gradient(circle at top left, rgba(148, 176, 231, 0.18), transparent 28%),
    radial-gradient(circle at bottom right, rgba(242, 236, 226, 0.22), transparent 24%);
  border-color: rgba(149, 171, 210, 0.18);
}

.hero-glow {
  position: absolute;
  border-radius: 9999px;
  filter: blur(50px);
  opacity: 0.5;
}

.hero-glow-left {
  left: -4rem;
  top: -3rem;
  height: 12rem;
  width: 12rem;
  background: rgba(148, 176, 231, 0.22);
}

.hero-glow-right {
  right: -5rem;
  bottom: -4rem;
  height: 15rem;
  width: 15rem;
  background: rgba(220, 230, 244, 0.24);
}

.hero-text-gradient {
  background: linear-gradient(135deg, #27425f 0%, #6e92d8 48%, #93b2e6 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.hero-fact-card {
  position: relative;
  overflow: hidden;
  box-shadow: 0 12px 28px rgba(120, 146, 186, 0.08);
}

.hero-fact-card::before {
  content: "";
  position: absolute;
  inset: 0;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.24), transparent 44%),
    linear-gradient(90deg, rgba(148, 176, 231, 0.08), transparent 50%);
  pointer-events: none;
}

.hero-fact-card::after {
  content: "";
  position: absolute;
  inset: auto -12px -18px auto;
  width: 72px;
  height: 72px;
  border-radius: 999px;
  background: radial-gradient(circle, rgba(148, 176, 231, 0.2), transparent 72%);
}

.hero-fact-card__meta {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 40px;
  height: 24px;
  border-radius: 999px;
  padding: 0 0.55rem;
  background: rgba(255, 255, 255, 0.7);
  box-shadow: inset 0 0 0 1px rgba(149, 171, 210, 0.14);
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.18em;
  color: #7a8da9;
}

.hero-fact-card__footer {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  margin-top: 0.9rem;
  padding-top: 0.72rem;
  border-top: 1px solid rgba(149, 171, 210, 0.12);
}

.hero-fact-card__bars {
  display: inline-flex;
  align-items: flex-end;
  gap: 0.28rem;
}

.hero-fact-card__bars span {
  display: block;
  width: 16px;
  border-radius: 999px;
  background: linear-gradient(180deg, rgba(110, 146, 216, 0.92), rgba(255, 155, 227, 0.68));
  box-shadow: 0 6px 14px rgba(110, 146, 216, 0.16);
}

.hero-fact-card__bars span:nth-child(1) {
  height: 6px;
}

.hero-fact-card__bars span:nth-child(2) {
  height: 10px;
}

.hero-fact-card__bars span:nth-child(3) {
  height: 7px;
}

.hero-fact-card__caption {
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.08em;
  color: var(--cosmic-text-light-muted);
}

.section-heading-shell {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(149, 171, 210, 0.14);
  border-radius: 24px;
  padding: 1rem 1.1rem;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.74), rgba(246, 249, 255, 0.7)),
    radial-gradient(circle at top right, rgba(148, 176, 231, 0.12), transparent 26%);
  box-shadow: 0 18px 34px rgba(120, 146, 186, 0.08);
}

.section-heading-shell::after {
  content: "";
  position: absolute;
  inset: auto 1.1rem 0 1.1rem;
  height: 1px;
  background: linear-gradient(90deg, rgba(148, 176, 231, 0.42), rgba(148, 176, 231, 0));
}

.section-heading__line {
  width: min(220px, 100%);
  height: 8px;
  margin-top: 0.85rem;
  border-radius: 999px;
  background:
    linear-gradient(90deg, rgba(110, 146, 216, 0.18), rgba(255, 155, 227, 0.14), transparent 78%);
}

.section-status-pill {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  border-radius: 999px;
  border: 1px solid rgba(149, 171, 210, 0.16);
  background: rgba(255, 255, 255, 0.72);
  padding: 0.42rem 0.85rem;
  font-size: 12px;
  font-weight: 500;
  color: var(--cosmic-text-light-muted);
  box-shadow: 0 10px 22px rgba(120, 146, 186, 0.08);
}

.section-status-pill__dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--cosmic-blue), #ff9be3);
  box-shadow: 0 0 0 4px rgba(148, 176, 231, 0.12);
}

.section-sort-panel {
  position: relative;
  z-index: 1;
  padding: 0.55rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.54);
  box-shadow: inset 0 0 0 1px rgba(149, 171, 210, 0.12);
  backdrop-filter: blur(10px);
}

.reveal-item {
  animation: revealUp 0.7s ease both;
}

.fade-up-enter-active,
.fade-up-leave-active {
  transition: all 0.35s ease;
}

.fade-up-enter-from,
.fade-up-leave-to {
  opacity: 0;
  transform: translateY(12px);
}

@keyframes revealUp {
  from {
    opacity: 0;
    transform: translateY(22px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 640px) {
  .hero-search-button {
    padding-left: 1rem;
    padding-right: 1rem;
  }

  .section-heading-shell {
    padding: 0.9rem;
    border-radius: 20px;
  }

  .section-sort-panel {
    width: 100%;
    border-radius: 18px;
  }
}

</style>
