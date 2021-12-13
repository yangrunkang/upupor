$(function () {
    addConsultantApply();
});


/**
 * 添加广告申请
 */
function addConsultantApply() {
    $('form').submit(function (event) {

        let topic = $("#topic").val();
        let desc = $("#desc").val();

        if (cvIsNull(topic)) {
            $('#topic').popover('show');
            return false;
        } else {
            $('#topic').popover('hide');
        }

        if(cvIsNull(desc)){
            $.cvWarn("描述信息为空");
            return false;
        }

        let adReq = {
            topic: topic,
            desc: desc,
            type: 0,
        };

        $.cvPost('/apply/addConsultant', adReq, function (data) {
            if (respSuccess(data)) {
                $.cvSuccess("申请成功");
                setTimeout(function () {
                    window.location.href = '/user/manage/apply';
                }, 1600)
            } else {
                $.cvError(data)
            }
        });

        return false;
    });
}
