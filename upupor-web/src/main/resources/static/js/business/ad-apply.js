$(function () {
    addAdApply();
    // 开启提示
    $('[data-toggle="tooltip"]').tooltip();
});

/**
 * 添加广告申请
 */
function addAdApply() {
    $('form').submit(function (event) {

        let adIdList = applyAd();
        if (cvIsNull(adIdList) || adIdList.length === 0) {
            $.cvError("您未选择任何广告位置,请重新选择");
            return false;
        }

        let applyUserName = $("#applyUserName").val();
        let applyUserPhone = $("#applyUserPhone").val();
        let applyUserEmail = $("#applyUserEmail").val();
        let applyUserQq = $("#applyUserQq").val();
        let applyUserWechat = $("#applyUserWechat").val();
        let adIntro = $("#adIntro").val();

        if (cvIsNull(applyUserName)) {
            $('#applyUserName').popover('show');
            return false;
        } else {
            $('#applyUserName').popover('hide');
        }

        if (cvIsNull(applyUserPhone)) {
            $('#applyUserPhone').popover('show');
            return false;
        } else {
            $('#applyUserPhone').popover('hide');
        }

        if (cvIsNull(applyUserEmail)) {
            $('#applyUserEmail').popover('show');
            return false;
        } else {
            $('#applyUserEmail').popover('hide');
        }

        if (cvIsNull(applyUserQq)) {
            $('#applyUserQq').popover('show');
            return false;
        } else {
            $('#applyUserQq').popover('hide');
        }

        if (cvIsNull(applyUserWechat)) {
            $('#applyUserWechat').popover('show');
            return false;
        } else {
            $('#applyUserWechat').popover('hide');
        }

        // if (cvIsNull(adIntro)) {
        //     $('#adIntro').popover('show');
        //     return false;
        // } else {
        //     $('#adIntro').popover('hide');
        // }

        let _positionIdList = '';
        for (let i = 0; i < adIdList.length; i++) {
            _positionIdList = _positionIdList + adIdList[i] + ',';
        }

        if(cvIsNull(applyUserPhone)){
            $.cvError("手机号建议填写,方便我们及时与您联系,沟通业务或者广告方案,尽最大程度提升您的广告转化效率");
            return false;
        }

        let adReq = {
            applyUserName: applyUserName,
            applyUserPhone: applyUserPhone,
            applyUserEmail: applyUserEmail,
            applyUserQq: applyUserQq,
            applyUserWechat: applyUserWechat,
            adIntro: adIntro,
            positionIdList: _positionIdList,
            type: 2,
        };

        $.cvPost('/apply/addAd', adReq, function (data) {
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

/**
 * 申请广告位
 */
function applyAd() {
    let adIdList = [];

    $("input:checkbox:checked").each(function (i) {
        //使用循环遍历迭代的方式得到所有被选中的checkbox复选框
        console.log($(this).val());
        adIdList.push($(this).val()); //当前被选中checkbox背后对应的值
    });
    return adIdList;
}
