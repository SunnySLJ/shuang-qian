-- =============================================
-- AI 视频生成相关字段扩展
-- 用于支持文生视频、图生视频、黄金 6 秒、AI 混剪、视频拆解等功能
-- =============================================

USE shuang_pro;

-- 修改 ai_image 表，添加视频生成相关字段
ALTER TABLE `ai_image`
ADD COLUMN `generation_type` TINYINT DEFAULT 1 COMMENT '生成类型：1-生图，2-文生视频，3-图生视频，4-黄金 6 秒，5-AI 混剪，6-视频拆解 - 提取脚本，7-视频拆解 - 分析元素，8-视频拆解 - 生成提示词',
ADD COLUMN `input_image_url` VARCHAR(512) DEFAULT NULL COMMENT '输入图片 URL（图生视频用）',
ADD COLUMN `input_video_url` VARCHAR(512) DEFAULT NULL COMMENT '输入视频 URL（视频拆解用）',
ADD COLUMN `output_url` VARCHAR(512) DEFAULT NULL COMMENT '输出 URL（视频或图片）',
ADD COLUMN `cover_url` VARCHAR(512) DEFAULT NULL COMMENT '视频封面 URL',
ADD COLUMN `duration` INT DEFAULT NULL COMMENT '视频时长（秒）',
ADD COLUMN `task_id` VARCHAR(128) DEFAULT NULL COMMENT '任务编号（舞墨 AI/Midjourney）';

-- 添加索引优化查询性能
CREATE INDEX `idx_generation_type` ON `ai_image` (`generation_type`);
CREATE INDEX `idx_task_id` ON `ai_image` (`task_id`);

-- 更新现有数据的 generation_type 为 1（生图）
UPDATE `ai_image` SET `generation_type` = 1 WHERE `generation_type` IS NULL;
