package cn.shuang.module.ai.controller.app.tutorial.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 教程视频分类响应 VO
 *
 * @author shuang-pro
 */
@Schema(description = "用户 APP - 教程视频分类 Response VO")
@Data
public class AppTutorialCategoryRespVO {

    @Schema(description = "分类 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "初级入门")
    private String name;

    @Schema(description = "分类描述", example = "新手入门教程，了解基础功能和使用方法")
    private String description;

    @Schema(description = "分类图标 URL", example = "https://example.com/icon.png")
    private String iconUrl;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "视频数量", example = "10")
    private Integer videoCount;

}