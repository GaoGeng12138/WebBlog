package com.gaog.weblog.web.service;

import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;

public interface FavoriteService {
    /**
     * 收藏文章
     *
     * @param articleId 文章ID
     * @return
     */
    Response favoriteArticle(Long articleId);

    /**
     * 取消收藏文章
     *
     * @param articleId 文章ID
     * @return
     */
    Response unfavoriteArticle(Long articleId);

    /**
     * 获取用户收藏的文章列表
     *
     * @param current 当前页码
     * @param size 每页大小
     * @return
     */
    Response getFavoriteArticles(Long current, Long size);

    /**
     * 检查文章是否已收藏
     *
     * @param articleId 文章ID
     * @return
     */
    Response isFavorited(Long articleId);
}