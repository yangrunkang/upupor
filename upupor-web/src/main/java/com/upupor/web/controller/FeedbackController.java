package com.upupor.web.controller;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.*;
import com.upupor.service.dao.entity.Feedback;
import com.upupor.service.service.aggregation.service.FeedbackService;
import com.upupor.service.service.aggregation.service.MessageService;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.AddFeedbackReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.upupor.service.common.CcConstant.SKIP_SUBSCRIBE_EMAIL_CHECK;


/**
 * 反馈
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 01:51
 */
@Api(tags = "反馈服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final MessageService messageService;

    @ApiOperation("添加反馈")
    @PostMapping("/add")
    public CcResponse add(AddFeedbackReq add) {
        CcResponse ccResponse = new CcResponse();

        if (StringUtils.isEmpty(add.getContent())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "反馈内容为空");
        }

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(CcUtils.getUuId());
        feedback.setFeedbackContent(add.getContent());
        feedback.setStatus(CcEnum.FeedBackStatus.NORMAL.getStatus());
        feedback.setCreateTime(CcDateUtil.getCurrentTime());
        try {
            feedback.setUserId(ServletUtils.getUserId());
        } catch (Exception e) {
            feedback.setUserId(ServletUtils.getSession().getId());
        }
        feedback.setSysUpdateTime(new Date());

        Integer result = feedbackService.addFeedBack(feedback);
        ccResponse.setData(result > 0);

        // 邮件发送
        messageService.sendEmail(CcConstant.UPUPOR_EMAIL, "网站反馈" + CcDateUtil.formatDate(new Date()), "有收到新的反馈,请及时处理,反馈内容:<br />" + feedback.getFeedbackContent(), SKIP_SUBSCRIBE_EMAIL_CHECK);

        return ccResponse;
    }

}
