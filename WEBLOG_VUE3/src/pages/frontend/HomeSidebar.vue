<template>
  <aside class="w-full">
    <div class="lg:sticky lg:top-28">
      <section class="sidebar-panel">
        <div class="sidebar-heading sidebar-heading--highlight">
          <span class="sidebar-heading__badge">热门文章</span>
          <span class="sidebar-heading__track"></span>
        </div>

        <div class="mt-5 space-y-4">
          <router-link
            v-for="item in hotArticles"
            :key="item.id"
            :to="`/article/${item.id}`"
            class="sidebar-hot-card group"
          >
            <div class="sidebar-hot-cover">
              <img
                v-if="item.cover"
                :src="item.cover"
                :alt="item.title"
                class="h-full w-full bg-slate-950 object-contain transition-transform duration-500 group-hover:scale-[1.02]"
              />
              <div v-else class="sidebar-hot-cover__fallback">
                <span class="sidebar-hot-cover__glow"></span>
                <svg class="relative z-10 h-7 w-7 text-white/80" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14zm-5.04-6.71l-2.75 3.54-1.96-2.36L6.5 17h11l-3.54-4.71z"/>
                </svg>
              </div>
            </div>

            <div class="min-w-0 flex-1 py-0.5">
              <p class="line-clamp-3 text-[0.94rem] font-medium leading-6 tracking-[0.01em] text-slate-800 transition-colors duration-300 group-hover:text-[var(--theme-primary)]">
                {{ item.title }}
              </p>
              <div class="mt-2.5 flex items-center gap-3 text-[12px] text-slate-400">
                <span>{{ formatDate(item.createTime) }}</span>
                <span class="inline-flex items-center gap-1.5">
                  <svg class="h-3.5 w-3.5 text-slate-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                  {{ item.readNum }}
                </span>
              </div>
            </div>
          </router-link>

          <div v-if="hotArticles.length === 0" class="rounded-2xl border border-dashed border-[rgba(129,158,196,0.2)] bg-[rgba(244,248,252,0.76)] px-4 py-6 text-center text-sm text-slate-400">
            暂无热门文章
          </div>
        </div>
      </section>
    </div>
  </aside>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import moment from 'moment'
import { getArticlePageList } from '@/api/frontend/article'

const hotArticles = ref([])

onMounted(() => {
  loadHotArticles()
})

async function loadHotArticles() {
  try {
    const res = await getArticlePageList({ current: 1, size: 24, name: '', userId: '' })
    if (res && res.success) {
      const list = Array.isArray(res.data) ? res.data : (res.data?.records || [])
      hotArticles.value = list
        .map((item) => ({
          id: item.id || item._id,
          cover: item.cover,
          title: item.title,
          readNum: Number(item.readNum || item.viewCount || 0),
          createTime: item.createTime
        }))
        .sort((a, b) => {
          if (b.readNum !== a.readNum) return b.readNum - a.readNum
          return new Date(b.createTime || 0).getTime() - new Date(a.createTime || 0).getTime()
        })
        .slice(0, 6)
    }
  } catch (error) {
    console.error('加载热门文章失败:', error)
  }
}

function formatDate(ts) {
  if (!ts) return ''
  return moment(ts).format('MM/DD')
}
</script>

<style scoped>
.sidebar-panel {
  padding-bottom: 1.5rem;
}

.sidebar-heading {
  display: flex;
  align-items: center;
  gap: 0.7rem;
  font-size: 0.84rem;
  font-weight: 500;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: #0f172a;
}

.sidebar-heading--highlight {
  gap: 0;
}

.sidebar-heading__badge {
  display: inline-flex;
  align-items: center;
  min-height: 2.5rem;
  padding: 0 1rem;
  background: linear-gradient(135deg, #5d7fac, #7f9fcc);
  color: white;
  letter-spacing: 0.06em;
  box-shadow: 0 14px 28px rgba(116, 149, 195, 0.18);
}

.sidebar-heading__track {
  flex: 1;
  height: 2px;
  background: linear-gradient(90deg, rgba(116, 149, 195, 0.85), rgba(116, 149, 195, 0.12));
}

.sidebar-hot-card {
  display: flex;
  gap: 0.9rem;
  align-items: stretch;
  border-radius: 1rem;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(129, 158, 196, 0.16);
  padding: 0.62rem;
  box-shadow: 0 16px 34px rgba(120, 146, 184, 0.08);
  transition: all 0.28s ease;
}

.sidebar-hot-card:hover {
  transform: translateY(-2px);
  border-color: rgba(116, 149, 195, 0.22);
  box-shadow: 0 22px 40px rgba(120, 146, 184, 0.14);
}

.sidebar-hot-cover {
  width: 8.8rem;
  min-width: 8.8rem;
  height: 4.9rem;
  overflow: hidden;
  border-radius: 0.9rem;
  background: #e2e8f0;
}

.sidebar-hot-cover__fallback {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: linear-gradient(135deg, #6889b6, #8facd6);
}

.sidebar-hot-cover__glow {
  position: absolute;
  width: 4rem;
  height: 4rem;
  border-radius: 9999px;
  background: rgba(255, 255, 255, 0.14);
  filter: blur(12px);
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

</style>
