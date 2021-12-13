function navLogout() {
    $.cvGet('/member/logout',function (res) {
        if(respCodeOk(res)){
            // swal("退出账户成功", {
            //     icon: "success",
            // });
            // setTimeout(function () {
            //     window.location.href = '/';
            // }, 1600)
            window.location.href = '/';
        }
    });
}