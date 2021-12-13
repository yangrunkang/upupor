package com.upupor.framework.utils;

import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 文件工具类
 *
 * @author YangRunkang(cruise)
 * @date 2020/11/21 00:44
 */
public class FileUtils {

    private final static Tika TIKA = new Tika();

    public static String getFileType(File file) throws IOException {
        if (Objects.isNull(file)) {
            throw new RuntimeException("文件为空");
        }

        return TIKA.detect(file);
    }

    public static String getFileType(InputStream inputStream) throws IOException {
        if (Objects.isNull(inputStream)) {
            throw new RuntimeException("输入流为空");
        }

        return TIKA.detect(inputStream);
    }


}
