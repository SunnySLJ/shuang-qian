-- =============================================
-- Shuang-Pro 代理邀请码字段扩展
-- 用于在 system_users 表中添加 invite_code 字段
-- =============================================

-- 选择数据库
USE shuang_pro;

-- 为 system_users 表添加 invite_code 字段
ALTER TABLE `system_users`
ADD COLUMN `invite_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户邀请码' AFTER `mobile`,
ADD UNIQUE KEY `uk_invite_code` (`invite_code`) COMMENT '邀请码唯一索引';

-- 为现有用户生成邀请码（使用用户 ID 作为基础生成）
UPDATE `system_users`
SET `invite_code` = MD5(CONCAT('invite_', id, '_', UNIX_TIMESTAMP()))
WHERE `invite_code` IS NULL;
