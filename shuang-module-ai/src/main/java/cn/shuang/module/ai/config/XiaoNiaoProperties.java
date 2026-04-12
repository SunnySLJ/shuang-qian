package cn.shuang.module.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 小云雀 AI 视频生成配置项
 * <p>
 * 基于火山引擎即梦AI的小云雀营销成片Agent
 * 文档：https://www.volcengine.com/docs/85621/2283654
 *
 * @author shuang-pro
 */
@ConfigurationProperties(prefix = "yudao.ai.xiaoniao")
@Data
@Validated
public class XiaoNiaoProperties {

    /**
     * 是否启用小云雀
     */
    private Boolean enable = false;

    /**
     * API Key（从火山引擎获取）
     */
    private String apiKey = "";

    /**
     * AccessKeyId，适用于 OpenAPI AK/SK 鉴权
     */
    private String accessKeyId = "";

    /**
     * SecretAccessKey，适用于 OpenAPI AK/SK 鉴权
     */
    private String secretAccessKey = "";

    /**
     * App ID
     */
    private String appId = "";

    /**
     * API 基础地址
     */
    private String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";

    /**
     * OpenAPI Endpoint
     */
    private String endpoint = "visual.volcengineapi.com";

    /**
     * OpenAPI 服务名
     */
    private String service = "cv";

    /**
     * OpenAPI 区域
     */
    private String region = "cn-north-1";

    /**
     * 提交任务 Action
     */
    private String submitAction = "CVSync2AsyncSubmitTask";

    /**
     * 查询任务 Action
     */
    private String queryAction = "CVSync2AsyncGetResult";

    /**
     * OpenAPI 版本
     */
    private String version = "2022-08-31";

    /**
     * req_key，具体值支持通过配置覆盖
     */
    private String reqKey = "jimeng_video_generation";

    /**
     * 模型名称
     */
    private String model = "video-01-20250515";

    /**
     * 默认画幅比例
     */
    private String defaultRatio = "16:9";

    /**
     * 默认风格类型
     */
    private String defaultStyleType = "product_showcase";

    /**
     * 默认质感等级
     */
    private String defaultQualityLevel = "cinematic";

    /**
     * 轮询间隔（毫秒）
     */
    private Integer pollInterval = 5000;

    /**
     * 最大等待时间（毫秒）
     */
    private Integer maxWaitTime = 300000;

    /**
     * 是否启用
     */
    public boolean isEnabled() {
        boolean bearerEnabled = apiKey != null && !apiKey.isBlank();
        boolean openApiEnabled = accessKeyId != null && !accessKeyId.isBlank()
                && secretAccessKey != null && !secretAccessKey.isBlank();
        return enable != null && enable && (bearerEnabled || openApiEnabled);
    }
}
