<template>
    <div class="admin-article-page p-4">
        <el-page-header :icon="ArrowLeft" title="返回" @back="goBack">
            <template #content>
                <span class="text-large font-600 mr-3"> {{ isEdit ? '编辑文章' : '发布文章' }} </span>
            </template>
        </el-page-header>

        <el-card shadow="never" class="admin-card mt-5">
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" size="large" class="admin-article-form">
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
                    <div class="flex items-center gap-3">
                        <el-tag type="info">{{ isEdit ? (form.articleSourceLabel || '后台发布') : '后台发布' }}</el-tag>
                        <el-tag :type="getStatusMeta(form.status).type">{{ getStatusMeta(form.status).text }}</el-tag>
                    </div>
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
                                <p class="admin-editor-shell__eyebrow">Backend Writing Desk</p>
                                <h3 class="admin-editor-shell__title">正文编辑器</h3>
                                <p class="admin-editor-shell__desc">{{ editorModeDetails.desc }}</p>
                            </div>
                            <span class="admin-editor-shell__badge">后台发布</span>
                        </div>
                        <div class="admin-editor-shell__toolbar">
                            <el-upload
                                :auto-upload="false"
                                :show-file-list="false"
                                accept=".doc,.docx"
                                :before-upload="beforeWordImport"
                                :on-change="handleWordImport"
                            >
                                <el-button :loading="wordImportLoading" class="admin-btn-secondary">
                                    导入 Word
                                </el-button>
                            </el-upload>
                            <el-text type="info" size="small">上传 doc / docx 文件后，会自动解析成 Markdown 并填充到正文。</el-text>
                        </div>
                        <ArticleDualModeEditor
                            v-model="form.content"
                            v-model:editorType="form.editorType"
                            variant="admin"
                            eyebrow="Backend Writing Desk"
                            editor-id="publishArticleEditor"
                            height="760px"
                            markdown-placeholder="请输入文章正文，建议用标题层级把内容结构整理清楚。"
                            :upload-handler="onUploadImg"
                        />
                    </div>
                </el-form-item>

                <!-- 提交按钮 -->
                <el-form-item>
                    <el-button @click="onSubmit('draft')" :loading="btnLoading && submitAction === 'draft'" class="admin-btn-secondary">
                        {{ isEdit ? '保存草稿' : '存为草稿' }}
                    </el-button>
                    <el-button type="primary" @click="onSubmit('publish')" :loading="btnLoading && submitAction === 'publish'" class="admin-btn-primary">
                        {{ isEdit ? '发布更新' : '发布文章' }}
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
import { parseWordFile, uploadFile } from '@/api/admin/file'
import { getTagSelectList } from '@/api/admin/tag'
import { getUserSelectList } from '@/api/admin/user'
import { useTagList } from '@/composables/useTagList'
import { showMessage } from '@/composables/util'
import { ArrowLeft, Loading, Plus } from '@element-plus/icons-vue'
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, shallowRef, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import MarkdownEditorSurface from '@/components/article/MarkdownEditorSurface.vue'
import ArticleDualModeEditor from '@/components/article/ArticleDualModeEditor.vue'
import MarkdownIt from 'markdown-it'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const { menuStore, activeTab, tabList, tabChange, removeTab, handleCloseTab } = useTagList()
const userStore = useUserStore()
const canConfigureVisibility = () => ['ROLE_ADMIN', 'ROLE_EDITOR'].some(role => userStore.userInfo?.roles?.includes(role))


// 判断是否为编辑模式
const isEdit = ref(false)
const articleId = ref(null)

const editorRef = shallowRef()
const coverUploading = ref(false)
const wordImportLoading = ref(false)
const localCoverPreviewUrl = ref('')

// 表单数据
const form = reactive({
    title: '',
    cover: '',
    categoryId: null,
    tagIds: [],
    summary: '',
    content: '',
    editorType: 'markdown',
    articleSource: 1,
    articleSourceLabel: '后台发布',
    status: 4,
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
    editorType: 'markdown',
    articleSource: 1,
    articleSourceLabel: '后台发布',
    status: 4,
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

const editorModeLabel = computed(() => (form.editorType === 'markdown' ? 'Markdown' : '富文本'))
const editorModeDetails = computed(() => editorModeMap[form.editorType] || editorModeMap.markdown)

const markdownToHtml = (markdown) => markdownRenderer.render(String(markdown || ''))

const convertNodeToMarkdown = (node, context = {}) => {
    const { listDepth = 0, inPre = false } = context

    if (node.nodeType === Node.TEXT_NODE) {
        const text = node.nodeValue || ''
        return inPre ? text : text.replace(/\s+/g, ' ')
    }

    if (node.nodeType !== Node.ELEMENT_NODE) return ''

    const tag = node.tagName.toLowerCase()
    const children = Array.from(node.childNodes || [])
    const renderChildren = (nextContext = context) => children.map((child) => convertNodeToMarkdown(child, nextContext)).join('')

    switch (tag) {
        case 'br':
            return '  \n'
        case 'strong':
        case 'b':
            return `**${renderChildren(context).trim()}**`
        case 'em':
        case 'i':
            return `*${renderChildren(context).trim()}*`
        case 'code':
            return inPre ? (node.textContent || '') : `\`${(node.textContent || '').trim()}\``
        case 'a': {
            const text = renderChildren(context).trim() || node.textContent || ''
            const href = node.getAttribute('href') || '#'
            return `[${text}](${href})`
        }
        case 'img': {
            const alt = node.getAttribute('alt') || ''
            const src = node.getAttribute('src') || ''
            return `![${alt}](${src})`
        }
        case 'h1':
        case 'h2':
        case 'h3':
        case 'h4':
        case 'h5':
        case 'h6': {
            const level = Number(tag.slice(1))
            return `${'#'.repeat(level)} ${renderChildren(context).trim()}\n\n`
        }
        case 'p':
            return `${renderChildren(context).trim()}\n\n`
        case 'blockquote': {
            const block = renderChildren(context).trim().split('\n').filter(Boolean).map((line) => `> ${line}`).join('\n')
            return `${block}\n\n`
        }
        case 'ul': {
            const prefix = '  '.repeat(listDepth)
            const items = Array.from(node.children || []).map((child) => convertNodeToMarkdown(child, { ...context, listDepth: listDepth + 1 })).filter(Boolean)
            return `${items.map((item) => `${prefix}${item}`).join('\n')}\n\n`
        }
        case 'ol': {
            const prefix = '  '.repeat(listDepth)
            const items = Array.from(node.children || []).map((child, index) => {
                const content = convertNodeToMarkdown(child, { ...context, listDepth: listDepth + 1 }).replace(/^\s*[-*]\s*/, '').trim()
                return `${prefix}${index + 1}. ${content}`
            })
            return `${items.join('\n')}\n\n`
        }
        case 'li': {
            const text = renderChildren({ ...context, listDepth }).trim().replace(/\n+/g, ' ')
            return `- ${text}`
        }
        case 'pre': {
            const code = node.querySelector('code')?.textContent || node.textContent || ''
            return `\`\`\`\n${code.replace(/\n+$/, '')}\n\`\`\`\n\n`
        }
        case 'hr':
            return `---\n\n`
        case 'table': {
            const rows = Array.from(node.querySelectorAll('tr'))
            if (!rows.length) return ''
            const matrix = rows.map((tr) => Array.from(tr.children).map((cell) => (cell.textContent || '').trim()))
            const header = matrix[0] || []
            const separator = header.map(() => '---')
            const body = matrix.slice(1)
            return [header, separator, ...body].map((row) => `| ${row.join(' | ')} |`).join('\n') + '\n\n'
        }
        case 'div':
        case 'section':
        case 'article':
        case 'span':
        case 'tbody':
        case 'thead':
        case 'tr':
        case 'td':
        case 'th':
            return renderChildren(context)
        default:
            return renderChildren(context)
    }
}

const htmlToMarkdown = (html) => {
    const value = String(html || '').trim()
    if (!value) return ''

    if (typeof window === 'undefined' || typeof DOMParser === 'undefined') {
        return value
    }

    try {
        const doc = new DOMParser().parseFromString(`<div id="markdown-root">${value}</div>`, 'text/html')
        const root = doc.getElementById('markdown-root')
        if (!root) return value
        return Array.from(root.childNodes).map((node) => convertNodeToMarkdown(node)).join('').replace(/\n{3,}/g, '\n\n').trim()
    } catch (error) {
        console.warn('HTML 转 Markdown 失败，保留原始内容:', error)
        return value
    }
}

const cloneContentForMode = (sourceType, sourceContent, targetType) => {
    if (sourceType === targetType) return String(sourceContent || '')
    return targetType === 'markdown'
        ? htmlToMarkdown(sourceContent)
        : markdownToHtml(sourceContent)
}

const editorModeCacheKey = (type) => (type === 'markdown' ? 'markdown' : 'richtext')

// 分类列表
const categories = ref([])
// 标签列表
const tags = ref([])
// 用户列表
const users = ref([])
// 标签搜索加载状态
const tagsLoading = ref(false)
const editorContentCache = reactive({
    markdown: '',
    richtext: ''
})
const toolbarConfig = {}
const editorConfig = {
    placeholder: '请输入文章正文...',
    MENU_CONF: {
        uploadImage: {
            async customUpload(file, insertFn) {
                const url = await uploadSingleEditorImage(file)
                insertFn(url, 'image', url)
            }
        }
    }
}

const markdownRenderer = new MarkdownIt({
    html: true,
    breaks: true,
    linkify: true,
    typographer: true
})

const editorModeMap = {
    markdown: {
        title: 'Markdown 快写模式',
        desc: '适合后台写长文、教程和结构化内容，输入效率最高。',
        hint: '适合技术类文章或需要快速排版的内容。切到富文本时，系统会尽量保留当前结构。',
        chips: ['标题层级', '代码块', '表格', '图片链接']
    },
    richtext: {
        title: '富文本可视化模式',
        desc: '适合后台协作编辑、图文混排和不想手敲语法的场景。',
        hint: '适合需要所见即所得的内容。切回 Markdown 时，系统会尽量保留标题和列表结构。',
        chips: ['所见即所得', '点击排版', '图片拖拽', '后台协作']
    }
}

const handleCreated = (editor) => {
    editorRef.value = editor
}

watch(
    () => [form.editorType, form.content],
    ([type, content]) => {
        editorContentCache[editorModeCacheKey(type)] = String(content || '')
    },
    { immediate: true }
)

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
        showMessage(error.message || '图片上传失败', 'error')
    }
}

// Word 文件导入前置校验
const beforeWordImport = (file) => {
    const extension = file.name?.split('.').pop()?.toLowerCase()
    const isWordFile = ['doc', 'docx'].includes(extension)
    const isLt10M = file.size / 1024 / 1024 < 10

    if (!isWordFile) {
        showMessage('只能导入 doc / docx 格式的 Word 文件', 'error')
    }
    if (!isLt10M) {
        showMessage('Word 文件大小不能超过 10MB', 'error')
    }
    return isWordFile && isLt10M
}

// 导入 Word 文件并填充正文
const handleWordImport = async (uploadFileInfo) => {
    const rawFile = uploadFileInfo?.raw
    if (!rawFile || !beforeWordImport(rawFile)) {
        return
    }

    wordImportLoading.value = true
    try {
        const formData = new FormData()
        formData.append('file', rawFile)
        const res = await parseWordFile(formData)
        if (!res?.success) {
            showMessage(res?.message || 'Word 文档解析失败', 'error')
            return
        }

        const markdownContent = res?.data?.content || ''
        if (!markdownContent) {
            showMessage('解析结果为空，请检查 Word 内容', 'warning')
            return
        }

        form.editorType = 'markdown'
        form.content = markdownContent
        if (!form.title && res?.data?.title) {
            form.title = res.data.title
        }
        showMessage('Word 文档已成功导入')
    } catch (error) {
        showMessage(error?.message || 'Word 文档导入失败', 'error')
    } finally {
        wordImportLoading.value = false
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

const handleManualTypeChange = (type) => {
    if (form.editorType === type) return

    const sourceType = form.editorType
    const sourceContent = String(form.content || '')
    const hasContent = sourceContent.trim() && sourceContent !== '<p><br></p>'
    const nextContent = editorContentCache[editorModeCacheKey(type)] || cloneContentForMode(sourceType, sourceContent, type)

    if (hasContent) {
        showMessage('切换编辑器时会尽量保留内容，复杂排版可能有轻微差异', 'warning')
    }

    if (sourceType === 'richtext' && editorRef.value) {
        editorRef.value.destroy()
        editorRef.value = null
    }

    editorContentCache[editorModeCacheKey(sourceType)] = sourceContent
    form.editorType = type
    form.content = nextContent
    editorContentCache[editorModeCacheKey(type)] = nextContent
}


// 组件销毁时，销毁编辑器
onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor != null) {
        editor.destroy()
    }

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
const submitAction = ref('publish')
const detailRequestSerial = ref(0)

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

const resetFormState = async () => {
    Object.assign(form, getDefaultFormState())
    editorContentCache.markdown = ''
    editorContentCache.richtext = ''
    if (editorRef.value) {
        editorRef.value.destroy()
        editorRef.value = null
    }

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
const buildSubmitData = (action) => ({
    title: form.title?.trim() || '',
    cover: form.cover?.startsWith('blob:') ? '' : form.cover,
    categoryId: form.categoryId,
    summary: form.summary,
    content: form.content,
    tags: form.tagIds,
    editorType: form.editorType,
    submitAction: action,
    visibilityScope: canConfigureVisibility() ? form.visibilityScope : 1,
    visibleUserIds: canConfigureVisibility() && form.visibilityScope === 2 ? form.visibleUserIds : []
})

const executeSubmit = (action) => {
    submitAction.value = action
    btnLoading.value = true

    const submitData = buildSubmitData(action)
    const apiCall = isEdit.value
        ? updateArticle({ ...submitData, id: articleId.value })
        : publishArticle(submitData)

    apiCall.then((res) => {
        if (res.success) {
            const successText = action === 'draft'
                ? (isEdit.value ? '草稿已保存' : '草稿已创建')
                : (isEdit.value ? '文章已发布' : '文章发布成功')
            showMessage(successText)
            removeTab(route.path, true)
            router.push('/admin/article/list')
        } else {
            showMessage(res.message, 'error')
        }
    }).finally(() => {
        btnLoading.value = false
    })
}

const onSubmit = (action = 'publish') => {
    if (action === 'draft') {
        executeSubmit(action)
        return
    }

    formRef.value.validate((valid) => {
        if (!valid) {
            return false
        }
        if (form.editorType === 'richtext' && editorRef.value?.isEmpty?.()) {
            showMessage('请输入文章内容', 'warning')
            return false
        }
        executeSubmit(action)
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
            const editorType = article.editorType || 'markdown'
            const articleContent = article.content || ''
            form.title = article.title || ''
            form.cover = article.cover
            form.categoryId = article.categoryId
            // 将标签 ID 数组转换为标签名称数组
            form.tagIds = article.tags ? article.tags.map(tag => tag.value) : []
            form.summary = article.summary
            form.content = articleContent
            form.editorType = editorType
            form.articleSource = article.articleSource || 1
            form.articleSourceLabel = article.articleSourceLabel || '后台发布'
            form.status = article.status ?? 4
            form.visibilityScope = article.visibilityScope || 1
            form.visibleUserIds = article.visibleUserIds || []
            editorContentCache[editorType] = articleContent
            editorContentCache[editorModeCacheKey(editorType === 'markdown' ? 'richtext' : 'markdown')] = cloneContentForMode(editorType, articleContent, editorType === 'markdown' ? 'richtext' : 'markdown')
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

.admin-editor-shell__desc {
    margin-top: 6px;
    max-width: 56rem;
    font-size: 13px;
    line-height: 1.7;
    color: #64748b;
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

.admin-editor-shell__body {
    display: grid;
    gap: 1rem;
    padding: 1rem;
}

.admin-editor-shell__toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 0.75rem;
    padding: 0.9rem 1.25rem;
    border-bottom: 1px solid rgba(226, 232, 240, 0.92);
    background: rgba(248, 250, 252, 0.72);
}

.admin-mode-switch {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0.75rem;
}

.admin-mode-switch__button {
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 0.2rem;
    min-height: 64px;
    border: 1px solid rgba(226, 232, 240, 0.95);
    border-radius: 18px;
    background: rgba(248, 250, 252, 0.96);
    color: #64748b;
    font-size: 0.92rem;
    font-weight: 700;
    transition: all 0.25s ease;
}

.admin-mode-switch__button--active {
    border-color: rgba(37, 99, 235, 0.3);
    background: linear-gradient(135deg, #eff6ff, #ffffff);
    color: #2563eb;
    box-shadow: 0 10px 24px rgba(37, 99, 235, 0.12);
}

.admin-mode-switch__button-title {
    font-size: 0.94rem;
    font-weight: 800;
}

.admin-mode-switch__button-subtitle {
    font-size: 0.72rem;
    line-height: 1.2;
    color: inherit;
    opacity: 0.82;
}

.admin-editor-shell__notice {
    border-radius: 20px;
    border: 1px solid rgba(226, 232, 240, 0.95);
    background:
        radial-gradient(circle at top right, rgba(239, 246, 255, 0.95), transparent 42%),
        linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.98));
    padding: 1rem 1.1rem;
}

.admin-editor-shell__notice-label {
    display: inline-flex;
    align-items: center;
    border-radius: 999px;
    background: rgba(15, 23, 42, 0.05);
    color: #64748b;
    padding: 0.28rem 0.6rem;
    font-size: 0.72rem;
    font-weight: 700;
    letter-spacing: 0.14em;
    text-transform: uppercase;
}

.admin-editor-shell__notice-title {
    display: block;
    margin-top: 0.55rem;
    font-size: 1.02rem;
    font-weight: 900;
    color: #0f172a;
}

.admin-editor-shell__notice-desc {
    margin-top: 0.35rem;
    font-size: 0.92rem;
    line-height: 1.7;
    color: #64748b;
}

.admin-rich-editor-shell {
    border-radius: 24px;
    border: 1px solid rgba(226, 232, 240, 0.95);
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.98));
    overflow: hidden;
    box-shadow: 0 18px 38px rgba(15, 23, 42, 0.06);
}

.admin-rich-editor-shell__meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
    padding: 1rem 1.15rem;
    border-bottom: 1px solid rgba(226, 232, 240, 0.92);
}

.admin-rich-editor-shell__badge {
    display: inline-flex;
    align-items: center;
    border-radius: 999px;
    background: linear-gradient(135deg, #2563eb, #0ea5e9);
    color: #fff;
    padding: 0.42rem 0.8rem;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

.admin-rich-editor-shell__hint {
    font-size: 12px;
    color: #64748b;
}

.admin-rich-editor-shell__toolbar {
    border-bottom: 1px solid rgba(226, 232, 240, 0.92);
}

.admin-rich-editor-shell__content {
    height: 760px;
    overflow: hidden;
}

@media (max-width: 768px) {
    .admin-article-page {
        padding: 0.75rem;
    }

    .admin-article-page :deep(.el-page-header) {
        flex-wrap: wrap;
        gap: 0.5rem;
    }

    .avatar-uploader .avatar,
    .el-icon.avatar-uploader-icon {
        width: 132px;
        height: 132px;
    }

    .admin-article-page :deep(.el-form-item__label) {
        width: auto !important;
        padding-right: 0 !important;
    }

    .admin-article-page :deep(.el-form-item__content) {
        display: block;
    }

    .admin-article-page :deep(.el-select) {
        width: 100% !important;
    }

    .admin-article-page :deep(.el-radio-group) {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem 1rem;
    }

    .admin-article-page :deep(.el-form-item__content > .flex) {
        flex-wrap: wrap;
    }

    .admin-editor-shell__meta {
        flex-direction: column;
        align-items: flex-start;
    }

    .admin-editor-shell__toolbar {
        flex-direction: column;
        align-items: flex-start;
    }

    .admin-editor-shell__title {
        font-size: 18px;
    }

    .admin-editor-shell :deep(.md-editor),
    .admin-editor-shell :deep(.md-editor__content) {
        min-height: 520px !important;
    }

    .admin-article-page :deep(.el-form-item:last-child .el-form-item__content) {
        display: flex;
        flex-wrap: wrap;
        gap: 0.75rem;
    }

    .admin-article-page :deep(.el-button) {
        width: 100%;
    }
}
</style>
