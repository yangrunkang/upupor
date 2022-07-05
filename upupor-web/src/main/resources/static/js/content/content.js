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
    $.cvLoadShowImg();
});

function copyContentUrl() {
    let clipboard = new ClipboardJS('.copy-url');
    clipboard.on('success', function (e) {
        $("#btn_copy_url_code").html('已复制');
    });
    clipboard.on('error', function (e) {
        $("#btn_copy_url_code").html('复制失败');
    });
}


function submitLike(contentId) {

    let updateReq = {
        contentId: contentId,
    };

    $.cvPost('/content/like', updateReq, function (res) {
        if (respSuccess(res)) {
            let likeNum = $("#like_badge").html();
            let newLike = 0;

            /*按钮文字改变*/
            let like_text = $("#like_text").text();
            if (like_text === '已点赞') {
                newLike = parseInt(likeNum) - 1;
                $("#like_text").text("点赞");
            } else {
                newLike = parseInt(likeNum) + 1;
                $("#like_text").text("已点赞");
            }
            $("#like_badge").html(newLike);
            // 异步刷新
            $("#sync_like_area").load('/sync/content/like?contentId=' + contentId);
        } else {
        }
    });
}

/*点赞Start*/
function like(contentId) {
    submitLike(contentId);
}

/*点赞End*/

function collect(contentId) {
    // 根据文字来判断
    let collectText = $("#collect_span_text").text();
    if (cvIsNull(collectText)) {
        console.error("文章详情页,收藏文字有误,empty")
    }
    let isCollect = false;
    if (collectText === '已收藏') {
        isCollect = true;
    } else if (collectText === '收藏') {
        isCollect = false;
    } else {
        console.error("文章详情页,收藏文字有误")
    }
    let req = {
        collectValue: contentId,
        collectType: 'CONTENT',
        isCollect: isCollect,
    };

    $.cvPost('/collect/add', req, function (res) {
        if (respCodeOk(res)) {
            if (res.data === true) {
                if (isCollect) {
                    // 已收藏的操作重复点击(二次点击)意味着取消收藏
                    showCancelCollectText()
                } else {
                    showCollectText();
                }
            } else if (res.data === 0) {
                showCancelCollectText();
            }
        } else {
            $.cvWarn("收藏失败");
        }
    });
}

function showCancelCollectText() {
    $("#collect_span_text").text('收藏');
    // 处理收藏数
    let newCollect = 0;
    let collectNum = $("#collect_badge").html();
    newCollect = parseInt(collectNum) - 1;
    $("#collect_badge").html(newCollect);
}

function showCollectText() {
    $("#collect_span_text").text('已收藏');
    // 处理收藏数
    let newCollect = 0;
    let collectNum = $("#collect_badge").html();
    newCollect = parseInt(collectNum) + 1;
    $("#collect_badge").html(newCollect);
}


