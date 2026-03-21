<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-6">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900 flex items-center gap-2">
        <el-icon class="text-blue-600"><Lock /></el-icon>
        角色管理
      </h1>
      <p class="text-gray-500 mt-2 text-sm">管理系统角色和权限分配</p>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="bg-white rounded-xl shadow-md border border-gray-200 p-5 mb-6 hover:shadow-lg transition-shadow duration-200">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div class="flex flex-col sm:flex-row gap-3">
          <el-input
            v-model="searchForm.name"
            placeholder="🔍 搜索角色名称或标识"
            clearable
            @keyup.enter="handleSearch"
            class="w-full md:w-64"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="flex gap-2">
          <el-button v-if="can('admin:role:add')" type="primary" @click="handleAddRole">
            <el-icon><Plus /></el-icon>
            新增角色
          </el-button>
          <el-button @click="handleReset">
            重置
          </el-button>
        </div>
      </div>
    </div>

    <!-- 角色列表 -->
    <div class="w-full bg-white rounded-xl shadow-md border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow duration-200">
      <div class="flex justify-end items-center px-6 pt-5 pb-4">
        <div class="text-sm font-semibold text-gray-700 flex items-center gap-2">
          <el-icon class="text-blue-500"><DocumentCopy /></el-icon>
          共 <span class="text-blue-600 font-bold text-lg">{{ total }}</span> 条角色
        </div>
      </div>
      <el-table
        :data="roleList"
        v-loading="loading"
        stripe
        style="width: 100%"
        class="role-table"
        header-cell-class-name="bg-gradient-to-r from-blue-50 to-blue-100 font-semibold text-gray-800 border-b-2 border-blue-200"
      >
        <el-table-column type="selection" width="50" fixed="left" />
        <el-table-column prop="id" label="ID" width="60" fixed="left" />
        <el-table-column prop="name" label="角色标识" min-width="140">
          <template #default="scope">
            <el-tag type="primary" size="small">{{ scope.row.name }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
        <el-table-column prop="isEnabled" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.isEnabled ? 'success' : 'danger'" size="small">
              {{ scope.row.isEnabled ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="140" show-overflow-tooltip>
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" min-width="140" show-overflow-tooltip>
          <template #default="scope">
            {{ formatDate(scope.row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="scope">
            <div class="flex flex-wrap gap-1">
              <el-button
                v-if="can('admin:role:update')"
                size="small"
                type="primary"
                @click="handleEdit(scope.row)"
              >
                编辑
              </el-button>
              <el-button
                v-if="can('admin:role:permission-assign')"
                size="small"
                type="info"
                @click="handleAssignPermissions(scope.row)"
              >
                权限
              </el-button>
              <el-button
                v-if="can('admin:role:update')"
                size="small"
                :type="scope.row.isEnabled ? 'warning' : 'success'"
                @click="handleChangeStatus(scope.row)"
              >
                {{ scope.row.isEnabled ? '停用' : '启用' }}
              </el-button>
              <el-button
                v-if="can('admin:role:delete')"
                size="small"
                type="danger"
                @click="handleDelete(scope.row)"
                :disabled="scope.row.isSystem"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="px-6 py-4 flex items-center justify-between border-t border-gray-200 bg-gradient-to-r from-gray-50 to-gray-100">
        <div class="text-sm font-semibold text-gray-700 flex items-center gap-2">
          <el-icon class="text-blue-500"><DocumentCopy /></el-icon>
          共 <span class="text-blue-600 font-bold text-lg">{{ total }}</span> 条数据
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

    <!-- 新增/编辑角色对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleFormRules"
        label-width="100px"
      >
        <el-form-item label="角色标识" prop="name">
          <el-input
            v-model="roleForm.name"
            :disabled="!!roleForm.id"
            placeholder="例如: ROLE_EDITOR"
          />
          <el-text type="info" size="small" class="mt-1">
            角色标识必须以 ROLE_ 开头，使用大写字母和下划线
          </el-text>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="roleForm.isEnabled"
            :active-value="true"
            :inactive-value="false"
            active-text="启用"
            inactive-text="停用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
            :loading="submitLoading"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分配权限对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="分配权限"
      width="600px"
      @close="handlePermissionDialogClose"
    >
      <div class="mb-4">
        <el-alert
          title="权限说明"
          type="info"
          :closable="false"
          show-icon
        >
          <p>为角色 <strong>{{ currentRole?.name }}</strong> 分配系统权限</p>
        </el-alert>
      </div>
      
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermissions"
        :props="{ children: 'children', label: 'name' }"
        class="permission-tree"
      >
        <template #default="{ node, data }">
          <span class="flex items-center gap-2">
            <el-icon v-if="data.type === 'menu'"><Menu /></el-icon>
            <el-icon v-else-if="data.type === 'button'"><Position /></el-icon>
            <span>{{ data.name }}</span>
            <el-tag v-if="data.permissionKey" size="small" type="info">{{ data.permissionKey }}</el-tag>
          </span>
        </template>
      </el-tree>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmitPermissions"
            :loading="permissionSubmitLoading"
          >
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getRoleList,
  addRole,
  updateRole,
  deleteRole,
  getRolePermissions,
  updateRolePermissions,
  getAllPermissions
} from '@/api/admin/role'
import { hasAccess } from '@/composables/permission'
import { useUserStore } from '@/stores/user'
import { Search, Plus, Menu, Position, Lock, DocumentCopy } from '@element-plus/icons-vue'
import moment from 'moment'

// 搜索表单
const searchForm = reactive({
  name: ''
})

const userStore = useUserStore()
const can = (permission) => hasAccess(userStore.userInfo, permission)

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

// 数据
const roleList = ref([])
const total = ref(0)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)

// 权限对话框
const permissionDialogVisible = ref(false)
const permissionSubmitLoading = ref(false)
const currentRole = ref(null)
const permissionTree = ref([])
const checkedPermissions = ref([])

// 表单引用
const roleFormRef = ref()
const permissionTreeRef = ref()

// 角色表单
const roleForm = reactive({
  id: null,
  name: '',           // 角色标识，如 ROLE_ADMIN
  description: '',
  isEnabled: true
})

// 表单验证规则
const roleFormRules = {
  name: [
    { required: true, message: '请输入角色标识', trigger: 'blur' },
    { 
      pattern: /^ROLE_[A-Z_]+$/, 
      message: '角色标识必须以ROLE_开头，只能包含大写字母和下划线', 
      trigger: 'blur' 
    }
  ],
  description: [
    { required: true, message: '请输入角色描述', trigger: 'blur' },
    { min: 2, max: 100, message: '角色描述长度为2-100个字符', trigger: 'blur' }
  ]
}

// 加载角色列表
const loadRoleList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.currentPage,
      size: pagination.pageSize,
      name: searchForm.name || null
    }
    
    const response = await getRoleList(params)
    if (response.success) {
      roleList.value = response.data || []
      total.value = response.total || 0
    } else {
      ElMessage.error(response.message || '获取角色列表失败')
    }
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadRoleList()
}

// 重置搜索
const handleReset = () => {
  searchForm.name = ''
  pagination.currentPage = 1
  loadRoleList()
}

// 分页变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadRoleList()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadRoleList()
}

// 新增角色
const handleAddRole = () => {
  dialogTitle.value = '新增角色'
  dialogVisible.value = true
  // 重置表单
  Object.assign(roleForm, {
    id: null,
    name: '',
    description: '',
    isEnabled: true
  })
}

// 编辑角色
const handleEdit = (row) => {
  dialogTitle.value = '编辑角色'
  dialogVisible.value = true
  // 填充表单数据
  Object.assign(roleForm, {
    id: row.id,
    name: row.name,
    description: row.description,
    isEnabled: row.isEnabled
  })
}

// 删除角色
const handleDelete = (row) => {
  if (row.isSystem) {
    ElMessage.warning('系统角色不能删除')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除角色 "${row.name}" 吗？此操作不可恢复！`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await deleteRole(row.id)
      if (response.success) {
        ElMessage.success('删除成功')
        loadRoleList()
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      console.error('删除角色失败:', error)
      ElMessage.error('删除角色失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 更改角色状态
const handleChangeStatus = async (row) => {
  try {
    const newStatus = !row.isEnabled
    const action = newStatus ? '启用' : '停用'
    
    const response = await updateRole(row.id, { isEnabled: newStatus })
    if (response.success) {
      ElMessage.success(`${action}成功`)
      // 更新本地数据
      row.isEnabled = newStatus
    } else {
      ElMessage.error(response.message || `${action}失败`)
    }
  } catch (error) {
    console.error('更改角色状态失败:', error)
    ElMessage.error('更改角色状态失败')
  }
}

// 提交表单
const handleSubmit = () => {
  roleFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      let response
      if (roleForm.id) {
        // 编辑角色
        const { id, ...updateData } = roleForm
        response = await updateRole(id, updateData)
      } else {
        // 新增角色
        response = await addRole(roleForm)
      }
      
      if (response.success) {
        ElMessage.success(roleForm.id ? '更新成功' : '新增成功')
        dialogVisible.value = false
        loadRoleList()
      } else {
        ElMessage.error(response.message || (roleForm.id ? '更新失败' : '新增失败'))
      }
    } catch (error) {
      console.error('保存角色失败:', error)
      ElMessage.error(roleForm.id ? '更新失败' : '新增失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 分配权限
const handleAssignPermissions = async (row) => {
  currentRole.value = row
  permissionDialogVisible.value = true
  
  try {
    checkedPermissions.value = []
    permissionTree.value = []

    // 获取所有权限
    const permissionsRes = await getAllPermissions()
    if (!permissionsRes.success) {
      ElMessage.error(permissionsRes.message || '获取权限列表失败')
      return
    }
    permissionTree.value = permissionsRes.data || []
    
    // 获取角色已有权限
    const rolePermissionsRes = await getRolePermissions(row.id)
    if (!rolePermissionsRes.success) {
      ElMessage.error(rolePermissionsRes.message || '获取角色权限失败')
      return
    }
    checkedPermissions.value = rolePermissionsRes.data || []
    await nextTick()
    permissionTreeRef.value?.setCheckedKeys(checkedPermissions.value, false)
  } catch (error) {
    console.error('加载权限数据失败:', error)
    ElMessage.error('加载权限数据失败')
  }
}

// 提交权限分配
const handleSubmitPermissions = async () => {
  permissionSubmitLoading.value = true
  try {
    const checkedKeys = permissionTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
    const allKeys = [...checkedKeys, ...halfCheckedKeys]
    
    const response = await updateRolePermissions({
      roleId: currentRole.value.id,
      permissionIds: allKeys
    })
    
    if (response.success) {
      await userStore.ensureUserInfoReady(true)
      ElMessage.success('权限分配成功')
      permissionDialogVisible.value = false
    } else {
      ElMessage.error(response.message || '权限分配失败')
    }
  } catch (error) {
    console.error('权限分配失败:', error)
    ElMessage.error('权限分配失败')
  } finally {
    permissionSubmitLoading.value = false
  }
}

// 对话框关闭
const handleDialogClose = () => {
  roleFormRef.value.resetFields()
}

const handlePermissionDialogClose = () => {
  currentRole.value = null
  permissionTree.value = []
  checkedPermissions.value = []
  permissionTreeRef.value?.setCheckedKeys([], false)
}

// 格式化日期
const formatDate = (timestamp) => {
  return timestamp ? moment(timestamp).format('YYYY-MM-DD HH:mm:ss') : ''
}

// 初始化加载
onMounted(() => {
  loadRoleList()
})
</script>

<style scoped>
/* 表格样式 优化 */
.role-table :deep(.el-table__header th) {
  background-color: #f0f9ff !important;
  border-bottom: 2px solid #3b82f6 !important;
}

.role-table :deep(.el-table__body tr:hover > td) {
  background-color: #f0f9ff !important;
}

.role-table :deep(.el-table__row) {
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
.permission-tree {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
}

:deep(.el-tree-node__content) {
  height: 36px;
}
</style>
