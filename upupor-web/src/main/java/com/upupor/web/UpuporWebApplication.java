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

package com.upupor.web;


import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.utils.CcUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Upupor启动类
 *
 * @author runkangyang
 */
@Slf4j
@MapperScan("com.upupor.service.dao.mapper")
@ComponentScan("com.upupor")
@SpringBootApplication
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 86400)
@EnableAsync
public class UpuporWebApplication {

    private static volatile boolean running = true;
    private static ConfigurableApplicationContext applicationContext = null;
    public static final String STATIC_SOURCE_VERSION;


    static {
        System.setProperty("druid.mysql.usePingMethod", "false");
        STATIC_SOURCE_VERSION = LocalDateTime.now().toString();
    }

    public static void main(String[] args) {

        startApplication(args);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (applicationContext != null) {
                applicationContext.stop();
                applicationContext.close();
                applicationContext = null;
            }
            synchronized (UpuporWebApplication.class) {
                running = false;
                UpuporWebApplication.class.notifyAll();
            }
        }));

        synchronized (UpuporWebApplication.class) {
            while (running) {
                try {
                    UpuporWebApplication.class.wait();
                } catch (Exception e) {
                    log.error("UpuporWebApplication 启动异常", e);
                }
            }
        }
    }

    private static void startApplication(String[] args) {
        applicationContext = SpringApplication.run(UpuporWebApplication.class, args);
        log.info("UpuporWebApplication 启动成功");
    }
}
