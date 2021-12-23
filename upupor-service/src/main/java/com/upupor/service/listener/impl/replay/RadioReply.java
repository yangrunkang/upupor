package com.upupor.service.listener.impl.replay;

import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.listener.abstracts.AbstractReplyComment;
import com.upupor.service.listener.event.ReplayCommentEvent;
import com.upupor.service.service.MemberService;
import com.upupor.service.service.MessageService;
import com.upupor.service.service.RadioService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.MsgTemplate.PROFILE_INNER_MSG;
import static com.upupor.service.common.CcConstant.MsgTemplate.RADIO_INNER_MSG;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月18日 00:50
 * @email: yangrunkang53@gmail.com
 */
@Component
public class RadioReply extends AbstractReplyComment<Radio> {
    @Resource
    private RadioService radioService;

    public RadioReply(MemberService memberService, MessageService messageService) {
        super(memberService, messageService);
    }

    @Override
    public Boolean isHandled(String targetId, CcEnum.CommentSource commentSource) {
        return Objects.nonNull(getTarget(targetId)) && CcEnum.CommentSource.RADIO.equals(commentSource);
    }

    @Override
    public void reply(ReplayCommentEvent replayCommentEvent) {
        String creatorReplayUserId = replayCommentEvent.getCreateReplayUserId();
        String creatorReplayUserName = replayCommentEvent.getCreateReplayUserName();
        String msgId = msgId();
        String beRepliedUserId = replayCommentEvent.getBeRepliedUserId();
        Member beReplayedUser = getMember(beRepliedUserId);

        Radio radio = getTarget(replayCommentEvent.getTargetId());
        String msg = "电台《" + String.format(RADIO_INNER_MSG, radio.getRadioId(), msgId, radio.getRadioIntro())
                + "》,收到了来自"
                + String.format(PROFILE_INNER_MSG, creatorReplayUserId, msgId, creatorReplayUserName)
                + "的回复,请" + String.format(RADIO_INNER_MSG, radio.getRadioId(), msgId, "点击查看");


        // 站内信通知
        getMessageService().addMessage(beRepliedUserId, msg, CcEnum.MessageType.USER_REPLAY.getType(), msgId);
        // 邮件通知
        getMessageService().sendEmail(beReplayedUser.getEmail(), title(), msg, beRepliedUserId);
    }

    @Override
    protected Radio getTarget(String targetId) {
        return radioService.getByRadioId(targetId);
    }


}
