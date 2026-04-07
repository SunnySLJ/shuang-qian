package cn.shuang.module.ai.controller.admin.tutorial.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 教程视频分类创建请求 VO
 *
 * @author shuang-pro
 */
@Schema(description = "管理后台 - 教程视频分类创建 Request VO")
@Data
public class AiTutorialCategoryCreateReqVO {

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "初级入门")
    @NotBlank(message = "分类名称不能为空")
    private String name;

    @Schema(description = "分类描述", example = "新手入门教程")
    private String description;

    @Schema(description = "分类图标 URL", example = "https://example.com/icon.png")
    private String iconUrl;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

}