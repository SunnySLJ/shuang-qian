package cn.shuang.module.system.dal.mysql.mail;

import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.framework.mybatis.core.mapper.BaseMapperX;
import cn.shuang.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.shuang.module.system.controller.admin.mail.vo.account.MailAccountPageReqVO;
import cn.shuang.module.system.dal.dataobject.mail.MailAccountDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailAccountMapper extends BaseMapperX<MailAccountDO> {

    default PageResult<MailAccountDO> selectPage(MailAccountPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<MailAccountDO>()
                .likeIfPresent(MailAccountDO::getMail, pageReqVO.getMail())
                .likeIfPresent(MailAccountDO::getUsername , pageReqVO.getUsername())
                .orderByDesc(MailAccountDO::getId));
    }

}
