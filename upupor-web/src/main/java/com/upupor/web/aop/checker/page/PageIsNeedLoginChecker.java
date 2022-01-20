package com.upupor.web.aop.checker.page;

import com.upupor.web.aop.WhiteService;
import com.upupor.web.aop.checker.page.dto.PageCheckDto;
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
public class PageIsNeedLoginChecker implements PageAspectChecker {
    private final WhiteService whiteService;

    @Override
    public void check(PageCheckDto pageCheckDto) {
        String servletPath = pageCheckDto.getRequest().getServletPath();
        whiteService.pageCheck(servletPath);
    }

}
