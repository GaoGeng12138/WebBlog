<template>
    <div class="min-h-screen bg-[#F5F7FA] pb-20">
        <div class="bg-white/80 backdrop-blur-md shadow-sm sticky top-0 z-50 border-b border-gray-100">
            <div class="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
                <div class="flex items-center cursor-pointer group" @click="goBack">
                    <div
                        class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center group-hover:bg-blue-50 text-gray-500 group-hover:text-blue-600 transition-colors mr-3">
                        <el-icon>
                            <ArrowLeft />
                        </el-icon>
                    </div>
                    <span
                        class="text-sm font-medium text-gray-600 group-hover:text-blue-600 transition-colors">返回</span>
                </div>
                <div class="text-xs text-gray-400 font-mono bg-gray-50 px-2 py-1 rounded">{{ isEdit ? 'EDIT MODE' :
                    'CREATE MODE' }}</div>
            </div>
        </div>

        <div class="max-w-6xl mx-auto px-4 py-8">
            <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 md:p-8">

                <div
                    class="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8 border-b border-gray-100 pb-6">
                    <div>
                        <h1 class="text-2xl font-bold text-gray-800">{{ isEdit ? '编辑文章' : '撰写新文章' }}</h1>
                    </div>
                    <div class="bg-gray-100 p-1 rounded-lg flex items-center">
                        <button v-for="type in ['markdown', 'richtext']" :key="type"
                            @click="handleManualTypeChange(type)"
                            class="px-4 py-2 text-sm font-medium rounded-md transition-all duration-200"
                            :class="form.editorType === type ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'">
                            {{ type === 'markdown' ? 'Markdown' : '富文本' }}
                        </button>
                    </div>
                </div>

                <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">

                    <el-form-item prop="title" class="mb-8">
                        <input v-model="form.title" placeholder="请输入文章标题..."
                            class="w-full text-3xl font-bold border-none outline-none placeholder-gray-300 border-b border-gray-100 focus:border-gray-100 bg-transparent py-4 transition-colors" />
                    </el-form-item>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mb-8">
                        <el-form-item label="文章分类" prop="categoryId">
                            <el-select v-model="form.categoryId" placeholder="选择分类" class="w-full">
                                <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="文章标签" prop="tags">
                            <el-select v-model="form.tags" multiple filterable placeholder="选择标签" class="w-full">
                                <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
                            </el-select>
                        </el-form-item>
                    </div>

                    <el-form-item label="文章摘要" prop="summary">
                        <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="编写一段引人入胜的摘要（选填）"
                            maxlength="200" show-word-limit />
                    </el-form-item>

                    <el-form-item label="文章封面" prop="cover">
                        <el-upload class="w-full" :show-file-list="false" :on-change="handleCoverChange"
                        :auto-upload="false" :before-upload="beforeUpload" accept="image/*">
                            <div v-if="form.cover"
                                class="relative group w-full md:w-80 h-48 rounded-xl overflow-hidden border border-gray-200 cursor-pointer">
                                <img :src="form.cover" class="w-full h-full object-cover" />
                                <div
                                    class="absolute inset-0 bg-black/40 flex flex-col items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity text-white">
                                    <el-icon class="text-2xl mb-2">
                                        <Picture />
                                    </el-icon>
                                    <span class="text-sm font-medium">点击更换封面</span>
                                </div>
                            </div>
                            <div v-else
                                class="w-full md:w-80 h-48 border-2 border-dashed border-gray-200 rounded-xl flex flex-col items-center justify-center hover:border-blue-400 hover:bg-blue-50/50 transition-all cursor-pointer bg-gray-50 text-gray-400 hover:text-blue-500">
                                <el-icon class="text-3xl mb-3">
                                    <Picture />
                                </el-icon>
                                <span class="text-sm font-medium">点击上传封面图</span>
                            </div>
                        </el-upload>
                    </el-form-item>
                    <el-form-item label="正文内容" prop="content" class="mt-8">

                        <div v-if="form.editorType === 'markdown'"
                            class="w-full bg-white rounded-xl border border-gray-200 shadow-sm relative z-0">
                            <MdEditor v-model="form.content" :theme="isDark ? 'dark' : 'light'"
                                placeholder="开始使用 Markdown 写作..." @onUploadImg="onMdUploadImg" style="height: 650px;"
                                class="w-full rounded-xl overflow-hidden" :toolbarsExclude="['github']" />
                        </div>

                        <div v-else
                            class="w-full bg-white rounded-xl border border-gray-200 shadow-sm flex flex-col relative z-0 wang-editor-wrapper">
                            <Toolbar style="border-bottom: 1px solid #f1f5f9;" :editor="editorRef"
                                :defaultConfig="toolbarConfig" mode="default" class="bg-gray-50/50 rounded-t-xl" />
                            <div class="h-[600px] overflow-hidden rounded-b-xl relative pb-2">
                                <Editor style="height: 100%; overflow-y: hidden;" v-model="form.content"
                                    :defaultConfig="editorConfig" mode="default" @onCreated="handleCreated" />
                            </div>
                        </div>

                    </el-form-item>

                    <div class="flex justify-end items-center gap-4 mt-8 pt-8 border-t border-gray-100">
                        <span class="text-sm text-gray-400 mr-auto" v-if="form.content.length > 0">
                            当前模式: {{ form.editorType === 'markdown' ? 'Markdown' : '富文本' }}
                        </span>
                        <el-button size="large" @click="goBack" round>取消</el-button>
                        <el-button size="large" @click="resetForm" round>重置</el-button>
                        <el-button type="primary" size="large" round :loading="submitting" @click="onSubmit"
                            class="px-10 shadow-lg shadow-blue-500/20">
                            {{ isEdit ? '保存修改' : '立即发布' }}
                        </el-button>
                    </div>

                </el-form>
            </div>
        </div>
    </div>
</template>

<script setup>
// JS 部分保持不变，无需修改
import { ref, reactive, onMounted, shallowRef, onBeforeUnmount,computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Picture } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import { publishArticle, getArticleDetail, updateArticle } from '@/api/frontend/article'
import { getAllCategoryList } from '@/api/frontend/category'
import { getAllTagList } from '@/api/frontend/tag'
import { uploadFile } from '@/api/frontend/file'

import { MdEditor } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
const user = computed(() => userStore.frontendUserInfo)

const router = useRouter()
const route = useRoute()

const formRef = ref()
const isEdit = ref(false)
const submitting = ref(false)
const isDark = ref(false)

const categories = ref([])
const tags = ref([])

const form = reactive({
    title: '',
    cover: '',
    categoryId: null,
    tags: [],
    summary: '',
    content: '',
    userId:user.value.userId || null,
    editorType: 'markdown'
})

const editorRef = shallowRef()
const toolbarConfig = {}
const editorConfig = {
    placeholder: '请输入正文内容...',
    MENU_CONF: {
        uploadImage: {
            async customUpload(file, insertFn) {
                const reader = new FileReader()
                reader.onload = (e) => {
                    insertFn(e.target.result, 'image', e.target.result)
                }
                reader.readAsDataURL(file)
            }
        }
    }
}

const handleCreated = (editor) => {
    editorRef.value = editor
}

const onMdUploadImg = async (files, callback) => {
    const res = await Promise.all(
        files.map((file) => {
            return new Promise((resolve) => {
                const reader = new FileReader()
                reader.onload = (e) => resolve(e.target.result)
                reader.readAsDataURL(file)
            });
        })
    );
    callback(res);
};

const rules = {
    title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
    categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
    tags: [{ type: 'array', required: true, message: '请选择标签', trigger: 'change' }],
    cover: [{ required: true, message: '请上传封面图片', trigger: 'change' }],
    content: [{ required: true, message: '文章内容不能为空', trigger: 'blur' }]
}

const handleManualTypeChange = (type) => {
    if (form.editorType === type) return;
    if (form.content && form.content !== '<p><br></p>') {
        ElMessageBox.confirm(
            '切换编辑器可能会导致格式丢失。建议在空内容时切换。确认继续吗？',
            '提示',
            { confirmButtonText: '确定切换', cancelButtonText: '取消', type: 'warning' }
        ).then(() => { form.editorType = type }).catch(() => { })
    } else {
        form.editorType = type
    }
}

const loadCategoriesAndTags = async () => {
    try {
        const [catRes, tagRes] = await Promise.all([getAllCategoryList(), getAllTagList()])
        if (catRes?.success) categories.value = catRes.data || []
        if (tagRes?.success) tags.value = tagRes.data || []
    } catch (e) {
        console.error(e)
    }
}

const loadArticle = async (id) => {
    try {
        const res = await getArticleDetail(id)
        if (res && res.success && res.data) {
            const article = res.data
            Object.assign(form, {
                title: article.title || '',
                cover: article.cover || '',
                categoryId: article.categoryId || null,
                tags: article.tags || [],
                summary: article.summary || '',
                content: article.content || '',
                editorType: article.editorType || 'markdown'
            })
        }
    } catch (e) {
        console.error(e)
    }
}

const onSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate()

    if (form.editorType === 'richtext' && editorRef.value.isEmpty()) {
        ElMessage.warning('请输入文章内容')
        return
    }

    submitting.value = true
    try {
        const payload = { ...form }
        const api = isEdit.value && route.params.id ? updateArticle(route.params.id, payload) : publishArticle(payload)
        const res = await api

        if (res?.success) {
            ElMessage.success('操作成功')
            setTimeout(() => router.push('/user'), 1000)
        } else {
            ElMessage.error(res?.message || '失败')
        }
    } catch (e) {
        ElMessage.error('提交发生错误')
    } finally {
        submitting.value = false
    }
}

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
    // 表单对象
    let formData = new FormData()
    // 添加 file 字段，并将文件传入 
    formData.append('file', file.raw)
    uploadFile(formData).then((e) => {
        // 响参失败，提示错误消息
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'error')
            return
        }

        // 成功则设置表单对象中的封面链接，并提示上传成功
        form.cover = e.data.url
        showMessage('上传成功')
    })
}

const goBack = () => router.back()
const resetForm = () => formRef.value?.resetFields()

onMounted(() => {
    loadCategoriesAndTags()
    if (route.params.id) {
        isEdit.value = true
        loadArticle(route.params.id)
    }
})

onBeforeUnmount(() => {
    if (editorRef.value) editorRef.value.destroy()
})
</script>

<style scoped>
/* MdEditor 样式微调：去除默认边框 */
:deep(.md-editor) {
    border: none !important;
}

/* 修复 MdEditor 预览区样式 */
:deep(.md-editor-preview ul),
:deep(.md-editor-preview ol) {
    list-style: revert;
    margin: revert;
    padding: revert;
}

:deep(.md-editor-preview h1) {
    font-size: 2em;
    font-weight: bold;
    border-bottom: 1px solid #eaecef;
    padding-bottom: 0.3em;
}

:deep(.md-editor-preview h2) {
    font-size: 1.5em;
    font-weight: bold;
    border-bottom: 1px solid #eaecef;
    padding-bottom: 0.3em;
}

/* WangEditor 样式修复 */
.wang-editor-wrapper :deep(.w-e-text-container) {
    ul {
        list-style: disc inside;
        margin-bottom: 10px;
    }

    ol {
        list-style: decimal inside;
        margin-bottom: 10px;
    }

    h1 {
        font-size: 2em;
        font-weight: bold;
        margin: 0.67em 0;
    }

    h2 {
        font-size: 1.5em;
        font-weight: bold;
        margin: 0.75em 0;
    }

    p {
        margin-bottom: 10px;
        line-height: 1.6;
    }
}

/* 修复 Toolbar 圆角 */
.wang-editor-wrapper :deep(.w-e-toolbar) {
    border-radius: 12px 12px 0 0;
}

/* 全屏层级修复 */
:deep(.md-editor-fullscreen),
:deep(.w-e-full-screen-container) {
    z-index: 9999 !important;
}

input:focus {
    border-color: #3b82f6;
}

/* 强制修正 MdEditor 底部状态栏的高度和可见性 */
:deep(.md-editor-footer) {
    height: auto !important;
    padding: 4px 10px !important;
    border-top: 1px solid #f1f1f1;
    background-color: #fff;
}

/* 确保没有元素覆盖在底部 */
:deep(.md-editor) {
    z-index: 1; 
}
</style>