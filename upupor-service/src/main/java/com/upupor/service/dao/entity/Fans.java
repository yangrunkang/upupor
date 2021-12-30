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

package com.upupor.service.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import lombok.Data;

import java.util.Date;

@Data
public class Fans extends BaseEntity {

    private String fanId;

    private String userId;

    private String fanUserId;

    private Integer fanStatus;

    private Long createTime;

    private Date sysUpdateTime;

    @TableField(exist = false)
    private Member member;

    /**
     * 页面冗余字段
     */
    @TableField(exist = false)
    private String createDate;
    @TableField(exist = false)
    private String createDateDiff;

    public String getCreateDate() {
        return CcDateUtil.timeStamp2Date(createTime);
    }

    @JSONField(serialize = false)
    public String getCreateDateDiff() {
        return CcDateUtil.snsFormat(createTime);
    }
}
