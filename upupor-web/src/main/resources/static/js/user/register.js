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
    // 注册
    register();
});

function register() {
    $("form").submit(function () {
        let userName = $("#userName").val();
        let email = $("#email").val();
        let password = $("#password").val();
        let verifyCode = $("#verifyCode").val();

        if (cvIsNull(userName)) {
            $.cvWarn("请输入用户名");
            return false;
        }

        if (cvIsNull(email)) {
            $.cvWarn("请输入邮件地址");
            return false;
        }

        if (cvIsNull(password)) {
            $.cvWarn("请输入密码");
            return false;
        }

        if (cvIsNull(verifyCode)) {
            $.cvWarn("验证码为空");
            return false;
        }

        let param = {
            userName: userName,
            email: email,
            password: password,
            verifyCode: verifyCode,
        };
        $.cvPost('/member/add', param, function (data) {
            if (respSuccess(data)) {
                $.cvSuccess("注册成功,已为您自动登录");
                setTimeout(function () {
                    window.location.href = '/';
                }, 1600);
            } else {
                $.cvError(data)
            }
        });

        return false;
    });
}

function sendVerifyCode() {
    let userName = $("#userName").val();
    let email = $("#email").val();
    let password = $("#password").val();

    if (cvIsNull(userName)) {
        $.cvWarn("请输入用户名");
        return false;
    }

    if (cvIsNull(email)) {
        $.cvWarn("请输入邮件地址");
        return false;
    }

    if (cvIsNull(password)) {
        $.cvWarn("请输入密码");
        return false;
    }

    let req = {
        email: email,
        source: 'register'
    };

    $.cvPost("/member/sendVerifyCode", req, function (data) {
        if (respSuccess(data)) {
            $.cvSuccess("发送成功,请查收邮件");
            // 添加按钮不可点击的样式
            $("#send-verify-code-btn").addClass('disabled');
            // 真实控制按钮不可点击的
            $("#send-verify-code-btn").attr('disabled', true);

            let timeout = 90;
            let interval = setInterval(function () {
                $("#send-verify-code-btn").text('已发送,请查收邮件(' + timeout + '秒)')
                timeout = timeout - 1;
                if (timeout <= 0) {
                    clearInterval(interval);
                    $("#send-verify-code-btn").removeClass('disabled');
                    $("#send-verify-code-btn").attr('disabled', false);
                    $("#send-verify-code-btn").text('发送验证码');
                }
            }, 1000)
        } else {
            $.cvError("邮件发送失败");
        }
    });
}

