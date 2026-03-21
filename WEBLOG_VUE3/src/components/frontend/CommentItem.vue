<template>
  <div class="comment-item border-b border-gray-200 pb-6 last:border-0">
    <div class="flex space-x-3">
      <!-- User Avatar -->
      <div class="flex-shrink-0">
        <div v-if="comment.avatar" class="w-10 h-10 rounded-full overflow-hidden">
          <img :src="comment.avatar" :alt="comment.nickname" class="w-full h-full object-cover" @error="handleAvatarError" />
        </div>
        <div v-else class="w-10 h-10 rounded-full bg-gradient-to-r from-blue-500 to-purple-500 flex items-center justify-center text-white font-bold">
          {{ comment.nickname?.charAt(0) || 'U' }}
        </div>
      </div>

      <!-- Comment Content -->
      <div class="flex-1 min-w-0">
        <!-- User Info & Meta -->
        <div class="flex items-center justify-between mb-2">
          <div class="flex items-center space-x-2">
            <span class="font-semibold text-gray-900">{{ comment.nickname || '匿名用户' }}</span>
            <span v-if="comment.isUserDeleted" class="px-2 py-0.5 bg-gray-100 text-gray-500 text-xs rounded-full">已注销</span>
            <span v-if="comment.isAuthor" class="px-2 py-0.5 bg-blue-100 text-blue-700 text-xs rounded-full">作者</span>
            <span v-if="comment.isTop" class="px-2 py-0.5 bg-red-100 text-red-700 text-xs rounded-full">置顶</span>
            <span class="text-sm text-gray-500">{{ formatDate(comment.createTime) }}</span>
          </div>
          
          <!-- Actions Menu -->
          <el-dropdown v-if="canDelete" trigger="click">
            <span class="text-gray-400 hover:text-gray-600 cursor-pointer">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10 6a2 2 0 110-4 2 2 0 010 4zM10 12a2 2 0 110-4 2 2 0 010 4zM10 18a2 2 0 110-4 2 2 0 010 4z" />
              </svg>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleDelete">
                  <span class="text-red-600">删除</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <!-- Reply To Info -->
        <div v-if="comment.replyToNickname" class="mb-2">
          <span class="text-sm text-gray-500">
            回复 <span class="text-blue-600 font-medium">@{{ comment.replyToNickname }}</span>
          </span>
        </div>

        <!-- Comment Text -->
        <div class="text-gray-700 text-base leading-relaxed mb-3 whitespace-pre-wrap">
          {{ comment.content }}
        </div>

        <!-- Action Buttons -->
        <div class="flex items-center space-x-4 text-sm">
          <!-- Like Button -->
          <button
            v-if="isLikeEnabled && isLoggedIn"
            @click="handleLike"
            class="flex items-center space-x-1 text-gray-500 hover:text-blue-600 transition-colors"
            :class="{ 'text-blue-600': comment.isLiked }"
          >
            <svg class="w-4 h-4" :class="{ 'fill-current': comment.isLiked }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5" />
            </svg>
            <span>{{ comment.likeCount || 0 }}</span>
          </button>
          <!-- Like Count Only (when feature disabled or user not logged in) -->
          <span v-if="!isLikeEnabled || !isLoggedIn" class="flex items-center space-x-1 text-gray-400">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5" />
            </svg>
            <span>{{ comment.likeCount || 0 }}</span>
          </span>

          <!-- Reply Button -->
          <button
            v-if="canReply"
            @click="handleReply"
            class="flex items-center space-x-1 text-gray-500 hover:text-blue-600 transition-colors"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6" />
            </svg>
            <span>回复</span>
            <!-- Only show counter for top-level comments -->
            <span v-if="!comment.parentId && replyCount > 0" class="text-xs text-gray-400">({{ replyCount }}/{{ MAX_REPLIES_PER_COMMENT }})</span>
          </button>
          <!-- Reply Limit Reached (only for top-level comments) -->
          <span 
            v-else-if="!comment.parentId && replyCount >= MAX_REPLIES_PER_COMMENT" 
            class="flex items-center space-x-1 text-gray-400 cursor-not-allowed"
            :title="`此评论已达到最大回复数量（${MAX_REPLIES_PER_COMMENT}条）`"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6" />
            </svg>
            <span>回复已满</span>
            <span class="text-xs">({{ replyCount }}/{{ MAX_REPLIES_PER_COMMENT }})</span>
          </span>
        </div>

        <!-- Child Comments -->
        <div v-if="comment.replies && comment.replies.length > 0" class="mt-4 space-y-4">
          <CommentItem
            v-for="child in comment.replies"
            :key="child.id"
            :comment="child"
            :current-user-id="currentUserId"
            :is-logged-in="isLoggedIn"
            :is-anonymous-enabled="isAnonymousEnabled"
            :is-like-enabled="isLikeEnabled"
            :article-id="articleId"
            @reply="$emit('reply', $event)"
            @delete="$emit('delete', $event)"
            @refresh="$emit('refresh')"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import moment from 'moment'
import { likeComment, unlikeComment } from '@/api/frontend/comment'
import { ElMessage } from 'element-plus'

const props = defineProps({
  comment: {
    type: Object,
    required: true
  },
  currentUserId: {
    type: Number,
    default: null
  },
  isLoggedIn: {
    type: Boolean,
    default: false
  },
  isAnonymousEnabled: {
    type: Boolean,
    default: false
  },
  isLikeEnabled: {
    type: Boolean,
    default: true
  },
  articleId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['reply', 'delete', 'refresh'])

// Constants
const MAX_REPLIES_PER_COMMENT = 10

// Computed
const canDelete = computed(() => {
  return props.isLoggedIn && props.currentUserId === props.comment.userId
})

const replyCount = computed(() => {
  return props.comment.replies?.length || 0
})

const canReply = computed(() => {
  // Allow reply to both top-level comments and their replies
  // But all replies go under the top-level comment (flat structure)
  
  // For nested replies (this is already a reply to another comment)
  // Check the parent comment's reply count instead
  if (props.comment.parentId) {
    // This is a nested reply, we can reply to it but need to check parent's limit
    // However, we don't have access to parent's data here
    // So we allow the reply, and the backend will enforce the limit
    return props.isLoggedIn || props.isAnonymousEnabled
  }
  
  // For top-level comments, check if reply limit is reached
  if (replyCount.value >= MAX_REPLIES_PER_COMMENT) {
    return false
  }
  
  // Check if user can comment (logged in or anonymous enabled)
  return props.isLoggedIn || props.isAnonymousEnabled
})

// Methods
function formatDate(timestamp) {
  if (!timestamp) return ''
  const now = moment()
  const commentTime = moment(timestamp)
  const diff = now.diff(commentTime, 'days')
  
  if (diff === 0) {
    const hourDiff = now.diff(commentTime, 'hours')
    if (hourDiff === 0) {
      const minuteDiff = now.diff(commentTime, 'minutes')
      if (minuteDiff === 0) {
        return '刚刚'
      }
      return `${minuteDiff} 分钟前`
    }
    return `${hourDiff} 小时前`
  } else if (diff === 1) {
    return '昨天'
  } else if (diff < 7) {
    return `${diff} 天前`
  }
  return commentTime.format('YYYY-MM-DD HH:mm')
}

async function handleLike() {
  if (!props.isLikeEnabled) {
    ElMessage.warning('点赞功能已关闭')
    return
  }
  
  if (!props.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    if (props.comment.isLiked) {
      await unlikeComment(props.comment.id)
      props.comment.isLiked = false
      props.comment.likeCount = Math.max(0, (props.comment.likeCount || 0) - 1)
      ElMessage.success('已取消点赞')
    } else {
      await likeComment(props.comment.id)
      props.comment.isLiked = true
      props.comment.likeCount = (props.comment.likeCount || 0) + 1
      ElMessage.success('点赞成功')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败')
  }
}

function handleReply() {
  // Only check reply limit for top-level comments
  if (!props.comment.parentId && replyCount.value >= MAX_REPLIES_PER_COMMENT) {
    ElMessage.warning(`此评论已达到最大回复数量（${MAX_REPLIES_PER_COMMENT}条）`)
    return
  }
  emit('reply', props.comment)
}

function handleDelete() {
  emit('delete', props.comment.id)
}

function handleAvatarError(event) {
  event.target.src = `${import.meta.env.BASE_URL}default-avatar.svg`
}
</script>

<style scoped>
.comment-item {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
