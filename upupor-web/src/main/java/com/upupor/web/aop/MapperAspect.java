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
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.security.sensitive.UpuporSensitive;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.dao.entity.Radio;
import com.upupor.service.dto.cache.CacheSensitiveWord;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
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


    @Pointcut("execution(public * com.upupor.service.data.dao.mapper..*.*(..))")
    public void sensitiveAnno() {
    }


    @Around("sensitiveAnno()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object proceed = proceedingJoinPoint.proceed();


        Annotation annotation = proceedingJoinPoint.getSignature().getDeclaringType().getAnnotation(UpuporSensitive.class);
        if (Objects.nonNull(annotation)) {
            handle(proceed);
        }

        return proceed;
    }

    private void handle(Object proceed) {
        if (Objects.isNull(proceed)) {
            return;
        }

        // 处理不同场景的返回值
        if (proceed instanceof Page) {
            Page proceed1 = (Page) proceed;
            List result = proceed1.getResult();
            toSensitive(result);
        } else if (proceed instanceof List) {
            toSensitive((List) proceed);
        } else {
            toSensitive(Lists.newArrayList(proceed));
        }

    }

    private void toSensitive(List result) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }

        String sensitiveJson = RedisUtil.get(CACHE_SENSITIVE_WORD);
        CacheSensitiveWord cacheSensitiveWord = JSON.parseObject(sensitiveJson, CacheSensitiveWord.class);
        if (Objects.isNull(cacheSensitiveWord) || CollectionUtils.isEmpty(cacheSensitiveWord.getWordList())) {
            return;
        }

        for (Object o : result) {
            if (o instanceof Content) {
                Content content = (Content) o;
                content.setTitle(replaceSensitiveWord(content.getTitle(), cacheSensitiveWord));
            }
            if (o instanceof Member) {
                Member member = (Member) o;
                member.setUserName(replaceSensitiveWord(member.getUserName(), cacheSensitiveWord));
            }
            if (o instanceof Radio) {
                Radio radio = (Radio) o;
                radio.setRadioIntro(replaceSensitiveWord(radio.getRadioIntro(), cacheSensitiveWord));
            }
        }
    }

    private String replaceSensitiveWord(String target, CacheSensitiveWord cacheSensitiveWord) {
        for (String sensitiveWord : cacheSensitiveWord.getWordList()) {
            if (target.contains(sensitiveWord)) {
                return target.replace(sensitiveWord, "[*敏感词*]");
            }
        }
        return target;
    }

}
