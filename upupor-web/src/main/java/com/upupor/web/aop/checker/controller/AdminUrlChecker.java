package com.upupor.web.aop.checker.controller;

import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.web.aop.checker.controller.dto.ControllerCheckerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @author cruise
 * @createTime 2022-01-20 18:01
 */
@Service
@Order(1)
@RequiredArgsConstructor
public class AdminUrlChecker implements ControllerAspectChecker {
    private  final MemberService memberService;

    @Override
    public void check(ControllerCheckerDto controllerCheckerDto) {
        String servletPath = controllerCheckerDto.getRequest().getServletPath();
        String adminUrl = "/admin";
        if (servletPath.startsWith(adminUrl)) {
            memberService.checkIsAdmin();
        }
    }
}
