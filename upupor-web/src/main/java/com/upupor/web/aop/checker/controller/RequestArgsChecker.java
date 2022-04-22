/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.web.aop.checker.controller;

import com.upupor.framework.CcConstant;
import com.upupor.framework.utils.ValidationUtil;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
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
