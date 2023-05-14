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

import com.upupor.data.dao.entity.MemberConfig;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.dao.mapper.MemberConfigMapper;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.service.utils.SessionUtils;
import com.upupor.service.business.member.abstracts.AbstractMember;
import com.upupor.service.business.member.common.MemberBusiness;
import com.upupor.service.outer.req.member.UpdateDefaultContentTypeReq;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-09-11 13:22
 * @email: yangrunkang53@gmail.com
 */
@Component
public class DefaultContentType extends AbstractMember<UpdateDefaultContentTypeReq> {

    @Resource
    private MemberConfigMapper memberConfigMapper;


    @Override
    public MemberBusiness memberBusiness() {
        return MemberBusiness.DEFAULT_CONTENT_TYPE;
    }

    @Override
    public CcResponse handle() {
        CcResponse cc = new CcResponse();
        String userId = SessionUtils.getUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR_USER_ID);
        }

        MemberEnhance memberEnhance = memberService.memberInfo(userId);
        MemberConfig memberConfig = memberEnhance.getMemberConfigEnhance().getMemberConfig();
        if (Objects.isNull(memberConfig)) {
            throw new BusinessException(ErrorCode.MEMBER_CONFIG_LESS);
        }
        UpdateDefaultContentTypeReq updateDefaultContentTypeReq = transferReq();
        memberConfig.setDefaultContentType(updateDefaultContentTypeReq.getSelectedContentType());
        int result = memberConfigMapper.updateById(memberConfig);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SETTING_DEFAULT_CONTENT_TYPE_FAILED);
        }
        cc.setData(true);
        return cc;
    }
}
