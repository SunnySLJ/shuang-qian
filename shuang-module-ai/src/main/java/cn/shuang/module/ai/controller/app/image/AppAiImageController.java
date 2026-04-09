package cn.shuang.module.ai.controller.app.image;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.framework.security.core.util.SecurityFrameworkUtils;
import cn.shuang.module.ai.controller.admin.image.vo.AiImagePageReqVO;
import cn.shuang.module.ai.controller.app.image.vo.AiImageGenerateReqVO;
import cn.shuang.module.ai.controller.app.image.vo.AiImageRespVO;
import cn.shuang.module.ai.dal.dataobject.image.AiImageDO;
import cn.shuang.module.ai.service.image.AiImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static cn.shuang.framework.common.pojo.CommonResult.success;

/**
 * 用户端 AI 图片生成 Controller
 *
 * @author shuang-pro
 */
@Tag(name = "用户 APP - AI 图片生成")
@RestController
@RequestMapping("/app/ai/image")
@Validated
public class AppAiImageController {

    @Resource
    private AiImageService imageService;

    @PostMapping("/generate")
    @Operation(summary = "生成图片", description = "文生图或图生图，根据是否传入参考图片URL判断")
    public CommonResult<Long> generateImage(@Valid @RequestBody AiImageGenerateReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        // 使用现有的绘图方法
        cn.shuang.module.ai.controller.admin.image.vo.AiImageDrawReqVO drawReqVO =
                new cn.shuang.module.ai.controller.admin.image.vo.AiImageDrawReqVO();
        drawReqVO.setPrompt(reqVO.getPrompt());
        drawReqVO.setModelId(reqVO.getModelId());
        drawReqVO.setWidth(reqVO.getWidth() != null ? reqVO.getWidth() : 1024);
        drawReqVO.setHeight(reqVO.getHeight() != null ? reqVO.getHeight() : 1024);

        Long imageId = imageService.drawImage(userId, drawReqVO);
        return success(imageId);
    }

    @GetMapping("/list")
    @Operation(summary = "获取我的图片列表", description = "获取用户生成的图片列表")
    @Parameter(name = "limit", description = "数量限制", example = "10")
    public CommonResult<List<AiImageRespVO>> getMyImageList(
            @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AiImagePageReqVO pageReqVO = new AiImagePageReqVO();
        pageReqVO.setPageNo(1);
        pageReqVO.setPageSize(limit);
        PageResult<AiImageDO> pageResult = imageService.getImagePageMy(userId, pageReqVO);
        List<AiImageRespVO> list = pageResult.getList().stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
        return success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取我的图片列表")
    public CommonResult<PageResult<AiImageRespVO>> getMyImagePage(@Valid AiImagePageReqVO pageReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        PageResult<AiImageDO> pageResult = imageService.getImagePageMy(userId, pageReqVO);
        List<AiImageRespVO> list = pageResult.getList().stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    @GetMapping("/get")
    @Operation(summary = "获取图片详情")
    @Parameter(name = "id", description = "图片 ID", required = true, example = "1024")
    public CommonResult<AiImageRespVO> getImageDetail(@RequestParam("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AiImageDO image = imageService.getImage(id);
        if (image == null || !image.getUserId().equals(userId)) {
            return success(null);
        }
        return success(convertToRespVO(image));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除图片")
    @Parameter(name = "id", description = "图片 ID", required = true, example = "1024")
    public CommonResult<Boolean> deleteImage(@RequestParam("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        imageService.deleteImageMy(id, userId);
        return success(true);
    }

    /**
     * 转换为响应 VO
     */
    private AiImageRespVO convertToRespVO(AiImageDO image) {
        if (image == null) {
            return null;
        }
        AiImageRespVO respVO = new AiImageRespVO();
        respVO.setId(image.getId());
        respVO.setPrompt(image.getPrompt());
        respVO.setReferenceImageUrl(image.getPicUrl());
        respVO.setOutputUrl(image.getOutputUrl() != null ? image.getOutputUrl() : image.getPicUrl());
        respVO.setStatus(image.getStatus());
        respVO.setErrorMessage(image.getErrorMessage());
        respVO.setGenerationType(image.getGenerationType());
        respVO.setWidth(image.getWidth());
        respVO.setHeight(image.getHeight());
        respVO.setCreateTime(image.getCreateTime());
        return respVO;
    }

}