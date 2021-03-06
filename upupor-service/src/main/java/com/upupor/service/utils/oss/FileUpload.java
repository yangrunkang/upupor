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

import com.google.common.collect.Lists;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.config.enums.OssSource;
import com.upupor.framework.utils.CcUtils;
import com.upupor.framework.utils.FileUtils;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.data.dao.entity.File;
import com.upupor.service.data.service.FileService;
import com.upupor.service.types.UploadStatus;
import com.upupor.service.utils.ServletUtils;
import com.upupor.service.utils.UpuporFileUtils;
import com.upupor.service.utils.oss.enums.FileDic;
import com.upupor.service.utils.oss.sources.MinioOss;
import com.upupor.service.utils.oss.enums.IsAsync;
import com.upupor.service.utils.oss.enums.IsImg;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * @param dic    ??????: dicName + /
     * @param suffix ???????????? png ??????.
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
     * ??????MultipartFile??????????????????,????????????File?????????
     *
     * @param file
     * @param fileDic
     * @return
     */
    public static String upload(MultipartFile file, FileDic fileDic) throws IOException {
        if (Objects.isNull(file)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "????????????");
        }
        // ??????
        String userId = ServletUtils.getUserId();
        ServletUtils.checkOperatePermission(userId);

        FileUpload fileUpload = new FileUpload();
        FileService fileService = SpringContextUtils.getBean(FileService.class);

        // ????????????md5???,???????????????????????????????????????
        String md5 = UpuporFileUtils.getMd5(file.getInputStream());
        File fileByMd5 = fileService.selectByMd5(md5);
        if (Objects.isNull(fileByMd5)) {
            // ??????????????????
            fileUpload = FileUpload.create(fileDic.getDic(), getFileSuffix(file));


            InputStream fileInputStream = null;

            // ????????????
            if (fileDic.getIsImg().equals(IsImg.YES)) {
                // ?????????????????????
                String fileType = FileUtils.getFileType(file.getInputStream());
                if (!fileType.startsWith("image/")) {
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "???????????????????????????");
                }

                checkThumbnailsSuffix(file);

                double outputQuality;
                if (Objects.isNull(fileDic.getQuality())) {
                    outputQuality = SpringContextUtils.getBean(UpuporConfig.class).getThumbnails().getOutputQuality();
                } else {
                    outputQuality = fileDic.getQuality();
                }
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Thumbnails.of(file.getInputStream())
                        // ??????
                        .scale(1)
                        // ???????????? 0.75 Good,0.5 ok,0.1 not ok
                        .outputQuality(outputQuality)
                        .toOutputStream(os);
                fileInputStream = new ByteArrayInputStream(os.toByteArray());
            } else if (FileDic.RADIO.equals(fileDic)) {
                // ??????????????????
                String fileType = FileUtils.getFileType(file.getInputStream());
                List<String> detectTypes = Lists.newArrayList("audio/mpeg", "application/octet-stream");
                if (!detectTypes.contains(fileType.toLowerCase())) {
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "???????????????mp3???aac??????");
                }

                // ??????????????????
                String suffix = FileUpload.getFileSuffix(file);
                List<String> supportFileType = Lists.newArrayList("mp3", "aac");
                if (!supportFileType.contains(suffix.toLowerCase())) {
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "?????????????????????mp3???aac??????");
                }
            } else {
                fileInputStream = file.getInputStream();
            }

            if (fileDic.getIsAsync().equals(IsAsync.YES)) {
                Executor threadPool = (Executor) SpringContextUtils.getBean(UPUPOR_THREAD_POOL);
                threadPool.execute(new SyncUpload(file.getInputStream(), fileUpload.getFolderName(), fileService, md5));
            } else {
                uploadToOss(fileUpload.getFolderName(), fileInputStream);
            }

            // ????????????
            File upuporFile = UpuporFileUtils.getUpuporFile(md5, fileUpload.getFullUrl(), userId);
            if (fileDic.getIsAsync().equals(IsAsync.YES)) {
                upuporFile.setUploadStatus(UploadStatus.UPLOADING);
            }

            fileService.addFile(upuporFile);
        } else {
            fileUpload.setFullUrl(fileByMd5.getFileUrl());
        }

        return fileUpload.getFullUrl();
    }

    /**
     * ??????????????????,?????? png,?????????
     *
     * @param file
     * @return
     */
    public static String getFileSuffix(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        return originalFilename.substring(originalFilename.lastIndexOf(CcConstant.ONE_DOTS) + 1);
    }

    /**
     * ??????????????????????????????
     *
     * @param file
     */
    public static void checkThumbnailsSuffix(MultipartFile file) {
        UpuporConfig upuporConfig = SpringContextUtils.getBean(UpuporConfig.class);

        String allowsFilesSuffix = upuporConfig.getThumbnails().getAllows();
        // ???????????????????????????????????????
        if (StringUtils.isEmpty(allowsFilesSuffix)) {
            throw new BusinessException(ErrorCode.LESS_CONFIG);
        }
        String[] split = allowsFilesSuffix.split(CcConstant.COMMA);
        if (ArrayUtils.isEmpty(split)) {
            throw new BusinessException(ErrorCode.LESS_CONFIG);
        }
        if (!Arrays.asList(split).contains(getFileSuffix(file))) {
            throw new BusinessException(ErrorCode.ILLEGAL_FILE_SUFFIX);
        }
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
            // ????????????
            try {
                uploadToOss(folderFileName, inputStream);
                File fileByMd5 = fileService.selectByMd5(fileMd5);
                if (Objects.nonNull(fileByMd5) && fileByMd5.getUploadStatus().equals(UploadStatus.UPLOADING)) {
                    fileByMd5.setUploadStatus(UploadStatus.UPLOADED);
                    fileService.update(fileByMd5);
                }
            } catch (Exception e) {
                log.error("??????????????????", e);
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
        // ?????????????????????oss?????????
        if (isDev && OssSource.ALI_OSS.equals(upuporConfig.getOssSource())) {
            log.warn("???????????????,???????????????OSS");
            return;
        }

        for (AbstractOss abstractOss : abstractOssList) {
            if (abstractOss.ossSource().equals(upuporConfig.getOssSource())) {
                abstractOss.init(upuporConfig);
                abstractOss.upload(folderName, inputStream);
            }
        }
    }


}

