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
    let status = 0;
    if (operate === 'read') {
        status = 1;
        handlePostMessage(messageId,userId,status);
    } else if (operate === 'delete') {
        status = 2;

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
                status: 2
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
