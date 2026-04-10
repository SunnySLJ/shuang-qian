package cn.shuang.module.member.config;

import cn.shuang.framework.common.biz.ShuangProProperties;
import cn.shuang.module.member.enums.point.MemberPointBizTypeEnum;
import lombok.Getter;

/**
 * 积分消耗服务
 * <p>
 * 提供统一的积分消耗规则查询和业务类型获取
 *
 * @author shuang-pro
 */
@Getter
public class PointsCostService {

    private final ShuangProProperties properties;

    public PointsCostService(ShuangProProperties properties) {
        this.properties = properties;
    }

    // ========== 积分消耗查询 ==========

    /**
     * 获取图片生成消耗积分
     */
    public Integer getImageCost() {
        return properties.getPoints().getImageCost();
    }

    /**
     * 获取视频生成消耗积分（根据时长）
     *
     * @param duration 秒数（5、15、30）
     */
    public Integer getVideoCost(Integer duration) {
        if (duration == null) {
            return properties.getPoints().getVideo5sCost();
        }
        if (duration <= 5) {
            return properties.getPoints().getVideo5sCost();
        } else if (duration <= 15) {
            return properties.getPoints().getVideo15sCost();
        } else {
            return properties.getPoints().getVideo30sCost();
        }
    }

    /**
     * 获取视频拆解消耗积分
     */
    public Integer getVideoAnalyzeCost() {
        return properties.getPoints().getVideoAnalyzeCost();
    }

    // ========== 业务类型获取 ==========

    /**
     * 获取 AI 图片生成的业务类型
     */
    public MemberPointBizTypeEnum getAiImageBizType() {
        return MemberPointBizTypeEnum.AI_IMAGE;
    }

    /**
     * 获取 AI 视频生成的业务类型
     */
    public MemberPointBizTypeEnum getAiVideoBizType() {
        return MemberPointBizTypeEnum.AI_VIDEO;
    }

    /**
     * 获取充值业务类型
     */
    public MemberPointBizTypeEnum getRechargeBizType() {
        return MemberPointBizTypeEnum.RECHARGE;
    }

    // ========== 代理分佣配置查询 ==========

    /**
     * 获取一级代理分佣比例（百分比）
     */
    public Integer getLevel1CommissionRate() {
        return properties.getAgency().getLevel1CommissionRate();
    }

    /**
     * 获取二级代理分佣比例（百分比）
     */
    public Integer getLevel2CommissionRate() {
        return properties.getAgency().getLevel2CommissionRate();
    }

    /**
     * 计算分佣金额
     *
     * @param amount  原金额
     * @param level   代理等级（1=一级，2=二级）
     */
    public Integer calculateCommission(Integer amount, Integer level) {
        if (level == null || level < 1) {
            return 0;
        }
        Integer rate = level == 1 ? getLevel1CommissionRate() : getLevel2CommissionRate();
        return amount * rate / 100;
    }

    // ========== 充值配置查询 ==========

    /**
     * 获取每元可购积分数量
     */
    public Integer getPricePerYuan() {
        return properties.getRecharge().getPricePerYuan();
    }

    /**
     * 计算充值金额对应积分
     *
     * @param yuan 元
     */
    public Integer calculatePoints(Double yuan) {
        if (yuan == null || yuan <= 0) {
            return 0;
        }
        return (int) (yuan * getPricePerYuan());
    }

    /**
     * 计算积分对应充值金额
     *
     * @param points 积分
     */
    public Double calculateYuan(Integer points) {
        if (points == null || points <= 0) {
            return 0.0;
        }
        return (double) points / getPricePerYuan();
    }
}