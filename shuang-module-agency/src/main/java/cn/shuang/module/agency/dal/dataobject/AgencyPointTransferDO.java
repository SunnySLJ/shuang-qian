package cn.shuang.module.agency.dal.dataobject;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 积分分配记录表
 *
 * @author shuang-pro
 */
@TableName("agency_point_transfer")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyPointTransferDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分配方用户 ID (代理)
     */
    private Long fromUserId;

    /**
     * 接收方用户 ID
     */
    private Long toUserId;

    /**
     * 积分数量
     */
    private Integer pointAmount;

    /**
     * 关联订单 ID (如果有)
     */
    private Long orderId;

    /**
     * 描述
     */
    private String description;

    /**
     * 业务类型 (1:充值分成 2:手动分配 3:退款扣回)
     */
    private Integer bizType;

}
