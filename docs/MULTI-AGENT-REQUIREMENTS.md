# 多 Agent 自媒体内容生成系统需求分析

> 基于 Claw-AI-Lab 多 Agent 架构，结合 Shuang-Pro 现有业务体系设计

---

## 一、需求概述

### 1.1 背景分析

**当前 Shuang-Pro 项目状态：**
- 已具备完整的代理分销体系和积分系统
- AI 生成功能依赖外部 API（通义千问、Midjourney、Suno 等）
- 用户通过积分消耗使用 AI 功能，但内容质量依赖用户自身提示词能力
- 缺少"爆款内容"的智能化生成能力

**Claw-AI-Lab 核心价值：**
- 多 Agent 协作完成复杂研究任务
- 包含调研、实验设计、代码生成、验证、可视化等完整流程
- 支持讨论模式（多 Agent 辩论）和复现模式
- 自动产出论文 + 代码 + 图表

### 1.2 产品目标

将 Claw-AI-Lab 的多 Agent 架构应用到 Shuang-Pro，打造**面向自媒体人的爆款内容生成系统**：

| 维度 | 目标 |
|------|------|
| **用户价值** | 降低爆款内容创作门槛，一键拆解热门视频并生成同款 |
| **技术价值** | 复用 Claw-AI-Lab 的 Orchestrator 模式，构建内容生成工作流 |
| **商业价值** | 提升积分消耗效率，增强用户粘性，扩大代理分销吸引力 |

---

## 二、Claw-AI-Lab 架构分析

### 2.1 核心架构模式

```
┌─────────────────────────────────────────────────────────────────┐
│                    Claw-AI-Lab 架构总览                          │
└─────────────────────────────────────────────────────────────────┘

用户输入研究主题
       │
       ▼
┌─────────────────────────────────────────────────────────────────┐
│  Orchestrator (编排器)                                          │
│  - 协调多个 Agent 的顺序执行                                      │
│  - 管理 retry 循环                                                │
│  - 聚合输出结果                                                 │
└─────────────────────────────────────────────────────────────────┘
       │
       ├──► Surveyor Agent → 文献/数据调研
       │
       ├──► Selector Agent → 选择 benchmarks/baselines
       │
       ├──► Acquirer Agent → 获取代码/数据
       │
       ├──► Validator Agent → 验证代码正确性
       │
       ├──► Planner Agent → 实验规划
       │
       ├──► CodeGen Agent → 生成实验代码
       │
       ├──► Renderer Agent → 生成图表
       │
       ├──► Critic Agent → 质量审查
       │
       └──► Decision Agent → 决策/继续/终止
```

### 2.2 Agent 基类设计 (BaseAgent)

```python
class BaseAgent:
    """所有子 Agent 的基类"""
    
    name: str = "base"
    
    def execute(self, context: dict) -> AgentStepResult:
        """执行 Agent 的核心任务"""
        raise NotImplementedError
    
    def _chat(self, system: str, user: str) -> str:
        """调用 LLM 进行对话"""
        
    def _chat_json(self, system: str, user: str) -> dict:
        """调用 LLM 并期望返回 JSON"""
```

### 2.3 编排器模式 (AgentOrchestrator)

```python
class AgentOrchestrator:
    """协调多个 Agent 的顺序执行"""
    
    def orchestrate(self, context: dict) -> dict:
        """运行多 Agent 工作流"""
        # 1. 按顺序调用子 Agent
        # 2. 管理重试循环
        # 3. 累积 LLM 调用统计
        # 4. 返回最终结果
```

### 2.4 关键设计模式

| 模式 | 说明 | 可复用点 |
|------|------|----------|
| **Pipeline 模式** | Surveyor → Selector → Acquirer → Validator | 内容生成工作流 |
| **Retry 循环** | CodeGen → Renderer → Critic (最多 3 次) | 质量迭代优化 |
| **配置驱动** | Config 数据类定义各 Agent 参数 | 灵活的策略配置 |
| **结果封装** | BenchmarkPlan/FigurePlan 封装输出 | 统一的结果结构 |

---

## 三、自媒体内容生成系统架构设计

### 3.1 系统架构总览

```
┌─────────────────────────────────────────────────────────────────┐
│                  自媒体内容生成系统架构                           │
└─────────────────────────────────────────────────────────────────┘

用户输入创作需求
       │
       ▼
┌─────────────────────────────────────────────────────────────────┐
│  ContentOrchestrator (内容编排器)                                │
│  - 协调内容生成工作流                                            │
│  - 管理质量审查循环                                              │
│  - 聚合多 Agent 输出                                              │
└─────────────────────────────────────────────────────────────────┘
       │
       ├──► TrendAnalyzer Agent → 热门内容分析
       │       - 分析抖音/小红书/B 站热门视频
       │       - 提取爆款元素（标题、封面、节奏、BGM）
       │
       ├──► ContentPlanner Agent → 内容规划
       │       - 生成内容大纲
       │       - 设计分镜脚本
       │       - 推荐 BGM 和特效
       │
       ├──► ScriptWriter Agent → 脚本撰写
       │       - 生成视频台词/文案
       │       - 匹配情绪和节奏
       │
       ├──► VisualDesigner Agent → 视觉设计
       │       - 生成画面描述
       │       - 推荐构图和色彩
       │
       ├──► VideoComposer Agent → 视频合成
       │       - 调用视频生成 API
       │       - 多片段拼接
       │
       ├──► QualityCritic Agent → 质量审查
       │       - 检查内容完整性
       │       - 评估爆款潜力
       │
       └──► SEOOptimizer Agent → 流量优化
               - 生成标题和标签
               - 优化封面文案
```

### 3.2 模块映射关系

| Claw-AI-Lab | Shuang-Pro 内容生成 | 说明 |
|-------------|---------------------|------|
| Surveyor | TrendAnalyzer | 文献调研 → 热门内容分析 |
| Selector | ContentPlanner | 选择 benchmarks → 选择内容策略 |
| Acquirer | ScriptWriter | 获取代码 → 生成脚本 |
| Validator | QualityCritic | 验证代码 → 审查内容质量 |
| Planner | VisualDesigner | 实验规划 → 视觉设计 |
| CodeGen | VideoComposer | 生成代码 → 合成视频 |
| FigureAgent | SEOOptimizer | 生成图表 → 流量优化 |

### 3.3 技术架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        技术栈选择                                │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  前端层 (Vue 3 + Element Plus)                                   │
│  - 内容创作工作台                                                │
│  - 爆款案例库浏览                                               │
│  - 生成任务管理                                                 │
└─────────────────────────────────────────────────────────────────┘
       │ HTTP/HTTPS
┌─────────────────────────────────────────────────────────────────┐
│  后端层 (Spring Boot 3.5.9 + Spring AI)                          │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Controller 层                                           │   │
│  │  - ContentController (内容生成接口)                       │   │
│  │  - TrendController (热门分析接口)                         │   │
│  │  - TemplateController (模板管理接口)                      │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Service 层 (Orchestrator 模式)                           │   │
│  │  - ContentOrchestratorService                           │   │
│  │  - TrendAnalyzerAgent                                   │   │
│  │  - ContentPlannerAgent                                  │   │
│  │  - ScriptWriterAgent                                    │   │
│  │  - VideoComposerAgent                                   │   │
│  │  - QualityCriticAgent                                   │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  DAL 层                                                   │   │
│  │  - ContentRecordMapper (生成记录)                         │   │
│  │  - ContentTemplateMapper (内容模板)                       │   │
│  │  - TrendCacheMapper (热门缓存)                           │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
       │
┌─────────────────────────────────────────────────────────────────┐
│  数据层 & 外部服务                                               │
│  - MySQL (主数据)                                               │
│  - Redis (缓存/任务队列)                                         │
│  - 外部 AI API (通义千问/Midjourney/可灵/即梦)                    │
│  - 爬虫服务 (热门内容抓取)                                       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 四、功能设计

### 4.1 核心功能清单

| 功能模块 | 功能点 | 优先级 | 积分消耗 |
|----------|--------|--------|----------|
| **爆款拆解** | 上传视频自动拆解 | P0 | 50 积分 |
| **文案提取** | 提取视频台词/字幕 | P0 | 20 积分 |
| **元素分析** | 识别画面元素/运镜 | P0 | 20 积分 |
| **提示词生成** | 生成同款提示词 | P0 | 20 积分 |
| **文生视频** | 文字生成短视频 | P0 | 50 积分 |
| **图生视频** | 图片转视频 | P0 | 50 积分 |
| **黄金 6 秒** | 多片段拼接 | P1 | 80 积分 |
| **AI 混剪** | 多视频一键成片 | P1 | 100 积分 |
| **灵感案例** | 21 个行业案例库 | P0 | - |
| **内容模板** | 一键使用模板 | P1 | - |

### 4.2 爆款拆解功能详细设计

#### 4.2.1 功能流程

```
用户上传视频
     │
     ▼
┌─────────────────────────────────────────────────────────────────┐
│  TrendAnalyzerAgent.execute()                                    │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 1. 视频预处理                                             │   │
│  │    - 提取音频 → 语音转文字                                │   │
│  │    - 抽帧分析 → 识别场景/人物/物体                        │   │
│  │    - 分析节奏 → 计算剪辑频率                              │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 2. 文案提取                                              │   │
│  │    - 提取完整台词                                        │   │
│  │    - 识别金句/爆点                                       │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 3. 元素分析                                              │   │
│  │    - 画面元素识别 (场景、人物、道具)                       │   │
│  │    - 运镜方式识别 (推、拉、摇、移、跟)                    │   │
│  │    - BGM 识别和情绪分析                                    │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 4. 提示词生成                                            │   │
│  │    - 生成画面描述提示词                                  │   │
│  │    - 生成运镜指令                                        │   │
│  │    - 生成 BGM 推荐                                         │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
     │
     ▼
输出拆解报告 (JSON + Markdown)
```

#### 4.2.2 输出数据结构

```json
{
  "taskId": "breakdown_abc123",
  "videoUrl": "https://...",
  "status": "completed",
  "analysis": {
    "script": {
      "fullText": "视频完整台词...",
      "keyLines": ["金句 1", "金句 2"],
      "emotionalPeaks": [{"timestamp": "00:15", "text": "..."}]
    },
    "visuals": {
      "scenes": [
        {
          "timestamp": "00:00-00:05",
          "description": "室内场景，暖色调灯光",
          "elements": ["桌子", "咖啡杯", "笔记本电脑"],
          "cameraMovement": "缓慢推进"
        }
      ],
      "colorPalette": ["#FF6B6B", "#4ECDC4", "#45B7D1"],
      "lightingStyle": "暖色调侧光"
    },
    "audio": {
      "bgmName": "轻松的爵士乐",
      "bgmMood": "轻松、愉悦",
      "voiceSpeed": "1.2x",
      "soundEffects": ["转场音效", "强调音效"]
    },
    "rhythm": {
      "avgShotLength": "3.5s",
      "cutFrequency": "高",
      "paceAnalysis": "前慢后快，高潮在 00:30"
    }
  },
  "prompts": {
    "imagePrompts": ["画面描述提示词 1", "提示词 2"],
    "videoPrompts": ["视频生成提示词"],
    "cameraInstructions": ["运镜指令 1", "指令 2"]
  },
  "costPoints": 50,
  "createdAt": "2026-04-06T12:00:00Z"
}
```

### 4.3 内容生成工作流设计

#### 4.3.1 工作流状态机

```
┌─────────────────────────────────────────────────────────────────┐
│                    内容生成状态机                                │
└─────────────────────────────────────────────────────────────────┘

[PENDING] ──► [ANALYZING] ──► [PLANNING] ──► [SCRIPTING]
                                           │
                                           ▼
     [COMPLETED] ◄── [REVIEWING] ◄── [GENERATING]
           │
           ▼
     [DELIVERED]
```

#### 4.3.2 Agent 执行流程

```java
@Service
public class ContentOrchestratorService {
    
    @Autowired private TrendAnalyzerAgent trendAnalyzer;
    @Autowired private ContentPlannerAgent contentPlanner;
    @Autowired private ScriptWriterAgent scriptWriter;
    @Autowired private VideoComposerAgent videoComposer;
    @Autowired private QualityCriticAgent qualityCritic;
    @Autowired private SEOOptimizerAgent seoOptimizer;
    
    public ContentGenerationResult generate(ContentGenerationContext context) {
        // 1. 热门内容分析
        TrendAnalysisResult trendResult = trendAnalyzer.execute(context);
        
        // 2. 内容规划
        ContentPlanResult planResult = contentPlanner.execute(
            context.withTrendResult(trendResult)
        );
        
        // 3. 脚本撰写
        ScriptResult scriptResult = scriptWriter.execute(
            context.withPlanResult(planResult)
        );
        
        // 4. 视频生成 (支持 retry 循环)
        VideoResult videoResult = null;
        for (int i = 0; i < 3; i++) {
            videoResult = videoComposer.execute(
                context.withScriptResult(scriptResult)
            );
            
            // 5. 质量审查
            CriticResult criticResult = qualityCritic.execute(
                context.withVideoResult(videoResult)
            );
            
            if (criticResult.isPassed()) {
                break; // 审查通过，退出循环
            }
            // 审查不通过， retry 优化
        }
        
        // 6. SEO 优化
        SEOResult seoResult = seoOptimizer.execute(
            context.withVideoResult(videoResult)
        );
        
        // 7. 聚合结果
        return ContentGenerationResult.builder()
            .trendAnalysis(trendResult)
            .contentPlan(planResult)
            .script(scriptResult)
            .video(videoResult)
            .criticReport(qualityCritic)
            .seoRecommendation(seoResult)
            .build();
    }
}
```

---

## 五、数据库设计

### 5.1 新增数据表

#### 5.1.1 内容生成记录表 (content_generation_record)

```sql
CREATE TABLE content_generation_record (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id             BIGINT NOT NULL COMMENT '用户 ID',
    generation_type     TINYINT NOT NULL COMMENT '生成类型：1-爆款拆解，2-文生视频，3-图生视频，4-黄金 6 秒，5-AI 混剪',
    sub_type            TINYINT COMMENT '子类型',
    
    -- 输入
    input_text          TEXT COMMENT '输入文本 (提示词/参考文案)',
    input_image_url     VARCHAR(512) COMMENT '输入图片 URL',
    input_video_url     VARCHAR(512) COMMENT '输入视频 URL (拆解用)',
    template_id         BIGINT COMMENT '使用的模板 ID',
    
    -- 输出
    output_url          VARCHAR(512) COMMENT '输出结果 URL',
    output_thumbnail    VARCHAR(512) COMMENT '缩略图 URL',
    output_duration_ms  INT COMMENT '视频时长 (ms)',
    
    -- AI 分析结果 (JSON 格式存储)
    analysis_result     JSON COMMENT 'AI 分析结果 (拆解报告)',
    script_content      TEXT COMMENT '生成的脚本内容',
    seo_title           VARCHAR(255) COMMENT 'SEO 优化标题',
    seo_tags            VARCHAR(512) COMMENT 'SEO 标签 (逗号分隔)',
    
    -- 状态和统计
    status              TINYINT DEFAULT 0 COMMENT '状态：0-处理中，1-完成，2-失败',
    cost_points         INT NOT NULL COMMENT '消耗积分',
    duration_ms         INT COMMENT '耗时 (ms)',
    retry_count         INT DEFAULT 0 COMMENT '重试次数',
    error_message       VARCHAR(512) COMMENT '错误信息',
    
    -- 审计字段
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             BIT DEFAULT 0,
    
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_type (generation_type)
) COMMENT '内容生成记录表';
```

#### 5.1.2 内容模板表 (content_template)

```sql
CREATE TABLE content_template (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    category            VARCHAR(32) NOT NULL COMMENT '行业分类',
    title               VARCHAR(128) NOT NULL COMMENT '模板标题',
    description         VARCHAR(512) COMMENT '模板描述',
    cover_image_url     VARCHAR(512) COMMENT '封面图 URL',
    demo_video_url      VARCHAR(512) COMMENT '演示视频 URL',
    
    -- 模板内容
    prompt_template     TEXT COMMENT '提示词模板',
    script_template     TEXT COMMENT '脚本模板',
    visual_template     JSON COMMENT '视觉模板 (JSON)',
    
    -- 使用统计
    use_count           INT DEFAULT 0 COMMENT '使用次数',
    like_count          INT DEFAULT 0 COMMENT '点赞数',
    is_featured         BIT DEFAULT 0 COMMENT '是否精选',
    sort_order          INT DEFAULT 0 COMMENT '排序',
    
    -- 审计字段
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             BIT DEFAULT 0,
    
    INDEX idx_category (category),
    INDEX idx_featured (is_featured)
) COMMENT '内容模板表';
```

#### 5.1.3 热门内容缓存表 (trend_cache)

```sql
CREATE TABLE trend_cache (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    platform            VARCHAR(32) NOT NULL COMMENT '平台：douyin/xiaohongshu/bilibili',
    category            VARCHAR(32) COMMENT '分类',
    content_id          VARCHAR(128) NOT NULL COMMENT '内容 ID',
    title               VARCHAR(255) COMMENT '标题',
    author              VARCHAR(128) COMMENT '作者',
    
    -- 热门指标
    like_count          INT DEFAULT 0 COMMENT '点赞数',
    comment_count       INT DEFAULT 0 COMMENT '评论数',
    share_count         INT DEFAULT 0 COMMENT '分享数',
    view_count          INT DEFAULT 0 COMMENT '播放数',
    
    -- 分析结果
    trend_score         DECIMAL(5,2) COMMENT '热门分数 (0-100)',
    analysis_result     JSON COMMENT '分析结果 (JSON)',
    tags                VARCHAR(512) COMMENT '标签',
    
    -- 审计字段
    crawled_at          DATETIME COMMENT '抓取时间',
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_platform_content (platform, content_id),
    INDEX idx_trend_score (trend_score)
) COMMENT '热门内容缓存表';
```

---

## 六、API 接口设计

### 6.1 内容生成接口

| 接口 | 方法 | 权限 | 说明 | 积分消耗 |
|------|------|------|------|----------|
| `/app/content/video/generate` | POST | 用户 | 文生视频 | 50 积分 |
| `/app/content/video/image2video` | POST | 用户 | 图生视频 | 50 积分 |
| `/app/content/video/golden6s` | POST | 用户 | 黄金 6 秒 | 80 积分 |
| `/app/content/video/mix` | POST | 用户 | AI 混剪 | 100 积分 |
| `/app/content/video/breakdown` | POST | 用户 | 爆款拆解 | 50 积分 |
| `/app/content/script/generate` | POST | 用户 | 脚本生成 | 20 积分 |
| `/app/content/seo/optimize` | POST | 用户 | SEO 优化 | 10 积分 |

### 6.2 模板和灵感接口

| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/app/content/templates` | GET | 公开 | 模板列表 |
| `/app/content/templates/{id}` | GET | 公开 | 模板详情 |
| `/app/content/templates/{id}/use` | POST | 用户 | 使用模板 |
| `/app/content/inspiration/categories` | GET | 公开 | 行业分类 |
| `/app/content/inspiration/cases` | GET | 公开 | 案例列表 |

### 6.3 请求/响应示例

#### 爆款拆解请求

```http
POST /app/content/video/breakdown
Content-Type: application/json
Authorization: Bearer {token}

{
  "videoUrl": "https://...",
  "analysisDepth": "deep"  // shallow | deep
}

Response:
{
  "code": 0,
  "msg": "success",
  "data": {
    "taskId": "breakdown_abc123",
    "status": 0,  // 0-处理中，1-完成
    "estimatedTime": 60,  // 预计耗时 (秒)
    "costPoints": 50
  }
}
```

#### 生成结果查询

```http
GET /app/content/history/{taskId}
Authorization: Bearer {token}

Response:
{
  "code": 0,
  "data": {
    "taskId": "breakdown_abc123",
    "status": 1,  // 完成
    "outputUrl": "https://...",
    "thumbnailUrl": "https://...",
    "analysisResult": {
      "script": {...},
      "visuals": {...},
      "audio": {...},
      "rhythm": {...}
    },
    "prompts": [...],
    "costPoints": 50,
    "createdAt": "2026-04-06T12:00:00Z"
  }
}
```

---

## 七、Agent 设计详细

### 7.1 Agent 基类设计 (Java)

```java
/**
 * 所有内容生成 Agent 的基类
 */
public abstract class BaseContentAgent {
    
    protected final String name;
    protected final LlmClient llmClient;
    
    public BaseContentAgent(String name, LlmClient llmClient) {
        this.name = name;
        this.llmClient = llmClient;
    }
    
    /**
     * 执行 Agent 的核心任务
     * @param context 上下文（包含所有前置 Agent 的输出）
     * @return 执行结果
     */
    public abstract AgentStepResult execute(ContentContext context);
    
    /**
     * 调用 LLM 进行对话
     */
    protected String chat(String system, String user) {
        return llmClient.chat(system, user);
    }
    
    /**
     * 调用 LLM 并期望返回 JSON
     */
    protected <T> T chatJson(String system, String user, Class<T> responseType) {
        String json = llmClient.chat(system, user, true); // jsonMode=true
        return JsonUtils.parseObject(json, responseType);
    }
    
    /**
     * 创建执行结果
     */
    protected AgentStepResult result(boolean success, Object data, String error) {
        return AgentStepResult.builder()
            .success(success)
            .data(data)
            .error(error)
            .build();
    }
}
```

### 7.2 Agent 执行结果

```java
@Data
@Builder
public class AgentStepResult {
    
    private boolean success;
    private Object data;
    private String error;
    private int llmCalls;
    private int tokenUsage;
}
```

### 7.3 内容上下文

```java
@Data
@Builder
public class ContentContext {
    
    // 用户输入
    private String userInput;
    private String referenceVideoUrl;
    private String style;
    
    // 各 Agent 的输出
    private TrendAnalysisResult trendResult;
    private ContentPlanResult planResult;
    private ScriptResult scriptResult;
    private VideoResult videoResult;
    
    // 配置
    private int maxRetryCount = 3;
    private int qualityThreshold = 80;
    
    /**
     * 链式调用 helper
     */
    public ContentContext withTrendResult(TrendAnalysisResult result) {
        this.trendResult = result;
        return this;
    }
    
    public ContentContext withPlanResult(ContentPlanResult result) {
        this.planResult = result;
        return this;
    }
    
    // ... 其他 with 方法
}
```

### 7.4 具体 Agent 实现示例

#### TrendAnalyzerAgent

```java
@Component
public class TrendAnalyzerAgent extends BaseContentAgent {
    
    public TrendAnalyzerAgent(LlmClient llmClient) {
        super("TrendAnalyzer", llmClient);
    }
    
    @Override
    public AgentStepResult execute(ContentContext context) {
        String system = """
            你是一名短视频内容分析专家，擅长拆解爆款视频的成功要素。
            请分析用户提供的视频，从以下维度输出结构化分析结果：
            1. 文案分析：提取完整台词，识别金句和情绪高潮点
            2. 视觉元素：场景、人物、道具、色彩、灯光
            3. 运镜方式：推、拉、摇、移、跟等
            4. 节奏分析：平均镜头时长、剪辑频率、节奏变化
            5. BGM 分析：音乐风格、情绪、音效使用
            
            输出格式为 JSON。
            """;
        
        String user = "请分析这个视频：" + context.getReferenceVideoUrl();
        
        try {
            TrendAnalysisResult result = chatJson(system, user, TrendAnalysisResult.class);
            return result(true, result, null);
        } catch (Exception e) {
            return result(false, null, "分析失败：" + e.getMessage());
        }
    }
}
```

---

## 八、与现有系统集成

### 8.1 积分扣减集成

```java
@Service
public class ContentGenerationServiceImpl implements ContentGenerationService {
    
    @Autowired private ContentOrchestratorService orchestrator;
    @Autowired private PayWalletService walletService;
    @Autowired private ContentRecordMapper recordMapper;
    
    @Transactional(rollbackFor = Exception.class)
    public ContentGenerationResult generate(
        Long userId, 
        ContentGenerationReqVO req
    ) {
        // 1. 检查并扣除积分
        int costPoints = calculateCostPoints(req.getType());
        walletService.reduceBalance(userId, costPoints, 
            WalletBizTypeEnum.AI_VIDEO_GENERATE, 
            "AI 内容生成：" + req.getType());
        
        // 2. 创建生成记录
        ContentGenerationRecordDO record = new ContentGenerationRecordDO();
        record.setUserId(userId);
        record.setGenerationType(req.getType());
        record.setInputText(req.getPrompt());
        record.setCostPoints(costPoints);
        record.setStatus(0); // 处理中
        recordMapper.insert(record);
        
        // 3. 异步执行内容生成
        CompletableFuture<ContentGenerationResult> future = 
            CompletableFuture.supplyAsync(() -> {
                ContentContext context = buildContext(req);
                return orchestrator.generate(context);
            });
        
        // 4. 更新记录
        future.thenAccept(result -> {
            record.setOutputUrl(result.getVideoUrl());
            record.setStatus(1); // 完成
            recordMapper.updateById(record);
        }).exceptionally(ex -> {
            record.setStatus(2); // 失败
            record.setErrorMessage(ex.getMessage());
            recordMapper.updateById(record);
            return null;
        });
        
        return ContentGenerationResult.builder()
            .taskId(record.getId())
            .status(0)
            .costPoints(costPoints)
            .build();
    }
}
```

### 8.2 代理关系集成

```java
/**
 * 充值分成计算（复用现有逻辑）
 */
@Service
public class CommissionCalculator {
    
    @Autowired private AgencyUserService agencyUserService;
    @Autowired private AgencyCommissionRecordService commissionService;
    
    /**
     * 计算内容消费分成（可选：消费也可产生分成）
     */
    public void calculateConsumptionCommission(
        Long userId, 
        int consumptionAmount
    ) {
        // 查询用户的上级代理
        AgencyUserDO agencyUser = agencyUserService.getByUserId(userId);
        if (agencyUser == null || agencyUser.getBrokerageUserId() == null) {
            return; // 没有上级代理
        }
        
        // 获取上级代理信息
        AgencyUserDO brokerageUser = agencyUserService.getById(
            agencyUser.getBrokerageUserId()
        );
        
        // 计算分成
        int commissionRate = brokerageUser.getLevel() == 1 ? 2000 : 800;
        int commissionAmount = consumptionAmount * commissionRate / 10000;
        
        // 创建佣金记录
        commissionService.createRecord(
            userId,
            agencyUser.getBrokerageUserId(),
            BizTypeEnum.CONSUMPTION,
            commissionAmount,
            commissionRate
        );
    }
}
```

---

## 九、开发计划

### 9.1 迭代规划

| 迭代 | 周期 | 主要内容 |
|------|------|----------|
| Phase 1 | 第 1 周 | Agent 基础设施、数据库设计 |
| Phase 2 | 第 2 周 | 爆款拆解 Agent、内容规划 Agent |
| Phase 3 | 第 3 周 | 脚本撰写 Agent、视频合成集成 |
| Phase 4 | 第 4 周 | 质量审查 Agent、SEO 优化 Agent |
| Phase 5 | 第 5 周 | 前端工作台、联调测试 |

### 9.2 Phase 1 详细任务

#### Sprint 1.1 - 基础设施

| 任务 | 优先级 | 估时 | 负责人 |
|------|--------|------|--------|
| 设计 Agent 基类和接口 | P0 | 4h | 后端 |
| 实现 ContentOrchestrator | P0 | 8h | 后端 |
| 数据库表迁移脚本 | P0 | 4h | 后端 |
| ContentRecordMapper | P0 | 4h | 后端 |

#### Sprint 1.2 - 核心 Agent

| 任务 | 优先级 | 估时 |
|------|--------|------|
| TrendAnalyzerAgent | P0 | 8h |
| ContentPlannerAgent | P0 | 8h |
| ScriptWriterAgent | P1 | 8h |
| QualityCriticAgent | P1 | 8h |

### 9.3 里程碑

| 里程碑 | 时间 | 交付物 |
|--------|------|--------|
| M1: Agent 框架完成 | 第 1 周末 | 可运行的 Orchestrator + 2 个 Agent |
| M2: 爆款拆解完成 | 第 2 周末 | 可上传视频并输出拆解报告 |
| M3: 内容生成完成 | 第 3 周末 | 可输入提示词生成完整视频 |
| M4: 质量优化完成 | 第 4 周末 | 审查循环 + 自动优化 |
| M5: 上线发布 | 第 5 周末 | 前端工作台 + 完整测试 |

---

## 十、风险与应对

### 10.1 技术风险

| 风险 | 可能性 | 影响 | 应对措施 |
|------|--------|------|----------|
| 视频分析准确率低 | 中 | 高 | 多模型投票、人工标注优化 |
| 生成内容质量不稳定 | 中 | 高 | 强化 Critic Agent、retry 循环 |
| 外部 API 不稳定 | 中 | 高 | 多服务商冗余、队列缓冲 |

### 10.2 业务风险

| 风险 | 可能性 | 影响 | 应对措施 |
|------|--------|------|----------|
| 生成内容侵权 | 中 | 高 | 内容审核、版权检测 |
| 积分定价不合理 | 低 | 中 | 动态调整、AB 测试 |

---

## 十一、总结

本方案将 Claw-AI-Lab 的多 Agent 协作架构成功迁移到 Shuang-Pro 项目，设计了完整的自媒体内容生成系统：

### 核心价值

1. **复用成熟架构**：直接应用 Claw-AI-Lab 的 Orchestrator 模式和 Agent 设计
2. **增强现有业务**：与代理体系、积分系统无缝集成
3. **提升用户体验**：一键拆解爆款、智能生成内容

### 下一步行动

1. 确认需求范围和优先级
2. 启动 Phase 1 开发
3. 同步设计前端工作台 UI

---

**文档版本**: v1.0  
**创建日期**: 2026-04-06  
**最后更新**: 2026-04-06
