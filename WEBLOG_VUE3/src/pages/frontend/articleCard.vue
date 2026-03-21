<template>
  <router-link
    v-if="variant === 'list'"
    :to="articleLink"
    class="list-card group grid overflow-hidden rounded-[18px] border border-slate-200/80 bg-white/92 transition-all duration-300 hover:-translate-y-0.5 hover:border-amber-200 hover:shadow-[0_14px_26px_rgba(245,158,11,0.08)] sm:grid-cols-[156px_minmax(0,1fr)]"
    :class="{ 'article-card-active': isCurrentArticle }"
  >
    <div class="relative h-24 overflow-hidden bg-slate-100 sm:h-full sm:min-h-[92px]">
      <img
        v-if="article.cover"
        :src="coverImageUrl"
        :alt="articleAlt"
        :loading="imageLoadingAttrs.loading"
        :fetchpriority="imageLoadingAttrs.fetchpriority"
        :decoding="imageLoadingAttrs.decoding"
        class="h-full w-full bg-slate-950 object-contain transition-transform duration-500 ease-out group-hover:scale-[1.02]"
      />
      <div v-else class="card-fallback">
        <div class="card-fallback__shape card-fallback__shape--one"></div>
        <div class="card-fallback__shape card-fallback__shape--two"></div>
        <svg class="relative z-10 h-10 w-10 text-white/75" fill="currentColor" viewBox="0 0 24 24">
          <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14zm-5.04-6.71l-2.75 3.54-1.96-2.36L6.5 17h11l-3.54-4.71z"/>
        </svg>
      </div>

      <div class="absolute inset-0 bg-gradient-to-t from-slate-950/50 via-slate-900/5 to-transparent"></div>

    </div>

    <div class="flex min-w-0 flex-col justify-between p-3 sm:p-3.5">
      <div>
        <div v-if="displayCategory" class="mb-2">
          <span class="category-chip" :class="categoryToneClass">
            {{ displayCategory }}
          </span>
        </div>
        <h3 v-if="hasTitle" class="line-clamp-2 text-[0.98rem] font-bold leading-6 text-slate-900">
          {{ displayTitle }}
        </h3>
        <p class="line-clamp-3 text-[0.9rem] leading-[1.45rem] text-slate-600/95" :class="hasTitle ? 'mt-1.5' : 'mt-0'">
          {{ articleContentPreview }}
        </p>
      </div>

      <div class="mt-2.5 flex flex-wrap items-center gap-2 text-[12px] text-slate-500">
        <span v-if="article.author" class="inline-flex items-center gap-1.5">
          <svg class="h-3.5 w-3.5 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
          {{ article.author }}
        </span>
        <span class="text-slate-300">·</span>
        <span class="inline-flex items-center gap-1.5">
          <svg class="h-3.5 w-3.5 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          {{ formatDate(article.updateTime || article.createTime) }}
        </span>
        <span class="ml-auto text-[11px] font-medium text-amber-600 transition-colors duration-300 group-hover:text-amber-700">
          阅读全文
        </span>
      </div>
    </div>
  </router-link>

  <router-link
    v-else-if="featured"
    :to="articleLink"
    class="featured-card group grid overflow-hidden rounded-[32px] border border-white/70 bg-white/80 shadow-[0_24px_60px_rgba(15,23,42,0.08)] backdrop-blur-xl transition-all duration-500 hover:-translate-y-1 hover:shadow-[0_32px_80px_rgba(37,99,235,0.16)] lg:grid-cols-[1.05fr_0.95fr]"
  >
    <div class="relative min-h-[260px] overflow-hidden bg-slate-100">
      <img
        v-if="article.cover"
        :src="featuredCoverImageUrl"
        :alt="articleAlt"
        :loading="imageLoadingAttrs.loading"
        :fetchpriority="imageLoadingAttrs.fetchpriority"
        :decoding="imageLoadingAttrs.decoding"
        class="h-full w-full bg-slate-950 object-contain transition-transform duration-700 ease-out group-hover:scale-[1.02]"
      />
      <div v-else class="featured-fallback">
        <div class="featured-fallback__orb"></div>
        <svg class="h-14 w-14 text-white/70" fill="currentColor" viewBox="0 0 24 24">
          <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14zm-5.04-6.71l-2.75 3.54-1.96-2.36L6.5 17h11l-3.54-4.71z"/>
        </svg>
      </div>

      <div class="absolute inset-0 bg-gradient-to-tr from-slate-950/70 via-slate-900/20 to-transparent"></div>

      <div class="absolute left-5 top-5 flex flex-wrap gap-2">
        <span v-if="displayCategory" class="category-chip" :class="categoryToneClass">
          {{ displayCategory }}
        </span>
        <span class="rounded-full bg-white/20 px-3 py-1 text-xs font-medium text-white backdrop-blur">
          Featured Post
        </span>
      </div>

      <div class="absolute bottom-5 left-5 right-5 flex items-center justify-between text-xs text-white/80">
        <span>{{ formatDate(article.updateTime || article.createTime) }}</span>
        <span>{{ readTimeText }}</span>
      </div>
    </div>

    <div class="flex flex-col justify-between p-6 sm:p-8">
      <div>
        <p class="text-sm font-semibold uppercase tracking-[0.25em] text-slate-400">焦点文章</p>
        <p class="mt-4 line-clamp-5 text-[15px] leading-8 text-slate-600">
          {{ articleSummary }}
        </p>
      </div>

      <div class="mt-8 flex flex-wrap items-center gap-3 text-sm text-slate-500">
        <span v-if="article.author" class="inline-flex items-center gap-2 rounded-full bg-slate-100 px-3 py-2">
          <svg class="h-4 w-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
          {{ article.author }}
        </span>
        <span class="inline-flex items-center gap-2 rounded-full bg-slate-100 px-3 py-2">
          <svg class="h-4 w-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
          </svg>
          {{ Number(article.readNum || article.viewCount || 0) }} 浏览
        </span>
        <span class="inline-flex items-center gap-2 rounded-full bg-blue-600 px-4 py-2 font-semibold text-white shadow-lg shadow-blue-600/20">
          阅读全文
        </span>
      </div>
    </div>
  </router-link>

  <router-link
    v-else
    :to="articleLink"
    class="home-card group block overflow-hidden rounded-[16px] border border-white/80 bg-white shadow-[0_12px_26px_rgba(15,23,42,0.07)] transition-all duration-300 hover:-translate-y-1 hover:shadow-[0_18px_32px_rgba(15,23,42,0.12)]"
    :class="{ 'article-card-active': isCurrentArticle }"
  >
    <div class="relative h-36 overflow-hidden bg-slate-100 sm:h-38">
      <img
        v-if="article.cover"
        :src="coverImageUrl"
        :alt="articleAlt"
        :loading="imageLoadingAttrs.loading"
        :fetchpriority="imageLoadingAttrs.fetchpriority"
        :decoding="imageLoadingAttrs.decoding"
        class="h-full w-full bg-slate-950 object-contain transition-transform duration-500 ease-out group-hover:scale-[1.02]"
      />
      <div v-else class="card-fallback">
        <div class="card-fallback__shape card-fallback__shape--one"></div>
        <div class="card-fallback__shape card-fallback__shape--two"></div>
        <svg class="relative z-10 h-10 w-10 text-white/75" fill="currentColor" viewBox="0 0 24 24">
          <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14zm-5.04-6.71l-2.75 3.54-1.96-2.36L6.5 17h11l-3.54-4.71z"/>
        </svg>
      </div>

      <div class="absolute inset-0 bg-gradient-to-t from-slate-950/30 via-transparent to-transparent opacity-80"></div>
    </div>

    <div class="px-3.5 py-3">
      <h3 v-if="hasTitle" class="line-clamp-2 min-h-[2.7rem] text-[15px] font-bold leading-5 text-slate-900">
        {{ displayTitle }}
      </h3>
      <p
        class="line-clamp-2 text-[13px] leading-5 text-slate-600/95"
        :class="[
          hasTitle ? 'mt-1.5 min-h-[2.8rem]' : 'mt-0 min-h-[3.4rem]'
        ]"
      >
        {{ articleSummary }}
      </p>
      <div class="mt-1.5 flex flex-wrap items-center gap-2 text-[11px] text-slate-500">
        <span v-if="article.author" class="inline-flex items-center gap-1.5">
          <svg class="h-3 w-3 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
          {{ article.author }}
        </span>
        <span class="text-slate-300">·</span>
        <span class="inline-flex items-center gap-1.5">
          <svg class="h-3 w-3 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          {{ formatDate(article.updateTime || article.createTime) }}
        </span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
import { computed, toRefs } from 'vue'
import { useRoute } from 'vue-router'
import moment from 'moment'
import { getImageLoadingAttrs, getOptimizedImageUrl } from '@/utils/image'

const props = defineProps({
  article: {
    type: Object,
    required: true
  },
  categoryId: {
    type: [String, Number],
    default: ''
  },
  compact: {
    type: Boolean,
    default: false
  },
  variant: {
    type: String,
    default: 'grid'
  },
  featured: {
    type: Boolean,
    default: false
  },
  imageIndex: {
    type: Number,
    default: 99
  }
})

const { article } = toRefs(props)
const route = useRoute()

const articleId = computed(() => String(article.value.id || article.value._id || ''))
const articleLink = computed(() => {
  const path = `/article/${articleId.value}`
  if (!props.categoryId) {
    return { path }
  }

  return {
    path,
    query: {
      categoryId: String(props.categoryId)
    }
  }
})
const isCurrentArticle = computed(() => route.path === `/article/${articleId.value}`)

const displayCategory = computed(() => {
  const category = article.value?.category
  if (!category) return ''
  return typeof category === 'string' ? category : (category.name || '')
})

const displayTitle = computed(() => {
  const rawTitle = article.value?.title
  return typeof rawTitle === 'string' ? rawTitle.trim() : ''
})

const hasTitle = computed(() => !!displayTitle.value)

const articleSummary = computed(() => {
  return article.value.summary || article.value.description || trimContent(article.value.content)
})

const articleContentPreview = computed(() => {
  return trimContent(article.value.content, 120)
})

const articleAlt = computed(() => {
  return displayTitle.value || article.value.summary || '文章封面'
})

const imageLoadingAttrs = computed(() => getImageLoadingAttrs(props.imageIndex))

const coverImageUrl = computed(() => getOptimizedImageUrl(article.value?.cover, {
  width: props.variant === 'list' ? 360 : 420,
  height: props.variant === 'list' ? 220 : 260,
  fit: 'contain'
}))

const featuredCoverImageUrl = computed(() => getOptimizedImageUrl(article.value?.cover, {
  width: 960,
  height: 540,
  fit: 'contain'
}))

const readTimeText = computed(() => {
  const source = `${displayTitle.value || ''} ${article.value.summary || ''} ${trimContent(article.value.content || '')}`
  const plainText = source.replace(/<[^>]+>/g, '').replace(/\s+/g, ' ').trim()
  const words = plainText.length
  const minutes = Math.max(1, Math.round(words / 320))
  return `${minutes} 分钟阅读`
})

const categoryToneClass = computed(() => {
  const text = displayCategory.value || displayTitle.value || ''
  const tones = [
    'category-chip--blue',
    'category-chip--emerald',
    'category-chip--amber',
    'category-chip--violet'
  ]
  const hash = Array.from(text).reduce((total, char) => total + char.charCodeAt(0), 0)
  return tones[hash % tones.length]
})

function trimContent(content, limit = 110) {
  if (!content) return '这里记录了完整的思路、解决过程和最后沉淀下来的经验。'
  const plain = content.replace(/<[^>]+>/g, '').replace(/\s+/g, ' ').trim()
  return plain.slice(0, limit) + (plain.length > limit ? ' [...]' : '')
}

function formatDate(ts) {
  if (!ts) return ''
  return moment(ts).format('YYYY年MM月DD日')
}
</script>

<style scoped>
.home-card,
.featured-card,
.list-card {
  position: relative;
}

.article-card-active {
  border-color: rgba(245, 158, 11, 0.55) !important;
  box-shadow: 0 0 0 1px rgba(251, 191, 36, 0.2), 0 18px 36px rgba(245, 158, 11, 0.14) !important;
}

.featured-fallback,
.card-fallback {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  overflow: hidden;
  background: linear-gradient(135deg, #0f172a 0%, #1d4ed8 55%, #38bdf8 100%);
}

.featured-fallback__orb {
  position: absolute;
  height: 12rem;
  width: 12rem;
  border-radius: 9999px;
  background: rgba(255, 255, 255, 0.14);
  filter: blur(18px);
}

.card-fallback__shape {
  position: absolute;
  border-radius: 9999px;
  background: rgba(255, 255, 255, 0.12);
  filter: blur(10px);
}

.card-fallback__shape--one {
  left: 1rem;
  top: 1rem;
  height: 4rem;
  width: 4rem;
}

.card-fallback__shape--two {
  right: 1rem;
  bottom: 1rem;
  height: 6rem;
  width: 6rem;
}

.category-chip {
  display: inline-flex;
  align-items: center;
  border-radius: 9999px;
  padding: 0.45rem 0.9rem;
  font-size: 0.75rem;
  font-weight: 500;
  backdrop-filter: blur(12px);
}

.category-chip--blue {
  background: rgba(239, 246, 255, 0.88);
  color: #2563eb;
}

.category-chip--emerald {
  background: rgba(236, 253, 245, 0.88);
  color: #059669;
}

.category-chip--amber {
  background: rgba(255, 251, 235, 0.9);
  color: #d97706;
}

.category-chip--violet {
  background: rgba(245, 243, 255, 0.9);
  color: #7c3aed;
}

.line-clamp-2,
.line-clamp-3,
.line-clamp-4 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-2 {
  -webkit-line-clamp: 2;
}

.line-clamp-3 {
  -webkit-line-clamp: 3;
}

.line-clamp-4 {
  -webkit-line-clamp: 4;
}

</style>
