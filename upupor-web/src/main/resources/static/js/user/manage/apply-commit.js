$(function () {
    userLeftContentBtnActive();
    applyDocumentCommit();

    $('input[type="file"]').change(function(e){
        let fileName = e.target.files[0].name;
        $('.custom-file-label').html(fileName);
    });
});


function applyDocumentCommit() {
    $("form").submit(function () {
        let applyId = $("#apply-commit-target").val();
        // let adImgUrl = $("#adImgUrl").val();
        // let upload = $("#upload").val();
        let file = $("input[type=file]")[0].files[0];
        let applyAdText = $("#applyAdText").val();

        if (cvIsNull(applyId)) {
            $.cvError("无该申请,提交失败");
            return false;
        }

        if (cvIsNull(file)) {
            $.cvError("文件上传为空,请选择文件");
            return false;
        }

        if (cvIsNull(applyAdText)) {
            $.cvError("广告文案为空,如果不需要文案,可以填写 '无'  ");
            return false;
        }

        let formData = new FormData();
        formData.append('applyId', applyId);
        // formData.append('upload', upload);
        formData.append('applyAdText', applyAdText);
        formData.append('file', file);

        //  这里涉及到上传文件,所以单独写一个ajax请求
        $.ajax({
            url: '/apply/commit',
            type: 'post',
            async: false,
            data: formData,
            processData: false,// 告诉jQuery不要去处理发送的数据
            contentType: false,// 告诉jQuery不要去设置Content-Type请求头
            beforeSend: function () {//过程...
                console.log('正在进行，请稍候')
            },
            success: function (res) {
                console.log(res);
                if (res.code === 0) {
                    if (respSuccess(res.data)) {
                        $.cvSuccess("提交材料成功");
                        setTimeout(function () {
                            window.location.href = '/user/manage/apply';
                        }, 1600)
                    } else {
                        $.cvError("提交材料失败");
                    }
                } else {
                    $.cvError("提交材料失败");
                }
            },
            error: function () {
                console.log('导入失败')
            }
        });
        return false;
    });

}

function userLeftContentBtnActive() {
    let path_name = window.location.pathname;
    $("." + path_name.split("/").slice(1).join("_")).addClass('active');
}
