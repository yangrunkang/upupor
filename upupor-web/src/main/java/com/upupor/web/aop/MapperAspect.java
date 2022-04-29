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

package com.upupor.web.aop;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.security.sensitive.UpuporSensitive;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.dao.entity.Radio;
import com.upupor.service.dto.cache.CacheSensitiveWord;
import com.upupor.web.aop.mapper.AbstractMapperHandle;
import com.upupor.web.aop.mapper.ContentMapperHandle;
import com.upupor.web.aop.mapper.MemberMapperHandle;
import com.upupor.web.aop.mapper.RadioMapperHandle;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.upupor.framework.CcConstant.CvCache.CACHE_SENSITIVE_WORD;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-04-27 19:25
 * @email: yangrunkang53@gmail.com
 */
@Aspect
@Component
@RequiredArgsConstructor
public class MapperAspect {

    private final static List<AbstractMapperHandle<?>> abstractMapperHandleList = new ArrayList<>();

    static {
        abstractMapperHandleList.add(new ContentMapperHandle());
        abstractMapperHandleList.add(new RadioMapperHandle());
        abstractMapperHandleList.add(new MemberMapperHandle());
    }

    private CacheSensitiveWord cacheSensitiveWord;

    @Pointcut("execution(public * com.upupor.service.data.dao.mapper..*.*(..))")
    public void sensitiveAnno() {
    }

    public static void main(String[] args) {

    }

    @Around("sensitiveAnno()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object proceed = proceedingJoinPoint.proceed();


        cacheSensitiveWord = JSON.parseObject(RedisUtil.get(CACHE_SENSITIVE_WORD), CacheSensitiveWord.class);
        if(Objects.nonNull(cacheSensitiveWord)){
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
        if(CollectionUtils.isEmpty(proceedList)){
            return;
        }

        for (AbstractMapperHandle<?> abstractMapperHandle : abstractMapperHandleList) {
            if (abstractMapperHandle.isHandle(proceedList.get(0).getClass())) {
                abstractMapperHandle.initData(proceedList, cacheSensitiveWord);
                abstractMapperHandle.sensitive();
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
