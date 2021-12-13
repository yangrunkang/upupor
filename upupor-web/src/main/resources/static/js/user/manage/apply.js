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
                    status: 4,
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
