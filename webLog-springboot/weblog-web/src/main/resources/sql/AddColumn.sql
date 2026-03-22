-- 站点设置兼容字段补丁
-- 兼容旧库的前台文章列表数量字段
ALTER TABLE `t_blog_settings`
    ADD COLUMN `frontend_article_page_size` int(11) DEFAULT '12' COMMENT '前台文章列表每页数量' AFTER `logo_url`;

-- 活跃度配置字段
ALTER TABLE `t_blog_settings`
    ADD COLUMN `activity_level_rules` text COMMENT '活跃度等级规则JSON' AFTER `frontend_article_page_size`;

ALTER TABLE `t_blog_settings`
    ADD COLUMN `activity_score_rules` text COMMENT '活跃度加分规则JSON' AFTER `activity_level_rules`;

-- 文章发布兼容字段
ALTER TABLE `t_article`
    ADD COLUMN `article_source` tinyint(1) NOT NULL DEFAULT '1' COMMENT '文章来源：1-后台发布，2-前台发布' AFTER `author`;

ALTER TABLE `t_article`
    ADD COLUMN `visibility_scope` tinyint(1) NOT NULL DEFAULT '1' COMMENT '可见范围：1-公开，2-指定用户可见' AFTER `article_source`;

-- 分类展示兼容字段
ALTER TABLE `t_category`
    ADD COLUMN `show_on_front` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否在前台导航展示：1-是，0-否' AFTER `illustrate`;

ALTER TABLE `t_category`
    ADD COLUMN `visibility_scope` tinyint(1) NOT NULL DEFAULT '1' COMMENT '可见范围：1-公开，2-指定用户可见' AFTER `show_on_front`;

-- 旧字段清理
ALTER TABLE `t_blog_settings`
    DROP COLUMN `slogan`;

ALTER TABLE `t_blog_settings`
    DROP COLUMN `contact_email`;

-- 用户注册功能字段兼容
ALTER TABLE `t_user`
    ADD COLUMN `is_enabled` tinyint(2) NOT NULL DEFAULT '0' COMMENT '账户启用状态：0：未启用 1：已启用' AFTER `is_deleted`;

ALTER TABLE `t_user`
    MODIFY COLUMN `avatar` varchar(255) DEFAULT NULL COMMENT '头像';

ALTER TABLE `t_user`
    ADD COLUMN `nickname` varchar(100) DEFAULT NULL COMMENT '昵称' AFTER `avatar`;

ALTER TABLE `t_user`
    ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER `nickname`;

ALTER TABLE `t_user`
    ADD COLUMN `introduction` varchar(255) DEFAULT NULL COMMENT '个性签名' AFTER `email`;
