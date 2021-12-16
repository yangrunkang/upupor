package com.upupor.service.listener.impl;

import com.upupor.service.common.CcEnum;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.listener.abstracts.AbstractComment;
import com.upupor.service.service.CommentService;
import com.upupor.service.service.MemberIntegralService;
import com.upupor.service.service.MemberService;
import com.upupor.service.service.MessageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.upupor.service.common.CcConstant.MsgTemplate.*;

/**
 * 留言板评论
 *
 * @author Yang Runkang (cruise)
 * @date 2021年12月16日 21:22
 * @email: yangrunkang53@gmail.com
 */
@Component
public class MessageBoardComment extends AbstractComment<Member> {

    @Resource
    private MessageService messageService;

    @Resource
    private MemberIntegralService memberIntegralService;

    public MessageBoardComment(CommentService commentService, MemberService memberService) {
        super(commentService, memberService);
    }

    @Override
    public void comment(String targetId, String commenterUserId, String commentId) {
        String msgId = getMsgId();

        // 获取被评论的用户
        Member targetMember = getTarget(targetId);
        String targetUserId = targetMember.getUserId();

        // 获取评论者信息
        Member commenter = getMemberInfo(commenterUserId);
        String commenterUserName = commenter.getUserName();


        // 站内信通知对方收到新的留言
        String msg = "您收到了新的留言信息,点击" + String.format(MESSAGE_INTEGRAL, targetUserId, msgId, "<strong>留言板</strong>") + "查看. 留言来自"
                + String.format(PROFILE_INNER_MSG, commenterUserId, msgId, commenterUserName);
        messageService.addMessage(targetUserId, msg, CcEnum.MessageType.USER_REPLAY.getType(), msgId);

        // 发送邮件通知对方收到新的留言

        String emailTitle = "您有新的留言,快去看看吧";
        String emailContent = "点击" + String.format(MESSAGE_EMAIL, targetUserId, msgId, "<strong>留言板</strong>") + ",留言来自 "
                + String.format(PROFILE_EMAIL, commenterUserId, msgId, commenterUserName);
        messageService.sendEmail(targetMember.getEmail(), emailTitle, emailContent, targetUserId);

        // 留言赠送积分
        IntegralEnum integralEnum = IntegralEnum.MESSAGE;
        String text = "您给 " + String.format(MESSAGE_INTEGRAL, targetUserId, msgId, targetMember.getUserName()) + " 留言了,赠送 " +
                integralEnum.getIntegral() + " 积分;";
        memberIntegralService.addIntegral(integralEnum, text, commenterUserId, commentId);
    }

    @Override
    protected Member getTarget(String targetId) {
        return getMemberInfo(targetId);
    }

    @Override
    public Boolean confirmSource(CcEnum.CommentSource commentSource) {
        return CcEnum.CommentSource.MESSAGE.getSource().equals(commentSource.getSource());
    }
}
