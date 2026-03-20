<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-6">
    <!-- 页面标题 -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900 flex items-center gap-2">
        <el-icon class="text-blue-600"><User /></el-icon>
        用户管理
      </h1>
      <p class="text-gray-500 mt-2 text-sm">管理系统中的所有用户，可以新增、编辑、删除用户以及更改用户状态</p>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="bg-white rounded-xl shadow-md border border-gray-200 p-5 mb-6 hover:shadow-lg transition-shadow duration-200">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div class="flex flex-col sm:flex-row gap-3 flex-1">
          <el-input
            v-model="searchForm.keyword"
            placeholder="🔍 搜索用户名或昵称"
            clearable
            @keyup.enter="handleSearch"
            class="md:w-72"
            prefix-icon="Search"
          />
          <el-select
            v-model="searchForm.isEnabled"
            placeholder="用户状态"
            clearable
            class="md:w-40"
            @change="handleSearch"
          >
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </div>
        <div class="flex gap-2">
          <el-button type="primary" @click="handleAddUser" size="default">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
          <el-button @click="handleReset">
            重置
          </el-button>
        </div>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="w-full bg-white rounded-xl shadow-md border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow duration-200">
      <el-table
        :data="userList"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
        :default-sort="{ prop: 'id', order: 'descending' }"
        class="user-table"
        header-cell-class-name="bg-gradient-to-r from-blue-50 to-blue-100 font-semibold text-gray-800 border-b-2 border-blue-200"
      >
        <el-table-column type="selection" width="50" fixed="left" />
        <el-table-column prop="id" label="ID" width="60" fixed="left" />
        <el-table-column prop="username" label="用户名" width="110" />
        <el-table-column prop="nickname" label="昵称" width="110" />
        <el-table-column prop="email" label="邮箱" min-width="150" />
        <el-table-column prop="roles" label="角色" width="140">
          <template #default="scope">
            <div class="flex flex-wrap gap-1">
              <template v-if="scope.row.roles && scope.row.roles.length > 0">
                <el-tag 
                  v-for="roleName in scope.row.roles" 
                  :key="roleName"
                  :type="getRoleTagType(roleName)"
                  size="small"
                >
                  {{ getRoleDisplayName(roleName) }}
                </el-tag>
              </template>
              <el-tag v-else type="info" size="small">普通用户</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="isEnabled" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.isEnabled ? 'success' : 'danger'" size="small">
              {{ scope.row.isEnabled ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="140">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="360" fixed="right">
          <template #default="scope">
            <div class="flex flex-wrap gap-1">
              <el-button
                size="small"
                type="primary"
                @click="handleEdit(scope.row)"
              >
                编辑
              </el-button>
              <el-button
                size="small"
                type="info"
                @click="handleAuthorize(scope.row)"
              >
                授权
              </el-button>
              <el-button
                size="small"
                type="warning"
                @click="handleChangePassword(scope.row)"
              >
                修改密码
              </el-button>
              <el-button
                size="small"
                :type="scope.row.isEnabled ? 'warning' : 'success'"
                @click="handleChangeStatus(scope.row)"
              >
                {{ scope.row.isEnabled ? '停用' : '启用' }}
              </el-button>
              <el-button
                size="small"
                type="danger"
                @click="handleDelete(scope.row)"
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

    <!-- 新增/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userFormRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
          />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input
            v-model="userForm.nickname"
            placeholder="请输入昵称"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="userForm.email"
            placeholder="请输入邮箱"
          />
        </el-form-item>
        <el-form-item v-if="!userForm.id" label="密码" prop="password">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item v-if="!userForm.id" label="确认密码" prop="confirmPassword">
          <el-input
            v-model="userForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="介绍" prop="introduction">
          <el-input
            v-model="userForm.introduction"
            type="textarea"
            :rows="3"
            placeholder="请输入用户介绍"
          />
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <div class="flex items-center gap-4">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :on-change="handleAvatarChange"
              :auto-upload="false"
              :before-upload="beforeAvatarUpload"
              :disabled="avatarUploading"
            >
              <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar-preview" />
              <el-icon v-else class="avatar-uploader-icon">
                <Plus />
              </el-icon>
            </el-upload>
            <div class="text-sm text-gray-500 leading-6">
              <div>点击上传头像</div>
              <div>支持 jpg、png，大小不超过 2MB</div>
              <div v-if="avatarUploading" class="text-blue-500">头像上传中...</div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="Github" prop="githubUrl">
          <el-input
            v-model="userForm.githubUrl"
            placeholder="https://github.com/username"
          />
        </el-form-item>
        <el-form-item label="Twitter" prop="twitterUrl">
          <el-input
            v-model="userForm.twitterUrl"
            placeholder="https://twitter.com/username"
          />
        </el-form-item>
        <el-form-item label="微博" prop="weiboUrl">
          <el-input
            v-model="userForm.weiboUrl"
            placeholder="https://weibo.com/username"
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="userForm.roleIds" class="flex flex-col gap-2">
            <div v-for="role in allRoles" :key="role.id" class="flex items-center">
              <el-checkbox :label="role.id">
                <span class="ml-2">{{ role.name }}</span>
              </el-checkbox>
            </div>
            <div v-if="allRoles.length === 0" class="text-gray-400 text-sm">
              暂无角色
            </div>
          </el-checkbox-group>
          <el-text type="info" size="small" class="mt-2 block">未选择时默认为普通用户</el-text>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="userForm.isEnabled"
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

    <!-- 用户授权对话框 -->
    <el-dialog
      v-model="authDialogVisible"
      title="用户授权"
      width="500px"
      @close="handleAuthDialogClose"
    >
      <div class="mb-4">
        <el-alert
          title="选择下列角色为此用户授予权限"
          type="info"
          :closable="false"
          show-icon
        >
          <p>用户：<strong>{{ currentUser?.username }}</strong> ({{ currentUser?.nickname }})</p>
        </el-alert>
      </div>

      <div class="auth-form">
        <div class="mb-4">
          <p class="text-sm font-medium text-gray-900 mb-3">不选择任何角色为普通用户</p>
          <el-scrollbar height="300px" class="border border-gray-200 rounded-lg p-3">
            <el-checkbox-group v-model="authForm.roleIds" class="flex flex-col gap-3">
              <div v-for="role in availableRoles" :key="role.id" class="flex items-center">
                <el-checkbox :label="role.id" class="flex-1">
                  <template #default>
                    <div class="flex items-center justify-between flex-1 ml-2">
                      <span>
                        <strong>{{ role.name }}</strong>
                      </span>
                      <el-tag type="info" size="small">{{ role.description }}</el-tag>
                    </div>
                  </template>
                </el-checkbox>
              </div>
              <div v-if="availableRoles.length === 0" class="text-center text-gray-400 py-4">
                暂无可用角色
              </div>
            </el-checkbox-group>
          </el-scrollbar>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="authDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmitAuth"
            :loading="authSubmitLoading"
          >
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="450px"
      @close="handlePasswordDialogClose"
    >
      <div class="mb-4">
        <el-alert
          title="管理员修改用户密码"
          type="warning"
          :closable="false"
          show-icon
        >
          <p>用户：<strong>{{ currentUser?.username }}</strong> ({{ currentUser?.nickname }})</p>
        </el-alert>
      </div>

      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordFormRules"
        label-width="80px"
      >
        <el-form-item label="新密码" prop="password">
          <el-input
            v-model="passwordForm.password"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmitPassword"
            :loading="passwordSubmitLoading"
          >
            修改
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getUserList,
  addUser,
  updateUser,
  deleteUser,
  updateUserStatus
} from '@/api/admin/user'
import { uploadFile } from '@/api/admin/file'
import { getRoleList,assignRole } from '@/api/admin/role'
import { Search, Plus, User, DocumentCopy } from '@element-plus/icons-vue'
import moment from 'moment'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  isEnabled: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

// 数据
const userList = ref([])
const total = ref(0)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const avatarUploading = ref(false)

// 授权对话框
const authDialogVisible = ref(false)
const authSubmitLoading = ref(false)
const currentUser = ref(null)
const availableRoles = ref([])
const allRoles = ref([])  // 所有角色，用于表单中显示

// 授权表单
const authForm = reactive({
  userId: null,
  roleIds: []
})

// 修改密码对话框
const passwordDialogVisible = ref(false)
const passwordSubmitLoading = ref(false)
const passwordFormRef = ref()

// 修改密码表单
const passwordForm = reactive({
  password: '',
  confirmPassword: ''
})

// 修改密码表单验证规则
const passwordFormRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 表单引用
const userFormRef = ref()

// 用户表单
const userForm = reactive({
  id: '',
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: '',
  introduction: '',
  avatar: '',
  githubUrl: '',
  twitterUrl: '',
  weiboUrl: '',
  roleIds: [],
  isEnabled: true
})

// 表单验证规则
const userFormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度为2-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== userForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 加载用户列表
const loadUserList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.currentPage,
      size: pagination.pageSize,
      ...searchForm
    }
    
    const response = await getUserList(params)
    if (response.success) {
      userList.value = response.data.records || response.data.list || response.data
      total.value = response.total || 0
    } else {
      ElMessage.error(response.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadUserList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.isEnabled = ''
  pagination.currentPage = 1
  loadUserList()
}

// 分页变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadUserList()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadUserList()
}

// 新增用户
const handleAddUser = () => {
  dialogTitle.value = '新增用户'
  dialogVisible.value = true
  // 重置表单
  Object.assign(userForm, {
    id: null,
    username: '',
    nickname: '',
    email: '',
    password: '',
    confirmPassword: '',
    introduction: '',
    avatar: '',
    githubUrl: '',
    twitterUrl: '',
    weiboUrl: '',
    roleIds: [],
    isEnabled: true
  })
  // 加载所有角色（如果沒有加载）
loadAllRoles()
}

// 编辑用户
const handleEdit = async (row) => {
  await loadAllRoles()
  dialogTitle.value = '编辑用户'
  dialogVisible.value = true
  // 填充表单数据
  Object.assign(userForm, {
    id: row.id,
    username: row.username,
    nickname: row.nickname,
    email: row.email,
    password: '',
    confirmPassword: '',
    introduction: row.introduction || '',
    avatar: row.avatar || '',
    githubUrl: row.githubUrl || '',
    twitterUrl: row.twitterUrl || '',
    weiboUrl: row.weiboUrl || '',
    roleIds: Array.isArray(row.roles)
      ? allRoles.value.filter(role => row.roles.includes(role.name)).map(role => role.id)
      : [],
    isEnabled: row.isEnabled
  })
}

// 加载所有角色
const loadAllRoles = async () => {
  if (allRoles.value.length > 0) return  // 已加载则不再加载
  try {
    const response = await getRoleList({ current: 1, size: 100 })
    if (response.success) {
      allRoles.value = response.data || []
    }
  } catch (error) {
    console.error('加载角色列表失败:', error)
  }
}

// 授权功能
const handleAuthorize = async (row) => {
  currentUser.value = row
  authDialogVisible.value = true
  authForm.userId = row.id
  // 根据用户的角色名称找到对应的角色ID
  authForm.roleIds = []
  if (Array.isArray(row.roles) && row.roles.length > 0) {
    // 先加载所有角色，然后找到匹配的ID
    const response = await getRoleList({ current: 1, size: 100 })
    if (response.success) {
      availableRoles.value = response.data || []
      // 找到用户当前角色对应的ID
      authForm.roleIds = availableRoles.value
        .filter(role => row.roles.includes(role.name))
        .map(role => role.id)
    }
  } else {
    // 没有角色时，直接加载角色列表
    const response = await getRoleList({ current: 1, size: 100 })
    if (response.success) {
      availableRoles.value = response.data || []
    }
  }
}

// 提交用户授权
const handleSubmitAuth = async () => {
  authSubmitLoading.value = true
  try {
    const response = await assignRole({ userId: authForm.userId, roleIds: authForm.roleIds })
    if (response.success) {
      ElMessage.success('授权成功')
      authDialogVisible.value = false
      loadUserList()
    } else {
      ElMessage.error(response.message || '授权失败')
    }
  } catch (error) {
    console.error('授权失败:', error)
    ElMessage.error('授权失败')
  } finally {
    authSubmitLoading.value = false
  }
}

// 授权对话框关闭
const handleAuthDialogClose = () => {
  currentUser.value = null
  availableRoles.value = []
  authForm.userId = null
  authForm.roleIds = []
}

// 修改密码
const handleChangePassword = (row) => {
  currentUser.value = row
  passwordDialogVisible.value = true
  // 重置密码表单
  Object.assign(passwordForm, {
    password: '',
    confirmPassword: ''
  })
}

// 提交修改密码
const handleSubmitPassword = () => {
  passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    passwordSubmitLoading.value = true
    try {
      // 使用更新用户信息接口（需要传id和新密码）
      const updateData = {
        id: currentUser.value.id,
        password: passwordForm.password
      }
      const response = await updateUser(updateData)
      if (response.success) {
        ElMessage.success('密码修改成功')
        passwordDialogVisible.value = false
        loadUserList()
      } else {
        ElMessage.error(response.message || '密码修改失败')
      }
    } catch (error) {
      console.error('密码修改失败:', error)
      ElMessage.error('密码修改失败')
    } finally {
      passwordSubmitLoading.value = false
    }
  })
}

// 修改密码对话框关闭
const handlePasswordDialogClose = () => {
  passwordFormRef.value.resetFields()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除用户 "${row.username}" 吗？此操作不可恢复！`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await deleteUser(row.id)
      if (response.success) {
        ElMessage.success('删除成功')
        loadUserList()
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 更改用户状态
const handleChangeStatus = async (row) => {
  try {
    const newStatus = !row.isEnabled
    const action = newStatus ? '启用' : '停用'
    
    const response = await updateUserStatus({ id: row.id, isEnabled: newStatus })
    if (response.success) {
      ElMessage.success(`${action}成功`)
      // 更新本地数据
      row.isEnabled = newStatus
    } else {
      ElMessage.error(response.message || `${action}失败`)
    }
  } catch (error) {
    console.error('更改用户状态失败:', error)
    ElMessage.error('更改用户状态失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('头像图片只能是 JPG/PNG 格式')
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB')
  }

  return isImage && isLt2M
}

const handleAvatarChange = (file) => {
  if (!file?.raw || avatarUploading.value) {
    return
  }

  if (!beforeAvatarUpload(file.raw)) {
    return
  }

  const formData = new FormData()
  formData.append('file', file.raw)
  avatarUploading.value = true

  uploadFile(formData).then((res) => {
    if (res?.success && res?.data?.url) {
      userForm.avatar = res.data.url
      ElMessage.success('头像上传成功')
      return
    }
    ElMessage.error(res?.message || '头像上传失败')
  }).catch((error) => {
    console.error('头像上传失败:', error)
    ElMessage.error(error?.response?.data?.message || '头像上传失败')
  }).finally(() => {
    avatarUploading.value = false
  })
}

// 提交表单
const handleSubmit = () => {
  if (avatarUploading.value) {
    ElMessage.warning('头像上传中，请等待上传完成后再提交')
    return
  }

  userFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      let response
      if (userForm.id) {
        // 编辑用户 - 不传递 confirmPassword，包含 id
        const { confirmPassword, ...updateData } = userForm
        response = await updateUser(updateData)
      } else {
        // 新增用户 - 不传递 confirmPassword
        const { confirmPassword, ...addData } = userForm
        response = await addUser(addData)
      }
      
      if (response.success) {
        ElMessage.success(userForm.id ? '更新成功' : '新增成功')
        dialogVisible.value = false
        loadUserList()
      } else {
        ElMessage.error(response.message || (userForm.id ? '更新失败' : '新增失败'))
      }
    } catch (error) {
      console.error('保存用户失败:', error)
      ElMessage.error(userForm.id ? '更新失败' : '新增失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 对话框关闭
const handleDialogClose = () => {
  userFormRef.value.resetFields()
}

// 格式化日期
const formatDate = (timestamp) => {
  return timestamp ? moment(timestamp).format('YYYY-MM-DD HH:mm:ss') : ''
}

// 根据角色名称获取自定义显示名称
const getRoleDisplayName = (roleName) => {
  const roleDisplayMap = {
    'ROLE_ADMIN': '管理员',
    'ROLE_EDITOR': '编辑',
    'ROLE_VISITOR': '访客'
  }
  return roleDisplayMap[roleName] || roleName
}

// 根据角色名称获取自定义上下文类型
const getRoleTagType = (roleName) => {
  const roleTypeMap = {
    'ROLE_ADMIN': 'danger',
    'ROLE_EDITOR': 'success',
    'ROLE_VISITOR': 'warning'
  }
  return roleTypeMap[roleName] || 'info'
}

// 初始化加载
onMounted(() => {
  loadUserList()
  loadAllRoles()  // 置载所有角色
})
</script>

<style scoped>
/* 表格样式 优化 */
.user-table :deep(.el-table__header th) {
  background-color: #f0f9ff !important;
  border-bottom: 2px solid #3b82f6 !important;
}

.avatar-uploader :deep(.el-upload) {
  width: 96px;
  height: 96px;
  border: 1px dashed #cbd5e1;
  border-radius: 14px;
  overflow: hidden;
  background: linear-gradient(135deg, #f8fbff 0%, #eef4ff 100%);
  transition: all 0.25s ease;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #4f7cff;
  box-shadow: 0 8px 24px rgba(79, 124, 255, 0.14);
}

.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-uploader-icon {
  width: 96px;
  height: 96px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  color: #94a3b8;
}

.user-table :deep(.el-table__body tr:hover > td) {
  background-color: #f0f9ff !important;
}

.user-table :deep(.el-table__row) {
  transition: all 0.3s ease;
}

.user-table :deep(.el-table__row:hover) {
  box-shadow: inset 0 0 6px rgba(59, 130, 246, 0.1);
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

/* 选择框样式 */
:deep(.el-select__wrapper) {
  border-radius: 8px;
}

/* 分页样式 */
:deep(.el-pagination) {
  margin: 0;
}

/* 标签样式 优化 */
:deep(.el-tag) {
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 12px;
  font-weight: 500;
}

/* 弥框样式 */
:deep(.el-dialog__header) {
  border-bottom: 1px solid #e5e7eb;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  color: #111827;
}

/* 表单样式 */
:deep(.el-form-item__label) {
  color: #374151;
  font-weight: 500;
}

/* 警告框样式 */
:deep(.el-alert) {
  border-radius: 8px;
}

/* 一般方框样式 */
:deep(.el-drawer__header) {
  border-bottom: 1px solid #e5e7eb;
}
</style>
