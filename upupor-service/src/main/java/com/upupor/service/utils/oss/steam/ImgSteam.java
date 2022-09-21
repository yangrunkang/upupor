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

package com.upupor.service.utils.oss.steam;

import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.FileUtils;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.utils.oss.AbstractInputSteam;
import com.upupor.service.utils.oss.enums.IsImg;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

import static com.upupor.service.utils.oss.FileUpload.getFileSuffix;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-09-21 23:08
 * @email: yangrunkang53@gmail.com
 */
public class ImgSteam extends AbstractInputSteam {
    @Override
    protected Boolean hit() {
        return fileDic.getIsImg().equals(IsImg.YES);
    }


    @Override
    protected InputStream getUploadInputStream() throws IOException {
        // 检查是否是图片
        String fileType = FileUtils.getFileType(file.getInputStream());
        if (!fileType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "禁止上传非图像文件");
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
                // 比例
                .scale(1)
                // 输出质量 0.75 Good,0.5 ok,0.1 not ok
                .outputQuality(outputQuality)
                .toOutputStream(os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    /**
     * 校验被压缩文件的后缀
     *
     * @param file
     */
    public static void checkThumbnailsSuffix(MultipartFile file) {
        UpuporConfig upuporConfig = SpringContextUtils.getBean(UpuporConfig.class);

        String allowsFilesSuffix = upuporConfig.getThumbnails().getAllows();
        // 判定是否是允许上传文件后缀
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

}
