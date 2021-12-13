package com.upupor.web;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

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


    static {
        System.setProperty("druid.mysql.usePingMethod", "false");
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
