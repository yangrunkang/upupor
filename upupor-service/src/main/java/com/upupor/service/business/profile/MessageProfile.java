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

package com.upupor.service.business.profile;

import com.upupor.service.business.ad.AbstractAd;
import com.upupor.service.data.aggregation.service.CommentService;
import com.upupor.service.business.profile.dto.Query;
import com.upupor.service.dto.page.common.ListCommentDto;
import com.upupor.service.outer.req.ListCommentReq;
import com.upupor.service.types.CommentStatus;
import com.upupor.service.types.ContentType;
import com.upupor.service.types.ViewTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.upupor.framework.CcConstant.ProfileView.PROFILE_MESSAGE;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月27日 21:08
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class MessageProfile extends AbstractProfile {
    private final CommentService commentService;

    @Override
    public ViewTargetType viewTargetType() {
        return ViewTargetType.PROFILE_MESSAGE;
    }

    @Override
    protected void setSpecifyData(Query query) {
        String userId = query.getUserId();
        Integer pageNum = query.getPageNum();
        Integer pageSize = query.getPageSize();

        ListCommentReq listCommentReq = new ListCommentReq();
        listCommentReq.setPageNum(pageNum);
        listCommentReq.setPageSize(pageSize);
        listCommentReq.setTargetId(userId);
        listCommentReq.setStatus(CommentStatus.NORMAL);
        listCommentReq.setCommentSource(ContentType.MESSAGE);

        ListCommentDto listCommentDto = commentService.listComment(listCommentReq);
        getMemberIndexDto().setListCommentDto(listCommentDto);
    }

    @Override
    protected void addAd() {
        ListCommentDto listCommentDto = getMemberIndexDto().getListCommentDto();
        AbstractAd.ad(listCommentDto.getCommentList());
    }

    @Override
    public String viewName() {
        return PROFILE_MESSAGE;
    }

}
