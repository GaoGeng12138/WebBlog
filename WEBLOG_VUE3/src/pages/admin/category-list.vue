<template>
    <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-6">
        <!-- 页面标题 -->
        <div class="mb-8">
            <h1 class="text-3xl font-bold text-gray-900 flex items-center gap-2">
                <el-icon class="text-blue-600"><FolderOpened /></el-icon>
                分类管理
            </h1>
            <p class="text-gray-500 mt-2 text-sm">管理您的博客文章分类</p>
        </div>

        <!-- 查询条件卡片 -->
        <div class="bg-white rounded-xl shadow-md border border-gray-200 p-5 mb-6 hover:shadow-lg transition-shadow duration-200">
            <div class="flex flex-wrap items-center gap-4">
                <div class="flex items-center">
                    <el-text class="mr-2 font-medium text-gray-700">分类名称</el-text>
                    <div class="w-52">
                        <el-input v-model="searchCategoryName" placeholder="🔍 请输入（模糊查询）" clearable />
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

        <!-- 分类列表卡片 -->
        <div class="w-full bg-white rounded-xl shadow-md border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow duration-200">
            <!-- 操作栏 -->
            <div class="flex justify-between items-center px-6 pt-5 pb-4">
                <div>
                    <el-button type="primary" @click="addCategoryBtnClick">
                        <el-icon class="mr-1">
                            <Plus />
                        </el-icon>
                        新增分类
                    </el-button>
                </div>
                <div class="text-sm font-semibold text-gray-700 flex items-center gap-2">
                    <el-icon class="text-blue-500"><DocumentCopy /></el-icon>
                    共 <span class="text-blue-600 font-bold text-lg">{{ pagination.total }}</span> 条记录
                </div>
            </div>

            <!-- 分类表格 -->
            <el-table 
                :data="tableData" 
                stripe 
                style="width: 100%" 
                v-loading="tableLoading"
                class="category-table"
                header-cell-class-name="bg-gradient-to-r from-blue-50 to-blue-100 font-semibold text-gray-800 border-b-2 border-blue-200"
            >
                <el-table-column prop="name" label="分类名称" width="200">
                    <template #default="{ row }">
                        <div class="flex items-center">
                            <el-tag type="success">{{ row.name }}</el-tag>
                        </div>
                    </template>
                </el-table-column>
                
                <el-table-column prop="illustrate" label="分类描述" min-width="250">
                    <template #default="{ row }">
                        <div class="py-2">
                            <div class="text-gray-700">{{ row.illustrate || '暂无描述' }}</div>
                        </div>
                    </template>
                </el-table-column>
                
                <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
                
                <el-table-column label="操作" width="150" fixed="right" align="center">
                    <template #default="scope">
                        <el-button type="danger" size="small" @click="deleteCategorySubmit(scope.row)" class="admin-btn-secondary">
                            <el-icon class="mr-1">
                                <Delete />
                            </el-icon>
                            删除
                        </el-button>
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

        <!-- 新增分类对话框 -->
        <el-dialog v-model="dialogVisible" title="添加文章分类" width="40%" :draggable="true">
            <el-form ref="formRef" :rules="rules" :model="form" @submit="onSubmit" @submit.prevent>
                <el-form-item label="分类名称" prop="name" label-width="80px" size="large">
                    <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="20" show-word-limit clearable class="admin-input" />
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
// 引入所需图标
import { getCategoryPageList, addCategory, deleteCategory } from '@/api/admin/category'
import { RefreshRight, Search, Plus, Delete, FolderOpened, DocumentCopy } from '@element-plus/icons-vue'
import moment from 'moment'
import { ref, reactive } from 'vue'
import { showMessage, showModel } from '@/composables/util'
import AdminPagination from '@/components/admin/AdminPagination.vue'


// 表格数据
const tableData = ref([])
// 分页数据
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 对话框是否显示
const dialogVisible = ref(false)
const btnLoading = ref(false)

// 新增分类按钮点击事件
const addCategoryBtnClick = () => {
    dialogVisible.value = true
    // 重置表单
    form.name = ''
    if (formRef.value) {
        formRef.value.resetFields()
    }
}

// 时间显示快捷选项
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
// 分页查询的分类名称
const searchCategoryName = ref('')
// 日期
const pickDate = ref('')
// 查询条件：开始结束时间
const startDate = ref('')
const endDate = ref('')

// 监听日期组件改变事件，并将开始结束时间设置到变量中
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
    searchCategoryName.value = ''
    pickDate.value = ''
    startDate.value = null
    endDate.value = null
    getTableData()
}

//表单加载数据
const tableLoading = ref(false)
// 获取分页数据
function getTableData() {
    // 显示表格 loading
    tableLoading.value = true
    // 调用后台分页接口，并传入所需参数
    getCategoryPageList({ 
        current: pagination.value.current, 
        size: pagination.value.size, 
        startDate: startDate.value, 
        endDate: endDate.value, 
        name: searchCategoryName.value 
    })
        .then((res) => {
            if (res.success == true) {
                tableData.value = res.data
                pagination.value.current = res.current
                pagination.value.size = res.size
                pagination.value.total = res.total
            }
        }).finally(() => {
            tableLoading.value = false// 隐藏表格 loading
        })
}
getTableData()

// 添加文章分类表单对象
const form = reactive({
    name: ''
})

// 规则校验
const rules = {
    name: [
        {
            required: true,
            message: '分类名称不能为空',
            trigger: 'blur',
        },
        { min: 1, max: 20, message: '分类名称字数要求大于 1 个字符，小于 20 个字符', trigger: 'blur' },
    ]
}
//表单
const formRef = ref(null)
const onSubmit = () => {
    // 先验证 form 表单字段
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('表单验证不通过')
            return false
        }
        // 显示提交按钮 loading
        btnLoading.value = true
        // 请求添加分类接口
        addCategory(form).then((res) => {
            if (res.success == true) {
                showMessage('添加成功')
                // 将表单中分类名称置空
                form.name = ''
                // 隐藏对话框
                dialogVisible.value = false
                // 重新请求分页接口，渲染数据
                getTableData()
            } else {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
            }
        }).finally(() => {
            btnLoading.value = false
        }) // 隐藏提交按钮 loading

    })
}
// 删除分类
const deleteCategorySubmit = (row) => {
    showModel('是否确定要删除该分类？').then(() => {
        deleteCategory(row.id).then((res) => {
            if (res.success == true) {
                showMessage('删除成功')
                // 重新请求分页接口，渲染数据
                getTableData()
            } else {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
            }
        })
    }).catch(() => {
        console.log('取消了')
    })
}
</script>

<style scoped>
/* 表格样式 优化 */
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