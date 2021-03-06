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

package com.upupor.service.listener;

import com.upupor.framework.utils.SystemUtil;
import com.upupor.service.listener.event.GenerateGoogleSiteMapEvent;
import com.upupor.service.listener.event.InitLuceneIndexEvent;
import com.upupor.service.listener.event.InitSensitiveWordEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Upupor?????????????????????
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/02 00:08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpuporEventListener implements ApplicationListener {

    @Value("${server.port}")
    private Integer serverPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        // ?????????????????????
        if (event instanceof ContextRefreshedEvent) {

        }

        // ??????????????????
        if (event instanceof ApplicationStartedEvent) {
            // url ??????
            printUrl();
            // ??????????????????
            eventPublisher.publishEvent(new GenerateGoogleSiteMapEvent());
            // ????????????
            eventPublisher.publishEvent(new InitLuceneIndexEvent());
            // ??????????????????
            eventPublisher.publishEvent(new InitSensitiveWordEvent());

        }

        // ??????????????????
        if (event instanceof ContextClosedEvent) {

        }
    }

    /**
     * ????????????url
     */
    private void printUrl() {
        String hostAddress = SystemUtil.getHostAddress();
        log.info("Upupor ????????????: http://{}:{}", hostAddress, serverPort);
        log.info("Upupor swagger2 ????????????: http://{}:{}/{}", hostAddress, serverPort, "doc.html");
    }
}
