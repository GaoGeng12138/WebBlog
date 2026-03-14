INSERT INTO `weblog`.`t_user_role` (`id`, `username`, `role`, `create_time`)
VALUES (1, 'admin', 'ROLE_ADMIN', '2023-07-07 01:21:15');
INSERT INTO `weblog`.`t_user_role` (`id`, `username`, `role`, `create_time`)
VALUES (2, 'test', 'ROLE_VISITOR', '2023-07-07 01:23:33');


INSERT INTO `t_role` (`name`, `description`, `is_enabled`, `create_time`, `update_time`, `is_deleted`)
VALUES
    ('ROLE_ADMIN', 'Administrator role with full access', 1, NOW(), NOW(), 0),
    ('ROLE_EDITOR', 'Editor role can publish and edit articles', 1, NOW(), NOW(), 0),
    ('ROLE_VISITOR', 'Visitor role can only view articles and comment', 1, NOW(), NOW(), 0);