package cn.shuang.module.pay.dal.dataobject;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 积分钱包 DO
 *
 * @author shuang-pro
 */
@TableName("pay_wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WalletDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 可用余额（积分，单位：分）
     */
    private Integer balance;

    /**
     * 冻结余额（积分，单位：分）
     */
    private Integer frozenBalance;

    /**
     * 累计充值（积分，单位：分）
     */
    private Integer totalRecharge;

    /**
     * 累计消耗（积分，单位：分）
     */
    private Integer totalUsed;

    /**
     * 累计收到分配（积分，单位：分）
     */
    private Integer totalReceived;

    /**
     * 累计分配给出（积分，单位：分）
     */
    private Integer totalGiven;

    /**
     * 累计分成收入（积分，单位：分）
     */
    private Integer totalCommission;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

}
