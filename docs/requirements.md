# Shuang-Pro 需求文档

## 一、项目概述

### 1.1 产品定位

基于 AI 视频/图片生成工具的代理分销系统，通过代理体系快速获客，实现"推广 - 充值 - 生成"的业务闭环。

### 1.2 核心业务流程

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  用户注册   │ →  │ 代理分配积分 │ →  │ AI 内容生成   │ →  │ 继续充值   │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
                           ↑
                    ┌──────┴──────┐
                    │  代理获得分成 │
                    └─────────────┘
```

---

## 二、用户角色

| 角色 | 说明 | 核心权益 |
|------|------|----------|
| **平台** | 系统运营方 | 控制分成比例、审核代理资质 |
| **一级代理** | 付费购买或推广≥100 人 | 下级充值的 20% 积分分成，可分配积分给用户 |
| **二级代理** | 一级代理的下级 | 下级充值的 8% 积分分成，可分配积分给用户 |
| **普通用户** | 终端使用者 | 使用积分生成 AI 视频/图片 |

---

## 三、核心功能

### 3.1 代理管理模块

#### 3.1.1 代理等级

| 等级 | 获取条件 | 权益 |
|------|----------|------|
| 一级代理 | ①付费购买 OR ②直推≥100 人 | 20% 分成 + 积分分配权 |
| 二级代理 | 一级代理邀请的下级 | 8% 分成 + 积分分配权 |

#### 3.1.2 代理配置（可后台调整）

```yaml
agency:
  level1:
    fee: 99900          # 代理费 999 元（单位：分）
    min_direct_invite: 100  # 或直推 100 人
    commission_rate: 2000 # 分成比例 20%（单位：万分比）
  level2:
    commission_rate: 800  # 分成比例 8%
```

#### 3.1.3 积分分配

- 代理可将自己的积分分配给用户
- 用户只能从自己的上级代理获取积分
- 分配记录永久保存，可追溯

---

### 3.2 积分系统

#### 3.2.1 积分获取

| 来源 | 说明 |
|------|------|
| 代理分配 | 从上级代理处获得 |
| 充值赠送 | 充值时按比例赠送（可配置） |
| 推广分成 | 下级充值时获得分成积分 |

#### 3.2.2 积分消耗

| 功能 | 消耗积分 |
|------|----------|
| AI 视频生成 | 10 积分/次 |
| AI 图片生成 | 2 积分/次 |

#### 3.2.3 积分规则

- ❌ 不可提现
- ❌ 不可转账给用户
- ❌ 不可退款
- ✅ 永久有效（可配置过期）

---

### 3.3 AI 生成功能

#### 3.3.1 支持的生成类型

| 类型 | 说明 | 消耗 |
|------|------|------|
| 爆款视频 | 短视频内容生成 | 10 积分 |
| 网点图片 | 营销图片生成 | 2 积分 |
| 对话聊天 | AI 智能对话 | 1 积分/次（可选） |

#### 3.3.2 生成限制

- 根据用户积分余额限制
- 可配置每日生成次数上限
- 支持生成历史查看

---

### 3.4 充值系统

#### 3.4.1 充值流程

```
用户充值 → 扣除实际金额 → 一级代理获得分成积分 → 二级代理获得分成积分 → 用户获得充值积分
```

#### 3.4.2 充值套餐（示例）

| 套餐 | 金额 | 赠送积分 |
|------|------|----------|
| 基础版 | ¥30 | 30 积分 |
| 标准版 | ¥99 | 100 积分 |
| 专业版 | ¥199 | 220 积分 |
| 旗舰版 | ¥399 | 500 积分 |

---

## 四、数据库设计

### 4.1 核心表结构

#### 代理用户表 (trade_brokerage_user)

```sql
id                  bigint      主键
user_id             bigint      用户 ID
brokerage_user_id   bigint      上级分销用户 ID
bind_mode           tinyint     绑定方式
brokerage_enabled   bit         是否代理
level               tinyint     代理等级 (1:一级，2:二级)
total_commission    int         累计佣金（积分）
frozen_commission   int         冻结佣金
withdrew_commission int         已提现佣金
create_time         datetime    创建时间
update_time         datetime    更新时间
deleted             bit         逻辑删除
```

#### 佣金记录表 (trade_brokerage_record)

```sql
id                  bigint      主键
user_id             bigint      用户 ID
brokerage_user_id   bigint      分销用户 ID
biz_type            tinyint     业务类型
order_id            bigint      订单 ID
amount              int         金额（积分）
status              tinyint     状态
settle_time         datetime    结算时间
create_time         datetime    创建时间
```

#### 积分钱包表 (pay_wallet)

```sql
id                  bigint      主键
user_id             bigint      用户 ID
balance             int         余额（积分）
frozen_balance      int         冻结余额
total_recharge      int         累计充值
total_used          int         累计使用
create_time         datetime    创建时间
update_time         datetime    更新时间
```

#### 积分流水表 (pay_wallet_transaction)

```sql
id                  bigint      主键
wallet_id           bigint      钱包 ID
user_id             bigint      用户 ID
biz_type            tinyint     业务类型
amount              int         金额
balance             int         变动后余额
description         varchar     描述
create_time         datetime    创建时间
```

#### 代理配置表 (agency_config) - 新增

```sql
id                  bigint      主键
config_key          varchar     配置键
config_value        varchar     配置值
description         varchar     描述
create_time         datetime    创建时间
update_time         datetime    更新时间
```

#### 积分分配记录表 (agency_point_transfer) - 新增

```sql
id                  bigint      主键
from_user_id        bigint      分配方用户 ID
to_user_id          bigint      接收方用户 ID
point_amount        int         积分数量
order_id            bigint      关联订单 ID
description         varchar     描述
create_time         datetime    创建时间
```

---

## 五、API 接口设计

### 5.1 代理管理接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/app/agency/user/bind` | POST | 绑定上级代理 |
| `/app/agency/user/my` | GET | 获取我的代理信息 |
| `/app/agency/user/children` | GET | 获取下级列表 |
| `/app/agency/point/transfer` | POST | 分配积分给用户 |
| `/app/agency/point/records` | GET | 分配记录列表 |
| `/admin/agency/config/get` | GET | 获取代理配置 |
| `/admin/agency/config/save` | POST | 保存代理配置 |
| `/admin/agency/user/list` | GET | 代理用户列表 |

### 5.2 积分接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/app/point/wallet` | GET | 获取积分钱包 |
| `/app/point/records` | GET | 积分流水列表 |
| `/app/point/recharge` | POST | 充值积分 |

### 5.3 AI 生成接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/app/ai/image/generate` | POST | 生成图片 |
| `/app/ai/video/generate` | POST | 生成视频 |
| `/app/ai/history/list` | GET | 生成历史列表 |

---

## 六、业务规则

### 6.1 分成计算

```
一级代理分成 = 充值金额 × 20%
二级代理分成 = 充值金额 × 8%
平台收入 = 充值金额 - 一级分成 - 二级分成
```

### 6.2 代理升级条件

```
条件 1: 支付代理费 999 元
条件 2: 直推人数 ≥ 100 人
满足任一条件即可成为一级代理
```

### 6.3 积分分配限制

```
- 代理只能分配自己的积分
- 用户只能从上级代理接收积分
- 分配后积分立即到账
- 分配记录不可删除
```

---

## 七、开发计划

### Phase 1 - 基础架构 (当前)
- [x] 项目分析
- [x] 需求梳理
- [ ] 项目重命名
- [ ] 删除无关模块
- [ ] 创建需求文档

### Phase 2 - 核心功能
- [ ] 代理管理模块开发
- [ ] 积分分配功能
- [ ] 分成结算逻辑

### Phase 3 - AI 集成
- [ ] AI 视频生成对接
- [ ] AI 图片生成对接
- [ ] 积分扣减逻辑

### Phase 4 - 完善优化
- [ ] 数据统计
- [ ] 性能优化
- [ ] 安全加固

---

## 八、变更记录

| 日期 | 版本 | 变更内容 | 操作人 |
|------|------|----------|--------|
| 2026-03-28 | v1.0 | 初始版本 | - |
