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

function removeAll() {
    window.location.href = window.location.pathname;
}

function select(condition) {
    if(cvIsNull(condition)){
        return;
    }
    window.location.href = window.location.pathname + '?select=' + condition;
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

/**
 * 激活右边栏
 */
function userLeftContentBtnActive() {
    let path_name = window.location.pathname;
    $("." + path_name.split("/").slice(1).join("_")).addClass('active');
}

/**
 * 位置置顶
 * @param contentId
 */
function pinned(contentId,ope) {
    let _title = '';
    let _pinnedStatus = 0;
    if(ope === 'pinned'){
        _title = "确定将文章置顶吗?";
        _pinnedStatus = 1;
    }else if(ope === 'cancel'){
        _title = "确定将取消文章置顶吗?";
        _pinnedStatus = 0;
    }

    swal({
        title: _title,
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
            let pinnedReq = {
                contentId: contentId,
                pinnedStatus: _pinnedStatus,
            };
            $.cvPost('/content/pinned', pinnedReq, function (data) {
                if (respCodeOk(data)) {
                    history.go();
                } else {
                    $.cvError("置顶失败")
                }
            });
        }
    });



}


/**
 * 内容状态变更
 * @param operation
 * @param content
 */
function changeContentStatus(operation, status,contentId) {
    if (operation === 'draft') {
        operation = "确定将文章变更为草稿吗";
        status = 1;
    } else if (operation === 'delete') {
        operation = "确定将文章删除吗";
        status = 4;
    } else if (operation === 'only-self-see') {
        operation = "确定将文章变更为自己可见吗";
        status = 6;
    } else if (operation === 'normal') {
        operation = "确定将文章变更为正常吗";
        status = 0;
    } else {
        $.cvError("异常操作,已被禁止");
        return;
    }

    swal({
        title: operation,
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
            let updateContent = {
                contentId: contentId,
                status: status,
            };
            $.cvPost('/content/status', updateContent, function (data) {
                if (respSuccess(data)) {
                    history.go();
                } else {
                    $.cvError("状态变更失败")
                }
            });
        }
    });

}
