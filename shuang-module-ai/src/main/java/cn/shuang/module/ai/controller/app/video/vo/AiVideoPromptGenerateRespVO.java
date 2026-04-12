package cn.shuang.module.ai.controller.app.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "APP - 多图生成视频提示词 Response VO")
public class AiVideoPromptGenerateRespVO {

    @Schema(description = "图片理解摘要")
    private String visualSummary;

    @Schema(description = "基础提示词")
    private String basePrompt;

    @Schema(description = "电影级优化提示词")
    private String optimizedPrompt;
}
