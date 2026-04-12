-- ================================================================
-- 追梦 AI 平台：支付渠道初始化 SQL
-- 作者：Claude
-- 日期：2026-04-11
-- 描述：P0-5 微信/支付宝真实支付对接
-- ================================================================
--
-- 说明：
--   1. 微信支付使用 V3 API（推荐）
--   2. 支付宝使用沙箱/正式环境
--   3. 执行前请确保已创建 pay_app 表和 pay_channel 表
--   4. 密钥等敏感信息请通过环境变量或配置文件注入，不要硬编码
-- ================================================================

-- ================================================================
-- 【第一步】创建钱包充值支付应用（如果不存在）
-- ================================================================
-- 注意：appKey 为 "wallet"，与 PayProperties.walletPayAppKey 默认值对应
INSERT IGNORE INTO pay_app (id, app_key, name, status, order_notify_url, refund_notify_url, transfer_notify_url, remark, creator, create_time, updater, update_time, deleted)
VALUES (
    100,
    'wallet',
    '追梦 AI - 钱包充值应用',
    1,                                       -- 状态：1-启用
    '${YUDAO_PAY_ORDER_NOTIFY_URL:https://your-domain.com/admin-api/pay/notify/order}',   -- 支付回调地址
    '${YUDAO_PAY_REFUND_NOTIFY_URL:https://your-domain.com/admin-api/pay/notify/refund}',  -- 退款回调地址
    '${YUDAO_PAY_TRANSFER_NOTIFY_URL:https://your-domain.com/admin-api/pay/notify/transfer}', -- 转账回调地址
    '追梦 AI 平台钱包充值专用支付应用',
    1,
    NOW(),
    1,
    NOW(),
    0
);

-- ================================================================
-- 【第二步】配置微信小程序支付渠道（wx_lite）
-- ================================================================
-- 适用于：微信小程序内支付
-- 前端 channelCode 传值：wx_lite
-- 前端额外参数：{ "openid": "用户 OpenId" }
--
-- 配置说明（WxPayClientConfig）：
--   appId         → 微信公众平台 AppID（公众号/小程序）
--   mchId         → 商户号
--   apiVersion    → v2 或 v3（推荐 v3）
--   V2 密钥字段：
--     mchKey      → APIv2 商户密钥
--     keyContent  → apiclient_cert.p12 证书 base64 内容（退款时需要）
--   V3 密钥字段：
--     privateKeyContent   → apiclient_key.pem 内容
--     apiV3Key            → APIv3 密钥
--     certSerialNo         → 证书序列号
INSERT IGNORE INTO pay_channel (id, app_id, code, status, config, fee_rate, remark, creator, create_time, updater, update_time, deleted)
VALUES (
    10001,                                         -- 渠道 ID
    100,                                           -- 支付应用 ID（对应 pay_app.id）
    'wx_lite',                                     -- 微信小程序支付
    1,                                             -- 状态：1-启用
    -- ============================================
    -- 【V3 配置示例】（推荐使用 V3）
    -- ============================================
    CONCAT(
        '{',
            '"appId": "${WECHAT_MP_APP_ID}",',
            '"mchId": "${WECHAT_PAY_MCH_ID}",',
            '"apiVersion": "v3",',
            '"privateKeyContent": "${WECHAT_PAY_V3_PRIVATE_KEY}",',
            '"apiV3Key": "${WECHAT_PAY_V3_API_KEY}",',
            '"certSerialNo": "${WECHAT_PAY_CERT_SERIAL_NO}"',
        '}'
    ),
    -- ============================================
    -- 【V2 配置示例】（如果 V3 不可用时使用）
    -- ============================================
    -- CONCAT(
    --     '{',
    --         '"appId": "${WECHAT_MP_APP_ID}",',
    --         '"mchId": "${WECHAT_PAY_MCH_ID}",',
    --         '"apiVersion": "v2",',
    --         '"mchKey": "${WECHAT_PAY_V2_API_KEY}",',
    --         '"keyContent": "${WECHAT_PAY_CERT_BASE64}"',
    --     '}'
    -- ),
    0.006,                                          -- 手续费率：0.6%（微信标准费率）
    '微信小程序支付渠道（V3）',
    1,
    NOW(),
    1,
    NOW(),
    0
);

-- ================================================================
-- 【第三步】配置微信 Native 支付渠道（wx_native）
-- ================================================================
-- 适用于：PC 网页扫码支付
-- 前端 channelCode 传值：wx_native
INSERT IGNORE INTO pay_channel (id, app_id, code, status, config, fee_rate, remark, creator, create_time, updater, update_time, deleted)
VALUES (
    10002,
    100,
    'wx_native',
    1,
    CONCAT(
        '{',
            '"appId": "${WECHAT_MP_APP_ID}",',
            '"mchId": "${WECHAT_PAY_MCH_ID}",',
            '"apiVersion": "v3",',
            '"privateKeyContent": "${WECHAT_PAY_V3_PRIVATE_KEY}",',
            '"apiV3Key": "${WECHAT_PAY_V3_API_KEY}",',
            '"certSerialNo": "${WECHAT_PAY_CERT_SERIAL_NO}"',
        '}'
    ),
    0.006,
    '微信 Native 扫码支付（PC 端）',
    1,
    NOW(),
    1,
    NOW(),
    0
);

-- ================================================================
-- 【第四步】配置支付宝扫码支付渠道（alipay_qr）
-- ================================================================
-- 适用于：PC 网页扫码支付
-- 前端 channelCode 传值：alipay_qr
--
-- 配置说明（AlipayPayClientConfig）：
--   appId              → 支付宝应用 AppID
--   privateKey         → 应用私钥
--   alipayPublicKey    → 支付宝公钥
INSERT IGNORE INTO pay_channel (id, app_id, code, status, config, fee_rate, remark, creator, create_time, updater, update_time, deleted)
VALUES (
    10003,
    100,
    'alipay_qr',
    1,
    CONCAT(
        '{',
            '"appId": "${ALIPAY_APP_ID}",',
            '"privateKey": "${ALIPAY_PRIVATE_KEY}",',
            '"alipayPublicKey": "${ALIPAY_PUBLIC_KEY}"',
        '}'
    ),
    0.008,                                          -- 手续费率：0.8%（支付宝标准费率）
    '支付宝扫码支付（QR）',
    1,
    NOW(),
    1,
    NOW(),
    0
);

-- ================================================================
-- 【第五步】配置支付宝 App 支付渠道（alipay_app）
-- ================================================================
-- 适用于：App 内支付宝支付
-- 前端 channelCode 传值：alipay_app
INSERT IGNORE INTO pay_channel (id, app_id, code, status, config, fee_rate, remark, creator, create_time, updater, update_time, deleted)
VALUES (
    10004,
    100,
    'alipay_app',
    1,
    CONCAT(
        '{',
            '"appId": "${ALIPAY_APP_ID}",',
            '"privateKey": "${ALIPAY_PRIVATE_KEY}",',
            '"alipayPublicKey": "${ALIPAY_PUBLIC_KEY}"',
        '}'
    ),
    0.008,
    '支付宝 App 支付',
    1,
    NOW(),
    1,
    NOW(),
    0
);

-- ================================================================
-- 【第六步】配置微信 JSAPI 支付渠道（wx_pub）
-- ================================================================
-- 适用于：微信公众号网页支付
-- 前端 channelCode 传值：wx_pub
-- 前端额外参数：{ "openid": "用户在微信公众号的 OpenId" }
INSERT IGNORE INTO pay_channel (id, app_id, code, status, config, fee_rate, remark, creator, create_time, updater, update_time, deleted)
VALUES (
    10005,
    100,
    'wx_pub',
    1,
    CONCAT(
        '{',
            '"appId": "${WECHAT_MP_APP_ID}",',
            '"mchId": "${WECHAT_PAY_MCH_ID}",',
            '"apiVersion": "v3",',
            '"privateKeyContent": "${WECHAT_PAY_V3_PRIVATE_KEY}",',
            '"apiV3Key": "${WECHAT_PAY_V3_API_KEY}",',
            '"certSerialNo": "${WECHAT_PAY_CERT_SERIAL_NO}"',
        '}'
    ),
    0.006,
    '微信 JSAPI 支付（公众号）',
    1,
    NOW(),
    1,
    NOW(),
    0
);

-- ================================================================
-- 部署检查清单
-- ================================================================
-- [ ] 1. 在微信支付商户平台申请微信支付商户号并完成认证
-- [ ] 2. 在微信公众平台申请小程序/公众号，获得 AppID
-- [ ] 3. 在支付宝开放平台创建应用，获得 AppID
-- [ ] 4. 申请微信支付 V3 API 证书（apiclient_key.pem 等）
-- [ ] 5. 在 application-local.yaml 或环境变量中配置密钥
-- [ ] 6. 配置支付回调地址（公网可访问的 HTTPS 地址）
-- [ ] 7. 执行上述 SQL 脚本初始化支付渠道
-- [ ] 8. 前端传入正确的 channelCode 和 channelExtras 参数
-- [ ] 9. 测试完整支付流程（下单 → 支付 → 回调 → 增加积分）
--
-- ================================================================
-- 环境变量参考（application-local.yaml 或环境变量）
-- ================================================================
-- # 微信支付
-- WECHAT_MP_APP_ID: wx1234567890abcdef
-- WECHAT_PAY_MCH_ID: 1234567890
-- WECHAT_PAY_V3_API_KEY: your_api_v3_key_32_chars
-- WECHAT_PAY_V3_PRIVATE_KEY: "-----BEGIN RSA PRIVATE KEY-----\n..."
-- WECHAT_PAY_CERT_SERIAL_NO: A1B2C3D4E5F6...
--
-- # 支付宝
-- ALIPAY_APP_ID: 2021001234567890
-- ALIPAY_PRIVATE_KEY: "-----BEGIN RSA PRIVATE KEY-----\n..."
-- ALIPAY_PUBLIC_KEY: "-----BEGIN PUBLIC KEY-----\n..."
--
-- # 回调地址
-- YUDAO_PAY_ORDER_NOTIFY_URL: https://your-domain.com/admin-api/pay/notify/order
-- ================================================================
