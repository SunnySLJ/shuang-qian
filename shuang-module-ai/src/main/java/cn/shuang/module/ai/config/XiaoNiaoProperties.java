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
     * App ID
     */
    private String appId = "";

    /**
     * API 基础地址
     */
    private String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";

    /**
     * 模型名称
     */
    private String model = "video-01-20250515";

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
        return enable != null && enable && apiKey != null && !apiKey.isBlank();
    }
}
