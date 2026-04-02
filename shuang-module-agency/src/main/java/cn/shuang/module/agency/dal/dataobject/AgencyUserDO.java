package cn.shuang.module.agency.dal.dataobject;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 代理用户关系表
 *
 * @author shuang-pro
 */
@TableName("agency_user")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyUserDO extends BaseDO {

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
     * 上级代理用户 ID
     */
    private Long parentAgencyId;

    /**
     * 代理等级 (1:一级代理，2:二级代理)
     */
    private Integer level;

    /**
     * 是否代理 (0:否 1:是)
     */
    private Boolean agencyEnabled;

    /**
     * 绑定方式 (1:主动绑定 2:被动绑定)
     */
    private Integer bindMode;

    /**
     * 累计获得积分
     */
    private Integer totalPoints;

    /**
     * 已分配积分
     */
    private Integer distributedPoints;

    /**
     * 累计分成收入（积分，单位：分）
     */
    private Integer totalCommission;

    /**
     * 直推人数
     */
    private Integer directInviteCount;

    /**
     * 团队总人数
     */
    private Integer teamTotalCount;

    /**
     * 用户昵称（从 system_users 联查）
     */
    @TableField(exist = false)
    private String nickname;

    /**
     * 代理费（分）
     */
    private Integer agencyFee;

    /**
     * 代理费支付时间
     */
    private java.time.LocalDateTime payFeeTime;

}
