package cn.shuang.module.ai.service.tutorial;

import cn.shuang.module.ai.dal.dataobject.tutorial.AiTutorialCategoryDO;
import cn.shuang.module.ai.dal.dataobject.tutorial.AiTutorialVideoDO;

import java.util.List;

/**
 * 教程视频 Service 接口
 *
 * @author shuang-pro
 */
public interface AiTutorialVideoService {

    /**
     * 获取所有教程分类列表
     *
     * @return 分类列表
     */
    List<AiTutorialCategoryDO> getCategoryList();

    /**
     * 获取分类详情
     *
     * @param id 分类 ID
     * @return 分类详情
     */
    AiTutorialCategoryDO getCategory(Long id);

    /**
     * 根据分类获取视频列表
     *
     * @param categoryId 分类 ID
     * @param limit      数量限制
     * @return 视频列表
     */
    List<AiTutorialVideoDO> getVideoListByCategory(Long categoryId, Integer limit);

    /**
     * 获取所有视频列表
     *
     * @param limit 数量限制
     * @return 视频列表
     */
    List<AiTutorialVideoDO> getAllVideoList(Integer limit);

    /**
     * 获取视频详情
     *
     * @param id 视频 ID
     * @return 视频详情
     */
    AiTutorialVideoDO getVideo(Long id);

    /**
     * 增加观看次数
     *
     * @param id 视频 ID
     */
    void incrementViewCount(Long id);

    /**
     * 增加点赞次数
     *
     * @param id 视频 ID
     */
    void incrementLikeCount(Long id);

    // ========== 分类管理 ==========

    /**
     * 创建教程分类
     *
     * @param category 分类信息
     */
    void createCategory(AiTutorialCategoryDO category);

    /**
     * 更新教程分类
     *
     * @param category 分类信息
     */
    void updateCategory(AiTutorialCategoryDO category);

    /**
     * 删除教程分类
     *
     * @param id 分类 ID
     */
    void deleteCategory(Long id);

    // ========== 视频管理 ==========

    /**
     * 创建教程视频
     *
     * @param video 视频信息
     */
    void createVideo(AiTutorialVideoDO video);

    /**
     * 更新教程视频
     *
     * @param video 视频信息
     */
    void updateVideo(AiTutorialVideoDO video);

    /**
     * 删除教程视频
     *
     * @param id 视频 ID
     */
    void deleteVideo(Long id);

}