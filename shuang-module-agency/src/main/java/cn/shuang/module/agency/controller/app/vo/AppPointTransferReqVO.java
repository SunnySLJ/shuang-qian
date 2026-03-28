package cn.shuang.module.agency.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 积分分配请求 VO - 用户端
 *
 * @author shuang-pro
 */
@Schema(description = "用户端 - 积分分配请求")
@Data
public class AppPointTransferReqVO {

    @Schema(description = "接收方用户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long toUserId;

    @Schema(description = "积分数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer pointAmount;

    @Schema(description = "描述", example = "给用户的积分奖励")
    private String description;

}
