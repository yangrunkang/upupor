/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

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
        if (key === 13) {
            event.preventDefault();
            manageContentSearch('title');
        }
    })
}

function intiSearchInput() {
    let searchQuery = getQueryString("searchTitle");
    let searchContentId = getQueryString("searchContentId");

    function handlePaginationAndAlertTips() {
        // 隐藏分页组件的div
        $("#cv-pagination").addClass("hidden");
        let searchTotal = $("#content_more_alert_value").val();
        let searchTotalNum = parseInt(searchTotal);
        if (searchTotalNum > 300) {
            $("#content_more_alert").show();
        } else {
            $("#content_more_alert").hide();
        }
    }

    if (!cvIsNull(searchQuery)) {
        $("#searchTitle").val(searchQuery);
        $(".by-title").addClass("active");
        handlePaginationAndAlertTips();
    } else if (!cvIsNull(searchContentId)) {
        $("#searchTitle").val(searchContentId);
        $(".by-id").addClass("active");
        $(".from-content-to-draft").show();
        handlePaginationAndAlertTips();
    } else {
        $("#content_more_alert").hide();
    }
}

function removeAll() {
    window.location.href = window.location.pathname;
}

function select(condition) {
    if (cvIsNull(condition)) {
        return;
    }
    window.location.href = window.location.pathname + '?select=' + condition;
}

/**
 * 内容搜索
 */
function manageContentSearch(tag) {
    let title = $("#searchTitle").val();
    if (cvIsNull(title.trim())) {
        if (tag === 'title') {
            $.cvWarn("请输入内容进行搜索");
        } else if (tag === 'id') {
            $.cvWarn("请输入内容id进行搜索");
        }
        return;
    }
    if (tag === 'title') {
        window.location.href = window.location.pathname + '?searchTitle=' + title;
    } else if (tag === 'id') {
        window.location.href = window.location.pathname + '?searchContentId=' + title;
    }

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
function pinned(contentId, ope) {
    let _title = '';
    let _pinnedStatus = 'UN_PINNED';
    if (ope === 'pinned') {
        _title = "确定将文章置顶吗?";
        _pinnedStatus = 'PINNED';
    } else if (ope === 'cancel') {
        _title = "确定将取消文章置顶吗?";
        _pinnedStatus = 'UN_PINNED';
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
function changeContentStatus(status, contentId) {
    let operation = '';
    if (status === 'DELETED') {
        operation = "确定将文章删除吗";
    } else if (status === 'DELETE_DRAFT') {
        operation = "确定将草稿删除吗";
    } else if (status === 'ONLY_SELF_CAN_SEE') {
        operation = "确定将文章变更为自己可见吗";
    } else if (status === 'NORMAL') {
        operation = "确定将文章变更为正常吗";
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

            if (status === 'DELETE_DRAFT') {
                $.cvPost('/editor/clean-draft', {contentId}, function (data) {
                    if (respCodeOk(data)) {
                        history.go(0);
                    } else {
                        $.cvError("清除草稿失败")
                    }
                });

            } else {
                let updateContent = {
                    contentId: contentId,
                    status: status,
                };
                $.cvPost('/content/status', updateContent, function (data) {
                    if (data.data.success) {
                        history.go();
                    } else {
                        $.cvError("状态变更失败")
                    }
                });
            }
        }
    });

}


function toDraft(contentId) {
    window.location.href = '/user/manage/draft?searchContentId=' + contentId;
}


function toExistedContent(contentId) {
    window.location.href = '/user/manage/content?searchContentId=' + contentId;
}
