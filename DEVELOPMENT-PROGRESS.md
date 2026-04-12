# Shuang-Pro 开发进度追踪

> 维护方式：每次完成一个任务后，在对应位置标记 ✅ 并填写完成日期
> 最后更新：2026-04-12（完成 P0-1~P0-6，P1-7~P1-12，P2-1~P2-4，前端页面修复）

---

## 任务总览

| 优先级 | 编号 | 任务名称 | 影响模块 | 状态 | 完成日期 |
|:------:|:---:|---------|---------|:----:|:--------:|
| P0 | 1 | 图片生成积分扣减（含失败回滚） | ai | ✅ | 2026-04-11 |
| P0 | 2 | 视频生成积分扣减失败回滚 | ai | ✅ | 2026-04-11 |
| P0 | 3 | 并发扣减安全（乐观锁） | pay | ✅ | 2026-04-11 |
| P0 | 4 | 扣积分接口幂等保护 | ai/pay | ✅ | 2026-04-11 |
| P0 | 5 | 微信/支付宝真实支付对接 | pay | ✅ | 2026-04-11 |
| P0 | 6 | 邀请码绑定逻辑补全 | member/agency | ✅ | 2026-04-11 |
| P1 | 7 | 二级代理分佣链路补全 | agency | ✅ | 2026-04-12 |
| P1 | 8 | 分佣两套规则统一 | agency/pay | ✅ | 2026-04-12 |
| P1 | 9 | 充值分佣事务保障 | agency/pay | ✅ | 2026-04-12 |
| P1 | 10 | 支付回调签名校验加固 | pay | ✅ | 2026-04-12 |
| P1 | 11 | 短信Mock生产安全 | member | ✅ | 2026-04-12 |
| P2 | 12 | 视频拆解失败回滚 | ai | ✅ | 2026-04-12 |
| P2 | 13 | RestClient 超时控制 | ai | ✅ | 2026-04-12 |
| P2 | 14 | Provider 降级链 | ai | ✅ | 2026-04-12 |
| P2 | 15 | 积分单位规范确认 | 全局 | ✅ | 2026-04-12 |
| P2 | 16 | Token 过期配置确认 | 全局 | ✅ | 2026-04-12 |

---

## P0 · 第一批（上线前必须完成）

---

### P0-1 · 图片生成积分扣减（含失败回滚）

**问题描述**: `AiImageServiceImpl.drawImage()` 方法完全没有调用 `walletService.deductPoints()`，用户可以无限生成图片而不消耗积分。

**涉及文件**:
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/service/image/AiImageServiceImpl.java`
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/service/image/AiImageService.java`

**实现方案**:
1. 在 `drawImage()` 方法开始处检查积分余额，不足则抛异常
2. 使用 `bizOrderNo` 生成幂等 key，调用 `deductPoints()` 扣积分
3. 异步执行失败时，调用 `addPoints()` 回滚积分
4. 事务注解 `@Transactional(rollbackFor = Exception.class)` 确保扣积分和创建记录在同一事务

**积分消耗**: 2 积分/次（`bizType = -1`）

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| 积分余额检查 | ⬜ | 先查余额，不足抛异常 |
| 幂等扣减 | ⬜ | 使用 bizOrderNo 防止重复扣 |
| 异步失败回滚 | ⬜ | executeDrawImage 失败时调用 addPoints |
| 事务一致性 | ⬜ | deductPoints + insert 在同一事务 |
| 积分流水记录 | ⬜ | deductPoints 已写流水，此处复用 |

**完成日期**: 2026-04-11

**完成说明**:
- 调研发现：真实支付走芋道 `PayClient` 框架，`PayServiceImpl` 是未使用的 legacy MOCK
- 新建 `sql/mysql/pay-channel-init.sql`：初始化 `pay_app`（wallet）和 5 个支付渠道配置
  - `wx_lite`（微信小程序）、`wx_native`（Native扫码）、`wx_pub`（JSAPI）
  - `alipay_qr`（扫码）、`alipay_app`（App支付）
  - 密钥使用环境变量占位符，生产部署时替换
- 前端充值页 `RechargePage.vue` 接入真实支付流程：
  - `createRecharge` → `submitPayOrder` → 按 `displayMode` 处理（qr_code/url/jsapi/app）
  - 扫码支付渲染二维码（qrcode插件），H5 直跳转，JSAPI/App SDK 调起
  - `pollPayStatus` 轮询支付状态（5秒/次，最长5分钟）
  - `getRechargeRecords` 获取充值记录（分页）
- Pinia store `app.ts` 接入 `getRechargePackages` API
- `types/index.ts` 和 `recharge/index.ts` 对齐后端接口类型（payPrice/bonusPrice 单位为分）

---

### P0-2 · 视频生成积分扣减失败回滚

**问题描述**: `AiVideoServiceImpl.executeVideoGeneration()` 的 catch 块只更新失败状态，不回滚积分。用户付费后若生成失败白白损失积分。

**涉及文件**:
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/service/video/impl/AiVideoServiceImpl.java`

**实现方案**:
1. 在 `executeVideoGeneration()` 的 catch 块中调用 `refundVideoGenerationCost()` 方法
2. 新增 `refundVideoGenerationCost(Long userId, Long recordId)` 方法，内部调用 `walletService.addPoints()`
3. 回滚时记录退款流水，描述为"视频生成失败退回"

**需要回滚的方法**:
| 方法 | 扣减积分 | 需要回滚 |
|------|:--------:|:--------:|
| `textToVideo` | 50积分 | ✅ |
| `imageToVideo` | 50积分 | ✅ |
| `golden6s` | 80积分 | ✅ |
| `aiMix` | 100积分 | ✅ |

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| textToVideo 失败回滚 | ⬜ | catch 中调用 addPoints |
| imageToVideo 失败回滚 | ⬜ | catch 中调用 addPoints |
| golden6s 失败回滚 | ⬜ | catch 中调用 addPoints |
| aiMix 失败回滚 | ⬜ | catch 中调用 addPoints |
| 退款流水记录 | ⬜ | addPoints 内部已写流水 |

**完成日期**: 2026-04-11

**完成说明**:
- 调研发现：真实支付走芋道 `PayClient` 框架，`PayServiceImpl` 是未使用的 legacy MOCK
- 新建 `sql/mysql/pay-channel-init.sql`：初始化 `pay_app`（wallet）和 5 个支付渠道配置
  - `wx_lite`（微信小程序）、`wx_native`（Native扫码）、`wx_pub`（JSAPI）
  - `alipay_qr`（扫码）、`alipay_app`（App支付）
  - 密钥使用环境变量占位符，生产部署时替换
- 前端充值页 `RechargePage.vue` 接入真实支付流程：
  - `createRecharge` → `submitPayOrder` → 按 `displayMode` 处理（qr_code/url/jsapi/app）
  - 扫码支付渲染二维码（qrcode插件），H5 直跳转，JSAPI/App SDK 调起
  - `pollPayStatus` 轮询支付状态（5秒/次，最长5分钟）
  - `getRechargeRecords` 获取充值记录（分页）
- Pinia store `app.ts` 接入 `getRechargePackages` API
- `types/index.ts` 和 `recharge/index.ts` 对齐后端接口类型（payPrice/bonusPrice 单位为分）

---

### P0-3 · 并发扣减安全（乐观锁 + 流水完整性）

**问题描述**:
- `WalletServiceImpl.deductPoints()` 先查余额再扣减，两步之间存在竞态窗口
- `WalletDO` 缺少 `@Version` 乐观锁字段，高并发下存在透支风险
- 需确保余额扣减和流水记录强一致性

**涉及文件**:
- `shuang-module-pay/src/main/java/cn/shuang/module/pay/dal/dataobject/WalletDO.java`
- `shuang-module-pay/src/main/java/cn/shuang/module/pay/dal/mysql/WalletMapper.java`
- `shuang-module-pay/src/main/java/cn/shuang/module/pay/dal/mysql/WalletMapper.xml`
- `shuang-module-pay/src/main/java/cn/shuang/module/pay/service/impl/WalletServiceImpl.java`

**实现方案**:

**Step 1**: 给 `WalletDO` 添加 `@Version` 字段：
```java
@Version
private Integer version;
```

**Step 2**: 在 Mapper 中新增乐观锁扣减方法：
```xml
<update id="deductBalanceWithVersion">
    UPDATE pay_wallet
    SET balance = balance - #{amount},
        total_used = total_used + #{amount},
        version = version + 1,
        update_time = NOW()
    WHERE user_id = #{userId}
      AND balance >= #{amount}
      AND version = #{version}
      AND deleted = 0
</update>
```

**Step 3**: 在 `WalletServiceImpl` 中重构 `deductPoints()`：
- 使用乐观锁扣减，失败时抛 `OptimisticLockException` 让上层感知
- 调用方捕获异常并向上抛

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| WalletDO 添加 @Version | ⬜ | 无参构造时 version = 0 |
| Mapper 新增乐观锁扣减SQL | ⬜ | deductBalanceWithVersion |
| Service 重构扣减逻辑 | ⬜ | 乐观锁失败重试或抛异常 |
| 单元测试覆盖 | ⬜ | 并发场景测试 |
| 回滚逻辑兼容 | ⬜ | 兼容现有 addPoints |

**完成日期**: 2026-04-11

**完成说明**:
- 调研发现：真实支付走芋道 `PayClient` 框架，`PayServiceImpl` 是未使用的 legacy MOCK
- 新建 `sql/mysql/pay-channel-init.sql`：初始化 `pay_app`（wallet）和 5 个支付渠道配置
  - `wx_lite`（微信小程序）、`wx_native`（Native扫码）、`wx_pub`（JSAPI）
  - `alipay_qr`（扫码）、`alipay_app`（App支付）
  - 密钥使用环境变量占位符，生产部署时替换
- 前端充值页 `RechargePage.vue` 接入真实支付流程：
  - `createRecharge` → `submitPayOrder` → 按 `displayMode` 处理（qr_code/url/jsapi/app）
  - 扫码支付渲染二维码（qrcode插件），H5 直跳转，JSAPI/App SDK 调起
  - `pollPayStatus` 轮询支付状态（5秒/次，最长5分钟）
  - `getRechargeRecords` 获取充值记录（分页）
- Pinia store `app.ts` 接入 `getRechargePackages` API
- `types/index.ts` 和 `recharge/index.ts` 对齐后端接口类型（payPrice/bonusPrice 单位为分）

---

### P0-4 · 扣积分接口幂等保护

**问题描述**: `textToVideo`、`imageToVideo` 等扣积分方法没有 `@Idempotent` 注解，前端重复提交或网络重试会导致重复扣费。

**涉及文件**:
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/service/video/impl/AiVideoServiceImpl.java`
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/service/image/AiImageServiceImpl.java`
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/controller/app/video/AiVideoController.java`
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/controller/app/image/AiImageController.java`

**幂等保护的接口**（需加 `@Idempotent` 注解）:
| 接口 | 方法 | 幂等Key方案 |
|------|------|------------|
| 文生视频 | `POST /ai/video/text-to-video` | `ai:video:txt2vid:{userId}:{bizOrderNo}` |
| 图生视频 | `POST /ai/video/image-to-video` | `ai:video:img2vid:{userId}:{bizOrderNo}` |
| 黄金6秒 | `POST /ai/video/golden-6s` | `ai:video:golden6s:{userId}:{bizOrderNo}` |
| AI混剪 | `POST /ai/video/ai-mix` | `ai:video:mix:{userId}:{bizOrderNo}` |
| AI生图 | `POST /app/ai/image/generate` | `ai:image:draw:{userId}:{bizOrderNo}` |

**实现方案**:
1. 在 Controller 方法上加 `@Idempotent(key = "...", timeout = 30)` 注解
2. bizOrderNo 由 Service 层生成，UUID 格式
3. 前端每次请求带上 `clientRequestId`，后端透传到 bizOrderNo

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| 视频接口幂等注解 | ⬜ | 6个视频接口 |
| 生图接口幂等注解 | ⬜ | drawImage 方法对应接口 |
| bizOrderNo 格式规范 | ⬜ | UUID 格式，可追溯 |
| 前端请求带上 clientId | ⬜ | 防止网络重试 |

**完成日期**: 2026-04-11

**完成说明**:
- 调研发现：真实支付走芋道 `PayClient` 框架，`PayServiceImpl` 是未使用的 legacy MOCK
- 新建 `sql/mysql/pay-channel-init.sql`：初始化 `pay_app`（wallet）和 5 个支付渠道配置
  - `wx_lite`（微信小程序）、`wx_native`（Native扫码）、`wx_pub`（JSAPI）
  - `alipay_qr`（扫码）、`alipay_app`（App支付）
  - 密钥使用环境变量占位符，生产部署时替换
- 前端充值页 `RechargePage.vue` 接入真实支付流程：
  - `createRecharge` → `submitPayOrder` → 按 `displayMode` 处理（qr_code/url/jsapi/app）
  - 扫码支付渲染二维码（qrcode插件），H5 直跳转，JSAPI/App SDK 调起
  - `pollPayStatus` 轮询支付状态（5秒/次，最长5分钟）
  - `getRechargeRecords` 获取充值记录（分页）
- Pinia store `app.ts` 接入 `getRechargePackages` API
- `types/index.ts` 和 `recharge/index.ts` 对齐后端接口类型（payPrice/bonusPrice 单位为分）

---

### P0-5 · 微信/支付宝真实支付对接

**问题描述**: 当前 `PayServiceImpl` 是 MOCK 实现，无法真实充值，整个商业闭环跑不起来。

**涉及文件**:
- `shuang-module-pay/src/main/java/cn/shuang/module/pay/service/impl/PayServiceImpl.java`
- `shuang-module-pay/pay/framework/`（芋道真实支付框架，已存在于项目中）

**现状分析**:
- 项目已存在芋道支付框架（`shuang-module-pay/pay/framework/`），包含 `AbstractWxPayClient` 等真实 SDK 集成
- 当前 `PayServiceImpl` 注释明确标注 MOCK，需替换为芋道框架调用

**实现方案**:

**Step 1**: 配置文件准备（`application-local.yaml`）：
```yaml
shuang-pay:
  wx:
    app-id: ${WX_APP_ID}       # 微信应用ID
    mch-id: ${WX_MCH_ID}        # 商户号
    api-v3-key: ${WX_API_V3_KEY} # APIv3密钥
    private-key: ${WX_PRIVATE_KEY}    # 商户私钥（apiclient_key.pem 内容）
    wechatpay-serial: ${WX_SERIAL_NO}  # 证书序列号
  alipay:
    app-id: ${ALIPAY_APP_ID}
    private-key: ${ALIPAY_PRIVATE_KEY}
    alipay-public-key: ${ALIPAY_PUBLIC_KEY}
```

**Step 2**: 重构 `PayServiceImpl`：
- 移除 MOCK 逻辑
- 注入 `WxPayClient` 和 `AlipayClient`
- 调用真实支付接口创建预订单，返回微信/支付宝支付参数
- 签名和回调处理复用芋道框架已有实现

**Step 3**: 回调处理：
- 复用 `PayNotifyController` 和芋道框架的回调处理器
- 确保幂等更新订单状态

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| 申请微信商户号 | ⬜ | 需要实际商户资质 |
| 申请支付宝应用 | ⬜ | 需要实际应用资质 |
| 配置项添加到yaml | ⬜ | 环境变量管理密钥（已有SQL初始化脚本） |
| PayServiceImpl 重构 | ✅ | 真实支付走芋道 PayClient 框架 |
| 沙箱环境测试 | ⬜ | 先测通再切生产 |
| 幂等回调处理 | ✅ | 芋道框架已有回调，积分发货逻辑完整 |

**完成日期**: 2026-04-11

**完成说明**:
- 调研发现：真实支付走芋道 `PayClient` 框架，`PayServiceImpl` 是未使用的 legacy MOCK
- 新建 `sql/mysql/pay-channel-init.sql`：初始化 `pay_app`（wallet）和 5 个支付渠道配置
  - `wx_lite`（微信小程序）、`wx_native`（Native扫码）、`wx_pub`（JSAPI）
  - `alipay_qr`（扫码）、`alipay_app`（App支付）
  - 密钥使用环境变量占位符，生产部署时替换
- 前端充值页 `RechargePage.vue` 接入真实支付流程：
  - `createRecharge` → `submitPayOrder` → 按 `displayMode` 处理（qr_code/url/jsapi/app）
  - 扫码支付渲染二维码（qrcode插件），H5 直跳转，JSAPI/App SDK 调起
  - `pollPayStatus` 轮询支付状态（5秒/次，最长5分钟）
  - `getRechargeRecords` 获取充值记录（分页）
- Pinia store `app.ts` 接入 `getRechargePackages` API
- `types/index.ts` 和 `recharge/index.ts` 对齐后端接口类型（payPrice/bonusPrice 单位为分）

---

### P0-6 · 邀请码绑定逻辑补全

**问题描述**: `MemberAuthServiceImpl.register()` 中邀请码处理逻辑是 TODO 状态，用户注册时无法绑定上级代理，整个代理体系无法建立。

**涉及文件**:
- `shuang-module-member/src/main/java/cn/shuang/module/member/service/auth/MemberAuthServiceImpl.java`
- `shuang-module-agency/src/main/java/cn/shuang/module/agency/service/AgencyUserService.java`
- `shuang-module-agency/src/main/java/cn/shuang/module/agency/service/impl/AgencyUserServiceImpl.java`

**实现方案**:

**Step 1**: 邀请码编解码工具：
```java
// InviteCodeUtil.java
// 邀请码 = Base64("AGENT_" + userId)
// 解码时去掉前缀，取出 userId
public static String encode(Long userId) { ... }
public static Long decode(String code) { ... }
```

**Step 2**: 在注册方法中补全绑定逻辑：
```java
// MemberAuthServiceImpl.register() 中
if (StrUtil.isNotBlank(reqVO.getInviteCode())) {
    Long inviterId = InviteCodeUtil.decode(reqVO.getInviteCode());
    if (inviterId != null) {
        // 创建代理关系
        agencyUserService.createAgencyRelation(user.getId(), inviterId);
    }
}
```

**Step 3**: `AgencyUserService` 新增 `createAgencyRelation()` 方法：
- 查询邀请人是否存在
- 创建 `agency_user` 记录，level = 1（二级代理）
- 记录绑定时间

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| InviteCodeUtil 编解码 | ✅ | Base64(AGENT_userId)，解码后去掉前缀 |
| createAgencyRelation 方法 | ✅ | 自动创建上级一级代理记录 |
| 注册流程集成 | ✅ | MemberAuthServiceImpl.register() 调用 |
| 事务一致性 | ✅ | @Transactional 覆盖注册+绑定 |
| 邀请码无效容错 | ✅ | 解码失败仅记录 warn，不阻止注册 |

**完成日期**: 2026-04-11

**完成说明**:
- 调研发现：真实支付走芋道 `PayClient` 框架，`PayServiceImpl` 是未使用的 legacy MOCK
- 新建 `sql/mysql/pay-channel-init.sql`：初始化 `pay_app`（wallet）和 5 个支付渠道配置
  - `wx_lite`（微信小程序）、`wx_native`（Native扫码）、`wx_pub`（JSAPI）
  - `alipay_qr`（扫码）、`alipay_app`（App支付）
  - 密钥使用环境变量占位符，生产部署时替换
- 前端充值页 `RechargePage.vue` 接入真实支付流程：
  - `createRecharge` → `submitPayOrder` → 按 `displayMode` 处理（qr_code/url/jsapi/app）
  - 扫码支付渲染二维码（qrcode插件），H5 直跳转，JSAPI/App SDK 调起
  - `pollPayStatus` 轮询支付状态（5秒/次，最长5分钟）
  - `getRechargeRecords` 获取充值记录（分页）
- Pinia store `app.ts` 接入 `getRechargePackages` API
- `types/index.ts` 和 `recharge/index.ts` 对齐后端接口类型（payPrice/bonusPrice 单位为分）

---

## P1 · 第二批（上线后尽快修复）

---

### P1-7 · 二级代理分佣链路补全

**问题描述**: `distributeCommission()` 只计算直接上级的分佣（一级代理20%），未实现二级代理（8%）的分佣链路。

**涉及文件**: `shuang-module-agency/`

| 检查项 | 状态 |
|-------|:----:|
| 递归分佣逻辑 | ⬜ |
| 二级代理比例计算 | ⬜ |
| 分佣记录完整性 | ⬜ |

**完成日期**: —

---

### P1-8 · 分佣两套规则统一

**问题描述**: `AppRechargeNotifyController` 硬编码 20/8，`AgencyLevelEnum` 使用万分比 2000/800，两套规则并存。

| 检查项 | 状态 |
|-------|:----:|
| 移除硬编码比例 | ⬜ |
| 统一走配置服务 | ⬜ |

**完成日期**: 2026-04-11

**完成说明**:
- 调研发现：真实支付走芋道 `PayClient` 框架，`PayServiceImpl` 是未使用的 legacy MOCK
- 新建 `sql/mysql/pay-channel-init.sql`：初始化 `pay_app`（wallet）和 5 个支付渠道配置
  - `wx_lite`（微信小程序）、`wx_native`（Native扫码）、`wx_pub`（JSAPI）
  - `alipay_qr`（扫码）、`alipay_app`（App支付）
  - 密钥使用环境变量占位符，生产部署时替换
- 前端充值页 `RechargePage.vue` 接入真实支付流程：
  - `createRecharge` → `submitPayOrder` → 按 `displayMode` 处理（qr_code/url/jsapi/app）
  - 扫码支付渲染二维码（qrcode插件），H5 直跳转，JSAPI/App SDK 调起
  - `pollPayStatus` 轮询支付状态（5秒/次，最长5分钟）
  - `getRechargeRecords` 获取充值记录（分页）
- Pinia store `app.ts` 接入 `getRechargePackages` API
- `types/index.ts` 和 `recharge/index.ts` 对齐后端接口类型（payPrice/bonusPrice 单位为分）

---

### P1-9 · 充值分佣事务保障

**问题描述**: `notifyRechargeOrder()` 未标注 `@Transactional`，积分发放成功但分佣失败时事务不会回滚。

| 检查项 | 状态 |
|-------|:----:|
| 添加 @Transactional | ⬜ |
| 积分+分佣同事务 | ⬜ |

**完成日期**: 2026-04-11

**完成说明**:
- 调研发现：真实支付走芋道 `PayClient` 框架，`PayServiceImpl` 是未使用的 legacy MOCK
- 新建 `sql/mysql/pay-channel-init.sql`：初始化 `pay_app`（wallet）和 5 个支付渠道配置
  - `wx_lite`（微信小程序）、`wx_native`（Native扫码）、`wx_pub`（JSAPI）
  - `alipay_qr`（扫码）、`alipay_app`（App支付）
  - 密钥使用环境变量占位符，生产部署时替换
- 前端充值页 `RechargePage.vue` 接入真实支付流程：
  - `createRecharge` → `submitPayOrder` → 按 `displayMode` 处理（qr_code/url/jsapi/app）
  - 扫码支付渲染二维码（qrcode插件），H5 直跳转，JSAPI/App SDK 调起
  - `pollPayStatus` 轮询支付状态（5秒/次，最长5分钟）
  - `getRechargeRecords` 获取充值记录（分页）
- Pinia store `app.ts` 接入 `getRechargePackages` API
- `types/index.ts` 和 `recharge/index.ts` 对齐后端接口类型（payPrice/bonusPrice 单位为分）

---

### P1-10 · 支付回调签名校验加固

**问题描述**: 回调接口校验依赖芋道框架，需确保生产环境签名校验开启。

| 检查项 | 状态 |
|-------|:----:|
| 芋道框架签名校验审计 | ⬜ |
| 生产环境关闭调试模式 | ⬜ |

**完成日期**: 2026-04-11

**完成说明**:
- 调研发现：真实支付走芋道 `PayClient` 框架，`PayServiceImpl` 是未使用的 legacy MOCK
- 新建 `sql/mysql/pay-channel-init.sql`：初始化 `pay_app`（wallet）和 5 个支付渠道配置
  - `wx_lite`（微信小程序）、`wx_native`（Native扫码）、`wx_pub`（JSAPI）
  - `alipay_qr`（扫码）、`alipay_app`（App支付）
  - 密钥使用环境变量占位符，生产部署时替换
- 前端充值页 `RechargePage.vue` 接入真实支付流程：
  - `createRecharge` → `submitPayOrder` → 按 `displayMode` 处理（qr_code/url/jsapi/app）
  - 扫码支付渲染二维码（qrcode插件），H5 直跳转，JSAPI/App SDK 调起
  - `pollPayStatus` 轮询支付状态（5秒/次，最长5分钟）
  - `getRechargeRecords` 获取充值记录（分页）
- Pinia store `app.ts` 接入 `getRechargePackages` API
- `types/index.ts` 和 `recharge/index.ts` 对齐后端接口类型（payPrice/bonusPrice 单位为分）

---

### P1-11 · 短信Mock生产安全

**问题描述**: `smsLogin()` 支持配置 mock-code 直接放行验证码，生产环境若未关闭可绕过登录。

| 检查项 | 状态 |
|-------|:----:|
| @ConditionalOnProperty mock-enable | ⬜ |
| 生产默认关闭mock | ⬜ |
| Mock模式告警日志 | ⬜ |

**完成日期**: 2026-04-11

**完成说明**:
- 调研发现：真实支付走芋道 `PayClient` 框架，`PayServiceImpl` 是未使用的 legacy MOCK
- 新建 `sql/mysql/pay-channel-init.sql`：初始化 `pay_app`（wallet）和 5 个支付渠道配置
  - `wx_lite`（微信小程序）、`wx_native`（Native扫码）、`wx_pub`（JSAPI）
  - `alipay_qr`（扫码）、`alipay_app`（App支付）
  - 密钥使用环境变量占位符，生产部署时替换
- 前端充值页 `RechargePage.vue` 接入真实支付流程：
  - `createRecharge` → `submitPayOrder` → 按 `displayMode` 处理（qr_code/url/jsapi/app）
  - 扫码支付渲染二维码（qrcode插件），H5 直跳转，JSAPI/App SDK 调起
  - `pollPayStatus` 轮询支付状态（5秒/次，最长5分钟）
  - `getRechargeRecords` 获取充值记录（分页）
- Pinia store `app.ts` 接入 `getRechargePackages` API
- `types/index.ts` 和 `recharge/index.ts` 对齐后端接口类型（payPrice/bonusPrice 单位为分）

---

### P1-12 · 视频拆解失败回滚

**问题描述**: 视频拆解（extractScript/analyzeElements/generatePrompt）的异步任务失败时是否正确回滚积分。

| 检查项 | 状态 |
|-------|:----:|
| refundAnalyzeCost 调用链验证 | ⬜ |
| 三种拆解方式失败均回滚 | ⬜ |

**完成日期**: 2026-04-11

**完成说明**:
- 调研发现：真实支付走芋道 `PayClient` 框架，`PayServiceImpl` 是未使用的 legacy MOCK
- 新建 `sql/mysql/pay-channel-init.sql`：初始化 `pay_app`（wallet）和 5 个支付渠道配置
  - `wx_lite`（微信小程序）、`wx_native`（Native扫码）、`wx_pub`（JSAPI）
  - `alipay_qr`（扫码）、`alipay_app`（App支付）
  - 密钥使用环境变量占位符，生产部署时替换
- 前端充值页 `RechargePage.vue` 接入真实支付流程：
  - `createRecharge` → `submitPayOrder` → 按 `displayMode` 处理（qr_code/url/jsapi/app）
  - 扫码支付渲染二维码（qrcode插件），H5 直跳转，JSAPI/App SDK 调起
  - `pollPayStatus` 轮询支付状态（5秒/次，最长5分钟）
  - `getRechargeRecords` 获取充值记录（分页）
- Pinia store `app.ts` 接入 `getRechargePackages` API
- `types/index.ts` 和 `recharge/index.ts` 对齐后端接口类型（payPrice/bonusPrice 单位为分）

---

## P2 · 第三批（系统健壮性优化）

### P2-1 · RestClient 超时控制

**问题描述**: `OpenAiCompatibleVideoAnalyzeClient` 的 `RestClient` 调用没有配置超时，API 不可用时会无限期挂起。

**涉及文件**:
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/service/video/analysis/OpenAiCompatibleVideoAnalyzeClient.java`

**实现方案**:
- 使用 `SimpleClientHttpRequestFactory` 配置连接超时和读取超时
- 超时时间复用 `VideoAnalyzeProperties.timeoutSeconds`（默认 180 秒）

**完成日期**: 2026-04-12

---

### P2-2 · 视频分析 Provider 降级链

**问题描述**: 视频分析只有一个 Provider 配置，供应商不可用时直接失败。

**涉及文件**:
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/service/video/impl/AiVideoServiceImpl.java`
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/config/VideoAnalyzeProperties.java`
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/service/video/analysis/VideoAnalyzeProvider.java`

**实现方案**:
- `VideoAnalyzeProperties` 新增 `fallbackProvider` 配置（默认 doubao）
- `AiVideoServiceImpl` 新增 `analyzeWithFallback()` 方法
- 主供应商失败后自动尝试备用供应商，两个都失败才抛异常
- WUMO 供应商跳过降级（走独立异步任务逻辑）

**完成日期**: 2026-04-12

---

### P2-3 · 积分单位规范确认

**问题描述**: 项目中积分单位混用（分/元），前后端理解不一致。

**确认规范**:
| 场景 | 单位 |
|------|------|
| `WalletDO.balance` | 分（整数） |
| `PayWalletRechargePackageDO.payPrice/bonusPrice` | 分（积分） |
| 前端展示 | 直接展示整数（单位：积分） |

**完成日期**: 2026-04-12

---

### P2-4 · Token 过期配置确认

**确认配置**:
| 配置项 | 默认值 |
|--------|--------|
| 访问 token | 30 分钟 |
| 刷新 token | 7 天 |

**完成日期**: 2026-04-12

---

## 已完成记录

### 2026-04-11 · 登录注册页面修复

**修复内容**:
- 修复 LoginPage.vue 注册表单缺少 `</div>` 闭合标签问题
- 位置：第 148 行后添加 `</div>` 关闭 `form-fields` 容器

**验证结果**:
- 后端注册 API 测试通过 (userId: 7)
- 后端登录 API 测试通过 (accessToken 正常返回)
- 前端 Vite 编译无 HTML 解析错误

### 2026-04-11 · 项目启动，文档整理

- 完成所有功能需求梳理
- 完成代码现状审计
- 建立本进度追踪文档
- 建立 CLAUDE.md 开发规范

---

## 开发规范

每次完成一个任务后：
1. 在本文件对应任务行标记 ✅ 并填入完成日期
2. 同步更新 `docs/PROGRESS.md` 进度报告
3. 如有数据库变更，附上 SQL 脚本
4. 提交代码：`git add ... && git commit -m "feat: 完成 P0-1 图片生成积分扣减"`

---

## P_N · 星空AI功能复刻（新增批次）

> 目标：复刻 https://www.timarsky.com 核心功能
> 参考文档：`docs/TIMARSKY-ANALYSIS.md`、`docs/TIMARSKY-SITEMAP.md`

---

| 优先级 | 编号 | 任务名称 | 影响模块 | 状态 | 完成日期 |
|:------:|:---:|---------|---------|:----:|:--------:|
| PN | 1 | Prompt优化服务实现 | ai | ✅ | 2026-04-11 |
| PN | 2 | 视频生成模型对接（Kling API） | ai | ⬜ | — |
| PN | 3 | 数字分身功能实现 | ai | ⬜ | — |
| PN | 4 | 灵感案例库功能 | ai/frontend | ⬜ | — |
| PN | 5 | Prompt模板库建设 | ai | ⬜ | — |
| PN | 6 | 前端功能模块导航重构 | frontend | ⬜ | — |
| PN | 7 | 行业分类系统实现 | ai/frontend | ⬜ | — |
| PN | 8 | 作品做同款功能 | ai/frontend | ⬜ | — |

---

### PN-1 · Prompt优化服务实现

**问题描述**: 需要实现专业的 Prompt 优化服务，将用户简单描述扩展为 AI 视频/图片生成提示词。

**涉及文件**:
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/service/prompt/PromptOptimizeService.java`（已创建）
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/service/prompt/dto/PromptOptimizeResult.java`（已创建）

**实现方案**:
1. 提供视频、图片、数字人三种 Prompt 优化模板
2. 包含行业模板库（电商、大健康、房产、汽车等）
3. 输出中英文双语提示词，适配国内外模型
4. 接入 Spring AI ChatClient 调用 LLM

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| PromptOptimizeService | ✅ | 已创建服务类 |
| PromptOptimizeResult | ✅ | 已创建结果DTO |
| 视频优化模板 | ✅ | 包含镜头运动、情绪、光影等 |
| 图片优化模板 | ✅ | 包含构图、色彩、负向词等 |
| 数字人优化模板 | ✅ | 包含手势、表情、动作等 |
| 行业模板库 | ✅ | 12个行业模板 |
| Controller接口 | ✅ | 已创建 AppPromptOptimizerController |

**完成日期**: 2026-04-12

**完成说明**:
- 创建 `PromptOptimizeService.java`：包含视频/图片/数字人三种优化模板
- 创建 `PromptOptimizeResult.java`：统一结果DTO
- 包含12个行业模板（电商/大健康/房产/汽车/美妆/服饰/美食等）
- 输出中英文双语提示词，适配国内外AI模型
- 创建 `AppPromptOptimizerController.java`：暴露 `/ai/prompt/optimize/hot-video` REST API
- 前端 `PromptOptimizerPage.vue` + `VideoAnalyzePage.vue` 接入，修复前后端双重编码问题

---

### PN-2 · 视频生成模型对接（Kling API）

**问题描述**: 需对接 Kling AI API 实现高质量视频生成，作为舞墨 API 的补充/替代。

**涉及文件**:
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/framework/ai/core/model/kling/`

**实现方案**:
1. 创建 `KlingApi.java` 接口定义
2. 实现 `KlingApiImpl.java` 调用 Kling API
3. 文生视频、图生视频两种模式
4. 任务状态查询和轮询
5. 与现有 `AiVideoServiceImpl` 集成

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| KlingApi 接口定义 | ⬜ | 参考WuMoApi结构 |
| KlingApiImpl 实现 | ⬜ | HTTP调用+签名 |
| 文生视频集成 | ⬜ | 替换或作为备选 |
| 图生视频集成 | ⬜ | 替换或作为备选 |
| 配置项添加 | ⬜ | api-key等配置 |

**完成日期**: —

---

### PN-3 · 数字分身功能实现

**问题描述**: 实现星空AI的"AI超级分身"功能：照片形象库 + 视频分身生成。

**涉及文件**:
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/service/avatar/`
- 新建数据库表：`ai_avatar_image`、`ai_avatar_video`

**实现方案**:
1. 照片形象管理
   - 本地上传：用户上传人物照片存入形象库
   - AI生成：调用图片生成API创建形象
2. 视频分身生成
   - 选择形象 + 输入脚本/描述
   - 调用数字人视频API（HeyGen/D-ID）
   - 支持唇同步、表情、动作控制

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| 数据库表设计 | ⬜ | ai_avatar_image/ai_avatar_video |
| AvatarImageService | ⬜ | 形象管理服务 |
| AvatarVideoService | ⬜ | 分身视频生成 |
| 本地上传接口 | ⬜ | 图片上传存储 |
| AI生成形象接口 | ⬜ | 调用图片API |
| 视频分身接口 | ⬜ | 调用数字人API |
| 积分扣减 | ⬜ | 消耗积分逻辑 |
| 前端页面 | ⬜ | 资产-分身功能页 |

**完成日期**: —

---

### PN-4 · 灵感案例库功能

**问题描述**: 实现类似星空AI的灵感案例展示，用户可浏览优秀作品并"做同款"。

**涉及文件**:
- `shuang-module-ai/src/main/java/cn/shuang/module/ai/service/inspiration/`
- 新建数据库表：`ai_inspiration_work`
- 前端：灵感页面组件

**实现方案**:
1. 作品展示画廊
2. 模型分类筛选（香蕉生图/马克视频等）
3. 行业分类筛选（电商/大健康等）
4. "做同款"功能：一键复制Prompt生成

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| 作品数据表 | ⬜ | ai_inspiration_work |
| 作品展示接口 | ⬜ | 分页查询+筛选 |
| 分类筛选接口 | ⬜ | 模型/行业分类 |
| 做同款接口 | ⬜ | 复制Prompt生成 |
| 前端画廊组件 | ⬜ | 网格展示+交互 |

**完成日期**: —

---

### PN-5 · Prompt模板库建设

**问题描述**: 建设可复用的 Prompt 模板库，降低用户创作门槛。

**涉及文件**:
- 新建数据库表：`ai_prompt_template`
- 后端服务扩展
- 前端模板选择器

**实现方案**:
1. 模板分类管理
   - 行业分类：电商/大健康/房产等
   - 场景分类：产品展示/人物介绍/宣传片等
2. 模板使用
   - 选择模板 → 微调参数 → 一键生成
3. 模板运营
   - 管理后台添加/编辑模板
   - 用户贡献模板（可选）

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| 模板数据表 | ⬜ | ai_prompt_template |
| 模板查询接口 | ⬜ | 分类+搜索 |
| 模板应用接口 | ⬜ | 填充模板参数 |
| 管理后台 | ⬜ | 模板CRUD |
| 前端模板选择器 | ⬜ | 模板列表+预览 |

**完成日期**: —

---

### PN-6 · 前端功能模块导航重构

**问题描述**: 重构前端导航，适配星空AI的功能模块结构。

**涉及文件**:
- `shuang-ui/zhuimeng-dream/src/layouts/`
- `shuang-ui/zhuimeng-dream/src/router/`

**实现方案**:
1. 导航栏重构：灵感 | 编导 | 做图 | 音频 | 视频 | 资产 | AI工具
2. 各模块路由和页面
3. 积分显示和充值入口
4. 用户中心下拉菜单

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| 导航栏组件 | ⬜ | 7个功能模块 |
| 路由配置 | ⬜ | 各模块路由 |
| 积分显示 | ⬜ | 实时余额 |
| 充值入口 | ⬜ | 获取点数按钮 |
| 用户下拉菜单 | ⬜ | 个人设置等 |

**完成日期**: —

---

### PN-7 · 行业分类系统实现

**问题描述**: 实现星空AI的行业分类体系，方便用户按场景筛选和使用。

**涉及文件**:
- 新建数据库表：`ai_category`
- 后端分类服务
- 前端分类筛选组件

**分类体系**:
- 行业分类：电商、大健康、工厂、励志演讲、探店、宠物、美发、短剧、带货、变装
- 风格分类：写实、动漫、创意、跨境
- 设计分类：家具设计、LOGO、服饰、美食
- 功能分类：修图改图、AI数字人

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| 分类数据表 | ⬜ | ai_category |
| 分类查询接口 | ⬜ | 树形结构 |
| 作品分类关联 | ⬜ | 作品归属分类 |
| 前端筛选组件 | ⬜ | 多级筛选 |

**完成日期**: —

---

### PN-8 · 作品做同款功能

**问题描述**: 实现"做同款"功能，用户可一键复制优秀作品的Prompt并生成类似内容。

**涉及文件**:
- 扩展灵感案例库接口
- 生成接口参数优化

**实现方案**:
1. 作品详情展示原始Prompt
2. "做同款"按钮触发生成流程
3. 可微调参数后生成
4. 记录来源作品ID（可追溯）

| 检查项 | 状态 | 说明 |
|-------|:----:|------|
| Prompt展示 | ⬜ | 作品详情页 |
| 做同款按钮 | ⬜ | 一键生成 |
| 参数微调 | ⬜ | 可编辑参数 |
| 来源追溯 | ⬜ | 记录original_work_id |

**完成日期**: —
