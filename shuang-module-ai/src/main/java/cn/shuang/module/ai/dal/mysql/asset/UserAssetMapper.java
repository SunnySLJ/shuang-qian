package cn.shuang.module.ai.dal.mysql.asset;

import cn.shuang.framework.mybatis.core.mapper.BaseMapperX;
import cn.shuang.module.ai.dal.dataobject.asset.UserAssetDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户资产 Mapper
 *
 * @author shuang-pro
 */
@Mapper
public interface UserAssetMapper extends BaseMapperX<UserAssetDO> {

    /**
     * 获取用户资产列表
     */
    default List<UserAssetDO> selectListByUserId(Long userId, Integer assetType, Long groupId, Integer limit) {
        LambdaQueryWrapper<UserAssetDO> wrapper = new LambdaQueryWrapper<UserAssetDO>()
                .eq(UserAssetDO::getUserId, userId)
                .eq(assetType != null, UserAssetDO::getAssetType, assetType)
                .eq(groupId != null, UserAssetDO::getGroupId, groupId)
                .orderByDesc(UserAssetDO::getCreateTime);
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        return selectList(wrapper);
    }

}