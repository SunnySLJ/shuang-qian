package cn.shuang.module.agency.dal.mysql;

import cn.shuang.module.agency.dal.dataobject.CommissionRecordDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 佣金记录 Mapper
 *
 * @author shuang-pro
 */
@Mapper
public interface CommissionRecordMapper extends BaseMapper<CommissionRecordDO> {

    /**
     * 查询用户的佣金记录列表
     *
     * @param brokerageUserId 代理用户 ID
     * @param offset          偏移量
     * @param limit           限制数
     * @return 佣金记录列表
     */
    @Select("SELECT * FROM agency_commission_record WHERE brokerage_user_id = #{brokerageUserId} AND deleted = 0 " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<CommissionRecordDO> selectByBrokerageUserId(@Param("brokerageUserId") Long brokerageUserId,
                                                     @Param("offset") int offset,
                                                     @Param("limit") int limit);

    /**
     * 查询待结算的佣金记录
     *
     * @param brokerageUserId 代理用户 ID
     * @return 佣金记录列表
     */
    @Select("SELECT * FROM agency_commission_record WHERE brokerage_user_id = #{brokerageUserId} AND status = 0 AND deleted = 0")
    List<CommissionRecordDO> selectPendingByBrokerageUserId(@Param("brokerageUserId") Long brokerageUserId);

    /**
     * 统计累计佣金
     *
     * @param brokerageUserId 代理用户 ID
     * @return 累计佣金总额
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM agency_commission_record WHERE brokerage_user_id = #{brokerageUserId} AND status = 1 AND deleted = 0")
    Integer sumTotalCommission(@Param("brokerageUserId") Long brokerageUserId);

    /**
     * 根据订单号查询佣金记录
     *
     * @param bizOrderNo 业务订单号
     * @return 佣金记录
     */
    @Select("SELECT * FROM agency_commission_record WHERE biz_order_no = #{bizOrderNo} AND deleted = 0")
    CommissionRecordDO selectByBizOrderNo(@Param("bizOrderNo") String bizOrderNo);

    /**
     * 查询用户的上级代理佣金记录
     *
     * @param userId          用户 ID
     * @param brokerageUserId 代理用户 ID
     * @return 佣金记录列表
     */
    @Select("SELECT * FROM agency_commission_record WHERE user_id = #{userId} AND brokerage_user_id = #{brokerageUserId} AND deleted = 0 " +
            "ORDER BY create_time DESC LIMIT 10")
    List<CommissionRecordDO> selectByUserAndBrokerage(@Param("userId") Long userId,
                                                       @Param("brokerageUserId") Long brokerageUserId);

}
