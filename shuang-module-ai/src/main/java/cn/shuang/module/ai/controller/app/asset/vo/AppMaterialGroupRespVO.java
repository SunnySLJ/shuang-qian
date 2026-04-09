package cn.shuang.module.ai.controller.app.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 素材分组响应 VO
 *
 * @author shuang-pro
 */
@Schema(description = "用户 APP - 素材分组 Response VO")
@Data
public class AppMaterialGroupRespVO {

    @Schema(description = "分组 ID", example = "1")
    private Long id;

    @Schema(description = "分组名称", example = "我的收藏")
    private String name;

    @Schema(description = "素材数量", example = "10")
    private Integer materialCount;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}