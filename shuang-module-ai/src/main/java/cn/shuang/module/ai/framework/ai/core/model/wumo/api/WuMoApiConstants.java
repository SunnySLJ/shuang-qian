package cn.shuang.module.ai.framework.ai.core.model.wumo.api;

/**
 * 舞墨 AI API 常量
 * <p>
 * 文档地址：https://api.wuyinkeji.com/docs
 *
 * @author shuang-pro
 */
public interface WuMoApiConstants {

    /**
     * API 基础 URL
     */
    String BASE_URL = "https://api.wuyinkeji.com";

    /**
     * 图片生成接口
     */
    String IMAGE_GENERATE_ENDPOINT = "/image/generate";

    /**
     * 视频生成接口
     */
    String VIDEO_GENERATE_ENDPOINT = "/video/generate";

    /**
     * 图生视频接口
     */
    String IMAGE_TO_VIDEO_ENDPOINT = "/video/image-to-video";

    /**
     * 黄金 6 秒拼接接口
     */
    String GOLDEN_6S_ENDPOINT = "/video/golden-6s";

    /**
     * 视频分析接口
     */
    String VIDEO_ANALYZE_ENDPOINT = "/video/analyze";

    /**
     * 任务查询接口
     */
    String TASK_QUERY_ENDPOINT = "/task/query";

    // ========== 模型列表 ==========

    /**
     * 图片生成模型
     */
    String IMAGE_MODEL_FLUX_1 = "FLUX.1";
    String IMAGE_MODEL_SD_XL = "SD-XL";
    String IMAGE_MODEL_MIDJOURNEY = "Midjourney";

    /**
     * 视频生成模型
     */
    String VIDEO_MODEL_RUNWAY = "Runway";
    String VIDEO_MODEL_PIKA = "Pika";
    String VIDEO_MODEL_KELING = "Keling";
    String VIDEO_MODEL_SEEWARE = "Seeware";

    // ========== 默认参数 ==========

    /**
     * 默认图片宽度
     */
    int DEFAULT_IMAGE_WIDTH = 1024;

    /**
     * 默认图片高度
     */
    int DEFAULT_IMAGE_HEIGHT = 1024;

    /**
     * 默认生成数量
     */
    int DEFAULT_N = 1;

}
