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

package com.upupor.service.business.member.business;

import com.upupor.framework.CcRedis;
import com.upupor.framework.CcResponse;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.service.business.member.abstracts.AbstractMember;
import com.upupor.service.business.member.common.MemberBusiness;
import com.upupor.service.outer.req.member.BaseMemberReq;
import com.upupor.service.utils.SessionUtils;
import org.springframework.stereotype.Component;


/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-09-11 13:32
 * @email: yangrunkang53@gmail.com
 */
@Component
public class Logout extends AbstractMember<BaseMemberReq> {
    @Override
    public MemberBusiness memberBusiness() {
        return MemberBusiness.LOGIN_OUT;
    }

    @Override
    public CcResponse handle() {
        // 过期Session
        String userId = SessionUtils.getUserId();
        String loginExpiredTimeKey = CcRedis.Key.memberLoginExpiredTimeKey(userId);
        RedisUtil.remove(loginExpiredTimeKey);

        // 清除session
        SessionUtils.getPageSession().invalidate();
        return new CcResponse();
    }
}
