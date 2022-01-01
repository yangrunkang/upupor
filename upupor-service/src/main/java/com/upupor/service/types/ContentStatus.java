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

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容状态
 *
 * @author cruise
 * @createTime 2021-12-31 18:03
 */
@Getter
public enum ContentStatus {
    /**
     * 正常
     */
    NORMAL(0, "正常"),

    /**
     * 草稿
     */
    DRAFT(1, "草稿"),


    /**
     * 审核中
     */
    Applying(2, "审核中"),


    /**
     * 异常
     */
    EXCEPTION(3, "异常"),


    /**
     * 已删除
     */
    DELETED(4, "已删除"),


    /**
     * 回收站
     */
    RUBBISH(5, "回收站"),

    /**
     * 仅自己可见
     */
    ONLY_SELF_CAN_SEE(6, "仅自己可见"),

    ;
    @EnumValue
    private final Integer status;
    private final String name;

    ContentStatus(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public static List<ContentStatus> notIn(){
        List<ContentStatus> statusList = new ArrayList<ContentStatus>();
        statusList.add(ContentStatus.DRAFT);
        statusList.add(ContentStatus.DELETED);
        return statusList;
    }
}
