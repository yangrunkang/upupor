package com.upupor.service.listener.event;

import com.upupor.service.common.CcEnum;
import lombok.Builder;
import lombok.Data;

/**
 * 评论内容成功事件
 * @author Yang Runkang (cruise)
 * @date 2021年12月16日 00:24
 */
@Builder
@Data
public class ToCommentSuccessEvent {
    /**
     * 评论创建时间
     */
    private Long createTime;
    /**
     * 评论者Id
     */
    private String commenterUserId;

    /**
     * 目标id
     */
    private String targetId;

    /**
     * 评论来源
     */
    private CcEnum.CommentSource commentSource;

    /**
     * 评论Id
     */
    private String commentId;


}
