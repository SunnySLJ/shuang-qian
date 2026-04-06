-- =============================================
-- AI 视频生成相关字段扩展
-- 用于支持文生视频、图生视频、黄金 6 秒、AI 混剪、视频拆解等功能
-- =============================================

USE shuang_pro;

-- 添加字段（如果已存在会返回错误，不影响执行）
ALTER TABLE `ai_image` ADD COLUMN `generation_type` TINYINT DEFAULT 1 COMMENT '生成类型：1-生图，2-文生视频，3-图生视频，4-黄金 6 秒，5-AI 混剪，6-视频拆解';
ALTER TABLE `ai_image` ADD COLUMN `input_image_url` VARCHAR(512) DEFAULT NULL COMMENT '输入图片 URL';
ALTER TABLE `ai_image` ADD COLUMN `input_video_url` VARCHAR(512) DEFAULT NULL COMMENT '输入视频 URL';
ALTER TABLE `ai_image` ADD COLUMN `output_url` VARCHAR(512) DEFAULT NULL COMMENT '输出 URL';
ALTER TABLE `ai_image` ADD COLUMN `cover_url` VARCHAR(512) DEFAULT NULL COMMENT '视频封面 URL';
ALTER TABLE `ai_image` ADD COLUMN `duration` INT DEFAULT NULL COMMENT '视频时长';
ALTER TABLE `ai_image` ADD COLUMN `task_id` VARCHAR(128) DEFAULT NULL COMMENT '任务编号';

-- 添加索引
ALTER TABLE `ai_image` ADD INDEX `idx_generation_type` (`generation_type`);
ALTER TABLE `ai_image` ADD INDEX `idx_task_id` (`task_id`);

-- 更新现有数据的 generation_type 为 1（生图）
UPDATE `ai_image` SET `generation_type` = 1 WHERE `generation_type` IS NULL;
