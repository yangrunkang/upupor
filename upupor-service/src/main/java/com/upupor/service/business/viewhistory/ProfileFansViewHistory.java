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

package com.upupor.service.business.viewhistory;

import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.ViewHistory;
import org.springframework.stereotype.Component;

/**
 * @author cruise
 * @createTime 2021-12-28 18:50
 */
@Component
public class ProfileFansViewHistory extends ProfileAttentionViewHistory {
    @Override
    public CcEnum.ViewTargetType viewTargetType() {
        return CcEnum.ViewTargetType.PROFILE_FANS;
    }
    @Override
    public void setViewHistoryTitleAndUrl() {
        for (ViewHistory viewHistory : getSpecifyViewHistory()) {
            for (Member member : getTargetList()) {
                if (member.getUserId().equals(viewHistory.getTargetId())) {
                    viewHistory.setTitle(member.getUserName()+"的粉丝");
                    viewHistory.setUrl("/profile/" + member.getUserId() + "/fans");
                    viewHistory.setSource(viewTargetType().getName());
                }
            }
        }
    }
}
