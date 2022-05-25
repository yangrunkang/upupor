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

import lombok.Getter;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-05-26 00:22
 * @email: yangrunkang53@gmail.com
 */
public enum FileDic {

    // 广告申请文件
    APPLY("apply/"),
    // 系统生成的图片
    PROFILE_SYSTEM("profile_system/", Boolean.TRUE,0.75D),
    // 用户头像
    PROFILE("profile/", Boolean.TRUE,0.75D),
    // 文章图片
    CONTENT("content/", Boolean.TRUE,1D),
    // 电台音频文件
    RADIO(Boolean.TRUE,"radio/"),
    ;

    FileDic(String dic) {
        this.dic = dic;
    }

    FileDic(String dic, Boolean isImg) {
        this.dic = dic;
        this.isImg = isImg;
    }

    FileDic(Boolean isAsync,String dic) {
        this.isAsync = isAsync;
        this.dic = dic;
    }

    FileDic(String dic, Boolean isImg, Double quality) {
        this.dic = dic;
        this.isImg = isImg;
        this.quality = quality;
    }




    /**
     * 目录
     */
    @Getter
    private String dic;

    /**
     * 是否是图片
     */
    @Getter
    private Boolean isImg = Boolean.FALSE;

    /**
     * 图片压缩质量
     */
    @Getter
    private Double quality = 0.75D;

    /**
     * 是否异步上传
     */
    @Getter
    private Boolean isAsync = Boolean.FALSE;
}
