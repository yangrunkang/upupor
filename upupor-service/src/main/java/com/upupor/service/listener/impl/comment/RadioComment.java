package com.upupor.service.listener.impl.comment;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.listener.abstracts.AbstractComment;
import com.upupor.service.service.CommentService;
import com.upupor.service.service.MemberService;
import com.upupor.service.service.MessageService;
import com.upupor.service.service.RadioService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.MsgTemplate.*;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月16日 21:21
 * @email: yangrunkang53@gmail.com
 */
@Component
public class RadioComment extends AbstractComment<Radio> {
    @Resource
    private RadioService radioService;

    @Resource
    private MessageService messageService;

    public RadioComment(CommentService commentService, MemberService memberService) {
        super(commentService, memberService);
    }

    @Override
    public void comment(String targetId, String commenterUserId, String commentId) {
        String msgId = getMsgId();

        Radio radio = radioService.getByRadioId(targetId);
        String radioName = radio.getRadioIntro();

        Member radioAuthor = getMemberInfo(radio.getUserId());
        String radioAuthorUserId = radioAuthor.getUserId();

        // 获取评论者信息
        Member commenter = getMemberInfo(commenterUserId);
        String commenterUserName = commenter.getUserName();

        // 如果作者自己评论自己就不用发邮件了
        if(commenter.getUserId().equals(radio.getUserId())){
            return;
        }



        // 站内信通知对方收到新的留言
        String msg = "您收到了新的电台评论,点击<strong>《" + String.format(RADIO_INTEGRAL, radio.getRadioId(), msgId, radioName) + "》</strong>查看,评论来自"
                + String.format(PROFILE_INNER_MSG, commenterUserId, msgId, commenterUserName);
        messageService.addMessage(radioAuthorUserId, msg, CcEnum.MessageType.USER_REPLAY.getType(), msgId);

        // 发送邮件通知对方收到新的留言

        String emailTitle = "您有新的电台评论,快去看看吧";
        String emailContent = "点击" + String.format(RADIO_EMAIL, radio.getRadioId(), msgId, radioName) + ",评论来自 "
                + String.format(PROFILE_EMAIL, commenterUserId, msgId, commenterUserName);
        messageService.sendEmail(radioAuthor.getEmail(), emailTitle, emailContent, radioAuthorUserId);
    }

    @Override
    protected Radio getTarget(String targetId) {
        return radioService.getByRadioId(targetId);
    }

    @Override
    public Boolean confirmSource(CcEnum.CommentSource commentSource, String targetId) {
        return Objects.nonNull(getTarget(targetId)) && CcEnum.CommentSource.RADIO.getSource().equals(commentSource.getSource());
    }

    @Override
    public void updateTargetCommentCreatorInfo(String targetId, String commenterUserId) {
        Radio radio = getTarget(targetId);
        radio.setLatestCommentTime(CcDateUtil.getCurrentTime());
        radio.setLatestCommentUserId(commenterUserId);
        radioService.updateRadio(radio);
    }
}
