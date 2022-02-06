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

package com.upupor.service.utils;

import com.upupor.framework.CcConstant;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * Servlet工具类
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/29 12:02
 */
public class ServletUtils {


    public static String getUserId() {
        return getSession().getAttribute(CcConstant.Session.USER_ID).toString();
    }

    /**
     * 获取Session
     *
     * @return
     */
    public static HttpSession getSession() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            throw new BusinessException(ErrorCode.GET_SESSION_FAILED);
        }
        HttpServletRequest request = requestAttributes.getRequest();
        return request.getSession();
    }

    /**
     * 获取 HttpServlet 请求
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            throw new BusinessException(ErrorCode.GET_SESSION_FAILED);
        }
        return requestAttributes.getRequest();
    }


    public static void checkOperatePermission(String reqUserId) {

        // 检查操作对象存储的用户id是否是当前用户的用户id
        if (StringUtils.isEmpty(reqUserId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }
        if (!reqUserId.equals(ServletUtils.getUserId())) {
            throw new BusinessException(ErrorCode.YOU_HAVE_NO_PERMISSION);
        }
    }


}
