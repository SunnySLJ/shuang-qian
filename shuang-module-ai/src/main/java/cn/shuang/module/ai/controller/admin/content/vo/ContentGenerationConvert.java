package cn.shuang.module.ai.controller.admin.content.vo;

import cn.shuang.module.ai.dal.dataobject.content.ContentGenerationRecordDO;
import cn.shuang.module.ai.framework.content.result.ContentGenerationResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 内容生成 VO 转换
 */
@Mapper
public interface ContentGenerationConvert {

    ContentGenerationConvert INSTANCE = Mappers.getMapper(ContentGenerationConvert.class);

    /**
     * 将生成结果转换为 Response VO
     */
    ContentGenerateRespVO convert(ContentGenerationResult result);

    /**
     * 将 DO 转换为详情 VO
     */
    ContentDetailRespVO convertDetail(ContentGenerationRecordDO record);

    /**
     * 将 DO 列表转换为详情 VO 列表
     */
    List<ContentDetailRespVO> convertDetailList(List<ContentGenerationRecordDO> records);
}
