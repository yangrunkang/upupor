$(function () {
    // 开启提示
    $('[data-toggle="tooltip"]').tooltip();

    // 实现点击图片放大效果
    $(".image").children().click(function(){
        showImg(this);
    });
});
