package cn.shuang.module.ai.controller.app.prompt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppPromptOptimizeReqVO {

    @Schema(description = "原始需求描述", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "做一个适合抖音投流的减脂代餐视频，突出7天轻断食和真实反馈")
    @NotBlank(message = "原始需求不能为空")
    private String rawPrompt;

    @Schema(description = "行业", example = "电商-产品广告")
    private String industry;

    @Schema(description = "平台", example = "抖音")
    private String platform;

    @Schema(description = "目标模型", example = "豆包")
    private String targetModel;

    @Schema(description = "优化模式", example = "hot_video")
    private String mode;
}
