package cn.shuang.module.ai.controller.admin.inspiration.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * AI 灵感案例更新 Request VO
 *
 * @author shuang-pro
 */
@Data
public class AiInspirationCaseUpdateReqVO {

    @Schema(description = "案例 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "案例 ID 不能为空")
    private Long id;

    @Schema(description = "行业分类", example = "电商")
    private String category;

    @Schema(description = "案例标题", example = "电商产品主图设计")
    private String title;

    @Schema(description = "案例描述", example = "适用于淘宝/拼多多/抖音小店商品主图")
    private String description;

    @Schema(description = "封面图 URL", example = "https://example.com/cover.jpg")
    private String coverImageUrl;

    @Schema(description = "演示视频 URL", example = "https://example.com/demo.mp4")
    private String videoUrl;

    @Schema(description = "提示词模板", example = "一个精致的产品展示台，专业摄影灯光，4k 画质")
    private String promptTemplate;

    @Schema(description = "是否精选", example = "true")
    private Boolean featured;

    @Schema(description = "排序（越小越前）", example = "1")
    private Integer sortOrder;

}
