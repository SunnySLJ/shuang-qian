package cn.shuang.module.agency.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 代理用户 VO
 *
 * @author shuang-pro
 */
@Schema(description = "管理后台 - 代理用户")
@Data
public class AgencyUserVO {

    @Schema(description = "主键 ID", example = "1")
    private Long id;

    @Schema(description = "用户 ID", example = "100")
    private Long userId;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "代理等级", example = "1")
    private Integer level;

    @Schema(description = "是否代理", example = "true")
    private Boolean agencyEnabled;

    @Schema(description = "直推人数", example = "50")
    private Integer directInviteCount;

    @Schema(description = "累计获得积分", example = "10000")
    private Integer totalPoints;

    @Schema(description = "已分配积分", example = "5000")
    private Integer distributedPoints;

}
