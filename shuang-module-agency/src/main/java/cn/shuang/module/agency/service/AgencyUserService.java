package cn.shuang.module.agency.service;

import cn.shuang.framework.common.pojo.PageResult;
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
     * 通过邀请码创建代理关系（自动创建上级代理记录如不存在）
     *
     * @param userId      新用户 ID
     * @param inviterId   邀请人 userId
     * @return 是否成功（userId已绑定或inviterId不存在时返回false）
     */
    boolean createAgencyRelation(Long userId, Long inviterId);

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

    /**
     * 分页查询代理用户列表
     *
     * @param nickname 用户昵称（可选）
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<AgencyUserDO> getPage(String nickname, Integer pageNo, Integer pageSize);

    /**
     * 根据 ID 查询代理用户
     *
     * @param id 主键 ID
     * @return 代理用户信息
     */
    AgencyUserDO getById(Long id);

    /**
     * 分配积分给用户
     *
     * @param fromUserId 分配方用户 ID
     * @param toUserId 接收方用户 ID
     * @param pointAmount 积分数量
     * @param description 描述
     * @return 是否成功
     */
    boolean transferPoints(Long fromUserId, Long toUserId, Integer pointAmount, String description);

    /**
     * 查询用户的积分钱包
     *
     * @param userId 用户 ID
     * @return 钱包信息 [可用积分，冻结积分，累计分配，累计获得]
     */
    int[] getWallet(Long userId);

}
