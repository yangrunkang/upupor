/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2023 yangrunkang
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

package com.upupor.framework.utils;

import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2023-04-02 01:24
 * @email: yangrunkang53@gmail.com
 */
public class DeflaterUtils {
    private static final int BYTE_SIZE = 2048;
    private static final String START = "UPUPOR_LSDH&%^UOIHJKNHBGFR%E^&T*YUIHJBKVH..akti2GC^%E&*Y21687615_ZIP(";
    private static final String END = ")";

    /**
     * 压缩
     */
    public static String zipString(String unzipString) {
        if (StringUtils.isEmpty(unzipString)) {
            return unzipString;
        }

        // 已压缩的不再压缩
        if (isZipped(unzipString)) {
            return unzipString;
        }

        /*
         *     0 ~ 9 压缩等级 低到高
         *     public static final int BEST_COMPRESSION = 9;            最佳压缩的压缩级别。
         *     public static final int BEST_SPEED = 1;                  压缩级别最快的压缩。
         *     public static final int DEFAULT_COMPRESSION = -1;        默认压缩级别。
         *     public static final int DEFAULT_STRATEGY = 0;            默认压缩策略。
         *     public static final int DEFLATED = 8;                    压缩算法的压缩方法(目前唯一支持的压缩方法)。
         *     public static final int FILTERED = 1;                    压缩策略最适用于大部分数值较小且数据分布随机分布的数据。
         *     public static final int FULL_FLUSH = 3;                  压缩刷新模式，用于清除所有待处理的输出并重置拆卸器。
         *     public static final int HUFFMAN_ONLY = 2;                仅用于霍夫曼编码的压缩策略。
         *     public static final int NO_COMPRESSION = 0;              不压缩的压缩级别。
         *     public static final int NO_FLUSH = 0;                    用于实现最佳压缩结果的压缩刷新模式。
         *     public static final int SYNC_FLUSH = 2;                  用于清除所有未决输出的压缩刷新模式; 可能会降低某些压缩算法的压缩率。
         */
        //使用指定的压缩级别创建一个新的压缩器。
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        //设置压缩输入数据。
        deflater.setInput(unzipString.getBytes(StandardCharsets.UTF_8));
        //当被调用时，表示压缩应该以输入缓冲区的当前内容结束。
        deflater.finish();

        final byte[] bytes = new byte[BYTE_SIZE];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(BYTE_SIZE);

        while (!deflater.finished()) {
            //压缩输入数据并用压缩数据填充指定的缓冲区。
            int length = deflater.deflate(bytes);
            outputStream.write(bytes, 0, length);
        }
        //关闭压缩器并丢弃任何未处理的输入。
        deflater.end();
        return wrap(Base64.getEncoder().encodeToString(outputStream.toByteArray()));
    }

    /**
     * 解压缩
     */
    public static String unzipString(String zipString) {
        if (StringUtils.isEmpty(zipString)) {
            return zipString;
        }

        // 没有压缩,就不用解压
        if (!isZipped(zipString)) {
            return zipString;
        }

        zipString = zipString.substring(START.length(), zipString.length() - END.length());


        byte[] decode = Base64.getDecoder().decode(zipString);
        //创建一个新的解压缩器

        Inflater inflater = new Inflater();
        //设置解压缩的输入数据。
        inflater.setInput(decode);
        final byte[] bytes = new byte[BYTE_SIZE];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(BYTE_SIZE);
        try {
            //finished() 如果已到达压缩数据流的末尾，则返回true。
            while (!inflater.finished()) {
                //将字节解压缩到指定的缓冲区中。
                int length = inflater.inflate(bytes);
                outputStream.write(bytes, 0, length);
            }
        } catch (DataFormatException e) {
            e.printStackTrace();
            return null;
        } finally {
            //关闭解压缩器并丢弃任何未处理的输入。
            inflater.end();
        }
        try {
            return outputStream.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String wrap(String zippedStr) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(START);
        stringBuilder.append(zippedStr);
        stringBuilder.append(END);
        return stringBuilder.toString();
    }

    private static Boolean isZipped(String zippedStr) {
        return zippedStr.startsWith(START) && zippedStr.endsWith(END);
    }

    public static void main(String[] args) {
//        String He = "Hello";
//        System.out.println(DeflaterUtils.zipString(He));
//        System.out.println(DeflaterUtils.unzipString(DeflaterUtils.zipString(He)));

//        String hel = wrap("Hello world");
//        System.out.println(hel);
//        System.out.println(hel.substring(START.length(), hel.length() - END.length()));
    }
}
