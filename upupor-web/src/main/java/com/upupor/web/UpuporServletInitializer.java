package com.upupor.web;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * upupor初始化
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/03 17:16
 */
public class UpuporServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(UpuporWebApplication.class);
    }
}
