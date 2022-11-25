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
import com.upupor.data.dao.entity.File;
import com.upupor.data.dao.entity.Member;
import com.upupor.service.base.FileService;
import com.upupor.service.outer.req.member.UpdateViaReq;
import com.upupor.framework.utils.ServletUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-09-11 13:22
 * @email: yangrunkang53@gmail.com
 */
@Component
public class Via extends AbstractMember<UpdateViaReq> {

    @Resource
    private FileService fileService;

    @Override
    public MemberBusiness memberBusiness() {
        return MemberBusiness.VIA;
    }

    @Override
    public CcResponse handle() {
        CcResponse ccResponse = new CcResponse();

        UpdateViaReq updateViaReq = transferReq();

        String via = updateViaReq.getVia();
        File fileByFileUrl = fileService.selectByFileUrl(via);
        if (Objects.isNull(fileByFileUrl)) {
            throw new BusinessException(ErrorCode.SELECTED_VIA_NOT_EXISTS);
        }
        String userId = ServletUtils.getUserId();
        Member member = memberService.memberInfo(userId);
        member.setVia(via);
        member.setSysUpdateTime(new Date());
        Boolean result = memberService.update(member);
        if (result) {
            ServletUtils.getSession().setAttribute(CcConstant.Session.USER_VIA, via);
        }

        ccResponse.setData(result);
        return ccResponse;
    }
}
