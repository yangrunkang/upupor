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
