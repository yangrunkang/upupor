package com.upupor.web.aop.checker.page.dto;

import com.upupor.web.aop.checker.BaseChecker;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;

/**
 * @author cruise
 * @createTime 2022-01-20 18:01
 */
@Data
public class PageCheckDto extends BaseChecker {
    private HttpServletRequest request;

    public PageCheckDto(HttpServletRequest request) {
        this.request = request;
    }
}