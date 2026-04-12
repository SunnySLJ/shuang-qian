package cn.shuang.module.pay.service;

import cn.shuang.module.pay.dal.dataobject.WalletDO;
import cn.shuang.module.pay.dal.dataobject.WalletTransactionDO;

import java.util.List;

/**
 * 积分钱包 Service 接口
 *
 * @author shuang-pro
 */
public interface WalletService {

    /**
     * 获取用户钱包
     *
     * @param userId 用户 ID
     * @return 钱包信息，不存在则创建并返回
     */
    WalletDO getOrCreateWallet(Long userId);

    /**
     * 获取用户钱包（不自动创建）
     *
     * @param userId 用户 ID
     * @return 钱包信息，不存在返回 null
     */
    WalletDO getWallet(Long userId);

    /**
     * 增加积分
     *
     * @param userId      用户 ID
     * @param amount      积分金额（单位：分）
     * @param description 描述
     * @return 增加后的余额
     */
    Integer addPoints(Long userId, Integer amount, String description);

    /**
     * 增加积分（带业务类型）
     *
     * @param userId      用户 ID
     * @param amount      积分金额（单位：分）
     * @param bizType     业务类型
     * @param bizOrderNo  业务订单号
     * @param description 描述
     * @return 增加后的余额
     */
    Integer addPoints(Long userId, Integer amount, Integer bizType, String bizOrderNo, String description);

    /**
     * 扣减积分
     *
     * @param userId      用户 ID
     * @param amount      积分金额（单位：分）
     * @param description 描述
     * @return 扣减后的余额
     * @throws RuntimeException 积分不足时抛出
     */
    Integer deductPoints(Long userId, Integer amount, String description);

    /**
     * 扣减积分（带业务类型）
     *
     * @param userId      用户 ID
     * @param amount      积分金额（单位：分）
     * @param bizType     业务类型（负数）
     * @param bizOrderNo  业务订单号
     * @param description 描述
     * @return 扣减后的余额
     * @throws RuntimeException 积分不足时抛出
     */
    Integer deductPoints(Long userId, Integer amount, Integer bizType, String bizOrderNo, String description);

    /**
     * 检查积分是否足够
     *
     * @param userId        用户 ID
     * @param requiredAmount 需要的积分（单位：分）
     * @return true-足够，false-不足
     */
    boolean hasEnoughPoints(Long userId, Integer requiredAmount);

    /**
     * 获取积分流水列表
     *
     * @param userId 用户 ID
     * @param page   页码
     * @param size   每页大小
     * @return 流水列表
     */
    List<WalletTransactionDO> getTransactions(Long userId, Integer page, Integer size);

    /**
     * 冻结积分（用于待确认的交易）
     *
     * @param userId 用户 ID
     * @param amount 冻结金额（单位：分）
     * @return 冻结后的可用余额
     */
    Integer freezePoints(Long userId, Integer amount);

    /**
     * 解冻积分（交易取消）
     *
     * @param userId 用户 ID
     * @param amount 解冻金额（单位：分）
     * @return 解冻后的可用余额
     */
    Integer unfreezePoints(Long userId, Integer amount);

    /**
     * 获取钱包详情（包含流水统计）
     *
     * @param userId 用户 ID
     * @return 钱包详情
     */
    WalletDO getWalletDetail(Long userId);

    /**
     * 根据业务记录 ID 查询积分流水
     *
     * @param recordId 业务记录 ID（从 description 中提取）
     * @return 流水记录，不存在返回 null
     */
    WalletTransactionDO getTransactionByRecord(Long recordId);

    /**
     * 根据业务订单号查询积分流水（用于退款时找回原始扣减金额）
     *
     * @param bizOrderNo 业务订单号
     * @return 流水记录，不存在返回 null
     */
    WalletTransactionDO getTransactionByBizOrderNo(String bizOrderNo);

}
