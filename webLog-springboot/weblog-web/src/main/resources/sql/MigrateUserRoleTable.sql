-- Migration script to convert t_user_role table from (username, role) to (user_id, role_id)
-- This script should be run after ensuring all data is migrated

-- Step 1: Add new columns to existing table (if table exists)
-- ALTER TABLE `t_user_role` ADD COLUMN `user_id` BIGINT(20) UNSIGNED DEFAULT NULL;
-- ALTER TABLE `t_user_role` ADD COLUMN `role_id` BIGINT(20) UNSIGNED DEFAULT NULL;

-- Step 2: Migrate data from username/role to user_id/role_id
-- UPDATE `t_user_role` tr
-- SET tr.`user_id` = (SELECT u.`id` FROM `t_user` u WHERE u.`username` = tr.`username`),
--     tr.`role_id` = (SELECT r.`id` FROM `t_role` r WHERE r.`name` = tr.`role`)
-- WHERE tr.`user_id` IS NULL OR tr.`role_id` IS NULL;

-- Step 3: Drop old columns (after verifying migration was successful)
-- ALTER TABLE `t_user_role` DROP COLUMN `username`;
-- ALTER TABLE `t_user_role` DROP COLUMN `role`;

-- Step 4: Add indexes on new columns
-- ALTER TABLE `t_user_role` ADD KEY `idx_user_id` (`user_id`);
-- ALTER TABLE `t_user_role` ADD KEY `idx_role_id` (`role_id`);

-- Alternative: Create new table and migrate data in one go
-- CREATE TABLE `t_user_role_new` (
--     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
--     `user_id` bigint(20) unsigned NOT NULL,
--     `role_id` bigint(20) unsigned NOT NULL,
--     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     PRIMARY KEY (`id`),
--     KEY `idx_user_id` (`user_id`),
--     KEY `idx_role_id` (`role_id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- 
-- INSERT INTO `t_user_role_new` (user_id, role_id, create_time)
-- SELECT u.id, r.id, tr.create_time
-- FROM `t_user_role` tr
-- JOIN `t_user` u ON u.username = tr.username
-- JOIN `t_role` r ON r.name = tr.role;
-- 
-- DROP TABLE `t_user_role`;
-- RENAME TABLE `t_user_role_new` TO `t_user_role`;

-- For new installations, use the schema from CreateTable.sql which already has the correct structure
