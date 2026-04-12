package cn.shuang.module.ai.controller.app.inspiration;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.module.ai.controller.app.inspiration.vo.AppInspirationCaseRespVO;
import cn.shuang.module.ai.controller.app.inspiration.vo.AppIndustryCategoryRespVO;
import cn.shuang.module.ai.dal.dataobject.category.AiInspirationCategoryDO;
import cn.shuang.module.ai.dal.dataobject.inspiration.AiInspirationCaseDO;
import cn.shuang.module.ai.service.category.AiInspirationCategoryService;
import cn.shuang.module.ai.service.inspiration.AiInspirationCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.shuang.framework.common.pojo.CommonResult.success;

/**
 * 用户端灵感案例 Controller
 * <p>
 * 提供灵感案例查询和行业分类列表接口，供前端展示使用
 *
 * @author shuang-pro
 */
@Tag(name = "用户 APP - 灵感案例")
@RestController
@RequestMapping("/ai/inspiration")
@Validated
public class AppInspirationCaseController {

    @Resource
    private AiInspirationCaseService inspirationCaseService;

    @Resource
    private AiInspirationCategoryService categoryService;

    @GetMapping("/categories")
    @Operation(summary = "获取行业分类列表", description = "返回所有行业分类及其案例数量")
    public CommonResult<List<AppIndustryCategoryRespVO>> getCategoryList() {
        // 获取所有分类
        List<AiInspirationCategoryDO> allCategories = categoryService.getCategoryList();

        // 获取所有案例，按分类聚合统计
        List<AiInspirationCaseDO> allCases = inspirationCaseService.getAllCases();

        // 按分类ID聚合
        Map<Integer, List<AiInspirationCaseDO>> categoryMap = allCases.stream()
                .filter(c -> c.getCategoryId() != null)
                .collect(Collectors.groupingBy(AiInspirationCaseDO::getCategoryId));

        // 构建分类列表响应
        List<AppIndustryCategoryRespVO> categoryList = new ArrayList<>();
        for (AiInspirationCategoryDO category : allCategories) {
            AppIndustryCategoryRespVO vo = new AppIndustryCategoryRespVO();
            vo.setId(category.getId());
            vo.setName(category.getName());
            List<AiInspirationCaseDO> cases = categoryMap.get(category.getId());
            if (cases != null) {
                vo.setCaseCount(cases.size());
                vo.setSortOrder(cases.stream()
                        .mapToInt(c -> c.getSortOrder() != null ? c.getSortOrder() : 0)
                        .min().orElse(category.getSortOrder() != null ? category.getSortOrder() : 0));
                vo.setHasFeatured(cases.stream().anyMatch(c -> Boolean.TRUE.equals(c.getFeatured())));
            } else {
                vo.setCaseCount(0);
                vo.setSortOrder(category.getSortOrder() != null ? category.getSortOrder() : 0);
                vo.setHasFeatured(false);
            }
            categoryList.add(vo);
        }

        // 按排序值排序
        categoryList.sort((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()));

        return success(categoryList);
    }

    @GetMapping("/list")
    @Operation(summary = "获取灵感案例列表", description = "返回灵感案例列表，支持按分类筛选")
    @Parameter(name = "categoryId", description = "行业分类 ID（可选）", example = "5")
    @Parameter(name = "type", description = "类型（可选）", example = "veo")
    @Parameter(name = "limit", description = "数量限制", example = "10")
    public CommonResult<List<AppInspirationCaseRespVO>> getCaseList(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        List<AiInspirationCaseDO> caseList;

        if (categoryId != null && categoryId > 0) {
            // 按分类获取
            caseList = inspirationCaseService.getListByCategoryId(categoryId, limit);
        } else if (type != null && !type.isEmpty()) {
            // 按类型获取
            caseList = inspirationCaseService.getListByType(type, limit);
        } else {
            // 获取全部（按排序）
            caseList = inspirationCaseService.getAllList(limit);
        }

        // 转换为响应 VO
        List<AppInspirationCaseRespVO> respVOList = caseList.stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());

        return success(respVOList);
    }

    @GetMapping("/featured")
    @Operation(summary = "获取精选灵感案例", description = "返回精选案例列表")
    @Parameter(name = "limit", description = "数量限制", example = "10")
    public CommonResult<List<AppInspirationCaseRespVO>> getFeaturedList(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<AiInspirationCaseDO> caseList = inspirationCaseService.getFeaturedList(limit);

        List<AppInspirationCaseRespVO> respVOList = caseList.stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());

        return success(respVOList);
    }

    @GetMapping("/get")
    @Operation(summary = "获取灵感案例详情", description = "返回单个案例详情")
    @Parameter(name = "id", description = "案例 ID", required = true, example = "1024")
    public CommonResult<AppInspirationCaseRespVO> getCase(@RequestParam("id") Long id) {
        AiInspirationCaseDO inspirationCase = inspirationCaseService.getCase(id);
        if (inspirationCase == null) {
            return success(null);
        }

        // 增加浏览次数
        inspirationCaseService.incrementViewCount(id);

        return success(convertToRespVO(inspirationCase));
    }

    @PostMapping("/use")
    @Operation(summary = "记录使用案例", description = "用户使用案例生成内容时调用，增加使用次数")
    @Parameter(name = "id", description = "案例 ID", required = true, example = "1024")
    public CommonResult<Boolean> recordUse(@RequestParam("id") Long id) {
        inspirationCaseService.incrementUseCount(id);
        return success(true);
    }

    /**
     * 转换为 Response VO
     */
    private AppInspirationCaseRespVO convertToRespVO(AiInspirationCaseDO inspirationCase) {
        if (inspirationCase == null) {
            return null;
        }
        AppInspirationCaseRespVO respVO = new AppInspirationCaseRespVO();
        respVO.setId(inspirationCase.getId());
        respVO.setType(inspirationCase.getType());
        respVO.setCategoryId(inspirationCase.getCategoryId());

        // 获取分类名称
        if (inspirationCase.getCategoryId() != null) {
            AiInspirationCategoryDO category = categoryService.getCategory(inspirationCase.getCategoryId());
            if (category != null) {
                respVO.setCategoryName(category.getName());
            }
        }

        respVO.setTitle(inspirationCase.getTitle());
        respVO.setContent(inspirationCase.getContent());
        respVO.setImage(inspirationCase.getImage());
        respVO.setImageFirst(inspirationCase.getImageFirst());
        respVO.setImageTail(inspirationCase.getImageTail());
        respVO.setVideoUrl(inspirationCase.getVideoUrl());
        respVO.setDuration(inspirationCase.getDuration());
        respVO.setLikeCount(inspirationCase.getLikeCount());
        respVO.setViewCount(inspirationCase.getViewCount());
        respVO.setUseCount(inspirationCase.getUseCount());
        respVO.setLabel(inspirationCase.getLabel());
        respVO.setIcon(inspirationCase.getIcon());
        respVO.setFeatured(inspirationCase.getFeatured());
        respVO.setCreateTime(inspirationCase.getCreateTime());
        return respVO;
    }

}