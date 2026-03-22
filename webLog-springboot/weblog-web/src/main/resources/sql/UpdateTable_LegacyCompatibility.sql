-- Legacy schema compatibility patches for existing databases

-- Older databases may miss this column used by the frontend list page
ALTER TABLE `t_blog_settings`
    ADD COLUMN `frontend_article_page_size` int(11) DEFAULT '12' COMMENT '前台文章列表每页数量' AFTER `logo_url`;

-- Activity configuration fields used by the configurable level/score feature
ALTER TABLE `t_blog_settings`
    ADD COLUMN `activity_level_rules` text COMMENT '活跃度等级规则JSON' AFTER `frontend_article_page_size`;

ALTER TABLE `t_blog_settings`
    ADD COLUMN `activity_score_rules` text COMMENT '活跃度加分规则JSON' AFTER `activity_level_rules`;

-- Article visibility compatibility fields
ALTER TABLE `t_article`
    ADD COLUMN `article_source` tinyint(1) NOT NULL DEFAULT '1' COMMENT '文章来源：1-后台发布，2-前台发布' AFTER `author`;

ALTER TABLE `t_article`
    ADD COLUMN `visibility_scope` tinyint(1) NOT NULL DEFAULT '1' COMMENT '可见范围：1-公开，2-指定用户可见' AFTER `article_source`;

-- Category visibility compatibility fields
ALTER TABLE `t_category`
    ADD COLUMN `show_on_front` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否在前台导航展示：1-是，0-否' AFTER `illustrate`;

ALTER TABLE `t_category`
    ADD COLUMN `visibility_scope` tinyint(1) NOT NULL DEFAULT '1' COMMENT '可见范围：1-公开，2-指定用户可见' AFTER `show_on_front`;

-- Remove deprecated blog setting columns from legacy databases
ALTER TABLE `t_blog_settings`
    DROP COLUMN `slogan`;

ALTER TABLE `t_blog_settings`
    DROP COLUMN `contact_email`;
