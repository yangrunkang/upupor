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

package com.upupor.service.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static com.upupor.service.utils.CcUtils.getProperty;

/**
 * Oss工具类
 *
 * @author YangRunkang(cruise)
 * @date 2020/04/12 11:49
 */
@Slf4j
public class OssUtils {


    public static void uploadImgFile(MultipartFile file, String folderName, Double quality) throws IOException {
        double outputQuality;
        if (Objects.isNull(quality)) {
            // 获取压缩后输出图片的质量
            String outputQualityKey = "upupor.thumbnails.output-quality";
            outputQuality = Double.parseDouble(getProperty(outputQualityKey));
        } else {
            outputQuality = quality;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream())
                // 比例
                .scale(1)
                // 输出质量 0.75 Good,0.5 ok,0.1 not ok
                .outputQuality(outputQuality)
                .toOutputStream(os);
        ByteArrayInputStream swapStream = new ByteArrayInputStream(os.toByteArray());


        uploadToOss(folderName, swapStream);
    }

    public static void uploadAnyFile(MultipartFile file, String folderFileName) throws IOException {
        uploadToOss(folderFileName, file.getInputStream());
    }


    public static void uploadToOss(String folderName, InputStream inputStream) {
        Boolean isDev = checkEnvIsDev();
        if (isDev) {
            log.warn("非上传环境,不会上传到OSS");
            return;
        }

        OSS ossClient = null;
        try {
            // Endpoint以杭州为例，其它Region请按实际情况填写。
            String endpoint = "http://oss-cn-hangzhou-internal.aliyuncs.com";
            // 如果是dev就采用公网上传
            if (isDev) { // 这里用于调试,勿删
                endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
            }
            // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
            String accessKeyId = CcUtils.getProperty("upupor.email.accesskey-id");
            String accessKeySecret = CcUtils.getProperty("upupor.email.accesskey-secret");
            String bucketName = CcUtils.getProperty("upupor.oss.bucket-name");
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.putObject(bucketName, folderName, inputStream);
            ossClient.shutdown();
        } catch (Exception e) {
            throw e;
        } finally {
            if (Objects.nonNull(ossClient)) {
                // 关闭OSSClient。
                ossClient.shutdown();
            }
        }
    }

    public static Boolean checkEnvIsDev() {
        String property = getProperty("upupor.env");
        return "dev".equals(property);
    }

}
