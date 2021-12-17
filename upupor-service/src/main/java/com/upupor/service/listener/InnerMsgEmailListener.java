package com.upupor.service.listener;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Attention;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.listener.abstracts.AbstractComment;
import com.upupor.service.listener.event.*;
import com.upupor.service.service.ContentService;
import com.upupor.service.service.MemberService;
import com.upupor.service.service.MessageService;
import com.upupor.service.service.RadioService;
import com.upupor.service.utils.CcUtils;
import com.upupor.spi.req.AddAttentionReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.MsgTemplate.*;

/**
 * upupor 网站站内信
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 19:21
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class InnerMsgEmailListener {

    private final ContentService contentService;
    private final MessageService messageService;
    private final MemberService memberService;
    private final RadioService radioService;
    private final List<AbstractComment> abstractCommentList;

    /**
     * 回复评论事件
     *
     * @param replayCommentEvent
     */
    @EventListener
    @Async
    public void replayCommentEvent(ReplayCommentEvent replayCommentEvent) {
        String targetId = replayCommentEvent.getTargetId();
        Integer commentSource = replayCommentEvent.getCommentSource();

        // 添加评论的人
        String creatorReplayUserId = replayCommentEvent.getCreateReplayUserId();
        String creatorReplayUserName = replayCommentEvent.getCreateReplayUserName();
        String msgId = CcUtils.getUuId();

        // 如果是回复别人的评论要告知别人已经被回复
        // 获取用户id
        try {
            Member beReplayedUser = memberService.memberInfo(replayCommentEvent.getBeRepliedUserId());
            if (Objects.nonNull(beReplayedUser)) {
                // 消息内容
                String msg = null;
                // 消息标题
                String title = "您的评论收到了新的回复";
                // 如果自己回复自己则啥也不做
                if (beReplayedUser.getUserId().equals(creatorReplayUserId)) {
                    return;
                }

                if (CcEnum.CommentSource.contentSource().contains(commentSource)) {
                    Content content = contentService.getNormalContent(targetId);
                    // 评论回复站内信
                    msg = "您关于《" + String.format(CONTENT_INNER_MSG, content.getContentId(), msgId, content.getTitle())
                            + "》的文章评论,收到了来自"
                            + String.format(PROFILE_INNER_MSG, creatorReplayUserId, msgId, creatorReplayUserName)
                            + "的回复,请" + String.format(CONTENT_INNER_MSG, content.getContentId(), msgId, "点击查看");
                }


                if (commentSource.equals(CcEnum.CommentSource.MESSAGE.getSource())) {
                    title = "留言板有新的回复消息";
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

                }

                if (CcEnum.CommentSource.RADIO.getSource().equals(commentSource)) {
                    Radio radio = radioService.getByRadioId(targetId);
                    msg = "电台《" + String.format(RADIO_INNER_MSG, radio.getRadioId(), msgId, radio.getRadioIntro())
                            + "》,收到了来自"
                            + String.format(PROFILE_INNER_MSG, creatorReplayUserId, msgId, creatorReplayUserName)
                            + "的回复,请" + String.format(RADIO_INNER_MSG, radio.getRadioId(), msgId, "点击查看");
                }


                // 站内信通知
                messageService.addMessage(beReplayedUser.getUserId(), msg, CcEnum.MessageType.USER_REPLAY.getType(), msgId);

                // 邮件通知
                messageService.sendEmail(beReplayedUser.getEmail(), title, msg, beReplayedUser.getUserId());
            }
        } catch (Exception e) {

        }

    }

    /**
     * 点赞事件
     *
     * @param contentLikeEvent
     */
    @EventListener
    @Async
    public void likeMessage(ContentLikeEvent contentLikeEvent) {
        String msgId = CcUtils.getUuId();
        Content content = contentLikeEvent.getContent();
        Member member = memberService.memberInfo(contentLikeEvent.getClickUserId());

        String message = "您的文章《" +
                String.format(CONTENT_INNER_MSG, content.getContentId(), msgId, content.getTitle()) + "》被 " +
                String.format(PROFILE_INNER_MSG, member.getUserId(), msgId, member.getUserName()) +
                " 在" + CcDateUtil.snsFormat(CcDateUtil.getCurrentTime()) + "点赞了";
        messageService.addMessage(content.getUserId(), message, CcEnum.MessageType.SYSTEM.getType(), msgId);
    }


    /**
     * 新用户注册时间
     *
     * @param memberRegisterEvent
     */
    @EventListener
    @Async
    public void registerInnerMessage(MemberRegisterEvent memberRegisterEvent) {
        // 添加站内信
        String msg = "<div class='text-info' style='font-size: 16px;'>愿景: 让每个人享受分享</div>" +
                "<div>欢迎使用Upupor</div>" +
                "<div>官方微信公众号: <a class='cv-link' data-toggle='modal' data-target='#wechat'>Upupor</a></div>" +
                "<div>官方微博: <a class='cv-link' data-toggle='modal' data-target='#weibo'>UpuporCom</a></div>";
        String msgId = CcUtils.getUuId();
        messageService.addMessage(memberRegisterEvent.getUserId(), msg, CcEnum.MessageType.SYSTEM.getType(), msgId);
    }

    /**
     * 添加评论成功
     *
     * @param event
     */
    @EventListener
    @Async
    public void toCommentSuccessEvent(ToCommentSuccessEvent event) {
        // 更新最新的评论者及评论时间(这里主要是为了方便查询,冗余字段,减少连表操作)
        if (CcEnum.CommentSource.contentSource().contains(event.getCommentSource().getSource())) {
            Content content = contentService.getNormalContent(event.getTargetId());
            content.setLatestCommentTime(event.getCreateTime());
            content.setLatestCommentUserId(event.getCommenterUserId());
            contentService.updateContent(content);
        }
        if (CcEnum.CommentSource.RADIO.getSource().equals(event.getCommentSource().getSource())) {
            Radio radio = radioService.getByRadioId(event.getTargetId());
            radio.setLatestCommentTime(event.getCreateTime());
            radio.setLatestCommentUserId(event.getCommenterUserId());
            radioService.updateRadio(radio);
        }

        for (AbstractComment abstractComment : abstractCommentList) {
            try {
                if (abstractComment.confirmSource(event.getCommentSource(), event.getTargetId())) {
                    abstractComment.comment(event.getTargetId(), event.getCommenterUserId(), event.getCommentId());
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 关注消息事件
     *
     * @param attentionUserEvent
     */
    @EventListener
    @Async
    public void attentionMessage(AttentionUserEvent attentionUserEvent) {
        AddAttentionReq addAttentionReq = attentionUserEvent.getAddAttentionReq();
        Attention attention = attentionUserEvent.getAttention();
        Member attentionUser = memberService.memberInfo(attention.getUserId());
        String msgId = CcUtils.getUuId();
        String msg = "您有新的关注者 " + String.format(PROFILE_INNER_MSG, attentionUser.getUserId(), msgId, attentionUser.getUserName())
                + " 去Ta的主页看看吧";
        messageService.addMessage(addAttentionReq.getAttentionUserId(), msg, CcEnum.MessageType.SYSTEM.getType(), msgId);

        // 发送邮件 被关注的人要通知ta
        Member member = memberService.memberInfo(addAttentionReq.getAttentionUserId());
        String emailTitle = "您有新的关注者";
        String emailContent = "点击" + String.format(PROFILE_EMAIL, attentionUser.getUserId(), msgId, attentionUser.getUserName()) + " 去Ta的主页看看吧";
        messageService.sendEmail(member.getEmail(), emailTitle, emailContent, member.getUserId());
    }

}
