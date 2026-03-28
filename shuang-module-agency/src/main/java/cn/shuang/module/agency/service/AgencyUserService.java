package cn.shuang.module.agency.service;

import cn.shuang.module.agency.dal.dataobject.AgencyUserDO;

import java.util.List;

/**
 * 代理用户 Service 接口
 *
 * @author shuang-pro
 */
public interface AgencyUserService {

    /**
     * 获取用户的代理信息
     *
     * @param userId 用户 ID
     * @return 代理信息
     */
    AgencyUserDO getAgencyByUserId(Long userId);

    /**
     * 绑定上级代理
     *
     * @param userId 用户 ID
     * @param parentAgencyId 上级代理 ID
     * @return 是否成功
     */
    boolean bindParentAgency(Long userId, Long parentAgencyId);

    /**
     * 升级为一级代理
     *
     * @param userId 用户 ID
     * @param payFee 代理费（分）
     * @return 是否成功
     */
    boolean upgradeToLevel1(Long userId, Integer payFee);

    /**
     * 获取下级列表
     *
     * @param parentAgencyId 上级代理 ID
     * @return 下级列表
     */
    List<AgencyUserDO> getChildren(Long parentAgencyId);

    /**
     * 统计直推人数
     *
     * @param userId 用户 ID
     * @return 人数
     */
    int countDirectInvite(Long userId);

    /**
     * 添加积分 (代理分成)
     *
     * @param userId 用户 ID
     * @param points 积分数量
     * @param orderId 订单 ID
     * @return 是否成功
     */
    boolean addPoints(Long userId, Integer points, Long orderId);

}
