-- =============================================
-- 舞墨 AI 模型初始化数据
-- 用于支持文生视频、图生视频、黄金 6 秒、AI 混剪、视频拆解等功能
-- =============================================

USE shuang_pro;

-- 插入舞墨 AI 平台模型数据
-- 模型类型：1-图片生成，2-视频生成
INSERT INTO `ai_model` (`id`, `name`, `model`, `platform`, `type`, `sort`, `status`, `created_at`, `updated_at`) VALUES
(100, '可灵文生视频', 'Keling', 'WuMo', 2, 100, 1, NOW(), NOW()),
(101, '可灵图生视频', 'Keling', 'WuMo', 2, 99, 1, NOW(), NOW()),
(102, '海螺视频生成', 'Seeware', 'WuMo', 2, 98, 1, NOW(), NOW()),
(103, 'Runway 专业版', 'Runway', 'WuMo', 2, 97, 1, NOW(), NOW()),
(104, 'Pika 创意视频', 'Pika', 'WuMo', 2, 96, 1, NOW(), NOW());

-- 如果 ai_model 表不存在，先创建表结构
-- CREATE TABLE IF NOT EXISTS `ai_model` (
--   `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
--   `name` varchar(50) NOT NULL COMMENT '模型名称',
--   `model` varchar(50) NOT NULL COMMENT '模型标识',
--   `platform` varchar(20) NOT NULL COMMENT '平台类型',
--   `type` tinyint NOT NULL DEFAULT 1 COMMENT '模型类型：1-图片生成，2-视频生成',
--   `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
--   `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
--   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   PRIMARY KEY (`id`),
--   KEY `idx_platform` (`platform`),
--   KEY `idx_type` (`type`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 模型表';
