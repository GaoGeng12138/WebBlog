package com.gaog.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.common.domain.dos.ArticleDO;
import com.gaog.weblog.common.domain.dos.BlogSettingDO;
import com.gaog.weblog.common.domain.dos.CommentDO;
import com.gaog.weblog.common.domain.dos.UserDO;
import com.gaog.weblog.common.domain.dos.UserLikeCommentDO;
import com.gaog.weblog.common.domain.mapper.ArticleMapper;
import com.gaog.weblog.common.domain.mapper.CommentMapper;
import com.gaog.weblog.common.domain.mapper.SiteSettingMapper;
import com.gaog.weblog.common.domain.mapper.UserLikeCommentMapper;
import com.gaog.weblog.common.domain.mapper.UserMapper;
import com.gaog.weblog.common.enums.CommentStatusEnum;
import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.exception.BizException;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.jwt.utils.SecurityContextUtil;
import com.gaog.weblog.web.model.vo.comment.AddCommentReqVO;
import com.gaog.weblog.web.model.vo.comment.DeleteCommentReqVO;
import com.gaog.weblog.web.model.vo.comment.FindArticleCommentsReqVO;
import com.gaog.weblog.web.model.vo.comment.FindCommentDetailRspVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comment Service Implementation
 */
@Service
@Slf4j
public class CommentServiceImpl implements com.gaog.weblog.web.service.CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SiteSettingMapper siteSettingMapper;

    @Autowired
    private UserLikeCommentMapper userLikeCommentMapper;

    @Autowired(required = false)
    private HttpServletRequest request;

    @Override
    public Response addComment(AddCommentReqVO addCommentReqVO) {
        // Check if comment feature is enabled
        BlogSettingDO setting = siteSettingMapper.findSingleton();
        if (setting != null && Boolean.FALSE.equals(setting.getCommentEnabled())) {
            throw new BizException(ResponseCodeEnum.COMMENT_DISABLED);
        }

        // Verify article exists
        Long articleId = addCommentReqVO.getArticleId();
        ArticleDO article = articleMapper.selectById(articleId);
        if (article == null || article.getIsDeleted()) {
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // Get current user (if logged in)
        Long userId = null;
        String nickname = addCommentReqVO.getNickname();
        String email = addCommentReqVO.getEmail();
        String avatar = null;

        try {
            userId = SecurityContextUtil.getCurrentUserId();
            // If user is logged in, get user info
            if (userId != null) {
                UserDO user = userMapper.selectById(userId);
                if (user != null) {
                    nickname = user.getNickname() != null ? user.getNickname() : user.getUsername();
                    email = user.getEmail();
                    avatar = user.getAvatar();
                }
            }
        } catch (Exception e) {
            // User not logged in, check if anonymous comments are allowed
            if (setting != null && Boolean.FALSE.equals(setting.getAnonymousCommentEnabled())) {
                throw new BizException(ResponseCodeEnum.ANONYMOUS_COMMENT_DISABLED);
            }

            // For anonymous comments, require nickname and email
            if (StringUtils.isBlank(nickname) || StringUtils.isBlank(email)) {
                throw new BizException(ResponseCodeEnum.COMMENT_REQUIRE_INFO);
            }
        }

        // Get reply-to user nickname if this is a reply
        Long replyToUserId = null;
        if (addCommentReqVO.getReplyToId() != null) {
            CommentDO replyToComment = commentMapper.selectById(addCommentReqVO.getReplyToId());
            if (replyToComment != null) {
                replyToUserId = replyToComment.getUserId();
            }
        }

        // Get IP address
        String ipAddress = getClientIpAddress();

        // Determine initial status based on settings
        Integer status = CommentStatusEnum.APPROVED.getCode(); // Default: approved
        if (setting != null && Boolean.TRUE.equals(setting.getCommentReviewRequired())) {
            status = CommentStatusEnum.PENDING.getCode(); // Need review
        }

        // Build comment entity
        CommentDO comment = CommentDO.builder()
                .articleId(articleId)
                .userId(userId)
                .parentId(addCommentReqVO.getParentId())
                .replyToId(addCommentReqVO.getReplyToId())
                .replyToUserId(replyToUserId)
                .nickname(nickname)
                .email(email)
                .website(addCommentReqVO.getWebsite())
                .content(addCommentReqVO.getContent())
                .avatar(avatar)
                .ipAddress(ipAddress)
                .status(status)
                .likeCount(0L)
                .isTop(false)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();

        commentMapper.insert(comment);

        String message = status.equals(CommentStatusEnum.PENDING.getCode())
                ? "Comment submitted, pending review"
                : "Comment added successfully";
        return Response.success(message);
    }

    @Override
    public PageResponse findArticleComments(FindArticleCommentsReqVO findArticleCommentsReqVO) {
        Long articleId = findArticleCommentsReqVO.getArticleId();
        Long current = findArticleCommentsReqVO.getCurrent();
        Long size = findArticleCommentsReqVO.getSize();

        // Get all approved comments for this article
        List<CommentDO> allComments = commentMapper.selectByArticleId(articleId);

        if (CollectionUtils.isEmpty(allComments)) {
            return PageResponse.success(null, null);
        }

        // Get current logged-in user ID
        Long currentUserId = null;
        try {
            currentUserId = SecurityContextUtil.getCurrentUserId();
        } catch (Exception e) {
            // Not logged in
        }

        // Get article author ID
        ArticleDO article = articleMapper.selectById(articleId);
        Long articleAuthorId = article != null ? article.getUserId() : null;

        // Build comment tree structure
        Map<Long, FindCommentDetailRspVO> commentMap = new HashMap<>();
        List<FindCommentDetailRspVO> topLevelComments = new ArrayList<>();

        // First pass: convert all comments to VO
        for (CommentDO comment : allComments) {
            FindCommentDetailRspVO vo = buildCommentVO(comment, articleAuthorId);
            commentMap.put(comment.getId(), vo);

            if (comment.getParentId() == null) {
                topLevelComments.add(vo);
            }
        }

        // Second pass: build tree structure
        for (CommentDO comment : allComments) {
            if (comment.getParentId() != null) {
                FindCommentDetailRspVO parentVO = commentMap.get(comment.getParentId());
                if (parentVO != null) {
                    if (parentVO.getReplies() == null) {
                        parentVO.setReplies(new ArrayList<>());
                    }
                    parentVO.getReplies().add(commentMap.get(comment.getId()));
                }
            }
        }

        // Manual pagination for top-level comments
        int start = (int) ((current - 1) * size);
        int end = (int) Math.min(start + size, topLevelComments.size());
        List<FindCommentDetailRspVO> pagedComments = topLevelComments.subList(start, end);

        Page<FindCommentDetailRspVO> page = new Page<>(current, size);
        page.setRecords(pagedComments);
        page.setTotal(topLevelComments.size());

        return PageResponse.success(page, pagedComments);
    }

    @Override
    public Response deleteComment(DeleteCommentReqVO deleteCommentReqVO) {
        Long commentId = deleteCommentReqVO.getCommentId();
        CommentDO comment = commentMapper.selectById(commentId);

        if (comment == null || comment.getIsDeleted()) {
            throw new BizException(ResponseCodeEnum.COMMENT_NOT_FOUND);
        }

        // Check permission: user can only delete their own comments
        Long currentUserId = SecurityContextUtil.getCurrentUserId();
        if (!comment.getUserId().equals(currentUserId)) {
            throw new BizException(ResponseCodeEnum.PERMISSION_DENIED);
        }

        // Soft delete
        comment.setIsDeleted(true);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);

        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response likeComment(Long commentId) {
        BlogSettingDO setting = siteSettingMapper.findSingleton();
        if (setting != null && Boolean.FALSE.equals(setting.getLikeEnabled())) {
            return Response.fail(ResponseCodeEnum.LIKE_DISABLED);
        }

        // Get current logged-in user
        Long userId = SecurityContextUtil.getCurrentUserId();


        // Check if comment exists
        CommentDO comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getIsDeleted()) {
            return Response.fail("Comment not found");
        }

        // Check if already liked
        UserLikeCommentDO likeDO = userLikeCommentMapper.selectOne(new LambdaQueryWrapper<UserLikeCommentDO>()
                .eq(UserLikeCommentDO::getUserId, userId)
                .eq(UserLikeCommentDO::getCommentId, commentId));

        if (likeDO != null) {
            return Response.fail("Already liked this comment");
        }

        // Add like record
        UserLikeCommentDO userLikeCommentDO = UserLikeCommentDO.builder()
                .userId(userId)
                .commentId(commentId)
                .build();
        userLikeCommentMapper.insert(userLikeCommentDO);

        // Increment like count
        comment.setLikeCount(comment.getLikeCount() + 1);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);

        return Response.success("Like successfully");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response unlikeComment(Long commentId) {
        BlogSettingDO setting = siteSettingMapper.findSingleton();
        if (setting != null && Boolean.FALSE.equals(setting.getLikeEnabled())) {
            return Response.fail(ResponseCodeEnum.LIKE_DISABLED);
        }

        // Get current logged-in user
        Long userId = SecurityContextUtil.getCurrentUserId();

        // Check if comment exists
        CommentDO comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getIsDeleted()) {
            return Response.fail("Comment not found");
        }

        // Delete like record
        int deleted = userLikeCommentMapper.delete(new LambdaQueryWrapper<UserLikeCommentDO>()
                .eq(UserLikeCommentDO::getUserId, userId)
                .eq(UserLikeCommentDO::getCommentId, commentId));

        if (deleted == 0) {
            return Response.fail("You have not liked this comment");
        }

        // Decrement like count
        if (comment.getLikeCount() > 0) {
            comment.setLikeCount(comment.getLikeCount() - 1);
            comment.setUpdateTime(LocalDateTime.now());
            commentMapper.updateById(comment);
        }

        return Response.success("Unlike successfully");
    }

    /**
     * Build comment VO
     */
    private FindCommentDetailRspVO buildCommentVO(CommentDO comment, Long articleAuthorId) {
        // Get reply-to user nickname if applicable
        String replyToNickname = null;
        if (comment.getReplyToId() != null) {
            CommentDO replyToComment = commentMapper.selectById(comment.getReplyToId());
            if (replyToComment != null) {
                UserDO replyToUser = replyToComment.getUserId() == null ? null : userMapper.selectById(replyToComment.getUserId());
                replyToNickname = resolveCommentDisplayName(replyToComment, replyToUser, true);
            }
        }

        UserDO commenter = comment.getUserId() == null ? null : userMapper.selectById(comment.getUserId());
        boolean isUserDeleted = comment.getUserId() != null
                && (commenter == null || Boolean.TRUE.equals(commenter.getIsDeleted()));

        // Check if commenter is article author
        Boolean isAuthor = comment.getUserId() != null
                && articleAuthorId != null
                && comment.getUserId().equals(articleAuthorId);

        return FindCommentDetailRspVO.builder()
                .id(comment.getId())
                .articleId(comment.getArticleId())
                .userId(comment.getUserId())
                .parentId(comment.getParentId())
                .replyToId(comment.getReplyToId())
                .replyToNickname(replyToNickname)
                .nickname(resolveCommentDisplayName(comment, commenter, false))
                .avatar(comment.getAvatar())
                .isUserDeleted(isUserDeleted)
                .content(comment.getContent())
                .website(comment.getWebsite())
                .likeCount(comment.getLikeCount())
                .isTop(comment.getIsTop())
                .createTime(comment.getCreateTime())
                .isAuthor(isAuthor)
                .build();
    }

    private String resolveCommentDisplayName(CommentDO comment, UserDO commenter, boolean deletedAsLabel) {
        if (comment == null) {
            return deletedAsLabel ? "已注销用户" : "匿名用户";
        }

        if (comment.getUserId() == null) {
            if (StringUtils.isNotBlank(comment.getNickname())) {
                return comment.getNickname();
            }
            return "匿名用户";
        }

        if (commenter == null || Boolean.TRUE.equals(commenter.getIsDeleted())) {
            if (deletedAsLabel) {
                return "已注销用户";
            }
            if (StringUtils.isNotBlank(comment.getNickname())) {
                return comment.getNickname();
            }
            return "已注销用户";
        }

        if (StringUtils.isNotBlank(commenter.getNickname())) {
            return commenter.getNickname();
        }

        if (StringUtils.isNotBlank(commenter.getUsername())) {
            return commenter.getUsername();
        }

        return StringUtils.isNotBlank(comment.getNickname()) ? comment.getNickname() : "匿名用户";
    }

    /**
     * Get client IP address
     */
    private String getClientIpAddress() {
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
