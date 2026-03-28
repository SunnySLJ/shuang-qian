package cn.shuang.module.agency.controller.admin;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.module.agency.controller.admin.vo.AgencyUserVO;
import cn.shuang.module.agency.dal.dataobject.AgencyUserDO;
import cn.shuang.module.agency.service.AgencyUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 代理用户管理 - 管理后台
 *
 * @author shuang-pro
 */
@Tag(name = "管理后台 - 代理用户管理")
@RestController
@RequestMapping("/agency/user")
@RequiredArgsConstructor
@Validated
public class AgencyUserController {

    private final AgencyUserService agencyUserService;

    @GetMapping("/list")
    @Operation(summary = "获取代理用户列表")
    @PreAuthorize("@ss.hasPermission('agency:user:list')")
    public CommonResult<List<AgencyUserVO>> list(
            @Parameter(description = "用户昵称") @RequestParam(required = false) String nickname) {

        // TODO: 实现分页查询
        List<AgencyUserDO> list = new java.util.ArrayList<>();
        return CommonResult.success(list.stream().map(this::convert).collect(Collectors.toList()));
    }

    @GetMapping("/get")
    @Operation(summary = "获取代理用户详情")
    @PreAuthorize("@ss.hasPermission('agency:user:query')")
    public CommonResult<AgencyUserVO> get(
            @Parameter(description = "主键 ID") @RequestParam Long id) {

        // TODO: 实现详情查询
        return CommonResult.success(null);
    }

    @PostMapping("/upgrade")
    @Operation(summary = "升级用户为一级代理")
    @PreAuthorize("@ss.hasPermission('agency:user:upgrade')")
    public CommonResult<Boolean> upgrade(
            @Parameter(description = "用户 ID") @RequestParam Long userId,
            @Parameter(description = "代理费（分）") @RequestParam Integer payFee) {

        boolean result = agencyUserService.upgradeToLevel1(userId, payFee);
        return CommonResult.success(result);
    }

    private AgencyUserVO convert(AgencyUserDO entity) {
        AgencyUserVO vo = new AgencyUserVO();
        vo.setId(entity.getId());
        vo.setUserId(entity.getUserId());
        vo.setLevel(entity.getLevel());
        vo.setAgencyEnabled(entity.getAgencyEnabled());
        vo.setDirectInviteCount(entity.getDirectInviteCount());
        vo.setTotalPoints(entity.getTotalPoints());
        vo.setDistributedPoints(entity.getDistributedPoints());
        return vo;
    }

}
