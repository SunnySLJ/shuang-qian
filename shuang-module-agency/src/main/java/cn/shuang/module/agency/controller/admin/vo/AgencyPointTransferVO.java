package cn.shuang.module.agency.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分分配记录 VO
 *
 * @author shuang-pro
 */
@Schema(description = "管理后台 - 积分分配记录")
@Data
public class AgencyPointTransferVO {

    @Schema(description = "主键 ID", example = "1")
    private Long id;

    @Schema(description = "分配方用户 ID", example = "100")
    private Long fromUserId;

    @Schema(description = "接收方用户 ID", example = "200")
    private Long toUserId;

    @Schema(description = "积分数量", example = "1000")
    private Integer pointAmount;

    @Schema(description = "描述", example = "奖励积分")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
