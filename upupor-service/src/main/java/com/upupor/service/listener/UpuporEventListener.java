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

import com.upupor.service.listener.event.GenerateGoogleSiteMapEvent;
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
 * Upupor初始化完毕监听
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

        // 程序初始化完毕
        if (event instanceof ContextRefreshedEvent) {

        }

        // 程序已经启动
        if (event instanceof ApplicationStartedEvent) {
            // url 打印
            printUrl();
            // 生成站点地图
            eventPublisher.publishEvent(new GenerateGoogleSiteMapEvent());
        }

        // 程序已经关闭
        if (event instanceof ContextClosedEvent) {

        }
    }

    /**
     * 打印一些url
     */
    private void printUrl() {
        String hostAddress = "localhost";
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostAddress = address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        log.info("Upupor 访问地址: http://{}:{}", hostAddress, serverPort);
        log.info("Upupor swagger2 接口地址: http://{}:{}/{}", hostAddress, serverPort, "doc.html");
    }
}
