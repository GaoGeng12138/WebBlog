<template>
  <nav class="w-full" :class="direction === 'vertical' ? '' : 'mt-0'">
    <div v-if="direction === 'vertical'" class="grid grid-cols-2 gap-3">
      <button
        v-for="item in navItems"
        :key="item.key"
        type="button"
        class="px-4 py-3 rounded-xl text-sm font-normal transition-all duration-300 inline-flex items-center justify-center border"
        :class="isActive(item.key)
          ? 'text-[var(--cosmic-blue-deep)] bg-[rgba(148,176,231,0.18)] shadow-[0_10px_22px_rgba(110,146,216,0.12)] border-[rgba(148,176,231,0.26)]'
          : 'text-[var(--cosmic-muted)] bg-[rgba(255,255,255,0.54)] border-[rgba(149,171,210,0.14)] hover:text-[var(--cosmic-blue-deep)] hover:bg-[rgba(255,255,255,0.84)] hover:border-[rgba(148,176,231,0.2)] hover:shadow-[0_10px_22px_rgba(108,137,184,0.08)]'"
        @click="handleNavigate(item.path)"
      >
        <span class="truncate">{{ item.label }}</span>
      </button>
    </div>

    <div v-else class="navigation-strip">
      <div class="navigation-scroll">
        <button
          v-for="item in navItems"
          :key="item.key"
          type="button"
          class="nav-link group relative overflow-hidden"
          :class="isActive(item.key) ? 'nav-link-active' : 'nav-link-idle'"
          @click="handleNavigate(item.path)"
        >
          <div class="relative z-10 flex items-center transition-transform duration-300 group-hover:-translate-y-0.5">
            <span class="truncate whitespace-nowrap">{{ item.label }}</span>
          </div>
          
          <!-- Bottom Indicator Line -->
          <div class="absolute bottom-0 left-0 w-full h-[3px] rounded-t-full bg-gradient-to-r from-[var(--cosmic-blue-soft)] via-[var(--cosmic-blue)] to-[var(--cosmic-blue-deep)] transition-all duration-300"
               :class="isActive(item.key) ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-2 group-hover:opacity-100 group-hover:translate-y-0'">
          </div>
          
          <!-- Hover Blur Background -->
          <div v-if="isActive(item.key)" class="absolute bottom-0 left-1/2 -translate-x-1/2 w-3/4 h-2 bg-[rgba(148,176,231,0.2)] blur-md pointer-events-none rounded-full"></div>
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAllCategoryList } from '@/api/frontend/category'

const props = defineProps({
  direction: {
    type: String,
    default: 'horizontal'
  }
})

const emit = defineEmits(['navigate'])

const route = useRoute()
const router = useRouter()

const defaultNavs = [
  { key: 'home', label: '首页', path: '/' }
]

const navItems = ref([...defaultNavs])

onMounted(async () => {
  try {
    const res = await getAllCategoryList()
    if (res && res.success) {
      const categoryNavs = (res.data || [])
        .filter(category => category.showOnFront !== false)
        .map(category => ({
          key: `category-${category.id}`,
          label: category.name,
          path: `/category/${category.id}`
        }))
      navItems.value = [...defaultNavs, ...categoryNavs]
    }
  } catch (error) {
    console.error('Failed to load categories for navigation:', error)
  }
})

const currentKey = computed(() => {
  const path = route.path || ''
  if (path === '/') return 'home'

  if (path.startsWith('/category/')) {
    const parts = path.split('/')
    if (parts.length >= 3) {
      return `category-${parts[2]}`
    }
  }
  return ''
})

function isActive(key) {
  return currentKey.value === key
}

function handleNavigate(path) {
  router.push(path)
  emit('navigate', path)
}
</script>

<style scoped>
.navigation-strip {
  width: 100%;
}

.navigation-scroll {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: none;
}

.navigation-scroll::-webkit-scrollbar {
  display: none; /* Hide scrollbar for Chrome, Safari and Opera */
}

.nav-link {
  flex: 0 0 auto;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 0.75rem 0.95rem;
  font-size: 0.94rem;
  font-weight: 400;
  letter-spacing: 0.01em;
  border-radius: 0.5rem; 
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: transparent;
  outline: none;
}

.nav-link-idle {
  color: var(--cosmic-muted);
}

.nav-link-idle:hover {
  color: var(--cosmic-text-strong);
  background-color: rgba(255, 255, 255, 0.78);
}

.nav-link-active {
  color: var(--cosmic-blue-deep);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(240, 246, 253, 0.9));
  text-shadow: none;
}
</style>
