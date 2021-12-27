package com.upupor.framework.thread;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/09 18:13
 */
@Data
@ConfigurationProperties(prefix = "upupor.pool")
public class ThreadPoolConfig {

    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 最大线程数
     */
    private int maxPoolSize;

    /**
     * 线程池维护线程所允许的空闲时间
     */
    private int keepAliveSeconds;

    /**
     * 队列最大长度 >=mainExecutor.maxSize
     */
    private int queueCapacity;

}
