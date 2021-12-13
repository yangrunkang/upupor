package com.upupor.service.utils;

import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
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
