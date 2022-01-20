package com.upupor.web.aop.view;

import com.upupor.framework.CcConstant;
import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.outer.req.ListMessageReq;
import com.upupor.service.types.MessageStatus;
import com.upupor.service.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 获取未读消息
 * @author cruise
 * @createTime 2022-01-19 18:01
 */
@RequiredArgsConstructor
@Service
@Order(2)
public class UnReadMessageCount implements PrepareData {
    private final MessageService messageService;
    @Override
    public void prepare(ViewData viewData) {
        ModelAndView modelAndView = viewData.getModelAndView();
        try {
            ListMessageReq listMessageReq = new ListMessageReq();
            listMessageReq.setUserId(ServletUtils.getUserId());
            listMessageReq.setStatus(MessageStatus.UN_READ);
            Integer unReadCount = messageService.unReadMessageTotal(listMessageReq);
            modelAndView.addObject(CcConstant.UNREAD_MSG_COUNT, unReadCount);
            // 将维度消息数显示在title
            Object title = modelAndView.getModelMap().getAttribute(CcConstant.SeoKey.TITLE);
            if (!Objects.isNull(title)) {
                if (unReadCount > 0) {
                    modelAndView.addObject(CcConstant.SeoKey.TITLE, "(" + unReadCount + ")" + title);
                }
            }

        } catch (Exception e) {
            // 用户未登录异常 不处理
            modelAndView.addObject(CcConstant.UNREAD_MSG_COUNT, BigDecimal.ZERO);
        }
    }
}
