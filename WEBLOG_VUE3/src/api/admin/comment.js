import axios from '@/axios'

// 获取评论分页数据
export function getCommentPageList(data) {
  return axios.post('/admin/comment/list', data)
}

// 审核评论
export function auditComment(data) {
  return axios.post('/admin/comment/audit', data)
}

// 删除评论
export function deleteComment(commentId) {
  return axios.post('/admin/comment/delete', { commentId })
}

// 置顶/取消置顶评论
export function setCommentTop(data) {
  return axios.post('/admin/comment/top', data)
}
