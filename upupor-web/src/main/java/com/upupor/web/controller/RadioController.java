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

package com.upupor.web.controller;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.FileUtils;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.business.aggregation.service.FileService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.business.aggregation.service.RadioService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcResponse;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.File;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.spi.req.AddRadioReq;
import com.upupor.service.spi.req.DelRadioReq;
import com.upupor.service.types.RadioStatus;
import com.upupor.service.types.UploadStatus;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.OssUtils;
import com.upupor.service.utils.ServletUtils;
import com.upupor.service.utils.UpuporFileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;

import static com.upupor.framework.thread.UpuporThreadPoolInit.UPUPOR_THREAD_POOL;


/**
 * @author YangRunkang(cruise)
 * @date 2020/11/15 19:08
 */
@Api(tags = "音频相关服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/radio")
public class RadioController {

    private final MemberService memberService;

    private final FileService fileService;

    private final RadioService radioService;

    private final ContentService contentService;

    @Value("${upupor.oss.file-host}")
    private String ossFileHost;

    @ApiOperation("删除音频")
    @PostMapping(value = "/delete")
    public CcResponse deleteRadio(DelRadioReq delRadioReq) {

        if (Objects.isNull(delRadioReq) || StringUtils.isEmpty(delRadioReq.getRadioId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        String userId = ServletUtils.getUserId();

        Radio radio = radioService.getByRadioId(delRadioReq.getRadioId());
        if (Objects.isNull(radio)) {
            throw new BusinessException(ErrorCode.RADIO_NOT_EXISTS);
        }

        if (!radio.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.RADIO_NOT_BELONG_TO_YOU);
        }

        radio.setStatus(RadioStatus.DELETED);
        radio.setSysUpdateTime(new Date());
        Integer integer = radioService.updateRadio(radio);


        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(integer > 0);

        return ccResponse;

    }


    @ApiOperation("上传音频")
    @PostMapping(value = "/addRadioFile", consumes = "multipart/form-data")
    public CcResponse uploadRadioFile(@RequestParam("radioFile") MultipartFile file) throws IOException {
        CcResponse ccResponse = new CcResponse();

        // 检查文件类型
        String fileType = FileUtils.getFileType(file.getInputStream());
        if (!"audio/mpeg".equals(fileType)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "禁止上传非mp3文件");
        }

        // 检查文件之前是否已经上传过
        String md5 = UpuporFileUtils.getMd5(file.getInputStream());
        File fileByMd5 = fileService.selectByMd5(md5);

        // 音频地址
        String radioUrl;
        if (Objects.isNull(fileByMd5)) {
            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;
            String suffix = originalFilename.substring(originalFilename.lastIndexOf(CcConstant.ONE_DOTS) + 1);

            if (!"mp3".equalsIgnoreCase(suffix)) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "文件类型必须是mp3格式");
            }

            // 文件名
            String fileName = "radio" + CcUtils.getUuId() + CcConstant.ONE_DOTS + suffix;
            String folderFileName;
            try {
                folderFileName = "radio/" + fileName;
                // 异步上传
                Executor threadPool = (Executor) SpringContextUtils.getBean(UPUPOR_THREAD_POOL);
                threadPool.execute(new SyncUploadRadio(file, folderFileName, fileService, md5));
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException(ErrorCode.UPLOAD_ERROR);
            }
            radioUrl = ossFileHost + folderFileName;

            // 文件入库
            try {
                File upuporFile = UpuporFileUtils.getUpuporFile(md5, radioUrl, ServletUtils.getUserId());
                upuporFile.setUploadStatus(UploadStatus.UPLOADING);
                fileService.addFile(upuporFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            radioUrl = fileByMd5.getFileUrl();
        }

        ccResponse.setData(radioUrl);
        return ccResponse;
    }

    @ApiOperation("添加音频记录")
    @PostMapping(value = "/add")
    public CcResponse addRadio(AddRadioReq addRadioReq) {
        CcResponse ccResponse = new CcResponse();
        if (Objects.isNull(addRadioReq.getFileUrl())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "音频文件为空");
        }

        if (Objects.isNull(addRadioReq.getRadioIntro())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "音频简介为空");
        }

        // 获取用户
        Member member = memberService.memberInfo(ServletUtils.getUserId());
        if (Objects.isNull(member)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXISTS);
        }

        // 检查文件是否上传成功
        File file = fileService.selectByFileUrl(addRadioReq.getFileUrl());
        if (Objects.isNull(file)) {
            throw new BusinessException(ErrorCode.RADIO_NOT_EXITS_IN_DB);
        }

        Radio radio = new Radio();
        radio.setRadioId(CcUtils.getUuId());
        radio.setUserId(member.getUserId());
        radio.setContentId(null);
        radio.setStatus(RadioStatus.NORMAL);
        radio.setCreateTime(CcDateUtil.getCurrentTime());
        radio.setLatestCommentTime(CcDateUtil.getCurrentTime());
        radio.setSysUpdateTime(new Date());
        radio.setRadioIntro(addRadioReq.getRadioIntro());
        radio.setRadioUrl(file.getFileUrl());

        if (!radioService.addRadio(radio)) {
            throw new BusinessException(ErrorCode.UPLOAD_RADIO_ERROR);
        }

        // 初始化数据
        contentService.initContendData(radio.getRadioId());


        ccResponse.setData(file.getFileUrl());
        return ccResponse;
    }

    /**
     * 异步上传电台文件
     */
    @Slf4j
    @Data
    private static class SyncUploadRadio implements Runnable {

        private MultipartFile file;

        private String folderFileName;

        private String fileMd5;

        private FileService fileService;

        SyncUploadRadio(MultipartFile file, String folderFileName, FileService fileService, String fileMd5) {
            this.file = file;
            this.folderFileName = folderFileName;
            this.fileService = fileService;
            this.fileMd5 = fileMd5;
        }


        @Override
        public void run() {
            // 异步处理
            try {
                OssUtils.uploadAnyFile(file, folderFileName);

                File fileByMd5 = fileService.selectByMd5(this.fileMd5);
                if (Objects.isNull(fileByMd5)) {
                    // 等主流程入库结束
                    Thread.sleep(1300);
                    fileByMd5 = fileService.selectByMd5(this.fileMd5);
                }
                if (Objects.nonNull(fileByMd5) && fileByMd5.getUploadStatus().equals(UploadStatus.UPLOADING)) {
                    fileByMd5.setUploadStatus(UploadStatus.UPLOADED);
                    fileService.update(fileByMd5);
                }
            } catch (Exception e) {
                log.error("异步上传音频失败");
                e.printStackTrace();
            }
        }
    }

}
