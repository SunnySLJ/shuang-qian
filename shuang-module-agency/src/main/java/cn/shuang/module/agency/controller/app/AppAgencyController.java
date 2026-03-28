package cn.shuang.module.agency.controller.app;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.module.agency.controller.app.vo.AppAgencyUserVO;
import cn.shuang.module.agency.controller.app.vo.AppBindAgencyReqVO;
import cn.shuang.module.agency.controller.app.vo.AppPointTransferReqVO;
import cn.shuang.module.agency.dal.dataobject.AgencyUserDO;
import cn.shuang.module.agency.service.AgencyUserService;
import cn.shuang.module.system.core.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 代理管理 - 用户端
 *
 * @author shuang-pro
 */
@Tag(name = "用户端 - 代理管理")
@RestController
@RequestMapping("/app/agency")
@RequiredArgsConstructor
@Validated
public class AppAgencyController {

    private final AgencyUserService agencyUserService;

    @GetMapping("/user/my")
    @Operation(summary = "获取我的代理信息")
    public CommonResult<AppAgencyUserVO> getMyAgency() {
        Long userId = SecurityUtils.getLoginUserId();

        AgencyUserDO agencyUser = agencyUserService.getAgencyByUserId(userId);

        AppAgencyUserVO vo = new AppAgencyUserVO();
        if (agencyUser != null) {
            vo.setAgencyEnabled(agencyUser.getAgencyEnabled());
            vo.setLevel(agencyUser.getLevel());
            vo.setTotalPoints(agencyUser.getTotalPoints());
            vo.setDistributedPoints(agencyUser.getDistributedPoints());
            vo.setAvailablePoints(agencyUser.getTotalPoints() - agencyUser.getDistributedPoints());
            vo.setDirectInviteCount(agencyUser.getDirectInviteCount());
            vo.setTeamTotalCount(agencyUser.getTeamTotalCount());
        } else {
            vo.setAgencyEnabled(false);
            vo.setLevel(0);
            vo.setTotalPoints(0);
            vo.setDistributedPoints(0);
            vo.setAvailablePoints(0);
            vo.setDirectInviteCount(0);
            vo.setTeamTotalCount(0);
        }

        return CommonResult.success(vo);
    }

    @PostMapping("/user/bind")
    @Operation(summary = "绑定上级代理")
    public CommonResult<Boolean> bindAgency(@RequestBody @Validated AppBindAgencyReqVO reqVO) {
        Long userId = SecurityUtils.getLoginUserId();

        // TODO: 根据邀请码查找上级代理
        // Long parentAgencyId = xxx;

        // boolean result = agencyUserService.bindParentAgency(userId, parentAgencyId);
        return CommonResult.success(false);
    }

    @PostMapping("/point/transfer")
    @Operation(summary = "分配积分给用户")
    public CommonResult<Boolean> transferPoint(@RequestBody @Validated AppPointTransferReqVO reqVO) {
        Long userId = SecurityUtils.getLoginUserId();

        // TODO: 实现积分分配逻辑
        // 1. 检查是否是代理
        // 2. 检查可用积分是否充足
        // 3. 执行分配

        return CommonResult.success(false);
    }

    @GetMapping("/user/children")
    @Operation(summary = "获取我的下级列表")
    public CommonResult<?> getChildren() {
        Long userId = SecurityUtils.getLoginUserId();

        // List<AgencyUserDO> children = agencyUserService.getChildren(userId);
        return CommonResult.success(new java.util.ArrayList<>());
    }

}
