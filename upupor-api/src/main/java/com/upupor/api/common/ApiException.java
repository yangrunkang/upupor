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

package com.upupor.api.common;

/**
 * API异常
 *
 * @author Yang Runkang (cruise)
 * @createTime 2022-12-03 23:01
 * @email: yangrunkang53@gmail.com
 */
public class ApiException extends RuntimeException {

    private final Integer code;
    private String message;


    public ApiException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiException(ApiErrorCode apiErrorCode) {
        this.code = apiErrorCode.getCode();
        this.message = apiErrorCode.getError();
    }

    /**
     * @param apiErrorCode
     * @param appendMessage 是追加的异常信息
     */
    public ApiException(ApiErrorCode apiErrorCode, String appendMessage) {
        this.code = apiErrorCode.getCode();
        this.message = apiErrorCode.getError() + "," + appendMessage;
    }


    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return String.format("【%d】 %s", this.code, this.message);
    }
}
