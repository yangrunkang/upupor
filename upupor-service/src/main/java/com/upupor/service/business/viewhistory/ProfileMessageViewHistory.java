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

package com.upupor.service.business.viewhistory;


import com.upupor.service.business.aggregation.dao.entity.Member;
import com.upupor.service.business.aggregation.dao.entity.ViewHistory;
import com.upupor.service.business.aggregation.dao.mapper.CommentMapper;
import com.upupor.service.types.ViewTargetType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author cruise
 * @createTime 2021-12-28 18:49
 */
@Component
public class ProfileMessageViewHistory extends ProfileAttentionViewHistory{
    @Resource
    private CommentMapper commentMapper;

    @Override
    public ViewTargetType viewTargetType() {
        return ViewTargetType.PROFILE_MESSAGE;
    }
    @Override
    public void setViewHistoryTitleAndUrl() {
        for (ViewHistory viewHistory : getSpecifyViewHistory()) {
            for (Member member : getTargetList()) {
                if (member.getUserId().equals(viewHistory.getTargetId())) {
                    viewHistory.setTitle(member.getUserName()+"的留言板");
                    viewHistory.setUrl("/profile-message/" + member.getUserId());
                    viewHistory.setSource(viewTargetType().getName());
                }
            }
        }
    }


}
