$(function () {
    $('.original_radio').click(function(){
        let checkValue = $('input:radio[class="align-self-center original_radio"]:checked').val();
        if(checkValue === 'none_original'){
            $('.none_original').show();
            $('.original').hide();
        }else{
            $('.none_original').hide();
            $('.original').show();
        }
    });

    setInterval(function () {
        let cookie = getCookie("isOpenEditor");
        if(cvIsNull(cookie)){
            document.cookie="isOpenEditor=yes;max-age=1";
        }
    }, 500);
});

let autoSaveInterval;
$(window).on('load', function() {
    $.cvLoadBootstrapRichText();
    // 开启提示
    // $('[data-toggle="tooltip"]').tooltip();
    // 自动保存 10秒执行一次
    autoSaveInterval = setInterval(function () {
        autoSave();
    }, 10000);
});

function autoSave() {
    let title = $("#title").val();
    let vcrEditorContent = $.cvGetEditorData();

    if (!cvIsNull(title) || !cvIsNull(vcrEditorContent)) {

        let origin_type = 0;
        let checkValue = $('input:radio[class="align-self-center original_radio"]:checked').val();
        let none_origin_link = $('#none_original_link').val();
        if(checkValue === 'none_original'){
            origin_type = 1;
        }else{
            origin_type = 2;
        }

        let req = {
            title: title,
            content: vcrEditorContent,
            contentType: getQueryVariable("type"),
            tagIds: getSelectedTagIds(),
            edit: getQueryVariable("edit"),
            contentId: $(".hide-content-content-id").val(),
            userId: $(".hide-content-user-id").val(),
            originType: origin_type,
            noneOriginLink: none_origin_link
        };

        $.cvPost('/cache/add', req, function (data) {
            if (respCodeOk(data)) {
                $(".auto-save-card").fadeIn();
                $(".auto-save").text(getFormatDate() + "自动保存").fadeIn();
            }
        });

        return false;
    }

}

/**
 * 提交form表单
 */
let lock_click = true;

function saveContent(operation) {
    // 清除 定时器
    if (!cvIsNull(autoSaveInterval)) {
        clearInterval(autoSaveInterval);
    }
    if (lock_click) {
        lock_click = false;
        handleSaveContentEvent(operation);
        // 定时器 1 只能点击一次
        setTimeout(function () {
            lock_click = true;
        }, 1000);
    }
}

function updateContent(fromSource, contentId, userId) {
    if (lock_click) {
        lock_click = false;
        if (cvIsNull(contentId) === false) {
            // 清除 定时器
            if (!cvIsNull(autoSaveInterval)) {
                clearInterval(autoSaveInterval);
            }
            handleEditContentEvent(contentId, userId,false,"编辑成功");
        }
        // 定时器 1 只能点击一次
        setTimeout(function () {
            lock_click = true;
        }, 1000);
    }
}

function updateContentPublic(fromSource, contentId, userId) {
    if (lock_click) {
        lock_click = false;
        if (cvIsNull(contentId) === false) {
            // 清除 定时器
            if (!cvIsNull(autoSaveInterval)) {
                clearInterval(autoSaveInterval);
            }
            handleEditContentEvent(contentId, userId,true,"发布成功");
        }
        // 定时器 1 只能点击一次
        setTimeout(function () {
            lock_click = true;
        }, 1000);
    }
}

/**
 *
 * @param contentId
 * @param userId
 * @param isDraftPublic 是否从草稿变更为正常
 * @returns {boolean}
 */
function handleEditContentEvent(contentId, userId,isDraftPublic,tips) {
    let title = $("#title").val();
    let vcrEditorContent = $.cvGetEditorData();
    let editReason = $("#edit_reason").val();

    if (cvIsNull(title)) {
        $.cvWarn("标题为空");
        return false;
    }
    let origin_type = 0;
    let checkValue = $('input:radio[class="align-self-center original_radio"]:checked').val();
    let none_origin_link = $('#none_original_link').val();
    if(checkValue === 'none_original'){
        origin_type = 1;
    }else{
        origin_type = 2;
    }

    // 获取的是option的value
    let content = {
        contentId: contentId,
        title: title,
        detailContent: vcrEditorContent,
        // todo
        picture: null,
        tagIds: getSelectedTagIds(),
        userId: userId,
        editReason: editReason,
        originType: origin_type,
        noneOriginLink: none_origin_link,
        isDraftPublic: isDraftPublic
    };

    $.cvPost('/content/edit', content, function (data) {
        if (respSuccess(data)) {
            $.cvSuccess(tips);
            jumpContent();
        } else {
            $.cvError("文章保存失败")
        }
    });
}


function handleSaveContentEvent(operation) {
    let title = $("#title").val();
    let vcrEditorContent = $.cvGetEditorData();

    if (cvIsNull(title)) {
        $.cvWarn("标题为空");
        return false;
    }

    let origin_type = 0;
    let none_origin_link = $('#none_original_link').val();
    let checkValue = $('input:radio[class="align-self-center original_radio"]:checked').val();
    if(checkValue === 'none_original'){
        origin_type = 1;
    }else{
        origin_type = 2;
    }

    let content = {
        title: title,
        content: vcrEditorContent,
        picture: null,
        contentType: getQueryVariable("type"),
        tagIds: getSelectedTagIds(),
        operation: operation,
        originType: origin_type,
        noneOriginLink: none_origin_link,
        preContentId: $(".hidden-pre-content-id").val()
    };

    $.cvPost('/content/add', content, function (data) {
        if (respSuccess(data)) {
            if (!cvIsNull(operation)) {
                if (operation === 'temp') {
                    $.cvSuccess("文章已保存为草稿");
                    jumpContent();
                }
            } else {
                $.cvSuccess("文章已发布");
                jumpContent();
            }
        } else {
            $.cvError("文章保存失败")
        }
    });
}

function jumpContent() {
    setTimeout(function () {
        // 使用href会刷新
        window.location.href = '/router/jump/content';
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
