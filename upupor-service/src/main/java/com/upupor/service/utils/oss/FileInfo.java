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

import com.upupor.framework.CcConstant;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.CcUtils;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.data.dao.entity.File;
import com.upupor.service.data.service.FileService;
import com.upupor.service.utils.OssUtils;
import com.upupor.service.utils.ServletUtils;
import com.upupor.service.utils.UpuporFileUtils;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-05-25 23:12
 * @email: yangrunkang53@gmail.com
 */
@Data
public class FileInfo {

    private String folderName;
    private String fullUrl;

    /**
     *
     * @param dic 格式: dicName + /
     * @param suffix 格式例如 png 不带.
     * @return
     */
    public static FileInfo getUploadFileUrl(String dic,String suffix){

        UpuporConfig upuporConfig = SpringContextUtils.getBean(UpuporConfig.class);

        String fileName = CcUtils.getUuId() + CcConstant.ONE_DOTS +suffix;
        String folderName = dic + fileName;

        FileInfo fileInfo = new FileInfo();
        fileInfo.setFullUrl(upuporConfig.getUploadFilePrefix() + folderName);
        fileInfo.setFolderName(folderName);
        return fileInfo;
    }

}

