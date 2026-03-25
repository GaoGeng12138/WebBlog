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
              先存草稿，确认内容完整后再提交发布。Markdown 和富文本可以自由切换，系统会尽量帮你保留内容。
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

    <div class="mx-auto max-w-[1600px] px-4 py-6 sm:px-6 lg:px-8 lg:py-8">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div v-if="isMobileLayout" class="space-y-4 md:hidden">
          <section class="mobile-workspace-shell">
            <div class="mobile-workspace-shell__header">
              <div>
                <p class="mobile-workspace-shell__eyebrow">手机写作台</p>
                <h2 class="mobile-workspace-shell__title">{{ isEdit ? '编辑这篇文章' : '开始新文章' }}</h2>
                <p class="mobile-workspace-shell__desc">把发布流程拆成三步，减少手机上来回滚动和误触。</p>
              </div>

              <div class="mobile-workspace-shell__progress">
                <span class="mobile-workspace-shell__progress-label">进度</span>
                <strong>{{ mobilePublishStepIndex + 1 }}/3</strong>
              </div>
            </div>

            <div class="mobile-stepper">
              <button
                v-for="step in mobilePublishSteps"
                :key="step.key"
                type="button"
                class="mobile-stepper__item"
                :class="{ 'mobile-stepper__item--active': mobilePublishStep === step.key }"
                @click="mobilePublishStep = step.key"
              >
                <span class="mobile-stepper__index">{{ step.index }}</span>
                <span class="mobile-stepper__text">
                  <span class="mobile-stepper__label">{{ step.label }}</span>
                  <span class="mobile-stepper__hint">{{ step.hint }}</span>
                </span>
              </button>
            </div>
          </section>

          <section v-show="mobilePublishStep === 'info'" class="editor-panel mobile-section-shell mobile-section-shell--info space-y-5">
            <div class="editor-panel__header">
              <div>
                <p class="editor-panel__eyebrow">基础信息</p>
                <h2 class="editor-panel__title">先把文章骨架搭好</h2>
              </div>
              <div class="editor-pill">{{ editorModeLabel }}</div>
            </div>

            <el-form-item label="文章标题" class="mb-0">
              <input
                v-model="form.title"
                placeholder="给文章起一个清晰的标题"
                class="editor-title-input"
              />
            </el-form-item>

            <el-form-item label="文章分类" prop="categoryId" class="mb-0">
              <el-select v-model="form.categoryId" placeholder="选择一个分类" class="w-full" size="large">
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
              </el-select>
            </el-form-item>

            <el-form-item label="文章标签" prop="tags" class="mb-0">
              <el-select
                v-model="form.tags"
                multiple
                filterable
                placeholder="可搜索并选择标签"
                class="w-full"
                size="large"
              >
                <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
              </el-select>
            </el-form-item>

            <div class="mobile-info-grid">
              <div class="mobile-info-card">
                <p class="mobile-info-card__label">文章来源</p>
                <strong class="mobile-info-card__value">{{ isEdit ? (form.articleSourceLabel || '前台发布') : '前台发布' }}</strong>
              </div>
              <div class="mobile-info-card">
                <p class="mobile-info-card__label">当前状态</p>
                <strong class="mobile-info-card__value" :class="statusBadgeClass">{{ currentStatusLabel }}</strong>
              </div>
            </div>

            <div class="mode-switch-card mode-switch-card--mobile">
              <div class="mode-switch-card__head">
                <div>
                  <p class="editor-panel__eyebrow">编辑模式</p>
                  <h3 class="mode-switch-card__title">{{ editorModeDetails.title }}</h3>
                </div>
                <span class="mode-switch-card__badge">{{ editorModeLabel }}</span>
              </div>

              <div class="mobile-mode-switch">
                <button
                  v-for="type in ['markdown', 'richtext']"
                  :key="type"
                  type="button"
                  class="editor-mode-button"
                  :class="{ 'editor-mode-button--active': form.editorType === type }"
                  @click="handleManualTypeChange(type)"
                >
                  <span class="editor-mode-button__title">{{ type === 'markdown' ? 'Markdown' : '富文本' }}</span>
                  <span class="editor-mode-button__subtitle">
                    {{ type === 'markdown' ? '适合技术文章' : '适合图文排版' }}
                  </span>
                </button>
              </div>

              <p class="mode-switch-card__hint">{{ editorModeDetails.hint }}</p>
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

            <el-form-item label="文章封面" prop="cover" class="mb-0">
              <el-upload
                class="w-full"
                :show-file-list="false"
                :on-change="handleCoverChange"
                :auto-upload="false"
                :before-upload="beforeUpload"
                accept="image/*"
              >
                <div v-if="form.cover" class="cover-uploader cover-uploader--filled mobile-cover-uploader">
                  <img :src="form.cover" class="h-full w-full object-cover" />
                  <div class="cover-uploader__overlay">
                    <el-icon class="mb-2 text-2xl"><Picture /></el-icon>
                    <span>点击更换封面</span>
                  </div>
                </div>

                <div v-else class="cover-uploader mobile-cover-uploader">
                  <div class="cover-uploader__icon">
                    <el-icon><Picture /></el-icon>
                  </div>
                  <h3 class="text-lg font-bold text-slate-800">上传封面图</h3>
                  <p class="mt-2 text-sm leading-6 text-slate-500">建议比例 16:9，支持 JPG / PNG / WEBP，大小不超过 2MB。</p>
                </div>
              </el-upload>
            </el-form-item>

            <div class="mobile-step-actions">
              <el-button class="w-full" size="large" round @click="mobilePublishStep = 'content'">下一步，去写正文</el-button>
            </div>
          </section>

          <section v-show="mobilePublishStep === 'content'" class="editor-panel mobile-section-shell mobile-section-shell--content space-y-5">
            <div class="editor-panel__header">
              <div>
                <p class="editor-panel__eyebrow">正文创作</p>
                <h2 class="editor-panel__title">把内容写完整</h2>
              </div>
              <span class="editor-outline-chip">{{ contentWordCount }} 字</span>
            </div>

            <div class="mode-switch-card mode-switch-card--content">
              <div class="mode-switch-card__head">
                <div>
                  <p class="editor-panel__eyebrow">编辑模式</p>
                  <h3 class="mode-switch-card__title">{{ editorModeDetails.title }}</h3>
                </div>
                <span class="mode-switch-card__badge">{{ editorModeLabel }}</span>
              </div>

              <div class="mobile-mode-switch">
                <button
                  v-for="type in ['markdown', 'richtext']"
                  :key="`content-${type}`"
                  type="button"
                  class="editor-mode-button"
                  :class="{ 'editor-mode-button--active': form.editorType === type }"
                  @click="handleManualTypeChange(type)"
                >
                  <span class="editor-mode-button__title">{{ type === 'markdown' ? 'Markdown' : '富文本' }}</span>
                  <span class="editor-mode-button__subtitle">
                    {{ type === 'markdown' ? '适合技术文章' : '适合图文排版' }}
                  </span>
                </button>
              </div>

              <p class="mode-switch-card__hint">{{ editorModeDetails.hint }}</p>
            </div>

            <el-form-item label="正文内容" prop="content" class="mb-0">
              <ArticleDualModeEditor
                v-model="form.content"
                v-model:editorType="form.editorType"
                variant="frontend"
                eyebrow="正文创作"
                editor-id="frontend-article-editor-mobile"
                height="560px"
                markdown-placeholder="从问题、方案、步骤和结果开始写。手机端建议先写结构，再补图和代码。"
                :compact-mode="isMobileLayout"
                :upload-handler="onMdUploadImg"
              />
            </el-form-item>

            <div class="mobile-step-actions mobile-step-actions--split">
              <el-button size="large" round @click="mobilePublishStep = 'info'">上一步</el-button>
              <el-button size="large" round type="primary" @click="mobilePublishStep = 'publish'">去发布</el-button>
            </div>
          </section>

          <section v-show="mobilePublishStep === 'publish'" class="editor-panel mobile-section-shell mobile-section-shell--publish space-y-5">
            <div class="editor-panel__header">
              <div>
                <p class="editor-panel__eyebrow">发布确认</p>
                <h2 class="editor-panel__title">发布前再看一眼</h2>
              </div>
              <span class="editor-outline-chip">{{ estimatedReadMinutes }} 分钟阅读</span>
            </div>

            <div class="mobile-summary-grid">
              <div class="mobile-summary-card">
                <span class="mobile-summary-card__label">标题</span>
                <strong class="mobile-summary-card__value">{{ form.title?.trim() || '未命名文章' }}</strong>
              </div>
              <div class="mobile-summary-card">
                <span class="mobile-summary-card__label">分类</span>
                <strong class="mobile-summary-card__value">{{ categories.find((item) => item.id === form.categoryId)?.name || '未选择' }}</strong>
              </div>
              <div class="mobile-summary-card">
                <span class="mobile-summary-card__label">摘要</span>
                <strong class="mobile-summary-card__value">{{ form.summary?.trim() ? '已填写' : '待补充' }}</strong>
              </div>
              <div class="mobile-summary-card">
                <span class="mobile-summary-card__label">封面</span>
                <strong class="mobile-summary-card__value">{{ form.cover ? '已上传' : '未上传' }}</strong>
              </div>
            </div>

            <div class="mobile-publish-hint">
              {{ publishHint }}
            </div>

            <div class="mobile-step-actions mobile-step-actions--stack">
              <el-button size="large" round @click="mobilePublishStep = 'content'">返回正文</el-button>
              <el-button size="large" round @click="resetForm">重置草稿</el-button>
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
          </section>
        </div>

        <div v-else class="hidden md:grid gap-8 xl:grid-cols-[380px_minmax(0,1fr)]">
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
                <div class="mt-3 rounded-2xl bg-white p-1 shadow-sm">
                  <div class="grid grid-cols-2 gap-2">
                    <button
                      v-for="type in ['markdown', 'richtext']"
                      :key="type"
                      type="button"
                      class="editor-mode-button"
                      :class="{ 'editor-mode-button--active': form.editorType === type }"
                      @click="handleManualTypeChange(type)"
                    >
                      <span class="editor-mode-button__title">{{ type === 'markdown' ? 'Markdown' : '富文本' }}</span>
                      <span class="editor-mode-button__subtitle">
                        {{ type === 'markdown' ? '适合技术文章' : '适合图文排版' }}
                      </span>
                    </button>
                  </div>
                </div>
                <p class="mt-3 text-sm leading-6 text-slate-500">{{ editorModeDetails.hint }}</p>
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

            <div class="editor-mode-banner">
              <div class="editor-mode-banner__copy">
                <span class="editor-mode-banner__label">当前模式</span>
                <strong class="editor-mode-banner__title">{{ editorModeDetails.title }}</strong>
                <p class="editor-mode-banner__desc">{{ editorModeDetails.desc }}</p>
              </div>
              <div class="editor-mode-banner__chips">
                <span v-for="chip in editorModeDetails.chips" :key="`desktop-${chip}`" class="editor-mode-banner__chip">
                  {{ chip }}
                </span>
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
              <ArticleDualModeEditor
                v-model="form.content"
                v-model:editorType="form.editorType"
                variant="frontend"
                eyebrow="正文创作"
                editor-id="frontend-article-editor"
                height="780px"
                markdown-placeholder="从场景、问题、步骤、结果开始写。支持粘贴截图、代码块、表格和标题结构。"
                :upload-handler="onMdUploadImg"
              />
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
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Picture } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import MarkdownIt from 'markdown-it'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

import { publishArticle, getArticleDetail, updateArticle } from '@/api/frontend/article'
import { getAllCategoryList } from '@/api/frontend/category'
import { getAllTagList } from '@/api/frontend/tag'
import { uploadFile } from '@/api/frontend/file'
import { useUserStore } from '@/stores/user'
import { useSiteConfigStore } from '@/stores/siteConfig'
import ArticleDualModeEditor from '@/components/article/ArticleDualModeEditor.vue'
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
const isMobileLayout = ref(false)
const mobilePublishStep = ref('info')

const mobilePublishSteps = [
  { key: 'info', label: '基础信息', hint: '标题、分类、封面', index: '01' },
  { key: 'content', label: '正文编辑', hint: '写作与排版', index: '02' },
  { key: 'publish', label: '发布确认', hint: '草稿与发布', index: '03' }
]
let resizeHandler = null

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
const editorContentCache = reactive({
  markdown: '',
  richtext: ''
})

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

const markdownRenderer = new MarkdownIt({
  html: true,
  breaks: true,
  linkify: true,
  typographer: true
})

const editorModeMap = {
  markdown: {
    title: 'Markdown 快写模式',
    desc: '适合技术文章、教程和长内容，键盘输入效率最高。',
    hint: '如果你习惯先写结构再补细节，Markdown 会更顺手。切到富文本时，系统会尽量把当前内容转成可视化排版。',
    chips: ['标题层级', '代码块', '表格', '图片链接']
  },
  richtext: {
    title: '富文本可视化模式',
    desc: '适合图文混排、活动稿和不想记语法的普通创作者。',
    hint: '如果你更希望像写 Word 一样排版，可以直接用富文本。切回 Markdown 时，系统会尽量保留标题和列表结构。',
    chips: ['所见即所得', '点击式排版', '图片拖拽', '新手友好']
  }
}

const editorModeLabel = computed(() => (form.editorType === 'markdown' ? 'Markdown' : '富文本'))
const editorModeDetails = computed(() => editorModeMap[form.editorType] || editorModeMap.markdown)
const mobilePublishStepIndex = computed(() => {
  const index = mobilePublishSteps.findIndex((step) => step.key === mobilePublishStep.value)
  return index >= 0 ? index : 0
})
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

watch(
  () => [form.editorType, form.content],
  ([type, content]) => {
    editorContentCache[type] = String(content || '')
  },
  { immediate: true }
)

const syncPublishLayout = () => {
  if (typeof window === 'undefined') return
  isMobileLayout.value = window.innerWidth < 768
  if (!isMobileLayout.value) {
    mobilePublishStep.value = 'info'
  }
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

  const sourceType = form.editorType
  const sourceContent = String(form.content || '')
  const hasContent = sourceContent.trim() && sourceContent !== '<p><br></p>'
  const nextContent = editorContentCache[type] || cloneContentForMode(sourceType, sourceContent, type)

  if (hasContent) {
    ElMessageBox.confirm(
      '切换编辑器时，系统会尽量保留当前内容，但复杂排版仍可能出现轻微差异。确认继续切换吗？',
      '切换编辑器',
      {
        confirmButtonText: '继续切换',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      if (sourceType === 'richtext' && editorRef.value) {
        editorRef.value.destroy()
        editorRef.value = null
      }
      editorContentCache[sourceType] = sourceContent
      form.editorType = type
      form.content = nextContent
      editorContentCache[type] = nextContent
    }).catch(() => {})
    return
  }

  if (sourceType === 'richtext' && editorRef.value) {
    editorRef.value.destroy()
    editorRef.value = null
  }

  editorContentCache[sourceType] = sourceContent
  form.editorType = type
  form.content = nextContent
  editorContentCache[type] = nextContent
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
      const editorType = article.editorType || 'markdown'
      const articleContent = String(article.content || '')
      Object.assign(form, {
        title: article.title || '',
        cover: article.cover || '',
        categoryId: article.categoryId || article.category?.id || null,
        tags: Array.isArray(article.tags)
          ? article.tags.map((tag) => tag.id || tag.tagId || tag)
          : [],
        summary: article.summary || '',
        content: articleContent,
        userId: article.userId || user.value?.userId || null,
        editorType,
        articleSource: article.articleSource || 2,
        articleSourceLabel: article.articleSourceLabel || '前台发布',
        status: article.status ?? 3,
        visibilityScope: article.visibilityScope || 1,
        visibleUserIds: article.visibleUserIds || []
      })

      editorContentCache[editorType] = articleContent
      editorContentCache[editorType === 'markdown' ? 'richtext' : 'markdown'] = cloneContentForMode(editorType, articleContent, editorType === 'markdown' ? 'richtext' : 'markdown')
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
  editorContentCache.markdown = ''
  editorContentCache.richtext = ''

  if (isEdit.value && route.params.id) {
    loadArticle(route.params.id)
  }

  formRef.value?.clearValidate?.()
}

onMounted(async () => {
  resizeHandler = syncPublishLayout
  syncPublishLayout()
  window.addEventListener('resize', resizeHandler)

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
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
  }
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

.mode-switch-card {
  border-radius: 24px;
  border: 1px solid rgba(226, 232, 240, 0.95);
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.94), rgba(255, 255, 255, 0.98));
  padding: 1rem;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.92);
}

.mode-switch-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.mode-switch-card__title {
  margin-top: 0.35rem;
  font-size: 1rem;
  font-weight: 800;
  color: #0f172a;
}

.mode-switch-card__badge {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.08);
  color: #2563eb;
  padding: 0.4rem 0.75rem;
  font-size: 0.75rem;
  font-weight: 700;
}

.mode-switch-card__hint {
  margin-top: 0.85rem;
  font-size: 0.9rem;
  line-height: 1.7;
  color: #64748b;
}

.editor-mode-banner {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
  border-radius: 24px;
  border: 1px solid rgba(226, 232, 240, 0.95);
  background:
    radial-gradient(circle at top right, rgba(239, 246, 255, 0.95), transparent 42%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.98));
  padding: 1rem 1.1rem;
}

.editor-mode-banner__copy {
  min-width: 0;
}

.editor-mode-banner__label {
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

.editor-mode-banner__title {
  display: block;
  margin-top: 0.55rem;
  font-size: 1.05rem;
  font-weight: 900;
  color: #0f172a;
}

.editor-mode-banner__desc {
  margin-top: 0.35rem;
  max-width: 52rem;
  font-size: 0.92rem;
  line-height: 1.7;
  color: #64748b;
}

.editor-mode-banner__chips {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.5rem;
}

.editor-mode-banner__chip {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  border: 1px solid rgba(191, 219, 254, 0.95);
  background: rgba(239, 246, 255, 0.95);
  color: #1d4ed8;
  padding: 0.32rem 0.7rem;
  font-size: 0.75rem;
  font-weight: 700;
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
  padding: 0.72rem 0.9rem;
  border-radius: 1rem;
  color: #64748b;
  font-size: 0.92rem;
  font-weight: 700;
  transition: all 0.25s ease;
  min-height: 60px;
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.15rem;
}

.editor-mode-button--active {
  background: linear-gradient(135deg, #eff6ff, #ffffff);
  color: #2563eb;
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.12);
}

.editor-mode-button__title {
  font-size: 0.92rem;
  font-weight: 800;
}

.editor-mode-button__subtitle {
  font-size: 0.72rem;
  line-height: 1.2;
  color: inherit;
  opacity: 0.82;
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

.mobile-workspace-shell {
  border: 1px solid rgba(226, 232, 240, 0.92);
  border-radius: 26px;
  background:
    radial-gradient(circle at top left, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.98)),
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.98));
  padding: 1rem;
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.06);
}

.mobile-section-shell {
  border-radius: 26px;
  padding: 1rem;
}

.mobile-workspace-shell__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.mobile-workspace-shell__eyebrow {
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: #94a3b8;
}

.mobile-workspace-shell__title {
  margin-top: 0.35rem;
  font-size: 1.25rem;
  font-weight: 900;
  color: #0f172a;
}

.mobile-workspace-shell__desc {
  margin-top: 0.35rem;
  font-size: 0.88rem;
  line-height: 1.6;
  color: #64748b;
}

.mobile-workspace-shell__progress {
  display: inline-flex;
  min-width: 72px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  border-radius: 18px;
  border: 1px solid rgba(226, 232, 240, 0.92);
  background: white;
  padding: 0.75rem 0.8rem;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.04);
}

.mobile-workspace-shell__progress-label {
  font-size: 0.68rem;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: #94a3b8;
}

.mobile-stepper {
  display: grid;
  gap: 0.75rem;
  margin-top: 1rem;
}

.mobile-stepper__item {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  width: 100%;
  border: 1px solid rgba(226, 232, 240, 0.95);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  padding: 0.85rem 0.95rem;
  text-align: left;
  transition: all 0.25s ease;
}

.mobile-stepper__item--active {
  border-color: rgba(37, 99, 235, 0.22);
  background: linear-gradient(135deg, rgba(239, 246, 255, 0.98), rgba(255, 255, 255, 0.98));
  box-shadow: 0 12px 26px rgba(37, 99, 235, 0.08);
}

.mobile-stepper__index {
  display: inline-flex;
  min-width: 2.2rem;
  min-height: 2.2rem;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  background: linear-gradient(135deg, #e2e8f0, #f8fafc);
  color: #64748b;
  font-size: 0.72rem;
  font-weight: 800;
  letter-spacing: 0.1em;
}

.mobile-stepper__item--active .mobile-stepper__index {
  background: linear-gradient(135deg, #2563eb, #0ea5e9);
  color: white;
}

.mobile-stepper__text {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: 0.15rem;
}

.mobile-stepper__label {
  font-size: 0.96rem;
  font-weight: 800;
  color: #0f172a;
}

.mobile-stepper__hint {
  font-size: 0.8rem;
  color: #64748b;
}

.mobile-mode-switch {
  display: flex;
  gap: 0.5rem;
  padding: 0.35rem;
  border: 1px solid rgba(226, 232, 240, 0.92);
  border-radius: 18px;
  background: rgba(248, 250, 252, 0.96);
}

.mobile-info-grid,
.mobile-summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.75rem;
}

.mobile-info-card,
.mobile-summary-card {
  border: 1px solid rgba(226, 232, 240, 0.92);
  border-radius: 18px;
  background: white;
  padding: 0.85rem 0.95rem;
}

.mobile-info-card__label,
.mobile-summary-card__label {
  display: block;
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: #94a3b8;
}

.mobile-info-card__value,
.mobile-summary-card__value {
  display: block;
  margin-top: 0.4rem;
  font-size: 0.95rem;
  font-weight: 800;
  color: #0f172a;
}

.mobile-cover-uploader {
  min-height: 200px;
}

.mobile-publish-hint {
  border-radius: 18px;
  border: 1px solid rgba(191, 219, 254, 0.8);
  background: linear-gradient(135deg, rgba(239, 246, 255, 0.96), rgba(255, 255, 255, 0.96));
  padding: 0.95rem 1rem;
  color: #334155;
  line-height: 1.7;
}

.mobile-step-actions {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.mobile-step-actions--split {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.mobile-step-actions :deep(.el-button),
.mobile-step-actions :deep(.el-button + .el-button) {
  width: 100%;
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
  .article-publish-page {
    background:
      radial-gradient(circle at top left, rgba(148, 176, 231, 0.12), transparent 28%),
      radial-gradient(circle at bottom right, rgba(255, 255, 255, 0.72), transparent 26%),
      linear-gradient(180deg, #f5f8fd 0%, #eef3fb 100%);
  }

  .editor-panel {
    padding: 1rem;
    border-radius: 24px;
    box-shadow: 0 16px 36px rgba(15, 23, 42, 0.05);
  }

  .workspace-stat {
    min-width: 0;
  }

  .article-publish-page .editor-mode-button {
    padding: 0.72rem 0.6rem;
    font-size: 0.86rem;
  }

  .article-publish-page .editor-mode-button__subtitle {
    font-size: 0.68rem;
  }

  .article-publish-page .editor-title-input {
    font-size: clamp(1.4rem, 7vw, 2rem);
  }

  .rich-editor-shell__content {
    height: 66svh;
  }

  .article-publish-page .rich-editor-shell__meta {
    flex-direction: column;
  }

  .article-publish-page .workspace-stat {
    width: 100%;
  }

  .mobile-workspace-shell__header {
    flex-direction: column;
  }

  .mobile-workspace-shell__progress {
    align-items: flex-start;
  }

  .mobile-workspace-shell__title {
    font-size: 1.15rem;
  }

  .mobile-workspace-shell__desc {
    font-size: 0.84rem;
  }

  .mobile-stepper {
    display: flex;
    overflow-x: auto;
    gap: 0.55rem;
    padding-bottom: 0.25rem;
    scroll-snap-type: x mandatory;
  }

  .mobile-stepper__item {
    min-width: 160px;
    flex: 0 0 auto;
    scroll-snap-align: start;
  }

  .mobile-stepper__hint {
    font-size: 0.72rem;
    line-height: 1.35;
  }

  .mobile-info-grid,
  .mobile-summary-grid {
    grid-template-columns: 1fr;
  }

  .mobile-step-actions--split {
    grid-template-columns: 1fr;
  }

  .mobile-step-actions--stack {
    padding-top: 0.25rem;
  }

  .editor-mode-banner {
    flex-direction: column;
  }

  .editor-mode-banner__chips {
    justify-content: flex-start;
  }

  .mode-switch-card {
    padding: 0.9rem;
  }

  .article-publish-page .mobile-workspace-shell {
    padding: 0.85rem;
    border-radius: 24px;
  }

  .article-publish-page .mobile-section-shell {
    padding: 0.9rem;
    border-radius: 24px;
  }

  .article-publish-page .mobile-section-shell--content {
    min-height: calc(100svh - 14rem);
  }

  .article-publish-page .mobile-section-shell--content .editor-panel__header {
    margin-bottom: 1rem;
  }

  .article-publish-page .mobile-publish-hint {
    font-size: 0.9rem;
  }

  .article-publish-page .mobile-step-actions :deep(.el-button) {
    min-height: 46px;
  }

  .article-publish-page .mobile-step-actions--stack {
    position: sticky;
    bottom: 0.75rem;
    z-index: 20;
    padding: 0.9rem;
    border-radius: 22px;
    background: rgba(255, 255, 255, 0.94);
    box-shadow: 0 16px 34px rgba(15, 23, 42, 0.08);
    border: 1px solid rgba(148, 176, 231, 0.16);
    backdrop-filter: blur(16px);
  }
}

@media (max-width: 520px) {
  .article-publish-page .mobile-stepper__item {
    min-width: 148px;
  }

  .article-publish-page .mobile-summary-grid {
    gap: 0.6rem;
  }

  .article-publish-page .mobile-summary-card {
    padding: 0.8rem;
  }

  .article-publish-page .mobile-cover-uploader {
    min-height: 180px;
  }

  .article-publish-page .rich-editor-shell__content {
    height: 60svh;
  }

  .article-publish-page .mobile-stepper__hint {
    display: none;
  }
}
</style>
