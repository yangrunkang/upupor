$(function () {
    showKeyword();
    intiSearchInput()
});

function showKeyword() {
    let queryString = getQueryString('keyword');
    if(cvIsNull(queryString)){
        $.cvError("您未输入任何关键词,无法搜索. 即将跳转至首页");
        setTimeout(function(){
            window.location.href = '/';
        },6000);
        return;
    }
    $("#navbar_search_keyword").text(queryString);
}


function intiSearchInput() {
    let searchTotal = $("#content_more_alert_value").val();
    let searchTotalNum = parseInt(searchTotal);
    if (searchTotalNum > 300) {
        $("#content_more_alert").show();
    } else {
        $("#content_more_alert").hide();
    }
}