package cn.shuang.module.ai.service.inspiration;

import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.ai.controller.admin.inspiration.vo.AiInspirationCaseCreateReqVO;
import cn.shuang.module.ai.controller.admin.inspiration.vo.AiInspirationCasePageReqVO;
import cn.shuang.module.ai.controller.admin.inspiration.vo.AiInspirationCaseUpdateReqVO;
import cn.shuang.module.ai.dal.dataobject.inspiration.AiInspirationCaseDO;

import java.util.List;

/**
 * AI 灵感案例 Service 接口
 *
 * @author shuang-pro
 */
public interface AiInspirationCaseService {

    /**
     * 创建灵感案例
     *
     * @param createReqVO 创建请求
     * @return 案例 ID
     */
    Long createCase(AiInspirationCaseCreateReqVO createReqVO);

    /**
     * 更新灵感案例
     *
     * @param updateReqVO 更新请求
     */
    void updateCase(AiInspirationCaseUpdateReqVO updateReqVO);

    /**
     * 删除灵感案例
     *
     * @param id 案例 ID
     */
    void deleteCase(Long id);

    /**
     * 获取灵感案例详情
     *
     * @param id 案例 ID
     * @return 灵感案例
     */
    AiInspirationCaseDO getCase(Long id);

    /**
     * 获取灵感案例分页
     *
     * @param pageReqVO 分页请求
     * @return 灵感案例分页
     */
    PageResult<AiInspirationCaseDO> getPage(AiInspirationCasePageReqVO pageReqVO);

    /**
     * 根据分类获取灵感案例列表
     *
     * @param category 行业分类
     * @param limit    数量限制
     * @return 灵感案例列表
     */
    List<AiInspirationCaseDO> getListByCategory(String category, Integer limit);

    /**
     * 获取精选灵感案例列表
     *
     * @param limit 数量限制
     * @return 灵感案例列表
     */
    List<AiInspirationCaseDO> getFeaturedList(Integer limit);

    /**
     * 增加浏览次数
     *
     * @param id 案例 ID
     */
    void incrementViewCount(Long id);

    /**
     * 增加使用次数
     *
     * @param id 案例 ID
     */
    void incrementUseCount(Long id);

}
