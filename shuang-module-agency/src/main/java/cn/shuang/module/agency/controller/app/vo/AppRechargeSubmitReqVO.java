package cn.shuang.module.agency.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "用户 APP - 提交充值订单 Request VO")
@Data
public class AppRechargeSubmitReqVO {

    @Schema(description = "套餐 ID（使用套餐充值时传）")
    private Long packageId;

    @Schema(description = "自定义充值金额（元），使用自定义金额充值时传")
    @DecimalMin(value = "1", message = "充值金额最小为1元")
    private Double customAmount;

    @Schema(description = "支付渠道: wechat=微信支付, alipay=支付宝", required = true)
    @NotBlank(message = "支付渠道不能为空")
    private String channelCode;
}
