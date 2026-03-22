<template>
  <div class="admin-list-page admin-comment-list-page min-h-screen bg-transparent p-6">
    <div class="mb-8">
      <h1 class="flex items-center gap-2 text-3xl font-bold text-slate-900">
        <el-icon class="text-[var(--theme-primary)]"><ChatLineRound /></el-icon>
        评论管理
      </h1>
      <p class="mt-2 text-sm text-slate-500">按文章、状态和日期筛选评论，并对评论进行审核、置顶和删除操作。</p>
    </div>

    <div class="admin-card mb-6 p-5">
      <div class="flex flex-col gap-4 xl:flex-row xl:items-center xl:justify-between">
        <div class="grid flex-1 gap-3 md:grid-cols-2 xl:grid-cols-4">
          <el-input
            v-model="searchForm.articleId"
            placeholder="文章 ID"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="searchForm.status" placeholder="评论状态" clearable @change="handleSearch">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
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
        :data="commentList"
        v-loading="loading"
        style="width: 100%"
        class="comment-table"
        header-cell-class-name="comment-table__header"
      >
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="articleTitle" label="文章" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="font-medium text-slate-700">{{ row.articleTitle || '未知文章' }}</div>
            <div class="text-xs text-slate-400">文章 ID：{{ row.articleId }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" width="140">
          <template #default="{ row }">
            <span class="text-slate-700">{{ row.nickname || '匿名用户' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="text-slate-600">{{ row.email || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" min-width="280" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusMeta(row.status).type" effect="light" round>
              {{ getStatusMeta(row.status).text }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞" width="100" align="center">
          <template #default="{ row }">
            <span class="font-medium text-slate-700">{{ row.likeCount ?? 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="isTop" label="置顶" width="110" align="center">
          <template #default="{ row }">
          <el-switch
            :model-value="!!row.isTop"
            inline-prompt
            active-text="是"
            inactive-text="否"
            :disabled="!can('admin:comment:top')"
            :loading="actionLoadingId === row.id && actionLoadingType === 'top'"
            @change="(value) => handleTopChange(row, value)"
          />
        </template>
      </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <div class="flex flex-wrap justify-center gap-2">
              <el-button
                v-if="can('admin:comment:audit') && row.status !== 1"
                type="primary"
                size="small"
                :loading="actionLoadingId === row.id && actionLoadingType === 'audit'"
                @click="handleAudit(row, 1)"
              >
                通过
              </el-button>
              <el-button
                v-if="can('admin:comment:audit') && row.status !== 2"
                type="warning"
                size="small"
                :loading="actionLoadingId === row.id && actionLoadingType === 'audit'"
                @click="handleAudit(row, 2)"
              >
                拒绝
              </el-button>
              <el-button
                v-if="can('admin:comment:delete')"
                type="danger"
                size="small"
                :loading="actionLoadingId === row.id && actionLoadingType === 'delete'"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="flex items-center justify-between border-t border-[rgba(149,171,210,0.14)] bg-[rgba(248,251,255,0.68)] px-6 py-4">
        <div class="text-sm text-slate-600">
          共 <span class="font-semibold text-[var(--theme-primary-deep)]">{{ total }}</span> 条评论
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
import { reactive, ref, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatLineRound } from '@element-plus/icons-vue'
import { hasAccess } from '@/composables/permission'
import { useUserStore } from '@/stores/user'
import { auditComment, deleteComment, getCommentPageList, setCommentTop } from '@/api/admin/comment'

const userStore = useUserStore()
const can = (permission) => hasAccess(userStore.userInfo, permission)

const searchForm = reactive({
  articleId: '',
  status: '',
  dateRange: []
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

const loading = ref(false)
const total = ref(0)
const commentList = ref([])
const actionLoadingId = ref(null)
const actionLoadingType = ref('')

const statusMap = {
  0: { text: '待审核', type: 'warning' },
  1: { text: '已通过', type: 'success' },
  2: { text: '已拒绝', type: 'danger' }
}

const getStatusMeta = (status) => statusMap[status] || { text: '未知', type: 'info' }

const toNullableNumber = (value) => {
  if (value === '' || value === null || value === undefined) {
    return null
  }
  const num = Number(value)
  return Number.isNaN(num) ? null : num
}

const loadCommentList = async () => {
  loading.value = true
  try {
    const response = await getCommentPageList({
      current: pagination.currentPage,
      size: pagination.pageSize,
      articleId: toNullableNumber(searchForm.articleId),
      status: searchForm.status === '' ? null : searchForm.status,
      startDate: searchForm.dateRange?.[0] || null,
      endDate: searchForm.dateRange?.[1] || null
    })

    if (response.success) {
      commentList.value = response.data || []
      total.value = response.total || 0
    } else {
      ElMessage.error(response.message || '获取评论列表失败')
    }
  } catch (error) {
    console.error('获取评论列表失败:', error)
    ElMessage.error('获取评论列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadCommentList()
}

const handleReset = () => {
  searchForm.articleId = ''
  searchForm.status = ''
  searchForm.dateRange = []
  pagination.currentPage = 1
  loadCommentList()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadCommentList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadCommentList()
}

const handleAudit = async (row, status) => {
  try {
    await ElMessageBox.confirm(
      status === 1 ? '确定要通过这条评论吗？' : '确定要拒绝这条评论吗？',
      '评论审核',
      { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
    )

    actionLoadingId.value = row.id
    actionLoadingType.value = 'audit'

    const response = await auditComment({
      commentId: row.id,
      status
    })

    if (response.success) {
      ElMessage.success(status === 1 ? '已通过' : '已拒绝')
      await loadCommentList()
    } else {
      ElMessage.error(response.message || '审核失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核评论失败:', error)
      ElMessage.error('审核评论失败')
    }
  } finally {
    actionLoadingId.value = null
    actionLoadingType.value = ''
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '删除评论', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })

    actionLoadingId.value = row.id
    actionLoadingType.value = 'delete'

    const response = await deleteComment(row.id)

    if (response.success) {
      ElMessage.success('删除成功')
      await loadCommentList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除评论失败')
    }
  } finally {
    actionLoadingId.value = null
    actionLoadingType.value = ''
  }
}

const handleTopChange = async (row, isTop) => {
  const previousTop = row.isTop
  try {
    row.isTop = isTop
    actionLoadingId.value = row.id
    actionLoadingType.value = 'top'

    const response = await setCommentTop({
      commentId: row.id,
      isTop
    })

    if (response.success) {
      ElMessage.success(isTop ? '已置顶' : '已取消置顶')
    } else {
      row.isTop = previousTop
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    row.isTop = previousTop
    console.error('设置评论置顶失败:', error)
    ElMessage.error('设置评论置顶失败')
  } finally {
    actionLoadingId.value = null
    actionLoadingType.value = ''
  }
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

onActivated(() => {
  loadCommentList()
})
</script>

<style scoped>
.comment-table :deep(.comment-table__header) {
  background: linear-gradient(180deg, rgba(240, 246, 253, 0.96), rgba(234, 242, 252, 0.82));
  color: #425a7f;
  font-weight: 600;
}

.comment-table :deep(.el-table__row) {
  color: #4a5f7d;
}

.comment-table :deep(.el-table__row:hover > td) {
  background: rgba(244, 248, 252, 0.72) !important;
}

@media (max-width: 768px) {
  .admin-comment-list-page {
    padding: 1rem !important;
  }

  .admin-comment-list-page .mb-8 h1 {
    font-size: 1.5rem;
    line-height: 2rem;
  }

  .admin-comment-list-page .admin-card {
    padding: 1rem !important;
  }

  .admin-comment-list-page .grid {
    grid-template-columns: 1fr !important;
  }

  .admin-comment-list-page .flex.gap-2 {
    width: 100%;
    flex-direction: column;
  }

  .admin-comment-list-page .flex.gap-2 .el-button {
    width: 100%;
  }

  .comment-table :deep(.el-table__header th),
  .comment-table :deep(.el-table__body td) {
    font-size: 12px;
  }

  .comment-table :deep(.el-table__header th:nth-child(1)),
  .comment-table :deep(.el-table__body td:nth-child(1)),
  .comment-table :deep(.el-table__header th:nth-child(3)),
  .comment-table :deep(.el-table__body td:nth-child(3)),
  .comment-table :deep(.el-table__header th:nth-child(4)),
  .comment-table :deep(.el-table__body td:nth-child(4)),
  .comment-table :deep(.el-table__header th:nth-child(7)),
  .comment-table :deep(.el-table__body td:nth-child(7)),
  .comment-table :deep(.el-table__header th:nth-child(8)),
  .comment-table :deep(.el-table__body td:nth-child(8)),
  .comment-table :deep(.el-table__header th:nth-child(9)),
  .comment-table :deep(.el-table__body td:nth-child(9)) {
    display: none !important;
  }

  .comment-table :deep(.el-table__body td:nth-child(5)) {
    white-space: normal;
  }

  .comment-table :deep(.el-button) {
    min-width: 0;
    padding-inline: 0.75rem;
  }

  .admin-comment-list-page .el-pagination {
    flex-wrap: wrap;
    gap: 0.35rem;
  }

  .admin-comment-list-page .flex.items-center.justify-between {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }
}
</style>
