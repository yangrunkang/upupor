package com.upupor.web.aop.view_data;

import com.upupor.framework.CcConstant;
import com.upupor.service.business.aggregation.service.ContentService;
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
 * 草稿数量
 * @author cruise
 * @createTime 2022-01-19 18:01
 */
@RequiredArgsConstructor
@Service
@Order(3)
public class DraftCount implements PrepareData {
    private final ContentService contentService;
    @Override
    public void prepare(ModelAndView modelAndView) {
        try {
            String userId = ServletUtils.getUserId();
            Integer draftCount = contentService.countDraft(userId);
            modelAndView.addObject(CcConstant.DRAFT_COUNT, draftCount);
        } catch (Exception e) {
            // 用户未登录异常 不处理
            modelAndView.addObject(CcConstant.UNREAD_MSG_COUNT, BigDecimal.ZERO);
        }
    }
}
