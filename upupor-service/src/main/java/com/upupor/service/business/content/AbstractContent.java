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

import com.upupor.framework.CcConstant;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.service.ContentService;
import com.upupor.service.data.service.TagService;
import com.upupor.service.dto.page.ContentIndexDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.dto.page.common.TagDto;
import com.upupor.service.outer.req.ListContentReq;
import com.upupor.service.types.ContentStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.upupor.framework.thread.CompletableFuturePool.ASYNC;

/**
 * 抽象文章类
 *
 * @author cruise
 * @createTime 2021-12-31 18:03
 */

@RequiredArgsConstructor
public abstract class AbstractContent {
    @Resource
    private ContentService contentService;
    @Resource
    private TagService tagService;
    @Getter
    private final ContentIndexDto contentIndexDto = new ContentIndexDto();
    @Getter
    private String contentId;
    @Getter
    private Content content;
    @Getter
    private Integer pageNum;
    @Getter
    private Integer pageSize;

    /**
     * 获取文章
     *
     * @return
     */
    protected abstract Content queryContent();

    /**
     * 共用逻辑
     */
    protected void commonLogic() {

        // 获取文章数据
        CompletableFuture<Void> bindContentData = CompletableFuture.runAsync(() -> {
            contentService.bindContentData(Collections.singletonList(content));
        }, ASYNC);

        // 绑定文章作者
        CompletableFuture<Void> bindContentMember = CompletableFuture.runAsync(() -> {
            contentService.bindContentMember(content);
        }, ASYNC);

        // 绑定文章声明
        CompletableFuture<Void> bindContentStatement = CompletableFuture.runAsync(() -> {
            contentService.bindContentStatement(content);
        }, ASYNC);

        // 绑定最近编辑的原因
        CompletableFuture<Void> bindContentEditReason = CompletableFuture.runAsync(() -> {
            contentService.bindContentEditReason(content);
        }, ASYNC);

        // 获取文章标签
        CompletableFuture<Void> setTagDtoList = CompletableFuture.runAsync(() -> {
            List<TagDto> tagDtoList = tagService.listTagNameByTagId(content.getTagIds());
            contentIndexDto.setTagDtoList(tagDtoList);
        }, ASYNC);

        // 作者其他的文章
        CompletableFuture<Void> bindAuthorOtherContent = CompletableFuture.runAsync(this::bindAuthorOtherContent, ASYNC);

        CompletableFuture<Void> allCompletableFuture = CompletableFuture.allOf(bindContentData,
                bindContentMember,
                bindContentStatement,
                bindContentEditReason,
                setTagDtoList,
                bindAuthorOtherContent
        );

        allCompletableFuture.join();
    }

    private void bindAuthorOtherContent() {
        ListContentReq listContentReq = new ListContentReq();
        listContentReq.setPageNum(CcConstant.Page.NUM);
        listContentReq.setPageSize(CcConstant.Page.SIZE);
        listContentReq.setUserId(contentIndexDto.getContent().getUserId());
        listContentReq.setStatus(ContentStatus.NORMAL);
        ListContentDto listContentDto = contentService.listContent(listContentReq);
        if (Objects.nonNull(listContentDto)) {
            List<Content> contentList = listContentDto.getContentList();
            if (!CollectionUtils.isEmpty(contentList)) {
                // 排除当前用户正在浏览的文章
                List<Content> otherContentList = contentList.stream().filter(c -> !c.getContentId().equals(contentIndexDto.getContent().getContentId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(otherContentList)) {
                    contentIndexDto.setAuthorOtherContentList(otherContentList);
                }
            }
        }
    }

    /**
     * 个性化业务
     */
    protected abstract void individuateBusiness();

    /**
     * 页面内容
     *
     * @return
     */
    public ContentIndexDto pageContentIndexDto(String contentId, Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.contentId = contentId;

        // 获取文章
        this.content = queryContent();
        contentIndexDto.setContent(this.content);
        // 共用逻辑
        commonLogic();
        // 个性化业务
        individuateBusiness();
        return contentIndexDto;
    }


}
