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
    // 激活右边内容按钮
    userLeftContentBtnActive();

    // 编辑用户信息
    editUserInfo();

    $("#self-define-css").focus(function () {
        let cssPatternValue = $('input:radio[class="css_pattern_radio"]:checked').val();
        if (!cvIsNull(cssPatternValue) && cssPatternValue !== 'self-define') {
            $.cvError("编写自定义css样式之前请优先选中自定义按钮");
        }
    })
});


function editUserInfo() {
    $("form").submit(function () {
        let cssPatternValue = $('input:radio[class="css_pattern_radio"]:checked').val();
        let formData = new FormData();

        if (cssPatternValue === 'self-define') {
            let selfDefineCss = $("#self-define-css").val();
            if (cvIsNull(selfDefineCss)) {
                $.cvError("自定义css不能为空");
                return false;
            }
            formData.append('selfDefineCss', selfDefineCss);
        } else {
            formData.append('cssPatternValue', cssPatternValue);
        }
        $.ajax({
            headers: {
                UpuporToken: localStorage.getItem("upupor_token"),
            },
            url: '/member/edit/bg-style-settings',
            type: 'post',
            async: false,
            data: formData,
            contentType: "application/json; charset=utf-8",
            success: function (res) {
                if (res.code === 0) {
                    if (respSuccess(res.data)) {
                        $.cvSuccess("背景样式设置成功");
                        setTimeout(function () {
                            window.location.href = '/user/manage/bg-style-settings';
                        }, 1600)
                    } else {
                        $.cvError("背景样式设置失败");
                    }
                } else {
                    $.cvError("背景样式设置失败");
                }
            },
            error: function () {
                console.log('背景样式设置失败')
            }
        });

        return false;
    });
}

function userLeftContentBtnActive() {
    let path_name = window.location.pathname;
    $("." + path_name.split("/").slice(1).join("_")).addClass('active');
}
