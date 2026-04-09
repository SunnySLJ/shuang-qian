# Shuang-Pro 产品需求文档 (PRD)

> 版本：v4.0 | 更新日期：2026-04-07
> 参考：timarsky.com (星巢引擎科技)

---

## 一、产品概述

### 1.1 产品定位

Shuang-Pro 是一个 AI 驱动的视频/图片内容生成平台，通过代理分销体系进行推广。平台提供专业级的 AI 生图、生视频能力，结合"灵感案例库"、"教程视频"和"爆款拆解"功能，帮助用户快速创作高质量内容。

### 1.2 核心价值主张

| 价值维度 | 说明 |
|---------|------|
| **降低创作门槛** | 一句话即可生成专业级视频/图片，无需设计技能 |
| **爆款可复制** | 一键拆解热门视频，获取爆款提示词和元素 |
| **行业定制化** | 21 个行业分类的灵感案例，开箱即用 |
| **代理分销裂变** | 二级代理体系，快速获客，积分锁定用户 |

### 1.3 竞品功能对标 (timarsky.com)

| 功能模块 | 竞品名称 | 我方命名 | 消耗积分 |
|---------|---------|---------|:--------:|
| 图片生成 | Nano Banana 香蕉生图 | AI生图 | 4积分/次 |
| 视频生成 | 索拉 文生/图生视频 | 索拉视频 | 按时长 |
| 图生视频 | 马克 任意图片生视频 | 马克视频 | 按时长 |
| 图生视频 | 威尔 任意图片生视频 | 威尔视频 | 按时长 |
| 图生视频 | 燃动数字人 任意图片生视频 | 数字人视频 | 按时长 |
| 多视频混剪 | AI超级混剪 | AI混剪 | 按时长 |
| 视频拆解 | 一键拆解视频 | 爆款拆解 | 按功能 |

---

## 二、用户角色体系

### 2.1 角色定义

```
                    ┌─────────┐
                    │  平台   │  控制分成比例、审核代理
                    └────┬────┘
                         │
              ┌──────────┴──────────┐
              │   一级代理 (20%)    │  付费999元或直推≥100人
              └──────────┬──────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
   ┌────┴────┐     ┌────┴────┐     ┌────┴────┐
   │二级代理 │     │二级代理 │     │二级代理 │  8%分成
   │  (8%)   │     │  (8%)   │     │  (8%)   │
   └────┬────┘     └────┬────┘     └────┬────┘
        │                │                │
   ┌────┴────┐     ┌────┴────┐     ┌────┴────┐
   │普通用户 │     │普通用户 │     │普通用户 │  消费积分
   └─────────┘     └─────────┘     └─────────┘
```

### 2.2 权限矩阵

| 权限项 | 平台 | 一级代理 | 二级代理 | 普通用户 |
|--------|:----:|:--------:|:--------:|:--------:|
| 充值积分 | ✅ | ✅ | ✅ | ❌ |
| 分配积分 | ✅ | ✅ | ✅ | ❌ |
| 接收积分 | ✅ | ✅ | ✅ | ✅ |
| AI生图 | ✅ | ✅ | ✅ | ✅ |
| AI视频 | ✅ | ✅ | ✅ | ✅ |
| 爆款拆解 | ✅ | ✅ | ✅ | ✅ |
| AI混剪 | ✅ | ✅ | ✅ | ✅ |
| 发展下级 | - | ✅ | ✅ | ❌ |
| 获得分成 | - | 20% | 8% | - |
| 代理配置 | ✅ | ❌ | ❌ | ❌ |

---

## 三、功能模块清单

### 3.1 模块总览

```
Shuang-Pro
├── 用户系统
│   ├── 注册/登录（密码登录、验证码登录）
│   ├── 个人中心
│   └── 积分钱包
├── AI生成系统
│   ├── AI生图 (香蕉生图 - 文生图、图生图)
│   ├── AI视频
│   │   ├── 索拉 (文生视频、图生视频)
│   │   ├── 马克 (图生视频)
│   │   ├── 威尔 (图生视频)
│   │   └── 燃动数字人 (图生视频)
│   ├── 黄金6秒 (多片段拼接)
│   ├── AI混剪 (多视频重组)
│   └── 爆款拆解 (视频分析)
├── 灵感系统
│   ├── 灵感案例库 (21个行业分类，1093+案例)
│   ├── 教程视频 (13+教程)
│   └── 内容生成模板
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

---

## 四、灵感系统（核心功能）

### 4.1 灵感案例库

#### 4.1.1 行业分类 (21个)

| ID | 分类名称 | ID | 分类名称 | ID | 分类名称 |
|:--:|---------|:--:|---------|:--:|---------|
| 0 | 全部 | 11 | 跨境 | 22 | 搞笑 |
| 5 | 电商 | 12 | 宠物 | 23 | 美发 |
| 6 | 探店 | 13 | 短剧带货 | 24 | 修图改图 |
| 7 | 变装 | 14 | 设计 | 25 | AI数字人 |
| 8 | 写实 | 15 | 大健康 | - | - |
| 9 | 动漫 | 16 | 工厂 | - | - |
| 10 | 创意 | 17 | 励志演讲 | - | - |
| 18 | 家具设计 | 19 | LOGO | 20 | 服饰 |
| 21 | 美食 | - | - | - | - |

#### 4.1.2 案例数据结构

```json
{
  "id": 379,
  "type": "veo",           // 类型：banana(生图)/veo(威尔)/grok(马克)/seedance(索拉)
  "title": "活字印刷",
  "content": "广角镜头展现了一位神态安详的僧人...",  // 提示词
  "image": "https://cdn.fenshen123.com/...",  // 封面图
  "image_first": "https://cdn.fenshen123.com/...",  // 首帧图
  "image_tail": "",        // 尾帧图
  "video_url": "https://cdn.fenshen123.com/...",  // 视频URL
  "duration": 8,           // 视频时长(秒)
  "like_count": 322,       // 点赞数
  "label": "威尔视频",     // 标签
  "icon": "https://cdn.fenshen123.com/icons/grok.png",  // 图标
  "like_status": 0         // 点赞状态
}
```

#### 4.1.3 案例类型对应

| type值 | 模型名称 | 图标 | 说明 |
|--------|---------|------|------|
| banana | 香蕉生图 | nano_small_icon.png | 专业级图片生成 |
| veo | 威尔视频 | grok.png | 图生视频 |
| grok | 马克视频 | veo.png | 图生视频 |
| seedance | 索拉视频 | sora | 文生/图生视频 |

### 4.2 教程视频

#### 4.2.1 教程列表

| ID | 教程名称 | 说明 |
|:--:|---------|------|
| 640 | 燃动数字人 | 数字人视频生成教程 |
| 624 | 有图必应 | 图片响应教程 |
| 543 | 威尔视频 | 威尔视频使用指南 |
| 437 | 索拉使用指南 | 索拉视频生成教程 |
| 438 | 马克使用指南 | 马克视频生成教程 |
| 439 | 香蕉使用指南 | 香蕉生图使用教程 |
| 440 | 超级分身 | 分身功能教程 |
| 443 | 索拉智能包装 | 智能包装功能 |
| 450 | 索拉常见问题使用指南 | FAQ教程 |
| 460 | 一键拆解 | 爆款拆解教程 |
| 470 | 超级混剪 | AI混剪教程 |
| 477 | AI润色全球翻译 | 翻译功能教程 |
| 512 | 角色替换/动作迁移 | 角色替换教程 |

#### 4.2.2 教程数据结构

```json
{
  "id": 640,
  "type_id": 0,
  "name": "燃动数字人",
  "cover": "https://cdn.fenshen123.com/...",  // 封面图
  "video_url": "https://cdn.fenshen123.com/..."  // 视频URL
}
```

---

## 五、AI生成功能

### 5.1 AI生图（香蕉生图）

| 功能 | 说明 | 消耗积分 |
|------|------|:--------:|
| 文生图 | 输入文字描述生成图片 | 4积分/次 |
| 图生图 | 基于参考图生成新图片 | 4积分/次 |
| 尺寸选择 | 9:16、1:1、16:9 等比例 | - |
| 清晰度 | 普通1K、高清2K、超清4K | 按清晰度调整 |
| 生成数量 | 1张、2张、4张 | 按数量调整 |

### 5.2 AI视频

| 功能 | 说明 | 消耗积分 |
|------|------|:--------:|
| 索拉视频 | 文生视频、图生视频 | 按时长 |
| 马克视频 | 任意图片生视频 | 按时长 |
| 威尔视频 | 任意图片生视频 | 按时长 |
| 燃动数字人 | 图片生成数字人视频 | 按时长 |

### 5.3 高级功能

| 功能 | 说明 | 消耗积分 |
|------|------|:--------:|
| AI超级混剪 | 多视频一键混剪 | 按时长 |
| 一键拆解视频 | 爆款提示词提取 | 按功能 |
| 超级分身 | 人物分身功能 | 按功能 |
| 角色替换/动作迁移 | 角色替换功能 | 按功能 |

---

## 六、API接口清单

### 6.1 灵感系统API

| 接口 | 方法 | 说明 |
|------|:----:|------|
| `GET /api/video/promptCaseGroup` | GET | 获取灵感案例分类列表 |
| `GET /api/video/promptCase` | GET | 获取灵感案例列表（支持分页、分类筛选） |
| `GET /api/index/guide` | GET | 获取教程视频列表 |

### 6.2 用户系统API

| 接口 | 方法 | 说明 |
|------|:----:|------|
| `GET /api/index/site` | GET | 获取站点配置信息 |
| `GET /api/user/videoGenerate` | GET | 获取用户生成历史 |
| `GET /api/user/materialGroup` | GET | 获取用户素材分组 |
| `GET /api/user/material` | GET | 获取用户素材列表 |

### 6.3 已开发接口（本系统）

#### 用户端 (App)

| 模块 | 接口 | 方法 | 说明 |
|------|------|:----:|------|
| **灵感案例** | `/app/ai/inspiration/categories` | GET | 获取行业分类列表 |
| | `/app/ai/inspiration/list` | GET | 获取灵感案例列表 |
| | `/app/ai/inspiration/featured` | GET | 获取精选案例 |
| | `/app/ai/inspiration/get` | GET | 获取案例详情 |
| | `/app/ai/inspiration/use` | POST | 记录使用案例 |
| **教程视频** | `/app/ai/tutorial/categories` | GET | 获取教程分类列表 |
| | `/app/ai/tutorial/list` | GET | 获取教程视频列表 |
| | `/app/ai/tutorial/get` | GET | 获取视频详情 |
| | `/app/ai/tutorial/like` | POST | 点赞视频 |
| **AI生图** | `/app/ai/image/generate` | POST | 生成图片（文生图/图生图） |
| | `/app/ai/image/list` | GET | 获取我的图片列表 |
| | `/app/ai/image/page` | GET | 分页获取图片列表 |
| | `/app/ai/image/get` | GET | 获取图片详情 |
| | `/app/ai/image/delete` | DELETE | 删除图片 |
| **AI视频** | `/ai/video/text-to-video` | POST | 文生视频 |
| | `/ai/video/image-to-video` | POST | 图生视频 |
| | `/ai/video/golden-6s` | POST | 黄金6秒拼接 |
| | `/ai/video/ai-mix` | POST | AI超级混剪 |
| | `/ai/video/extract-script` | POST | 视频拆解-提取脚本 |
| | `/ai/video/analyze-elements` | POST | 视频拆解-分析元素 |
| | `/ai/video/generate-prompt` | POST | 视频拆解-生成提示词 |
| | `/ai/video/page` | GET | 获取视频生成记录 |
| | `/ai/video/detail` | GET | 获取视频详情 |
| **内容生成** | `/app/ai/content/generate` | POST | 生成内容 |
| | `/app/ai/content/get` | GET | 查询生成详情 |
| | `/app/ai/content/list` | GET | 查询生成记录列表 |
| | `/app/ai/content/status` | GET | 轮询生成状态 |
| **用户资产** | `/app/ai/asset/list` | GET | 获取我的资产列表 |
| | `/app/ai/asset/get` | GET | 获取资产详情 |
| | `/app/ai/asset/delete` | DELETE | 删除资产 |
| | `/app/ai/asset/move` | PUT | 移动资产到分组 |
| | `/app/ai/asset/group/list` | GET | 获取素材分组列表 |
| | `/app/ai/asset/group/create` | POST | 创建素材分组 |
| | `/app/ai/asset/group/update` | PUT | 更新素材分组 |
| | `/app/ai/asset/group/delete` | DELETE | 删除素材分组 |
| **钱包系统** | `/pay/wallet/get` | GET | 获取钱包信息 |
| | `/pay/wallet-transaction/page` | GET | 获取钱包流水分页 |
| | `/pay/wallet-transaction/get-summary` | GET | 获取钱包流水统计 |
| **代理系统** | `/app/agency/user/my` | GET | 获取我的代理信息 |
| | `/app/agency/user/bind` | POST | 绑定上级代理 |
| | `/app/agency/user/children` | GET | 获取下级列表 |
| | `/app/agency/point/transfer` | POST | 分配积分 |
| | `/app/agency/point/wallet` | GET | 获取积分钱包 |

#### 管理端 (Admin)

| 模块 | 接口 | 方法 | 说明 |
|------|------|:----:|------|
| **灵感案例** | `/admin/ai/inspiration-case/create` | POST | 创建案例 |
| | `/admin/ai/inspiration-case/update` | PUT | 更新案例 |
| | `/admin/ai/inspiration-case/delete` | DELETE | 删除案例 |
| | `/admin/ai/inspiration-case/page` | GET | 分页查询 |
| **教程视频** | `/admin/ai/tutorial/category/*` | CRUD | 分类管理 |
| | `/admin/ai/tutorial/video/*` | CRUD | 视频管理 |
| **代理配置** | `/admin/agency/config/*` | CRUD | 代理配置管理 |
| **代理用户** | `/admin/agency/user/*` | CRUD | 代理用户管理 |
| **积分管理** | `/admin/agency/point/*` | CRUD | 积分分配管理 |

---

## 七、数据库设计

### 7.1 灵感案例表 (ai_inspiration_case)

```sql
CREATE TABLE ai_inspiration_case (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    type            VARCHAR(32) COMMENT '类型：banana/veo/grok/seedance',
    category_id     INT COMMENT '分类ID',
    title           VARCHAR(128) NOT NULL COMMENT '案例标题',
    content         TEXT COMMENT '提示词内容',
    image           VARCHAR(512) COMMENT '封面图URL',
    image_first     VARCHAR(512) COMMENT '首帧图URL',
    image_tail      VARCHAR(512) COMMENT '尾帧图URL',
    video_url       VARCHAR(512) COMMENT '视频URL',
    duration        INT DEFAULT 0 COMMENT '视频时长(秒)',
    like_count      INT DEFAULT 0 COMMENT '点赞数',
    view_count      INT DEFAULT 0 COMMENT '浏览数',
    use_count       INT DEFAULT 0 COMMENT '使用数',
    label           VARCHAR(64) COMMENT '标签',
    icon            VARCHAR(512) COMMENT '图标URL',
    featured        BIT DEFAULT 0 COMMENT '是否精选',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         BIT DEFAULT 0,
    INDEX idx_category (category_id),
    INDEX idx_type (type)
) COMMENT '灵感案例表';
```

### 7.2 教程视频表 (ai_tutorial_video)

```sql
CREATE TABLE ai_tutorial_video (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id     BIGINT COMMENT '分类ID',
    name            VARCHAR(128) NOT NULL COMMENT '教程名称',
    cover           VARCHAR(512) COMMENT '封面图URL',
    video_url       VARCHAR(512) NOT NULL COMMENT '视频URL',
    duration        INT DEFAULT 0 COMMENT '视频时长(秒)',
    view_count      INT DEFAULT 0 COMMENT '观看数',
    like_count      INT DEFAULT 0 COMMENT '点赞数',
    is_free         BIT DEFAULT 1 COMMENT '是否免费',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         BIT DEFAULT 0,
    INDEX idx_category (category_id)
) COMMENT '教程视频表';
```

### 7.3 案例分类表 (ai_inspiration_category)

```sql
CREATE TABLE ai_inspiration_category (
    id              INT PRIMARY KEY,
    name            VARCHAR(64) NOT NULL COMMENT '分类名称',
    icon            VARCHAR(512) COMMENT '分类图标',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sort (sort_order)
) COMMENT '灵感案例分类表';
```

---

## 八、技术架构

### 8.1 技术选型

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.5.9 |
| ORM | MyBatis Plus | 3.5.15 |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis | 6.0+ |
| JDK | OpenJDK | 17 |
| 构建 | Maven | 3.6+ |
| 前端 | Vue 3 + Nuxt | Latest |

### 8.2 CDN资源

- 图片CDN: `https://cdn.fenshen123.com/`
- API基础地址: `https://s.xingchaoyiqing.com/api/`

---

## 九、开发进度

### 9.1 已完成

| 模块 | 功能 | 状态 |
|------|------|:----:|
| **数据库** | 核心表设计 | ✅ |
| | 初始化脚本 | ✅ |
| **灵感系统** | 灵感案例库接口 | ✅ |
| | 教程视频接口 | ✅ |
| | 行业分类管理 | ✅ |
| **AI生图** | 文生图/图生图接口 | ✅ |
| | 生成历史记录 | ✅ |
| **AI视频** | 文生视频/图生视频 | ✅ |
| | 黄金6秒 | ✅ |
| | AI混剪 | ✅ |
| | 爆款拆解 | ✅ |
| **资产系统** | 用户资产管理 | ✅ |
| | 素材分组管理 | ✅ |
| **钱包系统** | 钱包查询 | ✅ |
| | 积分流水 | ✅ |
| | 充值功能 | ✅ |
| **代理系统** | 代理关系绑定 | ✅ |
| | 积分分配 | ✅ |
| | 佣金结算 | ✅ |
| | 下级管理 | ✅ |
| **AI模块** | 舞墨AI对接 | ✅ |
| | 视频生成 | ✅ |
| | 内容生成模板 | ✅ |

### 9.2 待开发

| 模块 | 功能 | 优先级 |
|------|------|:------:|
| **支付** | 微信支付真实对接 | P0 |
| | 支付宝真实对接 | P0 |
| **前端** | 首页 | P0 |
| | AI生图页 | P0 |
| | AI视频页 | P0 |
| | 代理中心 | P0 |
| | 积分充值页 | P0 |

---

## 十、变更记录

| 日期 | 版本 | 变更内容 |
|------|------|----------|
| 2026-04-07 | v4.0 | 基于 timarsky.com 竞品分析，完善灵感系统设计，添加API接口文档 |
| 2026-04-07 | v3.0 | 重构文档结构，添加教程视频模块 |
| 2026-04-02 | v2.0 | 基于参考网站完善功能清单 |
| 2026-03-28 | v1.0 | 初始版本 |