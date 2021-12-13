package com.upupor.service.listener.eventbus;

import com.upupor.service.dto.email.SendEmailEvent;
import com.upupor.service.utils.CcEmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.StringUtils;

/**
 * 消息事件总线
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 19:21
 */
@Slf4j
@Component
public class EmailListener {

    @EventListener
    @Async
    public void sendEmail(SendEmailEvent sendEmailEvent) {
        if (StringUtils.isEmpty(sendEmailEvent.getToAddress())) {
            log.error("发件地址为空");
            return;
        }
        if (StringUtils.isEmpty(sendEmailEvent.getTitle())) {
            log.error("发件标题为空");
            return;
        }
        if (StringUtils.isEmpty(sendEmailEvent.getContent())) {
            log.error("发件内容为空");
            return;
        }

        // 内容重新使用模板内容
        CcEmailUtils.sendEmail(sendEmailEvent);
    }


}
