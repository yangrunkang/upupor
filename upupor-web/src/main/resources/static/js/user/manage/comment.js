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
