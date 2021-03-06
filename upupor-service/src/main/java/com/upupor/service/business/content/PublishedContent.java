/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.service.business.content;

import com.upupor.service.business.ad.AbstractAd;
import com.upupor.service.data.aggregation.CommonAggregateService;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.service.*;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.dto.page.ContentIndexDto;
import com.upupor.service.outer.req.GetMemberIntegralReq;
import com.upupor.service.types.CollectType;
import com.upupor.service.types.ContentStatus;
import com.upupor.service.types.ViewTargetType;
import com.upupor.service.utils.ServletUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.upupor.framework.thread.completable.CompletableFuturePool.ASYNC;

/**
 * ???????????????
 *
 * @author cruise
 * @createTime 2021-12-31  18:03
 */
@Component
public class PublishedContent extends AbstractContent {
    @Resource
    private ContentService contentService;
    @Resource
    private CommentService commentService;
    @Resource
    private ViewerService viewerService;
    @Resource
    private CollectService collectService;
    @Resource
    private RadioService radioService;
    @Resource
    private MemberIntegralService memberIntegralService;
    @Resource
    private TagService tagService;

    @Override
    protected Content queryContent() {
        Content content = contentService.getContentDetail(getContentId());
        // ?????????????????????
        if (content.getStatus().equals(ContentStatus.DELETED)) {
            throw new BusinessException(ErrorCode.ARTICLE_DELETED);
        }
        // ???????????????????????????
        if (!content.getStatus().equals(ContentStatus.NORMAL)) {
            throw new BusinessException(ErrorCode.ILLEGAL_PATH_TO_VIEW_CONTENT);
        }
        return content;
    }


    @Override
    protected void individuateBusiness() {
        Content content = getContent();
        Integer pageNum = getPageNum();
        Integer pageSize = getPageSize();
        ContentIndexDto contentIndexDto = getContentIndexDto();


        // ??????????????????
        CompletableFuture<Void> bindContentComment = CompletableFuture.runAsync(() -> {
            commentService.bindContentComment(content, pageNum, pageSize);
        }, ASYNC);

        // ??????????????????
        CompletableFuture<Void> bindLikesMember = CompletableFuture.runAsync(() -> {
            contentService.bindLikesMember(content);
        }, ASYNC);

        // ??????????????????
        CompletableFuture<Void> lastAndNextContent = CompletableFuture.runAsync(() -> {
            contentService.lastAndNextContent(content);
        }, ASYNC);

        // ???????????????
        CompletableFuture<Void> setViewerList = CompletableFuture.runAsync(() -> {
            content.setViewerList(viewerService.listViewerByTargetIdAndType(content.getContentId(), ViewTargetType.CONTENT));
        }, ASYNC);

        // ?????????????????????+1
        CompletableFuture<Void> viewNumPlusOne = CompletableFuture.runAsync(() -> {
            contentService.viewNumPlusOne(content.getContentId());
        }, ASYNC);

        // ?????????????????????
        CompletableFuture<Void> handleContentCollectNum = CompletableFuture.runAsync(this::handleContentCollectNum, ASYNC);

        // ????????????
        CompletableFuture<Void> setRandomContentList = CompletableFuture.runAsync(() -> {
            contentIndexDto.setRandomContentList(contentService.randomContent(content.getUserId()));
            AbstractAd.ad(contentIndexDto.getRandomContentList());
        }, ASYNC);

        // ???????????????
        CompletableFuture<Void> addViewer = CompletableFuture.runAsync(() -> {
            viewerService.addViewer(getContentId(), ViewTargetType.CONTENT);
        }, ASYNC);

        // ???????????????????????????
        CompletableFuture<Void> setHasRadio = CompletableFuture.runAsync(() -> {
            contentIndexDto.setHasRadio(radioService.userHasRadio(content.getUserId()));
        }, ASYNC);

        CompletableFuture<Void> setting = CompletableFuture.runAsync(() -> {
            settingIsAttention(contentIndexDto,content);
            settingIsLike(contentIndexDto,content);
            settingIsCollect(contentIndexDto,content);
        }, ASYNC);

        // ????????????????????????url
        CompletableFuture<Void> setCreateContentDesc = CompletableFuture.runAsync(() -> {
            contentIndexDto.setCreateContentDesc(CommonAggregateService.getCreateContentInfo(content.getContentType(), content.getTagIds(),tagService.getNameById(content.getTagIds())));
        }, ASYNC);

        CompletableFuture<Void> allCompletableFuture = CompletableFuture.allOf(
                bindContentComment,
                bindLikesMember,
                lastAndNextContent,
                setViewerList,
                viewNumPlusOne,
                handleContentCollectNum,
                setRandomContentList,
                addViewer,
                setHasRadio,
                setting,
                setCreateContentDesc
        );
        allCompletableFuture.join();
    }


    /**
     * ?????????????????????
     */
    private void handleContentCollectNum() {
        Content content = getContent();
        if (Objects.isNull(content)) {
            throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
        }
        Integer collectNum = collectService.collectNum(CollectType.CONTENT, content.getContentId());
        content.setCollectNum(collectNum);
    }


    private void settingIsAttention(ContentIndexDto contentIndexDto, Content content) {
        String contentUserId = content.getUserId();
        contentIndexDto.setCurrUserIsAttention(contentService.currentUserIsAttentionAuthor(contentUserId));
    }

    private void settingIsLike(ContentIndexDto contentIndexDto, Content content) {
        boolean currUserIsClickLike = false;
        try {
            GetMemberIntegralReq getMemberIntegralReq = new GetMemberIntegralReq();
            getMemberIntegralReq.setUserId(ServletUtils.getUserId());
            getMemberIntegralReq.setRuleId(IntegralEnum.CLICK_LIKE.getRuleId());
            getMemberIntegralReq.setTargetId(content.getContentId());
            currUserIsClickLike = memberIntegralService.checkExists(getMemberIntegralReq);
        } catch (Exception ignored) {
        }
        contentIndexDto.setCurrUserIsClickLike(currUserIsClickLike);
    }

    private void settingIsCollect(ContentIndexDto contentIndexDto, Content content) {
        boolean currUserIsCollect = false;
        try {
            currUserIsCollect = collectService.existsCollectContent(content.getContentId(), ServletUtils.getUserId());
        } catch (Exception ignored) {
        }
        contentIndexDto.setCurrUserIsCollect(currUserIsCollect);
    }
}
