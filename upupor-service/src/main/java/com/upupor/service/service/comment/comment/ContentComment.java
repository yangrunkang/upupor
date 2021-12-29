package com.upupor.service.service.comment.comment;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.service.aggregation.service.*;
import com.upupor.service.service.comment.AbstractComment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.MsgTemplate.*;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月16日 21:19
 * @email: yangrunkang53@gmail.com
 */
@Component
public class ContentComment extends AbstractComment<Content> {

    @Resource
    private ContentService contentService;

    @Resource
    private MessageService messageService;

    @Resource
    private MemberIntegralService memberIntegralService;

    public ContentComment(CommentService commentService, MemberService memberService) {
        super(commentService, memberService);
    }

    @Override
    public void comment(String targetId, String commenterUserId, String commentId) {
        String msgId = getMsgId();
        // 文章信息
        Content target = getTarget(targetId);
        String contentId = target.getContentId();
        String contentTitle = target.getTitle();
        Member contentAuthor = getMemberInfo(target.getUserId());

        // 获取评论者信息
        Member commenter = getMemberInfo(commenterUserId);
        String commenterUserName = commenter.getUserName();

        // 如果文章作者自己评论自己就不用发邮件了
        if (commenter.getUserId().equals(target.getUserId())) {
            return;
        }

        // 站内信通知作者
        String innerMsgText = "您的文章《" + String.format(CONTENT_INNER_MSG, contentId, msgId, contentTitle) + "》有新的评论,来自"
                + String.format(PROFILE_INNER_MSG, commenterUserId, msgId, commenterUserName)
                + ",快去" + String.format(CONTENT_INNER_MSG, contentId, msgId, "查看") + "吧";
        // 邮件内容
        String emailTitle = "您的文章有了新评论,快去看看吧";
        String emailContent = "点击《" + String.format(CONTENT_EMAIL, contentId, msgId, contentTitle) + "》查看评论,评论来自 "
                + String.format(PROFILE_EMAIL, commenterUserId, msgId, commenterUserName);

        // 赠送积分描述
        String integralText = "您评论了 《" + String.format(CONTENT_INTEGRAL, contentId, msgId, contentTitle) + "》文章,赠送 " +
                IntegralEnum.CREATE_COMMENT.getIntegral() + " 积分;快去写文章吧,您收到的评论越多,就会获得更多的积分~";

        // 站内信
        messageService.addMessage(contentAuthor.getUserId(), innerMsgText, CcEnum.MessageType.SYSTEM.getType(), msgId);

        // 邮件
        messageService.sendEmail(contentAuthor.getEmail(), emailTitle, emailContent, contentAuthor.getUserId());

        // 送积分
        memberIntegralService.addIntegral(IntegralEnum.CREATE_COMMENT, integralText, commenterUserId, commentId);
    }

    @Override
    protected Content getTarget(String targetId) {
        return contentService.getNormalContent(targetId);
    }

    @Override
    public Boolean confirmSource(CcEnum.CommentSource commentSource, String targetId) {
        return Objects.nonNull(getTarget(targetId)) && CcEnum.CommentSource.contentSource().contains(commentSource.getSource());
    }

    @Override
    public void updateTargetCommentCreatorInfo(String targetId, String commenterUserId) {
        Content content = getTarget(targetId);
        content.setLatestCommentTime(CcDateUtil.getCurrentTime());
        content.setLatestCommentUserId(commenterUserId);
        contentService.updateContent(content);
    }
}
