package cn.shuang.module.agency.dal.mysql;

import cn.shuang.framework.mybatis.core.mapper.BaseMapperX;
import cn.shuang.module.agency.dal.dataobject.AgencyPointTransferDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分分配记录 Mapper
 *
 * @author shuang-pro
 */
@Mapper
public interface AgencyPointTransferMapper extends BaseMapperX<AgencyPointTransferDO> {
}
