package com.gaog.weblog.admin.service;

import com.gaog.weblog.admin.model.vo.comment.*;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;

/**
 * Admin Comment Service
 */
public interface AdminCommentService {

    /**
     * Get comment page list
     *
     * @param findCommentPageListReqVO Request VO
     * @return Page response
     */
    PageResponse findCommentPageList(FindCommentPageListReqVO findCommentPageListReqVO);

    /**
     * Audit comment (approve/reject)
     *
     * @param auditCommentReqVO Request VO
     * @return Response
     */
    Response auditComment(AuditCommentReqVO auditCommentReqVO);

    /**
     * Delete comment
     *
     * @param deleteCommentReqVO Request VO
     * @return Response
     */
    Response deleteComment(DeleteCommentReqVO deleteCommentReqVO);

    /**
     * Set comment top
     *
     * @param setCommentTopReqVO Request VO
     * @return Response
     */
    Response setCommentTop(SetCommentTopReqVO setCommentTopReqVO);
}
