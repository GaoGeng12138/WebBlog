-- 默认角色
INSERT IGNORE INTO `t_role` (`id`, `name`, `description`, `is_enabled`, `create_time`, `update_time`, `is_deleted`)
VALUES
    (1, 'ROLE_ADMIN', 'Administrator role with full access', 1, NOW(), NOW(), 0),
    (2, 'ROLE_EDITOR', 'Editor role can publish and edit articles', 1, NOW(), NOW(), 0),
    (3, 'ROLE_VISITOR', 'Visitor role can only view articles and comment', 1, NOW(), NOW(), 0);

-- 默认管理员账号
-- 默认用户名：admin
-- 默认密码：123456
INSERT IGNORE INTO `t_user`
(`id`, `username`, `password`, `create_time`, `update_time`, `is_deleted`, `avatar`, `introduction`, `nickname`, `email`, `is_enabled`, `github_url`, `twitter_url`, `weibo_url`)
VALUES
    (1, 'admin', '$2a$10$ZpSUQADtql77ZgNJQ6ljMeJY2CKagbLMBdS8Az4YrEVhn46bVoyuy', NOW(), NOW(), 0, NULL, '系统初始化管理员账号', '管理员', 'admin@example.com', 1, NULL, NULL, NULL);

-- 默认管理员角色绑定
INSERT IGNORE INTO `t_user_role` (`id`, `create_time`, `user_id`, `role_id`)
VALUES
    (1, NOW(), 1, 1);

-- 默认角色权限
INSERT IGNORE INTO `t_role_permission` (`role_id`, `permission_key`, `create_time`)
VALUES
    (1, 'admin:dashboard:view', NOW()),
    (1, 'admin:article:list', NOW()),
    (1, 'admin:article:publish', NOW()),
    (1, 'admin:article:update', NOW()),
    (1, 'admin:article:delete', NOW()),
    (1, 'admin:article:audit', NOW()),
    (1, 'admin:category:list', NOW()),
    (1, 'admin:category:add', NOW()),
    (1, 'admin:category:delete', NOW()),
    (1, 'admin:category:update-front', NOW()),
    (1, 'admin:tag:list', NOW()),
    (1, 'admin:tag:add', NOW()),
    (1, 'admin:tag:delete', NOW()),
    (1, 'admin:user:list', NOW()),
    (1, 'admin:user:create', NOW()),
    (1, 'admin:user:update', NOW()),
    (1, 'admin:user:delete', NOW()),
    (1, 'admin:user:status', NOW()),
    (1, 'admin:user:password', NOW()),
    (1, 'admin:user:role-assign', NOW()),
    (1, 'admin:role:list', NOW()),
    (1, 'admin:role:add', NOW()),
    (1, 'admin:role:update', NOW()),
    (1, 'admin:role:delete', NOW()),
    (1, 'admin:role:permission-assign', NOW()),
    (1, 'admin:visitor:list', NOW()),
    (1, 'admin:setting:view', NOW()),
    (1, 'admin:setting:update', NOW()),
    (2, 'admin:dashboard:view', NOW()),
    (2, 'admin:article:list', NOW()),
    (2, 'admin:article:publish', NOW()),
    (2, 'admin:article:update', NOW()),
    (2, 'admin:category:list', NOW()),
    (2, 'admin:category:add', NOW()),
    (2, 'admin:tag:list', NOW()),
    (2, 'admin:tag:add', NOW()),
    (3, 'admin:dashboard:view', NOW());

-- 默认站点设置
INSERT IGNORE INTO `t_blog_settings`
(`id`, `title`, `description`, `logo_url`, `frontend_article_page_size`, `activity_level_rules`, `activity_score_rules`, `comment_enabled`, `create_time`, `update_time`, `is_deleted`, `like_enabled`, `favorite_enabled`, `user_register_enabled`, `user_publish_enabled`, `article_review_required`, `comment_review_required`, `anonymous_comment_enabled`, `github_show_front`, `github_show_register`, `twitter_show_front`, `twitter_show_register`, `weibo_show_front`, `weibo_show_register`, `github_enabled`, `twitter_enabled`, `weibo_enabled`)
VALUES
    (1, 'ThoughtFlow', '记录思考的流动，沉淀技术文章、开发笔记和项目经验。', NULL, 12, '[{"level":1,"minScore":0,"name":"新手"},{"level":2,"minScore":100,"name":"作者"},{"level":3,"minScore":300,"name":"进阶作者"},{"level":4,"minScore":600,"name":"资深作者"},{"level":5,"minScore":1000,"name":"传奇作者"}]', '[{"type":"article","name":"发布文章","score":10},{"type":"comment","name":"发表评论","score":2},{"type":"favorite","name":"收藏文章","score":1},{"type":"like","name":"点赞评论","score":1},{"type":"login","name":"每日登录","score":1}]', 1, NOW(), NOW(), 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
