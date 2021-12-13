package com.upupor.web.aop;

import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.config.UpuporConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.upupor.service.utils.ServletUtils.getSession;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月12日 23:16
 */
@Service
@RequiredArgsConstructor
public class WhiteService {
    private final UpuporConfig upuporConfig;

    public void interfaceWhiteCheck(String servletPath) {
        if (!upuporConfig.getInterfaceWhiteUrlList().contains(servletPath)) {
            checkIsLogin();
        }
    }

    private void checkIsLogin() {
        Object userId = getSession().getAttribute(CcConstant.Session.USER_ID);
        if (Objects.isNull(userId) || CcConstant.Session.UNKNOWN_USER_ID.equals(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_LOGIN);
        }
    }

    public void pageCheck(String servletPath) {
        upuporConfig.getPageCheckUrlList().forEach(page -> {
            if (servletPath.startsWith(page)) {
                checkIsLogin();
            }
        });
    }
}
