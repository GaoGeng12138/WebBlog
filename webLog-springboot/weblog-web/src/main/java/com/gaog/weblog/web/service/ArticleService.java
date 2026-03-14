package com.gaog.weblog.web.service;

import com.gaog.weblog.admin.model.vo.article.PublishArticleReqVO;
import com.gaog.weblog.admin.model.vo.article.UpdateArticleReqVO;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;
import com.gaog.weblog.web.model.vo.article.FindArticleByCategoryReqVO;
import com.gaog.weblog.web.model.vo.article.FindArticleByTagReqVO;
import com.gaog.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.gaog.weblog.web.model.vo.article.FrontendUpdateArticleReqVO;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @Author: gaoge
 * @Date: 2025/12/7 23:17
 * @Version: 1.0
 * @Description:
 */
public interface ArticleService {
    /**
     * 获取首页文章分页数据
     * @param findIndexArticlePageListReqVO
     * @param request
     * @return
     */
    Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO, HttpServletRequest request);
    
    /**
     * 根据分类ID获取文章分页数据
     * @param findArticleByCategoryReqVO
     * @return
     */
    PageResponse findArticlePageListByCategoryId(FindArticleByCategoryReqVO findArticleByCategoryReqVO);
    
    /**
     * 根据标签ID获取文章分页数据
     * @param findArticleByTagReqVO
     * @return
     */
    PageResponse findArticlePageListByTagId(FindArticleByTagReqVO findArticleByTagReqVO);
    
    /**
     * 获取文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

    /**
     * 发布文章
     * @param publishArticleReqVO
     * @return
     */
    Response publishArticle(PublishArticleReqVO publishArticleReqVO);

    /**
     * 更新文章
     * @param updateArticleReqVO
     * @return
     */
    Response updateArticle(FrontendUpdateArticleReqVO updateArticleReqVO);
}