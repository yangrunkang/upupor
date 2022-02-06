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
import com.upupor.service.business.aggregation.CommonAggregateService;
import com.upupor.service.business.aggregation.dao.entity.Content;
import com.upupor.service.business.aggregation.service.*;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
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

    @Override
    protected Content queryContent() {
        Content content = contentService.getContentDetail(getContentId());
        // 提示文章已删除
        if (content.getStatus().equals(ContentStatus.DELETED)) {
            throw new BusinessException(ErrorCode.ARTICLE_DELETED);
        }
        // 进一步校验访问来源
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

        // 获取评论信息
        commentService.bindContentComment(content, pageNum, pageSize);

        // 绑定点赞的人
        contentService.bindLikesMember(content);

        // 上一篇下一篇
        contentService.lastAndNextContent(content);

        // 记录访问者
        content.setViewerList(viewerService.listViewerByTargetIdAndType(content.getContentId(), ViewTargetType.CONTENT));

        // 文章浏览数数据+1
        contentService.viewNumPlusOne(content.getContentId());
        // 绑定文章收藏数
        handleContentCollectNum();

        // 推荐文章
        contentIndexDto.setRandomContentList(contentService.randomContent(content.getUserId()));
        AbstractAd.ad(contentIndexDto.getRandomContentList());

        // 记录访问者
        viewerService.addViewer(getContentId(), ViewTargetType.CONTENT);

        // 文章作者是否有电台
        contentIndexDto.setHasRadio(radioService.userHasRadio(content.getUserId()));

        settingIsAttention(contentIndexDto,content);
        settingIsLike(contentIndexDto,content);
        settingIsCollect(contentIndexDto,content);

        // 创建文章
        contentIndexDto.setCreateContentDesc(CommonAggregateService.getCreateContentInfo(content.getContentType(), null));
    }


    /**
     * 处理文章收藏数
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
