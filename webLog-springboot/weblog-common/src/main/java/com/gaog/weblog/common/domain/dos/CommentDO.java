package com.gaog.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Comment entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_comment")
public class CommentDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * Article ID
     */
    private Long articleId;
    
    /**
     * User ID (null for anonymous comments)
     */
    private Long userId;
    
    /**
     * Parent comment ID (null for top-level comments)
     */
    private Long parentId;
    
    /**
     * Reply to comment ID (for nested replies)
     */
    private Long replyToId;
    
    /**
     * Reply to user ID
     */
    private Long replyToUserId;
    
    /**
     * Commenter nickname (for anonymous or display)
     */
    private String nickname;
    
    /**
     * Commenter email (for anonymous)
     */
    private String email;
    
    /**
     * Commenter website (optional)
     */
    private String website;
    
    /**
     * Comment content
     */
    private String content;
    
    /**
     * Commenter avatar URL
     */
    private String avatar;
    
    /**
     * IP address
     */
    private String ipAddress;
    
    /**
     * Status: 0-pending review, 1-approved, 2-rejected, 3-deleted
     */
    private Integer status;
    
    /**
     * Like count
     */
    private Long likeCount;
    
    /**
     * Is top comment
     */
    private Boolean isTop;
    
    /**
     * Create time
     */
    private LocalDateTime createTime;
    
    /**
     * Update time
     */
    private LocalDateTime updateTime;
    
    /**
     * Logical delete flag
     */
    private Boolean isDeleted;
}
