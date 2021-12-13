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
