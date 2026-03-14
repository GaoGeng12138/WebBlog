import axios from "@/axios";

// 获取仪表盘统计数据{文章数、分类数、标签数、评论数}
export function getDashboardStats() {
    return axios.get("/admin/dashboard/stats")
}

// 获取最新文章列表
export function getLatestArticles() {
    return axios.get("/admin/dashboard/latest-articles")
}

// 获取文章发布趋势数据
export function getArticleStats() {
    return axios.get("/admin/dashboard/article-publish-trend")
}

// 获取浏览量趋势数据
export function getPvTrend() {
    return axios.get("/admin/dashboard/pv-trend")
}

// 获取用户活动趋势数据
export function getUserActivityTrend(data) {
    return axios.post("/admin/dashboard/user-activity-trend", data)
}