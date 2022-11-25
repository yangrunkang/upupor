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

package com.upupor.service.business.member.abstracts;

import com.upupor.framework.BusinessException;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.service.business.member.common.MemberBusiness;
import com.upupor.framework.common.UserCheckFieldType;
import com.upupor.data.dao.entity.BusinessConfig;
import com.upupor.data.dao.entity.Member;
import com.upupor.service.base.BusinessConfigService;
import com.upupor.service.base.MemberService;
import com.upupor.service.outer.req.member.BaseMemberReq;
import com.upupor.data.types.BusinessConfigType;
import com.upupor.framework.utils.ServletUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户行为抽象
 *
 * @author Yang Runkang (cruise)
 * @createTime 2022-09-11 11:44
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractMember<T extends BaseMemberReq> {
    protected static final String FORGET_PASSWORD = "forgetPassword";
    protected static final String REGISTER = "register";

    @Resource
    private BusinessConfigService businessConfigService;


    @Resource
    protected MemberService memberService;

    protected void checkUserName(String userName, Boolean logined) {
        if (StringUtils.isEmpty(userName)) {
            throw new BusinessException(ErrorCode.USER_NAME_CAN_NOT_EMPTY);
        }

        // 用户姓名,铭感词禁止注册
        List<BusinessConfig> businessConfigList = businessConfigService.listByBusinessConfigType(BusinessConfigType.ILLEGAL_USER_NAME);
        if (CollectionUtils.isEmpty(businessConfigList)) {
            return;
        }
        for (BusinessConfig businessConfig : businessConfigList) {
            if (userName.toLowerCase().contains(businessConfig.getValue())) {
                ErrorCode userNameError = ErrorCode.USER_NAME_ERROR;
                String replace = userNameError.getMessage().replace("{name}", businessConfig.getValue());
                throw new BusinessException(userNameError.getCode(), replace);
            }
        }

        if (logined) {
            String userId = ServletUtils.getUserId();
            Member member = memberService.memberInfo(userId);
            if (!member.getUserName().equals(userName)) {
                checkUserNameInDB(userName);
            }
        } else {
            checkUserNameInDB(userName);
        }
    }

    private void checkUserNameInDB(String userName) {
        // 检测用户名是否重复
        if (memberService.checkUserExists(userName, UserCheckFieldType.USER_NAME)) {
            throw new BusinessException(ErrorCode.USER_NAME_ALREADY_USED_BY_OTHERS);
        }
    }

    /**
     * 用户业务行为
     *
     * @return
     */
    public abstract MemberBusiness memberBusiness();

    /**
     * 转换
     *
     * @return
     */
    public T transferReq() {
        return (T) baseMemberReq;
    }

    private BaseMemberReq baseMemberReq;

    public void init(BaseMemberReq baseMemberReq) {
        this.baseMemberReq = baseMemberReq;
    }

    /**
     * 用户行为抽象
     *
     * @return
     */
    public abstract CcResponse handle();

}
