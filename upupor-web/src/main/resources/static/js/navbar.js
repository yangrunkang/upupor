$(function () {
    // 导航栏按钮状态切换
    switchNav();
    // 绑定搜索输入绑定按钮
    searchNavbarInputBindEnter();
    // 根据搜索参数进行初始化搜索框内容
    $("#cv_navbar_search_input").val(getQueryString("keyword"));
});

/**
 * 切换导航栏
 */
function switchNav() {
    // 获取导航栏url eg.http://localhost:8888/qa/content/20011223212625697792
    // 获取导航栏path_name /qa/content/20011223212625697792
    let location = window.location.pathname;
    try {
        // 数组中 坐标为1的元素就是导航栏目录 需要规范的url才能做到这一步,并且很方便
        let currentMenuId = location.split('/')[1];
        if (cvIsNull(currentMenuId)) {
            currentMenuId = "home";
        }
        // 检测二级菜单
        let second = location.split('/')[3];
        if(!cvIsNull(second)){
            if(second === 'message'){
                currentMenuId = second;
            }
        }
        $("." + currentMenuId).addClass("cv-active");
    } catch (e) {
        console.error("cv.js switchNav error" + e)
    }
}

/**
 * 搜索栏搜索
 */
function cvNavbarSearch(){
    let searchInput = $("#cv_navbar_search_input").val().trim();
    if(cvIsNull(searchInput)){
        $.cvWarn("您未输入任何内容,无法搜索");
        return;
    }

    window.location.href = '/search?keyword='+searchInput;
}

function searchNavbarInputBindEnter() {
    $("#cv_navbar_search_input").on('keydown', function (event) {
        let key = event.which;
        if(key === 13){
            event.preventDefault();
            cvNavbarSearch();
        }
    })
}
