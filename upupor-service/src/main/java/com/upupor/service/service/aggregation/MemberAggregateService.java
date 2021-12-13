package com.upupor.service.service.aggregation;

import com.alibaba.fastjson.JSON;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.*;
import com.upupor.service.dto.page.MemberIndexDto;
import com.upupor.service.dto.page.apply.ApplyContentDto;
import com.upupor.service.dto.page.common.*;
import com.upupor.service.service.*;
import com.upupor.service.service.manage.ApplyManageService;
import com.upupor.service.service.manage.CommentManageService;
import com.upupor.service.service.manage.ContentManageService;
import com.upupor.service.utils.Asserts;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.AddTagReq;
import com.upupor.spi.req.ListCommentReq;
import com.upupor.spi.req.ListContentReq;
import com.upupor.spi.req.ListMessageReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.upupor.service.common.ErrorCode.CONTENT_NOT_EXISTS;
import static com.upupor.service.common.ErrorCode.NOT_EXISTS_APPLY;

/**
 * 用户聚合服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/19 23:54
 */
@Service
@RequiredArgsConstructor
public class MemberAggregateService {

    private final MemberService memberService;

    private final ContentService contentService;

    private final TagService tagService;

    private final CommentManageService commentManageService;

    private final ApplyManageService applyManageService;

    private final ContentManageService contentManageService;

    private final CollectService collectService;

    private final AttentionService attentionService;

    private final FanService fanService;

    private final MessageService messageService;

    private final MemberIntegralService memberIntegralService;

    private final CssPatternService cssPatternService;

    private final ApplyService applyService;

    private final FileService fileService;

    public MemberIndexDto getEditMemberInfo() {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        String userId = getUserId();
        Member member = memberService.memberInfoData(userId);
        member.setMemberConfig(member.getMemberConfig());
        memberIndexDto.setMember(member);
        memberIndexDto.setListCssPatternDto(cssPatternService.getAll(userId));
        return memberIndexDto;
    }

    public MemberIndexDto manageContent(Integer pageNum, Integer pageSize, String searchTitle, String select) {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        String userId = getUserId();

        ListContentReq listContentReq = new ListContentReq();
        listContentReq.setUserId(userId);
        listContentReq.setPageSize(pageSize);
        listContentReq.setPageNum(pageNum);
        listContentReq.setSearchTitle(searchTitle);
        // 筛选
        listContentReq.setSelect(select);

        ListContentDto listContentDto = null;
        try {
            listContentDto = contentManageService.listContent(listContentReq);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                BusinessException businessException = (BusinessException) e;
                if (businessException.getCode().equals(CONTENT_NOT_EXISTS.getCode())) {
                    listContentDto = new ListContentDto();
                }
            }
            e.printStackTrace();
        }

        // 处理标签
        assert listContentDto != null;
        handListContentDtoTagName(listContentDto);

        contentService.handlePinnedContent(listContentDto, userId);

        memberIndexDto.setMember(memberService.memberInfoData(userId));
        memberIndexDto.setListContentDto(listContentDto);
        return memberIndexDto;
    }

    public Object manageFeedback() {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        String userId = getUserId();

        memberIndexDto.setMember(memberService.memberInfoData(userId));
        return memberIndexDto;
    }

    public MemberIndexDto manageCollect(Integer pageNum, Integer pageSize) {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        String userId = getUserId();

        ListCollectDto listCollectDto = collectService.listByUserIdManage(userId, pageNum, pageSize);

        // 封装被收藏的对象
        handleCollectContent(listCollectDto);

        memberIndexDto.setListCollectDto(listCollectDto);
        memberIndexDto.setMember(memberService.memberInfoData(userId));
        return memberIndexDto;
    }

    public MemberIndexDto manageIntegral(Integer pageNum, Integer pageSize) {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        ListIntegralDto listIntegralDto = memberIntegralService.list(getUserId(), pageNum, pageSize);
        memberIndexDto.setListIntegralDto(listIntegralDto);
        memberIndexDto.setMember(memberService.memberInfoData(getUserId()));
        return memberIndexDto;
    }


    public ListIntegralDto integralRecord(Integer ruleId, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(ruleId)) {
            throw new BusinessException(ErrorCode.RULE_ID_NULL);
        }
        String userId = getUserId();
        ListIntegralDto listIntegralDto = memberIntegralService.list(userId, ruleId, pageNum, pageSize);
        if (Objects.isNull(listIntegralDto)) {
            listIntegralDto = new ListIntegralDto();
        }
        // 计算总积分
        listIntegralDto.setTotalIntegral(memberIntegralService.getUserIntegral(userId));
        return listIntegralDto;
    }


    public MemberIndexDto manageMessage(String status, Integer pageNum, Integer pageSize) {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        String userId = getUserId();

        if (StringUtils.isEmpty(status)) {
            throw new BusinessException(ErrorCode.PATH_ERROR);
        }

        String unread = "un-read";
        String all = "all";

        ListMessageReq listMessageReq = new ListMessageReq();
        if (status.equals(unread)) {
            listMessageReq.setStatus(CcEnum.MessageStatus.UN_READ.getStatus());
        } else if (status.equals(all)) {
            // 所有消息
            listMessageReq.setStatus(null);
        } else {
            throw new BusinessException(ErrorCode.PATH_ERROR);
        }
        listMessageReq.setUserId(ServletUtils.getUserId());
        listMessageReq.setPageNum(pageNum);
        listMessageReq.setPageSize(pageSize);

        ListMessageDto listMessageDto = messageService.listMessage(listMessageReq);
        memberIndexDto.setListMessageDto(listMessageDto);
        memberIndexDto.setMember(memberService.memberInfoData(userId));
        return memberIndexDto;
    }

    public MemberIndexDto manageProfilePhoto() {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        String userId = getUserId();

        Member member = memberService.memberInfoData(userId);
        // 获取用户历史头像
        List<String> userHistoryViaList = fileService.getUserHistoryViaList(member.getUserId());
        if (!CollectionUtils.isEmpty(userHistoryViaList)) {
            member.setHistoryViaList(userHistoryViaList);
        }
        memberIndexDto.setMember(member);
        return memberIndexDto;
    }

    private void handleCollectContent(ListCollectDto listCollectDto) {
        if (Objects.isNull(listCollectDto)) {
            return;
        }

        if (CollectionUtils.isEmpty(listCollectDto.getCollectList())) {
            return;
        }

        List<String> contentIdList = listCollectDto.getCollectList().stream()
                .filter(collect -> collect.getCollectType().equals(CcEnum.CollectType.CONTENT.getType()))
                .map(Collect::getCollectValue).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(contentIdList)) {
            return;
        }
        List<Content> contents = contentService.listByContentIdList(contentIdList);
        if (CollectionUtils.isEmpty(contents)) {
            return;
        }
        listCollectDto.getCollectList().forEach(collect -> {
            contents.forEach(content -> {
                if (collect.getCollectValue().equals(content.getContentId())) {
                    collect.setContent(content);
                }
            });
        });
    }


    private String getUserId() {
        String userId = ServletUtils.getUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }
        return userId;
    }

    private void handListContentDtoTagName(ListContentDto listContentDto) {
        if (CollectionUtils.isEmpty(listContentDto.getContentList())) {
            return;
        }

        listContentDto.getContentList().forEach(content -> {
            if (!StringUtils.isEmpty(content.getTagIds())) {
                content.setTagDtoList(tagService.listTagNameByTagId(content.getTagIds()));
            }
        });
    }

    public MemberIndexDto manageComment(Integer pageNum, Integer pageSize) {
        MemberIndexDto commentIndexDto = new MemberIndexDto();
        String userId = getUserId();

        ListCommentReq listCommentReq = new ListCommentReq();
        listCommentReq.setUserId(userId);
        listCommentReq.setPageSize(pageSize);
        listCommentReq.setPageNum(pageNum);
        // 获取全部状态评论
        listCommentReq.setStatus(null);
        ListCommentDto listCommentDto = commentManageService.listComment(listCommentReq);
        // 处理评论的用户文章
        getCommentContentTitle(listCommentDto);
        // 处理留言板的评论
        getCommentProfileMessage(listCommentDto);
        // 处理评论中的内容,如果有则不显示评论内容
        hiddenSomeComment(listCommentDto);

        commentIndexDto.setListCommentDto(listCommentDto);
        commentIndexDto.setMember(memberService.memberInfoData(userId));
        return commentIndexDto;
    }

    private void hiddenSomeComment(ListCommentDto listCommentDto) {
        List<Comment> commentList = listCommentDto.getCommentList();
        if (CollectionUtils.isEmpty(commentList)) {
            return;
        }
        commentList.forEach(comment -> {
            String commentContent = comment.getCommentContent();
            if (!StringUtils.isEmpty(commentContent)) {
                if (commentContent.contains("language-css")
                        ||
                        commentContent.contains("<code")
                        ||
                        commentContent.contains("<img")
                ) {
                    comment.setCommentContent("<p style='color:#6c757d;'>* 评论内容含有代码块或者图片,暂不予显示,请转至原文查看</p>");
                }
            }
        });
    }

    private void getCommentProfileMessage(ListCommentDto listCommentDto) {
        List<Comment> commentList = listCommentDto.getCommentList();
        if (CollectionUtils.isEmpty(commentList)) {
            return;
        }
        List<String> targetIdList = commentList.stream()
                .filter(c -> c.getCommentSource().equals(CcEnum.CommentSource.MESSAGE.getSource()))
                .map(Comment::getTargetId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(targetIdList)) {
            return;
        }
        List<Member> memberList = memberService.listByUserIdList(targetIdList);
        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }
        memberList.forEach(member -> {
            commentList.forEach(comment -> {
                if (comment.getTargetId().equals(member.getUserId())) {
                    comment.setMember(member);
                }
            });
        });
    }


    private void getCommentContentTitle(ListCommentDto listCommentDto) {
        List<Comment> commentList = listCommentDto.getCommentList();
        if (CollectionUtils.isEmpty(commentList)) {
            return;
        }

        List<String> targetIdList = commentList.stream().map(Comment::getTargetId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(targetIdList)) {
            return;
        }

        List<Content> contentList = contentService.listByContentIdList(targetIdList);
        if (CollectionUtils.isEmpty(contentList)) {
            return;
        }

        commentList.forEach(comment -> contentList.forEach(content -> {
            if (comment.getTargetId().equals(content.getContentId())) {
                comment.setContent(content);
            }
        }));

    }

    public MemberIndexDto manageApply(Integer pageNum, Integer pageSize) {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        String userId = getUserId();
        ListApplyDto listApplyDto = applyManageService.listApplyListByUserIdManage(userId, pageNum, pageSize);

        // 处理申请内容
        handleApplyContent(listApplyDto);

        memberIndexDto.setListApplyDto(listApplyDto);
        memberIndexDto.setMember(memberService.memberInfoData(userId));
        return memberIndexDto;
    }


    private void handleApplyContent(ListApplyDto listApplyDto) {
        if (Objects.isNull(listApplyDto)) {
            return;
        }
        if (CollectionUtils.isEmpty(listApplyDto.getApplyList())) {
            return;
        }

        listApplyDto.getApplyList().forEach(apply -> {
            if (apply.getApplySource().equals(CcEnum.ApplySource.AD.getSource())) {
                ApplyContentDto applyContentDto = JSON.parseObject(apply.getApplyContent(), ApplyContentDto.class);
                apply.setApplyContent(applyContentDto.getApplyIntro());
            }
            if (apply.getApplySource().equals(CcEnum.ApplySource.TAG.getSource())) {
                AddTagReq addTagReq = JSON.parseObject(apply.getApplyContent(), AddTagReq.class);
                StringBuilder str = new StringBuilder();
                str.append("<strong>页面</strong>: ").append(addTagReq.getPageName()).append(CcConstant.HTML_BREAK_LINE);
                if (!StringUtils.isEmpty(addTagReq.getTagName())) {
                    str.append("<strong>标签名</strong>: ").append(addTagReq.getTagName());
                }
                if (!StringUtils.isEmpty(addTagReq.getChildTagName())) {
                    str.append(CcConstant.HTML_BREAK_LINE).append("<strong>子标签名</strong>: ").append(addTagReq.getChildTagName());
                }
                apply.setApplyContent(str.toString());
            }
            if (apply.getApplySource().equals(CcEnum.ApplySource.CONSULTING_SERVICE.getSource())) {
                ApplyContentDto applyContentDto = JSON.parseObject(apply.getApplyContent(), ApplyContentDto.class);
                StringBuilder str = new StringBuilder();
                str.append("<strong>主题</strong>: ").append(applyContentDto.getApplyProject()).append(CcConstant.HTML_BREAK_LINE);
                if (!StringUtils.isEmpty(applyContentDto.getApplyIntro())) {
                    str.append("<strong>描述</strong>: ").append(applyContentDto.getApplyIntro());
                }
                apply.setApplyContent(str.toString());
            }
        });

    }


    public MemberIndexDto manageAttention(Integer pageNum, Integer pageSize) {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        String userId = getUserId();

        ListAttentionDto listAttentionDto = attentionService.getAttentions(userId, pageNum, pageSize);

        List<Attention> attentionList = listAttentionDto.getAttentionList();
        if (!CollectionUtils.isEmpty(attentionList)) {

            bindAttentionMemberInfo(attentionList);
        }


        memberIndexDto.setListAttentionDto(listAttentionDto);
        memberIndexDto.setMember(memberService.memberInfoData(userId));
        return memberIndexDto;
    }

    public MemberIndexDto manageFans(Integer pageNum, Integer pageSize) {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        String userId = getUserId();
        ListFansDto listFansDto = fanService.getFans(userId, pageNum, pageSize);
        List<Fans> fansList = listFansDto.getFansList();

        //
        if (!CollectionUtils.isEmpty(fansList)) {
            bindFansMemberInfo(fansList);
        }

        memberIndexDto.setListFansDto(listFansDto);
        memberIndexDto.setMember(memberService.memberInfoData(userId));
        return memberIndexDto;
    }

    /**
     * 封装关注者 粉丝信息
     *
     * @param attentionList
     */
    private void bindAttentionMemberInfo(List<Attention> attentionList) {
        List<String> attentionUserIdList = attentionList.stream().map(Attention::getAttentionUserId).distinct().collect(Collectors.toList());
        List<Member> memberList = memberService.listByUserIdList(attentionUserIdList);

        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }

        for (Attention attention : attentionList) {
            for (Member member : memberList) {
                if (attention.getAttentionUserId().equals(member.getUserId())) {
                    attention.setMember(member);
                }
            }
        }
    }

    private void bindFansMemberInfo(List<Fans> fansList) {
        List<String> fanUserIdList = fansList.stream().map(Fans::getFanUserId).distinct().collect(Collectors.toList());
        List<Member> memberList = memberService.listByUserIdList(fanUserIdList);
        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }

        for (Fans fans : fansList) {
            for (Member member : memberList) {
                if (fans.getFanUserId().equals(member.getUserId())) {
                    fans.setMember(member);
                }
            }
        }
    }


    public MemberIndexDto manageApplyCommit(String applyId) {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        if (StringUtils.isEmpty(applyId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        String userId = getUserId();
        memberIndexDto.setMember(memberService.memberInfoData(userId));

        Apply apply = applyService.getByApplyId(applyId);
        Asserts.notNull(apply, NOT_EXISTS_APPLY);

        ApplyDto applyDto = new ApplyDto();
        applyDto.setApply(apply);
        memberIndexDto.setApplyDto(applyDto);

        return memberIndexDto;
    }

    public MemberIndexDto manageDraft(Integer pageNum, Integer pageSize, String searchTitle) {
        MemberIndexDto memberIndexDto = new MemberIndexDto();
        String userId = getUserId();
        ListContentDto listContentDto = null;
        try {
            listContentDto = contentManageService.listContentDraft(pageNum, pageSize, userId, searchTitle);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                BusinessException businessException = (BusinessException) e;
                if (businessException.getCode().equals(CONTENT_NOT_EXISTS.getCode())) {
                    listContentDto = new ListContentDto();
                }
            }
            e.printStackTrace();
        }

        // 处理标签
        assert listContentDto != null;
        handListContentDtoTagName(listContentDto);

        memberIndexDto.setMember(memberService.memberInfoData(userId));
        memberIndexDto.setListContentDto(listContentDto);
        return memberIndexDto;

    }
}
