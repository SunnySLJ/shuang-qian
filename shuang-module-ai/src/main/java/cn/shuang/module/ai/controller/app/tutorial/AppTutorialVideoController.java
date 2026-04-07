package cn.shuang.module.ai.controller.app.tutorial;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.module.ai.controller.app.tutorial.vo.AppTutorialCategoryRespVO;
import cn.shuang.module.ai.controller.app.tutorial.vo.AppTutorialVideoRespVO;
import cn.shuang.module.ai.dal.dataobject.tutorial.AiTutorialCategoryDO;
import cn.shuang.module.ai.dal.dataobject.tutorial.AiTutorialVideoDO;
import cn.shuang.module.ai.service.tutorial.AiTutorialVideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static cn.shuang.framework.common.pojo.CommonResult.success;

/**
 * 用户端教程视频 Controller
 * <p>
 * 提供教程视频查询和分类列表接口，供前端展示使用
 *
 * @author shuang-pro
 */
@Tag(name = "用户 APP - 教程视频")
@RestController
@RequestMapping("/app/ai/tutorial")
@Validated
public class AppTutorialVideoController {

    @Resource
    private AiTutorialVideoService tutorialVideoService;

    @GetMapping("/categories")
    @Operation(summary = "获取教程分类列表", description = "返回所有教程分类及其视频数量")
    public CommonResult<List<AppTutorialCategoryRespVO>> getCategoryList() {
        List<AiTutorialCategoryDO> categories = tutorialVideoService.getCategoryList();

        List<AppTutorialCategoryRespVO> respVOList = categories.stream()
                .map(this::convertCategoryToRespVO)
                .collect(Collectors.toList());

        return success(respVOList);
    }

    @GetMapping("/list")
    @Operation(summary = "获取教程视频列表", description = "返回教程视频列表，支持按分类筛选")
    @Parameter(name = "categoryId", description = "分类 ID（可选）", example = "1")
    @Parameter(name = "limit", description = "数量限制", example = "20")
    public CommonResult<List<AppTutorialVideoRespVO>> getVideoList(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        List<AiTutorialVideoDO> videoList;

        if (categoryId != null) {
            // 按分类获取
            videoList = tutorialVideoService.getVideoListByCategory(categoryId, limit);
        } else {
            // 获取全部
            videoList = tutorialVideoService.getAllVideoList(limit);
        }

        List<AppTutorialVideoRespVO> respVOList = videoList.stream()
                .map(this::convertVideoToRespVO)
                .collect(Collectors.toList());

        return success(respVOList);
    }

    @GetMapping("/get")
    @Operation(summary = "获取教程视频详情", description = "返回单个视频详情")
    @Parameter(name = "id", description = "视频 ID", required = true, example = "1024")
    public CommonResult<AppTutorialVideoRespVO> getVideo(@RequestParam("id") Long id) {
        AiTutorialVideoDO video = tutorialVideoService.getVideo(id);
        if (video == null) {
            return success(null);
        }

        // 增加观看次数
        tutorialVideoService.incrementViewCount(id);

        return success(convertVideoToRespVO(video));
    }

    @PostMapping("/like")
    @Operation(summary = "点赞教程视频", description = "用户点赞视频时调用，增加点赞次数")
    @Parameter(name = "id", description = "视频 ID", required = true, example = "1024")
    public CommonResult<Boolean> likeVideo(@RequestParam("id") Long id) {
        tutorialVideoService.incrementLikeCount(id);
        return success(true);
    }

    /**
     * 转换分类为 Response VO
     */
    private AppTutorialCategoryRespVO convertCategoryToRespVO(AiTutorialCategoryDO category) {
        if (category == null) {
            return null;
        }
        AppTutorialCategoryRespVO respVO = new AppTutorialCategoryRespVO();
        respVO.setId(category.getId());
        respVO.setName(category.getName());
        respVO.setDescription(category.getDescription());
        respVO.setIconUrl(category.getIconUrl());
        respVO.setSortOrder(category.getSortOrder());
        respVO.setVideoCount(category.getVideoCount());
        return respVO;
    }

    /**
     * 转换视频为 Response VO
     */
    private AppTutorialVideoRespVO convertVideoToRespVO(AiTutorialVideoDO video) {
        if (video == null) {
            return null;
        }
        AppTutorialVideoRespVO respVO = new AppTutorialVideoRespVO();
        respVO.setId(video.getId());
        respVO.setCategoryId(video.getCategoryId());

        // 获取分类名称
        AiTutorialCategoryDO category = tutorialVideoService.getCategory(video.getCategoryId());
        if (category != null) {
            respVO.setCategoryName(category.getName());
        }

        respVO.setName(video.getName());
        respVO.setCover(video.getCover());
        respVO.setVideoUrl(video.getVideoUrl());
        respVO.setDuration(video.getDuration());

        // 格式化时长
        if (video.getDuration() != null) {
            int minutes = video.getDuration() / 60;
            int seconds = video.getDuration() % 60;
            respVO.setDurationFormatted(String.format("%02d:%02d", minutes, seconds));
        }

        respVO.setViewCount(video.getViewCount());
        respVO.setLikeCount(video.getLikeCount());
        respVO.setIsFree(video.getIsFree());
        respVO.setCreateTime(video.getCreateTime());
        return respVO;
    }

}