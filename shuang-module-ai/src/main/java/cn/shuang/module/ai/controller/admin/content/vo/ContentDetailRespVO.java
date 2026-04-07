package cn.shuang.module.ai.controller.admin.content.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 内容生成详情 Response VO")
@Data
public class ContentDetailRespVO {

    @Schema(description = "任务 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long taskId;

    @Schema(description = "生成状态：0-处理中，1-完成，2-失败", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    // ==================== 输入信息 ====================

    @Schema(description = "用户输入", example = "一只可爱的橘猫")
    private String userInput;

    @Schema(description = "参考视频 URL", example = "https://...")
    private String referenceVideoUrl;

    @Schema(description = "输入图片 URL", example = "https://...")
    private String inputImageUrl;

    @Schema(description = "内容风格", example = "写实")
    private String style;

    // ==================== 输出信息 ====================

    @Schema(description = "输出视频 URL", example = "https://...")
    private String videoUrl;

    @Schema(description = "缩略图 URL", example = "https://...")
    private String thumbnailUrl;

    @Schema(description = "生成的脚本内容")
    private String scriptContent;

    @Schema(description = "SEO 优化标题", example = "震惊！这种方法让效率提升 10 倍")
    private String seoTitle;

    @Schema(description = "SEO 标签", example = "效率，时间管理，职场干货")
    private String seoTags;

    // ==================== AI 分析结果 ====================

    @Schema(description = "热门分析结果 (JSON)")
    private String analysisResult;

    // ==================== 统计信息 ====================

    @Schema(description = "消耗积分", example = "50")
    private Integer costPoints;

    @Schema(description = "质量评分", example = "85")
    private Integer qualityScore;

    @Schema(description = "总耗时 (ms)", example = "30000")
    private Long elapsedMs;

    @Schema(description = "LLM 调用次数", example = "5")
    private Integer totalLlmCalls;

    @Schema(description = "Token 使用量", example = "5000")
    private Integer totalTokenUsage;

    @Schema(description = "错误信息", example = "生成失败")
    private String errorMessage;

    @Schema(description = "创建时间")
    private String createTime;
}
