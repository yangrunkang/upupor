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

package com.upupor.app.api;

import com.upupor.api.ContentApi;
import com.upupor.api.common.ApiResp;
import com.upupor.api.request.content.CreateReq;
import com.upupor.api.request.content.DeleteReq;
import com.upupor.api.request.content.DetailReq;
import com.upupor.api.response.content.CreateResp;
import com.upupor.api.response.content.DeleteResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-11-26 23:18
 * @email: yangrunkang53@gmail.com
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/content-app")
public class ContentApp implements ContentApi {
    @Override
    public ApiResp<CreateResp> create(CreateReq createReq) {
        return null;
    }

    @Override
    public ApiResp<DeleteResp> delete(DeleteReq deleteReq) {
        return null;
    }

    @Override
    public ApiResp<DetailReq> detail(DetailReq deleteReq) {
        return null;
    }
}
