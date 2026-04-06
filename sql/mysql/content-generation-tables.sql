-- ==================== 多 Agent 内容生成系统 - 数据库迁移脚本 ====================
-- 创建时间：2026-04-06
-- 说明：创建内容生成相关的核心数据表
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 1. 内容生成记录表
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS `content_generation_record`;
CREATE TABLE `content_generation_record` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`             BIGINT       NOT NULL COMMENT '用户 ID',
    `generation_type`     TINYINT      NOT NULL COMMENT '生成类型：1-爆款拆解，2-文生视频，3-图生视频，4-黄金 6 秒，5-AI 混剪',
    `sub_type`            TINYINT      DEFAULT NULL COMMENT '子类型',

    -- 输入
    `input_text`          TEXT         DEFAULT NULL COMMENT '输入文本 (提示词/参考文案)',
    `input_image_url`     VARCHAR(512) DEFAULT NULL COMMENT '输入图片 URL',
    `input_video_url`     VARCHAR(512) DEFAULT NULL COMMENT '输入视频 URL(拆解用)',
    `template_id`         BIGINT       DEFAULT NULL COMMENT '使用的模板 ID',

    -- 输出
    `output_url`          VARCHAR(512) DEFAULT NULL COMMENT '输出结果 URL',
    `output_thumbnail`    VARCHAR(512) DEFAULT NULL COMMENT '缩略图 URL',
    `output_duration_ms`  INT          DEFAULT NULL COMMENT '视频时长 (ms)',

    -- AI 分析结果 (JSON 格式存储)
    `analysis_result`     JSON         DEFAULT NULL COMMENT 'AI 分析结果 (拆解报告)',
    `script_content`      TEXT         DEFAULT NULL COMMENT '生成的脚本内容',
    `seo_title`           VARCHAR(255) DEFAULT NULL COMMENT 'SEO 优化标题',
    `seo_tags`            VARCHAR(512) DEFAULT NULL COMMENT 'SEO 标签 (逗号分隔)',

    -- 状态和统计
    `status`              TINYINT      DEFAULT 0 COMMENT '状态：0-处理中，1-完成，2-失败',
    `cost_points`         INT          NOT NULL COMMENT '消耗积分',
    `duration_ms`         INT          DEFAULT NULL COMMENT '耗时 (ms)',
    `retry_count`         INT          DEFAULT 0 COMMENT '重试次数',
    `error_message`       VARCHAR(512) DEFAULT NULL COMMENT '错误信息',

    -- 审计字段
    `create_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT          DEFAULT 0 COMMENT '逻辑删除',

    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_generation_type` (`generation_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容生成记录表';

-- ---------------------------------------------------------------------------
-- 2. 内容模板表
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS `content_template`;
CREATE TABLE `content_template` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category`        VARCHAR(32)  NOT NULL COMMENT '行业分类',
    `title`           VARCHAR(128) NOT NULL COMMENT '模板标题',
    `description`     VARCHAR(512) DEFAULT NULL COMMENT '模板描述',
    `cover_image_url` VARCHAR(512) DEFAULT NULL COMMENT '封面图 URL',
    `demo_video_url`  VARCHAR(512) DEFAULT NULL COMMENT '演示视频 URL',

    -- 模板内容
    `prompt_template` TEXT         DEFAULT NULL COMMENT '提示词模板',
    `script_template` TEXT         DEFAULT NULL COMMENT '脚本模板',
    `visual_template` JSON         DEFAULT NULL COMMENT '视觉模板 (JSON)',

    -- 使用统计
    `use_count`       INT          DEFAULT 0 COMMENT '使用次数',
    `like_count`      INT          DEFAULT 0 COMMENT '点赞数',
    `is_featured`     BIT          DEFAULT 0 COMMENT '是否精选',
    `sort_order`      INT          DEFAULT 0 COMMENT '排序',

    -- 审计字段
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         BIT          DEFAULT 0 COMMENT '逻辑删除',

    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_featured` (`is_featured`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容模板表';

-- ---------------------------------------------------------------------------
-- 3. 热门内容缓存表
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS `trend_cache`;
CREATE TABLE `trend_cache` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `platform`          VARCHAR(32)  NOT NULL COMMENT '平台：douyin/xiaohongshu/bilibili',
    `category`          VARCHAR(32)  DEFAULT NULL COMMENT '分类',
    `content_id`        VARCHAR(128) NOT NULL COMMENT '内容 ID',
    `title`             VARCHAR(255) DEFAULT NULL COMMENT '标题',
    `author`            VARCHAR(128) DEFAULT NULL COMMENT '作者',

    -- 热门指标
    `like_count`        INT          DEFAULT 0 COMMENT '点赞数',
    `comment_count`     INT          DEFAULT 0 COMMENT '评论数',
    `share_count`       INT          DEFAULT 0 COMMENT '分享数',
    `view_count`        INT          DEFAULT 0 COMMENT '播放数',

    -- 分析结果
    `trend_score`       DECIMAL(5,2) DEFAULT NULL COMMENT '热门分数 (0-100)',
    `analysis_result`   JSON         DEFAULT NULL COMMENT '分析结果 (JSON)',
    `tags`              VARCHAR(512) DEFAULT NULL COMMENT '标签',

    -- 审计字段
    `crawled_at`        DATETIME     DEFAULT NULL COMMENT '抓取时间',
    `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_platform_content` (`platform`, `content_id`),
    KEY `idx_trend_score` (`trend_score`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='热门内容缓存表';

-- ---------------------------------------------------------------------------
-- 4. 初始化内容模板数据
-- ---------------------------------------------------------------------------
INSERT INTO `content_template` (`category`, `title`, `description`, `prompt_template`, `script_template`, `use_count`, `is_featured`, `sort_order`) VALUES
('电商', '商品展示模板', '适合电商产品展示的短视频模板', '展示{商品名称}的核心卖点，突出{产品优势}，用对比手法展现效果', '开场：你用过{产品}吗？\\n正文：传统产品的痛点...\\n结尾：现在有了{商品名称}...', 0, 1, 1),
('大健康', '知识科普模板', '适合大健康领域的知识科普视频', '用通俗易懂的方式讲解{健康知识}， debunk 常见误区', '开场：你知道吗？{常见误区}\\n正文：科学解释...\\n结尾：记住这 3 点...', 0, 1, 2),
('工厂', '生产流程模板', '展示工厂生产流程的短视频模板', '展示{产品}的生产过程，突出工艺和质量控制', '开场：带你看看{产品}是怎么生产的\\n正文：第一步...\\n结尾：品质保证...', 0, 0, 3),
('励志演讲', '金句励志模板', '适合励志类内容的短视频模板', '用金句 + 故事的方式传递正能量', '开场：送给正在{状态}的你\\n正文：记住这句话...\\n结尾：一起加油...', 0, 1, 4),
('探店', '店铺探店模板', '适合探店类内容的短视频模板', '以第一视角带用户体验{店铺类型}', '开场：今天带大家来一家{店铺类型}\\n正文：环境...菜品...\\n结尾：值得来吗？...', 0, 0, 5);

-- ---------------------------------------------------------------------------
-- 5. 添加内容生成记录到积分流水表的业务类型枚举说明
-- ---------------------------------------------------------------------------
-- 注意：需要在 pay_wallet_transaction 表的 biz_type 字段添加新的业务类型
-- 建议在代码中定义枚举类 WalletBizTypeEnum 时添加：
--
-- public static final int AI_CONTENT_GENERATE = -5;    // AI 内容生成
-- public static final int AI_VIDEO_BREAKDOWN = -6;     // AI 爆款拆解
-- public static final int AI_SCRIPT_GENERATE = -7;     // AI 脚本生成
