package com.upupor.web.aop.checker.page;

import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.web.aop.checker.page.dto.PageCheckDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @author cruise
 * @createTime 2022-01-20 18:01
 */
@Service
@Order(3)
@RequiredArgsConstructor
public class AdminPageChecker implements PageAspectChecker {
    private  final MemberService memberService;

    @Override
    public void check(PageCheckDto pageCheckDto) {
        String servletPath = pageCheckDto.getRequest().getServletPath();
        // 如果路径中包含 /user/admin,需要检验是否是管理员
        String adminUrl = "/user/admin";
        if (servletPath.startsWith(adminUrl)) {
            memberService.checkIsAdmin();
        }
    }
}
