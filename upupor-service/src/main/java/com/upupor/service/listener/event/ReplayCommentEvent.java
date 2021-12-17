package com.upupor.service.listener.event;

import lombok.Builder;
import lombok.Data;

/**
 * 回复评论的事件
 *
 * @author runkangyang (cruise)
 * @date 2020.03.06 01:22
 */
@Builder
@Data
public class ReplayCommentEvent {
    /**
     * 被回复用户的用户id
     */
    private String beRepliedUserId;

    /**
     * 创建回复的用户id
     */
    private String createReplayUserId;

    /**
     * 创建回复的用户名
     */
    private String createReplayUserName;

    /**
     * 评论的目标
     */
    private String targetId;

    /**
     * 评论来源
     */
    private Integer commentSource;
}
