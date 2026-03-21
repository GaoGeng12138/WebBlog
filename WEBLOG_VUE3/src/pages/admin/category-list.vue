<template>
    <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-6">
        <div class="mb-8">
            <h1 class="text-3xl font-bold text-gray-900 flex items-center gap-2">
                <el-icon class="text-blue-600"><FolderOpened /></el-icon>
                分类管理
            </h1>
            <p class="text-gray-500 mt-2 text-sm">控制分类资料与前台导航展示</p>
        </div>

        <div class="bg-white rounded-xl shadow-md border border-gray-200 p-5 mb-6 hover:shadow-lg transition-shadow duration-200">
            <div class="flex flex-wrap items-center gap-4">
                <div class="flex items-center">
                    <el-text class="mr-2 font-medium text-gray-700">分类名称</el-text>
                    <div class="w-52">
                        <el-input v-model="searchCategoryName" placeholder="请输入分类名称" clearable />
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

        <div class="w-full bg-white rounded-xl shadow-md border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow duration-200">
            <div class="flex justify-between items-center px-6 pt-5 pb-4">
                <div>
                    <el-button v-if="can('admin:category:add')" type="primary" @click="addCategoryBtnClick">
                        <el-icon class="mr-1"><Plus /></el-icon>
                        新增分类
                    </el-button>
                </div>
                <div class="text-sm font-semibold text-gray-700 flex items-center gap-2">
                    <el-icon class="text-blue-500"><DocumentCopy /></el-icon>
                    共 <span class="text-blue-600 font-bold text-lg">{{ pagination.total }}</span> 条记录
                </div>
            </div>

            <el-table
                :data="tableData"
                stripe
                style="width: 100%"
                v-loading="tableLoading"
                class="category-table"
                header-cell-class-name="bg-gradient-to-r from-blue-50 to-blue-100 font-semibold text-gray-800 border-b-2 border-blue-200"
            >
                <el-table-column prop="name" label="分类名称" width="180">
                    <template #default="{ row }">
                        <el-tag type="success">{{ row.name }}</el-tag>
                    </template>
                </el-table-column>

                <el-table-column prop="illustrate" label="分类描述" min-width="240">
                    <template #default="{ row }">
                        <div class="py-2 text-gray-700">{{ row.illustrate || '暂无描述' }}</div>
                    </template>
                </el-table-column>

                <el-table-column prop="showOnFront" label="前台导航展示" width="160" align="center">
                    <template #default="{ row }">
                        <el-switch
                            :model-value="row.showOnFront"
                            inline-prompt
                            active-text="显示"
                            inactive-text="隐藏"
                            :disabled="!can('admin:category:update-front')"
                            @change="(value) => handleShowOnFrontChange(row, value)"
                        />
                    </template>
                </el-table-column>

                <el-table-column prop="createTime" label="创建时间" width="180" align="center" />

                <el-table-column label="操作" width="150" fixed="right" align="center">
                    <template #default="scope">
                        <el-button v-if="can('admin:category:delete')" type="danger" size="small" @click="deleteCategorySubmit(scope.row)" class="admin-btn-secondary">
                            <el-icon class="mr-1"><Delete /></el-icon>
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="px-6 py-4 border-t border-gray-200 bg-gradient-to-r from-gray-50 to-gray-100">
                <AdminPagination
                    v-model="pagination"
                    :page-sizes="[10, 20, 50]"
                    @change="handlePaginationChange"
                />
            </div>
        </div>

        <el-dialog v-model="dialogVisible" title="添加文章分类" width="40%" :draggable="true">
            <el-form ref="formRef" :rules="rules" :model="form" label-width="96px" @submit="onSubmit" @submit.prevent>
                <el-form-item label="分类名称" prop="name" size="large">
                    <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="20" show-word-limit clearable class="admin-input" />
                </el-form-item>
                <el-form-item label="分类描述" prop="illustrate" size="large">
                    <el-input
                        v-model="form.illustrate"
                        type="textarea"
                        :rows="3"
                        placeholder="请输入分类描述"
                        maxlength="50"
                        show-word-limit
                    />
                </el-form-item>
                <el-form-item label="前台展示" prop="showOnFront" size="large">
                    <el-switch v-model="form.showOnFront" inline-prompt active-text="显示" inactive-text="隐藏" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false" class="admin-btn-secondary">取消</el-button>
                    <el-button type="primary" @click="onSubmit" :loading="btnLoading" class="admin-btn-primary">提交</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { addCategory, deleteCategory, getCategoryPageList, updateCategoryShowOnFront } from '@/api/admin/category'
import { hasAccess } from '@/composables/permission'
import { RefreshRight, Search, Plus, Delete, FolderOpened, DocumentCopy } from '@element-plus/icons-vue'
import moment from 'moment'
import { reactive, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { showMessage, showModel } from '@/composables/util'
import AdminPagination from '@/components/admin/AdminPagination.vue'

const userStore = useUserStore()
const can = (permission) => hasAccess(userStore.userInfo, permission)

const tableData = ref([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const btnLoading = ref(false)
const tableLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  name: '',
  illustrate: '',
  showOnFront: true
})

const rules = {
  name: [
    { required: true, message: '分类名称不能为空', trigger: 'blur' },
    { min: 1, max: 20, message: '分类名称字数要求大于 1 个字符，小于 20 个字符', trigger: 'blur' }
  ],
  illustrate: [
    { required: true, message: '分类描述不能为空', trigger: 'blur' },
    { min: 1, max: 50, message: '分类描述字数要求大于 1 个字符，小于 50 个字符', trigger: 'blur' }
  ]
}

const addCategoryBtnClick = () => {
    dialogVisible.value = true
    form.name = ''
    form.illustrate = ''
    form.showOnFront = true
    if (formRef.value) {
        formRef.value.resetFields()
    }
}

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

const searchCategoryName = ref('')
const pickDate = ref('')
const startDate = ref('')
const endDate = ref('')

const datepickerChange = (e) => {
    if (!e || e.length !== 2) {
        startDate.value = null
        endDate.value = null
        return
    }
    startDate.value = moment(e[0]).format('YYYY-MM-DD')
    endDate.value = moment(e[1]).format('YYYY-MM-DD')
}

const handlePaginationChange = (paginationData) => {
    pagination.value.current = paginationData.current
    pagination.value.size = paginationData.size
    pagination.value.total = paginationData.total
    getTableData()
}

const reset = () => {
    searchCategoryName.value = ''
    pickDate.value = ''
    startDate.value = null
    endDate.value = null
    getTableData()
}

function getTableData() {
    tableLoading.value = true
    getCategoryPageList({
        current: pagination.value.current,
        size: pagination.value.size,
        startDate: startDate.value,
        endDate: endDate.value,
        name: searchCategoryName.value
    }).then((res) => {
        if (res.success === true) {
            tableData.value = (res.data || []).map(item => ({
                ...item,
                showOnFront: item.showOnFront !== false
            }))
            pagination.value.current = res.current
            pagination.value.size = res.size
            pagination.value.total = res.total
        }
    }).finally(() => {
        tableLoading.value = false
    })
}
getTableData()

const onSubmit = () => {
    formRef.value.validate((valid) => {
        if (!valid) {
            return false
        }
        btnLoading.value = true
        addCategory({
            name: form.name,
            illustrate: form.illustrate,
            showOnFront: form.showOnFront
        }).then((res) => {
            if (res.success === true) {
                showMessage('添加成功')
                dialogVisible.value = false
                getTableData()
            } else {
                showMessage(res.message, 'error')
            }
        }).finally(() => {
            btnLoading.value = false
        })
    })
}

const handleShowOnFrontChange = (row, value) => {
    updateCategoryShowOnFront({
        id: row.id,
        showOnFront: value
    }).then((res) => {
        if (res.success === true) {
            row.showOnFront = value
            showMessage(`已${value ? '显示' : '隐藏'}该分类`) 
        } else {
            row.showOnFront = !value
            showMessage(res.message, 'error')
        }
    }).catch(() => {
        row.showOnFront = !value
        showMessage('更新失败', 'error')
    })
}

const deleteCategorySubmit = (row) => {
    showModel('是否确定要删除该分类？').then(() => {
        deleteCategory(row.id).then((res) => {
            if (res.success === true) {
                showMessage('删除成功')
                getTableData()
            } else {
                showMessage(res.message, 'error')
            }
        })
    }).catch(() => {
        console.log('取消了')
    })
}
</script>

<style scoped>
.category-table :deep(.el-table__header th) {
  background-color: #f0f9ff !important;
  border-bottom: 2px solid #3b82f6 !important;
}

.category-table :deep(.el-table__body tr:hover > td) {
  background-color: #f0f9ff !important;
}

.category-table :deep(.el-table__row) {
  transition: all 0.3s ease;
}

:deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner) {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover),
:deep(.el-textarea__inner:hover) {
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

:deep(.el-tag) {
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 12px;
  font-weight: 500;
}
</style>
