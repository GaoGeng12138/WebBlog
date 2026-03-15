import axios from "@/axios";

// 获取文章分页数据
export function getArticlePageList(data) {
    return axios.post("/article/list", data)
}

// 根据分类获取文章分页数据
export function getArticlePageListByCategory(data) {
    return axios.post("/article/list/by-category", data)
}

// 根据标签获取文章分页数据
export function getArticlePageListByTag(data) {
    return axios.post("/article/list/by-tag", data)
}

// 获取文章详情
export function getArticleDetail(articleId) {
    // 后端要求接收 JSON 格式 { "articleId": <id> }
    return axios.post("/article/detail", { articleId })
}

// 获取文章归档列表（按时间汇总）
export function getArticleArchive(params) {
    // params 可选，例如 { keyword }
    return axios.post('/archive/list', params || {})
}

// 发布新文章（前台）
export function publishArticle(data) {
    return axios.post("/article/publish", data)
}

// 更新文章（前台）
export function updateArticle(articleId, data) {
    return axios.put(`/article/${articleId}`, data)
}
