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
    $('.original_radio').click(function () {
        let checkValue = $('input:radio[class="align-self-center original_radio"]:checked').val();
        if (checkValue === 'NONE_ORIGIN') {
            $('.none_original').show();
            $('.original').hide();
        } else {
            $('.none_original').hide();
            $('.original').show();
        }
    });

    $.cvLoadBootstrapRichText();
});

let autoSaveInterval;
let auto_save_timeout = 10000; // 自动保存间隔
$(window).on('load', function () {
    // 开启提示
    // $('[data-toggle="tooltip"]').tooltip();
    // 自动保存 10秒执行一次
    autoSaveInterval = setInterval(function () {
        autoSave();
    }, auto_save_timeout);
});

function autoSave() {
    let content = getCommonReq();
    if (cvIsNull(content.title) && cvIsNull(content.content)) {
        return false;
    }

    if (cvIsNull(content.title)) {
        let date = new Date();
        content.title = date.toLocaleString() + ' 自动保存的草稿 ';
    }

    let draft = {
        draftId: content.preContentId,
        draftContent: JSON.stringify(content),
        draftSource: 'CONTENT'
    }


    $.cvPostUnder('/editor/auto-save', draft, function (data) {
        if (respSuccess(data)) {
            $(".auto-save-card").fadeIn();
            $(".auto-save").text(getFormatDate() + "自动保存").fadeIn();
        } else {
            $(".auto-save").text("文章保存失败").fadeIn();
        }
    });
    return false;
}

function draftSave() {
    try {
        if (cvIsNull(getCommonReq().title)) {
            $.cvError("请输入标题,标题不能为空");
            return false;
        }

        autoSave();
        $.cvSuccess("暂存成功")
    } catch (e) {
        $.cvError("暂存失败")
        return false;
    }
    setTimeout(function () {
        window.location.href = '/m/' + getCommonReq().preContentId;
    }, 1500);

}


function addContent() {
    lockLogic(null, function () {
        let content = getCommonReq();

        if (cvIsNull(content.title)) {
            $.cvWarn("标题为空");
            return false;
        }

        content.preContentId = $(".hidden-pre-content-id").val();
        $.cvPostJson('/content/add', content, function (data) {
            if (data.data.success) {
                redirectContent(data.data);
            } else {
                $.cvError("文章保存失败")
            }
        });
    })
}

function saveOrUpdateContent() {
    // 自动保存停止
    if (!cvIsNull(autoSaveInterval)) {
        clearInterval(autoSaveInterval);
    }

    let contentId = getCommonReq().preContentId;
    $.cvPostJson('/content/exists', {
        contentId
    }, function (data) {
        if (respSuccess(data)) {
            updateContent(contentId);
        } else {
            addContent();
        }
    });
}

/**
 * 锁定统一逻辑
 * @param operateFunction
 */

let lock_click = true;

function lockLogic(contentId, operateFunction) {
    if (lock_click) {
        lock_click = false;

        // 自动保存停止
        if (!cvIsNull(autoSaveInterval)) {
            clearInterval(autoSaveInterval);
        }

        operateFunction();

        // 定时器 1 只能点击一次
        setTimeout(function () {
            lock_click = true;
        }, 1000);
    }
}


function updateContent(contentId) {
    lockLogic(contentId, function () {
        let content = getCommonReq();
        if (cvIsNull(content.title)) {
            $.cvWarn("标题为空");
            return false;
        }
        content.contentId = contentId;

        $.cvPostJson('/content/edit', content, function (data) {
            if (data.data.success) {
                redirectContent(data.data);
            } else {
                $.cvError("文章保存失败");
            }
        });
    })
}

function redirectContent(operateContentDto) {
    if (operateContentDto.success === false) {
        return;
    }

    let tips;
    let url;

    if (operateContentDto.status === 'NORMAL') {
        tips = "文章已发布";
        url = '/u/' + operateContentDto.contentId;
    } else if (operateContentDto.status === 'ONLY_SELF_CAN_SEE') {
        tips = "文章编辑完成";
        url = '/m/' + operateContentDto.contentId;
    } else {
        tips = "未知操作";
        url = '/';
    }

    $.cvSuccess(tips);
    setTimeout(function () {
        window.location.href = url;
    }, 1500);
}


/**
 * 获取选中的选项值
 * @returns {null}
 */
function getSelectedTagIds() {
    // 获取的是option的value
    return $("#cv_selectpicker").val();
}

/**
 * 获取统一的入参
 */
function getCommonReq() {
    let title = $("#title").val();
    let vcrEditorContent = $.cvGetEditorData();
    let vcrEditorContentMd = $.cvGetEditorDataMd();
    let none_origin_link = $('#none_original_link').val();
    let origin_type = $('input:radio[class="align-self-center original_radio"]:checked').val();
    let contentType = getQueryVariable("type");
    let tagIds = getSelectedTagIds();
    let editReason = $("#edit_reason").val();

    // 只有新文章才会预生成文章内容Id
    let preContentId = $('.hidden-pre-content-id').val();

    return {
        title: title,
        content: vcrEditorContent,
        mdContent: vcrEditorContentMd,
        noneOriginLink: none_origin_link,
        originType: origin_type,
        contentType: contentType,
        tagIds: tagIds,
        picture: null,
        preContentId: preContentId,
        editReason: editReason,
    };
}

function cleanDraftAndReload() {
    let contentId = getCommonReq().preContentId;

    $.cvPostJson('/editor/clean-draft', {contentId}, function (data) {
        if (respCodeOk(data)) {
            history.go(0);
        } else {
            $.cvError("清除草稿失败")
        }
    });


}
