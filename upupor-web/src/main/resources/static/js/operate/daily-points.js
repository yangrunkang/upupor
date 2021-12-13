$(function () {

});

function dailyPoints() {
    $.cvPost('/member/dailyPoints', null, function (res) {
        if (respCodeOk(res)) {
            window.location.href = '/daily-points';
        }
    });
}