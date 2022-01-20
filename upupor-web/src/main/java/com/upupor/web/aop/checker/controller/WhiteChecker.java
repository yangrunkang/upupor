package com.upupor.web.aop.checker.controller;

import com.upupor.web.aop.WhiteService;
import com.upupor.web.aop.checker.controller.dto.ControllerCheckerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @author cruise
 * @createTime 2022-01-20 18:01
 */
@Service
@Order(2)
@RequiredArgsConstructor
public class WhiteChecker implements ControllerAspectChecker {
    private final WhiteService whiteService;

    @Override
    public void check(ControllerCheckerDto controllerCheckerDto) {
        String servletPath = controllerCheckerDto.getRequest().getServletPath();
        // 如果不在白名单中,需要检验是否登录
        whiteService.interfaceWhiteCheck(servletPath);
    }
}
