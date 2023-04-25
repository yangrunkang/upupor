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

import com.google.common.collect.Lists;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.FileUtils;
import com.upupor.service.utils.oss.AbstractInputSteam;
import com.upupor.service.utils.oss.enums.FileDic;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.upupor.service.utils.oss.FileUpload.getFileSuffix;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-09-21 23:08
 * @email: yangrunkang53@gmail.com
 */
public class AudioSteam extends AbstractInputSteam {
    @Override
    protected Boolean hit() {
        return FileDic.RADIO.equals(fileDic);
    }

    @Override
    protected InputStream getUploadInputStream() throws IOException {
        // 检查文件类型
        String fileType = FileUtils.getFileType(file.getInputStream());
        List<String> detectTypes = Lists.newArrayList("audio/mpeg", "application/octet-stream", "audio/vnd.wave");
        if (!detectTypes.contains(fileType.toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "禁止上传非mp3、aac、wav文件");
        }

        // 校验文件后缀
        String suffix = getFileSuffix(file);
        List<String> supportFileType = Lists.newArrayList("mp3", "aac", "wav");
        if (!supportFileType.contains(suffix.toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件类型必须是mp3或aac格式");
        }
        return file.getInputStream();
    }
}
