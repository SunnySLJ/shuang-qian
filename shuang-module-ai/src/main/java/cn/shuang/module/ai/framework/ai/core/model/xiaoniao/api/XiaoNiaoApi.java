package cn.shuang.module.ai.framework.ai.core.model.xiaoniao.api;

import java.util.List;
import java.util.Map;

/**
 * 小云雀 AI 视频生成 API 接口
 * <p>
 * 基于火山引擎即梦AI的小云雀营销成片Agent
 * 文档：https://www.volcengine.com/docs/85621/2283654
 *
 * @author shuang-pro
 */
public interface XiaoNiaoApi {

    // ========== 核心方法 ==========

    /**
     * 提交视频生成任务
     *
     * @param request 视频生成请求
     * @return 任务提交响应
     */
    VideoTaskResponse submitTask(VideoTaskRequest request);

    /**
     * 查询任务状态
     *
     * @param taskId 任务 ID
     * @return 任务查询响应
     */
    TaskStatusResponse queryTask(String taskId);

    /**
     * 轮询等待任务完成
     *
     * @param taskId       任务 ID
     * @param maxWaitTime  最大等待时间（毫秒）
     * @param pollInterval 轮询间隔（毫秒）
     * @return 最终任务状态
     */
    TaskStatusResponse waitForTaskComplete(String taskId, long maxWaitTime, long pollInterval);

    // ========== 请求对象 ==========

    /**
     * 视频生成任务提交请求
     */
    record VideoTaskRequest(
            /**
             * 视频主题/描述
             */
            String subject,
            /**
             * 视频时长（秒）：5、10、15、30
             */
            Integer duration,
            /**
             * 视频宽高比：16:9、9:16、1:1
             */
            String ratio,
            /**
             * 风格类型：
             * - vlog_travel: Vlog旅行
             * - product_showcase: 产品展示
             * - lifestyle:生活方式
             * - tech_review: 科技评测
             * - custom: 自定义
             */
            String styleType,
            /**
             * 脚本内容（可选，AI自动生成）
             */
            String script,
            /**
             * 背景音乐URL（可选）
             */
            String bgmUrl,
            /**
             * 水印文本（可选）
             */
            String watermarkText,
            /**
             * 回调地址（可选）
             */
            String callbackUrl,
            /**
             * 扩展参数
             */
            Map<String, Object> extra
    ) {
    }

    // ========== 响应对象 ==========

    /**
     * 视频任务提交响应
     */
    record VideoTaskResponse(
            /**
             * 状态码
             */
            Integer code,
            /**
             * 状态描述
             */
            String message,
            /**
             * 返回数据
             */
            VideoTaskData data
    ) {
        public record VideoTaskData(
                /**
                 * 任务 ID
                 */
                String taskId,
                /**
                 * 预估等待时间（秒）
                 */
                Integer estimatedTime
        ) {
        }
    }

    /**
     * 任务状态查询响应
     */
    record TaskStatusResponse(
            /**
             * 状态码
             */
            Integer code,
            /**
             * 状态描述
             */
            String message,
            /**
             * 返回数据
             */
            TaskStatusData data
    ) {
        public record TaskStatusData(
                /**
                 * 任务 ID
                 */
                String taskId,
                /**
                 * 任务状态：
                 * - Pending: 待处理
                 * - Running: 处理中
                 * - Succeeded: 成功
                 * - Failed: 失败
                 */
                String status,
                /**
                 * 视频 URL（成功时返回）
                 */
                String videoUrl,
                /**
                 * 封面 URL（成功时返回）
                 */
                String coverUrl,
                /**
                 * 失败原因
                 */
                String failReason,
                /**
                 * 进度百分比（0-100）
                 */
                Integer progress,
                /**
                 * 消耗积分（成功时返回）
                 */
                Integer credits,
                /**
                 * 生成片段列表
                 */
                List<VideoSegment> segments
        ) {
        }
    }

    /**
     * 视频片段
     */
    record VideoSegment(
            /**
             * 片段序号
             */
            Integer index,
            /**
             * 片段描述
             */
            String description,
            /**
             * 片段视频 URL
             */
            String videoUrl,
            /**
             * 片段时长（秒）
             */
            Integer duration,
            /**
             * 脚本内容
             */
            String script,
            /**
             * 旁白文本
             */
            String narration
    ) {
    }
}
