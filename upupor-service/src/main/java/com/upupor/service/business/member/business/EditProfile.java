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

import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.service.business.member.abstracts.AbstractMember;
import com.upupor.service.business.member.common.MemberBusiness;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.dto.OperateMemberDto;
import com.upupor.service.outer.req.member.UpdateMemberReq;
import com.upupor.service.utils.ServletUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-09-11 11:53
 * @email: yangrunkang53@gmail.com
 */
@Component
public class EditProfile extends AbstractMember<UpdateMemberReq> {

    @Override
    public MemberBusiness memberBusiness() {
        return MemberBusiness.EDIT_MEMBER_INFO;
    }

    @Override
    public CcResponse handle() {
        CcResponse cc = new CcResponse();

        UpdateMemberReq updateMemberReq = transferReq();
        checkUserName(updateMemberReq.getUserName(), Boolean.TRUE);

        String userId = ServletUtils.getUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }

        updateMemberReq.setUserId(userId);
        Boolean editSuccess = memberService.editMember(updateMemberReq);
        if (!editSuccess) {
            throw new BusinessException(ErrorCode.EDIT_MEMBER_FAILED);
        }
        // 变更用户名
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_NAME, updateMemberReq.getUserName());

        Member reGet = memberService.memberInfo(userId);

        OperateMemberDto operateMemberDto = new OperateMemberDto();
        operateMemberDto.setMemberId(reGet.getUserId());
        operateMemberDto.setSuccess(Boolean.TRUE);
        operateMemberDto.setStatus(reGet.getStatus());
        cc.setData(operateMemberDto);

        return cc;
    }
}
