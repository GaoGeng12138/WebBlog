<template>
  <nav class="mt-6 flex items-center justify-center">
    <ul class="flex flex-wrap gap-8 text-sm font-medium">
      <li>
        <a 
          @click.prevent="$router.push('/')" 
          class="pb-2 px-3 py-1.5 rounded-lg cursor-pointer transition-all duration-300 inline-block"
          :class="isActive('/') 
            ? 'text-white bg-gradient-to-r from-blue-600 to-blue-500 shadow-lg shadow-blue-500/50 font-bold transform scale-105' 
            : 'text-gray-600 hover:text-blue-600 hover:bg-blue-50'"
        >
          <span class="flex items-center gap-1.5">
            <svg v-if="isActive('/')" class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z"></path>
            </svg>
            首页
          </span>
        </a>
      </li>
      <li>
        <a 
          @click.prevent="$router.push('/category')" 
          class="pb-2 px-3 py-1.5 rounded-lg cursor-pointer transition-all duration-300 inline-block"
          :class="isActive('/category') 
            ? 'text-white bg-gradient-to-r from-blue-600 to-blue-500 shadow-lg shadow-blue-500/50 font-bold transform scale-105' 
            : 'text-gray-600 hover:text-blue-600 hover:bg-blue-50'"
        >
          <span class="flex items-center gap-1.5">
            <svg v-if="isActive('/category')" class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path d="M7 3a1 1 0 000 2h6a1 1 0 100-2H7zM4 7a1 1 0 011-1h10a1 1 0 110 2H5a1 1 0 01-1-1zM2 11a2 2 0 012-2h12a2 2 0 012 2v4a2 2 0 01-2 2H4a2 2 0 01-2-2v-4z"></path>
            </svg>
            分类
          </span>
        </a>
      </li>
      <li>
        <a 
          @click.prevent="$router.push('/tag')" 
          class="pb-2 px-3 py-1.5 rounded-lg cursor-pointer transition-all duration-300 inline-block"
          :class="isActive('/tag') 
            ? 'text-white bg-gradient-to-r from-blue-600 to-blue-500 shadow-lg shadow-blue-500/50 font-bold transform scale-105' 
            : 'text-gray-600 hover:text-blue-600 hover:bg-blue-50'"
        >
          <span class="flex items-center gap-1.5">
            <svg v-if="isActive('/tag')" class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M17.707 9.293a1 1 0 010 1.414l-7 7a1 1 0 01-1.414 0l-7-7A.997.997 0 012 10V5a3 3 0 013-3h5c.256 0 .512.098.707.293l7 7zM5 6a1 1 0 100-2 1 1 0 000 2z" clip-rule="evenodd"></path>
            </svg>
            标签
          </span>
        </a>
      </li>
      <li>
        <a 
          @click.prevent="$router.push('/archive')" 
          class="pb-2 px-3 py-1.5 rounded-lg cursor-pointer transition-all duration-300 inline-block"
          :class="isActive('/archive') 
            ? 'text-white bg-gradient-to-r from-blue-600 to-blue-500 shadow-lg shadow-blue-500/50 font-bold transform scale-105' 
            : 'text-gray-600 hover:text-blue-600 hover:bg-blue-50'"
        >
          <span class="flex items-center gap-1.5">
            <svg v-if="isActive('/archive')" class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path d="M4 3a2 2 0 100 4h12a2 2 0 100-4H4z"></path>
              <path fill-rule="evenodd" d="M3 8h14v7a2 2 0 01-2 2H5a2 2 0 01-2-2V8zm5 3a1 1 0 011-1h2a1 1 0 110 2H9a1 1 0 01-1-1z" clip-rule="evenodd"></path>
            </svg>
            归档
          </span>
        </a>
      </li>
    </ul>


  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const isActive = computed(() => (path) => {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
})
</script>