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

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.dao.entity.File;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author YangRunkang(cruise)
 * @date 2020/11/17 22:18
 */
public class UpuporFileUtils {

    public static String getMd5(InputStream inputStream) throws IOException {
        return DigestUtils.md5Hex(inputStream);
    }

    public static File getUpuporFile(InputStream inputStream, String url, String userId) throws IOException {
        File file = new File();
        file.setCreateTime(CcDateUtil.getCurrentTime());
        file.setFileMd5(getMd5(inputStream));
        file.setUserId(userId);
        file.setFileUrl(url);
        file.setSysUpdateTime(new Date());
        return file;
    }

    public static File getUpuporFile(String md5, String url, String userId) {
        File file = new File();
        file.setCreateTime(CcDateUtil.getCurrentTime());
        file.setFileMd5(md5);
        file.setUserId(userId);
        file.setFileUrl(url);
        file.setSysUpdateTime(new Date());
        return file;
    }

}
