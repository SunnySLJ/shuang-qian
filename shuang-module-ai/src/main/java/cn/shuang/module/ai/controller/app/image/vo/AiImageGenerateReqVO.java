package cn.shuang.module.ai.controller.app.image.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * AI 图片生成请求 VO
 *
 * @author shuang-pro
 */
@Schema(description = "用户 APP - AI 图片生成 Request VO")
@Data
public class AiImageGenerateReqVO {

    @Schema(description = "提示词", requiredMode = Schema.RequiredMode.REQUIRED, example = "一只可爱的橘猫在阳光下睡觉")
    @NotBlank(message = "提示词不能为空")
    private String prompt;

    @Schema(description = "参考图片URL（图生图时使用）", example = "https://example.com/image.jpg")
    private String referenceImageUrl;

    @Schema(description = "模型ID", example = "1")
    private Long modelId;

    @Schema(description = "图片宽度", example = "1024")
    private Integer width;

    @Schema(description = "图片高度", example = "1024")
    private Integer height;

    @Schema(description = "生成数量", example = "1")
    private Integer count;

    @Schema(description = "风格", example = "写实")
    private String style;

    @Schema(description = "负面提示词", example = "低质量,模糊")
    private String negativePrompt;

}