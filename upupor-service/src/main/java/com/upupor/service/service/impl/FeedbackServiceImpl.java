package com.upupor.service.service.impl;

import com.upupor.service.dao.entity.Feedback;
import com.upupor.service.dao.mapper.FeedbackMapper;
import com.upupor.service.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public Integer addFeedBack(Feedback feedback) {
        return feedbackMapper.insert(feedback);
    }
}
