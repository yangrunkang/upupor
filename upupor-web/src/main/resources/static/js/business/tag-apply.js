$(function () {
    addAdApply();
});

/**
 * 添加广告申请
 */
function addAdApply() {
    $('form').submit(function (event) {
        let pageName = $("#pageName").val();
        let tagName = $("#tagName").val();
        let childTagName = $("#childTagName").val();

        if (cvIsNull(pageName)) {
            $('#pageName').popover('show');
            return false;
        } else {
            $('#pageName').popover('hide');
        }

        if (cvIsNull(tagName)) {
            $('#tagName').popover('show');
            return false;
        } else {
            $('#tagName').popover('hide');
        }

        let adReq = {
            pageName: pageName,
            tagName: tagName,
            childTagName: childTagName,
        };

        $.cvPost('/apply/addTag', adReq, function (data) {
            if (respSuccess(data)) {
                window.location.href = '/user/manage/apply';
            } else {
                $.cvError(data)
            }
        });

        return false;

    });
}
