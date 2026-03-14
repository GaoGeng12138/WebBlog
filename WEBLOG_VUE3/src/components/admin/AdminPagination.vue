<template>
  <div class="flex items-center justify-between">
    <div class="text-sm font-semibold text-gray-700 flex items-center gap-2">
      <el-icon class="text-blue-500"><DocumentCopy /></el-icon>
      共 <span class="text-blue-600 font-bold text-lg">{{ total }}</span> 条数据
    </div>
    <el-pagination 
      v-model:current-page="currentPage" 
      v-model:page-size="pageSize" 
      :page-sizes="pageSizes" 
      :small="false" 
      :background="true" 
      layout="total, sizes, prev, pager, next, jumper" 
      :total="total"
      @size-change="handleSizeChange" 
      @current-change="handleCurrentChange" 
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { DocumentCopy } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50]
  }
})

const emit = defineEmits(['update:modelValue', 'change', 'size-change', 'current-change'])

const currentPage = ref(props.modelValue?.current || 1)
const pageSize = ref(props.modelValue?.size || 10)
const total = ref(props.modelValue?.total || 0)

// Watch for changes in props
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    currentPage.value = newVal.current || 1
    pageSize.value = newVal.size || 10
    total.value = newVal.total || 0
  }
}, { deep: true })

// Handle page size change
function handleSizeChange(val) {
  pageSize.value = val
  const paginationData = {
    current: currentPage.value,
    size: val,
    total: total.value
  }
  emit('update:modelValue', paginationData)
  emit('size-change', val)
  emit('change', paginationData)
}

// Handle current page change
function handleCurrentChange(val) {
  currentPage.value = val
  const paginationData = {
    current: val,
    size: pageSize.value,
    total: total.value
  }
  emit('update:modelValue', paginationData)
  emit('current-change', val)
  emit('change', paginationData)
}
</script>