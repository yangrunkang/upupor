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

package com.upupor.web.listener;

import com.upupor.framework.utils.SystemUtil;
import com.upupor.service.listener.event.GenerateGoogleSiteMapEvent;
import com.upupor.service.listener.event.InitLuceneIndexEvent;
import com.upupor.service.listener.event.InitSensitiveWordEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author Yang Runkang (cruise)
 * @date 2023年03月30日 11:32
 * @email: yangrunkang53@gmail.com
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class UpuporStartupRunner implements CommandLineRunner {
    @Value("${server.port}")
    private Integer serverPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void run(String... args) throws Exception {
        log.info("upupor已启动");
        // 生成站点地图
        eventPublisher.publishEvent(new GenerateGoogleSiteMapEvent());
        // 生成索引
        eventPublisher.publishEvent(new InitLuceneIndexEvent());
        // 初始化敏感词
        eventPublisher.publishEvent(new InitSensitiveWordEvent());
        // 初始化压缩内容
//        eventPublisher.publishEvent(new CompressContentEvent());
    }

    /**
     * 打印一些url
     */
    private void printUrl() {
        String hostAddress = SystemUtil.getHostAddress();
        log.info("Upupor 访问地址: http://{}:{}", hostAddress, serverPort);
        log.info("Upupor swagger2 接口地址: http://{}:{}/{}", hostAddress, serverPort, "doc.html");
    }
}
