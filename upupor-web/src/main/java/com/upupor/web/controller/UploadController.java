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

package com.upupor.web.controller;

import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.ServletUtils;
import com.upupor.security.limiter.UpuporLimit;
import com.upupor.service.base.MemberService;
import com.upupor.service.utils.oss.FileUpload;
import com.upupor.service.utils.oss.enums.FileDic;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static com.upupor.security.limiter.LimitType.UPLOAD_CONTENT_IMAGE;
import static com.upupor.security.limiter.LimitType.UPLOAD_PROFILE_IMAGE;


/**
 * 图片上传
 *
 * @author YangRunkang(cruise)
 * @date 2020/04/12 16:15
 */
@Api(tags = "文件上传服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class UploadController {

    private final MemberService memberService;

    @ApiOperation("上传头像")
    @PostMapping(value = "/uploadFile", consumes = "multipart/form-data")
    @UpuporLimit(limitType = UPLOAD_PROFILE_IMAGE, needSpendMoney = true)
    public CcResponse uploadMemberPhoto(@RequestParam("image") MultipartFile file) throws IOException {
        CcResponse ccResponse = new CcResponse();
        if (Objects.isNull(file)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件为空");
        }

        // 获取用户
        MemberEnhance memberEnhance = memberService.memberInfo(ServletUtils.getUserId());
        Member member = memberEnhance.getMember();
        if (Objects.isNull(member)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXISTS);
        }
        member.setVia(FileUpload.upload(file, FileDic.PROFILE));
        // 重新设置头像
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_VIA, member.getVia());
        ServletUtils.getSession().setAttribute(CcConstant.Session.LONG_TIME_UN_UPDATE_PROFILE_PHOTO, Boolean.FALSE);
        Boolean update = memberService.update(memberEnhance);
        if (!update) {
            throw new BusinessException(ErrorCode.UPLOAD_MEMBER_INFO_ERROR);
        }
        ccResponse.setData(member.getVia());
        return ccResponse;
    }

    /**
     * 返回上传文件的地址
     *
     * @param file
     * @return
     */
    @ApiOperation("上传文件")
    @PostMapping(value = "/upload/{fileDic}", consumes = "multipart/form-data")
    @UpuporLimit(limitType = UPLOAD_CONTENT_IMAGE, needSpendMoney = true)
    public CcResponse uploadFile(@RequestParam("file") MultipartFile file, @PathVariable(value = "fileDic", required = true) FileDic dic) throws IOException {
        return new CcResponse(FileUpload.upload(file, dic));
    }
}
