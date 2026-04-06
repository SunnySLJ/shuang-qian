package cn.shuang.module.ai.framework.content.context;

import lombok.Builder;
import lombok.Data;

/**
 * 视频生成结果
 * <p>
 * VideoComposerAgent 的输出结果
 */
@Data
@Builder
public class VideoResult {

    /**
     * 生成状态：0-处理中，1-完成，2-失败
     */
    private Integer status;

    /**
     * 输出视频 URL
     */
    private String videoUrl;

    /**
     * 缩略图 URL
     */
    private String thumbnailUrl;

    /**
     * 视频时长（毫秒）
     */
    private Long durationMs;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 生成元数据（分辨率、帧率、编码等）
     */
    private VideoMetadata metadata;

    @Data
    @Builder
    public static class VideoMetadata {
        /**
         * 分辨率（如 "1920x1080"）
         */
        private String resolution;

        /**
         * 帧率（如 "30"）
         */
        private String frameRate;

        /**
         * 编码格式（如 "H.264"）
         */
        private String codec;

        /**
         * 文件大小（字节）
         */
        private Long fileSize;
    }
}
