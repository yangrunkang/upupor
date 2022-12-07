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

import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dto.page.ContentIndexDto;
import com.upupor.data.dto.page.ad.AbstractAd;
import com.upupor.data.types.CollectType;
import com.upupor.data.types.ContentStatus;
import com.upupor.data.types.ViewTargetType;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.common.IntegralEnum;
import com.upupor.service.utils.JwtUtils;
import com.upupor.service.aggregation.CommonAggregateService;
import com.upupor.service.base.*;
import com.upupor.service.outer.req.GetMemberIntegralReq;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.upupor.framework.thread.completable.CompletableFuturePool.ASYNC;

/**
 * 公开的内容
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
    protected ContentEnhance queryContent() {
        ContentEnhance contentEnhance = contentService.getContentDetail(getContentId());
        // 提示文章已删除
        if (contentEnhance.getContent().getStatus().equals(ContentStatus.DELETED)) {
            throw new BusinessException(ErrorCode.ARTICLE_DELETED);
        }
        // 进一步校验访问来源
        if (!contentEnhance.getContent().getStatus().equals(ContentStatus.NORMAL)) {
            throw new BusinessException(ErrorCode.ILLEGAL_PATH_TO_VIEW_CONTENT);
        }
        return contentEnhance;
    }


    @Override
    protected void individuateBusiness() {
        ContentEnhance contentEnhance = getContentEnhance();
        Content content = contentEnhance.getContent();
        Integer pageNum = getPageNum();
        Integer pageSize = getPageSize();
        ContentIndexDto contentIndexDto = getContentIndexDto();


        // 获取评论信息
        CompletableFuture<Void> bindContentComment = CompletableFuture.runAsync(() -> {
            commentService.bindContentComment(contentEnhance, pageNum, pageSize);
        }, ASYNC);

        // 绑定点赞的人
        CompletableFuture<Void> bindLikesMember = CompletableFuture.runAsync(() -> {
            contentService.bindLikesMember(contentEnhance);
        }, ASYNC);

        // 上一篇下一篇
        CompletableFuture<Void> lastAndNextContent = CompletableFuture.runAsync(() -> {
            contentService.lastAndNextContent(contentEnhance);
        }, ASYNC);

        // 记录访问者
        CompletableFuture<Void> setViewerList = CompletableFuture.runAsync(() -> {
            contentEnhance.setViewHistoryEnhanceList(viewerService.listViewerByTargetIdAndType(content.getContentId(), ViewTargetType.CONTENT));
        }, ASYNC);

        // 文章浏览数数据+1
        CompletableFuture<Void> viewNumPlusOne = CompletableFuture.runAsync(() -> {
            contentService.viewNumPlusOne(content.getContentId());
        }, ASYNC);

        // 绑定文章收藏数
        CompletableFuture<Void> handleContentCollectNum = CompletableFuture.runAsync(this::handleContentCollectNum, ASYNC);

        // 推荐文章
        CompletableFuture<Void> setRandomContentList = CompletableFuture.runAsync(() -> {
            contentIndexDto.setRandomContentEnhanceList(contentService.randomContent(content.getUserId()));
            AbstractAd.ad(contentIndexDto.getRandomContentEnhanceList());
        }, ASYNC);

        // 记录访问者
        CompletableFuture<Void> addViewer = CompletableFuture.runAsync(() -> {
            viewerService.addViewer(getContentId(), ViewTargetType.CONTENT);
        }, ASYNC);

        // 文章作者是否有电台
        CompletableFuture<Void> setHasRadio = CompletableFuture.runAsync(() -> {
            contentIndexDto.setHasRadio(radioService.userHasRadio(content.getUserId()));
        }, ASYNC);

        CompletableFuture<Void> setting = CompletableFuture.runAsync(() -> {
            settingIsAttention(contentIndexDto, content);
            settingIsLike(contentIndexDto, content);
            settingIsCollect(contentIndexDto, content);
        }, ASYNC);

        // 快捷创建文章入库url
        CompletableFuture<Void> setCreateContentDesc = CompletableFuture.runAsync(() -> {
            contentIndexDto.setCreateContentDesc(CommonAggregateService.getCreateContentInfo(content.getContentType(), content.getTagIds(), tagService.getNameById(content.getTagIds())));
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
     * 处理文章收藏数
     */
    private void handleContentCollectNum() {
        ContentEnhance contentEnhance = getContentEnhance();
        if (Objects.isNull(contentEnhance.getContent())) {
            throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
        }
        Integer collectNum = collectService.collectNum(CollectType.CONTENT, contentEnhance.getContent().getContentId());
        contentEnhance.setCollectNum(collectNum);
    }


    private void settingIsAttention(ContentIndexDto contentIndexDto, Content content) {
        String contentUserId = content.getUserId();
        contentIndexDto.setCurrUserIsAttention(contentService.currentUserIsAttentionAuthor(contentUserId));
    }

    private void settingIsLike(ContentIndexDto contentIndexDto, Content content) {
        boolean currUserIsClickLike = false;
        try {
            GetMemberIntegralReq getMemberIntegralReq = new GetMemberIntegralReq();
            getMemberIntegralReq.setUserId(JwtUtils.getUserId());
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
            currUserIsCollect = collectService.existsCollectContent(content.getContentId(), JwtUtils.getUserId());
        } catch (Exception ignored) {
        }
        contentIndexDto.setCurrUserIsCollect(currUserIsCollect);
    }
}
