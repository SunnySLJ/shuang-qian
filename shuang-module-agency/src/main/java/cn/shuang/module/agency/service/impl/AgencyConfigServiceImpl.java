package cn.shuang.module.agency.service.impl;

import cn.shuang.module.agency.controller.admin.vo.AgencyConfigVO;
import cn.shuang.module.agency.dal.dataobject.AgencyConfigDO;
import cn.shuang.module.agency.dal.mysql.AgencyConfigMapper;
import cn.shuang.module.agency.service.AgencyConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 代理配置 Service 实现类
 * <p>
 * 用于管理代理系统的各项配置，包括：
 * - 代理级别配置（一级代理、二级代理的要求和权益）
 * - 分成比例配置（一级代理 20%、二级代理 8%）
 * - 升级条件配置（直推人数要求、代理费金额）
 *
 * @author shuang-pro
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AgencyConfigServiceImpl implements AgencyConfigService {

    private final AgencyConfigMapper agencyConfigMapper;

    @Override
    public List<AgencyConfigVO> getAllConfigs() {
        List<AgencyConfigDO> list = agencyConfigMapper.selectList(null);
        return list.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveConfig(AgencyConfigVO vo) {
        // 检查配置是否已存在
        AgencyConfigDO existing = agencyConfigMapper.selectOne(
            new LambdaQueryWrapper<AgencyConfigDO>()
                .eq(AgencyConfigDO::getConfigKey, vo.getConfigKey())
        );

        if (existing != null) {
            // 更新现有配置
            existing.setConfigValue(vo.getConfigValue());
            existing.setDescription(vo.getDescription());
            existing.setEnabled(vo.isEnabled());
            return agencyConfigMapper.updateById(existing) > 0;
        } else {
            // 插入新配置
            AgencyConfigDO entity = new AgencyConfigDO();
            entity.setConfigKey(vo.getConfigKey());
            entity.setConfigValue(vo.getConfigValue());
            entity.setDescription(vo.getDescription());
            entity.setEnabled(vo.isEnabled());
            return agencyConfigMapper.insert(entity) > 0;
        }
    }

    @Override
    public String getConfigValue(String configKey) {
        AgencyConfigDO config = agencyConfigMapper.selectOne(
            new LambdaQueryWrapper<AgencyConfigDO>()
                .eq(AgencyConfigDO::getConfigKey, configKey)
                .eq(AgencyConfigDO::getEnabled, true)
        );
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public String getConfigValueOrDefault(String configKey, String defaultVal) {
        String value = getConfigValue(configKey);
        return value != null ? value : defaultVal;
    }

    @Override
    public Integer getConfigIntValue(String configKey) {
        String value = getConfigValue(configKey);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("配置项 {} 的值 {} 无法转换为整数", configKey, value);
            return null;
        }
    }

    @Override
    public Integer getConfigIntValueOrDefault(String configKey, Integer defaultVal) {
        Integer value = getConfigIntValue(configKey);
        return value != null ? value : defaultVal;
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
        vo.setEnabled(entity.isEnabled());
        return vo;
    }

}
