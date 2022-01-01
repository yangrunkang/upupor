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

$(function () {
});

$(window).on('load', function() {
    // 登录
    login();
});

function login() {
    $('form').submit(function (event) {
        let email = $('#email').val();
        let password = $('#password').val();

        if (cvIsNull(email)) {
            $.cvError('请输入正确邮箱');
            return false;
        }
        if (cvIsNull(password)) {
            $.cvError('请输入密码');
            return false;
        }

        let param = {
            email: email,
            password: password
        };

        $.cvPost('/member/get', param, function (data) {
            if (respSuccess(data)) {
                let back = getQueryString('back');
                if(!cvIsNull(back)){
                    window.location.href = back;
                }else if(window.location.pathname !== '/login'){
                    // 回退到上一步
                    window.location.href = window.location.pathname + window.location.search;
                }else{
                    window.location.href = '/';
                }
            } else {
                $.cvError(data)
            }
        });

        // 不刷新
        return false;
    });
}
