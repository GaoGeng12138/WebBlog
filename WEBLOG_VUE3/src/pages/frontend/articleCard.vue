<template>
  <article class="bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow duration-300 overflow-hidden border border-gray-100">
    <div class="flex flex-col">
      <!-- Cover Image -->
      <div v-if="article.cover" class="h-40 w-full overflow-hidden bg-gray-200">
        <img :src="article.cover" class="w-full h-full object-cover transition-transform duration-500 hover:scale-105" />
      </div>
      
      <!-- Content -->
      <div class="p-4 flex flex-col flex-grow">
        <!-- Tags -->
        <div class="flex flex-wrap items-center gap-2 mb-2">
          <el-tag v-for="t in article.tags" :key="t.id || t.name" type="success" size="small">{{ t.name || t }}</el-tag>
        </div>
        
        <!-- Title -->
        <h2 class="text-lg font-bold text-gray-900 mb-2 line-clamp-2">{{ article.title }}</h2>
        
        <!-- Summary -->
        <p class="text-sm text-gray-600 mb-3 line-clamp-2">{{ article.summary || article.description || (article.content || '').slice(0, 100) + '...' }}</p>
        
        <!-- Footer -->
        <div class="flex items-center justify-between pt-3 border-t border-gray-100 text-xs text-gray-500">
          <div class="flex items-center gap-2">
            <span>{{ formatDate(article.createTime) }}</span>
            <span v-if="displayCategory" class="px-2 py-0.5 bg-green-50 text-green-700 rounded">{{ displayCategory }}</span>
          </div>
          <router-link :to="`/article/${article.id || article._id || ''}`" class="text-blue-600 hover:text-blue-800 font-medium">阅读更多 →</router-link>
        </div>
      </div>
    </div>
  </article>
</template>

<script setup>
import { toRefs, computed } from 'vue'
import moment from 'moment'

const props = defineProps({
  article: {
    type: Object,
    required: true
  }
})

const { article } = toRefs(props)

const displayCategory = computed(() => {
  const c = article.value && article.value.category
  if (!c) return ''
  return typeof c === 'string' ? c : (c.name || '')
})

function formatDate(ts) {
  if (!ts) return ''
  return moment(ts).format('YYYY-MM-DD')
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>