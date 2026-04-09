package cn.shuang.module.ai.dal.dataobject.asset;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户资产 DO
 *
 * @author shuang-pro
 */
@TableName(value = "ai_user_asset", autoResultMap = true)
@KeySequence("ai_user_asset_seq")
@Data
public class UserAssetDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 资产类型：1-图片，2-视频，3-音频
     */
    private Integer assetType;

    /**
     * 资源 URL
     */
    private String resourceUrl;

    /**
     * 缩略图 URL
     */
    private String thumbnailUrl;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 时长（秒，视频/音频）
     */
    private Integer duration;

    /**
     * 宽度
     */
    private Integer width;

    /**
     * 高度
     */
    private Integer height;

    /**
     * 分组 ID
     */
    private Long groupId;

}