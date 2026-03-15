<template>
  <div :class="wrapperClass">
    <div v-if="!simpleMode && !pagerOnly" class="text-sm font-semibold text-gray-700 flex items-center gap-2">
      <el-icon class="text-blue-500"><DocumentCopy /></el-icon>
      共 <span class="text-blue-600 font-bold text-lg">{{ total }}</span> 条数据
    </div>

    <el-pagination
      v-model:current-page="internalCurrentPage"
      v-model:page-size="internalCurrentPageSize"
      :page-sizes="[10, 20, 30, 50]"
      :small="simpleMode || pagerOnly"
      :background="true"
      :layout="layoutValue"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :class="paginationClass"
    />
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
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
  },
  simpleMode: {
    type: Boolean,
    default: false
  },
  pagerOnly: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:currentPage', 'update:pageSize', 'pageChange', 'sizeChange'])

const internalCurrentPage = ref(props.currentPage)
const internalCurrentPageSize = ref(props.pageSize)

watch(() => props.currentPage, (newVal) => {
  internalCurrentPage.value = newVal
})

watch(() => props.pageSize, (newVal) => {
  internalCurrentPageSize.value = newVal
})

const layoutValue = computed(() => {
  if (props.pagerOnly) return 'prev, pager, next'
  return props.simpleMode ? 'prev, pager, next' : 'sizes, prev, pager, next, jumper'
})

const wrapperClass = computed(() => {
  if (props.pagerOnly || props.simpleMode) return 'flex justify-center'
  return 'flex flex-col sm:flex-row items-center justify-between gap-4'
})

const paginationClass = computed(() => {
  if (props.pagerOnly) return 'frontend-pager-only'
  return props.simpleMode ? 'home-pagination' : 'flex-wrap'
})

function handleSizeChange(val) {
  internalCurrentPageSize.value = val
  emit('update:pageSize', val)
  emit('sizeChange', val)
  if (internalCurrentPage.value !== 1) {
    internalCurrentPage.value = 1
    emit('update:currentPage', 1)
    emit('pageChange', 1)
  }
}

function handleCurrentChange(val) {
  internalCurrentPage.value = val
  emit('update:currentPage', val)
  emit('pageChange', val)
}
</script>

<style scoped>
.home-pagination :deep(.el-pager li.is-active),
.frontend-pager-only :deep(.el-pager li.is-active) {
  background-color: #f59e0b;
  border-color: #f59e0b;
  color: #fff;
}

.home-pagination :deep(.btn-next),
.home-pagination :deep(.btn-prev),
.home-pagination :deep(.el-pager li),
.frontend-pager-only :deep(.btn-next),
.frontend-pager-only :deep(.btn-prev),
.frontend-pager-only :deep(.el-pager li) {
  border-radius: 9999px;
}

.frontend-pager-only :deep(.btn-prev),
.frontend-pager-only :deep(.btn-next) {
  min-width: 34px;
}
</style>
