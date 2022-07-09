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

    setInterval(function () {
        try {
            let cookie = getCookie("isOpenEditor");
            if (cvIsNull(cookie)) {
                document.cookie = "isOpenEditor=yes;max-age=1";
            }
        } catch (e) {

        }
    }, 500);

    $.cvLoadBootstrapRichText();
});

let autoSaveInterval;
$(window).on('load', function () {
    // 开启提示
    // $('[data-toggle="tooltip"]').tooltip();
    // 自动保存 10秒执行一次
    autoSaveInterval = setInterval(function () {
        autoSave();
    }, 10000);
});

function autoSave() {
    let content = getCommonReq();
    content.autoSave = 'YES';
    // saveContent()
    $(".auto-save-card").fadeIn();
    $(".auto-save").text(getFormatDate() + "自动保存").fadeIn();
    console.log('触发自动保存');
    return false;
}


function saveContent(operation) {

    lockLogic(null, function () {
        let content = getCommonReq();
        content.contentOperation = operation;

        if (cvIsNull(content.title)) {
            $.cvWarn("标题为空");
            return false;
        }

        content.preContentId = $(".hidden-pre-content-id").val();
        $.cvPost('/content/add', content, function (data) {
            if (data.data.success) {
                redirectContent(data.data);
            } else {
                $.cvError("文章保存失败")
            }
        });
    })
}

/**
 * 锁定统一逻辑
 * @param operateFunction
 */

let lock_click = true;

function lockLogic(contentId, operateFunction) {
    if (lock_click) {
        lock_click = false;
        if (cvIsNull(contentId) === false) {
            // 清除 定时器
            if (!cvIsNull(autoSaveInterval)) {
                clearInterval(autoSaveInterval);
            }
        }

        operateFunction();
        
        // 定时器 1 只能点击一次
        setTimeout(function () {
            lock_click = true;
        }, 1000);
    }
}


function updateContent(fromSource, contentId, userId, isDraftPublic) {
    lockLogic(contentId, function () {
        let content = getCommonReq();
        if (cvIsNull(content.title)) {
            $.cvWarn("标题为空");
            return false;
        }
        content.contentId = contentId;
        content.userId = userId;
        content.editReason = $("#edit_reason").val();
        content.isDraftPublic = isDraftPublic;

        $.cvPost('/content/edit', content, function (data) {
            if (data.data.success) {
                if (content.isDraftPublic) {
                    $.cvSuccess('发布成功');
                } else {
                    $.cvSuccess('编辑成功');
                }
                redirectContent(data.data);
            } else {
                $.cvError("文章保存失败");
            }
        });
    })
}

function redirectContent(operateContentDto) {
    setTimeout(function () {
        if (operateContentDto.success === false) {
            return;
        }

        if (operateContentDto.status === 'NORMAL') {
            $.cvSuccess("文章已发布");
            window.location.href = '/u/' + operateContentDto.contentId;
        }


        if (operateContentDto.status === 'ONLY_SELF_CAN_SEE' || operateContentDto.status === 'DRAFT') {
            $.cvSuccess("文章已保存为草稿");
            window.location.href = '/m/' + operateContentDto.contentId;
        }
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
    // let edit = getQueryVariable("edit");

    return {
        title: title,
        content: vcrEditorContent,
        mdContent: vcrEditorContentMd,
        noneOriginLink: none_origin_link,
        originType: origin_type,
        contentType: contentType,
        // edit: edit,
        tagIds: getSelectedTagIds(),
        picture: null,
    };
}
