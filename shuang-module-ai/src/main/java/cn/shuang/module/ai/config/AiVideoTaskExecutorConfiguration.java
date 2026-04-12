package cn.shuang.module.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * AI 视频任务专用线程池
 * <p>
 * 使用 Alibaba TTL 装饰器，自动传递用户上下文（UserContext、TenantContext 等）到异步线程，
 * 保证在异步线程中能正确获取登录用户信息。
 * <p>
 * 核心参数说明：
 * - corePoolSize: 5，每种视频操作至少需要 5 个并发
 * - maxPoolSize: 20，高峰期允许扩展到 20 个并发
 * - queueCapacity: 100，超过的请求排队，队列满后触发拒绝策略
 * - keepAliveSeconds: 60，空闲线程超过 60 秒回收
 * - threadNamePrefix: "ai-video-"，方便日志追踪
 */
@Configuration
public class AiVideoTaskExecutorConfiguration {

    public static final String VIDEO_TASK_EXECUTOR = "aiVideoTaskExecutor";

    @Bean(VIDEO_TASK_EXECUTOR)
    public Executor aiVideoTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("ai-video-");
        // CallerRunsPolicy：线程池满时由调用线程执行，保证任务不丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
