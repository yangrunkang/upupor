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

package com.upupor.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.upupor.data.dao.entity.*;
import com.upupor.data.dao.entity.converter.Converter;
import com.upupor.data.dao.entity.enhance.ContentDataEnhance;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dao.mapper.*;
import com.upupor.data.dto.OperateContentDto;
import com.upupor.data.dto.dao.CommentNumDto;
import com.upupor.data.dto.dao.LastAndNextContentDto;
import com.upupor.data.dto.dao.ListDraftDto;
import com.upupor.data.dto.page.common.CountTagDto;
import com.upupor.data.dto.page.common.ListContentDto;
import com.upupor.data.dto.page.common.TagDto;
import com.upupor.data.types.*;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.common.IntegralEnum;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.base.*;
import com.upupor.service.business.editor.AbstractEditor;
import com.upupor.service.outer.req.GetMemberIntegralReq;
import com.upupor.service.outer.req.ListContentReq;
import com.upupor.service.outer.req.content.CreateContentReq;
import com.upupor.service.outer.req.content.UpdateContentReq;
import com.upupor.service.outer.req.content.UpdateStatusReq;
import com.upupor.service.utils.Asserts;
import com.upupor.service.utils.SessionUtils;
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
    private final MemberIntegralService memberIntegralService;
    private final List<AbstractEditor> abstractEditorList;
    private final DraftService draftService;


    @Override
    public ContentEnhance getContentDetail(String contentId) {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>().eq(Content::getContentId, contentId).eq(Content::getStatus, ContentStatus.NORMAL);
        Content content = contentMapper.selectOne(query);
        Asserts.notNull(content, CONTENT_NOT_EXISTS);
        ContentEnhance contentEnhance = Converter.contentEnhance(content);
        bindContentExtend(contentEnhance);
        return contentEnhance;
    }


    @Override
    public ContentEnhance getManageContentDetail(String contentId) {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>().eq(Content::getContentId, contentId).notIn(Content::getStatus, ContentStatus.manageStatusList);
        Content content = contentMapper.selectOne(query);
        Asserts.notNull(content, CONTENT_NOT_EXISTS);
        ContentEnhance contentEnhance = Converter.contentEnhance(content);
        bindContentExtend(contentEnhance);
        return contentEnhance;
    }

    @Override
    public List<Content> list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Content> list = contentMapper.list();
        PageInfo<Content> pageInfo = new PageInfo<>(list);
        return pageInfo.getList();
    }


    @Override
    public ContentEnhance getNormalContent(String contentId) {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>().eq(Content::getContentId, contentId).eq(Content::getStatus, ContentStatus.NORMAL);
        Content content = contentMapper.selectOne(query);
        Asserts.notNull(content, CONTENT_NOT_EXISTS);

        ContentEnhance contentEnhance = Converter.contentEnhance(content);
        contentEnhance.setContentDataEnhance(getContentData(Lists.newArrayList(contentId)).get(0));
        return contentEnhance;
    }


    @Override
    public ContentEnhance getContentByContentIdNoStatus(String contentId) {
        if (StringUtils.isEmpty(contentId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>().eq(Content::getContentId, contentId);
        Content content = contentMapper.selectOne(query);
        Asserts.notNull(content, CONTENT_NOT_EXISTS);
        ContentEnhance contentEnhance = Converter.contentEnhance(content);
        return contentEnhance;
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
        // 处理文章置顶
        handlePinnedContent(listContentDto, listContentReq.getUserId());
        return listContentDto;
    }

    @Override
    public void handlePinnedContent(ListContentDto listContentDto, String userId) {
        if (Objects.isNull(listContentDto)) {
            return;
        }

        if (CollectionUtils.isEmpty(listContentDto.getContentEnhanceList())) {
            return;
        }

        if (StringUtils.isEmpty(userId)) {
            return;
        }

        List<ContentEnhance> pinnedContentList = getContentListByPinned(PinnedStatus.PINNED, userId);
        if (CollectionUtils.isEmpty(pinnedContentList)) {
            return;
        }
        this.bindContentData(pinnedContentList);
        this.bindContentMember(pinnedContentList);
        listContentDto.setPinnedContentEnhance(pinnedContentList.get(0));
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
     * 通用Query & 封装
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    private ListContentDto commonListContentDtoQuery(Integer pageNum, Integer pageSize, LambdaQueryWrapper<Content> query) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Content> contents = contentMapper.selectList(query);
        PageInfo<Content> pageInfo = new PageInfo<>(contents);

        List<ContentEnhance> contentEnhanceList = Converter.contentEnhance(contents);
        // 封装文章数据
        this.bindContentData(contentEnhanceList);
        this.bindContentMember(contentEnhanceList);
        this.bindContentTag(contentEnhanceList);
        // 不能添加广告,会引起首页文章列表布局紊乱问题,如果需要添加广告,请在各业务外面添加
//        AbstractAd.ad(pageInfo.getList());

        // 数据组装
        ListContentDto listContentDto = new ListContentDto(pageInfo);
        listContentDto.setContentEnhanceList(contentEnhanceList);
        return listContentDto;
    }

    @Override
    public ListContentDto typeContentList(SearchContentType searchType, Integer pageNum, Integer pageSize) {
        // 默认的全部文章: SearchContentType.ALL.equals(searchType)
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .eq(Content::getStatus, ContentStatus.NORMAL)
                .orderByDesc(Content::getLatestCommentTime);

        if (SearchContentType.RECENTLY_EDITED.equals(searchType)) {
            // 最近是否更新过
            // CcDateUtil.getCurrentTime() - editTime <= CONTENT_UPDATE_TIME;
            // ==> CcDateUtil.getCurrentTime() -  CONTENT_UPDATE_TIME<=  editTime;
            query.ge(Content::getEditTime, CcDateUtil.getCurrentTime() - CONTENT_UPDATE_TIME);
        }

        if (SearchContentType.NEW.equals(searchType)) {
            // 是否是最近的新文章
            // CcDateUtil.getCurrentTime() - createTime <= NEW_CONTENT_TIME;
            // ==> CcDateUtil.getCurrentTime() - NEW_CONTENT_TIME <= createTime
            query.ge(Content::getCreateTime, CcDateUtil.getCurrentTime() - NEW_CONTENT_TIME);
        }

        if (SearchContentType.REPRINT.equals(searchType)) {
            query.eq(Content::getOriginType, OriginType.NONE_ORIGIN);
        }

        return commonListContentDtoQuery(pageNum, pageSize, query);
    }

    /**
     * 绑定详细文章
     *
     * @param contentEnhance
     */
    @Override
    public void bindContentExtend(ContentEnhance contentEnhance) {
        Content content = contentEnhance.getContent();
        LambdaQueryWrapper<ContentExtend> query = new LambdaQueryWrapper<ContentExtend>().eq(ContentExtend::getContentId, content.getContentId());
        ContentExtend contentExtend = contentExtendMapper.selectOne(query);
        Asserts.notNull(contentExtend, DATA_MISSING);
        contentExtend.unZip();
        contentEnhance.setContentExtendEnhance(Converter.contentExtendEnhance(contentExtend));
    }

    @Override
    public Boolean insertContent(ContentEnhance contentEnhance) {
        Content content = contentEnhance.getContent();
        int count = contentMapper.insert(content);
        ContentExtend contentExtend = contentEnhance.getContentExtendEnhance().getContentExtend();
        contentExtend.zip();
        return contentExtendMapper.insert(contentExtend) + count > 1;
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
    public Boolean updateContent(ContentEnhance contentEnhance) {
        Content content = contentEnhance.getContent();
        Asserts.notNull(content, CONTENT_NOT_EXISTS);
        return contentMapper.updateById(content) > 0;
    }


    @Override
    public List<ContentEnhance> listByContentIdList(List<String> contentIdList) {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .in(Content::getContentId, contentIdList)
                .eq(Content::getStatus, ContentStatus.NORMAL)
                .orderByDesc(Content::getCreateTime);
        List<Content> contents = contentMapper.selectList(query);
        if (CollectionUtils.isEmpty(contents)) {
            return new ArrayList<>();
        }
        List<ContentEnhance> contentEnhanceList = Converter.contentEnhance(contents);
        this.bindContentData(contentEnhanceList);
        return contentEnhanceList;
    }

    @Override
    public Integer total() {
        return contentMapper.total();
    }

    @Override
    public void bindContentData(List<ContentEnhance> contentEnhanceList) {
        if (CollectionUtils.isEmpty(contentEnhanceList)) {
            return;
        }
        List<String> contentIdList = contentEnhanceList.stream()
                .map(ContentEnhance::getContent)
                .map(Content::getContentId)
                .distinct().collect(Collectors.toList());

        List<ContentDataEnhance> contentDataEnhanceList = getContentData(contentIdList);
        if (CollectionUtils.isEmpty(contentDataEnhanceList)) {
            return;
        }
        // 浏览数不用处理
        // 点赞数也不用处理
        // 需要处理评论数
        List<CommentNumDto> commentNumDtoList = commentMapper.selectByCommentIdList(contentIdList);
        if (!CollectionUtils.isEmpty(commentNumDtoList)) {
            contentDataEnhanceList.forEach(contentData -> commentNumDtoList.forEach(commentNumDto -> {
                if (contentData.getContentData().getContentId().equals(commentNumDto.getContentId())) {
                    contentData.getContentData().setCommentNum(commentNumDto.getTotal());
                }
            }));
        }

        // 绑定文章数据
        contentDataEnhanceList.forEach(contentDataEnhance -> {
            contentEnhanceList.forEach(contentEnhance -> {
                if (contentDataEnhance.getContentData().getContentId().equals(contentEnhance.getContent().getContentId())) {
                    contentEnhance.setContentDataEnhance(contentDataEnhance);
                }
            });
        });
    }

    @Override
    public List<ContentDataEnhance> getContentData(List<String> targetIdList) {
        LambdaQueryWrapper<ContentData> query = new LambdaQueryWrapper<ContentData>().in(ContentData::getContentId, targetIdList);
        List<ContentData> contentDataList = contentDataMapper.selectList(query);
        return Converter.contentDataEnhance(contentDataList);
    }


    @Override
    public List<ContentEnhance> listAllByUserId(List<String> userIdList) {
        if (StringUtils.isEmpty(userIdList)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .in(Content::getUserId, userIdList)
                .eq(Content::getStatus, ContentStatus.NORMAL)
                .orderByDesc(Content::getCreateTime);

        return Converter.contentEnhance(contentMapper.selectList(query));
    }

    @Override
    public void bindContentMember(ListContentDto listContentDto) {
        if (Objects.isNull(listContentDto)) {
            return;
        }

        if (CollectionUtils.isEmpty(listContentDto.getContentEnhanceList())) {
            return;
        }

        List<String> userIdList = listContentDto.getContentEnhanceList().stream()
                .map(ContentEnhance::getContent)
                .map(Content::getUserId).
                distinct().collect(Collectors.toList());

        List<String> latestCommentUserId = listContentDto.getContentEnhanceList().stream()
                .map(ContentEnhance::getContent)
                .map(Content::getLatestCommentUserId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());

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
        listContentDto.getContentEnhanceList().forEach(contentEnhance -> {
            memberList.forEach(member -> {
                Content content = contentEnhance.getContent();
                if (content.getUserId().equals(member.getUserId())) {
                    contentEnhance.setMemberEnhance(Converter.memberEnhance(member));
                }
                if (Objects.nonNull(content.getLatestCommentTime()) && Objects.nonNull(content.getLatestCommentUserId())) {
                    if (content.getLatestCommentUserId().equals(member.getUserId())) {
                        contentEnhance.setLatestCommentUserName(member.getUserName());
                    }
                }
            });
        });
    }

    @Override
    public void bindContentMember(ContentEnhance content) {
        if (Objects.isNull(content)) {
            return;
        }
        ListContentDto listContentDto = new ListContentDto();
        listContentDto.setContentEnhanceList(Collections.singletonList(content));
        this.bindContentMember(listContentDto);
    }

    @Override
    public void bindContentMember(List<ContentEnhance> contentList) {
        if (CollectionUtils.isEmpty(contentList)) {
            return;
        }
        ListContentDto listContentDto = new ListContentDto();
        listContentDto.setContentEnhanceList(contentList);
        this.bindContentMember(listContentDto);
    }

    @Override
    public Boolean currentUserIsAttentionAuthor(String contentUserId) {
        boolean currentUserIsAttention = false;
        try {
            Attention attention = attentionService.getAttention(contentUserId, SessionUtils.getUserId());
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
            List<ContentDataEnhance> contentDataEnhanceList = getContentData(Lists.newArrayList(targetId));
            if (CollectionUtils.isEmpty(contentDataEnhanceList)) {
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

            List<ContentDataEnhance> contentDataEnhanceList = getContentData(Lists.newArrayList(targetId));
            if (CollectionUtils.isEmpty(contentDataEnhanceList)) {
                initContendData(targetId);
                return;
            }

            ContentDataEnhance contentData = contentDataEnhanceList.get(0);
            contentData.getContentData().incrementViewNum();
            contentDataMapper.updateById(contentData.getContentData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<ContentEnhance> getContentListByPinned(PinnedStatus pinnedStatus, String userId) {

        if (Objects.isNull(pinnedStatus)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>().eq(Content::getUserId, userId).eq(Content::getPinnedStatus, pinnedStatus);
        return Converter.contentEnhance(contentMapper.selectList(query));
    }


    @Override
    public ContentEditReason latestEditReason(String contentId) {
        if (StringUtils.isEmpty(contentId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        return contentEditReasonMapper.latestEditReason(contentId);
    }

    @Override
    public void bindContentEditReason(ContentEnhance contentEnhance) {
        Content content = contentEnhance.getContent();
        ContentEditReason contentEditReason = latestEditReason(content.getContentId());
        if (Objects.nonNull(contentEditReason)) {
            contentEnhance.setContentEditReasonEnhance(Converter.contentEditReason(contentEditReason));
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
    public void bindContentStatement(ContentEnhance contentEnhance) {
        Content content = contentEnhance.getContent();
        if (Objects.isNull(content.getStatementId())) {
            return;
        }
        Statement statement = statementMapper.getByStatementId(content.getStatementId());
        if (Objects.nonNull(statement)) {
            contentEnhance.setStatementEnhance(Converter.statementEnhance(statement));
        }
    }


    @Override
    public void bindLikesMember(ContentEnhance contentEnhance) {
        Content content = contentEnhance.getContent();
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
        contentEnhance.setLikesMemberEnhanceList(Converter.memberEnhance(memberList));
    }

    @Override
    public void lastAndNextContent(ContentEnhance contentEnhance) {
        Content content = contentEnhance.getContent();
        if (Objects.isNull(content)) {
            return;
        }
        List<String> idList = contentMapper.lastAndNextContent(content.getContentId(), content.getTagIds(), content.getContentType().getType());
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }

        LastAndNextContentDto lastAndNextContentDto = new LastAndNextContentDto();
        for (String cId : idList) {
            ContentEnhance contentIdAndTitleEnhance = getNormalContent(cId);
            if (contentIdAndTitleEnhance.getContent().getLatestCommentTime() > content.getLatestCommentTime()) {
                lastAndNextContentDto.setNextContentEnhance(contentIdAndTitleEnhance);
            } else {
                lastAndNextContentDto.setLastContentEnhance(contentIdAndTitleEnhance);
            }
        }

        contentEnhance.setLastAndNextContentDto(lastAndNextContentDto);

    }

    @Override
    public List<ContentEnhance> randomContent(String notUserId) {
        List<String> contentIdList = contentMapper.randomContent(notUserId);
        if (CollectionUtils.isEmpty(contentIdList)) {
            return new ArrayList<>(1);
        }
        List<ContentEnhance> contentList = listByContentIdList(contentIdList);
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
    public List<ContentEnhance> latestContentList() {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .eq(Content::getStatus, ContentStatus.NORMAL)
                .ge(Content::getCreateTime, CcDateUtil.getCurrentTime() - NEW_CONTENT_TIME)
                .orderByDesc(Content::getCreateTime);

        PageHelper.startPage(CcConstant.Page.NUM, CcConstant.Page.SIZE_HALF);
        List<Content> contentList = contentMapper.selectList(query);
        List<ContentEnhance> contentEnhanceList = Converter.contentEnhance(contentList);
        this.bindContentMember(contentEnhanceList);
        return contentEnhanceList;
    }

    @Override
    public Boolean exists(String contentId) {
        ContentEnhance manageContentDetail = null;
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
        List<ContentEnhance> contentEnhanceList = listContentDto.getContentEnhanceList();
        if (CollectionUtils.isEmpty(contentEnhanceList)) {
            return;
        }

        List<String> contentIdList = contentEnhanceList.stream()
                .map(ContentEnhance::getContent)
                .map(Content::getContentId)
                .distinct().collect(Collectors.toList());
        List<Draft> drafts = draftService.listByDto(ListDraftDto.builder().draftIdList(contentIdList).build());
        if (CollectionUtils.isEmpty(drafts)) {
            return;
        }

        for (ContentEnhance contentEnhance : contentEnhanceList) {
            for (Draft draft : drafts) {
                if (contentEnhance.getContent().getContentId().equals(draft.getDraftId())) {
                    contentEnhance.setHasDraft(Boolean.TRUE);
                }
            }
        }
    }

    @Override
    public void bindContentTag(List<ContentEnhance> contentList) {
        if (CollectionUtils.isEmpty(contentList)) {
            return;
        }

        for (ContentEnhance contentEnhance : contentList) {
            List<TagDto> tagDtoList = tagService.listTagNameByTagId(contentEnhance.getContent().getTagIds());
            contentEnhance.setTagDtoList(tagDtoList);
        }

    }
}
