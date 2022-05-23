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

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.upupor.framework.config.Email;
import com.upupor.framework.config.Oss;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.config.enums.OssSource;
import com.upupor.framework.utils.SpringContextUtils;

import java.io.InputStream;
import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @email: yangrunkang53@gmail.com
 */
public class AliOss extends AbstractOss {

    @Override
    OssSource ossSource() {
        return OssSource.ALI_OSS;
    }

    @Override
    void uploadToOss(String folderName, InputStream inputStream) {
        UpuporConfig upuporConfig = SpringContextUtils.getBean(UpuporConfig.class);
        Email email = upuporConfig.getEmail();
        Oss oss = upuporConfig.getOss();

        String accessKeyId = email.getAccessKeyId();
        String accessKeySecret = email.getAccessKeySecret();
        String bucketName = oss.getBucketName();

        OSS ossClient = null;
        try {
            // Endpoint以杭州为例，其它Region请按实际情况填写。
            String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
            // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。

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
}
