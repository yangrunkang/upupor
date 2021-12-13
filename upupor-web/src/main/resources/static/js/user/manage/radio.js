$(function () {
    userLeftContentBtnActive();
    // 初始化搜索框
    intiSearchInput();
    // 搜索框绑定enter键
    searchInputBindEnter();
});


function searchInputBindEnter() {
    $("#searchTitle").on('keydown', function (event) {
        let key = event.which;
        if(key === 13){
            event.preventDefault();
            manageContentSearch();
        }
    })
}

/**
 * 内容搜索
 */
function manageContentSearch() {
    let title = $("#searchTitle").val();
    if (cvIsNull(title.trim())) {
        $.cvWarn("请输入内容进行搜索");
        return;
    }
    window.location.href = window.location.pathname + '?searchTitle=' + title;
}

function intiSearchInput() {
    let searchQuery = getQueryString("searchTitle");
    if (!cvIsNull(searchQuery)) {
        $("#searchTitle").val(searchQuery);
        // 隐藏分页组件的div
        $("#cv-pagination").addClass("hidden");
        let searchTotal = $("#content_more_alert_value").val();
        let searchTotalNum = parseInt(searchTotal);
        if (searchTotalNum > 300) {
            $("#content_more_alert").show();
        } else {
            $("#content_more_alert").hide();
        }
    } else {
        $("#content_more_alert").hide();
    }
}

/**
 * 激活右边栏
 */
function userLeftContentBtnActive() {
    let path_name = window.location.pathname;
    $("." + path_name.split("/").slice(1).join("_")).addClass('active');
}


function deleteRadio(radioId) {
    swal({
        title: '确定删除音频吗',
        text: "提示",
        icon: "warning",
        buttons: [{
            text: "确认",
            value: true,
            visible: true,
        }, {
            text: "取消",
            value: false,
            visible: true,
        }],
        closeOnClickOutside: false,
        closeOnEsc: false,
    }).then((willDelete) => {
        if (willDelete) {
            let deleteRadio = {
                radioId: radioId
            };
            $.cvPost('/radio/delete', deleteRadio, function (data) {
                if (respSuccess(data)) {
                    history.go();
                } else {
                    $.cvError("删除音频失败")
                }
            });
        }
    });

}
