package com.upupor.service.listener;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Fans;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.Viewer;
import com.upupor.service.dao.mapper.ViewerMapper;
import com.upupor.service.dto.page.common.ListFansDto;
import com.upupor.service.listener.event.ContentLikeEvent;
import com.upupor.service.listener.event.PublishContentEvent;
import com.upupor.service.listener.event.ViewerEvent;
import com.upupor.service.service.*;
import com.upupor.service.utils.CcUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.MsgTemplate.*;
import static com.upupor.service.utils.CcUtils.sleep2s;

/**
 * 内容事件监听器
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/08 11:11
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ContentListener {

    private final MemberIntegralService memberIntegralService;
    private final ContentService contentService;
    private final FanService fanService;
    private final MemberService memberService;
    private final MessageService messageService;
    private final ViewerMapper viewerMapper;

    @EventListener
    @Async
    public void viewer(ViewerEvent event) {
        String targetId = event.getTargetId();
        CcEnum.ViewTargetType targetType = event.getTargetType();

        String viewerUserId = event.getViewerUserId();
        // 如果为空,说明用户未登录
        if (Objects.isNull(viewerUserId)) {
            return;
        }

        int count = viewerMapper.countByTargetIdAndViewerUserId(targetId, viewerUserId, targetType.getType());
        if (count > 0) {
            return;
        }

        Viewer viewer = new Viewer();
        viewer.setTargetId(targetId);
        viewer.setViewerUserId(viewerUserId);
        viewer.setTargetType(targetType.getType());
        viewer.setDeleteStatus(CcEnum.ViewerDeleteStatus.NORMAL.getStatus());
        viewer.setSysUpdateTime(new Date());
        viewer.setCreateTime(CcDateUtil.getCurrentTime());

        viewerMapper.insert(viewer);
    }

    @EventListener
    @Async
    public void createContent(PublishContentEvent createContentEvent) {

        String contentId = createContentEvent.getContentId();
        if (StringUtils.isEmpty(contentId)) {
            return;
        }

        Content content = contentService.getContentByContentIdNoStatus(contentId);
        if (Objects.isNull(content) || !content.getStatus().equals(CcEnum.ContentStatus.NORMAL.getStatus())) {
            return;
        }

        String text = String.format("新增文章《%s》赠送积分", String.format(CONTENT_INTEGRAL, content.getContentId(), CcUtils.getUuId(), content.getTitle()));
        IntegralEnum integralEnum = IntegralEnum.CREATE_CONTENT;

        // 创建内容赠送
        memberIntegralService.addIntegral(integralEnum, text, createContentEvent.getUserId(), contentId);

        // 通知粉丝
        notifyFans(content);

    }


    private void notifyFans(Content content) {
        String userId = content.getUserId();

        Member contentMember = memberService.memberInfo(userId);
        int total = fanService.getFansNum(userId);

        int pageNum = total % CcConstant.Page.SIZE == 0 ? total / CcConstant.Page.SIZE : total / CcConstant.Page.SIZE + 1;

        for (int i = 0; i < pageNum; i++) {
            ListFansDto fans = fanService.getFans(userId, i + 1, CcConstant.Page.SIZE);
            if (Objects.isNull(fans)) {
                continue;
            }
            if (CollectionUtils.isEmpty(fans.getFansList())) {
                continue;
            }

            List<Fans> fansList = fans.getFansList();
            for (Fans fan : fansList) {
                String msgId = CcUtils.getUuId();
                String emailUser = String.format(PROFILE_EMAIL, contentMember.getUserId(), msgId, contentMember.getUserName());
                String innerMessageUser = String.format(PROFILE_INNER_MSG, contentMember.getUserId(), msgId, contentMember.getUserName());

                String emailContentTitle = String.format(CONTENT_EMAIL, content.getContentId(), msgId, content.getTitle());
                String innerMessageContentTitle = String.format(CONTENT_INNER_MSG, content.getContentId(), msgId, content.getTitle());


                String fanUserId = fan.getFanUserId();
                Member member = memberService.memberInfo(fanUserId);
                String email = "您关注的" + emailUser + "发表了新内容《" + emailContentTitle + "》,请"
                        + String.format(CONTENT_EMAIL, content.getContentId(), msgId, "点击查看");
                String innerMessage = "您关注的" + innerMessageUser + "发表了新内容《" + innerMessageContentTitle + "》,请"
                        + String.format(CONTENT_INNER_MSG, content.getContentId(), msgId, "点击查看");

                // 发送站内信
                messageService.addMessage(member.getUserId(), innerMessage, CcEnum.MessageType.SYSTEM.getType(), msgId);
                // 发送邮件
                messageService.sendEmail(member.getEmail(), content.getTitle(), email, member.getUserId());

                sleep2s();

            }
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


}
