package com.gaog.weblog.admin.service;

import com.gaog.weblog.admin.model.vo.article.AuditArticleReqVO;
import com.gaog.weblog.admin.model.vo.article.DeleteArticleReqVO;
import com.gaog.weblog.admin.model.vo.article.FindArticleDetailReqVO;
import com.gaog.weblog.admin.model.vo.article.FindArticlePageListReqVO;
import com.gaog.weblog.admin.model.vo.article.PublishArticleReqVO;
import com.gaog.weblog.admin.model.vo.article.UpdateArticleReqVO;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;

/**
 * @author ZSJ
 * @date 2025/11/25 16:10
 * @description
 */
public interface AdminArticleService {
    /**
     * 发布文章
     * @param publishArticleReqVO
     * @return
     */
    Response publishArticle(PublishArticleReqVO publishArticleReqVO);

    /**
     * 删除文章
     * @param deleteArticleReqVO
     * @return
     */
    Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO);

    /**
     * 查询文章分页数据
     * @param findArticlePageListReqVO
     * @return
     */
    PageResponse findArticlePageList(FindArticlePageListReqVO findArticlePageListReqVO);

    /**
     * 查询文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

    /**
     * 更新文章
     * @param updateArticleReqVO
     * @return
     */
    Response updateArticle(UpdateArticleReqVO updateArticleReqVO);

    /**
     * 审核文章
     * @param aditArticleReqVO
     * @return
     */
    Response auditArticle(AuditArticleReqVO aditArticleReqVO);
}
