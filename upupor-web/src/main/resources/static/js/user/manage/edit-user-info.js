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

});



function editUserInfo() {
    $("form").submit(function () {
        console.log("个人中心");
        let userName = $("#userName").val();
        let introduce = $("#introduce").val();
        let openEmail = $("#openEmail").val();

        let formData = new FormData();
        formData.append('userName', userName);
        formData.append('introduce', introduce);
        formData.append('openEmail', openEmail);

        $.ajax({
            url: '/member/edit',
            type: 'post',
            async: false,
            data: formData,
            processData: false,// 告诉jQuery不要去处理发送的数据
            contentType: false,// 告诉jQuery不要去设置Content-Type请求头
            beforeSend: function () {//过程...
                console.log('正在进行，请稍候')
            },
            success: function (res) {
                console.log(res);
                if (res.code === 0) {
                    if (respSuccess(res.data)) {
                        $.cvSuccess("更新个人资料成功");
                        setTimeout(function () {
                            window.location.href = '/user/manage/edit-user-info';
                        }, 1600)
                    } else {
                        $.cvError(res.data);
                    }
                } else {
                    $.cvError(res.data);
                }
            },
            error: function () {
                console.log('更新失败')
            }
        });

        return false;
    });
}

function userLeftContentBtnActive(){
    let path_name = window.location.pathname;
    console.log(path_name.split("/").slice(1).join("_"));
    $("." + path_name.split("/").slice(1).join("_")).addClass('active');
}
