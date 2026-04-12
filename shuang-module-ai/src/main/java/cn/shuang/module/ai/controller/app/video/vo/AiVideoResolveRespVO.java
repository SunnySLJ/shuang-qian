package cn.shuang.module.ai.controller.app.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AiVideoResolveRespVO {

    @Schema(description = "从分享文案中提取到的原始链接")
    private String extractedUrl;

    @Schema(description = "跳转后的页面链接")
    private String resolvedPageUrl;

    @Schema(description = "可直接用于视频预览和分析的直链")
    private String previewVideoUrl;

    @Schema(description = "识别到的平台")
    private String platform;

    @Schema(description = "是否可直接预览")
    private Boolean previewable;
}
