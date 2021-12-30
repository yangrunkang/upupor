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

$(function () {
    userLeftContentBtnActive();

});

function userLeftContentBtnActive() {
    let path_name = window.location.pathname;
    $("." + path_name.split("/").slice(1).join("_")).addClass('active');
}

function changeCommentStatus(operation, commentId) {
    let status = 0;
    if (operation === 'delete') {
        operation = "删除";
        status = 1;
    } else if (operation === 'only-self-see') {
        operation = "仅自己可见";
        status = 2;
    } else if (operation === 'normal') {
        operation = "正常";
        status = 0;
    }else {
        $.cvError("异常操作,已被禁止");
        return;
    }

    swal({
        title: "确定将状态变更为" + operation + "?",
        text: "提示",
        icon: "warning",
        buttons: [{
            text: "确认",
            value: true,
            visible: true,
        }, {
            text: "取消",
            value: false,
            visible: true,
        }],
        closeOnClickOutside: false,
        closeOnEsc: false,
    }).then((willDelete) => {
        if (willDelete) {
            let updateComment = {
                commentId: commentId,
                status: status,
            };
            $.cvPost('/comment/edit', updateComment, function (data) {
                if (respSuccess(data)) {
                    history.go();
                } else {
                    $.cvError("状态变更失败")
                }
            });
        }
    });
}
