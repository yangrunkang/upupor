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

package com.upupor.web.controller;

import com.upupor.framework.BusinessException;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcUtils;
import com.upupor.lucene.UpuporLucene;
import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.lucene.enums.LuceneOperationType;
import com.upupor.security.limiter.LimitType;
import com.upupor.security.limiter.UpuporLimit;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.dao.entity.ContentData;
import com.upupor.service.data.dao.entity.MemberIntegral;
import com.upupor.service.data.dao.mapper.MemberIntegralMapper;
import com.upupor.service.data.service.ContentService;
import com.upupor.service.data.service.MemberIntegralService;
import com.upupor.service.dto.OperateContentDto;
import com.upupor.service.listener.event.ContentLikeEvent;
import com.upupor.service.outer.req.GetMemberIntegralReq;
import com.upupor.service.outer.req.PinnedReq;
import com.upupor.service.outer.req.UpdateLikeReq;
import com.upupor.service.outer.req.content.CreateContentReq;
import com.upupor.service.outer.req.content.ExistContentReq;
import com.upupor.service.outer.req.content.UpdateContentReq;
import com.upupor.service.outer.req.content.UpdateStatusReq;
import com.upupor.service.types.ContentStatus;
import com.upupor.service.types.MemberIntegralStatus;
import com.upupor.service.types.PinnedStatus;
import com.upupor.service.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static com.upupor.framework.CcConstant.MsgTemplate.CONTENT_INTEGRAL;
import static com.upupor.framework.ErrorCode.FORBIDDEN_LIKE_SELF_CONTENT;


/**
 * ??????
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 01:51
 */
@Api(tags = "????????????")
@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
public class ContentController {

    private final ContentService contentService;
    private final MemberIntegralService memberIntegralService;
    private final ApplicationEventPublisher eventPublisher;
    private final MemberIntegralMapper memberIntegralMapper;

    @PostMapping("/exists")
    @ResponseBody
    @ApiOperation("??????????????????")
    public CcResponse exists(ExistContentReq existContentReq) {
        CcResponse cc = new CcResponse();
        cc.setData(contentService.exists(existContentReq.getContentId()));
        return cc;
    }

    @PostMapping("/add")
    @ResponseBody
    @ApiOperation("????????????")
    @UpuporLucene(dataType = LuceneDataType.CONTENT, operationType = LuceneOperationType.ADD)
    @UpuporLimit(limitType = LimitType.CREATE_CONTENT, needSpendMoney = true)
    public CcResponse add(CreateContentReq createContentReq) {
        CcResponse cc = new CcResponse();
        OperateContentDto operateContentDto = contentService.addContent(createContentReq);
        cc.setData(operateContentDto);
        return cc;
    }

    @PostMapping("/edit")
    @ResponseBody
    @UpuporLucene(dataType = LuceneDataType.CONTENT, operationType = LuceneOperationType.UPDATE)
    @ApiOperation("????????????")
    public CcResponse edit(UpdateContentReq updateContentReq) {
        CcResponse cc = new CcResponse();
        OperateContentDto operateContentDto = contentService.updateContent(updateContentReq);
        cc.setData(operateContentDto);
        return cc;
    }


    @PostMapping("/status")
    @ResponseBody
    @ApiOperation("??????????????????")
    @UpuporLucene(dataType = LuceneDataType.CONTENT, operationType = LuceneOperationType.UPDATE)
    public CcResponse status(UpdateStatusReq updateStatusReq) {
        CcResponse cc = new CcResponse();
        OperateContentDto operateContentDto = contentService.updateContentStatus(updateStatusReq);
        cc.setData(operateContentDto);
        return cc;
    }

    @PostMapping("/like")
    @ResponseBody
    @ApiOperation("??????")
    @UpuporLimit(limitType = LimitType.CLICK_LIKE, needSpendMoney = true)
    public CcResponse like(UpdateLikeReq updateLikeReq) {
        String clickUserId = ServletUtils.getUserId();
        CcResponse cc = new CcResponse();
        String contentId = updateLikeReq.getContentId();
        // ????????????
        Content content = contentService.getNormalContent(contentId);

        // ??????????????????
        ContentData contentData = content.getContentData();

        // ???????????????????????????
        GetMemberIntegralReq getMemberIntegralReq = new GetMemberIntegralReq();
        getMemberIntegralReq.setUserId(clickUserId);
        getMemberIntegralReq.setRuleId(IntegralEnum.CLICK_LIKE.getRuleId());
        getMemberIntegralReq.setTargetId(contentId);
        getMemberIntegralReq.setStatus(MemberIntegralStatus.NORMAL);
        Boolean exists = memberIntegralService.checkExists(getMemberIntegralReq);
        if (exists) {
            // ????????????
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

            // ??????????????????
            String text = String.format("???????????????%s???,????????????", String.format(CONTENT_INTEGRAL, content.getContentId(), CcUtils.getUuId(), content.getTitle()));
            memberIntegralService.addIntegral(IntegralEnum.CLICK_LIKE, text, clickUserId, contentId);
            // ???????????????
            contentData.incrementLikeNum();
            // ???????????????????????????
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
    @ResponseBody
    @ApiOperation("????????????")
    public CcResponse pinned(PinnedReq pinnedReq) {

        String userId = ServletUtils.getUserId();

        String contentId = pinnedReq.getContentId();
        Content content = contentService.getContentByContentIdNoStatus(contentId);
        if (Objects.isNull(content)) {
            throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
        }

        if (!content.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.CONTENT_NOT_BELONG_TO_YOU);
        }

        if (!content.getStatus().equals(ContentStatus.NORMAL)) {
            throw new BusinessException(ErrorCode.CONTENT_STATUS_NOT_NORMAL, "????????????");
        }

        if (PinnedStatus.PINNED.equals(pinnedReq.getPinnedStatus())) {
            cancelBeforePinnedContent(userId);

            content.setContentId(contentId);
            content.setUserId(userId);
            content.setPinnedStatus(PinnedStatus.PINNED);
            contentService.updateContent(content);
        } else if (PinnedStatus.UN_PINNED.equals(pinnedReq.getPinnedStatus())) {
            cancelBeforePinnedContent(userId);
        }

        return new CcResponse();
    }

    private void cancelBeforePinnedContent(String userId) {
        List<Content> pinnedContentList = contentService.getContentListByPinned(PinnedStatus.PINNED, userId);
        if (!CollectionUtils.isEmpty(pinnedContentList)) {
            // ???????????????????????????????????????
            pinnedContentList.forEach(c -> {
                c.setUserId(userId);
                c.setContentId(c.getContentId());
                c.setPinnedStatus(PinnedStatus.UN_PINNED);
                contentService.updateContent(c);
            });
        }
    }


}
