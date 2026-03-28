package cn.shuang.module.agency.dal.mysql;

import cn.shuang.module.agency.dal.dataobject.AgencyConfigDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 代理配置 Mapper
 *
 * @author shuang-pro
 */
@Mapper
public interface AgencyConfigMapper extends BaseMapper<AgencyConfigDO> {

    /**
     * 根据配置键获取配置
     *
     * @param configKey 配置键
     * @return 配置
     */
    AgencyConfigDO selectByKey(@Param("configKey") String configKey);

}
