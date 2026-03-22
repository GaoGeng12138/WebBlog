-- 旧用户默认启用状态修复
-- 保证历史账号在新增启用状态字段后仍然可以正常登录
UPDATE `t_user`
SET `is_enabled` = 1
WHERE `is_deleted` = 0;
