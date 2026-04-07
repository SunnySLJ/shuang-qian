package cn.shuang.module.ai.controller.admin.inspiration.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

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

    @Schema(description = "行业分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "电商")
    private String category;

    @Schema(description = "案例标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "电商产品主图设计")
    private String title;

    @Schema(description = "案例描述", example = "适用于淘宝/拼多多/抖音小店商品主图")
    private String description;

    @Schema(description = "封面图 URL", example = "https://example.com/cover.jpg")
    private String coverImageUrl;

    @Schema(description = "演示视频 URL", example = "https://example.com/demo.mp4")
    private String videoUrl;

    @Schema(description = "提示词模板", example = "一个精致的产品展示台，专业摄影灯光，4k 画质")
    private String promptTemplate;

    @Schema(description = "浏览次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer viewCount;

    @Schema(description = "使用次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private Integer useCount;

    @Schema(description = "是否精选", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean featured;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sortOrder;

    @Schema(description = "扩展数据")
    private Map<String, Object> extraData;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-01-01 00:00:00")
    private LocalDateTime createTime;

}
