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
