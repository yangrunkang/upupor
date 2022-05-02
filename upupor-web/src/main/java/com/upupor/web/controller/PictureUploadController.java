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

import com.upupor.framework.CcConstant;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.FileUtils;
import com.upupor.security.limiter.UpuporLimit;
import com.upupor.service.data.dao.entity.File;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.service.FileService;
import com.upupor.service.data.service.MemberService;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.utils.OssUtils;
import com.upupor.service.utils.ServletUtils;
import com.upupor.service.utils.UpuporFileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.ArrayUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static com.upupor.security.limiter.LimitType.UPLOAD_CONTENT_IMAGE;
import static com.upupor.security.limiter.LimitType.UPLOAD_PROFILE_IMAGE;


/**
 * 图片上传
 *
 * @author YangRunkang(cruise)
 * @date 2020/04/12 16:15
 */
@Api(tags = "图片上传服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/pic")
public class PictureUploadController {

    private final MemberService memberService;
    private final FileService fileService;
    private final UpuporConfig upuporConfig;

    @ApiOperation("上传头像")
    @PostMapping(value = "/uploadFile", consumes = "multipart/form-data")
    @UpuporLimit(limitType = UPLOAD_PROFILE_IMAGE, needSpendMoney = true)
    public CcResponse uploadMemberPhoto(@RequestParam("image") MultipartFile file) throws IOException {
        CcResponse ccResponse = new CcResponse();
        if (Objects.isNull(file)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件为空");
        }


        // 获取用户
        Member member = memberService.memberInfo(ServletUtils.getUserId());
        if (Objects.isNull(member)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXISTS);
        }

        String fileType = FileUtils.getFileType(file.getInputStream());
        if (!fileType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "禁止上传非图像文件");
        }

        // 检查文件之前是否已经上传过
        String md5 = UpuporFileUtils.getMd5(file.getInputStream());
        File fileByMd5 = fileService.selectByMd5(md5);

        // 设置图片地址
        String picUrl;
        if (Objects.isNull(fileByMd5)) {
            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;
            String suffix = originalFilename.substring(originalFilename.lastIndexOf(CcConstant.ONE_DOTS) + 1);

            String allowsFilesSuffix = upuporConfig.getThumbnails().getAllows();
            // 判定是否是允许上传文件后缀
            if (StringUtils.isEmpty(allowsFilesSuffix)) {
                throw new BusinessException(ErrorCode.LESS_CONFIG);
            }
            String[] split = allowsFilesSuffix.split(CcConstant.COMMA);
            if (ArrayUtils.isEmpty(split)) {
                throw new BusinessException(ErrorCode.LESS_CONFIG);
            }
            if (!Arrays.asList(split).contains(suffix)) {
                throw new BusinessException(ErrorCode.ILLEGAL_FILE_SUFFIX);
            }
            // 文件名
            String fileName = "profile_photo_" + CcUtils.getUuId() + CcConstant.ONE_DOTS + suffix;
            String folderFileName;
            try {
                folderFileName = "profile/" + fileName;
                OssUtils.uploadImgFile(file, folderFileName, 1d);

                String ossFileHost = upuporConfig.getOss().getFileHost();
                picUrl = ossFileHost + folderFileName;


                // 文件入库
                try {
                    File upuporFile = UpuporFileUtils.getUpuporFile(md5, picUrl, member.getUserId());
                    fileService.addFile(upuporFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException(ErrorCode.UPLOAD_ERROR);
            }
        } else {
            picUrl = fileByMd5.getFileUrl();
        }

        member.setVia(picUrl);
        // 重新设置头像
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_VIA, member.getVia());
        Boolean update = memberService.update(member);
        if (!update) {
            throw new BusinessException(ErrorCode.UPLOAD_MEMBER_INFO_ERROR);
        }

        ccResponse.setData(picUrl);
        return ccResponse;
    }


    /**
     * 返回方法参数
     * <p>
     * 要记住 @RequestParam("value")里面的value值,要和页面提交的一直,如果是页面是 filedddd,@RequestParam()里面就要写filedddd
     *
     * @param file
     * @return
     */
    @ApiOperation("编辑器上传图片文件")
    @PostMapping(value = "/uploadFile/editor", consumes = "multipart/form-data")
    @UpuporLimit(limitType = UPLOAD_CONTENT_IMAGE,needSpendMoney = true)
    public CcResponse uploadFileForEditor(@RequestParam("file") MultipartFile file) throws IOException {

        if (Objects.isNull(file)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件为空");
        }

        String fileType = FileUtils.getFileType(file.getInputStream());
        if (!fileType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "禁止上传非图像文件");
        }


        // 检查文件之前是否已经上传过
        String md5 = UpuporFileUtils.getMd5(file.getInputStream());
        File fileByMd5 = fileService.selectByMd5(md5);

        String pictureUrl = "Error";
        if (Objects.isNull(fileByMd5)) {
            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;
            String suffix = originalFilename.substring(originalFilename.lastIndexOf(CcConstant.ONE_DOTS) + 1);
            String allowsFilesSuffix = upuporConfig.getThumbnails().getAllows();
            // 判定是否是允许上传文件后缀
            if (StringUtils.isEmpty(allowsFilesSuffix)) {
                throw new BusinessException(ErrorCode.LESS_CONFIG);
            }
            String[] split = allowsFilesSuffix.split(CcConstant.COMMA);
            if (ArrayUtils.isEmpty(split)) {
                throw new BusinessException(ErrorCode.LESS_CONFIG);
            }
            if (!Arrays.asList(split).contains(suffix)) {
                throw new BusinessException(ErrorCode.ILLEGAL_FILE_SUFFIX);
            }
            // 文件名
            String fileName = "content_" + CcUtils.getUuId() + CcConstant.ONE_DOTS + suffix;
            String folderFileName;
            try {
                folderFileName = "content/" + fileName;
                OssUtils.uploadImgFile(file, folderFileName, null);
                String ossFileHost = upuporConfig.getOss().getFileHost();
                pictureUrl = ossFileHost + folderFileName;

                // 文件入库
                try {
                    File upuporFile = UpuporFileUtils.getUpuporFile(md5, pictureUrl, ServletUtils.getUserId());
                    fileService.addFile(upuporFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            pictureUrl = fileByMd5.getFileUrl();
        }

        return new CcResponse(pictureUrl);
    }
}
