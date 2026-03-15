package com.gaog.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 站点设置数据对象，单行配置表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_blog_settings")
public class BlogSettingDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 网站信息
    private String title;              // 网站标题
    private String slogan;             // 网站标语
    private String description;        // 网站描述
    
    // 网站资源
    private String logoUrl;            // 网站图标
    private Integer frontendArticlePageSize; // 前台文章列表每页数量
    
    // 社交链接设置（控制是否在前台和注册页显示）
    private Boolean githubEnabled;         // 是否启用GitHub链接功能
    private Boolean githubShowFront;       // 前台展示
    private Boolean githubShowRegister;    // 注册页展示
    
    private Boolean twitterEnabled;        // 是否启用Twitter链接功能
    private Boolean twitterShowFront;      // 前台展示
    private Boolean twitterShowRegister;   // 注册页展示
    
    private Boolean weiboEnabled;          // 是否启用微博链接功能
    private Boolean weiboShowFront;        // 前台展示
    private Boolean weiboShowRegister;     // 注册页展示

    // 权限控制开关
    private Boolean commentEnabled;          // 允许评论
    private Boolean likeEnabled;              // 允许点赞
    private Boolean favoriteEnabled;          // 允许收藏
    private Boolean userRegisterEnabled;      // 允许用户注册
    private Boolean userPublishEnabled;       // 允许用户发布文章
    private Boolean articleReviewRequired;    // 文章需要审核
    private Boolean commentReviewRequired;    // 评论需要审核
    private Boolean anonymousCommentEnabled;  // 允许匿名评论

    private Date createTime;
    private Date updateTime;
    private Boolean isDeleted;
}
