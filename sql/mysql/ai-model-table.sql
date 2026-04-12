-- =============================================
-- AI 模型表
-- =============================================

DROP TABLE IF EXISTS `ai_model`;
CREATE TABLE `ai_model` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name`        VARCHAR(50)  NOT NULL COMMENT '模型名称',
    `model`       VARCHAR(50)  NOT NULL COMMENT '模型标识',
    `platform`    VARCHAR(20)  NOT NULL COMMENT '平台类型',
    `type`        TINYINT      NOT NULL DEFAULT 2 COMMENT '模型类型：1-对话，2-图片，3-语音，4-视频，5-向量，6-重排序',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-启用，1-禁用',
    `api_key_id`  BIGINT       DEFAULT NULL COMMENT '关联的 API Key ID',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_platform` (`platform`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 模型表';

-- 插入舞墨 AI 平台模型数据
INSERT INTO `ai_model` (`id`, `name`, `model`, `platform`, `type`, `sort`, `status`, `api_key_id`) VALUES
(100, '可灵文生视频', 'Keling', 'WuMo', 4, 100, 0, NULL),
(106, '小云雀营销成片', 'xiao-niao-marketing-video', 'XiaoNiao', 4, 110, 0, NULL),
(101, '可灵图生视频', 'Keling', 'WuMo', 4, 99, 0, NULL),
(102, '海螺视频生成', 'Seeware', 'WuMo', 4, 98, 0, NULL),
(103, 'Runway 专业版', 'Runway', 'WuMo', 4, 97, 0, NULL),
(104, 'Pika 创意视频', 'Pika', 'WuMo', 4, 96, 0, NULL),
(105, 'SD 生图', 'sd-v1.5', 'WuMo', 2, 50, 0, NULL);
