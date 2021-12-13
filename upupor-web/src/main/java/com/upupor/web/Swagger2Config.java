package com.upupor.web;


import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Swagger2配置
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/11/26 10:45
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Profile({"dev"})
@Import(BeanValidatorPluginsConfiguration.class)
public class Swagger2Config {

    @Value("${server.port}")
    private Integer serverPort;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.upupor.web.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        String hostAddress = "localhost";
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostAddress = address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // 服务访问地址
        String url = String.format("http://%s:%s", hostAddress, serverPort);

        return new ApiInfoBuilder()
                .title("Upupor接口管理")
                .termsOfServiceUrl(url)
                .version("v0.0.1")
                .build();
    }
}