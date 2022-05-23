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

package com.upupor.service.utils.oss;

import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.config.Minio;
import com.upupor.framework.config.enums.OssSource;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.InputStream;

/**
 * @author Yang Runkang (cruise)
 * @email: yangrunkang53@gmail.com
 */
public class MinioOss extends AbstractOss {

    @Override
    OssSource ossSource() {
        return OssSource.MINIO_OSS;
    }

    @Override
    void uploadToOss(String folderName, InputStream inputStream) {
        Minio minio = upuporConfig.getMinio();
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(minio.getEndpoint())
                            .credentials(minio.getAccessKey(), minio.getSecretKey())
                            .build();

            // Make 'asiatrip' bucket if not exist.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(minio.getBucketName()).build());
            if (!found) {
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minio.getBucketName()).build());
            } else {
                System.out.println("Bucket 'asiatrip' already exists.");
            }

            // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
            // 'asiatrip'.

            String[] folderNameSplit = folderName.split(CcConstant.BACKSLASH);
            if(ArrayUtils.isEmpty(folderNameSplit)){
                throw new BusinessException(ErrorCode.UPLOAD_ERROR,"请指定目录及文件");
            }
            String fileName = folderNameSplit[folderNameSplit.length - 1];
            File file = new File(fileName);
            FileUtils.copyInputStreamToFile(inputStream,file);
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(minio.getBucketName())
                            .object(folderName)
                            .filename(file.getName())
                            .build());
            file.delete();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.UPLOAD_ERROR);
        }
    }

    public static void main(String[] args) {
        String sf = "sdf.zip";
        String[] split = sf.split("/");
        System.out.println(split);
    }
}
