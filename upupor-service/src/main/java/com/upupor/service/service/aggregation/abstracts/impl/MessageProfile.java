package com.upupor.service.service.aggregation.abstracts.impl;

import com.upupor.service.business.AdService;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dto.page.common.ListCommentDto;
import com.upupor.service.service.CommentService;
import com.upupor.service.service.aggregation.abstracts.ProfileAbstract;
import com.upupor.spi.req.ListCommentReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月27日 21:08
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class MessageProfile extends ProfileAbstract {
    private final CommentService commentService;

    @Override
    public CcEnum.ViewTargetType viewTargetType() {
        return CcEnum.ViewTargetType.PROFILE_MESSAGE;
    }

    @Override
    protected void setSpecifyData(String userId, Integer pageNum, Integer pageSize) {
        ListCommentReq listCommentReq = new ListCommentReq();
        listCommentReq.setPageNum(pageNum);
        listCommentReq.setPageSize(pageSize);
        listCommentReq.setTargetId(userId);
        listCommentReq.setStatus(CcEnum.CommentStatus.NORMAL.getStatus());
        listCommentReq.setCommentSource(CcEnum.CommentSource.MESSAGE.getSource());

        ListCommentDto listCommentDto = commentService.listComment(listCommentReq);
        if (Objects.isNull(listCommentDto)) {
            listCommentDto = new ListCommentDto();
        }

        // 绑定评论用户
        if (!CollectionUtils.isEmpty(listCommentDto.getCommentList())) {
            commentService.bindCommentUserName(listCommentDto.getCommentList());
        }
        getMemberIndexDto().setListCommentDto(listCommentDto);
    }

    @Override
    protected void addAd() {
        ListCommentDto listCommentDto = getMemberIndexDto().getListCommentDto();
        AdService.commentListAd(listCommentDto.getCommentList());
    }

}
