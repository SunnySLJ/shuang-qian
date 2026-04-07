package cn.shuang.module.ai.dal.mysql.content;

import cn.shuang.framework.mybatis.core.mapper.BaseMapperX;
import cn.shuang.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.shuang.module.ai.dal.dataobject.content.ContentGenerationRecordDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容生成记录 Mapper
 */
@Mapper
public interface ContentGenerationRecordMapper extends BaseMapperX<ContentGenerationRecordDO> {

    /**
     * 分页查询内容生成记录
     */
    default cn.shuang.framework.common.pojo.PageResult<ContentGenerationRecordDO> selectPage(
            Long userId, Integer generationType, Integer status, LocalDateTime createTime) {
        return selectPage(new cn.shuang.framework.common.pojo.PageParam(),
                new LambdaQueryWrapperX<ContentGenerationRecordDO>()
                        .eqIfPresent(ContentGenerationRecordDO::getUserId, userId)
                        .eqIfPresent(ContentGenerationRecordDO::getGenerationType, generationType)
                        .eqIfPresent(ContentGenerationRecordDO::getStatus, status)
                        .geIfPresent(ContentGenerationRecordDO::getCreateTime, createTime)
                        .orderByDesc(ContentGenerationRecordDO::getId));
    }

    /**
     * 查询用户的生成记录列表
     */
    default List<ContentGenerationRecordDO> selectByUserId(Long userId, Integer limit) {
        return selectList(new LambdaQueryWrapperX<ContentGenerationRecordDO>()
                .eq(ContentGenerationRecordDO::getUserId, userId)
                .orderByDesc(ContentGenerationRecordDO::getId)
                .last("LIMIT " + limit));
    }

    /**
     * 统计用户的生成次数
     */
    @Select("SELECT COUNT(*) FROM content_generation_record WHERE user_id = #{userId} AND deleted = 0")
    Long countByUserId(@Param("userId") Long userId);
}
