package cn.shuang.module.agency.controller.admin;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.module.agency.controller.admin.vo.AgencyConfigVO;
import cn.shuang.module.agency.dal.dataobject.AgencyConfigDO;
import cn.shuang.module.agency.dal.mysql.AgencyConfigMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 代理配置管理 - 管理后台
 *
 * @author shuang-pro
 */
@Tag(name = "管理后台 - 代理配置管理")
@RestController
@RequestMapping("/agency/config")
@RequiredArgsConstructor
@Validated
public class AgencyConfigController {

    private final AgencyConfigMapper agencyConfigMapper;

    @GetMapping("/list")
    @Operation(summary = "获取代理配置列表")
    @PreAuthorize("@ss.hasPermission('agency:config:list')")
    public CommonResult<List<AgencyConfigVO>> list() {
        List<AgencyConfigDO> list = agencyConfigMapper.selectList(null);
        return CommonResult.success(list.stream().map(this::convert).toList());
    }

    @PostMapping("/save")
    @Operation(summary = "保存代理配置")
    @PreAuthorize("@ss.hasPermission('agency:config:save')")
    public CommonResult<Boolean> save(
            @RequestBody AgencyConfigVO vo) {

        AgencyConfigDO entity = new AgencyConfigDO();
        entity.setConfigKey(vo.getConfigKey());
        entity.setConfigValue(vo.getConfigValue());
        entity.setDescription(vo.getDescription());
        entity.setEnabled(true);

        // TODO: 实现保存逻辑
        return CommonResult.success(true);
    }

    private AgencyConfigVO convert(AgencyConfigDO entity) {
        AgencyConfigVO vo = new AgencyConfigVO();
        vo.setConfigKey(entity.getConfigKey());
        vo.setConfigValue(entity.getConfigValue());
        vo.setDescription(entity.getDescription());
        return vo;
    }

}
