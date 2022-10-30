/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.web.aspects;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.security.sensitive.AbstractHandleSensitiveWord;
import com.upupor.security.sensitive.SensitiveWord;
import com.upupor.security.sensitive.UpuporSensitive;
import com.upupor.web.aspects.service.sensitive.ContentHandleSensitiveWord;
import com.upupor.web.aspects.service.sensitive.MemberHandleSensitiveWord;
import com.upupor.web.aspects.service.sensitive.RadioHandleSensitiveWord;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.upupor.framework.CcRedisKey.CACHE_SENSITIVE_WORD;

/**
 * 铭感次切面
 *
 * @author Yang Runkang (cruise)
 * @createTime 2022-04-27 19:25
 * @email: yangrunkang53@gmail.com
 */
@Aspect
@Component
@RequiredArgsConstructor
public class SensitiveWordAspect {

    private final static List<AbstractHandleSensitiveWord<?>> ABSTRACT_HANDLE_SENSITIVE_WORD_LIST = new ArrayList<>();

    static {
        ABSTRACT_HANDLE_SENSITIVE_WORD_LIST.add(new ContentHandleSensitiveWord());
        ABSTRACT_HANDLE_SENSITIVE_WORD_LIST.add(new RadioHandleSensitiveWord());
        ABSTRACT_HANDLE_SENSITIVE_WORD_LIST.add(new MemberHandleSensitiveWord());
    }

    private SensitiveWord sensitiveWord;

    @Pointcut("execution(public * com.upupor.service.data.dao.mapper..*.*(..))")
    public void sensitiveAnno() {
    }

    @Around("sensitiveAnno()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object proceed = proceedingJoinPoint.proceed();


        sensitiveWord = JSON.parseObject(RedisUtil.get(CACHE_SENSITIVE_WORD), SensitiveWord.class);
        if (Objects.nonNull(sensitiveWord)) {
            Class clazz = proceedingJoinPoint.getSignature().getDeclaringType();
            Annotation annotation = clazz.getAnnotation(UpuporSensitive.class);
            if (Objects.nonNull(annotation) || clazz.getName().equals(BaseMapper.class.getName())) {
                handle(proceed);
            }
        }

        return proceed;
    }

    private void handle(Object proceed) {
        if (Objects.isNull(proceed)) {
            return;
        }

        List<?> proceedList = convertToList(proceed);
        if (CollectionUtils.isEmpty(proceedList)) {
            return;
        }

        for (AbstractHandleSensitiveWord<?> abstractHandleSensitiveWord : ABSTRACT_HANDLE_SENSITIVE_WORD_LIST) {
            if (abstractHandleSensitiveWord.isHandle(proceedList.get(0).getClass())) {
                abstractHandleSensitiveWord.initData(proceedList, sensitiveWord);
                abstractHandleSensitiveWord.sensitive();
            }
        }

    }

    protected List<?> convertToList(Object proceed) {
        if (proceed instanceof Page) {
            Page<?> proceed1 = (Page<?>) proceed;
            return proceed1.getResult();
        } else if (proceed instanceof List) {
            return (List<?>) proceed;
        } else {
            return Lists.newArrayList(proceed);
        }
    }


}
