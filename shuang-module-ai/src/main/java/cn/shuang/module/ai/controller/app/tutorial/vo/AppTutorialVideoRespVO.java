package cn.shuang.module.ai.controller.app.tutorial.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教程视频响应 VO
 *
 * @author shuang-pro
 */
@Schema(description = "用户 APP - 教程视频 Response VO")
@Data
public class AppTutorialVideoRespVO {

    @Schema(description = "视频 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "分类 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "初级入门")
    private String categoryName;

    @Schema(description = "教程名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "燃动数字人")
    private String name;

    @Schema(description = "封面图 URL", example = "https://cdn.fenshen123.com/66/bcad75fa67d3fdeccf01c6999d33e9.jpg")
    private String cover;

    @Schema(description = "视频 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://cdn.fenshen123.com/60/d36d954b524c511a9a155a8b4b3d32.mp4")
    private String videoUrl;

    @Schema(description = "视频时长（秒）", example = "300")
    private Integer duration;

    @Schema(description = "视频时长格式化（分:秒）", example = "05:00")
    private String durationFormatted;

    @Schema(description = "观看次数", example = "1000")
    private Integer viewCount;

    @Schema(description = "点赞次数", example = "50")
    private Integer likeCount;

    @Schema(description = "是否免费观看", example = "true")
    private Boolean isFree;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}