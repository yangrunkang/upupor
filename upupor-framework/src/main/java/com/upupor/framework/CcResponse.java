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

package com.upupor.framework;

import lombok.Data;

import java.io.Serializable;

/**
 * UPUPOR响应对象
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/20 03:00
 */
@Data
public class CcResponse implements Serializable {

    /**
     * 响应码
     */
    private Integer code = 0;

    /**
     * 响应对象
     */
    private Object data;


    public CcResponse(Integer code, Object data) {
        this.code = code;
        this.data = data;
    }

    public CcResponse() {
    }

    public CcResponse(BusinessException businessException) {
        this.code = businessException.getCode();
        this.data = businessException.getMessage();
    }

    public CcResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.data = errorCode.getMessage();
    }

    public CcResponse(Object data) {
        this.data = data;
    }

}
