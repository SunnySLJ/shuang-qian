-- 小云雀视频模型初始化
-- 执行前请确认 ai_model 表存在

INSERT INTO `ai_model` (`id`, `name`, `model`, `platform`, `type`, `sort`, `status`, `api_key_id`)
VALUES (106, '小云雀营销成片', 'xiao-niao-marketing-video', 'XiaoNiao', 4, 110, 0, NULL)
ON DUPLICATE KEY UPDATE
`name` = VALUES(`name`),
`model` = VALUES(`model`),
`platform` = VALUES(`platform`),
`type` = VALUES(`type`),
`sort` = VALUES(`sort`),
`status` = VALUES(`status`);
