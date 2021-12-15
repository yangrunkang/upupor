package com.upupor.service.listener.eventbus;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.dao.entity.*;
import com.upupor.service.listener.event.*;
import com.upupor.service.service.*;
import com.upupor.service.utils.CcUtils;
import com.upupor.spi.req.AddAttentionReq;
import com.upupor.spi.req.AddCommentReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
    private final MemberIntegralService memberIntegralService;

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
                } else if (commentSource.equals(CcEnum.CommentSource.MESSAGE.getSource())) {
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

                } else if (commentSource >= CcEnum.CommentSource.RADIO.getSource()) {
                    Radio radio = radioService.getByRadioId(targetId);
                    msg = "电台《" + String.format(RADIO_INNER_MSG, radio.getRadioId(), msgId, radio.getId())
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
     * 产生新的评论
     *
     * @param commentMessageEvent
     */
    @EventListener
    @Async
    @Deprecated // 计划使用 com.upupor.service.listener.eventbus.InnerMsgEmailListener.toCommentSuccessEvent 替换
    public void commentMessageEvent(CommentMessageEvent commentMessageEvent) {
        Member addCommentMember = commentMessageEvent.getAddCommentMember();
        AddCommentReq addCommentReq = commentMessageEvent.getAddCommentReq();
        Comment comment = commentMessageEvent.getComment();
        String commentedUserId = addCommentMember.getUserId();

        // 处理内容的评论
        try {
            handleContentComment(addCommentMember, addCommentReq, comment, commentedUserId);
        } catch (Exception e) {
        }

        // 处理留言板的评论
        try {
            handleMessageComment(addCommentMember, addCommentReq, comment, commentedUserId);
        } catch (Exception e) {
        }
        // 处理电台的评论
        try {
            handleRadioComment(addCommentMember, addCommentReq, comment, commentedUserId);
        } catch (Exception e) {
        }
    }

    private void handleMessageComment(Member addCommentMember, AddCommentReq addCommentReq, Comment comment, String userId) {
        String msgId = CcUtils.getUuId();
        if (comment.getCommentSource().equals(CcEnum.CommentSource.MESSAGE.getSource())) {
            String targetUserId = addCommentReq.getTargetId();
            Member targetMember = memberService.memberInfo(targetUserId);
            if (Objects.isNull(targetMember)) {
                return;
            }

            // 站内信通知对方收到新的留言
            String msg = "您收到了新的留言信息,点击" + String.format(MESSAGE_INTEGRAL, targetMember.getUserId(), msgId, "<strong>留言板</strong>") + "查看. 留言来自"
                    + String.format(PROFILE_INNER_MSG, userId, msgId, addCommentMember.getUserName());
            messageService.addMessage(targetMember.getUserId(), msg, CcEnum.MessageType.USER_REPLAY.getType(), msgId);

            // 发送邮件通知对方收到新的留言

            String emailTitle = "您有新的留言,快去看看吧";
            String emailContent = "点击" + String.format(MESSAGE_EMAIL, targetMember.getUserId(), msgId, "<strong>留言板</strong>") + ",留言来自 "
                    + String.format(PROFILE_EMAIL, userId, msgId, addCommentMember.getUserName());
            messageService.sendEmail(targetMember.getEmail(), emailTitle, emailContent, targetMember.getUserId());

            // 留言赠送积分
            IntegralEnum integralEnum = IntegralEnum.MESSAGE;
            String text = "您给 " + String.format(MESSAGE_INTEGRAL, targetMember.getUserId(), msgId, targetMember.getUserName()) + " 留言了,赠送 " +
                    integralEnum.getIntegral() + " 积分;";
            memberIntegralService.addIntegral(integralEnum, text, userId, comment.getCommentId());
        }
    }

    private void handleRadioComment(Member addCommentMember, AddCommentReq addCommentReq, Comment comment, String userId) {
        String msgId = CcUtils.getUuId();
        if (comment.getCommentSource().equals(CcEnum.CommentSource.RADIO.getSource())) {
            String targetRadioId = addCommentReq.getTargetId();
            Radio byRadioId = radioService.getByRadioId(targetRadioId);

            Member targetMember = memberService.memberInfo(byRadioId.getUserId());
            if (Objects.isNull(targetMember)) {
                return;
            }


            // 站内信通知对方收到新的留言
            String msg = "您收到了新的电台评论,点击<strong>《" + String.format(RADIO_INTEGRAL, byRadioId.getRadioId(), msgId, byRadioId.getId()) + "》</strong>查看,评论来自"
                    + String.format(PROFILE_INNER_MSG, userId, msgId, addCommentMember.getUserName());
            messageService.addMessage(targetMember.getUserId(), msg, CcEnum.MessageType.USER_REPLAY.getType(), msgId);

            // 发送邮件通知对方收到新的留言

            String emailTitle = "您有新的电台评论,快去看看吧";
            String emailContent = "点击" + String.format(RADIO_EMAIL, byRadioId.getRadioId(), msgId, byRadioId.getId()) + ",评论来自 "
                    + String.format(PROFILE_EMAIL, userId, msgId, addCommentMember.getUserName());
            messageService.sendEmail(targetMember.getEmail(), emailTitle, emailContent, targetMember.getUserId());

//            // 留言赠送积分
//            IntegralEnum integralEnum = IntegralEnum.MESSAGE;
//            String text = "您给 " + String.format(MESSAGE_INTEGRAL, targetMember.getUserId(), msgId, targetMember.getUserName()) + " 留言了,赠送 " +
//                    integralEnum.getIntegral() + " 积分;";
//            memberIntegralService.addIntegral(integralEnum, text, userId, comment.getCommentId());
        }
    }

    private void handleContentComment(Member addCommentMember, AddCommentReq addCommentReq, Comment comment, String userId) {
        String msgId = CcUtils.getUuId();
        Content content = contentService.getNormalContent(addCommentReq.getTargetId());
        if (Objects.isNull(content)) {
            return;
        }

        // 内容作者
        Member contentMember = memberService.memberInfo(content.getUserId());
        if (Objects.isNull(contentMember)) {
            return;
        }


        String innerMsgText = null;
        // 邮件
        String emailTitle = null;
        String emailContent = null;
        // 新增评论,送积分
        IntegralEnum integralEnum = IntegralEnum.CREATE_COMMENT;
        String integralText = null;
        if (comment.getCommentSource() >= CcEnum.CommentSource.TECH.getSource()
                && comment.getCommentSource() <= CcEnum.CommentSource.SHORT_CONTENT.getSource()) {


            // 站内信通知作者
            innerMsgText = "您的文章《" + String.format(CONTENT_INNER_MSG, content.getContentId(), msgId, content.getTitle()) + "》有新的评论,来自"
                    + String.format(PROFILE_INNER_MSG, userId, msgId, addCommentMember.getUserName())
                    + ",快去" + String.format(CONTENT_INNER_MSG, content.getContentId(), msgId, "查看") + "吧";
            // 邮件内容
            emailTitle = "您的文章有了新评论,快去看看吧";
            emailContent = "点击《" + String.format(CONTENT_EMAIL, content.getContentId(), msgId, content.getTitle()) + "》查看评论,评论来自 "
                    + String.format(PROFILE_EMAIL, userId, msgId, addCommentMember.getUserName());

            // 赠送积分描述
            integralText = "您评论了 《" + String.format(CONTENT_INTEGRAL, content.getContentId(), msgId, content.getTitle()) + "》文章,赠送 " +
                    integralEnum.getIntegral() + " 积分;快去写文章吧,您收到的评论越多,就会获得更多的积分~";
        }

        // 站内信
        messageService.addMessage(contentMember.getUserId(), innerMsgText, CcEnum.MessageType.SYSTEM.getType(), msgId);

        // 邮件
        messageService.sendEmail(contentMember.getEmail(), emailTitle, emailContent, contentMember.getUserId());

        // 送积分
        memberIntegralService.addIntegral(integralEnum, integralText, userId, comment.getCommentId());
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
        Member member = memberRegisterEvent.getMember();
        String msg = "<div class='text-info' style='font-size: 16px;'>愿景: 让每个人享受分享</div>" +
                "<div>欢迎使用Upupor</div>" +
                "<div>官方微信公众号: <a class='cv-link' data-toggle='modal' data-target='#wechat'>Upupor</a></div>" +
                "<div>官方微博: <a class='cv-link' data-toggle='modal' data-target='#weibo'>UpuporCom</a></div>";
        String msgId = CcUtils.getUuId();
        messageService.addMessage(member.getUserId(), msg, CcEnum.MessageType.SYSTEM.getType(), msgId);
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
        if (CcEnum.CommentSource.contentSource().contains(event.getCommentSource())) {
            Content content = contentService.getNormalContent(event.getTargetId());
            content.setLatestCommentTime(event.getCreateTime());
            content.setLatestCommentUserId(event.getCommenterUserId());
            contentService.updateContent(content);
        }
        if (event.getCommentSource().equals(CcEnum.CommentSource.RADIO.getSource())) {
            Radio radio = radioService.getByRadioId(event.getTargetId());
            radio.setLatestCommentTime(event.getCreateTime());
            radio.setLatestCommentUserId(event.getCommenterUserId());
            radioService.updateRadio(radio);
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
