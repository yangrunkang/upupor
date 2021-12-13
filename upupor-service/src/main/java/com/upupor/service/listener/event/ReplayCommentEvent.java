package com.upupor.service.listener.event;

import com.upupor.service.dao.entity.Comment;
import com.upupor.service.dao.entity.Member;
import com.upupor.spi.req.AddCommentReq;
import lombok.Data;

/**
 * 回复评论的事件
 *
 * @author runkangyang (cruise)
 * @date 2020.03.06 01:22
 */
@Data
public class ReplayCommentEvent {
    /**
     * 添加的评论请求
     */
    private AddCommentReq addCommentReq;
    /**
     * 具体的评论内容
     */
    private Comment comment;
    /**
     * 发表评论的人
     */
    private Member addCommentMember;
}
