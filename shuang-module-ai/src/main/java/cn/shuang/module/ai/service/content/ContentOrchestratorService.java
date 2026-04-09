package cn.shuang.module.ai.service.content;

import cn.shuang.module.ai.framework.content.agent.*;
import cn.shuang.module.ai.framework.content.context.*;
import cn.shuang.module.ai.framework.content.result.ContentGenerationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 内容生成编排器服务
 * <p>
 * 参考 Claw-AI-Lab 的 AgentOrchestrator 设计，协调多个 Agent 完成内容生成工作流：
 * 1. TrendAnalyzer → 热门内容分析
 * 2. ContentPlanner → 内容规划
 * 3. ScriptWriter → 脚本撰写
 * 4. VideoComposer → 视频合成（待实现）
 * 5. QualityCritic → 质量审查（可 retry 循环）
 * 6. SEOOptimizer → SEO 优化
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "yudao.ai.content-generation.enable", havingValue = "true")
public class ContentOrchestratorService {

    private final TrendAnalyzerAgent trendAnalyzer;
    private final ContentPlannerAgent contentPlanner;
    private final ScriptWriterAgent scriptWriter;
    private final QualityCriticAgent qualityCritic;
    private final VideoComposerAgent videoComposer;
    private final SEOOptimizerAgent seoOptimizer;

    /**
     * 执行完整的内容生成工作流
     *
     * @param context 内容生成上下文
     * @return 生成结果
     */
    public ContentGenerationResult generate(ContentContext context) {
        long startTime = System.currentTimeMillis();
        int totalLlmCalls = 0;
        int totalTokenUsage = 0;

        log.info("[ContentOrchestrator] 开始内容生成工作流，用户 ID: {}", context.getUserId());

        try {
            // ==================== Step 1: 热门内容分析 ====================
            log.info("[ContentOrchestrator] Step 1: 热门内容分析");
            AgentStepResult trendResult = trendAnalyzer.execute(context);
            if (!trendResult.isSuccess()) {
                return buildErrorResult("热门分析失败：" + trendResult.getError());
            }
            context.withTrendResult((TrendAnalysisResult) trendResult.getData());
            totalLlmCalls += trendResult.getLlmCalls();
            totalTokenUsage += trendResult.getTokenUsage();

            // ==================== Step 2: 内容规划 ====================
            log.info("[ContentOrchestrator] Step 2: 内容规划");
            AgentStepResult planResult = contentPlanner.execute(context);
            if (!planResult.isSuccess()) {
                return buildErrorResult("内容规划失败：" + planResult.getError());
            }
            context.withPlanResult((ContentPlanResult) planResult.getData());
            totalLlmCalls += planResult.getLlmCalls();
            totalTokenUsage += planResult.getTokenUsage();

            // ==================== Step 3: 脚本撰写 ====================
            log.info("[ContentOrchestrator] Step 3: 脚本撰写");
            AgentStepResult scriptResult = scriptWriter.execute(context);
            if (!scriptResult.isSuccess()) {
                return buildErrorResult("脚本撰写失败：" + scriptResult.getError());
            }
            context.withScriptResult((ScriptResult) scriptResult.getData());
            totalLlmCalls += scriptResult.getLlmCalls();
            totalTokenUsage += scriptResult.getTokenUsage();

            // ==================== Step 4: 质量审查（支持 retry 循环） ====================
            log.info("[ContentOrchestrator] Step 4: 质量审查（最多重试 {} 次）", context.getMaxRetryCount());
            CriticResult finalCriticResult = null;
            boolean qualityPassed = false;

            for (int i = 0; i < context.getMaxRetryCount(); i++) {
                AgentStepResult criticResult = qualityCritic.execute(context);
                if (!criticResult.isSuccess()) {
                    log.warn("[ContentOrchestrator] 质量审查失败（第 {} 次）: {}", i + 1, criticResult.getError());
                    continue;
                }

                finalCriticResult = (CriticResult) criticResult.getData();
                if (finalCriticResult.isPassed()) {
                    qualityPassed = true;
                    log.info("[ContentOrchestrator] 质量审查通过（第 {} 次），评分：{}", i + 1, finalCriticResult.getOverallScore());
                    break;
                } else {
                    log.info("[ContentOrchestrator] 质量审查未通过（第 {} 次），评分：{}", i + 1, finalCriticResult.getOverallScore());
                    // TODO: 可以根据审查建议自动优化脚本后重试
                }
            }

            // ==================== Step 5: 视频合成 ====================
            log.info("[ContentOrchestrator] Step 5: 视频合成");
            AgentStepResult videoResult = videoComposer.execute(context);
            if (!videoResult.isSuccess()) {
                return buildErrorResult("视频合成失败：" + videoResult.getError());
            }
            context.withVideoResult((VideoResult) videoResult.getData());
            totalLlmCalls += videoResult.getLlmCalls();
            totalTokenUsage += videoResult.getTokenUsage();

            // ==================== Step 6: SEO 优化 ====================
            log.info("[ContentOrchestrator] Step 6: SEO 优化");
            AgentStepResult seoResult = seoOptimizer.execute(context);
            if (!seoResult.isSuccess()) {
                return buildErrorResult("SEO 优化失败：" + seoResult.getError());
            }
            SEOResult seoRecommendation = (SEOResult) seoResult.getData();
            totalLlmCalls += seoResult.getLlmCalls();
            totalTokenUsage += seoResult.getTokenUsage();

            // ==================== Step 7: 构建结果 ====================
            ContentGenerationResult result = ContentGenerationResult.builder()
                    .status(qualityPassed ? 1 : 2) // 质量通过则状态为完成，否则为失败
                    .qualityScore(finalCriticResult != null ? finalCriticResult.getOverallScore() : 0)
                    .videoUrl(context.getVideoResult() != null ? context.getVideoResult().getVideoUrl() : null)
                    .thumbnailUrl(context.getVideoResult() != null ? context.getVideoResult().getThumbnailUrl() : null)
                    .seoTitle(seoRecommendation.getOptimizedTitle())
                    .seoRecommendation(seoRecommendation)
                    .trendAnalysis(context.getTrendResult())
                    .contentPlan(context.getPlanResult())
                    .script(context.getScriptResult())
                    .criticReport(finalCriticResult)
                    .totalLlmCalls(totalLlmCalls)
                    .totalTokenUsage(totalTokenUsage)
                    .elapsedMs(System.currentTimeMillis() - startTime)
                    .build();

            log.info("[ContentOrchestrator] 工作流完成，耗时：{}ms, LLM 调用：{}次",
                    result.getElapsedMs(), result.getTotalLlmCalls());

            return result;

        } catch (Exception e) {
            log.error("[ContentOrchestrator] 工作流异常", e);
            return buildErrorResult("系统异常：" + e.getMessage());
        }
    }

    /**
     * 构建错误结果
     */
    private ContentGenerationResult buildErrorResult(String errorMessage) {
        return ContentGenerationResult.builder()
                .status(2)
                .errorMessage(errorMessage)
                .build();
    }
}
