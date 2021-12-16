package com.upupor.service.listener.abstracts;

import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Comment;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.service.CommentService;
import com.upupor.service.service.MemberService;
import com.upupor.service.utils.CcUtils;

/**
 * 评论抽象
 *
 * @author Yang Runkang (cruise)
 * @date 2021年12月16日 21:15
 */
public abstract class AbstractComment<T> {
    private final CommentService commentService;
    private final MemberService memberService;

    public AbstractComment(CommentService commentService, MemberService memberService) {
        this.commentService = commentService;
        this.memberService = memberService;
    }

    /**
     * 评论
     *  @param targetId        评论目标
     * @param commenterUserId 评论者id
     * @param commentId       评论记录的id
     */
    public abstract void comment(String targetId, String commenterUserId, String commentId);

    /**
     * 获取目标
     *
     * @param targetId 评论目标
     * @return 返回目标
     */
    protected abstract T getTarget(String targetId);

    /**
     * @param commentSource 评论来源
     * @return 判断是否处理
     */
    public abstract Boolean confirmSource(CcEnum.CommentSource commentSource);

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    protected Member getMemberInfo(String userId) {
        return memberService.memberInfo(userId);
    }

    /**
     * 获取评论信息
     *
     * @param commentId 评论id
     * @return
     */
    protected Comment getComment(String commentId) {
        return commentService.getCommentByCommentId(commentId);
    }

    /**
     * @return 消息Id
     */
    protected String getMsgId() {
        return CcUtils.getUuId();
    }

}
