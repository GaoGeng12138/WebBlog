<template>
    <div class="p-4">
        <el-page-header :icon="ArrowLeft" title="返回" @back="goBack">
            <template #content>
                <span class="text-large font-600 mr-3"> {{ isEdit ? '编辑文章' : '发布文章' }} </span>
            </template>
        </el-page-header>

        <el-card shadow="never" class="admin-card mt-5">
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" size="large">
                <!-- 文章标题 -->
                <el-form-item label="文章标题">
                    <el-input v-model="form.title" placeholder="请输入文章标题（选填）" maxlength="40" show-word-limit clearable class="admin-input" />
                    <el-text class="mx-1" type="info" size="small">可留空，仅展示封面与正文内容</el-text>
                </el-form-item>

                <!-- 文章封面 -->
                <el-form-item label="文章封面" prop="cover">
                    <el-upload class="avatar-uploader" :show-file-list="false" :on-change="handleCoverChange"
                        :auto-upload="false" :before-upload="beforeUpload">
                        <img v-if="form.cover" :src="form.cover" class="avatar" />
                        <div v-if="coverUploading" class="cover-uploading-mask">
                            <el-icon class="is-loading"><Loading /></el-icon>
                            <span>上传中...</span>
                        </div>
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                    <el-text class="mx-1" type="info" size="small">建议尺寸：800x450，支持 jpg、png 格式，大小不超过 2M</el-text>
                </el-form-item>

                <!-- 文章分类 -->
                <el-form-item label="文章分类" prop="categoryId">
                    <el-select v-model="form.categoryId" placeholder="请选择分类" clearable style="width: 240px" class="admin-input">
                        <el-option v-for="item in categories" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </el-form-item>

                <!-- 文章标签 -->
                <el-form-item label="文章标签" prop="tagIds">
                    <el-select v-model="form.tagIds" multiple filterable allow-create default-first-option remote
                        reserve-keyword placeholder="请选择或输入标签" :remote-method="remoteSearchTags" :loading="tagsLoading"
                        clearable style="width: 400px" class="admin-input">
                        <el-option v-for="item in tags" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                    <el-text class="mx-1" type="info" size="small">&nbsp;&nbsp; 可选择已有标签或输入新标签，支持模糊搜索</el-text>
                </el-form-item>

                <el-form-item label="文章来源">
                    <el-tag type="info">{{ isEdit ? (form.articleSourceLabel || '后台发布') : '后台发布' }}</el-tag>
                </el-form-item>

                <!-- 文章摘要 -->
                <el-form-item label="文章摘要" prop="summary">
                    <!-- :rows="3" 指定 textarea 默认显示 3 行 -->
                    <el-input v-model="form.summary" :rows="3" type="textarea" placeholder="请输入文章摘要" class="admin-input" />
                </el-form-item>

                <el-form-item v-if="canConfigureVisibility()" label="可见范围">
                    <el-radio-group v-model="form.visibilityScope">
                        <el-radio :label="1">公开</el-radio>
                        <el-radio :label="2">指定用户可见</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item v-if="canConfigureVisibility() && form.visibilityScope === 2" label="指定用户">
                    <el-select
                        v-model="form.visibleUserIds"
                        multiple
                        filterable
                        clearable
                        placeholder="请选择可查看的用户"
                        style="width: 400px"
                    >
                        <el-option v-for="item in users" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>

                <!-- 文章内容 -->
                <el-form-item label="文章内容" prop="content">
                    <div class="admin-editor-shell">
                        <div class="admin-editor-shell__meta">
                            <div>
                                <p class="admin-editor-shell__eyebrow">Markdown Workspace</p>
                                <h3 class="admin-editor-shell__title">正文编辑器</h3>
                            </div>
                            <span class="admin-editor-shell__badge">后台发布</span>
                        </div>
                        <MarkdownEditorSurface
                            v-model="form.content"
                            editor-id="publishArticleEditor"
                            height="760px"
                            placeholder="请输入文章正文，建议用标题层级把内容结构整理清楚。"
                            :upload-handler="onUploadImg"
                        />
                    </div>
                </el-form-item>

                <!-- 提交按钮 -->
                <el-form-item>
                    <el-button type="primary" @click="onSubmit" :loading="btnLoading" class="admin-btn-primary">
                        {{ isEdit ? '更新文章' : '发布文章' }}
                    </el-button>
                    <el-button @click="goBack" class="admin-btn-secondary">取消</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { getArticleDetail, publishArticle, updateArticle } from '@/api/admin/article'
import { getCategorySelectList } from '@/api/admin/category'
import { uploadFile } from '@/api/admin/file'
import { getTagSelectList } from '@/api/admin/tag'
import { getUserSelectList } from '@/api/admin/user'
import { useTagList } from '@/composables/useTagList'
import { showMessage } from '@/composables/util'
import { ArrowLeft, Loading, Plus } from '@element-plus/icons-vue'
import { nextTick, onBeforeUnmount, onMounted, reactive, ref, shallowRef, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import MarkdownEditorSurface from '@/components/article/MarkdownEditorSurface.vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const { menuStore, activeTab, tabList, tabChange, removeTab, handleCloseTab } = useTagList()
const userStore = useUserStore()
const canConfigureVisibility = () => ['ROLE_ADMIN', 'ROLE_EDITOR'].some(role => userStore.userInfo?.roles?.includes(role))


// 判断是否为编辑模式
const isEdit = ref(false)
const articleId = ref(null)

// 富文本编辑器实例
const editorRef = shallowRef()
const coverUploading = ref(false)
const localCoverPreviewUrl = ref('')

// 表单数据
const form = reactive({
    title: '',
    cover: '',
    categoryId: null,
    tagIds: [],
    summary: '',
    content: '',
    articleSource: 1,
    articleSourceLabel: '后台发布',
    visibilityScope: 1,
    visibleUserIds: []
})

const getDefaultFormState = () => ({
    title: '',
    cover: '',
    categoryId: null,
    tagIds: [],
    summary: '',
    content: '',
    articleSource: 1,
    articleSourceLabel: '后台发布',
    visibilityScope: 1,
    visibleUserIds: []
})

// 表单校验规则
const rules = {
    content: [
        { required: true, message: '请输入文章内容', trigger: 'blur' }
    ],
    cover: [
        { required: true, message: '请上传文章封面', trigger: 'blur' }
    ],
    categoryId: [
        { required: true, message: '请选择文章分类', trigger: 'blur' }
    ],
}

// 分类列表
const categories = ref([])
// 标签列表
const tags = ref([])
// 用户列表
const users = ref([])
// 标签搜索加载状态
const tagsLoading = ref(false)

// 获取分类列表
const getCategories = () => {
    getCategorySelectList().then((res) => {
        if (res.success) {
            categories.value = res.data
        }
    })
}

// 获取标签列表
const getTags = (name = '') => {
    tagsLoading.value = true
    getTagSelectList({ name }).then((res) => {
        if (res.success) {
            tags.value = res.data
        }
    }).finally(() => {
        tagsLoading.value = false
    })
}

// 远程搜索标签
const remoteSearchTags = (query) => {
    if (query) {
        getTags(query)
    } else {
        getTags()
    }
}

// 编辑器图片上传
const uploadSingleEditorImage = async (file) => {
    console.log('==> 编辑器开始上传文件...')
    const formData = new FormData()
    formData.append('file', file)

    const res = await uploadFile(formData)
    if (!res?.success || !res?.data?.url) {
        throw new Error(res?.message || '图片上传失败')
    }

    return res.data.url
}

const onUploadImg = async (files, callback) => {
    try {
        const urls = await Promise.all(files.map((file) => uploadSingleEditorImage(file)))
        callback(urls)
    } catch (error) {
        console.error('Markdown 图片上传失败:', error)
        showMessage(error.message || '图片上传失败', 'error')
    }
}

const getUsers = () => {
    if (!canConfigureVisibility()) return
    getUserSelectList().then((res) => {
        if (res.success) {
            users.value = res.data || []
        }
    })
}


// 组件销毁时，销毁编辑器
onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor == null) return
    editor.destroy()

    if (localCoverPreviewUrl.value) {
        URL.revokeObjectURL(localCoverPreviewUrl.value)
    }
})


// 上传文件前校验
const beforeUpload = (file) => {
    const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isJPG) {
        showMessage('封面图片只能是 JPG/PNG 格式!', 'error')
    }
    if (!isLt2M) {
        showMessage('封面图片大小不能超过 2MB!', 'error')
    }
    return isJPG && isLt2M
}

// 上传文章封面图片
const handleCoverChange = (file) => {
    if (!file?.raw || !beforeUpload(file.raw)) {
        return
    }

    if (localCoverPreviewUrl.value) {
        URL.revokeObjectURL(localCoverPreviewUrl.value)
    }

    const previewUrl = URL.createObjectURL(file.raw)
    localCoverPreviewUrl.value = previewUrl
    form.cover = previewUrl
    coverUploading.value = true

    // 表单对象
    let formData = new FormData()
    // 添加 file 字段，并将文件传入 
    formData.append('file', file.raw)
    uploadFile(formData).then((e) => {
        // 响参失败，提示错误消息
        if (e.success == false) {
            let message = e.message
            form.cover = ''
            showMessage(message, 'error')
            return
        }

        // 成功则设置表单对象中的封面链接，并提示上传成功
        form.cover = e.data.url
        showMessage('上传成功')
    }).catch(() => {
        form.cover = ''
        showMessage('上传失败', 'error')
    }).finally(() => {
        coverUploading.value = false
        if (localCoverPreviewUrl.value) {
            URL.revokeObjectURL(localCoverPreviewUrl.value)
            localCoverPreviewUrl.value = ''
        }
    })
}



// 表单引用
const formRef = ref(null)
const btnLoading = ref(false)
const detailRequestSerial = ref(0)

const resetFormState = async () => {
    Object.assign(form, getDefaultFormState())

    await nextTick()
    formRef.value?.clearValidate()
}

const syncPageState = async (id) => {
    await resetFormState()

    if (id) {
        isEdit.value = true
        articleId.value = id
        loadArticleDetail(id)
        return
    }

    isEdit.value = false
    articleId.value = null
}

// 提交表单
const onSubmit = () => {
    formRef.value.validate((valid) => {
        if (!valid) {
            return false
        }

        btnLoading.value = true
        // 将 tagIds 从标签名称数组转换为后端需要的格式
        const submitData = {
            title: form.title?.trim() || '',
            cover: form.cover,
            categoryId: form.categoryId,
            summary: form.summary,
            content: form.content,
            tags: form.tagIds, // 后端接收 tags 字段，为标签名称数组
            visibilityScope: canConfigureVisibility() ? form.visibilityScope : 1,
            visibleUserIds: canConfigureVisibility() && form.visibilityScope === 2 ? form.visibleUserIds : []
        }

        // 根据编辑或新增调用不同接口
        const apiCall = isEdit.value
            ? updateArticle({ ...submitData, id: articleId.value })
            : publishArticle(submitData)

        apiCall.then((res) => {
            if (res.success) {
                showMessage(isEdit.value ? '更新成功' : '发布成功')
                // 如果是发布文章/更新文章成功移除当前标签页
                removeTab(route.path, true)
                router.push('/admin/article/list')
            } else {
                showMessage(res.message, 'error')
            }
        }).finally(() => {
            btnLoading.value = false
        })
    })
}

// 返回
const goBack = () => {
    router.back()
}

// 页面加载时执行
onMounted(() => {
    // 获取分类和标签列表
    getCategories()
    getTags()
    getUsers()
    syncPageState(route.params.id)
})

watch(
    () => route.params.id,
    (newId, oldId) => {
        if (newId === oldId) return
        syncPageState(newId)
    }
)

// 加载文章详情
const loadArticleDetail = (id = articleId.value) => {
    const requestSerial = ++detailRequestSerial.value

    getArticleDetail(id).then((res) => {
        if (requestSerial !== detailRequestSerial.value || String(id) !== String(articleId.value)) {
            return
        }

        if (res.success) {
            const article = res.data
            form.title = article.title || ''
            form.cover = article.cover
            form.categoryId = article.categoryId
            // 将标签 ID 数组转换为标签名称数组
            form.tagIds = article.tags ? article.tags.map(tag => tag.value) : []
            form.summary = article.summary
            form.content = article.content
            form.articleSource = article.articleSource || 1
            form.articleSourceLabel = article.articleSourceLabel || '后台发布'
            form.visibilityScope = article.visibilityScope || 1
            form.visibleUserIds = article.visibleUserIds || []
        } else {
            showMessage('加载文章详情失败', 'error')
        }
    })
}
</script>

<style scoped>
.avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
    object-fit: cover;
}

.avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.cover-uploading-mask {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    background: rgba(255, 255, 255, 0.72);
    color: #475569;
    font-size: 13px;
    font-weight: 600;
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
}

.admin-editor-shell {
    width: 100%;
    border-radius: 24px;
    overflow: hidden;
    border: 1px solid rgba(226, 232, 240, 0.9);
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.96));
    box-shadow: 0 18px 38px rgba(15, 23, 42, 0.06);
}

.admin-editor-shell__meta {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 1rem;
    padding: 1rem 1.25rem;
    border-bottom: 1px solid rgba(226, 232, 240, 0.92);
}

.admin-editor-shell__eyebrow {
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 0.18em;
    text-transform: uppercase;
    color: #94a3b8;
}

.admin-editor-shell__title {
    margin-top: 6px;
    font-size: 20px;
    font-weight: 800;
    color: #0f172a;
}

.admin-editor-shell__badge {
    display: inline-flex;
    align-items: center;
    border-radius: 999px;
    background: linear-gradient(135deg, #2563eb, #0ea5e9);
    color: #fff;
    padding: 0.45rem 0.8rem;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}
</style>
