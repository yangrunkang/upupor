package com.upupor.service.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import static com.upupor.service.common.CcConstant.URL_404;
import static com.upupor.service.common.CcConstant.URL_500;

/**
 * @author YangRunkang(cruise)
 * @date 2020/05/01 00:02
 */
@Configuration
public class ErrorPageConfig implements ErrorPageRegistrar {

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        // 错误页面映射到真正的 Controller url
        ErrorPage error404 = new ErrorPage(HttpStatus.NOT_FOUND, URL_404);
        ErrorPage error500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, URL_500);
        registry.addErrorPages(error404, error500);
    }
}
