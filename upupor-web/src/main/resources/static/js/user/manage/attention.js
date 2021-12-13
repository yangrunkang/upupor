$(function () {

    userLeftContentBtnActive();
});

/**
 * 激活右边栏
 */
function userLeftContentBtnActive() {
    let path_name = window.location.pathname;
    $("." + path_name.split("/").slice(1).join("_")).addClass('active');
}

function deleteAttention(attentionId) {
    console.log(attentionId);
    swal({
        title: "确定删除?",
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
            let req = {
                attentionId: attentionId,
            };
            $.cvPost("/attention/del", req, function (res) {
                if (respSuccess(res)) {
                    history.go();
                }
            })
        }
    });
}
