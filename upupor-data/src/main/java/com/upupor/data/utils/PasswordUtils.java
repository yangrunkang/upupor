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

package com.upupor.data.utils;

import org.springframework.util.DigestUtils;

/**
 * 密码工具类
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/29 02:35
 */
public class PasswordUtils {

    private final static String PREFIX = "#UPUPOR#";

    /**
     * 加密用户输入的密码
     *
     * @param inputPassword
     * @return
     */
    public static String encryptMemberPassword(String inputPassword, String userId, Long createTime) {
        return getMd5(inputPassword, userId, createTime);
    }

    /**
     * 以用户Id 和 用户创建时间 维度 创建 MD5
     *
     * @param inputPassword
     * @param userId
     * @param userCreateTime
     * @return
     */
    private static String getMd5(String inputPassword, String userId, Long userCreateTime) {
        String slat = "codingvcr-slat" + userId + userCreateTime;
        String base = inputPassword + slat;
        // 11+32位长度
        return PREFIX + DigestUtils.md5DigestAsHex(base.getBytes());
    }

}
