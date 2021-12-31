/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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

package com.upupor.service.types;

import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cruise
 * @createTime 2021-12-31 18:03
 */
@Getter
public enum CommentSource {

    /**
     * 技术
     */
    TECH(1, "技术"),

    /**
     * 问答
     */
    QA(2, "问答"),

    /**
     * 分享
     */
    SHARE(3, "分享"),

    /**
     * 职场
     */
    WORKPLACE(4, "职场"),

    /**
     * 记录
     */
    RECORD(5, "记录"),

    /**
     * 短内容
     */
    SHORT_CONTENT(6, "短内容"),

    /**
     * 留言
     */
    MESSAGE(7, "留言"),

    /**
     * 电台
     */
    RADIO(8, "电台"),

    ;
    private final Integer source;
    private final String name;

    CommentSource(Integer source, String name) {
        this.source = source;
        this.name = name;
    }

    public static CommentSource getBySource(Integer source) {
        for (CommentSource value : values()) {
            if (value.getSource().equals(source)) {
                return value;
            }
        }
        throw new BusinessException(ErrorCode.PARAM_ERROR);
    }

    /**
     * 内容资源
     *
     * @return
     */
    public static List<Integer> contentSource() {
        List<Integer> commentSourceList = new ArrayList<>();
        commentSourceList.add(TECH.getSource());
        commentSourceList.add(QA.getSource());
        commentSourceList.add(SHARE.getSource());
        commentSourceList.add(WORKPLACE.getSource());
        commentSourceList.add(RECORD.getSource());
        commentSourceList.add(SHORT_CONTENT.getSource());
        return commentSourceList;
    }

}