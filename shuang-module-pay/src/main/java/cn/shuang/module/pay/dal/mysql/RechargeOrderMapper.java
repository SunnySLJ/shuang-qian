package cn.shuang.module.pay.dal.mysql;

import cn.shuang.module.pay.dal.dataobject.RechargeOrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 充值订单 Mapper
 *
 * @author shuang-pro
 */
@Mapper
public interface RechargeOrderMapper extends BaseMapper<RechargeOrderDO> {

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 充值订单
     */
    @Select("SELECT * FROM pay_recharge_order WHERE order_no = #{orderNo} AND deleted = 0")
    RechargeOrderDO selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户 ID 查询订单列表
     *
     * @param userId 用户 ID
     * @param offset 偏移量
     * @param limit  限制数
     * @return 充值订单列表
     */
    @Select("SELECT * FROM pay_recharge_order WHERE user_id = #{userId} AND deleted = 0 " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<RechargeOrderDO> selectByUserId(@Param("userId") Long userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    /**
     * 查询待支付的订单（未过期）
     *
     * @param userId 用户 ID
     * @return 充值订单列表
     */
    @Select("SELECT * FROM pay_recharge_order WHERE user_id = #{userId} AND status = 0 " +
            "AND (expired_time IS NULL OR expired_time > NOW()) AND deleted = 0")
    List<RechargeOrderDO> selectPendingOrders(@Param("userId") Long userId);

    /**
     * 根据交易流水号查询订单
     *
     * @param transactionId 第三方支付流水号
     * @return 充值订单
     */
    @Select("SELECT * FROM pay_recharge_order WHERE transaction_id = #{transactionId} AND deleted = 0")
    RechargeOrderDO selectByTransactionId(@Param("transactionId") String transactionId);

    /**
     * 更新订单状态
     *
     * @param id            订单 ID
     * @param status        新状态
     * @param transactionId 支付交易流水号
     * @param paidTime      支付时间
     * @return 受影响的行数
     */
    @Update("UPDATE pay_recharge_order SET status = #{status}, transaction_id = #{transactionId}, " +
            "paid_time = #{paidTime}, update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int updateStatus(@Param("id") Long id,
                     @Param("status") Integer status,
                     @Param("transactionId") String transactionId,
                     @Param("paidTime") LocalDateTime paidTime);

    /**
     * 关闭过期订单
     *
     * @param expiredTime 过期时间
     * @return 受影响的行数
     */
    @Update("UPDATE pay_recharge_order SET status = 2, update_time = NOW() " +
            "WHERE status = 0 AND expired_time < #{expiredTime} AND deleted = 0")
    int closeExpiredOrders(@Param("expiredTime") LocalDateTime expiredTime);

}
