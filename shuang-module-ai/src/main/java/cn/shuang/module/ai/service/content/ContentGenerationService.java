package cn.shuang.module.ai.service.content;

import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.ai.controller.admin.content.vo.ContentGenerateReqVO;
import cn.shuang.module.ai.controller.admin.content.vo.ContentGenerateRespVO;
import cn.shuang.module.ai.controller.admin.content.vo.ContentGenerationPageReqVO;
import cn.shuang.module.ai.dal.dataobject.content.ContentGenerationRecordDO;
import cn.shuang.module.ai.framework.content.context.ContentContext;
import cn.shuang.module.ai.framework.content.result.ContentGenerationResult;

/**
 * 内容生成 Service 接口
 */
public interface ContentGenerationService {

    /**
     * 生成内容（文生视频/图生视频/爆款拆解等）
     *
     * @param reqVO  生成请求
     * @param userId 用户 ID
     * @return 生成结果
     */
    ContentGenerateRespVO generate(ContentGenerateReqVO reqVO, Long userId);

    /**
     * 查询生成详情
     *
     * @param taskId 任务 ID
     * @param userId 用户 ID
     * @return 生成详情
     */
    ContentGenerationRecordDO getGenerationDetail(Long taskId, Long userId);

    /**
     * 分页查询生成记录
     *
     * @param pageReqVO 分页请求
     * @return 分页结果
     */
    PageResult<ContentGenerationRecordDO> getGenerationPage(ContentGenerationPageReqVO pageReqVO);

    /**
     * 查询用户的生成记录列表
     *
     * @param userId 用户 ID
     * @param limit  数量限制
     * @return 生成记录列表
     */
    java.util.List<ContentGenerationRecordDO> getUserGenerationList(Long userId, Integer limit);
}
