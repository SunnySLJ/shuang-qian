package cn.shuang.module.ai.service.asset;

import cn.shuang.module.ai.controller.app.asset.vo.AppMaterialGroupCreateReqVO;
import cn.shuang.module.ai.dal.dataobject.asset.UserAssetDO;
import cn.shuang.module.ai.dal.dataobject.asset.MaterialGroupDO;

import java.util.List;

/**
 * 用户资产 Service 接口
 *
 * @author shuang-pro
 */
public interface UserAssetService {

    // ========== 资产管理 ==========

    /**
     * 获取用户资产列表
     *
     * @param userId    用户 ID
     * @param assetType 资产类型（可选）
     * @param groupId   分组 ID（可选）
     * @param limit     数量限制
     * @return 资产列表
     */
    List<UserAssetDO> getUserAssetList(Long userId, Integer assetType, Long groupId, Integer limit);

    /**
     * 获取资产详情
     *
     * @param id     资产 ID
     * @param userId 用户 ID
     * @return 资产详情
     */
    UserAssetDO getAssetDetail(Long id, Long userId);

    /**
     * 删除资产
     *
     * @param id     资产 ID
     * @param userId 用户 ID
     */
    void deleteAsset(Long id, Long userId);

    /**
     * 移动资产到分组
     *
     * @param id      资产 ID
     * @param groupId 分组 ID
     * @param userId  用户 ID
     */
    void moveAssetToGroup(Long id, Long groupId, Long userId);

    // ========== 素材分组管理 ==========

    /**
     * 获取用户素材分组列表
     *
     * @param userId 用户 ID
     * @return 分组列表
     */
    List<MaterialGroupDO> getMaterialGroupList(Long userId);

    /**
     * 创建素材分组
     *
     * @param userId 用户 ID
     * @param reqVO  创建请求
     * @return 分组 ID
     */
    Long createMaterialGroup(Long userId, AppMaterialGroupCreateReqVO reqVO);

    /**
     * 更新素材分组
     *
     * @param id     分组 ID
     * @param name   分组名称
     * @param userId 用户 ID
     */
    void updateMaterialGroup(Long id, String name, Long userId);

    /**
     * 删除素材分组
     *
     * @param id     分组 ID
     * @param userId 用户 ID
     */
    void deleteMaterialGroup(Long id, Long userId);

}