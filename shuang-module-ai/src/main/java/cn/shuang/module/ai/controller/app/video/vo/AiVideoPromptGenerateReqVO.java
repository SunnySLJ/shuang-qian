package cn.shuang.module.ai.controller.app.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "APP - 多图生成视频提示词 Request VO")
public class AiVideoPromptGenerateReqVO {

    @Schema(description = "图片 URL 列表，最多 4 张")
    @NotEmpty(message = "至少上传 1 张图片")
    @Size(max = 4, message = "最多上传 4 张图片")
    private List<String> imageUrls;

    @Schema(description = "用户补充需求")
    private String userPrompt;

    @Schema(description = "提示词风格等级", example = "cinematic")
    private String promptLevel;
}
