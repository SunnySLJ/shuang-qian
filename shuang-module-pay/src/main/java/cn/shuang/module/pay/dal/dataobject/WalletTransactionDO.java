package cn.shuang.module.pay.dal.dataobject;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 积分流水 DO
 *
 * @author shuang-pro
 */
@TableName("pay_wallet_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WalletTransactionDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 钱包 ID
     */
    private Long walletId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 业务类型
     * 收入类（正数）: 1-充值获得，2-代理分配，3-推广分成，4-奖励赠送，5-退款退回
     * 支出类（负数）: -1-AI 生图，-2-AI 文生视频，-3-AI 图生视频，-4-黄金 6 秒，-5-AI 混剪，-6~ -8 视频拆解
     */
    private Integer bizType;

    /**
     * 业务订单号
     */
    private String bizOrderNo;

    /**
     * 变动金额（正数增加，负数减少，单位：分）
     */
    private Integer amount;

    /**
     * 变动后余额（单位：分）
     */
    private Integer balanceAfter;

    /**
     * 描述
     */
    private String description;

    /**
     * 扩展数据
     */
    private String extraData;

}
