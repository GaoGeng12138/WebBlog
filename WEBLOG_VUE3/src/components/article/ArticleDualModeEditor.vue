<template>
  <div class="article-dual-mode-editor" :class="[`article-dual-mode-editor--${variant}`]">
    <div class="article-dual-mode-editor__switcher">
      <div class="article-dual-mode-editor__switcher-head">
        <div>
          <p class="article-dual-mode-editor__eyebrow">{{ eyebrow }}</p>
          <h3 class="article-dual-mode-editor__title">{{ modeDetails.title }}</h3>
        </div>
        <span class="article-dual-mode-editor__badge">{{ editorModeLabel }}</span>
      </div>

      <div class="article-dual-mode-editor__buttons">
        <button
          v-for="type in ['markdown', 'richtext']"
          :key="type"
          type="button"
          class="article-dual-mode-editor__button"
          :class="{ 'article-dual-mode-editor__button--active': currentEditorType === type }"
          @click="handleModeChange(type)"
        >
          <span class="article-dual-mode-editor__button-title">{{ type === 'markdown' ? 'Markdown' : '富文本' }}</span>
          <span class="article-dual-mode-editor__button-subtitle">
            {{ type === 'markdown' ? markdownHint : richtextHint }}
          </span>
        </button>
      </div>

      <p class="article-dual-mode-editor__hint">{{ modeDetails.hint }}</p>
    </div>

    <div class="article-dual-mode-editor__canvas">
      <div class="article-dual-mode-editor__banner">
        <div class="article-dual-mode-editor__banner-copy">
          <span class="article-dual-mode-editor__banner-label">当前模式</span>
          <strong class="article-dual-mode-editor__banner-title">{{ modeDetails.title }}</strong>
          <p class="article-dual-mode-editor__banner-desc">{{ modeDetails.desc }}</p>
        </div>
        <div class="article-dual-mode-editor__chips">
          <span v-for="chip in modeDetails.chips" :key="chip" class="article-dual-mode-editor__chip">
            {{ chip }}
          </span>
        </div>
      </div>

      <MarkdownEditorSurface
        v-if="currentEditorType === 'markdown'"
        :model-value="modelValue"
        :editor-id="editorId"
        :height="height"
        :placeholder="markdownPlaceholder"
        :compact-mode="compactMode"
        :upload-handler="uploadHandler"
        @update:modelValue="emit('update:modelValue', $event)"
      />

      <div v-else class="article-dual-mode-editor__rich-shell">
        <div class="article-dual-mode-editor__rich-meta">
          <span class="article-dual-mode-editor__rich-badge">富文本编辑器</span>
          <span class="article-dual-mode-editor__rich-hint">{{ richtextHint }}</span>
        </div>
        <Toolbar
          :editor="editorRef"
          :defaultConfig="toolbarConfig"
          mode="default"
          class="article-dual-mode-editor__toolbar"
        />
        <div class="article-dual-mode-editor__rich-content" :style="{ height }">
          <Editor
            v-model="localRichtextValue"
            :defaultConfig="editorConfig"
            mode="default"
            style="height: 100%; overflow-y: hidden;"
            @onCreated="handleCreated"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, reactive, ref, shallowRef, watch } from 'vue'
import MarkdownIt from 'markdown-it'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import MarkdownEditorSurface from '@/components/article/MarkdownEditorSurface.vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  editorType: {
    type: String,
    default: 'markdown'
  },
  editorId: {
    type: String,
    default: 'shared-article-editor'
  },
  height: {
    type: String,
    default: '760px'
  },
  compactMode: {
    type: Boolean,
    default: false
  },
  variant: {
    type: String,
    default: 'frontend'
  },
  eyebrow: {
    type: String,
    default: '编辑模式'
  },
  markdownPlaceholder: {
    type: String,
    default: '请输入文章正文...'
  },
  uploadHandler: {
    type: Function,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'update:editorType'])

const editorRef = shallowRef()
const toolbarConfig = {}
const localRichtextValue = ref('')
const editorContentCache = reactive({
  markdown: '',
  richtext: ''
})

const markdownRenderer = new MarkdownIt({
  html: true,
  breaks: true,
  linkify: true,
  typographer: true
})

const modeMap = {
  markdown: {
    title: 'Markdown 快写模式',
    desc: '适合技术文章、教程和长内容，键盘输入效率最高。',
    hint: '适合先搭结构再补细节的写作方式，切到富文本时会尽量保留内容。',
    chips: ['标题层级', '代码块', '表格', '图片链接'],
    buttonHint: '适合结构化长文'
  },
  richtext: {
    title: '富文本可视化模式',
    desc: '适合图文混排、活动稿和不想手敲语法的场景。',
    hint: '适合所见即所得的排版体验，切回 Markdown 时会尽量保留标题和列表。',
    chips: ['所见即所得', '点击排版', '图片拖拽', '协作友好'],
    buttonHint: '适合可视化排版'
  }
}

const editorModeLabel = computed(() => (props.editorType === 'markdown' ? 'Markdown' : '富文本'))
const currentEditorType = computed(() => props.editorType || 'markdown')
const modeDetails = computed(() => modeMap[currentEditorType.value] || modeMap.markdown)
const markdownHint = computed(() => modeMap.markdown.buttonHint)
const richtextHint = computed(() => modeMap.richtext.buttonHint)

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

const syncEditorCache = (type, value) => {
  editorContentCache[type] = String(value || '')
}

watch(
  () => [props.editorType, props.modelValue],
  ([type, value]) => {
    syncEditorCache(type, value)
    if (type === 'richtext' && editorRef.value == null) {
      localRichtextValue.value = editorContentCache.richtext || markdownToHtml(value)
    }
  },
  { immediate: true }
)

watch(
  () => props.editorType,
  (nextType, prevType) => {
    if (!nextType || nextType === prevType) return
    const sourceContent = String(props.modelValue || '')
    const nextContent = editorContentCache[nextType] || cloneContentForMode(prevType || 'markdown', sourceContent, nextType)
    if (nextType === 'richtext') {
      localRichtextValue.value = nextContent
    }
    emit('update:modelValue', nextContent)
  }
)

watch(
  () => localRichtextValue.value,
  (value) => {
    if (currentEditorType.value === 'richtext') {
      emit('update:modelValue', value)
      syncEditorCache('richtext', value)
    }
  }
)

const handleCreated = (editor) => {
  editorRef.value = editor
}

const handleModeChange = (type) => {
  if (currentEditorType.value === type) return

  const sourceType = currentEditorType.value
  const sourceContent = String(props.modelValue || '')
  const nextContent = editorContentCache[type] || cloneContentForMode(sourceType, sourceContent, type)

  if (sourceType === 'richtext' && editorRef.value) {
    editorRef.value.destroy()
    editorRef.value = null
  }

  syncEditorCache(sourceType, sourceContent)
  emit('update:editorType', type)
  emit('update:modelValue', nextContent)
  syncEditorCache(type, nextContent)
  if (type === 'richtext') {
    localRichtextValue.value = nextContent
  }
}

onBeforeUnmount(() => {
  if (editorRef.value) {
    editorRef.value.destroy()
  }
})
</script>

<style scoped>
.article-dual-mode-editor {
  display: grid;
  gap: 1rem;
}

.article-dual-mode-editor__switcher {
  border-radius: 24px;
  border: 1px solid rgba(226, 232, 240, 0.95);
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.94), rgba(255, 255, 255, 0.98));
  padding: 1rem;
}

.article-dual-mode-editor__switcher-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.article-dual-mode-editor__eyebrow {
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: #94a3b8;
}

.article-dual-mode-editor__title {
  margin-top: 0.35rem;
  font-size: 1rem;
  font-weight: 800;
  color: #0f172a;
}

.article-dual-mode-editor__badge {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.08);
  color: #2563eb;
  padding: 0.4rem 0.75rem;
  font-size: 0.75rem;
  font-weight: 700;
}

.article-dual-mode-editor__buttons {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.75rem;
  margin-top: 0.85rem;
}

.article-dual-mode-editor__button {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.2rem;
  min-height: 60px;
  border: 1px solid rgba(226, 232, 240, 0.95);
  border-radius: 18px;
  background: rgba(248, 250, 252, 0.96);
  color: #64748b;
  font-size: 0.92rem;
  font-weight: 700;
  transition: all 0.25s ease;
}

.article-dual-mode-editor__button--active {
  border-color: rgba(37, 99, 235, 0.3);
  background: linear-gradient(135deg, #eff6ff, #ffffff);
  color: #2563eb;
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.12);
}

.article-dual-mode-editor__button-title {
  font-size: 0.94rem;
  font-weight: 800;
}

.article-dual-mode-editor__button-subtitle {
  font-size: 0.72rem;
  line-height: 1.2;
  color: inherit;
  opacity: 0.82;
}

.article-dual-mode-editor__hint {
  margin-top: 0.85rem;
  font-size: 0.9rem;
  line-height: 1.7;
  color: #64748b;
}

.article-dual-mode-editor__banner {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  border-radius: 24px;
  border: 1px solid rgba(226, 232, 240, 0.95);
  background:
    radial-gradient(circle at top right, rgba(239, 246, 255, 0.95), transparent 42%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.98));
  padding: 1rem 1.1rem;
}

.article-dual-mode-editor__banner-label {
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

.article-dual-mode-editor__banner-title {
  display: block;
  margin-top: 0.55rem;
  font-size: 1.05rem;
  font-weight: 900;
  color: #0f172a;
}

.article-dual-mode-editor__banner-desc {
  margin-top: 0.35rem;
  max-width: 52rem;
  font-size: 0.92rem;
  line-height: 1.7;
  color: #64748b;
}

.article-dual-mode-editor__chips {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.5rem;
}

.article-dual-mode-editor__chip {
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

.article-dual-mode-editor__canvas {
  display: grid;
  gap: 1rem;
}

.article-dual-mode-editor__rich-shell {
  border-radius: 24px;
  border: 1px solid rgba(226, 232, 240, 0.95);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.98));
  overflow: hidden;
  box-shadow: 0 18px 38px rgba(15, 23, 42, 0.06);
}

.article-dual-mode-editor__rich-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 1rem 1.15rem;
  border-bottom: 1px solid rgba(226, 232, 240, 0.92);
}

.article-dual-mode-editor__rich-badge {
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

.article-dual-mode-editor__rich-hint {
  font-size: 12px;
  color: #64748b;
}

.article-dual-mode-editor__toolbar {
  border-bottom: 1px solid rgba(226, 232, 240, 0.92);
}

.article-dual-mode-editor__rich-content {
  overflow: hidden;
}

.article-dual-mode-editor--admin .article-dual-mode-editor__switcher {
  padding: 0;
  border: none;
  background: transparent;
}

.article-dual-mode-editor--admin .article-dual-mode-editor__banner {
  margin-top: 0;
}

@media (max-width: 768px) {
  .article-dual-mode-editor__switcher-head,
  .article-dual-mode-editor__banner,
  .article-dual-mode-editor__rich-meta {
    flex-direction: column;
  }

  .article-dual-mode-editor__chips {
    justify-content: flex-start;
  }
}
</style>
