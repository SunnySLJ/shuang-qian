package cn.shuang.module.agency.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 代理用户信息 VO - 用户端
 *
 * @author shuang-pro
 */
@Schema(description = "用户端 - 代理用户信息")
@Data
public class AppAgencyUserVO {

    @Schema(description = "是否代理", example = "true")
    private Boolean agencyEnabled;

    @Schema(description = "代理等级", example = "1")
    private Integer level;

    @Schema(description = "累计获得积分", example = "10000")
    private Integer totalPoints;

    @Schema(description = "已分配积分", example = "5000")
    private Integer distributedPoints;

    @Schema(description = "可用积分", example = "5000")
    private Integer availablePoints;

    @Schema(description = "直推人数", example = "50")
    private Integer directInviteCount;

    @Schema(description = "团队总人数", example = "200")
    private Integer teamTotalCount;

    @Schema(description = "绑定邀请码", example = "INVITE123")
    private String inviteCode;

}
