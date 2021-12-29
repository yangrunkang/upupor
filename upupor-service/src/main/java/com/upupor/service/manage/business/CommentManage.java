package com.upupor.service.manage.business;

import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Comment;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dto.page.common.ListCommentDto;
import com.upupor.service.manage.AbstractManageInfoGet;
import com.upupor.service.manage.ManageDto;
import com.upupor.service.manage.service.CommentManageService;
import com.upupor.service.service.aggregation.service.ContentService;
import com.upupor.service.service.aggregation.service.MemberService;
import com.upupor.spi.req.ListCommentReq;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class CommentManage extends AbstractManageInfoGet {

    @Resource
    private CommentManageService commentManageService;

    @Resource
    private MemberService memberService;

    @Resource
    private ContentService contentService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {
        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();
        String userId = manageDto.getUserId();


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

        getMemberIndexDto().setListCommentDto(listCommentDto);

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
}