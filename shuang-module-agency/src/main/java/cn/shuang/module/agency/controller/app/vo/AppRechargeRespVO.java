package cn.shuang.module.agency.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 APP - 积分充值 Response VO")
@Data
public class AppRechargeRespVO {

    @Schema(description = "可用套餐列表")
    private List<RechargePackageVO> packages;

    @Schema(description = "当前充值比例（元 -> 积分）")
    private Integer pricePerYuan;

    @Schema(description = "最小充值金额（元）")
    private Double minAmount;

    @Schema(description = "最大充值金额（元）")
    private Double maxAmount;

    @Schema(description = "支持的支付方式: wechat=微信, alipay=支付宝")
    private List<String> supportedChannels;

    @Data
    public static class RechargePackageVO {
        @Schema(description = "套餐 ID")
        private Long id;
        @Schema(description = "价格（元）")
        private Double price;
        @Schema(description = "基础积分")
        private Integer points;
        @Schema(description = "赠送积分")
        private Integer bonus;
        @Schema(description = "套餐名称")
        private String name;
    }
}
