package com.upupor.web.controller;

import com.upupor.framework.utils.FileUtils;
import com.upupor.service.business.aggregation.service.FileService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcResponse;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.File;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.OssUtils;
import com.upupor.service.utils.ServletUtils;
import com.upupor.service.utils.UpuporFileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${upupor.thumbnails.allows}")
    private String allowsFilesSuffix;
    @Value("${upupor.oss.file-host}")
    private String ossFileHost;

    @ApiOperation("上传头像")
    @PostMapping(value = "/uploadFile", consumes = "multipart/form-data")
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
}
