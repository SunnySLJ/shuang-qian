package cn.shuang.module.agency.service.impl;

import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.agency.dal.dataobject.AgencyPointTransferDO;
import cn.shuang.module.agency.dal.dataobject.AgencyUserDO;
import cn.shuang.module.agency.dal.mysql.AgencyPointTransferMapper;
import cn.shuang.module.agency.dal.mysql.AgencyUserMapper;
import cn.shuang.module.agency.enums.AgencyLevelEnum;
import cn.shuang.module.agency.enums.PointTransferBizTypeEnum;
import cn.shuang.module.agency.service.AgencyPointService;
import cn.shuang.module.agency.service.AgencyUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 代理用户 Service 实现
 *
 * @author shuang-pro
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AgencyUserServiceImpl implements AgencyUserService {

    private final AgencyUserMapper agencyUserMapper;
    private final AgencyPointTransferMapper pointTransferMapper;
    private final AgencyPointService agencyPointService;

    @Override
    public AgencyUserDO getAgencyByUserId(Long userId) {
        return agencyUserMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindParentAgency(Long userId, Long parentAgencyId) {
        // 检查是否已经绑定
        AgencyUserDO existing = agencyUserMapper.selectByUserId(userId);
        if (existing != null) {
            log.warn("用户 {} 已经绑定过代理", userId);
            return false;
        }

        // 检查上级代理是否存在
        AgencyUserDO parentAgency = agencyUserMapper.selectByUserId(parentAgencyId);
        if (parentAgency == null) {
            log.warn("上级代理 {} 不存在", parentAgencyId);
            return false;
        }

        // 创建代理关系
        AgencyUserDO agencyUser = AgencyUserDO.builder()
                .userId(userId)
                .parentAgencyId(parentAgencyId)
                .level(2) // 默认为二级代理
                .agencyEnabled(false)
                .bindMode(1) // 主动绑定
                .totalPoints(0)
                .distributedPoints(0)
                .directInviteCount(0)
                .teamTotalCount(0)
                .build();

        int result = agencyUserMapper.insert(agencyUser);

        // 更新上级的直推人数
        if (result > 0 && parentAgencyId != null) {
            // 更新直推人数
            agencyUserMapper.update(null,
                new UpdateWrapper<AgencyUserDO>()
                    .eq("user_id", parentAgencyId)
                    .setSql("direct_invite_count = direct_invite_count + 1"));

            // 更新上级及其所有上级的团队总人数
            updateTeamTotalCount(parentAgencyId, 1);
        }

        return result > 0;
    }

    /**
     * 递归更新上级代理的团队总人数
     *
     * @param userId 用户 ID
     * @param count 增加的人数
     */
    private void updateTeamTotalCount(Long userId, int count) {
        AgencyUserDO agencyUser = agencyUserMapper.selectByUserId(userId);
        if (agencyUser == null || agencyUser.getParentAgencyId() == null) {
            return;
        }

        Long parentAgencyId = agencyUser.getParentAgencyId();
        agencyUserMapper.update(null,
            new UpdateWrapper<AgencyUserDO>()
                .eq("user_id", parentAgencyId)
                .setSql("team_total_count = team_total_count + " + count));

        // 递归更新上级的上级
        updateTeamTotalCount(parentAgencyId, count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean upgradeToLevel1(Long userId, Integer payFee) {
        AgencyUserDO agencyUser = agencyUserMapper.selectByUserId(userId);
        if (agencyUser == null) {
            log.warn("用户 {} 不是代理", userId);
            return false;
        }

        if (agencyUser.getLevel() == 1) {
            log.warn("用户 {} 已经是一级代理", userId);
            return false;
        }

        agencyUser.setLevel(1);
        agencyUser.setAgencyEnabled(true);
        agencyUser.setAgencyFee(payFee);
        agencyUser.setPayFeeTime(LocalDateTime.now());

        int result = agencyUserMapper.updateById(agencyUser);
        return result > 0;
    }

    @Override
    public java.util.List<AgencyUserDO> getChildren(Long parentAgencyId) {
        return agencyUserMapper.selectByParentAgencyId(parentAgencyId);
    }

    @Override
    public int countDirectInvite(Long userId) {
        return agencyUserMapper.countDirectInvite(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPoints(Long userId, Integer points, Long orderId) {
        if (points == null || points <= 0) {
            return false;
        }

        AgencyUserDO agencyUser = agencyUserMapper.selectByUserId(userId);
        if (agencyUser == null) {
            log.warn("用户 {} 不是代理", userId);
            return false;
        }

        // 1. 更新代理的累计积分
        agencyUserMapper.update(null,
            new UpdateWrapper<AgencyUserDO>()
                .eq("user_id", userId)
                .setSql("total_points = total_points + " + points));

        // 2. 记录积分流水
        AgencyPointTransferDO transfer = AgencyPointTransferDO.builder()
                .fromUserId(0L) // 系统奖励
                .toUserId(userId)
                .pointAmount(points)
                .orderId(orderId)
                .bizType(PointTransferBizTypeEnum.RECHARGE_COMMISSION.getType())
                .description("充值分成奖励")
                .build();

        pointTransferMapper.insert(transfer);

        // 3. 添加到钱包（调用 pay 模块的钱包服务）
        // payWalletService.addPoints(userId, points, "代理分成");

        return true;
    }

    @Override
    public PageResult<AgencyUserDO> getPage(String nickname, Integer pageNo, Integer pageSize) {
        // 使用联查 nickname 的分页方法
        return agencyUserMapper.selectPageByNickname(nickname);
    }

    @Override
    public AgencyUserDO getById(Long id) {
        return agencyUserMapper.selectById(id);
    }

    @Override
    public boolean transferPoints(Long fromUserId, Long toUserId, Integer pointAmount, String description) {
        // 委托给 AgencyPointService 处理
        return agencyPointService.transferPoints(fromUserId, toUserId, pointAmount, description);
    }

    @Override
    public int[] getWallet(Long userId) {
        return agencyPointService.getWallet(userId);
    }

}
