<template>
  <div class="min-h-screen bg-transparent p-6">
    <div class="mb-8">
      <h1 class="flex items-center gap-2 text-3xl font-bold text-slate-900">
        <el-icon class="text-[var(--theme-primary)]"><Position /></el-icon>
        访客记录
      </h1>
      <p class="mt-2 text-sm text-slate-500">查看访客 IP、归属信息、访问日期以及记录时间，快速了解最近的访问轨迹。</p>
    </div>

    <div class="admin-card mb-6 p-5">
      <div class="flex flex-col gap-4 xl:flex-row xl:items-center xl:justify-between">
        <div class="grid flex-1 gap-3 md:grid-cols-2 xl:grid-cols-4">
          <el-input
            v-model="searchForm.ipAddress"
            placeholder="搜索 IP 地址"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="searchForm.visitorType" placeholder="访客类型" clearable @change="handleSearch">
            <el-option label="登录用户" value="member" />
            <el-option label="匿名访客" value="anonymous" />
          </el-select>
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            clearable
          />
        </div>
        <div class="flex gap-2">
          <el-button class="admin-btn-primary" @click="handleSearch">查询</el-button>
          <el-button class="admin-btn-secondary" @click="handleReset">重置</el-button>
        </div>
      </div>
    </div>

    <div class="admin-card overflow-hidden">
      <el-table
        :data="visitorList"
        v-loading="loading"
        style="width: 100%"
        class="visitor-table"
        header-cell-class-name="visitor-table__header"
      >
        <el-table-column label="序号" width="90" align="center">
          <template #default="{ $index }">
            {{ (pagination.currentPage - 1) * pagination.pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="ipAddress" label="IP 地址" min-width="160">
          <template #default="{ row }">
            <div class="font-medium text-slate-700">{{ row.ipAddress || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="ipLocation" label="地址解析" min-width="220">
          <template #default="{ row }">
            <span class="rounded-full bg-[rgba(116,149,195,0.1)] px-3 py-1 text-xs font-medium text-[var(--theme-primary-deep)]">
              {{ row.ipLocation || '未知地址' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="visitorType" label="访客类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.visitorType === 'member' ? 'primary' : 'info'" effect="light" round>
              {{ row.visitorType === 'member' ? '登录用户' : '匿名访客' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="关联用户" min-width="140">
          <template #default="{ row }">
            <span class="text-slate-600">{{ row.username || '未登录' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="visitDate" label="访问日期" width="130" />
        <el-table-column prop="createTime" label="记录时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>

      <div class="flex items-center justify-between border-t border-[rgba(149,171,210,0.14)] bg-[rgba(248,251,255,0.68)] px-6 py-4">
        <div class="text-sm text-slate-600">
          共 <span class="font-semibold text-[var(--theme-primary-deep)]">{{ total }}</span> 条访客记录
        </div>
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Position } from '@element-plus/icons-vue'
import { getVisitorLogList } from '@/api/admin/visitor'

const searchForm = reactive({
  ipAddress: '',
  visitorType: '',
  dateRange: []
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

const loading = ref(false)
const total = ref(0)
const visitorList = ref([])

const loadVisitorList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.currentPage,
      size: pagination.pageSize,
      ipAddress: searchForm.ipAddress || null,
      visitorType: searchForm.visitorType || null,
      startDate: searchForm.dateRange?.[0] || null,
      endDate: searchForm.dateRange?.[1] || null
    }

    const response = await getVisitorLogList(params)
    if (response.success) {
      visitorList.value = response.data || []
      total.value = response.total || 0
    } else {
      ElMessage.error(response.message || '获取访客记录失败')
    }
  } catch (error) {
    console.error('获取访客记录失败:', error)
    ElMessage.error('获取访客记录失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadVisitorList()
}

const handleReset = () => {
  searchForm.ipAddress = ''
  searchForm.visitorType = ''
  searchForm.dateRange = []
  pagination.currentPage = 1
  loadVisitorList()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadVisitorList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadVisitorList()
}

const formatDateTime = (value) => {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }
  const yyyy = date.getFullYear()
  const mm = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')
  const hh = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  const sec = String(date.getSeconds()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd} ${hh}:${min}:${sec}`
}

onMounted(() => {
  loadVisitorList()
})
</script>

<style scoped>
.visitor-table :deep(.visitor-table__header) {
  background: linear-gradient(180deg, rgba(240, 246, 253, 0.96), rgba(234, 242, 252, 0.82));
  color: #425a7f;
  font-weight: 600;
}

.visitor-table :deep(.el-table__row) {
  color: #4a5f7d;
}

.visitor-table :deep(.el-table__row:hover > td) {
  background: rgba(244, 248, 252, 0.72) !important;
}
</style>
