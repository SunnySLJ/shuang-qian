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
     * 根据订单号和代理用户ID查询佣金记录（幂等检查用）
     *
     * @param bizOrderNo      业务订单号
     * @param brokerageUserId 代理用户 ID
     * @return 佣金记录
     */
    @Select("SELECT * FROM agency_commission_record WHERE biz_order_no = #{bizOrderNo} AND brokerage_user_id = #{brokerageUserId} AND deleted = 0 LIMIT 1")
    CommissionRecordDO selectByBizOrderNoAndBrokerageUserId(@Param("bizOrderNo") String bizOrderNo,
                                                          @Param("brokerageUserId") Long brokerageUserId);

    /**
     * 根据订单号、代理用户ID和层级查询佣金记录（二级分佣幂等检查用）
     *
     * @param bizOrderNo      业务订单号
     * @param brokerageUserId 代理用户 ID
     * @param level           代理层级（1=一级代理，2=二级代理）
     * @return 佣金记录
     */
    @Select("SELECT * FROM agency_commission_record WHERE biz_order_no = #{bizOrderNo} AND brokerage_user_id = #{brokerageUserId} AND level = #{level} AND deleted = 0 LIMIT 1")
    CommissionRecordDO selectByBizOrderNoAndLevel(@Param("bizOrderNo") String bizOrderNo,
                                                 @Param("brokerageUserId") Long brokerageUserId,
                                                 @Param("level") Integer level);

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
