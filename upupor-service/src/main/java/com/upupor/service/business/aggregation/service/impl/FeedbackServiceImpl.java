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

package com.upupor.service.business.aggregation.service.impl;

import com.upupor.framework.CcConstant;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.business.aggregation.dao.entity.Feedback;
import com.upupor.service.business.aggregation.dao.mapper.FeedbackMapper;
import com.upupor.service.business.aggregation.service.FeedbackService;
import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.outer.req.AddFeedbackReq;
import com.upupor.service.types.FeedBackStatus;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

import static com.upupor.framework.CcConstant.SKIP_SUBSCRIBE_EMAIL_CHECK;

/**
 * 反馈服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/29 07:48
 */
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackMapper feedbackMapper;
    private final MessageService messageService;

    @Override
    public Integer addFeedBack(AddFeedbackReq add) {

        if (StringUtils.isEmpty(add.getContent())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "反馈内容为空");
        }

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(CcUtils.getUuId());
        feedback.setFeedbackContent(add.getContent());
        feedback.setStatus(FeedBackStatus.NORMAL);
        feedback.setCreateTime(CcDateUtil.getCurrentTime());
        try {
            feedback.setUserId(ServletUtils.getUserId());
        } catch (Exception e) {
            feedback.setUserId(ServletUtils.getSession().getId());
        }
        feedback.setSysUpdateTime(new Date());

        Integer result = feedbackMapper.insert(feedback);
        if (result > 0) {
            // 邮件发送
            messageService.sendEmail(CcConstant.UPUPOR_EMAIL, "网站反馈" + CcDateUtil.formatDate(new Date()), "有收到新的反馈,请及时处理,反馈内容:<br />" + feedback.getFeedbackContent(), SKIP_SUBSCRIBE_EMAIL_CHECK);
        }
        return result;
    }
}
