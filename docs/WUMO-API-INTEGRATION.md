# 舞墨 AI API 对接完成报告

## 一、已完成工作

### 1. 核心文件清单

#### API 客户端层 (`framework/ai/core/model/wumo/api/`)
| 文件 | 说明 |
|------|------|
| `WuMoApiConstants.java` | 舞墨 API 常量定义（基础 URL、接口端点、默认参数） |
| `WuMoApi.java` | 舞墨 API 接口定义（包含请求/响应记录类） |
| `WuMoApiImpl.java` | 舞墨 API 实现类（支持图片生成、视频生成、任务查询、轮询等待） |

#### Service 层 (`service/video/`)
| 文件 | 说明 |
|------|------|
| `AiVideoService.java` | 视频生成 Service 接口 |
| `AiVideoServiceImpl.java` | 视频生成 Service 实现（支持文生视频、图生视频、黄金 6 秒、AI 混剪、视频拆解） |

#### Controller 层 (`controller/app/video/`)
| 文件 | 说明 |
|------|------|
| `AiVideoController.java` | 视频生成 Controller（REST API 端点） |
| `AiVideoGenerateReqVO.java` | 视频生成请求 VO |
| `AiVideoAnalyzeReqVO.java` | 视频分析请求 VO |

#### 实体类更新
| 文件 | 变更 |
|------|------|
| `AiImageDO.java` | 新增 `generationType`、`inputImageUrl`、`inputVideoUrl`、`outputUrl`、`coverUrl`、`duration` 字段 |
| `AiImageRespVO.java` | 新增视频相关响应字段 |
| `AiPlatformEnum.java` | 新增 `WU_MO("WuMo", "舞墨 AI")` 平台枚举 |

#### 数据库脚本
| 文件 | 说明 |
|------|------|
| `ai-video-extension.sql` | AI 视频生成字段扩展脚本 |

---

## 二、API 接口列表

### 2.1 文生视频
```
POST /ai/video/text-to-video
Body:
{
  "userId": 1,
  "prompt": "一只在南极跳舞的企鹅",
  "modelId": 1,
  "duration": 5
}
Response: 生成记录 ID
```

### 2.2 图生视频
```
POST /ai/video/image-to-video
Body:
{
  "userId": 1,
  "prompt": "让这张图片动起来",
  "modelId": 1,
  "imageUrl": "https://example.com/image.png",
  "duration": 5
}
Response: 生成记录 ID
```

### 2.3 黄金 6 秒拼接
```
POST /ai/video/golden-6s
Body:
{
  "userId": 1,
  "prompt": "拼接一个 6 秒的创意视频",
  "modelId": 1
}
Response: 生成记录 ID
```

### 2.4 AI 超级混剪
```
POST /ai/video/ai-mix
Body:
{
  "userId": 1,
  "prompt": "混剪多个视频素材",
  "modelId": 1
}
Response: 生成记录 ID
```

### 2.5 视频拆解 - 提取脚本
```
POST /ai/video/extract-script
Body:
{
  "userId": 1,
  "videoUrl": "https://example.com/video.mp4",
  "modelId": 1
}
Response: 生成记录 ID
```

### 2.6 视频拆解 - 分析元素
```
POST /ai/video/analyze-elements
Body:
{
  "userId": 1,
  "videoUrl": "https://example.com/video.mp4",
  "modelId": 1
}
Response: 生成记录 ID
```

### 2.7 视频拆解 - 生成提示词
```
POST /ai/video/generate-prompt
Body:
{
  "userId": 1,
  "videoUrl": "https://example.com/video.mp4",
  "modelId": 1
}
Response: 生成记录 ID
```

### 2.8 获取视频列表
```
GET /ai/video/page?userId=1&pageNo=1&pageSize=10
Response: 分页数据
```

### 2.9 获取视频详情
```
GET /ai/video/detail?id=1
Response: 视频详情
```

### 2.10 同步视频状态
```
POST /ai/video/sync-status?id=1
Response: 是否成功
```

---

## 三、舞墨 AI 平台推荐模型

根据市面上的主流 AI 视频/图片生成模型，推荐以下配置：

### 3.1 图片生成模型
| 模型名称 | 描述 | 推荐场景 |
|---------|------|---------|
| `FLUX.1` | FLUX 文生图模型，高质量 | 通用图片生成 |
| `SD-XL` | Stable Diffusion XL | 写实风格图片 |
| `Kolors` | 快手可图模型 | 人像、风景 |

### 3.2 视频生成模型
| 模型名称 | 描述 | 推荐场景 |
|---------|------|---------|
| `Keling` | 快手可灵 AI | 文生视频、图生视频 |
| `Seeware` | 海螺 AI | 高质量视频生成 |
| `Runway` | Runway Gen-2 | 专业视频生成 |
| `Pika` | Pika Labs | 创意视频 |

### 3.3 模型配置示例（数据库）
```sql
INSERT INTO `ai_model` (`id`, `name`, `model`, `platform`, `type`, `sort`, `status`) VALUES
(1, '可灵文生视频', 'Keling', 'WuMo', 2, 1, 1),
(2, '可灵图生视频', 'Keling', 'WuMo', 2, 2, 1),
(3, 'FLUX 生图', 'FLUX.1', 'WuMo', 1, 1, 1);
```

---

## 四、积分扣减配置

根据 PRD 需求，建议积分扣减配置如下：

| 功能 | 最低积分 | 推荐积分 |
|------|---------|---------|
| AI 生图 | 20 积分 | 20-50 积分 |
| 文生视频 | 20 积分 | 20 积分 |
| 图生视频 | 20 积分 | 20 积分 |
| 黄金 6 秒 | 30 积分 | 30 积分 |
| AI 混剪 | 50 积分 | 50 积分 |
| 视频拆解 - 提取脚本 | 20 积分 | 20 积分 |
| 视频拆解 - 分析元素 | 20 积分 | 20 积分 |
| 视频拆解 - 生成提示词 | 20 积分 | 20 积分 |

### 积分业务类型枚举
在 `WalletService` 中添加以下业务类型：

```java
// 支出类（负数）
public static final int BIZ_TYPE_AI_TEXT_VIDEO = -2;     // AI 文生视频
public static final int BIZ_TYPE_AI_IMAGE_VIDEO = -3;    // AI 图生视频
public static final int BIZ_TYPE_AI_GOLDEN_6S = -4;      // 黄金 6 秒
public static final int BIZ_TYPE_AI_MIX = -5;            // AI 混剪
public static final int BIZ_TYPE_AI_ANALYZE_SCRIPT = -6; // 视频拆解文案
public static final int BIZ_TYPE_AI_ANALYZE_ELEMENT = -7;// 视频拆解元素
public static final int BIZ_TYPE_AI_ANALYZE_PROMPT = -8; // 视频拆解提示词
```

---

## 五、待完成事项

### 5.1 API Key 配置 ✅ 已完成

API Key 已配置到 `application.yaml` 配置文件中：

```yaml
yudao:
  ai:
    wumo:
      enable: true
      api-key: XQuRvI6ZUoVg37e0PpcYDheRfY
      base-url: https://api.wuyinkeji.com
      poll-interval: 3000
      max-wait-time: 300000
```

配置类 `WuMoAiConfiguration` 使用 `@ConfigurationProperties` 自动绑定配置，并创建 `WuMoApi` Bean 供注入使用。

### 5.2 真实 API 对接
由于无法访问舞墨 API 文档，当前实现基于通用 AI API 设计。需要：
1. 访问 https://api.wuyinkeji.com/docs 获取真实 API 文档
2. 调整 `WuMoApiImpl` 中的请求/响应格式
3. 更新端点 URL

### 5.3 数据库迁移
执行数据库脚本：
```sql
source /Users/mima0000/Desktop/claude-skill/shuang-qian/sql/mysql/ai-video-extension.sql;
```

### 5.4 前端对接
前端需要调用以下接口：
1. 选择模型（从 `ai_model` 表获取）
2. 调用生成接口
3. 轮询状态（`/ai/video/sync-status`）
4. 展示结果

---

## 六、技术要点

### 6.1 异步处理
视频生成采用异步处理方式：
1. 提交任务后返回记录 ID
2. 后台线程轮询 API 直到完成
3. 前端通过轮询 `/ai/video/sync-status` 获取最新状态

### 6.2 错误处理
- API 调用失败时记录错误信息
- 任务失败时更新 `error_message` 字段
- 支持重试机制（待实现）

### 6.3 积分扣减
在视频生成前需要先扣减积分：
```java
if (!walletService.hasEnoughPoints(userId, costPoints)) {
    throw new IllegalArgumentException("积分不足");
}
walletService.deductPoints(userId, costPoints, bizType, orderNo, description);
```

---

## 七、下一步行动

### ✅ 已完成

1. **配置舞墨 API Key** - 已配置到 `application.yaml`
2. **创建配置类** - `WuMoAiConfiguration` 使用 `@ConfigurationProperties` 绑定配置
3. **更新 Service 实现** - `AiVideoServiceImpl` 使用注入的 `WuMoApi` Bean
4. **执行数据库脚本** - 运行 `ai-video-extension.sql` 创建扩展字段
5. **集成积分扣减** - 已添加积分检查和扣减逻辑，创建 `AiVideoBizTypeEnum` 枚举

### ⏳ 待完成

1. **验证 API 文档** - 访问 https://api.wuyinkeji.com/docs 确认 API 格式
2. **配置模型数据** - 在 `ai_model` 表中添加舞墨 AI 模型
3. **测试 API 集成** - 调用真实 API 验证响应格式
4. **前端开发** - 开发视频生成页面
