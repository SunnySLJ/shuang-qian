package cn.shuang.module.ai.framework.content.result;

import cn.shuang.module.ai.framework.content.context.*;
import lombok.Builder;
import lombok.Data;

/**
 * 内容生成最终结果
 * <p>
 * Orchestrator 服务的输出，包含所有 Agent 的执行结果
 */
@Data
@Builder
public class ContentGenerationResult {

    /**
     * 任务 ID
     */
    private Long taskId;

    /**
     * 生成状态：0-处理中，1-完成，2-失败
     */
    private Integer status;

    /**
     * 输出视频 URL
     */
    private String videoUrl;

    /**
     * 缩略图 URL
     */
    private String thumbnailUrl;

    /**
     * 脚本内容
     */
    private String scriptContent;

    /**
     * SEO 优化标题
     */
    private String seoTitle;

    /**
     * SEO 标签
     */
    private String seoTags;

    /**
     * 消耗积分
     */
    private Integer costPoints;

    /**
     * 质量评分
     */
    private Integer qualityScore;

    /**
     * 错误信息
     */
    private String errorMessage;

    // ==================== 各 Agent 输出结果 ====================

    /**
     * 热门分析结果
     */
    private TrendAnalysisResult trendAnalysis;

    /**
     * 内容规划结果
     */
    private ContentPlanResult contentPlan;

    /**
     * 脚本结果
     */
    private ScriptResult script;

    /**
     * 视频结果
     */
    private VideoResult video;

    /**
     * 质量审查报告
     */
    private CriticResult criticReport;

    /**
     * SEO 优化建议
     */
    private SEOResult seoRecommendation;

    // ==================== 统计信息 ====================

    /**
     * 总 LLM 调用次数
     */
    private Integer totalLlmCalls;

    /**
     * 总 Token 使用量
     */
    private Integer totalTokenUsage;

    /**
     * 总耗时（毫秒）
     */
    private Long elapsedMs;
}
