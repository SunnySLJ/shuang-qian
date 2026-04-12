-- ================================================================
-- 追梦 AI 平台：钱包充值套餐初始化 SQL
-- 作者：Claude
-- 日期：2026-04-11
-- 描述：初始化钱包充值套餐数据
-- ================================================================

-- 创建钱包充值套餐表
CREATE TABLE IF NOT EXISTS `pay_wallet_recharge_package` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '套餐 ID',
  `name` varchar(50) NOT NULL COMMENT '套餐名称',
  `pay_price` int NOT NULL COMMENT '支付金额（分）',
  `bonus_price` int NOT NULL DEFAULT '0' COMMENT '赠送金额（分）',
  `total_price` int NOT NULL COMMENT '总金额（分）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0:启用 1:禁用）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) DEFAULT b'0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包充值套餐表';

-- 插入初始套餐数据
-- 注意：status=0 表示启用（芋道框架规范）
INSERT INTO `pay_wallet_recharge_package` (`id`, `name`, `pay_price`, `bonus_price`, `total_price`, `status`, `remark`) VALUES
(1, '体验套餐', 990, 0, 990, 0, '9.9 元 = 10 积分'),
(2, '基础套餐', 2900, 300, 3200, 0, '29 元 = 32 积分（赠送 3 积分）'),
(3, '进阶套餐', 9900, 1500, 11400, 0, '99 元 = 114 积分（赠送 15 积分）'),
(4, '专业套餐', 19900, 4000, 23900, 0, '199 元 = 239 积分（赠送 40 积分）'),
(5, '企业套餐', 49900, 12000, 61900, 0, '499 元 = 619 积分（赠送 120 积分');
