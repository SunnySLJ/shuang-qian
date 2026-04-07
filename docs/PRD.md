# Shuang-Pro 产品需求文档 (PRD)

## 一、产品概述

### 1.1 产品定位

Shuang-Pro 是一个 AI 驱动的视频/图片内容生成平台，通过代理分销体系进行推广。平台提供专业级的 AI 生图、生视频能力，结合"灵感案例库"和"爆款拆解"功能，帮助用户快速创作高质量内容。

### 1.2 核心价值主张

| 价值维度 | 说明 |
|---------|------|
| **降低创作门槛** | 一句话即可生成专业级视频/图片，无需设计技能 |
| **爆款可复制** | 一键拆解热门视频，获取爆款提示词和元素 |
| **行业定制化** | 21 个行业分类的灵感案例，开箱即用 |
| **代理分销裂变** | 二级代理体系，快速获客，积分锁定用户 |

### 1.3 参考竞品分析 (timarsky.com)

**核心功能对标：**
```
┌─────────────────────────────────────────────────────────────┐
│  功能模块          │  参考网站名称        │  我方命名       │
├─────────────────────────────────────────────────────────────┤
│  图片生成          │  生图/改图           │  AI 生图        │
│  视频生成          │  一句话视频创作      │  AI 视频        │
│  图生视频          │  任意图生视频        │  图生视频       │
│  6 秒快剪          │  威尔黄金 6 秒         │  黄金 6 秒       │
│  多视频混剪        │  AI 超级混剪         │  AI 混剪        │
│  视频拆解          │  一键拆解视频        │  爆款拆解       │
└─────────────────────────────────────────────────────────────┘
```

---

## 二、用户角色与权限体系

### 2.1 用户角色定义

```
┌─────────────────────────────────────────────────────────────────────┐
│                           用户角色金字塔                             │
│                                                                      │
│                          ┌─────────┐                                │
│                          │  平台   │  控制分成比例、审核代理          │
│                          └────┬────┘                                │
│                               │                                     │
│                    ┌──────────┴──────────┐                         │
│                    │    一级代理 (20%)    │  付费 999 元或直推≥100 人   │
│                    └──────────┬──────────┘                         │
│                               │                                     │
│              ┌────────────────┼────────────────┐                   │
│              │                │                │                   │
│       ┌──────┴──────┐  ┌──────┴──────┐  ┌──────┴──────┐           │
│       │  二级代理    │  │  二级代理    │  │  二级代理    │  8% 分成   │
│       │   (8%)      │  │   (8%)      │  │   (8%)      │           │
│       └──────┬──────┘  └──────┬──────┘  └──────┬──────┘           │
│              │                │                │                   │
│       ┌──────┴──────┐  ┌──────┴──────┐  ┌──────┴──────┐           │
│       │  普通用户    │  │  普通用户    │  │  普通用户    │  消费积分  │
│       └─────────────┘  └─────────────┘  └─────────────┘           │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 权限矩阵

| 权限项 | 平台 | 一级代理 | 二级代理 | 普通用户 |
|--------|------|----------|----------|----------|
| 充值积分 | ✅ | ✅ | ✅ | ❌ |
| 分配积分 | ✅ | ✅ | ✅ | ❌ |
| 接收积分 | ✅ | ✅ | ✅ | ✅ |
| AI 生图 | ✅ | ✅ | ✅ | ✅ |
| AI 视频 | ✅ | ✅ | ✅ | ✅ |
| 爆款拆解 | ✅ | ✅ | ✅ | ✅ |
| AI 混剪 | ✅ | ✅ | ✅ | ✅ |
| 发展下级 | - | ✅ | ✅ | ❌ |
| 获得分成 | - | 20% | 8% | - |
| 代理配置 | ✅ | ❌ | ❌ | ❌ |

---

## 三、产品功能清单

### 3.1 功能模块总览

```
Shuang-Pro
├── 用户系统
│   ├── 注册/登录
│   ├── 个人中心
│   └── 积分钱包
├── AI 生成系统
│   ├── AI 生图 (文生图、图生图)
│   ├── AI 视频 (文生视频、图生视频)
│   ├── 黄金 6 秒 (多片段拼接)
│   ├── AI 混剪 (多视频重组)
│   └── 爆款拆解 (视频分析)
├── 灵感系统
│   ├── 行业案例库 (21 个分类)
│   ├── 教程视频
│   └── 搜索功能
├── 代理系统
│   ├── 代理等级管理
│   ├── 积分分配
│   ├── 分成结算
│   └── 下级管理
├── 充值系统
│   ├── 充值套餐
│   ├── 支付集成
│   └── 分成自动结算
└── 资产系统
    ├── 生成历史
    ├── 素材库
    └── 下载管理
```

### 3.2 详细功能说明

#### 3.2.1 AI 生图功能

| 子功能 | 说明 | 消耗积分 |
|--------|------|----------|
| 文生图 | 输入文字描述生成图片 | 20 积分/次 |
| 图生图 | 基于参考图生成新图片 | 20 积分/次 |
| 专业模式 | 高清大图、细节控制 | 50 积分/次 |

#### 3.2.2 AI 视频功能

| 子功能 | 说明 | 消耗积分 |
|--------|------|----------|
| 文生视频 | 输入文字生成短视频 | 50 积分/次 |
| 图生视频 | 任意图片转视频 | 50 积分/次 |
| 黄金 6 秒 | 6 秒快剪拼接 | 80 积分/次 |
| AI 混剪 | 多视频一键成片 | 100 积分/次 |

#### 3.2.3 爆款拆解功能

| 功能 | 说明 | 消耗积分 |
|------|------|----------|
| 视频上传 | 上传待拆解视频 | - |
| 文案提取 | 提取视频台词/字幕 | 5 积分/次 |
| 元素分析 | 识别画面元素、运镜方式 | 5 积分/次 |
| 提示词生成 | 生成同款创作提示词 | 5 积分/次 |

#### 3.2.4 灵感案例库

**21 个行业分类：**
1. 电商  2. 大健康  3. 工厂  4. 励志演讲  5. 探店
6. 宠物  7. 美发  8. 短剧带货  9. 变装  10. 写实
11. 动漫  12. 创意  13. 跨境  14. 设计  15. 家具设计
16. LOGO  17. 服饰  18. 美食  19. 搞笑  20. 修图改图  21. AI 数字人

---

## 四、代理分销机制

### 4.1 代理等级规则

```yaml
# 代理配置 (可后台调整)
agency:
  level1:
    # 升级条件 (满足任一)
    upgrade_conditions:
      - type: fee
        amount: 99900  # 999 元 (单位：分)
      - type: direct_invite_count
        min_count: 100
    # 权益
    benefits:
      commission_rate: 2000  # 20% 分成 (单位：万分比)
      can_allocate_points: true
      can_invite_sub_agency: true
      
  level2:
    # 升级条件
    upgrade_conditions:
      - type: invited_by_level1
    # 权益
    benefits:
      commission_rate: 800  # 8% 分成
      can_allocate_points: true
      can_invite_sub_agency: true
```

### 4.2 分成计算逻辑

```
┌─────────────────────────────────────────────────────────────────┐
│                      充值分成计算示例                            │
│                                                                  │
│  用户充值：¥100                                                  │
│                                                                  │
│  分配顺序：                                                       │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ 1. 一级代理分成 = 100 × 20% = ¥20 (积分形式)            │     │
│  │ 2. 二级代理分成 = 100 × 8% = ¥8 (积分形式)              │     │
│  │ 3. 平台收入 = 100 - 20 - 8 = ¥72                        │     │
│  └────────────────────────────────────────────────────────┘     │
│                                                                  │
│  积分换算：1 元 = 1 积分 (可配置)                                   │
└─────────────────────────────────────────────────────────────────┘
```

### 4.3 积分分配规则

| 规则项 | 说明 |
|--------|------|
| 分配方向 | 代理 → 下级用户 (不可越级) |
| 分配限制 | 只能分配自己钱包内的可用积分 |
| 到账方式 | 实时到账，不可撤销 |
| 流水记录 | 永久保存，可追溯审计 |

---

## 五、积分系统

### 5.5 积分获取方式

| 方式 | 说明 | 备注 |
|------|------|------|
| 代理分配 | 上级代理直接分配 | 主要来源 |
| 充值赠送 | 充值时按比例赠送 | 可配置赠送比例 |
| 推广分成 | 下级充值时获得分成 | 仅限代理 |

### 5.6 积分规则

- ❌ 不可提现
- ❌ 不可退款
- ❌ 不可转账给平级用户
- ✅ 永久有效 (可配置过期时间)
- ✅ 可查询流水明细

### 5.2 积分消耗定价

```yaml
# 积分定价配置
pricing:
  ai_image:
    standard: 20       # 标准生成 (至少 20 积分)
    premium: 50        # 专业高清
  ai_video:
    text_to_video: 50  # 文生视频 (至少 20 积分)
    image_to_video: 50 # 图生视频 (至少 20 积分)
    golden_6s: 80      # 黄金 6 秒
    ai_mix: 100        # AI 混剪
  video_analysis:
    extract_script: 20    # 文案提取
    analyze_elements: 20  # 元素分析
    generate_prompt: 20   # 提示词生成
```

### 5.3 积分规则

- ❌ 不可提现
- ❌ 不可退款
- ❌ 不可转账给平级用户
- ✅ 永久有效 (可配置过期时间)
- ✅ 可查询流水明细

---

## 五、充值系统

### 5.1 充值渠道

**重要：仅代理可充值，普通用户无充值入口**

| 渠道 | 说明 | 接入方式 |
|------|------|----------|
| **微信支付** | 微信扫码/小程序支付 | 微信支付 API v3 |
| **支付宝** | 支付宝扫码/H5 支付 | 支付宝开放平台 API |

### 5.2 充值套餐

| 套餐 | 金额 | 获得积分 | 适用人群 |
|------|------|----------|----------|
| 体验包 | ¥30 | 30 积分 | 新代理试用 |
| 基础包 | ¥99 | 100 积分 | 个人创作者 |
| 专业包 | ¥199 | 220 积分 | 小团队 |
| 企业包 | ¥399 | 500 积分 | 企业用户 |
| 定制包 | ¥999 | 1200 积分 | 一级代理专享 |

### 5.3 充值分成结算

```
充值流程：
1. 用户选择充值金额 → 2. 选择支付方式 (微信/支付宝)
3. 完成支付 → 4. 系统自动结算分成

分成示例 (用户充值 ¥100):
┌────────────────────────────────────────────────────────┐
│  充值金额：¥100                                         │
│  ↓                                                     │
│  一级代理分成 = ¥100 × 20% = ¥20 (积分形式即时到账)     │
│  二级代理分成 = ¥100 × 8% = ¥8 (积分形式即时到账)       │
│  平台收入 = ¥100 - ¥20 - ¥8 = ¥72                       │
└────────────────────────────────────────────────────────┘
```

### 5.4 支付回调处理

```yaml
# 支付回调处理逻辑
payment_callback:
  - verify_signature: true      # 验证签名
  - update_order_status: paid   # 更新订单状态
  - add_wallet_balance: true    # 增加钱包余额
  - calculate_commission: true  # 计算分成
  - create_transaction: true    # 创建流水记录
```

---

## 六、数据库设计

### 6.1 核心表结构

#### 6.1.1 代理用户表 (agency_user)

```sql
CREATE TABLE agency_user (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id             BIGINT NOT NULL COMMENT '关联用户 ID',
    brokerage_user_id   BIGINT COMMENT '上级分销用户 ID',
    bind_mode           TINYINT COMMENT '绑定方式：1-邀请码，2-自动绑定',
    brokerage_enabled   BIT DEFAULT 0 COMMENT '是否启用代理',
    level               TINYINT COMMENT '代理等级：1-一级，2-二级',
    total_commission    INT DEFAULT 0 COMMENT '累计佣金 (积分)',
    frozen_commission   INT DEFAULT 0 COMMENT '冻结佣金',
    withdrawn_commission INT DEFAULT 0 COMMENT '已提现佣金',
    direct_invite_count INT DEFAULT 0 COMMENT '直推人数',
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             BIT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_brokerage_user_id (brokerage_user_id),
    INDEX idx_level (level)
) COMMENT '代理用户表';
```

#### 6.1.2 佣金记录表 (agency_commission_record)

```sql
CREATE TABLE agency_commission_record (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id             BIGINT NOT NULL COMMENT '被分成用户 ID',
    brokerage_user_id   BIGINT NOT NULL COMMENT '分成获得用户 ID',
    biz_type            TINYINT NOT NULL COMMENT '业务类型：1-充值分成，2-消费分成',
    order_id            BIGINT COMMENT '关联订单 ID',
    amount              INT NOT NULL COMMENT '分成金额 (积分)',
    commission_rate     INT NOT NULL COMMENT '分成比例 (万分比)',
    status              TINYINT DEFAULT 0 COMMENT '状态：0-待结算，1-已结算，2-已取消',
    settle_time         DATETIME COMMENT '结算时间',
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_brokerage_user_id (brokerage_user_id),
    INDEX idx_order_id (order_id)
) COMMENT '佣金记录表';
```

#### 6.1.3 积分钱包表 (pay_wallet)

```sql
CREATE TABLE pay_wallet (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id             BIGINT NOT NULL UNIQUE COMMENT '用户 ID',
    balance             INT DEFAULT 0 COMMENT '可用余额 (积分)',
    frozen_balance      INT DEFAULT 0 COMMENT '冻结余额',
    total_recharge      INT DEFAULT 0 COMMENT '累计充值',
    total_used          INT DEFAULT 0 COMMENT '累计消耗',
    total_received      INT DEFAULT 0 COMMENT '累计收到分配',
    total_given         INT DEFAULT 0 COMMENT '累计分配给出',
    expire_time         DATETIME COMMENT '过期时间 (NULL=永久)',
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) COMMENT '积分钱包表';
```

#### 6.1.4 积分流水表 (pay_wallet_transaction)

```sql
CREATE TABLE pay_wallet_transaction (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    wallet_id           BIGINT NOT NULL COMMENT '钱包 ID',
    user_id             BIGINT NOT NULL COMMENT '用户 ID',
    biz_type            TINYINT NOT NULL COMMENT '业务类型',
    biz_order_no        VARCHAR(32) COMMENT '业务订单号',
    amount              INT NOT NULL COMMENT '变动金额 (+/-)',
    balance_after       INT NOT NULL COMMENT '变动后余额',
    description         VARCHAR(255) COMMENT '描述',
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_wallet_id (wallet_id),
    INDEX idx_user_id (user_id),
    INDEX idx_biz_order_no (biz_order_no)
) COMMENT '积分流水表';
```

#### 6.1.5 积分分配记录表 (agency_point_transfer)

```sql
CREATE TABLE agency_point_transfer (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    from_user_id        BIGINT NOT NULL COMMENT '分配方用户 ID',
    to_user_id          BIGINT NOT NULL COMMENT '接收方用户 ID',
    point_amount        INT NOT NULL COMMENT '积分数量',
    order_id            BIGINT COMMENT '关联订单 ID',
    description         VARCHAR(255) COMMENT '分配说明',
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_from_user_id (from_user_id),
    INDEX idx_to_user_id (to_user_id)
) COMMENT '积分分配记录表';
```

#### 6.1.6 AI 生成记录表 (ai_generation_record)

```sql
CREATE TABLE ai_generation_record (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id             BIGINT NOT NULL COMMENT '用户 ID',
    generation_type     TINYINT NOT NULL COMMENT '生成类型：1-生图，2-生视频，3-混剪，4-拆解',
    sub_type            TINYINT COMMENT '子类型',
    input_text          TEXT COMMENT '输入文本',
    input_image_url     VARCHAR(512) COMMENT '输入图片 URL',
    output_url          VARCHAR(512) COMMENT '输出结果 URL',
    output_thumbnail    VARCHAR(512) COMMENT '缩略图 URL',
    cost_points         INT NOT NULL COMMENT '消耗积分',
    status              TINYINT DEFAULT 0 COMMENT '状态：0-处理中，1-完成，2-失败',
    error_message       VARCHAR(512) COMMENT '错误信息',
    duration_ms         INT COMMENT '耗时 (ms)',
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) COMMENT 'AI 生成记录表';
```

#### 6.1.7 灵感案例表 (ai_inspiration_case)

```sql
CREATE TABLE ai_inspiration_case (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    category            VARCHAR(32) NOT NULL COMMENT '行业分类',
    title               VARCHAR(128) NOT NULL COMMENT '案例标题',
    description         VARCHAR(512) COMMENT '案例描述',
    cover_image_url     VARCHAR(512) COMMENT '封面图 URL',
    video_url           VARCHAR(512) COMMENT '演示视频 URL',
    prompt_template     TEXT COMMENT '提示词模板',
    view_count          INT DEFAULT 0 COMMENT '浏览次数',
    use_count           INT DEFAULT 0 COMMENT '使用次数',
    is_featured         BIT DEFAULT 0 COMMENT '是否精选',
    sort_order          INT DEFAULT 0 COMMENT '排序',
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_featured (is_featured)
) COMMENT '灵感案例表';
```

#### 6.1.8 代理配置表 (agency_config)

```sql
CREATE TABLE agency_config (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    config_key          VARCHAR(64) NOT NULL UNIQUE COMMENT '配置键',
    config_value        VARCHAR(512) NOT NULL COMMENT '配置值',
    config_type         TINYINT DEFAULT 1 COMMENT '类型：1-数字，2-字符串，3-JSON',
    description         VARCHAR(255) COMMENT '描述',
    update_time         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_key (config_key)
) COMMENT '代理配置表';

-- 初始化配置数据
INSERT INTO agency_config (config_key, config_value, config_type, description) VALUES
('level1_fee', '99900', 1, '一级代理费用 (分)'),
('level1_min_direct_invite', '100', 1, '一级代理最少直推人数'),
('level1_commission_rate', '2000', 1, '一级代理分成比例 (万分比)'),
('level2_commission_rate', '800', 1, '二级代理分成比例 (万分比)'),
('points_per_yuan', '1', 1, '1 元兑换积分');
```

### 6.2 数据关系图

```
┌─────────────────────────────────────────────────────────────────┐
│                        数据库 ER 关系图                           │
└─────────────────────────────────────────────────────────────────┘

user (系统用户表)
  │
  ├─→ agency_user (1:0..1 代理信息)
  │       │
  │       └─→ agency_user (n:1 上下级关系)
  │
  ├─→ pay_wallet (1:1 钱包)
  │       │
  │       └─→ pay_wallet_transaction (1:n 流水)
  │
  ├─→ agency_point_transfer (1:n 作为分配方)
  ├─→ agency_point_transfer (1:n 作为接收方)
  │
  └─→ ai_generation_record (1:n 生成记录)
```

---

## 七、API 接口设计

### 7.1 代理管理接口

| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/app/agency/user/bind` | POST | 用户 | 绑定上级代理 |
| `/app/agency/user/my` | GET | 用户 | 获取我的代理信息 |
| `/app/agency/user/children` | GET | 代理 | 获取下级列表 |
| `/app/agency/user/upgrade` | POST | 用户 | 申请升级一级代理 |
| `/app/agency/point/transfer` | POST | 代理 | 分配积分给用户 |
| `/app/agency/point/records` | GET | 代理 | 分配记录列表 |
| `/admin/agency/config/get` | GET | 平台 | 获取代理配置 |
| `/admin/agency/config/save` | POST | 平台 | 保存代理配置 |
| `/admin/agency/user/list` | GET | 平台 | 代理用户列表 |

### 7.2 积分接口

| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/app/point/wallet` | GET | 用户 | 获取积分钱包 |
| `/app/point/records` | GET | 用户 | 积分流水列表 |
| `/app/point/recharge` | POST | 平台/代理 | 充值积分 |

### 7.3 AI 生成接口

| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/app/ai/image/generate` | POST | 用户 | 生成图片 |
| `/app/ai/video/generate` | POST | 用户 | 生成视频 |
| `/app/ai/video/mix` | POST | 用户 | AI 混剪 |
| `/app/ai/video/golden6s` | POST | 用户 | 黄金 6 秒 |
| `/app/ai/video/analyze` | POST | 用户 | 爆款拆解 |
| `/app/ai/history/list` | GET | 用户 | 生成历史列表 |
| `/app/ai/history/{id}` | GET | 用户 | 生成详情 |

### 7.4 灵感接口

| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/app/inspiration/categories` | GET | 公开 | 获取行业分类 |
| `/app/inspiration/cases` | GET | 公开 | 案例列表 (支持筛选) |
| `/app/inspiration/cases/{id}` | GET | 公开 | 案例详情 |
| `/app/inspiration/cases/{id}/use` | POST | 用户 | 使用案例模板 |
| `/admin/inspiration/cases` | POST | 平台 | 新增案例 |

### 7.5 请求/响应示例

#### 代理分配积分

```http
POST /app/agency/point/transfer
Content-Type: application/json
Authorization: Bearer {token}

{
  "toUserId": 12345,
  "pointAmount": 100,
  "description": "新用户奖励"
}

Response:
{
  "code": 0,
  "msg": "success",
  "data": {
    "transferId": 67890,
    "fromUserId": 11111,
    "toUserId": 12345,
    "pointAmount": 100,
    "createTime": "2026-04-02 12:00:00"
  }
}
```

#### AI 生图

```http
POST /app/ai/image/generate
Content-Type: application/json
Authorization: Bearer {token}

{
  "prompt": "一只可爱的橘猫在阳光下睡觉",
  "style": "写实",
  "size": "1024x1024"
}

Response:
{
  "code": 0,
  "msg": "success",
  "data": {
    "generationId": "gen_abc123",
    "status": 0,
    "costPoints": 2,
    "estimatedTime": 30
  }
}
```

---

## 八、开发计划

### 8.1 迭代规划

```
┌─────────────────────────────────────────────────────────────────┐
│                        开发迭代时间表                            │
├──────────┬────────────┬─────────────────────────────────────────┤
│  迭代    │   周期     │              主要内容                   │
├──────────┼────────────┼─────────────────────────────────────────┤
│ Phase 1  │  第 1-2 周   │  基础架构、代理模块、积分系统           │
│ Phase 2  │  第 3-4 周   │  AI 生图、AI 视频生成对接               │
│ Phase 3  │  第 5 周    │  灵感案例库、资产系统                   │
│ Phase 4  │  第 6 周    │  爆款拆解、AI 混剪功能                  │
│ Phase 5  │  第 7 周    │  测试、优化、上线准备                   │
└──────────┴────────────┴─────────────────────────────────────────┘
```

### 8.2 Phase 1 - 基础架构 (第 1-2 周)

#### Sprint 1.1 - 项目初始化

- [ ] 清理无关模块
- [ ] 确认技术栈版本
- [ ] 初始化数据库
- [ ] 搭建开发环境

#### Sprint 1.2 - 代理管理模块

| 任务 | 优先级 | 估时 |
|------|--------|------|
| 代理用户表设计与迁移 | P0 | 4h |
| 代理绑定接口 | P0 | 4h |
| 上下级关系查询 | P0 | 4h |
| 代理升级逻辑 | P1 | 8h |
| 代理配置管理 | P1 | 4h |

#### Sprint 1.3 - 积分系统

| 任务 | 优先级 | 估时 |
|------|--------|------|
| 钱包表设计与迁移 | P0 | 4h |
| 积分流水表设计与迁移 | P0 | 4h |
| 积分分配接口 | P0 | 8h |
| 分成结算逻辑 | P0 | 8h |
| 钱包查询接口 | P1 | 4h |

### 8.3 Phase 2 - AI 生成功能 (第 3-4 周)

#### Sprint 2.1 - AI 生图

| 任务 | 优先级 | 估时 |
|------|--------|------|
| 对接 AI 生图 API | P0 | 8h |
| 生图任务队列 | P0 | 8h |
| 积分扣减逻辑 | P0 | 4h |
| 生成历史记录 | P1 | 4h |

#### Sprint 2.2 - AI 视频

| 任务 | 优先级 | 估时 |
|------|--------|------|
| 对接 AI 视频 API | P0 | 8h |
| 视频生成状态轮询 | P0 | 8h |
| 图生视频功能 | P1 | 8h |
| 生成结果存储 | P1 | 4h |

### 8.4 Phase 3 - 灵感与资产 (第 5 周)

| 模块 | 任务 | 优先级 | 估时 |
|------|------|--------|------|
| 灵感库 | 案例表设计 | P0 | 4h |
| 灵感库 | 分类管理接口 | P0 | 4h |
| 灵感库 | 案例 CRUD | P1 | 8h |
| 资产 | 用户资产表 | P0 | 4h |
| 资产 | 下载管理 | P1 | 4h |

### 8.5 Phase 4 - 高级功能 (第 6 周)

| 功能 | 任务 | 优先级 | 估时 |
|------|------|--------|------|
| 黄金 6 秒 | 片段拼接逻辑 | P1 | 8h |
| AI 混剪 | 多视频处理 | P1 | 16h |
| 爆款拆解 | 视频分析对接 | P1 | 16h |
| 爆款拆解 | 提示词生成 | P2 | 8h |

### 8.6 Phase 5 - 测试上线 (第 7 周)

| 任务 | 负责人 | 估时 |
|------|--------|------|
| 单元测试补齐 | 开发 | 8h |
| 集成测试 | 测试 | 16h |
| 性能压测 | 开发 | 8h |
| 安全审计 | 开发 | 8h |
| 部署文档 | 运维 | 4h |
| 上线检查清单 | 全员 | 4h |

---

## 九、技术架构

### 9.1 系统架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                         系统架构总览                            │
└─────────────────────────────────────────────────────────────────┘

                    ┌──────────────────┐
                    │   前端 (Vue3)     │
                    │  yudao-ui-admin  │
                    └────────┬─────────┘
                             │ HTTP/HTTPS
                    ┌────────▼─────────┐
                    │   API Gateway    │
                    │   (Spring Boot)  │
                    └────────┬─────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼────────┐  ┌───────▼────────┐  ┌───────▼────────┐
│  代理模块      │  │  积分模块      │  │  AI 生成模块     │
│  agency        │  │  pay/wallet    │  │  ai            │
└────────────────┘  └────────────────┘  └────────────────┘
        │                    │                    │
        └────────────────────┼────────────────────┘
                             │
                    ┌────────▼─────────┐
                    │   MyBatis Plus   │
                    └────────┬─────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼────────┐  ┌───────▼─────────┐  ┌───────▼────────┐
│    MySQL       │  │    Redis        │  │  外部 AI API    │
│   (主数据)     │  │   (缓存/会话)   │  │  (生图/视频)   │
└────────────────┘  └─────────────────┘  └────────────────┘
```

### 9.2 技术选型

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 后端框架 | Spring Boot | 3.5.9 | 主框架 |
| ORM | MyBatis Plus | 3.5.15 | 数据访问 |
| 数据库 | MySQL | 8.0+ | 主数据存储 |
| 缓存 | Redis | 6.0+ | 会话/热点数据 |
| JDK | OpenJDK | 17 | 运行环境 |
| 构建 | Maven | 3.6+ | 项目管理 |
| 前端 | Vue 3 + Element Plus | Latest | 管理后台 |

---

## 十、风险与应对

### 10.1 技术风险

| 风险 | 可能性 | 影响 | 应对措施 |
|------|--------|------|----------|
| AI API 不稳定 | 中 | 高 | 多服务商冗余、失败重试、队列缓冲 |
| 高并发积分扣减 | 低 | 高 | 分布式锁、乐观锁、幂等设计 |
| 视频存储成本高 | 中 | 中 | CDN 加速、冷热分离、压缩优化 |

### 10.2 业务风险

| 风险 | 可能性 | 影响 | 应对措施 |
|------|--------|------|----------|
| 代理体系被滥用 | 中 | 高 | 风控规则、人工审核、异常检测 |
| 生成内容违规 | 中 | 高 | 内容审核 API、关键词过滤、举报机制 |
| 积分通胀 | 低 | 中 | 动态调整分成比例、积分过期机制 |

---

## 十一、变更记录

| 日期 | 版本 | 变更内容 | 操作人 |
|------|------|----------|--------|
| 2026-04-02 | v2.0 | 基于参考网站完善功能清单 | - |
| 2026-03-28 | v1.0 | 初始版本 | - |

---

## 附录 A：积分业务类型枚举

```java
public class WalletBizTypeEnum {
    
    // 收入类
    public static final int RECHARGE = 1;           // 充值
    public static final int AGENCY_ALLOCATE = 2;    // 代理分配
    public static final int COMMISSION = 3;         // 分成收入
    public static final int REWARD = 4;             // 奖励
    
    // 支出类
    public static final int AI_IMAGE_GENERATE = -1;  // AI 生图
    public static final int AI_VIDEO_GENERATE = -2;  // AI 视频
    public static final int AI_MIX = -3;             // AI 混剪
    public static final int AI_ANALYZE = -4;         // 视频拆解
}
```

## 附录 B：代理等级枚举

```java
public class AgencyLevelEnum {
    
    public static final int LEVEL_1 = 1;   // 一级代理
    public static final int LEVEL_2 = 2;   // 二级代理
    
    // 分成比例 (万分比)
    public static final int LEVEL_1_RATE = 2000;  // 20%
    public static final int LEVEL_2_RATE = 800;   // 8%
}
```

## 附录 C：错误码定义

```java
public class ErrorCodeConstants {
    
    // 代理相关 (1000-1999)
    public static final int AGENCY_NOT_EXISTS = 1001;      // 代理信息不存在
    public static final int AGENCY_ALREADY_LEVEL1 = 1002;  // 已是一级代理
    public static final int AGENCY_UPGRADE_FAILED = 1003;  // 升级失败
    
    // 积分相关 (2000-2999)
    public static final int POINT_INSUFFICIENT = 2001;     // 积分不足
    public static final int POINT_TRANSFER_FAILED = 2002;  // 分配失败
    public static final int WALLET_NOT_EXISTS = 2003;      // 钱包不存在
    
    // AI 生成相关 (3000-3999)
    public static final int AI_API_ERROR = 3001;           // AI 服务错误
    public static final int AI_TIMEOUT = 3002;             // 生成超时
    public static final int AI_RATE_LIMIT = 3003;          // 频率限制
}
```
