package com.upupor.web.aop.checker.controller.dto;

import com.upupor.web.aop.checker.BaseChecker;
import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;

import javax.servlet.http.HttpServletRequest;

/**
 * @author cruise
 * @createTime 2022-01-20 18:01
 */
@Data
public class ControllerCheckerDto extends BaseChecker {
    private HttpServletRequest request;
    private ProceedingJoinPoint proceedingJoinPoint;

    public ControllerCheckerDto(HttpServletRequest request, ProceedingJoinPoint proceedingJoinPoint) {
        this.request = request;
        this.proceedingJoinPoint = proceedingJoinPoint;
    }
}
