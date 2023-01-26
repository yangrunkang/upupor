/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.service.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.ViewHistory;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dao.entity.enhance.FansEnhance;
import com.upupor.data.dao.mapper.ViewHistoryMapper;
import com.upupor.data.dto.page.common.ListFansDto;
import com.upupor.data.types.ContentStatus;
import com.upupor.data.types.MessageType;
import com.upupor.data.types.ViewTargetType;
import com.upupor.data.types.ViewType;
import com.upupor.framework.CcConstant;
import com.upupor.framework.common.IntegralEnum;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.base.*;
import com.upupor.service.business.build_msg.MessageBuilderInstance;
import com.upupor.service.business.build_msg.abstracts.BusinessMsgType;
import com.upupor.service.business.build_msg.abstracts.MsgType;
import com.upupor.service.business.build_msg.abstracts.dto.ContentMsgParamDto;
import com.upupor.service.business.build_msg.abstracts.dto.MemberProfileMsgParamDto;
import com.upupor.service.business.message.MessageSend;
import com.upupor.service.business.message.model.MessageModel;
import com.upupor.service.listener.event.ContentLikeEvent;
import com.upupor.service.listener.event.PublishContentEvent;
import com.upupor.service.listener.event.ViewerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static com.upupor.framework.utils.CcUtils.sleep2s;

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
    private final ViewHistoryMapper viewHistoryMapper;

    @EventListener
    @Async
    public void viewer(ViewerEvent event) {
        String targetId = event.getTargetId();
        ViewTargetType targetType = event.getTargetType();

        String viewerUserId = event.getViewerUserId();
        // 如果为空,说明用户未登录
        if (Objects.isNull(viewerUserId)) {
            return;
        }
        // 记录浏览者
        recordViewer(targetId, targetType, viewerUserId);
        // 记录浏览记录
        viewHistoryMapper.insert(ViewHistory.create(targetId, targetType, viewerUserId, ViewType.VIEW_RECORD));
    }

    private void recordViewer(String targetId, ViewTargetType targetType, String viewerUserId) {

        LambdaQueryWrapper<ViewHistory> queryViewHistory = new LambdaQueryWrapper<ViewHistory>()
                .eq(ViewHistory::getTargetId, targetId)
                .eq(ViewHistory::getViewerUserId, viewerUserId)
                .eq(ViewHistory::getTargetType, targetType)
                .eq(ViewHistory::getViewType, ViewType.VIEWER);
        Long count = viewHistoryMapper.selectCount(queryViewHistory);
        if (count > 0) {
            return;
        }
        viewHistoryMapper.insert(ViewHistory.create(targetId, targetType, viewerUserId, ViewType.VIEWER));
    }

    @EventListener
    @Async
    public void createContent(PublishContentEvent createContentEvent) {
        log.info("触发刷新创建新内容事件");

        String contentId = createContentEvent.getContentId();
        if (StringUtils.isEmpty(contentId)) {
            return;
        }

        ContentEnhance contentEnhance = contentService.getContentByContentIdNoStatus(contentId);
        Content content = contentEnhance.getContent();
        if (Objects.isNull(content) || !ContentStatus.NORMAL.equals(content.getStatus())) {
            log.warn("文章不存在或者状态不正常,不执行后续事件");
            return;
        }

        ContentMsgParamDto contentMsgParamDto = ContentMsgParamDto.builder()
                .contentId(content.getContentId())
                .msgId(CcUtils.getUuId())
                .contentTitle(content.getTitle())
                .build();
        String buildContentMsg = MessageBuilderInstance.buildMsg(BusinessMsgType.CONTENT, contentMsgParamDto, MsgType.INNER_MSG);

        String text = String.format("新增文章《%s》赠送积分", buildContentMsg);
        IntegralEnum integralEnum = IntegralEnum.CREATE_CONTENT;

        // 创建内容赠送
        memberIntegralService.addIntegral(integralEnum, text, createContentEvent.getUserId(), contentId);

        // 通知粉丝
        notifyFans(content);

    }


    private void notifyFans(Content content) {
        String userId = content.getUserId();

        Member contentMember = memberService.memberInfo(userId).getMember();
        int total = fanService.getFansNum(userId);

        int pageNum = total % CcConstant.Page.SIZE == 0 ? total / CcConstant.Page.SIZE : total / CcConstant.Page.SIZE + 1;

        for (int i = 0; i < pageNum; i++) {
            ListFansDto fans = fanService.getFans(userId, i + 1, CcConstant.Page.SIZE);
            if (Objects.isNull(fans)) {
                continue;
            }
            if (CollectionUtils.isEmpty(fans.getFansEnhanceList())) {
                continue;
            }

            List<FansEnhance> fansEnhanceList = fans.getFansEnhanceList();
            for (FansEnhance fansEnhance : fansEnhanceList) {
                String msgId = CcUtils.getUuId();
                ContentMsgParamDto contentMsgParamDto = ContentMsgParamDto.builder()
                        .contentId(content.getContentId())
                        .msgId(msgId)
                        .contentTitle(content.getTitle())
                        .build();
                String buildContentMsg = MessageBuilderInstance.buildMsg(BusinessMsgType.CONTENT, contentMsgParamDto, MsgType.INNER_MSG);
                String buildContentMsgEmail = MessageBuilderInstance.buildMsg(BusinessMsgType.CONTENT, contentMsgParamDto, MsgType.EMAIL);

                MemberProfileMsgParamDto memberProfileMsgParamDto = MemberProfileMsgParamDto.builder()
                        .memberUserId(contentMember.getUserId())
                        .msgId(msgId)
                        .memberUserName(contentMember.getUserName())
                        .build();
                String buildProfileMsg = MessageBuilderInstance.buildMsg(BusinessMsgType.MEMBER_PROFILE, memberProfileMsgParamDto, MsgType.INNER_MSG);
                String buildProfileMsgEmail = MessageBuilderInstance.buildMsg(BusinessMsgType.MEMBER_PROFILE, memberProfileMsgParamDto, MsgType.EMAIL);


                String fanUserId = fansEnhance.getFans().getFanUserId();
                Member member = memberService.memberInfo(fanUserId).getMember();
                String email = "您关注的" + buildProfileMsgEmail + "发表了新内容《" + buildContentMsgEmail + "》";
                String innerMessage = "您关注的" + buildProfileMsg + "发表了新内容《" + buildContentMsg + "》";

                MessageSend.send(MessageModel.builder()
                        .toUserId(member.getUserId())
                        .messageType(MessageType.SYSTEM)
                        .emailModel(MessageModel.EmailModel.builder()
                                .title(content.getTitle())
                                .content(email)
                                .build())
                        .innerModel(MessageModel.InnerModel.builder()
                                .message(innerMessage).build())
                        .messageId(msgId)
                        .build());

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
        Member member = memberService.memberInfo(contentLikeEvent.getClickUserId()).getMember();
        ContentMsgParamDto contentMsgParamDto = ContentMsgParamDto.builder()
                .contentId(content.getContentId())
                .msgId(msgId)
                .contentTitle(content.getTitle())
                .build();
        String buildContentMsg = MessageBuilderInstance.buildMsg(BusinessMsgType.CONTENT, contentMsgParamDto, MsgType.INNER_MSG);
        String buildContentMsgEmail = MessageBuilderInstance.buildMsg(BusinessMsgType.CONTENT, contentMsgParamDto, MsgType.EMAIL);

        MemberProfileMsgParamDto memberProfileMsgParamDto = MemberProfileMsgParamDto.builder()
                .memberUserId(member.getUserId())
                .msgId(msgId)
                .memberUserName(member.getUserName())
                .build();
        String buildProfileMsg = MessageBuilderInstance.buildMsg(BusinessMsgType.MEMBER_PROFILE, memberProfileMsgParamDto, MsgType.INNER_MSG);
        String buildProfileMsgEmail = MessageBuilderInstance.buildMsg(BusinessMsgType.MEMBER_PROFILE, memberProfileMsgParamDto, MsgType.EMAIL);

        String message = "您的文章《" + buildContentMsg + "》被 " + buildProfileMsg + " 在" + CcDateUtil.timeStamp2DateOnly(CcDateUtil.getCurrentTime()) + "点赞了";

        MessageSend.send(MessageModel.builder()
                .toUserId(content.getUserId())
                .messageType(MessageType.SYSTEM)
                .innerModel(MessageModel.InnerModel.builder()
                        .message(message).build())
                .messageId(msgId)
                .build());

    }


}
