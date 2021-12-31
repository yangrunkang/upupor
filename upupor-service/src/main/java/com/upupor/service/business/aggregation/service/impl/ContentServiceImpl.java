/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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
import com.upupor.service.business.aggregation.service.*;
import com.upupor.service.common.*;
import com.upupor.service.dao.entity.*;
import com.upupor.service.dao.mapper.*;
import com.upupor.service.dto.dao.CommentNumDto;
import com.upupor.service.dto.dao.ContentIdAndTitle;
import com.upupor.service.dto.dao.LastAndNextContentDto;
import com.upupor.service.dto.page.ContentIndexDto;
import com.upupor.service.dto.page.common.CountTagDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.dto.page.common.TagDto;
import com.upupor.service.listener.event.PublishContentEvent;
import com.upupor.service.utils.Asserts;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.RedisUtil;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.AddContentDetailReq;
import com.upupor.spi.req.GetMemberIntegralReq;
import com.upupor.spi.req.ListContentReq;
import com.upupor.spi.req.UpdateContentReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    private final ApplicationEventPublisher eventPublisher;
    private final MemberConfigMapper memberConfigMapper;
    private final MemberIntegralService memberIntegralService;


    @Override
    public Content getContentDetail(String contentId) {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .eq(Content::getContentId, contentId)
                .eq(Content::getStatus, CcEnum.ContentStatus.NORMAL.getStatus());
        Content content = contentMapper.selectOne(query);
        Asserts.notNull(content, CONTENT_NOT_EXISTS);
        bindContentExtend(content);
        return content;
    }


    @Override
    public Content getManageContentDetail(String contentId) {

        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .eq(Content::getContentId, contentId)
                .notIn(Content::getStatus, Content.manageStatusList);

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
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .eq(Content::getContentId, contentId)
                .eq(Content::getStatus, CcEnum.ContentStatus.NORMAL.getStatus());
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
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .eq(Content::getContentId, contentId);
        Content content = contentMapper.selectOne(query);
        Asserts.notNull(content, CONTENT_NOT_EXISTS);
        return content;
    }

    @Override
    public ListContentDto listContent(ListContentReq listContentReq) {
        // 如果有用户id 说明是管理后台或者个人主页过来的
        if (Strings.isEmpty(listContentReq.getUserId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }
        ListContentDto listContentDto = getListContentDto(listContentReq);

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

        List<Content> pinnedContentList = getContentListByPinned(CcEnum.PinnedStatus.PINNED.getStatus(), userId);
        if (CollectionUtils.isEmpty(pinnedContentList)) {
            return;
        }

        List<Content> contentList = listContentDto.getContentList();
        Iterator<Content> iterator = contentList.iterator();
        while (iterator.hasNext()) {
            Content content = iterator.next();
            pinnedContentList.forEach(c -> {
                if (c.getContentId().equals(content.getContentId())) {
                    iterator.remove();
                }
            });
        }

        listContentDto.setContentList(null);
        List<Content> newContentList = new ArrayList<>();
        newContentList.addAll(pinnedContentList);
        newContentList.addAll(contentList);
        listContentDto.setContentList(newContentList);
    }

    @Override
    public ListContentDto listContents(ListContentReq listContentReq) {
        ListContentDto listContentDto = getListContentDto(listContentReq);
        this.bindContentData(listContentDto.getContentList());
        return listContentDto;
    }

    private ListContentDto getListContentDto(ListContentReq listContentReq) {
        PageHelper.startPage(listContentReq.getPageNum(), listContentReq.getPageSize());
        List<Content> contents = contentMapper.listContent(listContentReq);
        PageInfo<Content> pageInfo = new PageInfo<>(contents);

        this.bindContentMember(contents);

        ListContentDto listContentDto = new ListContentDto(pageInfo);
        listContentDto.setContentList(pageInfo.getList());
        return listContentDto;
    }


    @Override
    public ListContentDto listContentByContentType(Integer contentType, Integer pageNum, Integer pageSize, String tag) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Content> contents = contentMapper.listContentByContentType(contentType, tag);
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
        LambdaQueryWrapper<ContentExtend> query = new LambdaQueryWrapper<ContentExtend>()
                .eq(ContentExtend::getContentId, content.getContentId());
        ContentExtend contentExtend = contentExtendMapper.selectOne(query);
        Asserts.notNull(contentExtend, DATA_MISSING);
        content.setContentExtend(contentExtend);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addContent(AddContentDetailReq addContentDetailReq) {
        Member member = memberService.memberInfo(ServletUtils.getUserId());
        String userId = member.getUserId();

        // 检查是否配置了发文间隔
        Integer existsCount = memberConfigMapper.countByUserId(userId);
        Long timeCreateContentInterval = null;
        String keyOfCreateContent = CcConstant.CvCache.CREATE_CONTENT_TIME_OUT + userId;
        if (existsCount >= 1) {
            MemberConfig memberConfig = memberService.memberInfoData(userId).getMemberConfig();
            Asserts.notNull(memberConfig, MEMBER_CONFIG_LESS);
            timeCreateContentInterval = memberConfig.getIntervalTimeCreateContent();
            if (Objects.nonNull(timeCreateContentInterval) && timeCreateContentInterval > 0) {
                if (Objects.nonNull(RedisUtil.get(keyOfCreateContent))) {
                    LocalDateTime canCreateContentTime = LocalDateTime.now().plus(timeCreateContentInterval, ChronoUnit.SECONDS);
                    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    throw new BusinessException(ErrorCode.NEXT_PUBLIC_TIME, dtf2.format(canCreateContentTime), false);
                }

            }
        }

        // 生成文章数据
        Content content = new Content();
        content.init();
        generateContentId(addContentDetailReq, content);
        content.setUserId(userId);
        content.setTitle(addContentDetailReq.getTitle());
        content.setContentType(addContentDetailReq.getContentType());
        content.setShortContent(addContentDetailReq.getShortContent());
        content.setTagIds(CcUtils.removeLastComma(addContentDetailReq.getTagIds()));
        content.setStatementId(member.getStatementId());
        // 初始化文章拓展表
        ContentExtend contentExtend = new ContentExtend();
        contentExtend.setContentId(content.getContentId());
        contentExtend.setDetailContent(addContentDetailReq.getContent());
        contentExtend.setSysUpdateTime(new Date());
        content.setContentExtend(contentExtend);
        // 初始化文章数据
        this.initContendData(content.getContentId());
        // 原创处理
        origialProcessing(addContentDetailReq, content);
        // 具体操作 发布 或者 草稿
        publishOperator(addContentDetailReq, content);


        int count = contentMapper.insert(content);
        int total = contentExtendMapper.insert(contentExtend) + count;
        boolean addSuccess = total > 1;

        if (addSuccess) {
            // 发送创建文章成功事件
            publishContentEvent(content);

            // 缓存创建文章的动作(标识),用于限制某一用户恶意刷文
            if (existsCount >= 1 && Objects.nonNull(timeCreateContentInterval)) {
                RedisUtil.set(keyOfCreateContent, content.getContentId(), timeCreateContentInterval);
            }
        }

        return addSuccess;
    }

    private void publishOperator(AddContentDetailReq addContentDetailReq, Content content) {
        if (!StringUtils.isEmpty(addContentDetailReq.getOperation())) {
            String draftTag = "temp";
            if (draftTag.equals(addContentDetailReq.getOperation())) {
                content.setStatus(CcEnum.ContentStatus.DRAFT.getStatus());
                content.setIsInitialStatus(CcEnum.ContentIsInitialStatus.NOT_FIRST_PUBLISH_EVER.getStatus());
            }
        }
    }

    private void origialProcessing(AddContentDetailReq addContentDetailReq, Content content) {
        if (Objects.nonNull(addContentDetailReq.getOriginType())) {
            content.setOriginType(addContentDetailReq.getOriginType());
            content.setNoneOriginLink(addContentDetailReq.getNoneOriginLink());
        }
    }

    private void generateContentId(AddContentDetailReq addContentDetailReq, Content content) {
        if (StringUtils.isEmpty(addContentDetailReq.getPreContentId())) {
            content.setContentId(CcUtils.getUuId());
        } else {
            // 检查preContentId是否已经使用
            checkPreContentId(addContentDetailReq);
            content.setContentId(addContentDetailReq.getPreContentId());
        }
    }

    private void checkPreContentId(AddContentDetailReq addContentDetailReq) {
        try {
            // 检查重复提交
            Content normalContent = getNormalContent(addContentDetailReq.getPreContentId());
            if (Objects.nonNull(normalContent)) {
                throw new BusinessException(ErrorCode.SUBMIT_REPEAT);
            }
        } catch (Exception e) {
            if (e instanceof BusinessException
                    && ((BusinessException) e).getCode().equals(CONTENT_NOT_EXISTS.getCode())) {
                // 忽略
            } else {
                throw new BusinessException(ErrorCode.UNKNOWN_EXCEPTION);
            }
        }
    }

    private void publishContentEvent(Content content) {
        PublishContentEvent createContentEvent = new PublishContentEvent();
        createContentEvent.setContentId(content.getContentId());
        createContentEvent.setUserId(content.getUserId());
        eventPublisher.publishEvent(createContentEvent);
    }

    @Override
    public Boolean updateContent(UpdateContentReq updateContentReq) {

        Content editContent = getManageContentDetail(updateContentReq.getContentId());

        // 校验内容所属的用户id是否是当前用户
        ServletUtils.checkOperatePermission(editContent.getUserId());
        // 检查置顶文章的状态
        pinnedContentCheck(updateContentReq, editContent);

        // 记录变更次数
        updateEditTimes(updateContentReq, editContent);

        // 编辑Content
        BeanUtils.copyProperties(updateContentReq, editContent);
        if (!StringUtils.isEmpty(updateContentReq.getTagIds())) {
            editContent.setTagIds(CcUtils.removeLastComma(updateContentReq.getTagIds()));
        }

        // 操作更改为公开、草稿
        boolean isSendCreateContentMessage = false;
        if (Objects.nonNull(updateContentReq.getIsDraftPublic()) && updateContentReq.getIsDraftPublic()) {
            editContent.setStatus(CcEnum.ContentStatus.NORMAL.getStatus());
            if (!CcEnum.ContentIsInitialStatus.FIRST_PUBLISHED.getStatus().equals(editContent.getIsInitialStatus())) {
                editContent.setCreateTime(CcDateUtil.getCurrentTime());
                editContent.setLatestCommentTime(CcDateUtil.getCurrentTime());
                editContent.setIsInitialStatus(CcEnum.ContentIsInitialStatus.FIRST_PUBLISHED.getStatus());
                // 第一次将文章正式发出,需要发送邮件
                isSendCreateContentMessage = true;
            }
        }
        editContent.setSysUpdateTime(new Date());
        int updateCount = contentMapper.updateById(editContent);

        // 内容不等时再变更
        if (!editContent.getContentExtend().getDetailContent().equals(updateContentReq.getDetailContent())) {
            editContent.getContentExtend().setDetailContent(updateContentReq.getDetailContent());
            editContent.getContentExtend().setSysUpdateTime(new Date());
            updateCount = updateCount + contentExtendMapper.updateById(editContent.getContentExtend());
        }

        if (updateCount > 0 && isSendCreateContentMessage) {
            publishContentEvent(editContent);
        }
        return Boolean.TRUE;
    }

    private void pinnedContentCheck(UpdateContentReq updateContentReq, Content editContent) {
        if (Objects.nonNull(editContent.getPinnedStatus()) && editContent.getPinnedStatus().equals(CcEnum.PinnedStatus.PINNED.getStatus())) {
            if (Objects.nonNull(updateContentReq.getStatus()) && !updateContentReq.getStatus().equals(CcEnum.ContentStatus.NORMAL.getStatus())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_SET_PINNED_CONTENT_STATUS_NOT_NORMAL);
            }
        }
    }

    @Override
    public Boolean updateContent(Content content) {
        Asserts.notNull(content, CONTENT_NOT_EXISTS);
        return contentMapper.updateById(content) > 0;
    }

    @Override
    public Boolean updateContentStatus(UpdateContentReq updateContentReq) {
        // check
        String contentId = updateContentReq.getContentId();
        Content editContent = getManageContentDetail(contentId);
        // 校验内容所属的用户id是否是当前用户
        ServletUtils.checkOperatePermission(editContent.getUserId());

        if(Objects.nonNull(editContent.getPinnedStatus())
        && CcEnum.PinnedStatus.PINNED.getStatus().equals(editContent.getPinnedStatus())
        && !CcEnum.ContentStatus.NORMAL.getStatus().equals(updateContentReq.getStatus())
        ){
            throw new BusinessException(FIRST_CANCEL_PINNED);
        }

        // 如果只是单纯的在控制中心更改状态
        boolean isSendCreateContentMessage = false;
        if (Objects.nonNull(updateContentReq.getStatus()) && updateContentReq.getStatus() == 0) {
            if (!CcEnum.ContentIsInitialStatus.FIRST_PUBLISHED.getStatus().equals(editContent.getIsInitialStatus())) {
                editContent.setCreateTime(CcDateUtil.getCurrentTime());
                editContent.setLatestCommentTime(CcDateUtil.getCurrentTime());
                editContent.setIsInitialStatus(CcEnum.ContentIsInitialStatus.FIRST_PUBLISHED.getStatus());
                // 第一次将文章正式发出,需要发送邮件
                isSendCreateContentMessage = true;
            }
        }

        editContent.setStatus(updateContentReq.getStatus());
        editContent.setSysUpdateTime(new Date());
        boolean result = contentMapper.updateById(editContent) > 0;

        if (result && isSendCreateContentMessage) {
            publishContentEvent(editContent);
        }

        return result;
    }

    @Override
    public Integer countDraft(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return 0;
        }
        return contentMapper.countDraft(userId);
    }

    private void updateEditTimes(UpdateContentReq updateContentReq, Content editContent) {
        String updateTitleContent = updateContentReq.getTitle() + updateContentReq.getDetailContent();
        String originTitleContent = editContent.getTitle() + editContent.getContentExtend().getDetailContent();
        if (!updateTitleContent.equals(originTitleContent)) {
            Integer editTimes = editContent.getEditTimes();
            if (Objects.isNull(editTimes)) {
                editTimes = 0;
            }
            editTimes = editTimes + 1;
            editContent.setEditTimes(editTimes);
            // 记录变更原因
            if (!StringUtils.isEmpty(updateContentReq.getEditReason())) {
                ContentEditReason contentEditReason = new ContentEditReason();
                contentEditReason.setContentId(editContent.getContentId());
                contentEditReason.setReason(updateContentReq.getEditReason());
                contentEditReason.setCreateTime(CcDateUtil.getCurrentTime());
                contentEditReason.setSysUpdateTime(new Date());
                contentEditReasonMapper.insert(contentEditReason);
            }
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
        LambdaQueryWrapper<ContentData> query = new LambdaQueryWrapper<ContentData>()
                .in(ContentData::getContentId, targetIdList);
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
        listContentReq.setStatus(CcEnum.ContentStatus.NORMAL.getStatus());
        return getListContentDto(listContentReq);
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

        List<String> userIdList = listContentDto.getContentList().stream()
                .map(Content::getUserId).distinct().collect(Collectors.toList());

        List<String> latestCommentUserId = listContentDto.getContentList().stream().map(Content::getLatestCommentUserId).filter(Objects::nonNull)
                .distinct().collect(Collectors.toList());

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
    public Boolean checkStatusIsOk(String contentId) {
        if (StringUtils.isEmpty(contentId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        return contentMapper.checkStatusIsOk(contentId) > 0;
    }


    @Override
    public Integer getTotalByContentType(Integer contentType) {
        return contentMapper.getTotalByContentType(contentType);
    }

    @Override
    public Boolean currentUserIsAttentionAuthor(String contentUserId) {
        boolean currentUserIsAttention = false;
        try {
            currentUserIsAttention = attentionService.checkExists(contentUserId, ServletUtils.getUserId());
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
    public List<Content> getContentListByPinned(Integer pinnedStatus, String userId) {

        if (Objects.isNull(pinnedStatus)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .eq(Content::getUserId, userId)
                .eq(Content::getPinnedStatus, pinnedStatus);
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
        getMemberIntegralReq.setStatus(CcEnum.IntegralStatus.NORMAL.getStatus());
        List<MemberIntegral> likesIntegralList = memberIntegralService.getByGetMemberIntegralReq(getMemberIntegralReq);
        if (CollectionUtils.isEmpty(likesIntegralList)) {
            return;
        }

        List<String> userIdList = likesIntegralList.stream()
                .map(MemberIntegral::getIntegralUserId)
                .distinct().collect(Collectors.toList());
        List<Member> memberList = memberMapper.listByUserIdList(userIdList);
        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }
        // 渲染喜欢html
        memberList.forEach(member -> {
            memberService.renderCardHtml(member);
        });
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
        List<Long> idList = contentMapper.lastAndNextContent(content.getContentId());
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
}
