<template>
  <div class="article-publish-page min-h-screen bg-[#f6f8fc] pb-24">
    <div class="sticky top-0 z-40 border-b border-slate-200/80 bg-white/88 backdrop-blur-xl">
      <div class="mx-auto flex max-w-[1600px] items-center justify-between gap-4 px-4 py-4 sm:px-6 lg:px-8">
        <div class="flex min-w-0 items-center gap-4">
          <button
            type="button"
            class="flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl border border-slate-200 bg-white text-slate-500 shadow-sm transition hover:-translate-y-0.5 hover:border-blue-200 hover:text-blue-600"
            @click="goBack"
          >
            <el-icon><ArrowLeft /></el-icon>
          </button>

          <div class="min-w-0">
            <div class="flex flex-wrap items-center gap-2 text-xs font-semibold uppercase tracking-[0.22em] text-slate-400">
              <span>Creator Workspace</span>
              <span class="rounded-full bg-slate-100 px-2 py-1 tracking-[0.14em] text-slate-500">
                {{ isEdit ? 'EDIT MODE' : 'CREATE MODE' }}
              </span>
            </div>
            <h1 class="mt-2 text-2xl font-black tracking-tight text-slate-900 sm:text-3xl">
              {{ isEdit ? '打磨这篇文章' : '开始一篇新作品' }}
            </h1>
            <p class="mt-1 text-sm text-slate-500">
              先存草稿，确认内容完整后再提交发布。前台与后台的 Markdown 体验现在保持一致。
            </p>
          </div>
        </div>

        <div class="hidden items-center gap-3 xl:flex">
          <div class="workspace-stat">
            <span class="workspace-stat__label">字数</span>
            <strong class="workspace-stat__value">{{ contentWordCount }}</strong>
          </div>
          <div class="workspace-stat">
            <span class="workspace-stat__label">阅读</span>
            <strong class="workspace-stat__value">{{ estimatedReadMinutes }} 分钟</strong>
          </div>
          <div class="workspace-stat">
            <span class="workspace-stat__label">模式</span>
            <strong class="workspace-stat__value">{{ editorModeLabel }}</strong>
          </div>
        </div>
      </div>
    </div>

    <div class="mx-auto max-w-[1600px] px-4 py-8 sm:px-6 lg:px-8">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="grid gap-8 xl:grid-cols-[380px_minmax(0,1fr)]">
        <aside class="space-y-6">
          <section class="editor-panel">
            <div class="editor-panel__header">
              <div>
                <p class="editor-panel__eyebrow">文章设置</p>
                <h2 class="editor-panel__title">基础信息</h2>
              </div>
              <div class="editor-pill">{{ editorModeLabel }}</div>
            </div>

            <el-form-item label="文章标题" class="mb-6">
              <input
                v-model="form.title"
                placeholder="给这篇文章起个名字，也可以留空"
                class="editor-title-input"
              />
              <p class="editor-help">标题可选。如果你想做更偏海报感或图片流的内容，也可以只展示封面与正文。</p>
            </el-form-item>

            <el-form-item label="文章分类" prop="categoryId" class="mb-5">
              <el-select v-model="form.categoryId" placeholder="选择一个分类" class="w-full" size="large">
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
              </el-select>
            </el-form-item>

            <el-form-item label="文章标签" prop="tags" class="mb-5">
              <el-select
                v-model="form.tags"
                multiple
                filterable
                placeholder="给文章贴上标签"
                class="w-full"
                size="large"
              >
                <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
              </el-select>
              <p class="editor-help">选择已有标签即可，后续内容筛选和聚合会更准确。</p>
            </el-form-item>

            <div class="mb-5 grid gap-4 sm:grid-cols-2">
              <div class="rounded-2xl border border-slate-200 bg-slate-50/80 p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">文章来源</p>
                <div class="mt-3 flex flex-wrap gap-2">
                  <div class="inline-flex rounded-full bg-white px-3 py-1.5 text-sm font-semibold text-slate-700 shadow-sm">
                    {{ isEdit ? (form.articleSourceLabel || '前台发布') : '前台发布' }}
                  </div>
                  <div class="inline-flex rounded-full px-3 py-1.5 text-sm font-semibold shadow-sm" :class="statusBadgeClass">
                    {{ currentStatusLabel }}
                  </div>
                </div>
              </div>

              <div class="rounded-2xl border border-slate-200 bg-slate-50/80 p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">编辑器模式</p>
                <div class="mt-3 flex rounded-2xl bg-white p-1 shadow-sm">
                  <button
                    v-for="type in ['markdown', 'richtext']"
                    :key="type"
                    type="button"
                    class="editor-mode-button"
                    :class="{ 'editor-mode-button--active': form.editorType === type }"
                    @click="handleManualTypeChange(type)"
                  >
                    {{ type === 'markdown' ? 'Markdown' : '富文本' }}
                  </button>
                </div>
              </div>
            </div>

            <el-form-item label="文章摘要" prop="summary" class="mb-0">
              <el-input
                v-model="form.summary"
                type="textarea"
                :rows="5"
                resize="none"
                maxlength="200"
                show-word-limit
                placeholder="用 2 到 4 句话总结文章亮点、场景和结论"
              />
            </el-form-item>
          </section>

          <section class="editor-panel">
            <div class="editor-panel__header">
              <div>
                <p class="editor-panel__eyebrow">封面区域</p>
                <h2 class="editor-panel__title">视觉入口</h2>
              </div>
            </div>

            <el-form-item label="文章封面" prop="cover" class="mb-0">
              <el-upload
                class="w-full"
                :show-file-list="false"
                :on-change="handleCoverChange"
                :auto-upload="false"
                :before-upload="beforeUpload"
                accept="image/*"
              >
                <div v-if="form.cover" class="cover-uploader cover-uploader--filled">
                  <img :src="form.cover" class="h-full w-full object-cover" />
                  <div class="cover-uploader__overlay">
                    <el-icon class="mb-2 text-2xl"><Picture /></el-icon>
                    <span>点击更换封面</span>
                  </div>
                </div>

                <div v-else class="cover-uploader">
                  <div class="cover-uploader__icon">
                    <el-icon><Picture /></el-icon>
                  </div>
                  <h3 class="text-lg font-bold text-slate-800">上传封面图</h3>
                  <p class="mt-2 text-sm leading-6 text-slate-500">建议比例 16:9，推荐 800 × 450，支持 JPG / PNG，大小不超过 2MB。</p>
                </div>
              </el-upload>
            </el-form-item>
          </section>

          <section class="editor-panel editor-panel--tips">
            <div class="editor-panel__header">
              <div>
                <p class="editor-panel__eyebrow">写作提示</p>
                <h2 class="editor-panel__title">发布前检查</h2>
              </div>
            </div>

            <ul class="space-y-3 text-sm leading-6 text-slate-600">
              <li class="editor-tip-item">
                <span class="editor-tip-item__dot"></span>
                封面、分类和正文是必填项，摘要建议写清楚受众和收获。
              </li>
              <li class="editor-tip-item">
                <span class="editor-tip-item__dot"></span>
                Markdown 更适合技术文章、教程和长文；富文本更适合轻量排版。
              </li>
              <li class="editor-tip-item">
                <span class="editor-tip-item__dot"></span>
                当前字数 {{ contentWordCount }}，预估阅读 {{ estimatedReadMinutes }} 分钟。
              </li>
            </ul>
          </section>
        </aside>

        <section class="space-y-6">
          <div class="editor-panel">
            <div class="editor-panel__header">
              <div>
                <p class="editor-panel__eyebrow">正文创作</p>
                <h2 class="editor-panel__title">内容编辑区</h2>
              </div>
              <div class="flex flex-wrap items-center gap-2">
                <span class="editor-outline-chip">{{ form.title?.trim() || '未命名文章' }}</span>
                <span class="editor-outline-chip">{{ form.summary?.trim() ? '已写摘要' : '摘要待补充' }}</span>
              </div>
            </div>

            <div class="mb-6 grid gap-4 md:grid-cols-3">
              <div class="editor-metric-card">
                <span class="editor-metric-card__label">正文字符</span>
                <strong class="editor-metric-card__value">{{ contentWordCount }}</strong>
              </div>
              <div class="editor-metric-card">
                <span class="editor-metric-card__label">摘要字符</span>
                <strong class="editor-metric-card__value">{{ summaryWordCount }}</strong>
              </div>
              <div class="editor-metric-card">
                <span class="editor-metric-card__label">当前模式</span>
                <strong class="editor-metric-card__value">{{ editorModeLabel }}</strong>
              </div>
            </div>

            <el-form-item label="正文内容" prop="content" class="mb-0">
              <div class="w-full">
                <MarkdownEditorSurface
                  v-if="form.editorType === 'markdown'"
                  v-model="form.content"
                  editor-id="frontend-article-editor"
                  height="780px"
                  placeholder="从场景、问题、步骤、结果开始写。支持粘贴截图、代码块、表格和标题结构。"
                  :upload-handler="onMdUploadImg"
                />

                <div v-else class="rich-editor-shell">
                  <div class="rich-editor-shell__meta">
                    <div>
                      <p class="editor-panel__eyebrow">可视化排版</p>
                      <h3 class="text-lg font-bold text-slate-900">富文本编辑器</h3>
                    </div>
                    <span class="editor-pill editor-pill--soft">适合轻量图文</span>
                  </div>

                  <Toolbar
                    :editor="editorRef"
                    :defaultConfig="toolbarConfig"
                    mode="default"
                    class="rich-editor-shell__toolbar"
                  />
                  <div class="rich-editor-shell__content">
                    <Editor
                      v-model="form.content"
                      :defaultConfig="editorConfig"
                      mode="default"
                      style="height: 100%; overflow-y: hidden;"
                      @onCreated="handleCreated"
                    />
                  </div>
                </div>
              </div>
            </el-form-item>
          </div>

          <div class="editor-action-bar">
            <div>
              <p class="text-sm font-semibold text-slate-700">准备发布了吗？</p>
              <p class="mt-1 text-sm text-slate-500">{{ publishHint }}</p>
            </div>

            <div class="flex flex-wrap items-center justify-end gap-3">
              <el-button size="large" round @click="goBack">取消</el-button>
              <el-button size="large" round @click="resetForm">重置</el-button>
              <el-button
                size="large"
                round
                :loading="submitting && submitAction === 'draft'"
                @click="onSubmit('draft')"
              >
                {{ isEdit ? '保存草稿' : '存为草稿' }}
              </el-button>
              <el-button
                type="primary"
                size="large"
                round
                :loading="submitting && submitAction === 'publish'"
                class="!px-10 shadow-lg shadow-blue-500/20"
                @click="onSubmit('publish')"
              >
                {{ publishPrimaryLabel }}
              </el-button>
            </div>
          </div>
        </section>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Picture } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

import { publishArticle, getArticleDetail, updateArticle } from '@/api/frontend/article'
import { getAllCategoryList } from '@/api/frontend/category'
import { getAllTagList } from '@/api/frontend/tag'
import { uploadFile } from '@/api/frontend/file'
import { useUserStore } from '@/stores/user'
import { useSiteConfigStore } from '@/stores/siteConfig'
import MarkdownEditorSurface from '@/components/article/MarkdownEditorSurface.vue'

const userStore = useUserStore()
const siteConfig = useSiteConfigStore()
const user = computed(() => userStore.frontendUserInfo)

const router = useRouter()
const route = useRoute()

const formRef = ref()
const isEdit = ref(false)
const submitting = ref(false)
const submitAction = ref('publish')

const categories = ref([])
const tags = ref([])

const createDefaultForm = () => ({
  title: '',
  cover: '',
  categoryId: null,
  tags: [],
  summary: '',
  content: '',
  userId: user.value?.userId || null,
  editorType: 'markdown',
  articleSource: 2,
  articleSourceLabel: '前台发布',
  status: 3,
  visibilityScope: 1,
  visibleUserIds: []
})

const form = reactive(createDefaultForm())

const editorRef = shallowRef()
const toolbarConfig = {}
const editorConfig = {
  placeholder: '请输入正文内容...',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file, insertFn) {
        const url = await uploadSingleImage(file)
        insertFn(url, 'image', url)
      }
    }
  }
}

const rules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  cover: [{ required: true, message: '请上传封面图片', trigger: 'change' }],
  content: [{ required: true, message: '文章内容不能为空', trigger: 'blur' }]
}

const editorModeLabel = computed(() => (form.editorType === 'markdown' ? 'Markdown' : '富文本'))
const requiresReview = computed(() => siteConfig.permissions.articleReviewRequired === true)
const publishPrimaryLabel = computed(() => {
  if (isEdit.value) {
    return requiresReview.value ? '提交审核' : '发布修改'
  }
  return requiresReview.value ? '提交审核' : '立即发布'
})
const currentStatusLabel = computed(() => getStatusText(form.status))
const statusBadgeClass = computed(() => getStatusClass(form.status))

const contentWordCount = computed(() => {
  const text = String(form.content || '')
    .replace(/<[^>]+>/g, ' ')
    .replace(/[#>*`\-\[\]\(\)!]/g, ' ')
    .replace(/\s+/g, '')

  return text.length
})

const summaryWordCount = computed(() => String(form.summary || '').trim().length)

const estimatedReadMinutes = computed(() => Math.max(1, Math.ceil(contentWordCount.value / 450)))

const publishHint = computed(() => {
  if (!form.cover) return '先补一个封面图，哪怕先存草稿，后面回来看也更容易进入状态。'
  if (!form.categoryId) return '再选一个分类，方便归档和导航。'
  if (!String(form.content || '').trim()) return '正文还是空的，可以先存草稿，把结构搭起来。'
  return requiresReview.value
    ? '内容已经可提交，提交后会进入待审核队列。'
    : `当前内容完成度不错，可以${isEdit.value ? '直接发布修改' : '直接发布'}了。`
})

const getStatusText = (status) => {
  const map = {
    0: '待审核',
    1: '审核通过',
    2: '审核未通过',
    3: '草稿',
    4: '已发布'
  }
  return map[status] || '未知状态'
}

const getStatusClass = (status) => {
  const map = {
    0: 'bg-amber-50 text-amber-700',
    1: 'bg-blue-50 text-blue-700',
    2: 'bg-rose-50 text-rose-700',
    3: 'bg-slate-100 text-slate-600',
    4: 'bg-emerald-50 text-emerald-700'
  }
  return map[status] || 'bg-slate-100 text-slate-600'
}

const handleCreated = (editor) => {
  editorRef.value = editor
}

async function uploadSingleImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  const res = await uploadFile(formData)

  if (!res?.success || !res?.data?.url) {
    throw new Error(res?.message || '图片上传失败')
  }

  return res.data.url
}

const onMdUploadImg = async (files, callback) => {
  try {
    const urls = await Promise.all(files.map((file) => uploadSingleImage(file)))
    callback(urls)
  } catch (error) {
    console.error('Markdown 图片上传失败:', error)
    ElMessage.error(error.message || '图片上传失败')
  }
}

const handleManualTypeChange = (type) => {
  if (form.editorType === type) return

  const hasContent = String(form.content || '').trim() && form.content !== '<p><br></p>'

  if (hasContent) {
    ElMessageBox.confirm(
      '切换编辑器可能会造成部分排版格式丢失，确认继续切换吗？',
      '切换编辑器',
      {
        confirmButtonText: '继续切换',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      form.editorType = type
    }).catch(() => {})
    return
  }

  form.editorType = type
}

const loadCategoriesAndTags = async () => {
  try {
    const [catRes, tagRes] = await Promise.all([getAllCategoryList(), getAllTagList()])
    if (catRes?.success) categories.value = catRes.data || []
    if (tagRes?.success) tags.value = tagRes.data || []
  } catch (error) {
    console.error('加载分类或标签失败:', error)
  }
}

const loadArticle = async (id) => {
  try {
    const res = await getArticleDetail(Number(id), { editable: true })
    if (res?.success && res.data) {
      const article = res.data
      Object.assign(form, {
        title: article.title || '',
        cover: article.cover || '',
        categoryId: article.categoryId || article.category?.id || null,
        tags: Array.isArray(article.tags)
          ? article.tags.map((tag) => tag.id || tag.tagId || tag)
          : [],
        summary: article.summary || '',
        content: article.content || '',
        userId: article.userId || user.value?.userId || null,
        editorType: article.editorType || 'markdown',
        articleSource: article.articleSource || 2,
        articleSourceLabel: article.articleSourceLabel || '前台发布',
        status: article.status ?? 3,
        visibilityScope: article.visibilityScope || 1,
        visibleUserIds: article.visibleUserIds || []
      })
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
  }
}

const buildPayload = (action) => ({
  id: route.params.id ? Number(route.params.id) : undefined,
  title: form.title?.trim() || '',
  cover: form.cover,
  categoryId: form.categoryId,
  tags: form.tags,
  summary: form.summary,
  content: form.content,
  userId: form.userId || user.value?.userId || null,
  editorType: form.editorType,
  submitAction: action,
  visibilityScope: form.visibilityScope,
  visibleUserIds: form.visibleUserIds
})

const executeSubmit = async (action) => {
  submitAction.value = action
  submitting.value = true

  try {
    const payload = buildPayload(action)
    const res = isEdit.value && route.params.id
      ? await updateArticle(payload)
      : await publishArticle(payload)

    if (res?.success) {
      const successText = action === 'draft'
        ? (isEdit.value ? '草稿已保存' : '草稿已创建')
        : (requiresReview.value ? '文章已提交审核' : (isEdit.value ? '文章修改已发布' : '文章发布成功'))
      ElMessage.success(successText)
      setTimeout(() => router.push('/user'), 800)
    } else {
      ElMessage.error(res?.message || '提交失败')
    }
  } catch (error) {
    console.error('提交文章失败:', error)
    ElMessage.error(error?.response?.data?.message || '提交发生错误')
  } finally {
    submitting.value = false
  }
}

const onSubmit = async (action = 'publish') => {
  if (!formRef.value) return

  if (action !== 'draft') {
    await formRef.value.validate()

    if (form.editorType === 'richtext' && editorRef.value?.isEmpty?.()) {
      ElMessage.warning('请输入文章内容')
      return
    }
  }

  await executeSubmit(action)
}

const beforeUpload = (file) => {
  const isAllowed = ['image/jpeg', 'image/png', 'image/webp'].includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isAllowed) {
    ElMessage.error('封面图片只能是 JPG、PNG 或 WEBP 格式')
  }

  if (!isLt2M) {
    ElMessage.error('封面图片大小不能超过 2MB')
  }

  return isAllowed && isLt2M
}

const handleCoverChange = async (file) => {
  try {
    const url = await uploadSingleImage(file.raw)
    form.cover = url
    ElMessage.success('封面上传成功')
  } catch (error) {
    console.error('封面上传失败:', error)
    ElMessage.error(error.message || '封面上传失败')
  }
}

const goBack = () => router.back()

const resetForm = () => {
  const nextState = createDefaultForm()
  Object.assign(form, nextState)

  if (isEdit.value && route.params.id) {
    loadArticle(route.params.id)
  }

  formRef.value?.clearValidate?.()
}

onMounted(async () => {
  await siteConfig.ensureConfigReady().catch((error) => {
    console.error('获取站点配置失败:', error)
  })

  await userStore.setFrontendUserInfo().catch((error) => {
    console.error('获取用户信息失败:', error)
  })

  form.userId = user.value?.userId || null
  await loadCategoriesAndTags()

  if (route.params.id) {
    isEdit.value = true
    await loadArticle(route.params.id)
  }
})

onBeforeUnmount(() => {
  if (editorRef.value) {
    editorRef.value.destroy()
  }
})
</script>

<style scoped>
.workspace-stat {
  display: flex;
  min-width: 110px;
  flex-direction: column;
  gap: 0.4rem;
  border-radius: 1.25rem;
  border: 1px solid rgba(226, 232, 240, 0.92);
  background: rgba(255, 255, 255, 0.92);
  padding: 0.85rem 1rem;
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.05);
}

.workspace-stat__label {
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: #94a3b8;
}

.workspace-stat__value {
  font-size: 1rem;
  color: #0f172a;
}

.editor-panel {
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 28px;
  background:
    radial-gradient(circle at top left, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.98)),
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 1));
  padding: 1.5rem;
  box-shadow: 0 20px 48px rgba(15, 23, 42, 0.06);
}

.editor-panel--tips {
  background: linear-gradient(145deg, #fffaf0, #ffffff);
}

.editor-panel__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.25rem;
}

.editor-panel__eyebrow {
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: #94a3b8;
}

.editor-panel__title {
  margin-top: 0.35rem;
  font-size: 1.25rem;
  font-weight: 800;
  color: #0f172a;
}

.editor-pill {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  background: linear-gradient(135deg, #2563eb, #0ea5e9);
  color: white;
  padding: 0.45rem 0.85rem;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.editor-pill--soft {
  background: #eff6ff;
  color: #2563eb;
}

.editor-title-input {
  width: 100%;
  border: none;
  border-bottom: 1px solid rgba(226, 232, 240, 0.95);
  background: transparent;
  padding: 0.75rem 0 1rem;
  color: #0f172a;
  font-size: clamp(1.7rem, 2.3vw, 2.55rem);
  font-weight: 900;
  line-height: 1.2;
  outline: none;
}

.editor-title-input::placeholder {
  color: #cbd5e1;
}

.editor-help {
  margin-top: 0.65rem;
  font-size: 0.85rem;
  line-height: 1.6;
  color: #94a3b8;
}

.editor-mode-button {
  flex: 1;
  border: none;
  background: transparent;
  padding: 0.7rem 0.9rem;
  border-radius: 1rem;
  color: #64748b;
  font-size: 0.92rem;
  font-weight: 700;
  transition: all 0.25s ease;
}

.editor-mode-button--active {
  background: linear-gradient(135deg, #eff6ff, #ffffff);
  color: #2563eb;
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.12);
}

.cover-uploader {
  display: flex;
  min-height: 240px;
  width: 100%;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1.5px dashed rgba(148, 163, 184, 0.45);
  border-radius: 24px;
  background:
    radial-gradient(circle at top, rgba(59, 130, 246, 0.08), transparent 48%),
    linear-gradient(180deg, rgba(248, 250, 252, 0.9), rgba(255, 255, 255, 0.98));
  padding: 1.5rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cover-uploader:hover {
  border-color: rgba(37, 99, 235, 0.45);
  transform: translateY(-2px);
}

.cover-uploader--filled {
  position: relative;
  min-height: 250px;
  padding: 0;
  overflow: hidden;
}

.cover-uploader__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 3.5rem;
  height: 3.5rem;
  border-radius: 1.25rem;
  background: white;
  color: #2563eb;
  font-size: 1.4rem;
  box-shadow: 0 18px 32px rgba(37, 99, 235, 0.12);
  margin-bottom: 1rem;
}

.cover-uploader__overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.18), rgba(15, 23, 42, 0.55));
  color: white;
  opacity: 0;
  transition: opacity 0.25s ease;
}

.cover-uploader--filled:hover .cover-uploader__overlay {
  opacity: 1;
}

.editor-tip-item {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
}

.editor-tip-item__dot {
  width: 0.55rem;
  height: 0.55rem;
  margin-top: 0.55rem;
  border-radius: 999px;
  background: linear-gradient(135deg, #f59e0b, #f97316);
  box-shadow: 0 0 0 5px rgba(245, 158, 11, 0.14);
  flex-shrink: 0;
}

.editor-outline-chip {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  border: 1px solid rgba(226, 232, 240, 0.96);
  background: white;
  padding: 0.4rem 0.75rem;
  font-size: 0.78rem;
  font-weight: 600;
  color: #64748b;
}

.editor-metric-card {
  border-radius: 22px;
  border: 1px solid rgba(226, 232, 240, 0.9);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.95));
  padding: 1rem 1.1rem;
}

.editor-metric-card__label {
  display: block;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #94a3b8;
}

.editor-metric-card__value {
  display: block;
  margin-top: 0.5rem;
  font-size: 1.2rem;
  font-weight: 800;
  color: #0f172a;
}

.rich-editor-shell {
  overflow: hidden;
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 24px;
  background: white;
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.06);
}

.rich-editor-shell__meta {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  padding: 1rem 1.25rem;
  border-bottom: 1px solid rgba(226, 232, 240, 0.9);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.95));
}

.rich-editor-shell__toolbar {
  border-bottom: 1px solid rgba(226, 232, 240, 0.9);
  background: rgba(248, 250, 252, 0.88);
}

.rich-editor-shell__content {
  height: 780px;
  overflow: hidden;
}

.editor-action-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.96);
  padding: 1.25rem 1.4rem;
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.05);
}

:deep(.el-form-item__label) {
  font-weight: 700;
  color: #334155;
}

:deep(.el-select__wrapper),
:deep(.el-textarea__inner) {
  border-radius: 18px !important;
  box-shadow: none !important;
}

:deep(.el-textarea__inner) {
  min-height: 140px !important;
  padding-top: 0.9rem;
  line-height: 1.75;
}

:deep(.w-e-toolbar),
:deep(.w-e-text-container),
:deep(.w-e-scroll) {
  border: none !important;
}

:deep(.w-e-text-container [data-slate-editor]) {
  padding: 1.2rem 1.25rem 2rem !important;
}

@media (max-width: 1280px) {
  .editor-action-bar {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 768px) {
  .editor-panel {
    padding: 1.1rem;
    border-radius: 22px;
  }

  .rich-editor-shell__content {
    height: 620px;
  }
}
</style>
