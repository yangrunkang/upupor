package com.upupor.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月12日 22:37
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "upupor")
public class UpuporConfig {
    /**
     * 不用校验是否登录的url
     */
    private List<String> interfaceWhiteUrlList;

    /**
     * 页面需要校验登录的url
     */
    private List<String> pageCheckUrlList;


}
