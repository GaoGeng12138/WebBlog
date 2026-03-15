<template>
  <aside class="w-full">
    <div class="lg:sticky lg:top-28">
      <section class="sidebar-panel">
        <div class="sidebar-heading">
          <span class="sidebar-heading__badge">每日一言</span>
          <span class="sidebar-heading__track"></span>
        </div>

        <div class="mt-5 rounded-[22px] border border-white/70 bg-white/90 p-4 shadow-[0_14px_30px_rgba(15,23,42,0.05)] backdrop-blur-xl">
          <p class="text-[10px] font-medium uppercase tracking-[0.24em] text-slate-400">Daily Note</p>
          <blockquote class="mt-3 break-words text-[0.86rem] font-medium leading-[1.9] tracking-[0.005em] text-slate-700">
            “{{ todayQuote.text }}”
          </blockquote>
          <div class="mt-3.5 flex flex-wrap items-center gap-2 text-[11px] text-slate-500">
            <span class="rounded-full bg-amber-50 px-2.5 py-1 font-medium text-amber-700">{{ todayQuote.tag }}</span>
            <span>{{ todayLabel }}</span>
          </div>
        </div>
      </section>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'

const now = new Date()

const quotePool = [
  { text: '慢一点也没关系，把想法记录下来，它就开始拥有形状。', tag: '记录' },
  { text: '真正有价值的输出，往往来自反复琢磨后的那一次说清楚。', tag: '表达' },
  { text: '比起立刻完美，更重要的是先留下第一版可继续生长的答案。', tag: '迭代' },
  { text: '技术不是终点，技术只是让灵感被更好看见的方式。', tag: '技术' },
  { text: '把复杂的问题拆开写清楚，焦虑就会慢慢变成路径。', tag: '思路' },
  { text: '持续积累的小笔记，常常会在未来的某一天突然连接成体系。', tag: '沉淀' }
]

const dayIndex = computed(() => {
  const year = now.getFullYear()
  const month = now.getMonth() + 1
  const date = now.getDate()
  return (year * 10000 + month * 100 + date) % quotePool.length
})

const todayQuote = computed(() => quotePool[dayIndex.value])
const todayLabel = computed(() =>
  new Intl.DateTimeFormat('zh-CN', {
    month: 'long',
    day: 'numeric'
  }).format(now)
)
</script>

<style scoped>
.sidebar-panel {
  padding-bottom: 1.5rem;
}

blockquote {
  overflow-wrap: anywhere;
  word-break: break-word;
}

.sidebar-heading {
  display: flex;
  align-items: center;
  gap: 0;
}

.sidebar-heading__badge {
  display: inline-flex;
  align-items: center;
  min-height: 2.5rem;
  padding: 0 1rem;
  background: linear-gradient(135deg, #f59e0b, #f97316);
  color: white;
  font-size: 0.84rem;
  font-weight: 500;
  letter-spacing: 0.06em;
  box-shadow: 0 14px 28px rgba(245, 158, 11, 0.16);
}

.sidebar-heading__track {
  flex: 1;
  height: 2px;
  background: linear-gradient(90deg, rgba(245, 158, 11, 0.9), rgba(245, 158, 11, 0.14));
}
</style>
