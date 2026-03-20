import axios from '@/axios'

/**
 * 获取文章评论列表
 * @param {number} articleId - 文章ID
 * @param {number} pageNum - 页码
 * @param {number} pageSize - 每页数量
 * @returns {Promise}
 */
export function getComments(articleId, pageNum = 1, pageSize = 10) {
    return axios.post('/comment/list', {
        articleId,
        pageNum,
        pageSize
    })
}

/**
 * 发表评论
 * @param {Object} commentData - 评论数据
 * @param {number} commentData.articleId - 文章ID
 * @param {string} commentData.content - 评论内容
 * @param {number} commentData.parentId - 父评论ID（可选，回复评论时使用）
 * @param {number} commentData.replyToId - 被回复用户ID（可选）
 * @returns {Promise}
 */
export function postComment(commentData) {
    return axios.post('/comment/add', commentData, {
        sensitiveFields: ['nickname', 'email']
    })
}

/**
 * 删除评论
 * @param {number} commentId - 评论ID
 * @returns {Promise}
 */
export function deleteComment(commentId) {
    return axios.post('/comment/delete', { commentId })
}

/**
 * 点赞评论
 * @param {number} commentId - 评论ID
 * @returns {Promise}
 */
export function likeComment(commentId) {
    return axios.post('/comment/like', { commentId })
}

/**
 * 取消点赞评论
 * @param {number} commentId - 评论ID
 * @returns {Promise}
 */
export function unlikeComment(commentId) {
    return axios.delete('/comment/like', { commentId })
}
