package cn.shuang.module.agency.service;

import cn.shuang.module.agency.dal.dataobject.CommissionRecordDO;

import java.util.List;

/**
 * 佣金 Service 接口
 *
 * @author shuang-pro
 */
public interface CommissionService {

    /**
     * 计算充值分成
     * <p>
     * 当用户充值时，自动计算上级代理的分成
     *
     * @param userId        用户 ID
     * @param rechargeAmount 充值金额（单位：分）
     * @param bizOrderNo    业务订单号
     * @return 分成记录 ID
     */
    Long calculateRechargeCommission(Long userId, Integer rechargeAmount, String bizOrderNo);

    /**
     * 计算消费分成
     * <p>
     * 当用户消费积分时，自动计算上级代理的分成
     *
     * @param userId      用户 ID
     * @param consumeAmount 消费金额（单位：分）
     * @param bizOrderNo  业务订单号
     * @return 分成记录 ID
     */
    Long calculateConsumeCommission(Long userId, Integer consumeAmount, String bizOrderNo);

    /**
     * 获取用户的佣金记录列表
     *
     * @param brokerageUserId 代理用户 ID
     * @param page            页码
     * @param size            每页大小
     * @return 佣金记录列表
     */
    List<CommissionRecordDO> getCommissionRecords(Long brokerageUserId, Integer page, Integer size);

    /**
     * 获取用户的累计佣金
     *
     * @param brokerageUserId 代理用户 ID
     * @return 累计佣金总额（单位：分）
     */
    Integer getTotalCommission(Long brokerageUserId);

    /**
     * 获取待结算的佣金列表
     *
     * @param brokerageUserId 代理用户 ID
     * @return 佣金记录列表
     */
    List<CommissionRecordDO> getPendingCommissions(Long brokerageUserId);

    /**
     * 结算佣金
     * <p>
     * 将待结算的佣金标记为已结算，并增加到代理钱包
     *
     * @param recordId 佣金记录 ID
     * @return 是否成功
     */
    boolean settleCommission(Long recordId);

    /**
     * 批量结算佣金
     *
     * @param brokerageUserId 代理用户 ID
     * @return 结算的记录数
     */
    int settleAllCommissions(Long brokerageUserId);

    /**
     * 取消佣金记录
     *
     * @param recordId 佣金记录 ID
     * @return 是否成功
     */
    boolean cancelCommission(Long recordId);

}
