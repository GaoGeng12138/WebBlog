package com.gaog.weblog.web.controller;

import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.comment.AddCommentReqVO;
import com.gaog.weblog.web.model.vo.comment.DeleteCommentReqVO;
import com.gaog.weblog.web.model.vo.comment.FindArticleCommentsReqVO;
import com.gaog.weblog.web.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * Comment Controller
 */
@RestController
@Api(tags = "Comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/comment/add")
    @ApiOperation(value = "Add Comment")
    @ApiOperationLog(description = "Add Comment")
    public Response addComment(@RequestBody @Validated AddCommentReqVO addCommentReqVO) {
        return commentService.addComment(addCommentReqVO);
    }

    @PostMapping("/comment/list")
    @ApiOperation(value = "Get Article Comments")
    @ApiOperationLog(description = "Get Article Comments")
    public PageResponse findArticleComments(@RequestBody @Validated FindArticleCommentsReqVO findArticleCommentsReqVO) {
        return commentService.findArticleComments(findArticleCommentsReqVO);
    }

    @PostMapping("/comment/delete")
    @ApiOperation(value = "Delete Comment")
    @ApiOperationLog(description = "Delete Comment")
    public Response deleteComment(@RequestBody @Validated DeleteCommentReqVO deleteCommentReqVO) {
        return commentService.deleteComment(deleteCommentReqVO);
    }

    @PostMapping("/comment/like")
    @ApiOperation(value = "Like Comment")
    @ApiOperationLog(description = "Like Comment")
    public Response likeComment(@RequestParam @NotNull Long commentId) {
        return commentService.likeComment(commentId);
    }

    @DeleteMapping("/comment/like")
    @ApiOperation(value = "Unlike Comment")
    @ApiOperationLog(description = "Unlike Comment")
    public Response unlikeComment(@RequestParam @NotNull Long commentId) {
        return commentService.unlikeComment(commentId);
    }
}
