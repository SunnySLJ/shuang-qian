package cn.shuang.module.agency.controller.admin;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.framework.common.pojo.PageParam;
import cn.shuang.framework.common.pojo.PageResult;
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

import static cn.shuang.framework.common.pojo.CommonResult.success;

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
    public CommonResult<PageResult<AgencyUserVO>> list(PageParam pageParam,
            @Parameter(description = "用户昵称") @RequestParam(required = false) String nickname) {
        PageResult<AgencyUserDO> pageResult = agencyUserService.getPage(nickname, pageParam.getPageNo(), pageParam.getPageSize());
        List<AgencyUserVO> list = pageResult.getList().stream().map(this::convert).toList();
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    @GetMapping("/get")
    @Operation(summary = "获取代理用户详情")
    @PreAuthorize("@ss.hasPermission('agency:user:query')")
    public CommonResult<AgencyUserVO> get(
            @Parameter(description = "主键 ID") @RequestParam Long id) {
        AgencyUserDO agencyUser = agencyUserService.getById(id);
        return success(convert(agencyUser));
    }

    @PostMapping("/upgrade")
    @Operation(summary = "升级用户为一级代理")
    @PreAuthorize("@ss.hasPermission('agency:user:upgrade')")
    public CommonResult<Boolean> upgrade(
            @Parameter(description = "用户 ID") @RequestParam Long userId,
            @Parameter(description = "代理费（分）") @RequestParam Integer payFee) {

        boolean result = agencyUserService.upgradeToLevel1(userId, payFee);
        return success(result);
    }

    @GetMapping("/children")
    @Operation(summary = "获取下级代理列表")
    @PreAuthorize("@ss.hasPermission('agency:user:list')")
    public CommonResult<List<AgencyUserVO>> children(
            @Parameter(description = "用户 ID") @RequestParam Long userId,
            @Parameter(description = "代理等级") @RequestParam(required = false) Integer level) {
        List<AgencyUserDO> list = agencyUserService.getChildren(userId);
        return success(list.stream().map(this::convert).collect(Collectors.toList()));
    }

    private AgencyUserVO convert(AgencyUserDO entity) {
        if (entity == null) {
            return null;
        }
        AgencyUserVO vo = new AgencyUserVO();
        vo.setId(entity.getId());
        vo.setUserId(entity.getUserId());
        vo.setNickname(entity.getNickname());
        vo.setLevel(entity.getLevel());
        vo.setAgencyEnabled(entity.getAgencyEnabled());
        vo.setDirectInviteCount(entity.getDirectInviteCount());
        vo.setTotalPoints(entity.getTotalPoints());
        vo.setDistributedPoints(entity.getDistributedPoints());
        return vo;
    }

}
