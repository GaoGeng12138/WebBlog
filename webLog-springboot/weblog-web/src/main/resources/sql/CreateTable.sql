-- t_article ddl
CREATE TABLE `t_article`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '文章id',
    `title`       varchar(120) NOT NULL DEFAULT '' COMMENT '文章标题',
    `cover`       varchar(120) NOT NULL DEFAULT '' COMMENT '文章封面',
    `summary`     varchar(160)          DEFAULT '' COMMENT '文章摘要',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
    `is_deleted`  tinyint(2) NOT NULL DEFAULT '0' COMMENT '删除标志位：0：未删除 1：已删除',
    `read_num`    int(11) unsigned NOT NULL DEFAULT '1' COMMENT '被阅读次数',
    `user_id`     bigint(20) NOT NULL COMMENT '用户id',
    `status`      tinyint(1) NOT NULL COMMENT '文章状态：0:待审核 1：审核通过 2：审核未通过 3、未发布 4、已发布 ',
    `author`      varchar(255) NOT NULL COMMENT '作者',
    `article_source` tinyint(1) NOT NULL DEFAULT '1' COMMENT '文章来源：1-后台发布，2-前台发布',
    `visibility_scope` tinyint(1) NOT NULL DEFAULT '1' COMMENT '可见范围：1-公开，2-指定用户可见',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章表';
-- t_article_access_user ddl
CREATE TABLE `t_article_access_user`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `article_id`  bigint(20) unsigned NOT NULL COMMENT '文章id',
    `user_id`     bigint(20) unsigned NOT NULL COMMENT '允许访问的用户id',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_article_user` (`article_id`,`user_id`) USING BTREE,
    KEY          `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章指定访问用户表';
-- t_article_category_rel ddl
CREATE TABLE `t_article_category_rel`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `article_id`  bigint(20) unsigned NOT NULL COMMENT '文章id',
    `category_id` bigint(20) unsigned NOT NULL COMMENT '分类id',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uni_article_id` (`article_id`) USING BTREE,
    KEY           `idx_category_id` (`category_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章所属分类关联表';
-- t_article_content ddl
CREATE TABLE `t_article_content`
(
    `id`         bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '文章内容id',
    `article_id` bigint(20) NOT NULL COMMENT '文章id',
    `content`    text COMMENT '教程正文',
    PRIMARY KEY (`id`) USING BTREE,
    KEY          `idx_article_id` (`article_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章内容表';
-- t_article_read_log ddl
CREATE TABLE `t_article_read_log`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `article_id`  bigint(20) unsigned NOT NULL COMMENT '文章ID',
    `user_id`     bigint(20) unsigned DEFAULT NULL COMMENT '用户ID（登录用户）',
    `ip_address`  varchar(50) NOT NULL COMMENT 'IP地址',
    `read_date`   date        NOT NULL COMMENT '阅读日期',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_article_user_date` (`article_id`,`user_id`,`read_date`) USING BTREE,
    UNIQUE KEY `uk_article_ip_date` (`article_id`,`ip_address`,`read_date`) USING BTREE,
    KEY           `idx_read_date` (`read_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章阅读记录表';
-- t_article_tag_rel ddl
CREATE TABLE `t_article_tag_rel`
(
    `id`         bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `article_id` bigint(20) unsigned NOT NULL COMMENT '文章id',
    `tag_id`     bigint(20) unsigned NOT NULL COMMENT '标签id',
    PRIMARY KEY (`id`) USING BTREE,
    KEY          `idx_article_id` (`article_id`) USING BTREE,
    KEY          `idx_tag_id` (`tag_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章对应标签关联表';
-- t_blog_settings ddl
CREATE TABLE `t_blog_settings`
(
    `id`                        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`                     varchar(255) DEFAULT NULL COMMENT '网站标题',
    `description`               text COMMENT '网站描述',
    `logo_url`                  varchar(512) DEFAULT NULL COMMENT 'Logo URL',
    `frontend_article_page_size` int(11) DEFAULT '12' COMMENT '前台文章列表每页数量',
    `comment_enabled`           tinyint(1) DEFAULT '1' COMMENT '是否启用评论功能',
    `create_time`               datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`               datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`                tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识',
    `like_enabled`              tinyint(1) DEFAULT '1' COMMENT '允许点赞',
    `favorite_enabled`          tinyint(1) DEFAULT '1' COMMENT '允许收藏',
    `user_register_enabled`     tinyint(1) DEFAULT '0' COMMENT '允许用户注册',
    `user_publish_enabled`      tinyint(1) DEFAULT '0' COMMENT '允许用户发布文章',
    `article_review_required`   tinyint(1) DEFAULT '0' COMMENT '文章需要审核',
    `comment_review_required`   tinyint(1) DEFAULT '0' COMMENT '评论需要审核',
    `anonymous_comment_enabled` tinyint(1) DEFAULT '0' COMMENT '允许匿名评论',
    `github_show_front`         tinyint(1) DEFAULT '0' COMMENT 'GitHub前台展示',
    `github_show_register`      tinyint(1) DEFAULT '0' COMMENT 'GitHub注册页展示',
    `twitter_show_front`        tinyint(1) DEFAULT '0' COMMENT 'Twitter前台展示',
    `twitter_show_register`     tinyint(1) DEFAULT '0' COMMENT 'Twitter注册页展示',
    `weibo_show_front`          tinyint(1) DEFAULT '0' COMMENT '微博前台展示',
    `weibo_show_register`       tinyint(1) DEFAULT '0' COMMENT '微博注册页展示',
    `github_enabled`            tinyint(1) DEFAULT '0' COMMENT '是否启用GitHub功能',
    `twitter_enabled`           tinyint(1) DEFAULT '0' COMMENT '是否启用Twitter功能',
    `weibo_enabled`             tinyint(1) DEFAULT '0' COMMENT '是否启用微博功能',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='站点设置表';
-- t_category ddl
CREATE TABLE `t_category`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '分类id',
    `name`        varchar(60)  NOT NULL DEFAULT '' COMMENT '分类名称',
    `illustrate`  varchar(120) NOT NULL DEFAULT '' COMMENT '分类描述',
    `show_on_front` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否在前台导航展示：1-是，0-否',
    `visibility_scope` tinyint(1) NOT NULL DEFAULT '1' COMMENT '可见范围：1-公开，2-指定用户可见',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
    `is_deleted`  tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标志位：0：未删除 1：已删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_name` (`name`) USING BTREE,
    KEY           `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章分类表';
-- t_category_access_user ddl
CREATE TABLE `t_category_access_user`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `category_id` bigint(20) unsigned NOT NULL COMMENT '分类id',
    `user_id`     bigint(20) unsigned NOT NULL COMMENT '允许访问的用户id',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_category_user` (`category_id`,`user_id`) USING BTREE,
    KEY          `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='分类指定访问用户表';
-- t_comment ddl
CREATE TABLE `t_comment`
(
    `id`               bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '评论id',
    `article_id`       bigint(20) unsigned NOT NULL COMMENT '文章id',
    `user_id`          bigint(20) unsigned DEFAULT NULL COMMENT '用户id（匿名评论为null）',
    `parent_id`        bigint(20) unsigned DEFAULT NULL COMMENT '父评论id（顶级评论为null）',
    `reply_to_id`      bigint(20) unsigned DEFAULT NULL COMMENT '回复的评论id',
    `reply_to_user_id` bigint(20) unsigned DEFAULT NULL COMMENT '回复的用户id',
    `nickname`         varchar(60) NOT NULL COMMENT '评论者昵称',
    `email`            varchar(100)         DEFAULT NULL COMMENT '评论者邮箱',
    `website`          varchar(255)         DEFAULT NULL COMMENT '评论者网站',
    `content`          text        NOT NULL COMMENT '评论内容',
    `avatar`           varchar(255)         DEFAULT NULL COMMENT '评论者头像',
    `ip_address`       varchar(50)          DEFAULT NULL COMMENT 'IP地址',
    `status`           tinyint(2) NOT NULL DEFAULT '1' COMMENT '状态：0-待审核 1-已通过 2-已拒绝 3-已删除',
    `like_count`       bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '点赞数',
    `is_top`           tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否置顶：0-否 1-是',
    `create_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`       tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                `idx_article_id` (`article_id`) USING BTREE,
    KEY                `idx_user_id` (`user_id`) USING BTREE,
    KEY                `idx_parent_id` (`parent_id`) USING BTREE,
    KEY                `idx_create_time` (`create_time`) USING BTREE,
    KEY                `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='评论表';
-- t_role ddl
CREATE TABLE `t_role`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Role ID',
    `name`        varchar(60) NOT NULL COMMENT 'Role name (e.g., ROLE_ADMIN, ROLE_EDITOR, ROLE_VIEWER)',
    `description` varchar(255)         DEFAULT NULL COMMENT 'Role description',
    `is_enabled`  tinyint(2) NOT NULL DEFAULT '1' COMMENT 'Role status: 1 = enabled, 0 = disabled',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    `is_deleted`  tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Logical delete: 0 = not deleted, 1 = deleted',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_name` (`name`) USING BTREE,
    KEY           `idx_is_enabled` (`is_enabled`) USING BTREE,
    KEY           `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='角色表';
-- t_role_permission ddl
CREATE TABLE `t_role_permission`
(
    `id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `role_id`        bigint(20) unsigned NOT NULL COMMENT '角色ID',
    `permission_key` varchar(100) NOT NULL COMMENT '权限标识',
    `create_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_role_permission` (`role_id`,`permission_key`) USING BTREE,
    KEY `idx_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='角色权限表';
-- t_statistics_article_pv ddl
CREATE TABLE `t_statistics_article_pv`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `pv_date`     date     NOT NULL COMMENT '被统计的日期',
    `pv_count`    bigint(20) unsigned NOT NULL COMMENT 'pv访问量',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_pv_date` (`pv_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='统计表 - 文章 PV (访问量)';
-- t_tag ddl
CREATE TABLE `t_tag`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '标签id',
    `name`        varchar(60) NOT NULL DEFAULT '' COMMENT '标签名称',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
    `is_deleted`  tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标志位：0：未删除 1：已删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_name` (`name`) USING BTREE,
    KEY           `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章标签表';
-- t_user ddl
CREATE TABLE `t_user`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `username`     varchar(60)  NOT NULL COMMENT '用户名',
    `password`     varchar(60)  NOT NULL COMMENT '密码',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
    `is_deleted`   tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0：未删除 1：已删除',
    `avatar`       varchar(255)          DEFAULT NULL COMMENT '头像',
    `introduction` varchar(255)          DEFAULT NULL COMMENT '个性签名',
    `nickname`     varchar(100)          DEFAULT NULL COMMENT '昵称',
    `email`        varchar(100)          DEFAULT NULL COMMENT '邮箱',
    `is_enabled`   tinyint(1) NOT NULL DEFAULT '1' COMMENT '账户启用状态：0-未启用 1-已启用',
    `github_url`   varchar(200)          DEFAULT NULL COMMENT 'GitHub链接',
    `twitter_url`  varchar(200)          DEFAULT NULL COMMENT 'Twitter链接',
    `weibo_url`    varchar(200)          DEFAULT NULL COMMENT '微博链接',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
-- t_user_activity_score ddl
CREATE TABLE `t_user_activity_score`
(
    `id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`        bigint(20) unsigned NOT NULL COMMENT '用户ID',
    `activity_date`  date     NOT NULL COMMENT '活动日期',
    `daily_score`    int(11) NOT NULL DEFAULT '0' COMMENT '当日总积分',
    `article_count`  int(11) NOT NULL DEFAULT '0' COMMENT '当日发布文章数',
    `comment_count`  int(11) NOT NULL DEFAULT '0' COMMENT '当日评论数',
    `favorite_count` int(11) NOT NULL DEFAULT '0' COMMENT '当日收藏数',
    `login_count`    int(11) NOT NULL DEFAULT '0' COMMENT '当日登录次数',
    `create_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_user_date` (`user_id`,`activity_date`) USING BTREE,
    KEY              `idx_user_id` (`user_id`) USING BTREE,
    KEY              `idx_activity_date` (`activity_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=764 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户活跃度积分表';
-- t_user_favorite_article ddl
CREATE TABLE `t_user_favorite_article`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`     bigint(20) unsigned NOT NULL COMMENT '用户id',
    `article_id`  bigint(20) unsigned NOT NULL COMMENT '文章id',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_user_article` (`user_id`,`article_id`) USING BTREE,
    KEY           `idx_user_id` (`user_id`) USING BTREE,
    KEY           `idx_article_id` (`article_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户收藏文章关系表';
-- t_user_like_comment ddl
CREATE TABLE `t_user_like_comment`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`     bigint(20) unsigned NOT NULL COMMENT '用户id',
    `comment_id`  bigint(20) unsigned NOT NULL COMMENT '评论id',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_user_comment` (`user_id`,`comment_id`) USING BTREE,
    KEY           `idx_user_id` (`user_id`) USING BTREE,
    KEY           `idx_comment_id` (`comment_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户点赞评论关系表';
-- t_user_role ddl
CREATE TABLE `t_user_role`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `user_id`     bigint(20) unsigned DEFAULT NULL,
    `role_id`     bigint(20) unsigned DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_user_role` (`user_id`,`role_id`) USING BTREE,
    KEY           `idx_user_id` (`user_id`) USING BTREE,
    KEY           `idx_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户角色表';
-- t_visitor_log ddl
CREATE TABLE `t_visitor_log`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`     bigint(20) unsigned DEFAULT NULL COMMENT '用户ID（登录用户）',
    `ip_address`  varchar(50) NOT NULL COMMENT 'IP地址',
    `visit_date`  date        NOT NULL COMMENT '访问日期',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_ip_date` (`ip_address`,`visit_date`) USING BTREE,
    UNIQUE KEY `uk_user_date` (`user_id`,`visit_date`) USING BTREE,
    KEY           `idx_visit_date` (`visit_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='访客记录表';

