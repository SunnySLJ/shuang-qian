package cn.shuang.module.pay.dal.mysql;

import cn.shuang.module.pay.dal.dataobject.WalletDO;
import cn.shuang.module.pay.dal.dataobject.WalletTransactionDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 积分钱包 Mapper（用于会员积分管理）
 *
 * @author shuang-pro
 */
@Mapper
@Repository("pointsWalletMapper")
public interface WalletMapper extends BaseMapper<WalletDO> {

    /**
     * 根据用户 ID 获取钱包
     *
     * @param userId 用户 ID
     * @return 钱包信息
     */
    @Select("SELECT * FROM pay_wallet WHERE user_id = #{userId} AND deleted = 0")
    WalletDO selectByUserId(@Param("userId") Long userId);

    /**
     * 创建钱包（如果不存在）
     *
     * @param userId 用户 ID
     * @return 影响行数
     */
    @Insert("INSERT INTO pay_wallet (user_id, balance, frozen_balance, total_recharge, total_used) " +
            "VALUES (#{userId}, 0, 0, 0, 0) " +
            "ON DUPLICATE KEY UPDATE user_id = user_id")
    int insertOrUpdate(@Param("userId") Long userId);

    /**
     * 更新余额（乐观锁）
     *
     * @param id         钱包 ID
     * @param balance    新余额
     * @param oldBalance 旧余额
     * @return 影响行数
     */
    @Update("UPDATE pay_wallet SET balance = #{balance}, update_time = NOW() " +
            "WHERE id = #{id} AND balance = #{oldBalance} AND deleted = 0")
    int updateBalance(@Param("id") Long id, @Param("balance") Integer balance, @Param("oldBalance") Integer oldBalance);

    /**
     * 增加余额
     *
     * @param userId 用户 ID
     * @param amount 金额
     * @return 影响行数
     */
    @Update("UPDATE pay_wallet SET balance = balance + #{amount}, total_received = total_received + #{amount}, update_time = NOW() " +
            "WHERE user_id = #{userId} AND deleted = 0")
    int addBalance(@Param("userId") Long userId, @Param("amount") Integer amount);

    /**
     * 扣减余额
     *
     * @param userId 用户 ID
     * @param amount 金额
     * @return 影响行数
     */
    @Update("UPDATE pay_wallet SET balance = balance - #{amount}, total_used = total_used + #{amount}, update_time = NOW() " +
            "WHERE user_id = #{userId} AND balance >= #{amount} AND deleted = 0")
    int deductBalance(@Param("userId") Long userId, @Param("amount") Integer amount);

    /**
     * 乐观锁扣减余额（推荐）
     *
     * @param userId 用户 ID
     * @param amount 金额
     * @param version 当前版本号
     * @return 影响行数，0 表示乐观锁冲突或余额不足
     */
    @Update("UPDATE pay_wallet SET balance = balance - #{amount}, total_used = total_used + #{amount}, version = version + 1, update_time = NOW() " +
            "WHERE user_id = #{userId} AND balance >= #{amount} AND version = #{version} AND deleted = 0")
    int deductBalanceWithVersion(@Param("userId") Long userId, @Param("amount") Integer amount, @Param("version") Integer version);

    /**
     * 查询流水列表
     *
     * @param userId 用户 ID
     * @param offset 偏移量
     * @param limit  限制数
     * @return 流水列表
     */
    @Select("SELECT * FROM pay_wallet_transaction WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<WalletTransactionDO> selectTransactions(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

}
