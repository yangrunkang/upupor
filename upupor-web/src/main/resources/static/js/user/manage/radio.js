/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
            $.cvPostJson('/radio/delete', deleteRadio, function (data) {
                if (data.data.success) {
                    history.go();
                } else {
                    $.cvError("删除音频失败")
                }
            });
        }
    });

}
