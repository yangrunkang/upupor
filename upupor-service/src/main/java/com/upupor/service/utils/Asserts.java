package com.upupor.service.utils;

import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.ContentExtend;

import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月12日 07:12
 */
public class Asserts {
    public static void notNull(Object target, ErrorCode errorCode) {
        if (Objects.isNull(target)) {
            throw new BusinessException(errorCode);
        }

        if (target instanceof Content) {
            if (Objects.isNull(((Content) target).getId())) {
                throw new BusinessException(errorCode);
            }
        }
        if (target instanceof ContentExtend) {
            if (Objects.isNull(((ContentExtend) target).getId())) {
                throw new BusinessException(errorCode);
            }
        }
    }
}
