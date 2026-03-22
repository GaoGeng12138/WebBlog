<template>
    <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-6">
        <!-- 页面标题 -->
        <div class="mb-8">
            <h1 class="text-3xl font-bold text-gray-900 flex items-center gap-2">
                <el-icon class="text-blue-600"><Setting /></el-icon>
                博客设置
            </h1>
            <p class="text-gray-500 mt-2 text-sm">配置博客基本信息和用户权限控制</p>
        </div>

        <!-- Tabs 标签页 -->
        <div class="bg-white rounded-xl shadow-md border border-gray-200 overflow-hidden">
            <el-tabs v-model="activeTab" class="blog-setting-tabs">
            <!-- 基本设置 -->
            <el-tab-pane label="基本设置" name="basic">
                <div class="p-6">
                    <el-form 
                        ref="formRef" 
                        :model="form" 
                        :rules="rules" 
                        label-width="140px" 
                        size="large"
                        class="max-w-3xl"
                    >
                        <el-divider content-position="left">
                            <span class="font-semibold text-gray-700">🌐 网站信息</span>
                        </el-divider>

                        <!-- 网站标题 -->
                        <el-form-item label="网站标题" prop="title">
                            <el-input 
                                v-model="form.title" 
                                placeholder="请输入网站标题" 
                                maxlength="50" 
                                show-word-limit 
                                clearable 
                                class="admin-input"
                            />
                        </el-form-item>

                        <!-- 网站描述 -->
                        <el-form-item label="网站描述" prop="description">
                            <el-input 
                                v-model="form.description" 
                                type="textarea" 
                                :rows="3" 
                                placeholder="请输入网站描述" 
                                maxlength="200" 
                                show-word-limit 
                                class="admin-input"
                            />
                        </el-form-item>

                        <el-divider content-position="left">
                            <span class="font-semibold text-gray-700">📸 网站资源</span>
                        </el-divider>

                        <el-form-item label="前台文章每页" prop="frontendArticlePageSize">
                            <div class="flex items-center gap-4">
                                <el-input-number 
                                    v-model="form.frontendArticlePageSize" 
                                    :min="4" 
                                    :max="60" 
                                    :step="4"
                                />
                                <el-text type="info" size="small">前台首页文章列表每页展示数量，仅展示页码分页</el-text>
                            </div>
                        </el-form-item>

                        <div class="mb-6 rounded-2xl border border-[rgba(148,176,231,0.18)] bg-[linear-gradient(180deg,rgba(248,251,255,0.95),rgba(241,247,254,0.88))] p-5">
                            <div class="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                                <div>
                                    <div class="text-base font-semibold text-slate-800">活跃度规则已独立管理</div>
                                    <div class="mt-1 text-sm text-slate-500">
                                        等级和积分规则已移动到「活跃度规则」标签页，可在表格中直接新增、编辑、删除。
                                    </div>
                                </div>
                                <el-button type="primary" plain @click="activeTab = 'activity'">去管理规则</el-button>
                            </div>
                        </div>
                        <!-- 网站图标上传 -->
                        <el-form-item label="网站Logo" prop="logoUrl">
                            <div class="flex items-center gap-6">
                                <el-upload
                                    class="logo-uploader"
                                    :show-file-list="false"
                                    :on-change="handleLogoChange"
                                    :auto-upload="false"
                                    :before-upload="beforeLogoUpload"
                                    :disabled="logoUploading"
                                >
                                    <img v-if="form.logoUrl" :src="form.logoUrl" class="logo" />
                                    <el-icon v-else class="logo-uploader-icon">
                                        <Plus />
                                    </el-icon>
                                </el-upload>
                                <div>
                                    <el-text class="mx-1" type="info" size="small">
                                        建议尺寸：200x200，支持 jpg、png 格式，大小不超过 2M
                                    </el-text>
                                    <div v-if="logoUploading" class="mt-2 text-sm text-blue-500">
                                        Logo 上传中...
                                    </div>
                                </div>
                            </div>
                        </el-form-item>

                        <el-divider content-position="left">
                            <span class="font-semibold text-gray-700">🔗 社交链接功能设置</span>
                        </el-divider>

                        <!-- GitHub 功能开关 -->
                        <el-form-item label="GitHub">
                            <div class="flex flex-col gap-3">
                                <div class="flex items-center space-x-4">
                                    <el-switch 
                                        v-model="form.githubEnabled" 
                                        active-text="启用" 
                                        inactive-text="关闭"
                                        @change="handleGithubEnabledChange"
                                    />
                                    <el-text type="info" size="small">启用后，用户可以在注册时填写和在前台展示 GitHub 链接</el-text>
                                </div>
                                <div v-if="form.githubEnabled" class="flex items-center space-x-4 ml-16">
                                    <el-checkbox v-model="form.githubShowFront">前台展示</el-checkbox>
                                    <el-checkbox v-model="form.githubShowRegister">注册页显示</el-checkbox>
                                </div>
                            </div>
                        </el-form-item>

                        <!-- Twitter 功能开关 -->
                        <el-form-item label="Twitter">
                            <div class="flex flex-col gap-3">
                                <div class="flex items-center space-x-4">
                                    <el-switch 
                                        v-model="form.twitterEnabled" 
                                        active-text="启用" 
                                        inactive-text="关闭"
                                        @change="handleTwitterEnabledChange"
                                    />
                                    <el-text type="info" size="small">启用后，用户可以在注册时填写和在前台展示 Twitter 链接</el-text>
                                </div>
                                <div v-if="form.twitterEnabled" class="flex items-center space-x-4 ml-16">
                                    <el-checkbox v-model="form.twitterShowFront">前台展示</el-checkbox>
                                    <el-checkbox v-model="form.twitterShowRegister">注册页显示</el-checkbox>
                                </div>
                            </div>
                        </el-form-item>

                        <!-- 微博功能开关 -->
                        <el-form-item label="微博">
                            <div class="flex flex-col gap-3">
                                <div class="flex items-center space-x-4">
                                    <el-switch 
                                        v-model="form.weiboEnabled" 
                                        active-text="启用" 
                                        inactive-text="关闭"
                                        @change="handleWeiboEnabledChange"
                                    />
                                    <el-text type="info" size="small">启用后，用户可以在注册时填写和在前台展示微博链接</el-text>
                                </div>
                                <div v-if="form.weiboEnabled" class="flex items-center space-x-4 ml-16">
                                    <el-checkbox v-model="form.weiboShowFront">前台展示</el-checkbox>
                                    <el-checkbox v-model="form.weiboShowRegister">注册页显示</el-checkbox>
                                </div>
                            </div>
                        </el-form-item>

                        <!-- 提交按钮 -->
                        <el-form-item>
                            <el-button 
                                v-if="can('admin:setting:update')"
                                type="primary" 
                                @click="onSubmitBasic"
                                :loading="basicBtnLoading"
                                size="large"
                            >
                                保存设置
                            </el-button>
                            <el-button @click="resetForm" size="large">重置</el-button>
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>

            <!-- 活跃度规则 -->
            <el-tab-pane label="活跃度规则" name="activity">
                <div class="p-6">
                    <div class="mb-6 flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
                        <div>
                            <h3 class="flex items-center gap-2 text-lg font-bold text-slate-900">
                                <el-icon class="text-[var(--theme-primary)]"><Link /></el-icon>
                                前台活跃度配置
                            </h3>
                            <p class="mt-2 text-sm text-slate-500">通过表格直接维护等级和积分规则，不再编辑 JSON 文本。</p>
                        </div>
                        <div class="flex gap-2">
                            <el-button @click="resetActivityRules" size="large">恢复默认</el-button>
                            <el-button
                                v-if="can('admin:setting:update')"
                                type="primary"
                                @click="onSubmitActivityRules"
                                :loading="activityBtnLoading"
                                size="large"
                                class="admin-btn-primary"
                            >
                                保存活跃度规则
                            </el-button>
                        </div>
                    </div>

                    <div class="grid gap-6 xl:grid-cols-2">
                        <div class="activity-panel">
                            <div class="activity-panel__head">
                                <div>
                                    <h4 class="activity-panel__title">活跃度等级</h4>
                                    <p class="activity-panel__desc">控制前台用户中心显示的 Lv 等级。</p>
                                </div>
                                <el-button type="primary" plain @click="addActivityLevelRule">新增等级</el-button>
                            </div>

                            <div class="activity-panel__body">
                                <el-table :data="activityLevelRules" border class="activity-table">
                                    <el-table-column label="等级" width="120" align="center">
                                        <template #default="{ row }">
                                            <el-input-number v-model="row.level" :min="1" :max="99" controls-position="right" />
                                        </template>
                                    </el-table-column>
                                    <el-table-column label="最低积分" width="160" align="center">
                                        <template #default="{ row }">
                                            <el-input-number v-model="row.minScore" :min="0" :step="10" controls-position="right" />
                                        </template>
                                    </el-table-column>
                                    <el-table-column label="等级名称" min-width="180">
                                        <template #default="{ row }">
                                            <el-input v-model="row.name" placeholder="例如：新手" />
                                        </template>
                                    </el-table-column>
                                    <el-table-column label="操作" width="100" align="center">
                                        <template #default="{ $index }">
                                            <el-button
                                                type="danger"
                                                link
                                                :disabled="activityLevelRules.length <= 1"
                                                @click="removeActivityLevelRule($index)"
                                            >
                                                删除
                                            </el-button>
                                        </template>
                                    </el-table-column>
                                </el-table>
                            </div>
                        </div>

                        <div class="activity-panel">
                            <div class="activity-panel__head">
                                <div>
                                    <h4 class="activity-panel__title">活跃度加分</h4>
                                    <p class="activity-panel__desc">控制发文章、评论、收藏、点赞、登录等行为积分。</p>
                                </div>
                                <el-button type="primary" plain @click="addActivityScoreRule">新增积分项</el-button>
                            </div>

                            <div class="activity-panel__body">
                                <el-table :data="activityScoreRules" border class="activity-table">
                                    <el-table-column label="行为类型" width="140" align="center">
                                        <template #default="{ row }">
                                            <el-select v-model="row.type" placeholder="选择类型">
                                                <el-option label="发布文章" value="article" />
                                                <el-option label="发表评论" value="comment" />
                                                <el-option label="收藏文章" value="favorite" />
                                                <el-option label="点赞评论" value="like" />
                                                <el-option label="每日登录" value="login" />
                                            </el-select>
                                        </template>
                                    </el-table-column>
                                    <el-table-column label="展示名称" min-width="180">
                                        <template #default="{ row }">
                                            <el-input v-model="row.name" placeholder="例如：发表评论" />
                                        </template>
                                    </el-table-column>
                                    <el-table-column label="积分" width="140" align="center">
                                        <template #default="{ row }">
                                            <el-input-number v-model="row.score" :min="0" :max="9999" controls-position="right" />
                                        </template>
                                    </el-table-column>
                                    <el-table-column label="操作" width="100" align="center">
                                        <template #default="{ $index }">
                                            <el-button
                                                type="danger"
                                                link
                                                :disabled="activityScoreRules.length <= 1"
                                                @click="removeActivityScoreRule($index)"
                                            >
                                                删除
                                            </el-button>
                                        </template>
                                    </el-table-column>
                                </el-table>
                            </div>
                        </div>
                    </div>
                </div>
            </el-tab-pane>

            <!-- 权限控制 -->
            <el-tab-pane label="权限控制" name="permissions">
                <div class="p-6">
                    <div class="mb-6">
                        <h3 class="text-lg font-bold text-gray-900 mb-2 flex items-center gap-2">
                            <el-icon class="text-blue-600"><Lock /></el-icon>
                            前台用户权限设置
                        </h3>
                        <p class="text-sm text-gray-500">控制前台用户可以执行的操作</p>
                    </div>
                    
                    <el-form 
                        ref="permissionFormRef" 
                        :model="permissionForm" 
                        label-width="160px" 
                        size="large"
                        class="max-w-3xl"
                    >
                        <!-- 评论功能 -->
                        <el-form-item label="允许评论">
                            <div class="flex items-center space-x-4">
                                <el-switch 
                                    v-model="permissionForm.enableComment" 
                                    :active-value="1"
                                    :inactive-value="0"
                                    active-text="开启" 
                                    inactive-text="关闭"
                                />
                                <el-text type="info" size="small">关闭后，前台用户将无法发表评论</el-text>
                            </div>
                        </el-form-item>

                        <!-- 点赞功能 -->
                        <el-form-item label="允许点赞">
                            <div class="flex items-center space-x-4">
                                <el-switch 
                                    v-model="permissionForm.enableLike" 
                                    :active-value="1"
                                    :inactive-value="0"
                                    active-text="开启" 
                                    inactive-text="关闭"
                                />
                                <el-text type="info" size="small">关闭后，前台用户将无法点赞文章</el-text>
                            </div>
                        </el-form-item>

                        <!-- 收藏功能 -->
                        <el-form-item label="允许收藏">
                            <div class="flex items-center space-x-4">
                                <el-switch 
                                    v-model="permissionForm.enableCollect" 
                                    :active-value="1"
                                    :inactive-value="0"
                                    active-text="开启" 
                                    inactive-text="关闭"
                                />
                                <el-text type="info" size="small">关闭后，前台用户将无法收藏文章</el-text>
                            </div>
                        </el-form-item>

                        <!-- 用户注册 -->
                        <el-form-item label="允许用户注册">
                            <div class="flex items-center space-x-4">
                                <el-switch 
                                    v-model="permissionForm.enableRegister" 
                                    :active-value="1"
                                    :inactive-value="0"
                                    active-text="开启" 
                                    inactive-text="关闭"
                                />
                                <el-text type="info" size="small">关闭后，新用户将无法注册账号</el-text>
                            </div>
                        </el-form-item>

                        <!-- 文章发布（前台） -->
                        <el-form-item label="允许用户发布文章">
                            <div class="flex items-center space-x-4">
                                <el-switch 
                                    v-model="permissionForm.enableUserPublish" 
                                    :active-value="1"
                                    :inactive-value="0"
                                    active-text="开启" 
                                    inactive-text="关闭"
                                />
                                <el-text type="info" size="small">关闭后，前台用户将无法发布文章</el-text>
                            </div>
                        </el-form-item>

                        <!-- 文章审核 -->
                        <el-form-item label="文章需要审核">
                            <div class="flex items-center space-x-4">
                                <el-switch 
                                    v-model="permissionForm.requireArticleApproval" 
                                    :active-value="1"
                                    :inactive-value="0"
                                    active-text="开启" 
                                    inactive-text="关闭"
                                />
                                <el-text type="info" size="small">开启后，用户发布的文章需要管理员审核后才能显示</el-text>
                            </div>
                        </el-form-item>

                        <!-- 评论审核 -->
                        <el-form-item label="评论需要审核">
                            <div class="flex items-center space-x-4">
                                <el-switch 
                                    v-model="permissionForm.requireCommentApproval" 
                                    :active-value="1"
                                    :inactive-value="0"
                                    active-text="开启" 
                                    inactive-text="关闭"
                                />
                                <el-text type="info" size="small">开启后，用户的评论需要管理员审核后才能显示</el-text>
                            </div>
                        </el-form-item>

                        <!-- 匿名评论 -->
                        <el-form-item label="允许匿名评论">
                            <div class="flex items-center space-x-4">
                                <el-switch 
                                    v-model="permissionForm.enableAnonymousComment" 
                                    :active-value="1"
                                    :inactive-value="0"
                                    active-text="开启" 
                                    inactive-text="关闭"
                                />
                                <el-text type="info" size="small">开启后，未登录用户也可以发表评论</el-text>
                            </div>
                        </el-form-item>

                        <!-- 提交按钮 -->
                        <el-form-item>
                            <el-button 
                                v-if="can('admin:setting:update')"
                                type="primary" 
                                @click="onSubmitPermissions" 
                                :loading="permissionBtnLoading"
                                size="large"
                                class="admin-btn-primary"
                            >
                                保存权限设置
                            </el-button>
                            <el-button @click="resetPermissionForm" size="large">重置</el-button>
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>
        </el-tabs>
        </div>
    </div>
</template>

<script setup>
import { getBlogSettings, updateBlogSettings } from '@/api/admin/blog'
import { uploadFile } from '@/api/admin/file'
import {
    DEFAULT_ACTIVITY_LEVEL_RULES,
    DEFAULT_ACTIVITY_SCORE_RULES
} from '@/composables/activityLevel'
import { hasAccess } from '@/composables/permission'
import { useUserStore } from '@/stores/user'
import { showMessage } from '@/composables/util'
import { Link, Plus, Setting, Lock } from '@element-plus/icons-vue'
import { onMounted, reactive, ref } from 'vue'

function cloneRules(rules) {
    return rules.map((item) => ({ ...item }))
}

function parseRuleArray(rawValue, fallbackRules) {
    if (rawValue === null || rawValue === undefined || rawValue === '') {
        return cloneRules(fallbackRules)
    }

    try {
        const parsed = typeof rawValue === 'string' ? JSON.parse(rawValue) : rawValue
        if (!Array.isArray(parsed) || parsed.length === 0) {
            return cloneRules(fallbackRules)
        }
        return parsed.map((item) => ({ ...item }))
    } catch (error) {
        return cloneRules(fallbackRules)
    }
}

function normalizeLevelRules(rules) {
    return rules
        .map((item, index) => ({
            level: Number(item.level) || index + 1,
            minScore: Number(item.minScore) || 0,
            name: String(item.name || '').trim() || `Lv.${index + 1}`
        }))
        .sort((a, b) => a.minScore - b.minScore)
}

function normalizeScoreRules(rules) {
    return rules
        .map((item) => ({
            type: String(item.type || '').trim(),
            name: String(item.name || '').trim(),
            score: Number(item.score) || 0
        }))
        .filter((item) => item.type)
}

const userStore = useUserStore()
const can = (permission) => hasAccess(userStore.userInfo, permission)

// 当前激活的标签页
const activeTab = ref('basic')

const activityLevelRules = ref(cloneRules(DEFAULT_ACTIVITY_LEVEL_RULES))
const activityScoreRules = ref(cloneRules(DEFAULT_ACTIVITY_SCORE_RULES))

// 基本设置表单数据
const form = reactive({
    title: '',
    description: '',
    logoUrl: '',
    githubEnabled: false,
    githubShowFront: false,
    githubShowRegister: false,
    twitterEnabled: false,
    twitterShowFront: false,
    twitterShowRegister: false,
    weiboEnabled: false,
    weiboShowFront: false,
    weiboShowRegister: false,
    frontendArticlePageSize: 12
})

// 权限控制表单数据
const permissionForm = reactive({
    enableComment: 1,           // 允许评论
    enableLike: 1,              // 允许点赞
    enableCollect: 1,           // 允许收藏
    enableRegister: 1,          // 允许用户注册
    enableUserPublish: 1,       // 允许用户发布文章
    requireArticleApproval: 0,  // 文章需要审核
    requireCommentApproval: 0,  // 评论需要审核
    enableAnonymousComment: 0   // 允许匿名评论
})

// 表单校验规则
const rules = {
    title: [
        { required: true, message: '请输入网站标题', trigger: 'blur' },
        { min: 1, max: 50, message: '网站标题长度在 1 到 50 个字符', trigger: 'blur' }
    ],
    description: [
        { max: 200, message: '网站描述长度不超过 200 个字符', trigger: 'blur' }
    ],
    frontendArticlePageSize: [
        { required: true, message: '请输入前台文章每页数量', trigger: 'blur' }
    ]
}

// 表单引用
const formRef = ref(null)
const permissionFormRef = ref(null)
const basicBtnLoading = ref(false)
const permissionBtnLoading = ref(false)
const activityBtnLoading = ref(false)
const logoUploading = ref(false)

// 上传文件前校验
const beforeLogoUpload = (file) => {
    const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isJPG) {
        showMessage('Logo图片只能是 JPG/PNG 格式!', 'error')
    }
    if (!isLt2M) {
        showMessage('Logo图片大小不能超过 2MB!', 'error')
    }
    return isJPG && isLt2M
}

// 上传Logo
const handleLogoChange = (file) => {
    if (!file?.raw) {
        return
    }

    if (!beforeLogoUpload(file.raw)) {
        return
    }

    let formData = new FormData()
    formData.append('file', file.raw)
    logoUploading.value = true
    uploadFile(formData).then((res) => {
        if (res.success) {
            form.logoUrl = res.data.url
            showMessage('上传成功')
        } else {
            showMessage(res.message || '上传失败', 'error')
        }
    }).catch(() => {
        showMessage('上传失败', 'error')
    }).finally(() => {
        logoUploading.value = false
    })
}

// GitHub 开关变化处理
const handleGithubEnabledChange = (value) => {
    if (value) {
        // 开启时，默认勾选前台展示和注册页显示
        form.githubShowFront = true
        form.githubShowRegister = true
    } else {
        // 关闭时，取消所有勾选
        form.githubShowFront = false
        form.githubShowRegister = false
    }
}

// Twitter 开关变化处理
const handleTwitterEnabledChange = (value) => {
    if (value) {
        form.twitterShowFront = true
        form.twitterShowRegister = true
    } else {
        form.twitterShowFront = false
        form.twitterShowRegister = false
    }
}

// 微博开关变化处理
const handleWeiboEnabledChange = (value) => {
    if (value) {
        form.weiboShowFront = true
        form.weiboShowRegister = true
    } else {
        form.weiboShowFront = false
        form.weiboShowRegister = false
    }
}

const buildSubmitData = () => ({
    title: form.title,
    description: form.description,
    logoUrl: form.logoUrl,
    frontendArticlePageSize: form.frontendArticlePageSize,
    activityLevelRules: JSON.stringify(normalizeLevelRules(activityLevelRules.value)),
    activityScoreRules: JSON.stringify(normalizeScoreRules(activityScoreRules.value)),
    githubEnabled: form.githubEnabled,
    githubShowFront: form.githubShowFront,
    githubShowRegister: form.githubShowRegister,
    twitterEnabled: form.twitterEnabled,
    twitterShowFront: form.twitterShowFront,
    twitterShowRegister: form.twitterShowRegister,
    weiboEnabled: form.weiboEnabled,
    weiboShowFront: form.weiboShowFront,
    weiboShowRegister: form.weiboShowRegister,
    commentEnabled: permissionForm.enableComment === 1,
    likeEnabled: permissionForm.enableLike === 1,
    favoriteEnabled: permissionForm.enableCollect === 1,
    userRegisterEnabled: permissionForm.enableRegister === 1,
    userPublishEnabled: permissionForm.enableUserPublish === 1,
    articleReviewRequired: permissionForm.requireArticleApproval === 1,
    commentReviewRequired: permissionForm.requireCommentApproval === 1,
    anonymousCommentEnabled: permissionForm.enableAnonymousComment === 1
})

const saveSettings = (successMessage) => {
    if (logoUploading.value) {
        showMessage('Logo 上传中，请等待上传完成后再保存', 'warning')
        return Promise.resolve(false)
    }

    return updateBlogSettings(buildSubmitData()).then((res) => {
        if (res.success) {
            showMessage(successMessage)
            return true
        }
        showMessage(res.message || '保存失败', 'error')
        return false
    })
}

// 保存基本设置
const onSubmitBasic = () => {
    if (!formRef.value) return

    formRef.value.validate((valid) => {
        if (!valid) {
            return false
        }

        basicBtnLoading.value = true
        saveSettings('保存成功').finally(() => {
            basicBtnLoading.value = false
        })
    })
}

// 保存活跃度规则
const onSubmitActivityRules = () => {
    activityBtnLoading.value = true
    saveSettings('活跃度规则保存成功').finally(() => {
        activityBtnLoading.value = false
    })
}

// 重置基本设置
const resetForm = () => {
    loadSettings()
}

// 加载设置
const loadSettings = () => {
    getBlogSettings().then((res) => {
        if (res.success && res.data) {
            // 加载基本设置
            Object.assign(form, {
                title: res.data.title || '',
                description: res.data.description || '',
                logoUrl: res.data.logoUrl || '',
                frontendArticlePageSize: res.data.frontendArticlePageSize || 12,
                githubShowFront: res.data.githubShowFront || false,
                githubShowRegister: res.data.githubShowRegister || false,
                twitterShowFront: res.data.twitterShowFront || false,
                twitterShowRegister: res.data.twitterShowRegister || false,
                weiboShowFront: res.data.weiboShowFront || false,
                weiboShowRegister: res.data.weiboShowRegister || false
            })

            activityLevelRules.value = normalizeLevelRules(
                parseRuleArray(res.data.activityLevelRules, DEFAULT_ACTIVITY_LEVEL_RULES)
            )
            activityScoreRules.value = normalizeScoreRules(
                parseRuleArray(res.data.activityScoreRules, DEFAULT_ACTIVITY_SCORE_RULES)
            )
            
            // 设置 Enabled 状态：优先使用后端返回的值，否则根据 ShowFront 或 ShowRegister 判断
            form.githubEnabled = res.data.githubEnabled !== undefined 
                ? res.data.githubEnabled 
                : (res.data.githubShowFront || res.data.githubShowRegister || false)
            form.twitterEnabled = res.data.twitterEnabled !== undefined 
                ? res.data.twitterEnabled 
                : (res.data.twitterShowFront || res.data.twitterShowRegister || false)
            form.weiboEnabled = res.data.weiboEnabled !== undefined 
                ? res.data.weiboEnabled 
                : (res.data.weiboShowFront || res.data.weiboShowRegister || false)
            
            // 加载权限设置（将 boolean 转换为 0/1）
            Object.assign(permissionForm, {
                enableComment: res.data.commentEnabled ? 1 : 0,
                enableLike: res.data.likeEnabled ? 1 : 0,
                enableCollect: res.data.favoriteEnabled ? 1 : 0,
                enableRegister: res.data.userRegisterEnabled ? 1 : 0,
                enableUserPublish: res.data.userPublishEnabled ? 1 : 0,
                requireArticleApproval: res.data.articleReviewRequired ? 1 : 0,
                requireCommentApproval: res.data.commentReviewRequired ? 1 : 0,
                enableAnonymousComment: res.data.anonymousCommentEnabled ? 1 : 0
            })
        }
    })
}

// 提交权限设置
const onSubmitPermissions = () => {
    permissionBtnLoading.value = true
    saveSettings('权限设置保存成功').finally(() => {
        permissionBtnLoading.value = false
    })
}

const addActivityLevelRule = () => {
    const nextLevel = activityLevelRules.value.length > 0
        ? Math.max(...activityLevelRules.value.map((item) => Number(item.level) || 0)) + 1
        : 1

    activityLevelRules.value.push({
        level: nextLevel,
        minScore: 0,
        name: `Lv.${nextLevel}`
    })
}

const removeActivityLevelRule = (index) => {
    if (activityLevelRules.value.length <= 1) {
        showMessage('至少保留一条等级规则', 'warning')
        return
    }
    activityLevelRules.value.splice(index, 1)
}

const addActivityScoreRule = () => {
    activityScoreRules.value.push({
        type: 'article',
        name: '发布文章',
        score: 0
    })
}

const removeActivityScoreRule = (index) => {
    if (activityScoreRules.value.length <= 1) {
        showMessage('至少保留一条积分规则', 'warning')
        return
    }
    activityScoreRules.value.splice(index, 1)
}

const resetActivityRules = () => {
    activityLevelRules.value = cloneRules(DEFAULT_ACTIVITY_LEVEL_RULES)
    activityScoreRules.value = cloneRules(DEFAULT_ACTIVITY_SCORE_RULES)
}

// 重置权限表单
const resetPermissionForm = () => {
    Object.assign(permissionForm, {
        enableComment: 1,
        enableLike: 1,
        enableCollect: 1,
        enableRegister: 1,
        enableUserPublish: 1,
        requireArticleApproval: 0,
        requireCommentApproval: 0,
        enableAnonymousComment: 0
    })
    loadSettings()
}

// 组件挂载时加载设置
onMounted(() => {
    loadSettings()
})
</script>

<style scoped>
/* Tabs 样式优化 */
.blog-setting-tabs :deep(.el-tabs__header) {
  background: linear-gradient(to right, #f0f9ff, #e0f2fe);
  margin: 0;
  padding: 0 24px;
  border-bottom: 2px solid #3b82f6;
}

.blog-setting-tabs :deep(.el-tabs__item) {
  font-weight: 500;
  font-size: 15px;
  color: #64748b;
  transition: all 0.3s ease;
}

.blog-setting-tabs :deep(.el-tabs__item.is-active) {
  color: #2563eb;
  font-weight: 600;
}

.blog-setting-tabs :deep(.el-tabs__item:hover) {
  color: #2563eb;
}

.blog-setting-tabs :deep(.el-tabs__active-bar) {
  background-color: #2563eb;
  height: 3px;
}

/* 按钮样式优化 */
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

/* Textarea 样式 */
:deep(.el-textarea__inner) {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

:deep(.el-textarea__inner:hover) {
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

:deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

/* Switch 样式 */
:deep(.el-switch) {
  --el-switch-on-color: #10b981;
  --el-switch-off-color: #94a3b8;
}

/* Divider 样式 */
:deep(.el-divider) {
  margin: 28px 0;
}

:deep(.el-divider__text) {
  font-weight: 600;
  font-size: 14px;
}

/* 活跃度规则配置区 */
.activity-panel {
    display: flex;
    flex-direction: column;
    min-height: 100%;
    border: 1px solid rgba(148, 163, 184, 0.18);
    border-radius: 18px;
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96));
    box-shadow: 0 16px 40px rgba(15, 23, 42, 0.06);
    overflow: hidden;
}

.activity-panel__head {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 16px;
    padding: 18px 20px 16px;
    border-bottom: 1px solid rgba(226, 232, 240, 0.9);
    background: linear-gradient(180deg, rgba(248, 250, 252, 0.92), rgba(255, 255, 255, 0.92));
}

.activity-panel__title {
    margin: 0;
    font-size: 15px;
    font-weight: 700;
    color: #0f172a;
}

.activity-panel__desc {
    margin: 6px 0 0;
    font-size: 12px;
    line-height: 1.6;
    color: #64748b;
}

.activity-panel__body {
    padding: 16px;
}

.activity-panel :deep(.activity-table) {
    width: 100%;
    border-radius: 14px;
    overflow: hidden;
}

.activity-panel :deep(.el-table) {
    --el-table-border-color: rgba(226, 232, 240, 0.95);
    --el-table-header-bg-color: #f8fafc;
    --el-table-tr-bg-color: #ffffff;
    --el-table-row-hover-bg-color: #f8fbff;
}

.activity-panel :deep(.el-table th.el-table__cell) {
    background: linear-gradient(180deg, #f8fafc, #f1f5f9);
    color: #334155;
    font-weight: 700;
}

.activity-panel :deep(.el-table .cell) {
    padding-left: 12px;
    padding-right: 12px;
}

.activity-panel :deep(.el-input),
.activity-panel :deep(.el-input-number),
.activity-panel :deep(.el-select) {
    width: 100%;
}

.activity-panel :deep(.el-input-number .el-input__wrapper) {
    padding-left: 10px;
    padding-right: 10px;
}

.activity-panel :deep(.el-button.is-link) {
    padding-left: 0;
    padding-right: 0;
    box-shadow: none;
    transform: none;
}

.activity-panel :deep(.el-button.is-link:hover) {
    box-shadow: none;
    transform: none;
}

.admin-btn-primary {
    box-shadow: 0 12px 24px rgba(37, 99, 235, 0.18);
}

.admin-btn-primary:hover {
    box-shadow: 0 16px 32px rgba(37, 99, 235, 0.22);
}

/* Logo 上传样式 */
.logo-uploader .logo {
    width: 120px;
    height: 120px;
    display: block;
    object-fit: contain;
}

.logo-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.logo-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.logo-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>

