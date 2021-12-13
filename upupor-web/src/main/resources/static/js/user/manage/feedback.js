$(function () {
    userLeftContentBtnActive();
});

function userLeftContentBtnActive(){
    let path_name = window.location.pathname;
    $("." + path_name.split("/").slice(1).join("_")).addClass('active');
}
