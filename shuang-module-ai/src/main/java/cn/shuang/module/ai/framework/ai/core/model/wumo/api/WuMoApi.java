package cn.shuang.module.ai.framework.ai.core.model.wumo.api;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

/**
 * 舞墨 AI API 接口定义
 * <p>
 * 参考文档：https://api.wuyinkeji.com/docs
 *
 * @author shuang-pro
 */
public interface WuMoApi {

    /**
     * 图片生成
     *
     * @param request 图片生成请求
     * @return 图片生成响应
     */
    ImageGenerateResponse generateImage(ImageGenerateRequest request);

    /**
     * 视频生成
     *
     * @param request 视频生成请求
     * @return 视频生成响应
     */
    VideoGenerateResponse generateVideo(VideoGenerateRequest request);

    /**
     * 图生视频
     *
     * @param referenceImage 参考图片 Base64
     * @param prompt 提示词
     * @param model 模型名称
     * @return 视频生成响应
     */
    VideoGenerateResponse imageToVideo(String referenceImage, String prompt, String model);

    /**
     * 任务查询
     *
     * @param taskId 任务 ID
     * @return 任务查询响应
     */
    TaskQueryResponse queryTask(String taskId);

    /**
     * 轮询等待任务完成
     *
     * @param taskId 任务 ID
     * @param maxWaitTime 最大等待时间（毫秒）
     * @param pollInterval 轮询间隔（毫秒）
     * @return 最终任务状态
     */
    TaskQueryResponse waitForTaskComplete(String taskId, long maxWaitTime, long pollInterval);

    /**
     * 图片生成请求
     */
    @Builder
    @Jacksonized
    record ImageGenerateRequest(
            /**
             * 提示词
             */
            String prompt,
            /**
             * 负向提示词
             */
            String negativePrompt,
            /**
             * 图片宽度
             */
            Integer width,
            /**
             * 图片高度
             */
            Integer height,
            /**
             * 生成数量
             */
            Integer n,
            /**
             * 模型名称
             */
            String model,
            /**
             * 风格预设
             */
            String stylePreset,
            /**
             * 参考图片 URL（Base64）
             */
            String referenceImage,
            /**
             * 扩展参数
             */
            Map<String, Object> extra
    ) {
    }

    /**
     * 视频生成请求
     */
    @Builder
    @Jacksonized
    record VideoGenerateRequest(
            /**
             * 提示词
             */
            String prompt,
            /**
             * 参考图片 URL（Base64）
             */
            String referenceImage,
            /**
             * 视频时长（秒）
             */
            Integer duration,
            /**
             * 模型名称
             */
            String model,
            /**
             * 扩展参数
             */
            Map<String, Object> extra
    ) {
    }

    /**
     * 图片生成响应
     */
    record ImageGenerateResponse(
            /**
             * 状态码：0 表示成功
             */
            Integer code,
            /**
             * 状态描述
             */
            String message,
            /**
             * 返回数据
             */
            ImageData data
    ) {
        public record ImageData(
                /**
                 * 任务 ID
                 */
                String taskId,
                /**
                 * 图片 URL 列表
                 */
                List<String> imageUrl,
                /**
                 * 图片 Base64 列表
                 */
                List<String> imageBase64,
                /**
                 * 生成状态：PENDING-待处理，PROCESSING-处理中，SUCCESS-成功，FAILED-失败
                 */
                String status,
                /**
                 * 失败原因
                 */
                String failReason
        ) {
        }
    }

    /**
     * 视频生成响应
     */
    record VideoGenerateResponse(
            /**
             * 状态码：0 表示成功
             */
            Integer code,
            /**
             * 状态描述
             */
            String message,
            /**
             * 返回数据
             */
            VideoData data
    ) {
        public record VideoData(
                /**
                 * 任务 ID
                 */
                String taskId,
                /**
                 * 视频 URL
                 */
                String videoUrl,
                /**
                 * 视频封面 URL
                 */
                String coverUrl,
                /**
                 * 生成状态：PENDING-待处理，PROCESSING-处理中，SUCCESS-成功，FAILED-失败
                 */
                String status,
                /**
                 * 失败原因
                 */
                String failReason
        ) {
        }
    }

    /**
     * 任务查询请求
     */
    record TaskQueryRequest(
            /**
             * 任务 ID
             */
            String taskId
    ) {
    }

    /**
     * 任务查询响应
     */
    record TaskQueryResponse(
            /**
             * 状态码：0 表示成功
             */
            Integer code,
            /**
             * 状态描述
             */
            String message,
            /**
             * 返回数据
             */
            TaskData data
    ) {
        public record TaskData(
                /**
                 * 任务 ID
                 */
                String taskId,
                /**
                 * 任务类型：image-图片，video-视频
                 */
                String taskType,
                /**
                 * 生成状态：PENDING-待处理，PROCESSING-处理中，SUCCESS-成功，FAILED-失败
                 */
                String status,
                /**
                 * 结果 URL（图片或视频）
                 */
                String resultUrl,
                /**
                 * 失败原因
                 */
                String failReason,
                /**
                 * 进度百分比
                 */
                Integer progress
        ) {
        }
    }

}
