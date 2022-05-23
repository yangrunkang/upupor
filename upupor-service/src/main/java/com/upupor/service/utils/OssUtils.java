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

package com.upupor.service.utils;

import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.config.enums.OssSource;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.utils.oss.AbstractOss;
import com.upupor.service.utils.oss.AliOss;
import com.upupor.service.utils.oss.MinioOss;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.upupor.framework.utils.CcUtils.checkEnvIsDev;

/**
 * Oss工具类
 *
 * @author YangRunkang(cruise)
 * @date 2020/04/12 11:49
 */
@Slf4j
public class OssUtils {
    private static List<AbstractOss> abstractOssList = new ArrayList<>();
    static {
        abstractOssList.add(new AliOss());
        abstractOssList.add(new MinioOss());
    }


    public static void uploadImgFile(MultipartFile file, String folderName, Double quality) throws IOException {
        double outputQuality;
        if (Objects.isNull(quality)) {
            // 获取压缩后输出图片的质量
            outputQuality = SpringContextUtils.getBean(UpuporConfig.class).getThumbnails().getOutputQuality();
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
        UpuporConfig upuporConfig = SpringContextUtils.getBean(UpuporConfig.class);
        // 只有使用阿里的oss才检测
        if (isDev && OssSource.ALI_OSS.equals(upuporConfig.getOssSource())) {
            log.warn("非上传环境,不会上传到OSS");
            return;
        }

        for (AbstractOss abstractOss : abstractOssList) {
            abstractOss.init(upuporConfig);
            abstractOss.upload(folderName,inputStream);
        }
    }



}
