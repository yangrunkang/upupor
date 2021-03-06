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

package com.upupor.service.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.business.editor.AbstractEditor;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.data.dao.entity.*;
import com.upupor.service.data.dao.mapper.*;
import com.upupor.service.data.service.*;
import com.upupor.service.dto.OperateContentDto;
import com.upupor.service.dto.dao.CommentNumDto;
import com.upupor.service.dto.dao.LastAndNextContentDto;
import com.upupor.service.dto.dao.ListDraftDto;
import com.upupor.service.dto.page.common.CountTagDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.outer.req.GetMemberIntegralReq;
import com.upupor.service.outer.req.ListContentReq;
import com.upupor.service.outer.req.content.CreateContentReq;
import com.upupor.service.outer.req.content.UpdateContentReq;
import com.upupor.service.outer.req.content.UpdateStatusReq;
import com.upupor.service.types.*;
import com.upupor.service.utils.Asserts;
import com.upupor.service.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.upupor.framework.CcConstant.Time.CONTENT_UPDATE_TIME;
import static com.upupor.framework.CcConstant.Time.NEW_CONTENT_TIME;
import static com.upupor.framework.ErrorCode.*;

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
    private final DraftService draftService;


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
        bindContentExtend(content);
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
                .orderByDesc(Content::getLatestCommentTime);

        ListContentDto listContentDto = commonListContentDtoQuery(listContentReq.getPageNum(), listContentReq.getPageSize(), query);
        // ??????????????????
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
                .eq(Objects.nonNull(contentType), Content::getContentType, contentType)
                .eq(Objects.nonNull(tag), Content::getTagIds, tag)
                .orderByDesc(Content::getLatestCommentTime);
        return commonListContentDtoQuery(pageNum, pageSize, query);
    }

    /**
     * ??????Query & ??????
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    private ListContentDto commonListContentDtoQuery(Integer pageNum, Integer pageSize, LambdaQueryWrapper<Content> query) {
        // ????????????
        PageHelper.startPage(pageNum, pageSize);
        List<Content> contents = contentMapper.selectList(query);
        PageInfo<Content> pageInfo = new PageInfo<>(contents);

        // ??????????????????
        this.bindContentData(pageInfo.getList());
        this.bindContentMember(contents);
        // ??????????????????,?????????????????????????????????????????????,????????????????????????,???????????????????????????
//        AbstractAd.ad(pageInfo.getList());

        // ????????????
        ListContentDto listContentDto = new ListContentDto(pageInfo);
        listContentDto.setContentList(pageInfo.getList());
        return listContentDto;
    }

    @Override
    public ListContentDto typeContentList(SearchContentType searchType, Integer pageNum, Integer pageSize) {
        // ?????????????????????: SearchContentType.ALL.equals(searchType)
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .eq(Content::getStatus, ContentStatus.NORMAL)
                .orderByDesc(Content::getLatestCommentTime);

        if (SearchContentType.RECENTLY_EDITED.equals(searchType)) {
            // ?????????????????????
            // CcDateUtil.getCurrentTime() - editTime <= CONTENT_UPDATE_TIME;
            // ==> CcDateUtil.getCurrentTime() -  CONTENT_UPDATE_TIME<=  editTime;
            query.ge(Content::getEditTime, CcDateUtil.getCurrentTime() - CONTENT_UPDATE_TIME);
        }

        if (SearchContentType.NEW.equals(searchType)) {
            // ???????????????????????????
            // CcDateUtil.getCurrentTime() - createTime <= NEW_CONTENT_TIME;
            // ==> CcDateUtil.getCurrentTime() - NEW_CONTENT_TIME <= createTime
            query.ge(Content::getCreateTime, CcDateUtil.getCurrentTime() - NEW_CONTENT_TIME);
        }

        return commonListContentDtoQuery(pageNum, pageSize, query);
    }

    /**
     * ??????????????????
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
    public Boolean insertContent(Content content) {
        int count = contentMapper.insert(content);
        return contentExtendMapper.insert(content.getContentExtend()) + count > 1;
    }

    @Override
    public OperateContentDto addContent(CreateContentReq createContentReq) {
        return AbstractEditor.execute(abstractEditorList, AbstractEditor.EditorType.CREATE, createContentReq);
    }

    @Override
    public OperateContentDto updateContent(UpdateContentReq updateContentReq) {
        return AbstractEditor.execute(abstractEditorList, AbstractEditor.EditorType.EDIT, updateContentReq);
    }

    @Override
    public OperateContentDto updateContentStatus(UpdateStatusReq updateStatusReq) {
        return AbstractEditor.execute(abstractEditorList, AbstractEditor.EditorType.UPDATE_STATUS, updateStatusReq);
    }


    @Override
    public Boolean updateContent(Content content) {
        Asserts.notNull(content, CONTENT_NOT_EXISTS);
        Boolean updateContent, updateContentExtend = Boolean.FALSE;
        updateContent = contentMapper.updateById(content) > 0;
        ContentExtend contentExtend = content.getContentExtend();
        if (Objects.nonNull(contentExtend)) {
            updateContentExtend = contentExtendMapper.updateById(contentExtend) > 0;
        }

        if (updateContent || updateContentExtend) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
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
        // ?????????????????????
        // ????????????????????????
        // ?????????????????????
        List<CommentNumDto> commentNumDtoList = commentMapper.selectByCommentIdList(contentIdList);
        if (!CollectionUtils.isEmpty(commentNumDtoList)) {
            contentDataList.forEach(contentData -> commentNumDtoList.forEach(commentNumDto -> {
                if (contentData.getContentId().equals(commentNumDto.getContentId())) {
                    contentData.setCommentNum(commentNumDto.getTotal());
                }
            }));
        }

        // ??????????????????
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
        // ?????????????????????
        // ????????????????????????
        // ?????????????????????
        List<CommentNumDto> commentNumDtoList = commentMapper.selectByCommentIdList(radioIdList);
        if (!CollectionUtils.isEmpty(commentNumDtoList)) {
            radioDataList.forEach(radioData -> commentNumDtoList.forEach(commentNumDto -> {
                if (radioData.getContentId().equals(commentNumDto.getContentId())) {
                    radioData.setCommentNum(commentNumDto.getTotal());
                }
            }));
        }

        // ??????????????????
        radioDataList.forEach(radioData -> {
            radioList.forEach(radio -> {
                if (radioData.getContentId().equals(radio.getRadioId())) {
                    radio.setContentData(radioData);
                }
            });
        });

    }


    @Override
    public List<Content> listAllByUserId(List<String> userIdList) {
        if (StringUtils.isEmpty(userIdList)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }
        return contentMapper.listAllByUserId(userIdList);
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
                // ?????????
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
            contentData.incrementViewNum();
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
        List<CountTagDto> countTagDtos = tagService.listAll();
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
        return tagService.listCountByTagIds(tagIdList);
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
        // ????????????html
        memberList.forEach(memberService::renderCardHtml);
        content.setLikesMemberList(memberList);
    }

    @Override
    public void lastAndNextContent(Content content) {
        if (Objects.isNull(content)) {
            return;
        }
        List<String> idList = contentMapper.lastAndNextContent(content.getContentId(), content.getTagIds(), content.getContentType().getType());
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }

        LastAndNextContentDto lastAndNextContentDto = new LastAndNextContentDto();
        for (String cId : idList) {
            Content contentIdAndTitle = getNormalContent(cId);
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
                .ge(Content::getCreateTime, CcDateUtil.getCurrentTime() - NEW_CONTENT_TIME)
                .orderByDesc(Content::getCreateTime);

        PageHelper.startPage(CcConstant.Page.NUM, CcConstant.Page.SIZE_HALF);
        List<Content> contentList = contentMapper.selectList(query);

        this.bindContentMember(contentList);
        return contentList;
    }

    @Override
    public Boolean exists(String contentId) {
        Content manageContentDetail = null;
        try {
            manageContentDetail = this.getManageContentDetail(contentId);
        } catch (Exception e) {
            if (!(e instanceof BusinessException && (((BusinessException) e).getCode().equals(CONTENT_NOT_EXISTS.getCode())))) {
                throw new BusinessException(ErrorCode.UNKNOWN_EXCEPTION);
            }
        }
        return Objects.nonNull(manageContentDetail);
    }

    @Override
    public void handleExistsDraft(ListContentDto listContentDto) {
        List<Content> contentList = listContentDto.getContentList();
        if (CollectionUtils.isEmpty(contentList)) {
            return;
        }

        List<String> contentIdList = contentList.stream().map(Content::getContentId).distinct().collect(Collectors.toList());
        List<Draft> drafts = draftService.listByDto(ListDraftDto.builder().draftIdList(contentIdList).build());
        if (CollectionUtils.isEmpty(drafts)) {
            return;
        }

        for (Content content : contentList) {
            for (Draft draft : drafts) {
                if (content.getContentId().equals(draft.getDraftId())) {
                    content.setHasDraft(Boolean.TRUE);
                }
            }
        }


    }
}
