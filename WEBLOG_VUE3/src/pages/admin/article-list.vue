<template>
    <div class="admin-list-page admin-article-list-page min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-6">
        <!-- 页面标题 -->
        <div class="mb-8">
            <h1 class="text-3xl font-bold text-gray-900 flex items-center gap-2">
                <el-icon class="text-blue-600"><Document /></el-icon>
                文章管理
            </h1>
            <p class="text-gray-500 mt-2 text-sm">管理您的博客文章</p>
        </div>

        <!-- 查询条件卡片 -->
        <div class="bg-white rounded-xl shadow-md border border-gray-200 p-5 mb-6 hover:shadow-lg transition-shadow duration-200">
            <div class="flex flex-wrap items-center gap-4">
                <div class="flex items-center">
                    <el-text class="mr-2 font-medium text-gray-700">文章标题</el-text>
                    <div class="w-52">
                        <el-input v-model="searchTitle" placeholder="🔍 请输入（模糊查询）" clearable />
                    </div>
                </div>

                <div class="flex items-center">
                    <el-text class="mr-2 font-medium text-gray-700">创建日期</el-text>
                    <div class="w-60">
                        <el-date-picker 
                            v-model="pickDate" 
                            type="daterange" 
                            range-separator="至" 
                            start-placeholder="开始时间"
                            end-placeholder="结束时间" 
                            :shortcuts="shortcuts" 
                            size="default" 
                            @change="datepickerChange"
                        />
                    </div>
                </div>

                <div class="flex items-center gap-2">
                    <el-button type="primary" :icon="Search" @click="getTableData">查询</el-button>
                    <el-button :icon="RefreshRight" @click="reset">重置</el-button>
                </div>
            </div>
        </div>

        <!-- 文章列表卡片 -->
        <div class="w-full bg-white rounded-xl shadow-md border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow duration-200">
            <!-- 操作栏 -->
            <div class="flex justify-between items-center px-6 pt-5 pb-4">
                <div>
                    <el-button v-if="can('admin:article:publish')" type="primary" @click="goToPublish">
                        <el-icon class="mr-1">
                            <Plus />
                        </el-icon>
                        发布文章
                    </el-button>
                </div>
                <div class="text-sm font-semibold text-gray-700 flex items-center gap-2">
                    <el-icon class="text-blue-500"><DocumentCopy /></el-icon>
                    共 <span class="text-blue-600 font-bold text-lg">{{ pagination.total }}</span> 条记录
                </div>
            </div>

            <!-- 文章表格 -->
            <el-table 
                :data="tableData" 
                stripe 
                style="width: 100%" 
                v-loading="tableLoading"
                class="article-table"
                header-cell-class-name="bg-gradient-to-r from-blue-50 to-blue-100 font-semibold text-gray-800 border-b-2 border-blue-200"
            >
                <el-table-column prop="title" label="文章标题" min-width="250" show-overflow-tooltip>
                    <template #default="{ row }">
                        <div class="py-2">
                            <div class="font-medium text-gray-900">{{ row.title }}</div>
                            <div class="text-xs text-gray-500 mt-1">{{ row.summary || '暂无摘要' }}</div>
                        </div>
                    </template>
                </el-table-column>
                
                <el-table-column label="封面" width="120" align="center">
                    <template #default="{ row }">
                        <div class="flex justify-center">
                            <el-image 
                                v-if="row.cover" 
                                :src="row.cover" 
                                fit="cover" 
                                style="width: 80px; height: 60px;"
                                :preview-src-list="[row.cover]" 
                                preview-teleported
                                class="rounded-md"
                            />
                            <div v-else class="w-20 h-15 flex items-center justify-center bg-gray-100 rounded-md">
                                <el-icon class="text-gray-400"><Picture /></el-icon>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                
                <el-table-column label="分类" width="120" align="center">
                    <template #default="{ row }">
                        <el-tag v-if="row.category" type="success" size="small">{{ row.category }}</el-tag>
                        <el-text v-else type="info">未分类</el-text>
                    </template>
                </el-table-column>
                
                <el-table-column label="标签" width="180">
                    <template #default="{ row }">
                        <div class="flex flex-wrap gap-1">
                            <el-tag 
                                v-for="(tag, index) in row.tags" 
                                :key="index" 
                                type="success" 
                                size="small"
                            >
                                {{ tag }}
                            </el-tag>
                            <el-text v-if="!row.tags || row.tags.length === 0" type="info">无标签</el-text>
                        </div>
                    </template>
                </el-table-column>

                <el-table-column label="文章来源" width="120" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.articleSource === 2 ? 'warning' : 'info'" size="small">
                            {{ row.articleSourceLabel || '后台发布' }}
                        </el-tag>
                    </template>
                </el-table-column>

                <el-table-column label="状态" width="120" align="center">
                    <template #default="{ row }">
                        <el-tag :type="getStatusMeta(row.status).type" size="small">
                            {{ row.statusLabel || getStatusMeta(row.status).text }}
                        </el-tag>
                    </template>
                </el-table-column>
                
                <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
                
                <el-table-column label="操作" width="200" fixed="right" align="center">
                    <template #default="{ row }">
                        <div class="flex gap-2 justify-center">
                            <el-button v-if="can('admin:article:update')" type="primary" size="small" @click="goToEdit(row.id)" class="admin-btn-primary">
                                <el-icon class="mr-1">
                                    <Edit />
                                </el-icon>
                                编辑
                            </el-button>
                            <el-button v-if="can('admin:article:delete')" type="danger" size="small" @click="deleteArticleSubmit(row)" class="admin-btn-secondary">
                                <el-icon class="mr-1">
                                    <Delete />
                                </el-icon>
                                删除
                            </el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页组件 -->
            <div class="px-6 py-4 border-t border-gray-200 bg-gradient-to-r from-gray-50 to-gray-100">
                <AdminPagination 
                    v-model="pagination" 
                    :page-sizes="[10, 20, 50]"
                    @change="handlePaginationChange"
                />
            </div>
        </div>
    </div>
</template>

<script setup>
import { getArticlePageList, deleteArticle } from '@/api/admin/article'
import { RefreshRight, Search, Plus, Edit, Delete, Picture, Document, DocumentCopy } from '@element-plus/icons-vue'
import { hasAccess } from '@/composables/permission'
import moment from 'moment'
import { ref,onActivated } from 'vue'
import { useUserStore } from '@/stores/user'
import { showMessage, showModel } from '@/composables/util'
import { useRouter } from 'vue-router'
import AdminPagination from '@/components/admin/AdminPagination.vue'

const router = useRouter()
const userStore = useUserStore()
const can = (permission) => hasAccess(userStore.userInfo, permission)
// 页面激活时获取表格数据
onActivated(() => {
    getTableData()
})

// 表格数据
const tableData = ref([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})
const tableLoading = ref(false)

const getStatusMeta = (status) => {
    const map = {
        0: { text: '待审核', type: 'warning' },
        1: { text: '审核通过', type: 'primary' },
        2: { text: '审核未通过', type: 'danger' },
        3: { text: '草稿', type: 'info' },
        4: { text: '已发布', type: 'success' }
    }
    return map[status] || { text: '未知状态', type: 'info' }
}

// 查询条件
const searchTitle = ref('')
const pickDate = ref('')
const startDate = ref('')
const endDate = ref('')

// 时间快捷选项
const shortcuts = [
    {
        text: '最近一周',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            return [start, end]
        },
    },
    {
        text: '最近一个月',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            return [start, end]
        },
    },
    {
        text: '最近三个月',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            return [start, end]
        },
    },
]

// 日期选择变化
const datepickerChange = (e) => {
    startDate.value = moment(e[0]).format('YYYY-MM-DD')
    endDate.value = moment(e[1]).format('YYYY-MM-DD')
}

// 处理分页变化
const handlePaginationChange = (paginationData) => {
    pagination.value.current = paginationData.current
    pagination.value.size = paginationData.size
    pagination.value.total = paginationData.total
    getTableData()
}

// 重置查询条件
const reset = () => {
    searchTitle.value = ''
    pickDate.value = ''
    startDate.value = null
    endDate.value = null
    getTableData()
}

// 获取分页数据
function getTableData() {
    
    tableLoading.value = true
    getArticlePageList({
        current: pagination.value.current,
        size: pagination.value.size,
        startDate: startDate.value,
        endDate: endDate.value,
        title: searchTitle.value
    })
        .then((res) => {
            if (res.success == true) {
                tableData.value = res.data
                pagination.value.current = res.current
                pagination.value.size = res.size
                pagination.value.total = res.total
            }
        })
        .finally(() => {
            tableLoading.value = false
        })
}
getTableData()

// 跳转到发布文章页面
const goToPublish = () => {
    router.push('/admin/article/publish')
}

// 跳转到编辑文章页面
const goToEdit = (id) => {
    router.push(`/admin/article/edit/${id}`)
}

// 删除文章
const deleteArticleSubmit = (row) => {
    showModel('是否确定要删除该文章？').then(() => {
        deleteArticle(row.id).then((res) => {
            if (res.success == true) {
                showMessage('删除成功')
                getTableData()
            } else {
                let message = res.message
                showMessage(message, 'error')
            }
        })
    }).catch(() => {})
}
</script>

<style scoped>
/* 表格样式 优化 */
.article-table :deep(.el-table__header th) {
  background-color: #f0f9ff !important;
  border-bottom: 2px solid #3b82f6 !important;
}

.article-table :deep(.el-table__body tr:hover > td) {
  background-color: #f0f9ff !important;
}

.article-table :deep(.el-table__row) {
  transition: all 0.3s ease;
}

/* 按钮样式 优化 */
:deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 输入框样式 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

/* 标签样式 优化 */
:deep(.el-tag) {
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 12px;
  font-weight: 500;
}
</style>

<style scoped>
@media (max-width: 768px) {
  .admin-article-list-page {
    padding: 1rem !important;
  }

  .admin-article-list-page .mb-8 h1 {
    font-size: 1.5rem;
    line-height: 2rem;
  }

  .admin-article-list-page .bg-white.rounded-xl {
    padding: 1rem !important;
  }

  .admin-article-list-page .flex.flex-wrap.items-center.gap-4 {
    gap: 0.75rem !important;
  }

  .admin-article-list-page .flex.flex-wrap.items-center.gap-4 > div,
  .admin-article-list-page .flex.flex-wrap.items-center.gap-4 .w-52,
  .admin-article-list-page .flex.flex-wrap.items-center.gap-4 .w-60 {
    width: 100% !important;
  }

  .admin-article-list-page .flex.justify-between.items-center {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }

  .admin-article-list-page .flex.justify-between.items-center > div:first-child {
    width: 100%;
  }

  .admin-article-list-page .flex.justify-between.items-center .el-button {
    width: 100%;
  }

  .admin-article-list-page .el-date-editor {
    width: 100% !important;
  }

  .article-table :deep(.el-table__header th),
  .article-table :deep(.el-table__body td) {
    font-size: 12px;
  }

  .article-table :deep(.el-table__header th:nth-child(2)),
  .article-table :deep(.el-table__body td:nth-child(2)),
  .article-table :deep(.el-table__header th:nth-child(3)),
  .article-table :deep(.el-table__body td:nth-child(3)),
  .article-table :deep(.el-table__header th:nth-child(4)),
  .article-table :deep(.el-table__body td:nth-child(4)),
  .article-table :deep(.el-table__header th:nth-child(5)),
  .article-table :deep(.el-table__body td:nth-child(5)),
  .article-table :deep(.el-table__header th:nth-child(7)),
  .article-table :deep(.el-table__body td:nth-child(7)) {
    display: none !important;
  }

  .article-table :deep(.el-table__header th:nth-child(1)),
  .article-table :deep(.el-table__body td:nth-child(1)) {
    width: auto !important;
  }

  .article-table :deep(.el-table__body td:nth-child(1) .font-medium) {
    font-size: 0.95rem;
  }

  .article-table :deep(.el-table__body td:nth-child(6)),
  .article-table :deep(.el-table__header th:nth-child(6)) {
    width: 90px !important;
  }

  .article-table :deep(.el-table__body td:nth-child(8)),
  .article-table :deep(.el-table__header th:nth-child(8)) {
    width: 110px !important;
  }

  .article-table :deep(.el-button) {
    min-width: 0;
    padding-inline: 0.75rem;
  }
}
</style>
