package cn.shuang.module.ai.controller.admin.content.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 内容生成 Response VO")
@Data
public class ContentGenerateRespVO {

    @Schema(description = "任务 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long taskId;

    @Schema(description = "生成状态：0-处理中，1-完成，2-失败", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "输出视频 URL", example = "https://...")
    private String videoUrl;

    @Schema(description = "缩略图 URL", example = "https://...")
    private String thumbnailUrl;

    @Schema(description = "生成的脚本内容", example = "完整的脚本文案...")
    private String scriptContent;

    @Schema(description = "SEO 优化标题", example = "震惊！这种方法让效率提升 10 倍")
    private String seoTitle;

    @Schema(description = "SEO 标签", example = "效率，时间管理，职场干货")
    private String seoTags;

    @Schema(description = "消耗积分", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private Integer costPoints;

    @Schema(description = "质量评分", example = "85")
    private Integer qualityScore;

    @Schema(description = "错误信息", example = "生成失败")
    private String errorMessage;

    @Schema(description = "总耗时 (ms)", example = "30000")
    private Long elapsedMs;

    @Schema(description = "创建时间", example = "2026-04-06 12:00:00")
    private LocalDateTime createTime;
}
