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
    // 个人中心-左边-个人中心按钮-激活(区分激活样式)
    let user_manage_message_tag = 'user_manage_message';
    if(path_name.split("/").join("_").slice(1,20) === user_manage_message_tag){
        $("." + user_manage_message_tag).addClass('active');
    }
}

function changeMessageStatus(operate, messageId,userId) {
    if (operate === 'READ') {
        handlePostMessage(messageId,userId,operate);
    } else if (operate === 'DELETED') {
        swal({
            title: "确定删除吗?",
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
                handlePostMessage(messageId,userId,status);
            }
        });
    } else {
        $.cvWarn("参数错误,禁止操作");
    }

}

function handlePostMessage(messageId,userId,status) {
    let req = {
        messageId: messageId,
        status: status,
        // 删除的消息必须是你自己的
        userId: userId
    };

    $.cvPost("/message/edit", req, function (res) {
        if (respDataTrue(res)) {
            history.go()
        } else {
            $.cvError("操作失败");
        }
    });
}

function clearAll(userId,total){
    if(total === 0){
        $.cvWarn("没有消息需要清除");
        return;
    }
    swal({
        title: "确定清除所有消息吗?",
        text: "提示",
        icon: "error",
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
            let req = {
                userId: userId,
                status: 'DELETED'
            };

            $.cvPost('/message/edit',req,function(data){
                if(respDataTrue(data)){
                    history.go()
                }else{
                    $.cvError("清除失败");
                }
            })
        }
    });
}

function readAll(userId){
    swal({
        title: "确定将所有消息标记为已读吗?",
        text: "提示",
        icon: "error",
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
            let req = {
                userId: userId,
                status: 1
            };

            $.cvPost('/message/edit',req,function(data){
                if(respDataTrue(data)){
                    history.go()
                }else{
                    $.cvError("操作失败");
                }
            })
        }
    });
}
