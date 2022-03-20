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

package com.upupor.service.business.aggregation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.business.aggregation.dao.entity.*;
import com.upupor.service.business.aggregation.dao.mapper.*;
import com.upupor.service.business.aggregation.service.*;
import com.upupor.service.business.editor.AbstractEditor;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.dto.dao.CommentNumDto;
import com.upupor.service.dto.dao.ContentIdAndTitle;
import com.upupor.service.dto.dao.LastAndNextContentDto;
import com.upupor.service.dto.page.common.CountTagDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.outer.req.AddContentDetailReq;
import com.upupor.service.outer.req.GetMemberIntegralReq;
import com.upupor.service.outer.req.ListContentReq;
import com.upupor.service.outer.req.UpdateContentReq;
import com.upupor.service.types.ContentStatus;
import com.upupor.service.types.ContentType;
import com.upupor.service.types.MemberIntegralStatus;
import com.upupor.service.types.PinnedStatus;
import com.upupor.service.utils.Asserts;
import com.upupor.service.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.upupor.service.common.ErrorCode.*;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 16:48
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentMapper contentMapper;
    private final ContentDataMapper contentDataMapper;
    private final ContentExtendMapper contentExtendMapper;
    private final ContentEditReasonMapper contentEditReasonMapper;
    private final CommentMapper commentMapper;
    private final MemberMapper memberMapper;
    private final AttentionService attentionService;
    private final TagService tagService;
    private final StatementMapper statementMapper;
    private final MemberService memberService;
    private final MemberIntegralService memberIntegralService;
    private final List<AbstractEditor> abstractEditorList;


    @Override
    public Content getContentDetail(String contentId) {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>().eq(Content::getContentId, contentId).eq(Content::getStatus, ContentStatus.NORMAL);
        Content content = contentMapper.selectOne(query);
        Asserts.notNull(content, CONTENT_NOT_EXISTS);
        bindContentExtend(content);
        return content;
    }


    @Override
    public Content getManageContentDetail(String contentId) {

        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>().eq(Content::getContentId, contentId).notIn(Content::getStatus, Content.manageStatusList);

        Content content = contentMapper.selectOne(query);
        Asserts.notNull(content, CONTENT_NOT_EXISTS);
        bindContentExtend(content);
        return content;
    }

    @Override
    public List<Content> list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Content> list = contentMapper.list();
        PageInfo<Content> pageInfo = new PageInfo<>(list);
        return pageInfo.getList();
    }


    @Override
    public Content getNormalContent(String contentId) {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>().eq(Content::getContentId, contentId).eq(Content::getStatus, ContentStatus.NORMAL);
        Content content = contentMapper.selectOne(query);
        Asserts.notNull(content, CONTENT_NOT_EXISTS);
        content.setContentData(getContentData(Lists.newArrayList(contentId)).get(0));
        return content;
    }


    @Override
    public Content getContentByContentIdNoStatus(String contentId) {
        if (StringUtils.isEmpty(contentId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>().eq(Content::getContentId, contentId);
        Content content = contentMapper.selectOne(query);
        Asserts.notNull(content, CONTENT_NOT_EXISTS);
        return content;
    }

    @Override
    public ListContentDto listContent(ListContentReq listContentReq) {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .eq(Objects.nonNull(listContentReq.getStatus()), Content::getStatus, listContentReq.getStatus())
                .eq(Objects.nonNull(listContentReq.getUserId()), Content::getUserId, listContentReq.getUserId())
                .eq(Objects.nonNull(listContentReq.getContentId()), Content::getContentId, listContentReq.getContentId())
                .like(Objects.nonNull(listContentReq.getSearchTitle()), Content::getTitle, listContentReq.getSearchTitle())
                .like(Objects.nonNull(listContentReq.getNavbarSearch()), Content::getTitle, listContentReq.getNavbarSearch())
                .in(!CollectionUtils.isEmpty(listContentReq.getTagIdList()), Content::getTagIds, listContentReq.getTagIdList())
                .orderByDesc(Content::getCreateTime);

        PageHelper.startPage(listContentReq.getPageNum(), listContentReq.getPageSize());
        List<Content> contents = contentMapper.selectList(query);
        PageInfo<Content> pageInfo = new PageInfo<>(contents);

        ListContentDto listContentDto = new ListContentDto(pageInfo);
        listContentDto.setContentList(pageInfo.getList());

        // 绑定文章数据
        this.bindContentData(listContentDto.getContentList());
        // 绑定用户信息
        this.bindContentMember(listContentDto.getContentList());
        // 处理文章置顶
        handlePinnedContent(listContentDto, listContentReq.getUserId());

        return listContentDto;
    }

    @Override
    public void handlePinnedContent(ListContentDto listContentDto, String userId) {
        if (Objects.isNull(listContentDto)) {
            return;
        }

        if (CollectionUtils.isEmpty(listContentDto.getContentList())) {
            return;
        }

        if (StringUtils.isEmpty(userId)) {
            return;
        }

        List<Content> pinnedContentList = getContentListByPinned(PinnedStatus.PINNED, userId);
        if (CollectionUtils.isEmpty(pinnedContentList)) {
            return;
        }
        this.bindContentData(pinnedContentList);
        this.bindContentMember(pinnedContentList);
        listContentDto.setPinnedContent(pinnedContentList.get(0));
    }



    @Override
    public ListContentDto listContentByContentType(ContentType contentType, Integer pageNum, Integer pageSize, String tag) {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .eq(Content::getStatus, ContentStatus.NORMAL)
                .eq(Objects.nonNull(contentType),Content::getContentType, contentType)
                .eq(Objects.nonNull(tag),Content::getTagIds, tag)
                .orderByDesc(Content::getLatestCommentTime);

        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Content> contents = contentMapper.selectList(query);
        PageInfo<Content> pageInfo = new PageInfo<>(contents);

        // 封装文章数据
        this.bindContentData(pageInfo.getList());
        this.bindContentMember(contents);
        // 数据组装
        ListContentDto listContentDto = new ListContentDto(pageInfo);
        listContentDto.setContentList(pageInfo.getList());

        return listContentDto;
    }

    /**
     * 绑定详细文章
     *
     * @param content
     */
    @Override
    public void bindContentExtend(Content content) {
        LambdaQueryWrapper<ContentExtend> query = new LambdaQueryWrapper<ContentExtend>().eq(ContentExtend::getContentId, content.getContentId());
        ContentExtend contentExtend = contentExtendMapper.selectOne(query);
        Asserts.notNull(contentExtend, DATA_MISSING);
        content.setContentExtend(contentExtend);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addContent(AddContentDetailReq addContentDetailReq) {
        return AbstractEditor.execute(abstractEditorList, AbstractEditor.EditorType.CREATE, addContentDetailReq);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateContent(UpdateContentReq updateContentReq) {
        return AbstractEditor.execute(abstractEditorList, AbstractEditor.EditorType.EDIT, updateContentReq);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateContentStatus(UpdateContentReq updateContentReq) {
        return AbstractEditor.execute(abstractEditorList, AbstractEditor.EditorType.UPDATE_STATUS, updateContentReq);
    }


    @Override
    public Boolean updateContent(Content content) {
        Asserts.notNull(content, CONTENT_NOT_EXISTS);
        return contentMapper.updateById(content) > 0;
    }


    @Override
    public Integer countDraft(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return 0;
        }
        return contentMapper.countDraft(userId);
    }

    @Override
    public List<Content> listByContentIdList(List<String> contentIdList) {
        List<Content> contents = contentMapper.listByContentIdList(contentIdList);
        if (CollectionUtils.isEmpty(contents)) {
            return new ArrayList<>();
        }
        this.bindContentData(contents);
        return contents;
    }

    @Override
    public Integer total() {
        return contentMapper.total();
    }

    @Override
    public void bindContentData(List<Content> contentList) {
        if (CollectionUtils.isEmpty(contentList)) {
            return;
        }
        List<String> contentIdList = contentList.stream().map(Content::getContentId).distinct().collect(Collectors.toList());

        List<ContentData> contentDataList = getContentData(contentIdList);
        if (CollectionUtils.isEmpty(contentDataList)) {
            return;
        }
        // 浏览数不用处理
        // 点赞数也不用处理
        // 需要处理评论数
        List<CommentNumDto> commentNumDtoList = commentMapper.selectByCommentIdList(contentIdList);
        if (!CollectionUtils.isEmpty(commentNumDtoList)) {
            contentDataList.forEach(contentData -> commentNumDtoList.forEach(commentNumDto -> {
                if (contentData.getContentId().equals(commentNumDto.getContentId())) {
                    contentData.setCommentNum(commentNumDto.getTotal());
                }
            }));
        }

        // 绑定文章数据
        contentDataList.forEach(contentData -> {
            contentList.forEach(content -> {
                if (contentData.getContentId().equals(content.getContentId())) {
                    content.setContentData(contentData);
                }
            });
        });
    }

    private List<ContentData> getContentData(List<String> targetIdList) {
        LambdaQueryWrapper<ContentData> query = new LambdaQueryWrapper<ContentData>().in(ContentData::getContentId, targetIdList);
        return contentDataMapper.selectList(query);
    }

    @Override
    public void bindRadioContentData(List<Radio> radioList) {

        if (CollectionUtils.isEmpty(radioList)) {
            return;
        }

        List<String> radioIdList = radioList.stream().map(Radio::getRadioId).distinct().collect(Collectors.toList());
        List<ContentData> radioDataList = getContentData(radioIdList);
        if (CollectionUtils.isEmpty(radioDataList)) {
            return;
        }
        // 浏览数不用处理
        // 点赞数也不用处理
        // 需要处理评论数
        List<CommentNumDto> commentNumDtoList = commentMapper.selectByCommentIdList(radioIdList);
        if (!CollectionUtils.isEmpty(commentNumDtoList)) {
            radioDataList.forEach(radioData -> commentNumDtoList.forEach(commentNumDto -> {
                if (radioData.getContentId().equals(commentNumDto.getContentId())) {
                    radioData.setCommentNum(commentNumDto.getTotal());
                }
            }));
        }

        // 绑定文章数据
        radioDataList.forEach(radioData -> {
            radioList.forEach(radio -> {
                if (radioData.getContentId().equals(radio.getRadioId())) {
                    radio.setContentData(radioData);
                }
            });
        });

    }

    @Override
    public void bindContentData(ListContentDto listContentDto) {
        if (Objects.isNull(listContentDto)) {
            return;
        }
        this.bindContentData(listContentDto.getContentList());
        this.bindContentMember(listContentDto);
    }

    @Override
    public ListContentDto listContentByTitleAndShortContent(ListContentReq listContentReq) {
        listContentReq.setStatus(ContentStatus.NORMAL);
        return listContent(listContentReq);
    }

    @Override
    public List<Content> listAllByUserId(String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }
        return contentMapper.listAllByUserId(userId);
    }

    @Override
    public void bindContentMember(ListContentDto listContentDto) {
        if (Objects.isNull(listContentDto)) {
            return;
        }

        if (CollectionUtils.isEmpty(listContentDto.getContentList())) {
            return;
        }

        List<String> userIdList = listContentDto.getContentList().stream().map(Content::getUserId).distinct().collect(Collectors.toList());

        List<String> latestCommentUserId = listContentDto.getContentList().stream().map(Content::getLatestCommentUserId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(latestCommentUserId)) {
            userIdList.addAll(latestCommentUserId);
        }
        List<String> userIdDistinct = userIdList.stream().distinct().collect(Collectors.toList());

        if (CollectionUtils.isEmpty(userIdDistinct)) {
            return;
        }

        List<Member> memberList = memberMapper.listByUserIdList(userIdDistinct);
        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }
        listContentDto.getContentList().forEach(content -> {
            memberList.forEach(member -> {
                if (content.getUserId().equals(member.getUserId())) {
                    content.setMember(member);
                }
                if (Objects.nonNull(content.getLatestCommentTime()) && Objects.nonNull(content.getLatestCommentUserId())) {
                    if (content.getLatestCommentUserId().equals(member.getUserId())) {
                        content.setLatestCommentUserName(member.getUserName());
                    }
                }
            });
        });
    }

    @Override
    public void bindContentMember(Content content) {
        if (Objects.isNull(content)) {
            return;
        }
        ListContentDto listContentDto = new ListContentDto();
        listContentDto.setContentList(Collections.singletonList(content));
        this.bindContentMember(listContentDto);
    }

    @Override
    public void bindContentMember(List<Content> contentList) {
        if (CollectionUtils.isEmpty(contentList)) {
            return;
        }
        ListContentDto listContentDto = new ListContentDto();
        listContentDto.setContentList(contentList);
        this.bindContentMember(listContentDto);
    }

    @Override
    public Boolean currentUserIsAttentionAuthor(String contentUserId) {
        boolean currentUserIsAttention = false;
        try {
            Attention attention = attentionService.getAttention(contentUserId, ServletUtils.getUserId());
            currentUserIsAttention = Objects.nonNull(attention);
        } catch (Exception e) {
        }
        return currentUserIsAttention;
    }

    @Override
    public void initContendData(String targetId) {
        if (StringUtils.isEmpty(targetId)) {
            return;
        }

        try {
            List<ContentData> contentDataList = getContentData(Lists.newArrayList(targetId));
            if (CollectionUtils.isEmpty(contentDataList)) {
                // 初始化
                ContentData contentData = new ContentData();
                contentData.setContentId(targetId);
                contentData.setLikeNum(BigDecimal.ZERO.intValue());
                contentData.setCommentNum(BigDecimal.ZERO.intValue());
                contentData.setViewNum(BigDecimal.ONE.intValue());
                contentDataMapper.insert(contentData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("targetId:{},error message:{}", targetId, INIT_DATA_ERROR.getMessage());
        }
    }

    @Override
    public void viewNumPlusOne(String targetId) {
        try {

            List<ContentData> contentDataList = getContentData(Lists.newArrayList(targetId));
            if (CollectionUtils.isEmpty(contentDataList)) {
                initContendData(targetId);
                return;
            }

            ContentData contentData = contentDataList.get(0);
            contentData.setViewNum(contentData.getViewNum() + 1);
            contentDataMapper.updateById(contentData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Content> getContentListByPinned(PinnedStatus pinnedStatus, String userId) {

        if (Objects.isNull(pinnedStatus)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>().eq(Content::getUserId, userId).eq(Content::getPinnedStatus, pinnedStatus);
        return contentMapper.selectList(query);
    }


    @Override
    public ContentEditReason latestEditReason(String contentId) {
        if (StringUtils.isEmpty(contentId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        return contentEditReasonMapper.latestEditReason(contentId);
    }

    @Override
    public void bindContentEditReason(Content content) {
        ContentEditReason contentEditReason = latestEditReason(content.getContentId());
        if (Objects.nonNull(contentEditReason)) {
            content.setContentEditReason(contentEditReason);
        }
    }

    @Override
    public List<CountTagDto> listAllTag() {
        List<CountTagDto> countTagDtos = contentMapper.listAll();
        if (CollectionUtils.isEmpty(countTagDtos)) {
            return new ArrayList<>();
        }
        Iterator<CountTagDto> iterator = countTagDtos.iterator();
        while (iterator.hasNext()) {
            CountTagDto next = iterator.next();
            String tagName = tagService.getNameById(next.getTagId());
            if (StringUtils.isEmpty(tagName)) {
                iterator.remove();
            } else {
                next.setTagName(tagName);
            }
        }
        return countTagDtos;
    }

    @Override
    public List<CountTagDto> listCountByTagIds(List<String> tagIdList) {
        if (CollectionUtils.isEmpty(tagIdList)) {
            return new ArrayList<>();
        }
        return contentMapper.listCountByTagIds(tagIdList);
    }

    @Override
    public void bindContentStatement(Content content) {
        if (Objects.isNull(content.getStatementId())) {
            return;
        }
        Statement statement = statementMapper.getByStatementId(content.getStatementId());
        if (Objects.nonNull(statement)) {
            content.setStatement(statement);
        }
    }

    @Override
    public void bindLikesMember(Content content) {
        if (Objects.isNull(content)) {
            return;
        }

        GetMemberIntegralReq getMemberIntegralReq = new GetMemberIntegralReq();
        getMemberIntegralReq.setTargetId(content.getContentId());
        getMemberIntegralReq.setRuleId(IntegralEnum.CLICK_LIKE.getRuleId());
        getMemberIntegralReq.setStatus(MemberIntegralStatus.NORMAL);
        List<MemberIntegral> likesIntegralList = memberIntegralService.getByGetMemberIntegralReq(getMemberIntegralReq);
        if (CollectionUtils.isEmpty(likesIntegralList)) {
            return;
        }

        List<String> userIdList = likesIntegralList.stream().map(MemberIntegral::getIntegralUserId).distinct().collect(Collectors.toList());
        List<Member> memberList = memberMapper.listByUserIdList(userIdList);
        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }
        // 渲染喜欢html
        memberList.forEach(memberService::renderCardHtml);
        content.setLikesMemberList(memberList);
    }

    @Override
    public Content latestContent(String userId) {
        return contentMapper.latestContent(userId);
    }

    @Override
    public void lastAndNextContent(Content content) {
        if (Objects.isNull(content)) {
            return;
        }
        List<Long> idList = contentMapper.lastAndNextContent(content.getContentId(), content.getTagIds(), content.getContentType().getType());
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }

        LastAndNextContentDto lastAndNextContentDto = new LastAndNextContentDto();
        for (Long cId : idList) {
            ContentIdAndTitle contentIdAndTitle = contentMapper.selectById(cId);
            if (contentIdAndTitle.getLatestCommentTime() > content.getLatestCommentTime()) {
                lastAndNextContentDto.setNextContent(contentIdAndTitle);
            } else {
                lastAndNextContentDto.setLastContent(contentIdAndTitle);
            }
        }

        content.setLastAndNextContentDto(lastAndNextContentDto);

    }

    @Override
    public List<Content> randomContent(String notUserId) {
        List<String> contentIdList = contentMapper.randomContent(notUserId);
        if (CollectionUtils.isEmpty(contentIdList)) {
            return new ArrayList<>(1);
        }
        List<Content> contentList = listByContentIdList(contentIdList);
        contentList.forEach(this::bindContentMember);
        return contentList;
    }

    @Override
    public Integer getUserTotalContentNum(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return BigDecimal.ZERO.intValue();
        }
        return contentMapper.totalByUserId(userId);
    }

    @Override
    public void updateContentData(ContentData contentData) {
        contentDataMapper.updateById(contentData);
    }

    @Override
    public List<Content> latestContentList() {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .eq(Content::getStatus, ContentStatus.NORMAL)
                .ge(Content::getCreateTime, CcDateUtil.getCurrentTime() - 7 * 24 * 60 * 60)
                .orderByDesc(Content::getCreateTime);

        List<Content> contentList = contentMapper.selectList(query);
        this.bindContentMember(contentList);
        return contentList;
    }
}
