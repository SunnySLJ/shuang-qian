package cn.shuang.module.pay.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 支付订单提交 Response VO")
@Data
public class PayOrderSubmitRespVO {

    @Schema(description = "支付单编号")
    private Long orderId;

    @Schema(description = "支付单号")
    private String orderNo;

    @Schema(description = "支付状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "10") // 参见 PayOrderStatusEnum 枚举
    private Integer status;

    @Schema(description = "展示模式", requiredMode = Schema.RequiredMode.REQUIRED, example = "url") // 参见 PayDisplayModeEnum 枚举
    private String displayMode;
    @Schema(description = "展示内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayContent;

    @Schema(description = "支付渠道的额外参数（如下单参数）")
    private Map<String, String> channelExtras;

}
