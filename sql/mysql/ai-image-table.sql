-- =============================================
-- AI 图片生成表
-- =============================================

DROP TABLE IF EXISTS `ai_image`;
CREATE TABLE `ai_image` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id`             BIGINT       NOT NULL COMMENT '用户 ID',
    `model_id`            BIGINT       NOT NULL COMMENT '模型 ID',
    `prompt`              TEXT         COMMENT '提示词',
    `negative_prompt`     TEXT         COMMENT '负向提示词',
    `style`               VARCHAR(32)  DEFAULT NULL COMMENT '风格',
    `size`                VARCHAR(16)  DEFAULT NULL COMMENT '尺寸',
    `status`              TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-处理中，1-成功，2-失败',
    `pic_url`             VARCHAR(512) DEFAULT NULL COMMENT '生成的图片 URL',
    `error_message`       VARCHAR(512) DEFAULT NULL COMMENT '错误信息',
    `task_id`             VARCHAR(128) DEFAULT NULL COMMENT '任务 ID',
    `finish_time`         DATETIME     DEFAULT NULL COMMENT '完成时间',
    `public_status`       BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否公开',
    `generation_type`     TINYINT      DEFAULT 1 COMMENT '生成类型：1-生图，2-文生视频，3-图生视频，4-黄金 6 秒，5-AI 混剪，6-视频拆解 - 提取脚本，7-视频拆解 - 分析元素，8-视频拆解 - 生成提示词',
    `input_image_url`     VARCHAR(512) DEFAULT NULL COMMENT '输入图片 URL（图生视频用）',
    `input_video_url`     VARCHAR(512) DEFAULT NULL COMMENT '输入视频 URL（视频拆解用）',
    `output_url`          VARCHAR(512) DEFAULT NULL COMMENT '输出 URL（视频或图片）',
    `cover_url`           VARCHAR(512) DEFAULT NULL COMMENT '视频封面 URL',
    `duration`            INT          DEFAULT NULL COMMENT '视频时长（秒）',
    `options`             JSON         DEFAULT NULL COMMENT '扩展选项',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_model_id` (`model_id`),
    KEY `idx_status` (`status`),
    KEY `idx_generation_type` (`generation_type`),
    KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 图片生成表';
