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
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.security.limiter.LimitType;
import com.upupor.security.limiter.UpuporLimit;
import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.lucene.enums.LuceneOperationType;
import com.upupor.lucene.UpuporLucene;
import com.upupor.service.data.dao.entity.File;
import com.upupor.service.data.aggregation.service.FileService;
import com.upupor.service.data.aggregation.service.RadioService;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.service.dto.OperateRadioDto;
import com.upupor.service.outer.req.AddRadioReq;
import com.upupor.service.outer.req.DelRadioReq;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executor;

import static com.upupor.framework.thread.UpuporThreadPoolInit.UPUPOR_THREAD_POOL;
import static com.upupor.security.limiter.LimitType.UPLOAD_RADIO_FILE;


/**
 * @author YangRunkang(cruise)
 * @date 2020/11/15 19:08
 */
@Api(tags = "音频相关服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/radio")
public class RadioController {

    private final FileService fileService;
    private final RadioService radioService;
    private final UpuporConfig upuporConfig;

    @ApiOperation("删除音频")
    @PostMapping(value = "/delete")
    @UpuporLucene(dataType = LuceneDataType.RADIO, operationType = LuceneOperationType.DELETE)
    public CcResponse deleteRadio(DelRadioReq delRadioReq) {
        OperateRadioDto operateRadioDto = radioService.deleteRadio(delRadioReq);

        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(operateRadioDto);
        return ccResponse;

    }


    @ApiOperation("添加音频记录")
    @PostMapping(value = "/add")
    @UpuporLucene(dataType = LuceneDataType.RADIO, operationType = LuceneOperationType.ADD)
    @UpuporLimit(limitType = LimitType.CREATE_RADIO)
    public CcResponse addRadio(AddRadioReq addRadioReq) {
        OperateRadioDto operateRadioDto = radioService.createNewRadio(addRadioReq);

        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(operateRadioDto);
        return ccResponse;
    }


    @ApiOperation("上传音频")
    @PostMapping(value = "/addRadioFile", consumes = "multipart/form-data")
    @UpuporLimit(limitType = UPLOAD_RADIO_FILE, needSpendMoney = true)
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

            radioUrl = upuporConfig.getOss().getFileHost() + folderFileName;

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
