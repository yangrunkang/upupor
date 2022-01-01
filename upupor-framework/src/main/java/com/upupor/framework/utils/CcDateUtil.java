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

package com.upupor.framework.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志工具类
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/12 02:02
 */

public class CcDateUtil {

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String FORMAT_HH_MM = "HH:mm";
    private static final String FORMAT_SHORT = "yyyy-MM-dd";
    private static final SimpleDateFormat SDF = new SimpleDateFormat(FORMAT);
    private static final SimpleDateFormat SDF_HH_MM = new SimpleDateFormat(FORMAT_HH_MM);
    private static final long zero = 0L;
    private static final long one = 1L;
    private static final long minute = 60L;
    private static final long hour = 60L * minute;
    private static final long day = 24 * hour;
    private static final long day30 = 30 * day;

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @return
     */
    public static String timeStamp2Date(Long seconds) {
        return get(seconds);
    }

    public static synchronized String timeStamp2DateOnly(Long seconds) {
        return SDF.format(new Date(seconds * 1000));
    }

    public static synchronized String snsFormat(long createTime) {
        return SDF.format(new Date(createTime * 1000));
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @return
     */
    public static String timeStamp2DateShort(Long seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_SHORT);
        return sdf.format(new Date(seconds * 1000));
    }

    public static String formatDate(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param date   字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String date2TimeStamp(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 取得当前时间戳（精确到秒）
     *
     * @return
     */
    public static long getCurrentTime() {
        long time = System.currentTimeMillis();
        return time / 1000;
    }


    public static String get(Long seconds) {

        try {
            long l = getCurrentTime() - seconds;

            // 显示多少秒之前
            if (l < minute) {
                if (l <= zero) {
                    return one + "秒前";
                }

                return l + "秒前";
            }

            // 显示多少分钟之前
            if (l > minute && l <= hour) {
                return l / minute + "分钟前";
            }

            // 显示多少小时前
            if (l > hour && l < day) {
                return l / hour + "小时前";
            }


            // 显示多少天之前
            if (l > day && l < day30) {
                return l / day + "天前";
            }

            return timeStamp2DateOnly(seconds);
        } catch (Exception e) {
            // 如果有异常,返回默认的年月日时间
            return timeStamp2DateOnly(seconds);
        }

    }


}
