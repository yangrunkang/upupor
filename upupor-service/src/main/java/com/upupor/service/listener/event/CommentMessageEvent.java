package com.upupor.service.listener.event;

import com.upupor.service.dao.entity.Comment;
import com.upupor.service.dao.entity.Member;
import com.upupor.spi.req.AddCommentReq;
import lombok.Data;

/**
 * 评论消息Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 19:22
 */
@Data
public class CommentMessageEvent {
    private AddCommentReq addCommentReq;
    private Comment comment;
    private Member addCommentMember;
}
