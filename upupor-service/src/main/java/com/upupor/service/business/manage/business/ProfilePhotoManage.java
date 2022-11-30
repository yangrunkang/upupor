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

package com.upupor.service.business.manage.business;

import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.framework.CcConstant;
import com.upupor.service.base.FileService;
import com.upupor.service.base.MemberService;
import com.upupor.service.business.manage.AbstractManage;
import com.upupor.service.business.manage.ManageDto;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class ProfilePhotoManage extends AbstractManage {

    @Resource
    private MemberService memberService;

    @Resource
    private FileService fileService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {
        String userId = manageDto.getUserId();

        MemberEnhance memberEnhance = memberService.memberInfoData(userId);
        // 获取用户历史头像
        List<String> userHistoryViaList = fileService.getUserHistoryViaList(memberEnhance.getMember().getUserId());
        if (!CollectionUtils.isEmpty(userHistoryViaList)) {
            memberEnhance.setHistoryViaList(userHistoryViaList);
        }
        getMemberIndexDto().setMemberEnhance(memberEnhance);

    }


    @Override
    public String viewName() {
        return CcConstant.UserManageView.USER_MANAGE_UPLOAD_PROFILE_PHOTO;
    }

    @Override
    public String viewDesc() {
        return "上传头像";
    }
}