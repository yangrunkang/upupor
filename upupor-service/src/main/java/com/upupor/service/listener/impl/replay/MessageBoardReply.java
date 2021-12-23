package com.upupor.service.listener.impl.replay;

import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.listener.abstracts.AbstractReplyComment;
import com.upupor.service.listener.event.ReplayCommentEvent;
import com.upupor.service.service.MemberService;
import com.upupor.service.service.MessageService;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.upupor.service.common.CcConstant.MsgTemplate.MESSAGE_INTEGRAL;
import static com.upupor.service.common.CcConstant.MsgTemplate.PROFILE_INNER_MSG;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月18日 00:50
 * @email: yangrunkang53@gmail.com
 */
@Component
public class MessageBoardReply extends AbstractReplyComment<Member> {

    public MessageBoardReply(MemberService memberService, MessageService messageService) {
        super(memberService, messageService);
    }

    @Override
    public Boolean isHandled(String targetId, CcEnum.CommentSource commentSource) {
        return Objects.nonNull(getTarget(targetId)) && CcEnum.CommentSource.MESSAGE.equals(commentSource);
    }

    @Override
    public void reply(ReplayCommentEvent replayCommentEvent) {
        String title = "留言板有新的回复消息";
        String msgId = msgId();
        String targetId = replayCommentEvent.getTargetId();
        String creatorReplayUserId = replayCommentEvent.getCreateReplayUserId();
        String creatorReplayUserName = replayCommentEvent.getCreateReplayUserName();

        Member beReplayedUser = getMember(replayCommentEvent.getBeRepliedUserId());

        String msg = null;

        // 留言板所有者(对应的就是事件的targetId)
        if (!targetId.equals(creatorReplayUserId)) {
            msg = String.format(MESSAGE_INTEGRAL, targetId, msgId, "<strong>留言板</strong>")
                    + "收到了来自"
                    + String.format(PROFILE_INNER_MSG, creatorReplayUserId, msgId, creatorReplayUserName)
                    + "的回复,请" + String.format(MESSAGE_INTEGRAL, targetId, msgId, "点击查看");
        } else {
            msg = String.format(MESSAGE_INTEGRAL, creatorReplayUserId, msgId, "<strong>留言板</strong>")
                    + "收到了来自"
                    + String.format(PROFILE_INNER_MSG, creatorReplayUserId, msgId, creatorReplayUserName)
                    + "的回复,请" + String.format(MESSAGE_INTEGRAL, creatorReplayUserId, msgId, "点击查看");
        }

        // 站内信通知
        getMessageService().addMessage(beReplayedUser.getUserId(), msg, CcEnum.MessageType.USER_REPLAY.getType(), msgId);

        // 邮件通知
        getMessageService().sendEmail(beReplayedUser.getEmail(), title, msg, beReplayedUser.getUserId());
    }

    @Override
    protected Member getTarget(String targetId) {
        return getMemberService().memberInfo(targetId);
    }
}
