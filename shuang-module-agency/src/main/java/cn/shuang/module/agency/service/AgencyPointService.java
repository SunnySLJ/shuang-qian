package cn.shuang.module.agency.service;

import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.agency.dal.dataobject.AgencyPointTransferDO;

/**
 * 积分管理 Service 接口
 *
 * @author shuang-pro
 */
public interface AgencyPointService {

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
     * 分页查询积分分配记录
     *
     * @param userId 用户 ID（可选）
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<AgencyPointTransferDO> getPage(Long userId, Integer pageNo, Integer pageSize);

    /**
     * 查询用户的积分钱包
     *
     * @param userId 用户 ID
     * @return 钱包信息 [可用积分，冻结积分，累计分配，累计获得]
     */
    int[] getWallet(Long userId);

    /**
     * 分页查询用户积分流水
     *
     * @param userId 用户 ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<AgencyPointTransferDO> getTransactions(Long userId, Integer pageNo, Integer pageSize);

}
