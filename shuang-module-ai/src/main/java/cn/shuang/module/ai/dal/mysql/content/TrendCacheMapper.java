package cn.shuang.module.ai.dal.mysql.content;

import cn.shuang.framework.mybatis.core.mapper.BaseMapperX;
import cn.shuang.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.shuang.module.ai.dal.dataobject.content.TrendCacheDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 热门内容缓存 Mapper
 */
@Mapper
public interface TrendCacheMapper extends BaseMapperX<TrendCacheDO> {

    /**
     * 查询热门内容列表
     */
    default List<TrendCacheDO> selectByPlatform(String platform, String category, Integer limit) {
        return selectList(new LambdaQueryWrapperX<TrendCacheDO>()
                .eq(TrendCacheDO::getPlatform, platform)
                .eqIfPresent(TrendCacheDO::getCategory, category)
                .orderByDesc(TrendCacheDO::getTrendScore)
                .last("LIMIT " + limit));
    }

    /**
     * 查询指定内容 ID 的缓存
     */
    default TrendCacheDO selectByContentId(String platform, String contentId) {
        return selectOne(new LambdaQueryWrapperX<TrendCacheDO>()
                .eq(TrendCacheDO::getPlatform, platform)
                .eq(TrendCacheDO::getContentId, contentId));
    }
}
