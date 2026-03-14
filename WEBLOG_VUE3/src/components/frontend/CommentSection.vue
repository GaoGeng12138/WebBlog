<template>
  <div class="comment-section bg-white rounded-xl shadow-md p-6 mt-8">
    <!-- Comment Header -->
    <div class="flex items-center justify-between mb-6">
      <h2 class="text-2xl font-bold text-gray-900">
        评论 <span class="text-sm text-gray-500">({{ totalComments }})</span>
      </h2>
    </div>

    <!-- Comment Input - Only show if comments are enabled -->
    <div v-if="isCommentEnabled" class="mb-8">
      <!-- Anonymous or Logged-in User Info -->
      <div class="flex items-start space-x-3 mb-4">
        <div class="flex-shrink-0">
          <div v-if="isLoggedIn" class="w-10 h-10 rounded-full bg-gradient-to-r from-blue-500 to-purple-500 flex items-center justify-center text-white font-bold">
            {{ userStore.frontendUserInfo?.nickname?.charAt(0) || 'U' }}
          </div>
          <div v-else class="w-10 h-10 rounded-full bg-gray-300 flex items-center justify-center text-gray-600">
            <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
            </svg>
          </div>
        </div>
        <div class="flex-1">
          <div class="text-sm text-gray-700 mb-2">
            <span v-if="isLoggedIn" class="font-semibold">{{ userStore.frontendUserInfo?.nickname }}</span>
            <span v-else-if="isAnonymousEnabled" class="text-gray-500">匿名评论</span>
            <span v-else class="text-gray-500">
              请 <router-link to="/login" class="text-blue-600 hover:text-blue-700">登录</router-link> 后发表评论
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
          <div class="flex justify-end mt-3">
            <el-button 
              type="primary" 
              @click="submitComment"
              :loading="submitLoading"
              :disabled="!newCommentContent.trim() || (!isLoggedIn && !isAnonymousEnabled)"
            >
              发表评论
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- No Comments Enabled Message -->
    <div v-else class="text-center py-8 text-gray-500">
      <svg class="mx-auto h-12 w-12 text-gray-400 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
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
    <div v-else-if="isCommentEnabled && comments.length === 0" class="text-center py-12 text-gray-500">
      <svg class="mx-auto h-16 w-16 text-gray-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
      </svg>
      <p class="text-lg">暂无评论</p>
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
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply" :loading="submitLoading">
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
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  transition: border-color 0.3s;
}

.comment-input :deep(.el-textarea__inner):focus {
  border-color: #3b82f6;
}

.comment-pagination :deep(.el-pagination) {
  justify-content: center;
}
</style>
