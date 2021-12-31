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
    $("#applyAdText").text('');
});


function userLeftContentBtnActive() {
    let path_name = window.location.pathname;
    $("." + path_name.split("/").slice(1).join("_")).addClass('active');
}

function applyOperate(operate, applyId,userId) {
    if (operate === 'stop') {
        swal({
            title: "确定终止申请流程吗?",
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
                let delReq = {
                    applyId: applyId,
                    status: 'APPLY_TERMINATE',
                    userId: userId,
                };
                $.cvPost('/apply/edit', delReq, function (res) {
                    if (respSuccess(res)) {
                        history.go();
                    } else {
                        $.cvError("申请终止失败")
                    }
                })
            }
        });
    } else if (operate === 'delete') {
        swal({
            title: "确定删除申请吗?",
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
                let delReq = {
                    applyId: applyId,
                    userId: userId,
                };
                $.cvPost('/apply/del', delReq, function (res) {
                    if (respSuccess(res)) {
                        history.go();
                    } else {
                        $.cvError("删除申请失败")
                    }
                })
            }
        });
    } else {
        $.cvError("无效操作");
        return false;
    }

}
