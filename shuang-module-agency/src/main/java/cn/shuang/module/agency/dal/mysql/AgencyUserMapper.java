package cn.shuang.module.agency.dal.mysql;

import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.framework.mybatis.core.mapper.BaseMapperX;
import cn.shuang.module.agency.dal.dataobject.AgencyUserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 代理用户 Mapper
 *
 * @author shuang-pro
 */
@Mapper
public interface AgencyUserMapper extends BaseMapperX<AgencyUserDO> {

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
    @org.apache.ibatis.annotations.Select("<script>" +
            "SELECT au.*, su.nickname " +
            "FROM agency_user au " +
            "LEFT JOIN system_users su ON au.user_id = su.id " +
            "WHERE au.parent_agency_id = #{parentAgencyId} AND au.deleted = 0 " +
            "ORDER BY au.id DESC" +
            "</script>")
    java.util.List<AgencyUserDO> selectByParentAgencyId(@org.apache.ibatis.annotations.Param("parentAgencyId") Long parentAgencyId);

    /**
     * 统计直推人数
     *
     * @param userId 用户 ID
     * @return 人数
     */
    Integer countDirectInvite(@Param("userId") Long userId);

    /**
     * 增加代理累计佣金
     *
     * @param id     代理 ID
     * @param amount 增加的佣金金额（积分，单位：分）
     * @return 受影响的行数
     */
    int addTotalCommission(@Param("id") Long id, @Param("amount") Integer amount);

    /**
     * 分页查询代理用户列表（联查 nickname）
     *
     * @param nickname 用户昵称（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT au.*, su.nickname " +
            "FROM agency_user au " +
            "LEFT JOIN system_users su ON au.user_id = su.id " +
            "WHERE au.deleted = 0 " +
            "<if test='nickname != null and nickname != \"\"'>" +
            "  AND su.nickname LIKE CONCAT('%', #{nickname}, '%') " +
            "</if>" +
            "ORDER BY au.id DESC" +
            "</script>")
    PageResult<AgencyUserDO> selectPageByNickname(@Param("nickname") String nickname);

}
