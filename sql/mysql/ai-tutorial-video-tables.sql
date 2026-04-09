-- 教程视频相关表
-- Date: 2026-04-07

SET NAMES utf8mb4;

-- ----------------------------
-- Table structure for ai_tutorial_category
-- ----------------------------
DROP TABLE IF EXISTS `ai_tutorial_category`;
CREATE TABLE `ai_tutorial_category` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(64) NOT NULL COMMENT '分类名称',
    `description` varchar(255) COMMENT '分类描述',
    `icon_url` varchar(512) COMMENT '分类图标 URL',
    `sort_order` int DEFAULT 0 COMMENT '排序（越小越前）',
    `video_count` int DEFAULT 0 COMMENT '视频数量',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    INDEX `idx_sort_order` (`sort_order`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教程视频分类表';

-- ----------------------------
-- Table structure for ai_tutorial_video
-- ----------------------------
DROP TABLE IF EXISTS `ai_tutorial_video`;
CREATE TABLE `ai_tutorial_video` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category_id` bigint NOT NULL COMMENT '分类 ID',
    `title` varchar(128) NOT NULL COMMENT '视频标题',
    `description` varchar(512) COMMENT '视频描述',
    `cover_image_url` varchar(512) COMMENT '封面图 URL',
    `video_url` varchar(512) NOT NULL COMMENT '视频 URL',
    `duration_seconds` int DEFAULT 0 COMMENT '视频时长（秒）',
    `view_count` int DEFAULT 0 COMMENT '观看次数',
    `like_count` int DEFAULT 0 COMMENT '点赞次数',
    `is_free` bit(1) DEFAULT b'1' COMMENT '是否免费观看',
    `sort_order` int DEFAULT 0 COMMENT '排序（越小越前）',
    `tags` varchar(255) COMMENT '标签（逗号分隔）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_sort_order` (`sort_order`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教程视频表';

-- ----------------------------
-- 初始化教程分类数据
-- ----------------------------
INSERT INTO `ai_tutorial_category` (`name`, `description`, `icon_url`, `sort_order`) VALUES
('初级入门', '新手入门教程，了解基础功能和使用方法', NULL, 1),
('进阶操作', '进阶操作教程，学习更多高级技巧', NULL, 2),
('高级技巧', '高级技巧教程，掌握专业创作方法', NULL, 3),
('爆款拆解', '爆款拆解教程，学习如何分析热门视频', NULL, 4),
('代理教程', '代理分销教程，了解代理体系和分佣规则', NULL, 5);

-- ----------------------------
-- 初始化示例教程视频数据
-- ----------------------------
INSERT INTO `ai_tutorial_video` (`category_id`, `title`, `description`, `cover_image_url`, `video_url`, `duration_seconds`, `is_free`, `sort_order`, `tags`) VALUES
(1, 'AI 生图基础教程', '从零开始学习如何使用 AI 生成图片', NULL, 'https://example.com/video1.mp4', 120, b'1', 1, '生图,入门,AI'),
(1, 'AI 视频入门指南', '快速了解 AI 视频生成功能', NULL, 'https://example.com/video2.mp4', 180, b'1', 2, '视频,入门,指南'),
(2, '黄金6秒创作技巧', '学习如何制作高质量的6秒短视频', NULL, 'https://example.com/video3.mp4', 240, b'1', 1, '黄金6秒,技巧,创作'),
(2, 'AI 混剪进阶教程', '掌握 AI 混剪的高级用法', NULL, 'https://example.com/video4.mp4', 300, b'0', 2, '混剪,进阶,教程'),
(3, '爆款视频创作秘籍', '揭秘爆款视频的创作方法和技巧', NULL, 'https://example.com/video5.mp4', 360, b'0', 1, '爆款,秘籍,创作'),
(3, '专业级视频调色技巧', '学习专业级视频调色和后期处理', NULL, 'https://example.com/video6.mp4', 480, b'0', 2, '调色,专业,后期'),
(4, '爆款拆解完整流程', '详细介绍爆款拆解的完整流程', NULL, 'https://example.com/video7.mp4', 600, b'1', 1, '拆解,流程,爆款'),
(4, '提示词优化技巧', '学习如何优化提示词获得更好的生成效果', NULL, 'https://example.com/video8.mp4', 420, b'0', 2, '提示词,优化,技巧'),
(5, '代理体系介绍', '详细了解代理分销体系和等级规则', NULL, 'https://example.com/video9.mp4', 300, b'1', 1, '代理,体系,介绍'),
(5, '分佣规则详解', '理解分佣计算和结算规则', NULL, 'https://example.com/video10.mp4', 240, b'1', 2, '分佣,规则,详解');