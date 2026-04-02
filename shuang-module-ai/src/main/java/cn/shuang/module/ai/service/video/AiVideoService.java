package cn.shuang.module.ai.service.video;

import cn.shuang.module.ai.controller.admin.image.vo.AiImagePageReqVO;
import cn.shuang.module.ai.dal.dataobject.image.AiImageDO;
import cn.shuang.framework.common.pojo.PageResult;

/**
 * AI 视频生成 Service 接口
 *
 * @author shuang-pro
 */
public interface AiVideoService {

    /**
     * 文生视频
     *
     * @param userId 用户 ID
     * @param prompt 提示词
     * @param modelId 模型 ID
     * @param duration 视频时长（秒）
     * @return 生成记录 ID
     */
    Long textToVideo(Long userId, String prompt, Long modelId, Integer duration);

    /**
     * 图生视频
     *
     * @param userId 用户 ID
     * @param imageUrl 参考图片 URL
     * @param prompt 提示词
     * @param modelId 模型 ID
     * @param duration 视频时长（秒）
     * @return 生成记录 ID
     */
    Long imageToVideo(Long userId, String imageUrl, String prompt, Long modelId, Integer duration);

    /**
     * 黄金 6 秒拼接
     *
     * @param userId 用户 ID
     * @param prompt 提示词
     * @param modelId 模型 ID
     * @return 生成记录 ID
     */
    Long golden6s(Long userId, String prompt, Long modelId);

    /**
     * AI 超级混剪
     *
     * @param userId 用户 ID
     * @param prompt 提示词
     * @param modelId 模型 ID
     * @return 生成记录 ID
     */
    Long aiMix(Long userId, String prompt, Long modelId);

    /**
     * 视频拆解 - 提取脚本
     *
     * @param userId 用户 ID
     * @param videoUrl 视频 URL
     * @param modelId 模型 ID
     * @return 生成记录 ID
     */
    Long extractScript(Long userId, String videoUrl, Long modelId);

    /**
     * 视频拆解 - 分析元素
     *
     * @param userId 用户 ID
     * @param videoUrl 视频 URL
     * @param modelId 模型 ID
     * @return 生成记录 ID
     */
    Long analyzeElements(Long userId, String videoUrl, Long modelId);

    /**
     * 视频拆解 - 生成提示词
     *
     * @param userId 用户 ID
     * @param videoUrl 视频 URL
     * @param modelId 模型 ID
     * @return 生成记录 ID
     */
    Long generatePrompt(Long userId, String videoUrl, Long modelId);

    /**
     * 获取视频生成记录列表
     *
     * @param userId 用户 ID
     * @param pageReqVO 分页参数
     * @return 记录列表
     */
    PageResult<AiImageDO> getVideoPage(Long userId, AiImagePageReqVO pageReqVO);

    /**
     * 获取视频详情
     *
     * @param id 记录 ID
     * @return 视频详情
     */
    AiImageDO getVideoDetail(Long id);

    /**
     * 轮询更新视频状态
     *
     * @param id 记录 ID
     * @return 是否更新成功
     */
    boolean syncVideoStatus(Long id);

}
