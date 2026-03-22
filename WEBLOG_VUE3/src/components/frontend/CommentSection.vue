<template>
  <div class="comment-section mt-8 rounded-[26px] border border-[rgba(129,158,196,0.18)] bg-white/92 p-4 shadow-[0_24px_70px_rgba(120,146,184,0.12)] backdrop-blur-xl sm:rounded-[30px] sm:p-6 sm:px-8">
    <!-- Comment Header -->
    <div class="mb-5 flex items-center justify-between gap-3 sm:mb-6">
      <h2 class="text-xl font-bold text-slate-900 sm:text-2xl">
        评论 <span class="text-sm text-slate-500">({{ totalComments }})</span>
      </h2>
    </div>

    <!-- Comment Input - Only show if comments are enabled -->
    <div v-if="isCommentEnabled" class="mb-8">
      <!-- Anonymous or Logged-in User Info -->
      <div class="mb-4 flex items-start gap-3">
        <div class="flex-shrink-0">
          <div v-if="isLoggedIn" class="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-[var(--theme-primary)] to-[var(--theme-primary-soft)] text-sm font-bold text-white shadow-[0_12px_26px_rgba(116,149,195,0.22)]">
            {{ userStore.frontendUserInfo?.nickname?.charAt(0) || 'U' }}
          </div>
          <div v-else class="flex h-10 w-10 items-center justify-center rounded-full bg-[rgba(240,245,251,0.95)] text-slate-500">
            <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
            </svg>
          </div>
        </div>
        <div class="flex-1">
          <div class="mb-2 text-sm text-slate-700">
            <span v-if="isLoggedIn" class="font-semibold">{{ userStore.frontendUserInfo?.nickname }}</span>
            <span v-else-if="isAnonymousEnabled" class="text-slate-500">匿名评论</span>
            <span v-else class="text-slate-500">
              请 <router-link to="/login" class="font-medium text-[var(--theme-primary-deep)] hover:text-[var(--theme-primary)]">登录</router-link> 后发表评论
            </span>
          </div>
          <el-input
            v-model="newCommentContent"
            type="textarea"
            :rows="3"
            placeholder="写下你的评论..."
            :disabled="!isLoggedIn && !isAnonymousEnabled"
            maxlength="500"
            show-word-limit
            class="comment-input"
          />
          <div class="mt-3 flex justify-end">
            <el-button 
              type="primary" 
              @click="submitComment"
              :loading="submitLoading"
              :disabled="!newCommentContent.trim() || (!isLoggedIn && !isAnonymousEnabled)"
              class="theme-btn-primary !rounded-full !px-6"
            >
              发表评论
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- No Comments Enabled Message -->
    <div v-else class="rounded-[24px] border border-dashed border-[rgba(129,158,196,0.2)] bg-[rgba(244,248,252,0.88)] py-8 text-center text-slate-500">
      <svg class="mx-auto mb-3 h-12 w-12 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
      </svg>
      <p>评论功能已关闭</p>
    </div>

    <!-- Comments List -->
    <div v-if="isCommentEnabled && comments.length > 0" class="space-y-6">
      <CommentItem
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
        :current-user-id="userStore.frontendUserInfo?.userId"
        :is-logged-in="isLoggedIn"
        :is-anonymous-enabled="isAnonymousEnabled"
        :is-like-enabled="isLikeEnabled"
        :article-id="articleId"
        @reply="handleReply"
        @delete="handleDelete"
        @refresh="loadComments"
      />
    </div>

    <!-- No Comments -->
    <div v-else-if="isCommentEnabled && comments.length === 0" class="rounded-[24px] border border-dashed border-[rgba(129,158,196,0.18)] bg-[rgba(244,248,252,0.84)] py-12 text-center text-slate-500">
      <svg class="mx-auto mb-3 h-16 w-16 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
      </svg>
      <p class="text-lg font-medium text-slate-700">暂无评论</p>
      <p class="text-sm mt-1">快来发表第一条评论吧！</p>
    </div>

    <!-- Pagination -->
    <div v-if="isCommentEnabled && totalComments > pageSize" class="mt-8 flex justify-center">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="totalComments"
        layout="prev, pager, next"
        @current-change="handlePageChange"
        class="comment-pagination"
      />
    </div>

    <!-- Reply Dialog -->
    <el-dialog
      v-model="replyDialogVisible"
      :title="`回复 @${replyToComment?.nickname || '用户'}`"
      width="500px"
      class="comment-dialog"
    >
      <el-input
        v-model="replyContent"
        type="textarea"
        :rows="4"
        placeholder="写下你的回复..."
        maxlength="500"
        show-word-limit
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button class="theme-btn-secondary !rounded-full !px-5" @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" class="theme-btn-primary !rounded-full !px-5" @click="submitReply" :loading="submitLoading">
            发表回复
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useSiteConfigStore } from '@/stores/siteConfig'
import { getComments, postComment, deleteComment } from '@/api/frontend/comment'
import CommentItem from './CommentItem.vue'

const props = defineProps({
  articleId: {
    type: Number,
    required: true
  }
})

const userStore = useUserStore()
const siteConfig = useSiteConfigStore()

// State
const comments = ref([])
const newCommentContent = ref('')
const replyContent = ref('')
const submitLoading = ref(false)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalComments = ref(0)
const replyDialogVisible = ref(false)
const replyToComment = ref(null)

// Computed
const isLoggedIn = computed(() => {
  return !!userStore.frontendUserInfo && !!userStore.frontendUserInfo.userId
})

const isCommentEnabled = computed(() => {
  return siteConfig.isFeatureEnabled('commentEnabled')
})

const isAnonymousEnabled = computed(() => {
  return siteConfig.isFeatureEnabled('anonymousCommentEnabled')
})

const isLikeEnabled = computed(() => {
  return siteConfig.isFeatureEnabled('likeEnabled')
})

// Methods
async function loadComments() {
  if (!isCommentEnabled.value) return
  
  loading.value = true
  try {
    const res = await getComments(props.articleId, currentPage.value, pageSize.value)
    if (res && res.success) {
      // API returns data as array directly, with total/size/current/pages at root
      comments.value = res.data || []
      totalComments.value = res.total || 0
      console.log('评论加载成功:', comments.value)
    }
  } catch (error) {
    console.error('加载评论失败:', error)
    ElMessage.error('加载评论失败')
  } finally {
    loading.value = false
  }
}

async function submitComment() {
  if (!newCommentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  if (!isLoggedIn.value && !isAnonymousEnabled.value) {
    ElMessage.warning('请先登录后再发表评论')
    return
  }

  submitLoading.value = true
  try {
    const commentData = {
      articleId: props.articleId,
      content: newCommentContent.value.trim()
    }
    
    const res = await postComment(commentData)
    if (res && res.success) {
      ElMessage.success('评论发表成功' + (siteConfig.permissions.commentReviewRequired ? '，等待审核' : ''))
      newCommentContent.value = ''
      // Reload comments
      currentPage.value = 1
      await loadComments()
    }
  } catch (error) {
    console.error('发表评论失败:', error)
    if (error.response && error.response.data) {
      const errorMsg = error.response.data.message || error.response.data.errorMsg
      ElMessage.error(errorMsg || '发表评论失败')
    } else {
      ElMessage.error('发表评论失败')
    }
  } finally {
    submitLoading.value = false
  }
}

function handleReply(comment) {
  replyToComment.value = comment
  replyContent.value = ''
  replyDialogVisible.value = true
}

async function submitReply() {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  submitLoading.value = true
  try {
    const commentData = {
      articleId: props.articleId,
      content: replyContent.value.trim(),
      parentId: replyToComment.value.id,
      replyToId: replyToComment.value.userId
    }
    
    const res = await postComment(commentData)
    if (res && res.success) {
      ElMessage.success('回复发表成功' + (siteConfig.permissions.commentReviewRequired ? '，等待审核' : ''))
      replyDialogVisible.value = false
      replyContent.value = ''
      replyToComment.value = null
      // Reload comments
      await loadComments()
    }
  } catch (error) {
    console.error('发表回复失败:', error)
    if (error.response && error.response.data) {
      const errorMsg = error.response.data.message || error.response.data.errorMsg
      ElMessage.error(errorMsg || '发表回复失败')
    } else {
      ElMessage.error('发表回复失败')
    }
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(commentId) {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条评论吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await deleteComment(commentId)
    if (res && res.success) {
      ElMessage.success('删除成功')
      await loadComments()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

function handlePageChange(page) {
  currentPage.value = page
  loadComments()
  // Scroll to comment section
  document.querySelector('.comment-section')?.scrollIntoView({ behavior: 'smooth' })
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.comment-input :deep(.el-textarea__inner) {
  border-radius: 20px;
  border: 1px solid rgba(129, 158, 196, 0.2);
  background: rgba(248, 251, 255, 0.95);
  box-shadow: inset 0 1px 2px rgba(148, 163, 184, 0.08);
  transition: border-color 0.3s, box-shadow 0.3s, background 0.3s;
}

.comment-input :deep(.el-textarea__inner):focus {
  border-color: rgba(116, 149, 195, 0.45);
  box-shadow: 0 0 0 4px rgba(116, 149, 195, 0.12);
  background: white;
}

.comment-pagination :deep(.el-pagination) {
  justify-content: center;
}

.comment-dialog :deep(.el-dialog) {
  border: 1px solid rgba(129, 158, 196, 0.18);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 24px 70px rgba(120, 146, 184, 0.18);
  backdrop-filter: blur(20px);
}

.comment-dialog :deep(.el-dialog__header) {
  padding: 24px 24px 12px;
}

.comment-dialog :deep(.el-dialog__body) {
  padding: 0 24px 8px;
}

.comment-dialog :deep(.el-dialog__footer) {
  padding: 12px 24px 24px;
}

@media (max-width: 640px) {
  .comment-section {
    border-radius: 22px;
  }

  .comment-section :deep(.el-button) {
    width: 100%;
  }

  .comment-section :deep(.el-pagination) {
    flex-wrap: wrap;
    gap: 0.35rem;
  }

  .comment-dialog :deep(.el-dialog) {
    width: calc(100vw - 1.5rem) !important;
    margin: 0.75rem auto !important;
    border-radius: 20px;
  }

  .comment-dialog :deep(.el-dialog__header),
  .comment-dialog :deep(.el-dialog__body),
  .comment-dialog :deep(.el-dialog__footer) {
    padding-left: 16px;
    padding-right: 16px;
  }
}
</style>
