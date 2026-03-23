<template>
  <div class="markdown-editor-surface" :class="{ 'markdown-editor-surface--compact': compactMode }">
    <div class="markdown-editor-surface__meta">
      <div class="markdown-editor-surface__label">
        <span class="markdown-editor-surface__dot"></span>
        沉浸式 Markdown
      </div>
      <div class="markdown-editor-surface__chips">
        <span class="markdown-editor-surface__chip">实时预览</span>
        <span class="markdown-editor-surface__chip">代码高亮</span>
        <span class="markdown-editor-surface__chip">字数统计</span>
      </div>
    </div>

    <MdEditor
      :model-value="modelValue"
      :theme="theme"
      :placeholder="placeholder"
      :id="editorId"
      language="zh-CN"
      preview-theme="github"
      code-theme="atom"
      :show-code-row-number="true"
      :toolbars-exclude="toolbarsExclude"
      :footers="footers"
      :style="{ height }"
      class="markdown-editor-surface__editor"
      @update:modelValue="$emit('update:modelValue', $event)"
      @onUploadImg="handleUploadImg"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '开始记录今天的灵感、步骤和结论...'
  },
  height: {
    type: String,
    default: '720px'
  },
  editorId: {
    type: String,
    default: 'shared-markdown-editor'
  },
  theme: {
    type: String,
    default: 'light'
  },
  uploadHandler: {
    type: Function,
    default: null
  },
  compactMode: {
    type: Boolean,
    default: false
  }
})

defineEmits(['update:modelValue'])

const footers = computed(() => (props.compactMode ? [] : ['markdownTotal', 'scrollSwitch']))
const toolbarsExclude = ['github', 'save', 'htmlPreview']

function handleUploadImg(...args) {
  if (props.uploadHandler) {
    props.uploadHandler(...args)
  }
}
</script>

<style scoped>
.markdown-editor-surface {
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 24px;
  overflow: hidden;
  background:
    radial-gradient(circle at top left, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.96)),
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.98));
  box-shadow: 0 22px 50px rgba(15, 23, 42, 0.08);
}

.markdown-editor-surface__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 1rem 1.25rem;
  border-bottom: 1px solid rgba(226, 232, 240, 0.92);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.96), rgba(241, 245, 249, 0.96));
}

.markdown-editor-surface__label {
  display: inline-flex;
  align-items: center;
  gap: 0.65rem;
  color: #0f172a;
  font-size: 0.92rem;
  font-weight: 700;
  letter-spacing: 0.03em;
}

.markdown-editor-surface__dot {
  width: 0.6rem;
  height: 0.6rem;
  border-radius: 999px;
  background: linear-gradient(135deg, #2563eb, #0ea5e9);
  box-shadow: 0 0 0 6px rgba(37, 99, 235, 0.12);
}

.markdown-editor-surface__chips {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.5rem;
}

.markdown-editor-surface__chip {
  padding: 0.3rem 0.7rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(226, 232, 240, 0.96);
  color: #64748b;
  font-size: 0.75rem;
  font-weight: 600;
}

.markdown-editor-surface__editor {
  border: none !important;
  background: transparent;
}

:deep(.md-editor) {
  border: none !important;
  background: transparent;
}

:deep(.md-editor-toolbar-wrapper) {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.95)) !important;
  border-bottom: 1px solid rgba(226, 232, 240, 0.92) !important;
}

:deep(.md-editor-toolbar) {
  padding: 0.6rem 0.75rem !important;
}

:deep(.md-editor-toolbar-item) {
  border-radius: 12px !important;
}

:deep(.md-editor-toolbar-item:hover) {
  background: rgba(37, 99, 235, 0.08) !important;
  color: #2563eb !important;
}

:deep(.md-editor-content) {
  background: transparent !important;
}

:deep(.md-editor-input-wrapper),
:deep(.md-editor-preview-wrapper) {
  background: rgba(255, 255, 255, 0.82) !important;
}

:deep(.md-editor-input) {
  font-size: 0.97rem !important;
  line-height: 1.85 !important;
}

:deep(.md-editor-preview) {
  padding: 1.5rem 1.6rem !important;
}

:deep(.md-editor-footer) {
  min-height: 44px !important;
  padding: 0.4rem 0.9rem !important;
  border-top: 1px solid rgba(226, 232, 240, 0.92) !important;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.96)) !important;
}

@media (max-width: 768px) {
  .markdown-editor-surface__meta {
    flex-direction: column;
    align-items: flex-start;
  }

  .markdown-editor-surface__chips {
    justify-content: flex-start;
  }

  .markdown-editor-surface--compact .markdown-editor-surface__chips {
    display: none;
  }

  .markdown-editor-surface--compact :deep(.md-editor-preview-wrapper) {
    display: none !important;
  }

  .markdown-editor-surface--compact :deep(.md-editor-input-wrapper) {
    width: 100% !important;
    flex: 1 1 auto !important;
  }

  .markdown-editor-surface--compact :deep(.md-editor-input) {
    min-height: 52svh !important;
    font-size: 0.98rem !important;
    line-height: 1.85 !important;
  }

  .markdown-editor-surface--compact :deep(.md-editor-toolbar-wrapper) {
    position: sticky;
    top: 0;
    z-index: 10;
  }

  .markdown-editor-surface--compact :deep(.md-editor-footer) {
    display: none !important;
  }
}
</style>
