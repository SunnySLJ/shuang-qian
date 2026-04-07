package cn.shuang.module.ai.controller.admin.content.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 内容生成 Request VO")
@Data
public class ContentGenerateReqVO {

    @Schema(description = "用户输入（提示词/文案）", example = "一只可爱的橘猫在阳光下睡觉")
    @NotEmpty(message = "用户输入不能为空")
    private String userInput;

    @Schema(description = "参考视频 URL（用于爆款拆解）", example = "https://...")
    private String referenceVideoUrl;

    @Schema(description = "输入图片 URL（用于图生视频）", example = "https://...")
    private String inputImageUrl;

    @Schema(description = "内容风格", example = "写实")
    private String style;

    @Schema(description = "行业分类", example = "电商")
    private String category;

    @Schema(description = "使用的模板 ID", example = "1024")
    private Long templateId;

    @Schema(description = "质量阈值 (0-100)", example = "80")
    private Integer qualityThreshold;
}
