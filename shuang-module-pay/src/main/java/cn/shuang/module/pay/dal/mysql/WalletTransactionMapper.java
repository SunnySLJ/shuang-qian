package cn.shuang.module.pay.dal.mysql;

import cn.shuang.module.pay.dal.dataobject.WalletTransactionDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 积分流水 Mapper
 *
 * @author shuang-pro
 */
@Mapper
@Repository("walletTransactionMapper")
public interface WalletTransactionMapper extends BaseMapper<WalletTransactionDO> {

    /**
     * 插入流水记录
     *
     * @param transaction 流水记录
     * @return 影响行数
     */
    @Insert("INSERT INTO pay_wallet_transaction (wallet_id, user_id, biz_type, biz_order_no, amount, balance_after, description, extra_data) " +
            "VALUES (#{walletId}, #{userId}, #{bizType}, #{bizOrderNo}, #{amount}, #{balanceAfter}, #{description}, #{extraData})")
    int insertTransaction(WalletTransactionDO transaction);

    /**
     * 根据 bizOrderNo 查询流水
     *
     * @param bizOrderNo 业务订单号
     * @return 流水记录
     */
    @Select("SELECT * FROM pay_wallet_transaction WHERE biz_order_no = #{bizOrderNo} AND deleted = 0 LIMIT 1")
    WalletTransactionDO selectByBizOrderNo(@Param("bizOrderNo") String bizOrderNo);

}
