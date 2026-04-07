package cn.shuang.module.agency.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 积分钱包 VO
 *
 * @author shuang-pro
 */
@Schema(description = "管理后台 - 积分钱包")
@Data
public class AgencyWalletVO {

    @Schema(description = "可用积分", example = "5000")
    private Integer balance;

    @Schema(description = "冻结积分", example = "0")
    private Integer frozenBalance;

    @Schema(description = "累计分配", example = "10000")
    private Integer totalDistributed;

    @Schema(description = "累计获得", example = "15000")
    private Integer totalReceived;

}
