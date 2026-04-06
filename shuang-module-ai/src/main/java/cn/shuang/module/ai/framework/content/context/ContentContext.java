package cn.shuang.module.ai.framework.content.context;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 内容生成上下文
 * <p>
 * 在各 Agent 之间传递数据，包含用户输入和各 Agent 的输出结果
 */
@Data
@Accessors(chain = true)
public class ContentContext {

    // ==================== 用户输入 ====================

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户输入（提示词/文案）
     */
    private String userInput;

    /**
     * 参考视频 URL（用于爆款拆解）
     */
    private String referenceVideoUrl;

    /**
     * 输入图片 URL（用于图生视频）
     */
    private String inputImageUrl;

    /**
     * 内容风格（写实/动漫/创意等）
     */
    private String style;

    /**
     * 行业分类（电商/大健康/工厂等）
     */
    private String category;

    // ==================== Agent 输出结果 ====================

    /**
     * 热门分析结果（TrendAnalyzer 输出）
     */
    private TrendAnalysisResult trendResult;

    /**
     * 内容规划结果（ContentPlanner 输出）
     */
    private ContentPlanResult planResult;

    /**
     * 脚本结果（ScriptWriter 输出）
     */
    private ScriptResult scriptResult;

    /**
     * 视频结果（VideoComposer 输出）
     */
    private VideoResult videoResult;

    // ==================== 配置参数 ====================

    /**
     * 最大重试次数，默认 3 次
     */
    private int maxRetryCount = 3;

    /**
     * 质量阈值（0-100），默认 80
     */
    private int qualityThreshold = 80;

    /**
     * 是否启用流式输出
     */
    private boolean enableStreaming = false;

    // ==================== 链式更新方法（用于 Agent 执行后更新上下文） ====================

    /**
     * 设置热门分析结果并返回当前上下文（链式调用）
     */
    public ContentContext withTrendResult(TrendAnalysisResult trendResult) {
        this.trendResult = trendResult;
        return this;
    }

    /**
     * 设置内容规划结果并返回当前上下文（链式调用）
     */
    public ContentContext withPlanResult(ContentPlanResult planResult) {
        this.planResult = planResult;
        return this;
    }

    /**
     * 设置脚本结果并返回当前上下文（链式调用）
     */
    public ContentContext withScriptResult(ScriptResult scriptResult) {
        this.scriptResult = scriptResult;
        return this;
    }

    /**
     * 设置视频结果并返回当前上下文（链式调用）
     */
    public ContentContext withVideoResult(VideoResult videoResult) {
        this.videoResult = videoResult;
        return this;
    }

    // ==================== 静态构造方法 ====================

    public static ContentContextBuilder builder() {
        return new ContentContextBuilder();
    }

    public static class ContentContextBuilder {
        private Long userId;
        private String userInput;
        private String referenceVideoUrl;
        private String inputImageUrl;
        private String style;
        private String category;
        private TrendAnalysisResult trendResult;
        private ContentPlanResult planResult;
        private ScriptResult scriptResult;
        private VideoResult videoResult;
        private int maxRetryCount = 3;
        private int qualityThreshold = 80;
        private boolean enableStreaming = false;

        public ContentContextBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public ContentContextBuilder userInput(String userInput) {
            this.userInput = userInput;
            return this;
        }

        public ContentContextBuilder referenceVideoUrl(String referenceVideoUrl) {
            this.referenceVideoUrl = referenceVideoUrl;
            return this;
        }

        public ContentContextBuilder inputImageUrl(String inputImageUrl) {
            this.inputImageUrl = inputImageUrl;
            return this;
        }

        public ContentContextBuilder style(String style) {
            this.style = style;
            return this;
        }

        public ContentContextBuilder category(String category) {
            this.category = category;
            return this;
        }

        public ContentContextBuilder trendResult(TrendAnalysisResult trendResult) {
            this.trendResult = trendResult;
            return this;
        }

        public ContentContextBuilder planResult(ContentPlanResult planResult) {
            this.planResult = planResult;
            return this;
        }

        public ContentContextBuilder scriptResult(ScriptResult scriptResult) {
            this.scriptResult = scriptResult;
            return this;
        }

        public ContentContextBuilder videoResult(VideoResult videoResult) {
            this.videoResult = videoResult;
            return this;
        }

        public ContentContextBuilder maxRetryCount(int maxRetryCount) {
            this.maxRetryCount = maxRetryCount;
            return this;
        }

        public ContentContextBuilder qualityThreshold(int qualityThreshold) {
            this.qualityThreshold = qualityThreshold;
            return this;
        }

        public ContentContextBuilder enableStreaming(boolean enableStreaming) {
            this.enableStreaming = enableStreaming;
            return this;
        }

        public ContentContext build() {
            ContentContext context = new ContentContext();
            context.setUserId(this.userId);
            context.setUserInput(this.userInput);
            context.setReferenceVideoUrl(this.referenceVideoUrl);
            context.setInputImageUrl(this.inputImageUrl);
            context.setStyle(this.style);
            context.setCategory(this.category);
            context.setTrendResult(this.trendResult);
            context.setPlanResult(this.planResult);
            context.setScriptResult(this.scriptResult);
            context.setVideoResult(this.videoResult);
            context.setMaxRetryCount(this.maxRetryCount);
            context.setQualityThreshold(this.qualityThreshold);
            context.setEnableStreaming(this.enableStreaming);
            return context;
        }
    }
}
