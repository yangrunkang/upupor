/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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
    $('input[type="file"]').change(function (e) {
        let fileName = e.target.files[0].name;
        $('.custom-file-label').html(fileName);
    });
});

$(function () {

    $("form").submit(function () {
        let radioIntro = $("#radio-intro").val();
        let file = $("input[type=file]")[0].files[0];

        if (cvIsNull(file)) {
            $.cvError("电台文件,请选择文件");
            return false;
        }

        if (cvIsNull(radioIntro)) {
            $.cvError("音频简介内容为空");
            return false;
        }

        let formData = new FormData();
        formData.append('radioFile', file);

        // 单数上传文件,可以读取进度
        $.ajax({
            url: '/radio/addRadioFile',
            type: 'post',
            data: formData,
            processData: false,// 告诉jQuery不要去处理发送的数据
            contentType: false,// 告诉jQuery不要去设置Content-Type请求头
            beforeSend: function () {//过程...
                $(".process-div").show();
                $(".progress-div-tips").text('上传中...请稍等');
                $(".progress-bar").removeClass("bg-danger").addClass("bg-success");
                $(".public-radio").addClass('disabled').text('发布中,请稍等');
            },
            xhr: function () {
                myXhr = $.ajaxSettings.xhr();
                if (myXhr.upload) { // check if upload property exists
                    myXhr.upload.addEventListener('progress', function (e) {
                        let loaded = e.loaded;//已经上传大小情况
                        let tot = e.total;//附件总大小
                        let per = ((loaded / tot) * 100).toFixed(2);
                        $(".progress-bar").attr('style', 'width:' + per + '%');
                        $(".public-radio").text('发布中,请稍等...' + per + '%');
                        $(".progress-text").text(per+'%');

                    }, false); // for handling the progress of the upload
                }
                return myXhr;
            },
            success: function (res) {
                if (res.code === 0) {
                    addRadio(radioIntro,res.data.data);
                } else {
                    publicFailed();
                }
            },
            error: function () {
                publicFailed();
            }
        });
        return false;
    });

});

function publicFailed() {
    $(".progress-text").text('发布失败,请重试');
    $(".progress-div-tips").text('上传失败,请重试');
    $(".progress-bar").removeClass("bg-success").addClass("bg-danger");
    $(".public-radio").removeClass('disabled').text('重新上传');
    $.cvError("上传电台文件失败,请重试");
}

function publicSuccess() {
    $(".public-radio").text('发布成功');
    $(".progress-text").text('发布成功');
    $(".progress-div-tips").text('上传成功');
    $.cvSuccess("发布成功");
    setTimeout(function () {
        window.location.href = '/radio-station';
    }, 1600);
}

function addRadio(radioIntro, fileUrl) {
    let formData = new FormData();
    formData.append('radioIntro', radioIntro);
    formData.append('fileUrl', fileUrl);

    //  这里涉及到上传文件,所以单独写一个ajax请求
    $.ajax({
        url: '/radio/add',
        type: 'post',
        data: formData,
        processData: false,// 告诉jQuery不要去处理发送的数据
        contentType: false,// 告诉jQuery不要去设置Content-Type请求头
        success: function (res) {
            if (res.code === 0) {
                publicSuccess();
            } else {
                publicFailed();
            }
        },
        error: function () {
            publicFailed();
        }
    });
}