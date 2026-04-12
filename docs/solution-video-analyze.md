# 视频拆解功能问题分析与解决方案

> 整理日期：2026-04-09
> 功能模块：AI 视频 → 爆款拆解（视频分析）
> 相关接口：视频链接/上传视频 → 提取文案/分析元素/生成提示词

---

## 一、现有能力梳理

### 1.1 后端接口（已实现）

| 接口 | 路径 | 功能 | 消耗积分 |
|------|------|------|:--------:|
| 提取脚本 | `POST /ai/video/extract-script` | 从视频中提取完整文案脚本 | ✅ 扣积分 |
| 分析元素 | `POST /ai/video/analyze-elements` | 拆解视频中的视觉/听觉元素 | ✅ 扣积分 |
| 生成提示词 | `POST /ai/video/generate-prompt` | 将拆解结果转化为 AI 生成提示词 | ✅ 扣积分 |

三个接口共用同一个异步执行方法 `executeVideoAnalysis()`，底层调用**舞墨 AI** 的视频分析 API，轮询任务状态后更新结果。

### 1.2 前端现状（缺口）

| 模块 | 状态 | 说明 |
|------|:----:|------|
| 前端 API 调用层 | ❌ 缺失 | `video.ts` 中没有三个接口的前端方法 |
| 视频拆解页面 | ❌ 缺失 | 无独立的爆款拆解 UI 入口 |
| 结果展示组件 | ❌ 缺失 | 分析结果如何展示未设计 |
| 一键复用功能 | ❌ 缺失 | 拆解出的提示词如何跳转到生成页未实现 |

---

## 二、核心问题

### 问题 1：前端 API 缺失

**现状**

后端三个接口已就绪，但当前前端项目 `shuang-ui/zhuimeng-dream` 中没有任何视频拆解相关的调用封装和页面入口。前端团队无法接入。

**影响**

- 用户完全无法使用视频拆解功能
- 后端接口成了"死代码"

---

### 问题 2：视频拆解无独立页面

**现状**

视频拆解功能没有 UI 入口，用户不知道在哪里可以使用。PRD 明确要求"一键拆解热门视频，获取爆款提示词和元素"。

**影响**

- 核心卖点功能无法触达用户
- 功能等于未上线

---

### 问题 3：视频来源处理不完整

**现状**

当前只接受 `videoUrl` 参数，**不支持本地上传视频**。用户想拆解自己本地的视频文件时无法处理。

**支持的视频来源**

| 来源 | 支持状态 | 说明 |
|------|:--------:|------|
| 视频链接（URL） | ✅ 已支持 | 直接传 URL 即可 |
| 本地上传（文件） | ❌ 未支持 | 需要新增文件上传逻辑 |
| 素材库已有视频 | ❌ 未支持 | 可选择历史生成的视频 |

---

### 问题 4：积分扣减失败无回滚

**现状**

`executeVideoAnalysis()` 异步任务失败时只更新记录状态为 FAIL，**已扣的积分不会退还**。

```java
// 当前代码（失败时不回滚）
catch (Exception e) {
    imageMapper.updateById(new AiImageDO().setId(image.getId())
            .setStatus(AiImageStatusEnum.FAIL.getStatus())
            .setErrorMessage(e.getMessage()));
    // ❌ 缺少：walletService.addPoints(userId, cost, "视频拆解失败退款");
}
```

**影响**

- 视频拆解失败时用户白白损失积分
- 用户体验差，可能引发投诉

---

### 问题 5：结果轮询体验差

**现状**

前端只能通过轮询 `GET /ai/video/detail` 或 `POST /ai/video/sync-status` 查询结果，无进度提示，用户感知是"点完就没反应了"。

**影响**

- 用户以为功能坏了，不知道在处理中
- 无进度百分比反馈，体验断档

---

### 问题 6：舞墨 API Key 硬编码

**现状**

`AiVideoServiceImpl.getWuMoApi()` 中有默认 API Key `XQuRvI6ZUoVg37e0PpcYDheRfY`，直接暴露在代码中。

**影响**

- 生产环境密钥泄露风险
- 密钥轮换需要改代码

---

## 三、解决方案

### 方案 1：前端 API 层补全

在 `shuang-ui/zhuimeng-dream` 中补充三个接口的调用方法：

```typescript
// ============== 视频拆解 API ==============

/** 视频拆解 - 提取脚本 */
export function extractScript(data: {
  videoUrl: string
  modelId: number
}): Promise<ApiResult<number>> {
  return request.post('/ai/video/extract-script', data)
}

/** 视频拆解 - 分析元素 */
export function analyzeElements(data: {
  videoUrl: string
  modelId: number
}): Promise<ApiResult<number>> {
  return request.post('/ai/video/analyze-elements', data)
}

/** 视频拆解 - 生成提示词 */
export function generatePrompt(data: {
  videoUrl: string
  modelId: number
}): Promise<ApiResult<number>> {
  return request.post('/ai/video/generate-prompt', data)
}
```

---

### 方案 2：创建独立视频拆解页面

新建页面：`shuang-ui/zhuimeng-dream/src/views/ai/VideoAnalyzePage.vue`

#### 2.1 页面布局

```
┌─────────────────────────────────────────────┐
│  NavBar / 侧栏入口（导航）                    │
├──────────────────────┬──────────────────────┤
│                      │                      │
│   视频输入区          │   拆解结果展示区       │
│                      │                      │
│  [上传/链接输入]       │  [提取脚本]           │
│                      │  [分析元素]           │
│  支持拖拽上传 mp4/mov  │  [生成提示词]         │
│  支持粘贴视频链接      │                      │
│                      │  [一键复制文案]        │
│  ─────────────────   │  [应用到AI生图]       │
│  视频预览区           │  [应用到AI视频]       │
│                      │                      │
├──────────────────────┴──────────────────────┤
│  操作记录区（历史拆解记录，可切换 Tab）          │
└─────────────────────────────────────────────┘
```

#### 2.2 核心交互流程

```
Step 1: 用户输入视频
        ├─ 本地上传 → 上传到文件服务器 → 获取 URL
        └─ 粘贴链接 → 校验链接有效性 → 预览

Step 2: 用户选择拆解模式
        ├─ 提取脚本
        ├─ 分析元素
        └─ 生成提示词（可多选，一次执行多个）

Step 3: 扣积分 & 提交任务
        ├─ 显示消耗积分数量
        ├─ 余额不足时禁用按钮
        └─ 成功后进入轮询

Step 4: 结果展示（轮询中）
        ├─ 进度动画（拆解中 0% → 100%）
        ├─ 完成后展示结果
        └─ 可一键复制 / 应用到生成页
```

#### 2.3 关键组件设计

**视频上传组件**

```typescript
// 支持三种来源
const videoSources = [
  { type: 'upload', label: '本地上传' },
  { type: 'link', label: '视频链接' },
  { type: 'library', label: '素材库选择' }
]

// 上传逻辑：先调后端 /infra/file/upload 获取 URL
const handleUpload = async (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  const res = await fileApi.upload(formData)
  videoUrl.value = res.data.url
}
```

**进度轮询 Hook**

```typescript
// src/composables/useVideoAnalyze.ts
import { ref, onUnmounted } from 'vue'

export function useVideoAnalyze() {
  const status = ref<'idle' | 'analyzing' | 'success' | 'fail'>('idle')
  const progress = ref(0)
  const result = ref<AnalyzeResult | null>(null)
  let pollTimer: number | null = null

  const analyze = async (videoUrl: string, type: 'script' | 'elements' | 'prompt', modelId: number) => {
    status.value = 'analyzing'
    progress.value = 0

    // 1. 提交任务，获取记录 ID
    const { data: recordId } = await videoApi.extractScript({ videoUrl, modelId })

    // 2. 轮询结果
    const maxWait = 60000
    const interval = 2000
    let elapsed = 0

    pollTimer = window.setInterval(async () => {
      elapsed += interval
      progress.value = Math.min(90, (elapsed / maxWait) * 100)

      const detail = await videoApi.getVideoDetail(recordId)
      if (detail.data.status === 'SUCCESS') {
        progress.value = 100
        result.value = detail.data
        status.value = 'success'
        clearInterval(pollTimer!)
      } else if (detail.data.status === 'FAIL') {
        status.value = 'fail'
        clearInterval(pollTimer!)
      }
    }, interval)
  }

  onUnmounted(() => {
    if (pollTimer) clearInterval(pollTimer)
  })

  return { status, progress, result, analyze }
}
```

**一键应用到生成页**

```typescript
// 从拆解结果跳转到生成页，自动填充 prompt
const applyToGenerate = (result: AnalyzeResult, type: 'image' | 'video') => {
  const encodedPrompt = encodeURIComponent(result.prompt)
  if (type === 'image') {
    router.push(`/ai/image/generate?prompt=${encodedPrompt}`)
  } else {
    router.push(`/ai/video/generate?prompt=${encodedPrompt}`)
  }
}
```

---

### 方案 3：支持本地上传视频

后端复用已有的文件上传接口 `POST /infra/file/upload`，前端直接调用，前端代码层面处理：

```typescript
// 素材库选择
const openLibraryPicker = async () => {
  // 调素材库列表接口，选中后自动填入 videoUrl
  const selected = await showMaterialPicker()
  videoUrl.value = selected.videoUrl
}

// 视频链接校验
const validateVideoUrl = (url: string) => {
  const allowedExts = ['.mp4', '.mov', '.avi', '.webm']
  return allowedExts.some(ext => url.toLowerCase().endsWith(ext))
    || url.includes('cdn.') || url.includes('cos.')
}
```

---

### 方案 4：积分扣减失败回滚

修改 `AiVideoServiceImpl.executeVideoAnalysis()` 方法，在异步任务失败时回滚积分：

```java
// 需要传入 cost 参数，失败时使用
CompletableFuture.runAsync(() -> {
    try {
        // ... 原有逻辑 ...
    } catch (Exception e) {
        log.error("[视频分析异常] id: {}", image.getId(), e);

        // 1. 更新记录状态
        imageMapper.updateById(new AiImageDO().setId(image.getId())
                .setStatus(AiImageStatusEnum.FAIL.getStatus())
                .setErrorMessage(e.getMessage())
                .setFinishTime(LocalDateTime.now()));

        // 2. 回滚积分（查询该记录关联的积分消耗记录并退还）
        // 注意：异步任务中需要知道 userId 和 cost
        // 建议：将 cost 存入 AiImageDO.options 或单独字段
        refundService.autoRefund(image.getId(), userId,
            "视频拆解失败自动退款");
    }
});
```

更好的方案：**定时扫描失败任务退款**（不依赖异步任务成功回滚）

```java
// 定时任务：扫描最近 N 分钟内失败且未退款的任务，自动退款
@Scheduled(fixedRate = 60000)
public void scanAndRefundFailedTasks() {
    List<AiImageDO> failedRecords = imageMapper.selectList(
        new LambdaQueryWrapper<AiImageDO>()
            .eq(AiImageDO::getStatus, AiImageStatusEnum.FAIL.getStatus())
            .eq(AiImageDO::getRefunded, false)
            .ge(AiImageDO::getCreateTime, LocalDateTime.now().minusMinutes(10))
    );

    for (AiImageDO record : failedRecords) {
        // 退还积分
        // 更新 refunded = true
        // 记录退款流水
    }
}
```

---

### 方案 5：结果数据结构化

当前 `options` 字段存的是 JSON，不便于前端解析。建议新增结构化字段或新建 `ai_video_analyze_result` 表：

```sql
CREATE TABLE ai_video_analyze_result (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    image_id        BIGINT NOT NULL COMMENT '关联记录ID',
    user_id         BIGINT NOT NULL,
    analyze_type    VARCHAR(32) NOT NULL COMMENT 'SCRIPT/ELEMENTS/PROMPT',
    script_text     TEXT COMMENT '提取的脚本文案',
    elements        JSON COMMENT '分析的元素（视觉/听觉/文字）',
    generated_prompt TEXT COMMENT '生成的AI提示词',
    source_video_url VARCHAR(512) COMMENT '源视频URL',
    result_url      VARCHAR(512) COMMENT '结果文件URL',
    status          TINYINT DEFAULT 0 COMMENT '状态',
    refund_flag     BIT DEFAULT 0 COMMENT '是否已退款',
    create_time     DATETIME,
    update_time     DATETIME,
    INDEX idx_image_id (image_id),
    INDEX idx_user_id (user_id)
) COMMENT '视频拆解结果表';
```

---

### 方案 6：API Key 配置化

```yaml
# application.yml
shuang:
  ai:
    wumo:
      api-key: ${WUMO_API_KEY}    # 从环境变量读取，禁止硬编码
      app-id: ${WUMO_APP_ID}
      base-url: https://api.wumo.com
      timeout-ms: 30000
```

移除代码中所有默认值和警告日志：

```java
@RequiredArgsConstructor
public class AiVideoServiceImpl implements AiVideoService {

    private final ShuangProProperties properties;

    private WuMoApi getWuMoApi(AiModelDO model) {
        // 不再从 model 拿 apiKey，统一走配置文件
        WuMoApiConfig config = properties.getAi().getWumo();
        Assert.hasText(config.getApiKey(), "未配置舞墨 API Key，请检查 shuang.ai.wumo.api-key");
        return wuMoApi;  // 返回预配置好的实例
    }
}
```

---

## 四、实施计划

### 第一阶段：让功能跑通（约 2 小时）

| 任务 | 工作量 | 说明 |
|------|:------:|------|
| 补全前端 API 调用 | 0.5h | `video.ts` 增加三个接口方法 |
| 创建视频拆解页面骨架 | 1h | 视频输入 + 结果展示 + 轮询逻辑 |
| 对接后端已有接口 | 0.5h | 联调确保数据能通 |

### 第二阶段：体验优化（约 3 小时）

| 任务 | 工作量 | 说明 |
|------|:------:|------|
| 本地上传视频支持 | 1h | 文件上传 + 链接校验 |
| 素材库选择 | 1h | 复用资产接口选择已有视频 |
| 进度动画 + 失败提示 | 1h | 用户感知更清晰 |

### 第三阶段：安全与健壮（约 2 小时）

| 任务 | 工作量 | 说明 |
|------|:------:|------|
| 积分失败回滚 | 1h | 定时任务 + 退款逻辑 |
| API Key 配置化 | 0.5h | 移除硬编码 |
| 结果表结构化 | 0.5h | 拆解结果独立存储 |

---

## 五、技术依赖

| 依赖项 | 状态 | 备注 |
|--------|:----:|------|
| 舞墨 AI 视频分析 API | ✅ 已对接 | `/video/analyze` |
| 文件上传接口 | ✅ 已实现 | `/infra/file/upload` |
| 积分扣减 | ⚠️ 有缺陷 | 失败无回滚 |
| 前端视频上传组件 | ❌ 缺失 | 需新开发 |
| 视频拆解页面 | ❌ 缺失 | 需新开发 |
| 轮询/Hook 封装 | ❌ 缺失 | 需新开发 |

---

## 六、验收标准

- [ ] 用户可通过视频链接使用拆解功能
- [ ] 用户可通过本地上传使用拆解功能
- [ ] 拆解失败时积分自动退还
- [ ] 结果支持一键复制 / 应用到生成页
- [ ] 轮询过程有进度反馈
- [ ] API Key 不在代码中硬编码
