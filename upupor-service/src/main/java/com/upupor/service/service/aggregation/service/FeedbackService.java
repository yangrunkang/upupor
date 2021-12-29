package com.upupor.service.service.aggregation.service;

import com.upupor.service.dao.entity.Feedback;

/**
 * 反馈服务
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 16:48
 */
public interface FeedbackService {
    /**
     * 添加反馈
     *
     * @param feedback
     * @return
     */
    Integer addFeedBack(Feedback feedback);

}