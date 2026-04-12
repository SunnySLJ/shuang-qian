# Shuang-Pro 开发实施计划

## 一、现有项目基础分析

### 1.1 后端项目 (shuang-qian)

**项目结构：**
```
shuang-qian/
├── shuang-dependencies/          # 统一依赖管理 ✅
├── shuang-framework/             # 框架核心 ✅
├── shuang-server/                # 主启动项目 ✅
├── shuang-module-system/         # 系统模块 ✅
├── shuang-module-infra/          # 基础设施模块 ✅
├── shuang-module-pay/            # 支付模块 ✅ (已有基础)
├── shuang-module-member/         # 会员模块 ✅
├── shuang-module-mall/           # 商城模块 ✅
├── shuang-module-ai/             # AI 模块 ✅ (已有多个 AI 模型对接)
└── shuang-module-agency/         # 代理模块 ✅ (基础结构已有)
```

**现有能力评估：**

| 模块 | 现有内容 | 需补充 | 完成度 |
|------|---------|--------|--------|
| agency | 代理用户表、绑定接口、分配接口 | 充值分成逻辑、升级逻辑 | 60% |
| ai | 对话/绘图模型对接 | 视频生成、爆款拆解 | 40% |
| pay | 支付基础框架 | 微信/支付宝充值接口 | 50% |
| mall | 分销/佣金功能 | 代理分成结算 | 70% |

### 1.2 前端项目 (shuang-ui/zhuimeng-dream)

**技术栈：** Vue 3 + TypeScript + Vite

**现有前端结构：**
```
shuang-ui/
└── zhuimeng-dream/
    ├── src/views/              # 页面视图
    ├── src/api/                # 前端 API 封装
    ├── src/router/             # 路由配置
    ├── src/components/         # 组件
    └── vite.config.ts          # Vite 配置
```

**需新增页面：**
- 首页 (AI 创作入口)
- AI 视频生成页
- 爆款拆解页
- 代理中心
- 积分充值页
- 灵感案例库

---

## 二、开发任务清单

### Phase 1 - 基础架构完善 (第 1-2 周)

#### 1.1 数据库初始化

**优先级：P0**

```bash
# 执行顺序
1. sql/mysql/ruoyi-vue-pro.sql      # 基础系统表
2. sql/mysql/shuang-pro-agency.sql  # 代理系统表
3. sql/mysql/shuang-pro-ai.sql      # AI 生成记录表
4. sql/mysql/shuang-pro-wallet.sql  # 积分钱包表
```

**需新建的表：**
| 表名 | 说明 | 状态 |
|------|------|------|
| `agency_user` | 代理用户表 | ✅ 已有 |
| `agency_commission_record` | 佣金记录表 | ⏳ 待建 |
| `agency_point_transfer` | 积分分配记录表 | ✅ 已有 |
| `pay_wallet` | 积分钱包表 | ⏳ 待建 |
| `pay_wallet_transaction` | 积分流水表 | ⏳ 待建 |
| `ai_generation_record` | AI 生成记录表 | ⏳ 待建 |
| `ai_inspiration_case` | 灵感案例表 | ⏳ 待建 |
| `agency_config` | 代理配置表 | ✅ 已有 |

#### 1.2 代理模块完善

**文件清单：**

| 文件路径 | 类型 | 优先级 |
|---------|------|--------|
| `shuang-module-agency/dal/mysql/CommissionRecordMapper.java` | CREATE | P0 |
| `shuang-module-agency/dal/dataobject/CommissionRecordDO.java` | CREATE | P0 |
| `shuang-module-agency/service/CommissionService.java` | CREATE | P0 |
| `shuang-module-agency/service/impl/CommissionServiceImpl.java` | CREATE | P0 |
| `shuang-module-agency/controller/app/vo/AppCommissionReqVO.java` | CREATE | P0 |
| `shuang-module-agency/enums/CommissionBizTypeEnum.java` | CREATE | P1 |

**核心逻辑实现：**

```java
// CommissionServiceImpl.java - 分成计算示例
@Service
public class CommissionServiceImpl implements CommissionService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateCommission(Long userId, Integer rechargeAmount) {
        // 1. 查询用户的代理链
        AgencyUserDO user = agencyUserMapper.selectByUserId(userId);
        if (user == null || user.getBrokerageUserId() == null) {
            return; // 没有上级代理，无需分成
        }
        
        // 2. 查询上级代理等级
        AgencyUserDO level1Agent = agencyUserMapper.selectById(user.getBrokerageUserId());
        Integer rate = getCommissionRate(level1Agent.getLevel()); // 一级 20%，二级 8%
        
        // 3. 计算分成金额
        Integer commissionAmount = rechargeAmount * rate / 10000;
        
        // 4. 创建佣金记录
        CommissionRecordDO record = new CommissionRecordDO();
        record.setUserId(userId);
        record.setBrokerageUserId(level1Agent.getUserId());
        record.setAmount(commissionAmount);
        record.setCommissionRate(rate);
        record.setStatus(1); // 已结算
        record.setSettleTime(LocalDateTime.now());
        commissionRecordMapper.insert(record);
        
        // 5. 增加上级代理积分钱包
        walletService.addPoints(level1Agent.getUserId(), commissionAmount, "下级充值分成");
    }
    
    private Integer getCommissionRate(Integer level) {
        if (level == 1) return 2000; // 20%
        if (level == 2) return 800;  // 8%
        return 0;
    }
}
```

#### 1.3 积分钱包模块

**文件清单：**

| 文件路径 | 类型 | 优先级 |
|---------|------|--------|
| `shuang-module-pay/dal/mysql/WalletMapper.java` | CREATE | P0 |
| `shuang-module-pay/dal/dataobject/WalletDO.java` | CREATE | P0 |
| `shuang-module-pay/dal/mysql/WalletTransactionMapper.java` | CREATE | P0 |
| `shuang-module-pay/dal/dataobject/WalletTransactionDO.java` | CREATE | P0 |
| `shuang-module-pay/service/WalletService.java` | CREATE | P0 |
| `shuang-module-pay/service/impl/WalletServiceImpl.java` | CREATE | P0 |

**核心方法：**

```java
// WalletService.java
public interface WalletService {
    /**
     * 获取用户钱包
     */
    WalletDO getWallet(Long userId);
    
    /**
     * 增加积分
     */
    void addPoints(Long userId, Integer amount, String description);
    
    /**
     * 扣减积分
     */
    void deductPoints(Long userId, Integer amount, String description);
    
    /**
     * 检查积分是否足够
     */
    boolean hasEnoughPoints(Long userId, Integer requiredAmount);
    
    /**
     * 获取积分流水
     */
    List<WalletTransactionDO> getTransactions(Long userId, Integer page, Integer size);
}
```

---

### Phase 2 - AI 生成功能 (第 3-4 周)

#### 2.1 AI 生图功能增强

**现有基础：** `shuang-module-ai` 已对接多个绘图模型

**需补充：**
- 积分扣减逻辑
- 生成历史记录
- 队列管理（防止并发过高）

**文件清单：**

| 文件路径 | 类型 | 优先级 |
|---------|------|--------|
| `shuang-module-ai/service/GenerationService.java` | CREATE | P0 |
| `shuang-module-ai/service/impl/GenerationServiceImpl.java` | CREATE | P0 |
| `shuang-module-ai/controller/app/AppGenerationController.java` | CREATE | P0 |

#### 2.2 AI 视频生成对接

**推荐对接的 API 服务：**

| 服务 | 功能 | 价格 | 文档 |
|------|------|------|------|
| 硅基流动 | 文生视频/图生视频 | ¥0.05/次 | siliconflow.cn |
| 即梦 AI | 视频生成 | ¥0.1/次 | jimeng.jianying.com |
| 可灵 AI | 图生视频 | ¥0.08/次 | kilng.kuaishou.com |
| RunWay | 专业视频生成 | $0.07/次 | runwayml.com |

**实现代码框架：**

```java
// VideoGenerationService.java
@Service
public class VideoGenerationService {
    
    @Resource
    private WalletService walletService;
    
    @Resource
    private GenerationRecordMapper recordMapper;
    
    /**
     * 文生视频
     */
    @Async
    public String generateVideoFromText(Long userId, String prompt, String style) {
        // 1. 检查积分
        if (!walletService.hasEnoughPoints(userId, 50)) {
            throw new ServiceException("积分不足，需要 50 积分");
        }
        
        // 2. 预扣积分
        walletService.deductPoints(userId, 50, "AI 视频生成");
        
        // 3. 创建生成记录
        GenerationRecordDO record = new GenerationRecordDO();
        record.setUserId(userId);
        record.setGenerationType(GenerationTypeEnum.VIDEO.getCode());
        record.setInputText(prompt);
        record.setStatus(0); // 处理中
        recordMapper.insert(record);
        
        // 4. 调用外部 API 生成视频
        try {
            String videoUrl = callVideoApi(prompt, style);
            record.setOutputUrl(videoUrl);
            record.setStatus(1); // 完成
        } catch (Exception e) {
            record.setStatus(2); // 失败
            record.setErrorMessage(e.getMessage());
            // 5. 失败退款
            walletService.addPoints(userId, 50, "视频生成失败退款");
        }
        recordMapper.updateById(record);
        
        return record.getId().toString();
    }
}
```

#### 2.3 爆款拆解功能

**技术实现方案：**

1. **视频上传** → 存入 OSS
2. **语音提取** → 使用 Whisper API 转文字
3. **画面分析** → 使用视觉大模型分析元素
4. **提示词生成** → 使用大语言模型生成同款提示词

**依赖的 API：**
- 阿里云 OSS（视频存储）
- OpenAI Whisper（语音转文字）
- GPT-4V 或 Claude（画面分析）

---

### Phase 3 - 充值系统 (第 5 周)

#### 3.1 微信支付对接

**前置准备：**
1. 申请微信商户号
2. 获取 AppID、MchID、API 密钥
3. 下载商户证书

**实现文件：**

| 文件路径 | 类型 |
|---------|------|
| `shuang-module-pay/controller/app/AppRechargeController.java` | CREATE |
| `shuang-module-pay/service/RechargeService.java` | CREATE |
| `shuang-module-pay/service/impl/WechatPayService.java` | CREATE |

**回调处理逻辑：**

```java
// WechatPayService.java - 支付回调
@PostMapping("/wechat/callback")
public String wechatCallback(@RequestBody String xmlData) {
    try {
        // 1. 解析回调数据
        Map<String, String> data = parseXml(xmlData);
        String outTradeNo = data.get("out_trade_no");
        String transactionId = data.get("transaction_id");
        
        // 2. 验证签名
        if (!verifySignature(data)) {
            return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
        }
        
        // 3. 更新订单状态
        RechargeOrderDO order = orderMapper.selectByOrderNo(outTradeNo);
        order.setStatus(1); // 已支付
        order.setTransactionId(transactionId);
        orderMapper.updateById(order);
        
        // 4. 增加用户积分
        walletService.addPoints(order.getUserId(), order.getPoints(), "充值获得");
        
        // 5. 计算代理分成
        commissionService.calculateCommission(order.getUserId(), order.getAmount());
        
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
    } catch (Exception e) {
        log.error("微信支付回调失败", e);
        return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
    }
}
```

#### 3.2 支付宝对接

流程与微信支付类似，使用支付宝开放平台 SDK。

---

### Phase 4 - 前端开发 (第 6-7 周)

#### 4.1 需新增的页面

| 页面 | 路由 | 优先级 |
|------|------|--------|
| 首页 | `/` | P0 |
| AI 生图 | `/ai/image` | P0 |
| AI 视频 | `/ai/video` | P0 |
| 爆款拆解 | `/ai/analyze` | P1 |
| 灵感案例 | `/inspiration` | P1 |
| 代理中心 | `/agency` | P0 |
| 积分充值 | `/recharge` | P0 |
| 个人中心 | `/profile` | P0 |

#### 4.2 前端组件开发

**基于 shuang-qian-ui 现有组件扩展：**

```
src/views/
├── ai/
│   ├── image/            # AI 生图页面
│   │   ├── index.vue
│   │   ├── components/
│   │   │   ├── PromptInput.vue      # 提示词输入
│   │   │   ├── StyleSelector.vue    # 风格选择
│   │   │   └── PreviewArea.vue      # 预览区域
│   │   └── hooks/
│   │       └── useImageGeneration.ts
│   ├── video/
│   │   ├── index.vue     # AI 视频页面
│   │   └── hooks/
│   │       └── useVideoGeneration.ts
│   └── analyze/
│       ├── index.vue     # 爆款拆解页面
│       └── components/
│           └── VideoUploader.vue
├── agency/
│   ├── index.vue         # 代理中心
│   ├── components/
│   │   ├── AgencyStats.vue       # 代理数据概览
│   │   ├── PointTransfer.vue     # 积分分配
│   │   └── ChildrenList.vue      # 下级列表
│   └── hooks/
│       └── useAgencyData.ts
└── recharge/
    ├── index.vue         # 积分充值
    └── components/
        └── PaymentMethod.vue       # 支付方式选择
```

---

## 三、立即执行任务

### Task 1: 数据库表创建

```sql
-- 1. 佣金记录表
CREATE TABLE IF NOT EXISTS `agency_commission_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '被分成用户 ID',
    `brokerage_user_id` BIGINT NOT NULL COMMENT '分成获得用户 ID',
    `biz_type` TINYINT NOT NULL COMMENT '业务类型：1-充值分成，2-消费分成',
    `order_id` BIGINT COMMENT '关联订单 ID',
    `amount` INT NOT NULL COMMENT '分成金额 (积分)',
    `commission_rate` INT NOT NULL COMMENT '分成比例 (万分比)',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待结算，1-已结算，2-已取消',
    `settle_time` DATETIME COMMENT '结算时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_brokerage_user_id` (`brokerage_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金记录表';

-- 2. 积分钱包表
CREATE TABLE IF NOT EXISTS `pay_wallet` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户 ID',
    `balance` INT DEFAULT 0 COMMENT '可用余额 (积分)',
    `frozen_balance` INT DEFAULT 0 COMMENT '冻结余额',
    `total_recharge` INT DEFAULT 0 COMMENT '累计充值',
    `total_used` INT DEFAULT 0 COMMENT '累计消耗',
    `total_received` INT DEFAULT 0 COMMENT '累计收到分配',
    `total_given` INT DEFAULT 0 COMMENT '累计分配给出',
    `expire_time` DATETIME COMMENT '过期时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分钱包表';

-- 3. 积分流水表
CREATE TABLE IF NOT EXISTS `pay_wallet_transaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `wallet_id` BIGINT NOT NULL COMMENT '钱包 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `biz_type` TINYINT NOT NULL COMMENT '业务类型',
    `biz_order_no` VARCHAR(32) COMMENT '业务订单号',
    `amount` INT NOT NULL COMMENT '变动金额 (+/-)',
    `balance_after` INT NOT NULL COMMENT '变动后余额',
    `description` VARCHAR(255) COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_wallet_id` (`wallet_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分流水表';

-- 4. AI 生成记录表
CREATE TABLE IF NOT EXISTS `ai_generation_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `generation_type` TINYINT NOT NULL COMMENT '生成类型',
    `sub_type` TINYINT COMMENT '子类型',
    `input_text` TEXT COMMENT '输入文本',
    `input_image_url` VARCHAR(512) COMMENT '输入图片 URL',
    `output_url` VARCHAR(512) COMMENT '输出结果 URL',
    `output_thumbnail` VARCHAR(512) COMMENT '缩略图 URL',
    `cost_points` INT NOT NULL COMMENT '消耗积分',
    `status` TINYINT DEFAULT 0 COMMENT '状态',
    `error_message` VARCHAR(512) COMMENT '错误信息',
    `duration_ms` INT COMMENT '耗时',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 生成记录表';
```

---

## 四、开发顺序建议

```
Week 1-2: 数据库 + 后端基础
├── 创建数据库表
├── 实现钱包 Service
├── 实现分成 Service
└── 测试分成逻辑

Week 3-4: AI 功能对接
├── 对接视频生成 API
├── 实现积分扣减
├── 生成历史记录
└── 端到端测试

Week 5: 支付对接
├── 微信支付接入
├── 支付宝接入
├── 回调测试
└── 分成自动结算测试

Week 6-7: 前端开发
├── 首页
├── AI 生图/视频页
├── 代理中心
├── 充值页面
└── 联调测试
```

---

## 五、下一步行动

请确认以下问题，我将开始具体实现：

1. **数据库**：是否需要我生成完整的 SQL 迁移脚本？
2. **AI 服务**：优先对接哪家视频生成 API？（硅基流动/即梦/可灵）
3. **支付**：已有微信支付/支付宝商户号吗？还是需要 mock 测试？
4. **前端**：希望从哪个页面开始开发？
