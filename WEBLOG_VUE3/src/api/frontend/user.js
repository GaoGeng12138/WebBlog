import axios from '@/axios'

// 获取前台用户信息
export function getUserInfoFrontend() {
    return axios.get('/user/info')
}

// 前台用户注册
export function registerFrontend(data) {
    return axios.post('/user/register', data)
}

// 获取用户中心统计数据
export function getUserCenterStatistics() {
    return axios.get('/user/stats')
}

//获取用户中心 评论历史
export function getUserCenterComments(params) {
    return axios.get('/user/comment/history', { params })
}

//获取用户中心 总览/动态
export function getUserCenterOverview(params) {
    return axios.get('/user/dynamic', { params })
}

// 提交用户活跃度评分
export function getActivityScore(data) {
    return axios.post('/user/score/activity-score', data)
}

// 获取用户活跃度统计
export function getActivityStatistics() {
    return axios.get('/user/score/activity-statistics')
}

// 获取用户活跃度趋势
export function getActivityTrend(params) {
    return axios.get('/user/score/activity-trend', { params })
}