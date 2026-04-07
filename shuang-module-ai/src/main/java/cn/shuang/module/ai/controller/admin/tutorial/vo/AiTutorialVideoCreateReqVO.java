package cn.shuang.module.ai.controller.admin.tutorial.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 教程视频创建请求 VO
 *
 * @author shuang-pro
 */
@Schema(description = "管理后台 - 教程视频创建 Request VO")
@Data
public class AiTutorialVideoCreateReqVO {

    @Schema(description = "分类 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "分类 ID 不能为空")
    private Long categoryId;

    @Schema(description = "教程名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "燃动数字人")
    @NotBlank(message = "教程名称不能为空")
    private String name;

    @Schema(description = "封面图 URL", example = "https://cdn.fenshen123.com/66/bcad75fa67d3fdeccf01c6999d33e9.jpg")
    private String cover;

    @Schema(description = "视频 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://cdn.fenshen123.com/60/d36d954b524c511a9a155a8b4b3d32.mp4")
    @NotBlank(message = "视频 URL 不能为空")
    private String videoUrl;

    @Schema(description = "视频时长（秒）", example = "300")
    private Integer duration;

    @Schema(description = "是否免费观看", example = "true")
    private Boolean isFree;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

}