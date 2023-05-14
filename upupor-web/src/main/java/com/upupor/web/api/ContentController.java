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

package com.upupor.web.api;

import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.ContentData;
import com.upupor.data.dao.entity.MemberIntegral;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dao.mapper.MemberIntegralMapper;
import com.upupor.data.dto.OperateContentDto;
import com.upupor.data.types.ContentStatus;
import com.upupor.data.types.MemberIntegralStatus;
import com.upupor.data.types.PinnedStatus;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.common.IntegralEnum;
import com.upupor.framework.utils.CcUtils;
import com.upupor.lucene.UpuporLucene;
import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.lucene.enums.LuceneOperationType;
import com.upupor.security.limiter.LimitType;
import com.upupor.security.limiter.UpuporLimit;
import com.upupor.service.base.ContentService;
import com.upupor.service.base.MemberIntegralService;
import com.upupor.service.business.links.LinkBuilderInstance;
import com.upupor.service.business.links.abstracts.BusinessLinkType;
import com.upupor.service.business.links.abstracts.MsgType;
import com.upupor.service.business.links.abstracts.dto.ContentLinkParamDto;
import com.upupor.service.listener.event.ContentLikeEvent;
import com.upupor.service.outer.req.GetMemberIntegralReq;
import com.upupor.service.outer.req.PinnedReq;
import com.upupor.service.outer.req.UpdateLikeReq;
import com.upupor.service.outer.req.content.CreateContentReq;
import com.upupor.service.outer.req.content.ExistContentReq;
import com.upupor.service.outer.req.content.UpdateContentReq;
import com.upupor.service.outer.req.content.UpdateStatusReq;
import com.upupor.service.utils.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static com.upupor.framework.ErrorCode.FORBIDDEN_LIKE_SELF_CONTENT;


/**
 * 内容
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 01:51
 */
@Api(tags = "内容服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
public class ContentController {

    private final ContentService contentService;
    private final MemberIntegralService memberIntegralService;
    private final ApplicationEventPublisher eventPublisher;
    private final MemberIntegralMapper memberIntegralMapper;

    @PostMapping("/exists")
    @ApiOperation("文章是否存在")
    public CcResponse exists(@RequestBody ExistContentReq existContentReq) {
        CcResponse cc = new CcResponse();
        cc.setData(contentService.exists(existContentReq.getContentId()));
        return cc;
    }

    @PostMapping("/add")
    @ApiOperation("创建内容")
    @UpuporLucene(dataType = LuceneDataType.CONTENT, operationType = LuceneOperationType.ADD)
    @UpuporLimit(limitType = LimitType.CREATE_CONTENT, needSpendMoney = true)
    public CcResponse add(@RequestBody CreateContentReq createContentReq) {
        CcResponse cc = new CcResponse();
        OperateContentDto operateContentDto = contentService.addContent(createContentReq);
        cc.setData(operateContentDto);
        return cc;
    }

    @PostMapping("/edit")
    @UpuporLucene(dataType = LuceneDataType.CONTENT, operationType = LuceneOperationType.UPDATE)
    @ApiOperation("更新内容")
    public CcResponse edit(@RequestBody UpdateContentReq updateContentReq) {
        CcResponse cc = new CcResponse();
        OperateContentDto operateContentDto = contentService.updateContent(updateContentReq);
        cc.setData(operateContentDto);
        return cc;
    }


    @PostMapping("/status")
    @ApiOperation("更新内容状态")
    @UpuporLucene(dataType = LuceneDataType.CONTENT, operationType = LuceneOperationType.UPDATE)
    public CcResponse status(@RequestBody UpdateStatusReq updateStatusReq) {
        CcResponse cc = new CcResponse();
        OperateContentDto operateContentDto = contentService.updateContentStatus(updateStatusReq);
        cc.setData(operateContentDto);
        return cc;
    }

    @PostMapping("/like")
    @ApiOperation("喜欢")
    @UpuporLimit(limitType = LimitType.CLICK_LIKE, needSpendMoney = true)
    public CcResponse like(@RequestBody UpdateLikeReq updateLikeReq) {
        String clickUserId = SessionUtils.getUserId();
        CcResponse cc = new CcResponse();
        String contentId = updateLikeReq.getContentId();
        // 获取文章
        ContentEnhance normalContent = contentService.getNormalContent(contentId);
        Content content = normalContent.getContent();
        // 获取文章数据
        ContentData contentData = normalContent.getContentDataEnhance().getContentData();

        // 检查是否已经点过赞
        GetMemberIntegralReq getMemberIntegralReq = new GetMemberIntegralReq();
        getMemberIntegralReq.setUserId(clickUserId);
        getMemberIntegralReq.setRuleId(IntegralEnum.CLICK_LIKE.getRuleId());
        getMemberIntegralReq.setTargetId(contentId);
        getMemberIntegralReq.setStatus(MemberIntegralStatus.NORMAL);
        Boolean exists = memberIntegralService.checkExists(getMemberIntegralReq);
        if (exists) {
            // 不加状态
            List<MemberIntegral> memberIntegralList = memberIntegralService.getByGetMemberIntegralReq(getMemberIntegralReq);
            int count = 0;
            for (MemberIntegral memberIntegral : memberIntegralList) {
                count = count + memberIntegralMapper.deleteById(memberIntegral);
                contentData.minusLikeNum();
            }
            cc.setData(count > 0);
        } else {
            if (content.getUserId().equals(clickUserId)) {
                throw new BusinessException(FORBIDDEN_LIKE_SELF_CONTENT);
            }

            // 添加积分数据
            ContentLinkParamDto contentLinkParamDto = ContentLinkParamDto.builder()
                    .contentId(content.getContentId())
                    .msgId(CcUtils.getUuId())
                    .contentTitle(content.getTitle())
                    .build();
            String buildContentMsg = LinkBuilderInstance.buildLink(BusinessLinkType.CONTENT, contentLinkParamDto, MsgType.INNER_MSG);
            String text = String.format("您点赞了《%s》,赠送积分", buildContentMsg);
            memberIntegralService.addIntegral(IntegralEnum.CLICK_LIKE, text, clickUserId, contentId);
            // 增加点赞数
            contentData.incrementLikeNum();
            // 通知作者有点赞消息
            ContentLikeEvent contentLikeEvent = new ContentLikeEvent();
            contentLikeEvent.setContent(content);
            contentLikeEvent.setClickUserId(clickUserId);
            eventPublisher.publishEvent(contentLikeEvent);
        }

        contentService.updateContentData(contentData);
        cc.setData(true);
        return cc;
    }

    @PostMapping("/pinned")
    @ApiOperation("文章置顶")
    public CcResponse pinned(@RequestBody PinnedReq pinnedReq) {

        String userId = SessionUtils.getUserId();

        String contentId = pinnedReq.getContentId();
        ContentEnhance contentEnhance = contentService.getContentByContentIdNoStatus(contentId);
        Content content = contentEnhance.getContent();
        if (Objects.isNull(content)) {
            throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
        }

        if (!content.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.CONTENT_NOT_BELONG_TO_YOU);
        }

        if (!content.getStatus().equals(ContentStatus.NORMAL)) {
            throw new BusinessException(ErrorCode.CONTENT_STATUS_NOT_NORMAL, "不能置顶");
        }

        if (PinnedStatus.PINNED.equals(pinnedReq.getPinnedStatus())) {
            cancelBeforePinnedContent(userId);

            content.setContentId(contentId);
            content.setUserId(userId);
            content.setPinnedStatus(PinnedStatus.PINNED);
            contentService.updateContent(contentEnhance);
        } else if (PinnedStatus.UN_PINNED.equals(pinnedReq.getPinnedStatus())) {
            cancelBeforePinnedContent(userId);
        }

        return new CcResponse();
    }

    private void cancelBeforePinnedContent(String userId) {
        List<ContentEnhance> pinnedContentList = contentService.getContentListByPinned(PinnedStatus.PINNED, userId);
        if (!CollectionUtils.isEmpty(pinnedContentList)) {
            // 先将之前的置顶文章取消置顶
            pinnedContentList.forEach(contentEnhance -> {
                Content content = contentEnhance.getContent();
                content.setUserId(userId);
                content.setContentId(content.getContentId());
                content.setPinnedStatus(PinnedStatus.UN_PINNED);
                contentService.updateContent(contentEnhance);
            });
        }
    }


}
