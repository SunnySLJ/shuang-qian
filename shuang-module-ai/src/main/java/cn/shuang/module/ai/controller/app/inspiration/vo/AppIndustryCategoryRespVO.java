package cn.shuang.module.ai.controller.app.inspiration.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 行业分类 Response VO
 *
 * @author shuang-pro
 */
@Schema(description = "用户 APP - 行业分类 Response VO")
@Data
public class AppIndustryCategoryRespVO {

    @Schema(description = "分类 ID", example = "5")
    private Integer id;

    @Schema(description = "分类名称", example = "电商")
    private String name;

    @Schema(description = "该分类下的案例数量", example = "10")
    private Integer caseCount;

    @Schema(description = "排序（越小越前）", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否有精选案例", example = "true")
    private Boolean hasFeatured;

}