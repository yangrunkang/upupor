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

package com.upupor.data.dao.entity.converter;

import com.upupor.data.dao.entity.*;
import com.upupor.data.dao.entity.enhance.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-11-28 1:00
 * @email: yangrunkang53@gmail.com
 */
public class Converter {

    public static List<CollectEnhance> collectEnhance(List<Collect> collectList) {
        if (CollectionUtils.isEmpty(collectList)) {
            return new ArrayList<>();
        }

        return collectList.stream().map(c -> {
            return CollectEnhance.builder()
                    .collect(c)
                    .build();
        }).collect(Collectors.toList());
    }

    public static List<AttentionEnhance> attentionEnhance(List<Attention> attentionList) {
        if (CollectionUtils.isEmpty(attentionList)) {
            return new ArrayList<>();
        }

        return attentionList.stream().map(c -> {
            return AttentionEnhance.builder()
                    .attention(c)
                    .build();
        }).collect(Collectors.toList());
    }


    public static List<CommentEnhance> commentEnhance(List<Comment> commentList) {
        if (CollectionUtils.isEmpty(commentList)) {
            return new ArrayList<>();
        }

        return commentList.stream().map(c -> {
            return CommentEnhance.builder()
                    .comment(c)
                    .build();
        }).collect(Collectors.toList());
    }

    public static CommentEnhance commentEnhance(Comment comment, MemberEnhance memberEnhance) {
        return CommentEnhance.builder()
                .comment(comment)
                .memberEnhance(memberEnhance)
                .build();
    }

    public static MemberEnhance memberEnhance(Member member) {
        return MemberEnhance.builder()
                .member(member)
                .build();
    }

    public static List<MemberEnhance> memberEnhance(List<Member> memberList) {
        if (CollectionUtils.isEmpty(memberList)) {
            return new ArrayList<>();
        }
        return memberList.stream().map(member -> {
            return memberEnhance(member);
        }).collect(Collectors.toList());
    }

    public static List<MemberIntegralEnhance> memberIntegralEnhance(List<MemberIntegral> memberIntegralList) {
        if (CollectionUtils.isEmpty(memberIntegralList)) {
            return new ArrayList<>();
        }
        return memberIntegralList.stream().map(memberIntegral -> {
            return MemberIntegralEnhance.builder().memberIntegral(memberIntegral).build();
        }).collect(Collectors.toList());
    }

    public static MemberEnhance memberEnhance(Member member, MemberExtend memberExtend, MemberConfig memberConfig) {
        return MemberEnhance.builder()
                .member(member)
                .memberExtendEnhance(Converter.memberExtendEnhance(memberExtend))
                .memberConfigEnhance(Converter.memberConfigEnhance(memberConfig))
                .build();
    }

    public static MemberConfigEnhance memberConfigEnhance(MemberConfig memberConfig) {
        return MemberConfigEnhance.builder().memberConfig(memberConfig).build();
    }

    public static MemberExtendEnhance memberExtendEnhance(MemberExtend memberExtend) {
        return MemberExtendEnhance.builder().memberExtend(memberExtend).build();
    }

    public static ContentEnhance contentEnhance(Content content) {
        return ContentEnhance.builder()
                .content(content)
                .build();
    }

    public static ContentEnhance contentEnhance(Content content, ContentExtend contentExtend) {
        return ContentEnhance.builder()
                .content(content)
                .contentExtendEnhance(contentExtendEnhance(contentExtend))
                .build();
    }

    public static ContentExtendEnhance contentExtendEnhance(ContentExtend contentExtend) {
        return ContentExtendEnhance.builder().contentExtend(contentExtend).build();
    }

    public static List<ContentEnhance> contentEnhance(List<Content> contentList) {
        if (CollectionUtils.isEmpty(contentList)) {
            return new ArrayList<>();
        }
        return contentList.stream().map(content -> {
            return contentEnhance(content);
        }).collect(Collectors.toList());
    }


    public static List<ApplyEnhance> applyEnhanceList(List<Apply> applyList) {
        if (CollectionUtils.isEmpty(applyList)) {
            return new ArrayList<>();
        }

        return applyList.stream().map(a -> {
            return applyEnhance(a);
        }).collect(Collectors.toList());
    }

    public static ApplyEnhance applyEnhance(Apply apply) {
        return ApplyEnhance.builder().apply(apply).build();
    }

    public static List<RadioEnhance> radioEnhanceList(List<Radio> radioList) {

        if (CollectionUtils.isEmpty(radioList)) {
            return new ArrayList<>();
        }

        return radioList.stream().map(a -> {
            return radioEnhance(a);
        }).collect(Collectors.toList());
    }

    public static RadioEnhance radioEnhance(Radio radio) {
        return RadioEnhance.builder().radio(radio).build();
    }

    public static TodoEnhance todoEnhance(Todo todo, TodoDetail todoDetail) {
        return TodoEnhance.builder()
                .todo(todo)
                .todoDetail(todoDetail)
                .build();
    }

    public static TodoEnhance todoEnhance(Todo todo) {
        return TodoEnhance.builder()
                .todo(todo)
                .build();
    }


    public static List<ViewHistoryEnhance> viewHistoryEnhanceList(List<ViewHistory> viewHistoryList) {
        if (CollectionUtils.isEmpty(viewHistoryList)) {
            return new ArrayList<>();
        }

        return viewHistoryList.stream().map(a -> {
            return ViewHistoryEnhance.builder().viewHistory(a).build();
        }).collect(Collectors.toList());
    }


    public static List<TodoEnhance> todoEnhanceList(List<Todo> todoList) {
        if (CollectionUtils.isEmpty(todoList)) {
            return new ArrayList<>();
        }

        return todoList.stream().map(a -> {
            return TodoEnhance.builder().todo(a).build();
        }).collect(Collectors.toList());
    }

    public static List<TagEnhance> tagEnhanceList(List<Tag> tags) {

        if (CollectionUtils.isEmpty(tags)) {
            return new ArrayList<>();
        }

        return tags.stream().map(a -> {
            return TagEnhance.builder()
                    .tag(a)
                    .count(0)
                    .build();
        }).collect(Collectors.toList());
    }

    public static List<BusinessConfigEnhance> businessConfigEnhanceList(List<BusinessConfig> businessConfigList) {

        if (CollectionUtils.isEmpty(businessConfigList)) {
            return new ArrayList<>();
        }

        return businessConfigList.stream().map(a -> {
            return BusinessConfigEnhance.builder().businessConfig(a).build();
        }).collect(Collectors.toList());
    }

    public static List<BannerEnhance> bannerEnhanceList(List<Banner> bannerList) {

        if (CollectionUtils.isEmpty(bannerList)) {
            return new ArrayList<>();
        }

        return bannerList.stream().map(a -> {
            return BannerEnhance.builder().banner(a).build();
        }).collect(Collectors.toList());
    }


    public static List<MessageEnhance> messageEnhance(List<Message> messageList) {
        if (CollectionUtils.isEmpty(messageList)) {
            return new ArrayList<>();
        }

        return messageList.stream().map(a -> {
            return MessageEnhance.builder().message(a).build();
        }).collect(Collectors.toList());
    }

    public static ContentEditReasonEnhance enhanceEditReason(ContentEditReason editReason) {
        return ContentEditReasonEnhance.builder().contentEditReason(editReason).build();
    }

    public static ContentDataEnhance contentDataEnhance(ContentData contentData) {
        return ContentDataEnhance.builder().contentData(contentData).build();
    }

    public static ContentEditReasonEnhance contentEditReason(ContentEditReason contentEditReason) {
        return ContentEditReasonEnhance.builder().contentEditReason(contentEditReason).build();
    }

    public static List<ContentDataEnhance> contentDataEnhance(List<ContentData> contentDataList) {
        if (CollectionUtils.isEmpty(contentDataList)) {
            return new ArrayList<>();
        }

        return contentDataList.stream().map(a -> {
            return Converter.contentDataEnhance(a);
        }).collect(Collectors.toList());
    }

    public static StatementEnhance statementEnhance(Statement statement) {
        return StatementEnhance.builder().statement(statement).build();
    }

}
