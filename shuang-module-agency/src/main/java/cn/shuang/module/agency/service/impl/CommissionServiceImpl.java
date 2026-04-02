package cn.shuang.module.agency.service.impl;

import cn.shuang.module.agency.dal.dataobject.AgencyUserDO;
import cn.shuang.module.agency.dal.dataobject.CommissionRecordDO;
import cn.shuang.module.agency.dal.mysql.AgencyUserMapper;
import cn.shuang.module.agency.dal.mysql.CommissionRecordMapper;
import cn.shuang.module.agency.enums.AgencyLevelEnum;
import cn.shuang.module.agency.enums.CommissionBizTypeEnum;
import cn.shuang.module.agency.service.CommissionService;
import cn.shuang.module.pay.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 佣金 Service 实现类
 *
 * @author shuang-pro
 */
@Service
@Slf4j
public class CommissionServiceImpl implements CommissionService {

    @Resource
    private AgencyUserMapper agencyUserMapper;

    @Resource
    private CommissionRecordMapper commissionRecordMapper;

    @Resource
    private WalletService walletService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long calculateRechargeCommission(Long userId, Integer rechargeAmount, String bizOrderNo) {
        log.info("[CommissionService] 开始计算充值分成 - userId: {}, amount: {}, orderNo: {}", userId, rechargeAmount, bizOrderNo);

        // 1. 查询用户的代理关系
        AgencyUserDO user = agencyUserMapper.selectByUserId(userId);
        if (user == null || user.getParentAgencyId() == null) {
            log.info("[CommissionService] 用户无上级代理，跳过分成 - userId: {}", userId);
            return null;
        }

        // 2. 查询上级代理信息
        AgencyUserDO agent = agencyUserMapper.selectById(user.getParentAgencyId());
        if (agent == null || !agent.getAgencyEnabled()) {
            log.info("[CommissionService] 上级代理不存在或未启用 - parentAgencyId: {}", user.getParentAgencyId());
            return null;
        }

        // 3. 获取分成比例
        Integer commissionRate = AgencyLevelEnum.getCommissionRateByLevel(agent.getLevel());
        if (commissionRate == null || commissionRate <= 0) {
            log.warn("[CommissionService] 代理等级无效 - level: {}", agent.getLevel());
            return null;
        }

        // 4. 计算分成金额（万分比）
        Integer commissionAmount = rechargeAmount * commissionRate / 10000;
        if (commissionAmount <= 0) {
            log.info("[CommissionService] 分成金额为 0，跳过 - rate: {}, amount: {}", commissionRate, rechargeAmount);
            return null;
        }

        // 5. 创建佣金记录
        CommissionRecordDO record = CommissionRecordDO.builder()
                .userId(userId)
                .brokerageUserId(agent.getUserId())
                .bizType(CommissionBizTypeEnum.RECHARGE.getType())
                .bizOrderNo(bizOrderNo)
                .amount(commissionAmount)
                .commissionRate(commissionRate)
                .status(1) // 直接标记为已结算
                .settleTime(LocalDateTime.now())
                .remark("充值分成 - 等级：" + agent.getLevel() + "，比例：" + (commissionRate / 100.0) + "%")
                .build();
        commissionRecordMapper.insert(record);

        // 6. 增加代理钱包积分
        try {
            walletService.addPoints(agent.getUserId(), commissionAmount, 3, bizOrderNo,
                    String.format("下级充值分成 - 订单：%s", bizOrderNo));
            log.info("[CommissionService] 充值分成成功 - agentId: {}, amount: {}, balance: {}",
                    agent.getUserId(), commissionAmount, commissionAmount);
        } catch (Exception e) {
            log.error("[CommissionService] 增加钱包积分失败 - agentId: {}, amount: {}", agent.getUserId(), commissionAmount, e);
            // 回滚佣金记录状态
            record.setStatus(2); // 已取消
            commissionRecordMapper.updateById(record);
            throw e;
        }

        log.info("[CommissionService] 充值分成完成 - userId: {}, agentId: {}, amount: {}, rate: {}",
                userId, agent.getUserId(), commissionAmount, commissionRate);

        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long calculateConsumeCommission(Long userId, Integer consumeAmount, String bizOrderNo) {
        log.info("[CommissionService] 开始计算消费分成 - userId: {}, amount: {}, orderNo: {}", userId, consumeAmount, bizOrderNo);

        // 1. 查询用户的代理关系
        AgencyUserDO user = agencyUserMapper.selectByUserId(userId);
        if (user == null || user.getParentAgencyId() == null) {
            log.info("[CommissionService] 用户无上级代理，跳过分成 - userId: {}", userId);
            return null;
        }

        // 2. 查询上级代理信息
        AgencyUserDO agent = agencyUserMapper.selectById(user.getParentAgencyId());
        if (agent == null || !agent.getAgencyEnabled()) {
            log.info("[CommissionService] 上级代理不存在或未启用 - parentAgencyId: {}", user.getParentAgencyId());
            return null;
        }

        // 3. 获取分成比例（消费分成使用相同的比例）
        Integer commissionRate = AgencyLevelEnum.getCommissionRateByLevel(agent.getLevel());
        if (commissionRate == null || commissionRate <= 0) {
            log.warn("[CommissionService] 代理等级无效 - level: {}", agent.getLevel());
            return null;
        }

        // 4. 计算分成金额（消费分成按消费金额的一定比例）
        // 注意：这里是按消费积分的百分比返还给代理
        Integer commissionAmount = consumeAmount * commissionRate / 10000;
        if (commissionAmount <= 0) {
            log.info("[CommissionService] 分成金额为 0，跳过 - rate: {}, amount: {}", commissionRate, consumeAmount);
            return null;
        }

        // 5. 创建佣金记录
        CommissionRecordDO record = CommissionRecordDO.builder()
                .userId(userId)
                .brokerageUserId(agent.getUserId())
                .bizType(CommissionBizTypeEnum.CONSUME.getType())
                .bizOrderNo(bizOrderNo)
                .amount(commissionAmount)
                .commissionRate(commissionRate)
                .status(1) // 直接标记为已结算
                .settleTime(LocalDateTime.now())
                .remark("消费分成 - 等级：" + agent.getLevel() + "，比例：" + (commissionRate / 100.0) + "%")
                .build();
        commissionRecordMapper.insert(record);

        // 6. 增加代理钱包积分
        try {
            walletService.addPoints(agent.getUserId(), commissionAmount, 3, bizOrderNo,
                    String.format("下级消费分成 - 订单：%s", bizOrderNo));
            log.info("[CommissionService] 消费分成成功 - agentId: {}, amount: {}", agent.getUserId(), commissionAmount);
        } catch (Exception e) {
            log.error("[CommissionService] 增加钱包积分失败 - agentId: {}, amount: {}", agent.getUserId(), commissionAmount, e);
            // 回滚佣金记录状态
            record.setStatus(2); // 已取消
            commissionRecordMapper.updateById(record);
            throw e;
        }

        // 7. 更新代理累计佣金
        agencyUserMapper.addTotalCommission(agent.getId(), commissionAmount);

        log.info("[CommissionService] 消费分成完成 - userId: {}, agentId: {}, amount: {}, rate: {}",
                userId, agent.getUserId(), commissionAmount, commissionRate);

        return record.getId();
    }

    @Override
    public List<CommissionRecordDO> getCommissionRecords(Long brokerageUserId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return commissionRecordMapper.selectByBrokerageUserId(brokerageUserId, offset, size);
    }

    @Override
    public Integer getTotalCommission(Long brokerageUserId) {
        AgencyUserDO agent = agencyUserMapper.selectByUserId(brokerageUserId);
        if (agent == null) {
            return 0;
        }
        // 从记录中统计
        return commissionRecordMapper.sumTotalCommission(brokerageUserId);
    }

    @Override
    public List<CommissionRecordDO> getPendingCommissions(Long brokerageUserId) {
        return commissionRecordMapper.selectPendingByBrokerageUserId(brokerageUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean settleCommission(Long recordId) {
        CommissionRecordDO record = commissionRecordMapper.selectById(recordId);
        if (record == null || record.getStatus() != 0) {
            log.warn("[CommissionService] 佣金记录不存在或已结算 - recordId: {}", recordId);
            return false;
        }

        // 1. 更新状态为已结算
        record.setStatus(1);
        record.setSettleTime(LocalDateTime.now());
        commissionRecordMapper.updateById(record);

        // 2. 增加代理钱包积分
        walletService.addPoints(record.getBrokerageUserId(), record.getAmount(), 3, record.getBizOrderNo(),
                String.format("佣金结算 - 记录 ID: %d", recordId));

        log.info("[CommissionService] 佣金结算成功 - recordId: {}, amount: {}", recordId, record.getAmount());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int settleAllCommissions(Long brokerageUserId) {
        List<CommissionRecordDO> pendingRecords = getPendingCommissions(brokerageUserId);
        if (pendingRecords.isEmpty()) {
            log.info("[CommissionService] 无待结算佣金 - userId: {}", brokerageUserId);
            return 0;
        }

        int settledCount = 0;
        for (CommissionRecordDO record : pendingRecords) {
            try {
                if (settleCommission(record.getId())) {
                    settledCount++;
                }
            } catch (Exception e) {
                log.error("[CommissionService] 结算单条佣金失败 - recordId: {}", record.getId(), e);
                // 继续结算其他记录
            }
        }

        log.info("[CommissionService] 批量结算完成 - userId: {}, settled: {}", brokerageUserId, settledCount);
        return settledCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelCommission(Long recordId) {
        CommissionRecordDO record = commissionRecordMapper.selectById(recordId);
        if (record == null || record.getStatus() != 0) {
            log.warn("[CommissionService] 佣金记录不存在或已处理 - recordId: {}", recordId);
            return false;
        }

        // 更新状态为已取消
        record.setStatus(2);
        commissionRecordMapper.updateById(record);

        log.info("[CommissionService] 佣金记录已取消 - recordId: {}", recordId);
        return true;
    }

}
