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

import com.upupor.data.dao.entity.Comment;
import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.Radio;
import com.upupor.data.dao.entity.enhance.CommentEnhance;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.dto.page.CommentIndexDto;
import com.upupor.data.dto.page.comment.CommentDto;
import com.upupor.data.dto.page.common.ListCommentDto;
import com.upupor.data.dto.query.ListCommentQuery;
import com.upupor.data.types.CommentStatus;
import com.upupor.data.types.ContentStatus;
import com.upupor.data.types.ContentType;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.service.base.CommentService;
import com.upupor.service.base.ContentService;
import com.upupor.service.base.MemberService;
import com.upupor.service.base.RadioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
    private final ContentService contentService;
    private final RadioService radioService;
    private final MemberService memberService;

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

        List<String> contentIdList =
                commentEnhanceList.stream()
                        .map(CommentEnhance::getComment)
                        .filter(s -> ContentType.isContent(s.getCommentSource()))
                        .map(Comment::getTargetId)
                        .collect(Collectors.toList());

        List<String> messageBoardUserIdList =
                commentEnhanceList.stream()
                        .map(CommentEnhance::getComment)
                        .filter(s -> ContentType.isMessagetBoard(s.getCommentSource()))
                        .map(Comment::getTargetId)
                        .collect(Collectors.toList());

        List<String> radioIdList =
                commentEnhanceList.stream()
                        .map(CommentEnhance::getComment)
                        .filter(s -> ContentType.isRadio(s.getCommentSource()))
                        .map(Comment::getTargetId)
                        .collect(Collectors.toList());

        Map<String, Content> contentMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(contentIdList)) {
            List<ContentEnhance> contentEnhances = contentService.listByContentIdList(contentIdList);
            contentEnhances.stream()
                    .map(ContentEnhance::getContent)
                    .filter(content -> ContentStatus.NORMAL.equals(content.getStatus()))
                    .forEach(s -> contentMap.putIfAbsent(s.getContentId(), s))
            ;
        }

        Map<String, Radio> radioMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(radioIdList)) {
            List<Radio> radios = radioService.listByRadioId(radioIdList);
            radios.forEach(s -> radioMap.putIfAbsent(s.getRadioId(), s));
        }

        Map<String, Member> memberMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(messageBoardUserIdList)) {
            List<MemberEnhance> memberEnhances = memberService.listByUserIdList(messageBoardUserIdList);
            memberEnhances.stream()
                    .map(MemberEnhance::getMember)
                    .forEach(s -> memberMap.putIfAbsent(s.getUserId(), s))
            ;
        }

        List<CommentDto> commentDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(commentEnhanceList)) {
            for (CommentEnhance commentEnhance : commentEnhanceList) {
                Comment comment = commentEnhance.getComment();
                ContentType commentSource = comment.getCommentSource();
                String targetId = comment.getTargetId();

                if (ContentType.isContent(commentSource)) {
                    Content content = contentMap.get(targetId);
                    commentDtoList.add(CommentDto.create(comment.getCommentContent(),
                            "/u/" + content.getContentId(), content.getTitle(), commentEnhance
                    ));
                } else if (ContentType.isMessagetBoard(commentSource)) {
                    Member member = memberMap.get(targetId);
                    commentDtoList.add(CommentDto.create(comment.getCommentContent(),
                            "/profile/" + member.getUserId() + "/message", member.getUserName(), commentEnhance
                    ));
                } else if (ContentType.isRadio(commentSource)) {
                    Radio radio = radioMap.get(targetId);
                    commentDtoList.add(CommentDto.create(comment.getCommentContent(),
                            "/r/" + radio.getRadioId(),
                            radio.getRadioIntro(), commentEnhance
                    ));
                } else {
                    throw new BusinessException(ErrorCode.UNKNOWN_TYPE_CONTENT);
                }
            }
        }

        commentIndexDto.setCommentDtoList(commentDtoList);
        commentIndexDto.setTotal(listCommentDto.getTotal());
        commentIndexDto.setPageDtoList(listCommentDto.getPageDtoList());

        return commentIndexDto;
    }
}
