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

package com.upupor.service.outer.req;

import com.upupor.service.types.ContentType;
import lombok.Data;

/**
 * 获取公共内容Req
 *
 * @author YangRunkang(cruise)
 * @date 2020/05/14 22:40
 */
@Data
public class GetCommonReq {

    private String tagId;

    private Integer pageNum;

    private Integer pageSize;

    /**
     * 获取指定类型的文章
     */
    private ContentType contentType;

    public static GetCommonReq create(Integer pageNum, Integer pageSize) {
        GetCommonReq getCommonReq = new GetCommonReq();
        getCommonReq.setPageNum(pageNum);
        getCommonReq.setPageSize(pageSize);
        return getCommonReq;
    }

    public static GetCommonReq create(String tagId, Integer pageNum, Integer pageSize, ContentType contentType) {
        GetCommonReq getCommonReq = new GetCommonReq();
        getCommonReq.setTagId(tagId);
        getCommonReq.setPageNum(pageNum);
        getCommonReq.setPageSize(pageSize);
        getCommonReq.setContentType(contentType);
        return getCommonReq;
    }
}
