package com.gaog.weblog.web.service;

import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.comment.AddCommentReqVO;
import com.gaog.weblog.web.model.vo.comment.DeleteCommentReqVO;
import com.gaog.weblog.web.model.vo.comment.FindArticleCommentsReqVO;

/**
 * Comment Service
 */
public interface CommentService {

    /**
     * Add comment
     *
     * @param addCommentReqVO Request VO
     * @return Response
     */
    Response addComment(AddCommentReqVO addCommentReqVO);

    /**
     * Get article comments
     *
     * @param findArticleCommentsReqVO Request VO
     * @return Page response
     */
    PageResponse findArticleComments(FindArticleCommentsReqVO findArticleCommentsReqVO);

    /**
     * Delete comment (user can only delete their own comments)
     *
     * @param deleteCommentReqVO Request VO
     * @return Response
     */
    Response deleteComment(DeleteCommentReqVO deleteCommentReqVO);

    /**
     * Like comment
     *
     * @param commentId Comment ID
     * @return Response
     */
    Response likeComment(Long commentId);

    /**
     * Unlike comment
     *
     * @param commentId Comment ID
     * @return Response
     */
    Response unlikeComment(Long commentId);
}
