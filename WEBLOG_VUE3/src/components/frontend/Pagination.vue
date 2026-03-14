<template>
  <div class="flex flex-col sm:flex-row items-center justify-between gap-4">
    <div class="text-sm font-semibold text-gray-700 flex items-center gap-2">
      <el-icon class="text-blue-500"><DocumentCopy /></el-icon>
      共 <span class="text-blue-600 font-bold text-lg">{{ total }}</span> 条数据
    </div>
    <el-pagination 
      v-model:current-page="internalCurrentPage" 
      v-model:page-size="internalPageSize" 
      :page-sizes="[10, 20, 30, 50]" 
      :small="false" 
      :background="true" 
      :layout="'sizes, prev, pager, next, jumper'" 
      :total="total"
      @size-change="handleSizeChange" 
      @current-change="handleCurrentChange" 
      class="flex-wrap"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { DocumentCopy } from '@element-plus/icons-vue'

const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  pageSize: {
    type: Number,
    default: 10
  },
  total: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['update:currentPage', 'update:pageSize', 'pageChange', 'sizeChange'])

const internalCurrentPage = ref(props.currentPage)
const internalPageSize = ref(props.pageSize)

// Watch for prop changes
watch(() => props.currentPage, (newVal) => {
  internalCurrentPage.value = newVal
})

watch(() => props.pageSize, (newVal) => {
  internalPageSize.value = newVal
})

// Handle page size change
function handleSizeChange(val) {
  internalPageSize.value = val
  emit('update:pageSize', val)
  emit('sizeChange', val)
  // Reset to page 1 when page size changes
  if (internalCurrentPage.value !== 1) {
    internalCurrentPage.value = 1
    emit('update:currentPage', 1)
    emit('pageChange', 1)
  }
}

// Handle current page change
function handleCurrentChange(val) {
  internalCurrentPage.value = val
  emit('update:currentPage', val)
  emit('pageChange', val)
}
</script>