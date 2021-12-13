function setKeywords(contentId) {
    let keywords = $("#keywords").val();
    $.cvPost('/admin/content/keywords',{
        contentId,
        keywords
    },function (res) {
        if (respSuccess(res)) {
            $.cvSuccess("设置关键字成功");
            setTimeout(function () {
                history.go(-1);
            }, 1600)
        } else {
            $.cvError("设置关键字失败")
        }
    });
}
