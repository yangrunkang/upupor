package com.upupor.web.aop.checker.controller;

import com.upupor.framework.CcConstant;
import com.upupor.framework.utils.ValidationUtil;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.web.aop.checker.controller.dto.ControllerCheckerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author cruise
 * @createTime 2022-01-20 18:01
 */
@Service
@Order(3)
@RequiredArgsConstructor
public class RequestArgsChecker implements ControllerAspectChecker {
    @Override
    public void check(ControllerCheckerDto controllerCheckerDto) {
        Object[] args = controllerCheckerDto.getProceedingJoinPoint().getArgs();
        for (Object arg : args) {
            ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(arg);
            if (!CollectionUtils.isEmpty(validResult.getAllErrors())) {
                // 格式化显示错误信息
                List<ValidationUtil.ErrorMessage> allErrors = validResult.getAllErrors();
                StringBuilder sb = new StringBuilder();
                sb.append("错误信息:").append(CcConstant.BREAK_LINE);
                for (int i = 0; i < allErrors.size(); i++) {
                    sb.append(i + 1).append(CcConstant.ONE_DOTS).append(allErrors.get(i).getMessage()).append(CcConstant.BREAK_LINE);
                }
                throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), sb.toString());
            }
        }
    }
}
