# 项目核心问题与解决方案

> 整理日期：2026-04-09
> 项目：shuang-pro
> 状态：待修复

---

## 问题总览

| 风险 | 数量 |
|:----:|:----:|
| 🔴 高危 | 8 |
| 🟡 中危 | 4 |

---

## 一、AI 生成积分扣减问题

### 问题 1.1：图片生成接口缺失积分扣减 🔴 高危

**问题描述**

`shuang-module-ai/src/main/java/cn/shuang/module/ai/service/image/AiImageServiceImpl.java` 的 `drawImage()` 方法仅保存记录并异步生成图片，**完全没有扣减用户积分**。用户可以无限生成图片而无需消耗积分，积分体系形同虚设。

**解决方案**

在保存记录前增加积分校验与扣减逻辑，并使用 `@Transactional` + 幂等 key 保证扣减只执行一次：

```java
// 1. 查询用户积分余额
Long balance = walletService.getBalance(userId);
if (balance < COSTS) {
    throw new PointInsufficientException();
}

// 2. 幂等检查：避免重复扣减
String idempotentKey = "draw_image:" + userId + ":" + UUID.randomUUID();
if (!idempotentService.tryAcquire(idempotentKey)) {
    throw new IdempotentProcessingException();
}

// 3. 扣减积分
walletService.deductPoints(userId, COSTS, "AI 图片生成");

try {
    // 4. 保存记录并异步生成
    AiImageDO image = ...;
    imageMapper.insert(image);
    getSelf().executeDrawImage(image, drawReqVO, model);
} catch (Exception e) {
    // 5. 生成失败时回滚积分
    walletService.addPoints(userId, COSTS, "AI 图片生成失败退款");
    throw e;
}
```

**影响模块**：`shuang-module-ai`

---

### 问题 1.2：视频生成失败不回滚积分 🔴 高危

**问题描述**

`AiVideoServiceImpl` 的 `textToVideo()`、`imageToVideo()` 等方法在扣减积分后，若异步执行失败，不会回滚积分，用户付费后若生成失败无法得到补偿。

**解决方案**

在异步执行方法中使用异步任务状态追踪，完成后判断是否成功：

```java
// 在 executeVideoGeneration 中捕获异常，标记状态
try {
    // 调用 AI 服务...
    wuMoApi.textToVideo(prompt, ...);
    imageMapper.updateStatus(id, SUCCESS);
} catch (Exception e) {
    imageMapper.updateStatus(id, FAIL);
    // 回滚积分：记录任务 ID 和积分，异步扫描失败任务退款
    refundService.scheduleRefund(userId, cost, "video_generation_failed:" + imageId);
    log.error("[视频生成失败] userId={}, imageId={}, cost={}", userId, imageId, cost);
}

// 或使用延迟任务：在创建视频记录时记录预扣积分，5分钟后扫描仍未成功的任务自动退款
```

**影响模块**：`shuang-module-ai`

---

## 二、代理分佣问题

### 问题 2.1：二级代理分佣链路未实现 🔴 高危

**问题描述**

当前 `distributeCommission()` 只计算直接上级的分佣，PRD 要求至少支持一级、二级代理，即：用户充值后，一级代理拿20%，用户的一级代理的上级（二级代理）也要拿8%。

**解决方案**

重构分佣链路，递归向上查找代理直到平台：

```java
/**
 * 递归分佣：用户充值后，向上所有代理按比例分佣
 * 例：用户A充值，一级代理B得20%，二级代理C（上级是B）得8%
 */
public void distributeCommission(Long userId, Long rechargeUserId, int points) {
    // 1. 查用户的上级代理
    AgencyUserDO agency = agencyUserMapper.selectByUserId(rechargeUserId);
    if (agency == null || agency.getParentId() == null) {
        return; // 无上级，不分佣
    }

    // 2. 一级代理分佣
    AgencyUserDO parent = agencyUserMapper.selectById(agency.getParentId());
    if (parent != null) {
        int rate = parent.getLevel() == 1
            ? agencyConfigService.getLevel1CommissionRate()    // 20%
            : agencyConfigService.getLevel2CommissionRate();  // 8%
        int commission = points * rate / 10000;  // 使用万分比
        agencyUserService.addPoints(parent.getUserId(), commission,
            "下级充值分佣[" + rechargeUserId + "]");
        recordCommissionLog(parent.getId(), rechargeUserId, commission, rate);

        // 3. 递归给更上级（二级代理的上级）分佣，比例更小
        if (parent.getParentId() != null) {
            AgencyUserDO grandParent = agencyUserMapper.selectById(parent.getParentId());
            if (grandParent != null) {
                int grandRate = agencyConfigService.getLevel3CommissionRate(); // 如3%
                int grandCommission = points * grandRate / 10000;
                agencyUserService.addPoints(grandParent.getUserId(), grandCommission,
                    "下下级充值分佣[" + rechargeUserId + "]");
                recordCommissionLog(grandParent.getId(), rechargeUserId, grandCommission, grandRate);
            }
        }
    }
}
```

**影响模块**：`shuang-module-agency`

---

### 问题 2.2：分佣计算逻辑不一致 🔴 高危

**问题描述**

`AppRechargeNotifyController` 中硬编码 20/8 百分比，而 `AgencyLevelEnum` 使用万分比（2000/800），两套规则并存维护困难。

**解决方案**

统一使用 `AgencyConfigService` 获取配置，移除硬编码：

```java
// 删除 AppRechargeNotifyController 中的硬编码
// 改用统一配置服务
int rate = agencyConfigService.getCommissionRate(parentAgency.getLevel());
int commission = points * rate / 10000;
```

---

### 问题 2.3：充值分佣缺少事务保障 🔴 高危

**问题描述**

`notifyRechargeOrder()` 中积分发放和分佣在同一方法中，但方法未标注 `@Transactional`，积分发放成功但分佣失败时事务不会回滚。

**解决方案**

将积分发放和分佣放入同一个 `@Transactional` 事务方法中：

```java
@Transactional(rollbackFor = Exception.class)
public void notifyRechargeOrder(Long orderId) {
    // 1. 更新订单状态（幂等）
    // 2. 积分发放
    walletService.addPoints(userId, points, "积分充值");
    // 3. 分佣（自动回滚）
    distributeCommission(userId, points);
}
```

---

## 三、积分钱包一致性问题

### 问题 3.1：并发扣减存在竞态条件 🔴 高危

**问题描述**

`WalletServiceImpl.deductPoints()` 先查询余额再扣减，Check 和 Act 之间存在时间窗口，高并发下可能导致余额扣成负数或超扣。

**解决方案**

使用乐观锁（version 字段）：

```java
// 方法1：使用乐观锁（推荐）
@Version
private Integer version;  // 在 WalletDO 中添加 version 字段

@Transactional
public void deductPointsWithVersion(Long userId, int amount, String reason) {
    WalletDO wallet = walletMapper.selectByUserId(userId);
    if (wallet.getBalance() < amount) {
        throw new PointInsufficientException();
    }
    int updated = walletMapper.deductBalanceWithVersion(userId, amount, wallet.getVersion());
    if (updated == 0) {
        throw new OptimisticLockException("积分扣减失败，请重试");
    }
    // 记录流水...
}

// 方法2：使用悲观锁（次选）
@Transactional
public void deductPointsWithLock(Long userId, int amount, String reason) {
    WalletDO wallet = walletMapper.selectByUserIdWithLock(userId);
    if (wallet.getBalance() < amount) {
        throw new PointInsufficientException();
    }
    walletMapper.deductBalance(userId, amount);
    // 记录流水...
}
```

Mapper SQL 改用 version 乐观锁：

```xml
<update id="deductBalanceWithVersion">
    UPDATE wallet
    SET balance = balance - #{amount}, version = version + 1
    WHERE user_id = #{userId}
      AND balance >= #{amount}
      AND version = #{version}
</update>
```

**影响模块**：`shuang-module-pay`

---

### 问题 3.2：积分扣减缺少完整流水记录 🔴 高危

**问题描述**

`deductPoints()` 和 `addPoints()` 没有同时记录积分流水（`wallet_transaction`），导致积分变动无法追踪和审计。

**解决方案**

每个积分变动都必须同时写入流水表：

```java
@Transactional(rollbackFor = Exception.class)
public void deductPoints(Long userId, int amount, String reason) {
    // 1. 扣减余额
    walletMapper.deductBalance(userId, amount);
    // 2. 记录流水（必须！）
    WalletTransactionDO tx = new WalletTransactionDO()
        .setUserId(userId)
        .setType(TransactionTypeEnum.CONSUME.getCode())
        .setAmount(-amount)
        .setBalanceBefore(xxx)  // 扣减前余额
        .setBalanceAfter(xxx)    // 扣减后余额
        .setReason(reason)
        .setBizNo(bizNo)
        .setCreateTime(new Date());
    walletTransactionMapper.insert(tx);
}
```

---

## 四、支付与安全问题

### 问题 4.1：支付 API Key 硬编码 🔴 高危

**问题描述**

`AiVideoServiceImpl.getWuMoApi()` 中包含硬编码的默认 API Key，存在安全风险。

**解决方案**

将 API Key 移入配置文件：

```yaml
# application.yml
shuang:
  ai:
    wumo:
      api-key: ${WUMO_API_KEY}  # 从环境变量读取
      base-url: https://api.wumo.com
```

代码中移除默认值和警告日志，改为强制要求配置：

```java
@RequiredArgsConstructor
public class WuMoApiImpl implements WuMoApi {
    private final ShuangProProperties properties;

    public WuMoApiImpl init() {
        if (StrUtil.isBlank(properties.getAi().getWumo().getApiKey())) {
            throw new IllegalStateException("未配置舞墨 API Key，请检查 shuang.ai.wumo.api-key 配置");
        }
        return this;
    }
}
```

**影响模块**：`shuang-module-ai`

---

### 问题 4.2：短信 Mock 模式安全风险 🔴 高危

**问题描述**

`MemberAuthServiceImpl.smsLogin()` 支持配置 mock-code 直接放行验证码，若生产环境未关闭 mock，验证码登录可被绕过。

**解决方案**

生产环境强制关闭 Mock：

```java
// 仅在 dev/test 环境允许 mock
@ConditionalOnProperty(name = "shuang.sms.mock-enable", havingValue = "true")
@Component
public class MockSmsChannelService implements SmsChannelService {
    // 仅开发环境使用...
}

// 生产默认配置
// application-prod.yml
shuang:
  sms:
    mock-enable: false  # 生产强制关闭
```

同时增加告警日志：

```java
if (Boolean.TRUE.equals(shuangProProperties.getSms().getMockEnable())) {
    log.warn("[smsLogin][MOCK MODE] 模拟登录已启用，请确认当前环境！");
}
```

**影响模块**：`shuang-module-member`

---

### 问题 4.3：充值回调无支付渠道校验 🔴 高危

**问题描述**

`AppRechargeNotifyController.notifyRechargeOrder()` 收到回调后直接发放积分，**未校验回调签名和渠道来源**，可能被伪造回调攻击。

**解决方案**

支付回调必须校验：

```java
@PostMapping("/notify/recharge")
public String notifyRechargeOrder(@RequestBody RechargeNotifyReqVO reqVO,
                                  HttpServletRequest request) {
    // 1. 校验签名（每种渠道不同）
    String channelId = identifyChannel(request);
    if ("wechat".equals(channelId)) {
        wechatPayConfig.validateNotify(reqVO);
    } else if ("alipay".equals(channelId)) {
        alipayPayConfig.validateNotify(reqVO);
    }

    // 2. 校验金额是否匹配
    PayOrderDO order = payOrderMapper.selectById(reqVO.getOrderId());
    if (order == null || order.getStatus() != PayOrderStatusEnum.PENDING) {
        return "success";  // 已处理返回成功避免重复回调
    }
    if (order.getAmount().compareTo(reqVO.getAmount()) != 0) {
        throw new IllegalStateException("回调金额与订单金额不符，疑似伪造");
    }

    // 3. 幂等更新订单状态
    // 4. 发放积分
    // ...
}
```

**影响模块**：`shuang-module-pay`

---

## 五、注册登录问题

### 问题 5.1：邀请码绑定逻辑未实现 🔴 高危

**问题描述**

`MemberAuthServiceImpl.register()` 中邀请码处理逻辑标注为 TODO，用户注册时无法绑定上级代理，影响整个代理体系的建立。

**解决方案**

补全邀请码绑定逻辑：

```java
@Transactional(rollbackFor = Exception.class)
public Long register(MemberRegisterReqVO reqVO, String userIp) {
    // 1. 创建用户...
    MemberUserDO user = createUser(reqVO);
    userMapper.insert(user);

    // 2. 处理邀请码绑定
    if (StrUtil.isNotBlank(reqVO.getInviteCode())) {
        // 邀请码 = 上级用户ID 的加密
        Long inviterId = decodeInviteCode(reqVO.getInviteCode());
        if (inviterId != null) {
            AgencyUserDO inviter = agencyUserMapper.selectByUserId(inviterId);
            if (inviter != null) {
                // 创建代理关系
                AgencyUserDO newAgency = new AgencyUserDO()
                    .setUserId(user.getId())
                    .setParentId(inviterId)
                    .setLevel(inviter.getLevel() + 1)  // 下级代理等级
                    .setBindTime(new Date());
                agencyUserMapper.insert(newAgency);
                log.info("[邀请码绑定] 用户 {} 绑定上级 {} 成功", user.getId(), inviterId);
            }
        }
    }

    // 3. 新用户注册奖励积分（可选）
    pointConfigService.grantRegisterPoints(user.getId());
    return user.getId();
}

// 邀请码编解码：用户ID -> Base64/Hash
public String encodeInviteCode(Long userId) {
    return Base64.getEncoder().encodeToString(("U" + userId).getBytes());
}
public Long decodeInviteCode(String code) {
    try {
        String decoded = new String(Base64.getDecoder().decode(code));
        if (decoded.startsWith("U")) {
            return Long.parseLong(decoded.substring(1));
        }
    } catch (Exception ignored) {}
    return null;
}
```

**影响模块**：`shuang-module-member`、`shuang-module-agency`

---

## 六、幂等性问题

### 问题 6.1：扣积分接口无幂等保护 🔴 高危

**问题描述**

`AiVideoService.textToVideo()`、`imageToVideo()` 等扣积分方法没有使用项目已有的 `@Idempotent` 注解，前端重复提交或网络重试会导致重复扣费。

**解决方案**

对所有涉及扣积分的接口添加幂等注解：

```java
@PostMapping("/text-to-video")
@Idempotent(key = "ai:video:text-to-video:#{#reqVO.userId}:#{#reqVO.prompt.hashCode()}",
             expireMillis = 30000,
             note = "AI 文生视频")
public CommonResult<Long> textToVideo(@RequestBody VideoGenerateReqVO reqVO) {
    // 业务逻辑...
}
```

前端也需要传递唯一请求 ID：

```typescript
// 生成页请求带上 clientId
const clientId = localStorage.getItem('client_id') || UUID.generate()
localStorage.setItem('client_id', clientId)
axios.post('/ai/video/text-to-video', { ...reqVO, clientId })
```

**影响模块**：`shuang-module-ai`、`shuang-module-pay`

---

### 问题 6.2：充值接口无幂等保护 🔴 高危

**问题描述**

`/pay/order/submit` 提交支付订单接口无幂等保护，重复提交会创建多个订单。

**解决方案**

```java
@PostMapping("/submit")
@Idempotent(key = "pay:order:submit:#{#reqVO.userId}:#{#reqVO.packageId}:#{#reqVO.clientId}",
             expireMillis = 60000)
public CommonResult<Long> submitOrder(@RequestBody PayOrderSubmitReqVO reqVO) {
    // 业务逻辑...
}
```

---

## 七、修复��先级建议

### 第一批（上线前必须修复）

| 序号 | 问题 | 修复方式 |
|:--:|------|----------|
| 1 | 图片生成无积分扣减 | 补全扣减逻辑 |
| 2 | 视频生成失败无回滚 | 补全回滚逻辑 |
| 3 | 并发扣减竞态条件 | 改用乐观锁 |
| 4 | 扣积分无幂等保护 | 添加 @Idempotent |
| 5 | 邀请码绑定未实现 | 补全绑定逻辑 |
| 6 | 充值回调无签名校验 | 添加校验逻辑 |
| 7 | 支付 API Key 硬编码 | 移除硬编码 |
| 8 | 短信 Mock 安全风险 | 生产关闭 Mock |

### 第二批（上线后尽快修复）

| 序号 | 问题 | 修复方式 |
|:--:|------|----------|
| 9 | 二级代理分佣链路 | 实现递归分佣 |
| 10 | 分佣两套规则 | 统一配置服务 |
| 11 | 充值分佣无事务 | 添加 @Transactional |
| 12 | 积分变动无流水 | 补全流水记录 |

### 第三批（后续优化）

| 序号 | 问题 | 修复方式 |
|:--:|------|----------|
| 13 | AI 服务单点依赖 | 增加备用渠道 |
| 14 | PRD 模型未全部实现 | 补充模型对接 |

---

## 附：相关文件清单

| 模块 | 关键文件 |
|------|---------|
| AI 生成 | `shuang-module-ai/src/.../service/image/AiImageServiceImpl.java` |
| | `shuang-module-ai/src/.../service/video/impl/AiVideoServiceImpl.java` |
| 支付钱包 | `shuang-module-pay/src/.../service/impl/WalletServiceImpl.java` |
| | `shuang-module-pay/src/.../controller/app/AppRechargeNotifyController.java` |
| 会员登录 | `shuang-module-member/src/.../service/auth/MemberAuthServiceImpl.java` |
| 代理分佣 | `shuang-module-agency/src/.../controller/app/AppRechargeNotifyController.java` |
