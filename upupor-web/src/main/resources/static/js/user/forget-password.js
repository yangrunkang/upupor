$(function () {

});

$(window).on('load', function() {
    // 登录
    login();
});

function login() {
    $('form').submit(function (event) {
        let email = $('#email').val();
        let password = $('#password').val();
        let verifyCode = $("#verifyCode").val();

        if (cvIsNull(email)) {
            $.cvWarn("请输入邮件地址");
            return false;
        }

        if (cvIsNull(password)) {
            $.cvWarn("请输入密码");
            return false;
        }

        if (cvIsNull(verifyCode)) {
            $.cvWarn("验证码为空");
            return false;
        }

        let param = {
            email: email,
            password: password,
            verifyCode: verifyCode,
        };

        $.cvPost('/member/resetPassword', param, function (data) {
            if (respSuccess(data)) {
                $.cvSuccess("重置密码成功,正在跳转至登录页");
                setTimeout(function () {
                    window.location.href = '/login';
                }, 1500);
            } else {
                $.cvError(data)
            }
        });

        // 不刷新
        return false;
    });
}


function sendVerifyCode() {
    let email = $('#email').val();
    let password = $('#password').val();

    if (cvIsNull(email)) {
        $.cvWarn("请输入邮件地址");
        return false;
    }

    if (cvIsNull(password)) {
        $.cvWarn("请输入密码");
        return false;
    }

    let req = {
        email: email,
        source: 'forgetPassword'
    };

    $.cvPost("/member/sendVerifyCode", req, function (data) {
        if (respSuccess(data)) {
            $.cvSuccess("发送成功,请查收邮件");
            // 添加按钮不可点击的样式
            $("#send-verify-code-btn").addClass('disabled');
            // 真实控制按钮不可点击的
            $("#send-verify-code-btn").attr('disabled',true);

            let timeout = 90;
            let interval = setInterval(function () {
                $("#send-verify-code-btn").text('已发送,请查收邮件(' + timeout + '秒)');
                timeout = timeout - 1;
                if(timeout <= 0){
                    clearInterval(interval);
                    $("#send-verify-code-btn").removeClass('disabled');
                    $("#send-verify-code-btn").text('发送验证码');
                    $("#send-verify-code-btn").attr('disabled',false);
                }
            }, 1000)
        } else {
            $.cvError("邮件发送失败");
        }
    });
}
