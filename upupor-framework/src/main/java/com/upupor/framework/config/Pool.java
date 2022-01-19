package com.upupor.framework.config;

import lombok.Data;

/**
 * @author cruise
 * @createTime 2022-01-19 11:58
 */
@Data
public class Pool {
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer keepAliveSeconds;
    private Integer queueCapacity;
}