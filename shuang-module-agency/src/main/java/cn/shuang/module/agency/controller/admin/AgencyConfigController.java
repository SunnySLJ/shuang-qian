package cn.shuang.module.agency.controller.admin;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.module.agency.controller.admin.vo.AgencyConfigVO;
import cn.shuang.module.agency.dal.dataobject.AgencyConfigDO;
import cn.shuang.module.agency.dal.mysql.AgencyConfigMapper;
import cn.shuang.module.agency.service.AgencyConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代理配置管理 - 管理后台
 * <p>
 * 用于管理代理系统的各项配置，包括：
 * - 代理级别配置（一级代理、二级代理的要求和权益）
 * - 分成比例配置（一级代理 20%、二级代理 8%）
 * - 升级条件配置（直推人数要求、代理费金额）
 *
 * @author shuang-pro
 */
@Tag(name = "管理后台 - 代理配置管理")
@RestController
@RequestMapping("/agency/config")
@RequiredArgsConstructor
@Validated
public class AgencyConfigController {

    private final AgencyConfigService agencyConfigService;

    @GetMapping("/list")
    @Operation(summary = "获取代理配置列表")
    @PreAuthorize("@ss.hasPermission('agency:config:list')")
    public CommonResult<List<AgencyConfigVO>> list() {
        List<AgencyConfigVO> list = agencyConfigService.getAllConfigs();
        return CommonResult.success(list);
    }

    @PostMapping("/save")
    @Operation(summary = "保存代理配置")
    @PreAuthorize("@ss.hasPermission('agency:config:save')")
    public CommonResult<Boolean> save(@RequestBody AgencyConfigVO vo) {
        boolean result = agencyConfigService.saveConfig(vo);
        return CommonResult.success(result);
    }

    /**
     * 将 DO 对象转换为 VO 对象
     *
     * @param entity 代理配置实体
     * @return 代理配置 VO
     */
    private AgencyConfigVO convert(AgencyConfigDO entity) {
        AgencyConfigVO vo = new AgencyConfigVO();
        vo.setConfigKey(entity.getConfigKey());
        vo.setConfigValue(entity.getConfigValue());
        vo.setDescription(entity.getDescription());
        return vo;
    }

}
