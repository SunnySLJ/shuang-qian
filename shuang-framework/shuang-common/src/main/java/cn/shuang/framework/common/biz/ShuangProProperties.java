package cn.shuang.framework.common.biz;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 追梦 Dream 业务配置项
 * <p>
 * 包含：积分消耗规则、充值套餐、代理分佣、支付配置、短信配置
 * <p>
 * 配置前缀：yudao.shuang-pro
 *
 * @author shuang-pro
 */
@ConfigurationProperties(prefix = "yudao.shuang-pro")
@Data
@Validated
public class ShuangProProperties {

    // ========== 积分消耗规则 ==========

    /**
     * 积分消耗配置
     */
    @NotNull
    private PointsConfig points = new PointsConfig();

    @Data
    public static class PointsConfig {
        /** 图片生成消耗积分 */
        private Integer imageCost = 2;
        /** 5秒视频生成消耗积分 */
        private Integer video5sCost = 20;
        /** 15秒视频生成消耗积分 */
        private Integer video15sCost = 40;
        /** 30秒视频生成消耗积分 */
        private Integer video30sCost = 60;
        /** 视频拆解消耗积分 */
        private Integer videoAnalyzeCost = 10;
    }

    // ========== 充值套餐配置 ==========

    /**
     * 充值配置
     */
    @NotNull
    private RechargeConfig recharge = new RechargeConfig();

    @Data
    public static class RechargeConfig {
        /** 每元可购积分数量（默认 3:1） */
        private Integer pricePerYuan = 3;
        /** 套餐列表 */
        private List<RechargePackage> packages = new ArrayList<>();
    }

    @Data
    public static class RechargePackage {
        /** 套餐 ID */
        private Long id;
        /** 价格（元） */
        private Double price;
        /** 基础积分 */
        private Integer points;
        /** 赠送积分 */
        private Integer bonus = 0;
    }

    // ========== 代理分佣配置 ==========

    /**
     * 代理配置
     */
    @NotNull
    private AgencyConfig agency = new AgencyConfig();

    @Data
    public static class AgencyConfig {
        /** 一级代理分佣比例（百分比） */
        private Integer level1CommissionRate = 20;
        /** 二级代理分佣比例（百分比） */
        private Integer level2CommissionRate = 8;
        /** 成为一级代理条件 */
        private LevelConditions level1Conditions = new LevelConditions();
        /** 积分分配配置 */
        private PointTransferConfig pointTransfer = new PointTransferConfig();
    }

    @Data
    public static class LevelConditions {
        /** 直推人数达到多少人 */
        private Integer directInviteCount = 100;
        /** 累计充值达到多少元 */
        private Integer totalRecharge = 999;
    }

    @Data
    public static class PointTransferConfig {
        /** 每次最小分配积分 */
        private Integer minAmount = 10;
        /** 最大分配比例（占可分配积分的百分比） */
        private Integer maxRatio = 100;
    }

    // ========== 支付配置 ==========

    /**
     * 支付配置
     */
    @NotNull
    private PayConfig pay = new PayConfig();

    @Data
    public static class PayConfig {
        /** 微信支付配置 */
        private PayChannelConfig wechat = new PayChannelConfig();
        /** 支付宝配置 */
        private PayChannelConfig alipay = new PayChannelConfig();
    }

    @Data
    public static class PayChannelConfig {
        private String appId;
        private String mchId;
        private String apiKey;
        private String privateKey;
        private String alipayPublicKey;
        private String certPath;
        private String notifyUrl;
    }

    // ========== 短信配置 ==========

    /**
     * 短信配置
     */
    @NotNull
    private SmsConfig sms = new SmsConfig();

    @Data
    public static class SmsConfig {
        /** 开发环境是否开启假验证码 */
        private Boolean mockEnable = true;
        /** 假验证码（开发环境使用） */
        private String mockCode = "999999";
    }
}
