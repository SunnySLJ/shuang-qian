# 星空AI (timarsky.com) 复刻项目技术分析

> 整理日期：2026-04-11
> 目标网站：https://www.timarsky.com
> 项目框架：基于现有 shuang-pro Spring Boot 项目

---

## 一、网站功能概览（实测）

### 核心功能模块

| 模块 | 功能描述 | 状态 |
|------|----------|------|
| **灵感** | 作品展示/灵感库，按分类浏览 | 已实现参考 |
| **编导** | 视频编导功能 | 待分析 |
| **做图** | AI 图片生成 | 已有基础 |
| **音频** | 音频处理功能 | 待分析 |
| **视频** | AI 视频生成（图生视频/文生视频） | 已有基础 |
| **资产** | 用户素材管理（含数字分身） | 待实现 |
| **AI工具** | 其他 AI 辅助工具 | 待分析 |

### 内容分类

- **行业分类**：电商、大健康、工厂、励志演讲、探店、宠物、美发、短剧、带货、变装
- **风格分类**：写实、动漫、创意、跨境
- **设计分类**：家具设计、LOGO、服饰、美食
- **辅助功能**：修图改图、AI数字人

### 模型来源（推测）

| 模型名称 | 用途 | 可能来源 |
|----------|------|----------|
| 威尔视频 | 视频生成 | 可蕊/Wan |
| 香蕉生图 | 图片生成 | Midjourney/Stable Diffusion |
| 马克视频 | 视频生成 | Kling/Runway |
| 方舟视频 | 视频生成 | Sora 类模型 |
| 索拉视频 | 视频生成 | OpenAI Sora |

### 新上线功能（2026-04-10 公告）

**AI超级分身**（位于【资产】-【分身】功能页）

1. **照片形象**
   - 本地上传：上传人物图片存入形象库
   - AI生成：根据提示词自动生成人物形象

2. **视频分身**
   - 基于数字人形象一键生成视频
   - 无需真人出镜完成高质量视频创作

---

## 二、复刻难点分析（按严重程度排序）

### ★★★★★ 高危难点（核心痛点）

#### 2.1 视频生成模型集成

| 问题 | 具体表现 | 技术原因 |
|------|----------|----------|
| 计算资源极高 | 单次生成 10-60 秒 | 需 24G+ GPU 显存 |
| API 限制 | 速率/额度受限 | 商业化规模化困难 |
| 人物一致性 | "鬼脸"、"手畸形" | 长视频风格漂移 |
| 成本控制 | GPU 调度复杂 | 用户并发队列管理 |

**开源模型参考**：
- CogVideoX（国产视频生成）
- Stable Video Diffusion
- AnimateAnyone（全身驱动）
- LivePortrait + SadTalker（唇同步）

**闭源 API 参考**：
- Kling AI（中国最强图生视频）
- Runway Gen-3
- Luma Dream Machine
- Pika 1.5
- HeyGen / D-ID（数字人唇同步）

#### 2.2 提示词工程（Prompt）

| 问题 | 影响 | 解决难度 |
|------|------|----------|
| 用户不会写提示词 | 效果差 80% | 需 LLM 自动优化 |
| Prompt 质量不稳定 | 结果差异大 | 需多轮测试调优 |
| 多语言支持 | 国际化需求 | 翻译 + 本地化 |
| 视频模型敏感 | 需专业描述 | 镜头、动作、光影、时长 |

**视频 Prompt 必须包含**：
```
- Camera movement（镜头运动）
- Emotion（情绪表达）
- Lighting（光影效果）
- Style（画面风格）
- Duration（时长）
- Subject description（主体描述）
```

---

### ★★★★☆ 中高危难点

#### 2.3 系统架构

| 模块 | 难点 | 解决方案 |
|------|------|----------|
| 异步生成 | 任务队列管理 | Celery + Redis |
| GPU 资源调度 | 并发控制 | 任务优先级队列 |
| 失败重试 | 生成失败补偿 | 自动重试 + 积分回滚 |
| 计费扣费 | 积分一致性 | 幂等 + 事务 |

#### 2.4 前端用户体验

| 功能 | 要求 |
|------|------|
| 拖拽上传 | 流畅的素材上传体验 |
| Prompt 智能补全 | AI 辅助写提示词 |
| 生成进度条 | 实时进度反馈 |
| 作品画廊 | 历史/收藏展示 |
| 一键生成 | 低门槛操作 |

---

### ★★★☆ 中等难点

#### 2.5 合规与安全

| 问题 | 风险 | 措施 |
|------|------|------|
| Deepfake | 虚假宣传 | 水印 + 审核 + 协议 |
| 内容审核 | 违规内容 | AI 审核 + 人工复核 |
| 用户协议 | 肖像权 | 法律条款 + 授权 |

#### 2.6 支付与商业化

| 功能 | 实现 |
|------|------|
| 订阅制 | 套餐购买 |
| 点数系统 | 积分充值/消耗 |
| 微信支付 | 支付渠道对接 |

---

### ★★☆ 较低难点

#### 2.7 数据隐私

- 用户照片加密存储
- GDPR/CCPA 合规
- 数据脱敏处理

---

## 三、技术选型（基于现有项目）

### 3.1 后端架构（复用 shuang-pro）

| 层级 | 现有技术 | 扩展需求 |
|------|----------|----------|
| 框架 | Spring Boot 3.5.9 | 无需更换 |
| 数据库 | MySQL + MyBatis Plus | 无需更换 |
| 缓存 | Redis | 任务队列扩展 |
| 认证 | 已有会员系统 | 扩展数字分身 |
| 支付 | 已有钱包模块 | 扩展套餐 |

### 3.2 大模型对接方案

#### 方案 A：API 集成（推荐 MVP）

| 用途 | 推荐 API | 备选 |
|------|----------|------|
| 图生视频 | Kling AI API | Runway Gen-3 |
| 文生视频 | Kling AI API | Luma Dream Machine |
| 数字人唇同步 | HeyGen API | D-ID API |
| 图片生成 | 已有舞墨 API | Midjourney API |
| Prompt 优化 | Claude 3.5 / GPT-4o | 通义千问 / 豆包 |

**优势**：
- 快速上线，技术门槛低
- 效果稳定，无需运维 GPU
- 按量付费，成本可控

**劣势**：
- API 依赖，受第三方限制
- 成本随用量增长
- 数据隐私需评估

#### 方案 B：开源模型自建（中期）

| 用途 | 推荐模型 | 硬件要求 |
|------|----------|----------|
| 视频生成 | CogVideoX | 24G+ GPU |
| 数字人驱动 | LivePortrait + SadTalker | 16G+ GPU |
| 唇同步 | Wav2Lip | 8G+ GPU |
| Prompt 优化 | Qwen2-72B | 需微调 |

**部署方案**：
- RunPod / Vast.ai 租 GPU 节点
- ComfyUI 可视化 Pipeline
- vLLM / Text-Generation-WebUI

#### 方案 C：混合方案（长期）

- 高频低成本功能：开源模型自建
- 高质量关键功能：商用 API
- Prompt 层：自建 LLM 微调

### 3.3 Prompt 优化系统设计

#### System Prompt 模板

```markdown
你是一个专业的 AI 视频提示词工程师。用户会给你一个简单的中文描述，
你需要将其扩展为专业的视频生成提示词。

输出格式：
{
  "camera_movement": "镜头运动方式（推拉摇移跟升降）",
  "emotion": "人物情绪（开心/悲伤/紧张/平静等）",
  "lighting": "光影效果（自然光/人工光/逆光等）",
  "style": "画面风格（写实/动漫/电影感等）",
  "duration": "时长建议（秒）",
  "subject": "主体详细描述",
  "background": "背景描述",
  "audio": "音频建议（音乐/音效）",
  "full_prompt": "合并后的完整英文提示词"
}

示例输入：卖车广告
示例输出：
{
  "camera_movement": "慢速推镜 + 环绕拍摄",
  "emotion": "自信、专业",
  "lighting": "金色阳光侧光",
  "style": "电影级质感",
  "duration": "15秒",
  "subject": "一位穿着西装的销售顾问站在豪华轿车旁，自信微笑",
  "background": "现代化展厅，背景有其他车辆",
  "audio": "轻快商业背景音乐",
  "full_prompt": "A professional car salesman in a suit stands beside a luxury sedan in a modern showroom, confident smile, cinematic golden sunlight from side, slow push-in camera movement, 15 seconds, high quality commercial style"
}
```

#### Prompt Library 结构

```
按行业分类：
- 电商类：产品展示、促销广告
- 大健康：医疗科普、产品介绍
- 房产/汽车：销售宣传、样板展示
- 美妆/服饰：模特展示、搭配推荐
- 餐饮/美食：菜品展示、环境介绍

按场景分类：
- 励志演讲：演讲者 + 背景 + 表情
- 探店：博主 + 店内 + 走动
- 短剧：剧情 + 角色 + 动作
```

### 3.4 前端技术

| 功能 | 现有 | 扩展 |
|------|------|------|
| UI 框架 | Vue 3 | 无需更换 |
| 状态管理 | Pinia | 无需更换 |
| 上传组件 | 需新增 | 拖拽 + 裁剪 |
| 进度展示 | 需新增 | 实时进度条 |
| 作品画廊 | 需新增 | 网格展示 |

---

## 四、MVP 实施路径

### Phase 1：基础框架（Week 1-2）

| 任务 | 产出 |
|------|------|
| 前端框架调整 | 功能模块导航 |
| 后端 API 接入 | Kling API 对接 |
| 基础生成流程 | 图片 + 视频生成 |

### Phase 2：Prompt 系统（Week 3）

| 任务 | 产出 |
|------|------|
| Prompt 优化层 | LLM 自动扩展 |
| 模板库 | 按行业分类模板 |
| 多语言支持 | 中英文翻译 |

### Phase 3：数字分身（Week 4）

| 任务 | 产出 |
|------|------|
| 形象库 | 上传 + AI 生成 |
| 视频分身 | 基于形象生成视频 |
| 资产管理 | 用户素材管理 |

### Phase 4：商业化（Week 5-6）

| 任务 | 产出 |
|------|------|
| 套餐系统 | 订阅 + 点数 |
| 支付渠道 | 微信支付对接 |
| 用户系统 | 登录注册完善 |

---

## 五、核心代码参考

### 5.1 视频生成接口（幂等 + 事务）

```java
@PostMapping("/video/generate")
@Idempotent(key = "ai:video:generate:#{#userId}:#{#bizOrderNo}",
            timeout = 60, keyResolver = ExpressionIdempotentKeyResolver.class)
@Transactional(rollbackFor = Exception.class)
public CommonResult<Long> generateVideo(@RequestBody VideoGenerateReqVO reqVO) {
    Long userId = getLoginUserId();
    String bizOrderNo = UUID.randomUUID().toString();
    
    // 1. 查询积分余额
    int cost = getVideoCost(reqVO.getType(), reqVO.getDuration());
    if (!walletService.hasEnoughPoints(userId, cost)) {
        throw new PointInsufficientException();
    }
    
    // 2. 扣减积分（幂等）
    walletService.deductPoints(userId, cost, bizType, bizOrderNo);
    
    // 3. 优化 Prompt
    String optimizedPrompt = promptService.optimize(reqVO.getPrompt(), reqVO.getStyle());
    
    // 4. 创建生成记录
    AiVideoDO video = new AiVideoDO()
        .setUserId(userId)
        .setPrompt(optimizedPrompt)
        .setOriginalPrompt(reqVO.getPrompt())
        .setStatus(VideoStatusEnum.PROCESSING)
        .setCost(cost)
        .setBizOrderNo(bizOrderNo);
    videoMapper.insert(video);
    
    // 5. 异步生成
    getSelf().executeVideoGeneration(video.getId(), optimizedPrompt, reqVO);
    
    return success(video.getId());
}

// 异步执行，失败时回滚积分
@Async
public void executeVideoGeneration(Long videoId, String prompt, VideoGenerateReqVO reqVO) {
    try {
        // 调用 AI API
        String videoUrl = klingApi.generateVideo(prompt, reqVO);
        videoMapper.updateStatus(videoId, VideoStatusEnum.SUCCESS, videoUrl);
    } catch (Exception e) {
        videoMapper.updateStatus(videoId, VideoStatusEnum.FAIL);
        // 回滚积分
        walletService.addPoints(userId, cost, "视频生成失败退款", bizOrderNo);
        log.error("[视频生成失败] videoId={}", videoId, e);
    }
}
```

### 5.2 Prompt 优化服务

```java
@Service
public class PromptOptimizeService {
    
    private final ClaudeApi claudeApi;
    
    public String optimize(String userInput, String style) {
        String systemPrompt = loadSystemPrompt(style);
        
        // 调用 LLM 优化
        String response = claudeApi.chat(systemPrompt, userInput);
        
        // 解析 JSON
        PromptResult result = parsePromptResult(response);
        
        return result.getFullPrompt();
    }
    
    private String loadSystemPrompt(String style) {
        // 根据风格加载不同的 System Prompt
        return """
            你是专业 AI 视频提示词工程师...
            输出格式见上方模板...
            """;
    }
}
```

---

## 六、风险评估与建议

### 技术风险

| 风险 | 等级 | 措施 |
|------|------|------|
| API 依赖 | 高 | 多渠道备份 |
| GPU 成本 | 高 | 混合方案 |
| 生成质量不稳定 | 中 | Prompt 优化 + 重试 |
| 并发压力 | 中 | 队列 + 限流 |

### 合规风险

| 风险 | 等级 | 措施 |
|------|------|------|
| Deepfake 争议 | 高 | 水印 + 协议 + 审核 |
| 肖像权纠纷 | 高 | 用户授权 + 法律条款 |
| 内容违规 | 中 | AI 审核 + 人工 |

### 建议

1. **先做 MVP**：只支持"1张照片 + 1句话生成15秒视频"
2. **API 优先**：先用 Kling API，验证后再考虑自建
3. **法律先行**：尽早咨询律师，起草用户协议
4. **成本可控**：生成前预估点数，失败自动退款

---

## 七、后续扩展

### 7.1 功能扩展路线

- **短期**：图生视频 + Prompt 优化 + 数字分身
- **中期**：批量生成 + 模板库 + 多模型
- **长期**：自建 GPU Pipeline + 本地部署选项

### 7.2 商业化扩展

- **订阅套餐**：基础版/专业版/企业版
- **点数充值**：一次性购买 + 月度订阅
- **代理分销**：复用现有代理模块

---

## 附录：网站实测信息

### 备案信息
- ICP备案号：辽ICP备2024044460号-8
- 版权所有：星巢引擎科技（2024-2026）

### 作品模型分类
| 模型 | 作品数量（首页） | 推测用途 |
|------|------------------|----------|
| 香蕉生图 | 约 12 个 | 图片生成主力 |
| 马克视频 | 约 4 个 | 视频生成 |
| 尔视频 | 约 3 个 | 视频生成 |
| 方舟视频 | 约 1 个 | 视频生成 |
| 索拉视频 | 约 2 个 | 视频生成 |

---

> **文档维护**：随着开发进展，定期更新此文档
> **关联文档**：`docs/difficult.md`（项目核心问题）、`docs/IMPLEMENTATION-PLAN.md`（实施计划）