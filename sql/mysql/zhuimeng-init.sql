-- =============================================
-- 追梦 Dream 数据库初始化脚本
-- 包含：会员、积分钱包、代理分销、AI生成等核心业务表
-- =============================================

-- 选择数据库
USE zhuimeng;

-- =============================================
-- 1. 会员用户表
-- =============================================
DROP TABLE IF EXISTS `member_user`;
CREATE TABLE `member_user` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `tenant_id`          BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    `nickname`           VARCHAR(30)  NOT NULL DEFAULT '' COMMENT '用户昵称',
    `name`               VARCHAR(30)  DEFAULT NULL COMMENT '真实名字',
    `sex`                TINYINT      DEFAULT NULL COMMENT '性别',
    `birthday`           DATETIME     DEFAULT NULL COMMENT '出生日期',
    `area_id`            INT          DEFAULT NULL COMMENT '所在地',
    `mark`               VARCHAR(255) DEFAULT NULL COMMENT '用户备注',
    `point`              INT          NOT NULL DEFAULT 0 COMMENT '积分',
    `avatar`             VARCHAR(255) NOT NULL DEFAULT '' COMMENT '头像',
    `status`             TINYINT      NOT NULL COMMENT '状态',
    `mobile`             VARCHAR(11)  NOT NULL COMMENT '手机号',
    `password`           VARCHAR(100) NOT NULL DEFAULT '' COMMENT '密码',
    `register_ip`        VARCHAR(32)  NOT NULL COMMENT '注册 IP',
    `login_ip`           VARCHAR(50)  DEFAULT NULL COMMENT '最后登录IP',
    `login_date`         DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    `tag_ids`            VARCHAR(255) DEFAULT NULL COMMENT '用户标签编号列表',
    `level_id`           BIGINT       DEFAULT NULL COMMENT '等级编号',
    `experience`         BIGINT       DEFAULT NULL COMMENT '经验',
    `group_id`           BIGINT       DEFAULT NULL COMMENT '用户分组编号',
    `register_terminal`  INT          DEFAULT NULL COMMENT '注册终端',
    `creator`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`            BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_mobile` (`mobile`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员用户表';

-- =============================================
-- 2. 会员标签表
-- =============================================
DROP TABLE IF EXISTS `member_tag`;
CREATE TABLE `member_tag` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `name`        VARCHAR(30)  NOT NULL COMMENT '标签名称',
    `creator`     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员标签表';

-- =============================================
-- 3. 会员等级表
-- =============================================
DROP TABLE IF EXISTS `member_level`;
CREATE TABLE `member_level` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `name`        VARCHAR(30)  NOT NULL COMMENT '等级名称',
    `experience`  INT          NOT NULL DEFAULT 0 COMMENT '升级所需经验',
    `icon`        VARCHAR(255) DEFAULT NULL COMMENT '等级图标',
    `level`       INT          NOT NULL COMMENT '等级值',
    `creator`     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员等级表';

-- 初始化默认等级
INSERT INTO `member_level` (`name`, `experience`, `level`, `tenant_id`) VALUES
('普通会员', 0, 1, 0),
('铜牌会员', 100, 2, 0),
('银牌会员', 500, 3, 0),
('金牌会员', 1000, 4, 0),
('钻石会员', 5000, 5, 0);

-- =============================================
-- 4. 会员分组表
-- =============================================
DROP TABLE IF EXISTS `member_group`;
CREATE TABLE `member_group` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `name`        VARCHAR(30)  NOT NULL COMMENT '分组名称',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `creator`     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员分组表';

-- =============================================
-- 5. 积分钱包表
-- =============================================
DROP TABLE IF EXISTS `pay_wallet`;
CREATE TABLE `pay_wallet` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id`             BIGINT       NOT NULL COMMENT '用户 ID',
    `balance`             INT          NOT NULL DEFAULT 0 COMMENT '可用余额（积分）',
    `frozen_balance`      INT          NOT NULL DEFAULT 0 COMMENT '冻结余额（积分）',
    `total_recharge`      INT          NOT NULL DEFAULT 0 COMMENT '累计充值（积分）',
    `total_used`          INT          NOT NULL DEFAULT 0 COMMENT '累计消耗（积分）',
    `total_received`      INT          NOT NULL DEFAULT 0 COMMENT '累计收到分配（积分）',
    `total_given`         INT          NOT NULL DEFAULT 0 COMMENT '累计分配给出（积分）',
    `total_commission`    INT          NOT NULL DEFAULT 0 COMMENT '累计分成收入（积分）',
    `expire_time`         DATETIME     DEFAULT NULL COMMENT '过期时间',
    `creator`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    `tenant_id`           BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分钱包表';

-- =============================================
-- 6. 积分流水表
-- =============================================
DROP TABLE IF EXISTS `pay_wallet_transaction`;
CREATE TABLE `pay_wallet_transaction` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `wallet_id`           BIGINT       NOT NULL COMMENT '钱包 ID',
    `user_id`             BIGINT       NOT NULL COMMENT '用户 ID',
    `biz_type`            TINYINT      NOT NULL COMMENT '业务类型',
    `biz_order_no`        VARCHAR(32)  NOT NULL COMMENT '业务订单号',
    `amount`              INT          NOT NULL COMMENT '变动金额（正数增加，负数减少）',
    `balance_after`       INT          NOT NULL COMMENT '变动后余额',
    `description`         VARCHAR(255) NOT NULL COMMENT '描述',
    `extra_data`          JSON         DEFAULT NULL COMMENT '扩展数据',
    `creator`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    `tenant_id`           BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_wallet_id` (`wallet_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_biz_order_no` (`biz_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分流水表';

-- =============================================
-- 7. 代理用户表
-- =============================================
DROP TABLE IF EXISTS `agency_user`;
CREATE TABLE `agency_user` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id`             BIGINT       NOT NULL COMMENT '用户 ID',
    `parent_id`           BIGINT       DEFAULT NULL COMMENT '上级代理 ID',
    `grandparent_id`      BIGINT       DEFAULT NULL COMMENT '上上级代理 ID',
    `level`               TINYINT      NOT NULL DEFAULT 0 COMMENT '代理等级（0-普通用户，1-一级代理，2-二级代理）',
    `invite_code`         VARCHAR(32)  NOT NULL COMMENT '邀请码',
    `total_invite_count`  INT          NOT NULL DEFAULT 0 COMMENT '累计邀请人数',
    `direct_invite_count` INT          NOT NULL DEFAULT 0 COMMENT '直推人数',
    `total_recharge`      INT          NOT NULL DEFAULT 0 COMMENT '累计充值金额（分）',
    `total_commission`    INT          NOT NULL DEFAULT 0 COMMENT '累计分佣（积分）',
    `status`              TINYINT      NOT NULL DEFAULT 0 COMMENT '状态（0-正常，1-冻结）',
    `creator`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    `tenant_id`           BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    UNIQUE KEY `uk_invite_code` (`invite_code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代理用户表';

-- =============================================
-- 8. 佣金记录表
-- =============================================
DROP TABLE IF EXISTS `agency_commission_record`;
CREATE TABLE `agency_commission_record` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id`             BIGINT       NOT NULL COMMENT '被分成用户 ID（谁的消费/充值）',
    `brokerage_user_id`   BIGINT       NOT NULL COMMENT '分成获得用户 ID（哪个代理获得分成）',
    `biz_type`            TINYINT      NOT NULL COMMENT '业务类型：1-充值分成，2-消费分成',
    `biz_order_no`        VARCHAR(32)  NOT NULL COMMENT '业务订单号',
    `order_id`            BIGINT       DEFAULT NULL COMMENT '关联订单 ID',
    `amount`              INT          NOT NULL COMMENT '分成金额（积分）',
    `commission_rate`     INT          NOT NULL COMMENT '分成比例（万分比）',
    `status`              TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-待结算，1-已结算，2-已取消',
    `settle_time`         DATETIME     DEFAULT NULL COMMENT '结算时间',
    `remark`              VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `creator`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    `tenant_id`           BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_brokerage_user_id` (`brokerage_user_id`),
    KEY `idx_biz_order_no` (`biz_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='佣金记录表';

-- =============================================
-- 9. 充值订单表
-- =============================================
DROP TABLE IF EXISTS `pay_recharge_order`;
CREATE TABLE `pay_recharge_order` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `order_no`            VARCHAR(32)  NOT NULL COMMENT '订单号',
    `user_id`             BIGINT       NOT NULL COMMENT '用户 ID',
    `amount`              INT          NOT NULL COMMENT '充值金额（分）',
    `points`              INT          NOT NULL COMMENT '获得积分（分）',
    `bonus_points`        INT          NOT NULL DEFAULT 0 COMMENT '赠送积分（分）',
    `total_points`        INT          NOT NULL COMMENT '总计积分（分）',
    `payment_method`      TINYINT      DEFAULT NULL COMMENT '支付方式：1-微信，2-支付宝',
    `status`              TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-待支付，1-已支付，2-已关闭，3-已退款',
    `transaction_id`      VARCHAR(64)  DEFAULT NULL COMMENT '第三方支付流水号',
    `paid_time`           DATETIME     DEFAULT NULL COMMENT '支付时间',
    `expired_time`        DATETIME     DEFAULT NULL COMMENT '过期时间',
    `remark`              VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `extra_data`          JSON         DEFAULT NULL COMMENT '扩展数据',
    `creator`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    `tenant_id`           BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值订单表';

-- =============================================
-- 10. AI 生成记录表
-- =============================================
DROP TABLE IF EXISTS `ai_generation_record`;
CREATE TABLE `ai_generation_record` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id`             BIGINT       NOT NULL COMMENT '用户 ID',
    `generation_type`     TINYINT      NOT NULL COMMENT '生成类型：1-生图，2-文生视频，3-图生视频，4-视频拆解',
    `sub_type`            TINYINT      DEFAULT NULL COMMENT '子类型',
    `input_text`          TEXT         COMMENT '输入文本（提示词）',
    `input_image_url`     VARCHAR(512) DEFAULT NULL COMMENT '输入图片 URL',
    `input_video_url`     VARCHAR(512) DEFAULT NULL COMMENT '输入视频 URL',
    `output_url`          VARCHAR(512) DEFAULT NULL COMMENT '输出结果 URL',
    `output_thumbnail`    VARCHAR(512) DEFAULT NULL COMMENT '输出缩略图 URL',
    `cost_points`         INT          NOT NULL DEFAULT 0 COMMENT '消耗积分',
    `status`              TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-处理中，1-完成，2-失败',
    `error_message`       VARCHAR(512) DEFAULT NULL COMMENT '错误信息',
    `duration_ms`         INT          DEFAULT NULL COMMENT '耗时（毫秒）',
    `api_provider`        VARCHAR(64)  DEFAULT NULL COMMENT 'API 服务商',
    `api_request_id`      VARCHAR(128) DEFAULT NULL COMMENT 'API 请求 ID',
    `extra_data`          JSON         DEFAULT NULL COMMENT '扩展数据',
    `creator`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    `tenant_id`           BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 生成记录表';

-- =============================================
-- 11. AI 灵感案例表
-- =============================================
DROP TABLE IF EXISTS `ai_inspiration_case`;
CREATE TABLE `ai_inspiration_case` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `category`            VARCHAR(32)  NOT NULL COMMENT '行业分类',
    `title`               VARCHAR(128) NOT NULL COMMENT '案例标题',
    `description`         VARCHAR(512) DEFAULT NULL COMMENT '案例描述',
    `cover_image_url`     VARCHAR(512) DEFAULT NULL COMMENT '封面图 URL',
    `video_url`           VARCHAR(512) DEFAULT NULL COMMENT '演示视频 URL',
    `prompt_template`     TEXT         COMMENT '提示词模板',
    `view_count`          INT          NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `use_count`           INT          NOT NULL DEFAULT 0 COMMENT '使用次数',
    `is_featured`         BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否精选',
    `sort_order`          INT          NOT NULL DEFAULT 0 COMMENT '排序（越小越前）',
    `extra_data`          JSON         DEFAULT NULL COMMENT '扩展数据',
    `creator`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    `tenant_id`           BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_featured` (`is_featured`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 灵感案例表';

-- 初始化灵感案例
INSERT INTO `ai_inspiration_case` (`category`, `title`, `description`, `sort_order`, `is_featured`) VALUES
('电商', '电商产品主图设计', '适用于淘宝/拼多多/抖音小店商品主图', 1, b'1'),
('大健康', '大健康科普视频', '医疗健康类科普短视频模板', 2, b'1'),
('工厂', '工厂实拍宣传片', '工厂环境/生产流程展示', 3, b'0'),
('探店', '美食探店视频', '餐厅/咖啡店探店模板', 4, b'0'),
('宠物', '宠物日常视频', '猫狗宠物可爱瞬间', 5, b'0'),
('美食', '美食制作教程', '家常菜/甜点制作教程', 6, b'0'),
('数字人', 'AI 数字人口播', '数字人播报/直播带货', 7, b'1');

-- =============================================
-- 12. 代理配置表
-- =============================================
DROP TABLE IF EXISTS `agency_config`;
CREATE TABLE `agency_config` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `config_key`          VARCHAR(64)  NOT NULL COMMENT '配置键',
    `config_value`        VARCHAR(512) NOT NULL COMMENT '配置值',
    `config_type`         TINYINT      NOT NULL DEFAULT 1 COMMENT '类型：1-数字，2-字符串，3-JSON',
    `description`         VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `creator`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    `tenant_id`           BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代理配置表';

-- 初始化代理配置
INSERT INTO `agency_config` (`config_key`, `config_value`, `config_type`, `description`) VALUES
('level1_fee', '99900', 1, '一级代理费用（单位：分）'),
('level1_min_direct_invite', '100', 1, '一级代理最少直推人数'),
('level1_min_recharge', '99900', 1, '一级代理最少累计充值（单位：分）'),
('level1_commission_rate', '2000', 1, '一级代理分成比例（万分比，2000=20%）'),
('level2_commission_rate', '800', 1, '二级代理分成比例（万分比，800=8%）'),
('points_per_yuan', '100', 1, '1 元兑换积分（单位：分）'),
('recharge_bonus_rate', '0', 1, '充值赠送比例（万分比，0=不赠送）');

-- =============================================
-- 初始化测试用户
-- =============================================
-- 创建测试用户（手机号：13800138000，密码：123456）
-- 注意：BCrypt 加密后的密码，需要运行程序后通过接口注册生成
-- 这里只初始化钱包
INSERT INTO `pay_wallet` (`user_id`, `balance`, `tenant_id`)
SELECT id, 100000, 0 FROM `member_user` WHERE mobile = '13800138000'
ON DUPLICATE KEY UPDATE `balance` = `balance`;

-- =============================================
-- 完成提示
-- =============================================
SELECT '数据库初始化完成！' AS message;
