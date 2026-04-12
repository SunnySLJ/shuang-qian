# 技术难点清单

> 记录项目中所有复杂、有风险、需要特别注意的技术点。

## 1. 积分扣减一致性

### 1.1 积分扣减标准流程

```
1. 生成幂等 key（bizOrderNo = UUID）
2. 检查积分余额（hasEnoughPoints）
3. 扣减积分（deductPoints）—— 在事务中
4. 创建业务记录（insert）
5. 异步执行（如有）
6. 失败时回滚积分（addPoints）—— 在异步 catch 中调用
```

### 1.2 当前实现情况

**图片生成（正确）**：
- bizOrderNo = `"img:" + UUID` — ✅ 唯一可追溯
- deductPoints 传入 bizOrderNo — ✅
- 异步用 `@Async` + `SpringUtil.getBean()` — ✅ AOP 代理有效
- 失败回滚用同一 bizOrderNo — ✅

**视频生成（已修复 ✅）**：
- ✅ deductPoints 传入明确 bizOrderNo（`video:{bizType}:{userId}:{uuid}`）
- ✅ 改为 Spring `ThreadPoolTaskExecutor`（`aiVideoTaskExecutor`）
- ✅ 失败回滚通过 `getTransactionByBizOrderNo` 找到原始 transaction 进行回滚
- ✅ 使用 `TransactionSynchronizationManager.registerSynchronization` 确保事务提交后执行

### 1.3 相关文件

- `AiVideoServiceImpl.java` — 视频生成/分析异步执行逻辑
- `AiVideoTaskExecutorConfiguration.java` — 线程池配置
- `WalletServiceImpl.getTransactionByBizOrderNo` — 根据 bizOrderNo 查找流水

---

## 2. 异步处理

### 2.1 修复完成 ✅

**视频生成已改为 Spring `ThreadPoolTaskExecutor`**：
```java
// AiVideoServiceImpl 中使用 aiVideoTaskExecutor
aiVideoTaskExecutor.execute(TtlRunnable.get(task));
```

**线程池配置**（`AiVideoTaskExecutorConfiguration.java`）：
- 核心线程：5，最大线程：20，队列容量：100
- 线程名前缀：`ai-video-`
- 拒绝策略：`CallerRunsPolicy`（保证任务不丢失）
- 优雅停机：`awaitTerminationSeconds = 30`

### 2.2 事务后执行

使用 `TransactionSynchronizationManager.registerSynchronization` 确保异步任务在主事务提交后执行：

```java
TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
    @Override
    public void afterCommit() {
        aiVideoTaskExecutor.execute(TtlRunnable.get(task));
    }
});
```

### 2.3 相关文件

- `AiVideoTaskExecutorConfiguration.java` — 线程池配置
- `YudaoAsyncAutoConfiguration.java` — 确认使用 TtlRunnable 进行上下文传播

---

## 3. 幂等性

### 3.1 当前实现

使用 `@Idempotent` 注解 + `ExpressionIdempotentKeyResolver`。

### 3.2 视频分析幂等 key 已修复 ✅

**修复前（有问题）**：
```java
key = "'ai_video_analyze_' + videoUrl.hashCode() + '_' + provider + '_' + clientId"
```

**修复后**（`VideoAnalyzeIdempotentKeyResolver.java`）：
```java
// 使用 MurmurHash3（跨 JVM 稳定）+ 显式包含 analyzeType
String hash = String.valueOf(MurmurHash.hash64(safeUrl));
return String.format("video_analyze:%s:%s:%s:%s", hash, provider, analyzeType, clientId);
```

**已修复的问题**：
- ✅ `String.hashCode()` → `MurmurHash.hash64()`（稳定）
- ✅ 幂等 key 包含 `analyzeType`（不同分析类型互不冲突）

### 3.3 相关文件

- `VideoAnalyzeIdempotentKeyResolver.java` — 自定义幂等 key 解析器
- `AiVideoController.java` — 视频分析接口的 `@Idempotent` 注解

---

## 4. 代理分佣幂等性

### 4.1 已修复 ✅

**充值回调分佣增加幂等保护**：

- `CommissionServiceImpl.calculateRechargeCommission` 和 `calculateConsumeCommission` 在插入记录前，先通过 `commissionRecordMapper.selectByBizOrderNoAndBrokerageUserId` 检查是否已存在相同 `bizOrderNo` 和 `brokerageUserId` 的记录
- 如果记录已存在，跳过计算，直接返回
- `CommissionRecordMapper` 已新增该查询方法并建立唯一索引

### 4.2 相关文件

- `CommissionServiceImpl.java` — 分佣计算（含幂等检查）
- `CommissionRecordMapper.java` — 新增 `selectByBizOrderNoAndBrokerageUserId`

---

## 5. 多 AI Provider 集成

### 5.1 Base64 内存问题已修复 ✅

`OpenAiCompatibleVideoAnalyzeClient.toVideoDataUrl()` 增加文件大小限制：

```java
private static final long MAX_VIDEO_SIZE_BYTES = 50 * 1024 * 1024L; // 50MB

// 先检查 Content-Length，再流式读取验证
if (contentLength > MAX_VIDEO_SIZE_BYTES || bytesRead > MAX_VIDEO_SIZE_BYTES) {
    throw new RuntimeException("视频文件超过50MB限制，无法处理");
}
```

**策略**：
1. 先通过 `Content-Length` header 快速判断，超过直接拒绝
2. 流式读取中累积计数，超过 50MB 中断并抛异常
3. 避免一次性将大文件加载到内存再 Base64 编码

### 5.2 Provider 降级链已实现 ✅

`AiVideoServiceImpl` 新增 `analyzeWithFallback()` 方法：

```java
// 主供应商失败后自动尝试备用供应商（默认 doubao）
String result = analyzeWithFallback(provider, analyzeType, videoUrl);
```

**配置项**（`VideoAnalyzeProperties`）：
```yaml
yudao:
  ai:
    video-analyze:
      fallback-provider: doubao  # 主供应商失败后降级到此供应商
      timeout-seconds: 180
```

**降级规则**：
1. WUMO 供应商跳过（走独立异步任务逻辑）
2. 主备相同时跳过降级
3. 主供应商失败才尝试备用，两个都失败抛出主供应商错误

### 5.3 RestClient 超时已配置 ✅

`OpenAiCompatibleVideoAnalyzeClient` 的 RestClient 添加了 `SimpleClientHttpRequestFactory` 超时：

```java
Duration timeout = Duration.ofSeconds(properties.getTimeoutSeconds());
SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
requestFactory.setConnectTimeout(timeout);
requestFactory.setReadTimeout(timeout);
RestClient restClient = RestClient.builder()
        .requestFactory(requestFactory)
        // ...
        .build();
```

**超时说明**：
- 连接超时 + 读取超时均使用 `timeoutSeconds` 配置（默认 180 秒）
- 视频下载（Base64 编码）也使用同一配置
- 避免了 API 调用无限期挂起

### 5.4 待完善

- [ ] 支持更多备用供应商（目前最多 1 个备用）

---

## 6. 支付集成

### 6.1 当前状态

本地配置中 WeChat Pay 和 Alipay 的密钥均为空字符串：
```yaml
WECHAT_PAY_APP_ID:
WECHAT_PAY_MCH_ID:
WECHAT_PAY_API_KEY:
ALIPAY_APP_ID:
ALIPAY_PRIVATE_KEY:
```

无法在本地测试完整支付流程

### 6.2 潜在风险

- 支付回调 URL 硬编码为 `http://localhost:48080/admin-api/pay/notify/order`
- NAT 内网穿透问题（生产环境需要公网可访问）
- 无支付超时和重复回调处理验证

### 6.3 待完善

- [ ] 支付密钥配置化（通过环境变量）
- [ ] 回调地址可配置
- [ ] 支付幂等保护（基于 bizOrderNo）

---

## 7. 乐观锁

### 7.1 当前实现

`WalletDO` 有 `version` 字段，SQL 使用：
```sql
UPDATE pay_wallet SET balance = ?, version = version + 1
WHERE id = ? AND version = ?
```

### 7.2 重试机制已修复 ✅

`WalletServiceImpl` 的 `deductPoints` 现在使用 `deductBalanceWithRetry` 方法：

```java
private int deductBalanceWithRetry(Long userId, Integer amount, Integer currentVersion) {
    int retry = 0;
    while (retry < 3) {
        int updated = pointsWalletMapper.deductBalanceWithVersion(userId, amount, currentVersion);
        if (updated > 0) {
            return updated;
        }
        retry++;
        if (retry >= 3) break;
        // 获取最新版本后重试
        WalletDO latest = getWallet(userId);
        currentVersion = latest.getVersion();
        Thread.sleep(50);
    }
    return 0;
}
```

- ✅ 乐观锁冲突时最多重试 3 次
- ✅ 每次重试间隔 50ms
- ✅ 重新查询最新 version 后重试

### 7.3 相关文件

- `WalletServiceImpl.deductBalanceWithRetry` — 乐观锁重试逻辑
- `WalletServiceImpl.getTransactionByBizOrderNo` — 通过 bizOrderNo 查找流水（用于退款）
- `WalletTransactionMapper.selectByBizOrderNo` — MyBatis  mapper 方法

---

## 8. 积分单位规范 ✅ 已确认

### 8.1 项目规范（已确认）

| 场景 | 内部存储单位 | 说明 |
|------|------------|------|
| `WalletDO.balance` | **分（整数）** | 数据库直接存储 |
| `PayWalletRechargePackageDO.payPrice` | **分（积分）** | 1元=100分 |
| `PayWalletRechargePackageDO.bonusPrice` | **分（积分）** | 赠送积分 |
| `AppPayWalletRechargeRespVO.totalPrice` | **分（积分）** | 充值总额 |
| 配置文件 `price-per-yuan: 3` | — | 仅用于自定义充值换算 |

### 8.2 前端展示规范（已统一）

- `Wallet.balance`/`totalRecharge`/`totalUsed` → 直接展示整数（单位：积分）
- `PayWalletRechargePackage.payPrice` → 直接展示整数（单位：积分）
- 不需要额外除以 100 或乘以任何系数

### 8.3 配置字段命名

- `price-per-yuan: 3` 的含义：**每 1 元人民币 = 3 积分**
- 仅在用户自定义充值时用于积分换算，不影响套餐显示

---

## 9. Session / Token 管理 ✅ 已确认

### 9.1 当前实现

OAuth2 token 存在 Redis，使用 yudao-cloud 框架的默认配置。

### 9.2 Token 过期配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `yudao.captcha.token-expire-time` | 5 分钟 | 验证码 token |
| `yudao.security.token-expire-time` | 30 分钟 | 访问 token |
| `yudao.security.refresh-token-expire-time` | 7 天 | 刷新 token |

### 9.3 风险说明

- Token 刷新无并发锁，存在 race condition 风险（低概率）
- Refresh token 撤销后需确保正确处理 `InvalidTokenException`
- 生产环境建议通过环境变量注入 token 过期时间

---

## 10. 数据库事务边界

### 10.1 当前事务划分

**图片生成（正确）**：
```java
@Transactional
public void drawImage() {
    deductPoints();    // 事务中
    insertRecord();    // 事务中
    transactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
            afterCommit() { asyncExecute(); }
        }
    );
}
```

**视频生成（需确认）**：
- `deductPoints` 和 `insert` 在 `@Transactional` 中
- 异步用 `new Thread()` 在 `afterCommit` 中

### 10.2 潜在问题

- 视频生成如果 `afterCommit` 中异步任务失败，`transactionId` 无法追踪
- 建议增加 `bizOrderNo` 到事务上下文中

### 10.3 待修复

- [ ] 视频生成：确认事务提交后异步执行
- [ ] 增加 bizOrderNo 到事务记录中

---

## 技术难点优先级

| 优先级 | 难点 | 状态 |
|:------:|------|------|
| P0 | 视频生成异步改线程池 | ✅ 已完成 |
| P0 | 视频生成 bizOrderNo 不一致 | ✅ 已完成 |
| P0 | 支付分佣无幂等 | ✅ 已完成 |
| P1 | 视频分析幂等 key 含 hashCode | ✅ 已完成 |
| P1 | Base64 内存 OOM | ✅ 已完成 |
| P1 | 乐观锁无重试 | ✅ 已完成 |
| P2 | 视频下载超时控制 | ✅ 已完成 |
| P2 | Provider 降级链 | ✅ 已完成 |
| P2 | 积分单位规范 | ✅ 已确认 |
| P2 | Token 过期配置 | ✅ 已确认 |

## 11. 前端页面问题

**充值页面（RechargePage.vue）**：
- `payPrice`/`bonusPrice`/`totalPrice` 单位统一为分（积分），与后端 `PayWalletRechargePackageDO` 保持一致
- `pkgPoints` → 直接返回 `payPrice`（已包含 bonusPrice）
- `pkgBonusPoints` → 直接返回 `bonusPrice`（赠送积分）
- `pkgRatio` → 固定返回 `'100'`（1 元 = 100 分）
- `rechargeRecordPoints` → 直接返回 `totalPrice`
- `customRecharge` → `payPrice = Math.floor(customPoints / 3 * 100)`（积分转分）

**图片生成页面（ImageGeneratePage.vue）**：
- `clientId` 移除随机数 `Math.random()`，改为 `image-generate-${Date.now()}`

**视频生成页面（VideoGeneratePage.vue）**：
- `durations` 积分与后端配置对齐（5s=20分, 15s=40分, 30s=60分）
- `clientId` 移除随机数，改为 `video-generate-${Date.now()}`
- `videoHistory` 从硬编码假数据改为调用 `getVideoPage()` 真实 API

**前端积分单位规范**：
- `Wallet.balance`/`totalRecharge`/`totalUsed` → 单位是分（整数），后端存储和返回
- 前端展示时无需额外转换（直接展示整数）
- `PayWalletRechargePackage.payPrice`/`bonusPrice` → 单位是分（整数），直接展示
- `AppPayWalletRechargeRespVO.totalPrice` → 单位是分（积分），直接展示

**Prompt 优化页面（PromptOptimizerPage.vue + VideoAnalyzePage.vue）**：
- 移除前端 `buildHotVideoOptimizationInput` 预格式化逻辑，改为直接传递原始 `rawPrompt`
- 后端 `PromptOptimizeService.optimizeHotVideoPrompt` 统一接收 `industry/platform/targetModel` 参数，在 system prompt 中拼装
- 修复前后端双重编码问题：`industry/platform/targetModel` 不再被前端预格式化成文字段落后再传入后端再次格式化

## 更新日志

- 2026-04-12: 初始化技术难点清单
- 2026-04-12: 完成所有 P0/P1 级优化（视频异步线程池、bizOrderNo一致性、分佣幂等、视频分析幂等key、Base64限制、乐观锁重试）
- 2026-04-12: 完成前端页面修复（充值页面积分单位统一、图片/视频生成幂等clientId、视频时长积分配置对齐、视频历史接入真实API、Prompt优化器前后端数据流修复）
- 2026-04-12: 完成所有 P2 级优化（RestClient超时控制、视频分析Provider降级链、积分单位规范确认、Token过期配置确认）
