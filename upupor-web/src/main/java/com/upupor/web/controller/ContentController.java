package com.upupor.web.controller;

import com.upupor.service.common.*;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.ContentData;
import com.upupor.service.dao.entity.MemberIntegral;
import com.upupor.service.listener.event.ContentLikeEvent;
import com.upupor.service.service.ContentService;
import com.upupor.service.service.MemberIntegralService;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.RedisUtil;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.MsgTemplate.CONTENT_INTEGRAL;
import static com.upupor.service.common.ErrorCode.FORBIDDEN_LIKE_SELF_CONTENT;


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

    @PostMapping("/add")
    @ResponseBody
    @ApiOperation("创建内容")
    public CcResponse add(AddContentDetailReq addContentDetailReq) {
        CcResponse cc = new CcResponse();

        Boolean addSuccess = contentService.addContent(addContentDetailReq);
        if (addSuccess) {
            // 发布成功,清除Key
            RedisUtil.remove(CcConstant.CvCache.CONTENT_CACHE_KEY + ServletUtils.getUserId());
        }
        cc.setData(addSuccess);
        return cc;
    }

    @PostMapping("/edit")
    @ResponseBody
    @ApiOperation("更新内容")
    public CcResponse edit(UpdateContentReq updateContentReq) {
        ServletUtils.checkOperatePermission(updateContentReq.getUserId());
        CcResponse cc = new CcResponse();
        Boolean editSuccess = contentService.updateContent(updateContentReq);
        if (editSuccess) {
            // 发布成功,清除Key
            RedisUtil.remove(CcConstant.CvCache.CONTENT_CACHE_KEY + ServletUtils.getUserId());
        }
        cc.setData(editSuccess);
        return cc;
    }


    @PostMapping("/status")
    @ResponseBody
    @ApiOperation("更新内容状态")
    public CcResponse status(UpdateContentReq updateContentReq) {
        CcResponse cc = new CcResponse();
        Boolean editSuccess = contentService.updateStatus(updateContentReq);
        if (editSuccess) {
            // 发布成功,清除Key
            RedisUtil.remove(CcConstant.CvCache.CONTENT_CACHE_KEY + ServletUtils.getUserId());
        }
        cc.setData(editSuccess);
        return cc;
    }

    @GetMapping("/get/{contentId}")
    @ResponseBody
    @ApiOperation("获取内容")
    public CcResponse get(@PathVariable("contentId") String contentId) {
        CcResponse cc = new CcResponse();
        GetContentReq getContentReq = new GetContentReq();
        getContentReq.setContentId(contentId);
        cc.setData(contentService.getContentDetail(contentId));
        return cc;
    }

    @PostMapping("/like")
    @ResponseBody
    @ApiOperation("喜欢")
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
        Boolean exists = memberIntegralService.checkExists(getMemberIntegralReq);
        if (exists) {
            // 不加状态
            List<MemberIntegral> memberIntegralList = memberIntegralService.getByGetMemberIntegralReq(getMemberIntegralReq);
            if (CollectionUtils.isEmpty(memberIntegralList)) {
                // exists 为true,却没有数据,原则上要抛出异常,这里不抛出,直接返回cc
                return cc;
            }
            changeStatus(contentData, memberIntegralList);
        } else {
            // 如果系统中有删除的,则变更状态,否则新增积分数据
            List<MemberIntegral> memberIntegralList = memberIntegralService.getByGetMemberIntegralReq(getMemberIntegralReq);
            if (CollectionUtils.isEmpty(memberIntegralList)) {
                if (content.getUserId().equals(clickUserId)) {
                    throw new BusinessException(FORBIDDEN_LIKE_SELF_CONTENT);
                }

                // 增加点赞数
                addLikeNum(contentData);

                // 添加积分数据
                String text = String.format("您点赞了《%s》,赠送积分", String.format(CONTENT_INTEGRAL, content.getContentId(), CcUtils.getUuId(), content.getTitle()));
                memberIntegralService.addIntegral(IntegralEnum.CLICK_LIKE, text, clickUserId, contentId);

                // 通知作者有点赞消息
                ContentLikeEvent contentLikeEvent = new ContentLikeEvent();
                contentLikeEvent.setContent(content);
                contentLikeEvent.setClickUserId(clickUserId);
                eventPublisher.publishEvent(contentLikeEvent);
            } else {
                changeStatus(contentData, memberIntegralList);
            }
        }

        contentService.updateContentData(contentData);
        cc.setData(true);
        return cc;
    }

    private void addLikeNum(ContentData contentData) {
        Integer likeNum = contentData.getLikeNum();
        contentData.setLikeNum(likeNum + 1);
    }

    private void changeStatus(ContentData contentData, List<MemberIntegral> memberIntegralList) {
        if (CollectionUtils.isEmpty(memberIntegralList)) {
            return;
        }
        if (Objects.isNull(contentData)) {
            return;
        }

        memberIntegralList.forEach(memberIntegral -> {
            if (memberIntegral.getStatus().equals(CcEnum.MemberIntegralStatus.DELETED.getStatus())) {
                memberIntegral.setStatus(CcEnum.MemberIntegralStatus.NORMAL.getStatus());
                // 新增点赞数
                addLikeNum(contentData);
            } else {
                memberIntegral.setStatus(CcEnum.MemberIntegralStatus.DELETED.getStatus());
                // 扣减点赞数
                minusLikeNum(contentData);
            }
            memberIntegralService.update(memberIntegral);
        });
    }

    private void minusLikeNum(ContentData contentData) {
        Integer likeNum = contentData.getLikeNum();
        contentData.setLikeNum(likeNum - 1);
    }


    @PostMapping("/check")
    @ResponseBody
    @ApiOperation("检查文章状态是否正常")
    public CcResponse check(GetContentStatusReq getContentStatusReq) {
        CcResponse cc = new CcResponse();

        String contentId = getContentStatusReq.getContentId();

        Boolean isOk = contentService.checkStatusIsOk(contentId);
        cc.setData(isOk);
        return cc;
    }

    @PostMapping("/pinned")
    @ResponseBody
    @ApiOperation("文章置顶")
    public CcResponse pinned(AddPinnedReq addPinnedReq) {

        String userId = ServletUtils.getUserId();

        String contentId = addPinnedReq.getContentId();
        Content content = contentService.getContentByContentIdNoStatus(contentId);
        if (Objects.isNull(content)) {
            throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
        }

        if (!content.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.CONTENT_NOT_BELONG_TO_YOU);
        }

        if (!content.getStatus().equals(CcEnum.ContentStatus.NORMAL.getStatus())) {
            throw new BusinessException(ErrorCode.CONTENT_STATUS_NOT_NORMAL, "不能置顶");
        }

        List<Content> pinnedContentList = contentService.getContentListByPinned(CcEnum.PinnedStatus.PINNED.getStatus(), userId);
        if (!CollectionUtils.isEmpty(pinnedContentList)) {
            // 先将之前的置顶文章取消置顶
            pinnedContentList.forEach(c -> {
                c.setUserId(userId);
                c.setContentId(c.getContentId());
                c.setPinnedStatus(CcEnum.PinnedStatus.UN_PINNED.getStatus());
                contentService.updateContent(c);
            });
        }

        content.setContentId(contentId);
        content.setUserId(userId);
        content.setPinnedStatus(CcEnum.PinnedStatus.PINNED.getStatus());
        contentService.updateContent(content);
        return new CcResponse();
    }


}
