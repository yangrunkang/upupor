package com.upupor.framework.thread;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 时间总线线程池
 *
 * @author runkangyang (cruise)
 * @date 2020.01.18 11:24
 */
@Configuration
@EnableConfigurationProperties({EventPoolConfig.class})
@RequiredArgsConstructor
public class EventBusThreadPool {
    public static final String UPUPOR_THREAD_POOL = "upuporThreadPool";
    private final EventPoolConfig eventPoolConfig;

    @Bean(UPUPOR_THREAD_POOL)
    public Executor eventThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程池大小
        executor.setCorePoolSize(eventPoolConfig.getCorePoolSize());
        // 最大线程数
        executor.setMaxPoolSize(eventPoolConfig.getMaxPoolSize());
        // 队列容量
        executor.setQueueCapacity(eventPoolConfig.getQueueCapacity());
        // 活跃时间
        executor.setKeepAliveSeconds(eventPoolConfig.getKeepAliveSeconds());
        // 线程名字前缀
        executor.setThreadNamePrefix(UPUPOR_THREAD_POOL);
        // 线程池对拒绝任务(无线程可用)的处理策略 ThreadPoolExecutor.CallerRunsPolicy策略 ,调用者的线程会执行该任务,如果执行器已关闭,则丢弃
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }


}
