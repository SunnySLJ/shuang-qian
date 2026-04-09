package cn.shuang.module.ai.controller.admin.inspiration.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 灵感案例 Response VO
 *
 * @author shuang-pro
 */
@Data
@Schema(description = "管理后台 - AI 灵感案例")
public class AiInspirationCaseRespVO {

    @Schema(description = "案例 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "类型：banana/veo/grok/seedance", requiredMode = Schema.RequiredMode.REQUIRED, example = "veo")
    private String type;

    @Schema(description = "分类 ID", example = "5")
    private Integer categoryId;

    @Schema(description = "案例标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "活字印刷")
    private String title;

    @Schema(description = "提示词内容", example = "广角镜头展现了一位神态安详的僧人...")
    private String content;

    @Schema(description = "封面图 URL", example = "https://cdn.fenshen123.com/80/ec1a53231ea9d09f085cf07d0cafc8.png")
    private String image;

    @Schema(description = "首帧图 URL", example = "https://cdn.fenshen123.com/80/ec1a53231ea9d09f085cf07d0cafc8.png")
    private String imageFirst;

    @Schema(description = "尾帧图 URL", example = "")
    private String imageTail;

    @Schema(description = "视频 URL", example = "https://cdn.fenshen123.com/5b/2f38a9503b4fb21d4c33a6b69a4cf4.mp4")
    private String videoUrl;

    @Schema(description = "视频时长（秒）", example = "8")
    private Integer duration;

    @Schema(description = "点赞数", example = "322")
    private Integer likeCount;

    @Schema(description = "浏览数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    private Integer viewCount;

    @Schema(description = "使用数", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private Integer useCount;

    @Schema(description = "标签", example = "威尔视频")
    private String label;

    @Schema(description = "图标 URL", example = "https://cdn.fenshen123.com/icons/grok.png")
    private String icon;

    @Schema(description = "是否精选", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean featured;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sortOrder;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-01-01 00:00:00")
    private LocalDateTime createTime;

}