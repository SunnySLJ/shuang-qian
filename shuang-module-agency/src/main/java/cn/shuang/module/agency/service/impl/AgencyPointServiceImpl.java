package cn.shuang.module.agency.service.impl;

import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.agency.dal.dataobject.AgencyPointTransferDO;
import cn.shuang.module.agency.dal.dataobject.AgencyUserDO;
import cn.shuang.module.agency.dal.mysql.AgencyPointTransferMapper;
import cn.shuang.module.agency.dal.mysql.AgencyUserMapper;
import cn.shuang.module.agency.enums.PointTransferBizTypeEnum;
import cn.shuang.module.agency.service.AgencyPointService;
import cn.shuang.module.pay.service.wallet.PayWalletService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 积分管理 Service 实现
 *
 * @author shuang-pro
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AgencyPointServiceImpl implements AgencyPointService {

    private final AgencyUserMapper agencyUserMapper;
    private final AgencyPointTransferMapper pointTransferMapper;
    private final PayWalletService payWalletService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferPoints(Long fromUserId, Long toUserId, Integer pointAmount, String description) {
        if (pointAmount == null || pointAmount <= 0) {
            log.warn("积分数量必须大于 0");
            return false;
        }

        // 1. 检查分配方是否是代理且有足够积分
        AgencyUserDO fromUser = agencyUserMapper.selectByUserId(fromUserId);
        if (fromUser == null || !fromUser.getAgencyEnabled()) {
            log.warn("用户 {} 不是代理", fromUserId);
            return false;
        }

        int availablePoints = fromUser.getTotalPoints() - fromUser.getDistributedPoints();
        if (availablePoints < pointAmount) {
            log.warn("用户 {} 可用积分不足，当前可用：{}", fromUserId, availablePoints);
            return false;
        }

        // 2. 更新已分配积分
        int updated = agencyUserMapper.update(null,
            cn.shuang.framework.mybatis.core.util.MyBatisUtils.buildUpdateWrapper(new AgencyUserDO())
                .eq("user_id", fromUserId)
                .setSql("distributed_points = distributed_points + " + pointAmount));

        if (updated <= 0) {
            log.error("更新已分配积分失败");
            return false;
        }

        // 3. 记录积分流水
        AgencyPointTransferDO transfer = AgencyPointTransferDO.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .pointAmount(pointAmount)
                .bizType(PointTransferBizTypeEnum.MANUAL_DISTRIBUTION.getType())
                .description(description != null ? description : "手动分配")
                .build();

        pointTransferMapper.insert(transfer);

        // 4. 添加到接收方钱包
        // payWalletService.addPoints(toUserId, pointAmount, "积分分配");

        return true;
    }

    @Override
    public PageResult<AgencyPointTransferDO> getPage(Long userId, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<AgencyPointTransferDO> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(AgencyPointTransferDO::getFromUserId, userId);
        }
        wrapper.orderByDesc(AgencyPointTransferDO::getId);
        return pointTransferMapper.selectPage(pageNo, pageSize, wrapper);
    }

    @Override
    public int[] getWallet(Long userId) {
        AgencyUserDO agencyUser = agencyUserMapper.selectByUserId(userId);
        if (agencyUser == null) {
            return new int[]{0, 0, 0, 0};
        }

        int availablePoints = agencyUser.getTotalPoints() - agencyUser.getDistributedPoints();
        int frozenPoints = 0; // TODO: 如果有冻结积分逻辑，从这里补充
        int totalDistributed = agencyUser.getDistributedPoints();
        int totalReceived = 0; // TODO: 需要查询收到的积分总和

        return new int[]{availablePoints, frozenPoints, totalDistributed, totalReceived};
    }

    @Override
    public PageResult<AgencyPointTransferDO> getTransactions(Long userId, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<AgencyPointTransferDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(AgencyPointTransferDO::getFromUserId, userId)
                         .or().eq(AgencyPointTransferDO::getToUserId, userId));
        wrapper.orderByDesc(AgencyPointTransferDO::getId);
        return pointTransferMapper.selectPage(pageNo, pageSize, wrapper);
    }

}
