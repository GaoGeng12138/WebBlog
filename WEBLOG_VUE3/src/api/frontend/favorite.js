import axios from "@/axios"; 

// 收藏文章：POST /favorite/article?articleId=xxx
export function collectArticle(articleId) {
  return axios.post("/favorite/article", null, {
    params: { articleId },
  });
}

// 取消收藏：DELETE /favorite/article?articleId=xxx
export function uncollectArticle(articleId) {
  return axios.delete("/favorite/article", {
    params: { articleId },
  });
}

// 检查文章是否已收藏：如果后端也是 @RequestParam，则用 params
// 例如：POST /favorite/check?articleId=xxx
export function isArticleCollected(articleId) {
  return axios.get("/favorite/check", {
    params: { articleId },
  });
}

// 获取用户收藏的文章列表：通常是分页/筛选条件，用 body 传（@RequestBody）
// POST /favorite/list   body: { pageNo, pageSize, ... }
export function getCollectedArticles(data) {
  return axios.post("/favorite/list", data);
}