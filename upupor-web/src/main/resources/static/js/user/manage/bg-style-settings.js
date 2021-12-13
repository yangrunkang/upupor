$(function () {
    // 激活右边内容按钮
    userLeftContentBtnActive();

    // 编辑用户信息
    editUserInfo();

    $("#self-define-css").focus(function () {
        let cssPatternValue = $('input:radio[class="css_pattern_radio"]:checked').val();
        if(!cvIsNull(cssPatternValue) && cssPatternValue !== 'self-define'){
            $.cvError("编写自定义css样式之前请优先选中自定义按钮");
        }
    })
});



function editUserInfo() {
    $("form").submit(function () {
        let cssPatternValue = $('input:radio[class="css_pattern_radio"]:checked').val();
        let formData = new FormData();

        if(cssPatternValue === 'self-define'){
            let selfDefineCss = $("#self-define-css").val();
            if(cvIsNull(selfDefineCss)){
                $.cvError("自定义css不能为空");
                return false;
            }
            formData.append('selfDefineCss', selfDefineCss);
        }else {
            formData.append('cssPatternValue', cssPatternValue);
        }
        $.ajax({
            url: '/member/edit/bg-style-settings',
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

function userLeftContentBtnActive(){
    let path_name = window.location.pathname;
    $("." + path_name.split("/").slice(1).join("_")).addClass('active');
}
