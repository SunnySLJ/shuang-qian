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

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 佣金 Service 实现类
 *
 * @author shuang-pro
 */
@Service
@Slf4j
public class CommissionServiceImpl implements CommissionService {

    private static final int MAX_PARENT_CHAIN_DEPTH = 2;

    @Resource
    private AgencyUserMapper agencyUserMapper;

    @Resource
    private CommissionRecordMapper commissionRecordMapper;

    @Resource
    private WalletService walletService;

    /**
     * 构建上级代理链（最多 N 级）
     *
     * @param startParentId 起始上级 ID（当前用户的直接上级 ID）
     * @param maxDepth     最大深度
     * @return 按层级顺序排列的上级代理列表（index 0 = 一级代理，index 1 = 二级代理）
     */
    private List<AgencyUserDO> buildParentChain(Long startParentId, int maxDepth) {
        List<AgencyUserDO> chain = new ArrayList<>();
        Long currentParentId = startParentId;
        for (int i = 0; i < maxDepth; i++) {
            if (currentParentId == null) break;
            AgencyUserDO parent = agencyUserMapper.selectById(currentParentId);
            if (parent == null) break;
            chain.add(parent);
            currentParentId = parent.getParentAgencyId();
        }
        return chain;
    }

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

        // 2. 构建上级代理链（最多支持两级）
        List<AgencyUserDO> chain = buildParentChain(user.getParentAgencyId(), MAX_PARENT_CHAIN_DEPTH);
        if (chain.isEmpty()) {
            log.info("[CommissionService] 上级代理链为空，跳过分成 - userId: {}", userId);
            return null;
        }

        Long lastRecordId = null;
        // 3. 遍历每一级代理，分别计算分佣
        for (int i = 0; i < chain.size(); i++) {
            AgencyUserDO agent = chain.get(i);
            if (agent == null || !agent.getAgencyEnabled()) {
                log.info("[CommissionService] 第{}级代理不存在或未启用，跳过", i + 1);
                continue;
            }

            // 获取分佣比例（一级代理按自己的等级比例，间接上级按二级分佣比例）
            Integer commissionRate = i == 0
                    ? AgencyLevelEnum.getCommissionRateByLevel(agent.getLevel())
                    : AgencyLevelEnum.getSecondaryCommissionRate();
            if (commissionRate == null || commissionRate <= 0) {
                log.info("[CommissionService] 第{}级代理分佣比例为0，跳过 - agentId: {}", i + 1, agent.getUserId());
                continue;
            }

            // 计算分成金额（万分比）
            Integer commissionAmount = rechargeAmount * commissionRate / 10000;
            if (commissionAmount <= 0) {
                log.info("[CommissionService] 第{}级代理分成金额为0，跳过 - rate: {}", i + 1, commissionRate);
                continue;
            }

            // 幂等检查（同一订单号 + 同一代理 + 同一层级）
            Integer level = i + 1;
            CommissionRecordDO existing = commissionRecordMapper.selectByBizOrderNoAndLevel(
                    bizOrderNo, agent.getUserId(), level);
            if (existing != null) {
                log.info("[CommissionService] 第{}级代理已分佣，跳过 - orderNo: {}, agentId: {}",
                        level, bizOrderNo, agent.getUserId());
                lastRecordId = existing.getId();
                continue;
            }

            // 创建佣金记录
            String remark = i == 0
                    ? "充值分成-一级代理，比例：" + (commissionRate / 100.0) + "%"
                    : "充值分成-二级代理(间接上级)，比例：" + (commissionRate / 100.0) + "%";
            CommissionRecordDO record = CommissionRecordDO.builder()
                    .userId(userId)
                    .brokerageUserId(agent.getUserId())
                    .bizType(CommissionBizTypeEnum.RECHARGE.getType())
                    .bizOrderNo(bizOrderNo)
                    .amount(commissionAmount)
                    .commissionRate(commissionRate)
                    .level(level)
                    .status(1)
                    .settleTime(LocalDateTime.now())
                    .remark(remark)
                    .build();
            commissionRecordMapper.insert(record);
            lastRecordId = record.getId();

            // 增加代理钱包积分
            try {
                walletService.addPoints(agent.getUserId(), commissionAmount, 3, bizOrderNo,
                        String.format("下级充值分成(第%d级) - 订单：%s", level, bizOrderNo));
                log.info("[CommissionService] 第{}级代理充值分成成功 - agentId: {}, amount: {}",
                        level, agent.getUserId(), commissionAmount);
            } catch (Exception e) {
                log.error("[CommissionService] 增加钱包积分失败 - agentId: {}, amount: {}",
                        agent.getUserId(), commissionAmount, e);
                record.setStatus(2);
                commissionRecordMapper.updateById(record);
                throw e;
            }
        }

        log.info("[CommissionService] 充值分成完成 - userId: {}, orderNo: {}", userId, bizOrderNo);
        return lastRecordId;
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

        // 2. 构建上级代理链（最多支持两级）
        List<AgencyUserDO> chain = buildParentChain(user.getParentAgencyId(), MAX_PARENT_CHAIN_DEPTH);
        if (chain.isEmpty()) {
            log.info("[CommissionService] 上级代理链为空，跳过分成 - userId: {}", userId);
            return null;
        }

        Long lastRecordId = null;
        // 3. 遍历每一级代理，分别计算分佣
        for (int i = 0; i < chain.size(); i++) {
            AgencyUserDO agent = chain.get(i);
            if (agent == null || !agent.getAgencyEnabled()) {
                log.info("[CommissionService] 第{}级代理不存在或未启用，跳过", i + 1);
                continue;
            }

            // 获取分佣比例（一级代理按自己的等级比例，间接上级按二级分佣比例）
            Integer commissionRate = i == 0
                    ? AgencyLevelEnum.getCommissionRateByLevel(agent.getLevel())
                    : AgencyLevelEnum.getSecondaryCommissionRate();
            if (commissionRate == null || commissionRate <= 0) {
                log.info("[CommissionService] 第{}级代理分佣比例为0，跳过 - agentId: {}", i + 1, agent.getUserId());
                continue;
            }

            // 计算分成金额（万分比）
            Integer commissionAmount = consumeAmount * commissionRate / 10000;
            if (commissionAmount <= 0) {
                log.info("[CommissionService] 第{}级代理分成金额为0，跳过 - rate: {}", i + 1, commissionRate);
                continue;
            }

            // 幂等检查（同一订单号 + 同一代理 + 同一层级）
            Integer level = i + 1;
            CommissionRecordDO existing = commissionRecordMapper.selectByBizOrderNoAndLevel(
                    bizOrderNo, agent.getUserId(), level);
            if (existing != null) {
                log.info("[CommissionService] 第{}级代理已分佣，跳过 - orderNo: {}, agentId: {}",
                        level, bizOrderNo, agent.getUserId());
                lastRecordId = existing.getId();
                continue;
            }

            // 创建佣金记录
            String remark = i == 0
                    ? "消费分成-一级代理，比例：" + (commissionRate / 100.0) + "%"
                    : "消费分成-二级代理(间接上级)，比例：" + (commissionRate / 100.0) + "%";
            CommissionRecordDO record = CommissionRecordDO.builder()
                    .userId(userId)
                    .brokerageUserId(agent.getUserId())
                    .bizType(CommissionBizTypeEnum.CONSUME.getType())
                    .bizOrderNo(bizOrderNo)
                    .amount(commissionAmount)
                    .commissionRate(commissionRate)
                    .level(level)
                    .status(1)
                    .settleTime(LocalDateTime.now())
                    .remark(remark)
                    .build();
            commissionRecordMapper.insert(record);
            lastRecordId = record.getId();

            // 增加代理钱包积分
            try {
                walletService.addPoints(agent.getUserId(), commissionAmount, 3, bizOrderNo,
                        String.format("下级消费分成(第%d级) - 订单：%s", level, bizOrderNo));
                log.info("[CommissionService] 第{}级代理消费分成成功 - agentId: {}, amount: {}",
                        level, agent.getUserId(), commissionAmount);
            } catch (Exception e) {
                log.error("[CommissionService] 增加钱包积分失败 - agentId: {}, amount: {}",
                        agent.getUserId(), commissionAmount, e);
                record.setStatus(2);
                commissionRecordMapper.updateById(record);
                throw e;
            }

            // 更新代理累计佣金
            agencyUserMapper.addTotalCommission(agent.getId(), commissionAmount);
        }

        log.info("[CommissionService] 消费分成完成 - userId: {}, orderNo: {}", userId, bizOrderNo);
        return lastRecordId;
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

        record.setStatus(2);
        commissionRecordMapper.updateById(record);

        log.info("[CommissionService] 佣金记录已取消 - recordId: {}", recordId);
        return true;
    }

}
