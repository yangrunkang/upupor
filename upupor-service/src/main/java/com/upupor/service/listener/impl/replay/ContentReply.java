package com.upupor.service.listener.impl.replay;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.listener.abstracts.AbstractReplyComment;
import com.upupor.service.listener.event.ReplayCommentEvent;
import com.upupor.service.service.ContentService;
import com.upupor.service.service.MemberService;
import com.upupor.service.service.MessageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.MsgTemplate.CONTENT_INNER_MSG;
import static com.upupor.service.common.CcConstant.MsgTemplate.PROFILE_INNER_MSG;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月18日 00:50
 * @email: yangrunkang53@gmail.com
 */
@Component
public class ContentReply extends AbstractReplyComment<Content> {
    @Resource
    private ContentService contentService;

    public ContentReply(MemberService memberService, MessageService messageService) {
        super(memberService, messageService);
    }


    @Override
    public Boolean isHandled(String targetId, CcEnum.CommentSource commentSource) {
        return Objects.nonNull(getTarget(targetId)) && CcEnum.CommentSource.contentSource().contains(commentSource.getSource());
    }

    @Override
    public void reply(ReplayCommentEvent replayCommentEvent) {
        String msgId = msgId();
        String beRepliedUserId = replayCommentEvent.getBeRepliedUserId();
        Member beRepliedMember = getMember(beRepliedUserId);
        String createReplayUserId = replayCommentEvent.getCreateReplayUserId();
        String createReplayUserName = replayCommentEvent.getCreateReplayUserName();

        Content content = getTarget(replayCommentEvent.getTargetId());

        // 评论回复站内信
        String msg = "您关于《" + String.format(CONTENT_INNER_MSG, content.getContentId(), msgId, content.getTitle())
                + "》的文章评论,收到了来自"
                + String.format(PROFILE_INNER_MSG, createReplayUserId, msgId, createReplayUserName)
                + "的回复,请" + String.format(CONTENT_INNER_MSG, content.getContentId(), msgId, "点击查看");

        // 站内信通知
        getMessageService().addMessage(beRepliedUserId, msg, CcEnum.MessageType.USER_REPLAY.getType(), msgId);
        // 邮件通知
        getMessageService().sendEmail(beRepliedMember.getEmail(), title(), msg, beRepliedUserId);
    }

    @Override
    protected Content getTarget(String targetId) {
        return contentService.getNormalContent(targetId);
    }

    @Override
    public void updateTargetCommentCreatorInfo(String targetId, String commenterUserId) {
        Content content = getTarget(targetId);
        content.setLatestCommentTime(CcDateUtil.getCurrentTime());
        content.setLatestCommentUserId(commenterUserId);
        contentService.updateContent(content);
    }
}
