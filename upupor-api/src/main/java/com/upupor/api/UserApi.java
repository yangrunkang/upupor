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

package com.upupor.api;


import com.upupor.api.common.ApiResp;
import com.upupor.api.request.user.LoginReq;
import com.upupor.api.request.user.RegisterReq;
import com.upupor.api.response.user.DetailResp;
import com.upupor.api.response.user.LoginResp;
import com.upupor.api.response.user.RegisterResp;

/**
 * 用户API
 *
 * @author Yang Runkang (cruise)
 * @date 2022年11月23日
 * @email: yangrunkang53@gmail.com
 */
public interface UserApi {
    /**
     * 注册
     *
     * @param registerReq
     * @return
     */
    ApiResp<RegisterResp> register(RegisterReq registerReq);


    /**
     * 登录
     *
     * @param loginReq
     * @return 返回token
     */
    ApiResp<LoginResp> login(LoginReq loginReq);


    /**
     * 登录
     *
     * @param userId
     * @return 返回token
     */
    ApiResp<DetailResp> info(String userId);

}
