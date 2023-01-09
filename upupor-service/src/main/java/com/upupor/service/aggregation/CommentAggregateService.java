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

package com.upupor.service.aggregation;

import com.upupor.data.dao.entity.enhance.CommentEnhance;
import com.upupor.data.dto.page.CommentIndexDto;
import com.upupor.data.dto.page.comment.CommentDto;
import com.upupor.data.dto.page.common.ListCommentDto;
import com.upupor.data.dto.query.ListCommentQuery;
import com.upupor.data.types.CommentStatus;
import com.upupor.framework.thread.UpuporForkJoin;
import com.upupor.service.base.CommentService;
import com.upupor.service.business.comment.list.abstracts.AbstractCommentList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * 评论服务
 *
 * @author Yang Runkang (cruise)
 * @date 1/9/2023
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentAggregateService {
    private final CommentService commentService;
    @Resource
    private List<AbstractCommentList<?>> abstractCommentListList;

    public CommentIndexDto index(Integer pageNum, Integer pageSize) {
        CommentIndexDto commentIndexDto = new CommentIndexDto();

        ListCommentQuery listCommentQuery = new ListCommentQuery();
        listCommentQuery.setStatus(CommentStatus.NORMAL);
        listCommentQuery.setPageNum(pageNum);
        listCommentQuery.setPageSize(pageSize);
        listCommentQuery.setQueryAll(Boolean.TRUE);
        listCommentQuery.setOrderByCreateTimeBool(Boolean.TRUE);
        ListCommentDto listCommentDto = commentService.listComment(listCommentQuery);
        List<CommentEnhance> commentEnhanceList = listCommentDto.getCommentEnhanceList();

        List<CommentDto> commentDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(commentEnhanceList)) {
            List<CompletableFuture<Void>> voidCompletableFutureList = new ArrayList<>();
            for (AbstractCommentList<?> abstractCommentList : abstractCommentListList) {
                CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                    commentDtoList.addAll(abstractCommentList.handle(commentEnhanceList));
                }, UpuporForkJoin.commentListForkJoin());
                voidCompletableFutureList.add(voidCompletableFuture);
            }
            CompletableFuture.allOf(voidCompletableFutureList.toArray(new CompletableFuture[0])).join();
        }

        commentIndexDto.setCommentDtoList(commentDtoList);
        commentIndexDto.setTotal(listCommentDto.getTotal());
        commentIndexDto.setPageDtoList(listCommentDto.getPageDtoList());

        return commentIndexDto;
    }
}
