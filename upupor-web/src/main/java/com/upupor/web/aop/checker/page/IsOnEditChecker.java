package com.upupor.web.aop.checker.page;

import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.web.aop.checker.page.dto.PageCheckDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author cruise
 * @createTime 2022-01-20 18:01
 */
@Service
@Order(2)
@RequiredArgsConstructor
public class IsOnEditChecker implements PageAspectChecker {

    @Override
    public void check(PageCheckDto pageCheckDto) {
        String servletPath = pageCheckDto.getRequest().getServletPath();
        HttpServletRequest request = pageCheckDto.getRequest();
        if (StringUtils.isNotEmpty(servletPath)) {
            if (servletPath.startsWith("/continue-editor") || servletPath.startsWith("/editor") || servletPath.startsWith("/before-editor")) {
                Cookie[] cookies = request.getCookies();
                if (!ArrayUtils.isEmpty(cookies)) {
                    for (Cookie cookie : cookies) {
                        if ("isOpenEditor".equals(cookie.getName()) && "yes".equals(cookie.getValue())) {
                            throw new BusinessException(ErrorCode.EXISTS_IS_OPENING_EDITOR);
                        }
                    }
                }
            }
        }
    }

}
