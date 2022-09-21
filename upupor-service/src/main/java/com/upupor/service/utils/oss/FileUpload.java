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

package com.upupor.service.utils.oss;

import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.config.enums.OssSource;
import com.upupor.framework.utils.CcUtils;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.data.dao.entity.File;
import com.upupor.service.data.service.FileService;
import com.upupor.service.types.UploadStatus;
import com.upupor.service.utils.ServletUtils;
import com.upupor.service.utils.UpuporFileUtils;
import com.upupor.service.utils.oss.enums.FileDic;
import com.upupor.service.utils.oss.enums.IsAsync;
import com.upupor.service.utils.oss.sources.MinioOss;
import com.upupor.service.utils.oss.steam.AudioSteam;
import com.upupor.service.utils.oss.steam.ImgSteam;
import com.upupor.service.utils.oss.steam.NormalFileSteam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import static com.upupor.framework.thread.UpuporThreadPoolInit.UPUPOR_THREAD_POOL;
import static com.upupor.framework.utils.CcUtils.checkEnvIsDev;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-05-25 23:12
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@Data
public class FileUpload {

    private String folderName;
    private String fullUrl;

    static List<AbstractInputSteam> abstractInputSteamList = new ArrayList<>();

    static {
        abstractInputSteamList.add(new AudioSteam());
        abstractInputSteamList.add(new ImgSteam());
        // 打底
        abstractInputSteamList.add(new NormalFileSteam());
    }

    /**
     * @param dic    格式: dicName + /
     * @param suffix 格式例如 png 不带.
     * @return
     */
    public static FileUpload create(String dic, String suffix) {

        UpuporConfig upuporConfig = SpringContextUtils.getBean(UpuporConfig.class);

        String fileName = CcUtils.getUuId() + CcConstant.ONE_DOTS + suffix;
        String folderName = dic + fileName;

        FileUpload fileUpload = new FileUpload();
        fileUpload.setFullUrl(upuporConfig.getUploadFilePrefix() + folderName);
        fileUpload.setFolderName(folderName);
        return fileUpload;
    }


    public static String upload(FileUpload fileUpload, InputStream inputStream) throws IOException {
        uploadToOss(fileUpload.getFolderName(), inputStream);
        return fileUpload.getFullUrl();
    }

    /**
     * 接受MultipartFile对象上传文件,同时维护File表数据
     *
     * @param file
     * @param fileDic
     * @return
     */
    public static String upload(MultipartFile file, FileDic fileDic) throws IOException {
        if (Objects.isNull(file)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件为空");
        }
        // 校验
        String userId = ServletUtils.getUserId();
        ServletUtils.checkOperatePermission(userId);

        FileUpload fileUpload = new FileUpload();
        FileService fileService = SpringContextUtils.getBean(FileService.class);

        // 计算文件md5值,检查文件之前是否已经上传过
        String md5 = UpuporFileUtils.getMd5(file.getInputStream());
        File fileByMd5 = fileService.selectByMd5(md5);
        if (Objects.isNull(fileByMd5)) {
            // 系统无记录,新文件
            fileUpload = newFile(file, fileDic, userId, fileService, md5);
        } else {
            // 使用系统中已经处理过的文件url
            fileUpload.setFullUrl(fileByMd5.getFileUrl());
        }

        return fileUpload.getFullUrl();
    }

    @NotNull
    private static FileUpload newFile(MultipartFile file, FileDic fileDic, String userId, FileService fileService, String md5) throws IOException {
        FileUpload fileUpload;
        // 组装文件信息
        fileUpload = FileUpload.create(fileDic.getDic(), getFileSuffix(file));

        InputStream fileInputStream = null;
        for (AbstractInputSteam abstractInputSteam : abstractInputSteamList) {
            abstractInputSteam.init(file, fileDic);
            if (abstractInputSteam.hit()) {
                fileInputStream = abstractInputSteam.getUploadInputStream();
                break;
            }
        }

        if (Objects.isNull(fileInputStream)) {
            throw new BusinessException(ErrorCode.UPLOAD_ERROR, "处理文件输入流失败");
        }

        if (fileDic.getIsAsync().equals(IsAsync.YES)) {
            Executor threadPool = (Executor) SpringContextUtils.getBean(UPUPOR_THREAD_POOL);
            threadPool.execute(new SyncUpload(fileInputStream, fileUpload.getFolderName(), fileService, md5));
        } else {
            uploadToOss(fileUpload.getFolderName(), fileInputStream);
        }

        // 创建新的文件对象,入库
        File upuporFile = UpuporFileUtils.createUpuporFile(md5, fileUpload.getFullUrl(), userId);
        if (fileDic.getIsAsync().equals(IsAsync.YES)) {
            upuporFile.setUploadStatus(UploadStatus.UPLOADING);
        }
        fileService.addFile(upuporFile);
        return fileUpload;
    }


    @Slf4j
    @Data
    private static class SyncUpload implements Runnable {

        private InputStream inputStream;

        private String folderFileName;

        private String fileMd5;

        private FileService fileService;

        SyncUpload(InputStream inputStream, String folderFileName, FileService fileService, String fileMd5) {
            this.inputStream = inputStream;
            this.folderFileName = folderFileName;
            this.fileService = fileService;
            this.fileMd5 = fileMd5;
        }


        @Override
        public void run() {
            // 异步处理
            try {
                uploadToOss(folderFileName, inputStream);
                File fileByMd5 = fileService.selectByMd5(fileMd5);
                if (Objects.nonNull(fileByMd5) && fileByMd5.getUploadStatus().equals(UploadStatus.UPLOADING)) {
                    fileByMd5.setUploadStatus(UploadStatus.UPLOADED);
                    fileService.update(fileByMd5);
                }
            } catch (Exception e) {
                log.error("异步上传失败", e);
            }
        }
    }


    private static List<AbstractOss> abstractOssList = new ArrayList<>();

    static {
        abstractOssList.add(new MinioOss());
    }


    private static void uploadToOss(String folderName, InputStream inputStream) {
        Boolean isDev = checkEnvIsDev();
        UpuporConfig upuporConfig = SpringContextUtils.getBean(UpuporConfig.class);
        // 只有使用阿里的oss才检测
        if (isDev && OssSource.ALI_OSS.equals(upuporConfig.getOssSource())) {
            log.warn("非上传环境,不会上传到OSS");
            return;
        }

        for (AbstractOss abstractOss : abstractOssList) {
            if (abstractOss.ossSource().equals(upuporConfig.getOssSource())) {
                abstractOss.init(upuporConfig);
                abstractOss.upload(folderName, inputStream);
            }
        }
    }

    /**
     * 获取文件后缀,结果 png,不带点
     *
     * @param file
     * @return
     */
    public static String getFileSuffix(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        return originalFilename.substring(originalFilename.lastIndexOf(CcConstant.ONE_DOTS) + 1);
    }

}

