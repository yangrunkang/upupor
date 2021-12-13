$(function () {

    leftMenuActive();
});

function leftMenuActive() {
    let url = window.location.pathname;
    if (cvIsNull(url)) {
        return;
    }
    // 如果直接访问首页,则将技术标签设置为默认标签
    let urlArr = url.split('/');
    if (cvIsNull(urlArr[1])) {
        $(".all").addClass("active");
    }
    // 激活一级标签
    if(!cvIsNull(urlArr[1])){
        $("." + urlArr[1]).addClass("active");
    }

    // 激活二级标签
    if(!cvIsNull(urlArr[2])){
        $("." + urlArr[2]).addClass("active");
    }
}

