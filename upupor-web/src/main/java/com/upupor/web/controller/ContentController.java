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
import com.upupor.service.outer.req.*;
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

    @PostMapping("/add")
    @ResponseBody
    @ApiOperation("创建内容")
    @UpuporLucene(dataType = LuceneDataType.CONTENT, operationType = LuceneOperationType.ADD)
    @UpuporLimit(limitType = LimitType.CREATE_CONTENT, needSpendMoney = true)
    public CcResponse add(AddContentDetailReq addContentDetailReq) {
        CcResponse cc = new CcResponse();
        OperateContentDto operateContentDto = contentService.addContent(addContentDetailReq);
        cc.setData(operateContentDto);
        return cc;
    }

    @PostMapping("/edit")
    @ResponseBody
    @UpuporLucene(dataType = LuceneDataType.CONTENT, operationType = LuceneOperationType.UPDATE)
    @ApiOperation("更新内容")
    public CcResponse edit(UpdateContentReq updateContentReq) {
        ServletUtils.checkOperatePermission(updateContentReq.getUserId());
        CcResponse cc = new CcResponse();
        OperateContentDto operateContentDto = contentService.updateContent(updateContentReq);

        cc.setData(operateContentDto);
        return cc;
    }


    @PostMapping("/status")
    @ResponseBody
    @ApiOperation("更新内容状态")
    @UpuporLucene(dataType = LuceneDataType.CONTENT, operationType = LuceneOperationType.UPDATE)
    public CcResponse status(UpdateContentReq updateContentReq) {
        CcResponse cc = new CcResponse();
        OperateContentDto operateContentDto = contentService.updateContentStatus(updateContentReq);
        cc.setData(operateContentDto);
        return cc;
    }


    @PostMapping("/auto-save")
    @ResponseBody
    @ApiOperation("自动保存")
    @UpuporLimit(limitType = LimitType.CONTENT_AUTO_SAVE)
    public CcResponse autoSave(AddContentDetailReq addContentDetailReq) {
        CcResponse cc = new CcResponse();

        return cc;
    }

    @PostMapping("/like")
    @ResponseBody
    @ApiOperation("喜欢")
    @UpuporLimit(limitType = LimitType.CLICK_LIKE, needSpendMoney = true)
    public CcResponse like(UpdateLikeReq updateLikeReq) {
        String clickUserId = ServletUtils.getUserId();
        CcResponse cc = new CcResponse();
        String contentId = updateLikeReq.getContentId();
        // 获取文章
        Content content = contentService.getNormalContent(contentId);

        // 获取文章数据
        ContentData contentData = content.getContentData();

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
            String text = String.format("您点赞了《%s》,赠送积分", String.format(CONTENT_INTEGRAL, content.getContentId(), CcUtils.getUuId(), content.getTitle()));
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
    @ResponseBody
    @ApiOperation("文章置顶")
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
            throw new BusinessException(ErrorCode.CONTENT_STATUS_NOT_NORMAL, "不能置顶");
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
            // 先将之前的置顶文章取消置顶
            pinnedContentList.forEach(c -> {
                c.setUserId(userId);
                c.setContentId(c.getContentId());
                c.setPinnedStatus(PinnedStatus.UN_PINNED);
                contentService.updateContent(c);
            });
        }
    }


}
