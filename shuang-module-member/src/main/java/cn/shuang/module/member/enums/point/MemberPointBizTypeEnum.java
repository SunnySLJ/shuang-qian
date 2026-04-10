package cn.shuang.module.member.enums.point;

import cn.hutool.core.util.EnumUtil;
import cn.shuang.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 会员积分的业务类型枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum MemberPointBizTypeEnum implements ArrayValuable<Integer> {

    SIGN(1, "签到", "签到获得 {} 积分", true),
    ADMIN(2, "管理员修改", "管理员修改 {} 积分", true),

    ORDER_USE(11, "订单积分抵扣", "下单使用 {} 积分", false), // 下单时，扣减积分
    ORDER_USE_CANCEL(12, "订单积分抵扣（整单取消）", "订单取消，退还 {} 积分", true), // ORDER_USE 的取消
    ORDER_USE_CANCEL_ITEM(13, "订单积分抵扣（单个退款）", "订单退款，退还 {} 积分", true), // ORDER_USE 的取消

    ORDER_GIVE(21, "订单积分奖励", "下单获得 {} 积分", true), // 支付订单时，赠送积分
    ORDER_GIVE_CANCEL(22, "订单积分奖励（整单取消）", "订单取消，退还 {} 积分", false), // ORDER_GIVE 的取消
    ORDER_GIVE_CANCEL_ITEM(23, "订单积分奖励（单个退款）", "订单退款，扣除赠送的 {} 积分", false), // ORDER_GIVE 的取消

    // ========== AI 相关积分 ==========
    AI_IMAGE(31, "AI 图片生成", "AI 图片生成消耗 {} 积分", false),
    AI_VIDEO(32, "AI 视频生成", "AI 视频生成消耗 {} 积分", false),
    VIDEO_ANALYZE(33, "视频拆解", "视频拆解消耗 {} 积分", false),

    // ========== 代理相关积分 ==========
    RECHARGE(41, "积分充值", "积分充值获得 {} 积分", true),
    AGENCY_COMMISSION(42, "代理分佣", "代理分佣获得 {} 积分", true),
    AGENCY_TRANSFER_IN(43, "积分转入", "获得 {} 积分转入", true),
    AGENCY_TRANSFER_OUT(44, "积分转出", "转出 {} 积分", false)
    ;

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 名字
     */
    private final String name;
    /**
     * 描述
     */
    private final String description;
    /**
     * 是否为扣减积分
     */
    private final boolean add;

    @Override
    public Integer[] array() {
        return new Integer[0];
    }

    public static MemberPointBizTypeEnum getByType(Integer type) {
        return EnumUtil.getBy(MemberPointBizTypeEnum.class,
                e -> Objects.equals(type, e.getType()));
    }

}
