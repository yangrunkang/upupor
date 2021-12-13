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

        if (cvIsNull(email)) {
            $.cvError('请输入正确邮箱');
            return false;
        }
        if (cvIsNull(password)) {
            $.cvError('请输入密码');
            return false;
        }

        let param = {
            email: email,
            password: password
        };

        $.cvPost('/member/get', param, function (data) {
            if (respSuccess(data)) {
                let back = getQueryString('back');
                if(!cvIsNull(back)){
                    window.location.href = back;
                }else if(window.location.pathname !== '/login'){
                    // 回退到上一步
                    window.location.href = window.location.pathname + window.location.search;
                }else{
                    window.location.href = '/';
                }
            } else {
                $.cvError(data)
            }
        });

        // 不刷新
        return false;
    });
}
