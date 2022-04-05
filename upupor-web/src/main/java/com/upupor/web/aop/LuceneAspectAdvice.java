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

package com.upupor.web.aop;

import com.upupor.lucene.AbstractGetTargetId;
import com.upupor.lucene.LuceneEvent;
import com.upupor.lucene.UpuporLucene;
import com.upupor.service.business.lucene.get_id.ContentHandler;
import com.upupor.service.business.lucene.get_id.MemberHandler;
import com.upupor.service.business.lucene.get_id.RadioHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年04月04日 10:27
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LuceneAspectAdvice {
    private final ApplicationEventPublisher publisher;

    private static final List<AbstractGetTargetId> abstractGetTargetIdList = new ArrayList<>();

    static {
        abstractGetTargetIdList.add(new ContentHandler());
        abstractGetTargetIdList.add(new MemberHandler());
        abstractGetTargetIdList.add(new RadioHandler());
    }

    /**
     * 以 controller 包下定义的所有请求为切入点
     */
    @Pointcut("@annotation(com.upupor.lucene.UpuporLucene)")
    public void upuporLuceneAspect() {
    }

    @Before("upuporLuceneAspect() && @annotation(annotation)")
    public void doBefore(JoinPoint joinPoint, UpuporLucene annotation) {
        System.out.println();
    }

    /**
     * 环绕
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("upuporLuceneAspect() && @annotation(annotation)")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint, UpuporLucene annotation) throws Throwable {
        Object proceed = proceedingJoinPoint.proceed();

        LuceneEvent luceneEvent = new LuceneEvent();
        luceneEvent.setDataType(annotation.dataType());
        luceneEvent.setOperationType(annotation.operationType());

        // 获取目标Id
        for (AbstractGetTargetId abstractGetTargetId : abstractGetTargetIdList) {
            abstractGetTargetId.init(proceed);
            String targetId = abstractGetTargetId.targetId();
            if (StringUtils.isNotEmpty(targetId)) {
                luceneEvent.setTargetId(targetId);
                break;
            }
        }

        if(StringUtils.isNotEmpty(luceneEvent.getTargetId())){
            publisher.publishEvent(luceneEvent);
        }

        return proceed;
    }

    @After("upuporLuceneAspect()")
    public void doAfter() {

    }

}
