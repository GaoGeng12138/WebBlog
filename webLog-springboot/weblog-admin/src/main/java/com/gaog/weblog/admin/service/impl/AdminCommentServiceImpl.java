package com.gaog.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.admin.model.vo.comment.*;
import com.gaog.weblog.admin.service.AdminCommentService;
import com.gaog.weblog.common.domain.dos.ArticleDO;
import com.gaog.weblog.common.domain.dos.CommentDO;
import com.gaog.weblog.common.domain.mapper.ArticleMapper;
import com.gaog.weblog.common.domain.mapper.CommentMapper;
import com.gaog.weblog.common.enums.CommentStatusEnum;
import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.exception.BizException;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Admin Comment Service Implementation
 */
@Service
@Slf4j
public class AdminCommentServiceImpl implements AdminCommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public PageResponse findCommentPageList(FindCommentPageListReqVO findCommentPageListReqVO) {
        Long current = findCommentPageListReqVO.getCurrent();
        Long size = findCommentPageListReqVO.getSize();
        Long articleId = findCommentPageListReqVO.getArticleId();
        Integer status = findCommentPageListReqVO.getStatus();
        
        Page<CommentDO> commentPage = commentMapper.selectPageList(
                current, size, articleId, status,
                findCommentPageListReqVO.getStartDate(),
                findCommentPageListReqVO.getEndDate()
        );

        List<CommentDO> comments = commentPage.getRecords();
        
        if (CollectionUtils.isEmpty(comments)) {
            return PageResponse.success(commentPage, null);
        }

        // Get article titles
        List<Long> articleIds = comments.stream()
                .map(CommentDO::getArticleId)
                .distinct()
                .collect(Collectors.toList());
        
        List<ArticleDO> articles = articleMapper.selectBatchIds(articleIds);
        Map<Long, String> articleTitleMap = articles.stream()
                .collect(Collectors.toMap(ArticleDO::getId, ArticleDO::getTitle));

        // Convert to VO
        List<FindCommentPageListRspVO> vos = comments.stream()
                .map(comment -> FindCommentPageListRspVO.builder()
                        .id(comment.getId())
                        .articleId(comment.getArticleId())
                        .articleTitle(articleTitleMap.get(comment.getArticleId()))
                        .userId(comment.getUserId())
                        .nickname(comment.getNickname())
                        .email(comment.getEmail())
                        .content(comment.getContent())
                        .ipAddress(comment.getIpAddress())
                        .status(comment.getStatus())
                        .likeCount(comment.getLikeCount())
                        .isTop(comment.getIsTop())
                        .createTime(comment.getCreateTime())
                        .parentId(comment.getParentId())
                        .build())
                .collect(Collectors.toList());

        Page<FindCommentPageListRspVO> resultPage = new Page<>(current, size);
        resultPage.setRecords(vos);
        resultPage.setTotal(commentPage.getTotal());

        return PageResponse.success(resultPage, null);
    }

    @Override
    public Response auditComment(AuditCommentReqVO auditCommentReqVO) {
        Long commentId = auditCommentReqVO.getCommentId();
        Integer status = auditCommentReqVO.getStatus();

        CommentDO comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getIsDeleted()) {
            throw new BizException(ResponseCodeEnum.COMMENT_NOT_FOUND);
        }

        // Validate status
        if (!Objects.equals(status, CommentStatusEnum.APPROVED.getCode()) 
                && !Objects.equals(status, CommentStatusEnum.REJECTED.getCode())) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);
        }

        comment.setStatus(status);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);

        return Response.success();
    }

    @Override
    public Response deleteComment(DeleteCommentReqVO deleteCommentReqVO) {
        Long commentId = deleteCommentReqVO.getCommentId();
        
        CommentDO comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BizException(ResponseCodeEnum.COMMENT_NOT_FOUND);
        }

        // Soft delete
        comment.setIsDeleted(true);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);

        return Response.success();
    }

    @Override
    public Response setCommentTop(SetCommentTopReqVO setCommentTopReqVO) {
        Long commentId = setCommentTopReqVO.getCommentId();
        Boolean isTop = setCommentTopReqVO.getIsTop();

        CommentDO comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getIsDeleted()) {
            throw new BizException(ResponseCodeEnum.COMMENT_NOT_FOUND);
        }

        comment.setIsTop(isTop);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);

        return Response.success();
    }
}
