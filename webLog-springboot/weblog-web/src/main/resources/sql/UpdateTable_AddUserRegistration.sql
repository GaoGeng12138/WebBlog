-- 用户注册功能数据库升级脚本
-- 为现有用户表添加启用状态字段

-- 1. 添加 is_enabled 字段（默认为未启用）
ALTER TABLE `t_user` ADD COLUMN `is_enabled` tinyint(2) NOT NULL DEFAULT '0' COMMENT '账户启用状态：0：未启用 1：已启用' AFTER `is_deleted`;

-- 2. 修改 avatar、nickname、email、introduction 字段为可空（支持注册时不填写）
ALTER TABLE `t_user` MODIFY COLUMN `avatar` varchar(255) DEFAULT NULL COMMENT '头像';
ALTER TABLE `t_user` ADD COLUMN `nickname` varchar(100) DEFAULT NULL COMMENT '昵称' AFTER `avatar`;
ALTER TABLE `t_user` ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER `nickname`;
ALTER TABLE `t_user` ADD COLUMN `introduction` varchar(255) DEFAULT NULL COMMENT '个性签名' AFTER `email`;

-- 3. 将现有用户的启用状态设置为已启用（避免影响现有用户登录）
UPDATE `t_user` SET `is_enabled` = 1 WHERE `is_deleted` = 0;
