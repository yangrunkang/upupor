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

import com.github.houbb.segment.support.segment.result.impl.SegmentResultHandlers;
import com.github.houbb.segment.util.SegmentHelper;
import com.upupor.framework.CcConstant;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.common.algorithm.SnowFlake;
import joptsimple.internal.Strings;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.util.ArrayUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.upupor.framework.CcConstant.*;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/24 02:21
 */
@Component
public class CcUtils {


    private static final SimpleDateFormat SDF;

    /**
     * bug fixed 只能初始化一次
     */
    private static SnowFlake idWorker;

    static {
        SDF = new SimpleDateFormat("yyMMddHHmm");
        idWorker = new SnowFlake(0, 0);
    }

    /**
     * 获取UUID
     *
     * @return
     */
    public static String getUuId() {
        // 加锁 解决SDF线程安全问题
        synchronized (CcUtils.class) {
            if (Objects.isNull(idWorker)) {
                idWorker = new SnowFlake(0, 0);
            }
            // 生成id
            String id = String.valueOf(idWorker.nextId());
            String lStr = id.substring(id.length() / 2);
            String format = SDF.format(new Date());
            return String.format("%s" + lStr, format);
        }
    }

    public static String getVerifyCode() {
        // 生成验证码
        String uuId = CcUtils.getUuId();
        int length = uuId.length();
        return uuId.substring(length - 6);
    }

    /**
     * 判断字是否全部为空
     * true: 全部为空  false: 有一个不为空
     */
    public static Boolean isAllEmpty(String... strs) {
        for (String str : strs) {
            if (!StringUtils.isEmpty(str)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public static String convertListToStr(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return CcConstant.EMPTY_STR;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append(CcConstant.COMMA);
        }
        String s = sb.toString();
        // 返回的值移除了最后一个逗号
        return s.substring(0, s.length() - 1);
    }

    /**
     * 移除最后一个逗号
     *
     * @param s
     * @return
     */
    public static String removeLastComma(String s) {
        if (StringUtils.isEmpty(s)) {
            return s;
        }
        // 判断最后一个字符是否为 逗号
        if (s.endsWith(CcConstant.COMMA)) {
            // 返回的值移除了最后一个逗号
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    /**
     * 将传入的多个字符转换成List(判断了空值)
     *
     * @param strList 多个字符串
     * @return
     */
    public static List<String> toList(String... strList) {
        List<String> list = new ArrayList<>();
        for (String str : strList) {
            if (!StringUtils.isEmpty(str)) {
                list.add(str);
            }
        }

        return list;
    }

    public static Boolean checkEnvIsDev() {
        UpuporConfig upuporConfig = SpringContextUtils.getBean(UpuporConfig.class);
        return "dev".equals(upuporConfig.getEnv());
    }

    /**
     * 判断数组是否不为空
     *
     * @param array
     * @return
     */
    public static Boolean arrayIsNotEmpty(Object[] array) {
        return !ArrayUtils.isEmpty(array);
    }

    public static void sleep2s() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getSegmentResult(String description) {
        List<String> segment = SegmentHelper.segment(description, SegmentResultHandlers.word());
        if (!CollectionUtils.isEmpty(segment)) {
            List<String> collect = segment.stream()
                    .filter(s -> !s.equals(COMMA))
                    .filter(s -> !s.equals(COMMA_ZH))
                    .filter(s -> !s.equals(BLANK))
                    .filter(s -> !s.equals(ONE_DOTS))
                    .filter(s -> !s.equals(DE))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                return Strings.join(collect, COMMA);
            }
        }
        return null;
    }

    @Data
    public static class ThumbnailsImageDto {
        private String fullPath;

        private String fileName;
    }

}
