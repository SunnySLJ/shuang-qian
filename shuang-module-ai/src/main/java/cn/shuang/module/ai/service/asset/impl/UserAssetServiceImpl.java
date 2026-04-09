package cn.shuang.module.ai.service.asset.impl;

import cn.shuang.module.ai.controller.app.asset.vo.AppMaterialGroupCreateReqVO;
import cn.shuang.module.ai.dal.dataobject.asset.MaterialGroupDO;
import cn.shuang.module.ai.dal.dataobject.asset.UserAssetDO;
import cn.shuang.module.ai.dal.mysql.asset.MaterialGroupMapper;
import cn.shuang.module.ai.dal.mysql.asset.UserAssetMapper;
import cn.shuang.module.ai.service.asset.UserAssetService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.shuang.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.shuang.module.ai.enums.ErrorCodeConstants.*;

/**
 * 用户资产 Service 实现类
 *
 * @author shuang-pro
 */
@Service
@Slf4j
public class UserAssetServiceImpl implements UserAssetService {

    @Resource
    private UserAssetMapper userAssetMapper;

    @Resource
    private MaterialGroupMapper materialGroupMapper;

    // ========== 资产管理 ==========

    @Override
    public List<UserAssetDO> getUserAssetList(Long userId, Integer assetType, Long groupId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        return userAssetMapper.selectListByUserId(userId, assetType, groupId, limit);
    }

    @Override
    public UserAssetDO getAssetDetail(Long id, Long userId) {
        UserAssetDO asset = userAssetMapper.selectById(id);
        if (asset == null || !asset.getUserId().equals(userId)) {
            return null;
        }
        return asset;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAsset(Long id, Long userId) {
        UserAssetDO asset = userAssetMapper.selectById(id);
        if (asset == null || !asset.getUserId().equals(userId)) {
            throw exception(ASSET_NOT_EXISTS);
        }
        userAssetMapper.deleteById(id);
        log.info("[deleteAsset] 用户 {} 删除资产 {}", userId, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveAssetToGroup(Long id, Long groupId, Long userId) {
        UserAssetDO asset = userAssetMapper.selectById(id);
        if (asset == null || !asset.getUserId().equals(userId)) {
            throw exception(ASSET_NOT_EXISTS);
        }
        // 验证分组存在
        if (groupId != null && groupId > 0) {
            MaterialGroupDO group = materialGroupMapper.selectById(groupId);
            if (group == null || !group.getUserId().equals(userId)) {
                throw exception(MATERIAL_GROUP_NOT_EXISTS);
            }
        }
        UserAssetDO updateObj = new UserAssetDO();
        updateObj.setId(id);
        updateObj.setGroupId(groupId);
        userAssetMapper.updateById(updateObj);
        log.info("[moveAssetToGroup] 用户 {} 移动资产 {} 到分组 {}", userId, id, groupId);
    }

    // ========== 素材分组管理 ==========

    @Override
    public List<MaterialGroupDO> getMaterialGroupList(Long userId) {
        return materialGroupMapper.selectListByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMaterialGroup(Long userId, AppMaterialGroupCreateReqVO reqVO) {
        MaterialGroupDO group = new MaterialGroupDO();
        group.setUserId(userId);
        group.setName(reqVO.getName());
        group.setSortOrder(0);
        materialGroupMapper.insert(group);
        log.info("[createMaterialGroup] 用户 {} 创建分组 {}", userId, group.getId());
        return group.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMaterialGroup(Long id, String name, Long userId) {
        MaterialGroupDO group = materialGroupMapper.selectById(id);
        if (group == null || !group.getUserId().equals(userId)) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }
        MaterialGroupDO updateObj = new MaterialGroupDO();
        updateObj.setId(id);
        updateObj.setName(name);
        materialGroupMapper.updateById(updateObj);
        log.info("[updateMaterialGroup] 用户 {} 更新分组 {}", userId, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaterialGroup(Long id, Long userId) {
        MaterialGroupDO group = materialGroupMapper.selectById(id);
        if (group == null || !group.getUserId().equals(userId)) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }
        materialGroupMapper.deleteById(id);
        log.info("[deleteMaterialGroup] 用户 {} 删除分组 {}", userId, id);
    }

}