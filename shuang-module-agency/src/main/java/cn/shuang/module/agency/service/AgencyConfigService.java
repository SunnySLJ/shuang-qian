package cn.shuang.module.agency.service;

import cn.shuang.module.agency.controller.admin.vo.AgencyConfigVO;

import java.util.List;

/**
 * 代理配置 Service 接口
 * <p>
 * 用于管理代理系统的各项配置，包括：
 * - 代理级别配置（一级代理、二级代理的要求和权益）
 * - 分成比例配置（一级代理 20%、二级代理 8%）
 * - 升级条件配置（直推人数要求、代理费金额）
 *
 * @author shuang-pro
 */
public interface AgencyConfigService {

    /**
     * 获取所有代理配置
     *
     * @return 配置列表
     */
    List<AgencyConfigVO> getAllConfigs();

    /**
     * 保存代理配置
     *
     * @param vo 配置信息
     * @return 是否成功
     */
    boolean saveConfig(AgencyConfigVO vo);

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值，不存在返回 null
     */
    String getConfigValue(String configKey);

    /**
     * 获取配置值（带默认值）
     *
     * @param configKey  配置键
     * @param defaultVal 默认值
     * @return 配置值
     */
    String getConfigValueOrDefault(String configKey, String defaultVal);

    /**
     * 获取整数类型配置值
     *
     * @param configKey 配置键
     * @return 整数值
     */
    Integer getConfigIntValue(String configKey);

    /**
     * 获取整数类型配置值（带默认值）
     *
     * @param configKey  配置键
     * @param defaultVal 默认值
     * @return 整数值
     */
    Integer getConfigIntValueOrDefault(String configKey, Integer defaultVal);

}
