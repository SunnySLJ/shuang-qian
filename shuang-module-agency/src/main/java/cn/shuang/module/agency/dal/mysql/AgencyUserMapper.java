package cn.shuang.module.agency.dal.mysql;

import cn.shuang.module.agency.dal.dataobject.AgencyUserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 代理用户 Mapper
 *
 * @author shuang-pro
 */
@Mapper
public interface AgencyUserMapper extends BaseMapper<AgencyUserDO> {

    /**
     * 根据用户 ID 查询代理信息
     *
     * @param userId 用户 ID
     * @return 代理信息
     */
    AgencyUserDO selectByUserId(@Param("userId") Long userId);

    /**
     * 根据上级代理 ID 查询下级列表
     *
     * @param parentAgencyId 上级代理 ID
     * @return 下级列表
     */
    List<AgencyUserDO> selectByParentAgencyId(@Param("parentAgencyId") Long parentAgencyId);

    /**
     * 统计直推人数
     *
     * @param userId 用户 ID
     * @return 人数
     */
    Integer countDirectInvite(@Param("userId") Long userId);

}
