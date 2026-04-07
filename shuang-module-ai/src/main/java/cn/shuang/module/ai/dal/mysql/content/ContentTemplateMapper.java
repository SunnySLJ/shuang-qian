package cn.shuang.module.ai.dal.mysql.content;

import cn.shuang.framework.mybatis.core.mapper.BaseMapperX;
import cn.shuang.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.shuang.module.ai.dal.dataobject.content.ContentTemplateDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 内容模板 Mapper
 */
@Mapper
public interface ContentTemplateMapper extends BaseMapperX<ContentTemplateDO> {

    /**
     * 查询内容模板列表
     */
    default List<ContentTemplateDO> selectList(String category, Boolean featured) {
        return selectList(new LambdaQueryWrapperX<ContentTemplateDO>()
                .eqIfPresent(ContentTemplateDO::getCategory, category)
                .eqIfPresent(ContentTemplateDO::getFeatured, featured)
                .orderByAsc(ContentTemplateDO::getSortOrder, ContentTemplateDO::getId));
    }

    /**
     * 查询精选模板列表
     */
    default List<ContentTemplateDO> selectFeaturedList(Integer limit) {
        return selectList(new LambdaQueryWrapperX<ContentTemplateDO>()
                .eq(ContentTemplateDO::getFeatured, true)
                .orderByAsc(ContentTemplateDO::getSortOrder)
                .last("LIMIT " + limit));
    }

    /**
     * 增加模板使用次数
     */
    @Update("UPDATE content_template SET use_count = use_count + 1 WHERE id = #{templateId} AND deleted = 0")
    int incrementUseCount(@Param("templateId") Long templateId);
}
